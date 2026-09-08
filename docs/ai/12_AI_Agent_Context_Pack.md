# 12 — AI Agent Context Pack

## Purpose

Ensure consistent output when AI agents collaborate on strategy, documentation, analysis, or planning.

Canonical compact context:

- `docs/ai/IMPERATOR_Project_Context.md`

Current execution control:

- `docs/project/PROJECT_STATUS.md`
- `docs/project/PHASE_AND_SPRINT_MAP.md`

Use this long context pack only when the task needs broad cross-document
reasoning. Do not load it for every bounded implementation task.

## Current State

Phase 1 is complete for a limited MVP. Phase 2 Platform Foundation is complete
under D079: PostgreSQL persistence is certified, Spring Boot web runtime exists,
and the 15-route REST Adapter Foundation is closed. Sprint 3.0 runtime
composition is certified against PostgreSQL 18.2 and complete. Sprint 3.1
JSONL Evidence Import is certified and complete, and the functional import
route persists normalized Evidence through the Application layer. Phase 3 is
active. Sprint 3.2 deterministic Decision creation is certified and complete
with atomic retry and concurrency protection. Sprint 3.3 deterministic
Recommendation and ROI policy is certified and complete with deterministic
financial outputs, immutable retry behavior and PostgreSQL concurrency
protection. Sprint 3.3.1 provider-neutral Explanation integration is complete:
the provider receives bounded deterministic context only after Recommendation
persistence, and unavailability or failure cannot alter business truth. The
Sprint 3.4 Human Review, Ledger and Result Validation implementation is
complete with atomic Decision outcomes, immutable replay, strict Ledger
linearity and deterministic result validation. Its PostgreSQL 18.2 runtime
certification passed during Sprint 3.7, discharging the D084 obligation. Sprint
3.5 End-to-End Local Business Value
Demo is certified and complete: its deterministic 30-line NDJSON dataset drives
the existing workflow through Result Validation into a non-persisted
Application projection, with 89 Java 21 tests passing. Sprint 3.6 Basic Java
CI is certified and complete: GitHub Actions run `30708049322` verified Maven
Wrapper `3.3.4`, Apache Maven `3.9.16`, Eclipse Adoptium Java 21, 89 passing
tests and `BUILD SUCCESS` from commit `c3bb8f6`. D085 freezes the post-CI
delivery roadmap and D086 freezes the Functional REST Application contract.
Sprint 3.7 is certified and complete: Java 21, 98 default tests, PostgreSQL
18.2, Flyway validation and 29 integration tests proved all 15 D086 routes
through real Application and persistence composition. D087 freezes the JWT
Authentication contract. Sprint 3.8 is certified and complete: Java 21 passed
105 default tests, PostgreSQL 18.2 and Flyway passed the full runtime gate, and
29 integration tests proved the stateless RS256/JWKS perimeter plus JWT-derived
governance identity. D088 freezes RBAC authorization. Sprint 3.9 is certified
and complete: Java 21 passed 111 default tests, PostgreSQL 18.4 and Flyway
passed the full runtime gate, and 29 integration tests proved the complete
15-route/four-role matrix, preserved governance authority and Evidence
redaction. D089 freezes the first GitHub integration contract. Sprint 4.0 is
certified and complete: Java 21 passed 128 default tests, PostgreSQL 18.4 and
Flyway passed the full runtime gate, and 30 integration tests proved the
read-only GitHub-to-Evidence flow, stable replay and persistence isolation.
D090 freezes the AWS integration contract. Sprint 4.1 is certified and
complete: Java 21 passed 139 default tests, PostgreSQL 18.4 and Flyway passed
the full runtime gate, and 31 integration tests proved the read-only
AWS-to-Evidence flow, exact account and resource scope, deterministic replay
and credential isolation. D091 freezes the single-case Decision Review
Workspace contract. Sprint 4.2 is certified and complete: Node.js 24.19.0,
npm 11.17.0, format, lint, strict TypeScript, 35 frontend tests, contractual
coverage, production build, dependency audit and Playwright acceptance passed;
the unchanged Java backend also passed 139 default tests and 31 PostgreSQL 18.4
integration tests. Sprint 4.2.1 documentation synchronization is complete.
D092 froze the Docker Production Runtime, D093 authorized and now implements
the single R16 case-composition route, D094 certified the reproducible
PostgreSQL 18.6 supply-chain remediation, and D095 corrected the local runtime
certification boundary. Sprint 4.3 is certified: 151 default tests, 34
PostgreSQL integration tests, final-image security gates, local JWT/RBAC E2E
and persistence after recreation pass. Sprint 4.3.1 documentation
synchronization is complete. D096 freezes the Observability contract. Sprint
4.4 implementation is the sole authorized current gate;
external Keycloak HTTPS conformance remains mandatory before Sprint 4.5.

