# 21 — Technical Architecture Context

## Purpose

Prepare the canonical technical architecture context for IMPERATOR without starting production implementation.

This document translates the product strategy into a modern, controlled architecture structure for Phase 2 Platform Foundation and Phase 3 MVP implementation planning.

## Current Status

Phase 1 documentation is complete and Phase 2 Platform Foundation is defined but not started.

This architecture is a target context, not a command to build services yet.

## Authority and Interpretation

This document is authoritative for target architecture context, layer responsibilities, bounded contexts, communication model and implementation-phase planning.

It is subordinate to:

1. `docs/decisions/14_Decision_Log.md` for accepted strategic and architectural decisions.
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundary.
3. `docs/product/CORE_DOMAIN_MODEL.md` for business domain meaning.
4. `docs/product/API_SPECIFICATION.md` for conceptual API surface.
5. `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
6. `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
7. `docs/architecture/CONNECTOR_FRAMEWORK.md` for connector boundaries.
8. `docs/architecture/26_Security_Data_Governance_Threat_Model.md` for evidence sensitivity, AI boundaries and security threat model.
9. `docs/product/28_Identity_Access_Approval_Model.md` for roles, permissions and approval authority.
10. `docs/product/27_MVP_Acceptance_Test_Plan.md` for pre-code acceptance gates.
11. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for first MVP screen behavior.
12. `docs/architecture/27_Quality_Attributes.md` for MVP non-functional quality expectations.
13. `docs/architecture/28_Per_Connector_MVP_Contracts.md` for Jira, GitHub, AWS and OpenAI + Anthropic Claude connector contracts.
14. `docs/architecture/29_Event_Evidence_Vocabulary.md` for event, evidence, blocker, lifecycle and label vocabulary.
15. `docs/rnd/30_RD_Activity_Evidence_Dossier.md` for future development evidence, hours, objects, experiments and tests.
16. `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` for final Phase 0 readiness gates and go/no-go control.
17. `docs/architecture/31_MVP_Implementation_Standard.md` for future MVP implementation standards.
18. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` for exact Phase 1 objective, connector limit, data limit, AI boundary, scaffolding authorization and exit criteria.
19. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` for Canonical Evidence Model, PostgreSQL, Explanation Provider, JWT/RBAC and Decision Graph.
20. `docs/architecture/34_MVP_Implementation_Blueprint.md` for the final MVP value-loop contract.
21. `docs/architecture/35_Coding_Principles.md` for coding discipline, layering and adapter boundaries.
22. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` for Phase 2 Platform Foundation scope.
23. `docs/rfcs/0002-module-communication-architecture.md` for module communication rationale.
24. `docs/architecture/phase0-guidelines.md` for Phase 0 repository rules.

Founder-mode prompts should be interpreted as ambition and quality standards. If they conflict with this document, the current repository context wins unless a new decision is recorded in `docs/decisions/14_Decision_Log.md`.

Target stack choices are planning context. They do not authorize runnable services, generated bindings, production connectors, cloud infrastructure or deployment configuration unless the founder explicitly authorizes the relevant implementation phase.

## Product Architecture Goal

IMPERATOR is a **Decision ROI Platform** for **Executive Operational Intelligence**.

The architecture must optimize for:

- scalability
- modularity
- maintainability
- enterprise governance
- cross-system correlation
- decision-level economic reasoning

Never optimize only for a quick demo if it damages the future ability to scale the context engine, the decision ledger or recommendation quality.

Investor refactor:

Phase 2 should create technical foundation only. Phase 3 should optimize for proving one paid workflow before proving the full platform architecture.

Canonical audit reference:
- `docs/architecture/22_Technical_Investor_Audit.md`

Canonical MVP implementation standard:
- `docs/architecture/31_MVP_Implementation_Standard.md`

Canonical Phase 1 scope and exit criteria:
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`

Canonical Phase 2 Platform Foundation scope:
- `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`

## MVP Boundary

The MVP context must be able to explain one Decision ROI Case across four information domains:

| Domain | MVP integration | Architecture responsibility |
|---|---|---|
| Business Context | Jira | business intent, ticket, epic, owner, status |
| Code & Deployment | GitHub | pull requests, commits, reviews, deploy references |
| Infrastructure & Cost | AWS | resources, utilization, cost and operational metrics |
| AI Consumption | OpenAI + Anthropic Claude | model, tokens, requests, user/team, application and cost |

