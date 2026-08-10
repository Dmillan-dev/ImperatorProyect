# Phase 3 - First Business Value Loop Sprint Plan

Status: **ACTIVE**

Current gate: **Sprint 4.3 - Docker Production Runtime**

Authorization: **D079 - Phase 3 Vertical-Slice Acceleration; D085 - MVP Delivery Roadmap Evolution; D091 - Decision Review Workspace Contract**

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
- `docs/product/DECISION_LEDGER_V2.md`;
- `docs/architecture/48_GitHub_Integration_Contract.md`;
- `docs/architecture/49_AWS_Integration_Contract.md`;
- `docs/architecture/50_Executive_Dashboard_Contract.md`.

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
- two certified read-only Evidence connectors: GitHub and AWS; additional
  connectors only at their explicitly authorized gate;
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
| D091 | Decision Review Workspace Contract | ACCEPTED / COMPLETE |
| 4.2 | Executive Dashboard / Decision Review Workspace | CERTIFIED / COMPLETE |
| 4.2.1 | Documentation Synchronization | COMPLETE |
| 4.3 | Docker Production Runtime | NEXT |
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
- the temporary trusted actor context was superseded and removed by Sprint
  3.8 under D087;
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
certified state and handed control to Sprint 3.8. It changed no Java,
SQL, Flyway, tests, dependencies, frozen contracts or Decision Log entries.

## Sprint 3.8 - JWT Authentication

Sprint 3.8 is certified and complete under D087.

Certification evidence:

- integrated implementation commit: `90e5644`;
- Spring Security OAuth2 Resource Server with RS256 and framework-managed JWKS;
- exact issuer, audience, signature, `exp`, optional `nbf`, mandatory `kid`,
  canonical UUID subject and active MVP-role validation;
- all 15 D086 routes require one Bearer JWT;
- missing and invalid credentials preserve the four-field error and correlation
  contracts for every `Accept` value;
- stateless runtime with no login, session, token issuance or Authorization
  Server behavior;
- temporary trusted actor resolver and property removed; its headers are inert;
- governance actor UUID and role originate from validated JWT claims;
- Java 21, 105 default tests and executable JAR: PASS;
- PostgreSQL 18.2, Flyway migrate/validate/no-op migrate and 29 integration
  tests: PASS;
- no Domain, Application business behavior, Ports, schema, Flyway migration,
  route, frozen-contract or Decision Log change;
- status: **CERTIFIED / COMPLETE**.

Sprint 3.8.1 synchronized active control, agent and AI-context documentation
with this certified state and marked Sprint 3.9 as the sole next gate. It
changed no Java, SQL, Flyway, tests, dependencies, frozen contracts or Decision
Log entries.

## Sprint 3.9 - RBAC Authorization

Sprint 3.9 is certified and complete under D088.

Certification evidence:

- integrated contract-freeze commit: `b8e66e8`;
- integrated implementation and certification commit: `66d0e31`;
- exactly one validated role from D087, with no hierarchy or inheritance;
- all 15 D086 routes enforced for `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE` and
  `AUDITOR` according to the frozen matrix;
- D083 required-approver and governance rules remain downstream authority;
- denied commands return the exact route-level `403` contract and produce no
  Decision transition or Ledger entry;
- unknown routes and unsupported methods preserve D086 `404` and `405`;
- Public and Internal Evidence remain visible, Confidential Evidence follows
  the role/type matrix, and Restricted Evidence is always redacted;
- raw payloads and persistence metadata remain unavailable;
- Java 21, 111 default tests and executable JAR: PASS;
- PostgreSQL 18.4, Flyway migrate/validate/no-op migrate and 29 integration
  tests: PASS under the PostgreSQL 18.x (18.2+) certification gate;
- no Domain, Application business behavior, Ports, schema, Flyway migration,
  dependency, route, frozen-contract or Decision Log change;
- status: **CERTIFIED / COMPLETE**.

Sprint 3.9.1 synchronized active control, agent, security and AI-context
documentation with this certified state and marked Sprint 4.0 as the sole next
gate. It changed no Java, SQL, Flyway, tests, dependencies, frozen contracts or
Decision Log entries.

## Sprint 4.0 - GitHub Integration

Sprint 4.0 is certified and complete under D089.

Certification evidence:

- integrated contract-freeze commit: `5cfddd4`;
- integrated implementation and certification commit: `674b0ba`;
- one disabled-by-default, read-only GitHub REST adapter for one organization
  and one repository;
- exact `IMP-214` correlation and deterministic `E-GH-001`, `E-GH-002` and
  `E-GH-003` Evidence mapping;
- provider-neutral synchronization orchestration through the existing Evidence
  import boundary;
