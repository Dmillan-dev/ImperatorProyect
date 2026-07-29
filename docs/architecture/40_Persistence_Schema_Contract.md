# 40 - Persistence Schema Contract

Sprint: 2.7.6.1 - Persistence Design Contract

Status: Frozen contract for Sprint 2.7.6.2.

## Purpose

This document is the sole architectural deliverable of Sprint 2.7.6.1. It
freezes the relational persistence model required by the existing domain,
application, ports, PostgreSQL persistence records, mappers and repositories.

Sprint 2.7.6.2 must translate this contract into
`database/migrations/V1__initial_schema.sql` without making further
architectural decisions.

## Authority and Frozen Boundary

The Domain Model and all implementation completed through Sprint 2.7.6.0 are
frozen. This contract does not authorize changes to:

- entities, value objects or aggregates;
- inbound or outbound ports;
- application use cases or commands;
- repository contracts or repository implementations;
- PostgreSQL persistence records or mappers;
- the Maven build foundation.

The schema adapts to the existing model. The existing model does not adapt to
the schema.

Two clarifications were explicitly approved for this contract:

- cardinality is `Decision 0..1 Recommendation` and
  `Recommendation 1..1 Decision`;
- ledger immutability is a domain and repository contract reinforced through
  PostgreSQL application-role permissions in Sprint 2.7.7, not through
  triggers in V1.

Contract validation also confirmed that `DEFERRED` is intentionally editable:
`Decision.addEvidence` and `Decision.attachRecommendation` both accept that
state. A separately authorized correction limited to `PostgresDecisionMapper`
preserves `reviewedAt` and `updatedAt` independently during rehydration. It
changed no domain, persistence record or schema decision.

## 1. Persistence Strategy

### 1.1 Tenancy decision

V1 is single-tenant.

- One IMPERATOR deployment and database serve one logical organization.
- `organization_id` does not exist in any V1 table.
- No tenant discriminator, tenant default or placeholder tenant column is
  permitted.
- `case_id` and `correlation_key` must not be repurposed as tenant identifiers.

### 1.2 Future multi-tenant migration strategy

If multi-tenancy is approved later, it will use a shared-schema,
row-isolation model and an expand-migrate-contract sequence:

1. Approve the tenancy change at domain and architecture level, including a
   canonical organization identity.
2. Add an organization registry and nullable `organization_id` columns through
   a new versioned migration; V1 must never be edited.
3. Backfill every existing row to one explicit bootstrap organization.
4. Deploy tenant-aware code that writes and reads the organization identity.
5. Replace global relationship constraints with tenant-scoped composite
   relationships, add only tenant-required indexes and make every
   `organization_id` non-null.
6. Remove any temporary backfill default so tenant identity is always supplied
   explicitly.

That future change is outside V1 and requires explicit approval because it
changes aggregate identity and isolation semantics.

## 2. Table Inventory

The application schema contains exactly seven application-owned tables:

| Table | Ownership | Purpose |
|---|---|---|
| `evidence` | Evidence aggregate | Canonical evidence facts |
| `decisions` | Decision aggregate | Decision state and optional recommendation link |
| `decision_evidence` | Decision aggregate | Decision evidence membership |
| `recommendations` | Recommendation aggregate | One deterministic recommendation for a decision |
| `recommendation_evidence` | Recommendation aggregate | Recommendation evidence membership |
| `ledger_entries` | LedgerEntry aggregate | Append-only decision history |
| `ledger_evidence_snapshots` | LedgerEntry aggregate | Evidence references captured by a ledger entry |

No other application table is authorized in V1. In particular, V1 contains no
organization, user, role, dashboard, projection, read-model, outbox or audit
table.

Flyway may maintain its own technical schema-history table. That table is
tool-owned metadata, is not an IMPERATOR application table and must not be
authored inside V1.

## 3. Domain to Table Mapping

