# postgresql

## Purpose

Implementar progresivamente el adapter PostgreSQL que persistira el dominio
existente implementando los puertos de salida.

PostgreSQL must adapt to the domain. The domain must not be modified to
accommodate PostgreSQL.

## Who Uses This Folder

- Implementation Agent during Sprint 2.7 persistence microtasks.
- Architecture Guardian to ensure PostgreSQL remains an adapter detail.
- Future persistence tests that verify Repository -> PostgreSQL -> Domain.

## Contains

- PostgreSQL repository adapter implementations:
  - `PostgresEvidenceRepository` implemented for Sprint 2.7.4.1
  - `PostgresDecisionRepository` implemented for Sprint 2.7.4.2
  - `PostgresRecommendationRepository` implemented for Sprint 2.7.4.3
  - `PostgresLedgerRepository` implemented for Sprint 2.7.4.4
- `model/` persistence records.
- `mapper/` domain/persistence translation.

Sprint 2.7.3 status:
- Repository adapter skeleton only.
- Interfaces implemented.
- Methods fail explicitly as not implemented.
- Persistence model records exist.
- PostgreSQL mappers exist.
- No repository behavior yet.

Sprint 2.7.4 planned microdeliverables:

1. `PostgresEvidenceRepository`
   - `save`
   - `findById`
   - `existsById`
2. `PostgresDecisionRepository`
   - `save`
   - `findById`
   - `existsById`
   - decision/evidence relationship persistence
3. `PostgresRecommendationRepository`
   - `save`
   - `findById`
   - `existsById`
   - recommendation/evidence relationship persistence
4. `PostgresLedgerRepository`
   - `append`
   - `findById`
   - `findByDecisionId`
   - evidence snapshot relationship persistence

Repository Minimalism Rule:

- repositories persist and retrieve;
- repositories do not create domain entities except by mapper reconstruction from
  persisted records;
- repositories do not calculate ROI, validate business rules, generate
  recommendations, call AI providers, publish events or build DTOs;
- `PostgresLedgerRepository` must never expose `save`, `update` or `delete`
  for ledger history.

Ledger Immutability Rule:

- once written, a `LedgerEntry` must never be modified, deleted,
  overwritten, replaced or merged;
- corrections are represented by appending another entry;
- `findByDecisionId` must return ledger history in deterministic chronological
  order.

Light CQRS rule:

- write repositories must not grow read-model methods such as `findAllApproved`,
  `findBySeverity`, `findTop10ROI`, `search` or `findOpenCases`;
- dashboard, Business Value, KPI, list and filtering needs belong to future
  query/projection/read-model ports;
- aggregate repositories stay focused on write-model persistence.

Sprint 2.7.4.1 status:

- `PostgresEvidenceRepository` implements only `save`, `findById` and
  `existsById`.
- JDBC, SQL statements and `DataSource` are contained inside this PostgreSQL
  adapter package.
- No repository search, pagination, filtering, query builder, SQL joins,
  domain changes, application changes or port changes were introduced.

Persistence Dependency Rule:

- JDBC, SQL and PostgreSQL-driver-specific implementation details are allowed
  only inside `backend-java/adapters/out/postgresql/`.
- `backend-java/domain/`, `backend-java/application/` and
  `backend-java/ports/` must not import JDBC, SQL, Spring Data, JPA or
  PostgreSQL driver types.

Sprint 2.7.4.2 status:

- `DecisionRepository` contract fix replaced the unused case-id lookup with
  `existsById`.
- `PostgresDecisionRepository` implements only `save`, `findById` and
  `existsById`.
- Decision aggregate persistence includes its decision/evidence relationship
  records.
- Local JDBC transaction handling is used only to keep one aggregate save
  atomic inside the PostgreSQL adapter.
- No domain, application or use-case files were modified.

Sprint 2.7.4.3 status:

- `PostgresRecommendationRepository` implements only `save`, `findById` and
  `existsById`.
- Recommendation aggregate persistence includes its recommendation/evidence
  relationship records.
- Local JDBC transaction handling is used only to keep one aggregate save
  atomic inside the PostgreSQL adapter.
- No domain files were modified.
- The application/port changes in this sequence belong to the authorized
  pre-sprint contract fix that removed the unused recommendation-by-decision
  lookup.

Sprint 2.7.4.4 status:

- `PostgresLedgerRepository` implements only `append`, `findById` and
  `findByDecisionId`.
- `append` uses only `INSERT` statements.
- `findByDecisionId` orders ledger entries by `occurred_at ASC, id ASC`.
- No `save`, `update`, `delete`, `replace` or `merge` method exists on the
  ledger repository.
- Shared flat metadata JSON conversion now lives in a package-private helper
  inside the PostgreSQL adapter to avoid duplicated persistence mapping logic.

## Never Contains

- Domain changes.
- SQL outside authorized repository implementations.
- Migration files.
- Spring annotations.
- JPA annotations.
- JDBC outside authorized repository implementations.
- Connection pools.
- Transaction framework.
- Query builders.
- Ledger mutation operations beyond append.
- Seed data.
- Fake business evidence.