Agents may create runnable implementation only inside the explicitly
authorized sprint/module. They must use `agents/phase3/README.md`, the frozen
architecture baseline, persistence contracts 39 and 40, and
`docs/decisions/14_Decision_Log.md`. Phase 3 implements only the locked
`DRC-AOA-001` sequence and the current sprint boundary. The one-bounded-
deliverable-per-iteration rule remains mandatory.

From Sprint 1 onward, do not create new documents unless they justify a technical decision required to implement code.

No sprint may begin unless the previous sprint gate is green or explicitly waived by founder/CTO.

From Sprint 1 onward, use separated sprint roles: Architecture Guardian, Implementation Agent, Quality Agent, Context Keeper and CTO/Product Guardian. Only the Implementation Agent writes implementation files. Architecture Guardian and CTO/Product Guardian have veto power.

Every sprint reports ASI. Targets: Sprint 1 = 100%, Sprint 2 = 100%, Sprint 3 >= 95%, Sprint 4 and later >= 95%.

Every sprint reports Decision Stability. Target for Sprint 1, Sprint 2 and Sprint 3 is 0 modified prior decisions.

Golden file rule: if an agent wants to create, modify, move or delete a file outside the sprint deliverable, stop and ask for authorization.

No sprint may last more than one week.

## Semantic Reference Order

Execution authorization follows `docs/project/README.md`: explicit founder
authorization, Project Status, Decision Log, frozen contracts, then the active
phase plan. Within the semantic reference set, use this order:

1. `docs/decisions/14_Decision_Log.md` for accepted decisions and chronology.
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundary.
3. `docs/product/24_MVP_Vertical_Slice.md` for the first end-to-end MVP path.
4. `docs/product/25_MVP_ROI_Slice.md` for ROI calculation, assumptions and validation rules.
5. `docs/product/CORE_DOMAIN_MODEL.md` for domain entities, relationships and invariants.
6. `docs/product/API_SPECIFICATION.md` for conceptual API surface.
7. `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
8. `docs/architecture/21_Technical_Architecture_Context.md` for target architecture context.
9. `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
10. `docs/architecture/CONNECTOR_FRAMEWORK.md` for integration boundaries.
11. `docs/architecture/48_GitHub_Integration_Contract.md` for the frozen first
    connector behavior and its non-authority boundaries.
12. `docs/architecture/49_AWS_Integration_Contract.md` for the frozen second
    connector behavior, account boundary and credential isolation.
13. `docs/architecture/50_Executive_Dashboard_Contract.md` for the certified
    single-case Decision Review Workspace.
14. `docs/architecture/51_Docker_Production_Runtime_Contract.md` together with
    D093-D095 for the certified local runtime and deferred pilot identity gate.
