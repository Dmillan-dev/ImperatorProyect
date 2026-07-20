# 20 — MVP Decision ROI Platform Blueprint

## Purpose

Define the canonical MVP structure for IMPERATOR as a **Decision ROI Platform** and **Executive Operational Intelligence** product inside the broader commercial positioning of an **Operating System for Operational Intelligence**.

This document is the operating blueprint for Phase 0 validation and Phase 1 product design.

## Product Thesis

IMPERATOR finds hidden operational money inside shipped technology decisions and explains exactly which decision leadership should approve to recover it.

Commercial message:

> IMPERATOR reconstructs expensive shipped technology decisions and recommends approval-ready actions to recover money.

## MVP Category

External commercial category:

**Enterprise Decision Intelligence Platform**

Primary MVP product model:

**Decision ROI Platform**

Executive framing:

**Executive Operational Intelligence**

Canonical intelligence domain:

**Enterprise Context Intelligence**

Category analogy:

| System category | Manages |
|---|---|
| ERP | resources |
| CRM | customers |
| SIEM | security |
| Observability | systems |
| IMPERATOR | decisions |

## MVP Rule

**One Decision. One Timeline. One ROI.**

The MVP must not try to connect everything. It must prove one complete decision story across four information domains.

## MVP Paid Wedge

The first paid product motion is the **Decision Recovery Workflow**.

Promise:

**In 30 days, reconstruct one expensive AI/cloud decision and produce one approval-ready recovery action.**

The workflow must prove:
- why the decision exists,
- who implemented it,
- what it costs now,
- what usage or value signal exists,
- what action can recover money,
- who can approve, reject or defer the action,
- what value was later validated.

Minimum demonstrable MVP flow:

Event -> Connector -> Decision Engine -> ROI Engine -> Recommendation -> Decision Ledger -> Decision Review Workspace -> Result Validation

In this MVP framing, `Decision Engine` is a product/narrative block for correlation, context and recommendation reasoning. It does not require a separate microservice before the end-to-end value loop is proven.

The Decision Ledger records reviewed recommendations, approvals, rejections, deferrals and result validation. It preserves ROI, evidence and assumptions snapshots; it does not calculate ROI.

## MVP Integrations

The MVP uses four integration domains:

| Domain | MVP integration | Purpose |
|---|---|---|
| Business Context | Jira | Explains why the decision exists |
| Code & Deployment | GitHub | Explains who implemented it and what changed |
| Infrastructure & Cost | AWS | Explains cloud resources, usage and cost |
| AI Consumption | OpenAI + Anthropic Claude | Explains AI models, tokens, requests, cost, users and applications |

Do not expand beyond these four domains until the MVP proves repeatable decision-level ROI.

Phase 3 MVP implementation may start with one or two narrow read-only connectors plus approved manual/static or imported evidence for the remaining domains. The four-domain table defines the evidence story; it does not require full connector automation in the first build.

## Core Object

The central product object is the **Decision ROI Case**.

Each case should contain:

- Business Decision: Jira ticket, epic, owner, status and business goal
- Technical Change: GitHub pull request, commits, reviews, author and deployment reference
- Infrastructure: AWS resource, usage, cost, runtime and idle signals
- AI Consumption: provider, model, tokens, requests, user or team, application and cost
- ROI View: current cost, estimated recovery, assumptions, confidence and risk
- Recommendation: action, annual saving, risk, confidence, owner and approver
- Ledger State: recommendation created, approved, rejected, deferred, implemented or validated
- Result: realized saving only after result validation

## Recommendation Focus

The MVP should not build five engines at once.

MVP implementation should implement exactly one recommendation from the paid wedge. The default is AI model downgrade or model change unless a later decision changes it.

Primary paid wedge:

| Priority | Recommendation | MVP role |
|---|---|---|
| 5/5 | Downgrade or change AI model | Primary |

Expansion candidates after the first value loop works:

| Recommendation | Reason to defer |
|---|---|
| Remove unused AI agents | Similar evidence family, but not needed for the first deterministic recommendation |
| Detect underutilized AWS resources tied to the same decision | Useful supporting family after the model-change case is proven |
| Identify features with negative ROI | Requires stronger usage/value signals and business-value attribution |
| Consolidate duplicated services or agents | Requires similarity analysis and broader graph depth |

This paid-wedge focus supports the strongest commercial message:

> IMPERATOR finds hidden AI/cloud waste inside shipped decisions and explains exactly which action leadership should approve to recover it.

## Recommendation Examples and Deferred Families

Example 1 is the MVP recommendation family. Examples 2-5 are documented only as post-validation expansion context.

### 1) AI Model Downgrade

Signals:
- OpenAI or Anthropic Claude model cost
- AWS Lambda requests
- Jira project context
- GitHub agent code
- usage trend

Recommendation:

Downgrade GPT-4.1 or Claude high-cost model to a lower-cost model when usage, task complexity and quality evidence support the change.

Outcome:
- annual saving
- low risk
- confidence score
- approval owner

### 2) Remove Unused AI Agents

