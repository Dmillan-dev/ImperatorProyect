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

This folder is the operating map for bounded AI-agent work. It is not
implementation code or an orchestration runtime. Authorization comes from the
current project gate, not from the presence of an agent prompt.

Current control:

- `docs/project/PROJECT_STATUS.md`
- `docs/project/PHASE_AND_SPRINT_MAP.md`
- `docs/decisions/14_Decision_Log.md`

Phase 1 staged agentic work is defined in:

- `agents/phase1/README.md`

Phase 2 sprint execution is defined in:

- `agents/phase2/README.md`

Active Phase 3 sprint execution is defined in:

- `agents/phase3/README.md`

## Completed Phase Records

- Phase 0 strategy and architecture readiness is complete.
- `agents/phase1/12_phase1_closure.md` closes the Phase 1 documentation and
  control dossier.
- Phase 2 Platform Foundation is complete under D079. Sprints 2.9 through 2.13
  are deferred, not completed.
- Phase 0, Phase 1 and Phase 2 agent files are historical inputs. They do not
  authorize the current sprint.

## Current Lifecycle Rule

Phase 3 is named `First Business Value Loop` and is active under D079.

The current authorized gate is maintained in
`docs/project/PROJECT_STATUS.md`. At the time of this update D096 is frozen and
Sprint 4.4 Observability implementation is the sole authorized current gate.

Phase 3 agents may implement only the bounded `DRC-AOA-001` sequence in
`agents/phase3/README.md`. Sprint 3.0 runtime composition and Sprint 3.1 JSONL
Evidence Import are certified and complete. Sprint 3.2 deterministic Decision
creation and Sprint 3.3 deterministic Recommendation and ROI policy are also
certified and complete. Sprint 3.3.1 provider-neutral Explanation integration
is complete with post-transaction invocation and failure isolation. Sprint 3.4
Human Review, Ledger and Result Validation is certified against PostgreSQL
18.2, and the D084 deferred obligation is discharged. Sprint 3.5 has certified
the deterministic local Business Value workflow. Sprint 3.6 has certified the
Java 21 Maven backend through a real GitHub Actions run. D085 freezes the
remaining delivery roadmap, D086 freezes the Functional REST contract and
Sprint 3.7 has certified all 15 routes through real PostgreSQL composition.
D087 freezes the JWT contract, and Sprint 3.8 has certified the RS256/JWKS
Resource Server perimeter and JWT-derived actor identity against PostgreSQL
18.2. D088 freezes the RBAC contract, and Sprint 3.9 has certified the exact
15-route/four-role matrix, governance separation and Evidence redaction against
PostgreSQL 18.4. D089 freezes the GitHub contract, and Sprint 4.0 has certified
the read-only GitHub-to-Evidence synchronization with 128 default tests and 30
integration tests. D090 freezes the AWS contract, and Sprint 4.1 has certified
the read-only AWS-to-Evidence synchronization with 139 default tests and 31
integration tests. D091 freezes the Decision Review Workspace contract, and
Sprint 4.2 has certified the thin, single-case Decision Review and Business
Value surface with frontend quality gates, production build, Playwright
acceptance and unchanged backend/PostgreSQL regression. D092-D095 govern the
Docker runtime, D093 composition, PostgreSQL supply-chain remediation and
identity-evidence boundary. Sprint 4.3 is certified with 151 default tests, 34
PostgreSQL integration tests, hardened SHA-tagged images, local JWT/RBAC E2E
and persistence after recreation. D096 freezes the minimum observability
contract. Sprint 4.4 implementation is the sole authorized current gate and
may address only Document 56. External Keycloak HTTPS
conformance remains mandatory before Sprint 4.5.

The completed Phase 2 foundation contains:

- project structure,
- module boundaries,
- route shells,
- persistence foundation,
- Spring Boot web runtime.

The D085 delivery sequence is:

- Functional REST API;
- JWT Authentication and RBAC Authorization;
- GitHub and AWS integrations;
- the thin, single-case Decision Review and Business Value dashboard;
- Docker production runtime and observability;
- Pilot Readiness and MVP Release.

Phase 3 agents must not create:

- behavior owned by a later sprint,
- provider connectors outside the current gate,
- additional cases or recommendation families,
- AI decision authority,
- unauthorized schema, route or aggregate changes,
- production deployment.

Use D079 and `agents/phase3/README.md` as the current execution authority.
Use `docs/architecture/34_MVP_Implementation_Blueprint.md` as the value-loop contract.
Use `docs/architecture/37_Implementation_Contract.md` as the implementation contract.
Use `agents/phase2/README.md` only as the historical Phase 2 sequencing record.
Use `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` only as the
historical Phase 2 entry gate.

No Phase 3 agent may generate more than one bounded deliverable per iteration.

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

## Sprint Execution Roles

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

1. Explicit founder authorization for the current task.
2. `docs/project/PROJECT_STATUS.md` for the current gate.
3. `docs/decisions/14_Decision_Log.md` for accepted decisions.
4. Frozen product and architecture contracts for semantics.
5. `docs/project/PHASE_AND_SPRINT_MAP.md` and `agents/phase3/README.md` for
   execution order.
6. The specialist references required by the current task.
7. Historical prompts and gate reports only as audit evidence.

The task-specific reading matrix lives in `docs/project/README.md`.

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
| Phase 3 vertical-slice control | CTO | All | agents/phase3/README.md |
| Sprint 0 contract gate | CTO | All | Sprint 0 Contract Gate Report |
| Phase 1 staged agentic process | CTO | All | agents/phase1/README.md |
| Phase 1 stage dossier closure | CTO | All | agents/phase1/12_phase1_closure.md |

## Work Control Checklist

Before an agent edits anything:

1. Read `docs/project/PROJECT_STATUS.md`.
2. Confirm the single authorized sprint and module.
3. Identify the owning layer and document class.
4. Load only the task-specific contracts listed in `docs/project/README.md`.
5. Inspect the existing implementation before proposing changes.
6. Stop if a real contradiction requires a frozen-contract change.
7. Keep the implementation inside the authorized file boundary.
8. Update the Decision Log only when a decision is made.
9. Record only real R&D activity and executed verification.
10. Do not start a sprint unless the previous gate is green or explicitly
    waived.

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
