# IMPERATOR Phase and Sprint Map

Status: **Active execution index**

This document separates lifecycle phases from implementation sprints. It is an
index, not a replacement for contracts or acceptance evidence.

## Status Vocabulary

| Status | Meaning |
|---|---|
| COMPLETE | Formally closed and preserved as history |
| CERTIFIED | Executed against its required real runtime and accepted |
| ACTIVE | Parent phase currently being executed |
| NEXT | Only implementation gate currently authorized |
| PENDING | Planned but not authorized |
| FROZEN | Approved semantic contract; modification requires explicit approval |
| DEFERRED | Intentionally outside the current sequence |

## Phase Overview

| Phase | Objective | Status | Primary control |
|---|---|---|---|
| Phase 0 | Strategy, product definition and architecture readiness | COMPLETE | `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` |
| Phase 1 | Freeze the limited MVP contract and agentic delivery dossier | COMPLETE | `agents/phase1/12_phase1_closure.md` |
| Phase 2 | Build executable platform foundation without product intelligence | COMPLETE | D079; `agents/phase2/README.md` |
| Phase 3 | Implement the first `DRC-AOA-001` business value loop | ACTIVE | `agents/phase3/README.md` |

## Phase 0 - Strategy and Readiness

Phase 0 established:

- business problem, ICP and commercial thesis;
- limited Decision Recovery Workflow;
- canonical product language;
- core domain and conceptual API;
- security, quality and connector boundaries;
- architecture readiness and non-goals.

Primary document groups:

- `docs/business/`;
- `docs/product/`;
- `docs/architecture/18_*` through `30_*`;
- `docs/decisions/`;
- `docs/rfcs/`.

These documents are mostly reference or historical context. They do not identify
the current implementation gate.

## Phase 1 - MVP Contract Dossier

Phase 1 froze the first implementable MVP:

- one Decision ROI Case;
- one evidence chain;
- one deterministic recommendation;
- AI explanation only;
- human review;
- append-only ledger history;
- one Decision Review Workspace.

Primary contracts:

- `docs/architecture/31_MVP_Implementation_Standard.md`;
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`;
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`;
- `docs/architecture/34_MVP_Implementation_Blueprint.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `agents/phase1/`.

Phase 1 is closed as a documentation and control dossier. Its stage files are
historical inputs, not current sprint authorization.

## Phase 2 - Platform Foundation

Phase 2 builds technical foundation only. It must not implement ROI
calculation, recommendation-engine behavior, real AI calls, live connectors or
the Phase 3 business workflow.

### Main Sprint Sequence

| Sprint | Objective | Status |
|---|---|---|
| Sprint 0 | Contract Gate | COMPLETE |
| Sprint 1 | Repository and Project Shell | COMPLETE |
| Sprint 2.1 | Domain Package Skeleton | COMPLETE |
| Sprint 2.2 | Domain Value Objects | COMPLETE |
| Sprint 2.3 | Domain Entities and Aggregates | COMPLETE |
| Sprint 2.4 | Outbound Ports | COMPLETE |
| Sprint 2.5 | Application Layer | COMPLETE |
| Sprint 2.6 | Application Contracts | COMPLETE |
| Sprint 2.7 | PostgreSQL Persistence Foundation | CERTIFIED |
| Sprint 2.8 | REST Adapter Foundation | COMPLETE |
| Sprint 2.9 | JWT/RBAC Foundation | DEFERRED by D079 |
| Sprint 2.10 | React Frontend Foundation | DEFERRED by D079 |
| Sprint 2.11 | Docker Local Foundation | DEFERRED by D079 |
| Sprint 2.12 | Observability Foundation | DEFERRED by D079 |
| Sprint 2.13 | CI and R&D Evidence Foundation | DEFERRED by D079 |
| Python AI Provider Foundation | Technical provider boundary only | DEFERRED |

Phase 2 is complete at the certified foundation boundary. Deferred Sprints 2.9
through 2.13 were not executed and must never be reported as complete. D079
resequences their capabilities after the first local Phase 3 business-value
demonstration.

### Sprint 2.7 - PostgreSQL Persistence

| Micro-sprint | Deliverable | Status |
|---|---|---|
| 2.7.1 | PostgreSQL adapter foundation | COMPLETE |
| 2.7.2 | Adapter-owned persistence records | COMPLETE |
| 2.7.3 | Pure PostgreSQL/domain mappers | COMPLETE |
| 2.7.4.1 | Evidence repository | COMPLETE |
| 2.7.4.2 | Decision repository | COMPLETE |
| 2.7.4.3 | Recommendation repository | COMPLETE |
| 2.7.4.4 | Append-only ledger repository | COMPLETE |
| 2.7.5.1 | Persistence transaction contract | COMPLETE |
| 2.7.5.2 | Transaction standardization | COMPLETE |
| 2.7.6.0 | Maven and Java 21 build foundation | COMPLETE |
| 2.7.6.1 | Persistence schema contract | COMPLETE |
| 2.7.6.2 | Flyway V1 initial schema | COMPLETE |
| 2.7.6.3 | PostgreSQL/Flyway runtime validation | CERTIFIED |
| 2.7.7 | PostgreSQL integration and repository behavior | CERTIFIED |
| 2.7.7.1 | Persistence certification closure | COMPLETE |

Frozen persistence authorities:

- `docs/architecture/39_Persistence_Transaction_Contract.md`;
- `docs/architecture/40_Persistence_Schema_Contract.md`;
- `database/migrations/V1__initial_schema.sql`.

### Sprint 2.8 - REST Adapter

| Micro-sprint | Deliverable | Status |
|---|---|---|
| 2.8.0 | Spring Boot Web Runtime Foundation | COMPLETE |
| 2.8.1 | REST Error and Correlation Contract | COMPLETE |
| 2.8.2 | Evidence Import Route Shell | COMPLETE |
| 2.8.3 | Decision Collection and Detail Route Shells | COMPLETE |
| 2.8.4 | Decision Context Route Shells | COMPLETE |
| 2.8.5 | Recommendation Detail Route Shell | COMPLETE |
| 2.8.6 | Ledger Read Route Shells | COMPLETE |
| 2.8.7 | Ledger Command Route Shells | COMPLETE |
| 2.8.8 | Business Value Route Shell | COMPLETE |
| 2.8.8.2 | REST Adapter Foundation Closure | COMPLETE |
| Phase 3 list-query implementation | Pagination binding and validation | DEFERRED |

REST module convention:

```text
backend-java/api/errors           -> imperator.api.errors
backend-java/api/evidence         -> imperator.api.evidence
backend-java/api/decisions        -> imperator.api.decisions
backend-java/api/recommendations  -> imperator.api.recommendations
backend-java/api/ledger           -> imperator.api.ledger
backend-java/api/businessvalue    -> imperator.api.businessvalue
backend-java/api/pagination       -> imperator.api.pagination
```

HTTP paths retain documented kebab-case where applicable, for example
`/api/v1/business-value`.

REST closure authority:

- `docs/architecture/41_REST_Adapter_Foundation_Closure.md`.

## Phase 3 - First Business Value Loop

Phase 3 is authorized by D079 and active.

Its objective is the first complete `DRC-AOA-001` flow:

```text
Evidence
-> Decision
-> Recommendation
-> Human Review
-> Ledger
-> Result Validation
-> Realized Business Value
```

Phase 3 must not begin until the Phase 2 foundation gate is formally closed.
Its semantic authority is the MVP blueprint and vertical-slice documentation,
not the Phase 2 route-shell plan.

### Controlled Delivery Sequence

| Gate | Objective | Status |
|---|---|---|
| 3.0 | Functional Runtime Composition | CERTIFIED |
| 3.1 | JSONL Evidence Import, Validation And Normalization | CERTIFIED |
| 3.2 | Deterministic Decision Creation | CERTIFIED |
| 3.3 | Deterministic Recommendation And ROI Policy | CERTIFIED |
| 3.3.1 | Explanation Provider Integration | COMPLETE |
| 3.4 | Human Review, Ledger And Result Validation | CERTIFIED / COMPLETE; D084 obligation discharged |
| 3.4.1 | Documentation Synchronization | COMPLETE |
| 3.5 | End-to-End Local Business Value Demo | CERTIFIED / COMPLETE |
| 3.5.1 | Project Control Documentation Synchronization | COMPLETE |
| 3.6 | Basic Java CI | CERTIFIED / COMPLETE |
| 3.6.1 | Documentation Synchronization | COMPLETE |
| D085 | MVP Delivery Roadmap Evolution | ACCEPTED / COMPLETE |
| D086 | Functional REST Application Contract | ACCEPTED / COMPLETE |
| D087 | JWT Authentication Contract | ACCEPTED / COMPLETE |
| 3.7 | Functional REST API | CERTIFIED / COMPLETE |
| 3.7.1 | Documentation Synchronization | COMPLETE |
| 3.8 | JWT Authentication | CERTIFIED / COMPLETE |
| 3.8.1 | Documentation Synchronization | COMPLETE |
| D088 | RBAC Authorization Contract | ACCEPTED / COMPLETE |
| 3.9 | RBAC Authorization | CERTIFIED / COMPLETE |
| 3.9.1 | Documentation Synchronization | COMPLETE |
| D089 | GitHub Integration Contract | ACCEPTED / COMPLETE |
| 4.0 | GitHub Integration | CERTIFIED / COMPLETE |
| 4.0.1 | Documentation Synchronization | COMPLETE |
| D090 | AWS Integration Contract | ACCEPTED / COMPLETE |
| 4.1 | AWS Integration | CERTIFIED / COMPLETE |
| 4.1.1 | Documentation Synchronization | COMPLETE |
| 4.2 | Executive Dashboard | NEXT |
| 4.3 | Docker Production Runtime | PENDING |
| 4.4 | Observability | PENDING |
| 4.5 | Pilot Readiness | PENDING |
| 5.0 | MVP Release | PENDING |

Sprint 3.5 proved the local product flow with a deterministic 30-line NDJSON
dataset and an Application-only, non-persisted Business Value projection. Its
Java 21 verification passed 89 tests. It is not full MVP acceptance and does
not authorize real customer data, external exposure or a pilot. Security, safe
configuration and every later acceptance gate remain mandatory before Sprint
4.5 can pass. Functional REST delivery is followed by separate JWT and RBAC
gates.

Sprint 3.7 certification executed the full PostgreSQL integration profile on
2026-08-02 with Java 21 and PostgreSQL 18.2. Flyway migrate, validate and the
second no-op migrate passed; 98 default tests and 29 PostgreSQL integration
tests passed. The run covered the Sprint 3.4 governance, atomicity, replay,
rollback, concurrency, Ledger linearity and fork-prevention suite, so the D084
deferred obligation is discharged. It also certified all 15 D086 REST routes
through real Application and PostgreSQL composition. D084 remains historical
and no longer blocks Pilot Readiness or MVP closure.

Sprint 3.8 certification executed the full PostgreSQL integration profile on
2026-08-02 with Java 21 and PostgreSQL 18.2. Flyway migrate, validate and the
second no-op migrate passed; 105 default tests and 29 PostgreSQL integration
tests passed. The D087 Resource Server perimeter now protects all 15 D086
routes, validates RS256 JWTs through JWKS, and supplies governance actor identity
from validated claims. Authentication is complete; authorization remains the
separate Sprint 3.9 gate.

Sprint 3.9 certification executed the full PostgreSQL integration profile on
2026-08-03 with Java 21 and PostgreSQL 18.4. Flyway migrate, validate and the
second no-op migrate passed; 111 default tests and 29 PostgreSQL integration
tests passed. D088 is enforced across the complete 15-route/four-role matrix,
with D083 governance preserved and Evidence filtered before serialization.
The certification gate requires PostgreSQL major version 18 and minor version
2 or later. Authorization is complete.

Sprint 4.0 certification executed the full PostgreSQL integration profile on
2026-08-04 with Java 21 and PostgreSQL 18.4. Flyway migrate, validate and the
second no-op migrate passed; 128 default tests and 30 PostgreSQL integration
tests passed. D089 is implemented by one read-only GitHub REST adapter and one
provider-neutral synchronization use case. The run proved deterministic
`E-GH-001` through `E-GH-003` mapping, replay, conflict protection, bounded
failure behavior and persistence without route, schema, dependency or business
authority drift. GitHub Integration is complete and remains unchanged.

Sprint 4.1 certification executed the full PostgreSQL integration profile on
2026-08-09 with Java 21 and PostgreSQL 18.4. Flyway migrate, validate and the
second no-op migrate passed; 139 default tests and 31 PostgreSQL integration
tests passed. D090 is implemented by one read-only AWS SDK adapter for one
expected account, one Region and the exact `onboarding-assistant-prod` scope.
The run proved STS identity verification, deterministic `E-AWS-001` through
`E-AWS-004` mapping, final-microsecond period anchoring, replay, conflict
protection and bounded failure behavior without route, schema, Domain,
Application, Port, GitHub or business-authority drift. AWS Integration is
complete; Executive Dashboard is the separate Sprint 4.2 gate.

Active Phase 3 execution authority:

- D079 through D090 in `docs/decisions/14_Decision_Log.md`;
- `agents/phase3/README.md`.

## Gate Transition Rule

A sprint changes from `NEXT` to `COMPLETE` or `CERTIFIED` only when:

1. its authorized files and behavior are present;
2. its required build and tests pass;
3. Architecture Guardian and Product Guardian gates pass;
4. documentation status is synchronized;
5. the work is integrated cleanly;
6. the founder accepts the closure or explicitly waives a gate.

Only one delivery control gate may be `NEXT` at a time.
