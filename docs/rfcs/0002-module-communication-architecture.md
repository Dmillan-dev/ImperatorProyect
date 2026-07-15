# RFC 0002 — Module Communication Architecture

Date: 2026-07-15
Status: Proposed

Phase 0 note: this RFC is conceptual planning. It does not authorize runnable services, generated bindings, message brokers, production connectors or deployment configuration during Phase 0.

## Purpose

Define how IMPERATOR modules should communicate in future development and why.

This RFC translates the product architecture into communication rules that preserve domain ownership, connector replaceability, explainable ROI and AI safety.

## Context

IMPERATOR is a Decision ROI Platform. The platform must correlate Jira, GitHub, AWS and OpenAI + Anthropic Claude into one Decision ROI Case.

The future system has several logical modules:

- Presentation Layer
- API and Application Layer
- Connector Layer
- Ingestion and Normalization
- Context Engine
- Decision Context
- ROI Engine
- Recommendation Engine
- AI Intelligence Layer
- Decision Ledger
- Identity and Policy Context

The current Phase 0 repository defines context and contracts only. It must not start implementation.

## Goals

- Keep the Decision ROI Case as the central communication object.
- Keep connector-specific concerns outside the core domain.
- Ensure AI consumes prepared context, not raw source data.
- Preserve evidence lineage across every module boundary.
- Make ROI and recommendations explainable.
- Keep public API and internal service communication separate.
- Support future Java, Python, PostgreSQL and React work without coupling them prematurely.

## Non-Goals

- Define OpenAPI.
- Define protobuf schemas.
- Define SQL tables.
- Define deployment topology.
- Build services or brokers.
- Expose gRPC as the public SaaS API.

## Communication Principles

### 1. Domain-first communication

Modules communicate around domain concepts from `docs/product/CORE_DOMAIN_MODEL.md`:

- Decision ROI Case
- Decision
- Operational Event
- Evidence
- Timeline
- ROI
- Recommendation
- Approval
- Business Value
- Ledger Entry

Provider payloads must not become the shared platform language.

### 2. Public API is product-facing

The public customer API exposes product intent:

- decisions,
- timelines,
- evidence,
- ROI,
- recommendations,
- approvals,
- ledger,
- business value,
- integrations,
- policies.

The conceptual surface lives in `docs/product/API_SPECIFICATION.md`.

Future implementation may use HTTP/REST or GraphQL publicly. gRPC is not required as a public SaaS API.

### 3. Internal service communication is contract-first

Internal service-to-service communication follows ADR D013:

- gRPC for internal RPCs,
- Protocol Buffers as canonical internal contract format,
- no public customer exposure of internal gRPC by default.

This applies once services exist. In Phase 0 it remains a future internal mandate, not an implementation task.

### 4. Events are for facts, RPC is for decisions and queries

Use event-style communication conceptually when a module emits a fact:

- source event received,
- event normalized,
- evidence created,
- timeline updated,
- recommendation generated,
- approval recorded,
- rejection recorded,
- deferral recorded,
- implementation marked,
- result validated,
- ledger entry created.

Use request/response conceptually when a module needs an immediate answer:

- get Decision ROI Case,
- calculate ROI,
- evaluate policy,
- explain recommendation,
- retrieve evidence.

### 5. The ledger is append-only

Decision Ledger writes represent accountability events. Modules may request ledger entries, but they must not rewrite historical meaning.

### 6. AI is downstream of context

AI modules never read raw Jira, GitHub, AWS or OpenAI + Anthropic Claude payloads directly.

Allowed AI inputs:

- prepared Decision ROI Cases,
- Evidence summaries,
- Timeline records,
- ROI assumptions,
- Ledger history,
- policy-filtered context.

## Conceptual Module Flow

```
External Platform
  -> Connector
  -> Ingestion and Normalization
  -> Context Engine
  -> Decision Context
  -> ROI Engine
  -> Recommendation Engine
  -> API/Application Layer
  -> Product Surface
  -> Decision Ledger
```

