# 26 - Manual Evidence Pack: AI Onboarding Assistant Recovery

## Purpose

Define the manual evidence pack for the first IMPERATOR MVP slice:

**AI Onboarding Assistant Recovery**

This document proves whether the Domain Model, ROI Slice, Decision Ledger, API intent and Connector Framework fit together before writing code.

It is a Phase 0 validation artifact. It does not create executable fixtures, services, connectors, database schema, migrations, API handlers, generated bindings or production data loaders.

## Canonical References

- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
- `docs/product/24_MVP_Vertical_Slice.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`

## What This Pack Must Prove

This pack is valid only if it can manually reconstruct one complete Decision ROI Case:

```text
Operational Event
-> Connector Intake
-> Normalized Evidence
-> Decision ROI Case
-> ROI View
-> Recommendation
-> Decision Ledger Entry
-> Decision Review Workspace
-> Result Validation
```

The evidence must prove:

- why the decision exists,
- who owns it,
- who implemented it,
- what runs because of it,
- what it costs today,
- what usage or value signal exists,
- which action can recover money,
- who can approve, reject or defer,
- which evidence, ROI and assumptions should be snapshotted in the ledger.

## Case Snapshot

| Field | Manual value |
|---|---|
| Decision ROI Case | AI Onboarding Assistant Recovery |
| Conceptual case ID | `DRC-AOA-001` |
| Business decision | Create an AI assistant for customer onboarding |
| Organization | PilotCo SaaS |
| Department | Customer Success / Product |
| Project | Customer Onboarding |
| Business owner | Head of Customer Success |
| Technical owner | Platform Lead |
| Financial reviewer | FinOps owner |
| Approver | CTO or VP Engineering |
| Review surface | Decision Review Workspace |
| Pilot cost period | 2026-06-01 to 2026-06-30 |
| Review date | 2026-07-18 |
| Recommendation family | AI model downgrade or model change |
| Current state | Review-ready estimate |

The names above are manual pilot placeholders. They are business-context examples, not production customer records.

## Evidence Rules

Each evidence item must preserve:

- evidence ID,
- source domain,
- source system,
- source object ID,
- source timestamp or period,
- observed fact,
- business meaning,
- related Decision ROI Case,
- confidence contribution,
- sensitivity,
- whether raw payload is required.

Raw provider payloads are not the domain object. The domain object is the Evidence item derived from the source signal.

## Sensitivity Levels

| Level | Meaning | MVP handling |
|---|---|---|
| Public | Safe to share broadly | May appear in demos if anonymized |
| Internal | Business or operational context | May appear in internal pilot review |
| Confidential | Cost, ownership, implementation or usage detail | Show only to authorized reviewers |
| Restricted | Secrets, credentials, customer PII, raw prompts or raw conversations | Do not include in this pack |

The MVP pack should use summaries, counts and source references. It should not include raw customer conversations, secrets, credentials, private prompts or sensitive personal data.

## Confidence Contribution Model

Use the ROI confidence model from `docs/product/25_MVP_ROI_Slice.md`.

| Evidence area | Max weight |
|---|---:|
| Business context evidence | 15 |
| GitHub implementation evidence | 15 |
| AWS cost evidence | 25 |
| AI usage and cost evidence | 25 |
| Usage or value signal | 15 |
| Owner and approver clarity | 5 |
| Total | 100 |

This pack targets **92/100 confidence**:

| Evidence area | Score | Reason |
|---|---:|---|
| Business context evidence | 15/15 | Jira decision and business goal are present |
| GitHub implementation evidence | 14/15 | PR and deployment reference are present |
| AWS cost evidence | 24/25 | Cost and resource tags are present |
| AI usage and cost evidence | 24/25 | Model, tokens, requests and cost are present |
| Usage or value signal | 10/15 | Active-user signal exists, but value proxy is still partial |
| Owner and approver clarity | 5/5 | Business owner, technical owner, FinOps and approver are known |
| Total | 92/100 | Review-ready, not automatically approved |

Confidence measures evidence completeness. It is not AI certainty and it is not financial guarantee.

