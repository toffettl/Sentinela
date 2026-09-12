# ☕ Dev 2 — Implementação Incremental (Spec + Instruções para IA)

## ⚠️ REGRAS PARA A IA — LEIA ANTES DE QUALQUER COISA

A IA deve seguir estas regras **À RISCA**, sem exceções:

### ❌ A IA NÃO PODE:

1. **Adicionar campos** que não estejam na spec (ex: não criar campo `phone` em User se não está definido)
2. **Adicionar endpoints** que não estejam na spec (ex: não criar `PUT /api/users/{id}` se não está listado)
3. **Adicionar métodos** nos services que não estejam na spec
4. **Adicionar validações** extras além das que estão definidas nos DTOs
5. **Adicionar annotations** que não estão no código de exemplo (ex: não criar `@JsonIgnore` se não está no exemplo)
6. **Modificar a estrutura** dos DTOs de response (ex: não adicionar campos extras no JSON)
7. **Criar classes utilitárias** ou helper classes que não estão na spec
8. **Adicionar tratamento de erro** além do que está definido no GlobalExceptionHandler
9. **Criar interfaces** que não estão na spec
10. **Modificar nomes** de classes, métodos ou variáveis em relação ao que está na spec

### ✅ A IA DEVE:

1. **Copiar o código exatamente** como está na spec (adaptando apenas imports se necessário)
2. **Seguir a ordem** das tarefas dentro de cada dia
3. **Parar após completar** cada dia e aguardar confirmação
4. **Perguntar ao dev** se tiver qualquer dúvida antes de implementar
5. **Reportar se algo não compilar** e aguardar instrução do dev

### 🔒 REGRA DE OURO:

> **Se não está na spec, não existe.**
> A IA é uma executora, não uma criadora. Ela implementa o que está documentado, nada mais.

---

## Como usar este documento

Este documento é dividido em **dias de implementação**.

**Regra:** A IA deve implementar **um dia por vez**. Após concluir todas as tarefas do dia, **parar e aguardar** o dev confirmar para prosseguir ao próximo dia.

```
Dia 1 → Implementa → Para → Espera confirmação
Dia 2 → Implementa → Para → Espera confirmação
Dia 3 → Implementa → Para → Espera confirmação
Dia 4 → Implementa → Para → Espera confirmação
Dia 5 → Implementa → Para → Fim
```

---

## Informações do Projeto

| Item | Valor |
|------|-------|
| Build Tool | Maven |
| Java | 21 |
| Package base | `com.sentinela` |
| Database | PostgreSQL |
| Flyway | Sim |

---

# DIA 1 — Fundação + Entidades Base

## Objetivo

Criar a estrutura do projeto e implementar as entidades **User**, **Asset** e **Incident** com suas migrations, repositories e services básicos.

## Tarefas

### 1.1 — Configurar projeto Spring Boot

Criar projeto com as seguintes dependências:
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL Driver
- Flyway
- Bean Validation
- Lombok (opcional)

**application.yml:**

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/sentinela
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
  flyway:
    enabled: true
    locations: classpath:db/migration
```

### 1.2 — Criar migrations

**V10__create_users_table.sql:**

```sql
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'ANALYST',
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

**V11__create_assets_table.sql:**

```sql
CREATE TABLE assets (
    id                BIGSERIAL PRIMARY KEY,
    name              VARCHAR(255) NOT NULL,
    hostname          VARCHAR(255) NOT NULL,
    ip                VARCHAR(45)  NOT NULL,
    operating_system  VARCHAR(100),
    status            VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

**V12__create_incidents_table.sql:**

```sql
CREATE TABLE incidents (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    description     TEXT,
    severity        VARCHAR(20)  NOT NULL,
    risk_score      INTEGER      NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'OPEN',
    user_involved   VARCHAR(255),
    ip_involved     VARCHAR(45),
    asset_involved  VARCHAR(255),
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

### 1.3 — Implementar User

**Entity — `User.java`:**

```java
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
```

**Enum — `UserRole.java`:**

```java
public enum UserRole {
    ADMIN,
    ANALYST
}
```

**Repository — `UserRepository.java`:**

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

**DTO Request — `UserRequest.java`:**

```java
public class UserRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    @NotNull(message = "Role é obrigatório")
    private UserRole role;
}
```

**DTO Response — `UserResponse.java`:**

```java
public class UserResponse {

    private Long id;
    private String name;
    private String email;
    private UserRole role;
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        return response;
    }
}
```

> **IMPORTANTE:** Nunca retornar `password` no response.

**Service — `UserService.java`:**

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
            .map(UserResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return UserResponse.fromEntity(user);
    }

    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole() != null ? request.getRole() : UserRole.ANALYST);

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }
        userRepository.deleteById(id);
    }
}
```

**Controller — `UserController.java`:**

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserResponse> list() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserResponse findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
```