| Aggregate | Persistence record | Primary table | Primary key | Relationships |
|---|---|---|---|---|
| `Evidence` | `PostgresEvidenceRecord` | `evidence` | `evidence.id` | Referenced by decision, recommendation and ledger evidence relations |
| `Decision` | `PostgresDecisionRecord` | `decisions` | `decisions.id` | Originating Evidence; evidence membership through `PostgresDecisionEvidenceRecord` and `decision_evidence`; optional Recommendation; LedgerEntry history |
| `Recommendation` | `PostgresRecommendationRecord` | `recommendations` | `recommendations.id` | Exactly one owning Decision; evidence membership through `PostgresRecommendationEvidenceRecord` and `recommendation_evidence` |
| `LedgerEntry` | `PostgresLedgerEntryRecord` | `ledger_entries` | `ledger_entries.id` | Exactly one Decision; optional Recommendation; optional previous LedgerEntry in the same Decision; evidence snapshots through `PostgresLedgerEvidenceSnapshotRecord` and `ledger_evidence_snapshots` |

Bridge persistence mapping is fixed as follows:

| Persistence record | Table | Primary key |
|---|---|---|
| `PostgresDecisionEvidenceRecord` | `decision_evidence` | (`decision_id`, `evidence_id`) |
| `PostgresRecommendationEvidenceRecord` | `recommendation_evidence` | (`recommendation_id`, `evidence_id`) |
| `PostgresLedgerEvidenceSnapshotRecord` | `ledger_evidence_snapshots` | (`ledger_entry_id`, `evidence_id`) |

## 4. Column Matrix

### 4.1 Type and constraint conventions

- Java `UUID` maps to PostgreSQL `UUID`.
- Java `Instant` maps to PostgreSQL `TIMESTAMPTZ`.
- Unbounded domain strings map to `TEXT`; the schema does not invent length
  limits absent from the domain.
- Confidence percentages map to `INTEGER`.
- Monetary amounts map to unconstrained-precision `NUMERIC` plus non-negative
  and two-decimal checks. A fixed precision would introduce a maximum value not
  present in the domain.
- All identifiers and timestamps are supplied by the application. V1 defines
  no database-generated values.
- No V1 column has a database default. Every non-null value is explicit, and
  every optional value is explicitly nullable.
- `PK` and `UNIQUE` entries include the indexes PostgreSQL creates to enforce
  those constraints.
- “Nonblank” means the value must contain at least one non-whitespace character
  after trimming.
- External identity UUIDs are stored as values without a foreign key because
  the authorized table inventory contains no identity table.

### 4.2 `evidence`

| Column | SQL type | Nullable | Default | Unique | Check constraints | FK | Index |
|---|---|---:|---|---|---|---|---|
| `id` | `UUID` | No | None | PK | None | None | `pk_evidence` |
| `timestamp` | `TIMESTAMPTZ` | No | None | No | None | None | None |
| `source` | `TEXT` | No | None | No | Nonblank | None | None |
| `source_type` | `TEXT` | No | None | No | Nonblank | None | None |
| `source_object_ref` | `TEXT` | No | None | No | Nonblank | None | None |
| `entity` | `TEXT` | No | None | No | Nonblank | None | None |
| `event_type` | `TEXT` | No | None | No | Nonblank | None | None |
| `severity` | `TEXT` | No | None | No | One of `INFO`, `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` | None | None |
| `actor` | `TEXT` | No | None | No | Nonblank | None | None |
| `evidence_type` | `TEXT` | No | None | No | Nonblank | None | None |
| `observed_fact` | `TEXT` | No | None | No | Nonblank | None | None |
| `business_meaning` | `TEXT` | No | None | No | Nonblank | None | None |
| `correlation_key` | `TEXT` | No | None | No | Nonblank | None | None |
| `sensitivity` | `TEXT` | No | None | No | One of `PUBLIC`, `INTERNAL`, `CONFIDENTIAL`, `RESTRICTED` | None | None |
| `confidence` | `TEXT` | No | None | No | One of `LOW`, `MEDIUM`, `HIGH` | None | None |
| `review_status` | `TEXT` | No | None | No | One of `ACCEPTED`, `REJECTED`, `MISSING`, `STALE`, `DISPUTED`, `NEEDS_REVIEW` | None | None |
| `raw_payload_mode` | `TEXT` | No | None | No | Exactly `not_stored` | None | None |
| `metadata` | `TEXT` | No | None | No | Flat JSON object with string keys and string values | None | None |

The `actor` value `unknown` is produced by the domain when the inbound actor is
absent or blank. The database does not invent that default.

### 4.3 `decisions`

