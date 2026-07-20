# 34 - MVP Implementation Blueprint

## Purpose

Define the final MVP value-loop implementation blueprint for IMPERATOR development.

This is the contract between architecture and development.

It answers:

```text
If a team of 5 developers started IMPERATOR tomorrow, what exactly would they build during the first 6 weeks?
```

This document contains implementation specifications only.

It does not create code, SQL, migrations, services, package manifests, OpenAPI definitions, Docker, Kubernetes, Terraform, cloud resources or credentials.

## Authority

This blueprint must follow:

1. `docs/decisions/14_Decision_Log.md`
2. `docs/architecture/31_MVP_Implementation_Standard.md`
3. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
4. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
5. `agents/phase1/12_phase1_closure.md`

This blueprint does not change product strategy, domain scope or architecture decisions.

## 1. MVP Goal

Build one functional MVP proving:

```text
One Decision ROI Case.
One evidence chain.
One ROI View.
One deterministic recommendation.
One AI explanation.
One human review action.
One append-only ledger history.
Minimal auth, security and observability.
```

The MVP solves one problem:

```text
A technical organization cannot reconstruct, explain and approve a cost-recovery decision from scattered operational evidence.
```

The MVP must prove that IMPERATOR can import evidence, normalize it, calculate ROI, generate one deterministic recommendation, explain it with AI, record human review and preserve the decision trail.

Locked first case:

| Field | Value |
| --- | --- |
| Case ID | `DRC-AOA-001` |
| Case | AI Onboarding Assistant Recovery |
| Recommendation | AI model downgrade/change with fallback |
| Evidence import | JSONL, one Enterprise Evidence Event per line |
| Persistence | PostgreSQL |
| Auth | JWT-compatible RBAC |
| Roles | `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR` |
| AI boundary | `ExplanationProvider` |
| Decision relationship model | Decision Graph in PostgreSQL |

## 2. Functional Flow

MVP flow:

```text
Evidence Import
-> Evidence Validation
-> Evidence Store
-> Decision Engine
-> ROI Engine
-> AI Explanation
-> Human Review
-> Decision Ledger
-> Decision Review Workspace
```

The prompt term "Executive Dashboard" is corrected for Phase 1.

The MVP implements `Decision Review Workspace`, not a broad Executive Dashboard. Executive dashboard surfaces remain post-MVP.

### 2.1 Evidence Import

Inputs:

- JSONL file.
- One Enterprise Evidence Event per line.
- `DRC-AOA-001` evidence pack.

Outputs:

- accepted Enterprise Evidence Events,
- rejected event list with safe errors,
- import status.

Responsibilities:

- parse JSONL,
- validate event envelope shape,
- reject malformed records,
- reject forbidden raw payload content,
- attach import correlation ID,
- record import attempt.

Does not:

- calculate ROI,
- create recommendation,
- call AI,
- write ledger approval entries,
- mutate external providers,
- store raw provider dumps.

### 2.2 Evidence Validation

Inputs:

- Enterprise Evidence Events,
- sensitivity labels,
- freshness labels,
- event/evidence vocabulary.

Outputs:

- normalized evidence summaries,
- validation warnings,
- blockers,
- sensitivity-safe evidence set.

Responsibilities:

- validate `raw_payload.mode = not_stored`,
- reject Restricted evidence content,
- check required fields,
- check review period and freshness,
- map source events to evidence types,
- map evidence to `DRC-AOA-001`.

Does not:

- infer missing financial truth,
- override sensitivity,
- hide missing evidence,
- approve the case.

### 2.3 Evidence Store

Inputs:

- accepted Enterprise Evidence Events,
- normalized evidence summaries,
- source/import metadata.

Outputs:

- persisted evidence event records,
- persisted evidence summaries,
- lineage references,
- Decision Graph `derived_from` and `supports` relationships.

Responsibilities:

- persist in PostgreSQL,
- preserve evidence IDs,
- preserve source references,
- preserve freshness, sensitivity and confidence,
- support retrieval by Decision ROI Case.

Does not:

- use memory, JSON files, Redis, MongoDB or SQLite as source of truth,
- persist raw prompts,
- persist raw completions,
- persist secrets,
- expose provider-specific objects to the domain.

