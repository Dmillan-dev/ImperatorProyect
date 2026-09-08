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
- Spring Security OAuth2 Resource Server authentication under
  `imperator.api.security`.
- D088 route/method authorization and Evidence response filtering under
  `imperator.api.security` and `imperator.api.decisions`.
- D089 provider-neutral synchronization orchestration and the read-only GitHub
  REST outbound adapter under `imperator.application.synchronizeevidence` and
  `imperator.adapters.out.github`.
- D090 read-only AWS SDK outbound adapter under `imperator.adapters.out.aws`,
  with independently named GitHub and AWS runtime compositions.

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
- PostgreSQL 18.x integration certification covers repositories, constraints,
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
  deterministic Result Validation; its PostgreSQL 18.2 runtime certification
  passed during Sprint 3.7 and discharged D084.
- Sprint 3.5 certifies the deterministic local Evidence-to-Business-Value flow
  and exposes Business Value only as a non-persisted Application projection.
- REST errors use the frozen four-field envelope and `X-Correlation-ID`.
- Sprint 3.7 implements all 15 D086 routes through REST DTOs, mappers and
  Application input ports, including the read-only PostgreSQL query adapter.
- Sprint 3.8 protects every `/api/v1/**` request through the D087 RS256/JWKS
  Resource Server perimeter and derives governance actor identity from the
  validated JWT subject and active role.
- Sprint 3.9 enforces the complete D088 15-route/four-role matrix, preserves
  D083 Application governance and redacts Confidential or Restricted Evidence
  before serialization according to role and evidence type.
- Sprint 4.0 synchronizes the bounded GitHub source into deterministic
  `E-GH-001`, `E-GH-002` and `E-GH-003` Evidence without adding a route,
  scheduler, schema object or business authority.
- Sprint 4.1 synchronizes one bounded AWS account and Region into deterministic
  `E-AWS-001` through `E-AWS-004` Evidence through STS, Cost Explorer, Resource
  Groups Tagging API and CloudWatch read operations only.
- Java 21 verification passes 139 default tests and 31 PostgreSQL integration
  tests against PostgreSQL 18.4 under the PostgreSQL 18.x (18.2+) gate.
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

JWT authentication uses external Spring Resource Server configuration:

- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI`
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWK_SET_URI`
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_AUDIENCES=imperator-api`
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWS_ALGORITHMS=RS256`

Missing or invalid authentication configuration fails closed. IMPERATOR does
not issue tokens, persist users or provide login. D088 authorization consumes
the single validated role and adds no role hierarchy, identity persistence or
business-approval shortcut.

GitHub synchronization uses external Spring configuration:

- `IMPERATOR_GITHUB_ENABLED=true`
- `IMPERATOR_GITHUB_TOKEN`
- `IMPERATOR_GITHUB_ORGANIZATION`
- `IMPERATOR_GITHUB_REPOSITORY`

The connector is disabled by default. When enabled, missing or invalid values
fail closed before any network request. The token is used only by the outbound
adapter and is never persisted, logged or returned. Production access is fixed
to the versioned GitHub.com REST API and remains read-only.

AWS synchronization uses external Spring configuration:

- `IMPERATOR_AWS_ENABLED=true`
- `IMPERATOR_AWS_EXPECTED_ACCOUNT_ID`
- `IMPERATOR_AWS_REGION`

The connector is disabled by default and fails closed before provider calls
when its account or Region configuration is invalid. Credentials are resolved
only through the AWS SDK `DefaultCredentialsProvider`; IMPERATOR does not
accept, persist or log AWS secret settings. STS verifies the exact configured
account before the read-only cost, resource-tag and metric calls proceed.

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

Sprints 3.0 through 4.3 are complete at their documented gates. The Java 21
Maven build is certified through GitHub Actions, and the complete Functional
REST security runtime is certified against PostgreSQL 18.4. D084 is discharged;
D085 through D096 are accepted. D093 adds the bounded R16 composition entry
point without changing Domain policy; Sprint 4.3 certification passed 151
default tests and 34 PostgreSQL 18.6 integration tests and packaged this module
as a non-root, read-only image. D096 freezes the observability boundary;
Sprint 4.4 implementation is the sole next gate and requires separate
authorization. It must not change D086 routes, D087 identity, D088
authorization, connector isolation, Evidence redaction or business authority.