## Evidence Table

### Business Context Evidence

| Evidence ID | Source | Source object | Observed fact | Business meaning | Period/date | Confidence | Sensitivity | Raw payload needed? |
|---|---|---|---|---|---|---:|---|---|
| `E-JIRA-001` | Jira | `IMP-214` | Jira issue requests an AI assistant for customer onboarding | Establishes why the decision exists | 2026-03-04 | 8 | Internal | No |
| `E-JIRA-002` | Jira | `IMP-214` | Issue belongs to Customer Onboarding project and is marked Done | Links decision to project and lifecycle state | 2026-03-04 to 2026-04-05 | 3 | Internal | No |
| `E-JIRA-003` | Jira | `IMP-214` | Business owner is Head of Customer Success | Identifies accountable owner | 2026-03-04 | 2 | Confidential | No |
| `E-JIRA-004` | Manual pilot note | `AOA-BIZ-001` | Expected goal was faster onboarding answers, not direct revenue attribution | Defines value proxy boundary | 2026-03-04 | 2 | Internal | No |

Business context contribution: **15/15**

### Code And Deployment Evidence

| Evidence ID | Source | Source object | Observed fact | Business meaning | Period/date | Confidence | Sensitivity | Raw payload needed? |
|---|---|---|---|---|---|---:|---|---|
| `E-GH-001` | GitHub | PR `#184` | PR title references `IMP-214` and implements onboarding assistant backend path | Connects Jira decision to code change | 2026-04-02 | 7 | Confidential | No |
| `E-GH-002` | GitHub | PR `#184` | PR merged by Platform engineer after review | Establishes implementation actor and review | 2026-04-02 | 3 | Confidential | No |
| `E-GH-003` | GitHub | Deployment `deploy-2026-04-05` | Deployment references onboarding assistant production release | Establishes shipped date | 2026-04-05 | 3 | Internal | No |
| `E-GH-004` | Manual code review note | `AOA-CODE-001` | Model selection is configurable for low-complexity onboarding tasks | Supports feasibility of model change | 2026-07-12 | 1 | Confidential | No |

GitHub implementation contribution: **14/15**

### AWS Infrastructure And Cost Evidence

| Evidence ID | Source | Source object | Observed fact | Business meaning | Period/date | Confidence | Sensitivity | Raw payload needed? |
|---|---|---|---|---|---|---:|---|---|
| `E-AWS-001` | AWS Cost Explorer export | Service/resource group `onboarding-assistant-prod` | AWS monthly cost is EUR410 | Provides infrastructure cost input | 2026-06 | 10 | Confidential | No |
| `E-AWS-002` | AWS resource tags | `project=customer-onboarding`, `jira_ticket=IMP-214` | Resource tags connect AWS spend to Jira decision | Supports attribution to Decision ROI Case | 2026-06 | 7 | Confidential | No |
| `E-AWS-003` | AWS utilization summary | Lambda/API runtime metrics | Resource utilization is low to moderate relative to provisioned runtime | Supports optimization review | 2026-06 | 4 | Confidential | No |
| `E-AWS-004` | AWS billing owner note | `AOA-AWS-OWNER-001` | Platform team owns the resource group | Clarifies technical ownership | 2026-06 | 3 | Confidential | No |

AWS cost contribution: **24/25**

### AI Consumption Evidence

| Evidence ID | Source | Source object | Observed fact | Business meaning | Period/date | Confidence | Sensitivity | Raw payload needed? |
|---|---|---|---|---|---|---:|---|---|
| `E-AI-001` | OpenAI + Anthropic Claude usage export | Application `onboarding-assistant` | AI monthly cost is EUR1,930 | Provides AI cost input | 2026-06 | 10 | Confidential | No |
| `E-AI-002` | OpenAI + Anthropic Claude usage export | Model usage summary | Assistant uses a high-cost model for most requests | Supports model-change recommendation | 2026-06 | 5 | Confidential | No |
| `E-AI-003` | OpenAI + Anthropic Claude usage export | Token/request summary | Requests and token volume are concentrated in low-complexity onboarding tasks | Supports lower-cost model hypothesis | 2026-06 | 4 | Confidential | No |
| `E-AI-004` | AI usage metadata | Application/team tags | Usage metadata links consumption to Customer Onboarding project | Supports attribution to Decision ROI Case | 2026-06 | 3 | Confidential | No |
| `E-AI-005` | Manual quality review | `AOA-QUALITY-001` | Sample review indicates lower-cost model is acceptable for routine onboarding answers | Lowers quality risk | 2026-07-12 | 2 | Confidential | No |

