# Core Domain Model

## Purpose

Define the business domain model of IMPERATOR before implementation.

This document is the shared language for Product, Java, Python, PostgreSQL, React and future AI agents. It does not define classes, tables, protobuf fields, OpenAPI schemas or UI components.

Everything in IMPERATOR revolves around one central business object:

**Decision ROI Case**

A Decision ROI Case connects a business decision to evidence, ownership, cost, value, recommendation, approval and result.

Canonical lifecycle:

Business Decision -> Operational Event -> Evidence -> Timeline -> ROI -> Recommendation -> Approval -> Business Value -> Decision Ledger

## Domain Principles

- A Decision is the primary unit of meaning.
- Evidence explains why the platform believes something.
- ROI numbers must be traceable to cost, value signals and assumptions.
- Recommendations are approval-ready, not autonomous execution.
- The company decides; IMPERATOR recommends and records.
- Integrations provide signals, but they are not the product domain.
- The Decision Ledger records business accountability over time.

## Core Aggregate

### Decision ROI Case

The aggregate that binds the MVP story.

It contains:
- one Decision,
- one Timeline,
- one or more Evidence items,
- one current cost view,
- one ROI view,
- zero or more Recommendations,
- approval state,
- resulting Business Value when action is taken.

MVP reconstruction:

Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude -> Recommendation -> Result

## Core Entities

### Decision

A business-relevant choice, initiative, change or shipped capability whose cost, value, ownership and result can be reviewed.

Examples:
- approve an AI onboarding assistant,
- deploy a new infrastructure service,
- keep or retire an internal agent,
- change an AI model,
- consolidate duplicated services.

Key business questions:
- Why does this decision exist?
- Who owns it?
- Who approved it?
- What changed because of it?
- What does it cost now?
- What value does it appear to create?
- What should leadership approve next?

### Recommendation

An evidence-backed action proposed by IMPERATOR to recover money, time or risk.

MVP families:
- downgrade or change AI model,
- remove unused AI agents,
- detect underutilized AWS resources,
- identify features with negative ROI,
- consolidate duplicated services or agents.

A Recommendation must include reason, evidence, owner, approver, risk, confidence, estimated savings and approval path.

### Evidence

A piece of traceable proof used to support a Decision ROI Case.

Evidence may come from Jira, GitHub, AWS, OpenAI + Anthropic Claude, policy records, usage signals, ownership metadata or manual pilot reconstruction.

Evidence must answer:
- source,
- observed fact,
- timestamp or period,
- related decision,
- confidence contribution,
- sensitivity level.

### Operational Event

An observed event from an enterprise system that may become evidence after normalization and correlation.

Examples:
- Jira ticket created or closed,
- pull request merged,
- deployment completed,
- AWS cost increased,
- model usage dropped,
- policy violation detected.

Operational Events are raw signals. Evidence is a trusted, contextualized signal.

### Timeline

The ordered business narrative of a Decision ROI Case.

The MVP Timeline connects:
- Business Context,
- Code & Deployment,
- Infrastructure & Cost,
- AI Consumption,
- Financial Impact,
- Recommendation,
- Approval,
- Result.

### Owner

The person, team or function accountable for a Decision, Recommendation, resource, agent, cost or approval.

Ownership must be explicit enough for a buyer to know who can act.

### Approval

The business authorization to accept, reject, defer or request more evidence for a Recommendation.

Approval is not execution. Approval records company intent and accountability.

### Decision Ledger

The immutable accountability history of Decision ROI Cases.

The Decision Ledger records what the company decided, why, who acted, which evidence and assumptions were reviewed, and what result was later validated.

For the MVP, ledger history should first live inside each Decision ROI Case. A standalone Decision Ledger surface becomes valuable after enough decisions have been reviewed to audit outcomes and accumulated value.

Canonical module contract:
- `docs/product/DECISION_LEDGER_V2.md`

### Ledger Entry

An append-only record of a business-relevant state change in a Decision ROI Case.

Examples:
- recommendation created,
- recommendation approved,
- recommendation rejected,
- recommendation deferred,
- implementation marked,
- result validated,
- case closed.

A Ledger Entry must preserve actor, timestamp, decision state, reason, evidence snapshot, ROI snapshot, assumptions snapshot, estimated saving and realized saving when available.