15. `docs/architecture/26_Security_Data_Governance_Threat_Model.md` for evidence sensitivity, AI boundaries and security threat model.
16. `docs/product/28_Identity_Access_Approval_Model.md` for roles, permissions and approval authority.
17. `docs/product/27_MVP_Acceptance_Test_Plan.md` for pre-code acceptance gates.
18. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for first MVP screen behavior.
19. `docs/architecture/27_Quality_Attributes.md` for MVP non-functional quality expectations.
20. `docs/architecture/28_Per_Connector_MVP_Contracts.md` for Jira, GitHub, AWS and OpenAI + Anthropic Claude connector contracts.
21. `docs/architecture/29_Event_Evidence_Vocabulary.md` for event names, evidence types, lifecycle states and blockers.
22. `docs/rfcs/0002-module-communication-architecture.md` for module communication rationale.
23. `agents/README.md` for agent work partition and handoff rules.
24. This file for agent behavior and response consistency.
25. `docs/rnd/30_RD_Activity_Evidence_Dossier.md` for development evidence, hours, objects, experiments and tests.
26. `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` for final Phase 0 readiness gates and go/no-go control.
27. `docs/architecture/31_MVP_Implementation_Standard.md` for future MVP implementation standards.
28. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` for exact Phase 1 objective, minimal data model, connector limit, AI explanation boundary, scaffolding authorization and exit criteria.
29. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` for Canonical Evidence Model, PostgreSQL, Explanation Provider, JWT/RBAC and Decision Graph.
30. `docs/architecture/34_MVP_Implementation_Blueprint.md` for the final six-week implementation contract.
31. `docs/architecture/35_Coding_Principles.md` for coding boundaries, hexagonal layering and AI-agent implementation discipline.
32. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` for Phase 2 Platform Foundation scope.
33. `docs/architecture/37_Implementation_Contract.md` for mandatory implementation rules and one-module agent execution.
34. `agents/phase2/README.md` for historical Phase 2 sprint sequencing and prompt control.
35. `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` for the historical Phase 2 GO gate and first-agent instruction.
36. `agents/phase1/README.md` and `agents/phase1/12_phase1_closure.md` for Phase 1 stage sequencing, closure and future implementation handoff.
37. `docs/product/13_Glossary_and_Canonical_Language.md` for terms and wording.
38. `agents/phase3/README.md` for active Phase 3 sprint sequencing and current vertical-slice boundaries.

Founder-mode or master-prompt guidance sets ambition and quality bar. It does not override current decisions when it uses older framing such as AI Cost Attribution as the primary wedge, dashboard-led language, generic Decision Intelligence without the operating-system and Decision ROI framing, or production-ready implementation during Phase 0.

## Canonical Mandates

1. Use commercial positioning: **Operating System for Operational Intelligence** / **Enterprise Decision Intelligence Platform**.
2. Use intelligence-domain term: **Enterprise Context Intelligence**.
3. Use core narrative: **"We transform operational chaos into business decisions."**
4. Keep long-term ICP: Enterprise SaaS multi-cloud (100–500 employees, 50+ SaaS apps, active AI usage, Platform+DevOps+Security). For v1 paid validation, prefer AWS-first B2B SaaS using Jira, GitHub and OpenAI or Anthropic Claude in production.
5. Keep wedge: Cross-platform Decision Traceability, now expressed through a **Decision ROI Timeline** for the MVP.
6. Emphasize: Multi-stakeholder simultaneous value (Platform Eng → traceability; Finance/FinOps → ROI and costs; Security → risk; CTO → strategic visibility).
7. Emphasize: Neutrality as competitive moat (vs hyperscalers who cannot be neutral).
8. Use MVP rule: **"One Decision. One Timeline. One ROI."**
9. For v1, prioritize information domains over connector breadth:
   - Business Context: Jira
   - Code & Deployment: GitHub
   - Infrastructure & Cost: AWS
   - AI Consumption: OpenAI + Anthropic Claude
10. Defer Slack, Microsoft 365, Salesforce, Azure OpenAI, Google Gemini, Mistral, Azure DevOps, ServiceNow, Azure and GCP as expansion systems unless explicitly needed for a validated pilot.
11. Treat the MVP product surface as **Decision Review Workspace**. The broader `Workspace -> Decision -> Ledger` model is expansion after multiple Decision ROI Cases exist.
12. Do not require full navigation for MVP. Executive Workspace, standalone Ledger, Business Value, Policies and Settings are expansion surfaces.
13. Decision Review Workspace answers: can the company trust and approve this recovery action? It needs decision summary, timeline, evidence, ROI assumptions, recommendation, owner, approver, approve/reject/defer action and ledger history.
14. Decision Detail and Decision Review Workspace are the same MVP product idea unless a later product decision separates them.
15. Decision Ledger answers: what has the company decided over time? It is an immutable ledger of all business decisions, not only recommendations. Ledger v2 records approval, rejection, deferral, implementation and result-validation history without becoming a workflow engine.
16. Business Value answers: what economic value has IMPERATOR generated? It should use validated ledger outcomes and remain separate from estimated recovery.
17. Integrations answers: what operating systems are connected? For MVP, focus on AWS, GitHub, Jira and OpenAI + Anthropic Claude.
18. Use monthly savings in decision queues and annualized value for executive summaries.
19. Prioritize one MVP paid-wedge recommendation: AI model downgrade/change for `DRC-AOA-001`. Defer unused AI agent removal, underutilized AWS resource detection, negative-ROI feature analysis and duplicated service/agent consolidation until the first value loop works.
20. Do not let product surface work drift into chart-heavy analytics or evidence overload.
21. Use `Review Decision` on the executive workspace; use `Approve Recommendation` only inside the decision detail page.
22. Use `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` as the canonical MVP blueprint.
23. Use `docs/product/CORE_DOMAIN_MODEL.md` as the canonical domain model.
24. Use `docs/product/API_SPECIFICATION.md` as the conceptual API contract before implementation.
25. Use `docs/product/DECISION_LEDGER_V2.md` as the Decision Ledger module contract.
26. Use `docs/product/24_MVP_Vertical_Slice.md` as the first end-to-end MVP path.
27. Use `docs/product/25_MVP_ROI_Slice.md` for ROI calculations, assumptions, confidence, risk and result validation.
28. Use `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` as the first manual evidence proof.
29. Use `docs/architecture/26_Security_Data_Governance_Threat_Model.md` for evidence visibility, AI filtering and Restricted-data boundaries.
30. Use `docs/product/27_MVP_Acceptance_Test_Plan.md` as the pre-code acceptance gate.
31. Use `docs/product/28_Identity_Access_Approval_Model.md` for roles, permissions and approval authority.
32. Use `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for what the first MVP screen shows, hides, blocks and records.
33. Use `docs/architecture/27_Quality_Attributes.md` for explainability, auditability, freshness, traceability, latency, resilience, observability and performance non-goals.
34. Use `docs/architecture/28_Per_Connector_MVP_Contracts.md` for Jira, GitHub, AWS and OpenAI + Anthropic Claude source objects, evidence, permissions, freshness, sensitivity and failures.
35. Use `docs/architecture/29_Event_Evidence_Vocabulary.md` for normalized event names, evidence types, lifecycle states, blockers, freshness, sensitivity and confidence labels.
36. Use `agents/README.md` to keep CTO, Product, Connector, Backend, Frontend, AI, Security, FinOps and QA work separated.
37. Use `agents/phase1/README.md` before splitting Phase 1 into autonomous agent stages.
38. Use `docs/rnd/30_RD_Activity_Evidence_Dossier.md` to document future architecture, code, hours, objects, experiments and tests without inventing activity.
39. Use `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` as the final Phase 0 readiness gate before Phase 1 authorization.
40. Use `docs/architecture/31_MVP_Implementation_Standard.md` for future implementation standards: reduced scope, hexagonal architecture, auth direction and minimal observability.
41. Use `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` before proposing Phase 1 scaffolding, data model, connector work, AI explanation flow or exit tests.
42. Use `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` before choosing evidence format, persistence, AI provider boundary, auth model or Decision Graph model.
43. Use `docs/architecture/34_MVP_Implementation_Blueprint.md` before Phase 2 Platform Foundation or Phase 3 MVP implementation.
44. Use `docs/architecture/35_Coding_Principles.md` before creating backend, Python, frontend, database, API, auth, observability or CI code.
45. Use `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` as the frozen authority for Phase 2 Platform Foundation. Phase 2 creates foundation only: no ROI calculation, no autonomous/AI/ROI-driven recommendation engine, no real AI calls, no live connectors and no business rules. Explicitly authorized application-layer contracts may create or link a deterministic Recommendation object without becoming the recommendation engine.
46. Use `docs/architecture/37_Implementation_Contract.md` before any implementation task. No agent may generate more than one module per iteration.
47. Use `agents/phase2/README.md` as the historical Phase 2 sprint record and `agents/phase3/README.md` for active Phase 3 sprint work.
48. Use `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` as the historical Sprint 0 authorization record; use `docs/project/PROJECT_STATUS.md`, D079 and `agents/phase3/README.md` for the current implementation gate.
49. Use `docs/architecture/DATABASE_MODEL.md` as the conceptual database model.
50. Use `docs/architecture/CONNECTOR_FRAMEWORK.md` as the connector expansion model.
51. Use `docs/rfcs/0002-module-communication-architecture.md` for communication design rationale.
52. Use `docs/architecture/21_Technical_Architecture_Context.md` as the canonical architecture context.
53. Do not change category/ICP/wedge/domain/API/database/connector/architecture boundary without logging in `docs/decisions/14_Decision_Log.md`.

