# IMPERATOR AI Agent Operating Model

## Rule Zero

Purpose:
Define how AI agents should work on IMPERATOR without mixing responsibilities.

Who uses this folder:
- CTO / Product Guardian.
- Architecture Guardian.
- Implementation Agent.
- Quality Agent.
- Context Keeper.

Contains:
- Agent roles.
- Sprint prompts.
- Phase execution plans.
- Review and handoff rules.

Never contains:
- Product runtime code.
- Build manifests.
- Application source.
- Secrets.
- Business data.

## Purpose

Define how AI agents should work on IMPERATOR without mixing responsibilities.

This folder is an operating map for future work. It is not implementation code, not an orchestration runtime and not a permission to create services.

Phase 1 staged agentic work is defined in:

- `agents/phase1/README.md`

Phase 2 sprint execution is defined in:

- `agents/phase2/README.md`

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
10. `docs/architecture/37_Implementation_Contract.md`
11. `agents/phase2/README.md`
12. `docs/rnd/30_RD_Activity_Evidence_Dossier.md`

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
- autonomous, AI-driven or ROI-driven recommendation engine behavior,
- real AI provider calls,
- live provider connectors,
- approval workflow logic outside explicitly authorized application use cases,
- fake business evidence,
- fake ledger state,
- production deployment.

Use `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` as the phase contract.
Use `docs/architecture/37_Implementation_Contract.md` as the implementation contract.
Use `agents/phase2/README.md` as the sprint sequencing guide.
Use `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` as the current GO gate before Sprint 1.

No Phase 2 agent may generate more than one module per iteration.

Current Sprint 0 result:

- `STATUS: GO`
- authorized next action: Sprint 1 - Repository and Project Shell only
- first code-bearing module after Sprint 1 acceptance: `backend-java/domain`

Documentation rule:

- From Sprint 1 onward, create a new document only if it justifies a technical decision required to implement code.
- If an existing contract can absorb the rule, update the existing contract instead.

Sprint gate rule:

- Every sprint must finish with the green-gate table from `docs/architecture/37_Implementation_Contract.md`.
- No sprint may start unless the previous sprint is green or founder/CTO records an explicit exception.
- No sprint may last more than one week.

Golden file rule:

- If an agent wants to create, modify, move or delete a file outside the sprint deliverable, it must stop and ask for authorization.

ASI rule:

- Every sprint reports Architectural Stability Index.
- Sprint 1 target is 100%.
- Sprint 2 target is 100%.
- Sprint 3 target is at least 95%.
- Sprint 4 and later target is at least 95%.

Decision Stability rule:

- Every sprint reports how many prior accepted decisions had to be modified.
- Sprint 1 target is 0.
- Sprint 2 target is 0.
- Sprint 3 target is 0.
- Later sprints also target 0 unless founder/CTO records an exception.

## Phase 2 Sprint Execution Roles

From Sprint 1 onward, do not use one general-purpose agent for implementation.

Use five separated roles:

| Role | Writes code | Purpose | Veto |
|---|---|---|---|
| Architecture Guardian | No | Checks hexagonal architecture, DDD, documents 31-38, dependencies, debt and contract drift. | Yes |
| Implementation Agent | Yes | Implements only the assigned sprint deliverable/module. | No |
| Quality Agent | No product functionality | Reviews naming, complexity, duplication, dead code, imports, coupling, SOLID and Clean Architecture. | Can block acceptance |
| Context Keeper | No implementation code | Synchronizes README, Decision Log and implementation-critical docs only. | Can block documentation sync |
| CTO / Product Guardian | No implementation code | Confirms the change increases MVP product value and rejects premature infrastructure. | Yes |

Default CTO / Product Guardian answer is no for:

- Redis,
- Kafka,
- RabbitMQ,
- Kubernetes,
- Terraform,
- microservices,
- Event Sourcing,
- extra auth providers,
- new connector families,
- new recommendation families.

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
28. `docs/architecture/37_Implementation_Contract.md`
29. `agents/phase2/README.md`
30. `docs/architecture/38_Sprint_0_Contract_Gate_Report.md`
31. `agents/phase1/README.md`
32. `agents/phase1/12_phase1_closure.md`
33. `docs/product/13_Glossary_and_Canonical_Language.md`

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
| Implementation contract | CTO | All | Implementation Contract |
| Phase 2 sprint control | CTO | All | agents/phase2/README.md |
| Sprint 0 contract gate | CTO | All | Sprint 0 Contract Gate Report |
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
15. Use `docs/architecture/37_Implementation_Contract.md` before any implementation task.
16. Use `agents/phase2/README.md` before splitting Phase 2 into sprint/module work.
17. Use `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` before executing Sprint 1.
18. Use `agents/phase1/README.md` before splitting Phase 1 into autonomous agent stages.
19. Use `agents/phase1/12_phase1_closure.md` before starting Phase 2 Platform Foundation or Phase 3 MVP Implementation.
20. Do not create new documentation unless it justifies a technical decision needed to implement code.
21. Do not start a sprint unless the previous sprint gate is green or explicitly waived.

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
