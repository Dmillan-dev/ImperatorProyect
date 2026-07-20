# Phase 1 Agentic MVP Creation Guide

## Purpose

Define the recommended process for creating IMPERATOR Phase 1 with autonomous AI agents while preserving the full project context and the reduced MVP scope.

This guide is operational. It does not create code, services, connectors, database schema, package manifests, generated bindings, credentials, Docker, Kubernetes, Terraform or cloud resources.

Use this guide when Phase 1 planning or implementation is explicitly authorized.

## Core Rule

Autonomous agents may work independently inside a bounded stage.

They must not invent product scope, domain concepts, connector behavior, AI authority, data models or acceptance criteria.

Every agent must optimize for:

```text
One Decision ROI Case
-> one evidence chain
-> one ROI view
-> one deterministic recommendation
-> one AI explanation
-> one review workspace
-> one ledger history
-> minimal health and observability
```

Controlling rule:

> Todo lo que no sea imprescindible para demostrar un unico Decision ROI Case queda automaticamente fuera del alcance.

## Authority Baseline

Before any Phase 1 agent work, every agent must read or be given a brief derived from:

1. `docs/decisions/14_Decision_Log.md`
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
3. `docs/product/24_MVP_Vertical_Slice.md`
4. `docs/product/25_MVP_ROI_Slice.md`
5. `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
6. `docs/product/27_MVP_Acceptance_Test_Plan.md`
7. `docs/product/28_Identity_Access_Approval_Model.md`
8. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
9. `docs/product/CORE_DOMAIN_MODEL.md`
10. `docs/product/API_SPECIFICATION.md`
11. `docs/product/DECISION_LEDGER_V2.md`
12. `docs/architecture/21_Technical_Architecture_Context.md`
13. `docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md`
14. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
15. `docs/architecture/27_Quality_Attributes.md`
16. `docs/architecture/28_Per_Connector_MVP_Contracts.md`
17. `docs/architecture/29_Event_Evidence_Vocabulary.md`
18. `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`
19. `docs/architecture/31_MVP_Implementation_Standard.md`
20. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
21. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
22. `docs/architecture/34_MVP_Implementation_Blueprint.md`
23. `docs/architecture/DATABASE_MODEL.md`
24. `docs/architecture/CONNECTOR_FRAMEWORK.md`
25. `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
26. `agents/README.md`

If any instruction conflicts, the Decision Log, `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`, `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` and `docs/architecture/34_MVP_Implementation_Blueprint.md` win for Phase 1 scope, foundational implementation choices and final implementation order.

## Agent Operating Principle

Use this model:

```text
CTO Agent controls scope and sequence.
Specialist agents own bounded outputs.
QA Agent verifies gates.
R&D evidence is recorded from day one of real development.
Decision Log records scope, architecture or domain changes.
```

Autonomy is local.

Authority is centralized.

## Recommended Agent Sections

The current agent structure is role-based:

```text
agents/
  cto/
  product/
  backend/
  frontend/
  connectors/
  ai/
  security/
  finops/
  qa/
  phase1/
```

Recommended future stage files, created only when needed:

```text
agents/phase1/
  00_context_control.md
  01_product_case_lock.md
  02_acceptance_contract.md
  03_architecture_scaffolding_plan.md
  04_data_persistence_slice.md
  05_evidence_intake_slice.md
  06_domain_roi_recommendation_slice.md
  07_ai_explanation_slice.md
  08_ledger_approval_slice.md
  09_review_workspace_slice.md
  10_auth_observability_security_slice.md
  11_integrated_demo_acceptance.md
  12_phase1_closure.md
```

Do not create all stage files upfront unless the founder explicitly wants a full stage dossier. Start with the next stage only.

Current created stage files:

- `agents/phase1/00_context_control.md`
- `agents/phase1/01_product_case_lock.md`
- `agents/phase1/02_acceptance_contract.md`
- `agents/phase1/03_architecture_scaffolding_plan.md`
- `agents/phase1/04_data_persistence_slice.md`
- `agents/phase1/05_evidence_intake_slice.md`
- `agents/phase1/06_domain_roi_recommendation_slice.md`
- `agents/phase1/07_ai_explanation_slice.md`
- `agents/phase1/08_ledger_approval_slice.md`
- `agents/phase1/09_review_workspace_slice.md`
- `agents/phase1/10_auth_observability_security_slice.md`
- `agents/phase1/11_integrated_demo_acceptance.md`
- `agents/phase1/12_phase1_closure.md`

