# Database Model

## Purpose

Define IMPERATOR's conceptual data model before implementation.

This is not SQL, not an ORM model and not a migration plan. It explains which data groups must exist, what each group means, how they relate and which invariants future PostgreSQL, graph, search or object-storage designs must preserve.

## Source Documents

- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/21_Technical_Architecture_Context.md`
- `docs/rfcs/0001-knowledge-graph-model.md`
- `docs/rfcs/0002-module-communication-architecture.md`

## Database Principles

- The database model must serve the Decision ROI Case.
- Business meaning comes from the domain model, not from provider payloads.
- Evidence lineage must be preserved.
- Ledger history must be append-only.
- Estimated value and realized value must be separated.
- Connector sync state must not pollute core business entities.
- AI-readable context must be prepared, filtered and policy-aware.
- Raw source data, normalized evidence and executive views are different data layers.

## Conceptual Data Areas

### 1. Organization and Identity

Purpose:

Represent the customer company, its users, roles, teams and access boundaries.

Conceptual records:
- Organization
- Department
- Team
- User
- Role
- Permission
- Tenant Boundary

Key relationships:
- Organization contains Departments.
- Department contains Teams and Users.
- User has Roles.
- Role controls access to Decisions, Evidence, Ledger and Business Value.

### 2. Decision Domain

Purpose:

Represent the central business object of IMPERATOR.

Conceptual records:
- Decision ROI Case
- Decision
- Timeline
- Timeline Step
- Owner
- Workflow State

Key relationships:
- Decision ROI Case contains one Decision.
- Decision belongs to Organization and usually Project.
- Decision has Owner.
- Decision produces Timeline.
- Timeline contains ordered Timeline Steps.

### 3. Evidence and Event Domain

Purpose:

Preserve why IMPERATOR believes a fact is true.

Conceptual records:
- Operational Event
- Normalized Event
- Evidence
- Evidence Source
- Evidence Lineage
- Evidence Sensitivity

Key relationships:
- Operational Event comes from Integration.
- Normalized Event is derived from Operational Event.
- Evidence is derived from Normalized Event.
- Evidence supports Timeline, ROI or Recommendation.
- Evidence has lineage and sensitivity.

Layering:
- raw source metadata: provider-specific and isolated,
- normalized events: platform-shaped facts,
- evidence: trusted business-context facts.

### 4. Integration and Connector Domain

Purpose:

Represent external systems and sync health without coupling them to the core domain.

Conceptual records:
- Integration
- Connector
- Connector Configuration
- Sync Run
- Sync Cursor
- Provider Account
- Integration Health
- Rate Limit State

Key relationships:
- Organization configures Integration.
- Integration uses Connector.
- Connector produces Operational Events.
- Sync Run records fetch/receive activity.
- Integration Health affects Decision ROI Case confidence.

### 5. Project and Work Domain

Purpose:

Connect business intent, code changes and deployment context.

Conceptual records:
- Project
- Work Item
- Pull Request
- Commit
- Review
- Deployment Reference

MVP source systems:
- Jira for Work Item and business context,
- GitHub for Pull Request, Commit, Review and Deployment Reference.

Key relationships:
- Project contains Work Items.
- Work Item may link to Decision.
- Pull Request may implement Work Item.
- Deployment Reference may create or modify Resources.

### 6. Infrastructure and Cost Domain

Purpose:

Represent resources, usage and cost signals connected to decisions.

Conceptual records:
- Resource
- Resource Tag
- Usage Metric
- Cost
- Cost Period
- Cost Allocation

MVP source:
- AWS.

Key relationships:
- Resource belongs to Project, Owner or Decision ROI Case when correlation is known.
- Cost is measured over a period.
- Usage Metric explains whether cost appears justified.
- Cost Allocation supports ROI calculations.

### 7. AI Consumption Domain

Purpose:

Represent model, token, request and AI agent usage connected to decisions.

Conceptual records:
- AI Provider
- AI Model
- AI Usage
- AI Agent
- Prompt or Task Type
- Token Usage
- AI Cost

MVP source:
- OpenAI + Anthropic Claude.

Key relationships:
- AI Usage belongs to User, Team, Application or Agent.
- AI Usage may support Decision ROI Case evidence.
- AI Cost contributes to ROI.
- AI Agent may be removed, consolidated or downgraded by recommendation.

### 8. ROI and Business Value Domain

Purpose:

Represent cost, value, savings and assumptions.

Conceptual records:
- ROI View
- ROI Assumption
- Current Cost
- Estimated Saving
- Annualized Recovery
- Realized Saving
- Business Value
- Value Proxy

Key relationships:
- ROI View belongs to Decision ROI Case.
- ROI View references Evidence.
- ROI Assumption explains how a number was produced.
- Estimated Saving is separate from Realized Saving.
- Business Value can include cost saved, time recovered and risk avoided.

### 9. Recommendation Domain

Purpose:

Represent approval-ready actions generated from evidence and ROI.

Conceptual records:
- Recommendation
- Recommendation Family
- Recommendation Rationale
- Confidence
- Risk Assessment
- Approval Path
- Recommendation Status

Key relationships:
- Recommendation belongs to Decision ROI Case.
- Recommendation references Evidence and ROI View.
- Recommendation requires Owner and Approver.
- Recommendation can be approved, rejected or deferred.

### 10. Policy and Governance Domain

Purpose:

Represent rules that affect recommendations, evidence visibility and approvals.

Conceptual records:
- Policy
- Policy Scope
- Policy Evaluation
- Approval Rule
- Data Sensitivity Rule
- Compliance Requirement

Key relationships:
- Policy belongs to Organization.
- Policy governs Recommendation or Approval.
- Policy can restrict Evidence access.
- Policy Evaluation may block or modify Recommendation status.

### 11. Decision Ledger Domain

Purpose:

Preserve immutable decision history.

Conceptual records:
- Ledger Entry
- Evidence Snapshot
- ROI Snapshot
- Assumptions Snapshot
- Approval Record
- Rejection Record
- Deferral Record
- Implementation Record
- Result Validation Record
- Audit Evidence Reference

Key relationships:
- Ledger Entry references Decision ROI Case.
- Ledger Entry references Recommendation when applicable.
- Ledger Entry records actor, timestamp, state change, reason, evidence, ROI and assumptions.
- Evidence Snapshot preserves decision-time proof.
- ROI Snapshot preserves decision-time cost, savings, risk and confidence.
- Assumptions Snapshot preserves accepted or reviewed assumptions.
- Result Validation Record separates realized saving from estimated saving.
- Ledger Entry is append-only.

Entry types:
- recommendation_created,
- approved,
- rejected,
- deferred,
- implementation_marked,
- result_validated,
- evidence_requested,
- case_closed.

## Conceptual Read Models

Future product surfaces need optimized views. These are conceptual read models, not implementation tables.

### Executive Workspace View

Answers:

How is the company right now?

Needs:
- projected annual savings,
- top recommendation queue,
- selected Decision ROI Case,
- risk and confidence,
- evidence summary,
- business value summary.

### Decision Detail View

Answers:

Can we trust this recommendation enough to approve it?

Needs:
- full Decision ROI Case,
- timeline,
- evidence,
- ownership,
- ROI assumptions,
- recommendation rationale,
- approval action.

### Decision Ledger View

Answers:

What has the company decided over time?

Needs:
- ledger entries,
- approval state,
- evidence references,
- financial result,
- actor and timestamp.

### Business Value View

Answers:

What economic value has IMPERATOR generated?

Needs:
- recovered value,
- recovered time,
- risk avoided,
- value by department,
- projected versus realized savings.

## Data Lifecycle

1. Connector observes provider data.
2. Operational Event is captured with source metadata.
3. Ingestion normalizes event.
4. Context Engine creates Evidence.
5. Evidence attaches to Decision ROI Case.
6. ROI Engine creates ROI View with assumptions.
7. Recommendation Engine creates Recommendation.
8. User approves, rejects or defers.
9. Decision Ledger records immutable outcome.
10. External implementation may be marked when the company acts.
11. Result validation records realized savings separately from estimates.
12. Business Value updates when savings or outcomes are validated.

## Storage Responsibilities

### Canonical relational store

Purpose:
- organizations,
- users,
- decisions,
- recommendations,
- ROI,
- evidence snapshots,
- ROI snapshots,
- assumptions snapshots,
- policies,
- ledger entries,
- integration configuration.

Candidate future technology:
- PostgreSQL.

### Relationship / graph layer

Purpose:
- decision-to-evidence traversal,
- owner-to-cost traversal,
- project-to-resource traversal,
- duplicate service or agent analysis.

Candidate future approach:
- explicit relationship tables or edge model first,
- graph database only if traversal complexity justifies it.

### Evidence artifact storage

Purpose:
- source excerpts,
- approved exports,
- evidence files,
- audit artifacts.

Candidate future technology:
- object storage.

### Search and semantic layer

Purpose:
- searchable decision history,
- semantic evidence retrieval,
- AI Advisor context.

Candidate future approach:
- PostgreSQL search, pgvector or dedicated search/vector store later.

### Cache and ephemeral state

Purpose:
- short-lived computation state,
- sync progress,
- expensive read-model acceleration.

Candidate future technology:
- Redis.

## Data Invariants

- No Recommendation without Decision ROI Case.
- No approval-ready Recommendation without Evidence.
- No ROI without assumptions.
- No Ledger mutation after write.
- No approval ledger entry without evidence, ROI and assumptions snapshots.
- No rejection ledger entry without a reason.
- No deferral ledger entry without required evidence or review date.
- No AI context without sensitivity and policy filtering.
- No connector-owned business logic.
- No provider payload as canonical domain object.
- No realized savings recorded as estimated savings.
- No Business Value realized total without a result validation entry.

## MVP Data Boundary

The MVP conceptual database model must support:

- Jira business context,
- GitHub code and deployment evidence,
- AWS resource, usage and cost signals,
- OpenAI + Anthropic Claude usage and cost signals,
- Decision ROI Timeline,
- five recommendation families,
- approval record,
- ledger history,
- business value reporting.

The MVP should not model:

- universal connector marketplace,
- full observability telemetry,
- full GRC case management,
- autonomous execution state,
- unlimited raw log storage.
