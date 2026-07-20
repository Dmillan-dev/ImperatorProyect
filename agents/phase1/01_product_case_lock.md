# 01 - Product Case Lock

## Purpose

Lock the operational product brief for `DRC-AOA-001` before any Phase 1 scaffolding is created.

This document converts the existing IMPERATOR project context into one fixed MVP case brief for autonomous agents.

It does not create code, services, connectors, database schema, fixtures, package manifests, generated bindings, credentials, Docker, Kubernetes, Terraform, cloud resources or UI implementation.

## Stage Verdict

**Verdict: LOCKED for Phase 1 MVP product case.**

The first Phase 1 case is:

```text
DRC-AOA-001 - AI Onboarding Assistant Recovery
```

The case is locked only for:

- one Decision ROI Case,
- one evidence chain,
- one ROI View,
- one deterministic recommendation,
- one AI explanation,
- one Decision Review Workspace,
- one ledger history,
- manual/static or file/import evidence first.

It is not approval to build:

- multiple cases,
- multiple recommendations,
- ranking,
- learning,
- broad connectors,
- autonomous execution,
- a full executive dashboard,
- a full integration platform.

## Authority Inputs

This case lock is derived from:

- `agents/phase1/00_context_control.md`
- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
- `docs/product/24_MVP_Vertical_Slice.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/architecture/31_MVP_Implementation_Standard.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`

If this document conflicts with any authority document, stop and hand off to CTO Agent before implementation planning continues.

## One-Page Case Brief

| Field | Locked value |
|---|---|
| Decision ROI Case ID | `DRC-AOA-001` |
| Case name | AI Onboarding Assistant Recovery |
| Business decision | Create an AI assistant for customer onboarding |
| Pilot organization | PilotCo SaaS |
| Department | Customer Success / Product |
| Project | Customer Onboarding |
| Business source | Jira issue `IMP-214` or manual pilot record |
| Implementation source | GitHub PR `#184` and deployment `deploy-2026-04-05` |
| Infrastructure source | AWS cost/resource evidence for `onboarding-assistant-prod` |
| AI consumption source | OpenAI + Anthropic Claude usage export for `onboarding-assistant` |
| Review period | 2026-06 |
| Review date | 2026-07-18 |
| Business owner | Head of Customer Success |
| Technical owner | Platform Lead |
| Financial reviewer | FinOps owner |
| Final approver | CTO or VP Engineering |
| First UI surface | Decision Review Workspace |
| Current state | Review-ready estimate |
| Recommendation family | AI model downgrade or model change |
| Recommendation count | Exactly one |
| Current monthly cost | EUR2,340 |
| AWS monthly cost | EUR410 |
| AI monthly cost | EUR1,930 |
| Projected monthly cost after action | EUR720 |
| Estimated monthly recovery | EUR1,620 |
| Estimated annualized recovery | EUR19,440 |
| Labor recovery per reconstructed decision | EUR805 |
| Usage signal | 17 active users in June 2026 |
| Confidence | 92/100 |
| Risk | Low if quality evidence is accepted; Medium if quality evidence is missing |
| Realized saving | Not available until result validation |

The values above are manual pilot placeholders from the canonical evidence pack. They are not production customer records.

## User Story

As a CTO or VP Engineering reviewing operational spend, I want IMPERATOR to reconstruct why the AI onboarding assistant exists, what it costs, who owns it, what evidence supports the recommendation and what action I can approve, reject or defer, so that I can make one evidence-backed recovery decision without trusting a generic dashboard or an autonomous AI decision.

## Product Question

The first screen and workflow must answer:

**Can we trust and approve this recovery action?**

Everything in Phase 1 must support that question.

If a field, workflow, connector, UI block or AI output does not help answer that question for `DRC-AOA-001`, it is out of scope.

## Locked Narrative

PilotCo SaaS approved a business decision to create an AI assistant for customer onboarding.

The decision is represented by Jira issue `IMP-214`.

Engineering implemented the assistant through GitHub PR `#184` and shipped it through deployment `deploy-2026-04-05`.