## Phase 1 Stage Map

### Stage 00 - Context Control And Go/No-Go

Lead agent:

- CTO Agent

Supporting agents:

- QA Agent
- Product Agent

Inputs:

- Decision Log
- Phase 0 Closure Readiness Review
- MVP Implementation Standard
- Phase 1 Scope and Exit Criteria
- Agent Operating Model

Output:

- explicit Phase 1 go/no-go recommendation,
- confirmed first case: `DRC-AOA-001`,
- confirmed recommendation family: AI model downgrade or model change,
- confirmed first data mode: manual/import or one narrow read-only connector,
- confirmed auth mode,
- confirmed R&D evidence method.

Gate:

- no code starts until scope, first source mode, recommendation family and evidence tracking method are recorded.

Stop if:

- the plan requires more than one Decision ROI Case,
- the plan requires broad connector automation,
- the plan treats Phase 1 as platform build-out.

### Stage 01 - Product Case Lock

Lead agent:

- Product Agent

Supporting agents:

- FinOps Agent
- Connector Agent
- Security Agent
- QA Agent

Inputs:

- MVP Blueprint
- Vertical Slice
- ROI Slice
- Manual Evidence Pack
- Core Domain Model
- Screen Contract

Output:

- final Phase 1 case brief for `DRC-AOA-001`,
- one-page user story,
- evidence-to-claim table,
- owner, approver and reviewer path,
- final recommendation wording.

Gate:

- a reviewer can explain the case without external narration.

Stop if:

- the case needs several recommendations,
- the case cannot explain why the decision exists,
- the recommendation cannot point to evidence and ROI assumptions.

### Stage 02 - Acceptance Contract

Lead agent:

- QA Agent

Supporting agents:

- Product Agent
- Security Agent
- FinOps Agent
- Backend Agent
- Frontend Agent

Inputs:

- MVP Acceptance Test Plan
- Quality Attributes
- Phase 1 Scope and Exit Criteria
- Screen Contract
- Security Threat Model

Output:

- Phase 1 acceptance checklist,
- negative test checklist,
- demo pass/fail criteria,
- traceability from each acceptance case to source documents.

Gate:

- every future implementation task maps to at least one acceptance criterion.

Stop if:

- a feature has no acceptance test,
- a test requires post-MVP infrastructure,
- acceptance depends on AI judgment instead of deterministic rules.

### Stage 03 - Architecture Scaffolding Plan

Lead agent:

- CTO Agent

Supporting agents:

- Backend Agent
- Frontend Agent
- Security Agent
- QA Agent

Inputs:

- MVP Implementation Standard
- Phase 1 Scope and Exit Criteria
- Technical Architecture Context
- Database Model
- API Specification
- Connector Framework

Output:

- minimal module map,
- hexagonal boundary plan,
- allowed ports and adapters list,
- forbidden infrastructure list,
- first repository layout proposal if scaffolding is authorized.

Gate:

- architecture can prove the MVP without microservices, Kafka, Kubernetes, Terraform, Graph DB/vector stores, public APIs or SDKs.

Stop if:

- a bounded context is turned into a microservice by default,
- provider-specific logic enters the domain,
- the plan creates broad API or connector surfaces.

### Stage 04 - Data And Persistence Slice

Lead agent:

- Backend Agent

Supporting agents:

- FinOps Agent
- Security Agent
- QA Agent

Inputs:

- Core Domain Model
- Database Model
- Decision Ledger v2
- ROI Slice
- Phase 1 Scope and Exit Criteria

Output:

- minimal persistence concept for one Decision ROI Case,
- evidence summary and lineage plan,
- ROI assumptions persistence plan,
- one recommendation persistence plan,
- ledger-entry persistence plan.

Gate:

- no persistent object exists unless it supports the first Decision ROI Case.

Stop if:

- the model adds full audit tables,
- complex versioning appears before it is needed,
- multi-tenancy admin or generic workflow tables appear.

### Stage 05 - Evidence Intake Slice

Lead agent:

- Connector Agent

Supporting agents:

- Backend Agent
- Security Agent
- FinOps Agent
- Product Agent
- QA Agent

Inputs:

- Manual Evidence Pack
- Connector Framework
- Per-Connector MVP Contracts
- Event/Evidence Vocabulary
- Security Threat Model
- Phase 1 Scope and Exit Criteria

