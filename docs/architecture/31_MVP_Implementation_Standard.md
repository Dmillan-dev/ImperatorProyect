# 31 - MVP Implementation Standard

Baseline: **v1.0 - Phase 2 Authority**

Freeze rule: this document must not change during Phase 2 unless implementation
proves an objective contradiction.

## Purpose

Standardize how the first IMPERATOR MVP should be built when implementation is explicitly authorized.

This document translates the Phase 0 context into a practical implementation standard without creating code.

It defines:

- the minimum MVP scope,
- the hexagonal architecture rule,
- backend, frontend, connector, data, security and observability expectations,
- what remains future vision,
- how independent AI agents should work together during implementation planning.

It is an architecture standard for future Phase 2 Platform Foundation and Phase 3 MVP Implementation. It does not authorize source code, runnable services, real connectors, database migrations, OpenAPI generation, OAuth apps, Docker, Kubernetes, Terraform or cloud resources during documentation-only work.

The exact Phase 1 objective, data-model limit, connector limit, AI explanation boundary, limited scaffolding authorization and exit criteria are defined in:

- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`

The final pre-code foundational choices are defined in:

- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

Coding rules and Phase 2 scope are defined in:

- `docs/architecture/35_Coding_Principles.md`
- `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
- `docs/architecture/37_Implementation_Contract.md`
- `agents/phase2/README.md`

## Executive Standard

Build the smallest product that proves the value loop.

Do not build the full platform first.

The MVP should prove:

```text
Manual/static evidence or narrow read-only intake
-> Decision ROI Case
-> ROI calculation
-> Recommendation
-> Decision Ledger entry
-> Decision Review Workspace
-> Result Validation path
```

The product vision may remain broad. The first build must stay narrow.

## MVP Scope Standard

### In The First Build

The first implementation should include only:

| Area | MVP standard |
|---|---|
| Product object | One Decision ROI Case |
| First case | AI Onboarding Assistant Recovery, unless a new decision changes it |
| Evidence input | Manual/static evidence first; imported files or narrow read-only intake only if approved |
| Canonical evidence | Enterprise Evidence Event for every connector/import adapter |
| First import file | JSONL, one Enterprise Evidence Event per line |
| Backend shape | Modular monolith or tightly bounded service |
| Architecture style | Hexagonal Architecture with clear ports and adapters |
| Backend stack | Java 21 + Spring Boot |
| Frontend stack | Next.js + React + TypeScript + Tailwind CSS |
| Data store | PostgreSQL |
| ROI | Deterministic formulas and explicit assumptions |
| Recommendation | One approval-ready recommendation family |
| Ledger | Append-only state history and snapshots |
| Security | Role-aware evidence visibility and no Restricted data |
| Auth | JWT-compatible demo login with simple RBAC |
| Observability | Structured logs, request/correlation ID, health checks and basic metrics |
| AI | Explanation over prepared context only |
| Internal relationships | Decision Graph relationships stored in PostgreSQL, not Graph DB |

### Out Of The First Build

Keep these documented as future vision, not MVP dependencies:

- Kafka,
- Kubernetes,
- Terraform,
- OpenSearch,
- Graph DB,
- Vector DB,
- data lake,
- public API gateway,
- SDKs,
- AI Advisor,
- autonomous execution,
- broad policy engine,
- multi-tenant enterprise admin surface,
- connector marketplace,
- multiple OAuth providers at once,
- full observability platform,
- full workflow engine,
- broad executive dashboard suite,
- negative-ROI portfolio analysis,
- duplicated service or agent consolidation.

## Hexagonal Architecture Standard

Use **Hexagonal Architecture / Ports and Adapters** as the default implementation style.

Target dependency direction:

```text
Controller / Interface
-> Application Use Case
-> Domain
-> Ports
-> Adapters
```

Dependency rule:

**Domain must not depend on frameworks, providers, databases, APIs, UI or AI services.**

### Layers

| Layer | Owns | Must not own |
|---|---|---|
| Controller / Interface | HTTP or UI-facing request/response boundary | domain decisions |
| Application Use Case | orchestration of one user/system action | provider-specific implementation |
| Domain | Decision ROI Case, Evidence, ROI rules, Recommendation, Ledger meaning | Spring, PostgreSQL, Jira, GitHub, AWS, AI provider SDKs |
| Ports | interfaces needed by domain/use cases | provider details |
| Adapters | PostgreSQL, Jira, GitHub, AWS, OpenAI, Anthropic Claude, file/manual imports | business meaning or approval authority |

