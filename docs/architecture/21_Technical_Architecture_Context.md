# 21 — Technical Architecture Context

## Purpose

Prepare the canonical technical architecture context for IMPERATOR without starting production implementation.

This document translates the product strategy into a modern, controlled architecture structure for Phase 1 planning.

## Current Status

Phase 0 remains documentation, validation and structure only.

This architecture is a target context, not a command to build services yet.

## Authority and Interpretation

This document is authoritative for target architecture context, layer responsibilities, bounded contexts, communication model and Phase 1 planning.

It is subordinate to:

1. `docs/decisions/14_Decision_Log.md` for accepted strategic and architectural decisions.
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundary.
3. `docs/product/CORE_DOMAIN_MODEL.md` for business domain meaning.
4. `docs/product/API_SPECIFICATION.md` for conceptual API surface.
5. `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
6. `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
7. `docs/architecture/CONNECTOR_FRAMEWORK.md` for connector boundaries.
8. `docs/rfcs/0002-module-communication-architecture.md` for module communication rationale.
9. `docs/architecture/phase0-guidelines.md` for Phase 0 repository rules.

Founder-mode prompts should be interpreted as ambition and quality standards. If they conflict with this document, the current repository context wins unless a new decision is recorded in `docs/decisions/14_Decision_Log.md`.

Target stack choices are planning context. They do not authorize runnable services, generated bindings, production connectors, cloud infrastructure or deployment configuration during Phase 0.

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

## MVP Boundary

The MVP must correlate one Decision ROI Case across four integrations:

| Domain | MVP integration | Architecture responsibility |
|---|---|---|
| Business Context | Jira | business intent, ticket, epic, owner, status |
| Code & Deployment | GitHub | pull requests, commits, reviews, deploy references |
| Infrastructure & Cost | AWS | resources, utilization, cost and operational metrics |
| AI Consumption | OpenAI + Anthropic Claude | model, tokens, requests, user/team, application and cost |

Future integrations are allowed in the roadmap, but they must not shape the MVP architecture before the four-domain story is validated.

## Central Domain Object

The system revolves around the **Decision ROI Case**.

Canonical flow:

Business Decision -> Technical Change -> Infrastructure -> AI Consumption -> Recommendation -> Approval -> Implementation -> Result Validation -> Decision Ledger

Every module must either enrich this object, evaluate it, expose it, govern it or record it.

The business meaning of core entities and relationships is defined in `docs/product/CORE_DOMAIN_MODEL.md`.

## High-Level Architecture

```
External Platforms
  -> Connector Layer
  -> Ingestion and Normalization
  -> Context Engine
  -> Decision Graph
  -> ROI Engine
  -> Recommendation Engine
  -> Product Surfaces
  -> Decision Ledger
```

## Logical Layers

### 1) Presentation Layer

Target stack:
- Next.js
- React
- TypeScript
- Tailwind CSS
- shadcn/ui or equivalent component system

Product surfaces:
- Executive Workspace
- Decisions
- Decision Ledger
- Business Value
- Integrations
- Policies
- Settings

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
- attach tenant, owner and system identifiers
- emit canonical events for the context layer

Connector rule:
Every connector must be replaceable. No business logic should live inside connector-specific code.

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
- explicit relationship tables or edge table for graph traversal
- optional vector store or pgvector for semantic search later

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

Initial recommendation families:

1. Downgrade or change AI model.
2. Remove unused AI agents.
3. Detect underutilized AWS resources.
4. Identify features with negative ROI.
5. Consolidate duplicated services or agents.

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

Responsibilities:
- LLM orchestration
- semantic similarity
- prompt and agent analysis
- recommendation explanation
- embeddings and retrieval
- AI Advisor queries over prepared context

Rule:
AI services must never read raw external sources directly. They consume prepared context from the Context Engine and Decision Ledger.

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

Phase 1 target primitives:
- Docker for local development and packaging
- PostgreSQL for canonical storage
- Redis for caching and short-lived computation state
- object storage for evidence artifacts and exports

Future scale primitives:
- Kafka for event streams
- Kubernetes for service orchestration
- OpenSearch or analytics store for search and large-scale exploration
- data lake for long-term operational history

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
- Hexagonal Architecture for connectors and external systems.
- Event-driven design for ingestion and future scale.
- CQRS is a future option for separating ledger writes from executive reads.
- Every connector must be replaceable.
- Every recommendation must be explainable.
- AI logic must not be coupled to infrastructure ingestion.
- Presentation must not contain domain logic.
- Business rules must be configurable and auditable.

## Bounded Contexts

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
| Executive Workspace | How is the company right now? | ROI, Recommendation, Decision |
| Decisions | Can we trust this recommendation? | Decision, Context, Ledger, ROI |
| Decision Ledger | What has the company decided over time? | Ledger, Decision, ROI |
| Business Value | What economic value has IMPERATOR generated? | ROI, Ledger |
| Integrations | What systems feed the intelligence layer? | Integration, Context |
| Policies | What can be recommended or approved? | Identity, Policy, Recommendation |
| Settings | How is the organization configured? | Identity, Integration |

## Repository Control Model

Authoritative documents:
- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundaries.
- `docs/product/CORE_DOMAIN_MODEL.md` for core business entities and relationships.
- `docs/product/API_SPECIFICATION.md` for conceptual API surface before OpenAPI or implementation.
- `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
- `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
- `docs/architecture/CONNECTOR_FRAMEWORK.md` for integration and connector boundaries.
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

## Architecture Risks

- premature connector expansion
- overbuilding before pilot validation
- AI recommendations without explainable evidence
- ROI estimates without explicit assumptions
- mixing connector logic with domain logic
- treating the Executive Workspace as a technical dashboard

## Next Architecture Steps

1. Keep Phase 0 documentation-only.
2. Use this document as context for future architecture agents.
3. Update the Core Domain Model and create RFCs before adding new bounded contexts or major data models.
4. Create ADRs before locking implementation stack choices beyond current mandates.
5. Validate one complete Decision ROI Case before expanding integrations.
