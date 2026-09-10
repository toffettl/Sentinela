# Boneco de Testes — Sentinela

## 1. Objetivo

O Boneco de Testes será uma aplicação **ASP.NET Core Web API** utilizada para simular uma aplicação real dentro do ambiente monitorado pelo Sentinela.

A aplicação terá endpoints simples que simulam operações comuns de uma aplicação, como:

* Login de usuários.
* Acesso a área administrativa.
* Acesso a arquivos.
* Consulta de usuários.

A responsabilidade do Boneco **não é detectar ataques**.

Ele apenas executará as operações normalmente e registrará acontecimentos relevantes em um arquivo de log.

O **Rust Agent** será responsável por observar esse arquivo, ler os novos registros e transformá-los em eventos de segurança.

---

## 2. Papel do Boneco na Arquitetura

O Boneco representa uma aplicação real que uma empresa poderia possuir.

O objetivo é demonstrar uma das principais ideias do Sentinela:

> A aplicação não precisa conhecer o Sentinela nem fazer chamadas diretamente para ele.

O fluxo será:

```text
                    Python Attacker
                          │
                          │ HTTP
                          ▼
                ┌───────────────────┐
                │   .NET Test API   │
                │                   │
                │  Login            │
                │  Admin            │
                │  Files            │
                │  Users            │
                └─────────┬─────────┘
                          │
                          │ escreve logs
                          ▼
                    ┌───────────┐
                    │  app.log  │
                    └─────┬─────┘
                          │
                          │ lê
                          ▼
                ┌───────────────────┐
                │    Rust Agent     │
                └───────────────────┘
```

A aplicação .NET não terá integração direta com o Rust.

---

## 3. Tecnologias

O Boneco utilizará:

```text
ASP.NET Core
        │
        ├── Web API
        │
        ├── Controllers
        │
        └── Swagger

Serilog
        │
        └── File Sink
              │
              └── app.log
```

### ASP.NET Core

Será responsável pela API HTTP e pelos endpoints.

### Serilog

Será responsável pelo sistema de logs.

O Serilog permitirá:

* Registrar eventos.
* Escrever no console.
* Escrever em arquivo.
* Utilizar logs estruturados.
* Gerar logs em JSON.

### JSON

Os logs serão preferencialmente estruturados em JSON.

Isso facilita o trabalho do Rust Agent, pois cada linha do arquivo poderá representar um evento que pode ser desserializado.

---

# 4. Escopo do MVP

O Boneco deve ser propositalmente simples.

### Deve possuir

* API ASP.NET Core.
* Swagger.
* Serilog.
* Escrita de logs em arquivo.
* Logs estruturados em JSON.
* Endpoint de login.
* Endpoint de acesso administrativo.
* Endpoint de acesso a arquivos.
* Endpoint de acesso a usuários.
* Logs de sucesso e falha.
* Informações suficientes para o Rust transformar o log em `SecurityEvent`.

### Não deve possuir

* Banco de dados.
* Entity Framework.
* JWT.
* ASP.NET Identity.
* Sistema real de autenticação.
* Sistema de autorização complexo.
* Risk Score.
* Detecção de ataques.
* Comunicação direta com Rust.
* Comunicação direta com Java.
* Kafka.
* RabbitMQ.
* Machine Learning.

O objetivo é **simular uma aplicação**, e não construir uma aplicação de produção completa.

---

# 5. Estrutura do Projeto

Uma estrutura inicial pode ser:

```text
test-api/
├── Controllers/
│   ├── AuthController.cs
│   ├── AdminController.cs
│   ├── FilesController.cs
│   └── UsersController.cs
│
├── Models/
│   └── LoginRequest.cs
│
├── logs/
│   └── app.log
│
├── Program.cs
├── appsettings.json
└── TestApi.csproj
```

A estrutura pode crescer posteriormente caso seja necessário.

---

# 6. Eventos que o Boneco irá gerar

O Boneco deve gerar eventos que sejam úteis para o Sentinela.

Inicialmente:

| Evento              | Descrição                       |
| ------------------- | ------------------------------- |
| `LOGIN_SUCCESS`     | Login realizado com sucesso     |
| `LOGIN_FAILED`      | Tentativa de login inválida     |
| `PRIVILEGED_ACCESS` | Acesso a recurso administrativo |
| `FILE_DOWNLOAD`     | Download/acesso a arquivo       |
| `USER_ACCESS`       | Consulta de usuário             |

Podemos adicionar outros eventos posteriormente.

Exemplo:

```text
LOGIN_SUCCESS
LOGIN_FAILED
PRIVILEGED_ACCESS
FILE_DOWNLOAD
USER_ACCESS
```

---

# 7. Endpoint de Login

O primeiro endpoint será:

```http
POST /api/auth/login
```

Ele receberá:

```json
{
    "username": "felipe",
    "password": "123456"
}
```

O Boneco terá uma credencial fixa apenas para simulação.

Por exemplo:

```text
username: felipe
password: 123456
```

Caso as credenciais estejam corretas:

```text
HTTP 200
```

E será registrado:

```text
LOGIN_SUCCESS
```