### 2.4 Decision Engine

Inputs:

- evidence summaries,
- case lock for `DRC-AOA-001`,
- owner and role references,
- readiness rules.

Outputs:

- Decision ROI Case view,
- readiness state,
- blockers,
- Decision Graph case relationships.

Responsibilities:

- assemble one Decision ROI Case,
- check evidence readiness,
- expose why the case exists,
- expose owner, approver and review state,
- prepare context for ROI and recommendation.

Does not:

- calculate AI explanation,
- execute external changes,
- generate multiple cases,
- rank recommendations.

### 2.5 ROI Engine

Inputs:

- cost evidence,
- usage evidence,
- ROI assumptions,
- review period.

Outputs:

- ROI View,
- current monthly cost,
- projected monthly cost,
- estimated monthly recovery,
- annualized recovery,
- confidence,
- risk.

Responsibilities:

- calculate deterministic ROI,
- separate estimates from realized value,
- expose assumptions,
- block approval if required cost evidence is missing.

Does not:

- use LLM judgment,
- count realized savings,
- mutate ledger entries,
- create Business Value before validation.

Locked ROI:

| Metric | Value |
| --- | ---: |
| AWS monthly cost | EUR410 |
| AI monthly cost | EUR1,930 |
| Current monthly cost | EUR2,340 |
| Projected monthly cost | EUR720 |
| Estimated monthly recovery | EUR1,620 |
| Annualized recovery | EUR19,440 |
| Confidence | 92/100 |

### 2.6 AI Explanation

Inputs:

- prepared deterministic context,
- evidence IDs,
- ROI View,
- recommendation,
- assumptions,
- risk and confidence.

Outputs:

- explanation text,
- cited evidence IDs,
- explanation status.

Responsibilities:

- call `ExplanationProvider.generateExplanation(preparedContext)`,
- explain recommendation in natural language,
- cite evidence IDs,
- avoid raw provider payloads.

Does not:

- calculate ROI,
- choose recommendation,
- create evidence,
- approve,
- reject,
- defer,
- validate result,
- mutate persistence.

### 2.7 Human Review

Inputs:

- authenticated actor,
- RBAC role,
- Decision ROI Case,
- ROI View,
- recommendation,
- AI explanation,
- blockers.

Outputs:

- approved, rejected or deferred review action,
- review reason,
- ledger command.

Responsibilities:

- enforce RBAC,
- require reason for rejection or deferral,
- block unauthorized action,
- block approval when required evidence is missing.

Does not:

- execute provider-side changes,
- bypass ledger,
- allow AI or connectors to approve.

### 2.8 Decision Ledger

Inputs:

- review action,
- actor and role,
- evidence snapshot,
- ROI snapshot,
- assumption snapshot,
- recommendation snapshot.

Outputs:

- append-only LedgerEntry,
- case state update,
- Decision Graph `recorded_as` relationship.

Responsibilities:

- append entries,
- preserve reviewed context,
- prevent historical mutation,
- expose ledger history.

Does not:

- calculate ROI,
- create recommendations,
- rewrite history,
- count estimated recovery as Business Value.

### 2.9 Decision Review Workspace

Inputs:

- Decision ROI Case view,
- evidence summaries,
- ROI View,
- recommendation,
- AI explanation,
- ledger history,
- authenticated actor role.

Outputs:

- one operational review screen,
- role-aware action states,
- evidence and ledger visibility.

Responsibilities:

- show one case,
- show one recommendation,
- show ROI and assumptions,
- show AI explanation,
- show ledger history,
- allow approve, reject or defer if authorized.

Does not:

- become broad Executive Dashboard,
- show realized value before validation,
- expose Restricted data,
- add multi-case portfolio analytics.

## 3. Component Responsibilities

### 3.1 Connector Layer

Purpose:

- convert source-system data or imported records into Enterprise Evidence Events.

Public responsibilities:

- parse manual/import inputs,
- later adapt Jira, GitHub, AWS and AI provider sources,
- produce provider-neutral Enterprise Evidence Events,
- report intake status and safe errors.

Forbidden responsibilities:

- ROI calculation,
- recommendation generation,
- approval,
- ledger writes,
- provider mutation,
- provider-shaped domain models.

Dependencies:

- event/evidence vocabulary,
- security rules,
- Evidence Normalizer.

### 3.2 Evidence Normalizer

Purpose:

- transform Enterprise Evidence Events into normalized evidence summaries.

Public responsibilities:

- validate event type,
- validate sensitivity,
- validate freshness,
- map evidence type,
- create evidence summaries,
- produce blockers.

Forbidden responsibilities:

- inventing evidence,
- hiding missing evidence,
- deciding recommendation,
- calling AI.

Dependencies:

- Enterprise Evidence Event,
- evidence vocabulary,
- security model.

### 3.3 Evidence Repository

Purpose:

- persist evidence events, summaries and lineage in PostgreSQL.

Public responsibilities:

- save accepted evidence events,
- save evidence summaries,
- find evidence by case ID,
- expose evidence lineage.

Forbidden responsibilities:

- raw source dump storage,
- provider-specific business logic,
- ROI calculation,
- ledger mutation.

Dependencies:

- PostgreSQL,
- Decision Graph relationship persistence.

### 3.4 Decision Engine

Purpose:

- assemble and evaluate one Decision ROI Case.

Public responsibilities:

- load evidence,
- check readiness,
- assemble case view,
- expose blockers,
- coordinate deterministic case state.

Forbidden responsibilities:

- AI decisioning,
- provider integration,
- UI rendering,
- approval authority.

Dependencies:

- Evidence Repository,
- Decision Repository,
- Decision Graph Repository.

### 3.5 ROI Engine

Purpose:

- calculate deterministic ROI for `DRC-AOA-001`.

Public responsibilities:

- calculate current cost,
- calculate estimated recovery,
- annualize recovery,
- expose assumptions,
- assign risk/confidence from deterministic rules.

Forbidden responsibilities:

- realized value calculation before validation,
- LLM-based financial truth,
- recommendation ranking.

Dependencies:

- evidence summaries,
- ROI assumptions,
- Decision Repository.

### 3.6 Explanation Provider

Purpose:

- generate human-readable explanation from prepared deterministic context.

Public responsibilities:

- expose `generateExplanation(preparedContext)` conceptually,
- return explanation text,
- cite evidence IDs,
- report success/failure.

Forbidden responsibilities:

- ROI calculation,
- recommendation selection,
- evidence creation,
- approval,
- persistence mutation.

Dependencies:

- prepared context,
- security filtering,
- provider adapter.

### 3.7 Decision Ledger

Purpose:

- preserve append-only decision accountability.

Public responsibilities:

- append review entries,
- read ledger history,
- preserve snapshots,
- prevent mutation.

Forbidden responsibilities:

- ROI calculation,
- recommendation creation,
- provider execution,
- deleting historical entries.

Dependencies:

- Ledger Repository,
- actor/role context,
- snapshot builders,
- Decision Graph Repository.

### 3.8 Business Value Calculator

Purpose:

- expose realized Business Value only after result validation.

Public responsibilities:

- read validated ledger entries,
- distinguish estimated recovery from realized value,
- return zero/unavailable when no validation exists.

Forbidden responsibilities:

- treating estimated recovery as realized value,
- inventing post-action savings,
- changing original ROI snapshots.

Dependencies:

- Ledger Repository,
- BusinessValueRepository,
- result validation evidence.

### 3.9 Executive Workspace

Phase 1 correction:

```text
Executive Workspace = Decision Review Workspace for one case.
```

Purpose:

- give the reviewer one operational place to decide.

Public responsibilities:

- show case,
- show evidence,
- show ROI,
- show recommendation,
- show explanation,
- show ledger,
- expose role-aware actions.

Forbidden responsibilities:

- broad dashboard,
- portfolio analytics,
- multi-case ranking,
- connector administration,
- AI chat workspace.

Dependencies:

- read APIs,
- auth/RBAC,
- ledger commands.

## 4. Domain Objects

### DecisionROICase

Central aggregate for the MVP.

Responsibilities:

- group evidence, ROI, recommendation, review state and ledger history for `DRC-AOA-001`.

### Evidence

Responsibilities:

- represent normalized proof,
- preserve source lineage,
- carry sensitivity, freshness and confidence.

Input envelope:

- Enterprise Evidence Event.

### Decision

Responsibilities:

- represent the business-relevant choice under review.

### Recommendation

Responsibilities:

- represent one deterministic proposed action.

### ROI

Responsibilities:

- represent current cost, projected cost, estimated recovery, assumptions, confidence and risk.

### Approval

Responsibilities:

- represent human approve, reject or defer action.

### LedgerEntry

Responsibilities:

- represent append-only decision history and reviewed snapshots.

### BusinessValue

Responsibilities:

- represent realized value only after result validation.

### User

Responsibilities:

- represent authenticated human actor.

### Role

Responsibilities:

- represent RBAC authority.

Allowed MVP roles:

- `ADMIN`,
- `PLATFORM_ENGINEER`,
- `FINANCE`,
- `AUDITOR`.

## 5. Repository Interfaces

These are conceptual contracts, not code.

### EvidenceRepository

Intent:

- persist and retrieve Enterprise Evidence Events and evidence summaries.

Must support:

- save imported event,
- save normalized evidence,
- find evidence by Decision ROI Case,
- find evidence by evidence ID.

### DecisionRepository

Intent:

- persist and retrieve Decision ROI Case state.

Must support:

- get one Decision ROI Case,
- save readiness state,
- save ROI/recommendation references,
- expose current case status.

### LedgerRepository

Intent:

- append and read ledger entries.

Must support:

- append ledger entry,
- read ledger by case,
- read ledger entry by ID,
- prevent update/delete of historical entries.

### BusinessValueRepository

Intent:

- persist and retrieve validated Business Value.

Must support:

- save validated value,
- read Business Value by case,
- return unavailable when no validation exists.

### DecisionGraphRepository

Intent:

- persist and retrieve relationship records in PostgreSQL.

Must support:

- save node reference,
- save relationship,
- read relationships by case,
- distinguish `correlates_with` from `caused_by`.

## 6. Service Interfaces

These are conceptual application services, not code.

### DecisionService

Intent:

- assemble and expose one Decision ROI Case.

### ROIService

Intent:

- calculate deterministic ROI and expose assumptions.

### RecommendationService

Intent:

- create exactly one deterministic recommendation.

### ExplanationService

Intent:

- prepare safe context and invoke Explanation Provider.

### ApprovalService

Intent:

- authorize and process approve, reject and defer actions.

### ConnectorService

Intent:

- handle evidence import and future source adapters through Enterprise Evidence Events.

### LedgerService

Intent:

- create append-only ledger entries from business actions.

### BusinessValueService

Intent:

- expose realized value only after result validation.

## 7. REST API - High Level

No OpenAPI is defined here.

Route convention uses plural REST resources, for example `/decisions/{id}`.

### `POST /evidence/import`

Behavior:

- accepts JSONL import,
- validates Enterprise Evidence Events,
- stores accepted evidence,
- returns import result and safe errors.

Must not:

- accept raw prompts/completions,
- calculate ROI directly,
- approve recommendations.

### `GET /decisions`

Behavior:

- returns MVP decision list.

Phase 1 may return only `DRC-AOA-001`.

### `GET /decisions/{id}`

Behavior:

- returns one Decision ROI Case view with evidence, ROI, recommendation, explanation status, review state and ledger summary.

### `GET /decisions/{id}/timeline`

Behavior:

- returns ordered lifecycle entries with evidence references.

### `GET /decisions/{id}/evidence`

Behavior:

- returns filtered evidence summaries and lineage for the case.

### `GET /decisions/{id}/roi`

Behavior:

- returns deterministic ROI, assumptions, risk and confidence for the case.

### `GET /recommendations/{id}`

Behavior:

- returns one recommendation with evidence references, ROI link, risk, confidence and approval path.

### `GET /decisions/{id}/ledger`

Behavior:

- returns append-only ledger history for one Decision ROI Case.

### `POST /decisions/{id}/ledger/approve`

Behavior:

- requires `ADMIN`,
- requires recommendation ready,
- creates append-only ledger entry.

### `POST /decisions/{id}/ledger/reject`

Behavior:

- requires `ADMIN`,
- requires rejection reason,
- creates append-only ledger entry.

### `POST /decisions/{id}/ledger/defer`

Behavior:

- requires authorized role,
- requires deferral reason,
- records blocker or required evidence,
- creates append-only ledger entry.

### `POST /decisions/{id}/ledger/mark-implemented`

Behavior:

- requires authorized technical owner,
- records external implementation marker,
- creates append-only ledger entry.

Must not:

- mutate external providers,
- validate realized value.

### `POST /decisions/{id}/ledger/validate-result`

Behavior:

- requires authorized FinOps or business reviewer,
- records result validation when post-action evidence exists,
- creates append-only ledger entry,
- enables Business Value to become realized.

Must not:

- count estimated recovery as realized value,
- mutate original ROI or approval snapshots.

### `GET /business-value`

Behavior:

- returns realized Business Value only from validated results,
- returns unavailable/zero for `DRC-AOA-001` until result validation exists.

### `GET /ledger`

Behavior:

- returns append-only ledger history,
- supports filtering by decision ID.

## 8. PostgreSQL Tables

No `CREATE TABLE` is defined here.

Expected tables:

| Table | Purpose | Relationships |
| --- | --- | --- |
| `users` | Authenticated actor references | has roles; creates ledger entries |
| `roles` | MVP RBAC roles | assigned to users |
| `evidence_imports` | Import attempts and status | has evidence events |
| `enterprise_evidence_events` | Canonical imported events | derived into evidence summaries |
| `evidence_summaries` | Normalized evidence | supports decision cases, ROI and recommendations |
| `decision_cases` | Decision ROI Case state | has ROI, recommendation, ledger entries |
| `roi_views` | Deterministic ROI outputs | belongs to decision case |
| `roi_assumptions` | Explicit ROI assumptions | used by ROI view |
| `recommendations` | One deterministic recommendation | belongs to decision case and references ROI |
| `ai_explanations` | Explanation Provider output | explains recommendation |
| `ledger_entries` | Append-only decision history | belongs to decision case |
| `business_values` | Validated realized value | created after result validation |
| `decision_graph_nodes` | Internal graph node references | used by graph edges |
| `decision_graph_edges` | Internal graph relationships | connects evidence, case, recommendation, ledger and result |

PostgreSQL is the source of truth.

Do not use in-memory store, JSON files, SQLite, MongoDB or Redis as source of truth.

## 9. Security

Required:

- JWT-compatible login,
- RBAC with `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR`,
- static policies,
- audit logging for review actions,
- append-only ledger,
- no secrets persisted,
- no raw prompts persisted,
- no raw completions persisted,
- no provider payload dumps persisted,
- no Restricted evidence sent to AI,
- correlation ID in logs.

Security rules:

- authentication identifies actor,
- authorization controls action,
- approval authority is product-specific,
- `ADMIN` is Phase 1 demo approver only,
- connectors and AI cannot approve.

## 10. Non Functional Requirements

### Performance

- Import one MVP JSONL evidence pack within interactive demo time.
- Load one Decision ROI Case in a normal web request.
- No batch-scale performance target in MVP.

### Reliability

- Persist evidence, decision, recommendation and ledger state in PostgreSQL.
- Process restart must not lose ledger history.
- AI explanation failure must not invalidate deterministic recommendation.

### Determinism

- ROI must be deterministic.
- Recommendation must be deterministic.
- AI text must not alter business state.

### Explainability

- Recommendation must reference evidence IDs.
- ROI must expose assumptions.
- Explanation must cite evidence IDs.

### Auditability

- Ledger entries are append-only.
- Approval/rejection/deferral preserve reviewed snapshots.
- Decision Graph preserves relationships.

### Extensibility

- Connectors are adapters.
- AI providers are adapters.
- PostgreSQL schema should preserve future connector and case expansion without adding Graph DB in MVP.

## 11. Out Of Scope

Do not implement in the MVP:

- more than one Decision ROI Case,
- multiple recommendations,
- ranking,
- learning systems,
- autonomous execution,
- live full connectors,
- provider write operations,
- OAuth,
- enterprise SSO,
- SAML,
- SCIM,
- full policy engine,
- public API/SDK,
- Kafka,
- Kubernetes,
- Terraform,
- Graph DB,
- vector database,
- Redis,
- MongoDB,
- SQLite,
- JSON-file source of truth,
- full observability stack,
- broad Executive Dashboard,
- AI chat workspace,
- connector marketplace,
- multi-tenant admin console.

## 12. Implementation Order

Team assumption:

- 5 developers.
- 6 weeks.
- No platform expansion.

### Week 1 - Project Skeleton And Persistence Boundary

Implement:

- repository skeleton,
- backend modular monolith boundary,
- frontend app shell,
- PostgreSQL connection boundary,
- migration framework setup,
- JWT-compatible auth skeleton,
- base RBAC roles.

Deliverable:

- app starts,
- database connects,
- `/health` and `/ready` exist,
- roles are represented.

Do not implement:

- business logic,
- live connectors,
- AI provider calls.

### Week 2 - Evidence Import And Normalization

Implement:

- `POST /evidence/import`,
- JSONL parser,
- Enterprise Evidence Event validation,
- evidence normalization,
- evidence persistence,
- import errors,
- Decision Graph `derived_from` and `supports` relationships.

Deliverable:

- `DRC-AOA-001` evidence pack can be imported and retrieved.

Do not implement:

- ROI,
- recommendation,
- approval.

### Week 3 - Decision Case, ROI And Recommendation

Implement:

- Decision ROI Case assembly,
- `GET /decisions`,
- `GET /decisions/{id}`,
- `GET /decisions/{id}/timeline`,
- `GET /decisions/{id}/evidence`,
- `GET /decisions/{id}/roi`,
- `GET /recommendations/{id}`,
- deterministic ROI calculation,
- ROI assumptions,
- one deterministic recommendation,
- blockers and readiness state.

Deliverable:

- one case shows evidence, ROI and recommendation.

Do not implement:

- multiple recommendations,
- ranking,
- AI decisioning.

### Week 4 - Explanation Provider And Review Actions

Implement:

- prepared AI context,
- `ExplanationProvider` adapter boundary,
- first explanation adapter,
- explanation persistence,
- approve/reject/defer commands,
- RBAC enforcement.

Deliverable:

- AI explanation appears,
- `ADMIN` can approve/reject,
- authorized roles can defer according to policy.

Do not implement:

- OAuth,
- AI-created recommendation,
- provider-specific domain logic.

### Week 5 - Ledger, Business Value Boundary And Workspace

Implement:

- append-only ledger entries,
- snapshots for evidence/ROI/assumptions/recommendation,
- `GET /ledger`,
- `GET /business-value`,
- Decision Review Workspace,
- role-aware action states,
- realized value unavailable until validation.

Deliverable:

- user can review one case, act, and see ledger history.

Do not implement:

- broad dashboard,
- portfolio analytics,
- real post-action savings automation.

### Week 6 - Acceptance Hardening And Demo Closure

Implement:

- integrated acceptance path,
- negative test coverage,
- audit logging,
- safe error handling,
- metrics,
- correlation IDs,
- demo seed/import pack,
- R&D evidence records for actual implementation work.

Deliverable:

- end-to-end MVP demo passes:

```text
import evidence
-> validate evidence
-> build Decision ROI Case
-> calculate ROI
-> generate recommendation
-> explain with AI
-> approve/reject/defer
-> record ledger
-> show workspace
```

Do not implement:

- post-MVP infrastructure,
- new product decisions,
- broad connector automation.

## Final Phase 1 Statement

After this blueprint, Phase 1 documentation is complete.

No more conceptual Phase 1 documents should be added unless a contradiction is discovered.

Next phases:

```text
Phase 2 - Platform Foundation
Phase 3 - MVP Implementation
```

Phase 2 should create the controlled technical foundation only: project structure, module boundaries, route shells, persistence foundation, auth foundation, observability foundation, Docker/local development foundation and CI foundation, without business intelligence.

Phase 2 implementation mechanics are governed by `docs/architecture/37_Implementation_Contract.md`.

Phase 2 sprint sequencing is governed by `agents/phase2/README.md`.

Phase 3 should implement the complete `DRC-AOA-001` value loop.
