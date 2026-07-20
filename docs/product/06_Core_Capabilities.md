# 06 — Core Capabilities

## 1) Context API
Future entry point to register/validate operational decisions and events with business metadata.

MVP note:
Do not build a public Context API or SDKs before the Decision Recovery Workflow is validated.

## 2) Context Ledger
Persistent, immutable record of business-relevant operational decisions.

Decision Ledger v2 defines the current module contract:
- approval, rejection and deferral records
- evidence, ROI and assumptions snapshots
- implementation markers
- result validation
- separation of estimated and realized savings

Canonical reference:
- `docs/product/DECISION_LEDGER_V2.md`

## 3) Enterprise Context Enrichment
Automatic enrichment with:
- actor (person/agent)
- ownership
- team/department
- cost signal
- usage/value signal
- risk signal
- applied policies
- involved systems/models

## 4) Enterprise Context Intelligence
Analytical layer to detect:
- inefficiencies
- operational risk patterns
- policy failures
- optimization opportunities
- negative-ROI or low-value decisions

MVP first paid-wedge action:
- downgrade or change AI model

Expansion paid-wedge families:
- remove unused AI agents
- detect underutilized AWS resources tied to the same Decision ROI Case
- identify features with negative ROI
- consolidate duplicated services or agents

## 5) Customer Interaction Surfaces (Conceptual)
Customer value is delivered through:
- Decision Review Workspace (MVP)
- Executive Workspace (company-status Home for CEO/CIO/CTO)
- Decisions (evidence and approval workspace)
- Decision Ledger (immutable decision history)
- Business Value (economic proof page)
- Integrations (operating connectivity map)
- Context API (open integration entry point)
- SDKs (Java, Python, Go, JavaScript)
- Information-domain integrations (Business Context, Code & Deployment, Infrastructure & Cost, AI Consumption)

MVP note:
The first customer-facing surface should be the Decision Review Workspace. Other surfaces support expansion after multiple Decision ROI Cases exist.

## 6) Internal Context Engine (Conceptual Cycle)
Operational events follow a common intelligence pipeline:

Event -> Normalization -> Enrichment -> Correlation -> Decision ROI Case -> ROI -> Recommendation -> Human Review -> Decision Ledger -> Result Validation

MVP narrative object:

Decision ROI Case:

Business Decision -> Technical Change -> Infrastructure -> AI Consumption -> ROI -> Recommendation -> Approval/Rejection/Deferral -> Decision Ledger -> Result Validation

MVP correlation path:

Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude -> ROI View -> Recommendation -> Decision Ledger -> Result Validation

## Product Principle

IMPERATOR augments existing enterprise systems; it does not replace them.
