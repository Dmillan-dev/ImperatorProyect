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
| Sprint 3.3 acceptance | CERTIFIED |
| Sprint 3.3 closure | COMPLETE |
| Sprint 3.3.1 closure | COMPLETE |
| Last completed sprint | Sprint 3.3.1 - Explanation Provider Integration |
| Phase 2 closure | COMPLETE under D079; Sprints 2.9-2.13 deferred |
| Next authorized sprint | Sprint 3.4 - Human Review, Ledger And Result Validation |
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
| Deterministic Recommendation and ROI | CERTIFIED | DRC-AOA-001-v1 derives one atomic Recommendation with annualized savings, confidence and risk |
| Explanation Provider integration | COMPLETE | Optional provider-neutral explanation executes after deterministic persistence and has no decision, ROI or persistence authority |
| Human Review, Ledger and Result Validation | NEXT | Sole authorized Phase 3 implementation gate |
| Security runtime | DEFERRED | Resequenced after the local business-value demo by D079 |
| Frontend runtime | DEFERRED | Thin Decision Review Workspace follows minimum security |
| Local container runtime | DEFERRED | Operational hardening follows pilot readiness |
| Observability runtime | DEFERRED | Minimum expansion follows operational hardening |
| Java backend CI | NOT STARTED | Required before minimum security and pilot readiness |

## Latest Verification

The accepted Sprint 3.3.1 implementation was verified with:

```text
mvnw.cmd -o clean verify
```

Latest accepted result:

- Java 21 gate: PASS;
- Maven Enforcer: PASS;
- production compilation: 107 files;
- test compilation: 17 files;
- default unit and HTTP contract tests: 73 passed, 0 failed;
- successful provider explanation: PASS;
- provider invocation after the deterministic transaction: PASS;
- unavailable-provider isolation: PASS;
- provider-exception isolation: PASS;
- deterministic Recommendation fields unchanged: PASS;
- executable JAR: created.

The latest real-database certification remains Sprint 3.3 and was verified
with:

```text
mvnw.cmd -o clean verify
mvnw.cmd -Ppostgresql-integration clean verify
```

Sprint 3.3 database-certification result:

- Java 21 gate: PASS;
- Maven Enforcer: PASS;
- production compilation: 107 files;
- test compilation: 16 files;
- default unit and HTTP contract tests: 70 passed, 0 failed;
- PostgreSQL integration tests: 22 passed, 0 failed;
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
- canonical `DRC-AOA-001-v1` Evidence and assumption policy: PASS;
- exact `MODEL_CHANGE` action and deterministic reason: PASS;
- monthly recovery `EUR 1620.00`: PASS;
- annualized `Recommendation.estimatedSavings` `EUR 19440.00`: PASS;
- complete-pack confidence/risk `92 / LOW`: PASS;
- missing-quality confidence/risk `90 / MEDIUM`: PASS;
- incomplete mandatory Evidence persists no Recommendation: PASS;
- atomic `RecommendationRepository.createIfAbsent`: PASS;
- identical and progressed Recommendation replay protection: PASS;
- conflicting immutable Recommendation replay: PASS;
- equivalent and conflicting concurrent Recommendation creation: PASS;
- Recommendation and Decision attachment rollback: PASS;
- `ExplanationProvider` invocation during Recommendation creation: none;
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

- Markdown files checked: 152;
- broken local links: 0;
- current gate consistency: PASS;
- exactly one `NEXT` sprint: PASS;
- changed control documentation language: English;
- frozen contracts 34-40 modified: no.

The PostgreSQL integration profile was re-executed for Sprint 3.3 against an
isolated disposable PostgreSQL 18.2 runtime. Sprint 3.3.1 changed no SQL,
repository or persistence behavior and therefore did not claim a new
real-database certification.

## Known Non-Blocking Risks

- Default `clean verify` executes the Spring Boot and HTTP contract suite only.
  Real-database certification remains intentionally explicit through the
  `postgresql-integration` profile and external `IMPERATOR_IT_*` configuration.
- `.github/workflows/proto-ci.yml` is a legacy proto-only workflow. It does not
  validate the Java 21 Maven backend and contains permissive generation steps;
  replacement or hardening belongs to Sprint 3.6.

These risks do not require widening Sprint 3.4. They must remain visible and
must not be misreported as current backend CI coverage.

## Next Sprint Boundary

Sprint 3.4 - Human Review, Ledger And Result Validation is the sole next
implementation gate. Its implementation boundary must be reviewed against the
existing Application, Ledger, persistence and frozen MVP contracts before any
code change. Sprint 3.4 must preserve deterministic Recommendation and
Explanation boundaries and must not widen security, frontend, live-connector
or observability scope.

Current execution authorities:

- D079 through D082 in `docs/decisions/14_Decision_Log.md`;
- `agents/phase3/README.md`;
- `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md`.

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
