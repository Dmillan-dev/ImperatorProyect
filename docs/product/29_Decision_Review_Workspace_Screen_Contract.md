# 29 - Decision Review Workspace Screen Contract

## Purpose

Define the first operational MVP screen for IMPERATOR before writing implementation code.

This document answers exactly what the Decision Review Workspace:

- shows,
- hides,
- blocks,
- records,
- allows by role,
- refuses when evidence, ROI, security or authority is incomplete.

It is a Phase 0 product contract. It does not define visual design, React components, routes, CSS, API handlers, database schema, services, connectors, migrations, generated bindings, Docker, Kubernetes, Terraform or runtime infrastructure.

## Architect Coherence Check

Verdict: the project still follows the logical MVP script.

The current documentation sequence is coherent:

```text
Vision and Positioning
-> MVP Blueprint
-> MVP Vertical Slice
-> MVP ROI Slice
-> Manual Evidence Pack
-> Security and Data Governance
-> MVP Acceptance Test Plan
-> Identity, Access and Approval Model
-> Decision Review Workspace Screen Contract
```

There is no logical drift if the first product surface remains a review surface for one Decision ROI Case, not a broad executive dashboard.

| Area | Status | Screen-contract implication |
|---|---|---|
| Vision | Coherent | IMPERATOR manages decisions, so the screen must review a decision, not monitor systems. |
| MVP boundary | Coherent | One Decision. One Timeline. One ROI. |
| Domain model | Coherent | The screen revolves around Decision ROI Case, Evidence, ROI, Recommendation, Approval and Ledger. |
| Evidence pack | Coherent | `DRC-AOA-001` gives enough manual evidence for a screen contract. |
| ROI model | Coherent | Estimated recovery and realized value remain separate. |
| Security model | Coherent | Raw provider payloads and Restricted data are not shown. |
| Identity model | Coherent | Human authority is explicit before actions appear. |
| Acceptance plan | Coherent | Suite H now has a concrete screen behavior contract. |
| Project structure | Coherent | No code or service scaffolding is needed yet. |

The Phase 0 architecture gaps after this document are now closed for non-functional quality expectations and per-connector MVP contracts through `docs/architecture/27_Quality_Attributes.md` and `docs/architecture/28_Per_Connector_MVP_Contracts.md`.

## Canonical References

- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
- `docs/product/24_MVP_Vertical_Slice.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/21_Technical_Architecture_Context.md`
- `docs/architecture/24_MVP_Project_Structure.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`

## Product Question

The screen answers one question:

**Can we trust and approve this recovery action?**

Everything visible in the workspace must support that question.

If a UI block does not help a reviewer trust, approve, reject, defer, implement or validate the Decision ROI Case, it does not belong in the MVP screen.

## MVP Screen Identity

| Field | Value |
|---|---|
| Screen name | Decision Review Workspace |
| First case | `DRC-AOA-001` |
| First slice | AI Onboarding Assistant Recovery |
| Primary recommendation | AI model downgrade or model change |
| Primary buyer/reviewer | CTO or VP Engineering |
| Supporting reviewers | Business Owner, Platform Lead, FinOps, Security |
| Primary output | Ledger-backed approve, reject, defer, implementation or result-validation entry |
| Product category | Enterprise Decision Intelligence Platform |

## Scope

### In Scope

- One Decision ROI Case review surface.
- Decision summary.
- Evidence chain.
- Timeline.
- Current cost.
- ROI assumptions.
- Estimated recovery.
- Usage or value signal.
- Risk and confidence.
- Recommendation rationale.
- Role-aware action controls.
- Approval, rejection, deferral, implementation and validation behavior.
- Ledger history for the current Decision ROI Case.
- Missing evidence and blocker states.
- Security and visibility rules.

### Out Of Scope

- Full Executive Workspace.
- Multi-case portfolio dashboard.
- Standalone Decision Ledger product page.
- Business Value page with accumulated realized value.
- Integration configuration page.
- Policy-engine admin page.
- Connector health operations console.
- Public API or SDK surface.
- AI Advisor conversation surface.
- Autonomous execution.
- Provider-side write actions.
- Raw provider payload exploration.
- Production UI design system.

