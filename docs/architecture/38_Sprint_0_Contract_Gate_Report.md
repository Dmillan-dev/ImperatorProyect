# 38 - Sprint 0 Contract Gate Report

## Purpose

Validate whether IMPERATOR is ready to begin Phase 2 - Platform Foundation.

This sprint is review and control only.

It does not create code, project structure, `src/`, framework files, package manifests, migrations, Docker files, dependencies or runnable services.

## Authority Normalization

The founder prompt references some documents by short labels. The canonical repository paths are:

| Prompt label | Canonical repository document |
|---|---|
| `31_Core_Domain_Model` | `docs/product/CORE_DOMAIN_MODEL.md` |
| `32_API_Specification` | `docs/product/API_SPECIFICATION.md` |
| `33_Phase_1_Foundational_Implementation_Decisions` | `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` |
| `34_MVP_Implementation_Blueprint` | `docs/architecture/34_MVP_Implementation_Blueprint.md` |
| `35_Coding_Principles` | `docs/architecture/35_Coding_Principles.md` |
| `36_Phase_2_Platform_Foundation_Blueprint` | `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` |
| `37_Implementation_Contract` | `docs/architecture/37_Implementation_Contract.md` |

Sprint 0 uses those documents as authority.

## Pre-Gate Health Check

Overall context health:

```text
Healthy with one sequencing correction applied outside documents 31-37.
```

Findings:

- Product vision remains coherent: IMPERATOR manages decisions, not generic dashboards.
- MVP remains narrow: one Decision ROI Case, one timeline, one ROI, one deterministic recommendation.
- The active MVP recommendation remains AI model downgrade/change for `DRC-AOA-001`.
- Phase 2 remains Platform Foundation, not business implementation.
- The domain-first concern is valid: the sprint plan must not let PostgreSQL, API shells or Spring Boot define the domain.
- `agents/phase2/README.md` has been aligned to the domain-first order:
  - Sprint 2 - Java Domain Foundation,
  - Sprint 3 - Application Layer Foundation,
  - Sprint 4 - PostgreSQL Foundation.
- No code or project structure has been created.

## 1. Architecture Validation

### Hexagonal Architecture

Validated:

```text
API
-> Application
-> Domain
-> Ports
<- Adapters
```

Confirmed:

- API is an external interface layer.
- Application owns use-case orchestration.
- Domain owns core business meaning.
- Ports define required capabilities.
- Adapters implement ports for PostgreSQL, providers, security, observability and future integrations.
- The domain must not depend on Spring, FastAPI, React, PostgreSQL, provider SDKs or HTTP clients.

### Layer Separation

| Layer | Sprint 0 verdict |
|---|---|
| API | Valid as route shell layer only in Phase 2. |
| Application | Valid and should exist before database/API business behavior. |
| Domain | Must be created before PostgreSQL schema and adapters. |
| Ports | Required between application/domain needs and adapters. |
| Adapters | Must remain replaceable and must not define business meaning. |

### Contradiction Check

No blocking contradiction found.

Clarification:

- Older simplified arrows such as `Domain -> Ports -> Adapters` are interpreted as architectural layering, not as a literal dependency from domain objects to adapters.
- The implementation rule is stricter: domain stays pure; application depends on ports; adapters implement ports.

## 2. Scope Validation

### Phase 2 Includes

Phase 2 may include:

- project structure,
- backend foundation,
- domain skeleton,
- application use-case shells,
- database foundation,
- API route shells,
- authentication foundation,
- frontend foundation,
- Docker local foundation,
- observability foundation,
- CI foundation,
- R&D evidence capture foundation.

### Phase 2 Does Not Include

Phase 2 must not include:

- ROI calculation,
- recommendation engine,
- recommendation generation,
- AI provider calls,
- live connectors,
- AWS integration,
- GitHub integration,
- Jira integration,
- OpenAI/Claude live integration,
- approval workflow business logic,
- fake business evidence,
- fake ledger state,
- production deployment.

### Scope Verdict

Phase 2 is allowed only as foundation.

Phase 3 remains the first phase that implements the `DRC-AOA-001` business value loop.

## 3. First Implementation Module Decision

### Decision

The first code-bearing implementation module after the repository shell should be:

```text
backend-java/domain
```

Initial domain package groups:

```text
backend-java/domain/shared
backend-java/domain/evidence
backend-java/domain/decision
backend-java/domain/recommendation
backend-java/domain/ledger
backend-java/domain/business-value
backend-java/domain/identity
backend-java/domain/exceptions
```

It may contain:

- Evidence aggregate skeleton,
- base domain objects,
- value objects,
- domain exceptions,
- enums only if already canonical,
- no framework coupling.