## Writing Style

- clear, professional, low-hype
- business and operations oriented
- measurable and actionable

## Guardrails

Do not introduce:
- unvalidated technical claims
- absolute compliance guarantees
- ICP expansion without explicit rationale
- broad connector-roadmap promises before MVP validation
- ROI claims that are not tied to explicit assumptions
- dashboards that prioritize charts over executive decisions
- product screens that repeat the same KPI summary instead of separating prioritization, evidence and audit record
- product surfaces that expand before the Decision Recovery Workflow is validated
- architecture proposals that bypass `docs/architecture/21_Technical_Architecture_Context.md`
- implementation proposals that bypass `docs/architecture/31_MVP_Implementation_Standard.md`
- Phase 1 proposals that bypass `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- scaffolding or implementation proposals that bypass `docs/architecture/34_MVP_Implementation_Blueprint.md`
- implementation work that bypasses `docs/architecture/35_Coding_Principles.md`
- Phase 2 work that bypasses `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
- implementation work that bypasses `docs/architecture/37_Implementation_Contract.md`
- Phase 2 sprint work that bypasses `agents/phase2/README.md`
- Phase 3 work that bypasses D079 or `agents/phase3/README.md`
- Sprint 1 work that bypasses `docs/architecture/38_Sprint_0_Contract_Gate_Report.md`
- agent prompts that ask for more than one implementation module per iteration
- new documents that do not justify an implementation-critical technical decision
- sprint starts without the previous sprint green-gate table passing
- sprint execution that collapses Architecture Guardian, Implementation Agent, Quality Agent, Context Keeper and CTO/Product Guardian into one uncontrolled implementation role
- ASI below target without explicit founder/CTO exception
- Decision Stability above target without explicit founder/CTO exception
- file creation outside the sprint deliverable without explicit authorization
- sprints longer than one week
- staged Phase 1 agent work that bypasses `agents/phase1/README.md`
- architecture proposals that treat conceptual bounded contexts as mandatory Phase 1 microservices
- AI behavior that modifies persistent data, executes business rules, replaces the Decision Engine, approves, rejects, defers, marks implementation or validates results
- multiple recommendations, ranking or learning systems as prerequisites for Phase 1
- agent work that bypasses `agents/README.md` ownership and handoff rules
- event/evidence names that bypass `docs/architecture/29_Event_Evidence_Vocabulary.md`
- I+D/R&D claims, hours or test results that are not evidenced in `docs/rnd/`
- implementation details that violate Phase 0 no-code boundaries