| Column | SQL type | Nullable | Default | Unique | Check constraints | FK | Index |
|---|---|---:|---|---|---|---|---|
| `id` | `UUID` | No | None | PK | None | None | `pk_decisions` |
| `case_id` | `TEXT` | No | None | No | Nonblank | None | None |
| `title` | `TEXT` | No | None | No | Nonblank | None | None |
| `business_need` | `TEXT` | No | None | No | Nonblank | None | None |
| `originating_evidence_id` | `UUID` | No | None | No | None | `evidence.id` | None |
| `owner_id` | `UUID` | No | None | No | None | External identity reference | None |
| `required_approver_id` | `UUID` | No | None | No | None | External identity reference | None |
| `created_at` | `TIMESTAMPTZ` | No | None | No | None | None | None |
| `status` | `TEXT` | No | None | No | One of `CREATED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED`, `DEFERRED` | None | None |
| `recommendation_id` | `UUID` | Yes | None | No | State-dependent presence | Composite ownership FK | None |
| `reviewed_by` | `UUID` | Yes | None | No | Review tuple consistency | External identity reference | None |
| `reviewed_at` | `TIMESTAMPTZ` | Yes | None | No | Review tuple and chronology consistency | None | None |
| `review_reason` | `TEXT` | Yes | None | No | Nonblank when present; review tuple consistency | None | None |
| `updated_at` | `TIMESTAMPTZ` | No | None | No | Not before `created_at`; closed-state consistency | None | None |

Decision row checks are fixed:

- `CREATED` and `UNDER_REVIEW` require `reviewed_by`, `reviewed_at` and
  `review_reason` all to be absent.
- `APPROVED`, `REJECTED` and `DEFERRED` require `reviewed_by`, `reviewed_at`
  and `review_reason` all to be present.
- `UNDER_REVIEW`, `APPROVED` and `REJECTED` require `recommendation_id`.
- `CREATED` and `DEFERRED` may have no recommendation, matching the frozen
  aggregate lifecycle.
- `reviewed_at`, when present, must not precede `created_at`.
- `updated_at` must not precede `created_at`.
- For `APPROVED` and `REJECTED`, `updated_at` must equal `reviewed_at`.
- For `DEFERRED`, `updated_at` and `reviewed_at` are independent after both
  satisfy their respective creation-time lower bound. A deferred Decision may
  receive evidence or a recommendation later, and those edits change only
  `updated_at`.

The originating evidence must also be present in `decision_evidence`. That is a
cross-table aggregate invariant produced by `PostgresDecisionMapper` and
committed atomically by `DecisionRepository.save`; it is validated by the
integration gate in Sprint 2.7.7 rather than by a trigger.

### 4.4 `decision_evidence`

| Column | SQL type | Nullable | Default | Unique | Check constraints | FK | Index |
|---|---|---:|---|---|---|---|---|
| `decision_id` | `UUID` | No | None | Composite PK | None | `decisions.id` | Leading column of `pk_decision_evidence` |
| `evidence_id` | `UUID` | No | None | Composite PK | None | `evidence.id` | Covered by pair uniqueness; no standalone index |

The primary-key order is (`decision_id`, `evidence_id`). It supports both
replacement and loading of a Decision aggregate's evidence membership.

### 4.5 `recommendations`

| Column | SQL type | Nullable | Default | Unique | Check constraints | FK | Index |
|---|---|---:|---|---|---|---|---|
| `id` | `UUID` | No | None | PK; member of ownership key | None | None | `pk_recommendations`; `uq_recommendations_id_decision_id` |
| `decision_id` | `UUID` | No | None | `uq_recommendations_decision_id`; member of ownership key | None | `decisions.id` | Constraint-backed unique indexes |
| `type` | `TEXT` | No | None | No | One of `MODEL_DOWNGRADE`, `MODEL_CHANGE`, `RIGHTSIZE_INSTANCE`, `REMOVE_UNUSED_RESOURCE`, `OPTIMIZE_PIPELINE` | None | None |
| `suggested_action` | `TEXT` | No | None | No | Nonblank | None | None |
| `reason` | `TEXT` | No | None | No | Nonblank | None | None |
| `estimated_saving_amount` | `NUMERIC` | No | None | No | Non-negative; at most two fractional digits | None | None |
| `estimated_saving_currency` | `TEXT` | No | None | No | Exactly three uppercase ASCII letters | None | None |
| `confidence_percentage` | `INTEGER` | No | None | No | From 0 through 100 inclusive | None | None |
| `risk` | `TEXT` | No | None | No | One of `INFO`, `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` | None | None |
| `owner_id` | `UUID` | No | None | No | None | External identity reference | None |
| `required_approver_id` | `UUID` | No | None | No | None | External identity reference | None |
| `created_at` | `TIMESTAMPTZ` | No | None | No | None | None | None |

