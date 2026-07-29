# IMPERATOR — Strategic Documentation System

Status: **Design Phase Closed; Phase 2 PostgreSQL Persistence Certified; Sprint 2.8 REST Adapter Not Started**

This documentation system defines IMPERATOR’s canonical context for:
1. human strategic execution,
2. consistent AI-agent collaboration,
3. controlled evolution from idea phase to product phase.

## Current Physical State

The repository now contains:
- `backend-java/`
- `backend-python/`
- `frontend/`
- `database/`
- `infra/`
- `scripts/`
- `samples/decision-cases/ai-onboarding-assistant/`

Implemented Java foundation exists under `backend-java/`:
- pure domain objects and value objects,
- application use cases,
- inbound and outbound ports,
- application exceptions,
- application data-boundary policy,
- Maven Wrapper build on Java 21,
- JDBC PostgreSQL repository adapters and explicit transaction runner,
- Flyway V1 schema,
- real-PostgreSQL repository, constraint, lifecycle and transaction tests.

The repository still does not contain Spring Boot, a production composition
root, REST controllers, connector logic, real AI integrations, Docker runtime,
JWT runtime, React app code or production deployment.

## Rule Zero

Every project folder must answer four questions:
- Why does it exist?
- Who uses it?
- What does it contain?
- What must it never contain?

Top-level implementation folders answer these questions in their local
`README.md`. A folder without an explicit boundary is not an implementation
target for an autonomous agent.

## Baseline v1.0

Documents `31` through `38` are the Phase 2 implementation authority baseline.
They are frozen for Phase 2.

They may change only if implementation proves an objective contradiction. Any
change must state:
- the contradiction found,
- the implementation evidence,
- the affected decision or contract,
- the approved correction.

Current implementation-evidence clarification:
- `D071` clarifies that Phase 2 forbids recommendation-engine behavior, ROI,
  real AI and live connectors, while explicitly scoped Java application use
  cases may create/link/review/append domain objects through ports.

## Index

Start here:
- [docs/README.md](docs/README.md)

Business:
- [Project Charter](docs/business/00_Project_Charter.md)
- [Vision and Positioning](docs/business/01_Vision_and_Positioning.md)
- [Problem and Market Context](docs/business/02_Problem_and_Market_Context.md)
- [ICP and Buyer Personas](docs/business/03_ICP_and_Buyer_Personas.md)
- [Value Proposition](docs/business/04_Value_Proposition.md)
- [Go-To-Market Hypotheses](docs/business/08_Go_To_Market_Hypotheses.md)
- [Executive Summary](docs/business/19_Executive_Summary.md)

Product:
- [MVP Blueprint](docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md)
- [Visual Operational Explainer](docs/product/23_IMPERATOR_Visual_Operational_Explainer.md)
- [MVP Vertical Slice](docs/product/24_MVP_Vertical_Slice.md)
- [MVP ROI Slice](docs/product/25_MVP_ROI_Slice.md)
- [Manual Evidence Pack - AI Onboarding Assistant](docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md)
- [MVP Acceptance Test Plan](docs/product/27_MVP_Acceptance_Test_Plan.md)
- [Identity, Access and Approval Model](docs/product/28_Identity_Access_Approval_Model.md)
- [Decision Review Workspace Screen Contract](docs/product/29_Decision_Review_Workspace_Screen_Contract.md)
- [Core Domain Model](docs/product/CORE_DOMAIN_MODEL.md)
- [API Specification](docs/product/API_SPECIFICATION.md)
- [Decision Ledger v2](docs/product/DECISION_LEDGER_V2.md)
- [Core Capabilities](docs/product/06_Core_Capabilities.md)
- [Product Surface and Context Engine Thesis](docs/product/17_Product_Surface_and_Context_Engine_Thesis.md)
- [Glossary and Canonical Language](docs/product/13_Glossary_and_Canonical_Language.md)

