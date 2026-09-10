#!/usr/bin/env python3
# simulador de ataque pro sentinela - manda eventos falsos pro rust engine

import argparse
import json
import os
import random
import sys
import time
import uuid
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timezone

try:
    import requests
except ImportError:
    requests = None

URL_PADRAO = "http://localhost:3000/events"

USUARIOS = ["felipe", "marina", "joao", "ana", "carlos", "beatriz",
            "lucas", "rafaela", "bruno", "camila", "diego", "julia"]


def sortear_usuarios(qtd):
    if qtd <= len(USUARIOS):
        return random.sample(USUARIOS, qtd)
    return [random.choice(USUARIOS) + str(i) for i in range(qtd)]


def ip_conhecido():
    return f"192.168.1.{random.randint(2, 50)}"


def ip_desconhecido():
    return f"{random.randint(20,200)}.{random.randint(0,255)}.{random.randint(0,255)}.{random.randint(1,254)}"


def montar_evento(event_type, user, ip, asset=None, timestamp=None):
    return {
        "id": str(uuid.uuid4()),
        "event_type": event_type,
        "timestamp": timestamp or datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ"),
        "source": "attacker-simulator",
        "user": user,
        "ip": ip,
        "asset": asset,
    }


def timestamp_fora_de_horario():
    # gera um horario de madrugada (00h-05h), fora do expediente comum
    agora = datetime.now(timezone.utc)
    hora_madrugada = random.randint(0, 5)
    minuto = random.randint(0, 59)
    fake = agora.replace(hour=hora_madrugada, minute=minuto, second=random.randint(0, 59))
    return fake.strftime("%Y-%m-%dT%H:%M:%SZ")


class EventSender:
    def __init__(self, url, api_key=None, dry_run=False):
        self.url = url
        self.api_key = api_key
        self.dry_run = dry_run

        if not dry_run and not url:
            raise ValueError("informe --url ou use --dry-run")
        if not dry_run and requests is None:
            raise RuntimeError("falta instalar o requests (pip install requests)")

    def send(self, evento):
        if self.dry_run:
            print("[dry-run]", json.dumps(evento, ensure_ascii=False))
            return

        headers = {"Content-Type": "application/json"}
        if self.api_key:
            headers["X-API-Key"] = self.api_key

        try:
            r = requests.post(self.url, json=evento, headers=headers, timeout=5)
            status = "ok" if r.ok else "falhou"
            print(f"[{status} {r.status_code}] {evento['event_type']} user={evento['user']} ip={evento['ip']}")
        except requests.RequestException as e:
            print(f"[erro] {e}", file=sys.stderr)


class Attacker:
    def __init__(self, sender, delay=1.0):
        self.sender = sender
        self.delay = delay

    def brute_force(self, user="felipe", ip=None, tentativas=5, intervalo=2.0):
        ip = ip or ip_conhecido()
        print(f"\nbrute force -> user={user} ip={ip} ({tentativas} tentativas)")
        for i in range(tentativas):
            self.sender.send(montar_evento("LOGIN_FAILED", user, ip))
            if i < tentativas - 1:
                time.sleep(intervalo)

    def ip_desconhecido_login(self, user="felipe", ip=None):
        ip = ip or ip_desconhecido()
        print(f"\nlogin de ip desconhecido -> user={user} ip={ip}")
        self.sender.send(montar_evento("LOGIN_SUCCESS", user, ip))

    def fora_de_horario(self, user="felipe", ip=None):
        ip = ip or ip_conhecido()
        ts = timestamp_fora_de_horario()
        print(f"\nlogin fora de horario -> user={user} ip={ip} timestamp={ts}")
        self.sender.send(montar_evento("LOGIN_SUCCESS", user, ip, timestamp=ts))

    def acesso_admin(self, user="felipe", ip=None, asset="srv-db-01"):
        ip = ip or ip_conhecido()
        print(f"\nacesso admin -> user={user} ip={ip} asset={asset}")
        self.sender.send(montar_evento("ADMIN_ACCESS", user, ip, asset))

    def download_anormal(self, user="felipe", ip=None, asset="fileserver-01", qtd=8, intervalo=1.0):
        ip = ip or ip_conhecido()
        print(f"\ndownload anormal -> user={user} ip={ip} asset={asset} qtd={qtd}")
        for i in range(qtd):
            self.sender.send(montar_evento("FILE_DOWNLOAD", user, ip, asset))
            if i < qtd - 1:
                time.sleep(intervalo)

    def combinado(self, user="felipe"):
        ip = ip_desconhecido()
        asset = "srv-db-01"
        print(f"\n=== ataque combinado: {user} ===")
        self.brute_force(user, ip)
        time.sleep(self.delay)
        self.ip_desconhecido_login(user, ip)
        time.sleep(self.delay)
        self.fora_de_horario(user, ip)
        time.sleep(self.delay)
        self.acesso_admin(user, ip, asset)
        time.sleep(self.delay)
        self.download_anormal(user, ip, asset)


def rodar(attacker, cenario, user, ip, args):
    if cenario == "brute-force":
        attacker.brute_force(user, ip, args.attempts, args.interval)
    elif cenario == "unknown-ip":
        attacker.ip_desconhecido_login(user, ip)
    elif cenario == "off-hours":
        attacker.fora_de_horario(user, ip)
    elif cenario == "admin-access":
        attacker.acesso_admin(user, ip)
    elif cenario == "abnormal-download":
        attacker.download_anormal(user, ip)
    elif cenario == "combined":
        attacker.combinado(user)


def main():
    parser = argparse.ArgumentParser(description="simulador de risco - sentinela")
    parser.add_argument("scenario", choices=[
        "brute-force", "unknown-ip", "off-hours",
        "admin-access", "abnormal-download", "combined",
    ])
    parser.add_argument("--url", default=os.environ.get("SENTINELA_TARGET_URL", URL_PADRAO))
    parser.add_argument("--api-key", default=os.environ.get("SENTINELA_API_KEY"))
    parser.add_argument("--user", default="felipe")
    parser.add_argument("--ip", default=None)
    parser.add_argument("--attempts", type=int, default=5)
    parser.add_argument("--interval", type=float, default=2.0)
    parser.add_argument("--delay", type=float, default=1.0)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--attackers", type=int, default=1, help="quantos atacantes rodar em paralelo")
    args = parser.parse_args()

    sender = EventSender(args.url, args.api_key, args.dry_run)
    attacker = Attacker(sender, delay=args.delay)

    if args.attackers <= 1:
        rodar(attacker, args.scenario, args.user, args.ip, args)
        return

    usuarios = sortear_usuarios(args.attackers)
    print(f"\ndisparando {args.attackers} atacantes em paralelo: {usuarios}")

    with ThreadPoolExecutor(max_workers=args.attackers) as pool:
        for user in usuarios:
            pool.submit(rodar, attacker, args.scenario, user, None, args)


if __name__ == "__main__":
    main()