### 1.4 — Implementar Asset

**Entity — `Asset.java`:**

```java
@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String hostname;

    @Column(nullable = false)
    private String ip;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AssetStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = AssetStatus.ACTIVE;
    }
}
```

**Enum — `AssetStatus.java`:**

```java
public enum AssetStatus {
    ACTIVE,
    INACTIVE,
    MONITORING
}
```

**Repository — `AssetRepository.java`:**

```java
public interface AssetRepository extends JpaRepository<Asset, Long> {
    long countByStatus(AssetStatus status);
}
```

**DTO Request — `AssetRequest.java`:**

```java
public class AssetRequest {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Hostname é obrigatório")
    private String hostname;

    @NotBlank(message = "IP é obrigatório")
    private String ip;

    private String operatingSystem;

    private AssetStatus status;
}
```

**DTO Response — `AssetResponse.java`:**

```java
public class AssetResponse {

    private Long id;
    private String name;
    private String hostname;
    private String ip;
    private String operatingSystem;
    private AssetStatus status;
    private LocalDateTime createdAt;

    public static AssetResponse fromEntity(Asset asset) {
        AssetResponse response = new AssetResponse();
        response.setId(asset.getId());
        response.setName(asset.getName());
        response.setHostname(asset.getHostname());
        response.setIp(asset.getIp());
        response.setOperatingSystem(asset.getOperatingSystem());
        response.setStatus(asset.getStatus());
        response.setCreatedAt(asset.getCreatedAt());
        return response;
    }
}
```

**Service — `AssetService.java`:**

```java
@Service
public class AssetService {

    @Autowired
    private AssetRepository assetRepository;

    public List<AssetResponse> findAll() {
        return assetRepository.findAll().stream()
            .map(AssetResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public AssetResponse findById(Long id) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ativo não encontrado"));
        return AssetResponse.fromEntity(asset);
    }

    public AssetResponse create(AssetRequest request) {
        Asset asset = new Asset();
        asset.setName(request.getName());
        asset.setHostname(request.getHostname());
        asset.setIp(request.getIp());
        asset.setOperatingSystem(request.getOperatingSystem());
        asset.setStatus(request.getStatus() != null ? request.getStatus() : AssetStatus.ACTIVE);

        return AssetResponse.fromEntity(assetRepository.save(asset));
    }

    public AssetResponse update(Long id, AssetRequest request) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Ativo não encontrado"));

        asset.setName(request.getName());
        asset.setHostname(request.getHostname());
        asset.setIp(request.getIp());
        asset.setOperatingSystem(request.getOperatingSystem());
        if (request.getStatus() != null) asset.setStatus(request.getStatus());

        return AssetResponse.fromEntity(assetRepository.save(asset));
    }

    public void delete(Long id) {
        if (!assetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ativo não encontrado");
        }
        assetRepository.deleteById(id);
    }
}
```

**Controller — `AssetController.java`:**

```java
@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @GetMapping
    public List<AssetResponse> list() {
        return assetService.findAll();
    }

    @GetMapping("/{id}")
    public AssetResponse findById(@PathVariable Long id) {
        return assetService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssetResponse create(@Valid @RequestBody AssetRequest request) {
        return assetService.create(request);
    }

    @PutMapping("/{id}")
    public AssetResponse update(@PathVariable Long id, @Valid @RequestBody AssetRequest request) {
        return assetService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assetService.delete(id);
    }
}
```

### 1.5 — Implementar Incident (Entity + Repository básico)

**Enum — `IncidentSeverity.java`:**

```java
public enum IncidentSeverity {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}
```

**Enum — `IncidentStatus.java`:**

```java
public enum IncidentStatus {
    OPEN,
    INVESTIGATING,
    RESOLVED,
    FALSE_POSITIVE
}
```

**Entity — `Incident.java`:**