### Evidence Snapshot

A versioned record of the evidence used at the moment of a ledger decision.

Evidence Snapshots prevent historical approvals from changing meaning when new evidence arrives or when a connector is disabled.

### ROI Snapshot

A versioned record of the ROI view used at the moment of a ledger decision.

ROI Snapshots preserve current monthly cost, estimated recovery, confidence, risk and assumptions at decision time.

### Assumptions Snapshot

A versioned record of the assumptions reviewed or accepted for a recommendation, approval, rejection, deferral or result validation.

### Result Validation

The act of recording realized business value after an approved recommendation is implemented outside IMPERATOR.

Result Validation creates a new Ledger Entry and must not overwrite the original estimate.

### Business Value

The measurable or estimated value created by a Decision or by acting on a Recommendation.

Business Value may include:
- cost saved,
- time recovered,
- risk avoided,
- compliance effort reduced,
- operational clarity improved.

### ROI

The economic interpretation of cost, value, savings and assumptions for a Decision ROI Case.

ROI must expose:
- current monthly cost,
- annualized recovery,
- value signal,
- assumptions,
- confidence,
- payback,
- risk.

### Policy

A business, financial, security or compliance rule that constrains decisions and recommendations.

Examples:
- AI model usage policy,
- spend threshold policy,
- approval policy,
- data sensitivity policy,
- compliance evidence policy.

### Resource

An operational asset that consumes cost or supports a shipped decision.

Examples:
- AWS Lambda function,
- ECS service,
- EC2 instance,
- database,
- storage bucket,
- AI application runtime,
- internal service.

A Resource can be linked to a Decision ROI Case when evidence can explain why it exists, who owns it and what value or cost it produces.

### Cost

A monetary signal associated with a Resource, AI Usage, Project, Decision ROI Case or Organization over a period of time.

Cost must include:
- amount,
- currency,
- period,
- source,
- related owner or decision when known,
- confidence or assumption when attribution is incomplete.

Cost is not the same as ROI. Cost is an input; ROI is the interpretation.

### Usage Signal

An observable signal that helps estimate whether a decision, resource, feature or agent is being used.

Examples:
- active users,
- request volume,
- token usage,
- runtime utilization,
- deployment frequency,
- feature adoption,
- project activity.

Usage Signal supports ROI confidence but does not prove business value by itself.

### AI Model

An AI model used by an application, workflow or agent.

Examples:
- OpenAI model,
- Anthropic Claude model,
- future provider model.

AI Model is relevant when model choice affects cost, quality, risk or policy.

### AI Usage

Observed consumption of an AI Model.

AI Usage may include:
- provider,
- model,
- requests,
- tokens,
- user/team/application,
- time period,
- cost.

AI Usage can become Evidence for model downgrade, unused agent or duplicated service recommendations.

### AI Agent

An automated or semi-automated AI workflow that performs tasks for users, teams or systems.

An AI Agent can have owner, model usage, cost, usage signal, policy risk and recommendation history.

AI Agent is not automatically allowed to execute changes. In the MVP, IMPERATOR recommends and the company decides.

### Integration

A connected external operating system that can provide operational signals.

MVP Integrations:
- Jira,
- GitHub,
- AWS,
- OpenAI + Anthropic Claude.

Integrations are managed capabilities. They are not the core product object.

### Connector

The mechanism that fetches, receives or syncs data from an Integration.

Connectors must remain replaceable. Business meaning must live in the domain, not inside provider-specific connector logic.

### Incident

An operational disruption, cost spike, audit request or risk event that may require reconstructing one or more Decision ROI Cases.

Incidents can be linked to Decisions, Evidence, Owners, Policies and Recommendations.

### Workflow

A repeatable business process around a Decision ROI Case.

Examples:
- review recommendation,
- approve recommendation,
- request more evidence,
- mark implementation complete,
- validate realized savings,
- record final result.

### Organization

The customer company using IMPERATOR.

It owns departments, projects, users, roles, integrations, policies, decisions and ledger history.

### Department

A business unit or functional area inside an Organization.

Departments help explain ownership, budget, approval path and value allocation.

### Team

A group of users inside a Department or Organization.

