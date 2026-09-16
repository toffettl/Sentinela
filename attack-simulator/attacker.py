# simulador de ataque pro sentinela


import argparse, json, os, random, sys, time, uuid
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timezone

try:
    import requests
except ImportError:
    requests = None

URL_PADRAO = "http://localhost:3000/events"
BONECO_URL_PADRAO = "http://localhost:5000"

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
    agora = datetime.now(timezone.utc)
    fake = agora.replace(hour=random.randint(0, 5), minute=random.randint(0, 59), second=random.randint(0, 59))
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


# ataca o boneco .net direto (login/admin/files/users) em vez de mandar
# o evento ja pronto - quem monta o log e o proprio boneco
class BonecoAttacker:
    def __init__(self, base_url, delay=1.0, dry_run=False):
        self.base_url = base_url.rstrip("/")
        self.delay = delay
        self.dry_run = dry_run
        if not dry_run and requests is None:
            raise RuntimeError("falta instalar o requests (pip install requests)")

    def _post(self, path, json_body=None, ip=None):
        url = self.base_url + path
        headers = {"Content-Type": "application/json"}
        if ip:
            headers["X-Forwarded-For"] = ip
        hora = datetime.now().strftime("%H:%M:%S")

        if self.dry_run:
            print(f"[{hora}] [dry-run] POST {path} body={json_body} ip={ip}")
            return None
        try:
            r = requests.post(url, json=json_body, headers=headers, timeout=5)
            if r.ok:
                status = "OK"
            elif r.status_code in (401, 403):
                status = "FALHA-ESPERADA"
            else:
                status = "ERRO"
            print(f"[{hora}] [{status} {r.status_code}] POST {path} ip={ip}")
            return r
        except requests.RequestException as e:
            print(f"[{hora}] [ERRO-CONEXAO] POST {path} -> {e}", file=sys.stderr)
            return None

    def _get(self, path, ip=None):
        url = self.base_url + path
        headers = {"X-Forwarded-For": ip} if ip else {}
        hora = datetime.now().strftime("%H:%M:%S")

        if self.dry_run:
            print(f"[{hora}] [dry-run] GET {path} ip={ip}")
            return None
        try:
            r = requests.get(url, headers=headers, timeout=5)
            status = "OK" if r.ok else "ERRO"
            print(f"[{hora}] [{status} {r.status_code}] GET {path} ip={ip}")
            return r
        except requests.RequestException as e:
            print(f"[{hora}] [ERRO-CONEXAO] GET {path} -> {e}", file=sys.stderr)
            return None

    def brute_force(self, user="felipe", ip=None, tentativas=5, intervalo=2.0):
        ip = ip or ip_conhecido()
        print(f"\n=== cenario: BRUTE-FORCE | user={user} ip={ip} | {tentativas} tentativas ===")
        for i in range(tentativas):
            self._post("/api/auth/login", {"username": user, "password": "senha_errada"}, ip)
            if i < tentativas - 1:
                time.sleep(intervalo)

    def ip_desconhecido_login(self, user="felipe", ip=None):
        ip = ip or ip_desconhecido()
        print(f"\n=== cenario: UNKNOWN-IP | user={user} ip={ip} ===")
        # credencial certa mas de um ip fora do padrao
        self._post("/api/auth/login", {"username": user, "password": "123456"}, ip)

    def fora_de_horario(self, user="felipe", ip=None):
        ip = ip or ip_conhecido()
        print(f"\n=== cenario: OFF-HOURS | user={user} ip={ip} ===")
        print("  obs: quem gera o timestamp aqui e o proprio boneco (na hora que recebe")
        print("  a requisicao), entao so da pra ver isso funcionando de verdade rodando")
        print("  fora do horario comercial mesmo, ou mudando o relogio da maquina")
        self._post("/api/auth/login", {"username": user, "password": "123456"}, ip)

    def acesso_admin(self, user="felipe", ip=None):
        ip = ip or ip_conhecido()
        print(f"\n=== cenario: ADMIN-ACCESS | user={user} ip={ip} ===")
        self._get("/api/admin", ip)

    def download_anormal(self, user="felipe", ip=None, qtd=8, intervalo=1.0):
        ip = ip or ip_conhecido()
        print(f"\n=== cenario: ABNORMAL-DOWNLOAD | user={user} ip={ip} | {qtd} downloads ===")
        for i in range(qtd):
            self._get(f"/api/files/{random.randint(1, 999)}", ip)
            if i < qtd - 1:
                time.sleep(intervalo)

    def acesso_usuario(self, user="felipe", ip=None):
        ip = ip or ip_desconhecido()
        print(f"\n=== cenario: USER-ACCESS | user={user} ip={ip} ===")
        self._get(f"/api/users/{random.randint(1, 999)}", ip)

    def combinado(self, user="felipe"):
        ip = ip_desconhecido()
        print(f"\n########## ATAQUE COMBINADO: {user} ##########")
        self.brute_force(user, ip)
        time.sleep(self.delay)
        self.ip_desconhecido_login(user, ip)
        time.sleep(self.delay)
        self.acesso_admin(user, ip)
        time.sleep(self.delay)
        self.download_anormal(user, ip)
        time.sleep(self.delay)
        self.acesso_usuario(user, ip)
        print(f"########## fim do ataque combinado: {user} ##########\n")


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
    parser.add_argument("scenario", choices=["brute-force", "unknown-ip", "off-hours",
                                              "admin-access", "abnormal-download", "combined"])
    parser.add_argument("--target", choices=["rust", "boneco"], default="rust",
                         help="rust = manda evento pronto pro Rust (/events). boneco = ataca a API .net direto")
    parser.add_argument("--url", default=os.environ.get("SENTINELA_TARGET_URL", URL_PADRAO))
    parser.add_argument("--base-url", default=os.environ.get("SENTINELA_BONECO_URL", BONECO_URL_PADRAO))
    parser.add_argument("--api-key", default=os.environ.get("SENTINELA_API_KEY"))
    parser.add_argument("--user", default="felipe")
    parser.add_argument("--ip", default=None)
    parser.add_argument("--attempts", type=int, default=5)
    parser.add_argument("--interval", type=float, default=2.0)
    parser.add_argument("--delay", type=float, default=1.0)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--attackers", type=int, default=1, help="quantos atacantes rodar em paralelo")
    args = parser.parse_args()

    if args.target == "boneco":
        attacker = BonecoAttacker(args.base_url, delay=args.delay, dry_run=args.dry_run)
    else:
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
