# SPRINT 2.7.6.2 — FLYWAY V1 INITIAL SCHEMA

## Role

Act as a Senior Principal PostgreSQL and Flyway Engineer.

Use the five separated IMPERATOR sprint roles:

- Architecture Guardian;
- Implementation Agent;
- Quality Agent;
- Context Keeper;
- CTO / Product Guardian.

Only the Implementation Agent may write the migration. The Context Keeper may write required documentation synchronization. Architecture Guardian and CTO / Product Guardian have veto power.

## Project

IMPERATOR

## Dependency Gate

This micro-sprint may start only when Sprint 2.7.6.1 ends with `STATUS: PASS` or an explicit founder/CTO waiver.

Required starting evidence:

- Java 21 Maven Wrapper build is green;
- Flyway Maven Plugin is installed with an exact version;
- `docs/architecture/40_Persistence_Schema_Contract.md` exists;
- the persistence contract contains no unresolved schema decision.

The frozen persistence contract is the sole design authority for V1. Do not reinterpret the domain or redesign the schema while writing DDL.

## Objective

Create the first complete PostgreSQL schema migration.

Nothing else.

## Single Deliverable

Create exactly:

```text
database/migrations/V1__initial_schema.sql
```

Minimal documentation synchronization is allowed only to record the migration’s real existence and verification result.

## Required Table Boundary

The migration must create exactly these seven product tables:

1. `evidence`
2. `decisions`
3. `decision_evidence`
4. `recommendations`
5. `recommendation_evidence`
6. `ledger_entries`
7. `ledger_evidence_snapshots`

Flyway’s own schema-history table is not a product table and must not be created manually.

## Migration Content

Include only what the frozen contract authorizes:

- PostgreSQL column types;
- primary keys;
- foreign keys;
- unique constraints;
- not-null constraints;
- check constraints;
- explicit `ON DELETE` behavior;
- minimal indexes justified by current FKs and repository queries;
- DDL ordering required to resolve the frozen Decision/Recommendation relationship.

Use the exact:

- table names;
- column names;
- constraint semantics;
- index column order;
- domain closed values;
- nullability;
- metadata type;
- UUID strategy;
- ledger enforcement boundary

defined by `40_Persistence_Schema_Contract.md`.

Do not make a new design choice inside SQL.

If the contract is incomplete, inconsistent or impossible to implement in PostgreSQL, stop and return `FAIL`. Do not silently repair the contract in the migration.

## Flyway Rules

- Use the exact Flyway version installed by Sprint 2.7.6.0.
- Configure the canonical migration location as `database/migrations`.
- Use Flyway versioned-migration naming exactly.
- Do not create a Java migration.
- Do not create a repeatable migration.
- Do not create an undo migration.
- Do not edit Flyway schema history manually.
- Do not use `IF NOT EXISTS` to hide partial or divergent schema state.
- Do not bind migration execution to the normal Maven `verify` lifecycle.
- Do not commit connection configuration or credentials.

V1 must apply cleanly to an empty supported PostgreSQL database.

After successful migration, a second Flyway migrate operation must report no pending migration rather than execute V1 again.

## Flyway Idempotency Gate

Run Flyway twice against the same isolated database.

The second run must produce:

```text
No pending migrations.
No checksum mismatch.
No schema drift.
```

Required interpretation:

- V1 executes exactly once;
- Flyway schema history retains the original successful checksum;
- the second run performs no DDL;
- Flyway validate remains successful;
- the resulting schema still matches `40_Persistence_Schema_Contract.md`.

Do not use `IF NOT EXISTS`, checksum repair or manual schema-history changes to manufacture idempotency.

## MUST NOT Include

- views;
- materialized views;
- triggers;
- stored procedures;
- user-defined functions;
- partitioning;
- row-level security;
- database users or roles;
- grants;
- extensions unless the frozen contract explicitly proves one is required;
- generated UUID defaults unless frozen by contract;
- seed/reference/business data;
- dashboard tables;
- read models;
- query projections;
- search indexes;
- full-text search;
- JSON indexes;
- speculative performance indexes;
- comments containing future DDL or TODO design;
- rollback scripts.

## MUST NOT Create or Modify

- Java production code;
- Domain;
- Application;
- Ports;
- persistence records;
- mappers;
- repositories;
- JDBC binding;
- transaction infrastructure;
- Maven dependencies/plugins except a strictly necessary correction proven by failed verification and explicitly authorized;
- Spring;
- REST;
- security;
- frontend;
- observability;
- Docker or Docker Compose files;
- tests or fixtures.

## Verification Environment

Flyway validation is database-backed; it cannot be truthfully certified offline.

Use only an isolated, disposable PostgreSQL database supplied for verification.

The verification database must:

- contain no business data;
- not be shared;
- not be staging or production;
- be safe to recreate;
- use no committed credentials.

The verification may use an externally supplied PostgreSQL instance or ephemeral test infrastructure, but this sprint must not add Docker/runtime files to the repository.

If no isolated PostgreSQL database is available, the migration may be authored but the sprint remains `FAIL` because V1 has not been executed.

## Required Verification

Run the build regression:

Linux/macOS:

```text
./mvnw clean verify
```

Windows:

```text
mvnw.cmd clean verify
```

Against an empty disposable PostgreSQL database, execute:

1. Flyway migrate.
2. Flyway validate.
3. Flyway info.
4. Flyway migrate a second time.

Required evidence:

- V1 applied once;
- Flyway checksum recorded;
- Flyway validate succeeds;
- Flyway info reports V1 successful/current;
- second migrate reports no pending work;
- second migrate reports no checksum mismatch;
- schema comparison reports no drift from the frozen contract;
- exactly seven product tables exist;
- every expected PK, FK, UNIQUE, NOT NULL and CHECK exists;
- only contract-authorized indexes exist;
- no seed rows exist;
- no production Java file changed;
- credentials are absent from Git diff and command output.

Do not report “Flyway validates” unless the real command ran successfully against PostgreSQL.

## Success Criteria

The sprint passes only when:

- `V1__initial_schema.sql` is the only migration;
- V1 matches the frozen contract exactly;
- V1 applies to an empty PostgreSQL database;
- Flyway validation succeeds;
- a second migrate is a no-op;
- no checksum mismatch exists;
- no schema drift exists;
- the Maven Java 21 build remains green;
- no extra product table or database feature exists;
- Architecture Guardian detects no domain/schema drift;
- Quality Agent finds no missing constraint or unjustified index;
- DII remains 100%;
- Decision Stability remains 0 unless explicitly waived.

## Output

Return:

1. Migration file created.
2. Documentation files modified.
3. Tables created.
4. Constraints created by table.
5. Indexes created and exact justification.
6. Flyway commands actually executed.
7. PostgreSQL version used for verification.
8. Migration, validation and second-run results.
9. Flyway Idempotency Gate result.
10. Schema-contract deviations, which must be zero for PASS.
11. Risks intentionally deferred.
12. Green-gate table.
13. Final Gate.

Mandatory green-gate table:

| Criterion | Status |
|---|---|
| Builds/compiles | Pending |
| Flyway migrate/validate checks pass | Pending |
| Flyway idempotency gate passes | Pending |
| Architecture respected | Pending |
| Schema matches frozen contract | Pending |
| No critical technical debt | Pending |
| No dead SQL or unresolved TODOs | Pending |
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

If any mandatory line fails, Sprint 2.7.7 must not start.