## Screen State Model

| Screen state | Meaning | Required facts | Primary actions | Ledger relation |
|---|---|---|---|---|
| `draft_case` | Case exists but is not review-ready | Partial evidence or ROI | Request evidence, defer if reviewer has authority | No approval entry |
| `not_reviewable` | Required owner, approver, evidence, ROI assumption or security classification is missing | Blocking gap | Request evidence, defer with reason | Optional `evidence_requested` or `deferred` |
| `review_ready` | Evidence, ROI, assumptions, owner and approver are present | Readiness gates pass | Approve, reject, defer | `recommendation_created` must be representable |
| `deferred` | Reviewer needs evidence, time or security review | Deferral reason or review date | Resolve blocker, review again | `deferred` |
| `approved` | Authorized human approved the recommendation | Evidence, ROI and assumptions snapshots | Mark implementation complete | `approved` |
| `rejected` | Authorized human rejected the recommendation | Rejection reason | Close or create a new recommendation later | `rejected` |
| `implemented` | Approved action was implemented outside IMPERATOR | Implementation note and evidence | Validate result | `implementation_marked` |
| `validated` | Realized value was validated | Post-action evidence and validation period | Close case, feed Business Value | `result_validated` |

State rule:

The screen may display future lifecycle slots, but it must not pretend that implementation or realized value already exists before the corresponding ledger entry exists.

## User Modes

| Role | Primary mode | Can see | Can do | Must not do |
|---|---|---|---|---|
| CTO / VP Engineering | Final review | Full relevant summary, evidence, ROI, risk, recommendation and ledger | Approve, reject, defer, acknowledge implementation/result | Execute provider changes or bypass missing evidence |
| Business Owner | Business impact review | Decision purpose, usage/value signal, relevant evidence, risk | Confirm impact, object, defer for business evidence | Final-approve by default |
| Platform Lead | Technical review | Technical evidence, implementation path, cost context, risk | Confirm feasibility, defer, mark implementation complete | Validate financial result alone |
| FinOps / Finance Owner | ROI review | Cost evidence, assumptions, ROI, estimated and realized value | Confirm ROI, defer for cost evidence, validate result | Final-approve by default |
| Security / Compliance Reviewer | Safety review | Sensitivity, evidence exposure, AI boundary and relevant summaries | Defer for sensitivity or governance risk | Approve business action by default |
| Engineer | Supporting evidence | Assigned technical evidence summaries | Provide implementation evidence | Approve, validate or view unrelated Confidential data |
| Executive | Impact reader | Executive summary and relevant business impact | Request review or acknowledge | Final-approve unless also CTO/VP Eng or delegated |
| Admin | Configuration operator later | Configuration context if authorized | Manage future configuration | Business approval by role alone |
| Viewer | Limited reader | Allowed summaries only | Read | View Confidential evidence or act |
| AI Component | Advisory explanation | Prepared, filtered context only | Explain with evidence IDs | Approve, reject, defer, implement, validate or see raw data |

## Information Architecture

The MVP workspace should contain these blocks in this order.

### 1. Decision Header

Must show:

- Decision ROI Case ID.
- Decision title.
- Current screen state.
- Review period.
- Business owner.
- Technical owner.
- Final approver.
- Recommendation summary.
- Risk level.
- Confidence score.

Must not show:

- unrelated portfolio metrics,
- generic cloud charts,
- provider dashboards,
- accumulated Business Value before validation.

### 2. Readiness And Blockers

Must show:

- approval readiness gates,
- missing evidence,
- missing owner or approver,
- unresolved sensitivity review,
- stale evidence warning,
- reason why approve is disabled when blocked.

The reviewer should never need to infer why an action is unavailable.

### 3. Decision Timeline

Must show the ordered lifecycle:

```text
Business Need
-> Decision Created
-> Implementation
-> Deployment
-> AI Consumption
-> Impact Analysis
-> Recommendation
-> Approval / Rejection / Deferral
-> Implementation Marked
-> Result Validation
```

