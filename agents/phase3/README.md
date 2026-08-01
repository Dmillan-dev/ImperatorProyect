# Phase 3 - First Business Value Loop Sprint Plan

Status: **ACTIVE**

Current gate: **Sprint 3.3.1 - Explanation Provider Integration**

Authorization: **D079 - Phase 3 Vertical-Slice Acceleration**

## Purpose

Phase 3 turns the certified platform foundation into one demonstrable product
flow. It implements only the locked `DRC-AOA-001` AI Onboarding Assistant
Recovery case before broader platform hardening.

This plan does not authorize every listed sprint at once. Only the sprint marked
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

| Sprint | Objective | Status |
|---|---|---|
| 3.0 | Functional Runtime Composition | CERTIFIED |
| 3.1 | JSONL Evidence Import, Validation And Normalization | CERTIFIED |
| 3.2 | Deterministic Decision Creation | CERTIFIED |
| 3.3 | Deterministic Recommendation And ROI Policy | CERTIFIED |
| 3.3.1 | Explanation Provider Integration | NEXT |
| 3.4 | Human Review, Ledger And Result Validation | PENDING |
| 3.5 | End-to-End Local Business Value Demo | PENDING |
| 3.6 | Basic Java CI | PENDING |
| 3.7 | Minimum JWT And RBAC | PENDING |
| 3.8 | Thin Decision Review Workspace | PENDING |
| 3.9 | Pilot Readiness | PENDING |
| 3.10 | Docker And Operational Hardening | PENDING |
| 3.11 | Observability Expansion And MVP Closure | PENDING |

The ordering after Sprint 3.5 preserves the capabilities originally planned as
Phase 2 Sprints 2.9 through 2.13. Those historical sprint identifiers are
deferred by D079 and must not be reported as complete.

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

Sprint 3.3.1 is the sole next gate. It may explain only the already persisted
deterministic Recommendation through the existing `ExplanationProvider` port
and a prepared, bounded context.

Required functional boundary:

```text
Persisted deterministic Recommendation
-> Prepared bounded context
-> ExplanationProvider
-> Natural-language explanation
```

The provider must not decide, calculate ROI, create Evidence, modify the
Recommendation, change Decision state, invoke repositories as business
authority or write Ledger history. Provider failure must preserve the complete
deterministic result and must not cause Recommendation rollback.

Sprint 3.3.1 must not implement Review, Ledger, Result Validation, additional
Recommendation families, new REST routes, schema changes or later Phase 3
capabilities.

## Demonstration And Pilot Boundary

Sprint 3.5 proves the business-value loop locally with approved synthetic,
manual or sanitized import evidence. It does not authorize real customer data,
external exposure or a pilot.

Before Sprint 3.9 pilot readiness can pass:

- Java CI must verify the backend;
- JWT/RBAC must protect the relevant routes and actions;
- secrets and database credentials must be externalized;
- the thin workspace must expose the review path safely;
- required acceptance, negative and audit tests must pass.

## Execution Discipline

- one bounded deliverable per iteration;
- one `NEXT` sprint at a time;
- Domain Isolation Index must remain 100%;
- Architectural Stability Index target is at least 95%;
- Decision Stability target is 100%;
- no speculative abstractions, routes, providers or persistence structures;
- every code-bearing sprint ends with Java 21 Maven verification and relevant
  real-runtime tests;
- documentation reports verified state only.

## Phase 3 Exit

Phase 3 is not complete when Sprint 3.5 passes. Full closure requires the
frozen MVP acceptance path, including deterministic value, AI explanation,
human authority, append-only audit history, minimum security, the review
workspace and minimum operational verification.
