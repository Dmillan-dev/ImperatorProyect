# application

## Purpose

Orchestrate IMPERATOR use cases without coupling them to REST, PostgreSQL,
frameworks or adapters.

## Who Uses This Folder

- Implementation Agent during application-layer sprints.
- Architecture Guardian to verify orchestration boundaries.
- Future inbound adapters that call use cases.

## Contains

- Business-named use cases.
- Commands and results owned by the application layer.
- Calls to domain objects and outbound ports.
- `APPLICATION_DATA_BOUNDARY_POLICY.md`.

Current Phase 2B status:
- `importevidence/` use case.
- `createdecision/` use case.
- `generaterecommendation/` use case.
- `reviewdecision/` use case.
- `appendledgerentry/` use case.
- `exceptions/` application error taxonomy.
- Application data boundary policy.
- No REST controllers.
- No persistence adapters.
- No transaction framework.
- Use cases implement inbound ports from `ports/in`.
- Use cases expose application exceptions instead of protocol-specific errors.
- Commands and Results must follow the Application Data Boundary Policy.
- Each use case is reserved as one future transaction boundary; transaction
  implementation details are deferred to the persistence adapter sprint.

Sprint 3.5 addition:
- `businessvalue/` projects the validated Evidence, Decision, Recommendation,
  and Ledger state without creating or persisting a Business Value aggregate.

## Never Contains

- Spring annotations.
- SQL or JPA.
- PostgreSQL code.
- HTTP request or response DTOs.
- React state.
- Provider SDK calls.
- Hidden domain rules outside domain entities.
