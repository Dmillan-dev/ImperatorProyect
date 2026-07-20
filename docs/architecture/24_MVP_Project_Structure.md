# 24 — MVP Project Structure

## Purpose

Define the future project structure for the first IMPERATOR MVP without creating services, source code or infrastructure yet.

This document is a planning boundary between Phase 0 documentation and future Phase 1 implementation.

It answers:

- what should exist now,
- what should not exist yet,
- what structure should be used once implementation starts,
- how to keep the first build aligned with the MVP vertical slice.

## Phase 0 Rule

During Phase 0, do not create:

- `src/`,
- runnable backend services,
- runnable frontend apps,
- connector implementations,
- Docker runtime configuration,
- Kubernetes manifests,
- Terraform modules,
- Kafka configuration,
- generated bindings,
- production `.env` files,
- database migrations.

Allowed in Phase 0:

- documentation,
- conceptual contracts,
- diagrams,
- validation scripts only if explicitly approved later,
- README scaffolds,
- sample narratives,
- manual pilot artifacts.

## Current Repository Shape

Current structure remains documentation-first:

```text
docs/
  business/
  product/
  architecture/
  ai/
  rfcs/
  decisions/
  research/
  rnd/

demos/
  executive_dashboard_demo/

agents/
  ai/
  backend/
  connectors/
  cto/
  finops/
  frontend/
  product/
  qa/
  security/

services/
  ai/
  backend/
  frontend/

proto/
```

The current `services/` folders are documentation placeholders only.

They are not implementation modules.

## MVP Build Principle

The first implementation should prove one vertical slice:

**Event -> Connector -> Decision Engine -> ROI Engine -> Recommendation -> Decision Ledger -> Decision Review Workspace -> Result Validation**

It should start as a modular monolith or tightly bounded service, not as distributed microservices.

It should follow Hexagonal Architecture / Ports and Adapters:

```text
Controller / Interface
-> Application Use Case
-> Domain
-> Ports
-> Adapters
```

GitHub, Jira, AWS, OpenAI + Anthropic Claude, PostgreSQL and future OAuth providers are adapters. They must not shape the domain model.

## Future Phase 1 Structure

When implementation is explicitly approved, use this structure as the preferred starting point:

```text
imperator/
  docs/
  demos/
  proto/

  src/
    backend/
      app/
      modules/
        intake/
        evidence/
        decision_case/
        roi/
        recommendation/
        ledger/
        identity/
        observability/
      shared/
      tests/

    frontend/
      app/
      components/
      features/
        decision-review/
        evidence-chain/
        roi-summary/
        recommendation-review/
        ledger-history/
      styles/
      tests/

    ai/
      explanation/
      evaluation/
      prompts/
      tests/

  samples/
    decision-cases/
      ai-onboarding-assistant/

  infra/
    README.md
```

This is a future structure, not a current task.

Do not create it during Phase 0 unless a new decision explicitly authorizes implementation scaffolding.

## Recommended First Backend Modules

When implementation starts, keep modules inside one backend boundary:

| Module | Responsibility | Should not own |
|---|---|---|
| `intake` | receive selected source signals or pilot imports | business meaning |
| `evidence` | normalize and preserve evidence lineage | recommendation logic |
| `decision_case` | build and expose Decision ROI Case | provider API details |
| `roi` | calculate cost, savings and assumptions | ledger mutation |
| `recommendation` | generate approval-ready recommendation | autonomous execution |
| `ledger` | append approval, rejection, deferral and result events | workflow orchestration |
| `identity` | actor, role and authorization context | business approval authority by default |
| `observability` | structured logs, correlation ID and basic metrics | full monitoring platform |

Do not split these into separate services at the start.

Hexagonal rule:
Each module may define ports needed by its application use cases, but provider-specific code belongs in adapters.

## Recommended First Frontend Features

The first frontend should focus on the Decision Review Workspace.

The screen behavior contract is defined in `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

| Feature | Purpose |
|---|---|
| `decision-review` | main MVP screen |
| `evidence-chain` | prove why the recommendation is trustworthy |
| `roi-summary` | show cost, savings and assumptions |
| `recommendation-review` | approve, reject or defer |
| `ledger-history` | show accountability history |

Executive Workspace, Business Value, standalone Decision Ledger, Integrations, Policies and Settings are expansion surfaces.

## Recommended First AI Boundary

AI should not be a critical dependency for first proof.

If used later, it should be limited to:

- explanation,
- evidence summarization,
- recommendation wording,
- confidence narrative.

AI must consume prepared Decision ROI Case context.

AI must not read raw Jira, GitHub, AWS, OpenAI or Anthropic Claude payloads directly.

## Samples Boundary

The future `samples/` folder should contain non-production validation assets.

Preferred sample structure:

```text
samples/
  decision-cases/
    ai-onboarding-assistant/
      README.md
      evidence_table.md
      roi_assumptions.md
      expected_decision_roi_case.md
      expected_ledger_sequence.md