Caso estejam incorretas:

```text
HTTP 401
```

E será registrado:

```text
LOGIN_FAILED
```

---

# 8. Exemplo do Endpoint

Um exemplo simplificado:

```csharp
using Microsoft.AspNetCore.Mvc;
using Serilog;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
    [HttpPost("login")]
    public IActionResult Login(LoginRequest request)
    {
        if (request.Username == "felipe" &&
            request.Password == "123456")
        {
            Log.Information(
                "LOGIN_SUCCESS user={User} ip={IP}",
                request.Username,
                HttpContext.Connection.RemoteIpAddress
            );

            return Ok();
        }

        Log.Warning(
            "LOGIN_FAILED user={User} ip={IP}",
            request.Username,
            HttpContext.Connection.RemoteIpAddress
        );

        return Unauthorized();
    }
}
```

Model:

```csharp
public class LoginRequest
{
    public string Username { get; set; } = string.Empty;

    public string Password { get; set; } = string.Empty;
}
```

Esse código é apenas um exemplo da ideia.

A implementação real deve utilizar o formato de log definido para o Rust Agent.

---

# 9. Formato dos Logs

Os logs deverão ser estruturados.

Um exemplo de evento:

```json
{
    "timestamp": "2026-09-10T18:30:15Z",
    "event_type": "LOGIN_FAILED",
    "user": "felipe",
    "ip": "127.0.0.1",
    "source": "test-api",
    "asset": "test-api"
}
```

Outro exemplo:

```json
{
    "timestamp": "2026-09-10T18:31:02Z",
    "event_type": "LOGIN_SUCCESS",
    "user": "felipe",
    "ip": "127.0.0.1",
    "source": "test-api",
    "asset": "test-api"
}
```

A ideia é que o Rust consiga fazer algo próximo de:

```text
linha do app.log
       │
       ▼
serde_json
       │
       ▼
SecurityEvent
```

Isso evita criar um parser extremamente complexo para o MVP.

---

# 10. Configuração do Serilog

O Serilog será configurado para escrever os logs em:

```text
logs/app.log
```

Exemplo simplificado:

```csharp
using Serilog;

Log.Logger = new LoggerConfiguration()
    .WriteTo.Console()
    .WriteTo.File(
        "logs/app.log",
        rollingInterval: RollingInterval.Day
    )
    .CreateLogger();
```

Depois:

```csharp
var builder = WebApplication.CreateBuilder(args);

builder.Host.UseSerilog();

builder.Services.AddControllers();

var app = builder.Build();

app.MapControllers();

app.Run();
```

Posteriormente, o formatter JSON será configurado para que o arquivo contenha eventos estruturados.

---

# 11. Endpoint Administrativo

Endpoint:

```http
GET /api/admin
```

Ele simulará o acesso a uma área privilegiada.

Ao ser chamado:

```text
HTTP 200
```

E será registrado:

```text
PRIVILEGED_ACCESS
```

Exemplo conceitual:

```csharp
[HttpGet]
public IActionResult AccessAdmin()
{
    Log.Warning(
        "PRIVILEGED_ACCESS user={User} ip={IP}",
        "felipe",
        HttpContext.Connection.RemoteIpAddress
    );

    return Ok();
}
```

O objetivo é permitir que o Rust posteriormente identifique esse tipo de evento.

---

# 12. Endpoint de Arquivos

Endpoint:

```http
GET /api/files/{id}
```

Exemplo:

```http
GET /api/files/123
```

Será registrado:

```text
FILE_DOWNLOAD
```

Exemplo de log:

```json
{
    "timestamp": "2026-09-10T18:35:00Z",
    "event_type": "FILE_DOWNLOAD",
    "user": "felipe",
    "ip": "127.0.0.1",
    "source": "test-api",
    "asset": "test-api"
}
```

Não será necessário possuir arquivos reais no MVP.

O endpoint apenas simulará a operação.

---

# 13. Endpoint de Usuários

Endpoint:

```http
GET /api/users/{id}
```

Exemplo:

```http
GET /api/users/10
```

Será registrado:

```text
USER_ACCESS
```

Novamente, não será necessário possuir um banco de dados.

O objetivo é gerar eventos para o Rust processar.

---

# 14. O Boneco não detecta ataques

Uma regra importante da arquitetura:

```text
Boneco
   │
   └── apenas gera eventos
```

Ele não deve fazer:

```text
5 LOGIN_FAILED
       ↓
BRUTE_FORCE
```

Essa responsabilidade pertence ao Rust.

O Boneco simplesmente registrará:

```text
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
LOGIN_FAILED
```

O Rust observará esses eventos e poderá detectar:

```text
BRUTE_FORCE
```

---

# 15. Exemplo de Ataque

O Python Attacker poderá enviar:

```text
POST /api/auth/login
username=felipe
password=senha_errada
```

Repetidamente:

```text
Python
   │
   ├── LOGIN_FAILED
   ├── LOGIN_FAILED
   ├── LOGIN_FAILED
   ├── LOGIN_FAILED
   └── LOGIN_FAILED
          │
          ▼
       app.log
          │
          ▼
      Rust Agent
          │
          ▼
      BRUTE_FORCE
```