```java
@Entity
@Table(name = "incidents")
public class Incident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IncidentSeverity severity;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private IncidentStatus status;

    @Column(name = "user_involved")
    private String userInvolved;

    @Column(name = "ip_involved")
    private String ipInvolved;

    @Column(name = "asset_involved")
    private String assetInvolved;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) this.status = IncidentStatus.OPEN;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

**Repository — `IncidentRepository.java`:**

```java
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    long countBySeverity(IncidentSeverity severity);
    long countByStatus(IncidentStatus status);
}
```

### 1.6 — Exceções customizadas

**`ResourceNotFoundException.java`:**

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

**`BusinessException.java`:**

```java
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
```

## ✅ Checklist Dia 1

- [ ] Projeto Spring Boot criado e rodando
- [ ] Migrations V10, V11, V12 executando
- [ ] User Entity, Repository, Service, Controller, DTOs
- [ ] Asset Entity, Repository, Service, Controller, DTOs
- [ ] Incident Entity, Repository (service e controller no Dia 2)
- [ ] Exceções customizadas

## 🛑 FIM DO DIA 1 — Aguardar confirmação para prosseguir

---

# DIA 2 — Incident CRUD + Regras de Negócio

## Objetivo

Completar o módulo de **Incident** com service, controller, relacionamento com Events e notas.

## Tarefas

### 2.1 — Criar migration para tabelas de relacionamento

**V13__create_incident_events_table.sql:**

```sql
CREATE TABLE incident_events (
    incident_id BIGINT NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    event_id    BIGINT NOT NULL REFERENCES events(id) ON DELETE CASCADE,
    PRIMARY KEY (incident_id, event_id)
);
```

> **NOTA:** A tabela `events` será criada pelo Dev 1. Se ainda não existir, criar um stub/migration temporário ou alinhar com Dev 1.

**V14__create_incident_notes_table.sql:**

```sql
CREATE TABLE incident_notes (
    id          BIGSERIAL PRIMARY KEY,
    incident_id BIGINT    NOT NULL REFERENCES incidents(id) ON DELETE CASCADE,
    content     TEXT      NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### 2.2 — Atualizar entidade Incident com relacionamentos

Adicionar na entidade `Incident.java`:

```java
@ManyToMany
@JoinTable(
    name = "incident_events",
    joinColumns = @JoinColumn(name = "incident_id"),
    inverseJoinColumns = @JoinColumn(name = "event_id")
)
private List<Event> events = new ArrayList<>();

@OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
private List<IncidentNote> notes = new ArrayList<>();
```

### 2.3 — Criar entidade IncidentNote

**Entity — `IncidentNote.java`:**

```java
@Entity
@Table(name = "incident_notes")
public class IncidentNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
```

**Repository — `IncidentNoteRepository.java`:**

```java
public interface IncidentNoteRepository extends JpaRepository<IncidentNote, Long> {
    List<IncidentNote> findByIncidentId(Long incidentId);
}
```

### 2.4 — Criar DTOs de Incident

**DTO Response — `IncidentResponse.java`:**

```java
public class IncidentResponse {

    private Long id;
    private String title;
    private String description;
    private IncidentSeverity severity;
    private Integer riskScore;
    private IncidentStatus status;
    private String userInvolved;
    private String ipInvolved;
    private String assetInvolved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EventSummary> events;
    private List<IncidentNoteResponse> notes;
    private List<String> rulesTriggered;

    public static IncidentResponse fromEntity(Incident incident) {
        IncidentResponse response = new IncidentResponse();
        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setSeverity(incident.getSeverity());
        response.setRiskScore(incident.getRiskScore());
        response.setStatus(incident.getStatus());
        response.setUserInvolved(incident.getUserInvolved());
        response.setIpInvolved(incident.getIpInvolved());
        response.setAssetInvolved(incident.getAssetInvolved());
        response.setCreatedAt(incident.getCreatedAt());
        response.setUpdatedAt(incident.getUpdatedAt());

        if (incident.getEvents() != null) {
            response.setEvents(
                incident.getEvents().stream()
                    .map(EventSummary::fromEntity)
                    .collect(Collectors.toList())
            );
        }

        if (incident.getNotes() != null) {
            response.setNotes(
                incident.getNotes().stream()
                    .map(IncidentNoteResponse::fromEntity)
                    .collect(Collectors.toList())
            );
        }

        return response;
    }
}
```

**DTO — `EventSummary.java`:**

```java
public class EventSummary {

    private Long id;
    private String eventType;
    private LocalDateTime timestamp;
    private String user;
    private String ip;
    private String source;

    public static EventSummary fromEntity(Event event) {
        EventSummary summary = new EventSummary();
        summary.setId(event.getId());
        summary.setEventType(event.getEventType());
        summary.setTimestamp(event.getTimestamp());
        summary.setUser(event.getUser());
        summary.setIp(event.getIp());
        summary.setSource(event.getSource());
        return summary;
    }
}
```

**DTO — `IncidentNoteRequest.java`:**

```java
public class IncidentNoteRequest {

    @NotBlank(message = "Conteúdo da nota é obrigatório")
    private String content;
}
```

**DTO — `IncidentNoteResponse.java`:**

```java
public class IncidentNoteResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;

    public static IncidentNoteResponse fromEntity(IncidentNote note) {
        IncidentNoteResponse response = new IncidentNoteResponse();
        response.setId(note.getId());
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        return response;
    }
}
```

**DTO — `IncidentUpdateRequest.java`:**

```java
public class IncidentUpdateRequest {

    @NotNull(message = "Status é obrigatório")
    private IncidentStatus status;
}
```

**DTO — `CreateIncidentRequest.java` (para criação automática pelo Dev 1):**

```java
public class CreateIncidentRequest {

    private String title;
    private String description;
    private IncidentSeverity severity;
    private Integer riskScore;
    private String userInvolved;
    private String ipInvolved;
    private String assetInvolved;
    private List<Long> eventIds;
    private List<String> rulesTriggered;
}
```

### 2.5 — Implementar IncidentService

**Service — `IncidentService.java`:**

```java
@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private IncidentNoteRepository noteRepository;

    @Autowired
    private EventRepository eventRepository; // Dev 1 fornece

    public List<IncidentResponse> findAll(IncidentStatus status, IncidentSeverity severity, String user) {
        // Implementar filtros básicos
        List<Incident> incidents;

        if (status != null && severity != null) {
            incidents = incidentRepository.findByStatusAndSeverity(status, severity);
        } else if (status != null) {
            incidents = incidentRepository.findByStatus(status);
        } else if (severity != null) {
            incidents = incidentRepository.findBySeverity(severity);
        } else {
            incidents = incidentRepository.findAll();
        }

        // Filtrar por user se fornecido
        if (user != null && !user.isBlank()) {
            incidents = incidents.stream()
                .filter(i -> i.getUserInvolved() != null &&
                    i.getUserInvolved().toLowerCase().contains(user.toLowerCase()))
                .collect(Collectors.toList());
        }

        return incidents.stream()
            .map(IncidentResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public IncidentResponse findById(Long id) {
        Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));
        return IncidentResponse.fromEntity(incident);
    }

    public List<EventSummary> findEventsByIncidentId(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
            .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));

        return incident.getEvents().stream()
            .map(EventSummary::fromEntity)
            .collect(Collectors.toList());
    }

    public IncidentResponse updateStatus(Long id, IncidentUpdateRequest request) {
        Incident incident = incidentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));

        validateStatusTransition(incident.getStatus(), request.getStatus());
        incident.setStatus(request.getStatus());

        return IncidentResponse.fromEntity(incidentRepository.save(incident));
    }

    public IncidentNoteResponse addNote(Long incidentId, IncidentNoteRequest request) {
        Incident incident = incidentRepository.findById(incidentId)
            .orElseThrow(() -> new ResourceNotFoundException("Incidente não encontrado"));

        IncidentNote note = new IncidentNote();
        note.setIncident(incident);
        note.setContent(request.getContent());

        return IncidentNoteResponse.fromEntity(noteRepository.save(note));
    }

    // Método chamado pelo Dev 1 para criação automática
    public IncidentResponse createIncident(CreateIncidentRequest request) {
        Incident incident = new Incident();
        incident.setTitle(request.getTitle());
        incident.setDescription(request.getDescription());
        incident.setRiskScore(request.getRiskScore());
        incident.setUserInvolved(request.getUserInvolved());
        incident.setIpInvolved(request.getIpInvolved());
        incident.setAssetInvolved(request.getAssetInvolved());

        // Calcular severidade automaticamente
        incident.setSeverity(calculateSeverity(request.getRiskScore()));

        // Associar eventos se fornecidos
        if (request.getEventIds() != null && !request.getEventIds().isEmpty()) {
            List<Event> events = eventRepository.findAllById(request.getEventIds());
            incident.setEvents(events);
        }

        return IncidentResponse.fromEntity(incidentRepository.save(incident));
    }

    private IncidentSeverity calculateSeverity(Integer riskScore) {
        if (riskScore >= 80) return IncidentSeverity.CRITICAL;
        if (riskScore >= 60) return IncidentSeverity.HIGH;
        if (riskScore >= 40) return IncidentSeverity.MEDIUM;
        return IncidentSeverity.LOW;
    }

    private void validateStatusTransition(IncidentStatus current, IncidentStatus next) {
        boolean valid = switch (current) {
            case OPEN -> next == IncidentStatus.INVESTIGATING ||
                        next == IncidentStatus.RESOLVED ||
                        next == IncidentStatus.FALSE_POSITIVE;
            case INVESTIGATING -> next == IncidentStatus.RESOLVED ||
                                 next == IncidentStatus.FALSE_POSITIVE;
            case RESOLVED, FALSE_POSITIVE -> false;
        };

        if (!valid) {
            throw new BusinessException(
                String.format("Transição inválida de %s para %s", current, next)
            );
        }
    }
}
```

### 2.6 — Implementar IncidentController

**Controller — `IncidentController.java`:**

```java
@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @GetMapping
    public List<IncidentResponse> list(
            @RequestParam(required = false) IncidentStatus status,
            @RequestParam(required = false) IncidentSeverity severity,
            @RequestParam(required = false) String user
    ) {
        return incidentService.findAll(status, severity, user);
    }

    @GetMapping("/{id}")
    public IncidentResponse findById(@PathVariable Long id) {
        return incidentService.findById(id);
    }

    @GetMapping("/{id}/events")
    public List<EventSummary> listEvents(@PathVariable Long id) {
        return incidentService.findEventsByIncidentId(id);
    }

    @PatchMapping("/{id}")
    public IncidentResponse updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody IncidentUpdateRequest request
    ) {
        return incidentService.updateStatus(id, request);
    }

    @PostMapping("/{id}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public IncidentNoteResponse addNote(
            @PathVariable Long id,
            @Valid @RequestBody IncidentNoteRequest request
    ) {
        return incidentService.addNote(id, request);
    }
}
```

### 2.7 — Adicionar Query Methods no IncidentRepository

```java
public interface IncidentRepository extends JpaRepository<Incident, Long> {
    long countBySeverity(IncidentSeverity severity);
    long countByStatus(IncidentStatus status);
    List<Incident> findByStatus(IncidentStatus status);
    List<Incident> findBySeverity(IncidentSeverity severity);
    List<Incident> findByStatusAndSeverity(IncidentStatus status, IncidentSeverity severity);
}
```

## ✅ Checklist Dia 2

- [ ] Migrations V13, V14 criadas
- [ ] IncidentNote Entity
- [ ] Incident com relacionamentos (events, notes)
- [ ] Todos os DTOs de Incident
- [ ] IncidentService completo (CRUD + filtros + criação automática)
- [ ] IncidentController completo
- [ ] Validação de transições de status

## 🛑 FIM DO DIA 2 — Aguardar confirmação para prosseguir

---

# DIA 3 — Endpoints de Consulta + Dashboard

## Objetivo

Criar endpoints de consulta para **Events**, **Users**, **Assets** e **Dashboard Summary**.

## Tarefas

### 3.1 — Implementar Dashboard

**DTO — `DashboardSummaryResponse.java`:**

```java
public class DashboardSummaryResponse {

    private Long totalEvents;
    private Long totalIncidents;
    private Long criticalIncidents;
    private Long highIncidents;
    private Long openIncidents;
    private Long activeAssets;

    // Construtor
    public DashboardSummaryResponse(Long totalEvents, Long totalIncidents,
            Long criticalIncidents, Long highIncidents,
            Long openIncidents, Long activeAssets) {
        this.totalEvents = totalEvents;
        this.totalIncidents = totalIncidents;
        this.criticalIncidents = criticalIncidents;
        this.highIncidents = highIncidents;
        this.openIncidents = openIncidents;
        this.activeAssets = activeAssets;
    }
}
```

**Controller — `DashboardController.java`:**

```java
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private EventRepository eventRepository; // Dev 1 fornece

    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private AssetRepository assetRepository;

    @GetMapping("/summary")
    public DashboardSummaryResponse getSummary() {
        return new DashboardSummaryResponse(
            eventRepository.count(),
            incidentRepository.count(),
            incidentRepository.countBySeverity(IncidentSeverity.CRITICAL),
            incidentRepository.countBySeverity(IncidentSeverity.HIGH),
            incidentRepository.countByStatus(IncidentStatus.OPEN),
            assetRepository.countByStatus(AssetStatus.ACTIVE)
        );
    }
}
```

### 3.2 — Endpoints de Events (alinhamento com Dev 1)

**NOTA:** Os endpoints de Events (`GET /api/events`, `GET /api/events/{id}`) são responsabilidade do Dev 1.

O Dev 2 deve:
1. Confirmar que o Dev 1 implementou esses endpoints
2. Se necessário, criar DTOs de resposta para uso no frontend

### 3.3 — Global Exception Handler

**`GlobalExceptionHandler.java`:**

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(404).body(new ErrorResponse(
            LocalDateTime.now(),
            404,
            ex.getMessage(),
            request.getRequestURI()
        ));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex, HttpServletRequest request) {
        return ResponseEntity.status(400).body(new ErrorResponse(
            LocalDateTime.now(),
            400,
            ex.getMessage(),
            request.getRequestURI()
        ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));

        return ResponseEntity.status(400).body(new ErrorResponse(
            LocalDateTime.now(),
            400,
            message,
            request.getRequestURI()
        ));
    }
}
```

**DTO — `ErrorResponse.java`:**

```java
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String path;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.path = path;
    }
}
```

## ✅ Checklist Dia 3

- [ ] DashboardController com GET /api/dashboard/summary
- [ ] GlobalExceptionHandler
- [ ] ErrorResponse DTO
- [ ] Confirmar endpoints de Events com Dev 1

## 🛑 FIM DO DIA 3 — Aguardar confirmação para prosseguir

---

# DIA 4 — Integração + Ajustes

## Objetivo

Testar integração com Dev 1, criar seed de dados e ajustar responses.

## Tarefas

### 4.1 — Endpoint de Seed (opcional)

**Controller — `SeedController.java`:**

```java
@RestController
@RequestMapping("/api/seed")
public class SeedController {