```

Do not add JSON fixtures, scripts or executable loaders until implementation is approved.

## Future Infrastructure Boundary

The first proof should not require:

- Kafka,
- Kubernetes,
- Terraform,
- OpenSearch,
- Graph DB,
- vector database,
- public API gateway,
- production CI/CD.

Future infrastructure may be introduced only when:

- the vertical slice is validated,
- at least one real Decision ROI Case has been reconstructed,
- the architecture change is recorded in `docs/decisions/14_Decision_Log.md`.

Minimal observability for the first build may include structured logs, correlation ID, health checks and basic metrics. Full Grafana/OpenTelemetry deployment is future scope unless explicitly needed.

## Mapping From Vision Diagram To MVP Structure

| Vision diagram element | MVP interpretation | Phase 0 action |
|---|---|---|
| AWS / GitHub / Jira / OpenAI / Claude | evidence sources | document source evidence needs |
| Connectors | intake boundary | use `docs/architecture/28_Per_Connector_MVP_Contracts.md`, do not implement |
| Event Ingestion Layer | selected source signal capture | keep conceptual |
| Core Engine Layer | context builder | define Decision ROI Case construction |
| Decision Intelligence Layer | Decision Engine + ROI + recommendation | keep as product capability |
| Ledger & Governance Layer | Decision Ledger v2 | use existing ledger contract |
| Presentation Layer | Decision Review Workspace | use `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` |
| OAuth2 / JWT | future identity adapter | document compatibility; implement only minimal auth needed |
| Micrometer / OpenTelemetry / Grafana | future observability stack | keep minimal until runtime exists |
| Kafka | future event streaming | defer |
| Policy Engine | future governance | defer |
| Kubernetes / Terraform | future operations | defer |

## Structure Health Check

The project structure is healthy if:

- `docs/` remains the source of truth during Phase 0,
- the MVP is explainable through one vertical slice,
- future code structure follows domain modules, not premature microservices,
- connectors do not own business logic,
- adapters do not change the domain,
- Decision ROI Case remains central,
- Decision Ledger remains append-only,
- ROI exposes assumptions,
- recommendation remains approval-based,
- demo surfaces do not redefine MVP scope.
- `agents/README.md` keeps AI-agent workstreams separated by ownership.

## Next Structuring Steps

1. Review `docs/product/24_MVP_Vertical_Slice.md`.
2. Review `docs/product/25_MVP_ROI_Slice.md`.
3. Validate the AI Onboarding Assistant Recovery scenario.
4. Use `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` as the manual evidence table.
5. Use `docs/product/27_MVP_Acceptance_Test_Plan.md` as the manual acceptance gate.
6. Use `docs/product/28_Identity_Access_Approval_Model.md` for role and approval authority.
7. Use `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for first-screen behavior.
8. Use `docs/architecture/27_Quality_Attributes.md` for non-functional expectations.
9. Use `docs/architecture/28_Per_Connector_MVP_Contracts.md` for Jira, GitHub, AWS and OpenAI + Anthropic Claude contracts.
10. Prepare one expected Decision ROI Case narrative.
11. Prepare one expected ledger sequence.
12. Use `docs/architecture/29_Event_Evidence_Vocabulary.md` to lock normalized events and evidence labels.
13. Use `docs/rnd/30_RD_Activity_Evidence_Dossier.md` to track future architecture, code, hours, technical objects, experiments and tests.
14. Use `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` as the final go/no-go control before Phase 1.
15. Use `docs/architecture/31_MVP_Implementation_Standard.md` to reduce first-build scope and apply hexagonal, auth and observability standards.
16. Use `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` to enforce one Decision ROI Case, one recommendation, connector limits, AI explanation boundaries and exit criteria.
17. Only after that, decide whether to create implementation scaffolding.

## Explicit Non-Decision

This document does not decide:

- Java package names,
- Python package names,
- frontend framework routes,
- database schema,
- OpenAPI specification,
- protobuf changes,
- deployment topology,
- local development setup.

Those decisions belong to future ADRs or RFCs after the vertical slice is accepted.
