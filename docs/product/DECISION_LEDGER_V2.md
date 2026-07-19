# Decision Ledger v2

## Purpose

Decision Ledger v2 defines the accountability module for IMPERATOR.

It is the immutable business record for reviewed Decision ROI Cases. It answers:

**What did the company decide, why, who approved it, under which assumptions, and what value did it produce?**

The ledger is not a raw log store, workflow engine, compliance suite or autonomous executor. It records business intent, evidence, decision state and outcome.

MVP approval authority is defined in `docs/product/28_Identity_Access_Approval_Model.md`.

MVP screen behavior is defined in `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

MVP quality attributes are defined in `docs/architecture/27_Quality_Attributes.md`.

Phase 1 scope and exit criteria are defined in `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`.

## Product Position

Decision Ledger v2 supports the canonical product rule:

**One Decision. One Timeline. One ROI.**

For the MVP, the ledger should first live inside each Decision ROI Case as a verifiable history. It becomes a standalone product surface only after enough decisions have been approved, rejected, deferred and validated to make audit and accumulated value review useful.

## MVP Scope

### In Scope

- record recommendation approval, rejection and deferral,
- preserve the evidence, ROI, assumptions, risk, confidence, owner, approver and timestamp used at decision time,
- separate estimated savings from realized savings,
- allow later audit of a Decision ROI Case without reading raw source systems,
- expose history by decision, actor, owner, state, period and recommendation family,
- provide inputs for Business Value reporting after outcomes are validated.

### Out Of Scope

- editing historical ledger entries,
- executing infrastructure, cloud or AI-provider changes,
- full workflow automation,
- advanced policy engine,
- AI Advisor or semantic search,
- compliance evidence packs,
- universal audit exports,
- configurable enterprise case-management workflows.

## Core Use Cases

### UC1 - Register Approval

An approver reviews a recommendation, accepts the assumptions and approves the action.

The ledger records:
- approver,
- approval note,
- accepted assumptions,
- evidence snapshot,
- ROI snapshot,
- estimated savings,
- risk and confidence,
- timestamp.

### UC2 - Register Rejection

An approver rejects a recommendation.

The ledger records:
- actor,
- rejection reason,
- evidence available at the time,
- ROI and assumptions reviewed,
- final recommendation state.

### UC3 - Defer Decision

An approver requests more evidence before accepting or rejecting.

The ledger records:
- deferral reason,
- required evidence,
- suggested review date,
- current confidence,
- current assumptions.

### UC4 - Mark Implementation

The company marks that an approved recommendation was implemented outside IMPERATOR.

The ledger records:
- actor,
- implementation note,
- implementation date or period,
- linked approval entry,
- implementation evidence when available.

### UC5 - Validate Result

The owner or FinOps user validates realized savings after implementation.

The ledger records:
- realized saving,
- validation period,
- validation evidence,
- variance versus estimate,
- outcome note.

### UC6 - Audit Decision History

An executive, auditor or platform owner reconstructs the full decision record.

The ledger returns:
- all entries for the Decision ROI Case,
- evidence and ROI snapshots,
- actors and timestamps,
- state transitions,
- estimated and realized value.

### UC7 - Feed Business Value

Business Value uses validated ledger outcomes to calculate recovered value, recovered time, risk avoided and realized versus projected value.

Only validated result entries should count as realized value.

## Domain Model

### Ledger Entry

Immutable record of a business-relevant state change for a Decision ROI Case.

Fields:
- `ledger_entry_id`
- `organization_id`
- `decision_roi_case_id`
- `recommendation_id`
- `entry_type`
- `decision_state`
- `actor_id`
- `actor_role`
- `timestamp`
- `reason`
- `evidence_snapshot_id`
- `roi_snapshot_id`
- `assumptions_snapshot_id`
- `risk_snapshot`
- `confidence_snapshot`
- `estimated_saving`
- `realized_saving`
- `currency`
- `source`
- `previous_entry_id`

### Entry Type

Allowed values:
- `recommendation_created`
- `approved`
- `rejected`
- `deferred`
- `implementation_marked`
- `result_validated`
- `evidence_requested`
- `case_closed`

### Decision State

Allowed values:
- `draft_case`
- `not_reviewable`
- `review_ready`
- `approved`
- `rejected`
- `deferred`
- `implemented`
- `validated`
- `closed`

### Evidence Snapshot

Versioned snapshot of evidence used at the moment of decision.

It must remain understandable even if an integration is later disabled.

Contains:
- source integration,
- source object references,
- observed facts,
- timestamps or periods,
- lineage,
- sensitivity,
- confidence contribution.

### ROI Snapshot

Versioned snapshot of the ROI view used at the moment of decision.

Contains:
- current monthly cost,
- estimated monthly saving,
- estimated annualized recovery,
- realized saving when available,
- payback,
- value signal,
- risk,
- confidence,
- assumptions.

### Assumptions Snapshot

Versioned record of the assumptions accepted or reviewed.

Examples:
- model downgrade price assumption,
- usage trend assumption,
- quality-equivalence assumption,
- engineering hourly cost,
- validation period.

### Result Validation

Record that connects an approved action to a measured or estimated realized outcome.

Result validation must never overwrite the original estimate. It creates a new ledger entry.

## Domain Invariants

- Ledger entries are append-only.
- A ledger entry cannot be edited after creation.
- An approval entry must reference evidence, ROI and assumptions snapshots.
- A rejection entry must include a rejection reason.
- A deferral entry must include required evidence or a review date.
- Realized saving must be recorded separately from estimated saving.
- New evidence creates a new snapshot or entry; it does not mutate historical meaning.
- The ledger records approval intent and outcome; it does not execute changes.
- Business Value can count realized value only from validated result entries.
- If a connector is disabled, past ledger entries must remain understandable.

## Conceptual API

This API is product-level intent. It is not OpenAPI, protobuf or implementation design.

### `GET /ledger`

Question:

Which decision records exist?

Filters:
- `decision_roi_case_id`
- `state`
- `entry_type`
- `actor_id`
- `owner_id`
- `from`
- `to`
- `recommendation_family`

Returns conceptually:
- ledger entry ID,
- related Decision ROI Case,
- entry type,
- state,
- actor,
- timestamp,
- estimated saving,
- realized saving,
- confidence,
- evidence reference.

### `GET /ledger/{ledgerEntryId}`

Question:

What exactly was recorded for this decision event?

Returns conceptually:
- ledger entry,
- evidence snapshot,
- ROI snapshot,
- assumptions snapshot,
- previous entry reference,
- related recommendation and decision.

### `GET /decisions/{decisionId}/ledger`

Question:

What is the full accountability history of this Decision ROI Case?

Returns conceptually:
- ordered ledger entries,
- state transitions,
- approvals, rejections, deferrals and result validations,
- estimated versus realized value.

### `POST /decisions/{decisionId}/ledger/approve`

Command:

Record approval of a recommendation.

Input:
- `actor_id`
- `recommendation_id`
- `approval_note`
- `accepted_assumptions`
- `expected_business_value`

Effect:
- creates an immutable `approved` ledger entry,
- records evidence, ROI and assumptions snapshots,
- changes recommendation state to approved,
- does not execute external changes.

### `POST /decisions/{decisionId}/ledger/reject`

Command:

Record rejection of a recommendation.

Input:
- `actor_id`
- `recommendation_id`
- `rejection_reason`

Effect:
- creates an immutable `rejected` ledger entry,
- preserves evidence and ROI reviewed at rejection time.

### `POST /decisions/{decisionId}/ledger/defer`

Command:

Record that a recommendation needs more evidence or later review.

Input:
- `actor_id`
- `recommendation_id`
- `deferral_reason`
- `required_evidence`
- `review_date`

Effect:
- creates an immutable `deferred` ledger entry,
- records the evidence gap.

### `POST /decisions/{decisionId}/ledger/mark-implemented`

Command:

Record that the approved action was implemented outside IMPERATOR.

Input:
- `actor_id`
- `implementation_note`
- `implementation_period`
- `implementation_evidence`

Effect:
- creates an immutable `implementation_marked` ledger entry,
- links implementation to the prior approval entry.

### `POST /decisions/{decisionId}/ledger/validate-result`

Command:

Record realized business value after implementation.

Input:
- `actor_id`
- `realized_saving`
- `currency`
- `validation_period`
- `validation_evidence`
- `outcome_note`

Effect:
- creates an immutable `result_validated` ledger entry,
- makes realized value available to Business Value reporting.

## Events

### Events Consumed

- `RecommendationGenerated`
- `EvidenceSnapshotCreated`
- `RoiSnapshotCreated`
- `AssumptionsSnapshotCreated`
- `ImplementationMarkedComplete`
- `BusinessValueValidated`

### Events Emitted

- `LedgerEntryCreated`
- `RecommendationApproved`
- `RecommendationRejected`
- `RecommendationDeferred`
- `DecisionImplementationMarked`
- `DecisionResultValidated`
- `DecisionCaseClosed`

## Event Rules

- Every emitted event must reference `ledger_entry_id`.
- Approval, rejection and deferral events must reference `decision_roi_case_id` and `recommendation_id`.
- Result validation events must include estimated versus realized value when both exist.
- Events must not contain raw provider payloads.
- Events must preserve tenant and organization boundary.

## Risks and Mitigations

### Risk 1 - Ledger becomes a workflow engine

Mitigation:
Keep Ledger v2 append-only and state-recording only. Workflow orchestration belongs outside the ledger.

### Risk 2 - Estimated and realized value are mixed

Mitigation:
Use separate fields, separate entries and separate reporting rules.

### Risk 3 - Evidence becomes mutable

Mitigation:
Use snapshots or versioned references. Never reinterpret old approvals using new evidence.

### Risk 4 - Compliance scope expands too early

Mitigation:
Support basic auditability, not full GRC workflows or compliance packs.

### Risk 5 - Ledger has no buyer-visible value

Mitigation:
Tie every entry to ROI, owner, approver, evidence and a Decision ROI Case.

### Risk 6 - Connector removal breaks history

Mitigation:
Preserve source metadata, observed facts and evidence summaries inside snapshots.

## Acceptance Criteria

- A full Decision ROI Case history can be reconstructed from ledger entries.
- Approval records actor, timestamp, recommendation, evidence, ROI and accepted assumptions.
- Rejection requires a reason.
- Deferral requires required evidence or a review date.
- Historical entries cannot be edited.
- Estimated saving and realized saving are always separate.
- Business Value can calculate realized value only from validated result entries.
- Decision Review Workspace can show ledger history without reading raw source systems.
- Ledger entries remain understandable after an integration is disabled.
- No ledger command executes external infrastructure, AI-provider or SaaS changes.
- Every state transition creates a new ledger entry.
- Every emitted ledger event references the created ledger entry.

## MVP Product Rule

Ledger v2 should be invisible until trust is needed.

For the first MVP, expose ledger history inside Decision Detail. Promote it to a standalone Decision Ledger surface only after customers have enough reviewed decisions to audit patterns, outcomes and accumulated value.