Phase 3 MVP implementation does not need to automate every domain as a live connector. It may use one or two narrow read-only connectors plus approved manual/static or import evidence for the remaining domains, as defined in `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`.

Future integrations are allowed in the roadmap, but they must not shape the MVP architecture before the first Decision ROI Case is validated.

## Central Domain Object

The system revolves around the **Decision ROI Case**.

Canonical flow:

Business Decision -> Technical Change -> Infrastructure -> AI Consumption -> ROI -> Recommendation -> Approval/Rejection/Deferral -> Implementation -> Result Validation

The Decision Ledger records review, approval, rejection, deferral, implementation and validation states across that flow. It does not sit at the end as a passive archive.

Every module must either enrich this object, evaluate it, expose it, govern it or record it.

The business meaning of core entities and relationships is defined in `docs/product/CORE_DOMAIN_MODEL.md`.

## High-Level Architecture

```
External Platforms
  -> Connector Layer
  -> Ingestion and Normalization
  -> Context Engine
  -> Decision ROI Case
  -> ROI Engine
  -> Recommendation Engine
  -> Decision Ledger
  -> Product Surfaces
```

## MVP Architecture Refactor

The first build should be a modular monolith or tightly bounded service, not a distributed microservice system.

The first build should follow Hexagonal Architecture / Ports and Adapters inside that boundary.

Dependency direction:

```text
Controller / Interface
-> Application Use Case
-> Domain
-> Ports
-> Adapters
```

Domain must not depend on Spring, PostgreSQL, Jira, GitHub, AWS, OpenAI, Anthropic Claude, OAuth providers or AI services.

Initial modules:
- Integration Intake
- Context Builder
- ROI Calculator
- Recommendation Rules
- Ledger
- Decision Review UI
- Identity and Authorization
- Minimal Observability

Deferred until repeated customer demand or scale requires them:
- independent AI Intelligence service,
- event broker,
- Graph DB,
- vector store,
- public API gateway,
- SDKs,
- policy engine,
- full enterprise SSO / SCIM,
- multi-provider auth administration,
- full observability platform,
- multi-cloud connector expansion,
- Kubernetes,
- OpenSearch or analytics lake.

D013 still applies when internal services exist. It should not force premature service separation before the MVP value loop is validated.

## Logical Layers

### 1) Presentation Layer

Target stack:
- Next.js
- React
- TypeScript
- Tailwind CSS
- shadcn/ui or equivalent component system

Product surfaces:
- Decision Review Workspace for MVP
- Executive Workspace after multiple Decision ROI Cases exist
- Decision Ledger as standalone surface after enough ledger history exists
- Business Value after result validation exists
- Integrations as supporting configuration and health context
- Policies and Settings after governance needs are validated

Rule:
The UI must not become a generic dashboard. Each screen answers one product question.

### 2) API and Application Layer

Target stack:
- Java 21
- Spring Boot

Responsibilities:
- authentication and authorization
- public API surface
- tenant and organization boundaries
- connector orchestration
- business rules
- policy evaluation
- Decision ROI Case APIs
- Decision Ledger write/read APIs
- integration health APIs

MVP auth direction:
- JWT-compatible session/token model,
- simple RBAC roles: `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR`,
- static policies,
- Azure AD, Okta, Keycloak, Google and GitHub as future provider adapters, not mandatory first-build scope.

Authentication must identify the actor. Authorization and approval authority remain product/domain rules governed by `docs/product/28_Identity_Access_Approval_Model.md`.

### 3) Connector and Ingestion Layer

Initial connectors:
- Jira
- GitHub
- AWS
- OpenAI + Anthropic Claude

Responsibilities:
- fetch or receive external events
- preserve source metadata
- normalize provider-specific payloads
- output Enterprise Evidence Events
- attach tenant, owner and system identifiers
- emit canonical events for the context layer

Connector rule:
Every connector must be replaceable. No business logic should live inside connector-specific code.

Hexagonal interpretation:
Jira, GitHub, AWS, OpenAI + Anthropic Claude and PostgreSQL are adapters behind ports. Replacing any adapter must not change the Decision ROI Case domain.

