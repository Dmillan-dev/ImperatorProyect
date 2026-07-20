# API Specification

## Purpose

Define the conceptual API surface before implementation.

This is not OpenAPI, not protobuf and not a final transport contract. It defines product-level API intent: resources, commands, questions and expected business outcomes.

The API must expose the Decision ROI Platform without leaking connector-specific implementation details.

Decision Ledger behavior is defined in:
- `docs/product/DECISION_LEDGER_V2.md`

MVP approval authority is defined in:
- `docs/product/28_Identity_Access_Approval_Model.md`

MVP first-screen behavior is defined in:
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`

MVP quality attributes are defined in:
- `docs/architecture/27_Quality_Attributes.md`

MVP implementation standard is defined in:
- `docs/architecture/31_MVP_Implementation_Standard.md`

Phase 1 scope and exit criteria are defined in:
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`

Phase 1 foundational implementation decisions are defined in:
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

## API Principles

- The API revolves around Decision ROI Cases.
- Evidence input revolves around Enterprise Evidence Events, not provider-specific source objects.
- Queries return prepared context, not raw provider payloads.
- Commands change business state such as approval, review or recommendation status.
- Commands that approve, reject, defer, mark implementation or validate result require role authority from `docs/product/28_Identity_Access_Approval_Model.md`.
- Every ROI response must expose assumptions.
- Every recommendation response must expose evidence, confidence and risk.
- Future reads and commands must support the quality expectations for explainability, auditability, freshness, latency and graceful degradation.
- Future implementation should be JWT/OAuth2-compatible, but the first MVP must not require multiple identity providers.
- Phase 1 auth uses JWT-compatible RBAC with `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE` and `AUDITOR`.
- Future implementation should preserve hexagonal boundaries: provider and persistence details stay behind adapters.
- Phase 1 API work must serve one Decision ROI Case, one deterministic recommendation and the exact exit criteria in `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`.
- Public customer APIs can be HTTP/REST or GraphQL later.
- Internal service APIs follow the architecture mandate in `docs/architecture/21_Technical_Architecture_Context.md`.

## Core Resources

- Decision
- Decision ROI Case
- Recommendation
- Evidence
- Enterprise Evidence Event
- Timeline
- ROI
- Approval
- Business Value
- Policy
- Integration
- Ledger Entry
- Evidence Snapshot
- ROI Snapshot
- Assumptions Snapshot

## API Surface Summary

The table below is the conceptual platform surface. The MVP should not implement every endpoint at once.

| Area | Endpoint | Purpose |
|---|---|---|
| Decisions | `GET /decisions` | List Decision ROI Cases for review |
| Decisions | `GET /decisions/{decisionId}` | Get one complete Decision ROI Case |
| Timeline | `GET /decisions/{decisionId}/timeline` | Get the ordered decision timeline |
| Evidence | `GET /decisions/{decisionId}/evidence` | Get supporting evidence and lineage |
| ROI | `GET /decisions/{decisionId}/roi` | Get cost, savings, value and assumptions |
| Recommendations | `GET /recommendations` | List active recommendations |
| Recommendations | `GET /recommendations/{recommendationId}` | Get one recommendation with evidence |
| Recommendations | `POST /recommendations` | Request or register a recommendation for a Decision ROI Case |
| Approval | `POST /recommendations/{recommendationId}/approve` | Approve a recommendation |
| Approval | `POST /recommendations/{recommendationId}/reject` | Reject a recommendation |
| Approval | `POST /recommendations/{recommendationId}/defer` | Defer a recommendation |
| Ledger | `GET /ledger` | List immutable decision history |
| Ledger | `GET /ledger/{entryId}` | Get one ledger entry |
| Ledger | `GET /decisions/{decisionId}/ledger` | Get full ledger history for one Decision ROI Case |
| Ledger | `POST /decisions/{decisionId}/ledger/approve` | Record approval with evidence, ROI and assumptions snapshots |
| Ledger | `POST /decisions/{decisionId}/ledger/reject` | Record rejection with reason |
| Ledger | `POST /decisions/{decisionId}/ledger/defer` | Record deferral with required evidence or review date |
| Ledger | `POST /decisions/{decisionId}/ledger/mark-implemented` | Record external implementation of an approved action |
| Ledger | `POST /decisions/{decisionId}/ledger/validate-result` | Record realized value after implementation |
| Business Value | `GET /business-value` | Show recovered value, time and risk avoided |
| Integrations | `GET /integrations` | List integration status and health |
| Policies | `GET /policies` | List policies affecting recommendations |