Output:

- selected first source mode,
- one narrow read-only adapter or manual/import evidence path,
- source-to-evidence mapping,
- freshness and failure handling,
- sensitivity labels,
- connector non-goals.

Gate:

- evidence can support the case without raw prompts, raw completions, secrets or provider mutation.

Stop if:

- connector owns ROI,
- connector creates recommendations,
- connector writes to the ledger,
- connector requires broad provider permissions.

### Stage 06 - Domain, ROI And Recommendation Slice

Lead agent:

- Backend Agent

Supporting agents:

- FinOps Agent
- Product Agent
- QA Agent

Inputs:

- Core Domain Model
- ROI Slice
- Acceptance Plan
- Phase 1 Scope and Exit Criteria

Output:

- deterministic use-case flow,
- ROI formula implementation plan,
- assumptions handling,
- confidence and risk handling,
- one recommendation rule.

Gate:

- the recommendation is deterministic, evidence-backed and explainable without LLM judgment.

Stop if:

- AI calculates financial truth,
- multiple recommendations or ranking are needed,
- ROI numbers lack evidence, period or assumptions.

### Stage 07 - AI Explanation Slice

Lead agent:

- AI Agent

Supporting agents:

- Security Agent
- Product Agent
- FinOps Agent
- QA Agent

Inputs:

- AI Agent Context Pack
- Security Threat Model
- Manual Evidence Pack
- ROI Slice
- Phase 1 Scope and Exit Criteria

Output:

- prepared-context contract,
- prompt boundary,
- evidence citation rule,
- failure behavior when AI explanation is unavailable,
- proof that AI does not mutate data or decide.

Gate:

- AI explains the deterministic recommendation and cites evidence IDs.

Stop if:

- AI reads raw provider payloads,
- AI modifies persistent data,
- AI executes business rules,
- AI approves, rejects, defers, marks implementation or validates result.

### Stage 08 - Ledger And Approval Slice

Lead agent:

- Backend Agent

Supporting agents:

- Security Agent
- Product Agent
- FinOps Agent
- QA Agent

Inputs:

- Decision Ledger v2
- Identity, Access and Approval Model
- Event/Evidence Vocabulary
- Acceptance Plan
- Phase 1 Scope and Exit Criteria

Output:

- ledger-state plan,
- approval/rejection/deferral command behavior,
- evidence/ROI/assumption snapshot rules,
- result-validation boundary,
- role authority mapping.

Gate:

- approval history is append-only and preserves reviewed context.

Stop if:

- ledger calculates ROI,
- ledger executes provider changes,
- historical entries can be rewritten,
- Admin or AI becomes business approver by default.

### Stage 09 - Decision Review Workspace Slice

Lead agent:

- Frontend Agent

Supporting agents:

- Product Agent
- Security Agent
- QA Agent
- Backend Agent

Inputs:

- Decision Review Workspace Screen Contract
- API Specification
- Acceptance Plan
- Quality Attributes
- Identity, Access and Approval Model

Output:

- first screen implementation plan,
- required UI sections,
- hidden/blocked states,
- action-state matrix,
- evidence visibility matrix,
- no-dashboard expansion list.

Gate:

- the screen answers: can we trust and approve this recovery action?

Stop if:

- the UI becomes a broad executive dashboard,
- it shows realized value before validation,
- it exposes Restricted data,
- it invents actions not in the identity/approval model.

### Stage 10 - Auth, Security And Observability Slice

Lead agent:

- Security Agent

Supporting agents:

- Backend Agent
- QA Agent
- CTO Agent

Inputs:

- Security Threat Model
- Identity, Access and Approval Model
- Quality Attributes
- MVP Implementation Standard
- Phase 1 Scope and Exit Criteria

Output:

- first auth mode,
- minimum role mapping,
- sensitivity enforcement checklist,
- log categories,
- health endpoints,
- minimal metrics,
- no Restricted data in logs or AI context.

Gate:

- a user can authenticate, actions are role-aware and basic system state is observable.

Stop if:

- full enterprise SSO is required,
- multiple OAuth providers are required,
- full Grafana/OpenTelemetry stack becomes prerequisite,
- logs contain secrets, raw prompts or customer conversations.

### Stage 11 - Integrated Vertical Demo

Lead agent:

- QA Agent

Supporting agents:

- CTO Agent
- Product Agent
- Backend Agent
- Frontend Agent
- Connector Agent
- AI Agent
- Security Agent
- FinOps Agent

Inputs:

- all previous stage outputs,
- Acceptance Plan,
- Phase 1 Exit Criteria,
- R&D Evidence Dossier.

Output:

- end-to-end demo checklist,
- pass/fail record,
- unresolved issue list,
- R&D evidence entries for real work performed,
- recommendation for close, iterate or defer.

Gate:

- user authenticates,
- one source is connected or imported,
- one Decision ROI Case runs end to end,
- one deterministic recommendation appears,
- AI explanation appears and cites evidence,
- ledger records review state,
- logs, metrics, `/health` and `/ready` are available.

Stop if:

- demo success depends on manual narration outside the product,
- acceptance tests fail without a recorded decision,
- the system must add post-MVP infrastructure to pass.

### Stage 12 - Phase 1 Closure And Phase 2 Candidates

Lead agent:

- CTO Agent

Supporting agents:

- Product Agent
- QA Agent
- FinOps Agent
- Security Agent

Inputs:

- Integrated demo results,
- Decision Log,
- R&D Evidence Dossier,
- unresolved risks,
- acceptance results.

Output:

- Phase 1 closure note,
- pass/fail against `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`,
- Phase 2 RFC candidates,
- explicit list of deferred features.

Gate:

- Phase 1 is closed only if the exit criteria in `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` pass.

Stop if:

- realized Business Value is claimed before result validation,
- Phase 2 work starts without closing Phase 1,
- deferred infrastructure is added to hide product gaps.

## Parallel Work Guidance

Run these stages strictly sequentially:

1. Stage 00 - Context Control And Go/No-Go
2. Stage 01 - Product Case Lock
3. Stage 02 - Acceptance Contract
4. Stage 03 - Architecture Scaffolding Plan

After Stage 03, these can proceed in parallel if each agent has a clear brief:

- Stage 04 - Data And Persistence Slice
- Stage 05 - Evidence Intake Slice
- Stage 07 - AI Explanation Slice
- Stage 09 - Decision Review Workspace Slice
- Stage 10 - Auth, Security And Observability Slice

These require integration order:

1. Stage 06 - Domain, ROI And Recommendation Slice
2. Stage 08 - Ledger And Approval Slice
3. Stage 11 - Integrated Vertical Demo
4. Stage 12 - Phase 1 Closure And Phase 2 Candidates

## Agent Brief Template

Use this template before assigning work to an autonomous agent:

```text
Agent:
Stage:
Objective:
Authority documents:
Inputs:
Expected output:
Must not:
Acceptance gate:
Handoff target:
Decision-log impact:
R&D evidence impact:
```

## Handoff Template

Every handoff should include:

```text
From agent:
To agent:
Stage:
Completed output:
Open assumptions:
Blocked items:
Evidence IDs involved:
Security sensitivity:
ROI impact:
Ledger impact:
Acceptance tests affected:
Decision needed:
```

## Stop Conditions For Any Agent

Stop and hand off to CTO Agent if a task requires:

- more than one Decision ROI Case,
- more than one recommendation,
- ranking,
- learning systems,
- autonomous execution,
- raw prompts or completions,
- provider write permissions,
- public APIs or SDKs,
- Kafka,
- Kubernetes,
- Terraform,
- Graph DB/vector/search infrastructure,
- full observability platform,
- multiple OAuth providers,
- broad dashboard surfaces,
- new domain entities not present in the Core Domain Model,
- changes to approval authority,
- changes to realized-value semantics.

## R&D Evidence Discipline

Once real Phase 1 work starts, every stage should capture evidence only for actual activity performed.

Record:

- date,
- contributor or agent,
- objective,
- source documents used,
- technical object touched or produced,
- time spent if known,
- tests or checks executed,
- result,
- unresolved uncertainty,
- next action.

Do not invent hours, tests, implementation results or technical uncertainty after the fact.

Use `docs/rnd/30_RD_Activity_Evidence_Dossier.md` as the governing model.

## Current Closure Status

The Phase 1 stage dossier is complete as documentation and context control:

```text
agents/phase1/12_phase1_closure.md
```

This means the MVP is ready for future limited implementation scaffolding when the founder explicitly requests it.

It does not mean software has been built.

Next implementation work must remain inside the locked Phase 1 flow and use the relevant stage file before creating code.