AI usage and cost contribution: **24/25**

### Usage Or Value Signal Evidence

| Evidence ID | Source | Source object | Observed fact | Business meaning | Period/date | Confidence | Sensitivity | Raw payload needed? |
|---|---|---|---|---|---|---:|---|---|
| `E-USAGE-001` | Product analytics or manual usage export | Application `onboarding-assistant` | 17 active users used the assistant in June | Provides usage signal | 2026-06 | 5 | Confidential | No |
| `E-USAGE-002` | Product analytics or manual usage export | Application `onboarding-assistant` | Usage is stable but not growing materially | Suggests cost should be optimized rather than expanded | 2026-06 | 3 | Confidential | No |
| `E-USAGE-003` | Customer Success note | `AOA-CS-001` | Assistant supports onboarding answers but is not mission-critical for all accounts | Supports user/business risk assessment | 2026-07-10 | 2 | Internal | No |

Usage or value signal contribution: **10/15**

### Owner And Approval Evidence

| Evidence ID | Source | Source object | Observed fact | Business meaning | Period/date | Confidence | Sensitivity | Raw payload needed? |
|---|---|---|---|---|---|---:|---|---|
| `E-OWNER-001` | Jira/manual owner note | `IMP-214` | Head of Customer Success owns the business decision | Defines business owner | 2026-03-04 | 2 | Confidential | No |
| `E-OWNER-002` | GitHub/AWS/manual owner note | `AOA-PLATFORM-001` | Platform Lead owns technical implementation and resource group | Defines technical owner | 2026-07-12 | 1 | Confidential | No |
| `E-OWNER-003` | Manual approval policy note | `AOA-APPROVAL-001` | CTO or VP Engineering can approve model change after FinOps review | Defines approval path | 2026-07-18 | 2 | Internal | No |

Owner and approver contribution: **5/5**

## Connector Mapping

| Connector | Source objects | Normalized event | Evidence types supported | Correlation keys |
|---|---|---|---|---|
| Jira | Issue, epic, project, owner, status | `business_context_requested` | Business decision, owner, project, status | `IMP-214`, project key, owner, dates |
| GitHub | PR, commit, review, deployment reference | `code_change_merged` and `deployment_reference_observed` | Implementation evidence, actor, review, shipped date | `IMP-214`, branch name, PR title, deployment name |
| AWS | Cost export, resource tags, utilization summary | `cloud_cost_observed` | Resource cost, usage, ownership, attribution | `jira_ticket=IMP-214`, resource name, project tag, period |
| OpenAI + Anthropic Claude | Usage export, model summary, token/request counts | `ai_usage_observed` | AI model, token/request volume, AI cost, app/team usage | application name, project tag, user/team, period |
| Manual pilot record | Owner note, quality note, approval note | `usage_signal_observed` or `approval_path_observed` | Value proxy, quality risk, approval path | case ID, owner, review date |

The connector boundary is healthy if these mappings can be prepared without changing the core Decision ROI Case model.

## Correlation Chain

```text
Jira IMP-214
-> GitHub PR #184 references IMP-214
-> Deployment deploy-2026-04-05 ships onboarding assistant
-> AWS resource tags reference project/customer-onboarding and IMP-214
-> AI usage metadata references onboarding-assistant application
-> Usage export shows 17 active users
-> ROI view calculates current cost and recovery estimate
-> Recommendation proposes model downgrade/change
-> Ledger preserves evidence, ROI and assumptions snapshots
```

Minimum correlation keys:

- `IMP-214`
- `customer-onboarding`
- `onboarding-assistant`
- `onboarding-assistant-prod`
- review period `2026-06`

## Timeline Reconstruction

| Step | Lifecycle phase | Evidence | Date/period | Product meaning |
|---|---|---|---|---|
| 1 | Business Need | `E-JIRA-001`, `E-JIRA-004` | 2026-03-04 | The company wanted faster onboarding answers |
| 2 | Decision Created | `E-JIRA-002`, `E-JIRA-003` | 2026-03-04 | Decision has project, owner and status |
| 3 | Implementation | `E-GH-001`, `E-GH-002` | 2026-04-02 | Engineering implemented the assistant |
| 4 | Deployment | `E-GH-003`, `E-AWS-002` | 2026-04-05 | The assistant was shipped and linked to AWS resources |
| 5 | AI Consumption | `E-AI-001`, `E-AI-002`, `E-AI-003` | 2026-06 | The assistant consumes high-cost AI usage |
| 6 | Impact Analysis | `E-AWS-001`, `E-USAGE-001`, `E-USAGE-002` | 2026-06 | Cost and usage signal make the case reviewable |
| 7 | Recommendation | ROI View + `E-AI-005` | 2026-07-18 | Model downgrade/change is proposed for approval |
| 8 | Approval | `E-OWNER-003` | 2026-07-18 | CTO/VP Engineering can approve, reject or defer |
| 9 | Implementation | Future ledger entry | Later | Customer acts outside IMPERATOR if approved |
| 10 | Outcome Validation | Future ledger entry | Later | Realized value is recorded only after validation |

## ROI Input Pack

| ROI input | Value | Source evidence | Assumption used? | Notes |
|---|---:|---|---|---|
| AWS monthly cost | EUR410 | `E-AWS-001` | No | Observed June cost |
| AI monthly cost | EUR1,930 | `E-AI-001` | No | Observed June cost |
| Current monthly cost | EUR2,340 | `E-AWS-001`, `E-AI-001` | Formula | AWS monthly cost + AI monthly cost |
| Projected monthly cost after action | EUR720 | `E-AI-002`, `E-AI-003`, `E-AI-005` | `A-ROI-001`, `A-ROI-002` | Assumes lower-cost model covers routine tasks |
| Monthly transition cost | EUR0 | Manual assumption | `A-ROI-003` | Assumes no recurring transition cost after approval |
| Estimated monthly recovery | EUR1,620 | Derived from ROI formula | Formula | 2,340 - 720 - 0 |
| Estimated annualized recovery | EUR19,440 | Derived from ROI formula | Formula | 1,620 x 12 |
| Baseline manual reconstruction effort | 12 hours | `docs/product/25_MVP_ROI_Slice.md` | `A-LABOR-001` | 6 people x 2 hours |
| Pilot assessment effort | 0.5 hours | `docs/product/25_MVP_ROI_Slice.md` | `A-LABOR-002` | 1 person x 30 minutes |
| Labor recovery per case | EUR805 | Derived from canonical labor formula | Formula | EUR840 - EUR35 |
| Realized saving | Not available | Future validation evidence | None yet | Must stay blank until result validation |

## ROI Assumptions

| Assumption ID | Assumption | Impact | Must be visible in review? |
|---|---|---|---|
| `A-ROI-001` | Routine onboarding tasks can use a lower-cost model without unacceptable quality loss | Drives projected AI cost after action | Yes |
| `A-ROI-002` | High-cost model can remain available for exceptions or fallback | Lowers business and user risk | Yes |
| `A-ROI-003` | Model change has no recurring monthly transition cost | Affects monthly recovery | Yes |
| `A-ROI-004` | June 2026 is representative enough for initial review | Affects confidence | Yes |
| `A-LABOR-001` | Baseline manual reconstruction is 12 hours at EUR70/hour | Supports labor recovery estimate | Yes |
| `A-LABOR-002` | Pilot assessment target is 0.5 hours at EUR70/hour | Supports labor recovery estimate | Yes |

