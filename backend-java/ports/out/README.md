# out

## Purpose

Define what the Application layer requires from the outside world without
choosing an implementation.

## Who Uses This Folder

- Application layer use cases.
- Architecture Guardian to verify ports stay provider-neutral.
- Persistence adapters in future sprints.

## Contains

- Repository interfaces for domain persistence and retrieval needs.
- Append-only ledger port.
- Explanation provider port for bounded natural-language explanation.

Sprint 2.5.3 status:
- `EvidenceRepository`.
- `DecisionRepository`.
- `RecommendationRepository`.
- `LedgerRepository`.
- `ExplanationProvider`.
- No `BusinessValueProjectionPort` until application code proves it is needed.

## Repository Minimalism Rule

Repository ports are persistence boundaries, not business services.

Allowed repository responsibilities:

- `save` an aggregate already created or changed by domain/application code;
- `find` an aggregate or exact relationship required by a use case;
- answer `exists` checks when existence is the required answer;
- `delete` only if the domain explicitly allows deletion.

Forbidden repository responsibilities:

- calculate ROI;
- validate business rules;
- create domain entities except by mapper reconstruction from persisted records;
- execute use cases;
- generate recommendations;
- call AI providers;
- publish events;
- build REST, CLI, UI or SDK DTOs;
- perform dashboard analytics.

Ledger exception:

- `LedgerRepository` exposes `append`, not `save`, because the Decision Ledger
  is append-only.

Light CQRS rule:

- write repositories must not grow read-model methods such as `findAllApproved`,
  `findBySeverity`, `findTop10ROI`, `search` or `findOpenCases`;
- dashboard, Business Value, KPI, list and filtering needs belong to future
  query/projection/read-model ports;
- aggregate repositories stay focused on write-model persistence.

## Never Contains

- PostgreSQL.
- Spring Data.
- JPA entities.
- SQL queries.
- REST DTOs.
- Provider SDKs.
- Implementations.
- Provider-specific model names.
- Raw prompts, completions, secrets or payloads.
