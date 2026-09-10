# Simulador de Atacante — Sentinela

 1. Visão Geral

O **Simulador de Atacante** é o componente responsável por gerar tráfego de eventos de segurança simulados, representando o comportamento de um ou mais usuários de risco dentro do ecossistema do Sentinela.


---

## 2. Objetivo

> **Gerar eventos técnicos realistas (login falho, IP desconhecido, acesso administrativo, download anormal) e enviá-los ao Rust Engine, permitindo validar todo o fluxo de detecção, correlação, regras e criação de incidentes do Sentinela.**

---

## 3. Onde se encaixa na arquitetura

```text
   Simulador (Python)
          │
          │  POST /events
          ▼
     Rust Engine
          │
          │  padrão detectado
          ▼
      Java API
          │
          ▼
     PostgreSQL
          │
          ▼
      Dashboard



 4. Cenários implementados

| Cenário | Evento gerado | Referência de risco (aplicada pelo Java) |
|---|---|---|
| `brute-force` | 5+ `LOGIN_FAILED`, mesmo user/ip, intervalo curto | +30 |
| `unknown-ip` | `LOGIN_SUCCESS` de IP fora do padrão | +20 |
| `off-hours` | `LOGIN_SUCCESS` fora do horário esperado | +15 |
| `admin-access` | `ADMIN_ACCESS` em ativo sensível | +30 |
| `abnormal-download` | Vários `FILE_DOWNLOAD` em sequência | +25 |
| `combined` | Todos os anteriores no mesmo usuário | soma > 100 → CRITICAL |



5. Contrato de integração

Formato do evento enviado ao Rust, alinhado ao contrato definido pela equipe Rust:

```json
{
  "id": "uuid-gerado",
  "event_type": "LOGIN_FAILED",
  "timestamp": "2026-09-10T02:31:47Z",
  "source": "attacker-simulator",
  "user": "felipe",
  "ip": "192.168.1.50",
  "asset": null
}
```
 6. Múltiplos atacantes

O simulador permite disparar **vários atacantes diferentes em paralelo** (`--attackers N`), cada um com usuário e IP distintos, rodando simultaneamente via threads. Isso serve para:

* Testar cenários mais realistas (mais de um usuário de risco ao mesmo tempo);
* Validar o processamento **concorrente** do Rust Engine, um dos pontos-chave da arquitetura (Tokio/Axum).

---

 7. Tecnologias

```text
Python 3
requests
argparse
concurrent.futures (ThreadPoolExecutor)
json, datetime (stdlib)
```

---

 8. Como testar sem depender do Rust pronto

Foi criado um mock receiver (`mock_receiver.py`) — um servidor HTTP simples que simula o endpoint `POST /events` do Rust. Ele:

* Recebe as requisições do simulador;
* Imprime o JSON recebido;
* Valida se todos os campos esperados do evento estão presentes;
* Responde com status 200.

Isso permitiu validar o simulador de ponta a ponta (formato de payload, timing entre eventos, múltiplos atacantes) sem esperar o Rust Engine estar pronto.

---
9. Como rodar

```bash
pip install requests

# ver os eventos sem enviar (teste local)
python attacker.py combined --dry-run

# enviar de verdade pro Rust (porta 3000 por padrão)
python attacker.py combined

# simular 5 atacantes diferentes em paralelo
python attacker.py combined --attackers 5

# apontar para outro destino, se necessário
python attacker.py brute-force --url http://localhost:3000/events
```

