# Sentinela
# ☕ Sentinela — Responsabilidades da Equipe Java

## 1. Visão geral

O Java será responsável pela **camada de negócio do Sentinela**.

Enquanto o Rust ficará responsável por receber e processar tecnicamente os eventos de segurança, o Java será responsável por responder:

> **"O que esse evento significa para a segurança da empresa?"**

O Java deverá receber eventos processados pelo Rust, aplicar regras de segurança, calcular o **Risk Score**, identificar possíveis incidentes, armazenar as informações e disponibilizá-las para o dashboard e para o analista de segurança.

Arquitetura:

```text
Fontes / Simulador
        │
        ▼
   🦀 Rust Engine
        │
        │ HTTP
        ▼
   ☕ Java API
        │
        ├── Regras de segurança
        ├── Risk Score
        ├── Incidentes
        ├── Investigação
        ├── Usuários
        ├── Ativos
        └── Persistência
                │
                ▼
           PostgreSQL
                │
                ▼
           Dashboard
```

---

# 2. O que o Java NÃO fará

Para evitar sobreposição com o Rust:

O Java **não precisa** ser responsável por:

* receber milhares de eventos diretamente das fontes;
* processamento concorrente de alto volume;
* normalização de diferentes formatos de log;
* rate limiting da ingestão;
* filtragem técnica de eventos;
* processamento assíncrono da pipeline;
* gerenciamento de workers da ingestão;
* parsing de logs de diferentes sistemas.

Essas responsabilidades pertencem ao Rust.

O Java recebe um **evento já processado e normalizado**.

---

# 3. O que o Java fará

O Java terá principalmente estas responsabilidades:

```text
1. Receber eventos do Rust
2. Persistir eventos
3. Manter regras de segurança
4. Analisar eventos segundo essas regras
5. Calcular Risk Score
6. Criar incidentes
7. Classificar severidade
8. Relacionar eventos aos incidentes
9. Permitir investigação
10. Gerenciar usuários e ativos
11. Disponibilizar dados para o dashboard
```

---

# 4. Modelo conceitual

É importante diferenciar três coisas:

### Event

É algo que aconteceu.

Exemplo:

```text
LOGIN_FAILED
```

### Pattern

É um comportamento identificado a partir de eventos.

Exemplo:

```text
5 LOGIN_FAILED em 60 segundos
```

### Incident

É uma ocorrência de segurança que merece acompanhamento.

Exemplo:

```text
Possível comprometimento de conta
Risk Score: 92
Severidade: CRITICAL
```

A responsabilidade do Java começa principalmente quando precisamos transformar **eventos/padrões em significado de negócio e segurança**.

---

# 5. Recebimento de eventos

O Rust enviará eventos para o Java através de HTTP.

Exemplo:

```http
POST /api/events
Content-Type: application/json
```

Payload:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "event_type": "LOGIN_FAILED",
  "timestamp": "2026-09-05T18:30:00Z",
  "source": "linux-server",
  "user": "felipe",
  "ip": "192.168.1.50",
  "asset": "server-01"
}
```

O Java deverá:

1. receber;
2. validar;
3. persistir;
4. analisar segundo as regras;
5. atualizar o Risk Score;
6. verificar se precisa criar/atualizar um incidente.

---

# 6. Entidade Event

Criar uma entidade para representar os eventos recebidos.

Campos mínimos:

```text
Event
├── id
├── eventType
├── timestamp
├── source
├── user
├── ip
└── asset
```

Exemplos de `eventType`:

```text
LOGIN_FAILED
LOGIN_SUCCESS
PASSWORD_CHANGE
ADMIN_ACCESS
DATABASE_ACCESS
FILE_DOWNLOAD
LOGOUT
```

Não precisamos criar dezenas de tipos agora.

---

# 7. Regras de segurança

O Java terá um conjunto de regras que determina quando um comportamento é suspeito.

Exemplos iniciais:

### Regra 1 — Brute Force

```text
5 ou mais LOGIN_FAILED
dentro de 60 segundos
```

Resultado:

```text
+30 Risk Score
```

---

### Regra 2 — Login fora do horário

```text
LOGIN_SUCCESS
fora do horário comercial
```

Resultado:

```text
+15 Risk Score
```

---

### Regra 3 — IP desconhecido

```text
LOGIN_SUCCESS
originado de IP não reconhecido
```

Resultado:

```text
+20 Risk Score
```

---

### Regra 4 — Download anormal

```text
FILE_DOWNLOAD
com volume muito acima do normal
```

Resultado:

```text
+25 Risk Score
```

---

### Regra 5 — Acesso privilegiado

```text
ADMIN_ACCESS
```

Resultado:

```text
+30 Risk Score
```

---

# 8. Risk Score

O Java deverá possuir um componente responsável por calcular o risco.

Exemplo:

```text
Brute Force        +30
Unknown IP         +20
Admin Access       +30
Abnormal Download  +25
-----------------------
Total              105
```

Como o máximo será 100:

```text
Risk Score = 100
```

Faixas:

```text
0 - 29    → NORMAL
30 - 59   → LOW
60 - 79   → SUSPICIOUS
80 - 100  → CRITICAL
```

O cálculo deve ficar isolado em um serviço próprio.

Por exemplo:

```text
RiskScoreService
```

A ideia não é colocar esse cálculo dentro do Controller.

---

# 9. Criação de incidentes

Quando o comportamento atingir determinadas condições, o Java deverá criar um incidente.

Exemplo:

```text
Eventos:

LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_SUCCESS
ADMIN_ACCESS
DATABASE_ACCESS
FILE_DOWNLOAD
```

O sistema identifica:

```text
Brute Force
+
Login suspeito
+
Acesso administrativo
+
Acesso ao banco
+
Download anormal
```

E cria:

```text
Incident
────────────────────────────
Título:
Possível comprometimento de conta

Risk Score:
95

Severity:
CRITICAL

Status:
OPEN
```

---

# 10. Entidade Incident

Campos mínimos:

```text
Incident
├── id
├── title
├── description
├── severity
├── riskScore
├── status
├── createdAt
├── updatedAt
└── user/asset relacionado
```

Status:

```text
OPEN
INVESTIGATING
RESOLVED
FALSE_POSITIVE
```

Severity:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

---

# 11. Relacionamento Incident ↔ Event

Um incidente precisa saber **quais eventos contribuíram para sua criação**.

Exemplo:

```text
INCIDENT #123
│
├── LOGIN_FAILED
├── LOGIN_FAILED
├── LOGIN_FAILED
├── LOGIN_SUCCESS
├── ADMIN_ACCESS
└── FILE_DOWNLOAD
```

Por isso devemos ter uma relação:

```text
Incident 1 ─────── N Events
```

ou uma tabela intermediária:

```text
incident_events
```

Isso será importante para a tela de investigação.

---

# 12. Regras relacionadas ao incidente

Também é interessante registrar **quais regras foram disparadas**.

Exemplo:

```text
Incident #123

Regras acionadas:

✓ Brute Force              +30
✓ Unknown IP               +20
✓ Privileged Access        +30
✓ Abnormal Download        +25
```

Isso permite mostrar para o analista:

> "Por que o sistema classificou isso como crítico?"

---

# 13. Investigação

A investigação será uma funcionalidade simples para o MVP.

O analista poderá abrir um incidente e visualizar:

```text
Incident #123

Risk Score: 95
Severity: CRITICAL
Status: INVESTIGATING

Usuário:
felipe

IP:
192.168.1.50

Asset:
server-01

Regras acionadas:
- Brute Force
- Unknown IP
- Privileged Access

Eventos relacionados:
10:01 LOGIN_FAILED
10:02 LOGIN_FAILED
10:02 LOGIN_FAILED
10:03 LOGIN_SUCCESS
10:04 ADMIN_ACCESS
```

Também poderá adicionar uma observação:

```text
"Conta bloqueada e credenciais redefinidas."
```

E alterar o status:

```text
OPEN
    ↓
INVESTIGATING
    ↓
RESOLVED
```

Não precisamos implementar uma ferramenta completa de forense digital.

---

# 14. Usuários

O Java também será responsável pelos usuários da plataforma.

Exemplo:

```text
User
├── id
├── name
├── email
├── password
└── role
```

Roles:

```text
ADMIN
ANALYST
```

O objetivo é permitir que apenas usuários autorizados acessem determinadas funcionalidades.

---

# 15. Autenticação

Para o MVP:

```text
Spring Security
+
JWT
```

Fluxo:

```text
POST /auth/login
       │
       ▼
   valida usuário
       │
       ▼
   gera JWT
       │
       ▼
 Dashboard/API
```

Não precisamos criar OAuth, SSO ou integração com provedores externos.

---

# 16. Assets

O sistema também precisa saber quais máquinas/sistemas estão sendo monitorados.

Exemplo:

```text
Asset
├── id
├── name
├── hostname
├── ip
├── operatingSystem
└── status
```

Exemplos:

```text
server-01
server-02
database-prod
web-server
```

Isso permite responder:

> "Qual máquina está sofrendo o ataque?"

---

# 17. Dashboard API

O Java deverá disponibilizar endpoints para o frontend.

Exemplos:

```http
GET /api/events
GET /api/events/{id}

GET /api/incidents
GET /api/incidents/{id}

GET /api/incidents/{id}/events

GET /api/assets