Architecture:
- [Technical Architecture Context](docs/architecture/21_Technical_Architecture_Context.md)
- [Technical Investor Audit](docs/architecture/22_Technical_Investor_Audit.md)
- [MVP Project Structure](docs/architecture/24_MVP_Project_Structure.md)
- [Pre-Code Architecture Readiness Audit](docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md)
- [Security, Data Governance and Threat Model](docs/architecture/26_Security_Data_Governance_Threat_Model.md)
- [Quality Attributes](docs/architecture/27_Quality_Attributes.md)
- [Per-Connector MVP Contracts](docs/architecture/28_Per_Connector_MVP_Contracts.md)
- [Event and Evidence Vocabulary](docs/architecture/29_Event_Evidence_Vocabulary.md)
- [Phase 0 Closure Readiness Review](docs/architecture/30_Phase_0_Closure_Readiness_Review.md)
- [MVP Implementation Standard](docs/architecture/31_MVP_Implementation_Standard.md)
- [Phase 1 MVP Scope and Exit Criteria](docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md)
- [Phase 1 Foundational Implementation Decisions](docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md)
- [MVP Implementation Blueprint](docs/architecture/34_MVP_Implementation_Blueprint.md)
- [Coding Principles](docs/architecture/35_Coding_Principles.md)
- [Phase 2 Platform Foundation Blueprint](docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md)
- [Implementation Contract](docs/architecture/37_Implementation_Contract.md)
- [Sprint 0 Contract Gate Report](docs/architecture/38_Sprint_0_Contract_Gate_Report.md)
- [Architecture Thesis](docs/architecture/18_Architecture_Thesis.md)
- [Database Model](docs/architecture/DATABASE_MODEL.md)
- [Connector Framework](docs/architecture/CONNECTOR_FRAMEWORK.md)
- [Phase 0 Guidelines](docs/architecture/phase0-guidelines.md)
- [Repository Structure](docs/architecture/structure.md)

AI:
- [AI Agent Context Pack](docs/ai/12_AI_Agent_Context_Pack.md)
- [One-Page Project Prompt ES](docs/ai/IMPERATOR_One_Page_Project_Prompt_ES.md)
- [AI Weekly Context Toning Prompt](docs/ai/AI_Weekly_Context_Toning_Prompt_Imperator_v2.md)

Agent work model:
- [AI Agent Operating Model](agents/README.md)
- [Phase 1 Agentic MVP Creation Guide](agents/phase1/README.md)
- [Phase 2 Platform Foundation Sprint Plan](agents/phase2/README.md)

Decisions and RFCs:
- [Decision Log](docs/decisions/14_Decision_Log.md)
- [Architecture Decision Records](docs/decisions/adr/)
- [RFCs](docs/rfcs/)
  - [RFC 0001 — Knowledge Graph Model](docs/rfcs/0001-knowledge-graph-model.md)
  - [RFC 0002 — Module Communication Architecture](docs/rfcs/0002-module-communication-architecture.md)

Research:
- [Hypothesis Template](docs/research/Hypothesis.md)
- [Experiment Record Template](docs/research/Experiment_Record.md)

I+D / R&D evidence:
- [I+D Evidence Documentation](docs/rnd/README.md)
- [I+D Activity Evidence Dossier](docs/rnd/30_RD_Activity_Evidence_Dossier.md)
- [Activity Log Template](docs/rnd/templates/01_ACTIVITY_LOG_TEMPLATE.md)
- [Technical Object Register Template](docs/rnd/templates/02_TECHNICAL_OBJECT_REGISTER_TEMPLATE.md)
- [Experiment and Test Record Template](docs/rnd/templates/03_EXPERIMENT_TEST_RECORD_TEMPLATE.md)
- [Monthly I+D Summary Template](docs/rnd/templates/04_MONTHLY_RD_SUMMARY_TEMPLATE.md)

## Canonical Definition

**IMPERATOR is an Operating System for Operational Intelligence.**

Commercial category:
- **Enterprise Decision Intelligence Platform**

MVP product model:
- **Decision ROI Platform for Executive Operational Intelligence**

