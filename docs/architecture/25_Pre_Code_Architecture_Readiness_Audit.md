# 25 - Pre-Code Architecture Readiness Audit

## Purpose

Audit IMPERATOR Phase 0 as a software architect before writing implementation code.

This document checks whether the current context is coherent, what is already strong, what is still missing, and which gates should be closed before Phase 1.

It is a Phase 0 planning artifact. It does not authorize source code, services, connectors, infrastructure, database migrations, generated bindings, Docker, Kafka, Kubernetes or Terraform.

## Authority

This audit is subordinate to:

1. `docs/decisions/14_Decision_Log.md`
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
3. `docs/product/24_MVP_Vertical_Slice.md`
4. `docs/product/25_MVP_ROI_Slice.md`
5. `docs/product/CORE_DOMAIN_MODEL.md`
6. `docs/product/API_SPECIFICATION.md`
7. `docs/product/DECISION_LEDGER_V2.md`
8. `docs/architecture/21_Technical_Architecture_Context.md`
9. `docs/architecture/DATABASE_MODEL.md`
10. `docs/architecture/CONNECTOR_FRAMEWORK.md`
11. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
12. `docs/product/28_Identity_Access_Approval_Model.md`
13. `docs/product/27_MVP_Acceptance_Test_Plan.md`
14. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
15. `docs/architecture/27_Quality_Attributes.md`
16. `docs/architecture/28_Per_Connector_MVP_Contracts.md`
17. `docs/architecture/29_Event_Evidence_Vocabulary.md`
18. `agents/README.md`
19. `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
20. `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`
21. `docs/architecture/31_MVP_Implementation_Standard.md`
22. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`

## Executive Verdict

Phase 0 is coherent and strategically usable.

The project has the right foundation for a future MVP:

- clear category: Operating System for Operational Intelligence / Enterprise Decision Intelligence Platform,
- narrow MVP product model: Decision ROI Platform for Executive Operational Intelligence,
- paid wedge: Decision Recovery Workflow for AI/cloud spend,
- scope rule: One Decision. One Timeline. One ROI,
- central domain object: Decision ROI Case,
- conceptual API, database model, connector framework and ledger contract,
- vertical slice and ROI slice defined before implementation.

However, the project should still avoid coding until a recorded go/no-go decision explicitly authorizes Phase 1.

The remaining risk is not lack of technology. Manual evidence, security boundaries, approval authority, acceptance tests, first-screen behavior, quality attributes, per-connector MVP contracts, event/evidence vocabulary, agent ownership and development evidence tracking now exist. The remaining action is a disciplined manual go/no-go review using `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`.

If Phase 1 is authorized, implementation should use `docs/architecture/31_MVP_Implementation_Standard.md` to reduce scope and apply Hexagonal Architecture, JWT/OAuth2-compatible auth direction and minimal observability, then use `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` to enforce the exact first-build objective and exit criteria.

## Coherence Check

| Area | Status | Architect verdict |
|---|---|---|
| Positioning | Green | The ERP/CRM/SIEM/Observability comparison is strong and easy to explain. |
| MVP scope | Green | Decision Recovery Workflow is narrow enough for a TFG and startup pilot. |
| Domain model | Green | Decision ROI Case, Evidence, ROI, Recommendation, Approval and Ledger are clear. |
| API specification | Green with boundary | Conceptual API exists. MVP must use only the Decision ROI Case subset. |
| Database model | Green | Conceptual data areas are correct and now cross-reference security, connector and event/evidence vocabulary boundaries. |
| Connector framework | Green | Connector boundary is correct and per-connector MVP contracts now exist for Jira, GitHub, AWS and OpenAI + Anthropic Claude. |
| Event/evidence vocabulary | Green | Normalized events, evidence types, blockers, lifecycle states and labels are controlled. |
| Decision Ledger | Green | Append-only, snapshot-based and not an executor. This is the right trust model. |
| ROI slice | Green | Estimated and realized value are separated. Assumptions are explicit. |
| Demo/product surface | Green | Useful for storytelling. The MVP surface remains Decision Review Workspace and now has a screen contract. |
| Project structure | Green | Documentation zones are clean. Current service folders are placeholders only. |

