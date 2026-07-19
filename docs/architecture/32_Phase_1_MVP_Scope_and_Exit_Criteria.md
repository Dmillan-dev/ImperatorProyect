# 32 - Phase 1 MVP Scope and Exit Criteria

## Purpose

Define the exact Phase 1 implementation contract for IMPERATOR before writing the first line of code.

This document turns the reduced MVP strategy into a controlled execution boundary:

- what problem Phase 1 proves,
- who the first buyer is,
- what the MVP includes,
- what data model is allowed,
- what connectors may do,
- what AI may do,
- what observability is enough,
- when Phase 1 is considered complete.

It is a scope and exit-criteria document. It does not create source code, services, connectors, database migrations, OpenAPI contracts, OAuth applications, Docker, Kubernetes, Terraform, cloud resources or production data handling.

## Phase 1 Objective

Build a functional MVP that demonstrates **one complete Decision ROI Case** end to end.

Do not optimize.

Do not scale.

Do not build the platform.

Phase 1 exists to prove that IMPERATOR can take evidence about one expensive operational decision, produce one deterministic recommendation, explain it clearly, record the human review path and show basic system health.

Canonical Phase 1 rule:

> Todo lo que no sea imprescindible para demostrar un unico Decision ROI Case queda automaticamente fuera del alcance.

## Project Context Answers

### What Problem Does IMPERATOR Solve?

Modern technical organizations ship decisions across Jira, GitHub, cloud infrastructure and AI providers, but lose the business trail afterward.

They often cannot answer:

- why a costly technical decision exists,
- who owns it,
- what it costs today,
- whether usage still justifies the cost,
- which action should be approved to recover money,
- what evidence supports that action,
- whether the value was later realized.

IMPERATOR solves this by managing decisions as economic, evidence-backed objects.

### Who Pays?

The first likely economic buyer is the CTO, VP Engineering or Head of Platform in an AWS-first B2B SaaS company with meaningful AI/cloud spend.

The strongest champion path is:

```text
Platform / DevOps / Engineering
-> FinOps / Finance
-> CTO or VP Engineering
```

Security and Compliance become important reviewers because evidence, ledger entries and AI explanations must not leak Restricted data.

### What Is The MVP?

The MVP is not the full Enterprise Decision Intelligence Platform.

The MVP is one operational flow:

```text
Authenticated user
-> Minimal source connection or approved evidence import
-> Evidence
-> Deterministic rules
-> Single Recommendation
-> AI-generated explanation
-> Human review
-> Decision Ledger state
-> Basic health and observability
```

Product shorthand:

```text
Input
-> Evidence
-> Rules
-> Recommendation
-> Explanation
```

Existing platform flow remains valid as vision when interpreted through the canonical lifecycle:

```text
Event
-> Connector
-> Decision Engine
-> ROI Engine
-> Recommendation
-> Decision Ledger
-> Decision Review Workspace
```

Phase 1 implements only the minimum needed to prove the first value loop.

### What Use Case Demonstrates Value?

Default Phase 1 case:

**AI Onboarding Assistant Recovery** (`DRC-AOA-001`)

Default recommendation family:

**AI model downgrade or model change**

The MVP should generate exactly one recommendation for this case.

No multiple recommendations.

No ranking.

No learning system.

No portfolio optimization.

### What Domain Entities Exist In Phase 1?

Phase 1 should keep the model intentionally small.

Allowed minimum domain concepts:

| Concept | Why it exists in Phase 1 |
|---|---|
| User / Actor | Identifies the authenticated human user. |
| Role | Controls basic review visibility and allowed actions. |
| Evidence Source / minimal Integration reference | Represents one connected, imported or manual evidence source using existing domain language. |
| Decision ROI Case | Central business object of the MVP. |
| Evidence | Supports claims about cost, usage, owner, implementation and risk. |
| ROI View | Holds deterministic cost, recovery, assumptions, confidence and risk. |
| Recommendation | Stores exactly one approval-ready proposed action. |
| AI Explanation | Natural-language explanation generated from prepared context. |
| Ledger Entry | Records recommendation, approval, rejection, deferral, implementation and result-validation states. |

Do not add tables or persistent objects that do not directly serve the Decision ROI Case.

Out of Phase 1 data model:

- complete enterprise audit subsystem,
- complex versioning,
- broad historical event store,
- advanced multi-tenancy administration,
- granular policy engine,
- connector marketplace,
- organization hierarchy beyond the minimum needed for ownership,
- graph/vector/search projections,
- analytics warehouse.

### What Decisions Are Irreversible Or Expensive To Reverse?

Treat these as high-control decisions before implementation:

| Decision | Why it matters |
|---|---|
| Centrality of Decision ROI Case | All services, UI and persistence must orbit this object. |
| Hexagonal Architecture | Domain must remain independent from Jira, GitHub, AWS, AI providers, PostgreSQL and UI frameworks. |
| Deterministic recommendation rules | The business outcome must be explainable and testable without relying on LLM judgment. |
| Append-only ledger semantics | Trust depends on not mutating historical review snapshots. |
| AI as explainer only | Prevents future agents from turning the product into autonomous AI decisioning. |
| Restricted-data exclusion | Raw prompts, completions, secrets and customer conversations must stay out of evidence, ledger, logs and AI context. |

### What Can Be Postponed?

Postpone everything that does not prove the first Decision ROI Case:

- Kafka,
- Kubernetes,
- Terraform,
- OpenSearch,
- graph database,
- vector database,
- data lake,
- public API gateway,
- SDKs,
- AI Advisor,
- autonomous execution,
- full policy engine,
- full enterprise SSO,
- multiple OAuth providers,
- full observability platform,
- broad dashboards,
- connector marketplace,
- negative-ROI feature analysis,
- duplicated service or agent consolidation,
- multiple recommendations,
- ranking,
- learning systems.

## Scaffolding Authorization

Phase 1 may create implementation scaffolding only after this scope contract and the decision log entry are accepted as authoritative.

Authorized future scaffolding is limited to:

- one minimal backend boundary,
- one minimal frontend surface,
- one minimal persistence boundary if needed,
- one AI explanation adapter or boundary,
- one or two minimal source adapters,
- basic auth boundary,
- basic health, logs and metrics.

Scaffolding must not include:

- production-ready connector breadth,
- all provider endpoints,
- cloud deployment,
- Kubernetes,
- Terraform,
- Kafka,
- full OAuth provider matrix,
- generated public API surface,
- SDKs,
- broad admin features,
- autonomous execution.

The purpose of scaffolding is to support one Decision ROI Case, not to create the whole future platform skeleton.

## Connector Scope

The full MVP context maps four information domains:

| Domain | Context source |
|---|---|
| Business context | Jira |
| Code and deployment | GitHub |
| Infrastructure and cost | AWS |
| AI consumption | OpenAI + Anthropic Claude |

Phase 1 implementation should not attempt to build all connectors fully.

Allowed Phase 1 connector approaches:

1. one live read-only connector plus approved static/manual evidence for the remaining domains,
2. two narrow read-only connectors plus approved static/manual evidence for the remaining domains,
3. a file/manual import adapter if live provider access is not yet justified.

Connector rules:

- read-only by default,
- minimum permissions,
- only fields needed for `DRC-AOA-001`,
- no provider mutation,
- no ROI calculation inside connectors,
- no recommendation generation inside connectors,
- no ledger mutation inside connectors,
- no raw prompt, completion, secret or customer conversation ingestion.

## Recommendation Scope

Phase 1 produces exactly one recommendation.

Default:

```text
Change or downgrade the AI model used by the AI Onboarding Assistant.
```

The recommendation must be deterministic and generated from rules.

It must include:

- action,
- evidence references,
- current monthly cost,
- estimated monthly recovery,
- estimated annualized recovery,
- assumptions,
- risk,
- confidence,
- owner,
- approver,
- implementation note,
- result-validation requirement.

It must not include:

- ranking,
- multiple alternatives,
- autonomous execution,
- reinforcement learning,
- optimization queue,
- broad portfolio analysis.

## AI Explanation Boundary

AI is included in Phase 1 only as an explainer.

The AI component may:

- explain prepared evidence,
- summarize the Decision ROI Case,
- improve human-readable wording,
- cite evidence IDs,
- explain ROI assumptions in natural language.

The AI component must not:

- modify persistent data,
- execute business rules,
- calculate financial truth,
- replace the Decision Engine,
- approve,
- reject,
- defer,
- mark implementation,
- validate result,
- create ledger entries,
- access raw provider payloads,
- access raw prompts or completions,
- access credentials, secrets or customer conversations.

Canonical AI rule:

```text
Recommendation
-> LLM
-> Human explanation
```

The LLM explains the deterministic recommendation. It does not decide the recommendation.

## Result Validation Boundary

The full IMPERATOR lifecycle keeps Result Validation as the point where estimated recovery becomes realized Business Value.

Phase 1 must preserve this rule, but it does not need to prove a real post-action saving to be considered complete.

Phase 1 must show:

- the recommendation requires later validation,
- realized value is not counted before validation,
- a future result-validation ledger state is representable,
- FinOps owns realized-value validation when post-action evidence exists.

