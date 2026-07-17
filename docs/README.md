# IMPERATOR Documentation Map

This folder separates business, product, architecture, AI, decisions, RFCs and research context.

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

### `architecture/`

Conceptual and target technical architecture.

Use for:
- architecture thesis,
- target architecture context,
- technical investor audit,
- conceptual database model,
- connector framework,
- Phase 0 repository rules,
- repository structure,
- layer and bounded-context guidance.

### `ai/`

AI-agent operating context and recurring AI collaboration prompts.

Use for:
- agent context packs,
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

## Authority Order

When documents conflict:

1. `decisions/14_Decision_Log.md`
2. `product/20_MVP_Decision_ROI_Platform_Blueprint.md`
3. `product/CORE_DOMAIN_MODEL.md`
4. `product/API_SPECIFICATION.md`
5. `product/DECISION_LEDGER_V2.md`
6. `architecture/21_Technical_Architecture_Context.md`
7. `architecture/DATABASE_MODEL.md`
8. `architecture/CONNECTOR_FRAMEWORK.md`
9. `rfcs/0002-module-communication-architecture.md`
10. `ai/12_AI_Agent_Context_Pack.md`
11. `product/13_Glossary_and_Canonical_Language.md`

## Growth Rule

If a new document does not clearly belong in one of these folders, decide its owner before creating it.

Do not place new strategic documents at the repository root unless they are entry points like `README.md`.