If `A-ROI-001` or `E-AI-005` is missing, the recommendation should move from review-ready to deferred.

## ROI View

| Metric | Value |
|---|---:|
| Current monthly cost | EUR2,340 |
| Projected monthly cost after action | EUR720 |
| Estimated monthly recovery | EUR1,620 |
| Estimated annualized recovery | EUR19,440 |
| Labor recovery per reconstructed decision | EUR805 |
| Realized saving | Not available yet |
| Confidence | 92/100 |
| Risk | Low if quality note is accepted; Medium if quality evidence is missing |

Do not add estimated annualized recovery and labor recovery into one blended headline unless the UI clearly separates direct spend recovery from investigation-time recovery.

## Risk Assessment

| Risk dimension | Rating | Evidence | Rationale |
|---|---|---|---|
| Quality risk | Low / Medium | `E-AI-005`, `A-ROI-001` | Low only if sampled quality review is accepted |
| User risk | Low | `E-USAGE-001`, `E-USAGE-003` | 17 active users; assistant is useful but not universal |
| Business risk | Medium | `E-JIRA-004`, `E-USAGE-003` | Onboarding matters, so fallback should remain available |
| Technical risk | Low | `E-GH-004` | Model selection is configurable |
| Evidence risk | Low / Medium | all evidence | Mostly manual/export based; acceptable for Phase 0 validation |

Overall recommendation risk:

**Low with accepted quality evidence. Medium if quality evidence is missing.**

## Recommendation Rationale

### Recommendation

Approve a model downgrade or model change for routine AI onboarding assistant tasks, with high-cost model fallback for exceptions.

### Why This Is Approval-Ready

- The decision origin is known.
- The implementation path is known.
- AWS and AI costs are attributed to the same Decision ROI Case.
- AI cost is the largest cost contributor.
- Usage signal exists and does not justify unchecked high-cost model use.
- Quality evidence exists for routine onboarding answers.
- Owner, technical owner, FinOps reviewer and approver are clear.
- ROI assumptions are visible.
- The ledger can preserve the reviewed evidence, ROI and assumptions snapshots.

### Expected Financial Impact

| Impact | Value |
|---|---:|
| Estimated monthly recovery | EUR1,620 |
| Estimated annualized recovery | EUR19,440 |
| Labor recovery per reconstructed decision | EUR805 |
| Realized recovery | Not recorded yet |

### Approval Path

| Role | Responsibility |
|---|---|
| Business owner | Confirms assistant still supports onboarding workflow |
| Platform Lead | Confirms model change is technically feasible |
| FinOps owner | Reviews cost and recovery estimate |
| CTO or VP Engineering | Approves, rejects or defers the recommendation |

IMPERATOR does not execute the model change. The company approves and acts outside IMPERATOR unless a later phase explicitly authorizes execution.

## Decision Ledger Snapshot Plan

### Evidence Snapshot

The approval ledger entry should preserve references to:

- `E-JIRA-001` to `E-JIRA-004`,
- `E-GH-001` to `E-GH-004`,
- `E-AWS-001` to `E-AWS-004`,
- `E-AI-001` to `E-AI-005`,
- `E-USAGE-001` to `E-USAGE-003`,
- `E-OWNER-001` to `E-OWNER-003`.

The snapshot should preserve source summaries, source object IDs, dates, sensitivity and confidence contribution. It should not require raw provider payloads.

### ROI Snapshot

The approval ledger entry should preserve:

- current monthly cost: EUR2,340,
- projected monthly cost after action: EUR720,
- estimated monthly recovery: EUR1,620,
- estimated annualized recovery: EUR19,440,
- labor recovery per case: EUR805,
- confidence: 92/100,
- risk: Low with accepted quality evidence,
- realized saving: blank until validation.

### Assumptions Snapshot

The approval ledger entry should preserve:

- `A-ROI-001`,
- `A-ROI-002`,
- `A-ROI-003`,
- `A-ROI-004`,
- `A-LABOR-001`,
- `A-LABOR-002`.

### Expected Ledger Sequence

