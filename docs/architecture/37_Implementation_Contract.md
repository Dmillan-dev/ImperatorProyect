# 37 - Implementation Contract

## Purpose

Define the mandatory implementation rules for every future IMPERATOR coding task.

This is not product strategy.
This is not architecture redesign.
This is not authorization to generate code.

It is the contract that governs how AI agents and human developers must write implementation files once Phase 2 or Phase 3 is explicitly authorized.

## Authority

This document assumes the following documents are definitive:

1. `docs/architecture/34_MVP_Implementation_Blueprint.md`
2. `docs/architecture/35_Coding_Principles.md`
3. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`

It must not override them.

If this document conflicts with the Decision Log, MVP Blueprint, Coding Principles or Phase 2 Blueprint, stop and request CTO/founder resolution before implementation continues.

## Universal Implementation Rule

No agent may generate more than one module per iteration.

A module is the smallest independently reviewable implementation boundary, such as:

- one Java package group,
- one Python package group,
- one React feature shell,
- one database migration boundary,
- one Docker/local foundation boundary,
- one observability foundation boundary,
- one CI foundation boundary.

An agent must not create adjacent modules "because they are convenient".

Every implementation task must state:

1. assigned module,
2. authority documents used,
3. files allowed to change,
4. forbidden files,
5. forbidden behavior,
6. verification command or manual verification,
7. R&D evidence to record if implementation is real.

Phase 2 creates foundation only.
Phase 3 implements the MVP value loop.

## 1. Layer Rules

### Frontend

Purpose:

Render the user interface, route between screens and call documented APIs.

Allowed responsibilities:

- layout,
- navigation,
- screen state,
- API client calls,
- form validation for user input shape,
- role-aware visibility from server-provided permissions,
- loading, empty and error states.

Forbidden responsibilities:

- ROI calculation,
- recommendation generation,
- approval authority decisions,
- evidence normalization,
- ledger mutation semantics,
- direct database access,
- direct provider access,
- hardcoded business truth.

### API

Purpose:

Expose REST routes and translate HTTP requests into application commands or queries.

Allowed responsibilities:

- route mapping,
- request parsing,
- authentication and authorization checks,
- input validation,
- response shaping,
- HTTP status selection,
- correlation ID propagation.

Forbidden responsibilities:

- business rule execution,
- ROI calculation,
- recommendation generation,
- SQL,
- provider SDK calls,
- prompt construction,
- ledger append rules,
- cross-module orchestration beyond delegating to application services.

### Application

Purpose:

Coordinate use cases across domain, repositories, providers and policies.

Allowed responsibilities:

- transaction boundary selection,
- use-case orchestration,
- calling domain methods,
- calling repository ports,
- calling provider ports,
- enforcing application-level preconditions,
- emitting domain/application events.

Forbidden responsibilities:

- framework-specific HTTP logic,
- SQL statements,
- provider-specific SDK code,
- UI state,
- hidden domain rules outside the domain layer,
- AI decision authority.

### Domain

Purpose:

Hold business objects, invariants and deterministic rules.

Allowed responsibilities:

- entities,
- value objects,
- domain enums,
- domain policies,
- deterministic calculations when Phase 3 authorizes them,
- state transition rules,
- domain events.

Forbidden responsibilities:

- Spring, FastAPI, React or database annotations that couple domain to frameworks,
- HTTP,
- SQL,
- provider SDK calls,
- file system access,
- environment variables,
- logging infrastructure,
- prompt construction,
- asynchronous job infrastructure.

### Repository

Purpose:

Define persistence ports and implement persistence adapters.

Allowed responsibilities:

- conceptual repository interfaces,
- PostgreSQL adapter implementations,
- mapping between persistence rows and domain objects,
- persistence transactions when owned by adapter/framework,
- query methods required by use cases.

Forbidden responsibilities:

- business decisions,
- ROI rules,
- recommendation rules,
- approval authority,
- AI explanation logic,
- provider calls,
- UI formatting.

### Infrastructure

Purpose:

Provide technical adapters and runtime wiring.

Allowed responsibilities:

- PostgreSQL connection configuration,
- migration tooling,
- JWT support,
- RBAC middleware wiring,
- local Docker Compose,
- OpenTelemetry/Micrometer/Prometheus/Grafana wiring,
- provider adapter shells,
- external configuration,
- health and readiness wiring.

Forbidden responsibilities:

- business meaning,
- evidence interpretation,
- decision scoring,
- financial truth,
- approval workflow semantics,
- hardcoded demo business data,
- real provider secrets committed to the repository.

### AI Engine

Purpose:

Generate natural-language explanations from prepared, bounded context through an Explanation Provider interface.

Allowed responsibilities:

- provider abstraction,
- prompt boundary enforcement,
- explanation request/response models,
- response validation,
- fallback explanation templates,
- provider adapter shells in Phase 2,
- real provider calls only when explicitly authorized after Phase 2.

Forbidden responsibilities:

- modifying persistent data,
- executing business rules,
- deciding recommendations,
- approving or rejecting decisions,
- reading raw source payloads directly,
- seeing secrets or tokens,
- becoming a dependency of the domain model.

## 2. Dependency Rules

Canonical dependency direction:

```text
Frontend
-> API
-> Application
-> Domain

