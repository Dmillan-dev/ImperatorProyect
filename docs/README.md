# IMPERATOR Documentation Map

This folder separates business, product, architecture, AI, decisions, RFCs, research and I+D/R&D evidence context.

Use this structure to avoid mixing strategic business documents with technical planning documents as the project grows.

## Zones

### `business/`

Commercial, market, buyer and strategy documents.

Use for:
- positioning,
- ICP,
- value proposition,
- GTM,
- business model,
- market risks,
- validation,
- sales narrative,
- executive summary.

### `product/`

Product definition, domain language and product contracts.

Authoritative product documents:
- `20_MVP_Decision_ROI_Platform_Blueprint.md`
- `CORE_DOMAIN_MODEL.md`
- `API_SPECIFICATION.md`
- `DECISION_LEDGER_V2.md`
- `13_Glossary_and_Canonical_Language.md`
- `15_Context_Boundaries_and_Non_Goals.md`

Supporting product explainers:
- `23_IMPERATOR_Visual_Operational_Explainer.md`
- `24_MVP_Vertical_Slice.md`
- `25_MVP_ROI_Slice.md`
- `26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `27_MVP_Acceptance_Test_Plan.md`
- `28_Identity_Access_Approval_Model.md`
- `29_Decision_Review_Workspace_Screen_Contract.md`

### `architecture/`

Conceptual and target technical architecture.

Use for:
- architecture thesis,
- target architecture context,
- technical investor audit,
- MVP project structure,
- pre-code readiness audits,
- security, data governance and threat models,
- quality attributes and non-functional expectations,
- per-connector MVP contracts,
- event and evidence vocabulary,
- Phase 0 closure readiness reviews,
- MVP implementation standards,
- Phase 1 MVP scope and exit criteria,
- Phase 1 foundational implementation decisions,
- final MVP implementation blueprint,
- conceptual database model,
- connector framework,
- Phase 0 repository rules,
- repository structure,
- layer and bounded-context guidance.

### `ai/`

AI-agent operating context and recurring AI collaboration prompts.

Use for:
- agent context packs,
- one-page project prompts,
- weekly context toning prompts,
- AI collaboration guardrails.

### `rfcs/`

Proposal documents for substantial product, domain or architecture evolution.

RFCs should be created before adding new bounded contexts, major data models, product surfaces or cross-team contracts.

Current RFCs:
- `0001-knowledge-graph-model.md`
- `0002-module-communication-architecture.md`

### `decisions/`

Accepted strategic and architectural decisions.

Use for:
- `14_Decision_Log.md`,
- ADRs under `decisions/adr/`.

### `research/`

Templates and records for validation, hypotheses and experiments.

### `rnd/`

I+D/R&D evidence documentation for future startup diligence or accreditation support.

Use for:
- development activity logs,
- hours and contributor traceability,
- technical object registers,
- experiment and test records,
- monthly I+D summaries,
- architecture/code/test evidence once implementation is approved.

## Authority Order

When documents conflict:

1. `decisions/14_Decision_Log.md`
2. `product/20_MVP_Decision_ROI_Platform_Blueprint.md`
3. `product/24_MVP_Vertical_Slice.md`
4. `product/25_MVP_ROI_Slice.md`
5. `product/CORE_DOMAIN_MODEL.md`
6. `product/API_SPECIFICATION.md`
7. `product/DECISION_LEDGER_V2.md`
8. `architecture/21_Technical_Architecture_Context.md`
9. `architecture/DATABASE_MODEL.md`
10. `architecture/CONNECTOR_FRAMEWORK.md`
11. `architecture/26_Security_Data_Governance_Threat_Model.md`
12. `product/28_Identity_Access_Approval_Model.md`
13. `product/27_MVP_Acceptance_Test_Plan.md`
14. `product/29_Decision_Review_Workspace_Screen_Contract.md`
15. `architecture/27_Quality_Attributes.md`
16. `architecture/28_Per_Connector_MVP_Contracts.md`
17. `architecture/29_Event_Evidence_Vocabulary.md`
18. `rfcs/0002-module-communication-architecture.md`
19. `../agents/README.md`
20. `ai/12_AI_Agent_Context_Pack.md`
21. `rnd/30_RD_Activity_Evidence_Dossier.md`
22. `architecture/30_Phase_0_Closure_Readiness_Review.md`
23. `architecture/31_MVP_Implementation_Standard.md`
24. `architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
25. `architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
26. `architecture/34_MVP_Implementation_Blueprint.md`
27. `../agents/phase1/README.md`
28. `../agents/phase1/12_phase1_closure.md`
29. `product/13_Glossary_and_Canonical_Language.md`

## Agent Work Model

The top-level `agents/` folder separates future AI workstreams by responsibility.

Use `../agents/README.md` before assigning work to CTO, Product, Connector, Backend, Frontend, AI, Security, FinOps or QA agents.

Use `../agents/phase1/README.md` before splitting Phase 1 into autonomous agent stages.

Use `../agents/phase1/12_phase1_closure.md` before starting any future Phase 1 implementation scaffolding.

Use `architecture/30_Phase_0_Closure_Readiness_Review.md` as the final Phase 0 readiness gate before authorizing Phase 1 planning or implementation scaffolding.

Use `architecture/31_MVP_Implementation_Standard.md` to keep future Phase 1 work reduced, hexagonal, auth-aware and observable without overbuilding.

Use `architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` to define the exact first-build objective, data-model limit, connector limit, AI explanation boundary, scaffolding authorization and Phase 1 completion criteria.

Use `architecture/33_Phase_1_Foundational_Implementation_Decisions.md` to lock Canonical Evidence Model, PostgreSQL, Explanation Provider, JWT/RBAC and Decision Graph before coding.

Use `architecture/34_MVP_Implementation_Blueprint.md` as the final contract before Phase 2 technical scaffolding.

## Growth Rule

If a new document does not clearly belong in one of these folders, decide its owner before creating it.

Do not place new strategic documents at the repository root unless they are entry points like `README.md`.
