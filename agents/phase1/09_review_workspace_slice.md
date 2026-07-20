# Phase 1 - Stage 09: Decision Review Workspace Slice

## Purpose

Define the first operational screen for Phase 1: the Decision Review Workspace for `DRC-AOA-001`.

This screen must help an authorized human answer:

```text
Can we trust and approve this recovery action?
```

This stage does not create UI code, components, routes, CSS, screenshots, dashboards or design assets.

## Authority Inputs

This stage is governed by:

1. `agents/phase1/02_acceptance_contract.md`
2. `agents/phase1/06_domain_roi_recommendation_slice.md`
3. `agents/phase1/07_ai_explanation_slice.md`
4. `agents/phase1/08_ledger_approval_slice.md`
5. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
6. `docs/product/28_Identity_Access_Approval_Model.md`
7. `docs/product/API_SPECIFICATION.md`
8. `docs/architecture/27_Quality_Attributes.md`
9. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
10. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
11. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

If there is conflict, the product screen contract and identity model control visible actions and role behavior.

## Stage Decision

Phase 1 implements one review workspace, not a broad executive dashboard.

The workspace is case-first, evidence-backed and action-oriented.

## Screen Scope

Allowed screen:

```text
Decision Review Workspace for one Decision ROI Case
```

Deferred screens:

- executive portfolio dashboard,
- connector marketplace,
- policy management,
- organization administration,
- analytics explorer,
- multi-case recommendation queue,
- AI chat workspace,
- public API console,
- SDK developer portal,
- full settings area.

## Required Screen Sections

| Section | Purpose |
| --- | --- |
| Case Header | Shows case title, state, review period, owner and approver path. |
| Recommendation Panel | Shows exactly one deterministic recommendation. |
| ROI Panel | Shows current monthly cost, projected monthly cost, estimated monthly recovery and annualized recovery. |
| Evidence Panel | Shows evidence IDs, summaries, freshness, confidence and sensitivity-safe references. |
| AI Explanation Panel | Shows AI-generated explanation when available. |
| Assumptions Panel | Shows ROI and risk assumptions explicitly. |
| Review Actions | Allows authorized approve, reject or defer action. |
| Ledger Timeline | Shows append-only review history for the case. |
| System Status Strip | Shows minimal readiness, health or explanation status relevant to the case. |

No section may claim realized Business Value before result validation.

## Must Show

The workspace must show:

- `DRC-AOA-001`,
- AI Onboarding Assistant Recovery,
- review period 2026-06,
- review date 2026-07-18,
- current monthly cost EUR2,340,
- projected monthly cost after action EUR720,
- estimated monthly recovery EUR1,620,
- annualized recovery EUR19,440,
- confidence 92/100,
- risk label,
- one recommendation,
- evidence IDs,
- visible assumptions,
- current review state,
- authorized next actions.

## Must Hide Or Redact

The workspace must not show:

- raw prompts,
- raw completions,
- secrets,
- API keys,
- OAuth tokens,
- customer conversations,
- full provider payloads,
- Restricted evidence content,
- hidden internal prompt instructions,
- unknown-sensitivity evidence content.

Restricted evidence may be represented by an evidence ID and access warning.

## Blocked States

| Blocker | Workspace Behavior |
| --- | --- |
| Missing cost evidence | ROI Panel shows blocked state; approval disabled. |
| Missing owner or approver | Review Actions disabled. |
| Unknown sensitivity | AI Explanation and approval disabled until reviewed. |
| Missing quality evidence | Risk cannot be Low; show Medium or defer guidance. |
| AI explanation unavailable | Recommendation remains visible; explanation shows unavailable state. |
| Unauthorized actor | Actions hidden or disabled with clear role boundary. |
| Invalid transition | Action rejected and ledger not mutated. |

## Action Matrix

| Action | When Enabled | Ledger Result |
| --- | --- | --- |
| Approve | Recommendation ready, approver role, no active blocker | `review_approved` |
| Reject | Approver role, reason provided | `review_rejected` |
| Defer | Authorized role for blocker, reason provided | `review_deferred` |
| Mark Implemented | Approved state, Technical Owner role | `implementation_marked` |
| Validate Result | Implementation marked, FinOps or authorized reviewer role | `result_validated` |

The first MVP screen must support at least approve, reject or defer for the demo.

Implementation marking and result validation must be representable even if real result validation is not part of the first demo.

## Role Visibility

| Role | Expected Workspace Ability |
| --- | --- |
| `ADMIN` | View review context, approve, reject or defer in Phase 1 demo, manage local demo users/imports. |
| `PLATFORM_ENGINEER` | View technical context, defer for feasibility, mark implemented after approval. |
| `FINANCE` | View cost and ROI context, defer for cost evidence, validate result after implementation. |
| `AUDITOR` | View ledger and evidence traceability, read-only by default, optionally defer audit/security blockers. |
| AI | No UI authority. |
| Connector | No UI authority. |

`ADMIN` is not a permanent enterprise approver model; future identity providers map external users to product roles.

## UX Rules For MVP

The MVP workspace should be operational and compact.

It should prioritize:

- decision clarity,
- evidence traceability,
- ROI visibility,
- explicit assumptions,
- action readiness,
- ledger history.

It should avoid:

- marketing hero sections,
- broad analytics pages,
- decorative surfaces,
- multi-case navigation as the main experience,
- speculative future modules,
- AI chat as the primary workflow.

## API Intent Mapping

The conceptual API surface needed by this screen remains narrow:

- get one case,
- get evidence summaries for one case,
- get ROI View for one case,
- get recommendation for one case,
- get AI explanation for one case,
- get ledger history for one case,
- submit approve, reject or defer action.

No public API, SDK, partner API or bulk workflow API is required for Phase 1.

## Acceptance Mapping

This stage supports:

- `AC-01`: authenticated reviewer can access the case;
- `AC-05`: ROI View is visible;
- `AC-06`: one recommendation is visible;
- `AC-07`: AI explanation is visible when available;
- `AC-08`: review action is possible;
- `AC-09`: ledger history is visible;
- `AC-10`: realized value is not claimed before validation;
- `AC-14`: restricted data is hidden or blocked.

It also supports negative tests rejecting:

- broad dashboards,
- exposed Restricted evidence,
- invented actions,
- realized value before validation,
- public API or SDK expansion.

## Stage Pass Criteria

Stage 09 is complete when:

1. the first screen is a one-case Decision Review Workspace;
2. required sections are defined;
3. blocked states are defined;
4. action enablement follows role and state rules;
5. sensitive data is hidden or redacted;
6. realized Business Value remains blocked until validation;
7. the screen can support the integrated demo without broad dashboard expansion.

## Stop Conditions

Stop and return to CTO Agent if any proposal requires:

- executive portfolio dashboard as the first screen,
- multiple Decision ROI Cases,
- recommendation queue,
- AI chat as primary interface,
- unrestricted evidence viewing,
- connector settings UI,
- policy engine UI,
- public API console,
- SDK portal,
- realized value before result validation.

## Handoff To Stage 10

Stage 10 must define the minimum auth, security and observability needed to make this workspace safe and demoable.

Stage 10 must not expand into enterprise SSO or full observability platform scope.