### MVP Port Examples

| Port | Purpose | Possible adapter |
|---|---|---|
| `DecisionCaseRepository` | load/save Decision ROI Case state | PostgreSQL adapter |
| `EvidenceSourcePort` | provide Enterprise Evidence Events and normalized evidence candidates | manual file adapter, future Jira/GitHub/AWS/AI adapters |
| `CostEvidencePort` | provide cost evidence summaries | manual import, AWS adapter, AI usage adapter |
| `LedgerRepository` | append/read ledger entries | PostgreSQL adapter |
| `IdentityContextPort` | provide current actor and role | JWT-compatible RBAC adapter |
| `ExplanationProvider` | generate explanation from prepared context | OpenAI, Claude, Ollama, Azure OpenAI or Gemini adapter |
| `DecisionGraphRepository` | persist relationships between evidence, decisions, recommendations, reviews and outcomes | PostgreSQL adapter |
| `ClockPort` | provide current time for deterministic tests | system clock adapter |
| `ObservationPort` | emit structured events/metrics | log/metrics adapter |

Port names are conceptual. They do not define Java interfaces during Phase 0.

### Adapter Rule

Each external dependency must be replaceable:

- Jira is an adapter.
- GitHub is an adapter.
- AWS is an adapter.
- OpenAI + Anthropic Claude are adapters.
- PostgreSQL is an adapter.
- OAuth providers are adapters.
- AI explanation service is an adapter.

Changing an adapter must not change the core Decision ROI Case domain.

## Backend Standard

Preferred future stack:

- Java 21,
- Spring Boot,
- Spring Security when auth is implemented,
- PostgreSQL,
- deterministic application services/use cases,
- append-only ledger persistence.

Recommended first backend modules inside one boundary:

| Module | Responsibility |
|---|---|
| `intake` | receive manual/static evidence or approved narrow intake |
| `evidence` | normalize Enterprise Evidence Events and preserve lineage |
| `decision_case` | assemble and expose Decision ROI Case |
| `roi` | calculate deterministic ROI and assumptions |
| `recommendation` | prepare approval-ready recommendation |
| `ledger` | append approval, rejection, deferral, implementation and validation entries |
| `decision_graph` | preserve relationships between evidence, recommendation, ledger and outcome |
| `identity` | actor, role and authorization context |
| `observability` | structured logs, correlation ID and basic metrics |

Do not split these into independent services before the MVP value loop is validated.

## Frontend Standard

Preferred future stack:

- Next.js,
- React,
- TypeScript,
- Tailwind CSS,
- shadcn/ui or equivalent component system if useful.

First screen:

**Decision Review Workspace**

The frontend should implement the screen contract, not invent product behavior.

Minimum features:

- decision summary,
- timeline,
- evidence chain,
- current cost,
- ROI assumptions,
- recommendation review,
- approve/reject/defer controls,
- ledger history,
- result validation state.

Do not build the full Executive Workspace, Business Value, Integrations, Policies and Settings suite before the first Decision ROI Case proves value.

## Connector Standard

MVP connectors are:

- Jira,
- GitHub,
- AWS,
- OpenAI + Anthropic Claude.

Implementation order should prefer:

1. manual/static evidence adapter,
2. file/import adapter,
3. one narrow read-only connector,
4. remaining connectors only after the first value loop works.

Connector rules:

- read-only by default,
- least privilege,
- no provider-side mutation,
- no raw prompt/completion ingestion,
- no secrets in evidence,
- no ROI calculation inside connector,
- no recommendation generation inside connector,
- no approval/ledger mutation inside connector.

## Data Standard

Initial canonical store:

- PostgreSQL.

Use PostgreSQL for:

- Enterprise Evidence Events,
- Decision ROI Case state,
- evidence summaries,
- ROI assumptions,
- recommendations,
- ledger entries,
- actor/role references,
- source references and lineage,
- Decision Graph relationship records.

Do not use as MVP source of truth:

- in-memory store,
- JSON files,
- SQLite,
- MongoDB,
- Redis.

Optional later after repeated cases prove the need:

- object storage for evidence artifacts or exports,
- Graph DB/vector/search stores.

## Authentication And Authorization Standard

Target SaaS direction:

- OAuth2 / OpenID Connect,
- JWT,
- provider adapters for Azure AD, Okta, Keycloak, Google and GitHub.

MVP reduction:

- use JWT-compatible local/demo login,
- use simple RBAC roles: `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR`,
- use static policies,
- do not build full enterprise SSO, SCIM, multi-provider admin or fine-grained policy engine in the first MVP.

Security rule:

Authentication proves who the actor is. Authorization decides what the actor may see or do. Approval authority remains a product/domain rule, not a generic admin permission.

## Security Standard

The first MVP must preserve:

- tenant/organization boundary on future records,
- role-aware evidence visibility,
- sensitivity labels,
- no Restricted data in evidence, UI, ledger, AI context or logs,
- append-only ledger behavior,
- human-only approval/rejection/deferral,
- no autonomous execution.

Restricted data includes:

- secrets,
- credentials,
- raw prompts,
- raw completions,
- customer conversations,
- unnecessary personal data.

## Observability Standard

Add observability only at the level that helps debugging and trust.

Minimum first-build observability:

- structured logs,
- request/correlation ID,
- health endpoint,
- connector/intake status,
- basic metrics for requests, errors and latency,
- ledger action audit events,
- evidence import/normalization failures.

Recommended future-compatible tools:

- Spring Boot Actuator,
- Micrometer,
- OpenTelemetry when useful,
- local Prometheus/Grafana foundation during Phase 2 if explicitly authorized.

Do not build a full production observability stack before the application exists and the MVP needs it.

## Testing Standard

Start with acceptance scenarios already defined in product language.

First implementation tests should prove:

- evidence can be attached to one Decision ROI Case,
- ROI cannot be approval-ready without assumptions,
- recommendation cannot be approval-ready without evidence,
- approval creates ledger snapshots,
- rejection requires reason,
- deferral requires missing evidence or review date,
- result validation separates realized value from estimates,
- role filtering hides Confidential evidence from unauthorized users,
- Restricted data is rejected,
- adapters can be replaced without changing domain behavior.

## AI Standard

Under the Phase 1 execution contract, AI explanation is included as a non-authoritative language layer.

Use an `ExplanationProvider` port with conceptual `generateExplanation(preparedContext)`.

Adapters may later include OpenAI, Anthropic Claude, Ollama, Azure OpenAI or Google Gemini.

If used, it may:

- explain prepared evidence,
- summarize the Decision ROI Case,
- improve recommendation wording,
- cite evidence IDs.

AI must not:

- read raw provider payloads,
- calculate financial truth,
- approve,
- reject,
- defer,
- execute,
- validate realized value,
- mutate ledger entries.

## Agent Work Standard

Future AI agents should use this ownership split:

| Agent | Implementation planning ownership |
|---|---|
| CTO Agent | hexagonal boundary, scope control, ADR/RFC sequencing |
| Product Agent | Decision ROI Case meaning and screen/product behavior |
| Backend Agent | use cases, ports, persistence implications and ledger semantics |
| Connector Agent | source adapters and failure/freshness behavior |
| Frontend Agent | Decision Review Workspace implementation plan |
| Security Agent | auth, authorization, sensitivity and data boundaries |
| FinOps Agent | ROI formulas, assumptions, validation and cost evidence |
| QA Agent | acceptance tests, negative tests and traceability |
| AI Agent | prepared-context explanation only |

Any agent proposing Kafka, Kubernetes, Graph DB/vector infrastructure, multiple OAuth providers or new connectors for the first MVP must mark it as post-MVP unless a new decision explicitly authorizes it.

## Implementation Entry Checklist

Before writing code, record:

1. first implementation data mode: manual/static, file import or one live read-only connector,
2. first recommendation family,
3. first auth mode,
4. minimum observability level,
5. PostgreSQL usage boundary,
6. Enterprise Evidence Event import boundary,
7. Explanation Provider prepared-context boundary,
8. JWT/RBAC role and policy boundary,
9. Decision Graph relationship boundary,
10. first UI route/surface,
11. R&D evidence capture method,
12. explicit authorization for Phase 2 Platform Foundation or Phase 3 MVP Implementation.

Then verify that the planned work satisfies `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`.

If the planned work is Phase 2, also verify `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`.

If the planned work creates implementation files, also verify `docs/architecture/35_Coding_Principles.md`.

If the planned work is assigned to an AI agent, also verify `docs/architecture/37_Implementation_Contract.md` and `agents/phase2/README.md`. No agent may generate more than one module per iteration.

## Final Rule

For the first MVP, reduce components, not clarity.

The project should keep the full IMPERATOR platform vision documented, but build only the smallest end-to-end value loop that proves one expensive decision can be reconstructed, reviewed, approved and later validated.
