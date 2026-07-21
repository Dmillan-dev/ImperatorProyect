# Application Data Boundary Policy

## Purpose

Cerrar Sprint 2.6.3 definiendo como se comunican adapters, application y
domain sin filtrar entidades de dominio, DTOs externos, modelos de lectura o
detalles de protocolo entre capas.

Esta policy no anade funcionalidad. Es una regla de implementacion para los
futuros sprints de PostgreSQL, REST, CLI, frontend y seguridad.

## Canonical Boundary Flow

```text
Inbound Adapter
-> External DTO
-> Adapter Mapper
-> Application Command or Query
-> Inbound Port
-> Use Case
-> Domain
-> Outbound Port
-> Adapter
```

Response flow:

```text
Use Case
-> Application Result or Query Model
-> Adapter Mapper
-> External DTO
-> Client
```

Forbidden flow:

```text
REST / CLI / UI
-> Domain Entity
```

## Commands

Commands represent intent.

Commands may contain:

- primitive values already converted into domain value objects;
- ids needed to execute one business capability;
- actor, timestamp and reason fields when the use case needs them;
- idempotency key when the command creates evidence, ledger entries or other
  persistent business records.

Commands must not contain:

- domain entities;
- repositories;
- adapters;
- HTTP request objects;
- JSON annotations;
- persistence entities;
- validation;
- input normalization;
- behavior;
- business decisions.

Commands never leave the application layer. External adapters map external
DTOs into commands.

Application use cases validate command completeness before reading from ports
or constructing domain objects.

## Results

Results represent the outcome of a use case.

Results may contain:

- ids;
- status value objects;
- small scalar summaries;
- booleans that communicate accepted/rejected/required next action;
- optional explanation text when already produced by an authorized application
  capability.

Results must not contain:

- full domain entities;
- repositories;
- persistence entities;
- lazy-loaded objects;
- HTTP response objects;
- frontend view state;
- validation logic;
- business logic.

Results never become REST responses directly. External adapters map results to
external response DTOs.

## External DTOs

External DTOs belong only to inbound or outbound adapters.

Examples:

- `EvidenceImportRequest`
- `DecisionResponse`
- `LedgerEntryResponse`
- `ErrorResponse`

External DTOs may contain protocol-friendly shapes, strings and client-facing
field names.

External DTOs must not enter:

- `domain/`;
- `application/`;
- `ports/in`;
- `ports/out`.

## Query Models

Query models are read-oriented shapes.

They are separate from:

- domain entities;
- persistence entities;
- REST response DTOs.

Query models may be introduced only when a read use case requires a stable
application-level read contract. They must not mutate domain state or become
the source of business truth.

Business Value is treated as a projection unless implementation proves it needs
identity and lifecycle.

## Mappers

Mappers belong in adapters.

Allowed mapper locations in future sprints:

- REST adapter;
- CLI adapter;
- persistence adapter;
- frontend adapter layer when applicable.

Forbidden mapper locations:

- domain;
- application use cases;
- domain entities;
- domain value objects;
- inbound ports;
- outbound ports.

Adapters own translation. Application owns orchestration. Domain owns business
meaning.

## Entity Leak Rule

Domain entities must never cross an adapter boundary.

Forbidden:

```text
Decision -> REST Response
Evidence -> REST Response
Recommendation -> REST Response
LedgerEntry -> REST Response
```

Required:

```text
Decision -> Adapter Mapper -> DecisionResponse
Evidence -> Adapter Mapper -> EvidenceResponse
Recommendation -> Adapter Mapper -> RecommendationResponse
LedgerEntry -> Adapter Mapper -> LedgerEntryResponse
```

The same rule applies to CLI output, future SDK responses and frontend-facing
models.

## Domain Boundary

Domain may expose:

- entities;
- value objects;
- domain behavior;
- domain invariants.

Domain must not import or know:

- commands;
- results;
- DTOs;
- mappers;
- query models;
- repositories as implementations;
- HTTP, JSON, SQL, Spring, JPA, React or provider SDKs.

## Application Boundary

Application may expose:

- input ports;
- use cases;
- commands;
- results;
- application exceptions.

Application may depend on:

- domain;
- inbound ports;
- outbound ports.

Application must not depend on:

- REST controllers;
- HTTP request/response types;
- persistence entities;
- SQL;
- JPA;
- Spring annotations;
- frontend models;
- provider SDKs.

## Future Transaction Boundary Rule

Each use case is reserved as one future application transaction boundary.

This is only a boundary rule. It does not authorize Spring, `@Transactional`,
JPA, JDBC, SQL, Unit of Work implementation or persistence adapters inside the
application layer.

| Use Case | Future transaction boundary |
|---|---|
| `ImportEvidenceUseCase` | 1 future read-write transaction |
| `CreateDecisionUseCase` | 1 future read-write transaction |
| `GenerateRecommendationUseCase` | 1 future read-write transaction |
| `ReviewDecisionUseCase` | 1 future read-write transaction |
| `AppendLedgerEntryUseCase` | 1 future read-write transaction |

Detailed choices such as read-only/read-write mode, rollback rules, isolation,
optimistic locking and Unit of Work belong to the future Persistence Adapter
Foundation sprint, where PostgreSQL and adapter behavior actually exist.

## Exception Codes

Stable public exception codes are reserved for a future explicit sprint.

Future format:

```text
IMP-1001 EvidenceNotFound
IMP-2004 DecisionAlreadyClosed
IMP-3002 RecommendationOwnershipViolation
```

Current application exception `code()` values are internal stable identifiers
until the formal error-code registry is authorized. Adapters must treat them as
opaque and must not parse business meaning from message text.

## Acceptance Criteria

Sprint 2.6.3 is complete when:

- commands are documented as intent-only input contracts;
- results are documented as small outcome contracts;
- external DTOs are forbidden from application and domain;
- mappers are forbidden from application and domain;
- query models are separated from domain entities;
- entity leaks to REST, CLI, UI or SDK boundaries are prohibited;
- exception-code reservation is recorded without implementing a registry.
