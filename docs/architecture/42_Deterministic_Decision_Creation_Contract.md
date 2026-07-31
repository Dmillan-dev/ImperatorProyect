# 42 - Deterministic Decision Creation Contract

Sprint: 3.2.0 - Deterministic Decision Creation Contract Freeze

Status: Frozen functional contract for Sprint 3.2.

Decision authority: D081.

## Scope

This document freezes the functional boundary for the first deterministic
Evidence-to-Decision transition in Phase 3. It applies only to the locked
`DRC-AOA-001` AI Onboarding Assistant Recovery slice and to the existing
`CreateDecisionInputPort`, `CreateDecisionUseCase`, `Decision` aggregate and
Decision persistence boundary.

This contract defines required behavior. It does not select the technical
mechanism used to make creation idempotent under retries or concurrency.

Sprint 3.2 may implement only:

```text
Eligible persisted Evidence
        |
        v
CreateDecisionCommand
        |
        v
CreateDecisionInputPort
        |
        v
CreateDecisionUseCase
        |
        v
Decision in CREATED state
        |
        v
DecisionRepository
        |
        v
PostgreSQL
```

The existing Domain, Application commands and ports remain the architectural
baseline. This contract does not authorize a new aggregate, table, HTTP route,
repository operation, database constraint or schema migration.

## Motivation

The existing implementation already expresses the basic transition:

- `CreateDecisionCommand` supplies the Decision identity and immutable
  creation inputs;
- `CreateDecisionUseCase` loads the originating Evidence inside a transaction;
- `caseId` is derived from `Evidence.correlationKey()`;
- `Decision.create(...)` creates the aggregate in `CREATED` state; and
- `DecisionRepository.save(...)` persists it.

That baseline does not yet satisfy the required retry behavior. The use case
does not distinguish an initial creation from a retry, while the current
PostgreSQL adapter implements `save()` as a full upsert. Replaying creation can
therefore overwrite an existing Decision and can reset a Decision that has
already progressed beyond its immutable creation state.

Sprint 3.2 must close that behavioral gap without moving decision-making into
the controller, importer or repository.

## Decision Lifecycle

The existing lifecycle remains authoritative:

```text
CREATED
   |
   v
UNDER_REVIEW
   |-------|--------|
   v       v        v
APPROVED REJECTED DEFERRED
                    |
                    +---- existing pre-review editing path
```

Sprint 3.2 owns creation only. It must not attach a Recommendation, enter
review, perform a review action, append a Ledger entry, mark implementation or
validate a result.

Decision creation is not equivalent to case readiness. A Decision may exist in
`CREATED` state with only its eligible originating Evidence. The evidence set
required for Recommendation, ROI and review readiness belongs to later Phase 3
sprints.

## Creation Trigger

Creation is an explicit Application operation through the existing
`CreateDecisionInputPort`.

The following rules are frozen:

- Evidence import does not automatically create a Decision per NDJSON line.
- The Evidence importer and REST controller do not contain Decision creation
  rules.
- A repository read or REST `GET` never creates a Decision as a side effect.
- The first Phase 3 flow invokes Decision creation only after the originating
  Evidence has been imported and committed successfully.
- No new public Decision-creation HTTP route is introduced by Sprint 3.2.

The caller at the Application boundary is responsible for supplying a complete
`CreateDecisionCommand`. Application owns the transition; transport and
persistence remain adapters.

## Eligible Originating Evidence

Exactly one persisted Evidence item originates the Decision. That Evidence is
also the first member of `Decision.evidenceIds`.

For Sprint 3.2, originating Evidence is eligible only when all of the following
are true:

| Rule | Required value or behavior |
| --- | --- |
| Persistence | The Evidence already exists in `EvidenceRepository`. |
| Case correlation | `correlationKey` is exactly `DRC-AOA-001`. |
| Evidence type | `evidenceType` is `business_context`. |
| Event type | `eventType` is `business_context_requested`. |
| Review status | `reviewStatus` is `ACCEPTED`. |
| Sensitivity | `sensitivity` is not `RESTRICTED`. |
| Raw payload | `rawPayloadMode` is `not_stored`. |

