# SPRINT 2.7.6.1 — PERSISTENCE DESIGN CONTRACT

## Role

Act as a Senior Principal Java Persistence Architect.

Use the five separated IMPERATOR sprint roles:

- Architecture Guardian;
- Implementation Agent;
- Quality Agent;
- Context Keeper;
- CTO / Product Guardian.

The Implementation Agent owns the technical mapping. The Context Keeper may write the authorized contract and required documentation synchronization. No role may write executable code in this sprint. Architecture Guardian and CTO / Product Guardian have veto power.

## Project

IMPERATOR

## Dependency Gate

This micro-sprint may start only when Sprint 2.7.6.0 ends with `STATUS: PASS` or an explicit founder/CTO waiver.

Required starting evidence:

- Maven Wrapper works;
- Maven verifies with Java 21;
- Flyway Maven Plugin is installed only; it has not been configured against a database or executed;
- current production sources compile unchanged.

Documents 31–39, accepted Decision Log entries, existing domain objects, persistence records, mappers and repository SQL are authority.

## Current State

The repository already contains:

- `Evidence`;
- `Decision`;
- `Recommendation`;
- `LedgerEntry`;
- corresponding PostgreSQL persistence records;
- corresponding PostgreSQL mappers;
- implemented PostgreSQL repositories;
- repository SQL identifiers and access paths.

Only the physical PostgreSQL schema contract is missing.

## Objective

Freeze the complete relational persistence contract before writing executable SQL.

Nothing else.

After this micro-sprint, Sprint 2.7.6.2 must be able to write `V1__initial_schema.sql` without making a new schema-design decision.

## Frozen Domain Rule

The Domain Model is frozen.

No:

- Entity;
- Value Object;
- Aggregate;
- Domain service;
- inbound Port;
- outbound Port

may be modified during this sprint.

The persistence contract must adapt PostgreSQL to the existing Domain and Ports.

If the persistence analysis reveals an inconsistency that cannot be represented without changing Domain or Port contracts:

1. document the exact inconsistency and affected invariant;
2. do not modify Java;
3. do not weaken the invariant;
4. return `STATUS: FAIL`;
5. request explicit architectural approval before any later change.

Schema convenience is never sufficient justification for a Domain or Port change.

## Single Deliverable

Create exactly:

```text
docs/architecture/40_Persistence_Schema_Contract.md
```

The contract must be implementation-ready, internally consistent and contain no `TBD`, unresolved alternative or optional branch.

Documentation synchronization outside this file is allowed only when required by the existing Context Keeper rules and must not change accepted architectural meaning.

## Required Persistence Boundary

The contract must define exactly these seven tables:

1. `evidence`
2. `decisions`
3. `decision_evidence`
4. `recommendations`
5. `recommendation_evidence`
6. `ledger_entries`
7. `ledger_evidence_snapshots`

Do not introduce:

- users or roles tables;
- organizations or tenants tables;
- ROI tables;
- assumptions tables;
- explanation tables;
- idempotency tables;
- outbox tables;
- read models;
- audit tables;
- framework metadata tables other than Flyway’s own runtime history table.

If an eighth product table appears necessary, stop and return `FAIL` with evidence. Do not invent it.

## Required Mapping

Produce a complete mapping for every persisted field:

```text
Domain Entity / Value Object
↓
Persistence Record
↓
Repository SQL Identifier
↓
Table
↓
Column
↓
PostgreSQL Type
↓
Nullability
↓
Constraint
↓
Index
↓
Enforcement Layer
```

Required domain roots:

- Evidence;
- Decision;
- Recommendation;
- LedgerEntry.

Required relationship records:

- PostgresDecisionEvidenceRecord;
- PostgresRecommendationEvidenceRecord;
- PostgresLedgerEvidenceSnapshotRecord.

Every constructor field in each PostgreSQL persistence record must map to exactly one column or one explicitly documented relationship.

Every column referenced by existing repository SQL must exist in the contract with the exact same identifier.

No persistence-record field may be silently dropped.

## Decisions That MUST Be Frozen

### 1. Tenant Boundary

Resolve:

- single-tenant V1 with no `organization_id`; or
- explicit `organization_id` representation supported by an existing authorized boundary.

Do not add a phantom tenant column that no current domain object, port, mapper or adapter can supply.

If the accepted documents conflict and a new founder/CTO decision is required, stop and return `FAIL`. Do not change the domain to satisfy the schema.

### 2. UUID Strategy

Define:

- PostgreSQL column type;
- whether IDs are application-assigned or database-generated;
- whether any extension or default is required;
- FK type consistency.

The choice must match existing domain IDs and repository bindings.

### 3. Metadata Storage

Choose exactly one:

- `TEXT`; or
- `JSONB`.

The contract must explain compatibility with the existing `setString` / `getString` JDBC behavior and `PostgresFlatMetadataJson`.

If the selected type requires an adapter binding change, identify that exact change as authorized work for Sprint 2.7.7. Do not modify JDBC in this sprint.

### 4. Domain Value Constraints

Map all currently enforced closed values and ranges, including:

- decision status;
- recommendation type;
- ledger entry type;
- severity;
- evidence sensitivity;
- evidence confidence;
- evidence review status;
- raw payload mode;
- ROI confidence range;
- non-negative money;
- currency format;
- paired nullable amount/currency fields.

