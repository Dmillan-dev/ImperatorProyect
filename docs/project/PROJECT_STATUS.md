# IMPERATOR Project Status

Last verified: **2026-08-09**

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
| Sprint 3.4 implementation | PASS |
| Sprint 3.4 PostgreSQL certification | PASS - D084 obligation discharged on 2026-08-02 |
| Sprint 3.4 closure | CERTIFIED / COMPLETE |
| Sprint 3.4.1 documentation synchronization | COMPLETE |
| Sprint 3.5 acceptance | CERTIFIED |
| Sprint 3.5 closure | COMPLETE |
| Sprint 3.5.1 documentation synchronization | COMPLETE |
| Sprint 3.6 implementation | PASS |
| Sprint 3.6 GitHub Actions certification | PASS |
| Sprint 3.6 closure | CERTIFIED / COMPLETE |
| Sprint 3.6.1 documentation synchronization | COMPLETE |
| D085 roadmap decision | ACCEPTED / COMPLETE |
| D086 Functional REST contract | ACCEPTED / COMPLETE |
| Sprint 3.7 implementation | PASS |
| Sprint 3.7 PostgreSQL certification | PASS |
| Sprint 3.7 closure | CERTIFIED / COMPLETE |
| Sprint 3.7.1 documentation synchronization | COMPLETE |
| D087 JWT Authentication contract | ACCEPTED / COMPLETE |
| Sprint 3.8 implementation | PASS |
| Sprint 3.8 PostgreSQL certification | PASS |
| Sprint 3.8 closure | CERTIFIED / COMPLETE |
| Sprint 3.8.1 documentation synchronization | COMPLETE |
| D088 RBAC Authorization contract | ACCEPTED / COMPLETE |
| Sprint 3.9 implementation | PASS |
| Sprint 3.9 PostgreSQL certification | PASS - PostgreSQL 18.4 under the 18.x (18.2+) gate |
| Sprint 3.9 closure | CERTIFIED / COMPLETE |
| Sprint 3.9.1 documentation synchronization | COMPLETE |
| D089 GitHub Integration contract | ACCEPTED / COMPLETE / FROZEN |
| Sprint 4.0 implementation | PASS |
| Sprint 4.0 PostgreSQL certification | PASS - PostgreSQL 18.4 under the 18.x (18.2+) gate |
| Sprint 4.0 closure | CERTIFIED / COMPLETE |
| Sprint 4.0.1 documentation synchronization | COMPLETE |
| Last completed gate | Sprint 4.0.1 - Documentation Synchronization |
| Phase 2 closure | COMPLETE under D079; Sprints 2.9-2.13 deferred |
| Next authorized sprint | Sprint 4.1 - AWS Integration |
| Phase 3 authorization | Authorized by D079 and evolved by D085 |

## Verified Foundation

| Capability | Status | Evidence |
|---|---|---|
| Java build | PASS | Java 21, Maven Wrapper 3.3.4, Maven 3.9.16 |
| Domain | PASS | Framework-free domain model and value objects |
| Application | PASS | Deterministic use cases plus the non-persisted Business Value projection |
| Ports | PASS | Inbound, outbound and explicit transaction ports |
| PostgreSQL persistence | PASS | JDBC adapters and PostgreSQL 18.x certification, most recently 18.4 |
| Database schema | PASS | Flyway V1 and frozen seven-table schema |
| Transactions | PASS | Repository and use-case atomicity certification |
| Web runtime | PASS | Spring Boot executable composition root |
| REST error contract | PASS | Four-field envelope for controlled and framework errors |
| HTTP correlation | PASS | `X-Correlation-ID` validation, normalization and propagation |
| REST adapter foundation | COMPLETE | All 15 frozen MVP route shells certified by document 41 |
| Functional runtime composition | CERTIFIED | Spring, existing use cases, repositories and transaction runner verified against PostgreSQL 18.2 |
| Functional REST | CERTIFIED / COMPLETE | All 15 D086 routes are wired through REST DTOs and mappers to Application input ports and certified against PostgreSQL 18.2 |
| Deterministic Decision creation | CERTIFIED | Eligible Evidence creates one atomic, retry-safe and concurrency-safe Decision |
| Deterministic Recommendation and ROI | CERTIFIED | DRC-AOA-001-v1 derives one atomic Recommendation with annualized savings, confidence and risk |
| Explanation Provider integration | COMPLETE | Optional provider-neutral explanation executes after deterministic persistence and has no decision, ROI or persistence authority |
| Human Review, Ledger and Result Validation | CERTIFIED / COMPLETE | Atomic review and Ledger behavior, replay, linearity, result validation, rollback and concurrency passed against PostgreSQL 18.2; D084 is discharged |
| End-to-End Local Business Value Demo | CERTIFIED / COMPLETE | Deterministic local `DRC-AOA-001` workflow and traceable projection verified with 30 Evidence records |
| JWT Authentication | CERTIFIED / COMPLETE | D087 Resource Server perimeter, RS256/JWKS validation and JWT-derived actor identity passed PostgreSQL 18.2 certification |
| RBAC Authorization | CERTIFIED / COMPLETE | D088 15-route/four-role enforcement, governance preservation and Evidence redaction passed PostgreSQL 18.4 certification |
| GitHub Integration | CERTIFIED / COMPLETE | D089 one-repository, read-only GitHub REST synchronization produces deterministic supporting Evidence and passed PostgreSQL 18.4 certification |
| AWS Integration | NEXT | Sprint 4.1 is authorized as the next isolated connector gate; implementation has not started |
| Frontend runtime | DEFERRED | Thin Decision Review Workspace follows minimum security |
| Local container runtime | DEFERRED | Operational hardening follows pilot readiness |
| Observability runtime | DEFERRED | Minimum expansion follows operational hardening |
| Java backend CI | CERTIFIED / COMPLETE | GitHub-hosted Ubuntu 24.04 run verified Java 21, Maven Wrapper, 89 tests and executable JAR packaging |

