# 03 - Architecture Scaffolding Plan

## Purpose

Define the minimum architecture plan for Phase 1 MVP scaffolding before any implementation files are created.

This document translates the locked product case and acceptance contract into a reduced technical shape that future agents can follow.

It does not create code, services, package manifests, database migrations, generated bindings, credentials, Docker, Kubernetes, Terraform, cloud resources or runnable infrastructure.

## Stage Verdict

**Verdict: LOCKED for minimal architecture scaffolding plan.**

Future scaffolding may be planned only for the smallest end-to-end value loop:

```text
manual/import evidence
-> Decision ROI Case
-> ROI View
-> deterministic recommendation
-> AI explanation
-> ledger review state
-> Decision Review Workspace
-> health and observability
```

This plan does not authorize broad platform architecture.

## Authority Inputs

- `agents/phase1/00_context_control.md`
- `agents/phase1/01_product_case_lock.md`
- `agents/phase1/02_acceptance_contract.md`
- `docs/architecture/21_Technical_Architecture_Context.md`
- `docs/architecture/31_MVP_Implementation_Standard.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`

## Architecture Style

Use:

```text
Hexagonal Architecture / Ports and Adapters
```

Dependency direction:

```text
Interface
-> Application Use Case
-> Domain
-> Ports
-> Adapters
```

Rule:

The domain must not depend on frameworks, databases, providers, UI, auth, logs, metrics or AI services.

## First Runtime Shape

Preferred first shape:

```text
modular monolith or tightly bounded service
```

Do not split conceptual contexts into microservices for Phase 1.

Reason:

- the MVP must prove one value loop,
- service boundaries would add coordination cost before product value is proven,
- adapters can still be replaceable through ports,
- future extraction remains possible after repeated cases exist.

## Minimal Technical Areas

| Area | Phase 1 responsibility | Must not own |
|---|---|---|
| Interface boundary | Accept user/system actions and expose prepared views | domain decisions |
| Application use cases | Orchestrate one action at a time | provider-specific logic |
| Domain | Decision ROI Case, Evidence meaning, ROI rules, Recommendation meaning, Ledger meaning | Spring, PostgreSQL, AI SDKs, provider APIs |
| Ports | Define needs for evidence, persistence, identity, AI explanation and observability | implementation details |
| Adapters | Manual/file import, PostgreSQL, local/demo auth, AI explanation, logs/metrics | business meaning or approval authority |
| Decision Review Workspace | Show prepared case and role-aware actions | domain rules or ledger mutation semantics |

## Minimal Module Map

Use these as conceptual modules, not mandatory folders or packages:

| Module | Responsibility |
|---|---|
| `identity` | Actor, role and authority context. |
| `evidence` | Manual/import evidence intake through Enterprise Evidence Event, sensitivity and lineage. |
| `decision_case` | Assemble `DRC-AOA-001` as one coherent Decision ROI Case. |
| `roi` | Deterministic cost and recovery calculations. |
| `recommendation` | One model downgrade/change recommendation rule. |
| `ai_explanation` | Explanation Provider port over prepared context. |
| `ledger` | Append-only review history and snapshots. |
| `decision_graph` | Internal PostgreSQL-backed relationship records between evidence, case, recommendation, ledger and result. |
| `workspace` | Prepared Decision Review Workspace read model. |
| `observability` | Structured logs, correlation ID, health/readiness and basic metrics. |

These modules must remain inside one implementation boundary until the value loop is validated.

## Conceptual Ports

These ports are planning concepts. They do not define Java interfaces yet.

| Port | Purpose | First adapter |
|---|---|---|
| Evidence Source Port | Provide Enterprise Evidence Events and evidence summaries for `DRC-AOA-001`. | Manual/static or file/import adapter. |
| Decision Case Repository Port | Load and save one Decision ROI Case state. | PostgreSQL adapter. |
| ROI Assumption Port | Provide locked assumptions and formula inputs. | In-app/static configuration or persisted assumptions. |
| Recommendation Rule Port | Produce the deterministic recommendation. | Local deterministic rule. |
| Explanation Provider Port | Explain prepared context and cite evidence IDs. | LLM adapter over filtered context. |
| Ledger Repository Port | Append and read ledger entries. | PostgreSQL adapter. |
| Decision Graph Repository Port | Persist relationships between evidence events, cases, recommendations, reviews, ledger entries and result validation. | PostgreSQL adapter. |
| Identity Context Port | Provide current actor and role. | Local/demo JWT-compatible RBAC adapter. |
| Observation Port | Emit logs, health and metrics. | Structured log and basic metrics adapter. |
| Clock Port | Provide deterministic timestamps for tests and ledger entries. | System clock adapter later; fixed clock for tests if needed. |

## First Adapters

Allowed first adapters:

- manual/static evidence adapter,
- file/import evidence adapter,
- PostgreSQL persistence adapter,
- local/demo JWT-compatible RBAC identity adapter,
- Explanation Provider adapter over prepared context,
- structured log/basic metrics adapter.

Deferred adapters:

- Jira live connector,
- GitHub live connector,
- AWS live connector,
- OpenAI + Anthropic Claude live usage connector,
- Google OAuth,
- Microsoft OAuth,
- GitHub OAuth,
- Kafka event adapter,
- Graph DB/vector/search adapters,
- public API/SDK adapters.

