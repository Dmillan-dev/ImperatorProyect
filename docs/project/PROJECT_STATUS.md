# IMPERATOR Project Status

Last verified: **2026-07-30**

## Current Gate

| Field | Value |
|---|---|
| Product lifecycle | Phase 2 - Platform Foundation |
| Phase status | Active |
| Sprint 2.8.2 acceptance | PASS |
| Sprint 2.8.3 acceptance | PASS |
| Last completed sprint | Sprint 2.8.3 - Decision Collection and Detail Route Shells |
| Next authorized sprint | Sprint 2.8.4 - Decision Context Route Shells |
| Phase 3 authorization | Not authorized |

## Verified Foundation

| Capability | Status | Evidence |
|---|---|---|
| Java build | PASS | Java 21, Maven Wrapper 3.3.4, Maven 3.9.16 |
| Domain | PASS | Framework-free domain model and value objects |
| Application | PASS | Five deterministic application use cases |
| Ports | PASS | Inbound, outbound and explicit transaction ports |
| PostgreSQL persistence | PASS | JDBC adapters and PostgreSQL 18.2 certification |
| Database schema | PASS | Flyway V1 and frozen seven-table schema |
| Transactions | PASS | Repository and use-case atomicity certification |
| Web runtime | PASS | Spring Boot executable composition root |
| REST error contract | PASS | Four-field envelope for controlled and framework errors |
| HTTP correlation | PASS | `X-Correlation-ID` validation, normalization and propagation |
| Product REST routes | ACTIVE | Evidence import and decision collection/detail shells return controlled `501` |
| Security runtime | NOT STARTED | Planned for Sprint 2.9 |
| Frontend runtime | NOT STARTED | Planned for Sprint 2.10 |
| Local container runtime | NOT STARTED | Planned for Sprint 2.11 |
| Observability runtime | NOT STARTED | Planned for Sprint 2.12 |
| Java backend CI | NOT STARTED | Planned for Sprint 2.13; legacy Proto CI exists separately |

## Latest Verification

The latest accepted REST foundation was verified with:

```text
mvnw.cmd -o clean verify
```

Latest accepted result:

- Java 21 gate: PASS;
- Maven Enforcer: PASS;
- production compilation: 94 files;
- test compilation: 5 files;
- tests: 20 passed, 0 failed;
- executable JAR: created.

Real HTTP contract verification:

- `POST /api/v1/evidence/import`: controlled `501`;
- `GET /api/v1/decisions`: controlled `501`;
- `GET /api/v1/decisions/{id}`: controlled `501`;
- unversioned product routes: controlled `404`;
- unsupported methods: controlled `405`;
- four-field error envelope: PASS;
- normalized `X-Correlation-ID` response header and body field: PASS.

Documentation integrity verification:

- Markdown files checked: 144;
- broken local links: 0;
- current gate consistency: PASS;
- exactly one `NEXT` sprint: PASS;
- changed control documentation language: English;
- frozen contracts 34-40 modified: no.

The PostgreSQL integration profile was certified previously against PostgreSQL
18.2. It was not re-executed during the documentation-only reorganization.

## Known Non-Blocking Risks

- The current machine has no `IMPERATOR_IT_*` variables and no running
  PostgreSQL service, so the real-database certification was not repeated in
  this gate.
- Default `clean verify` executes the Spring Boot and HTTP contract suite.
  `PostgresRepositoryIT` requires the explicit `postgresql-integration` profile.
- `.github/workflows/proto-ci.yml` is a legacy proto-only workflow. It does not
  validate the Java 21 Maven backend and contains permissive generation steps;
  replacement or hardening belongs to Sprint 2.13.

These risks do not require widening Sprint 2.8.4. They must remain visible and
must not be misreported as current backend CI coverage.

## Next Sprint Boundary

Sprint 2.8.4 may add only the remaining Decision Context HTTP route shells:

```text
GET /api/v1/decisions/{id}/timeline
GET /api/v1/decisions/{id}/evidence
GET /api/v1/decisions/{id}/roi
```

Required behavior:

- routes remain under `imperator.api.decisions`;
- every response is controlled HTTP `501`;
- the D075 error envelope is preserved;
- `X-Correlation-ID` is preserved;
- real HTTP contract tests verify every route.

Forbidden behavior:

- request DTO design;
- path-parameter validation;
- application-port invocation;
- repository or PostgreSQL access;
- transaction execution;
- timeline, evidence or ROI computation;
- domain, application, port or schema changes.

## Current Architectural Invariants

- Domain and Application do not depend on Spring, Servlet, JDBC or adapters.
- PostgreSQL adapts to the domain; it does not reshape it.
- REST code lives under `backend-java/api` and `imperator.api.*`.
- The public REST base path is `/api/v1`.
- HTTP correlation is not domain evidence correlation.
- Ledger history is append-only.
- Phase 2 contains foundation only; business intelligence belongs to Phase 3.
- One module is implemented per agent iteration.

## Status Update Ownership

The Context Keeper updates this file after a sprint is formally accepted.
Changes must report verified state only; planned or assumed work must not be
reported as complete.
