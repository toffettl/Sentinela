# Sentinela

## 1. Visão Geral

O **Sentinela** é uma plataforma de monitoramento e análise de eventos de segurança, desenvolvida com o objetivo de auxiliar empresas na identificação e investigação de comportamentos potencialmente suspeitos em seus sistemas.

A plataforma recebe eventos gerados por aplicações, servidores e outros ativos, processa essas informações, identifica padrões suspeitos e apresenta os resultados como ocorrências de segurança para análise.

A ideia é aproximar o funcionamento do sistema de um **SOC (Security Operations Center)**, criando uma solução capaz de centralizar eventos e auxiliar um analista de segurança na identificação de possíveis ameaças.

---

## 2. Problema

Em um ambiente corporativo, diferentes sistemas podem gerar uma grande quantidade de eventos:

* Tentativas de login;
* Falhas de autenticação;
* Acessos administrativos;
* Acessos fora do horário esperado;
* Alterações em sistemas;
* Downloads incomuns;
* Acessos provenientes de IPs desconhecidos;
* Eventos relacionados a servidores e dispositivos.

Analisar todos esses eventos manualmente pode ser difícil e demorado.

O Sentinela busca solucionar esse problema centralizando os eventos e aplicando processamento e regras para destacar comportamentos que merecem atenção.

---

## 3. Objetivo

O principal objetivo do Sentinela é:

> **Receber, processar e analisar eventos de segurança, identificando comportamentos suspeitos e transformando-os em informações úteis para analistas de segurança.**

O sistema deve permitir que um analista consiga visualizar:

* O que aconteceu;
* Quando aconteceu;
* Onde aconteceu;
* Qual usuário ou ativo estava envolvido;
* Por que o comportamento foi considerado suspeito;
* Qual o nível de risco associado;
* Quais eventos contribuíram para a ocorrência;
* Qual o estado atual da ocorrência;
* Quais ações de investigação foram realizadas.

---

## 4. Arquitetura

A arquitetura principal do Sentinela será dividida entre **Rust** e **Java**.

```text
                Fontes de Eventos
                       │
                       ▼
              ┌─────────────────┐
              │  Rust Engine    │
              │                 │
              │ Ingestão        │
              │ Validação       │
              │ Normalização    │
              │ Filtragem       │
              │ Processamento   │
              │ Correlação      │
              │ Pattern Detect. │
              └────────┬────────┘
                       │
                    HTTP/JSON
                       │
                       ▼
              ┌─────────────────┐
              │    Java API     │
              │                 │
              │ Regras          │
              │ Risk Score      │
              │ Incidentes      │
              │ Investigação    │
              │ Usuários        │
              │ Ativos          │
              └────────┬────────┘
                       │
                       ▼
                 PostgreSQL
                       │
                       ▼
                  Dashboard
```

---

## 5. Separação entre Rust e Java

A divisão das responsabilidades é um dos principais pontos arquiteturais do projeto.

### Rust

Rust será responsável principalmente pelo **processamento técnico dos eventos**.

A pergunta que o Rust responde é:

> **"O que aconteceu tecnicamente?"**

Exemplos:

* Receber um evento;
* Validar sua estrutura;
* Normalizar seus dados;
* Filtrar eventos irrelevantes;
* Processar eventos de forma assíncrona;
* Controlar concorrência;
* Aplicar rate limiting;
* Correlacionar eventos próximos no tempo;
* Identificar padrões técnicos.

### Java

Java será responsável pelas **decisões de negócio e segurança**.

A pergunta que o Java responde é:

> **"O que esse comportamento significa para a segurança da empresa?"**

Exemplos:

* Aplicar regras de segurança;
* Calcular Risk Score;
* Definir severidade;
* Criar e gerenciar incidentes;
* Relacionar eventos com incidentes;
* Gerenciar investigação;
* Gerenciar usuários e permissões;
* Gerenciar ativos;
* Disponibilizar dados para o dashboard.

Essa separação evita transformar o Rust em uma segunda aplicação de negócio e dá uma responsabilidade técnica real para cada tecnologia.

---

## 6. Exemplo de Funcionamento

Imagine que um usuário tenha realizado cinco tentativas de login malsucedidas em poucos segundos.

Os eventos podem chegar ao Rust:

```text
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
```

O Rust pode identificar:

```text
5 LOGIN_FAILED
mesmo usuário
mesmo IP
intervalo inferior a 60 segundos
```

E produzir um padrão técnico:

```text
BRUTE_FORCE
```

O Java recebe essa informação e aplica as regras de segurança.

Por exemplo:

```text
BRUTE_FORCE → +30 Risk Score
```

Caso outros comportamentos suspeitos também estejam presentes, o Java pode aumentar o risco e criar um incidente.

---

## 7. Evento, Padrão e Incidente

O projeto diferencia três conceitos importantes.

### Evento

Representa algo que aconteceu.

```text
LOGIN_FAILED
```

### Padrão

Representa uma combinação de eventos que apresenta comportamento suspeito.

```text
5 LOGIN_FAILED em 60 segundos
→ BRUTE_FORCE
```

### Incidente

Representa uma ocorrência de segurança que merece acompanhamento.

```text
Possível comprometimento de conta
Risk Score: 94
Severidade: CRITICAL
Status: OPEN
```

Essa distinção permite que o sistema processe milhares de eventos sem transformar cada evento individual em um incidente.

---

## 8. Risk Score

O Sentinela utilizará um sistema de pontuação para representar o risco de determinados comportamentos.

Exemplo:

```text
Brute Force          +30
IP desconhecido      +20
Login fora de hora   +15
Acesso privilegiado  +30
Download anormal     +25
```

O score pode ser limitado a 100.

```text
0–29    → NORMAL
30–59   → LOW
60–79   → SUSPICIOUS
80–100  → CRITICAL
```

O cálculo final pertence ao **Java**, pois representa uma decisão de segurança e negócio.

---

## 9. Incidentes

Quando o comportamento atingir determinadas condições, o sistema poderá criar um incidente.

Um incidente deverá permitir informações como:

```text
ID
Título
Descrição
Severidade
Risk Score
Status
Data de criação
Data de atualização
Usuário relacionado
Ativo relacionado
```

Também deverá ser possível identificar:

```text
Incidente
   │
   ├── Eventos relacionados
   │
   ├── Regras acionadas
   │
   └── Investigação
```

---

## 10. Investigação

O Sentinela também terá uma área destinada à investigação dos incidentes.

O objetivo do MVP não é criar uma plataforma completa de forense digital.

A ideia é permitir que o analista consiga:

* Visualizar informações do incidente;
* Visualizar eventos relacionados;
* Visualizar regras acionadas;
* Adicionar observações;
* Alterar o status;
* Registrar ações da investigação;
* Encerrar ou manter o incidente aberto.

---

## 11. Tecnologias

### Rust

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

Rust será utilizado principalmente para infraestrutura e processamento de eventos.

### Java

```text
Java 21
Spring Boot
Spring Web
Spring Security
Spring Data JPA
Bean Validation
JWT
Flyway
PostgreSQL
```

Java será responsável pela API de negócio e pelas regras de segurança.

---

## 12. Comunicação

Inicialmente, a comunicação entre Rust e Java será feita diretamente através de:

```text
HTTP + JSON
```

Fluxo:

```text
Evento
  ↓
Rust
  ↓
Processamento
  ↓
HTTP POST
  ↓
Java API
  ↓
Regras
  ↓
Risk Score
  ↓
Incidente
  ↓
PostgreSQL
```

Não será utilizado Kafka ou RabbitMQ no MVP.

A intenção é manter a arquitetura simples o suficiente para que o time consiga implementar e demonstrar o funcionamento completo dentro do prazo.

---

## 13. Responsabilidade do Rust

O Rust Engine terá como principais responsabilidades:

1. Ingestão de eventos;
2. Validação;
3. Normalização;
4. Filtragem;
5. Processamento assíncrono;
6. Controle de concorrência;
7. Rate limiting;
8. Correlação temporal;
9. Detecção de padrões técnicos;
10. Coleta de métricas;
11. Comunicação com a API Java.

O Rust **não será responsável por**:

* Risk Score final;
* Criação de incidentes;
* Autenticação de usuários;
* Gerenciamento de usuários;
* Regras de negócio;
* Persistência principal dos dados;
* Dashboard.

---

## 14. Responsabilidade do Java

A API Java será responsável por:

1. Receber eventos processados;
2. Persistir informações;
3. Gerenciar regras de segurança;
4. Calcular Risk Score;
5. Classificar severidade;
6. Criar incidentes;
7. Gerenciar incidentes;
8. Gerenciar investigação;
9. Gerenciar usuários;
10. Gerenciar autenticação e autorização;
11. Gerenciar ativos;
12. Disponibilizar informações para o dashboard.

---

## 15. Objetivo do MVP

O MVP não precisa representar um SOC empresarial completo.

O objetivo é conseguir demonstrar um fluxo funcional de ponta a ponta:

```text
Evento gerado
     ↓
Rust recebe
     ↓
Rust processa
     ↓
Rust identifica padrão suspeito
     ↓
Java recebe
     ↓
Java aplica regra
     ↓
Risk Score calculado
     ↓
Incidente criado
     ↓
Analista visualiza
     ↓
Analista investiga
```

Se esse fluxo estiver funcionando, o projeto já demonstra claramente a proposta do Sentinela.

---

## 16. Fluxo de Demonstração

Um cenário interessante para apresentação seria:

```text
1. Simulador gera LOGIN_FAILED

2. Rust recebe o evento

3. Outros LOGIN_FAILED são enviados

4. Rust identifica:
   5 tentativas em menos de 60 segundos

5. Rust gera:
   BRUTE_FORCE

6. Java recebe o padrão

7. Regra de segurança é acionada

8. Risk Score aumenta

9. Java cria um incidente

10. Dashboard apresenta:

    POSSÍVEL BRUTE FORCE

    Risk Score: 30+
    Severidade: LOW/SUSPICIOUS
    Usuário: felipe
    IP: 192.168.1.50

11. Analista abre o incidente

12. Visualiza os eventos relacionados

13. Registra a investigação
```

Esse cenário demonstra **Rust + Java + regras + Risk Score + incidentes + investigação** em um único fluxo.

---

## 17. Resultado Esperado

Ao final do projeto, o Sentinela deverá funcionar como uma plataforma capaz de transformar:

```text
Grande quantidade de eventos técnicos
                ↓
        Processamento
                ↓
       Padrões suspeitos
                ↓
       Regras de segurança
                ↓
          Risk Score
                ↓
           Incidentes
                ↓
          Investigação
```

O diferencial arquitetural do projeto será justamente a divisão entre o **processamento de infraestrutura de alto desempenho em Rust** e a **camada de negócio e segurança em Java**.

O objetivo não é simplesmente utilizar duas linguagens, mas fazer com que cada uma tenha uma responsabilidade clara dentro do sistema.
