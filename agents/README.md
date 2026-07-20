# IMPERATOR AI Agent Operating Model

## Purpose

Define how AI agents should work on IMPERATOR without mixing responsibilities.

This folder is an operating map for future work. It is not implementation code, not an orchestration runtime and not a permission to create services.

Phase 1 staged agentic work is defined in:

- `agents/phase1/README.md`

## Phase 0 Rule

Agents may:

- read and update documentation,
- propose RFCs or ADRs,
- refine contracts,
- prepare manual validation assets,
- review coherence against the MVP.

Agents must not:

- create runnable services,
- create production connectors,
- create source code under `src/`,
- create package manifests,
- generate bindings,
- add credentials,
- add Docker/Kubernetes/Terraform runtime configuration.

## Phase 1 Stage-Dossier Rule

`agents/phase1/12_phase1_closure.md` closes the Phase 1 documentation and control dossier.

Until the founder explicitly asks for implementation scaffolding, agents should continue producing documentation, stage briefs, acceptance plans and handoff artifacts only.

When implementation scaffolding is requested, agents must follow:

1. `agents/phase1/12_phase1_closure.md`
2. `agents/phase1/README.md`
3. the relevant `agents/phase1/NN_stage_file.md`
4. `docs/architecture/31_MVP_Implementation_Standard.md`
5. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
6. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
7. `docs/architecture/34_MVP_Implementation_Blueprint.md`
8. `docs/architecture/35_Coding_Principles.md`
9. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
10. `docs/rnd/30_RD_Activity_Evidence_Dossier.md`

## Phase 2 Platform Foundation Rule

Phase 2 is named `Platform Foundation`.

It may be started only when the founder explicitly asks for implementation of Phase 2.

Phase 2 agents may create technical foundation only:

- project structure,
- module boundaries,
- route shells,
- persistence foundation,
- auth foundation,
- observability foundation,
- Docker/local developer foundation,
- no-deploy CI foundation.

Phase 2 agents must not create:

- ROI calculation,
- recommendation generation,
- real AI provider calls,
- live provider connectors,
- approval workflow logic,
- fake business evidence,
- fake ledger state,
- production deployment.

Use `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` as the phase contract.

## Authority Order

