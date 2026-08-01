# 44 - Review, Ledger And Result Validation Contract

Sprint: 3.4.0 - Review And Ledger Contract Freeze

Status: Frozen functional contract for Sprint 3.4.

Decision authority: D083.

## Scope

This document freezes the first human-governance lifecycle for the locked
`DRC-AOA-001` AI Onboarding Assistant Recovery slice. It governs the
Application behavior that moves an existing Decision through human review,
records every accepted governance outcome in the append-only Decision Ledger,
marks external implementation and validates realized recovery.

Sprint 3.4 may implement only:

```text
Persisted Evidence
        |
        v
Persisted Decision + deterministic Recommendation
        |
        v
Advisory Explanation
        |
        v
Authorized Human Review
        |
        v
Atomic Decision transition + Ledger append
        |
        v
External implementation marker
        |
        v
Authorized result validation
```

This contract defines observable business outcomes. It does not select the
exact repository-port extension, PostgreSQL locking statement or conflict
resolution implementation required to produce those outcomes.

## Authority And Precedence

This contract specializes the governance loop defined by:

- D079 through D082 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/34_MVP_Implementation_Blueprint.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- `docs/architecture/39_Persistence_Transaction_Contract.md`;
- `docs/architecture/40_Persistence_Schema_Contract.md`;
- `docs/architecture/42_Deterministic_Decision_Creation_Contract.md`;
- `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md`;
- `docs/product/27_MVP_Acceptance_Test_Plan.md`;
- `docs/product/28_Identity_Access_Approval_Model.md`;
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`; and
- `docs/product/DECISION_LEDGER_V2.md`.

Broader conceptual lifecycle names remain useful product language. The exact
Decision states, Ledger facts and command outcomes in this contract govern
Sprint 3.4.

## Narrow Supersession Of Document 39

Document 39 currently requires `ReviewDecisionUseCase` and
`AppendLedgerEntryUseCase` to remain separate logical transactions. Document
37 requires a Decision state transition and its Ledger append to be atomic when
Phase 3 implements business behavior. Preserving the separation in Document 39
would permit an approved, rejected or deferred Decision without the matching
audit fact, or the inverse.

D083 therefore supersedes only the transactional-separation requirement in
Document 39 for the Sprint 3.4 MVP review operations `approve`, `reject` and
`defer`.

For those operations, one Application transaction must:

1. resolve an idempotent replay before applying another transition;
2. serialize governance activity for the Decision;
3. load the authoritative Decision and Recommendation;
4. validate the trusted actor and required evidence;
5. execute the permitted Domain transition;
6. construct the matching immutable Ledger entry and snapshot;
7. persist the Decision transition; and
8. append the Ledger entry and its Evidence snapshot links.

Either every step commits or none commits. No observable state may contain:

```text
Decision = APPROVED | REJECTED | DEFERRED
matching Ledger fact = absent
```

or:

```text
matching Ledger fact = present
Decision = prior state
```

All other boundaries and prohibitions in Document 39 remain intact. Evidence
import, Decision creation, deterministic Recommendation generation, provider
explanation, external implementation and Business Value aggregation are not
silently merged into the review transaction.

## Ownership Boundary

Responsibilities remain separated as follows:

```text
Transport
-> carries syntax and an already authenticated actor context only

Application
-> authorizes the operation, resolves replay, orchestrates one transaction,
   derives snapshots from authoritative persisted objects and preserves order

Domain
-> enforces Decision transitions and LedgerEntry invariants

