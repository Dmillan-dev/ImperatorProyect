# SPRINT 2.7.7 — POSTGRESQL INTEGRATION AND TESTS

## Role

Act as a Senior Principal Java Persistence and Integration Test Engineer.

Use the five separated IMPERATOR sprint roles:

- Architecture Guardian;
- Implementation Agent;
- Quality Agent;
- Context Keeper;
- CTO / Product Guardian.

Only the Implementation Agent may write implementation/test files. The Context Keeper may write required documentation synchronization. Architecture Guardian and CTO / Product Guardian have veto power.

## Project

IMPERATOR

## Dependency Gate

This sprint may start only when Sprint 2.7.6.2 ends with `STATUS: PASS` or an explicit founder/CTO waiver.

Required starting evidence:

- Java 21 Maven Wrapper build is green;
- `V1__initial_schema.sql` exists;
- V1 was applied and validated against an empty disposable PostgreSQL database;
- a second Flyway migrate was a no-op;
- the frozen schema contract and V1 have no known deviation.

V1 is immutable after the 2.7.6.2 PASS gate. Do not edit its checksum in this sprint.

## Objective

Prove that the existing IMPERATOR persistence flow works against a real PostgreSQL database created exclusively from Flyway V1.

No new business logic may be introduced.

## Single Deliverable

A repeatable PostgreSQL integration-test slice that validates the existing repositories, mappers, use cases and transaction contract against real PostgreSQL.

Persistence is not complete until this deliverable passes.

## Scope

Implement only what is required to execute and verify the current persistence boundary:

- test-scoped PostgreSQL lifecycle/configuration;
- DataSource creation/configuration;
- Flyway execution for the isolated test database;
- repository integration;
- mapper round trips through repositories;
- database-constraint verification;
- repository atomicity verification;
- existing use-case transaction-boundary verification;
- one complete persistence flow.

## Allowed Production Change

A minimal PostgreSQL `DataSource` / connection factory may be added inside the PostgreSQL adapter only if:

- it is used by the integration slice;
- it contains no credentials;
- it introduces no connection pool;
- it imports no Spring type;
- it does not move connection configuration into Domain, Application or Ports;
- it is the smallest reusable composition primitive.

Do not add a production main class, server bootstrap or application container.

## Allowed Test Infrastructure

Allowed test-scoped additions:

- JUnit 5;
- Maven Surefire/Failsafe as required by the chosen `*IT` convention;
- Testcontainers JUnit integration;
- Testcontainers PostgreSQL module;
- Flyway Core and its PostgreSQL database module in test scope when required to migrate the dynamically created test database;
- PostgreSQL JDBC Driver in the narrowest correct build scope;
- test fixtures under test sources only;
- a minimal deterministic `ExplanationProvider` test double.

Do not add:

- Mockito or another mocking framework unless a concrete unresolvable need is proven;
- H2;
- SQLite;
- an embedded substitute with different PostgreSQL semantics;
- production seed data;
- a second migration runner in production code.

Pin the PostgreSQL test image/version and every dependency/plugin version.

Testcontainers counts as real PostgreSQL. It must remain test infrastructure and must not introduce Docker Compose or production container files.

If Testcontainers cannot run, an externally supplied isolated PostgreSQL database may be used only when the test suite prevents accidental use of shared/staging/production databases.

## Required Database Lifecycle

Each accepted integration run must:

1. start from an empty isolated PostgreSQL database;
2. run Flyway V1;
3. validate Flyway;
4. run Flyway migrate a second time and verify no pending migration, checksum mismatch or schema drift;
5. run the repositories/use cases;
6. cleanly dispose of test state;
7. require no manually pre-created product table or seed row.

Tests must fail clearly when PostgreSQL is unavailable. Do not silently skip the integration suite and report PASS.

## Required Repository Verification

Verify all existing repository operations:

### EvidenceRepository

- `save`;
- `findById`;
- `existsById`;
- complete field/metadata round trip.

### DecisionRepository

- insert;
- update through existing `save`;
- `findById`;
- `existsById`;
- decision/evidence relationship persistence;
- status/reviewer/reason/timestamp reconstruction.

### RecommendationRepository

- `save`;
- `findById`;
- `existsById`;
- recommendation/evidence relationship persistence;
- money/currency/confidence/risk round trip.

### LedgerRepository

- `append`;
- `findById`;
- `findByDecisionId`;
- evidence snapshot relationship persistence;
- deterministic ordering by `occurred_at`, then `id`;
- append-only repository surface;
- estimated and realized money round trip;
- previous-entry reconstruction.

Do not add repository methods for test convenience.

## Repository Behaviour Gate

Every existing public repository operation must be executed against the PostgreSQL database created from Flyway V1.

Required gate:

| Repository | Operations that must pass | Status |
|---|---|---|
| EvidenceRepository | `save`, `findById`, `existsById` | Pending |
| DecisionRepository | `save`, `findById`, `existsById` | Pending |
| RecommendationRepository | `save`, `findById`, `existsById` | Pending |
| LedgerRepository | `append`, `findById`, `findByDecisionId` | Pending |

For each repository, PASS requires:

- every listed operation executes successfully against real PostgreSQL;
- stored fields and relationships reload into the expected Domain object;
- missing IDs return the existing empty/not-found behavior;
- duplicate/invalid writes fail according to the frozen contract;
- no new method is introduced to make the test easier.

If one operation fails or is not executed, that repository and the complete Repository Behaviour Gate are `FAIL`.

## Required Constraint Verification

Verify representative failures for the contract’s:

- PK;
- FK;
- UNIQUE;
- NOT NULL;
- CHECK;
- recommendation uniqueness;
- duplicate bridge relationships;
- invalid closed values/ranges;
- invalid paired amount/currency values;
- forbidden deletion behavior where applicable.

Test database failures through real PostgreSQL. Do not duplicate the whole schema as assertions in Java.

## Required Full Persistence Flow

Using existing application use cases and PostgreSQL repositories, verify:

```text
Evidence
↓
Decision
↓
Recommendation
↓
Review
↓
Ledger
↓
Reload from PostgreSQL
```

The test must:

1. import Evidence;
2. create Decision from that Evidence;
3. generate and attach Recommendation;
4. move the Decision through an existing review action;
5. append the corresponding LedgerEntry explicitly through the existing ledger use case;
6. reconstruct all persisted objects from PostgreSQL;
7. verify IDs, relationships, status, value objects, timestamps and metadata.

The explanation provider must remain an optional deterministic test double. No real AI/provider call is allowed.

Do not collapse Review and Ledger into one operation; the existing transaction contract keeps them separate.

## Required Transaction Verification

Validate both levels:

### Repository Aggregate Atomicity

Prove rollback for:

- Decision plus decision/evidence links;
- Recommendation plus recommendation/evidence links;
- LedgerEntry plus ledger/evidence snapshot links.

Inject a real database failure after the parent write and prove that no partial aggregate remains.

### Existing Use-Case Atomicity

Validate the documented logical boundary for:

- ImportEvidenceUseCase;
- CreateDecisionUseCase;
- GenerateRecommendationUseCase;
- ReviewDecisionUseCase;
- AppendLedgerEntryUseCase.

For each use case:

- identify every read/write participating in the documented transaction;
- inject a failure at the last possible write;
- verify no partial state remains;
- verify external explanation work is not inside the database transaction;
- verify ReviewDecision does not append Ledger implicitly.

Passing only repository-level atomicity is insufficient.

## Transaction Repair Gate

The current repositories open local transactions independently. Do not assume this satisfies use-case atomicity.

If an integration test proves a cross-repository transaction gap:

1. record the failing test and exact partial state;
2. stop before inventing a transaction framework;
3. determine whether the fix can remain entirely within the existing authorized PostgreSQL/composition boundary;
4. request explicit founder/CTO authorization before modifying Application, Ports or an accepted transaction decision.

Forbidden unapproved fixes:

- Spring `@Transactional`;
- JTA;
- hidden global connection state;
- undocumented `ThreadLocal` transaction context;
- reflection/proxy magic;
- domain changes;
- broad Unit of Work abstraction;
- swallowing/compensating for partial writes as if atomic.

If full use-case atomicity cannot be proven without an unauthorized architectural change, the correct result is:

```text
STATUS: FAIL
```

Do not downgrade the requirement or declare persistence complete.

## Defect Repair Policy

A production adapter/JDBC change is allowed only when:

- a failing real-PostgreSQL integration test proves the defect;
- the change is the smallest correction;
- Domain, Application and Ports remain unchanged;
- the frozen persistence contract remains unchanged;
- no new public repository method is added;
- the test passes afterward;
- Architecture Guardian approves it.

Examples may include:

- PostgreSQL type binding correction;
- nullable JDBC value handling;
- SQL identifier mismatch;
- mapper/repository round-trip defect;
- connection-resource handling defect.

If the defect is in V1 or the frozen contract, do not edit them silently. Return `FAIL` and identify which previous gate must be reopened by founder/CTO decision.

## MUST NOT Create

- REST;
- Spring or Spring Boot;
- controllers;
- JWT/security;
- frontend;
- observability platform;
- live connectors;
- real AI integration;
- new domain behavior;
- new use cases;
- new repository query capabilities;
- dashboards/read models;
- additional product migrations;
- Docker Compose or unrelated Docker files;
- production business data;
- performance optimization unrelated to a failing acceptance check.

## Verification Commands

The final gate must execute with Java 21:

Linux/macOS:

```text
./mvnw clean verify
```

Windows:

```text
mvnw.cmd clean verify
```