Phase 1 must not:

- count estimated recovery as realized Business Value,
- require a real customer implementation before the first MVP can be demonstrated,
- mutate the original recommendation or approval snapshot when validation evidence appears later.

## Initial Data Model Limit

The Phase 1 data model should be aggressively small.

If a table does not participate directly in the Decision ROI Case, it is out of scope.

Allowed persistence should cover only:

- authenticated actor and role reference,
- source connection/import metadata,
- Decision ROI Case,
- evidence summaries and lineage,
- ROI assumptions and calculated values,
- one recommendation,
- AI explanation text and evidence references,
- append-only ledger entries.

Do not implement:

- full audit tables,
- complex event sourcing,
- complex snapshot versioning beyond ledger needs,
- advanced multi-tenant admin,
- fine-grained permissions model,
- generic workflow tables,
- connector sync history beyond minimum status/error metadata,
- analytics tables.

## Authentication And Access Scope

Phase 1 requires that a user can authenticate.

Acceptable first implementation direction:

- JWT/OAuth2-compatible foundation,
- one provider only if needed,
- controlled local/demo auth only if explicitly recorded,
- basic role mapping aligned with the Identity, Access and Approval Model.

Do not implement:

- Google + Microsoft + GitHub all at once,
- SCIM,
- full enterprise SSO,
- advanced permission builder,
- generic policy administration.

Authentication identifies the actor. It does not grant business approval authority by itself.

## Observability Scope

Phase 1 observability should be minimal and useful.

Required logs:

- request,
- error,
- connector or import,
- AI explanation.

Required health endpoints:

- `/health`,
- `/ready`.

Required minimal metrics:

- request count,
- error count,
- request latency,
- recommendation generated count,
- AI explanation success/failure count,
- connector or import status count.

Do not require a full Grafana/OpenTelemetry/Prometheus deployment before the first Decision ROI Case works.

Micrometer, OpenTelemetry and Grafana remain valid future direction, but not mandatory platform work for the first MVP.

## Phase 1 Exit Criteria

Phase 1 is complete only when:

> La Fase 1 termina cuando un usuario puede autenticarse, conectar al menos una fuente de datos, ejecutar un unico flujo completo del Decision ROI Case, recibir una recomendacion determinista con una explicacion generada por IA, y consultar el estado basico del sistema mediante logs, metricas y endpoints de salud.

Operational acceptance checklist:

1. A user can authenticate.
2. At least one source of data can be connected or imported through the approved source boundary.
3. One Decision ROI Case can be executed end to end.
4. Evidence becomes normalized, reviewable and linked to the case.
5. Deterministic rules generate exactly one recommendation.
6. The AI explanation explains the recommendation without becoming the decision-maker.
7. A human can review, approve, reject or defer according to role authority.
8. The Decision Ledger records the state and preserves reviewed context.
9. Realized value remains unavailable until result validation.
10. Logs expose request, error, connector/import and AI explanation activity.
11. `/health` and `/ready` report basic system state.
12. Minimal metrics are available for request, error, latency, recommendation and AI explanation behavior.

## Phase 1 Is Not Complete If

Phase 1 is not complete if:

- the case needs manual explanation outside the product to make sense,
- ROI cannot be tied to evidence or assumptions,
- the recommendation is generated by LLM judgment instead of deterministic rules,
- multiple recommendations or rankings are required to demonstrate value,
- a connector must mutate an external provider,
- the ledger can be rewritten,
- AI can modify persistent data,
- Restricted data enters evidence, logs, ledger or AI context,
- a broad dashboard replaces the Decision Review Workspace,
- Kafka, Kubernetes, Terraform, graph/vector stores or a full observability platform become prerequisites.

## Phase Sequencing

### Phase 1

Functional MVP with one complete Decision ROI Case and architecture prepared to grow.

### Phase 2

Expand capabilities and connectors while preserving ports and adapters.

Candidate additions:

- Jira live read-only connector,
- GitHub live read-only connector,
- AWS live read-only connector,
- AI provider usage connector,
- additional recommendation families,
- broader Decision Ledger views.

### Later Phases

Add platform capabilities only after repeatable value is proven:

- Kubernetes,
- Terraform,
- event streaming,
- advanced observability,
- public APIs,
- SDKs,
- richer AI,
- multiple authentication providers,
- enterprise admin,
- connector marketplace.

## Final Architect Verdict

The project context is coherent for closing Phase 0 and preparing Phase 1 under strict control.

The next engineering movement is not platform construction. It is a disciplined implementation of one deterministic, evidence-backed Decision ROI Case with AI explanation and minimal operational visibility.
