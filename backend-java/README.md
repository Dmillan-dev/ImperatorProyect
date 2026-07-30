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
- REST error and HTTP correlation infrastructure under `imperator.api.errors`.

Current Phase 2 status:
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
- REST errors use the frozen four-field envelope and `X-Correlation-ID`.
- No product controllers or product routes.
- No JPA.

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

## Authorized Next Use

Sprint 2.7.7 persistence is certified and Sprints 2.8.0 and 2.8.1 are
complete. The next authorized roadmap item is Sprint 2.8.2 - Evidence Import
Route Shell. It may expose only `POST /api/v1/evidence/import` as a controlled
`501` route shell; it must not call application ports, repositories or
PostgreSQL.