The assistant runs on AWS resources associated with `onboarding-assistant-prod` and Customer Onboarding.

During June 2026, the assistant generated EUR410 of AWS cost and EUR1,930 of AI usage cost.

The total current monthly cost is EUR2,340.

Usage evidence shows 17 active users in June 2026. The assistant supports onboarding answers, but it is not mission-critical for every account.

The evidence suggests that routine onboarding tasks may be served by a lower-cost model while keeping the high-cost model available for exceptions or fallback.

IMPERATOR should produce one deterministic recommendation:

> Approve a model downgrade or model change for routine AI onboarding assistant tasks, with high-cost model fallback for exceptions.

IMPERATOR does not execute the model change. The company decides and acts outside IMPERATOR.

## Locked Recommendation

### Recommendation Text

Approve a model downgrade or model change for routine AI onboarding assistant tasks, while keeping the current high-cost model available as fallback for exceptions.

### Recommendation Rationale

This recommendation is review-ready because:

- the business decision origin is known through `E-JIRA-001`,
- the implementation path is known through `E-GH-001`, `E-GH-002` and `E-GH-003`,
- AWS cost is attributed through `E-AWS-001` and `E-AWS-002`,
- AI cost and model usage are attributed through `E-AI-001` to `E-AI-004`,
- quality evidence exists through `E-AI-005`,
- usage signal exists through `E-USAGE-001` to `E-USAGE-003`,
- owner and approver are known through `E-OWNER-001` to `E-OWNER-003`,
- ROI assumptions are visible,
- ledger snapshots can preserve evidence, ROI and assumptions.

### Recommendation Must Include

- action,
- rationale,
- evidence IDs,
- current monthly cost,
- estimated monthly recovery,
- estimated annualized recovery,
- confidence,
- risk,
- business owner,
- technical owner,
- FinOps reviewer,
- final approver,
- result-validation requirement.

### Recommendation Must Not Include

- multiple alternatives,
- ranking,
- model-learning loop,
- autonomous execution,
- provider-side mutation,
- AI-generated financial truth,
- realized value before validation.

## Evidence-To-Claim Table

| Claim | Required evidence | Locked meaning |
|---|---|---|
| The business decision exists | `E-JIRA-001` | Jira issue requests an AI assistant for customer onboarding. |
| The case belongs to Customer Onboarding | `E-JIRA-002` | Jira issue belongs to the Customer Onboarding project and is Done. |
| Business owner is known | `E-JIRA-003`, `E-OWNER-001` | Head of Customer Success owns the decision. |
| Value proxy boundary is known | `E-JIRA-004` | The goal is faster onboarding answers, not direct revenue attribution. |
| Implementation is linked to the decision | `E-GH-001` | GitHub PR `#184` references `IMP-214`. |
| Implementation actor and review exist | `E-GH-002` | PR was merged after review by Platform engineering. |
| Deployment exists | `E-GH-003` | Deployment `deploy-2026-04-05` shipped the assistant. |
| Model selection is technically feasible | `E-GH-004` | Model selection is configurable for low-complexity onboarding tasks. |
| AWS cost is known | `E-AWS-001` | AWS monthly cost is EUR410 for June 2026. |
| AWS cost is attributable | `E-AWS-002` | Resource tags link spend to Customer Onboarding and `IMP-214`. |
| AWS usage supports review | `E-AWS-003` | Utilization is low to moderate relative to provisioned runtime. |
| Technical owner is known | `E-AWS-004`, `E-OWNER-002` | Platform Lead owns the resource group and technical implementation. |
| AI cost is known | `E-AI-001` | AI monthly cost is EUR1,930 for June 2026. |
| High-cost model is used | `E-AI-002` | High-cost model handles most requests. |
| Workload is routine enough to review | `E-AI-003` | Requests are concentrated in low-complexity onboarding tasks. |
| AI usage is attributable | `E-AI-004` | Usage metadata links consumption to the onboarding assistant. |
| Quality risk can be low | `E-AI-005` | Manual review says lower-cost model is acceptable for routine answers. |
| Usage signal exists | `E-USAGE-001` | 17 active users used the assistant in June 2026. |
| Usage is stable but not growing materially | `E-USAGE-002` | Supports optimization instead of expansion. |
| Business criticality is bounded | `E-USAGE-003` | Assistant is useful but not mission-critical for all accounts. |
| Approval path is known | `E-OWNER-003` | CTO or VP Engineering can approve after FinOps review. |