## MVP API Refactor

For the first paid workflow, API intent should collapse around one aggregate:

**Decision ROI Case**

Minimum MVP API responsibilities:
- accept or load Enterprise Evidence Events through an approved JSONL import boundary,
- read one Decision ROI Case,
- read its timeline,
- read its evidence,
- read its ROI assumptions,
- read its recommendation,
- record approve, reject or defer through Ledger v2,
- record implementation and result validation later.

The first consumer of this subset is the Decision Review Workspace.

Do not prioritize public API, SDKs, GraphQL, broad policy APIs or connector marketplace APIs before the Decision Recovery Workflow is validated.

Do not require multiple OAuth providers, full enterprise SSO or a full observability stack before the Decision Recovery Workflow is validated.

Do not require multiple recommendations, ranking, learning systems or broad connector APIs for Phase 1.

## Decision API

### `GET /decisions`

Question:

Which Decision ROI Cases need attention?

Returns conceptually:
- decision ID,
- title,
- owner,
- status,
- current monthly cost,
- estimated annualized recovery,
- risk,
- confidence,
- recommendation summary,
- review priority.

Primary users:
- Executive Workspace,
- Decisions page,
- Platform and FinOps review workflows.

### `GET /decisions/{decisionId}`

Question:

Can the organization understand and trust this Decision ROI Case?

Returns conceptually:
- Decision,
- Timeline summary,
- Evidence summary,
- ROI summary,
- Recommendation list,
- ownership,
- approval state,
- ledger references.

## Timeline API

### `GET /decisions/{decisionId}/timeline`

Question:

What happened, in what order, and across which systems?

Returns conceptually:
- business context event,
- code and deployment events,
- infrastructure and cost events,
- AI consumption events,
- recommendation event,
- approval event,
- result event.

Timeline entries must preserve source, timestamp, actor or owner, evidence link and confidence contribution.

## Evidence API

### `GET /decisions/{decisionId}/evidence`

Question:

What proof supports this decision, recommendation and ROI?

Returns conceptually:
- evidence ID,
- source integration,
- evidence type,
- observed fact,
- timestamp or period,
- sensitivity,
- lineage,
- related recommendation or ROI claim.

Evidence should be filtered by role and policy.

## ROI API

### `GET /decisions/{decisionId}/roi`

Question:

What does this decision cost, what value does it appear to create, and what can be recovered?

Returns conceptually:
- current monthly cost,
- estimated annualized recovery,
- realized savings when available,
- value signal,
- payback,
- confidence,
- assumptions,
- risk.

Rule:

No ROI number should be returned without assumptions and evidence references.

## Recommendation API

### `GET /recommendations`

Question:

Which approval-ready actions should the company review?

Returns conceptually:
- recommendation ID,
- related decision,
- action,
- family,
- owner,
- approver,
- estimated savings,
- risk,
- confidence,
- status.

### `GET /recommendations/{recommendationId}`

Question:

Why should this recommendation be trusted?

Returns conceptually:
- action,
- rationale,
- evidence,
- ROI,
- risk,
- confidence,
- approval path,
- implementation status when available.

### `POST /recommendations`

Question:

Create, request or register a recommendation for a Decision ROI Case.

Conceptual command input:
- decision ID,
- recommendation family,
- reason,
- evidence references,
- assumptions,
- proposed owner,
- proposed approver.

This endpoint must not execute infrastructure or AI-provider changes.

## Approval API

Approval API describes product intent. Decision Ledger v2 commands are the canonical way to record approval, rejection and deferral because they preserve immutable snapshots.

### `POST /recommendations/{recommendationId}/approve`

Question:

Who approved this recommendation and under which assumptions?

Conceptual command input:
- approver,
- approval note,
- accepted assumptions,
- expected business value,
- policy context.

Effect:
- recommendation status changes to approved,
- ledger entry is created,
- execution remains outside MVP unless explicitly approved in a later phase.

Ledger v2 note:

The canonical approval record is the ledger command `POST /decisions/{decisionId}/ledger/approve`, because it preserves evidence, ROI and assumptions snapshots.

### `POST /recommendations/{recommendationId}/reject`

Records rejection reason and ledger evidence.