## Latest Verification

Sprint 4.0 was certified on 2026-08-04 from committed implementation and
certification state `674b0ba` with:

```text
mvnw.cmd -Ppostgresql-integration clean verify
```

Certification evidence:

- portable Eclipse Adoptium Java `21.0.12`: PASS;
- Maven Wrapper `3.3.4` and Apache Maven `3.9.16`: PASS;
- production compilation: 194 files;
- test compilation: 34 files;
- default unit, HTTP contract, security, GitHub and local demo tests: 128 passed,
  0 failed;
- PostgreSQL version `18.4`: PASS under the PostgreSQL 18.x gate requiring
  major version 18 and minor version 2 or later;
- Flyway V1 migrate: PASS;
- Flyway validate: PASS;
- second Flyway migrate: schema up to date, no pending migration;
- PostgreSQL integration tests: 30 passed, 0 failed;
- exact GET-only GitHub endpoint inventory and mandatory request headers: PASS;
- one organization, one repository and discovered default-branch scope: PASS;
- exact `IMP-214` correlation and ambiguity rejection: PASS;
- deterministic `E-GH-001`, `E-GH-002` and `E-GH-003` mapping and UUIDv5
  source identity: PASS;
- disabled/invalid configuration, serial pagination, bounded retry, rate limit,
  timeout and response-size behavior: PASS;
- stable replay and conflicting-source non-overwrite behavior: PASS;
- protocol-faithful local HTTP stub through existing import orchestration into
  certified PostgreSQL persistence: PASS;
- token, raw PR/review text and provider payload exposure: absent;
- D086 routes, D087 identity, D088 authorization and business authority:
  unchanged;
- executable Spring Boot JAR: created;
- `BUILD SUCCESS`: PASS.

Sprint 4.0.1 documentation verification:

- active control and AI-context documents synchronized: PASS;
- Markdown files checked: 166;
- broken local links: 0;
- stale active Sprint 4.0 status statements: 0;
- unique `NEXT` sprint: 4.1;
- Java, SQL, Flyway, tests, dependencies, frozen contracts and Decision Log
  modified: no.

This full integration-profile run includes the Sprint 3.4 governance and
Ledger certification suite. The D084 deferred obligation is therefore
**DISCHARGED**; D084 remains an immutable historical record of the earlier
environmental exception, not an active certification debt.

Sprint 3.6 CI remains certified independently through GitHub Actions run
`30708049322` for commit `c3bb8f6`, which verified Java 21, Maven Wrapper,
the default build, 89 tests and executable JAR packaging on Ubuntu 24.04.

## Known Non-Blocking Risks

- Default `clean verify` executes the Spring Boot and HTTP contract suite only.
  Real-database certification remains intentionally explicit through the
  `postgresql-integration` profile and external `IMPERATOR_IT_*` configuration.
- JWT runtime requires externally supplied issuer and HTTPS JWKS configuration;
  no identity provider or production key material belongs in this repository.
- D088 is deliberately limited to one validated role, the current single-case
  MVP and no tenant/organization entitlement model. It is not authorization
  for real-customer exposure or a pilot.
- D089 is deliberately limited to one fine-grained token, one organization,
  one repository, on-demand polling and GitHub Evidence only. It adds no public
  trigger, durable cursor or production credential lifecycle.
- No live GitHub smoke test is part of this certified automated gate. D089
  requires a separately authorized non-customer sandbox check before Pilot
  Readiness.
- Sprint 4.1 must keep AWS access least-privilege and read-only and must not
  modify the certified GitHub adapter or broaden Evidence visibility.

## Next Control Gate

Sprint 4.1 - AWS Integration is the sole next implementation gate. No AWS
implementation has started. It may add only the separately authorized AWS
evidence-source increment required by `DRC-AOA-001` and must preserve D086
routes, D087 identity, D088 authorization, D089 GitHub behavior, Evidence
redaction, Application boundaries and connector normalization. Additional
connectors, real customer data, pilot behavior and external exposure remain
prohibited.

Current execution authorities:

- D079 through D089 in `docs/decisions/14_Decision_Log.md`;
- `agents/phase3/README.md`;
- `docs/architecture/46_JWT_Authentication_Contract.md`;
- `docs/architecture/47_RBAC_Authorization_Contract.md`;
- `docs/architecture/48_GitHub_Integration_Contract.md`;
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`;
- `docs/architecture/CONNECTOR_FRAMEWORK.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`.

## Current Architectural Invariants

- Domain and Application do not depend on Spring, Servlet, JDBC or adapters.
- PostgreSQL adapts to the domain; it does not reshape it.
- REST code lives under `backend-java/api` and `imperator.api.*`.
- The public REST base path is `/api/v1`.
- HTTP correlation is not domain evidence correlation.
- Ledger history is append-only.
- Connectors normalize supporting Evidence and have no Decision, ROI, approval
  or Ledger authority.
- Phase 2 is closed; Phase 3 business behavior remains limited to
  `DRC-AOA-001` and the currently authorized sprint.
- One module is implemented per agent iteration.

## Status Update Ownership

The Context Keeper updates this file after a sprint is formally accepted.
Changes must report verified state only; planned or assumed work must not be
reported as complete.