Persistence
-> stores the resulting aggregate state and immutable history atomically
```

Controllers, repositories and provider adapters must not decide who may
approve, reject, defer, mark implementation or validate a result.

The Explanation Provider remains advisory. Its output cannot change review
readiness, Recommendation action, estimated savings, confidence, risk,
authority or any Ledger snapshot.

## Decision State Contract

The frozen Decision state inventory remains exactly:

- `CREATED`;
- `UNDER_REVIEW`;
- `APPROVED`;
- `REJECTED`; and
- `DEFERRED`.

Sprint 3.4 must not introduce `IMPLEMENTED`, `VALIDATED`, `CLOSED` or any other
Decision state. External implementation and result validation are later
immutable facts in the Decision Ledger. After those facts are appended, the
Decision remains `APPROVED`.

### Review transition matrix

| Command | Permitted current Decision state | Internal transition | Persisted final state | Ledger fact |
|---|---|---|---|---|
| Approve | `CREATED`, `UNDER_REVIEW`, `DEFERRED` | Enter or re-enter `UNDER_REVIEW`, then approve | `APPROVED` | `approved` |
| Reject | `CREATED`, `UNDER_REVIEW`, `DEFERRED` | Enter or re-enter `UNDER_REVIEW`, then reject | `REJECTED` | `rejected` |
| Defer | `CREATED`, `UNDER_REVIEW` | Record the deferral outcome | `DEFERRED` | `deferred` |
| Mark implemented | `APPROVED` | No Decision-state change | `APPROVED` | `implementation_marked` |
| Validate result | `APPROVED` | No Decision-state change | `APPROVED` | `result_validated` |

Entering `UNDER_REVIEW` is an internal Domain transition, not a new public HTTP
command in this slice. When approve or reject starts from `CREATED` or
`DEFERRED`, the intermediate state is part of the same transaction and must
never be persisted as the final outcome of that command.

An already `DEFERRED` Decision may be approved or rejected through the defined
re-entry into review. A second independent defer command against the same
deferred outcome is not a new review and must conflict. A later product policy
may define renewed deferral after material new Evidence, but Sprint 3.4 does
not invent that workflow.

`APPROVED` and `REJECTED` are terminal Decision states. Approval does not imply
that the external change was implemented. Rejection prohibits implementation
and result validation.

## Recommendation And Evidence Preconditions

Approve, reject and defer require:

- one persisted Recommendation owned by the Decision;
- the Decision to reference that same Recommendation;
- persisted Evidence snapshots belonging to the Decision and Recommendation;
- a nonblank human reason or note;
- a trusted actor identifier and authorized Phase 1 role; and
- an operation identifier and occurrence time.

The Application layer derives Recommendation identity, estimated savings,
confidence, risk and Evidence snapshot membership from persisted authoritative
objects. Those values must not be accepted as business truth from an HTTP DTO,
controller or other transport adapter.

Approval additionally requires the frozen owner, approver, Evidence, ROI and
assumption readiness gates. Rejection requires a rejection reason and the
current reviewed Evidence and ROI snapshot. Deferral requires a deferral reason
and either the required missing Evidence description or a review date.

## Authority Contract

Sprint 3.4 uses only the frozen Phase 1 role reduction:

| Role | Approve | Reject | Defer | Mark implemented | Validate result |
|---|---:|---:|---:|---:|---:|
| `ADMIN` | Yes | Yes | Yes | No | No |
| `PLATFORM_ENGINEER` | No | No | Yes | Yes | No |
| `FINANCE` | No | No | Yes | No | Yes |
| `AUDITOR` | No | No | No | No | No |

`ADMIN` represents CTO or VP Engineering approval authority only for the
controlled Phase 1 demonstration. It must not become permanent enterprise
approval semantics.

`AUDITOR` remains read-only. A future security decision may explicitly enable
security-blocker deferral, but Sprint 3.4 does not infer that authority.

The actor context must come from a trusted Application boundary. Because
JWT/RBAC is deferred, the existing Ledger command HTTP route shells remain
controlled `501 Not Implemented` responses during Sprint 3.4. A request body,
query parameter or arbitrary header must not self-assert an authoritative actor
identifier or role.

## Ledger Genesis

Sprint 3.4 does not fabricate or backdate a `recommendation_created` entry for
a Recommendation produced by the already certified Sprint 3.3 workflow.

The first Ledger entry for this slice is the first accepted human governance
outcome:

- `approved`;
- `rejected`; or
- `deferred`.

The persisted Recommendation already provides authoritative creation state.
Adding `recommendation_created` to future Recommendation generation would
widen the certified generation transaction and requires a separate explicit
decision.

## Ledger Immutability

Ledger history is append-only. Sprint 3.4 permits only:

- reading existing Ledger entries;
- inserting one new Ledger entry; and
- inserting its Evidence snapshot links in the same transaction.

Sprint 3.4 forbids:

- updating a Ledger entry;
- deleting a Ledger entry;
- replacing or deleting an Evidence snapshot link;
- changing a previous entry to correct history;
- reusing an entry identifier for different immutable content; and
- implementing immutability through triggers or a schema change.

The existing PostgreSQL `SELECT` and `INSERT` privilege boundary remains the
physical reinforcement. Corrections require a new compensating Ledger fact and
an explicitly authorized policy. No correction command is part of Sprint 3.4.

## Ledger Sequence

Ledger ordering is logical, immutable and strictly monotonic for each
Decision.

UUID ordering and timestamps alone do not guarantee a strict audit sequence.
V1 therefore represents the logical sequence through the existing
`previous_entry_id` relationship:

1. the first governance entry for a Decision has `previousEntryId = null`;
2. every later entry names the immediately preceding entry for that Decision;
3. exactly one logical root may exist for a Decision;
4. no entry may skip the current head;
5. no two committed entries may name the same current head and create a fork;
6. an entry must never reference itself or an entry belonging to another
   Decision; and
7. an entry's `occurredAt` must be strictly later than its predecessor's
   `occurredAt`.

The logical ordinal is the entry's one-based position when following the
unbroken predecessor chain from its root. The ordinal is derived and is not a
new V1 column:

```text
1 approved
    |
    v