The unique ownership key (`id`, `decision_id`) exists solely as the referenced
key for composite ownership foreign keys from `decisions` and
`ledger_entries`. It is a relational-integrity constraint, not a query
optimization.

The recommendation owner and required approver are immutable snapshots copied
from the Decision by the current use case. Their equality to Decision values is
an application construction invariant; no user table or cross-row trigger is
introduced.

### 4.6 `recommendation_evidence`

| Column | SQL type | Nullable | Default | Unique | Check constraints | FK | Index |
|---|---|---:|---|---|---|---|---|
| `recommendation_id` | `UUID` | No | None | Composite PK | None | `recommendations.id` | Leading column of `pk_recommendation_evidence` |
| `evidence_id` | `UUID` | No | None | Composite PK | None | `evidence.id` | Covered by pair uniqueness; no standalone index |

The primary-key order is (`recommendation_id`, `evidence_id`). It supports
loading a Recommendation aggregate's evidence membership.

A Recommendation must contain at least one evidence link. Standard row checks
cannot assert child-row cardinality, so the frozen domain constructor and the
atomic `RecommendationRepository.save` operation enforce this invariant.
Sprint 2.7.7 must validate it against PostgreSQL.

### 4.7 `ledger_entries`

| Column | SQL type | Nullable | Default | Unique | Check constraints | FK | Index |
|---|---|---:|---|---|---|---|---|
| `id` | `UUID` | No | None | PK; member of same-Decision key | None | None | `pk_ledger_entries`; `uq_ledger_entries_id_decision_id` |
| `decision_id` | `UUID` | No | None | Member of same-Decision key | None | `decisions.id` | `idx_ledger_entries_decision_timeline` |
| `recommendation_id` | `UUID` | Yes | None | No | Entry-type-dependent presence | Composite ownership FK | None |
| `actor_id` | `UUID` | No | None | No | None | External identity reference | None |
| `actor_role` | `TEXT` | No | None | No | Nonblank | None | None |
| `occurred_at` | `TIMESTAMPTZ` | No | None | No | None | None | `idx_ledger_entries_decision_timeline` |
| `entry_type` | `TEXT` | No | None | No | One of `recommendation_created`, `approved`, `rejected`, `deferred`, `implementation_marked`, `result_validated`, `evidence_requested`, `case_closed` | None | None |
| `change_summary` | `TEXT` | No | None | No | Nonblank | None | None |
| `reason` | `TEXT` | No | None | No | Nonblank | None | None |
| `estimated_saving_amount` | `NUMERIC` | Yes | None | No | Non-negative; at most two fractional digits; monetary-pair consistency | None | None |
| `estimated_saving_currency` | `TEXT` | Yes | None | No | Three uppercase ASCII letters when present; monetary-pair consistency | None | None |
| `realized_saving_amount` | `NUMERIC` | Yes | None | No | Non-negative; at most two fractional digits; monetary-pair consistency | None | None |
| `realized_saving_currency` | `TEXT` | Yes | None | No | Three uppercase ASCII letters when present; monetary-pair consistency | None | None |
| `confidence_percentage` | `INTEGER` | Yes | None | No | From 0 through 100 inclusive when present | None | None |
| `risk` | `TEXT` | Yes | None | No | One of `INFO`, `LOW`, `MEDIUM`, `HIGH`, `CRITICAL` when present | None | None |
| `previous_entry_id` | `UUID` | Yes | None | No | Must differ from `id` | Composite same-Decision FK | None |
| `metadata` | `TEXT` | No | None | No | Flat JSON object with string keys and string values; at most 10 entries | None | None |

Ledger row checks are fixed:

- Amount and currency for each monetary value must be either both present or
  both absent.
- `recommendation_id` is mandatory for `recommendation_created`, `approved`,
  `rejected`, `deferred`, `implementation_marked` and `result_validated`.
- `estimated_saving_amount`, `estimated_saving_currency`,
  `confidence_percentage` and `risk` are mandatory for
  `recommendation_created`, `approved` and `deferred`.
- `realized_saving_amount` and `realized_saving_currency` are mandatory for
  `result_validated`.
- Optional snapshots remain allowed for other entry types because the domain
  does not prohibit them.
- `previous_entry_id`, when present, must identify a different ledger entry
  belonging to the same Decision.

The domain additionally limits ledger metadata keys to 80 characters, values
to 500 characters and forbids secret-bearing key tokens. Those content rules
remain in the frozen domain. V1 validates the flat string-map shape and entry
count without introducing a custom database function.

Evidence snapshots are mandatory for `recommendation_created`, `approved`,
`rejected` and `deferred`. This child-row cardinality is enforced by
`LedgerEntry`, the atomic `LedgerRepository.append` operation and Sprint 2.7.7
integration tests because a row check cannot inspect the bridge table.

### 4.8 `ledger_evidence_snapshots`

| Column | SQL type | Nullable | Default | Unique | Check constraints | FK | Index |
|---|---|---:|---|---|---|---|---|
| `ledger_entry_id` | `UUID` | No | None | Composite PK | None | `ledger_entries.id` | Leading column of `pk_ledger_evidence_snapshots` |
| `evidence_id` | `UUID` | No | None | Composite PK | None | `evidence.id` | Covered by pair uniqueness; no standalone index |

The primary-key order is (`ledger_entry_id`, `evidence_id`). It supports loading
and deterministic ordering of one LedgerEntry's evidence snapshot references.

## 5. UUID Strategy

The identifier strategy is frozen:

- Version: UUID version 4 for all newly generated IMPERATOR aggregate
  identifiers.
- Storage: native PostgreSQL `UUID`.
- Generation: the initiating application boundary creates the identifier
  before constructing the command and domain object.
- Database behavior: no UUID default, extension or database-side generation.
- Repository behavior: preserve the supplied UUID unchanged in both
  directions.

The current value objects accept `java.util.UUID` without version validation.
V1 therefore does not add a UUID-version check that would create a stricter
database invariant than the frozen domain. Version 4 is the producer contract
for new aggregate identifiers. External identity UUIDs are opaque values
supplied by the future identity boundary and are never regenerated by
persistence.

## 6. Metadata Strategy

Metadata storage is frozen as `TEXT`.

Rationale:

- `PostgresEvidenceRepository` and `PostgresLedgerRepository` currently bind
  metadata with JDBC string binding and read it as a string.
- `PostgresFlatMetadataJson` owns deterministic translation between
  `Map<String, String>` and a flat JSON object.
- `JSONB` would require a PostgreSQL-specific JDBC binding or explicit cast,
  changing frozen adapter implementation during a design-only sprint.
- Current repositories perform no metadata filtering, containment lookup or
  indexing, so `JSONB` provides no required repository capability.

The stored text must represent one JSON object whose keys and values are
strings. `NULL`, arrays, scalars, nested objects and non-string values are
invalid. Empty metadata is represented as `{}`. No metadata index is permitted
in V1.

This decision intentionally prioritizes exact compatibility with the current
adapter. A future change to `JSONB` requires a new migration and adapter
contract; V1 must not be rewritten.

## 7. Foreign Keys

All foreign keys are `NOT DEFERRABLE`, use `ON DELETE RESTRICT` and
`ON UPDATE RESTRICT`, and have no cascading action. Nullable composite
relationships use simple matching: when the optional identifier is absent, the
relationship is absent.

