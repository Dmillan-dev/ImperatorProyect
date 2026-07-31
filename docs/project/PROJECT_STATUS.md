# IMPERATOR Project Status

Last verified: **2026-07-31**

## Current Gate

| Field | Value |
|---|---|
| Product lifecycle | Phase 2 - Platform Foundation |
| Phase status | Active |
| Sprint 2.8.2 acceptance | PASS |
| Sprint 2.8.3 acceptance | PASS |
| Sprint 2.8.4 acceptance | PASS |
| Sprint 2.8.5 acceptance | PASS |
| Sprint 2.8.6 acceptance | PASS |
| Sprint 2.8.7 acceptance | PASS |
| Sprint 2.8.8 acceptance | PASS |
| Last completed sprint | Sprint 2.8.8 - Business Value Route Shell |
| Next authorized sprint | Sprint 2.8.8.2 - REST Adapter Foundation Closure |
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
| Product REST routes | ACTIVE | All frozen MVP route shells return controlled `501` |
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
- production compilation: 98 files;
- test compilation: 10 files;
- tests: 53 passed, 0 failed;
- executable JAR: created.

Real HTTP contract verification:

- `POST /api/v1/evidence/import`: controlled `501`;
- `GET /api/v1/decisions`: controlled `501`;
- `GET /api/v1/decisions/{id}`: controlled `501`;
- `GET /api/v1/decisions/{id}/timeline`: controlled `501`;
- `GET /api/v1/decisions/{id}/evidence`: controlled `501`;
- `GET /api/v1/decisions/{id}/roi`: controlled `501`;
- `GET /api/v1/recommendations/{id}`: controlled `501`;
- post-MVP `GET /api/v1/recommendations`: controlled `404`;
- `GET /api/v1/ledger`: controlled `501`;
- `GET /api/v1/decisions/{id}/ledger`: controlled `501`;
- five canonical Ledger command routes: controlled `501`;
- broader `GET /api/v1/ledger/{entryId}`: controlled `404`;
- post-MVP recommendation review aliases: controlled `404`;
- `GET /api/v1/business-value`: controlled `501`;
- undefined Business Value detail routes: controlled `404`;
- unversioned product routes: controlled `404`;
- unsupported methods: controlled `405`;
- four-field error envelope: PASS;
- normalized `X-Correlation-ID` response header and body field: PASS.

Documentation integrity verification:

- Markdown files checked: 147;
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

These risks do not require widening Sprint 2.8.8.2. They must remain visible and
must not be misreported as current backend CI coverage.

## Next Sprint Boundary

Sprint 2.8.8.2 is a documentation-only certification of the complete REST
Adapter Foundation.

Required behavior:

- certify the complete 15-route frozen MVP inventory;
- certify the D075 error envelope and correlation contract;
- certify `/api/v1` versioning and negative route boundaries;
- record the accepted 53-test HTTP verification baseline;
- distinguish completed route topology from unimplemented functional REST;
- resolve the pending pagination roadmap item explicitly;
- close Sprint 2.8 only if every certification gate passes.

Forbidden behavior:

- Java, tests, SQL, dependencies or runtime configuration changes;
- new endpoints, controllers, DTOs, validation or application wiring;
- modifications to frozen contracts 34-40;
- functional REST or Phase 3 behavior;
- architecture redesign.

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