## ROI Lock

| ROI item | Locked value | Evidence or assumption |
|---|---:|---|
| AWS monthly cost | EUR410 | `E-AWS-001` |
| AI monthly cost | EUR1,930 | `E-AI-001` |
| Current monthly cost | EUR2,340 | AWS + AI cost |
| Projected monthly cost after action | EUR720 | `A-ROI-001`, `A-ROI-002`, `E-AI-002`, `E-AI-003`, `E-AI-005` |
| Monthly transition cost | EUR0 | `A-ROI-003` |
| Estimated monthly recovery | EUR1,620 | EUR2,340 - EUR720 - EUR0 |
| Estimated annualized recovery | EUR19,440 | EUR1,620 x 12 |
| Labor recovery per case | EUR805 | `A-LABOR-001`, `A-LABOR-002` |
| Realized saving | Not available | Future result validation evidence |

ROI must remain explainable and evidence-backed.

Estimated recovery may support review. It must not be counted as realized Business Value.

## Assumption Lock

| Assumption ID | Locked assumption | Review implication |
|---|---|---|
| `A-ROI-001` | Routine onboarding tasks can use a lower-cost model without unacceptable quality loss. | Required for low-risk approval. |
| `A-ROI-002` | High-cost model can remain available for exceptions or fallback. | Lowers user and business risk. |
| `A-ROI-003` | Model change has no recurring monthly transition cost. | Affects estimated monthly recovery. |
| `A-ROI-004` | June 2026 is representative enough for initial review. | Affects confidence. |
| `A-LABOR-001` | Baseline manual reconstruction is 12 hours at EUR70/hour. | Supports labor recovery estimate. |
| `A-LABOR-002` | Pilot assessment target is 0.5 hours at EUR70/hour. | Supports labor recovery estimate. |

If `A-ROI-001` or `E-AI-005` is missing, the recommendation must move from low-risk approval-ready to deferred or Medium risk.

## Review Path Lock

| Role | Locked responsibility | Authority limit |
|---|---|---|
| Business Owner | Confirms the assistant still supports onboarding workflow. | Does not final-approve by default. |
| Platform Lead | Confirms model change is technically feasible and marks implementation later if approved. | Does not validate realized financial result alone. |
| FinOps owner | Reviews cost evidence, ROI assumptions and later validates realized saving. | Does not final-approve by default. |
| Security / Compliance Reviewer | Reviews sensitivity and AI-boundary concerns if needed. | Does not approve business action by default. |
| CTO or VP Engineering | Approves, rejects or defers the recommendation. | Does not execute provider changes through IMPERATOR. |
| AI Component | Explains prepared context with evidence IDs. | Cannot approve, reject, defer, implement, validate, persist or decide. |

## Decision Review Workspace Lock

The first product surface is:

```text
Decision Review Workspace
```

Required blocks:

1. Decision header.
2. Readiness and blockers.
3. Decision timeline.
4. Evidence chain.
5. Cost and ROI.
6. Usage and business value signal.
7. Recommendation review.
8. Review action bar.
9. Ledger history.
10. Security and AI boundary indicators.

The workspace must not become:

- broad Executive Workspace,
- multi-case dashboard,
- integration admin page,
- Business Value suite,
- AI Advisor conversation surface,
- provider execution console.

## Ledger Lock

The first case must support the following ledger sequence:

| Sequence | Entry type | State | Required actor |
|---:|---|---|---|
| 1 | `recommendation_created` | `review_ready` | IMPERATOR analyst / system later |
| 2A | `approved` | `approved` | CTO or VP Engineering |
| 2B | `rejected` | `rejected` | CTO or VP Engineering |
| 2C | `deferred` | `deferred` | Authorized reviewer |
| 3 | `implementation_marked` | `implemented` | Platform Lead |
| 4 | `result_validated` | `validated` | FinOps owner |

For the first MVP demonstration, one of `approved`, `rejected` or `deferred` is sufficient to prove review-state recording.

Implementation and result validation must be representable, but real post-action savings are not required to close this product case lock.

## AI Explanation Lock

The AI explanation must:

- use prepared context only,
- cite evidence IDs,
- explain the deterministic recommendation,
- explain assumptions in natural language,
- preserve the distinction between estimated and realized value,
- avoid raw provider payloads,
- avoid raw prompts and completions,
- avoid secrets and customer conversations.

The AI explanation must not:

- generate the recommendation,
- calculate ROI truth,
- modify persistent data,
- create ledger entries,
- approve, reject, defer, mark implementation or validate result.

## Sensitivity Lock

The case can be demonstrated using summaries, counts and source references.

Restricted data is not required.

Do not include:

- secrets,
- credentials,
- raw prompts,
- raw completions,
- customer conversations,
- unnecessary personal data,
- raw provider payloads.

Confidential evidence must remain role-filtered according to `docs/product/28_Identity_Access_Approval_Model.md`.

## Acceptance Seeds For Stage 02

Stage 02 must test at minimum:

| Test seed | Expected outcome |
|---|---|
| Missing `E-JIRA-001` | Case cannot explain why the decision exists. |
| Missing `E-GH-001` | Implementation chain is incomplete. |
| Missing `E-AWS-001` | Current monthly cost is incomplete. |
| Missing `E-AI-001` | Model-change recommendation is not credible. |
| Missing `E-AI-005` | Risk becomes Medium or recommendation is deferred. |
| Missing `E-OWNER-003` | Recommendation cannot be approved. |
| Missing ROI assumptions | ROI cannot be approval-ready. |
| Raw prompt included | Evidence is rejected as Restricted data. |
| AI tries to decide | Rejected; AI explains only. |
| Estimated saving shown as realized value | Rejected; realized value requires result validation. |

## Product Case Pass Criteria

This stage passes only if:

1. `DRC-AOA-001` can be explained in one review session.
2. The case has one owner, one technical owner, one FinOps reviewer and one final approver path.
3. Every ROI number has evidence or assumption support.
4. The recommendation is exactly one deterministic model downgrade/change action.
5. AI explanation is advisory and evidence-citing only.
6. Decision Ledger can preserve the reviewed state.
7. Decision Review Workspace can answer the trust-and-approval question.
8. Restricted data is not needed.
9. No live connector is required before the manual/import path proves the value loop.
10. No post-MVP platform capability is required.

## Product Case Stop Conditions

Stop and return to CTO Agent if:

- the case requires a second recommendation,
- the recommendation family changes,
- the case cannot be reconstructed from the evidence pack,
- ROI cannot be explained with existing assumptions,
- owner or approver becomes unclear,
- a live connector becomes mandatory before product meaning is locked,
- implementation requires raw prompts, raw completions, secrets or customer conversations,
- any agent proposes autonomous execution,
- any agent proposes broad Executive Workspace or platform dashboards for Phase 1.

## Handoff To Stage 02

Next stage:

```text
02 - Acceptance Contract
```

Lead:

- QA Agent

Stage 02 objective:

Convert this locked product case into a Phase 1 acceptance checklist, negative tests, demo pass/fail criteria and traceability map.

Inputs for Stage 02:

- this document,
- `agents/phase1/00_context_control.md`,
- `docs/product/27_MVP_Acceptance_Test_Plan.md`,
- `docs/architecture/27_Quality_Attributes.md`,
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`,
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`,
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`.

Exit condition for Stage 02:

Every future implementation task must map to at least one acceptance criterion, and no acceptance criterion may require post-MVP infrastructure or AI decision-making.
