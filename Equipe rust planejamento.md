# 🦀 Sentinela — Responsabilidades da Equipe Rust

## 1. Visão geral

O Rust será responsável pelo **Event Processing Engine** do Sentinela.

A função principal do Rust é ficar entre as fontes de eventos e o backend Java:

```text
Fontes / Simulador
        │
        ▼
   🦀 RUST ENGINE
        │
        │ HTTP/JSON
        ▼
    ☕ JAVA API
```

O Rust será responsável por **receber, validar, normalizar e processar eventos de segurança de forma eficiente e concorrente**, entregando ao Java eventos estruturados e informações técnicas relevantes.

A ideia é utilizar Rust onde ele realmente faz sentido:

> **Processamento de dados em movimento, concorrência, baixo overhead e alto throughput.**

---

# 2. O que o Rust NÃO fará

Para evitar que Rust e Java façam a mesma coisa, o Rust não será responsável por:

* autenticação de usuários da plataforma;
* JWT;
* cadastro de usuários;
* gerenciamento de analistas;
* regras de negócio;
* cálculo final do Risk Score;
* criação e gerenciamento de incidentes;
* investigação de incidentes;
* persistência dos dados de negócio;
* gerenciamento do dashboard;
* autorização de usuários;
* relatórios de negócio.

Essas responsabilidades pertencem ao Java.

O Rust deve ser enxergado como uma **pipeline de processamento de eventos**.

---

# 3. O que o Rust fará

As responsabilidades principais serão:

```text
1. Ingestão de eventos
2. Validação
3. Normalização
4. Filtragem
5. Processamento assíncrono
6. Rate Limiting
7. Correlação técnica de eventos
8. Identificação de padrões técnicos
9. Métricas
10. Comunicação com Java
```

Nem todas precisam estar prontas no primeiro dia.

Vamos implementar progressivamente.

---

# 4. Ingestão de eventos

A primeira função do Rust será receber eventos.

Inicialmente utilizaremos HTTP.

Exemplo:

```http
POST /events
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

O Rust receberá esse evento e iniciará o pipeline.

---

# 5. Modelo de evento

Precisamos definir um formato comum para os eventos.

Estrutura inicial:

```text
SecurityEvent
├── id
├── event_type
├── timestamp
├── source
├── user
├── ip
└── asset
```

Exemplos de `event_type`:

```text
LOGIN_FAILED
LOGIN_SUCCESS
PASSWORD_CHANGE
ADMIN_ACCESS
DATABASE_ACCESS
FILE_DOWNLOAD
LOGOUT
```

Esse formato será nosso **evento canônico**.

---

# 6. Validação

Depois de receber um evento, o Rust precisa verificar se ele é válido.

Exemplos:

```text
ID existe?
event_type existe?
timestamp é válido?
source existe?
IP possui formato válido?
```

Um evento inválido não deve continuar normalmente pela pipeline.

Exemplo:

```json
{
  "event_type": null,
  "timestamp": "banana"
}
```

Resultado:

```text
Evento inválido
      ↓
Rejeitado
      ↓
Log / métrica
```

---

# 7. Por que validar no Rust?

Porque queremos impedir que dados inválidos avancem pela pipeline.

Além disso, como o Rust estará próximo da ingestão, conseguimos rejeitar rapidamente dados incorretos antes de enviá-los para o Java.

Isso reduz:

```text
tráfego desnecessário
processamento desnecessário
dados inválidos no backend
```

---

# 8. Normalização

Eventos podem vir de fontes diferentes.

Por exemplo:

```text
Linux
Windows
Aplicação Web
Servidor
Banco de dados
Simulador
```

Cada fonte pode utilizar nomes diferentes.

Exemplo:

```text
Windows:
Failed Login

Linux:
authentication failure

Application:
LOGIN_FAILED
```

O Rust deve transformar essas representações em um formato comum:

```text
LOGIN_FAILED
```

Assim o Java não precisa entender dezenas de formatos diferentes.

---

# 9. Exemplo de normalização

Entrada:

```json
{
  "source": "windows",
  "message": "Failed login attempt for user felipe"
}
```

O Rust pode transformar em:

```json
{
  "event_type": "LOGIN_FAILED",
  "source": "windows",
  "user": "felipe"
}
```

Outro sistema:

```json
{
  "source": "linux",
  "message": "authentication failure user=felipe"
}
```

Também vira:

```json
{
  "event_type": "LOGIN_FAILED",
  "source": "linux",
  "user": "felipe"
}
```

O Java recebe o mesmo conceito independentemente da origem.

---

# 10. Filtragem

Nem todo evento necessariamente precisa chegar ao Java.

O Rust poderá descartar eventos que não sejam relevantes.

Exemplo:

```text
Evento:
HEARTBEAT

