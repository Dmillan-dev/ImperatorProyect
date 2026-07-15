# API Specification

## Purpose

Define the conceptual API surface before implementation.

This is not OpenAPI, not protobuf and not a final transport contract. It defines product-level API intent: resources, commands, questions and expected business outcomes.

The API must expose the Decision ROI Platform without leaking connector-specific implementation details.

## API Principles

- The API revolves around Decision ROI Cases.
- Queries return prepared context, not raw provider payloads.
- Commands change business state such as approval, review or recommendation status.
- Every ROI response must expose assumptions.
- Every recommendation response must expose evidence, confidence and risk.
- Public customer APIs can be HTTP/REST or GraphQL later.
- Internal service APIs follow the architecture mandate in `docs/architecture/21_Technical_Architecture_Context.md`.

## Core Resources

- Decision
- Decision ROI Case
- Recommendation
- Evidence
- Timeline
- ROI
- Approval
- Business Value
- Policy
- Integration
- Ledger Entry

## API Surface Summary

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
| Business Value | `GET /business-value` | Show recovered value, time and risk avoided |
| Integrations | `GET /integrations` | List integration status and health |
| Policies | `GET /policies` | List policies affecting recommendations |

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

### `POST /recommendations/{recommendationId}/reject`

Records rejection reason and ledger evidence.

### `POST /recommendations/{recommendationId}/defer`

Records deferral reason, review date and required evidence.

## Ledger API

### `GET /ledger`

Question:

What has the company decided over time?

Returns conceptually:
- ledger entry ID,
- decision,
- recommendation,
- approver,
- decision state,
- financial result,
- timestamp,
- evidence references.

### `GET /ledger/{entryId}`

Question:

What exactly was recorded for this business decision?

Returns one immutable ledger entry with evidence, approval state, assumptions and outcome.

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
- reading ledger history,
- proving business value.

The MVP API should not support:
- autonomous execution,
- broad connector marketplace management,
- raw provider payload exploration,
- generic observability queries,
- customer-facing gRPC as the public API.