For each invariant, state whether it is enforced by:

- PostgreSQL row constraint;
- PK/FK/UNIQUE relationship;
- repository behavior;
- domain/application behavior;
- a later security/transaction boundary.

Do not pretend a PostgreSQL `CHECK` can validate rows in another table.

### 5. Primary and Unique Keys

Resolve:

- PK for every table;
- composite PKs for bridge tables;
- one-recommendation-per-decision enforcement;
- duplicate relationship prevention;
- whether `case_id` is unique;
- consistency between `decisions.recommendation_id` and `recommendations.decision_id`;
- previous-ledger-entry identity rules.

### 6. Foreign Keys

Define every FK and its exact target.

Explicitly resolve:

- originating evidence;
- decision/evidence links;
- recommendation/decision link;
- decision/current-recommendation link;
- recommendation/evidence links;
- ledger/decision link;
- optional ledger/recommendation link;
- optional previous ledger entry;
- ledger/evidence snapshot links.

Owner, approver, reviewer and actor UUIDs must not reference a user table that is outside the seven-table boundary.

### 7. Circular Decision/Recommendation Strategy

Resolve the circular relationship without changing the domain:

```text
recommendations.decision_id
decisions.recommendation_id
```

Document:

- creation order;
- nullable side;
- FK creation order;
- how cross-decision recommendation attachment is prevented or which layer owns that invariant;
- compatibility with the existing repository save sequence.

### 8. ON DELETE Rules

Define an explicit `ON DELETE` rule for every FK.

Preserve:

- evidence traceability;
- decision history;
- ledger history;
- append-only intent.

Do not use cascading deletion merely for convenience.

### 9. Ledger Immutability

Freeze the enforcement boundary.

Sprint 2.7.6.2 forbids triggers, functions and stored procedures. Therefore the contract must state precisely what V1 can enforce through tables/constraints and what remains enforced by repository API, application contract or future database-role permissions.

Do not claim physical database immutability if V1 cannot enforce it.

### 10. Minimal Indexes

Define only indexes justified by:

- FK access;
- existing `findById`;
- existing `existsById`;
- existing relationship loading;
- existing ledger lookup by decision ordered by `occurred_at`, then `id`;
- uniqueness enforcement.

For every index provide:

- table;
- ordered columns;
- uniqueness;
- exact existing query/FK justification.

Do not add indexes for hypothetical dashboards, filtering, search, ranking or analytics.

## MUST NOT Create or Modify

- executable SQL;
- migration files;
- `database/migrations/`;
- Java code;
- Domain;
- Application;
- Ports;
- persistence records;
- mappers;
- repositories;
- JDBC;
- Flyway configuration;
- PostgreSQL runtime configuration;
- Spring;
- REST;
- tests;
- Docker;
- seed data.

## Review Method

The contract must be derived in this order:

1. Domain/value-object invariants.
2. Persistence-record fields.
3. Mapper conversions.
4. Repository SQL identifiers and query patterns.
5. Documents 31–39.
6. Minimal PostgreSQL representation.

Do not start from a generic SaaS schema or a preferred PostgreSQL template.

## Verification

Run the previous build as a regression check:

Linux/macOS:

```text
./mvnw clean verify
```

Windows:

```text
mvnw.cmd clean verify
```

Also verify:

- exactly seven product tables are specified;
- every persistence-record field is mapped;
- every repository SQL table/column identifier is covered;
- every existing domain closed value/range is classified;
- every FK has an `ON DELETE` decision;
- every index has an existing-query/FK justification;
- no unresolved decision, `TBD` or contradictory alternative remains;
- no executable SQL exists;
- Git diff is limited to the authorized contract and required documentation synchronization.

## Success Criteria

The sprint passes only when:

- the complete contract exists;
- Domain, Value Objects, Aggregates and Ports remain unchanged;
- Architecture Guardian accepts domain fidelity;
- Quality Agent finds no missing field or contradictory constraint;
- CTO / Product Guardian accepts the seven-table MVP boundary;
- Sprint 2.7.6.2 requires no new schema-design decision;
- Java build regression remains green;
- DII remains 100%;
- Decision Stability remains 0 unless explicitly waived.

## Output

Return:

1. Contract file created.
2. Documentation files modified.
3. Seven-table summary.
4. Frozen decisions and evidence.
5. Domain invariants assigned to enforcement layers.
6. Explicitly rejected tables/constraints/indexes.
7. Commands/checks actually executed.
8. Risks carried into implementation without leaving design open.
9. Green-gate table.
10. Final Gate.

Mandatory green-gate table:

| Criterion | Status |
|---|---|
| Builds/compiles or valid no-code equivalent | Pending |
| Tests/checks pass or valid no-test justification | Pending |
| Architecture respected | Pending |
| Domain Model and Ports unchanged | Pending |
| No critical technical debt | Pending |
| No dead code | Pending |
| No unresolved TODOs or schema decisions | Pending |
| Documentation synchronized | Pending |
| ASI target met | Pending |
| DII target met | Pending |
| Decision Stability target met | Pending |
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
```

Targets:

- ASI: at least 95%;
- DII: 100%;
- Decision Stability: 0 modified prior accepted decisions.

If any mandatory line fails, Sprint 2.7.6.2 must not start.