It must not contain:

- controllers,
- database/JPA entities,
- repositories,
- adapters,
- API DTOs,
- Spring annotations,
- FastAPI code,
- React code,
- ROI calculation,
- recommendation generation,
- approval workflow behavior,
- connector logic,
- AI calls.

### Why This Module First

IMPERATOR's product value is not React, Docker, JWT, AWS or PostgreSQL.

The product value is:

```text
Evidence
-> Decision
-> Recommendation
-> Human Approval
-> Ledger
-> ROI / Business Value
```

PostgreSQL should store the domain.

The domain should not be reverse-engineered from PostgreSQL tables.

Therefore:

```text
Domain first.
Persistence second.
API third.
```

### Execution Note

Sprint 1 still creates only repository/project shell.

The first code-bearing module is authorized for Sprint 2 only after Sprint 1 is accepted.

## 4. Technology Freeze

| Area | Frozen choice | Sprint 0 note |
|---|---|---|
| Backend | Java 21, Spring Boot, Maven | Spring must not enter the domain layer. |
| AI | Python, FastAPI | Provider foundation only; no real AI calls in Phase 2. |
| Database | PostgreSQL | Source of truth from first implementation day. |
| Frontend | React, TypeScript | Next.js remains compatible with previous frontend direction. |
| External API | REST | Plural resources, route shells first. |
| Internal communication | gRPC + Protocol Buffers for Java/Python product calls | FastAPI does not replace internal gRPC/protobuf mandate. |
| Infrastructure | Docker Compose | Local developer foundation only. |
| CI | GitHub Actions | Build/lint/test/docker-build foundation only; no deployment. |

## 5. Dependency Freeze

### Required Now

For Sprint 1:

- no runtime dependencies,
- no framework dependencies,
- no package manifests unless explicitly scoped later,
- filesystem-only project shell.

For the first code-bearing domain module in Sprint 2:

- Java 21 language/runtime,
- Maven project foundation only if Sprint 1 or Sprint 2 explicitly authorizes build files,
- Java standard library,
- lightweight test dependency only when a test module is explicitly assigned.

### Allowed Later In Phase 2

Allowed later, by separate module and sprint:

- Spring Boot web foundation,
- Spring validation foundation,
- Spring security foundation,
- Spring Boot Actuator,
- Flyway,
- PostgreSQL JDBC driver,
- Micrometer,
- OpenTelemetry instrumentation,
- FastAPI,
- Pydantic,
- React,
- TypeScript,
- local Docker Compose,
- GitHub Actions.

### Forbidden In Phase 2

Forbidden:

- OpenAI/Anthropic real SDK calls,
- AWS/GitHub/Jira live connectors,
- Kafka,
- Kubernetes,
- Terraform,
- Redis,
- MongoDB,
- SQLite as source of truth,
- Graph DB,
- vector DB,
- OpenSearch,
- public SDKs,
- production OAuth provider matrix,
- production deployment tooling,
- business seed data,
- fake evidence,
- fake ledger records.

## 6. Risks Before Coding

### Architecture Risks

| Risk | Mitigation |
|---|---|
| Database defines the domain | Create Java domain foundation before PostgreSQL. |
| Spring/JPA leaks into domain | Domain module forbids framework annotations. |
| Ports/adapters become unclear | Application layer and ports get their own sprint before adapters. |
| Microservices appear too early | Phase 2 starts as modular foundation, not distributed runtime. |

### Scope Risks

| Risk | Mitigation |
|---|---|
| Agent creates full backend | One-module-per-iteration rule. |
| Phase 2 drifts into ROI/recommendations | Explicit forbidden list in Sprint 0 and 37. |
| Live connectors appear early | Connector SDKs and credentials forbidden in Phase 2. |
| Frontend becomes dashboard-first | React shell waits until API route shells exist. |

### AI Generated Code Risks

| Risk | Mitigation |
|---|---|
| Agent invents domain objects | Must use Core Domain Model and 37 naming rules. |
| Agent adds provider-specific logic | Provider calls forbidden in Phase 2. |
| Agent creates multiple modules | Prompt must list allowed and forbidden files. |
| Agent silently changes architecture docs | Documents 31-37 require explicit founder/CTO instruction to change. |

### Database Migration Risks

| Risk | Mitigation |
|---|---|
| Schema created before domain is clear | PostgreSQL sprint moved after domain and application foundations. |
| Ledger becomes mutable table | Append-only rule remains mandatory. |
| Seed data becomes fake product truth | Seed business data forbidden in Phase 2. |
| Decision Graph is overbuilt | Keep graph relationships in PostgreSQL; no Graph DB. |

