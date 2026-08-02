# Phase 3 - First Business Value Loop Sprint Plan

Status: **ACTIVE**

Current gate: **Sprint 3.8 - JWT Authentication**

Authorization: **D079 - Phase 3 Vertical-Slice Acceleration; D085 - MVP Delivery Roadmap Evolution**

## Purpose

Phase 3 turns the certified platform foundation into one demonstrable product
flow. It implements only the locked `DRC-AOA-001` AI Onboarding Assistant
Recovery case before broader platform hardening.

This plan does not authorize every listed gate at once. Only the gate marked
`NEXT` in `docs/project/PROJECT_STATUS.md` and
`docs/project/PHASE_AND_SPRINT_MAP.md` may execute.

## Mandatory Reading Order

Before changing any artifact:

1. `docs/project/README.md`;
2. `docs/project/PROJECT_STATUS.md`;
3. `docs/project/PHASE_AND_SPRINT_MAP.md`;
4. D079 and task-relevant decisions in
   `docs/decisions/14_Decision_Log.md`;
5. this plan;
6. task-specific frozen contracts;
7. the existing implementation.

Stop and request founder/CTO resolution if implementation evidence reveals a
real contradiction. Do not rewrite a frozen contract to make implementation
easier.

## Semantic Authorities

Phase 3 behavior must follow:

- `docs/architecture/34_MVP_Implementation_Blueprint.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- `docs/product/24_MVP_Vertical_Slice.md`;
- `docs/product/25_MVP_ROI_Slice.md`;
- `docs/product/27_MVP_Acceptance_Test_Plan.md`;
- `docs/product/API_SPECIFICATION.md`;
- `docs/product/DECISION_LEDGER_V2.md`.

Persistence remains governed by contracts 39 and 40 and by
`database/migrations/V1__initial_schema.sql`.

## Locked Product Boundary

Phase 3 implements exactly:

```text
JSONL Evidence Import
-> Evidence Validation And Normalization
-> Deterministic Decision Creation
-> Deterministic ROI And Recommendation
-> AI Explanation Of Prepared Context
-> Human Review
-> Append-only Ledger
-> Result Validation
-> Realized Business Value
```

Locked constraints:

- one case: `DRC-AOA-001`;
- one recommendation family: AI model downgrade/change with fallback;
- JSONL first, one Enterprise Evidence Event per line;
- manual, static or imported evidence before live connectors;
- PostgreSQL remains the source of truth;
- AI explains deterministic output and never decides, calculates ROI or mutates;
- estimated value never becomes realized value before result validation;
- no new table, route, aggregate or provider integration without explicit
  evidence and authorization.

## Controlled Sequence

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
| 3.7 | Functional REST API | CERTIFIED / COMPLETE |
| 3.7.1 | Documentation Synchronization | COMPLETE |
| 3.8 | JWT Authentication | NEXT |
| 3.9 | RBAC Authorization | PENDING |
| 4.0 | GitHub Integration | PENDING |
| 4.1 | AWS Integration | PENDING |
| 4.2 | Executive Dashboard | PENDING |
| 4.3 | Docker Production Runtime | PENDING |
| 4.4 | Observability | PENDING |
| 4.5 | Pilot Readiness | PENDING |
| 5.0 | MVP Release | PENDING |

The ordering after Sprint 3.6 is frozen by D085 and supersedes only D079's
unexecuted post-CI sequence. The historical Phase 2 Sprint identifiers 2.9
through 2.13 remain deferred and must not be reported as complete.

## Sprint 3.0 - Functional Runtime Composition

Objective:

Create the smallest executable composition that connects the existing Spring
Boot runtime to the existing application ports and PostgreSQL adapters.

Required outcome:

```text
Spring Boot
-> Existing Input Ports And Use Cases
-> Existing Output Ports
-> Existing PostgreSQL Adapters
-> Existing Transaction Runner
-> PostgreSQL V1 Schema
```

Sprint 3.0 may introduce only the configuration, dependencies and tests needed
to compose and verify existing components. It must not replace any controlled
`501` route with functional behavior.

Sprint 3.0 must not:

- change Domain, existing use-case semantics or repository contracts;
- change V1 or create a migration;
- add evidence parsing, ROI, recommendation or review behavior;
- add security, frontend, live connectors, AI provider calls or observability;
- use hardcoded credentials;
- claim PostgreSQL runtime verification without executing it.

Before implementation, the Sprint 3.0 file boundary and runtime configuration
contract must be reviewed against the existing source tree and `pom.xml`.

Certification result:

- PostgreSQL 18.2 runtime: PASS;
- Flyway migrate, validate and second-migrate idempotency: PASS;
- Spring runtime composition: PASS;
- repository and transaction behavior: 14 integration tests passed;
- `mvnw.cmd -Ppostgresql-integration clean verify`: PASS.

## Sprint 3.1 - JSONL Evidence Import, Validation And Normalization

Sprint 3.1 is certified and complete. D080 freezes its runtime contract.

Required flow:

```text
Raw NDJSON Line
-> Transport DTO
-> Transport Validation And Normalization
-> ImportEvidenceCommand
-> ImportEvidenceInputPort
-> ImportEvidenceUseCase
-> Evidence
-> EvidenceRepository
-> PostgreSQL
```

Required behavior:

- consume only `application/x-ndjson` on the frozen evidence-import route;
- process and transact each line independently;
- persist valid normalized Evidence immediately;
- return accepted and rejected counts plus safe per-line results;
- treat stable Evidence UUIDs as equivalent idempotency identifiers;
- return duplicates and other bounded line failures as per-line rejections;
- reserve the existing HTTP error envelope for request-wide failures;
- certify the complete route-to-PostgreSQL path against PostgreSQL 18.2.

Sprint 3.1 may modify only the evidence inbound adapter, the minimum shared
request-error mapping required by that adapter, the existing evidence import
use case for duplicate detection, relevant tests and implementation-facing
documentation. It must not create or modify a Domain entity, Port contract,
PostgreSQL repository contract, migration, table, Decision, Recommendation,
ROI, Review, Ledger, security, connector or provider behavior.

Certification result:

- Java 21 and Maven Enforcer: PASS;
- default unit and HTTP contract tests: 61 passed;
- PostgreSQL 18.2 integration tests: 15 passed;
- Flyway migrate, validate and no-op second migrate: PASS;
- per-line partial processing and duplicate rejection: PASS;
- restricted application-role persistence: PASS;
- Domain, Port, PostgreSQL schema and frozen contracts unchanged;
- status: **CERTIFIED / COMPLETE**.

## Sprint 3.2 - Deterministic Decision Creation

Sprint 3.2 is certified and complete under D081 and architecture contract 42.

Certified flow:

```text
Eligible persisted Evidence
-> CreateDecisionInputPort
-> CreateDecisionUseCase
-> Atomic DecisionRepository.createIfAbsent
-> Decision in CREATED or authoritative persisted state
-> PostgreSQL
```

Certification result:

- Java 21 and Maven Enforcer: PASS;
- default unit and HTTP contract tests: 67 passed;
- PostgreSQL 18.2 integration tests: 18 passed;
- Flyway migrate, validate and no-op second migrate: PASS;
- initial `CREATED` state and eligible Evidence policy: PASS;
- identical retry and immutable creation conflict: PASS;
- progressed Decision replay protection: PASS;
- equivalent and conflicting concurrent creation: PASS;
- one persisted Decision identity and evidence link: PASS;
- Domain, REST, Flyway V1 and frozen contracts unchanged;
- integrated commit: `8bbbc19`;
- status: **CERTIFIED / COMPLETE**.

## Sprint 3.3 - Deterministic Recommendation And ROI Policy

Objective:

Implement the frozen deterministic Decision-to-Recommendation transition for
`DRC-AOA-001`, including annualized estimated savings, confidence and risk.

Certified boundary:

```text
Certified Decision
-> Eligible Evidence Set
-> Deterministic ROI Policy
-> One Recommendation
-> Estimated Savings + Confidence + Risk
-> Existing Repository Ports
-> PostgreSQL
```

Certification result:

- Java 21 and Maven Enforcer: PASS;
- default unit and HTTP contract tests: 70 passed;
- PostgreSQL 18.2 integration tests: 22 passed;
- Flyway migrate, validate and no-op second migrate: PASS;
- exact `MODEL_CHANGE` action and deterministic reason: PASS;
- monthly recovery `EUR 1620.00`: PASS;
- annualized estimated savings `EUR 19440.00`: PASS;
- confidence/risk `92 / LOW` and `90 / MEDIUM` branches: PASS;
- incomplete mandatory Evidence creates no Recommendation: PASS;
- atomic create-if-absent and Decision-row locking: PASS;
- identical, conflicting and progressed replay behavior: PASS;
- equivalent and conflicting concurrent creation: PASS;
- Recommendation and Decision attachment atomicity: PASS;
- Explanation Provider calls: none;
- schema, migration, index and REST changes: none;
- status: **CERTIFIED / COMPLETE**.

## Sprint 3.3.1 - Explanation Provider Integration

Sprint 3.3.1 is complete. It explains only the already persisted deterministic
Recommendation through the existing `ExplanationProvider` port and a prepared,
bounded context.

Required functional boundary:

```text
Persisted deterministic Recommendation
-> Prepared bounded context
-> ExplanationProvider
-> Natural-language explanation
```

Completion result:

- deterministic Recommendation persistence completes before provider
  invocation: PASS;
- prepared context contains Recommendation truth, Evidence ids, assumption ids
  and policy version: PASS;
- optional natural-language explanation result: PASS;
- provider unavailability and exception isolation: PASS;
- Recommendation type, action, reason, savings, confidence and risk unchanged:
  PASS;
- default Java 21 verification: 73 tests passed;
- provider SDK, model, credential, schema, REST and Domain changes: none;
- integrated commit: `838f156`;
- status: **COMPLETE**.

No live vendor adapter is claimed. The runtime remains provider-neutral and
uses the controlled unavailable-provider behavior unless an explicitly
authorized adapter supplies the port.

## Sprint 3.4 - Human Review, Ledger And Result Validation

Sprint 3.4 implementation is complete under D083 and architecture contract 44.
It preserves the certified deterministic Recommendation and completed
Explanation Provider boundary.

Implemented boundary:

```text
Persisted Decision + Recommendation
-> Authorized approve, reject or defer
-> Atomic Decision transition + immutable Ledger append
-> Authorized implementation marker
-> Authorized result validation
-> Deterministic realized recovery and variance
```

Implementation evidence:

- `DecisionRepository.findByIdForUpdate` serializes governance per Decision;
- `LedgerRepository.append` returns the authoritative immutable entry;
- same-operation replay is idempotent and conflicting payloads are rejected;
- `LedgerChain` rejects duplicate roots, missing predecessors, forks, cycles
  and non-increasing occurrence times;
- implementation and result validation remain Ledger facts while Decision
  remains `APPROVED`;
- default Java 21 verification: 88 tests passed;
- REST route shells, Flyway V1, schema, dependencies and Explanation behavior
  unchanged.

The complete PostgreSQL 18.2 integration profile passed on 2026-08-02 with
Java 21 as part of Sprint 3.7 certification. It executed the Sprint 3.4
governance, atomicity, replay, rollback, concurrency, Ledger linearity and
fork-prevention suite. The D084 deferred obligation is therefore discharged;
D084 remains the immutable historical record of the earlier unavailable
environment.

Sprint 3.4.1 synchronized active documentation with this verified state. It
changed no Java, SQL, tests, frozen contracts or Decision Log entries.

## Sprint 3.5 - End-to-End Local Business Value Demo

Sprint 3.5 is certified and complete. It composes the existing bounded
`DRC-AOA-001` capabilities into one deterministic local workflow:

```text
NDJSON Evidence
-> Decision
-> Recommendation + deterministic Explanation
-> Approval
-> Implementation marker
-> Result Validation
-> non-persisted Business Value projection
```

Certification evidence:

- deterministic dataset: 30 accepted Evidence records, 0 rejected;
- Business Value remains an Application projection with no aggregate,
  repository, table, cache or REST wiring;
- realized savings originate only from the authoritative `result_validated`
  Ledger fact;
- policy version, assumptions, Evidence ids, Decision, Recommendation and
  ordered Ledger history remain visible;
- identical command replay creates no additional Ledger entries and produces
  an equal projection;
- Java 21 Maven verification: 89 tests passed;
- REST route behavior, Flyway V1, schema, dependencies, frozen contracts and
  Decision Log: unchanged;
- integrated commit: `f67257d`;
- status: **CERTIFIED / COMPLETE**.

Sprint 3.5.1 synchronized active project-control and AI-agent context
documentation. It changed no Java, SQL, tests, dependencies, frozen contracts
or Decision Log entries.

## Sprint 3.6 - Basic Java CI

Sprint 3.6 is certified and complete.

Certification evidence:

- integrated commit: `c3bb8f6`;
- workflow: `.github/workflows/java-ci.yml`;
- real GitHub Actions run: `30708049322`;
- GitHub-hosted Ubuntu 24.04 job: PASS;
- Maven Wrapper script `3.3.4`: PASS;
- Apache Maven `3.9.16`: PASS;
- Eclipse Adoptium Java 21: PASS;
- `clean verify`: PASS;
- tests: 89 passed, 0 failed, 0 errors, 0 skipped;
- executable JAR and `BUILD SUCCESS`: PASS;
- official GitHub actions use immutable commit pins;
- obsolete permissive Proto-only CI removed;
- Java, tests, dependencies, schema, Flyway, documentation, frozen contracts
  and roadmap unchanged by the implementation;
- status: **CERTIFIED / COMPLETE**.

Sprint 3.6.1 synchronized active documentation with this certified state. It
changed no Java, tests, SQL, Flyway, dependencies, frozen contracts or Decision
Log entries.

## Sprint 3.7 - Functional REST API

Sprint 3.7 is certified and complete under D086.

Certification evidence:

- integrated implementation commit: `9ba2138`;
- exactly 15 D086 routes mapped to REST DTOs, REST mappers and Application
  input ports;
- eight route-aligned query input ports and use cases plus one read-only
  `MvpReadModelQueryPort` PostgreSQL adapter;
- no controller-to-repository shortcut and no business policy in REST;
- temporary trusted actor context remains local-only and disabled by default;
- Java 21 and Maven Enforcer: PASS;
- 98 default unit, HTTP contract and local demo tests: PASS;
- PostgreSQL 18.2 and 29 integration tests: PASS;
- Flyway migrate, validate and second no-op migrate: PASS;
- functional read projections, pagination, ordering, governance commands,
  replay and transaction behavior through PostgreSQL: PASS;
- executable JAR and `BUILD SUCCESS`: PASS;
- Domain, write repositories, V1, Flyway migrations, dependencies, frozen
  contracts and Decision Log unchanged by implementation and certification;
- status: **CERTIFIED / COMPLETE**.

Sprint 3.7.1 synchronized active control and AI-context documentation with this
certified state and marked Sprint 3.8 as the sole next gate. It changed no Java,
SQL, Flyway, tests, dependencies, frozen contracts or Decision Log entries.

## Demonstration And Pilot Boundary

Sprint 3.5 proved the business-value loop locally with deterministic synthetic
Evidence. It does not authorize real customer data, external exposure or a
pilot.

Before Sprint 4.5 Pilot Readiness can pass:

- certified Java CI must continue to verify the backend;
- JWT Authentication and RBAC Authorization must protect the relevant routes
  and actions;
- secrets and database credentials must be externalized;
- the Sprint 4.2 surface must remain the thin, single-case Decision Review and
  Business Value experience, not a broad Executive Workspace;
- required acceptance, negative and audit tests must pass.

## Execution Discipline

- one bounded deliverable per iteration;
- one `NEXT` control gate at a time;
- Domain Isolation Index must remain 100%;
- Architectural Stability Index target is at least 95%;
- Decision Stability target is 100%;
- no speculative abstractions, routes, providers or persistence structures;
- every code-bearing sprint ends with Java 21 Maven verification and relevant
  real-runtime tests;
- documentation reports verified state only.

## Phase 3 Exit

Phase 3 remains active after Sprint 3.5. Full closure requires the frozen MVP
acceptance path, including deterministic value, AI explanation, human
authority, append-only audit history, minimum security, the review workspace
and minimum operational verification.