Signals:
- OpenAI + Anthropic Claude requests
- AWS cost
- GitHub last commit
- Jira project closed or inactive

Recommendation:

Delete or archive an AI agent with no meaningful usage but ongoing cost.

### 3) Underutilized AWS Resources

Signals:
- AWS CloudWatch utilization
- Cost Explorer spend
- resource age
- linked Jira/GitHub context

Recommendation:

Archive, downsize or stop resources with low usage and clear ownership.

### 4) Negative-ROI Features — Deferred

Signals:
- Jira business goal
- GitHub implementation effort
- AWS and AI cost
- usage or value proxy

Recommendation:

Review, disable or redesign a shipped feature whose operating cost exceeds observable value.

Phase:

Post-MVP, after value-signal reliability is validated.

### 5) Duplicate Services or Agents — Deferred

Signals:
- similar prompts or embeddings
- similar repositories
- overlapping Jira projects
- duplicated AWS resources
- duplicated AI costs

Recommendation:

Merge duplicated agents or services when overlap is high and ownership is clear.

Phase:

Post-MVP, after enough decision, service and agent history exists to make similarity analysis trustworthy.

## Correlation Engine

The MVP correlation engine should produce one explainable chain:

Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude -> ROI View -> Recommendation -> Decision Ledger -> Decision Review Workspace -> Result Validation

Correlation signals:
- ticket IDs in branches, commits and pull requests
- deployment names and tags
- owner/team metadata
- repository and service names
- AWS resource tags
- AI application/user/team metadata
- timestamps and lifecycle sequence
- usage decay and cost trend

## Product Surfaces

### MVP Surface: Decision Review Workspace

Question:

**Can we trust and approve this recovery action?**

Answers:
- decision summary,
- timeline,
- evidence,
- current cost,
- usage or value signal,
- ROI assumptions,
- recommendation,
- approval, rejection or deferral controls,
- ledger history for this Decision ROI Case.

For MVP, this single surface is enough. It can be presented as a Decision Detail or pilot workspace, but it should not require a full navigation suite.

### Deferred Surface: Executive Workspace

Question:

**How is the company right now?**

This becomes valuable after multiple Decision ROI Cases exist.

### Deferred Surface: Decision Ledger

Question:

**What has the company decided over time?**

The ledger is the immutable history of all business decisions, not only recommendations.

Ledger flow:

Business Decision -> Technical Change -> Infrastructure -> AI -> ROI -> Recommendation -> Approval/Rejection/Deferral -> Decision Ledger -> Implementation -> Result Validation -> Business Value

Decision Ledger v2 module contract:
- `docs/product/DECISION_LEDGER_V2.md`

MVP rule:
The ledger should first appear inside Decision Detail as the accountability history of one Decision ROI Case. A standalone Decision Ledger surface becomes valuable after enough approved, rejected, deferred and validated decisions exist to support audit and accumulated-value review.

### Deferred Surface: Business Value

Question:

**What economic value has IMPERATOR generated?**

Answers:
- recovered this year
- value by department
- value by category
- recovered time
- ROI of IMPERATOR

Business Value should count realized value only from validated ledger outcomes.

### Supporting Surface: Integrations

Question:

**Which operating systems feed the intelligence layer?**

MVP integrations:
- AWS
- GitHub
- Jira
- OpenAI + Anthropic Claude

## Validation Plan

The MVP should be validated through concierge reconstruction before production implementation.

Validation sequence:

1. Select one high-cost AI or cloud decision.
2. Reconstruct the Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude chain manually.
3. Identify current monthly cost.
4. Capture usage or value proxy.
5. Generate one recommendation.
6. Estimate annual recovery.
7. Ask CTO/Platform/FinOps if the recommendation is credible enough to approve.
8. Record approval, rejection or deferral in the ledger.
9. Validate realized value if the company implements the recommendation.

## Go Criteria

Proceed to Phase 1 product design if:

- at least 3 real customer decisions can be reconstructed
- at least 1 recommendation is judged approval-ready by technical owners
- at least 1 recommendation is approved by CTO or VP Engineering after FinOps review
- at least 1 measurable monthly saving or avoided cost is identified
- a technical owner trusts the evidence
- an economic buyer understands the ROI
- the buyer asks to review more decisions

Do not expand surfaces, connectors or recommendation families before these conditions are met.

## Implementation Standard

If Phase 1 is authorized, the MVP should use the reduced implementation standard in:

- `docs/architecture/31_MVP_Implementation_Standard.md`

The exact Phase 1 scope, data-model limit, connector limit, AI explanation boundary, scaffolding authorization and exit criteria are defined in:

- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`

Product interpretation:

- keep the full platform vision documented,
- build the smallest end-to-end value loop,
- use Hexagonal Architecture so providers and persistence remain adapters,
- keep JWT/OAuth2 and observability minimal,
- do not introduce deferred infrastructure to prove the first Decision ROI Case.

## Non-Goals

Do not position the MVP as:

- a generic dashboard
- a cloud cost tool only
- an observability product
- a SIEM
- a GRC workflow platform
- a universal connector platform
- an autonomous executor of changes

IMPERATOR recommends. The company decides.