## Logical Corrections Applied

The canonical pre-code flow should be:

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

Reason:

- ROI must exist before an approval snapshot can preserve it.
- Recommendation must reference evidence, ROI, risk, confidence and assumptions.
- Ledger records accountability after reviewable facts exist.
- Ledger does not calculate ROI, orchestrate workflow or execute external changes.

## What Is Already Implemented In Docs

| Foundation | Exists? | Canonical document |
|---|---|---|
| Domain Model | Yes | `docs/product/CORE_DOMAIN_MODEL.md` |
| API Specification | Yes | `docs/product/API_SPECIFICATION.md` |
| Architecture RFC | Yes | `docs/rfcs/0002-module-communication-architecture.md` |
| Database Model | Yes | `docs/architecture/DATABASE_MODEL.md` |
| Connector Framework | Yes | `docs/architecture/CONNECTOR_FRAMEWORK.md` |
| Decision Ledger | Yes | `docs/product/DECISION_LEDGER_V2.md` |
| MVP Vertical Slice | Yes | `docs/product/24_MVP_Vertical_Slice.md` |
| MVP ROI Slice | Yes | `docs/product/25_MVP_ROI_Slice.md` |
| MVP Project Structure | Yes | `docs/architecture/24_MVP_Project_Structure.md` |
| One-page Spanish project prompt | Yes | `docs/ai/IMPERATOR_One_Page_Project_Prompt_ES.md` |
| Security and Data Governance Threat Model | Yes | `docs/architecture/26_Security_Data_Governance_Threat_Model.md` |
| Identity, Access and Approval Model | Yes | `docs/product/28_Identity_Access_Approval_Model.md` |
| MVP Acceptance Test Plan | Yes | `docs/product/27_MVP_Acceptance_Test_Plan.md` |
| Decision Review Workspace Screen Contract | Yes | `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` |
| Quality Attributes | Yes | `docs/architecture/27_Quality_Attributes.md` |
| Per-Connector MVP Contracts | Yes | `docs/architecture/28_Per_Connector_MVP_Contracts.md` |
| Event and Evidence Vocabulary | Yes | `docs/architecture/29_Event_Evidence_Vocabulary.md` |
| AI Agent Operating Model | Yes | `agents/README.md` |
| I+D/R&D Evidence Dossier | Yes | `docs/rnd/30_RD_Activity_Evidence_Dossier.md` |
| Phase 0 Closure Readiness Review | Yes | `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` |
| MVP Implementation Standard | Yes | `docs/architecture/31_MVP_Implementation_Standard.md` |
| Phase 1 MVP Scope and Exit Criteria | Yes | `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` |

## Foundations Closed Before Code

### P0 - Required Before Phase 1

1. Manual Evidence Pack for AI Onboarding Assistant Recovery

Define the exact evidence rows that prove the first case:

- Jira business need, owner, approver and status,
- GitHub PR, commit, author and deployment reference,
- AWS resource, cost, utilization and period,
- OpenAI + Anthropic Claude model, tokens, requests, users/app and cost,
- usage or value signal,
- source IDs, timestamps, confidence contribution and sensitivity.

Status: created as `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`.

2. Security, Data Governance and Threat Model

Define:

- which evidence may contain sensitive data,
- which raw provider payloads are never shown to AI,
- what is stored as summary versus artifact,
- tenant and organization boundary,
- retention expectations,
- secret handling assumptions,
- role-based evidence visibility,
- audit and deletion constraints.

Status: created as `docs/architecture/26_Security_Data_Governance_Threat_Model.md`.

3. Identity, Access and Approval Model

Define:

- roles for Executive, CTO, Platform, FinOps, Engineer, Security, Compliance, Finance, Admin and Viewer,
- who can approve, reject, defer, mark implementation and validate result,
- who can see sensitive evidence,
- what happens when owner or approver is unknown,
- minimum approval path for the first MVP.

Status: created as `docs/product/28_Identity_Access_Approval_Model.md`.

4. MVP Acceptance Test Plan

Before code, define testable scenarios in product language:

- one event becomes evidence,
- evidence attaches to one Decision ROI Case,
- ROI cannot be approval-ready without assumptions,
- recommendation cannot be approval-ready without evidence,
- approval creates ledger snapshots,
- rejection requires reason,
- deferral requires missing evidence or review date,
- realized value cannot appear before result validation.

Status: created as `docs/product/27_MVP_Acceptance_Test_Plan.md`.

5. Decision Review Workspace Screen Contract

Define the first screen without implementation:

- sections,
- fields,
- empty states,
- confidence/risk display,
- approval actions,
- ledger history block,
- evidence drilldown rules.

Status: created as `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

6. Quality Attributes / Non-Functional Requirements

Define MVP expectations for:

- explainability,
- auditability,
- data freshness,
- reliability,
- latency for review screens,
- evidence completeness,
- reversibility of external actions,
- observability of connector health,
- performance non-goals for first proof.

Status: created as `docs/architecture/27_Quality_Attributes.md`.

### P1 - Required Before Broad Build-Out

1. Per-Connector MVP Contracts

Create one conceptual contract each for:

- Jira,
- GitHub,
- AWS,
- OpenAI + Anthropic Claude.

Each contract should list source objects, normalized events, evidence types, correlation keys, permissions, sensitivity and failure states.

Status: created as `docs/architecture/28_Per_Connector_MVP_Contracts.md`.

2. Event and Evidence Vocabulary

Create a small controlled vocabulary for the first slice:

- `business_context_requested`,
- `code_change_merged`,
- `deployment_reference_observed`,
- `cloud_cost_observed`,
- `ai_usage_observed`,
- `usage_signal_observed`,
- `recommendation_created`,
- `approved`,
- `result_validated`.

Status: created as `docs/architecture/29_Event_Evidence_Vocabulary.md`.

3. I+D/R&D Activity Evidence Dossier

Define how to document future development activity:

- architecture evidence,
- code evidence,
- hours,
- technical objects,
- experiments,
- tests,
- monthly summaries,
- costs and resources,
- routine versus I+D/i candidate work.

Status: created as `docs/rnd/30_RD_Activity_Evidence_Dossier.md`.

## Pre-Code Go / No-Go Gates

Do not begin implementation until these gates are true:

| Gate | Required answer |
|---|---|
| G1 - One case can be reconstructed manually | Can a human fill one Decision ROI Case using the planned evidence? |
| G2 - Every ROI number has proof | Does each number have source, period, assumption and confidence? |
| G3 - Evidence is safe to show | Is each evidence item classified by sensitivity and role visibility? |
| G4 - Approval is explicit | Is it clear who can approve, reject, defer and validate? |
| G5 - Ledger snapshots are defined | Do approval actions preserve evidence, ROI and assumptions snapshots? |
| G6 - MVP API subset is clear | Are future endpoints reduced to the few needed for one case? |
| G7 - No accidental platform build | Are public API, SDKs, Kafka, Kubernetes, Terraform, policy engine, graph/vector and AI Advisor still deferred? |
| G8 - Acceptance tests exist | Can Phase 1 verify the slice without inventing requirements during coding? |
| G9 - Screen behavior is explicit | Does the Decision Review Workspace define what it shows, hides, blocks and records? |
| G10 - Quality attributes are explicit | Are explainability, auditability, freshness, traceability, latency, resilience, observability and performance non-goals defined? |
| G11 - Connector contracts are explicit | Do Jira, GitHub, AWS and OpenAI + Anthropic Claude define source objects, evidence, permissions, freshness, sensitivity and failure behavior? |
| G12 - Vocabulary is explicit | Are event names, evidence types, lifecycle states, blockers and labels controlled before code? |
| G13 - Development evidence model exists | Can future architecture, code, hours, objects, experiments and tests be documented by work package? |
| G14 - Closure gate exists | Can the founder/architect make a recorded go/no-go decision without inventing missing context? |
| G15 - Implementation standard exists | Can future Phase 1 work reduce scope and apply hexagonal/auth/observability rules consistently? |
| G16 - Phase 1 scope contract exists | Can future implementation prove one Decision ROI Case without drifting into platform build-out? |

## What Not To Add Yet

Do not add until P0 acceptance scenarios pass, approval authority is resolved, the screen contract is reviewed and quality attributes are accepted:

- `src/`,
- runnable backend or frontend services,
- production connectors,
- OAuth/provider credentials,
- database migrations,
- OpenAPI or generated protobuf bindings,
- Docker Compose for runtime,
- Kafka,
- Kubernetes,
- Terraform,
- public API gateway,
- SDKs,
- graph database,
- vector database,
- AI Advisor,
- autonomous execution,
- broad policy engine,
- multi-tenant enterprise admin surface.

## Architecture Recommendations

1. Keep Phase 1 as a modular monolith or tightly bounded service.

The docs correctly separate contexts, but contexts should not become microservices until the value loop works.

2. Build from Decision ROI Case, not from connectors.

Connector data should be pulled into the domain only when it strengthens evidence, ROI, recommendation or ledger trust.

3. Use deterministic ROI and recommendation rules first.

LLMs can help explain or summarize prepared context later. They should not be the source of financial truth.

4. Treat manual reconstruction as the first architecture test.

If the team cannot reconstruct one case manually, code will only automate ambiguity.

5. Keep Business Value behind result validation.

Projected savings may guide approvals. Realized value must come only after implementation and validation are recorded.

6. Make security a product requirement, not a later technical concern.

Evidence is the product. Evidence access, sensitivity and retention must be designed before connectors exist.

## Completed Pre-Code Documents

These were created in order:

1. `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` - created
2. `docs/architecture/26_Security_Data_Governance_Threat_Model.md` - created
3. `docs/product/27_MVP_Acceptance_Test_Plan.md` - created
4. `docs/product/28_Identity_Access_Approval_Model.md` - created
5. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` - created
6. `docs/architecture/27_Quality_Attributes.md` - created
7. `docs/architecture/28_Per_Connector_MVP_Contracts.md` - created
8. `docs/architecture/29_Event_Evidence_Vocabulary.md` - created
9. `docs/rnd/30_RD_Activity_Evidence_Dossier.md` - created
10. `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` - created
11. `docs/architecture/31_MVP_Implementation_Standard.md` - created
12. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` - created

The immediate control step is a recorded manual go/no-go review using `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` and the execution boundary in `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`. The project now knows what must be proven, who can act, what the first review surface shows, hides, blocks and records, which quality attributes constrain implementation, how each MVP connector contributes evidence safely and how future development work should be evidenced.

If Phase 1 is authorized, use `docs/architecture/31_MVP_Implementation_Standard.md` and `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` before creating any scaffolding.

## Final Architect Assessment

IMPERATOR is not missing more vision.

It now has the core pre-code architecture and evidence-control documents needed before implementation:

- domain model,
- conceptual API,
- database model,
- connector framework,
- ledger contract,
- manual evidence pack,
- acceptance plan,
- identity and approval model,
- screen contract,
- quality attributes,
- per-connector MVP contracts,
- event/evidence vocabulary,
- I+D/R&D evidence dossier,
- Phase 0 closure readiness review,
- MVP implementation standard,
- Phase 1 MVP scope and exit criteria.

The remaining task before code is not another big vision document. It is a recorded go/no-go decision: confirm that `DRC-AOA-001` can be reconstructed, accepted, governed and documented end to end, then either freeze Phase 0 or authorize limited Phase 1 scaffolding under the scope contract.