### `POST /recommendations/{recommendationId}/defer`

Records deferral reason, review date and required evidence.

## Ledger API

Ledger v2 records business accountability. It is append-only and does not execute changes.

### `GET /ledger`

Question:

What has the company decided over time?

Conceptual filters:
- decision ROI case ID,
- decision state,
- entry type,
- actor,
- owner,
- recommendation family,
- date range.

Returns conceptually:
- ledger entry ID,
- related Decision ROI Case,
- entry type,
- actor,
- decision state,
- timestamp,
- estimated saving,
- realized saving,
- confidence,
- evidence reference.

### `GET /ledger/{entryId}`

Question:

What exactly was recorded for this business decision?

Returns one immutable ledger entry with:
- evidence snapshot,
- ROI snapshot,
- assumptions snapshot,
- previous entry reference,
- related recommendation,
- related Decision ROI Case.

### `GET /decisions/{decisionId}/ledger`

Question:

What is the full accountability history of this Decision ROI Case?

Returns conceptually:
- ordered ledger entries,
- state transitions,
- approvals,
- rejections,
- deferrals,
- implementation records,
- result validations,
- estimated versus realized value.

### `POST /decisions/{decisionId}/ledger/approve`

Question:

Who approved this recommendation and under which evidence, ROI and assumptions?

Conceptual command input:
- actor ID,
- recommendation ID,
- approval note,
- accepted assumptions,
- expected business value.

Effect:
- creates an immutable approved ledger entry,
- records evidence, ROI and assumptions snapshots,
- changes recommendation state to approved,
- does not execute external changes.

### `POST /decisions/{decisionId}/ledger/reject`

Question:

Why was this recommendation rejected?

Conceptual command input:
- actor ID,
- recommendation ID,
- rejection reason.

Effect:
- creates an immutable rejected ledger entry,
- preserves evidence and ROI reviewed at rejection time.

### `POST /decisions/{decisionId}/ledger/defer`

Question:

What evidence or timing is missing before this recommendation can be decided?

Conceptual command input:
- actor ID,
- recommendation ID,
- deferral reason,
- required evidence,
- review date.

Effect:
- creates an immutable deferred ledger entry,
- records the evidence gap or future review point.

### `POST /decisions/{decisionId}/ledger/mark-implemented`

Question:

Was the approved action implemented outside IMPERATOR?

Conceptual command input:
- actor ID,
- implementation note,
- implementation period,
- implementation evidence.

Effect:
- creates an immutable implementation-marked ledger entry,
- links implementation to the prior approval entry.

### `POST /decisions/{decisionId}/ledger/validate-result`

Question:

What realized value was observed after implementation?

Conceptual command input:
- actor ID,
- realized saving,
- currency,
- validation period,
- validation evidence,
- outcome note.

Effect:
- creates an immutable result-validated ledger entry,
- makes realized value available to Business Value reporting.

## Business Value API

### `GET /business-value`

Question:

What economic value has IMPERATOR generated?

Returns conceptually:
- recovered value,
- recovered time,
- risk avoided,
- value by department,
- value by recommendation family,
- estimated ROI of IMPERATOR,
- realized versus projected value.

Rule:

Realized value must come from `result_validated` ledger entries. Estimated value may be shown separately, but it must not be counted as realized recovery.

## Integration API

### `GET /integrations`

Question:

Which operating systems feed the intelligence layer?

Returns conceptually:
- integration name,
- domain,
- health,
- sync status,
- last successful sync,
- related Decision ROI Cases,
- data freshness.

MVP integrations:
- Jira,
- GitHub,
- AWS,
- OpenAI + Anthropic Claude.

## Policy API

### `GET /policies`

Question:

Which business, security, financial or compliance rules affect recommendations and approvals?

Returns conceptually:
- policy ID,
- policy type,
- scope,
- owner,
- affected recommendations,
- approval requirements.

## MVP API Boundary

The MVP API should support:
- reconstructing Decision ROI Cases,
- reviewing evidence,
- showing ROI,
- listing recommendations,
- approving or rejecting recommendations,
- deferring recommendations with required evidence,
- reading ledger history,
- marking external implementation,
- validating realized results,
- proving business value from validated outcomes.

The MVP API should not support:
- autonomous execution,
- broad connector marketplace management,
- raw provider payload exploration,
- generic observability queries,
- customer-facing gRPC as the public API.
