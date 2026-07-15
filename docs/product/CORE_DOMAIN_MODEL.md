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
| User | has | Role |
| User or Team | owns | Decision |
| Decision | belongs to | Project |
| Decision | produces | Timeline |
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
| Decision | records | Business Value |
| Incident | may involve | Decision |
| Workflow | advances | Decision ROI Case |
| Decision ROI Case | is recorded in | Decision Ledger |

## Domain Invariants

- A Recommendation cannot be approval-ready without Evidence.
- A Decision ROI Case cannot claim ROI without explicit assumptions.
- A Decision Ledger entry must preserve who approved, rejected or deferred the action.
- AI reasoning must consume prepared context, not raw external sources.
- A Connector cannot own business logic.
- A Timeline must preserve source lineage.
- A Decision can exist without a Recommendation; a Recommendation cannot exist without a Decision ROI Case.
- Realized Business Value must be separated from estimated Business Value.
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
- policies,
- business value,
- ledger history,
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