MVP connector contracts:
`docs/architecture/28_Per_Connector_MVP_Contracts.md` defines the source objects, evidence, permissions, freshness, sensitivity and failure behavior for Jira, GitHub, AWS and OpenAI + Anthropic Claude.

### 4) Context Engine

Responsibilities:
- normalize events into common entities
- correlate Jira, GitHub, AWS and AI evidence
- build the Decision ROI Case
- preserve evidence lineage
- enrich with owner, team, project and resource metadata
- prepare context for AI and recommendation logic

This is the strategic moat of IMPERATOR.

### 5) Decision Graph / Knowledge Layer

Recommended initial model:
- PostgreSQL as canonical relational store
- explicit relationship tables or edge table for Decision Graph traversal
- optional vector store or pgvector for semantic search later

MVP rule:
- Decision Graph relationships are stored in PostgreSQL.
- Graph DB is not required for the MVP.

Core entities:
- Decision
- Owner
- Team
- Project
- Pull Request
- Deployment
- Resource
- Cost
- AI Model
- AI Usage
- Recommendation
- Approval
- Result

### 6) ROI Engine

Responsibilities:
- calculate current monthly cost
- estimate annualized recovery
- compare usage and cost trends
- apply value proxies when direct revenue/value data is unavailable
- expose assumptions used in calculations

Rule:
Every ROI number must be explainable and tied to explicit evidence or assumptions.

### 7) Recommendation Engine

MVP recommendation focus:

The MVP produces exactly one deterministic recommendation:

1. Downgrade or change AI model for `DRC-AOA-001`.

Expansion candidates after the first value loop works:
- remove unused AI agents,
- detect underutilized AWS resources tied to the same Decision ROI Case,
- identify features with negative ROI,
- consolidate duplicated services or agents.

Each recommendation must include:
- reason
- evidence
- owner
- risk
- confidence
- estimated savings
- ROI
- approval path

IMPERATOR recommends. The company decides.

### 8) AI Intelligence Layer

Target stack:
- Python
- FastAPI

Communication note:
FastAPI is allowed for Python service shell, health/dev surface and provider-boundary organization. Internal Java/Python product calls remain governed by the existing gRPC/Protobuf decisions unless a later decision changes them.

Responsibilities:
- recommendation explanation over prepared context
- evidence summarization
- optional prompt and agent analysis after the first recovery wedge is validated
- optional semantic similarity, embeddings and AI Advisor later

Rule:
AI services must never read raw external sources directly. They consume prepared context from the Context Engine and Decision Ledger.

MVP rule:
Do not make AI orchestration a critical dependency for the first proof. Use deterministic rules and explicit assumptions first; use LLMs for explanation only when evidence quality is already trusted.

### 9) Decision Ledger

Purpose:
Immutable accountability record for reviewed Decision ROI Cases.

Ledger flow:

Business Decision -> Technical Change -> Infrastructure -> AI -> Recommendation -> Approval/Rejection/Deferral -> Implementation -> Result Validation

Stores:
- decision ID
- owner
- recommendation ID
- actor and role
- evidence snapshots
- ROI snapshots
- assumptions snapshots
- approval, rejection or deferral state
- implementation marker
- estimated saving
- realized saving
- ROI
- status

Rules:
- ledger entries are append-only
- approval must preserve evidence, ROI and assumptions snapshots
- rejection must preserve a reason
- deferral must preserve required evidence or review date
- realized value must be recorded through result validation, not by mutating estimates
- the ledger records accountability but does not execute changes

Canonical module contract:
- `docs/product/DECISION_LEDGER_V2.md`

### 10) Infrastructure and Runtime

Phase 2/3 target primitives:
- PostgreSQL for canonical storage
- Docker for local development and packaging during Phase 2 Platform Foundation, only after implementation is authorized
- optional object storage for evidence artifacts and exports, only if summaries/source references are not enough

Future scale primitives:
- Kafka for event streams
- Kubernetes for service orchestration
- Redis for cache or short-lived computation if later justified
- OpenSearch or analytics store for search and large-scale exploration
- data lake for long-term operational history

These are not Phase 2 Platform Foundation or Phase 3 MVP Implementation requirements.

### 11) Minimal Observability

The MVP should include observability only where it helps debugging, trust and auditability.

Minimum future expectations:
- structured logs,
- request/correlation ID,
- health endpoint,
- connector/intake status,
- basic metrics for requests, errors and latency,
- ledger action audit events,
- evidence import or normalization failures.