The command must:

- compile production and test sources;
- start/connect to isolated real PostgreSQL;
- migrate it from empty using V1;
- run all integration tests;
- fail if PostgreSQL tests are skipped or unavailable;
- leave no committed credentials or generated database artifacts.

Also verify:

- Flyway validate passes;
- second Flyway migrate reports no pending migration, checksum mismatch or schema drift;
- V1 checksum is unchanged;
- all four repositories are exercised;
- all five existing use cases are exercised;
- full persistence flow passes;
- repository atomicity passes;
- use-case atomicity passes;
- DII remains 100%;
- Git diff contains only authorized persistence-integration/test work.

## Success Criteria

The sprint passes only when:

- real PostgreSQL is used;
- schema is created solely by Flyway V1;
- every repository operation passes;
- mapper round trips pass through PostgreSQL;
- representative database constraints fail correctly;
- the complete Evidence → Decision → Recommendation → Review → Ledger flow passes;
- all existing use-case atomicity boundaries are proven;
- every row in the Repository Behaviour Gate is `PASS`;
- MVP Executability Index is 100%;
- no test is skipped;
- no unauthorized architecture change exists;
- Java 21 `clean verify` passes;
- Architecture Guardian, Quality Agent and Product Guardian all pass.

Compilation alone is not acceptance.

## MVP Executability Index

Report one final binary executability index for this backend persistence milestone.

| Component | PASS definition | Status |
|---|---|---|
| Maven | Maven Wrapper `clean verify` succeeds | Pending |
| Java 21 | Maven build actually runs with JDK 21 | Pending |
| Flyway | V1 migrate/validate and second-run idempotency pass with no checksum mismatch or drift | Pending |
| PostgreSQL | Tests run against an isolated real PostgreSQL instance | Pending |
| Repositories | All four Repository Behaviour Gate rows pass | Pending |
| Transactions | Repository and all existing use-case atomicity checks pass | Pending |
| Integration Tests | Complete persistence flow and all required integration tests pass with zero skipped | Pending |

Calculation:

```text
MVP Executability Index =
    PASS components / 7 * 100
```

Rules:

- each component is binary: PASS or FAIL;
- no partial credit;
- `N/A` is not allowed;
- rounded or estimated scores are forbidden;
- only 7/7 PASS equals 100%;
- only 100% authorizes declaring the backend persistence milestone executable.

This index certifies backend persistence executability. It does not claim that REST, security, frontend, observability or the complete product MVP exists.

## Deliverables

- DataSource/connection configuration required by the tested boundary;
- test-scoped Flyway/PostgreSQL lifecycle;
- repository integration tests;
- full persistence-flow integration test;
- transaction/rollback integration tests;
- minimal proven adapter fixes, if authorized;
- factual documentation synchronization;
- final persistence gate report.

## Output

Return:

1. Files created.
2. Files modified.
3. Dependencies/plugins added with scope and exact versions.
4. PostgreSQL version used.
5. Flyway migrate/validate result.
6. Repository operations verified.
7. Full-flow result.
8. Constraint tests.
9. Repository atomicity results.
10. Use-case atomicity results.
11. Production defects found and minimal fixes.
12. Tests executed/skipped/failed counts.
13. Remaining blockers.
14. Repository Behaviour Gate.
15. MVP Executability Index table and exact score.
16. Green-gate table.
17. Final Gate.

Mandatory green-gate table:

| Criterion | Status |
|---|---|
| Java 21 build/compile passes | Pending |
| Real PostgreSQL integration tests pass | Pending |
| Flyway V1 migrate/validate passes | Pending |
| Repository round trips pass | Pending |
| Repository Behaviour Gate passes | Pending |
| Full persistence flow passes | Pending |
| Repository atomicity passes | Pending |
| Existing use-case atomicity passes | Pending |
| Architecture respected | Pending |
| No critical technical debt | Pending |
| No dead code or unresolved TODOs | Pending |
| Documentation synchronized | Pending |
| ASI target met | Pending |
| DII target met | Pending |
| Decision Stability target met | Pending |
| MVP Executability Index = 100% | Pending |
| Sprint duration within one week | Pending |

Mandatory final block:

```text
STATUS: PASS / FAIL

Architecture Guardian: PASS / FAIL
Quality Agent: PASS / FAIL
Context Keeper: PASS / FAIL
Product Guardian: PASS / FAIL
Implementation Agent: PASS / FAIL
ASI: <score>%
DII: <score>%
Decision Stability: <number of prior accepted decisions modified>
MVP Executability Index: <score>%
```

Targets:

- ASI: at least 95%;
- DII: 100%;
- Decision Stability: 0 modified prior accepted decisions.
- MVP Executability Index: 100%.

Only `STATUS: PASS` authorizes declaring the PostgreSQL persistence foundation complete and starting Sprint 2.8.