It manages decisions across cloud, code and AI systems, finds hidden operational money inside shipped technology decisions, and explains which action leadership should approve to recover it.

MVP technical rule:
- **One Decision. One Timeline. One ROI.**

Current paid wedge:
- **Decision Recovery Workflow for AI/cloud spend**

Canonical positioning terms:
- Operating System for Operational Intelligence
- Enterprise Decision Intelligence Platform
- Decision ROI Platform
- Enterprise Context Intelligence

Canonical MVP blueprint:
- [docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md](docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md)

Canonical domain model:
- [docs/product/CORE_DOMAIN_MODEL.md](docs/product/CORE_DOMAIN_MODEL.md)

Canonical conceptual API:
- [docs/product/API_SPECIFICATION.md](docs/product/API_SPECIFICATION.md)

Canonical Decision Ledger module:
- [docs/product/DECISION_LEDGER_V2.md](docs/product/DECISION_LEDGER_V2.md)

Canonical technical architecture context:
- [docs/architecture/21_Technical_Architecture_Context.md](docs/architecture/21_Technical_Architecture_Context.md)

Current technical investor audit:
- [docs/architecture/22_Technical_Investor_Audit.md](docs/architecture/22_Technical_Investor_Audit.md)

Current pre-code architecture readiness audit:
- [docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md](docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md)

Current security and data-governance context:
- [docs/architecture/26_Security_Data_Governance_Threat_Model.md](docs/architecture/26_Security_Data_Governance_Threat_Model.md)

Current MVP acceptance test plan:
- [docs/product/27_MVP_Acceptance_Test_Plan.md](docs/product/27_MVP_Acceptance_Test_Plan.md)

Current identity, access and approval model:
- [docs/product/28_Identity_Access_Approval_Model.md](docs/product/28_Identity_Access_Approval_Model.md)

Current Decision Review Workspace screen contract:
- [docs/product/29_Decision_Review_Workspace_Screen_Contract.md](docs/product/29_Decision_Review_Workspace_Screen_Contract.md)

Current quality attributes:
- [docs/architecture/27_Quality_Attributes.md](docs/architecture/27_Quality_Attributes.md)

Current per-connector MVP contracts:
- [docs/architecture/28_Per_Connector_MVP_Contracts.md](docs/architecture/28_Per_Connector_MVP_Contracts.md)

Current event and evidence vocabulary:
- [docs/architecture/29_Event_Evidence_Vocabulary.md](docs/architecture/29_Event_Evidence_Vocabulary.md)

Current AI-agent work partition:
- [agents/README.md](agents/README.md)

Current Phase 1 agentic build process:
- [agents/phase1/README.md](agents/phase1/README.md)

Current I+D/R&D evidence dossier:
- [docs/rnd/30_RD_Activity_Evidence_Dossier.md](docs/rnd/30_RD_Activity_Evidence_Dossier.md)

Current Phase 0 closure review:
- [docs/architecture/30_Phase_0_Closure_Readiness_Review.md](docs/architecture/30_Phase_0_Closure_Readiness_Review.md)

Current MVP implementation standard:
- [docs/architecture/31_MVP_Implementation_Standard.md](docs/architecture/31_MVP_Implementation_Standard.md)

Current Phase 1 MVP scope and exit criteria:
- [docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md](docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md)

Current coding principles:
- [docs/architecture/35_Coding_Principles.md](docs/architecture/35_Coding_Principles.md)

Current Phase 2 Platform Foundation blueprint:
- [docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md](docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md)

Current implementation contract:
- [docs/architecture/37_Implementation_Contract.md](docs/architecture/37_Implementation_Contract.md)

Current Phase 2 sprint plan:
- [agents/phase2/README.md](agents/phase2/README.md)

Current Sprint 0 contract gate:
- [docs/architecture/38_Sprint_0_Contract_Gate_Report.md](docs/architecture/38_Sprint_0_Contract_Gate_Report.md)

MVP information domains:
- Business Context: Jira
- Code & Deployment: GitHub
- Infrastructure & Cost: AWS
- AI Consumption: OpenAI + Anthropic Claude