    @Autowired
    private UserService userService;

    @Autowired
    private AssetService assetService;

    @PostMapping
    public Map<String, Object> seed() {
        // Criar usuários de teste
        UserRequest admin = new UserRequest();
        admin.setName("Administrador");
        admin.setEmail("admin@sentinela.com");
        admin.setPassword("admin123");
        admin.setRole(UserRole.ADMIN);
        userService.create(admin);

        UserRequest analyst = new UserRequest();
        analyst.setName("Analista João");
        analyst.setEmail("joao@sentinela.com");
        analyst.setPassword("analista123");
        analyst.setRole(UserRole.ANALYST);
        userService.create(analyst);

        // Criar assets de teste
        AssetRequest server1 = new AssetRequest();
        server1.setName("Web Server Principal");
        server1.setHostname("web-server-01");
        server1.setIp("192.168.1.10");
        server1.setOperatingSystem("Ubuntu 22.04");
        assetService.create(server1);

        AssetRequest server2 = new AssetRequest();
        server2.setName("Database Produção");
        server2.setHostname("db-prod-01");
        server2.setIp("192.168.1.20");
        server2.setOperatingSystem("CentOS 8");
        assetService.create(server2);

        return Map.of(
            "message", "Dados de seed criados com sucesso",
            "users", 2,
            "assets", 2
        );
    }
}
```

### 4.2 — Testar integração

- [ ] Dev 1 envia evento via POST /api/events
- [ ] Dev 1 chama IncidentService.createIncident()
- [ ] Incidente aparece em GET /api/incidents
- [ ] Dashboard retorna totais corretos

### 4.3 — Ajustar DTOs de resposta

- Verificar se JSON de incidente inclui events e rules
- Garantir que responses não expõem dados sensíveis

## ✅ Checklist Dia 4

- [ ] Endpoint de seed funcionando
- [ ] Integração com Dev 1 testada
- [ ] Responses ajustadas

## 🛑 FIM DO DIA 4 — Aguardar confirmação para prosseguir

---

# DIA 5 — Polimento + Documentação

## Objetivo

Finalizar com testes, documentação e preparar demo.

## Tarefas

### 5.1 — Testes unitários

```java
@SpringBootTest
class IncidentServiceTest {