Each timeline item must reference evidence, a date or period and a source summary.

### 4. Evidence Chain

Must show:

- evidence ID,
- source system,
- observed fact,
- source object reference,
- period or timestamp,
- sensitivity,
- confidence contribution,
- whether the full detail is hidden for the current role.

Must hide:

- raw Jira payloads,
- raw GitHub source code by default,
- raw AWS exports,
- raw prompts,
- raw completions,
- customer conversations,
- secrets,
- credentials.

### 5. Cost And ROI

Must show:

- AWS monthly cost separately,
- AI monthly cost separately,
- total current monthly cost,
- projected monthly cost,
- estimated monthly recovery,
- estimated annualized recovery,
- labor recovery separately if shown,
- assumptions behind each number,
- evidence references behind each number.

Must not:

- merge labor recovery into direct-spend recovery without labeling,
- count estimated recovery as realized value,
- show ROI without assumptions.

### 6. Usage And Business Value Signal

Must show:

- active users or usage signal,
- value proxy limitation,
- quality or business-risk note when relevant,
- statement that realized value is unavailable until validation.

The MVP may show estimated opportunity. It must not claim validated recovered value until `result_validated` exists.

### 7. Recommendation Review

Must show:

- recommended action,
- recommendation family,
- rationale,
- expected saving,
- risk,
- confidence,
- fallback or rollback idea if documented,
- owner,
- approval path,
- evidence IDs and assumptions used.

Must not:

- present AI text as financial proof,
- hide missing assumptions,
- suggest autonomous execution.

### 8. Review Action Bar

Visible actions depend on role and state:

- Approve.
- Reject.
- Defer.
- Request evidence.
- Mark implementation complete.
- Validate result.

There must be no `Execute`, `Auto-fix`, `Apply in AWS`, `Change model now` or provider-side mutation action in the MVP screen.

### 9. Ledger History

Must show:

- ordered ledger entries for the current Decision ROI Case,
- entry type,
- actor,
- actor role,
- timestamp,
- reason or note,
- estimated saving snapshot,
- realized saving only when validated,
- evidence, ROI and assumptions snapshot references.

Must not:

- mutate historical entries,
- silently replace old snapshots with newer evidence,
- display raw provider payloads.

### 10. Security And AI Boundary Indicators

Must show:

- sensitivity summary,
- hidden evidence count if role filtering hides items,
- Restricted-data exclusion warning if relevant,
- AI-readable context status if AI explanation is used later.

The screen must make it clear that AI reads prepared context only.

## Field Contract

