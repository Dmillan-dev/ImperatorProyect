# IMPERATOR — Strategic Documentation System (Phase 0, No Code)

Status: **Idea & Market Validation** (no software implementation)

This documentation system defines IMPERATOR’s canonical context for:
1. human strategic execution,
2. consistent AI-agent collaboration,
3. controlled evolution from idea phase to product phase.

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
- [Core Domain Model](docs/product/CORE_DOMAIN_MODEL.md)
- [API Specification](docs/product/API_SPECIFICATION.md)
- [Decision Ledger v2](docs/product/DECISION_LEDGER_V2.md)
- [Core Capabilities](docs/product/06_Core_Capabilities.md)
- [Product Surface and Context Engine Thesis](docs/product/17_Product_Surface_and_Context_Engine_Thesis.md)
- [Glossary and Canonical Language](docs/product/13_Glossary_and_Canonical_Language.md)

Architecture:
- [Technical Architecture Context](docs/architecture/21_Technical_Architecture_Context.md)
- [Architecture Thesis](docs/architecture/18_Architecture_Thesis.md)
- [Database Model](docs/architecture/DATABASE_MODEL.md)
- [Connector Framework](docs/architecture/CONNECTOR_FRAMEWORK.md)
- [Phase 0 Guidelines](docs/architecture/phase0-guidelines.md)
- [Repository Structure](docs/architecture/structure.md)

AI:
- [AI Agent Context Pack](docs/ai/12_AI_Agent_Context_Pack.md)
- [AI Weekly Context Toning Prompt](docs/ai/AI_Weekly_Context_Toning_Prompt_Imperator_v2.md)

Decisions and RFCs:
- [Decision Log](docs/decisions/14_Decision_Log.md)
- [Architecture Decision Records](docs/decisions/adr/)
- [RFCs](docs/rfcs/)
  - [RFC 0001 — Knowledge Graph Model](docs/rfcs/0001-knowledge-graph-model.md)
  - [RFC 0002 — Module Communication Architecture](docs/rfcs/0002-module-communication-architecture.md)

Research:
- [Hypothesis Template](docs/research/Hypothesis.md)
- [Experiment Record Template](docs/research/Experiment_Record.md)

## Canonical Definition

**IMPERATOR is a Decision ROI Platform for Executive Operational Intelligence.**

It finds hidden operational money inside shipped technology decisions and explains which action leadership should approve to recover it.

MVP technical rule:
- **One Decision. One Timeline. One ROI.**

Canonical intelligence term:
- ✅ Enterprise Context Intelligence
- ❌ Decision Intelligence (as primary category term)

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

MVP information domains:
- Business Context: Jira
- Code & Deployment: GitHub
- Infrastructure & Cost: AWS
- AI Consumption: OpenAI + Anthropic Claude

MVP recommendation families:
- AI model downgrade or model change
- unused AI agent removal
- underutilized AWS resource detection
- negative-ROI feature identification
- duplicated service or agent consolidation

## Context Authority

When documents or prompts conflict, use this order:

1. `docs/decisions/14_Decision_Log.md` for accepted decisions and chronology.
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundary.
3. `docs/product/CORE_DOMAIN_MODEL.md` for domain entities, relationships and invariants.
4. `docs/product/API_SPECIFICATION.md` for conceptual API surface.
5. `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
6. `docs/architecture/21_Technical_Architecture_Context.md` for target architecture context.
7. `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
8. `docs/architecture/CONNECTOR_FRAMEWORK.md` for future integration rules.
9. `docs/rfcs/0002-module-communication-architecture.md` for module communication rationale.
10. `docs/ai/12_AI_Agent_Context_Pack.md` for AI-agent operating rules.
11. `docs/product/13_Glossary_and_Canonical_Language.md` for terms and wording.
12. Older documents, demos, prompts, ADRs and RFC drafts as historical context unless updated by the documents above.

Founder-mode or master-prompt instructions define ambition and quality bar. They do not override the current repository decisions when they mention older framing such as AI Cost Attribution as the primary wedge, dashboard-led product language, Enterprise Decision Intelligence as the current category, or production-ready implementation during Phase 0.

## Documentation Evolution Rule

Any strategic, product, domain, API or architecture change must update:
1. `docs/decisions/14_Decision_Log.md` first
2. the impacted document(s) second