MVP paid-wedge recommendation focus:
- AI model downgrade or model change for `DRC-AOA-001`

Deferred recommendation families:
- unused AI agent removal
- underutilized AWS resource detection tied to the same Decision ROI Case
- negative-ROI feature identification
- duplicated service or agent consolidation

## Context Authority

When documents or prompts conflict, use this order:

1. `docs/decisions/14_Decision_Log.md` for accepted decisions and chronology.
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundary.
3. `docs/product/24_MVP_Vertical_Slice.md` for the first end-to-end MVP path.
4. `docs/product/25_MVP_ROI_Slice.md` for ROI calculation, assumptions and validation rules.
5. `docs/product/CORE_DOMAIN_MODEL.md` for domain entities, relationships and invariants.
6. `docs/product/API_SPECIFICATION.md` for conceptual API surface.
7. `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
8. `docs/architecture/21_Technical_Architecture_Context.md` for target architecture context.
9. `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
10. `docs/architecture/CONNECTOR_FRAMEWORK.md` for future integration rules.
11. `docs/architecture/26_Security_Data_Governance_Threat_Model.md` for evidence sensitivity, AI boundaries, permissions and security threat model.
12. `docs/product/28_Identity_Access_Approval_Model.md` for roles, permissions and approval authority.
13. `docs/product/27_MVP_Acceptance_Test_Plan.md` for pre-code MVP acceptance gates.
14. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for first MVP screen behavior.
15. `docs/architecture/27_Quality_Attributes.md` for MVP non-functional quality expectations.
16. `docs/architecture/28_Per_Connector_MVP_Contracts.md` for source objects, evidence, permissions, freshness, sensitivity and failures per MVP connector.
17. `docs/architecture/29_Event_Evidence_Vocabulary.md` for canonical event names, evidence types, blockers, lifecycle states and labels.
18. `docs/rfcs/0002-module-communication-architecture.md` for module communication rationale.
19. `agents/README.md` for AI-agent work partition and handoff rules.
20. `docs/ai/12_AI_Agent_Context_Pack.md` for AI-agent operating rules.
21. `docs/rnd/30_RD_Activity_Evidence_Dossier.md` for documenting future development evidence, hours, objects and tests.
22. `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` for final Phase 0 readiness gates and go/no-go control.
23. `docs/architecture/31_MVP_Implementation_Standard.md` for future MVP implementation standards: hexagonal boundaries, reduced scope, auth direction and minimal observability.
24. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` for the exact Phase 1 objective, data limit, connector limit, AI boundary, scaffolding authorization and exit criteria.
25. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` for Canonical Evidence Model, PostgreSQL, Explanation Provider, JWT/RBAC and Decision Graph.
26. `docs/architecture/34_MVP_Implementation_Blueprint.md` for the final six-week implementation contract.
27. `docs/architecture/35_Coding_Principles.md` for implementation coding rules, hexagonal boundaries and AI-agent coding discipline.
28. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` for the controlled Phase 2 foundation scope.
29. `docs/architecture/37_Implementation_Contract.md` for mandatory implementation mechanics, layer rules and one-module agent execution.
30. `agents/phase2/README.md` for Phase 2 sprint sequencing and precise agent prompt control.
31. `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` for the current Phase 2 go/no-go decision and first-agent instruction.
32. `docs/product/13_Glossary_and_Canonical_Language.md` for terms and wording.
33. Older documents, demos, prompts, ADRs and RFC drafts as historical context unless updated by the documents above.

Founder-mode or master-prompt instructions define ambition and quality bar. They do not override the current repository decisions when they mention older framing such as AI Cost Attribution as the primary wedge, dashboard-led product language, generic Decision Intelligence without the operating-system and Decision ROI framing, or production-ready implementation during Phase 0.

## Documentation Evolution Rule

Any strategic, product, domain, API or architecture change must update:
1. `docs/decisions/14_Decision_Log.md` first
2. the impacted document(s) second