| Field or block | Required in MVP | Source | Sensitivity default | Visible to | Snapshot required |
|---|---|---|---|---|---|
| `decision_roi_case_id` | Yes | Decision ROI Case | Internal | All assigned roles, Viewer limited | Yes |
| `decision_title` | Yes | Jira/manual evidence | Internal | All assigned roles | Yes |
| `business_need_summary` | Yes | `E-JIRA-001` | Internal | All assigned roles | Yes |
| `business_owner` | Yes | `E-OWNER-001` | Confidential | CTO, Business Owner, Platform, FinOps | Yes |
| `technical_owner` | Yes | `E-OWNER-002` | Confidential | CTO, Platform, FinOps, Security | Yes |
| `final_approver` | Yes | `E-OWNER-003` | Confidential | CTO, Business Owner, Platform, FinOps | Yes |
| `review_period` | Yes | Evidence pack | Internal | All assigned roles | Yes |
| `screen_state` | Yes | Decision/ledger state | Internal | All assigned roles | Yes |
| `recommended_action` | Yes | Recommendation rationale | Internal | All assigned roles | Yes |
| `aws_monthly_cost` | Yes | `E-AWS-001` | Confidential | CTO, Platform, FinOps, Security if reviewing | Yes |
| `ai_monthly_cost` | Yes | `E-AI-001` | Confidential | CTO, Platform, FinOps, Security if reviewing | Yes |
| `current_monthly_cost` | Yes | ROI view | Confidential | CTO, Platform, FinOps, Business Owner summary | Yes |
| `projected_monthly_cost` | Yes | ROI assumptions | Confidential | CTO, Platform, FinOps | Yes |
| `estimated_monthly_recovery` | Yes | ROI calculation | Confidential | CTO, Platform, FinOps, Business Owner summary | Yes |
| `estimated_annualized_recovery` | Yes | ROI calculation | Confidential | CTO, Platform, FinOps, Business Owner summary | Yes |
| `labor_recovery_per_case` | Optional but documented | ROI slice | Internal or Confidential | CTO, FinOps, Business Owner | If used |
| `realized_saving` | No before validation | Result validation | Confidential | CTO, FinOps, Business Owner summary | Yes when validated |
| `roi_assumptions` | Yes | ROI slice | Internal or Confidential | CTO, Platform, FinOps, Business Owner summary | Yes |
| `confidence_score` | Yes | Evidence contribution | Internal | All assigned roles | Yes |
| `risk_level` | Yes | Recommendation/risk review | Internal | All assigned roles | Yes |
| `usage_signal` | Yes | `E-USAGE-*` | Confidential | CTO, Business Owner, Platform, FinOps | Yes |
| `quality_review_note` | Required for low-risk claim | `E-AI-005` | Confidential | CTO, Platform, Business Owner, FinOps if ROI depends on it | Yes |
| `evidence_items` | Yes | Evidence pack | Mixed | Role-filtered | Yes |
| `sensitivity_summary` | Yes | Security model | Internal | All assigned roles | Yes |
| `ledger_entries` | Yes | Decision Ledger | Internal or Confidential | Role-filtered | Already ledger |
| `blockers` | Yes when present | Readiness gates | Internal | All assigned roles | Yes if action recorded |
| `allowed_actions` | Yes | Identity model | Internal | Current actor | No |

## Visibility Rules

| Block | Default visibility | Restricted behavior |
|---|---|---|
| Decision summary | Visible to assigned roles; limited for Viewer | Hide owner detail from Viewer if Confidential |
| Timeline | Visible as summaries | Hide raw source payloads and sensitive actor details |
| Evidence chain | Role-filtered summaries | Hide Confidential details from unauthorized users; never show Restricted data |
| Cost and ROI | Visible to CTO, Platform and FinOps; summarized for Business Owner | Hide detailed cost rows from Viewer |
| Usage/value signal | Visible to business and review roles | Aggregate individual user data |
| Recommendation | Visible to assigned reviewers | Hide evidence details the actor cannot access |
| Action bar | Visible only when an actor has possible actions | Disable unauthorized or unsafe actions |
| Ledger history | Role-filtered | Preserve meaning without revealing Restricted data |
| AI explanation | Future optional | Prepared context only; cite evidence IDs |

Visibility rule:

If the screen hides evidence, it should say that evidence is hidden because of role or sensitivity. It should not pretend the evidence does not exist.

## Blocking Rules

Approve must be blocked when any of these conditions are true:

| ID | Blocker | Required screen behavior |
|---|---|---|
| B01 | Business owner missing | Show blocker; approve disabled |
| B02 | Technical owner missing | Show blocker; approve disabled |
| B03 | Final approver missing | Show blocker; approve disabled |
| B04 | Required Jira evidence missing | Show missing business context |
| B05 | Required GitHub implementation evidence missing | Show missing technical chain |
| B06 | AWS cost evidence missing | Show ROI incomplete |
| B07 | AI usage or cost evidence missing | Show model-change recommendation not credible |
| B08 | ROI assumptions missing | Hide approval readiness; show assumptions required |
| B09 | Quality review note missing for low-risk claim | Raise risk and suggest deferral |
| B10 | Evidence sensitivity unknown | Treat as Confidential until reviewed |
| B11 | Restricted data appears in evidence | Block review and require security deferral or redaction |
| B12 | User lacks authority | Hide or disable action with role reason |
| B13 | Approval note missing | Keep approve disabled |
| B14 | Evidence, ROI or assumptions snapshot unavailable | Block ledger action |
| B15 | Estimated and realized value are mixed | Block Business Value claim |
| B16 | Implementation attempted before approval | Block mark-implemented |
| B17 | Result validation attempted before post-action evidence | Block validate-result |