Before acting, every agent must respect:

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
18. `docs/rfcs/0002-module-communication-architecture.md`
19. `docs/ai/12_AI_Agent_Context_Pack.md`
20. `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
21. `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`
22. `docs/architecture/31_MVP_Implementation_Standard.md`
23. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
24. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
25. `docs/architecture/34_MVP_Implementation_Blueprint.md`
26. `docs/architecture/35_Coding_Principles.md`
27. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
28. `agents/phase1/README.md`
29. `agents/phase1/12_phase1_closure.md`
30. `docs/product/13_Glossary_and_Canonical_Language.md`

## Agent Areas

| Agent | Primary ownership | Must not own |
|---|---|---|
| CTO Agent | architecture coherence, decisions, sequencing, technical boundaries | product copy or connector facts alone |
| Product Agent | MVP story, domain meaning, buyer value, screen/product contracts | provider permissions or technical implementation |
| Connector Agent | Jira, GitHub, AWS and AI provider source contracts | ROI math, approval authority or recommendation ownership |
| Backend Agent | future application/module boundaries, API intent, persistence implications | premature microservices or provider-specific business logic |
| Frontend Agent | Decision Review Workspace behavior and future UI structure | domain rules or ledger mutation semantics |
| AI Agent | prepared-context explanation and evidence citation | raw source access, financial truth or decision authority |
| Security Agent | sensitivity, role visibility, AI boundaries, least privilege | ROI ownership or final approval authority |
| FinOps Agent | cost evidence, billing period, currency, ROI validation, realized value | final approval by default |
| QA Agent | acceptance scenarios, quality gates, blocker logic | product strategy or architecture pivots |

## Handoff Rules

| From | To | Handoff trigger |
|---|---|---|
| Product | Connector | Product claim needs source evidence |
| Connector | Security | Evidence contains Confidential, Restricted or unknown sensitivity |
| Connector | FinOps | Cost, currency, billing period or usage/cost attribution is involved |
| Connector | Backend | Source mapping affects future intake/read model |
| Product | Frontend | Screen behavior or user action needs definition |
| Frontend | QA | Screen blocker, empty state or action needs acceptance coverage |
| FinOps | Ledger/Backend | ROI snapshot or result validation needs persistence semantics |
| Security | QA | Access rule or data boundary needs negative test |
| Any agent | CTO | Scope, authority, architecture or decision-log change is needed |

## MVP Work Packages

| Work package | Lead agent | Supporting agents | Canonical docs |
|---|---|---|---|
| Decision ROI Case meaning | Product | CTO, Backend | Core Domain Model, MVP Blueprint |
| Manual evidence proof | Product | Connector, FinOps, Security, QA | Manual Evidence Pack |
| Connector contracts | Connector | Security, FinOps, Backend, Product | Per-Connector MVP Contracts |
| ROI and value validation | FinOps | Product, Ledger/Backend, QA | MVP ROI Slice, Decision Ledger v2 |
| Approval and identity | Security | Product, CTO, QA | Identity/Access/Approval Model |
| Decision Review Workspace | Frontend | Product, Security, QA | Screen Contract |
| Quality gates | QA | CTO, Product, Security, FinOps | Acceptance Plan, Quality Attributes |
| Architecture sequencing | CTO | All | Architecture Context, Readiness Audit |
| MVP implementation standard | CTO | Backend, Frontend, Connector, Security, QA | MVP Implementation Standard |
| Phase 1 execution contract | CTO | All | Phase 1 MVP Scope and Exit Criteria |
| Phase 1 foundational decisions | CTO | Backend, Connector, Security, AI, QA | Phase 1 Foundational Implementation Decisions |
| Phase 1 final implementation blueprint | CTO | All | MVP Implementation Blueprint |
| Coding principles | CTO | Backend, Frontend, Connector, AI, Security, QA | Coding Principles |
| Phase 2 Platform Foundation | CTO | Backend, Frontend, AI, Security, QA | Phase 2 Platform Foundation Blueprint |
| Phase 1 staged agentic process | CTO | All | agents/phase1/README.md |
| Phase 1 stage dossier closure | CTO | All | agents/phase1/12_phase1_closure.md |

## Work Control Checklist

Before an agent edits anything:

1. Identify the owning document.
2. Check authority order.
3. Confirm the change strengthens the first Decision ROI Case.
4. Confirm no new implementation is being created.
5. Update `docs/decisions/14_Decision_Log.md` for strategic, product, domain, API or architecture changes.
6. Update maps and prompts when authority changes.
7. Use `docs/rnd/30_RD_Activity_Evidence_Dossier.md` when future work needs activity, hours, object, experiment or test evidence.
8. Use `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` before recommending Phase 1 authorization.
9. Use `docs/architecture/31_MVP_Implementation_Standard.md` before proposing implementation structure, auth, observability or connector adapters.
10. Use `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` before proposing any Phase 1 scaffolding, data model, connector implementation, AI explanation flow or exit test.
11. Use `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` before choosing evidence format, persistence, AI provider boundary, auth model or graph relationship model.
12. Use `docs/architecture/34_MVP_Implementation_Blueprint.md` before starting Phase 2 Platform Foundation or Phase 3 MVP Implementation.
13. Use `docs/architecture/35_Coding_Principles.md` before creating implementation files.
14. Use `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` before starting Phase 2 Platform Foundation.
15. Use `agents/phase1/README.md` before splitting Phase 1 into autonomous agent stages.
16. Use `agents/phase1/12_phase1_closure.md` before starting Phase 2 Platform Foundation or Phase 3 MVP Implementation.

## Current MVP Boundary

The only MVP flow is:

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

Any agent proposal that requires broad platform surfaces, autonomous execution, public APIs, SDKs, Kafka, Kubernetes, Terraform, Graph DB/vector infrastructure, full production observability platform, multiple OAuth providers or new connectors is post-MVP unless a new decision explicitly changes the scope.

Any Phase 1 proposal that requires more than one Decision ROI Case, multiple recommendations, ranking, learning, broad connector automation or AI as decision-maker violates the Phase 1 scope contract.