    @Autowired
    private IncidentService incidentService;

    @Test
    void shouldCreateIncidentWithCorrectSeverity() {
        CreateIncidentRequest request = new CreateIncidentRequest();
        request.setTitle("Test Incident");
        request.setRiskScore(85);
        // ... configurar request

        IncidentResponse response = incidentService.createIncident(request);

        assertEquals(IncidentSeverity.CRITICAL, response.getSeverity());
        assertEquals("Test Incident", response.getTitle());
    }

    @Test
    void shouldNotAllowInvalidStatusTransition() {
        // Criar incidente RESOLVED
        // Tentar mudar para OPEN
        // Deve lançar BusinessException
    }
}
```

### 5.2 — Documentação (README)

Criar/atualizar `README.md` com:

```markdown
# Sentinela - API Java

## Endpoints

### Auth
- POST /auth/login - Autenticar

### Users
- GET /api/users - Listar usuários
- GET /api/users/{id} - Buscar usuário
- POST /api/users - Criar usuário
- DELETE /api/users/{id} - Deletar usuário

### Assets
- GET /api/assets - Listar ativos
- GET /api/assets/{id} - Buscar ativo
- POST /api/assets - Criar ativo
- PUT /api/assets/{id} - Atualizar ativo
- DELETE /api/assets/{id} - Deletar ativo