Blocking rule:

The screen may still allow an authorized reviewer to defer the case with a reason and required evidence. It must not allow approval through a blocker.

## Action Contracts

### Approve

| Contract item | Rule |
|---|---|
| Enabled when | Case is `review_ready`, actor is CTO/VP Engineering or explicit delegated approver, snapshots exist |
| Required input | Approval note and accepted assumptions |
| Records | `approved` ledger entry |
| Snapshot | Evidence, ROI, assumptions, risk, confidence, owner and approver |
| Does not do | Execute cloud, code or AI-provider change |

### Reject

| Contract item | Rule |
|---|---|
| Enabled when | Actor is CTO/VP Engineering or explicit delegated approver |
| Required input | Rejection reason |
| Records | `rejected` ledger entry |
| Snapshot | Evidence and ROI reviewed at rejection time |
| Does not do | Delete recommendation history |

### Defer

| Contract item | Rule |
|---|---|
| Enabled when | Actor is CTO/VP Engineering, Business Owner, Platform Lead, FinOps or Security with a relevant gap |
| Required input | Deferral reason and required evidence or review date |
| Records | `deferred` ledger entry |
| Snapshot | Current evidence, ROI, assumptions, confidence and blocker |
| Does not do | Count as rejection or approval |

### Request Evidence

| Contract item | Rule |
|---|---|
| Enabled when | Required reviewer identifies an evidence gap |
| Required input | Missing evidence description and owner if known |
| Records | `evidence_requested` if represented in ledger, otherwise screen blocker state |
| Snapshot | Current blocker and requester role |
| Does not do | Mark case as review-ready |

### Mark Implementation Complete

| Contract item | Rule |
|---|---|
| Enabled when | Case is `approved`, actor is Platform Lead or delegated technical owner |
| Required input | Implementation note, implementation period and implementation evidence |
| Records | `implementation_marked` ledger entry |
| Snapshot | Prior approval link and implementation evidence |
| Does not do | Execute provider changes |

### Validate Result

| Contract item | Rule |
|---|---|
| Enabled when | Case is `implemented`, actor is FinOps / Finance Owner |
| Required input | Validation period, post-action cost evidence, realized saving, variance and outcome note |
| Records | `result_validated` ledger entry |
| Snapshot | Validation evidence and realized value |
| Does not do | Rewrite the original estimate |

## Empty And Error States

| State | Message intent | Required behavior |
|---|---|---|
| Case not found | The decision cannot be loaded | Do not show fabricated ROI or evidence |
| Unauthorized actor | The current actor cannot view this case | Show limited access message |
| No Jira evidence | Business origin is missing | Block approval readiness |
| No GitHub evidence | Implementation chain is missing | Block technical trust |
| No AWS cost evidence | Cloud cost is missing | Block full ROI |
| No AI cost evidence | AI consumption is missing | Block model-change recommendation |
| No usage signal | Value context is weak | Lower confidence or defer |
| No quality note | Low-risk claim is unsupported | Raise risk or defer |
| No owner | Accountability is missing | Block approval readiness |
| No approver | Final authority is missing | Block approval |
| Stale evidence | Evidence period is outdated | Show freshness warning and lower confidence |
| Restricted data detected | Unsafe evidence | Hide item and require security review |
| No ledger entries yet | No decision history exists | Show empty ledger history, not fake events |
| No realized value yet | Outcome not validated | Show estimated value only |

## Recording Rules

The screen records business actions through Decision Ledger v2.

Every ledger-producing action must include:

- actor ID,
- actor role,
- organization or tenant boundary later,
- timestamp,
- Decision ROI Case ID,
- recommendation ID when applicable,
- reason or note,
- evidence snapshot reference,
- ROI snapshot reference,
- assumptions snapshot reference,
- risk and confidence snapshot when relevant.

The screen must never record:

- secrets,
- credentials,
- raw prompts,
- raw completions,
- customer conversations,
- raw provider payloads,
- hidden evidence contents inside unauthorized views.

