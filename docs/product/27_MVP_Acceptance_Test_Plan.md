# 27 - MVP Acceptance Test Plan

## Purpose

Define the acceptance test plan for the first IMPERATOR MVP before writing implementation code.

This document converts the current Phase 0 context into testable scenarios for:

- evidence,
- Decision ROI Case reconstruction,
- ROI,
- recommendation readiness,
- approval, rejection and deferral,
- Decision Ledger snapshots,
- security and data governance,
- Decision Review Workspace behavior.

It is a Phase 0 product and architecture validation artifact. It does not create automated tests, source code, fixtures, services, connectors, APIs, database schema, migrations, generated bindings, Docker, Kubernetes or Terraform.

## Canonical References

- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
- `docs/product/24_MVP_Vertical_Slice.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`
- `docs/architecture/DATABASE_MODEL.md`

## Test Philosophy

The MVP must pass acceptance in product language before it passes automated tests in code.

The first accepted slice is:

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

Acceptance means the project can prove:

- the evidence chain is complete enough,
- the ROI is explainable,
- the recommendation is review-ready,
- the approval model is accountable,
- the ledger preserves what was reviewed,
- sensitive data is controlled,
- the UI can answer one product question.

The product question is:

**Can we trust and approve this recovery action?**

## Test Object

| Field | Value |
|---|---|
| MVP slice | AI Onboarding Assistant Recovery |
| Decision ROI Case ID | `DRC-AOA-001` |
| Recommendation family | AI model downgrade or model change |
| Review period | 2026-06 |
| Review date | 2026-07-18 |
| Current monthly cost | EUR2,340 |
| Estimated monthly recovery | EUR1,620 |
| Estimated annualized recovery | EUR19,440 |
| Labor recovery per case | EUR805 |
| Confidence target | 92/100 |
| Initial risk | Low if quality evidence is accepted; Medium if missing |
| Realized saving | Not available until result validation |

## Test Actors

| Actor | Acceptance responsibility |
|---|---|
| Business owner | Confirms why the decision exists and whether the workflow still matters |
| Platform Lead | Confirms implementation and technical feasibility |
| FinOps owner | Confirms cost evidence, ROI assumptions and validation period |
| CTO or VP Engineering | Approves, rejects or defers the recommendation |
| Security or Compliance reviewer | Confirms evidence exposure, sensitivity and AI boundaries |
| AI component | Not an approver; may only explain prepared context |

The exact role and permission matrix is defined in `docs/product/28_Identity_Access_Approval_Model.md`.