Application
-> Ports
<- Adapters
<- Infrastructure wiring
```

The domain is the center.
Adapters depend inward through ports.
The domain never depends on adapters.
The domain also does not depend on ports unless a later ADR explicitly introduces domain services that require that boundary.

### Dependency Matrix

| From | May depend on | Must not depend on |
|---|---|---|
| Frontend | API contract, generated/client types when available, UI libraries | database, domain internals, provider SDKs, backend repositories |
| API | Application services, request/response DTOs, security middleware, validation | repositories directly for business routes, SQL, provider SDKs, UI components |
| Application | Domain, ports, application DTOs, transaction abstractions | controllers, React, SQL, provider SDKs, persistence entities as business truth |
| Domain | domain value objects, domain policies, domain events | Spring, FastAPI, React, PostgreSQL, OpenAI/Claude SDKs, HTTP clients |
| Ports | domain objects, command/query contracts | concrete adapters, provider SDKs, SQL clients |
| Repository adapters | repository ports, persistence models, database client/framework | controllers, UI, AI prompts, business decisions not expressed by domain/application |
| Provider adapters | provider ports, provider configuration, external clients | domain mutation, approval authority, persistence except technical telemetry |
| Infrastructure | framework configuration, adapters, observability, security wiring | product decisions, ROI rules, recommendation logic |
| AI Engine | Explanation Provider port, prepared context models | raw evidence stores, secrets, domain decision authority |

### Hard Rules

- Controllers call application services, not repositories.
- Application services call ports, not concrete adapters.
- Domain objects do not import framework, database, HTTP or AI packages.
- Persistence adapters implement repository ports.
- Provider adapters implement provider ports.
- React calls REST APIs, not provider APIs.
- Python AI service never becomes the source of recommendation truth.
- No circular dependencies are allowed.

## 3. Package Rules

### Java Package Structure

Official future root package:

```text
backend-java/src/main/java/com/imperator/
```

Allowed package groups:

```text
api/
  controller/
  dto/
  error/
application/
  usecase/
  service/
  command/
  query/
  dto/
domain/
  evidence/
  decision/
  recommendation/
  roi/
  ledger/
  businessvalue/
  identity/
  shared/
ports/
  in/
  out/
adapters/
  persistence/
  web/
  security/
  observability/
  provider/
config/
```

Rules:

- `domain/` contains no framework coupling.
- `api/dto/` DTOs must not enter the domain as entities.
- `ports/out/` defines repository/provider ports.
- `adapters/` implements ports.
- `config/` wires runtime dependencies.
- Phase 2 may create package skeletons, interfaces and route shells only.

### Python Package Structure

Official future root:

```text
backend-python/app/
```

Allowed package groups:

```text
api/
providers/
ports/
models/
services/
config/
observability/
tests/
```

Rules:

- Python exists for AI explanation provider foundation, not business authority.
- `providers/` contains adapter shells for OpenAI, Claude, Ollama, Azure OpenAI or Gemini only when authorized.
- Phase 2 provider implementations must not make real network calls.
- Internal product service-to-service contracts remain aligned with `proto/` and RFC 0002; FastAPI does not replace the internal gRPC/Protobuf direction.

### React Package Structure

Official future root:

```text
frontend/
```

Allowed package groups:

```text
app/
components/
features/
  decision-review/
  ledger/
  business-value/
  integrations/