Teams can own Decisions, Resources, AI Agents, Projects, Recommendations or approval responsibilities.

### Project

A business or technical initiative that groups decisions, work, resources, cost and outcomes.

Projects commonly connect Jira context, GitHub repositories, AWS resources and AI usage.

### User

A human actor inside an Organization.

Users can create, review, approve, own or audit Decision ROI Cases depending on Role and policy.

### Role

A permission and responsibility label.

Initial Role concepts:
- Executive,
- CTO / VP Engineering,
- Platform Lead,
- Engineer,
- FinOps,
- Security,
- Compliance,
- Finance,
- Admin,
- Viewer.

## Primary Relationships

| Source | Relationship | Target |
|---|---|---|
| Organization | owns | Department |
| Organization | owns | Project |
| Organization | configures | Integration |
| Organization | defines | Policy |
| Department | owns | Project |
| Department | contains | User |
| Department | contains | Team |
| Team | contains | User |
| User | has | Role |
| User or Team | owns | Decision |
| User or Team | owns | Resource |
| User or Team | owns | AI Agent |
| Decision | belongs to | Project |
| Decision | produces | Timeline |
| Decision | may create or use | Resource |
| Decision | may use | AI Model |
| Decision | may generate | AI Usage |
| Timeline | contains | Evidence |
| Evidence | is derived from | Operational Event |
| Operational Event | comes from | Integration |
| Connector | syncs | Integration |
| Decision | may trigger | Recommendation |
| Recommendation | references | Evidence |
| Recommendation | estimates | ROI |
| Recommendation | requires | Approval |
| Approval | is made by | User |
| Approval | is governed by | Policy |
| Approval | creates | Ledger Entry |
| Ledger Entry | references | Evidence Snapshot |
| Ledger Entry | references | ROI Snapshot |
| Ledger Entry | references | Assumptions Snapshot |
| Result Validation | creates | Ledger Entry |
| Cost | is associated with | Resource |
| Cost | is associated with | AI Usage |
| Usage Signal | supports | ROI |
| AI Usage | uses | AI Model |
| AI Agent | uses | AI Model |
| Decision | records | Business Value |
| Incident | may involve | Decision |
| Workflow | advances | Decision ROI Case |
| Decision ROI Case | is recorded in | Decision Ledger |

## Domain Invariants

- A Recommendation cannot be approval-ready without Evidence.
- A Decision ROI Case cannot claim ROI without explicit assumptions.
- A Decision Ledger entry must preserve who approved, rejected or deferred the action.
- A Decision Ledger entry must be append-only.
- A Decision Ledger approval must reference evidence, ROI and assumptions snapshots.
- A rejected recommendation must preserve a rejection reason.
- A deferred recommendation must preserve required evidence or a review date.
- AI reasoning must consume prepared context, not raw external sources.
- A Connector cannot own business logic.
- A Timeline must preserve source lineage.
- A Decision can exist without a Recommendation; a Recommendation cannot exist without a Decision ROI Case.
- Cost is an input to ROI; it is not an ROI claim by itself.
- Usage Signal can increase or decrease confidence; it does not prove value alone.
- Realized Business Value must be separated from estimated Business Value.
- Realized savings must be recorded through Result Validation, not by mutating estimated savings.
- Monthly savings are used for decision queues; annualized value is used for executive summaries.

## Domain Boundaries

### In Domain

- decisions,
- recommendations,
- evidence,
- timelines,
- ownership,
- approvals,
- ROI,
- cost,
- usage signals,
- resources,
- AI models,
- AI usage,
- AI agents,
- policies,
- business value,
- ledger history,
- ledger snapshots,
- result validation,
- integration health as context.

### Out Of Domain For MVP

- autonomous infrastructure execution,
- generic observability,
- universal connector marketplace,
- full GRC workflow platform,
- raw log analytics,
- unrestricted AI agent access to source systems.

## Canonical Domain Questions

Every major product or architecture proposal should answer:

1. Which Decision ROI Case does this strengthen?
2. Which entity owns the business meaning?
3. Which evidence proves the claim?
4. Which user can approve or reject the recommendation?
5. Which ROI assumption is being used?
6. Which ledger entry preserves accountability?
7. Which result validation separates estimated value from realized value?
