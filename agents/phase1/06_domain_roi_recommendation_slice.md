# Phase 1 - Stage 06: Domain, ROI And Recommendation Slice

## Purpose

Define the deterministic business flow that turns normalized evidence for `DRC-AOA-001` into one ROI View and one recommendation.

This stage is the heart of the Phase 1 MVP.

It does not define code, services, SQL, API handlers, UI implementation or AI prompts.

## Authority Inputs

This stage is governed by:

1. `agents/phase1/01_product_case_lock.md`
2. `agents/phase1/02_acceptance_contract.md`
3. `agents/phase1/04_data_persistence_slice.md`
4. `agents/phase1/05_evidence_intake_slice.md`
5. `docs/product/CORE_DOMAIN_MODEL.md`
6. `docs/product/25_MVP_ROI_Slice.md`
7. `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
8. `docs/product/27_MVP_Acceptance_Test_Plan.md`
9. `docs/architecture/29_Event_Evidence_Vocabulary.md`
10. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`

If a broader document suggests multiple recommendations, rankings, learning loops or autonomous actions, this stage overrides that behavior for Phase 1.

## Locked Case

| Field | Value |
| --- | --- |
| Case ID | `DRC-AOA-001` |
| Organization | PilotCo SaaS |
| Department | Customer Success / Product |
| Workflow | Customer Onboarding |
| Review period | 2026-06 |
| Review date | 2026-07-18 |
| Business owner | Head of Customer Success |
| Technical owner | Platform Lead |
| FinOps owner | FinOps Owner |
| Approver | CTO or VP Engineering |
| Recommendation family | AI model downgrade/change |
| Review state at creation | Pending human review |

## Deterministic Flow

The Phase 1 domain flow is:

```text
Normalized Evidence
-> Evidence Readiness Check
-> Decision ROI Case
-> ROI View
-> Recommendation Rule
-> Recommendation
-> AI Explanation Request
-> Human Review
```

The LLM appears only after the recommendation exists.

The LLM does not calculate ROI and does not choose the recommendation.

## Domain Responsibilities

The domain layer owns:

- case identity,
- evidence readiness,
- ROI assumptions,
- ROI calculation meaning,
- recommendation rule selection,
- confidence label,
- risk label,
- review readiness state.

The domain layer does not own:

- provider authentication,
- provider payload parsing,
- UI layout,
- AI wording,
- storage technology,
- telemetry export technology,
- user session implementation.

## Evidence Readiness Rules

The case is ready for ROI only when:

1. business context evidence exists;
2. AI cost evidence exists;
3. cloud cost evidence exists;
4. usage or adoption evidence exists;
5. owner and approver evidence exists;
6. sensitivity is not unknown for required evidence;
7. evidence freshness is acceptable or explicitly marked as stale.

The case is ready for Low-risk recommendation only when:

1. ROI readiness passes;
2. quality evidence `E-AI-005` exists;
3. no security blocker is active;
4. no owner or approver blocker is active.

If quality evidence is missing, the recommendation may be prepared as Medium risk or deferred for review, but it must not be presented as Low risk.

## ROI Inputs

Locked Phase 1 ROI values:

| Input | Value | Meaning |
| --- | ---: | --- |
| AWS monthly cost | EUR 410 | Monthly cloud cost for reviewed capability. |
| AI monthly cost | EUR 1,930 | Monthly AI-provider cost for reviewed capability. |
| Current monthly cost | EUR 2,340 | AWS plus AI cost. |
| Projected monthly cost after action | EUR 720 | Estimated monthly cost after model change and fallback policy. |
| Estimated monthly recovery | EUR 1,620 | Expected monthly avoidable cost. |
| Annualized recovery | EUR 19,440 | Estimated monthly recovery multiplied by 12. |
| Labor recovery | EUR 805 | Estimated operational labor recovery for the review period. |
| Active users | 17 | Usage scale signal for proportionality. |
| Confidence | 92/100 | Confidence of recommendation given current evidence. |

Realized saving is unavailable until result validation.

## ROI Formula Meaning

The conceptual formulas are:

```text
current_monthly_cost = aws_monthly_cost + ai_monthly_cost

estimated_monthly_recovery =
  current_monthly_cost - projected_monthly_cost_after_action

annualized_recovery =
  estimated_monthly_recovery * 12
```

For the locked case:

```text
EUR 410 + EUR 1,930 = EUR 2,340

EUR 2,340 - EUR 720 = EUR 1,620

EUR 1,620 * 12 = EUR 19,440
```

These are deterministic calculations from accepted evidence and assumptions.

They are not LLM outputs.

## ROI Assumption Rules

Every ROI value must be traceable to:

- evidence,
- a named assumption,
- the review period,
- the owner who can validate or challenge it.

Allowed assumption types:

- projected model cost after downgrade/change,
- exception or fallback cost,
- usage continuity,
- labor recovery estimate,
- quality-risk condition.

Not allowed:

- untraceable ROI numbers,
- AI-generated financial truth,
- hidden assumptions,
- realized savings before validation,
- future ARR or valuation claims inside Phase 1 acceptance.

## Recommendation Rule

Phase 1 produces exactly one recommendation.

Locked recommendation:

```text
Change the AI Onboarding Assistant to a lower-cost model for standard onboarding requests,
with a high-capability fallback for exception cases, because current AI spend is
disproportionate to observed usage and the projected monthly recovery is EUR 1,620.
```

The rule is valid only when:

1. current monthly cost is known;
2. projected monthly cost is known or explicitly assumed;
3. estimated monthly recovery is positive;
4. business context and usage evidence exist;
5. owner and approver evidence exist;
6. quality evidence either supports Low risk or clearly raises risk.

## Recommendation States

| State | Meaning |
| --- | --- |
| Not Ready | Required evidence or sensitivity review is missing. |
| Ready For Review | ROI and recommendation can be shown to a reviewer. |
| Deferred For Evidence | Reviewer cannot make a responsible decision until missing evidence is added. |
| Approved | Human approver approved the recommendation. |
| Rejected | Human approver rejected the recommendation. |
| Implementation Marked | Technical owner marked the action as implemented. |
| Result Validated | FinOps or authorized reviewer validated realized value. |

State changes after recommendation creation are handled by Stage 08.

## Risk And Confidence Rules

Confidence for the locked case is `92/100`.

Risk is Low only if:

- quality evidence exists,
- projected cost assumption is explicit,
- fallback path exists,
- no sensitivity or security blocker exists.

Risk becomes Medium if:

- quality evidence is missing,
- freshness is stale but accepted,
- projected cost depends on a manual assumption,
- fallback policy is not sufficiently evidenced.

Risk must become Blocked if:

- cost evidence is missing,
- owner or approver path is missing,
- Restricted evidence would be exposed to AI,
- raw prompts or completions are required to justify the recommendation.

## AI Boundary

The AI receives only a prepared explanation context after the deterministic recommendation exists.

The AI must not:

- calculate ROI,
- choose the recommendation,
- rank alternatives,
- create new evidence,
- change confidence,
- change risk,
- approve,
- reject,
- defer,
- mark implementation,
- validate result,
- persist data.

## Acceptance Mapping

This stage supports:

- `AC-04`: one Decision ROI Case,
- `AC-05`: one ROI View,
- `AC-06`: one deterministic recommendation,
- `AC-07`: AI explanation after deterministic recommendation,
- `AC-08`: human review action readiness,
- `AC-11`: traceability from recommendation to evidence and assumptions.

It also supports negative tests rejecting:

- multiple recommendations,
- ranking,
- learning systems,
- AI decision-making,
- AI-calculated ROI,
- realized value before result validation.

## Stage Pass Criteria

Stage 06 is complete when:

1. `DRC-AOA-001` has one deterministic ROI calculation path;
2. all locked ROI numbers are traceable to evidence or assumptions;
3. exactly one recommendation is defined;
4. recommendation readiness depends on evidence, not AI judgment;
5. Low, Medium and Blocked risk conditions are explicit;
6. realized saving remains unavailable until result validation;
7. Stage 07 can explain the recommendation without changing it.

## Stop Conditions

Stop and return to CTO Agent if any proposal requires:

- more than one recommendation,
- recommendation ranking,
- model learning,
- AI-generated ROI,
- AI-selected business action,
- autonomous execution,
- realized-value claims before validation,
- connector-owned recommendation logic,
- ledger-owned ROI calculation.

## Handoff To Stage 07

Stage 07 may create the AI explanation contract using only the prepared deterministic context from this stage.

Stage 07 must not ask the AI to decide, calculate, approve or enrich business truth.