2 implementation_marked
    |
    v
3 result_validated
```

The sequence must never contain duplicate positions, branches, gaps or reverse
links. Application-level validation and per-Decision transaction serialization
must enforce those outcomes even though V1 does not add a numeric sequence or
unique-head constraint.

## Review Ledger Snapshots

The `approved`, `rejected` and `deferred` entries must preserve the business
truth reviewed by the human actor:

- Decision and Recommendation identity;
- actor identifier and authorized role;
- nonblank reason or note;
- current Evidence snapshot membership;
- Recommendation estimated savings in EUR;
- Recommendation confidence; and
- Recommendation risk.

The Domain and V1 schema allow a smaller monetary snapshot for rejection, but
this functional contract requires the current ROI, confidence and risk snapshot
for all three review outcomes. Rejection must remain as auditable as approval.

## Implementation Marker Contract

`implementation_marked` records an external action; IMPERATOR does not execute
the provider-side model change.

The command requires:

- a Decision that remains `APPROVED`;
- the current Ledger head to be the matching `approved` entry;
- the same Decision and Recommendation ownership;
- a trusted `PLATFORM_ENGINEER` actor;
- a nonblank implementation note;
- an implementation date or period;
- persisted implementation Evidence; and
- an operation identifier and occurrence time later than the approved entry.

The new entry must point directly to the approved entry. It does not mutate the
Decision, Recommendation, Evidence or approved entry.

Only one authoritative implementation marker is permitted for this MVP
Decision. Once it exists, a different operation identifier attempting to mark
the same Decision again must conflict.

## Result Validation Contract

`result_validated` records human-validated realized recovery. It never changes
the deterministic Recommendation or its estimated savings.

The command requires:

- a Decision that remains `APPROVED`;
- the current Ledger head to be the matching `implementation_marked` entry;
- the same Decision and Recommendation ownership;
- a trusted `FINANCE` actor;
- a nonblank validation period;
- persisted post-action Evidence;
- annualized observed baseline cost in EUR;
- annualized observed post-action cost in EUR;
- actual transition cost in EUR;
- a nonblank outcome note; and
- an operation identifier and occurrence time later than the implementation
  entry.

All monetary inputs use EUR, scale two and `HALF_EVEN`. The deterministic
calculation is:

```text
annualized realized recovery
  = annualized observed baseline cost
  - annualized observed post-action cost
  - actual transition cost

variance
  = annualized realized recovery
  - Recommendation.estimatedSavings