Importância para análise de segurança:
baixa
```

Dependendo das regras do sistema:

```text
Recebe
 ↓
Filtra
 ↓
Descarta
```

Enquanto:

```text
LOGIN_FAILED
ADMIN_ACCESS
DATABASE_ACCESS
FILE_DOWNLOAD
```

continuam na pipeline.

---

# 11. Processamento assíncrono

Essa será uma das partes mais importantes para justificar Rust.

O Rust não deve processar todos os eventos de maneira bloqueante.

Queremos:

```text
Evento 1 ──┐
Evento 2 ──┤
Evento 3 ──┼──> processamento concorrente
Evento 4 ──┤
Evento 5 ──┘
```

Utilizaremos:

```text
Tokio
async/await
tasks
channels
```

A ideia é permitir que o engine continue recebendo eventos enquanto outros estão sendo processados.

---

# 12. Pipeline interna

O processamento pode seguir este modelo:

```text
              Evento
                 │
                 ▼
          ┌─────────────┐
          │  Ingestion  │
          └──────┬──────┘
                 ▼
          ┌─────────────┐
          │ Validation  │
          └──────┬──────┘
                 ▼
          ┌─────────────┐
          │Normalization│
          └──────┬──────┘
                 ▼
          ┌─────────────┐
          │  Filtering  │
          └──────┬──────┘
                 ▼
          ┌─────────────┐
          │ Processing  │
          └──────┬──────┘
                 ▼
          ┌─────────────┐
          │ Correlation │
          └──────┬──────┘
                 ▼
             Java API
```

---

# 13. Rate Limiting

O Rust também poderá controlar a quantidade de eventos recebidos.

Imagine que uma máquina comece a enviar:

```text
100.000 eventos/segundo
```

Isso pode sobrecarregar o sistema.

O Rust poderá limitar:

```text
máximo:
10.000 eventos/segundo
```

Quando o limite for ultrapassado:

```text
Evento
  ↓
Rate Limiter
  ↓
limite excedido
  ↓
rejeitado / atrasado
```

Isso também protege o Java.

---

# 14. Correlação técnica

Aqui temos uma responsabilidade interessante para o Rust.

O Rust pode observar eventos relacionados em uma janela de tempo.

Exemplo:

```text
10:00:01 LOGIN_FAILED
10:00:05 LOGIN_FAILED
10:00:09 LOGIN_FAILED
10:00:12 LOGIN_FAILED
10:00:15 LOGIN_FAILED
```

O Rust percebe:

```text
5 LOGIN_FAILED
para o mesmo usuário/IP
em 14 segundos
```

Isso é um **padrão técnico**.

Ele pode gerar:

```json
{
  "pattern": "BRUTE_FORCE",
  "user": "felipe",
  "ip": "192.168.1.50",
  "attempts": 5,
  "window_seconds": 14
}
```

---

# 15. Importante: Rust não decide o incidente

Mesmo detectando:

```text
BRUTE_FORCE
```

o Rust não deve decidir:

```text
"Risk Score = 90"
```

ou:

```text
"Crie um incidente crítico."
```

Essa decisão pertence ao Java.

A divisão será:

```text
Rust:

"Detectei 5 LOGIN_FAILED em 14 segundos."

              ↓

Java:

"Isso corresponde à regra BRUTE_FORCE."

              ↓

"Essa regra adiciona +30 ao Risk Score."

              ↓

"Agora o incidente possui Risk Score 85."

              ↓

"É CRITICAL."
```

Essa separação é importante.

---

# 16. Estado temporário

Para fazer correlação, o Rust precisará manter algumas informações temporariamente em memória.

Por exemplo:

```text
user = felipe
ip = 192.168.1.50

LOGIN_FAILED:
10:00:01
10:00:05
10:00:09
10:00:12
```

O Rust pode manter esses eventos durante uma janela:

```text
60 segundos
```

Depois eles podem expirar.

Não precisamos colocar esse estado no PostgreSQL.

Para o MVP, memória é suficiente.

---

# 17. Expiração de eventos

O estado de correlação não pode crescer infinitamente.

Exemplo:

```text
Evento
 ↓
memória
 ↓
60 segundos
 ↓
expira
```

Isso evita:

```text
memória crescendo indefinidamente
```

Podemos utilizar estruturas adequadas para manter apenas a janela necessária.

---

# 18. Comunicação com Java

Depois do processamento, o Rust enviará o evento para o Java.

Exemplo:

```http
POST /api/events
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

Se o Rust detectar um padrão:

```json
{
  "pattern": "BRUTE_FORCE",
  "user": "felipe",
  "ip": "192.168.1.50",
  "attempts": 5,
  "window_seconds": 14
}
```

O Java então pode utilizar essa informação.

---

# 19. Comunicação confiável

