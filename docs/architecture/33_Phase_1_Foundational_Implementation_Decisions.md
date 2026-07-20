# 33 - Phase 1 Foundational Implementation Decisions

## Purpose

Close the last foundational implementation decisions for IMPERATOR Phase 1 before any code is written.

This document does not create code, schemas, migrations, services, connectors, credentials, Docker, Kubernetes, Terraform, OpenAPI contracts or runtime infrastructure.

It locks five decisions that future implementation agents must follow:

1. Canonical Evidence Model.
2. PostgreSQL from day one.
3. AI Provider Interface.
4. JWT plus simple RBAC.
5. Decision Graph as internal relationship model.

## Verdict

**Accepted with scope control.**

These decisions strengthen the existing Phase 1 dossier and do not expand the MVP beyond one Decision ROI Case.

Important clarification:

```text
Decision Graph does not mean Graph DB in Phase 1.
```

Phase 1 should persist graph-like relationships in PostgreSQL. Graph databases, vector stores and search infrastructure remain deferred.

## Authority

This document refines:

- `docs/architecture/31_MVP_Implementation_Standard.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `agents/phase1/03_architecture_scaffolding_plan.md`
- `agents/phase1/04_data_persistence_slice.md`
- `agents/phase1/05_evidence_intake_slice.md`
- `agents/phase1/07_ai_explanation_slice.md`
- `agents/phase1/10_auth_observability_security_slice.md`
- `agents/phase1/12_phase1_closure.md`

If a future implementation proposal conflicts with this document, stop and return to CTO Agent before coding.

## Decision 1 - Canonical Evidence Model

All connectors and import adapters must produce the same internal evidence envelope:

```text
Enterprise Evidence Event
```

GitHub, AWS, Jira, OpenAI, Anthropic Claude, manual imports and future connectors must not create provider-specific domain models inside the core.

Provider-specific parsing belongs in adapters.

The domain receives normalized evidence only.

First file import encoding:

```text
JSONL
```

Rule:

```text
One Enterprise Evidence Event per line.
```

JSONL gives Phase 1 a simple import file while keeping a future event-stream shape.

## Enterprise Evidence Event

The canonical event must be serializable and provider-neutral.

Recommended conceptual fields:

| Field | Meaning |
| --- | --- |
| `id` | Stable evidence-event identifier. |
| `schema_version` | Version of the Enterprise Evidence Event contract. |
| `timestamp` | Source event time or observation time. |
| `ingested_at` | Time IMPERATOR accepted the evidence event. |
| `source` | Concrete system name, for example Jira, GitHub, AWS, OpenAI, Claude or Manual Import. |
| `source_type` | Controlled source category, for example `business_context`, `code`, `deployment`, `cloud_cost`, `ai_usage`, `manual`. |
| `source_object_ref` | Non-secret pointer to the original source object. |
| `entity` | Business or technical object observed by the event. |
| `event_type` | Controlled event type from the event/evidence vocabulary. |
| `severity` | `info`, `low`, `medium`, `high` or `critical` when applicable. |
| `actor` | Human, system or service actor associated with the event, if known. |
| `evidence_type` | Business context, implementation, cost, usage, quality, ownership, governance or security evidence. |
| `case_hint` | Optional Decision ROI Case hint such as `DRC-AOA-001`. |
| `sensitivity` | Public, Internal, Confidential or Restricted. |
| `confidence` | Low, Medium or High confidence. |
| `freshness` | Fresh, stale, unknown or not applicable. |
| `metadata` | Sanitized key-value context needed for reasoning. |
| `raw_payload` | Controlled raw-payload descriptor, not unrestricted payload storage. |

## Raw Payload Rule

The `raw_payload` field exists so the model can express raw-payload policy.

It does not authorize storing raw source dumps in Phase 1.

For Phase 1:

```text
raw_payload.mode = "not_stored"
```

Allowed raw payload metadata:

- `mode`: `not_stored`, `redacted`, `external_reference`, `stored_restricted` for future controlled use.
- `hash`: optional integrity hash if useful and safe.
- `ref`: optional future secure reference, not required for Phase 1.
- `redaction_status`: safe summary of whether sensitive fields were removed.

Forbidden in Phase 1:

- raw prompts,
- raw completions,
- secrets,
- API keys,
- OAuth tokens,
- customer conversations,
- full provider payload dumps,
- Restricted content in AI context.

## Connector Rule

Every connector must follow this path:

```text
Provider object
-> adapter-specific parser
-> Enterprise Evidence Event
-> Evidence Normalizer
-> Evidence Store
```

Connectors must not:

- calculate ROI,
- create recommendations,
- approve,
- reject,
- defer,
- write ledger entries directly,
- mutate provider systems,
- expose provider-specific objects to the domain.

## Decision 2 - PostgreSQL From Day One

Phase 1 implementation should start with PostgreSQL as the source of truth.

Reason:

- IMPERATOR contains a Decision Ledger;
- ledger history must survive process restarts;
- evidence lineage and reviewed snapshots need persistence;
- PostgreSQL is mature, free, robust and enough for the MVP;
- starting with memory or JSON files creates migration debt almost immediately.

Do not use as Phase 1 source of truth:

- in-memory store,
- JSON files,
- SQLite,
- MongoDB,
- Redis.

Redis, object storage, search, Graph DB or vector systems may be evaluated later only after repeated Decision ROI Cases prove the need.

## PostgreSQL MVP Ownership

PostgreSQL should eventually persist:

- Enterprise Evidence Events,
- normalized evidence summaries,
- Decision ROI Case state,
- ROI View and assumptions,
- one recommendation,
- AI explanation record,
- append-only ledger entries,
- actor and role references,
- Decision Graph nodes and edges as relational records.

No SQL is defined here.

No migration is created here.

## Decision 3 - AI Provider Interface

The Decision Engine must never depend directly on OpenAI, Anthropic Claude, Ollama, Azure OpenAI, Google Gemini or any other provider.

Phase 1 must use an interface/port concept:

```text
Explanation Provider
```

Conceptual method:

```text
generateExplanation(preparedContext)
```

Possible adapters:

- OpenAI Explanation Adapter,
- Anthropic Claude Explanation Adapter,
- Ollama Explanation Adapter,
- Azure OpenAI Explanation Adapter,
- Google Gemini Explanation Adapter.

The domain only knows:

```text
Prepared deterministic context -> human-readable explanation
```

The domain must not know:

- provider name,
- provider SDK,
- provider response JSON,
- token accounting implementation,
- model-specific prompt format,
- retry mechanism,
- vendor-specific moderation behavior.

## AI Boundary

The Explanation Provider may:

- explain a deterministic recommendation,
- cite evidence IDs,
- summarize assumptions already present,
- improve reviewer-facing language.

The Explanation Provider must not:

- calculate ROI,
- choose recommendation,
- create evidence,
- mutate persistence,
- write ledger entries,
- approve,
- reject,
- defer,
- mark implementation,
- validate result.

## Decision 4 - JWT Plus Simple RBAC

Phase 1 should not implement OAuth or enterprise SSO.

The first auth model is:

```text
Login
-> JWT-compatible token
-> Roles
-> Static policies
```

The model must remain replaceable later by Azure AD, Okta, Keycloak, Google, GitHub or another identity provider without changing the domain.

## MVP Roles

Use four Phase 1 demo roles:

| Role | Meaning |
| --- | --- |
| `ADMIN` | Demo approver and local system operator for Phase 1. Represents CTO/VP Engineering authority in the controlled demo. |
| `PLATFORM_ENGINEER` | Reviews technical evidence, can defer for feasibility and mark implementation after approval. |
| `FINANCE` | Reviews cost evidence, ROI assumptions and validates realized result when available. |
| `AUDITOR` | Reviews ledger, evidence traceability and security/data-governance posture; read-only by default with ability to flag or defer audit/security blockers if enabled. |

Important:

```text
ADMIN is not a permanent enterprise business-approver concept.
```

In future enterprise SSO, external identities map into IMPERATOR roles and policies. The domain must not depend on a specific identity provider.

## Static Policy Examples

| Policy | `ADMIN` | `PLATFORM_ENGINEER` | `FINANCE` | `AUDITOR` |
| --- | --- | --- | --- | --- |
| View case | Yes | Yes | Yes | Yes |
| View safe evidence summaries | Yes | Yes | Yes | Yes |
| View cost/ROI | Yes | Limited | Yes | Yes |
| Approve recommendation | Yes | No | No | No |
| Reject recommendation | Yes | No | No | No |
| Defer recommendation | Yes | Technical only | Cost only | Audit/security only if enabled |
| Mark implementation | No | Yes | No | No |
| Validate result | No | No | Yes | No |
| View ledger | Yes | Yes | Yes | Yes |
| Manage demo users/imports | Yes | No | No | No |

This is RBAC, not a full policy engine.

Full policy administration remains deferred.

## Decision 5 - Decision Graph As Internal Model

IMPERATOR should treat decisions as connected events, not isolated records.

This is strategically important and should start conceptually in Phase 1.

The internal Decision Graph connects:

```text
Evidence Event
-> Decision ROI Case
-> ROI View
-> Recommendation
-> AI Explanation
-> Human Review
-> Ledger Entry
-> Result Validation
-> Business Value
```

The purpose is to preserve relationships early so future IMPERATOR can answer:

- which deployments generate costly incidents,
- which teams approve or reject recommendations,
- which decision types produce higher ROI,
- which cloud or AI providers create the most recoverable spend,
- which evidence sources most often support approved actions.

## Decision Graph Scope In Phase 1

Allowed:

- conceptual graph model,
- relation records in PostgreSQL,
- evidence-to-case relationships,
- case-to-recommendation relationships,
- recommendation-to-ledger relationships,
- ledger-to-result-validation relationships,
- source-event correlation references.

Not allowed as Phase 1 prerequisites:

- Graph DB,
- graph query engine,
- graph visualization,
- knowledge graph platform,
- vector database,
- OpenSearch,
- analytics warehouse.

## Conceptual Node Types

| Node Type | Meaning |
| --- | --- |
| `evidence_event` | Enterprise Evidence Event. |
| `decision_case` | Decision ROI Case such as `DRC-AOA-001`. |
| `roi_view` | Deterministic ROI view and assumptions. |
| `recommendation` | One deterministic recommendation. |
| `ai_explanation` | Non-authoritative explanation record. |
| `human_review` | Approval, rejection or deferral action. |
| `ledger_entry` | Append-only historical record. |
| `result_validation` | Future validation of realized value. |
| `business_value` | Validated outcome only after result validation. |

## Conceptual Edge Types

| Edge Type | Meaning |
| --- | --- |
| `derived_from` | Evidence summary derives from an Enterprise Evidence Event. |
| `supports` | Evidence supports a case, ROI input or recommendation. |
| `correlates_with` | Two events are related but causality is not proven. |
| `caused_by` | Stronger causal relationship when evidence supports it. |
| `implemented_by` | Decision or recommendation maps to implementation evidence. |
| `costs` | Case, resource or AI usage maps to cost evidence. |
| `explained_by` | Recommendation has an AI explanation. |
| `reviewed_by` | Human actor reviewed the recommendation. |
| `recorded_as` | Review action is recorded as ledger entry. |
| `validated_by` | Result is validated by later evidence. |
| `realizes` | Validated result creates Business Value. |

If causality is not proven, use `correlates_with`, not `caused_by`.

This protects explainability and auditability.

## Revised Architecture Flow

The Phase 1 future architecture should be interpreted as:

```text
GitHub / AWS / Jira / AI providers / Manual import
-> Connector or Import Adapter
-> Enterprise Evidence Event
-> Evidence Normalizer
-> Evidence Store (PostgreSQL)
-> Decision Engine
-> ROI Engine
-> Explanation Provider
-> Decision Recommendation
-> Human Review
-> Decision Ledger (PostgreSQL)
-> Decision Graph relationships (PostgreSQL)
-> Decision Review Workspace
```

For Phase 1, the workspace remains the first operational UI.

The broader Executive Dashboard remains future vision after repeated cases exist.

## Implementation Non-Goals

These decisions do not authorize:

- creating code now,
- creating SQL now,
- creating migrations now,
- implementing OAuth now,
- implementing live connectors now,
- implementing Graph DB now,
- storing raw provider payloads,
- creating multiple recommendations,
- creating a broad dashboard,
- using AI as decision-maker.

## Closure Of Previously Open Choices

The previously open foundational choices are now closed:

| Choice | Final Decision |
| --- | --- |
| First import model | Enterprise Evidence Event. |
| First file import encoding | JSONL, one Enterprise Evidence Event per line. |
| Auth demo | JWT-compatible login with simple RBAC: `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR`. |
| AI provider boundary | Explanation Provider interface/port. |
| Persistence | PostgreSQL from first implementation day. |
| Decision relationship model | Decision Graph as internal PostgreSQL-backed relationship model. |

Remaining implementation details:

- first concrete Explanation Provider adapter for the demo;
- exact local login UX;
- exact PostgreSQL schema, to be created only when implementation is authorized.

These are implementation details, not open architecture decisions.

## Acceptance Impact

Future Phase 1 implementation must prove:

1. an import adapter can produce Enterprise Evidence Events;
2. evidence is normalized without leaking raw payloads;
3. PostgreSQL is the source of truth for ledger and evidence state;
4. Explanation Provider can be swapped without domain changes;
5. JWT-compatible RBAC blocks unauthorized review actions;
6. Decision Graph relationships preserve traceability between evidence, recommendation, human action and ledger entry;
7. no Graph DB, OAuth provider matrix, Redis, Mongo, SQLite, JSON-file store or in-memory source of truth is required.
