# backend-java

## Purpose

Primary Java domain and application runtime for IMPERATOR.

## Who Uses This Folder

- Implementation Agent during Java backend sprints.
- Architecture Guardian to verify hexagonal boundaries.
- Quality Agent to review domain, application and adapter code once authorized.

## Contains

- Java domain model.
- Application use cases.
- Inbound and outbound ports.
- Application exceptions and data-boundary policy.
- PostgreSQL JDBC outbound adapter implementations.
- Explicit transaction port and PostgreSQL transaction runner.
- Spring Boot web runtime and executable composition root.
- Conditional PostgreSQL runtime composition under `imperator.bootstrap`.
- REST error and HTTP correlation infrastructure under `imperator.api.errors`.

Current Phase 3 foundation:
- Pure Java domain foundation exists.
- Application use cases exist.
- Inbound and outbound ports exist.
- Application exceptions exist.
- Application data-boundary policy exists.
- Maven Wrapper builds the backend reproducibly with Java 21.
- PostgreSQL outbound repository implementations exist.
- PostgreSQL persistence records exist.
- PostgreSQL mappers exist.
- Flyway V1 defines the frozen seven-table schema.
- PostgreSQL 18.2 integration certification covers repositories, constraints,
  the full persistence lifecycle and all five use-case transaction boundaries.
- Spring Boot starts through `imperator.bootstrap.ImperatorApplication`.
- Sprint 3.0 composes the existing repositories, transaction runner and five
  application input ports when PostgreSQL runtime configuration is enabled.
- Sprint 3.1 implements partial per-line NDJSON evidence import through the
  existing evidence input port and certified PostgreSQL adapter.
- Sprint 3.2 implements deterministic, idempotent Decision creation.
- Sprint 3.3 derives the first `MODEL_CHANGE` Recommendation, annualized ROI,
  confidence and risk through the frozen `DRC-AOA-001-v1` Domain policy.
- Sprint 3.3.1 invokes the replaceable `ExplanationProvider` only after
  deterministic persistence and returns optional natural-language text without
  changing business truth.
- Sprint 3.4 implements atomic Human Review, append-only Ledger governance and
  deterministic Result Validation; its PostgreSQL runtime certification remains
  deferred under D084.
- Sprint 3.5 certifies the deterministic local Evidence-to-Business-Value flow
  and exposes Business Value only as a non-persisted Application projection.
- REST errors use the frozen four-field envelope and `X-Correlation-ID`.
- The Evidence import route is functional; the other 14 frozen MVP routes
  remain controlled `501` shells.
- No JPA.

## Functional Runtime Configuration

PostgreSQL composition is opt-in and uses external Spring configuration:

- `IMPERATOR_POSTGRESQL_ENABLED=true`
- `IMPERATOR_POSTGRESQL_URL`
- `IMPERATOR_POSTGRESQL_USERNAME`
- `IMPERATOR_POSTGRESQL_PASSWORD`

When disabled or omitted, the existing web route-shell runtime starts without
database beans. When enabled, missing or blank connection properties fail
startup. Bean creation does not open a connection; real connectivity is
verified by the `postgresql-integration` profile.

Application runtime credentials do not execute DDL. Flyway continues to run
through the migration profile with its separate administrative principal before
the application principal is used.

Deterministic Recommendation persistence completes before the optional
`ExplanationProvider` invocation. The runtime supplies an unavailable provider
by default and allows a replaceable provider adapter to override it. Provider
absence or failure cannot alter or roll back Recommendation or Decision state.
No vendor SDK, model or credential contract is selected here.

## Persistence Certification

Run the PostgreSQL certification gate with Java 21 and an empty isolated
PostgreSQL database:

```text
mvnw.cmd -Ppostgresql-integration clean verify
```

Required environment variables:

- `IMPERATOR_IT_DATABASE`
- `IMPERATOR_IT_DB_URL`
- `IMPERATOR_IT_ADMIN_USER`
- `IMPERATOR_IT_ADMIN_PASSWORD`
- `IMPERATOR_IT_APP_USER`
- `IMPERATOR_IT_APP_PASSWORD`

The profile executes Flyway migrate, Flyway validate, a second no-op migrate
and the complete Failsafe integration suite. It fails when no integration test
is found.

## Never Contains

- Python services.
- React frontend code.
- Docker or infrastructure runtime files.
- Prompt templates or AI provider prompts.
- Database migrations.
- Connector secrets or credentials.

## Authorized Current Use

Sprints 3.0 through 3.6 are complete at their documented gates. The Java 21
Maven build is certified through GitHub Actions with 89 passing tests. Sprint
3.4 PostgreSQL runtime certification remains deferred under D084. D085 - MVP
Delivery Roadmap Evolution is the sole next control gate and authorizes no
backend modification.