## Implementation references

For consistency between teams and agents, the following guidance applies before and during future implementation. Note: the internal communication mandate D013 requires gRPC + Protocol Buffers for all internal service-to-service communication — this is binding for internal contracts. This mandate does **not** expose gRPC as a public SaaS API.

- Protocol Buffers (`.proto`) is the canonical contract format for internal messages. Maintain `proto/` as source of truth.
- gRPC is the required transport for internal RPCs between Operational, Context, AI and other internal services (see ADR D013 in `docs/decisions/adr/`).
- Kafka messages used as event transport SHOULD be encoded in Protobuf if and when broker-based ingestion is introduced.
- The Context Layer is the canonical input for any AI agent; agents must not read directly from raw sources. Use the Context Engine outputs (Decision Ledger, enriched records) as the agent input.
- Target architecture context lives in `docs/architecture/21_Technical_Architecture_Context.md`. It is authoritative for layer responsibilities, bounded contexts, product surface mapping and future stack direction.
- MVP implementation standard lives in `docs/architecture/31_MVP_Implementation_Standard.md`. It is authoritative for future reduced scope, hexagonal boundaries, auth direction and minimal observability.
- Phase 1 execution contract lives in `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`. It is authoritative for one Decision ROI Case, one deterministic recommendation, limited connector scope, AI-as-explainer-only behavior and final exit criteria.
- Final MVP implementation blueprint lives in `docs/architecture/34_MVP_Implementation_Blueprint.md`. It is authoritative for the MVP value-loop contract used by Phase 2 Platform Foundation and Phase 3 MVP implementation.
- Coding principles live in `docs/architecture/35_Coding_Principles.md`. They are authoritative for future implementation layering, ports/adapters discipline and AI-agent coding rules.
- Phase 2 Platform Foundation blueprint lives in `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`. It is authoritative for creating technical foundation without business intelligence.
- Implementation contract lives in `docs/architecture/37_Implementation_Contract.md`. It is mandatory for layer rules, dependency rules, packages, naming, API behavior, database behavior, events, logging, AI boundaries, security, testing, Git, agent execution and done definitions.
- Phase 2 sprint plan lives in `agents/phase2/README.md` as a completed historical execution record.
- Phase 3 sprint plan lives in `agents/phase3/README.md`. It is mandatory for active vertical-slice sequencing and one-bounded-deliverable prompts.
- Sprint 0 contract gate lives in `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` as the historical Phase 2 entry record. Current authorization lives in `docs/project/PROJECT_STATUS.md`.
- Sprint roles and ASI discipline live in `docs/architecture/37_Implementation_Contract.md`; active sequencing lives in `agents/phase3/README.md`.

Any further changes to this mandate or expansions must be recorded in `docs/decisions/14_Decision_Log.md`.