services/
  api/
styles/
types/
tests/
```

Rules:

- `features/` owns screen composition and local UI state.
- `services/api/` owns HTTP client calls.
- `types/` contains frontend-facing API/domain view types, not backend domain classes.
- Phase 2 may create layout, sidebar, navbar, routing, theme and placeholder screens only.

## 4. Naming Rules

| Concept | Convention | Examples |
|---|---|---|
| Entity | noun, domain meaning, no suffix unless needed | `Evidence`, `Decision`, `Recommendation`, `LedgerEntry` |
| Value Object | noun phrase, immutable meaning | `MoneyAmount`, `EvidenceFreshness`, `ConfidenceLevel` |
| DTO | request/response suffix | `EvidenceImportRequest`, `DecisionResponse`, `ErrorResponse` |
| Repository port | domain name + `Repository` | `EvidenceRepository`, `DecisionRepository` |
| Repository adapter | technology + domain + `Repository` | `PostgresEvidenceRepository` |
| Controller | resource + `Controller` | `EvidenceController`, `DecisionController` |
| Application service | use-case area + `Service` | `DecisionReviewService`, `EvidenceImportService` |
| Use case | verb phrase + `UseCase` | `ImportEvidenceUseCase`, `ApproveDecisionUseCase` |
| Command | verb phrase + `Command` | `ApproveDecisionCommand`, `RejectDecisionCommand` |
| Query | noun phrase + `Query` | `FindDecisionByIdQuery`, `ListLedgerEntriesQuery` |
| Interface/port | capability + `Port`, `Provider` or `Repository` | `ExplanationProvider`, `EvidenceSourcePort` |
| Event | lower-case namespace + version | `evidence.import.accepted.v1`, `ledger.entry.appended.v1` |
| Policy | domain rule + `Policy` | `ApprovalPolicy`, `EvidenceSensitivityPolicy` |
| Enum | noun phrase | `DecisionStatus`, `RoleType`, `EvidenceSeverity` |
| Error class | cause + `Error` or `Exception` | `EvidenceValidationError`, `UnauthorizedActionException` |
| Error code | uppercase snake case | `EVIDENCE_VALIDATION_FAILED` |
| API response | resource + `Response` | `BusinessValueResponse`, `LedgerEntryResponse` |

Rules:

- Use `Decision` for the business object from the domain model.
- Use `DecisionCase` only when an implementation must distinguish the review case wrapper from the core decision object.
- Do not invent synonyms such as `Insight`, `Advice`, `Finding` or `Opportunity` for canonical domain objects.
- Event vocabulary must map back to `docs/architecture/29_Event_Evidence_Vocabulary.md`.

## 5. API Rules

API style:

- REST,
- JSON,
- plural resource names,
- stable logical version `v1`,
- documented behavior before OpenAPI generation.

Route rule:

- Documentation may reference paths such as `/decisions/{id}`.
- Implementation may mount them under a single central base path such as `/api/v1`.
- Do not mix versioning styles inside the same implementation.

Canonical MVP route families:

- `/evidence/import`
- `/decisions`
- `/decisions/{id}`
- `/decisions/{id}/timeline`
- `/decisions/{id}/evidence`
- `/decisions/{id}/roi`
- `/recommendations/{id}`
- `/decisions/{id}/ledger`
- `/decisions/{id}/ledger/approve`
- `/decisions/{id}/ledger/reject`
- `/decisions/{id}/ledger/defer`
- `/decisions/{id}/ledger/mark-implemented`
- `/decisions/{id}/ledger/validate-result`
- `/business-value`
- `/ledger`

### Status Codes

| Code | Use |
|---|---|
| `200` | successful read or command response with body |
| `201` | resource created |
| `202` | command accepted but not complete |
| `204` | successful command with no body |
| `400` | malformed request |
| `401` | missing or invalid authentication |
| `403` | authenticated but forbidden |
| `404` | resource not found |
| `409` | state conflict or idempotency conflict |
| `422` | semantically invalid request |
| `429` | rate limit exceeded |
| `500` | unexpected server error |
| `501` | Phase 2 route shell not implemented |

### Pagination

List endpoints must support a simple consistent pagination model before scale optimization:

- `page`,
- `size`,
- `sort`,
- `direction`.

Default page size must be small enough for local development and demo data.

### Filtering

Filters must be explicit query parameters.

Do not encode business logic in arbitrary search strings during Phase 2.

Examples:

- `status`
- `source`
- `severity`
- `owner`
- `from`
- `to`

### Sorting

Sorting must use allowlisted fields only.

Examples:

- `createdAt`
- `updatedAt`
- `timestamp`
- `businessValue`
- `severity`

### Idempotency

Commands that can create ledger or evidence records must support idempotency.

Required for:

- `POST /evidence/import`
- `POST /decisions/{id}/ledger/approve`
- `POST /decisions/{id}/ledger/reject`
- `POST /decisions/{id}/ledger/defer`
- `POST /decisions/{id}/ledger/mark-implemented`
- `POST /decisions/{id}/ledger/validate-result`

Rules:

- use an `Idempotency-Key` header or equivalent command identifier;
- repeat requests must not create duplicate ledger entries;
- conflicts must return `409`.

### Error Responses

All API errors must use a stable envelope:

```text
code
message
correlationId
details
```

Rules:

- `message` is human-readable but not a stack trace.
- `code` is stable and machine-readable.
- `correlationId` is always present when available.
- `details` must not expose secrets, provider payloads or restricted evidence.

## 6. Database Rules

Database:

- PostgreSQL from first implementation day.

Migration rule:

- Use a migration tool.
- Default choice for Phase 2 is Flyway unless a later ADR explicitly changes it.
- Migration `V1` creates the foundation schema only.
- No seed business data in Phase 2.

ID rule:

- Use UUID primary keys for domain records.
- Do not expose sequential internal IDs as public identifiers.

Relationships:

- Use foreign keys for canonical relationships.
- Decision Graph edges must be represented explicitly.
- Index every foreign key used by reads.
- Index timestamp fields used by timelines and freshness checks.

Soft delete:

- Soft delete may apply to mutable administrative records only.
- Evidence, ledger entries, approvals and decision graph edges are not deleted in normal operation.

Append-only:

- Decision Ledger is append-only.
- Audit-relevant evidence import records are append-only.
- Corrections are represented by new entries, not destructive mutation.

Transactions:

- Application use cases that create or mutate multiple records must use a transaction.
- Ledger append and state transition must be atomic when Phase 3 implements business behavior.

Forbidden:

- SQL in controllers,
- database access from React,
- provider payload storage without sensitivity review,
- secrets in database rows,
- in-memory ledger as implementation truth.

## 7. Event Rules

Event categories:

- evidence events,
- decision events,
- recommendation events,
- approval events,
- ledger events,
- business value events,
- security/audit events,
- technical observability events.

Naming:

- use lower-case namespaces,
- use dot separators,
- include version suffix.

Examples:

- `evidence.import.received.v1`
- `evidence.import.accepted.v1`
- `decision.case.created.v1`
- `recommendation.generated.v1`
- `ledger.entry.appended.v1`
- `approval.recorded.v1`
- `business_value.snapshot.created.v1`
- `security.access.denied.v1`

Required metadata:

- `eventId`
- `eventType`
- `schemaVersion`
- `occurredAt`
- `correlationId`
- `traceId`
- `causationId` when applicable
- `actorId` when applicable
- `source`
- `sensitivity`

Rules:

- Evidence events must map to the Enterprise Evidence Event model.
- Internal events must not invent product states outside the approved vocabulary.
- Event metadata must support traceability from dashboard back to evidence and ledger.
- Phase 2 may define event shells and metadata conventions only.

## 8. Logging Rules

Logs must be structured.

Required common fields:

- `timestamp`
- `level`
- `service`
- `module`
- `correlationId`
- `traceId`
- `actorId` when applicable
- `event`

### INFO

Log:

- application startup and shutdown,
- route shell invocation in Phase 2,
- evidence import request accepted by shell,
- health and readiness changes,
- provider adapter fallback selection,
- module initialization.

### WARN

Log:

- validation failures,
- unauthorized or forbidden attempts without sensitive detail,
- stale evidence,
- provider fallback,
- idempotency conflict,
- degraded dependency.

### ERROR

Log:

- unhandled exceptions,
- database connection failure,
- migration failure,
- provider adapter failure,
- failed security middleware,
- failed transaction.

### AUDIT

Audit-log:

- login success/failure,
- role/permission decision,
- evidence import command,
- approval/rejection/defer command,
- ledger append,
- result validation,
- security boundary violation.

Never log:

- JWTs,
- API keys,
- OAuth tokens,
- provider credentials,
- raw provider payloads,
- full raw evidence payloads unless explicitly classified safe,
- prompts or completions containing sensitive context,
- passwords,
- secrets,
- personal data not required for audit.

## 9. AI Integration Rules

AI role:

AI explains.
AI does not decide.

Allowed context:

- prepared evidence summaries,
- evidence IDs,
- decision ID,
- recommendation ID,
- deterministic recommendation selected by the backend,
- ROI summary already calculated by deterministic logic,
- sensitivity-filtered timeline,
- allowed role context.

Forbidden context:

- secrets,
- raw tokens,
- raw credentials,
- unrestricted raw payloads,
- confidential provider payloads without filtering,
- personal data not needed for explanation,
- hidden system policies,
- database credentials,
- prompts that ask the model to approve, reject or execute decisions.

Prompt boundaries:

- prompts must state that AI cannot change data;
- prompts must state that AI cannot approve or reject;
- prompts must require evidence citations by ID;
- prompts must require uncertainty wording when evidence is incomplete;
- prompts must not request unsupported financial truth.

Output validation:

- response must be valid structured output or a clearly bounded text field;
- citations must reference known evidence IDs;
- output must not invent sources;
- output must not change recommendation status;
- unsafe output must be discarded and replaced by fallback.

Provider abstraction:

- application code depends on `ExplanationProvider`, not OpenAI, Claude, Ollama, Gemini or Azure OpenAI directly;
- each provider adapter is replaceable;
- Phase 2 provider adapters return controlled placeholder responses only.

Fallbacks:

- if provider unavailable, return deterministic fallback explanation;
- fallback must preserve recommendation, ROI and evidence IDs;
- fallback must be observable and auditable.

## 10. Security Rules

Authentication:

- JWT for Phase 2/Phase 3 MVP foundation.
- OAuth2/enterprise SSO providers are future adapter options, not Phase 2 requirements.

Authorization:

- RBAC with MVP roles:
  - `ADMIN`
  - `PLATFORM_ENGINEER`
  - `FINANCE`
  - `AUDITOR`
- Approval authority remains product/domain-specific and must follow `docs/product/28_Identity_Access_Approval_Model.md`.

Input validation:

- validate all request bodies,
- validate all path/query parameters,
- reject unknown enum values,
- reject unsupported source types,
- reject invalid timestamps and currencies.

Output validation:

- never expose restricted fields by default,
- filter response fields by role,
- do not expose raw payload unless the role and sensitivity policy permit it.

Rate limiting:

- apply to auth endpoints,
- apply to import endpoints,
- apply to AI explanation endpoints when implemented,
- log rate-limit events without leaking input data.

Secrets:

- load from environment or secret manager in future deployment,
- never commit secrets,
- never store provider secrets in database rows,
- `.env.example` may contain names only, never real values.

PII and sensitive evidence:

- collect minimum necessary data,
- classify evidence sensitivity,
- redact before sending to AI,
- audit access to sensitive evidence,
- never use sensitive fields for UI convenience.

## 11. Testing Rules

Phase 2 testing objective:

Prove that the foundation builds, starts and preserves boundaries without business intelligence.

Required test categories:

- unit tests for pure domain structures and simple policies when present,
- application tests for empty use-case orchestration when present,
- repository tests for migration/schema foundation when repositories exist,
- API tests for route shell status, auth behavior and error envelope,
- contract tests for provider/repository interfaces,
- security tests for JWT/RBAC middleware,
- observability checks for health, readiness and metrics endpoints.

No UI end-to-end tests are required in Phase 2.

Allowed frontend checks in Phase 2:

- build,
- typecheck,
- lint,
- route rendering smoke tests if lightweight.

Forbidden Phase 2 tests:

- tests that assert ROI calculations,
- tests that assert recommendation ranking,
- tests that call real AI providers,
- tests that call live Jira/GitHub/AWS/OpenAI/Claude,
- tests that rely on fake business truth as product behavior.

## 12. Git Rules

Branch strategy:

- use `phase2/<sprint>-<module>` for Phase 2 foundation work;
- use `phase3/<case>-<module>` for MVP value-loop implementation;
- use `docs/<topic>` for documentation-only changes.

Commit format:

```text
type(scope): short imperative summary
```

Allowed types:

- `docs`
- `feat`
- `fix`
- `test`
- `refactor`
- `chore`
- `ci`
- `build`

Examples:

- `docs(architecture): add implementation contract`
- `feat(backend-java): add evidence domain skeleton`
- `test(api): cover route shell error envelope`

PR requirements:

- state assigned module,
- list authority documents consulted,
- confirm one-module rule,
- list forbidden work avoided,
- list verification performed,
- list R&D evidence updated when applicable.

Review checklist:

- no scope drift,
- no business logic in Phase 2,
- no secrets,
- no provider live calls,
- no controller business logic,
- no SQL outside repository/migration boundary,
- domain remains framework-free,
- logs redact sensitive data,
- tests match sprint scope.

## 13. Agent Rules

These rules apply equally to Codex, Claude Code, Cursor, GPT or any future AI agent.

Before editing:

1. read the assigned prompt,
2. identify the exact module,
3. read authority documents,
4. state forbidden work,
5. inspect existing files,
6. avoid unrelated changes.

During editing:

- edit only the assigned module and directly required test/config files;
- do not create more than one module;
- do not modify product or architecture decisions unless the task is explicitly documentation governance;
- do not infer missing business behavior;
- do not add provider calls;
- do not add secrets;
- do not silently rename canonical domain objects.

After editing:

- run the narrowest useful verification,
- report changed files,
- report tests/checks,
- report unresolved risks,
- update R&D evidence only when actual implementation activity, tests or technical objects exist.

Never modify without explicit founder/CTO instruction:

- Decision Log decisions,
- MVP scope,
- domain object names,
- canonical event vocabulary,
- approval authority,
- recommendation focus,
- AI decision boundary,
- Phase 2/Phase 3 boundary.

Conflict resolution:

1. Decision Log wins.
2. Product/domain contracts define business meaning.
3. Architecture contracts define technical boundaries.
4. This document governs implementation mechanics.
5. If conflict remains, stop and request founder/CTO decision.

## 14. Done Definition

### Implemented

A module is implemented when:

- the assigned module exists,
- it follows package/layer rules,
- it contains no forbidden behavior,
- it builds or passes the relevant local check,
- it is observable enough for its phase,
- tests or smoke checks match the sprint scope,
- no unrelated modules were changed.

### Finished

A sprint item is finished when:

- all assigned modules for that sprint are implemented through separate iterations,
- integration checks pass,
- documentation maps remain accurate,
- R&D evidence is updated for real implementation activity,
- open risks are listed.

### Accepted

A sprint is accepted when:

- reviewer confirms alignment with documents 31-37,
- one-module-per-iteration rule was respected,
- Phase 2 contains no business intelligence,
- Phase 3 contains only the authorized MVP value-loop behavior,
- CI or equivalent verification passes,
- no authority document was contradicted.

## Final Control Statement

This contract turns implementation into controlled execution.

It does not create the product.
It prevents agents from inventing the product while they build it.