O Boneco não precisa saber que isso é um ataque.

---

# 16. Exemplo Completo do Fluxo

### 1. Atacante

O Python envia:

```http
POST /api/auth/login
```

Com:

```json
{
    "username": "felipe",
    "password": "wrong"
}
```

### 2. Test API

A API responde:

```http
401 Unauthorized
```

E registra:

```json
{
    "timestamp": "2026-09-10T18:40:00Z",
    "event_type": "LOGIN_FAILED",
    "user": "felipe",
    "ip": "127.0.0.1",
    "source": "test-api",
    "asset": "test-api"
}
```

### 3. Rust Agent

O Agent percebe uma nova linha:

```text
app.log
   ↓
LOGIN_FAILED
```

Converte para:

```rust
SecurityEvent
```

E executa:

```text
Validation
    ↓
Normalization
    ↓
Filtering
    ↓
Correlation
    ↓
Pattern Detection
```

### 4. Rust detecta o padrão

Depois de várias tentativas:

```text
5 LOGIN_FAILED
mesmo usuário
mesmo IP
dentro de 60 segundos
```

Resultado:

```text
BRUTE_FORCE
```

### 5. Java

O Java recebe:

```json
{
    "pattern": "BRUTE_FORCE",
    "user": "felipe",
    "ip": "127.0.0.1"
}
```

E então:

```text
BRUTE_FORCE
      ↓
Regra de segurança
      ↓
Risk Score
      ↓
Severity
      ↓
Incident
```

---

# 17. Responsabilidade de Cada Componente

| Componente      | Responsabilidade                     |
| --------------- | ------------------------------------ |
| Python Attacker | Simular comportamento malicioso      |
| .NET Test API   | Simular aplicação real               |
| Serilog         | Registrar eventos                    |
| `app.log`       | Armazenar temporariamente os eventos |
| Rust Agent      | Ler e processar os logs              |
| Rust            | Detectar padrões técnicos            |
| Java            | Aplicar regras de segurança          |
| Java            | Calcular Risk Score                  |
| Java            | Criar incidentes                     |
| PostgreSQL      | Persistir os dados                   |
| Dashboard       | Apresentar informações ao analista   |

---

# 18. Regra Arquitetural Principal

O Boneco **não conhece o Sentinela**.

A aplicação deve funcionar normalmente mesmo que o Rust Agent não esteja executando.

```text
              ┌───────────────┐
              │  Test API     │
              └───────┬───────┘
                      │
                      ▼
                   app.log
                      │
                      │
              ┌───────▼───────┐
              │  Rust Agent   │
              └───────────────┘
```

Isso representa melhor o cenário real em que uma empresa instala um Agent para monitorar aplicações existentes.

---

# 19. Critérios de Pronto — MVP

O Boneco estará pronto quando:

* [ ] Projeto ASP.NET Core criado.
* [ ] Swagger funcionando.
* [ ] Serilog configurado.
* [ ] Logs escritos em `logs/app.log`.
* [ ] Logs estruturados em JSON.
* [ ] `POST /api/auth/login` funcionando.
* [ ] Login válido gera `LOGIN_SUCCESS`.
* [ ] Login inválido gera `LOGIN_FAILED`.
* [ ] `GET /api/admin` gera `PRIVILEGED_ACCESS`.
* [ ] `GET /api/files/{id}` gera `FILE_DOWNLOAD`.
* [ ] `GET /api/users/{id}` gera `USER_ACCESS`.
* [ ] Cada evento possui informações suficientes para o Rust.
* [ ] Python consegue realizar requisições contra a API.
* [ ] Rust consegue ler os logs gerados pelo Boneco.
* [ ] O Boneco não possui integração direta com Rust ou Java.

---

# 20. Ordem de Implementação

A implementação será feita nessa ordem:

```text
1. Criar ASP.NET Core Web API
        ↓
2. Configurar Swagger
        ↓
3. Instalar Serilog
        ↓
4. Configurar arquivo de log
        ↓
5. Configurar JSON
        ↓
6. Criar LoginRequest
        ↓
7. Criar AuthController
        ↓
8. Implementar LOGIN_SUCCESS
        ↓
9. Implementar LOGIN_FAILED
        ↓
10. Criar AdminController
        ↓
11. Criar FilesController
        ↓
12. Criar UsersController
        ↓
13. Testar geração dos logs
        ↓
14. Validar formato do app.log
        ↓
15. Integrar com o Rust Agent
```

---

# 21. Resultado Esperado

Ao final, será possível executar:

```text
.NET Test API
      │
      ▼
   app.log
      │
      ▼
 Rust Agent
      │
      ▼
SecurityEvent
      │
      ▼
Pattern Detection
      │
      ▼
   Java API
      │
      ▼
Risk Score / Incident
```

E demonstrar o principal conceito do projeto:

> **Uma aplicação existente pode ser monitorada pelo Sentinela sem precisar implementar uma integração específica com o sistema.**

O Boneco existe apenas para criar um ambiente controlado onde esse comportamento possa ser demonstrado e testado.