| Constraint | Source | Target | Meaning |
|---|---|---|---|
| `fk_decisions_originating_evidence` | `decisions.originating_evidence_id` | `evidence.id` | Every Decision has one existing originating Evidence |
| `fk_decisions_recommendation_ownership` | `decisions.(recommendation_id, id)` | `recommendations.(id, decision_id)` | An attached Recommendation must belong to that Decision |
| `fk_decision_evidence_decision` | `decision_evidence.decision_id` | `decisions.id` | Membership belongs to an existing Decision |
| `fk_decision_evidence_evidence` | `decision_evidence.evidence_id` | `evidence.id` | Membership references existing Evidence |
| `fk_recommendations_decision` | `recommendations.decision_id` | `decisions.id` | Every Recommendation belongs to exactly one existing Decision |
| `fk_recommendation_evidence_recommendation` | `recommendation_evidence.recommendation_id` | `recommendations.id` | Membership belongs to an existing Recommendation |
| `fk_recommendation_evidence_evidence` | `recommendation_evidence.evidence_id` | `evidence.id` | Membership references existing Evidence |
| `fk_ledger_entries_decision` | `ledger_entries.decision_id` | `decisions.id` | Every LedgerEntry belongs to one existing Decision |
| `fk_ledger_entries_recommendation_ownership` | `ledger_entries.(recommendation_id, decision_id)` | `recommendations.(id, decision_id)` | Optional Recommendation belongs to the same Decision |
| `fk_ledger_entries_previous_same_decision` | `ledger_entries.(previous_entry_id, decision_id)` | `ledger_entries.(id, decision_id)` | Optional previous entry belongs to the same Decision |
| `fk_ledger_snapshots_entry` | `ledger_evidence_snapshots.ledger_entry_id` | `ledger_entries.id` | Snapshot belongs to an existing LedgerEntry |
| `fk_ledger_snapshots_evidence` | `ledger_evidence_snapshots.evidence_id` | `evidence.id` | Snapshot references existing Evidence |

The circular Decision-to-Recommendation relationship is intentional and
compatible with the frozen lifecycle:

1. a Decision is first persisted without a recommendation;
2. a Recommendation is persisted with the existing Decision as owner;
3. the same logical use-case transaction updates the Decision with the
   Recommendation identifier.

Sprint 2.7.6.2 must declare base tables before applying the circular foreign-key
constraint. This is migration ordering, not a new schema decision.

`owner_id`, `required_approver_id`, `reviewed_by` and `actor_id` do not have
foreign keys in V1 because no identity table is authorized.

## 8. Recommendation Ownership

Recommendation ownership is frozen as:

- `Decision` has zero or one Recommendation.
- `Recommendation` has exactly one Decision.
- `recommendations.decision_id` is non-null and references `decisions.id`.
- `uq_recommendations_decision_id` permits at most one Recommendation row for
  each Decision.
- `decisions.recommendation_id` is nullable to support the interval between
  `CreateDecisionUseCase` and `GenerateRecommendationUseCase`.
- The composite ownership foreign key prevents a Decision from linking a
  Recommendation owned by another Decision.
- The equivalent composite foreign key on `ledger_entries` prevents ledger
  history from naming a Recommendation owned by another Decision.

The existence of a Recommendation row and attachment of its identifier to the
Decision are one logical atomic operation under Document 39. Their
all-or-nothing behavior is a required Sprint 2.7.7 integration test. No trigger
is authorized.

## 9. Ledger Immutability

`ledger_entries` and `ledger_evidence_snapshots` are append-only.

The persistence contract permits:

- inserting one new LedgerEntry;
- inserting its evidence snapshot links in the same transaction;
- reading an entry by identifier;
- reading a Decision's ordered ledger history.

The persistence contract forbids:

- updating a persisted ledger row;
- deleting a persisted ledger row;
- updating or deleting a persisted ledger evidence snapshot link;
- rewriting an earlier entry as a correction.

Corrections are represented by a new LedgerEntry, optionally linked through
`previous_entry_id`.

V1 does not use triggers, rules or stored procedures. Sprint 2.7.7 must use a
separate application database principal whose privileges on both ledger tables
are limited to `SELECT` and `INSERT`. The migration owner remains separate.
Integration tests must prove that application-role `UPDATE` and `DELETE`
attempts are rejected.

## 10. Index Policy

V1 indexes only primary keys, uniqueness/ownership constraints and access paths
used by current repository methods.

### 10.1 Constraint-backed indexes