AI Intelligence Layer reads prepared context from Context Engine, Decision Context and Ledger, then returns explanations or recommendation reasoning.

## Communication Matrix

| From | To | Mode | Purpose | Contract object |
|---|---|---|---|---|
| Connector | Ingestion | Event | Source data arrived | Operational Event |
| Ingestion | Context Engine | Event | Normalized fact ready | Normalized Event |
| Context Engine | Decision Context | Event or RPC | Attach evidence to decision | Evidence, Timeline |
| Decision Context | ROI Engine | RPC | Calculate cost/value view | Decision ROI Case |
| ROI Engine | Recommendation Engine | RPC or Event | Evaluate recovery opportunity | ROI, Evidence |
| Recommendation Engine | AI Layer | RPC | Explain or enrich recommendation | Prepared Context |
| AI Layer | Recommendation Engine | RPC response | Explanation/confidence support | Recommendation Rationale |
| API Layer | Decision Context | RPC | Read decision details | Decision ROI Case |
| API Layer | Ledger Context | RPC | Read or append decision record | Ledger Entry |
| Approval Workflow | Ledger Context | Event | Approval recorded | Approval |
| Decision Workflow | Ledger Context | Event | Rejection or deferral recorded | Ledger Entry |
| Result Validation | Ledger Context | Event | Realized value recorded | Ledger Entry |
| Integration Context | API Layer | RPC | Integration health/status | Integration Status |

## Why This Communication Model

### gRPC + Protobuf internally

Reason:
- strong contracts,
- language interoperability,
- safe evolution between Java and Python,
- lower ambiguity for core platform objects.

Tradeoff:
- requires contract governance,
- more ceremony than JSON.

Mitigation:
- keep contracts limited to stable domain boundaries,
- evolve through RFCs and ADRs.

### HTTP/REST or GraphQL publicly

Reason:
- easier customer adoption,
- familiar SaaS integration model,
- hides internal module topology.

Tradeoff:
- requires translation from public product API to internal contracts.

Mitigation:
- keep public API product-oriented and stable.

### Event-driven ingestion

Reason:
- external systems produce facts over time,
- ingestion needs resilience and replay,
- correlation improves as events accumulate.

Tradeoff:
- eventual consistency,
- more operational complexity.

Mitigation:
- keep Phase 1 simple until the four-domain MVP is validated.

## State Ownership

| Domain state | Owner module |
|---|---|
| integration configuration and sync health | Integration Context |
| raw source metadata | Connector / Ingestion Context |
| normalized events | Ingestion and Normalization |
| evidence lineage | Context Engine |
| Decision ROI Case lifecycle | Decision Context |
| cost, value and ROI assumptions | ROI Context |
| recommendation status and reasoning | Recommendation Context |
| immutable approval, rejection, deferral, implementation and result-validation history | Ledger Context |
| roles, permissions and policy rules | Identity and Policy Context |

No module should directly mutate another module's owned state.

## Failure and Trust Rules

- If evidence lineage is missing, do not mark recommendations as approval-ready.
- If ROI assumptions are missing, do not show ROI as a decision-grade number.
- If connector sync is stale, surface freshness and confidence degradation.
- If AI explanation conflicts with evidence, evidence wins.
- If policy blocks an action, recommendation status must reflect that block.
- If approval is recorded, ledger entry must preserve approver, timestamp, assumptions and evidence references.

## Phase 1 Candidate Sequencing

1. Define stable public API intent from `API_SPECIFICATION.md`.
2. Align internal contract boundaries with `CORE_DOMAIN_MODEL.md`.
3. Extend `proto/` only for stable internal contracts.
4. Keep initial communication simple until one Decision ROI Case is validated.
5. Introduce event broker patterns only when ingestion volume or replay needs justify them.

## Open Questions

- Which public API style should be chosen first: REST or GraphQL?
- Which internal contracts must exist before the first pilot?
- Which evidence objects are stable enough for protobuf?
- What is the minimal ledger append contract?
- Which module owns confidence scoring: ROI, Recommendation or AI?
