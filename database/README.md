# PostgreSQL Persistence

This directory contains the implemented Flyway history used by Java JDBC
adapters, the native integration profile and Docker runtime.

| Migration                                     | Responsibility                                                                |
| --------------------------------------------- | ----------------------------------------------------------------------------- |
| `V1__initial_schema.sql`                      | Evidence, Decisions, Recommendations, supporting links and append-only Ledger |
| `V2__unique_decision_case_id.sql`             | One deterministic Decision per canonical case                                 |
| `V3__recommendation_explanation_attempts.sql` | Separate explanation attempts, Evidence links and assumptions                 |

Migration files are forward-only history. Do not edit them to clear test data,
weaken foreign keys or give the application update/delete authority over audit
history. Test-only cleanup includes all related tables and uses an isolated
database with the integration owner identity.

Docker provisions separate application permissions through
`infra/docker/postgresql/provision-app-role.sh`. The integration profile checks
migration, validation, a second no-op migration, persistence and permissions.
Synthetic JSONL datasets live in `src/test/resources/evidence/`. Credentials,
dumps and runtime database files never belong here. Business policy stays in
`backend-java/domain/`.

See the [backend verification commands](../backend-java/README.md),
[Docker runtime](../infra/docker/README.md),
[current structure guide](../docs/project/PROJECT_STRUCTURE.md) and
[TFG evidence](../docs/tfg/04_EVIDENCE_REGISTER.md).

The former repository-shell description belonged to Phase 1 and no longer
describes the implementation. No schema or business behavior changed to
resolve that documentation discrepancy.