## Minimal Data Boundary

Phase 1 persistence, if created later, may store only:

- actor and role reference,
- evidence source/import metadata,
- Enterprise Evidence Events,
- Decision ROI Case state,
- evidence summaries and lineage,
- ROI values and assumptions,
- one recommendation,
- AI explanation text and evidence references,
- append-only ledger entries,
- Decision Graph relationship records,
- minimal observation metadata if required.

Do not create:

- full audit subsystem,
- generic workflow tables,
- complex event sourcing,
- broad sync history,
- analytics warehouse,
- connector marketplace data,
- enterprise tenant-admin model,
- Graph DB/vector/search projections.

## Minimal API Intent

Use only the conceptual API subset needed by the Decision Review Workspace:

- read one Decision ROI Case,
- read timeline,
- read evidence,
- read ROI,
- read recommendation,
- read ledger history,
- approve,
- reject,
- defer,
- mark implementation later,
- validate result later,
- read health/readiness separately from product API.

Do not create public API, SDK, GraphQL marketplace API or broad integration APIs in Phase 1.

## UI Boundary

First UI surface:

```text
Decision Review Workspace
```

Required product blocks:

- decision header,
- readiness and blockers,
- decision timeline,
- evidence chain,
- cost and ROI,
- usage/value signal,
- recommendation review,
- review action bar,
- ledger history,
- security and AI boundary indicators.

Do not implement the broader Executive Workspace, Business Value suite, Integrations admin, Policies or Settings before the MVP value loop is validated.

## Observability Boundary

Minimum:

- structured request logs,
- structured error logs,
- evidence import/source logs,
- AI explanation success/failure logs,
- correlation/request ID,
- `/health`,
- `/ready`,
- request count,
- error count,
- request latency,
- recommendation generated count,
- AI explanation success/failure count,
- evidence import/source status count.

Deferred:

- full OpenTelemetry deployment,
- Grafana dashboards,
- Prometheus deployment,
- alerting,
- distributed tracing,
- high-volume operational analytics.

## Security Boundary

Required:

- local/demo JWT-compatible RBAC actor and role claims,
- role-aware evidence visibility,
- no Restricted data,
- prepared AI context only,
- append-only ledger behavior,
- human-only approve/reject/defer,
- no provider-side execution.

Forbidden:

- secrets in evidence,
- raw prompts,
- raw completions,
- customer conversations,
- unrestricted source payload display,
- AI-created approval authority,
- Admin-as-business-approver by default.

## Architecture Acceptance Mapping

| Architecture decision | Acceptance support |
|---|---|
| Modular monolith / bounded service | Supports AC-01 to AC-14 without distributed complexity. |
| Hexagonal ports/adapters | Supports adapter replaceability and N7/N10 rejection rules. |
| Manual/import evidence first | Supports AC-02 and avoids live connector dependency. |
| Deterministic recommendation rule | Supports AC-05 and D1-D5. |
| AI explanation adapter | Supports AC-06 and E1-E5. |
| Append-only ledger boundary | Supports AC-09 and F1-F6. |
| Local/demo JWT-compatible RBAC auth | Supports AC-07 and H1-H2. |
| Minimal observability | Supports AC-13 and H3-H8. |

## Forbidden Architecture Moves

Stop and hand off to CTO Agent if any agent proposes:

- microservices by default,
- Kafka as required runtime,
- Kubernetes as required runtime,
- Terraform as required runtime,
- Graph DB/vector/search store as required runtime,
- live automation of all connectors,
- public API or SDK,
- multiple OAuth providers,
- full enterprise SSO,
- full observability platform,
- AI as recommendation decider,
- connector-owned ROI,
- ledger-owned ROI,
- UI-owned domain rules,
- provider-side mutation.

## Stage 03 Pass Criteria

This stage passes when:

1. the architecture can prove `DRC-AOA-001` inside one implementation boundary,
2. all external systems are adapters,
3. all business meaning remains in the domain/application layer,
4. the first source mode remains manual/static or file/import,
5. the first UI remains Decision Review Workspace,
6. observability remains minimal,
7. auth remains local/demo JWT-compatible RBAC unless a later decision changes it,
8. no post-MVP infrastructure is required.

## Handoff To Stage 04

Next stage:

```text
04 - Data And Persistence Slice
```

Lead:

- Backend Agent

Stage 04 objective:

Define the minimum persistence concept for one Decision ROI Case, evidence summaries, ROI assumptions, one recommendation, AI explanation and append-only ledger entries without creating database schema or migrations.

Inputs for Stage 04:

- this document,
- `agents/phase1/01_product_case_lock.md`,
- `agents/phase1/02_acceptance_contract.md`,
- `docs/product/CORE_DOMAIN_MODEL.md`,
- `docs/architecture/DATABASE_MODEL.md`,
- `docs/product/DECISION_LEDGER_V2.md`,
- `docs/product/25_MVP_ROI_Slice.md`,
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`.

Exit condition for Stage 04:

No persistent object exists unless it directly supports `DRC-AOA-001`, its evidence, ROI, recommendation, AI explanation or ledger history.