Preferred future-compatible tools:
- Spring Boot Actuator,
- Micrometer,
- OpenTelemetry when useful,
- local Prometheus/Grafana foundation during Phase 2 if explicitly authorized.

Do not make a full production observability stack a prerequisite for proving one Decision ROI Case.

## Communication Model

The detailed communication rationale lives in `docs/rfcs/0002-module-communication-architecture.md`.

Internal service communication:
- gRPC
- Protocol Buffers

Public API:
- HTTP/REST or GraphQL can be exposed through an API gateway if needed.
- gRPC is not required as a public customer API.

Event transport:
- Protobuf-encoded messages are preferred when Kafka or broker-based ingestion is introduced.

Source of truth:
- `proto/` remains the canonical internal contract location.
- ADR D013 remains binding for internal service-to-service communication once services exist.

## Architecture Principles

- Clean Architecture for separation of concerns.
- Domain-Driven Design for the Decision ROI Case and bounded contexts.
- Hexagonal Architecture / Ports and Adapters for connectors, persistence, identity, AI and external systems.
- Event-driven design for ingestion and future scale.
- CQRS is a future option for separating ledger writes from executive reads.
- Every connector must be replaceable.
- Every recommendation must be explainable.
- AI logic must not be coupled to infrastructure ingestion.
- Presentation must not contain domain logic.
- Business rules must be configurable and auditable.

## Bounded Contexts

These are conceptual ownership boundaries. They are not mandatory service boundaries for Phase 1.

### Integration Context
Owns connectors, provider health, authentication metadata and sync state.

### Correlation Context
Owns normalization, enrichment, correlation and evidence lineage.

### Decision Context
Owns Decision ROI Cases, lifecycle, ownership, status and approval flow.

### ROI Context
Owns cost calculations, assumptions, recovery estimates and realized value.

### Recommendation Context
Owns recommendation generation, confidence, risk and explanation.

### Ledger Context
Owns immutable decision history, evidence snapshots, ROI snapshots, assumptions snapshots and result validation records.

It records state transitions. It does not orchestrate workflow execution.

### Identity and Policy Context
Owns users, roles, permissions, tenant boundaries and governance policies.

## Product Surface Mapping

| Surface | Primary question | Backing contexts |
|---|---|---|
| Decision Review Workspace | Can we trust and approve this recovery action? | Decision, Context, ROI, Recommendation, Ledger |
| Executive Workspace | How is the company right now? | ROI, Recommendation, Decision |
| Decisions | Can we trust this recommendation? | Decision, Context, Ledger, ROI |
| Decision Ledger | What has the company decided over time? | Ledger, Decision, ROI |
| Business Value | What economic value has IMPERATOR generated? | ROI, Ledger |
| Integrations | What systems feed the intelligence layer? | Integration, Context |
| Policies | What can be recommended or approved? | Identity, Policy, Recommendation |
| Settings | How is the organization configured? | Identity, Integration |

Decision Review Workspace behavior is defined in `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

MVP quality attributes are defined in `docs/architecture/27_Quality_Attributes.md`.

## Repository Control Model

Authoritative documents:
- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundaries.
- `docs/product/CORE_DOMAIN_MODEL.md` for core business entities and relationships.
- `docs/product/API_SPECIFICATION.md` for conceptual API surface before OpenAPI or implementation.
- `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
- `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
- `docs/architecture/CONNECTOR_FRAMEWORK.md` for integration and connector boundaries.
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md` for evidence sensitivity, AI boundaries, permissions and threat model.
- `docs/product/28_Identity_Access_Approval_Model.md` for MVP role and approval authority.
- `docs/product/27_MVP_Acceptance_Test_Plan.md` for MVP acceptance scenarios before implementation.
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for the first MVP review surface behavior.
- `docs/architecture/27_Quality_Attributes.md` for explainability, auditability, freshness, traceability, latency, resilience, observability and performance non-goals.
- `docs/architecture/28_Per_Connector_MVP_Contracts.md` for per-source MVP connector contracts.
- `docs/architecture/29_Event_Evidence_Vocabulary.md` for canonical normalized event names, evidence types, states and blockers.
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md` for future development evidence, hours, objects, experiments and tests.
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` for final Phase 0 readiness gates and go/no-go control.
- `docs/architecture/31_MVP_Implementation_Standard.md` for hexagonal MVP implementation standard, auth direction, minimal observability and scope reduction.
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` for the exact Phase 1 objective, data limit, connector limit, AI boundary and exit criteria.
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` for Canonical Evidence Model, PostgreSQL, Explanation Provider, JWT/RBAC and Decision Graph.
- `docs/architecture/34_MVP_Implementation_Blueprint.md` for the final MVP value-loop contract.
- `docs/architecture/35_Coding_Principles.md` for implementation layering and adapter discipline.
- `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` for Phase 2 Platform Foundation scope.
- `docs/rfcs/0002-module-communication-architecture.md` for module communication rationale.
- `docs/architecture/21_Technical_Architecture_Context.md` for architecture context.
- `docs/architecture/18_Architecture_Thesis.md` for conceptual architecture thesis.
- `docs/decisions/14_Decision_Log.md` for strategic and architectural decisions.
- `docs/decisions/adr/` for architecture decision records.
- `docs/rfcs/` for proposals and model evolution.
- `proto/` for canonical internal contracts.