| Sequence | Entry type | State | Actor | Required snapshot | Notes |
|---:|---|---|---|---|---|
| 1 | `recommendation_created` | `review_ready` | IMPERATOR analyst / system later | Evidence + ROI + assumptions | Creates reviewable recommendation |
| 2A | `approved` | `approved` | CTO or VP Engineering | Evidence + ROI + assumptions | If action is approved |
| 2B | `rejected` | `rejected` | CTO or VP Engineering | Evidence + ROI | Requires rejection reason |
| 2C | `deferred` | `deferred` | CTO, VP Engineering or FinOps | Evidence gap + current ROI | Requires missing evidence or review date |
| 3 | `implementation_marked` | `implemented` | Platform Lead | Implementation evidence | Only if company acts outside IMPERATOR |
| 4 | `result_validated` | `validated` | FinOps owner | Validation evidence + variance | Only after post-action period exists |

For the MVP demonstration, one of `2A`, `2B` or `2C` is enough to prove ledger behavior. Realized value remains unavailable until step 4.

## Decision Review Workspace Inputs

| UI block | Evidence or object feeding it |
|---|---|
| Decision summary | Case Snapshot, `E-JIRA-001`, `E-JIRA-003` |
| Timeline | Timeline Reconstruction |
| Evidence chain | Evidence Table |
| Current cost | ROI Input Pack |
| Usage/value signal | `E-USAGE-001`, `E-USAGE-002`, `E-USAGE-003` |
| ROI assumptions | ROI Assumptions |
| Recommendation | Recommendation Rationale |
| Risk/confidence | Confidence Contribution Model, Risk Assessment |
| Approval controls | Approval Path, Ledger Sequence |
| Ledger history | Decision Ledger Snapshot Plan |

The screen should answer one question:

**Can we trust and approve this recovery action?**

## Manual Validation Checklist

Before Phase 1, this pack is acceptable only if:

1. Every evidence item has source, source ID, date or period and observed fact.
2. Every evidence item maps to a domain concept in `CORE_DOMAIN_MODEL.md`.
3. Every source can be explained through the `CONNECTOR_FRAMEWORK.md` boundary.
4. Every ROI number references evidence or an explicit assumption.
5. Estimated recovery and realized recovery remain separate.
6. Recommendation references evidence, ROI, confidence, risk, owner and approver.
7. Approval, rejection and deferral can each create a valid ledger entry.
8. Ledger snapshots can be understood without raw source payloads.
9. Sensitive or restricted data is not required for the demo.
10. The pack can be explained to CTO, Platform and FinOps reviewers in one review session.

## Failure Cases

| Missing item | Result |
|---|---|
| Jira business context missing | Case cannot explain why the decision exists |
| GitHub implementation evidence missing | Timeline is incomplete |
| AWS cost missing | Current monthly cost is incomplete |
| AI usage/cost missing | Model-change recommendation is not credible |
| Usage signal missing | ROI confidence drops; recommendation may be deferred |
| Quality evidence missing | Risk becomes Medium or High; approval should be deferred |
| Owner missing | Recommendation is not approval-ready |
| Approver missing | Ledger can record evidence, but cannot record valid approval |
| ROI assumptions missing | ROI cannot be decision-grade |
| Sensitivity unknown | Evidence should not be exposed to AI or broad reviewers |

## Open Questions

These are now mostly resolved by `docs/architecture/26_Security_Data_Governance_Threat_Model.md`, `docs/product/27_MVP_Acceptance_Test_Plan.md`, `docs/product/28_Identity_Access_Approval_Model.md` and `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

Remaining future-pilot questions:

- What customer-specific quality threshold makes model downgrade acceptable?
- What validation period is acceptable for each customer before realized savings can be recorded?
- How long should evidence summaries and snapshots be retained in production?

## Companion Documents

The companion Phase 0 artifacts are now created:

- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`

Reason:

This pack proves the evidence chain. The companion artifacts define who can act, what is shown, what is blocked, which quality attributes apply, how connectors contribute evidence, which vocabulary is valid and how future development activity should be evidenced.