Cost, AI usage, quality, deployment and ownership evidence are not prerequisites
for Decision creation. They are later inputs to readiness, Recommendation, ROI
or review behavior.

If the originating Evidence is missing or ineligible, no Decision is persisted.
The failure is an Application outcome. Sprint 3.2 must not weaken the Evidence
domain contract or reinterpret rejected import lines as eligible Evidence.

## Deterministic Identity

A deterministic Decision Creation means that the same eligible originating
Evidence, correlation key and creation inputs always produce the same Decision
identity and immutable creation state, regardless of execution timing or
retries.

The identity rules are:

- `DecisionId` is a UUID version 4 created by the initiating Application
  boundary before domain construction, as frozen by the persistence contract.
- The same logical creation operation must reuse the same `DecisionId` on every
  retry. Generating a new identifier during a retry creates a different command
  and is forbidden.
- `CreateDecisionUseCase` and persistence preserve the supplied identifier
  unchanged. They do not generate, replace or reinterpret it.
- `caseId` is derived only from the eligible originating
  `Evidence.correlationKey()` and is therefore `DRC-AOA-001` for this slice.
- Runtime clock time, transaction timing, thread scheduling and retry count do
  not contribute to Decision identity.

This contract does not choose how the initiating boundary remembers or
recovers the stable `DecisionId`. Sprint 3.2 must preserve the functional rule
without adding an unapproved transport or persistence model.

## Immutable Creation Attributes

The immutable creation state is the following normalized tuple:

| Attribute | Source | Normalization or invariant |
| --- | --- | --- |
| `id` | `CreateDecisionCommand.decisionId` | Stable supplied UUID; unchanged. |
| `caseId` | Originating `Evidence.correlationKey` | Derived, not independently supplied. |
| `originatingEvidenceId` | `CreateDecisionCommand.originatingEvidenceId` | Must identify the eligible persisted Evidence. |
| `title` | `CreateDecisionCommand.title` | Trimmed by the existing Domain rule; non-blank. |
| `businessNeed` | `CreateDecisionCommand.businessNeed` | Trimmed by the existing Domain rule; non-blank. |
| `ownerId` | `CreateDecisionCommand.ownerId` | Required and unchanged. |
| `requiredApproverId` | `CreateDecisionCommand.requiredApproverId` | Required and unchanged. |
| `createdAt` | `CreateDecisionCommand.createdAt` | Explicit stable input; never replaced by the retry time. |

These values form the semantic comparison for a creation retry. Later mutable
Decision state, including status, evidence links added after creation,
Recommendation reference and review fields, is not part of the creation tuple
and must never be overwritten while resolving a retry.

## Initial State (`CREATED`)

A successful first creation produces exactly the state already defined by the
`Decision` aggregate:

| Field | Initial value |
| --- | --- |
| `status` | `CREATED` |
| `updatedAt` | Equal to `createdAt`. |
| `evidenceIds` | Contains the originating Evidence exactly once. |
| `recommendationId` | Empty. |
| `reviewedBy` | Empty. |
| `reviewedAt` | Empty. |
| `reviewReason` | Empty. |

Decision creation produces no Ledger entry. Audit entries belong to explicit
later lifecycle actions under the Ledger contract.

## Idempotency Contract

Idempotency applies to one logical creation identity, not to an NDJSON batch.

The required outcomes are:

| Condition | Required outcome |
| --- | --- |
| No Decision exists for the logical identity | Persist one new Decision in the initial `CREATED` state. |
| An existing Decision has the same immutable creation tuple | Return the existing Decision-equivalent result without changing persisted state. |
| The same identity is presented with different immutable creation attributes | Reject the command as a creation conflict; do not overwrite any field. |
| An identical retry occurs after the Decision has progressed | Return the existing Decision-equivalent result; preserve all later state exactly. |

An idempotent retry must not:

- reset status to `CREATED`;
- replace `updatedAt`, Recommendation or review data;
- delete evidence relationships added after creation;
- append a Ledger entry;
- create a second Decision for the same logical operation; or
- report success after silently changing immutable creation attributes.

No HTTP status or REST error mapping is frozen here because Sprint 3.2 does not
introduce a Decision creation route.

## Concurrency Expectations

Concurrency must preserve the same functional guarantees as sequential
retries:

- equivalent concurrent creation commands yield exactly one persisted
  Decision identity and semantically equivalent successful outcomes;
- conflicting concurrent commands for the same identity never overwrite each
  other silently;
- at most one immutable creation tuple wins for one Decision identity;
- a command racing with an already progressed Decision cannot reset or delete
  that Decision's current state; and
- the existence check, conflict decision and creation effect must be protected
  as one atomic behavior.

Tests in Sprint 3.2 must exercise both equivalent retries and conflicting or
concurrent attempts against real PostgreSQL, in addition to deterministic
Application tests.

## Repository Considerations

The current `DecisionRepository` exposes:

- `save(Decision)`;
- `findById(DecisionId)`; and
- `existsById(DecisionId)`.

The current PostgreSQL `save()` uses `INSERT ... ON CONFLICT (id) DO UPDATE`
and replaces the complete Decision row and evidence-link set. That behavior is
appropriate for persisting intentional aggregate state changes, but by itself
it does not guarantee idempotent creation and can overwrite later state when a
creation command is replayed.

This document freezes the problem and the required result, not its technical
solution. It deliberately does not choose or authorize `createIfAbsent()`, an
optimistic-locking protocol, an additional SQL constraint, a schema migration,
a new lock, or any other repository technique.

Sprint 3.2 must review the smallest correct implementation mechanism before
changing code. If that mechanism requires a repository-port change, schema
change, migration, new table or modification to a frozen contract, the sprint
must stop and request explicit architectural approval. The existing port must
not be extended merely because this document exists.

## Explicit Exclusions

Sprint 3.2.0 and this contract do not authorize:

- Domain, aggregate, value-object or lifecycle changes;
- a repository-port extension or new persistence operation;
- changes to V1 or any new database migration;
- a new REST route, controller, DTO or HTTP error mapping;
- automatic Decision creation during Evidence import;
- an `EvidenceBatch`, raw-event store, import-attempt record or Event Store;
- Decision readiness evaluation or attachment of additional evidence;
- Recommendation generation or attachment;
- ROI calculation;
- Explanation Provider behavior;
- review, approval, rejection or deferral;
- Ledger append, implementation marking or result validation;
- JWT, RBAC, frontend, live connectors or pilot data; or
- additional cases or recommendation families.

## Consequences

### Positive consequences

- Sprint 3.2 has one testable functional target.
- Retries cannot erase business progress or audit-relevant state.
- Determinism is independent of runtime timing and infrastructure scheduling.
- Domain and Application continue to own Decision semantics.
- Recommendation, ROI, Explanation, Review and Ledger remain separately
  traceable Phase 3 increments.

### Required Sprint 3.2 evidence

Sprint 3.2 may be certified only when tests prove:

1. eligible persisted business-context Evidence creates one Decision;
2. the Decision has the frozen identity and initial `CREATED` state;
3. ineligible or missing Evidence creates no Decision;
4. an identical retry returns an equivalent result without mutation;
5. a conflicting retry cannot overwrite the existing Decision;
6. a retry cannot reset a Decision that has progressed;
7. equivalent concurrent attempts result in one persisted Decision identity;
8. the complete behavior is atomic against real PostgreSQL; and
9. no Recommendation, ROI, Review or Ledger behavior is introduced.

### Deliberately deferred implementation decision

The exact idempotent-creation technique remains deliberately deferred to the
Sprint 3.2 pre-implementation review. This is not an unresolved functional
requirement: the observable outcomes above are frozen. Only the internal
technical means of producing them remains unselected.