GET /api/dashboard/summary
```

---

# 18. Dashboard Summary

Um endpoint pode retornar informações como:

```json
{
  "totalEvents": 15234,
  "totalIncidents": 27,
  "criticalIncidents": 4,
  "highIncidents": 8,
  "activeAssets": 12
}
```

Isso alimentará os cards do dashboard.

---

# 19. Endpoint do incidente

Exemplo:

```http
GET /api/incidents/123
```

Resposta:

```json
{
  "id": 123,
  "title": "Possível comprometimento de conta",
  "severity": "CRITICAL",
  "riskScore": 95,
  "status": "INVESTIGATING",
  "user": "felipe",
  "asset": "server-01",
  "rules": [
    "BRUTE_FORCE",
    "UNKNOWN_IP",
    "PRIVILEGED_ACCESS"
  ]
}
```

---

# 20. Endpoint para atualizar incidente

O analista poderá alterar o status:

```http
PATCH /api/incidents/{id}
```

Exemplo:

```json
{
  "status": "RESOLVED"
}
```

---

# 21. Banco de dados

Para o MVP:

```text
PostgreSQL
```

Entidades principais:

```text
users
assets
events
security_rules
incidents
incident_events
incident_rules
investigations
```

Não precisamos criar tabelas que não tenham uso real.

---

# 22. Stack sugerida

Java:

```text
Java 21
Spring Boot
Spring Web
Spring Security
Spring Data JPA
PostgreSQL
Flyway
Bean Validation
JWT
```

Não precisamos adicionar tecnologias só para deixar o projeto "bonito".

---

# 23. Organização sugerida

Uma estrutura simples:

```text
src/main/java/.../

├── auth/
├── user/
├── asset/
├── event/
├── rule/
├── risk/
├── incident/
├── investigation/
└── dashboard/
```

Cada módulo pode ter:

```text
controller
service
repository
entity
dto
```

Não precisa aplicar arquitetura extremamente complexa.

---

# 24. Fluxo completo

O fluxo principal que precisamos fazer funcionar é:

```text
             SIMULADOR
                 │
                 ▼
          🦀 RUST ENGINE
                 │
       recebe/processa eventos
                 │
                 ▼
             HTTP
                 │
                 ▼
           ☕ JAVA API
                 │
                 ▼
             EventService
                 │
        ┌────────┴────────┐
        ▼                 ▼
    PostgreSQL       Security Rules
                          │
                          ▼
                    Risk Score
                          │
                          ▼
                     Incident
                          │
                          ▼
                    PostgreSQL
                          │
                          ▼
                     Dashboard
```

---

# 25. Exemplo completo

O simulador envia:

```text
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_SUCCESS
ADMIN_ACCESS
DATABASE_ACCESS
FILE_DOWNLOAD
```

Rust:

```text
Recebe
↓
Valida
↓
Normaliza
↓
Processa
↓
Envia para Java
```

Java:

```text
Recebe eventos
↓
Persiste
↓
Aplica regras
↓
Calcula Risk Score
↓
Identifica incidente
↓
Persiste incidente
```

Resultado:

```text
🚨 INCIDENTE CRÍTICO

Possível comprometimento de conta

Risk Score: 95/100

Usuário: felipe
IP: 192.168.1.50
Asset: server-01

Regras:

+30 Brute Force
+20 Unknown IP
+30 Privileged Access
+15 Abnormal Behavior
```

O dashboard então mostra o incidente para o analista.

---

# 26. Ordem de desenvolvimento da equipe Java

Para não tentarem desenvolver tudo de uma vez:

## Semana 1 — Base

Implementar:

```text
1. Spring Boot
2. PostgreSQL
3. Estrutura dos módulos
4. Event Entity
5. Event Repository
6. Event Service
7. POST /api/events
8. GET /api/events
```

Objetivo:

> Conseguir receber um evento e salvá-lo no banco.

---

## Semana 2 — Segurança

Implementar:

```text
1. Security Rules
2. Rule Engine
3. Risk Score
4. Incident Entity
5. Incident Service
6. Incident/Event relationship
7. Criação automática de incidentes
```

Objetivo:

> Receber eventos e transformar comportamento suspeito em incidente.

---

## Semana 3 — Produto

Implementar:

```text
1. Authentication
2. Users
3. Assets
4. Investigation
5. Dashboard endpoints
6. Filtros
7. Status dos incidentes
```

Objetivo:

> Transformar o backend em uma aplicação utilizável pelo analista.

---

# 27. Contrato entre Rust e Java

Esse é o ponto mais importante para as duas equipes trabalharem em paralelo.

O Rust precisa entregar algo neste formato:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "event_type": "LOGIN_FAILED",
  "timestamp": "2026-09-05T18:30:00Z",
  "source": "linux-server",
  "user": "felipe",
  "ip": "192.168.1.50",
  "asset": "server-01"
}
```

E o Java deve disponibilizar:

```http
POST /api/events
```

Assim, as equipes conseguem desenvolver **independentemente**.

O Rust não precisa conhecer o banco do Java.

O Java não precisa conhecer como o Rust processou internamente o evento.

O único contrato obrigatório entre eles é:

```text
Rust → HTTP/JSON → Java
```

---

# 28. Divisão final de responsabilidades

### 🦀 Rust

> **Event Processing Engine**

```text
Ingestion
Validation
Normalization
Filtering
Async Processing
Rate Limiting
Technical Correlation
Metrics
```

### ☕ Java

> **Security & Business Engine**

```text
Security Rules
Risk Score
Incident Detection
Incident Management
Investigation
Users
Assets
Authentication
Persistence
Dashboard API
```

A regra que devemos manter durante o desenvolvimento é:

> **Rust processa o evento. Java interpreta o risco.**

Se alguma funcionalidade não se encaixar claramente nessa divisão, devemos discutir antes de simplesmente colocar em uma das partes.
