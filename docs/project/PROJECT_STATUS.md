# IMPERATOR Project Status

Last verified: **2026-08-01**

## Current Gate

| Field | Value |
|---|---|
| Product lifecycle | Phase 3 - First Business Value Loop |
| Phase status | Active |
| Sprint 2.8.2 acceptance | PASS |
| Sprint 2.8.3 acceptance | PASS |
| Sprint 2.8.4 acceptance | PASS |
| Sprint 2.8.5 acceptance | PASS |
| Sprint 2.8.6 acceptance | PASS |
| Sprint 2.8.7 acceptance | PASS |
| Sprint 2.8.8 acceptance | PASS |
| Sprint 2.8.8.2 acceptance | PASS |
| Sprint 3.0 acceptance | CERTIFIED |
| Sprint 3.1 acceptance | CERTIFIED |
| Sprint 3.1 closure | COMPLETE |
| Sprint 3.2 acceptance | CERTIFIED |
| Sprint 3.2 closure | COMPLETE |
| Last completed sprint | Sprint 3.2 - Deterministic Decision Creation |
| Phase 2 closure | COMPLETE under D079; Sprints 2.9-2.13 deferred |
| Next authorized sprint | Sprint 3.3 - Deterministic Recommendation And ROI Policy |
| Phase 3 authorization | Authorized by D079 |

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
| REST adapter foundation | COMPLETE | All 15 frozen MVP route shells certified by document 41 |
| Functional runtime composition | CERTIFIED | Spring, existing use cases, repositories and transaction runner verified against PostgreSQL 18.2 |
| Functional REST | IN PROGRESS | Evidence import is functional; remaining MVP route shells retain controlled responses |
| Deterministic Decision creation | CERTIFIED | Eligible Evidence creates one atomic, retry-safe and concurrency-safe Decision |
| Security runtime | DEFERRED | Resequenced after the local business-value demo by D079 |
| Frontend runtime | DEFERRED | Thin Decision Review Workspace follows minimum security |
| Local container runtime | DEFERRED | Operational hardening follows pilot readiness |
| Observability runtime | DEFERRED | Minimum expansion follows operational hardening |
| Java backend CI | NOT STARTED | Required before minimum security and pilot readiness |

## Latest Verification

The latest accepted Sprint 3.2 implementation was verified with:

```text
mvnw.cmd -o clean verify
mvnw.cmd -Ppostgresql-integration clean verify
```

Latest accepted result:

- Java 21 gate: PASS;
- Maven Enforcer: PASS;
- production compilation: 105 files;
- test compilation: 14 files;
- default unit and HTTP contract tests: 67 passed, 0 failed;
- PostgreSQL integration tests: 18 passed, 0 failed;
- executable JAR: created.

Real runtime certification:

- PostgreSQL version: 18.2;
- Flyway V1 migrate: PASS;
- Flyway validate: PASS;
- second Flyway migrate: schema up to date, no pending migration;
- Spring-to-application-to-repository composition: PASS;
- evidence import through the restricted application database role: PASS;
- per-line transaction and duplicate behavior: PASS;
- eligible Evidence-to-Decision transition: PASS;
- atomic `DecisionRepository.createIfAbsent`: PASS;
- identical retry and immutable conflict behavior: PASS;
- progressed Decision replay protection: PASS;
- equivalent and conflicting concurrent creation: PASS;
- repository behavior and use-case transaction boundaries: PASS;
- PostgreSQL server shutdown after certification: PASS.

Real HTTP contract verification:

- `POST /api/v1/evidence/import`: functional `application/x-ndjson` import;
- independent accepted and rejected line results: PASS;
- duplicate UUID response as per-line `REJECTED / DUPLICATE`: PASS;
- normalized Evidence persistence with no raw-event or batch storage: PASS;
- request-wide four-field error envelope: PASS;
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

- Markdown files checked: 154;
- broken local links: 0;
- current gate consistency: PASS;
- exactly one `NEXT` sprint: PASS;
- changed control documentation language: English;
- frozen contracts 34-40 modified: no.

The PostgreSQL integration profile was re-executed for Sprint 3.2 against an
isolated disposable PostgreSQL 18.2 runtime.

## Known Non-Blocking Risks

- Default `clean verify` executes the Spring Boot and HTTP contract suite only.
  Real-database certification remains intentionally explicit through the
  `postgresql-integration` profile and external `IMPERATOR_IT_*` configuration.
- `.github/workflows/proto-ci.yml` is a legacy proto-only workflow. It does not
  validate the Java 21 Maven backend and contains permissive generation steps;
  replacement or hardening belongs to Sprint 3.6.

These risks do not require widening Sprint 3.3. They must remain visible and
must not be misreported as current backend CI coverage.

## Next Sprint Boundary

Sprint 3.3 - Deterministic Recommendation And ROI Policy is the sole next
implementation gate. Its exact formula, evidence-readiness, confidence, risk,
currency, rounding and immutable-input boundary must be reviewed against the
frozen contracts and existing implementation before any change.

Sprint 3.3 may transform the certified `DRC-AOA-001` Decision and its eligible
Evidence into one deterministic Recommendation with estimated savings,
confidence and risk. It does not authorize Explanation Provider or other AI
behavior, Review, Ledger, Result Validation, security, frontend, live
connectors or observability behavior.

Current execution authorities:

- D079 through D081 in `docs/decisions/14_Decision_Log.md`;
- `agents/phase3/README.md`;
- `docs/architecture/42_Deterministic_Decision_Creation_Contract.md`.

## Current Architectural Invariants

- Domain and Application do not depend on Spring, Servlet, JDBC or adapters.
- PostgreSQL adapts to the domain; it does not reshape it.
- REST code lives under `backend-java/api` and `imperator.api.*`.
- The public REST base path is `/api/v1`.
- HTTP correlation is not domain evidence correlation.
- Ledger history is append-only.
- Phase 2 is closed; Phase 3 business behavior remains limited to
  `DRC-AOA-001` and the currently authorized sprint.
- One module is implemented per agent iteration.

## Status Update Ownership

The Context Keeper updates this file after a sprint is formally accepted.
Changes must report verified state only; planned or assumed work must not be
reported as complete.
