# 02 - Acceptance Contract

## Purpose

Convert the locked Phase 1 product case into a concrete acceptance contract before implementation scaffolding.

This document defines what must pass, what must fail, and what evidence proves that `DRC-AOA-001` remains inside the reduced MVP scope.

It does not create automated tests, source code, fixtures, services, connectors, APIs, database schema, package manifests, generated bindings, Docker, Kubernetes, Terraform or cloud resources.

## Stage Verdict

**Verdict: LOCKED for Phase 1 acceptance.**

Future implementation work may proceed only if it can map to this acceptance contract.

No implementation task is valid unless it supports at least one acceptance gate in this document.

## Authority Inputs

- `agents/phase1/00_context_control.md`
- `agents/phase1/01_product_case_lock.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/DECISION_LEDGER_V2.md`

If any acceptance criterion conflicts with these documents, stop and hand off to CTO Agent.

## Acceptance Philosophy

Acceptance proves:

```text
one authenticated reviewer
-> one imported/manual evidence source
-> one Decision ROI Case
-> one evidence chain
-> one ROI View
-> one deterministic recommendation
-> one AI explanation
-> one review action
-> one ledger history
-> basic health and observability
```

Acceptance does not prove:

- full production integrations,
- all connector endpoints,
- enterprise SSO,
- broad dashboard navigation,
- public APIs,
- autonomous execution,
- realized savings before validation.

## Global Pass Criteria

Phase 1 MVP passes only if all required criteria below pass.

| ID | Acceptance area | Required result |
|---|---|---|
| AC-01 | Case identity | `DRC-AOA-001` loads as the only Phase 1 Decision ROI Case. |
| AC-02 | Source mode | Evidence comes from manual/static or file/import source; no live connector is required. |
| AC-03 | Evidence chain | Evidence is normalized, reviewable and linked to claims. |
| AC-04 | ROI View | Cost, recovery, assumptions, confidence and risk are visible. |
| AC-05 | Recommendation | Exactly one deterministic model downgrade/change recommendation is produced. |
| AC-06 | AI explanation | AI explains prepared context, cites evidence IDs and does not change the recommendation. |
| AC-07 | Identity and authority | Actor role controls visibility and allowed actions. |
| AC-08 | Review action | Authorized reviewer can approve, reject or defer. |
| AC-09 | Ledger history | Review action records append-only ledger history with snapshots. |
| AC-10 | Result validation boundary | Realized value remains unavailable until validation evidence exists. |
| AC-11 | Security | Restricted data is rejected from evidence, ledger, logs and AI context. |
| AC-12 | Workspace | Decision Review Workspace answers the trust-and-approval question. |
| AC-13 | Observability | Logs, minimal metrics, `/health` and `/ready` exist. |
| AC-14 | R&D evidence | Real development activity can be captured in `docs/rnd/` without inventing work. |

## Detailed Acceptance Gates

### Gate A - Evidence

| ID | Test | Expected result |
|---|---|---|
| A1 | Load manual/import evidence for `DRC-AOA-001` | Evidence items retain ID, source, observed fact, period, sensitivity and confidence contribution. |
| A2 | Remove `E-JIRA-001` | Case cannot explain why the decision exists. |
| A3 | Remove `E-GH-001` | Implementation chain is incomplete. |
| A4 | Remove `E-AWS-001` | Current monthly cost is incomplete. |
| A5 | Remove `E-AI-001` | Model-change recommendation is not credible. |
| A6 | Add raw prompt or completion | Evidence is rejected as Restricted data. |
| A7 | Add credential or secret | Evidence is rejected and must not reach AI, ledger or logs. |

### Gate B - Decision ROI Case

| ID | Test | Expected result |
|---|---|---|
| B1 | Reconstruct `DRC-AOA-001` | Case has decision title, owner, approver, timeline, evidence, ROI, recommendation and ledger state. |
| B2 | Attempt to load a second MVP case | Rejected or marked out of Phase 1 scope. |
| B3 | Remove owner evidence | Recommendation is not approval-ready. |
| B4 | Remove approver evidence | Approval action is blocked. |
| B5 | Explain case to CTO/Platform/FinOps | Case is understandable without external narration. |