```

The entry stores the annualized realized recovery in `realizedSaving`, retains
the unchanged Recommendation estimate in the estimated snapshot and records
the validation period, calculation inputs, variance and outcome note as safe
metadata. Its Evidence snapshot contains the post-action validation Evidence.

The V1 schema cannot represent a negative `realizedSaving`. For the locked
Sprint 3.4 slice, the calculated annualized realized recovery must therefore be
non-negative. A negative result must be rejected without appending an entry;
it must not be silently clamped, converted to a positive value or counted as
Business Value. Supporting audited negative realized value requires a future
explicit contract and schema compatibility review.

Only one authoritative result validation is permitted for this MVP Decision.
Once it exists, a different operation identifier attempting to validate the
same result again must conflict. Realized Business Value may be counted only
from the accepted `result_validated` entry.

## Idempotency And Replay

Each governance command uses one stable application-supplied UUID v4 as the
Ledger entry and operation identity.

The replay contract is:

| Existing operation | Incoming operation | Required outcome |
|---|---|---|
| No matching UUID | Valid new command for current state and Ledger head | Execute once |
| Same UUID and identical immutable payload | Equivalent replay | Return the existing outcome without mutation |
| Same UUID and different immutable payload | Conflicting replay | Reject with conflict; change nothing |
| Different UUID after that semantic command already succeeded | Duplicate business command | Reject with conflict; change nothing |

Replay equivalence includes Decision, Recommendation, entry type, actor,
actor role, reason, occurrence time, monetary snapshots, confidence, risk,
predecessor, Evidence snapshots and safe metadata.

Replay resolution must occur before reapplying the current-state transition.
This permits an identical approval replay to return its original result even
after a later implementation or validation entry exists. A replay must never
reset Decision state, append another entry, replace snapshots or move the
Ledger head.

## Concurrency

Governance commands are serialized per Decision. Observable outcomes are
frozen as follows:

- concurrent identical commands with the same operation UUID commit one
  Decision outcome and one Ledger entry; every successful caller resolves that
  same result;
- concurrent equivalent commands with different UUIDs commit only one
  authoritative business action and the loser conflicts;
- concurrent contradictory review commands commit exactly one final Decision
  state and its matching Ledger entry; the loser conflicts;
- concurrent attempts to append after the same Ledger head cannot create a
  branch;
- implementation cannot race ahead of approval; and
- result validation cannot race ahead of implementation.

The exact lock, atomic repository operation or conflict-resolution technique
remains a Sprint 3.4 pre-implementation decision. A read-then-insert sequence
without per-Decision serialization is insufficient.

## Contradictory Reviews

Approve, reject and defer are mutually exclusive outcomes for one review
attempt. If contradictory operations race, the first committed transaction is
authoritative. Every losing transaction must fail as a conflict and must leave
no Decision or Ledger mutation.

An `APPROVED` Decision cannot later be rejected or deferred. A `REJECTED`
Decision cannot later be approved, implemented or validated. A `DEFERRED`
Decision may later re-enter review and be approved or rejected, but the
original deferred entry remains immutable and becomes the predecessor of the
later review outcome.

No automatic conflict winner may be selected by role priority, timestamp,
UUID ordering, controller order or AI output.

## Repository Considerations

The existing repositories can represent Decision state and append Ledger
entries, but their current separate semantics do not by themselves prove:

- atomic Decision transition and Ledger append;
- idempotent create-or-resolve behavior;
- one logical Ledger head per Decision;
- fork prevention under concurrency;
- one implementation marker; or
- one result validation.

This document freezes the required outcomes, not their technical mechanism.
It does not authorize a new table, column, index, constraint, migration,
trigger or stored procedure. Sprint 3.4 must review the smallest correct
Application and repository-port boundary before changing code. Any required
port change must be explicit and narrowly express governance intent.

## Explicit Exclusions

Sprint 3.4.0 and this contract do not authorize:

- Java implementation;
- SQL or Flyway changes;
- new Decision states;
- a numeric Ledger sequence column;
- `recommendation_created` backfill or synthesis;
- REST implementation or DTOs;
- authentication, JWT or permanent RBAC;
- Business Value aggregation;
- Recommendation or ROI recalculation;
- Explanation Provider changes;
- external provider-side execution;
- corrections or case closure;
- additional recommendation families;
- live connectors; or
- control-document synchronization.

## Consequences

Sprint 3.4 implementation must preserve these properties:

1. every accepted review outcome has exactly one matching Ledger fact;
2. Decision transition and Ledger append are atomic;
3. Ledger history is append-only and strictly ordered per Decision;
4. no review authority is inferred from transport input;
5. implementation and validation remain Ledger facts while Decision remains
   `APPROVED`;
6. retries cannot duplicate or rewrite governance history;
7. concurrent contradictions cannot create split truth;
8. Recommendation creation history is not fabricated;
9. realized recovery is counted only after authorized validation; and
10. no schema or REST expansion is required.

## Sprint 3.4 Certification Contract

Sprint 3.4 may be certified only when automated verification proves:

1. approve atomically persists `APPROVED` and one `approved` entry;
2. reject atomically persists `REJECTED` and one `rejected` entry;
3. defer atomically persists `DEFERRED` and one `deferred` entry;
4. every failure rolls back both Decision and Ledger effects;
5. unauthorized roles are rejected without mutation;
6. implementation requires approval and appends once without changing
   Decision state;
7. result validation requires implementation and appends once without changing
   Decision state;
8. realized recovery and variance follow the frozen EUR calculation;
9. each entry points to the immediate prior entry and occurrence time increases;
10. identical replay resolves the existing outcome;
11. conflicting replay changes nothing;
12. concurrent identical commands produce one authoritative result;
13. concurrent contradictory commands produce one winning state and matching
    Ledger entry;
14. no Ledger fork, duplicate root or sequence gap is produced;
15. PostgreSQL update and delete protection for Ledger rows remains effective;
16. no `recommendation_created` entry is synthesized;
17. the Ledger REST command shells remain controlled `501` responses; and
18. no Domain-state inventory, V1 schema, deterministic Recommendation output
    or Explanation Provider behavior changes.

### Deliberately deferred implementation decision

The exact atomic review-and-append technique remains deferred to the Sprint
3.4 pre-implementation review. This is not an unresolved functional
requirement: every externally observable state, authorization, replay,
sequence and concurrency outcome is frozen above. Only the internal technical
means of producing those outcomes remains unselected.