### Incidents
- GET /api/incidents - Listar incidentes (filtros: status, severity, user)
- GET /api/incidents/{id} - Buscar incidente
- PATCH /api/incidents/{id} - Atualizar status
- GET /api/incidents/{id}/events - Eventos do incidente
- POST /api/incidents/{id}/notes - Adicionar nota

### Dashboard
- GET /api/dashboard/summary - Resumo

### Seed
- POST /api/seed - Popular dados de teste
```

### 5.3 — Preparar demo

Script de demo:
1. POST /auth/login → pegar JWT
2. POST /api/seed → popular dados
3. POST /api/events (via Dev 1) → gerar eventos
4. GET /api/incidents → mostrar incidentes
5. GET /api/incidents/1 → detalhe
6. PATCH /api/incidents/1 → mudar status
7. POST /api/incidents/1/notes → adicionar nota
8. GET /api/dashboard/summary → totais

## ✅ Checklist Dia 5

- [ ] Testes unitários passando
- [ ] README atualizado
- [ ] Demo preparada
- [ ] Código revisado

## 🛑 FIM DO DIA 5 — Projeto concluído!

---

# Referência Rápida

## Enums

| Enum | Valores |
|------|---------|
| UserRole | ADMIN, ANALYST |
| AssetStatus | ACTIVE, INACTIVE, MONITORING |
| IncidentSeverity | LOW, MEDIUM, HIGH, CRITICAL |
| IncidentStatus | OPEN, INVESTIGATING, RESOLVED, FALSE_POSITIVE |

## Severidade por Risk Score

| Score | Severidade |
|-------|------------|
| 0-39 | LOW |
| 40-59 | MEDIUM |
| 60-79 | HIGH |
| 80-100 | CRITICAL |

## Transições de Status Válidas

| De | Para |
|----|------|
| OPEN | INVESTIGATING, RESOLVED, FALSE_POSITIVE |
| INVESTIGATING | RESOLVED, FALSE_POSITIVE |
| RESOLVED | (nenhuma) |
| FALSE_POSITIVE | (nenhuma) |

## Endpoints Dev 2

| Endpoint | Método | Descrição |
|----------|--------|-----------|
| /api/users | GET | Listar usuários |
| /api/users/{id} | GET | Buscar usuário |
| /api/users | POST | Criar usuário |
| /api/users/{id} | DELETE | Deletar usuário |
| /api/assets | GET | Listar ativos |
| /api/assets/{id} | GET | Buscar ativo |
| /api/assets | POST | Criar ativo |
| /api/assets/{id} | PUT | Atualizar ativo |
| /api/assets/{id} | DELETE | Deletar ativo |
| /api/incidents | GET | Listar incidentes |
| /api/incidents/{id} | GET | Buscar incidente |
| /api/incidents/{id} | PATCH | Atualizar status |
| /api/incidents/{id}/events | GET | Eventos do incidente |
| /api/incidents/{id}/notes | POST | Adicionar nota |
| /api/dashboard/summary | GET | Resumo do dashboard |
| /api/seed | POST | Popular dados de teste |