- stable replay, source-identity conflict protection, serial pagination,
  bounded retries, rate limits, timeouts and safe failure outcomes;
- token and raw provider text excluded from Evidence, persistence and results;
- no GitHub route, scheduler, UI, Decision, Recommendation, ROI, Ledger or
  Business Value authority;
- Java 21, 128 default tests and executable JAR: PASS;
- PostgreSQL 18.4, Flyway migrate/validate/no-op migrate and 30 integration
  tests: PASS under the PostgreSQL 18.x (18.2+) certification gate;
- Domain Isolation Index, Architectural Stability Index and Decision Stability:
  100%;
- no Domain, REST, schema, Flyway migration, dependency, frozen-contract or
  Decision Log change during implementation;
- status: **CERTIFIED / COMPLETE**.

Sprint 4.0.1 synchronized active control, agent, security and AI-context
documentation with this certified state and marked Sprint 4.1 as the sole next
gate. It changed no Java, SQL, Flyway, tests, dependencies, frozen contracts or
Decision Log entries.

## Sprint 4.1 - AWS Integration

Sprint 4.1 is certified and complete under D090.

Certification evidence:

- one disabled-by-default, read-only AWS SDK adapter for one expected account,
  one Region and the exact `onboarding-assistant-prod` scope;
- AWS SDK `DefaultCredentialsProvider` with no IMPERATOR credential setting,
  persistence or logging, plus exact STS account verification;
- exact `IMP-214` correlation and deterministic `E-AWS-001` through
  `E-AWS-004` Evidence mapping;
- latest fully closed UTC month selection with the authorized final
  PostgreSQL-representable microsecond anchor;
- stable replay, source-identity conflict protection, serial pagination,
  bounded retries, throttling, timeouts and fail-closed provider outcomes;
- no AWS route, scheduler, UI, cloud mutation, Decision, Recommendation, ROI,
  Ledger or Business Value authority;
- Java 21, 139 default tests and executable JAR: PASS;
- PostgreSQL 18.4, Flyway migrate/validate/no-op migrate and 31 integration
  tests: PASS under the PostgreSQL 18.x (18.2+) certification gate;
- Domain Isolation Index and Architectural Stability Index: 100%;
- Decision Stability: PASS with the authorized D090 precision correction;
- no Domain, Application, Port, REST, schema or Flyway migration change;
- status: **CERTIFIED / COMPLETE**.

Sprint 4.1.1 synchronized active control, agent, security and AI-context
documentation with this certified state and marked Sprint 4.2 as the sole next
gate. It changed no Java, SQL, Flyway, tests, dependencies, frozen contracts or
Decision Log entries.

## Sprint 4.2 - Decision Review Workspace

Sprint 4.2 is certified and complete under D091. `Executive Dashboard` remains
the D085 roadmap label; the implemented MVP surface is the single-case
`DRC-AOA-001` Decision Review Workspace.

Certification evidence:

- contract-freeze commit `220c93b` and implementation commit `abf10a9`;
- Node.js 24.19.0, npm 11.17.0 and exact lockfile: PASS;
- format, lint with zero warnings and strict TypeScript: PASS;
- 35 frontend unit/component tests: PASS;
- D091 coverage thresholds: PASS with 95.51% statements, 78.49% branches,
  95.45% functions and 97.84% lines;
- Next.js 16.2.12 production build and npm audit with zero vulnerabilities:
  PASS;
- Playwright Chromium acceptance across desktop, compact and mobile: PASS;
- all four role presentations, Evidence redaction, not-ready Business Value,
  partial failures, timeout and `401` session clearing: PASS;
- same-origin API transport, volatile token handling, strict response schemas,
  correlation, idempotency and response bounds: PASS;
- Java 21, 139 default tests and executable JAR: PASS;
- PostgreSQL 18.4, Flyway migrate/validate/no-op migrate and 31 integration
  tests: PASS;
- no Java, SQL, Flyway, route, schema, Domain, Application, Port, connector,
  security-contract or prior-decision change;
- status: **CERTIFIED / COMPLETE**.

Sprint 4.2.1 synchronized active project-control, agent, security, frontend and
AI-context documentation and marked Sprint 4.3 as the sole next gate. It
changed no runtime code, tests, dependencies, migrations, frozen contracts or
Decision Log entries.

## Demonstration And Pilot Boundary

Sprint 3.5 proved the business-value loop locally with deterministic synthetic
Evidence. It does not authorize real customer data, external exposure or a
pilot.

Before Sprint 4.5 Pilot Readiness can pass:

- certified Java CI must continue to verify the backend;
- certified JWT Authentication and RBAC Authorization must continue to protect
  the relevant routes, data and actions;
- secrets and database credentials must be externalized;
- the certified Sprint 4.2 surface must remain the thin, single-case Decision Review and
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