| Index or constraint | Columns | Required capability |
|---|---|---|
| `pk_evidence` | `evidence.id` | Evidence `findById`, `existsById` |
| `pk_decisions` | `decisions.id` | Decision `save`, `findById`, `existsById` |
| `pk_decision_evidence` | `decision_evidence.(decision_id, evidence_id)` | Replace and load Decision evidence membership |
| `pk_recommendations` | `recommendations.id` | Recommendation `save`, `findById`, `existsById` |
| `uq_recommendations_decision_id` | `recommendations.decision_id` | At most one Recommendation per Decision |
| `uq_recommendations_id_decision_id` | `recommendations.(id, decision_id)` | Composite ownership foreign keys |
| `pk_recommendation_evidence` | `recommendation_evidence.(recommendation_id, evidence_id)` | Load Recommendation evidence membership |
| `pk_ledger_entries` | `ledger_entries.id` | Ledger `append`, `findById` |
| `uq_ledger_entries_id_decision_id` | `ledger_entries.(id, decision_id)` | Same-Decision previous-entry foreign key |
| `pk_ledger_evidence_snapshots` | `ledger_evidence_snapshots.(ledger_entry_id, evidence_id)` | Load one entry's evidence snapshots |

### 10.2 Explicit repository index

| Index | Columns and order | Required capability |
|---|---|---|
| `idx_ledger_entries_decision_timeline` | `ledger_entries.(decision_id ASC, occurred_at ASC, id ASC)` | Ledger `findByDecisionId` filtering and deterministic order |

No standalone indexes are created for:

- timestamps outside the ledger timeline;
- evidence correlation, source, type, severity, sensitivity or review status;
- Decision status, case, owner or approver;
- Recommendation type, risk, confidence or monetary values;
- metadata;
- foreign-key columns that current repositories never filter by;
- bridge-table second columns.

Those omissions are deliberate. Dashboard, search, filtering, deletion
optimization and analytical access paths belong to future query contracts, not
this write-model schema.

## 11. Repository Compatibility

### 11.1 Method gate

| Repository | Method | Relational support | Status |
|---|---|---|---|
| `EvidenceRepository` | `save` | One complete `evidence` row; all adapter-bound columns exist with compatible types | PASS |
| `EvidenceRepository` | `findById` | `evidence.id` primary key | PASS |
| `EvidenceRepository` | `existsById` | `evidence.id` primary key | PASS |
| `DecisionRepository` | `save` | `decisions` upsert plus atomic replacement of `decision_evidence` membership | PASS |
| `DecisionRepository` | `findById` | `decisions.id` primary key plus bridge primary-key prefix | PASS |
| `DecisionRepository` | `existsById` | `decisions.id` primary key | PASS |
| `RecommendationRepository` | `save` | One immutable `recommendations` row plus atomic `recommendation_evidence` membership | PASS |
| `RecommendationRepository` | `findById` | `recommendations.id` primary key plus bridge primary-key prefix | PASS |
| `RecommendationRepository` | `existsById` | `recommendations.id` primary key | PASS |
| `LedgerRepository` | `append` | One new `ledger_entries` row plus atomic `ledger_evidence_snapshots` rows | PASS |
| `LedgerRepository` | `findById` | `ledger_entries.id` primary key plus snapshot primary-key prefix | PASS |
| `LedgerRepository` | `findByDecisionId` | `idx_ledger_entries_decision_timeline` | PASS |

### 11.2 Aggregate and transaction compatibility

- `CreateDecisionUseCase` can persist a Decision with a null
  `recommendation_id`.
- `GenerateRecommendationUseCase` can create the Recommendation and attach it
  to the Decision in the order required by the foreign keys.
- `ReviewDecisionUseCase` can update only the Decision row and preserve its
  evidence membership.
- `AppendLedgerEntryUseCase` can append one row and its snapshots without
  mutating earlier ledger history.
- Bridge primary keys eliminate duplicate set members exactly as required by
  the domain collections.
- No repository requires a search, dashboard, aggregate list, ranking or
  projection index.

Repository Minimalism and the CQRS write-model boundary are preserved.

## 12. Domain Translation Contract

The write direction is fixed:

`Domain -> PostgreSQL Mapper -> PostgreSQL Persistence Record -> Database`

The read direction is fixed:

`Database -> PostgreSQL Persistence Record -> PostgreSQL Mapper -> Domain`

Rules:

- Domain objects never bind database columns directly.
- Repositories never return persistence records to application or inbound
  layers.