Future implementation should also audit security-relevant access attempts, especially attempts to view hidden Confidential evidence or any Restricted data. This audit is separate from the business Decision Ledger.

## Acceptance Alignment

This document locks the behavior behind Suite H in `docs/product/27_MVP_Acceptance_Test_Plan.md`.

| Acceptance scenario | Screen contract mapping |
|---|---|
| H01 - Decision summary | Decision Header |
| H02 - Timeline | Decision Timeline |
| H03 - Evidence chain | Evidence Chain and Visibility Rules |
| H04 - Current cost | Cost And ROI |
| H05 - Recovery estimate | Cost And ROI plus ROI assumptions |
| H06 - Usage/value signal | Usage And Business Value Signal |
| H07 - Recommendation | Recommendation Review |
| H08 - Approval controls | Review Action Bar and Action Contracts |
| H09 - Ledger history | Ledger History and Recording Rules |
| H10 - Business Value | No realized value before result validation |

It also supports:

- Suite C for ROI explainability,
- Suite E for ledger actions,
- Suite F for evidence visibility and AI boundaries,
- Suite G for conceptual API intent.

## Conceptual API Implication

Future implementation can support this screen with the smallest conceptual API subset:

- `GET /decisions/{decisionId}`
- `GET /decisions/{decisionId}/timeline`
- `GET /decisions/{decisionId}/evidence`
- `GET /decisions/{decisionId}/roi`
- `GET /recommendations/{recommendationId}`
- `GET /decisions/{decisionId}/ledger`
- `POST /decisions/{decisionId}/ledger/approve`
- `POST /decisions/{decisionId}/ledger/reject`
- `POST /decisions/{decisionId}/ledger/defer`
- `POST /decisions/{decisionId}/ledger/mark-implemented`
- `POST /decisions/{decisionId}/ledger/validate-result`

This is a product contract only. It does not authorize OpenAPI, protobuf, handlers, services or generated clients during Phase 0.

## Future Frontend Implication

When implementation is explicitly approved, the screen can map to the future frontend structure from `docs/architecture/24_MVP_Project_Structure.md`:

- `decision-review`
- `evidence-chain`
- `roi-summary`
- `recommendation-review`
- `ledger-history`

These names are planning guidance, not a command to create folders now.

## Negative Rules

Reject any future MVP design that:

- starts with Executive Workspace instead of Decision Review Workspace,
- turns the first screen into a generic dashboard,
- shows multiple unrelated decisions as the core proof,
- shows ROI without assumptions,
- counts estimated recovery as realized Business Value,
- hides why approval is blocked,
- lets AI approve, reject, defer, implement or validate,
- includes an execute button,
- requires connector write permissions,
- exposes raw prompts, raw completions, secrets or customer conversations,
- stores raw provider payloads in the ledger,
- lets Admin approve by role alone,
- lets FinOps validate realized value before implementation,
- mutates old ledger snapshots after new evidence appears.

## Manual Screen Review Checklist

Before Phase 1 UI design, manually verify:

1. A reviewer can identify the decision in under one minute.
2. The screen shows why the decision exists.
3. The screen shows the timeline from business need to recommendation.
4. The screen shows the evidence chain with sensitivity.
5. Every ROI number has evidence or assumptions.
6. Estimated value is separate from realized value.
7. The recommendation shows action, risk, confidence and owner.
8. Approval, rejection and deferral are role-aware.
9. Missing evidence creates visible blockers.
10. Ledger history explains what was reviewed and decided.
11. No raw provider data or Restricted data is needed to trust the case.
12. There is no autonomous execution path.

## Companion Documents

The companion quality artifact is:

`docs/architecture/27_Quality_Attributes.md`

Status: created.

Reason:

The project needed to define quality attributes such as explainability, auditability, freshness, latency, reliability, reversibility, observability and performance non-goals before implementation.

The companion vocabulary artifact is `docs/architecture/29_Event_Evidence_Vocabulary.md`.

Status: created.

The companion Phase 0 control artifact is `docs/rnd/30_RD_Activity_Evidence_Dossier.md`.

Status: created.

The final Phase 0 closure artifact is `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`.

Status: created.
