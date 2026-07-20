# Phase 1 - Stage 08: Ledger And Approval Slice

## Purpose

Define how Phase 1 records human review, approval, rejection, deferral, implementation marking and result validation for `DRC-AOA-001`.

This stage turns the deterministic recommendation into auditable decision history.

It does not define code, SQL, event sourcing, workflow engines, BPMN, provider automation or UI implementation.

## Authority Inputs

This stage is governed by:

1. `agents/phase1/02_acceptance_contract.md`
2. `agents/phase1/04_data_persistence_slice.md`
3. `agents/phase1/06_domain_roi_recommendation_slice.md`
4. `agents/phase1/07_ai_explanation_slice.md`
5. `docs/product/28_Identity_Access_Approval_Model.md`
6. `docs/product/DECISION_LEDGER_V2.md`
7. `docs/architecture/29_Event_Evidence_Vocabulary.md`
8. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
9. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
10. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

If there is conflict, the Identity, Access and Approval Model controls human authority, and the Phase 1 scope contract controls MVP limits.

## Stage Decision

Phase 1 uses an append-only ledger history for the first Decision ROI Case.

The ledger records what happened.

The ledger does not decide what should happen.

## Ledger Position In The Flow

The ledger begins recording after a Decision ROI Case becomes reviewable:

```text
Evidence
-> ROI View
-> Recommendation
-> AI Explanation
-> Human Review Action
-> Ledger Entry
-> Optional Implementation Mark
-> Optional Result Validation
```

The ledger may record evidence, ROI and recommendation snapshots.

The ledger must not calculate ROI or generate recommendations.

## Minimum Ledger Events

The Phase 1 MVP should be able to represent:

| Event | Meaning |
| --- | --- |
| `decision_case_created` | The case exists and has a reviewable identity. |
| `evidence_attached` | Evidence summaries are associated with the case. |
| `roi_view_generated` | Deterministic ROI View is available. |
| `recommendation_generated` | One deterministic recommendation is available. |
| `ai_explanation_generated` | Explanation was generated from prepared context. |
| `review_approved` | Authorized approver approved the recommendation. |
| `review_rejected` | Authorized approver rejected the recommendation. |
| `review_deferred` | Authorized actor deferred decision pending evidence or risk resolution. |
| `implementation_marked` | Authorized technical owner marked action as implemented. |
| `result_validated` | Authorized FinOps or reviewer validated realized result. |

The Phase 1 demo must show at least one human review action recorded.

`implementation_marked` and `result_validated` must be representable, but the first demo does not need to prove real post-action savings.

## Ledger Entry Contract

Each ledger entry should be representable with:

| Field | Meaning |
| --- | --- |
| `ledger_entry_id` | Stable entry identifier. |
| `case_id` | `DRC-AOA-001`. |
| `event_type` | One of the allowed ledger events. |
| `occurred_at` | When the event was recorded. |
| `actor_ref` | Human actor or system component reference. |
| `actor_role` | Role at the time of action. |
| `reason` | Human-readable reason or system reason. |
| `evidence_refs` | Evidence IDs relevant to the event. |
| `roi_snapshot_ref` | Snapshot reference where relevant. |
| `recommendation_snapshot_ref` | Recommendation snapshot reference where relevant. |
| `ai_explanation_ref` | Explanation reference where relevant. |
| `previous_state` | Case state before the event. |
| `new_state` | Case state after the event. |
| `correlation_id` | Trace identifier for observability. |

The ledger entry should preserve enough context to understand why a decision was made later.

## Snapshot Rules

When a human review action occurs, the ledger must preserve:

- recommendation text reviewed,
- ROI numbers reviewed,
- ROI assumptions reviewed,
- evidence IDs available at review time,
- risk label reviewed,
- confidence value reviewed,
- AI explanation reference if present,
- actor and role that performed the action.

Historical review entries must not be rewritten if evidence or assumptions change later.

Later corrections require new ledger entries.

## Human Authority Matrix