- Persistence records never become domain truth.
- Mappers remain the only translation boundary.
- Bridge records reconstruct aggregate-owned sets before returning the domain
  object.
- A deferred Decision is reconstructed by applying the review outcome before
  replaying its permitted post-deferral edits, preserving both `reviewedAt` and
  `updatedAt`.
- Controlled text values are reconstructed through their existing value
  objects so domain validation remains active.
- Monetary amount and currency columns are reconstructed together through
  `Money` and `ROIAmount`.
- Metadata text is reconstructed through `PostgresFlatMetadataJson` before
  entering the domain.
- No database row, map or persistence record bypasses a mapper.

## Constraint Enforcement Boundary

V1 enforces row-local validity, referential integrity, uniqueness and the
current repository access paths. Existing domain/application/transaction
contracts enforce invariants that require observing multiple child rows or
aggregate state:

| Invariant | Enforcement owner | Sprint 2.7.7 proof |
|---|---|---|
| Originating Evidence is included in Decision evidence membership | Domain, mapper and atomic Decision save | Persist and reload Decision |
| Recommendation has at least one Evidence | Domain and atomic Recommendation save | Reject invalid construction; persist and reload valid Recommendation |
| Recommendation creation and Decision attachment are atomic | Document 39 transaction boundary | Failure rollback across both repositories |
| Required ledger evidence snapshots exist | LedgerEntry and atomic ledger append | Entry-type integration cases |
| Evidence belongs to the Decision case | Application use cases | Traceability integration cases |
| Ledger correction points to the same Decision | Application plus composite FK | Cross-Decision reference rejection |
| Ledger rows cannot be updated or deleted | Repository API plus application-role privileges | Permission-denial integration cases |

No trigger, view, function, procedure or additional table is needed to satisfy
this boundary.

## Sprint 2.7.6.2 Handoff

The V1 migration must:

- create exactly the seven application tables in this contract;
- use the frozen types, nullability, keys, checks, foreign keys and indexes;
- preserve all specified constraint and index names;
- create no seed data;
- create no view, trigger, stored procedure, function, partition, read model or
  dashboard object;
- make no domain, application, port, mapper, record or repository change.

Flyway validation and repeat execution belong to Sprint 2.7.6.2. PostgreSQL
application-role permissions and real integration tests belong to Sprint
2.7.7.

## Architecture Review

| Criterion | Result |
|---|---|
| Frozen Domain respected | PASS |
| Aggregate boundaries preserved | PASS |
| Repository Minimalism preserved | PASS |
| CQRS write-model rule preserved | PASS |
| Ledger Append Rule preserved | PASS |
| Current mappers remain valid | PASS |
| Current repositories remain implementable | PASS |
| No framework or infrastructure behavior introduced | PASS |
| All schema decisions closed | PASS |

## Authorized Mapper Correction Gate

This correction is a separately authorized prerequisite discovered during
contract validation; it is not part of the schema deliverable.

| Gate | Result |
|---|---|
| Only `PostgresDecisionMapper` modified | PASS |
| `Decision` and all other domain types unchanged | PASS |
| Persistence records unchanged | PASS |
| Ports and use cases unchanged | PASS |
| `reviewedAt` preserved for `DEFERRED` | PASS |
| Independent `updatedAt` preserved for `DEFERRED` | PASS |
| Java 21 compilation | PASS |
| Deferred round-trip verification | PASS |

## Green Gate

| Gate | Result |
|---|---|
| One document created | PASS |
| No Java modified by the schema-contract deliverable | PASS |
| Separately authorized mapper correction isolated to one file | PASS |
| No SQL or DDL created | PASS |
| No Flyway migration created | PASS |
| No PostgreSQL executed | PASS |
| Exactly seven application tables specified | PASS |
| No `organization_id` in V1 | PASS |
| Metadata strategy frozen as `TEXT` | PASS |
| Recommendation cardinality frozen | PASS |
| Every foreign key action frozen | PASS |
| Every column type and nullability frozen | PASS |
| Every required repository index frozen | PASS |
| No speculative index | PASS |
| No architecture drift | PASS |
| DII | 100% |
| ASI | 100% |
| Decision Stability | 0 prior decisions modified |

STATUS: PASS