### Gate C - ROI

| ID | Test | Expected result |
|---|---|---|
| C1 | Calculate current monthly cost | EUR410 AWS + EUR1,930 AI = EUR2,340. |
| C2 | Calculate estimated monthly recovery | EUR2,340 - EUR720 - EUR0 = EUR1,620. |
| C3 | Calculate annualized recovery | EUR1,620 x 12 = EUR19,440. |
| C4 | Show labor recovery | EUR805 appears separately from direct spend recovery if shown. |
| C5 | Remove ROI assumptions | ROI is not approval-ready. |
| C6 | Show realized saving before validation | Rejected; realized value requires result validation. |
| C7 | Use AI output as financial proof | Rejected; ROI comes from deterministic formulas and assumptions. |

### Gate D - Recommendation

| ID | Test | Expected result |
|---|---|---|
| D1 | Generate recommendation | Exactly one deterministic model downgrade/change recommendation appears. |
| D2 | Remove `E-AI-005` | Risk becomes Medium or recommendation is deferred. |
| D3 | Request multiple recommendations | Rejected; Phase 1 proves one recommendation. |
| D4 | Request ranking or learning | Rejected; out of Phase 1 scope. |
| D5 | Request provider-side execution | Rejected; IMPERATOR records decision, company acts externally. |

### Gate E - AI Explanation

| ID | Test | Expected result |
|---|---|---|
| E1 | Ask AI to explain recommendation | AI cites evidence IDs and assumptions. |
| E2 | Ask AI to approve | Rejected; AI has no authority. |
| E3 | Ask AI to modify data | Rejected; AI cannot mutate persistent state. |
| E4 | Include raw provider payload | Rejected from AI context. |
| E5 | AI explanation unavailable | Recommendation remains deterministic; explanation failure is logged. |

### Gate F - Approval And Ledger

| ID | Test | Expected result |
|---|---|---|
| F1 | CTO/VP Engineering approves | Append-only `approved` ledger entry is recorded with snapshots. |
| F2 | CTO/VP Engineering rejects | Append-only `rejected` ledger entry is recorded with reason. |
| F3 | Authorized reviewer defers | Append-only `deferred` ledger entry is recorded with evidence gap or review date. |
| F4 | Attempt approval without snapshots | Blocked. |
| F5 | Attempt to edit historical ledger entry | Rejected; create a new entry instead. |
| F6 | Connector or AI writes ledger | Rejected. |

### Gate G - Decision Review Workspace

| ID | Test | Expected result |
|---|---|---|
| G1 | Open workspace for `DRC-AOA-001` | Shows decision header, timeline, evidence, ROI, recommendation, actions and ledger history. |
| G2 | Viewer role opens workspace | Confidential details are hidden or summarized. |
| G3 | CTO/VP Engineering opens workspace | Relevant evidence, ROI and actions are visible. |
| G4 | Evidence is missing | Blockers are visible; approve is disabled. |
| G5 | Restricted data appears | Review is blocked for security handling. |
| G6 | UI tries to show broad dashboard | Rejected; first surface is Decision Review Workspace. |

### Gate H - Auth, Security And Observability

| ID | Test | Expected result |
|---|---|---|
| H1 | User authenticates through local/demo JWT-compatible RBAC mode | Actor and one of `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE` or `AUDITOR` is available to the application boundary. |
| H2 | Unauthorized role attempts approve | Action is hidden or disabled. |
| H3 | Request executes | Structured request log includes correlation ID. |
| H4 | Error occurs | Error log is produced without Restricted data. |
| H5 | AI explanation runs or fails | AI log category records success/failure without raw prompts. |
| H6 | Health check | `/health` reports basic liveness. |
| H7 | Readiness check | `/ready` reports basic readiness. |
| H8 | Basic metrics | Request, error, latency, recommendation and AI explanation counters are available. |

## Negative Acceptance Tests

These must fail.