### Security Risks

| Risk | Mitigation |
|---|---|
| JWT built before protected resources exist | JWT/RBAC placed after domain, app, DB and API shells. |
| Admin becomes business approver by default | Approval authority remains product/domain rule. |
| Secrets enter repo | No real credentials; `.env.example` may contain names only later. |
| Role filtering is forgotten | API/frontend must use identity model before Phase 3 behavior. |

## 7. First Agent Instruction

### Sprint 1 Agent Prompt

Use this prompt after Sprint 0 is accepted:

```text
SPRINT 1 - REPOSITORY AND PROJECT SHELL

No generar codigo de aplicacion.
No crear src.
No instalar dependencias.
No crear package manifests.
No crear Dockerfiles.
No crear migraciones.

Objetivo:
Crear unicamente la estructura superior del proyecto para Phase 2 Platform Foundation.

Modulo permitido:
repository/project-shell

Archivos/carpetas permitidos:
- backend-java/README.md
- backend-python/README.md
- frontend/README.md
- database/README.md
- infra/README.md
- scripts/README.md
- samples/decision-cases/ai-onboarding-assistant/README.md

Archivos/carpetas prohibidos:
- cualquier src/
- pom.xml
- package.json
- pyproject.toml
- requirements.txt
- Dockerfile
- docker-compose.yml
- migrations SQL
- controladores
- servicios ejecutables
- codigo de dominio
- conectores
- credenciales
- datos fake de negocio

Documentos obligatorios:
- docs/decisions/14_Decision_Log.md
- docs/product/CORE_DOMAIN_MODEL.md
- docs/product/API_SPECIFICATION.md
- docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md
- docs/architecture/34_MVP_Implementation_Blueprint.md
- docs/architecture/35_Coding_Principles.md
- docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md
- docs/architecture/37_Implementation_Contract.md
- docs/architecture/38_Sprint_0_Contract_Gate_Report.md
- agents/phase2/README.md

Criterios de aceptacion:
- existe la estructura superior autorizada,
- cada carpeta tiene README de frontera y proposito,
- no existe codigo fuente,
- no existe framework,
- no existen dependencias,
- no existe Docker runtime,
- no existe base de datos o migracion,
- no se ha modificado ningun documento de autoridad,
- no se ha generado mas de un modulo.
```

### First Code-Bearing Agent Prompt After Sprint 1

Use this only after Sprint 1 is accepted:

```text
SPRINT 2 - JAVA DOMAIN FOUNDATION

Genera unicamente el modulo backend-java/domain.

Objetivo:
Crear el skeleton de dominio puro de IMPERATOR, sin framework y sin persistencia.

Permitido:
- backend-java/domain/shared
- backend-java/domain/evidence
- backend-java/domain/decision
- backend-java/domain/recommendation
- backend-java/domain/ledger
- backend-java/domain/business-value
- backend-java/domain/identity
- backend-java/domain/exceptions

Prohibido:
- controllers
- API DTOs
- Spring annotations
- JPA annotations
- repositories
- adapters
- database code
- ROI calculation
- recommendation generation
- approval workflow behavior
- connector code
- AI provider code

Documentos obligatorios:
- docs/product/CORE_DOMAIN_MODEL.md
- docs/product/API_SPECIFICATION.md
- docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md
- docs/architecture/34_MVP_Implementation_Blueprint.md
- docs/architecture/35_Coding_Principles.md
- docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md
- docs/architecture/37_Implementation_Contract.md
- docs/architecture/38_Sprint_0_Contract_Gate_Report.md
- agents/phase2/README.md

Criterios de aceptacion:
- dominio no importa frameworks,
- dominio no conoce PostgreSQL,
- dominio no conoce APIs externas,
- nombres respetan el Core Domain Model,
- no se implementa logica de negocio Phase 3,
- no se genera mas de un modulo.
```

## 8. GO / NO-GO Decision

STATUS:

```text
GO
```

Decision:

Phase 2 - Platform Foundation may begin with Sprint 1 - Repository and Project Shell only.

Authorization boundary:

- GO for Sprint 1 repository shell.
- GO in principle for domain-first Phase 2 sequencing.
- NO authorization for Spring Boot full project generation yet.
- NO authorization for PostgreSQL migrations yet.
- NO authorization for API route shells yet.
- NO authorization for JWT/RBAC yet.
- NO authorization for React, Docker, observability or CI yet.
- NO authorization for Phase 3 business behavior.

First code-bearing module after Sprint 1 acceptance:

```text
backend-java/domain
```

Final gate statement:

```text
IMPERATOR is ready to start Phase 2 only through controlled, module-by-module execution.
```