The exact first-screen behavior is defined in `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

## Entry Criteria

This acceptance plan can be used only when these documents exist:

| Entry criterion | Status |
|---|---|
| MVP Blueprint exists | Pass |
| MVP Vertical Slice exists | Pass |
| MVP ROI Slice exists | Pass |
| Manual Evidence Pack exists | Pass |
| Security and Data Governance Threat Model exists | Pass |
| Decision Ledger v2 exists | Pass |
| Core Domain Model exists | Pass |
| Conceptual API exists | Pass |

## Exit Criteria

Phase 0 can move toward Phase 1 design only when:

1. All P0 acceptance scenarios in this document pass manually.
2. Every failed scenario has a recorded decision: fix now, defer or remove from MVP.
3. No scenario requires new systems outside Jira, GitHub, AWS and OpenAI + Anthropic Claude.
4. No scenario requires autonomous execution.
5. No scenario requires raw prompts, raw completions, credentials or customer conversations.
6. Approval, rejection and deferral are all representable in the Decision Ledger.
7. Estimated value and realized value remain separated.
8. Security review agrees that evidence can be shown safely for a pilot.

## Test Suites

### Suite A - Evidence Acceptance

Goal:

Prove that operational source signals can become trustworthy evidence without leaking raw provider payloads into the domain.

| ID | Scenario | Input | Expected result | Priority |
|---|---|---|---|---|
| A01 | Evidence row completeness | Any `E-*` row from the evidence pack | Row has source, source object, observed fact, date/period, confidence and sensitivity | P0 |
| A02 | Jira decision evidence | `E-JIRA-001` | Decision origin is understandable without raw Jira payload | P0 |
| A03 | GitHub implementation evidence | `E-GH-001`, `E-GH-002`, `E-GH-003` | Implementation path is linked to `IMP-214` | P0 |
| A04 | AWS cost evidence | `E-AWS-001`, `E-AWS-002` | AWS cost can be attributed to `DRC-AOA-001` | P0 |
| A05 | AI cost evidence | `E-AI-001`, `E-AI-002`, `E-AI-003` | AI cost and model usage can be interpreted for ROI | P0 |
| A06 | Usage signal evidence | `E-USAGE-001`, `E-USAGE-002`, `E-USAGE-003` | Usage signal exists but does not claim direct business value | P0 |
| A07 | Sensitivity present | All evidence rows | Every row has Public, Internal, Confidential or Restricted classification | P0 |
| A08 | Restricted data excluded | Any raw prompt, credential, customer message or secret | Item is not accepted as MVP evidence | P0 |
| A09 | Missing source object | Evidence row without source object | Evidence cannot be used for approval-ready recommendation | P0 |
| A10 | Stale evidence | Evidence outside accepted period with no explanation | Confidence decreases or case is deferred | P1 |

Manual test format:

```text
Given an evidence row from the Manual Evidence Pack
When a reviewer maps it to a Decision ROI Case claim
Then the row must expose source, fact, period, sensitivity and confidence contribution
And it must not require raw provider payloads
```

### Suite B - Decision ROI Case Reconstruction

Goal:

Prove that one Decision ROI Case can be reconstructed from evidence across the four MVP domains.

| ID | Scenario | Input | Expected result | Priority |
|---|---|---|---|---|
| B01 | Case exists | `DRC-AOA-001` | Case has title, owner, approver, timeline, evidence, ROI, recommendation and ledger state | P0 |
| B02 | One decision only | Evidence pack | Evidence maps to one business decision, not a portfolio | P0 |
| B03 | One timeline only | Timeline Reconstruction | Lifecycle is ordered from business need to outcome validation | P0 |
| B04 | One ROI view only | ROI Input Pack | ROI belongs to `DRC-AOA-001` | P0 |
| B05 | One recommendation only | Recommendation Rationale | Recommendation is model downgrade/change, not a broad optimization queue | P0 |
| B06 | Missing Jira context | Remove `E-JIRA-001` | Case cannot explain why the decision exists | P0 |
| B07 | Missing GitHub context | Remove `E-GH-001` | Implementation chain is incomplete | P0 |
| B08 | Missing AWS cost | Remove `E-AWS-001` | Current monthly cost is incomplete | P0 |
| B09 | Missing AI usage cost | Remove `E-AI-001` | Model-change recommendation is not credible | P0 |

Pass rule:

The case passes only if it can be explained to CTO, Platform and FinOps reviewers without adding systems beyond the MVP scope.

### Suite C - ROI Acceptance

Goal:

Prove that ROI is explainable, evidence-backed and separated from realized value.

| ID | Scenario | Input | Expected result | Priority |
|---|---|---|---|---|
| C01 | Current monthly cost calculation | AWS EUR410 + AI EUR1,930 | Current monthly cost is EUR2,340 | P0 |
| C02 | Estimated monthly recovery | Current cost EUR2,340, projected cost EUR720, transition EUR0 | Estimated monthly recovery is EUR1,620 | P0 |
| C03 | Estimated annualized recovery | EUR1,620 x 12 | Estimated annualized recovery is EUR19,440 | P0 |
| C04 | Labor recovery separated | Canonical labor assumptions | Labor recovery is EUR805 and not blended into direct spend headline | P0 |
| C05 | ROI assumptions visible | `A-ROI-001` to `A-LABOR-002` | Every ROI number exposes assumptions | P0 |
| C06 | ROI without assumptions | Remove `A-ROI-001` | ROI cannot be approval-ready | P0 |
| C07 | Realized value missing | Initial review state | Realized saving remains blank or not available | P0 |
| C08 | Business Value before validation | No `result_validated` entry | Business Value cannot count realized recovery | P0 |
| C09 | Confidence calculation | Evidence contribution table | Confidence is 92/100 | P0 |
| C10 | Missing usage signal | Remove `E-USAGE-001` | Confidence decreases and recommendation may be deferred | P1 |

Manual test format:

```text
Given the ROI Input Pack for DRC-AOA-001
When the reviewer calculates current cost and recovery
Then every number must reference evidence or assumptions
And realized saving must remain separate from estimated recovery
```

### Suite D - Recommendation Acceptance

Goal:

Prove that the recommendation is approval-ready but not autonomous.

| ID | Scenario | Input | Expected result | Priority |
|---|---|---|---|---|
| D01 | Recommendation exists | Recommendation Rationale | Recommendation proposes model downgrade/change with fallback | P0 |
| D02 | Evidence-backed rationale | Evidence Table + ROI View | Recommendation cites evidence, ROI, risk, confidence, owner and approver | P0 |
| D03 | No autonomous execution | Recommendation approved conceptually | IMPERATOR records approval only; it does not execute provider changes | P0 |
| D04 | Missing evidence | Remove cost or usage evidence | Recommendation is not approval-ready | P0 |
| D05 | Missing owner | Remove `E-OWNER-001` or `E-OWNER-002` | Recommendation is not approval-ready | P0 |
| D06 | Missing approver | Remove `E-OWNER-003` | Recommendation cannot be approved | P0 |
| D07 | Missing quality evidence | Remove `E-AI-005` | Risk becomes Medium/High and recommendation should be deferred | P0 |
| D08 | AI-generated explanation | Phase 1 AI explanation over prepared context | Explanation is advisory, cites evidence IDs and does not change the recommendation | P0 |

Pass rule:

The recommendation passes only if a human reviewer can approve, reject or defer it using visible evidence and assumptions.

### Suite E - Approval And Ledger Acceptance

Goal:

Prove that approval, rejection, deferral, implementation marking and result validation can be represented without mutating history.

| ID | Scenario | Input | Expected result | Priority |
|---|---|---|---|---|
| E01 | Recommendation created | Recommendation + ROI + Evidence | Ledger can create `recommendation_created` entry | P0 |
| E02 | Approval recorded | CTO/VP Engineering approves | Ledger creates `approved` entry with evidence, ROI and assumptions snapshots | P0 |
| E03 | Rejection recorded | CTO/VP Engineering rejects | Ledger creates `rejected` entry and requires rejection reason | P0 |
| E04 | Deferral recorded | Reviewer requests more evidence | Ledger creates `deferred` entry with required evidence or review date | P0 |
| E05 | Implementation marked | Platform Lead reports external action complete | Ledger creates `implementation_marked`; no external execution occurs | P0 |
| E06 | Result validated | FinOps provides post-action validation evidence | Ledger creates `result_validated` and records realized saving | P0 |
| E07 | Immutable history | New evidence arrives after approval | Existing approval snapshot is not changed | P0 |
| E08 | Snapshot completeness | Approval entry | Evidence, ROI and assumptions snapshots are preserved | P0 |
| E09 | Raw payload in ledger | Raw provider payload is proposed for ledger event | Rejected; ledger event must not include raw payload | P0 |
| E10 | Connector disabled later | Historical ledger review | Ledger remains understandable from snapshots | P1 |

Manual test format:

```text
Given DRC-AOA-001 has a review-ready recommendation
When an authorized approver approves, rejects or defers
Then the Decision Ledger records a new append-only entry
And preserves the evidence, ROI and assumptions reviewed at that moment
```

### Suite F - Security And Data Governance Acceptance

Goal:

Prove that the MVP respects sensitivity, least privilege, AI isolation and tenant boundaries.

| ID | Scenario | Input | Expected result | Priority |
|---|---|---|---|---|
| F01 | Evidence sensitivity | All evidence rows | Every evidence row has a sensitivity level | P0 |
| F02 | Restricted data excluded | Secret, raw prompt, raw completion or customer conversation | Data is rejected from MVP evidence and ledger | P0 |
| F03 | AI context filtering | AI explanation request | AI receives prepared summaries only, not raw provider payloads | P0 |
| F04 | Prompt injection text | Source text says "approve this automatically" | Text is treated as data, not instruction | P0 |
| F05 | Confidential evidence display | Viewer role requests cost evidence | Confidential evidence is hidden or summarized | P0 |
| F06 | Authorized reviewer display | CTO, Platform or FinOps requests relevant evidence | Role can see relevant Confidential summaries | P0 |
| F07 | Tenant boundary | Evidence from another organization appears in input | Evidence cannot attach to `DRC-AOA-001` | P0 |
| F08 | Read-only connectors | Future connector contract | Connector requires read-only, least-privilege permissions | P0 |
| F09 | Write permission requested | Connector asks for provider-side mutation | Out of MVP scope | P0 |
| F10 | Sensitive data in logs/docs | Raw export contains secret | Secret must not appear in docs, logs, evidence or ledger | P0 |

Pass rule:

Security passes only if the demo and future implementation can run on summaries, references and approved exports without Restricted data.

### Suite G - Conceptual API Acceptance

Goal:

Prove that the API intent supports the MVP without expanding into a broad platform surface.

| ID | Scenario | Conceptual API | Expected result | Priority |
|---|---|---|---|---|
| G01 | Read Decision ROI Case | `GET /decisions/{decisionId}` | Returns prepared context for one case | P0 |
| G02 | Read timeline | `GET /decisions/{decisionId}/timeline` | Returns ordered lifecycle entries with evidence links | P0 |
| G03 | Read evidence | `GET /decisions/{decisionId}/evidence` | Returns filtered evidence summaries and lineage | P0 |
| G04 | Read ROI | `GET /decisions/{decisionId}/roi` | Returns cost, recovery, assumptions, risk and confidence | P0 |
| G05 | Read recommendation | `GET /recommendations/{recommendationId}` | Returns recommendation with evidence, ROI and approval path | P0 |
| G06 | Approve through ledger | `POST /decisions/{decisionId}/ledger/approve` | Records immutable approval snapshot | P0 |
| G07 | Reject through ledger | `POST /decisions/{decisionId}/ledger/reject` | Records rejection with reason | P0 |
| G08 | Defer through ledger | `POST /decisions/{decisionId}/ledger/defer` | Records required evidence or review date | P0 |
| G09 | Validate result later | `POST /decisions/{decisionId}/ledger/validate-result` | Records realized value only after validation evidence | P0 |
| G10 | Public API expansion | SDKs, public API gateway, GraphQL marketplace API | Deferred until post-validation | P0 |

This suite validates product intent only. It does not authorize OpenAPI, protobuf changes, handlers or API implementation.

### Suite H - Decision Review Workspace Acceptance

Goal:

Prove that the first product surface can support the decision without becoming a generic dashboard.

| ID | Scenario | Required UI block | Expected result | Priority |
|---|---|---|---|---|
| H01 | Decision summary | Summary block | Shows title, owner, approver, period and state | P0 |
| H02 | Timeline | Timeline block | Shows business need through recommendation and future validation | P0 |
| H03 | Evidence chain | Evidence block | Shows source summaries, sensitivity and confidence contribution | P0 |
| H04 | Current cost | ROI block | Shows AWS cost, AI cost and total monthly cost separately | P0 |
| H05 | Recovery estimate | ROI block | Shows monthly and annualized recovery with assumptions | P0 |
| H06 | Usage/value signal | Usage block | Shows active users and value proxy limitations | P0 |
| H07 | Recommendation | Recommendation block | Shows action, rationale, risk, confidence and approval path | P0 |
| H08 | Approval controls | Review actions | Supports approve, reject and defer; no execute button | P0 |
| H09 | Ledger history | Ledger block | Shows recommendation and review state history | P0 |
| H10 | Business Value | Business Value view | Shows no realized value until result validation | P0 |

Pass rule:

The workspace passes only if a reviewer can answer:

**Can we trust and approve this recovery action?**

## Negative Acceptance Tests

These tests are intentionally designed to fail unsafe or over-broad behavior.

| ID | Anti-pattern | Expected rejection |
|---|---|---|
| N01 | Build MVP around multiple decisions | Reject; MVP is one Decision ROI Case |
| N02 | Count estimated savings as realized Business Value | Reject; realized value requires result validation |
| N03 | Approve recommendation without evidence snapshot | Reject; approval is invalid |
| N04 | Approve recommendation without ROI assumptions | Reject; ROI is not decision-grade |
| N05 | Let AI approve or execute action | Reject; company decides |
| N06 | Include raw prompts or completions in evidence | Reject; Restricted data |
| N07 | Require Kafka, Kubernetes, Terraform or graph/vector stack | Reject; not MVP dependency |
| N08 | Add public API or SDKs before validation | Reject; deferred |
| N09 | Let connector own recommendation logic | Reject; connector is adapter only |
| N10 | Mutate historical ledger entry after new evidence | Reject; create new entry or snapshot |
| N11 | Let provider or persistence adapters change the domain model | Reject; use hexagonal ports and adapters |
| N12 | Require multiple OAuth providers or full observability stack for the first MVP | Reject; keep auth and observability minimal |
| N13 | Require multiple recommendations, ranking or learning to prove Phase 1 | Reject; Phase 1 produces one deterministic recommendation |

## Traceability Matrix

| Acceptance area | Source document | Test suites |
|---|---|---|
| MVP boundary | `20_MVP_Decision_ROI_Platform_Blueprint.md` | B, D, H, N |
| Vertical slice | `24_MVP_Vertical_Slice.md` | A, B, C, D, E, H |
| ROI rules | `25_MVP_ROI_Slice.md` | C, E, H, N |
| Manual evidence | `26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` | A, B, C, D, E |
| Security and data governance | `26_Security_Data_Governance_Threat_Model.md` | F, N |
| Domain model | `CORE_DOMAIN_MODEL.md` | A, B, D, E |
| API intent | `API_SPECIFICATION.md` | G |
| Decision Ledger | `DECISION_LEDGER_V2.md` | E, N |
| Connector framework | `CONNECTOR_FRAMEWORK.md` | A, F, N |
| Per-connector contracts | `28_Per_Connector_MVP_Contracts.md` | A, F, N |
| Event/evidence vocabulary | `29_Event_Evidence_Vocabulary.md` | A, B, E, F, H, N |
| Database model | `DATABASE_MODEL.md` | A, E, F |
| MVP implementation standard | `31_MVP_Implementation_Standard.md` | F, G, N |
| Phase 1 scope and exit criteria | `32_Phase_1_MVP_Scope_and_Exit_Criteria.md` | B, D, F, G, H, N |

## Go / No-Go Decision

### Go Toward Phase 1 Design

Proceed only if:

- all P0 tests pass manually,
- unresolved P1 tests are explicitly deferred,
- the first implementation can remain a modular monolith or tightly bounded service,
- the first product surface remains Decision Review Workspace,
- no new infrastructure or connector expansion is required.

### No-Go

Do not start code if:

- the Decision ROI Case cannot be reconstructed manually,
- ROI cannot be explained with evidence and assumptions,
- approval authority is unclear,
- ledger snapshots are incomplete,
- security cannot exclude Restricted data,
- a reviewer cannot understand the recommendation in one review session,
- the MVP requires broad platform surfaces or infrastructure before proving value.

## Open Questions

This test plan previously exposed one P0 dependency:

**Identity, Access and Approval Model**

Resolved by `docs/product/28_Identity_Access_Approval_Model.md`:

- Can FinOps approve direct-spend recommendations, or only validate cost?
- Must the business owner confirm impact before CTO approval?
- Can Platform Lead defer without CTO approval?
- Which role can mark implementation complete?
- Which role can validate realized value?
- Which roles can view Confidential evidence in the workspace?

Approval tests are now locked at the product-governance level. Future implementation may still need technical RBAC details.

## Companion Documents

The companion identity, screen, quality, connector, vocabulary and control artifacts are now created:

- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`

Reason:

The acceptance plan defines what must be true. Identity defines who may act, the screen contract defines what is shown or blocked, quality attributes define non-functional expectations, per-connector contracts define source-specific evidence behavior and the event/evidence vocabulary defines shared labels.