Phase 0 control:
- no production connectors
- no runnable service implementation
- no generated binaries
- no unlogged architecture pivots

## Architecture Health Check

The current project is healthy if:

- the MVP still uses Jira, GitHub, AWS and OpenAI + Anthropic Claude
- every new feature strengthens Decision ROI Cases
- product surfaces stay separated by user question
- AI consumes prepared context, not raw data
- ROI calculations expose assumptions
- recommendations remain approval-based, not autonomous execution
- Decision Ledger remains immutable and central
- Decision Ledger separates estimated savings from realized savings
- Decision Ledger records accountability without becoming a workflow engine
- quality attributes stay focused on trust for one Decision ROI Case, not premature scale
- connector contracts stay source-specific and do not move ROI, approval or recommendation logic into adapters
- hexagonal boundaries keep GitHub, Jira, AWS, OpenAI + Anthropic Claude, PostgreSQL and OAuth providers outside the domain
- event and evidence names stay controlled through `docs/architecture/29_Event_Evidence_Vocabulary.md`
- future code work is traceable through `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- Phase 2 Platform Foundation starts only after explicit founder authorization
- Phase 2 proposals satisfy `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
- Phase 3 MVP implementation proposals satisfy `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` and `docs/architecture/34_MVP_Implementation_Blueprint.md`

## Architecture Risks

- premature connector expansion
- overbuilding before pilot validation
- treating conceptual bounded contexts as mandatory microservices
- making AI, graph, vector or event-broker infrastructure a prerequisite for first value
- AI recommendations without explainable evidence
- ROI estimates without explicit assumptions
- mixing connector logic with domain logic
- treating adapters as domain services
- implementing multiple auth providers before one B2B pilot needs them
- building full production Grafana/OpenTelemetry infrastructure before there is useful runtime behavior to observe
- treating the Executive Workspace as a technical dashboard

## Next Architecture Steps

1. Keep the current repository documentation-only until the founder explicitly authorizes Phase 2.
2. Use this document as context for future architecture agents.
3. Update the Core Domain Model and create RFCs before adding new bounded contexts or major data models.
4. Create ADRs before locking implementation stack choices beyond current mandates.
5. Validate one complete Decision ROI Case before expanding integrations.
6. Validate the Decision Recovery Workflow before building broad platform surfaces.
7. Use `docs/architecture/27_Quality_Attributes.md` before turning target architecture into implementation tasks.
8. Use `docs/architecture/28_Per_Connector_MVP_Contracts.md` before assigning connector work to future agents.
9. Use `docs/architecture/29_Event_Evidence_Vocabulary.md` before implementation to lock normalized event and evidence language.
10. Use `docs/rnd/30_RD_Activity_Evidence_Dossier.md` to document future development activity, hours, objects, experiments and tests.
11. Use `docs/architecture/31_MVP_Implementation_Standard.md` to keep backend, connectors, auth, security and observability aligned.
12. Use `docs/architecture/35_Coding_Principles.md` before creating implementation files.
13. Use `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` before starting Phase 2 Platform Foundation.
14. Use `docs/architecture/34_MVP_Implementation_Blueprint.md` before starting Phase 3 MVP Implementation.