| Action | Authorized Role |
| --- | --- |
| View Internal evidence | `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR` according to sensitivity. |
| Approve recommendation | `ADMIN` in Phase 1 demo, representing CTO/VP Engineering authority. |
| Reject recommendation | `ADMIN` in Phase 1 demo, representing CTO/VP Engineering authority. |
| Defer for technical feasibility | `PLATFORM_ENGINEER` or `ADMIN`. |
| Defer for cost validation | `FINANCE` or `ADMIN`. |
| Defer for audit/security risk | `AUDITOR` if enabled, or `ADMIN`. |
| Mark implementation | `PLATFORM_ENGINEER`. |
| Validate realized result | `FINANCE`. |

AI, connectors and import adapters have no human review authority.

`ADMIN` is a Phase 1 demo approver role and must not become permanent enterprise business-approver logic.

## State Transition Rules

Allowed Phase 1 state movement:

```text
Draft
-> Evidence Ready
-> ROI Ready
-> Recommendation Ready
-> Pending Review
-> Approved
-> Implementation Marked
-> Result Validated
```

Alternative review outcomes:

```text
Pending Review -> Rejected
Pending Review -> Deferred
Deferred -> Pending Review
```

The first MVP demo may stop after `Approved`, `Rejected` or `Deferred` if result validation is not yet possible.

The product must not claim realized Business Value until `Result Validated`.

## Review Action Requirements

Approval requires:

- recommendation ready,
- ROI View ready,
- approver role present,
- no active security blocker,
- no required evidence sensitivity unknown,
- reviewed snapshot stored.

Rejection requires:

- approver role present,
- reason recorded,
- reviewed snapshot stored.

Deferral requires:

- authorized role for the blocker,
- blocker category recorded,
- reason recorded,
- required next evidence or action recorded.

Implementation marking requires:

- approved recommendation,
- technical owner role,
- implementation reference or note.

Result validation requires:

- implementation marked,
- FinOps or authorized reviewer role,
- validated outcome note,
- realized value evidence or explicit unavailable status.

## Ledger Security Rules

Ledger entries may reference sensitive evidence IDs.

Ledger entries must not duplicate:

- raw prompts,
- raw completions,
- secrets,
- API keys,
- OAuth tokens,
- customer conversations,
- full provider payloads.

If a review decision depends on Restricted evidence, the ledger should record a restricted reference and reason without exposing the restricted content.

## Observability Requirements

Minimum ledger observability:

- ledger entry created,
- ledger entry rejected by authorization rule,
- review action accepted,
- review action rejected,
- invalid transition rejected,
- snapshot created,
- correlation ID,
- case ID.

Logs must not contain restricted evidence content.

## Acceptance Mapping

This stage supports:

- `AC-08`: one human review action,
- `AC-09`: ledger history,
- `AC-10`: result validation boundary,
- `AC-11`: traceability,
- `AC-14`: sensitive data handling.

It also supports negative tests rejecting:

- ledger mutation,
- AI approval,
- connector approval,
- admin-as-approver by default,
- realized value before validation,
- historical entry rewriting.

## Stage Pass Criteria

Stage 08 is complete when:

1. append-only ledger behavior is explicit;
2. allowed event names are listed;
3. approval, rejection and deferral authorities are defined;
4. implementation marking and result validation are representable;
5. review snapshots preserve evidence, ROI, assumptions and recommendation context;
6. AI and connectors have no review authority;
7. realized Business Value remains blocked until result validation.

## Stop Conditions

Stop and return to CTO Agent if any proposal requires:

- mutable historical ledger entries,
- AI approval,
- connector approval,
- autonomous provider execution,
- ledger-owned ROI calculation,
- ledger-owned recommendation creation,
- full workflow engine,
- event sourcing infrastructure,
- broad audit subsystem beyond the MVP ledger,
- realized value without validation.

## Handoff To Stage 09

Stage 09 may design the Decision Review Workspace around these states, actions and visibility rules.

Stage 09 must not invent additional review actions or show realized value before result validation.