O Rust deverá lidar com situações em que o Java esteja indisponível.

Exemplo:

```text
Rust
 ↓
POST Java
 ↓
Java está offline
```

O Rust não deve simplesmente entrar em panic.

Inicialmente podemos ter uma estratégia simples:

```text
tentativa
 ↓
falhou
 ↓
log do erro
 ↓
retry
```

Para o MVP não precisamos criar um sistema distribuído complexo.

Nada de Kafka só para resolver isso.

---

# 20. Logs

O Rust deverá utilizar `tracing`.

Precisamos conseguir observar:

```text
evento recebido
evento validado
evento rejeitado
evento normalizado
evento filtrado
evento processado
padrão detectado
evento enviado ao Java
erro de comunicação
```

Exemplo conceitual:

```text
INFO  event_received
INFO  event_processed
INFO  pattern_detected pattern=BRUTE_FORCE
INFO  event_sent_to_java
ERROR java_request_failed
```

Isso será muito útil durante a apresentação.

---

# 21. Métricas

O Rust deverá manter algumas métricas básicas.

Exemplos:

```text
events_received
events_processed
events_rejected
events_filtered
patterns_detected
events_sent
java_errors
```

Também podemos medir:

```text
processing_latency
events_per_second
```

Exemplo:

```text
Eventos recebidos:       10.000
Processados:              9.850
Rejeitados:                 100
Filtrados:                   50
Padrões detectados:          20
Erros Java:                   0
```

Isso ajuda a demonstrar que o Rust realmente possui uma função de processamento.

---

# 22. Estrutura sugerida do projeto

Começaremos simples:

```text
rust-engine/
│
├── Cargo.toml
│
└── src/
    ├── main.rs
    │
    ├── models/
    │   └── event.rs
    │
    ├── ingestion/
    │   └── mod.rs
    │
    ├── validation/
    │   └── mod.rs
    │
    ├── normalization/
    │   └── mod.rs
    │
    ├── processing/
    │   └── mod.rs
    │
    ├── correlation/
    │   └── mod.rs
    │
    ├── rate_limit/
    │   └── mod.rs
    │
    ├── services/
    │   └── java_client.rs
    │
    └── metrics/
        └── mod.rs
```

Essa estrutura pode mudar conforme o desenvolvimento.

Não precisamos criar todos esses módulos imediatamente.

---

# 23. Stack Rust

Para o MVP:

```text
Rust
Tokio
Axum
Serde
Serde JSON
Reqwest
UUID
Chrono
Tracing
Tracing Subscriber
```

Cada biblioteca possui uma função clara.

```text
Tokio
→ async/concurrency

Axum
→ HTTP server

Serde
→ JSON

Reqwest
→ comunicação com Java

UUID
→ identificação de eventos

Chrono
→ timestamps

Tracing
→ observabilidade
```

---

# 24. Primeira versão do Rust

A primeira versão será extremamente simples:

```text
POST /events
      ↓
Receber JSON
      ↓
Desserializar
      ↓
Validar
      ↓
Logar
      ↓
Enviar para Java
```

Nesse momento ainda não teremos:

```text
correlation
rate limiting
métricas avançadas
detecção de padrões
```

Vamos adicionar isso progressivamente.

---

# 25. Segunda versão

Depois:

```text
POST /events
      ↓
Validation
      ↓
Normalization
      ↓
Filtering
      ↓
Async Processing
      ↓
Java
```

---

# 26. Terceira versão

Depois adicionamos:

```text
Correlation
      ↓
Pattern Detection
      ↓
Java
```

Exemplo:

```text
5 LOGIN_FAILED
      ↓
Rust detecta
      ↓
BRUTE_FORCE
      ↓
Java
```

---

# 27. Versão final do MVP

O Rust deverá conseguir:

```text
                    EVENTOS
                       │
                       ▼
                ┌────────────┐
                │  INGESTION │
                └─────┬──────┘
                      ▼
                ┌────────────┐
                │ VALIDATION │
                └─────┬──────┘
                      ▼
                ┌────────────┐
                │NORMALIZATION│
                └─────┬──────┘
                      ▼
                ┌────────────┐
                │  FILTERING │
                └─────┬──────┘
                      ▼
                ┌────────────┐
                │  RATE LIMIT│
                └─────┬──────┘
                      ▼
                ┌────────────┐
                │   TOKIO    │
                │ processing │
                └─────┬──────┘
                      ▼
                ┌────────────┐
                │CORRELATION │
                └─────┬──────┘
                      ▼
                ┌────────────┐
                │  METRICS   │
                └─────┬──────┘
                      ▼
                 ☕ JAVA API
```

---

# 28. Exemplo de funcionamento completo

O simulador envia:

```text
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_SUCCESS
ADMIN_ACCESS
```

O Rust:

```text
Recebe eventos
      ↓
Valida
      ↓
Normaliza
      ↓
Processa concorrentemente
      ↓
Correlaciona
      ↓
Detecta:

BRUTE_FORCE
```

Então envia para o Java:

```text
Eventos processados
+
BRUTE_FORCE pattern
```

O Java então:

```text
Aplica regra
      ↓
+30 Risk Score
      ↓
Combina com outras regras
      ↓
Calcula Risk Score final
      ↓
Cria incidente
```

---

# 29. Divisão final entre Rust e Java

### 🦀 Rust

> **"O que aconteceu tecnicamente?"**

```text
Receber
Validar
Normalizar
Filtrar
Processar
Correlacionar
Detectar padrões técnicos
Controlar fluxo
Medir processamento
Enviar para Java
```

### ☕ Java

> **"O que isso significa para a segurança da empresa?"**

```text
Aplicar regras
Calcular Risk Score
Classificar severidade
Criar incidente
Gerenciar incidente
Investigar
Gerenciar usuários
Gerenciar ativos
Persistir
Disponibilizar dashboard
```

---

# 30. Contrato entre as equipes

O contrato principal será:

```text
🦀 Rust
    │
    │ HTTP + JSON
    ▼
☕ Java
```

Rust não precisa conhecer:

```text
PostgreSQL
JPA
Spring Security
JWT
Dashboard
```

Java não precisa conhecer:

```text
Tokio
Axum
workers
channels
estruturas internas de correlação
```

Eles precisam concordar principalmente sobre o **formato dos dados enviados**.

---

# 31. Ordem de desenvolvimento da equipe Rust

## Etapa 1 — Servidor

```text
1. Criar projeto
2. Configurar Tokio
3. Configurar Axum
4. Criar GET /health
```

Objetivo:

> Ter o Rust Engine funcionando.

---

## Etapa 2 — Eventos

```text
1. Criar SecurityEvent
2. Criar POST /events
3. Receber JSON
4. Desserializar
5. Logar evento
```

Objetivo:

> Rust consegue receber eventos.

---

## Etapa 3 — Pipeline

```text
1. Validation
2. Normalization
3. Filtering
```

Objetivo:

> Evento passa por uma pipeline real de processamento.

---

## Etapa 4 — Comunicação

```text
Rust
 ↓
Reqwest
 ↓
Java
```

Objetivo:

> Rust consegue entregar eventos ao backend Java.

---

## Etapa 5 — Concorrência

```text
Tokio
async/await
tasks
channels
```

Objetivo:

> Processar múltiplos eventos sem bloquear a ingestão.

---

## Etapa 6 — Rate Limiting

Objetivo:

> Evitar que uma fonte sobrecarregue o engine.

---

## Etapa 7 — Correlação

Implementar inicialmente:

```text
5 LOGIN_FAILED
em até 60 segundos
para mesmo usuário/IP
```

Resultado:

```text
BRUTE_FORCE
```

---

## Etapa 8 — Métricas

Adicionar:

```text
received
processed
rejected
filtered
patterns_detected
java_errors
latency
```

---

# 32. Prioridade

Como temos prazo curto, a prioridade será:

```text
🔥 ESSENCIAL

1. Ingestion
2. Event Model
3. Validation
4. Normalization
5. Processing
6. Java Communication
```

Depois:

```text
⚡ IMPORTANTE

7. Async Processing
8. Correlation
9. Rate Limiting
10. Metrics
```

E somente se houver tempo:

```text
🟢 EXTRA

11. Retry mais elaborado
12. Buffer em memória
13. Mais padrões
14. Otimizações
15. Testes de carga
```

Não devemos sacrificar o sistema funcional para implementar extras.

---

# 33. Resultado esperado

Ao final, o Rust deverá ser um **serviço independente** que pode ser executado ao lado do Java:

```text
┌──────────────────────────────┐
│       CyberSOC               │
│                              │
│  🦀 Rust Engine              │
│                              │
│  Event Processing            │
│                              │
│  HTTP :3000                  │
└──────────────┬───────────────┘
               │
               │ HTTP
               ▼
┌──────────────────────────────┐
│       ☕ Java API            │
│                              │
│  Security / Business         │
│                              │
│  HTTP :8080                  │
└──────────────┬───────────────┘
               │
               ▼
          PostgreSQL
```

A justificativa técnica do Rust fica clara:

> **O Rust é o motor responsável por processar a grande quantidade de eventos antes que eles cheguem à camada de negócio.**

E a justificativa do Java também fica clara:

> **O Java transforma os eventos processados em decisões de segurança, incidentes e informações para o analista.**

Esse é o limite que devemos tentar preservar durante o desenvolvimento para que nenhuma das duas equipes fique fazendo o trabalho da outra.
