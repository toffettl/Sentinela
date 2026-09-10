#!/usr/bin/env python3
"""Servidor fake pra testar o attacker.py sem depender do Rust/Java prontos."""

import argparse
import json
from datetime import datetime
from http.server import BaseHTTPRequestHandler, HTTPServer

EXPECTED_FIELDS = {"id", "event_type", "timestamp", "source", "user", "ip", "asset"}


class Handler(BaseHTTPRequestHandler):
    def log_message(self, fmt, *args):
        pass

    def do_POST(self):
        length = int(self.headers.get("Content-Length", 0))
        body = self.rfile.read(length)
        ts = datetime.now().strftime("%H:%M:%S")
        print(f"\n[{ts}] POST {self.path}")

        try:
            payload = json.loads(body.decode("utf-8"))
        except json.JSONDecodeError:
            print("  body invalido")
            self._respond(400, {"error": "invalid json"})
            return

        print(f"  {json.dumps(payload, ensure_ascii=False)}")

        missing = EXPECTED_FIELDS - set(payload.keys())
        if missing:
            print(f"  campos faltando: {missing}")
        else:
            print("  ok, schema completo")

        self._respond(200, {"status": "received", "id": payload.get("id")})

    def _respond(self, status, body):
        data = json.dumps(body).encode("utf-8")
        self.send_response(status)
        self.send_header("Content-Type", "application/json")
        self.send_header("Content-Length", str(len(data)))
        self.end_headers()
        self.wfile.write(data)


def main():
    p = argparse.ArgumentParser()
    p.add_argument("--port", type=int, default=3000)
    args = p.parse_args()

    server = HTTPServer(("localhost", args.port), Handler)
    print(f"escutando em http://localhost:{args.port}\n")
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        print("\nencerrado")
        server.shutdown()


if __name__ == "__main__":
    main()