| ID | Anti-pattern | Expected rejection |
|---|---|---|
| N1 | Build multiple Decision ROI Cases for Phase 1 | Reject. |
| N2 | Generate multiple recommendations | Reject. |
| N3 | Rank recommendations | Reject. |
| N4 | Use learning system to decide recommendation | Reject. |
| N5 | Let AI approve, reject, defer, implement or validate | Reject. |
| N6 | Let connector calculate ROI | Reject. |
| N7 | Let connector write ledger | Reject. |
| N8 | Store raw prompts or completions | Reject. |
| N9 | Count estimated recovery as realized Business Value | Reject. |
| N10 | Require Kafka, Kubernetes, Terraform, Graph DB/vector/search or full observability stack | Reject. |
| N11 | Require multiple OAuth providers | Reject. |
| N12 | Build Executive Workspace as first product surface | Reject. |
| N13 | Add public API or SDK | Reject. |

## Demo Pass/Fail Criteria

### Pass

The Phase 1 demo passes if:

1. A user authenticates.
2. `DRC-AOA-001` is loaded.
3. Evidence is imported or available from the manual/static source path.
4. Evidence maps to the locked claims.
5. ROI View shows EUR2,340 current cost, EUR1,620 monthly recovery and EUR19,440 annualized recovery.
6. Exactly one deterministic recommendation appears.
7. AI explanation cites evidence IDs and remains advisory.
8. CTO/VP Engineering can approve, reject or defer.
9. Ledger records the chosen review state append-only.
10. Realized saving stays unavailable until result validation.
11. Restricted data is not required or exposed.
12. `/health`, `/ready`, logs and basic metrics are available.

### Fail

The Phase 1 demo fails if:

- the case requires manual narration outside the product to be understandable,
- ROI lacks assumptions,
- recommendation requires AI judgment,
- approval happens without evidence/ROI/assumption snapshots,
- raw prompts, raw completions, credentials or customer conversations enter evidence,
- broad dashboards or platform infrastructure become necessary to pass.

## Traceability Matrix

| Acceptance area | Source |
|---|---|
| Case facts | `agents/phase1/01_product_case_lock.md` |
| Scope | `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` |
| Implementation standard | `docs/architecture/31_MVP_Implementation_Standard.md` |
| Evidence | `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` |
| ROI | `docs/product/25_MVP_ROI_Slice.md` |
| Approval authority | `docs/product/28_Identity_Access_Approval_Model.md` |
| Screen behavior | `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` |
| Security | `docs/architecture/26_Security_Data_Governance_Threat_Model.md` |
| Ledger | `docs/product/DECISION_LEDGER_V2.md` |
| Quality | `docs/architecture/27_Quality_Attributes.md` |
| R&D evidence | `docs/rnd/30_RD_Activity_Evidence_Dossier.md` |

## Stage 02 Pass Criteria

This stage passes when:

1. every future implementation task can map to an acceptance gate,
2. negative tests cover scope creep and unsafe behavior,
3. demo pass/fail criteria are explicit,
4. no acceptance condition requires post-MVP infrastructure,
5. no acceptance condition gives AI or connectors decision authority.

## Handoff To Stage 03

Next stage:

```text
03 - Architecture Scaffolding Plan
```

Lead:

- CTO Agent

Stage 03 objective:

Create the minimal architecture scaffolding plan for a hexagonal, reduced Phase 1 MVP without creating code.

Inputs for Stage 03:

- this document,
- `agents/phase1/00_context_control.md`,
- `agents/phase1/01_product_case_lock.md`,
- `docs/architecture/31_MVP_Implementation_Standard.md`,
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`,
- `docs/architecture/21_Technical_Architecture_Context.md`,
- `docs/architecture/DATABASE_MODEL.md`,
- `docs/product/API_SPECIFICATION.md`,
- `docs/architecture/CONNECTOR_FRAMEWORK.md`.

Exit condition for Stage 03:

Architecture can prove the MVP without microservices, Kafka, Kubernetes, Terraform, Graph DB/vector/search stores, public APIs, SDKs, broad connectors or AI decision-making.
