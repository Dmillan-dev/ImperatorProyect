# 20 — MVP Decision ROI Platform Blueprint

## Purpose

Define the canonical MVP structure for IMPERATOR as a **Decision ROI Platform** and **Executive Operational Intelligence** product.

This document is the operating blueprint for Phase 0 validation and Phase 1 product design.

## Product Thesis

IMPERATOR finds hidden operational money inside shipped technology decisions and explains exactly which decision leadership should approve to recover it.

Commercial message:

> IMPERATOR automatically identifies technology decisions destroying economic value and recommends the highest-ROI actions to recover money.

## MVP Category

Primary product category:

**Decision ROI Platform**

Executive framing:

**Executive Operational Intelligence**

Canonical intelligence domain:

**Enterprise Context Intelligence**

## MVP Rule

**One Decision. One Timeline. One ROI.**

The MVP must not try to connect everything. It must prove one complete decision story across four information domains.

## MVP Integrations

The MVP uses four integration domains:

| Domain | MVP integration | Purpose |
|---|---|---|
| Business Context | Jira | Explains why the decision exists |
| Code & Deployment | GitHub | Explains who implemented it and what changed |
| Infrastructure & Cost | AWS | Explains cloud resources, usage and cost |
| AI Consumption | OpenAI + Anthropic Claude | Explains AI models, tokens, requests, cost, users and applications |

Do not expand beyond these four domains until the MVP proves repeatable decision-level ROI.

## Core Object

The central product object is the **Decision ROI Case**.

Each case should contain:

- Business Decision: Jira ticket, epic, owner, status and business goal
- Technical Change: GitHub pull request, commits, reviews, author and deployment reference
- Infrastructure: AWS resource, usage, cost, runtime and idle signals
- AI Consumption: provider, model, tokens, requests, user or team, application and cost
- Recommendation: action, annual saving, risk, confidence, owner and approver
- Result: approved, rejected, implemented, validated and realized saving

## Priority Recommendations

The MVP should focus on five recommendation families:

| Priority | Recommendation | Economic impact |
|---|---|---|
| 5/5 | Downgrade or change AI model | Very high |
| 5/5 | Remove unused AI agents | Very high |
| 4/5 | Detect underutilized AWS resources | High |
| 4/5 | Identify features with negative ROI | High |
| 4/5 | Consolidate duplicated services or agents | High |

These five recommendations support the strongest commercial message:

> IMPERATOR finds hidden money in daily operations and explains exactly which decision to take to recover it.

## MVP Recommendation Examples

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

### 4) Negative-ROI Features

Signals:
- Jira business goal
- GitHub implementation effort
- AWS and AI cost
- usage or value proxy

Recommendation:

Review, disable or redesign a shipped feature whose operating cost exceeds observable value.

### 5) Duplicate Services or Agents

Signals:
- similar prompts or embeddings
- similar repositories
- overlapping Jira projects
- duplicated AWS resources
- duplicated AI costs

Recommendation:

Merge duplicated agents or services when overlap is high and ownership is clear.

## Correlation Engine

The MVP correlation engine should produce one explainable chain:

Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude -> Recommendation -> Result

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

### Executive Workspace

Question:

**How is the company right now?**

Answers:
- how much money is at risk
- how much can be recovered
- which decision should be reviewed today
- what risk exists
- which teams generate the most value

The Executive Workspace is the Home. It should not contain detailed lifecycle evidence or final approval actions.

### Decisions

Question:

**Can we trust this recommendation enough to approve it?**

Answers:
- decision list
- evidence chain
- financial impact
- lifecycle evidence
- ownership
- status
- final approval action

### Decision Ledger

Question:

**What has the company decided over time?**

The ledger is the immutable history of all business decisions, not only recommendations.

Ledger flow:

Business Decision -> Technical Change -> Infrastructure -> AI -> Approval -> Financial Result

Decision Ledger v2 module contract:
- `docs/product/DECISION_LEDGER_V2.md`

MVP rule:
The ledger should first appear inside Decision Detail as the accountability history of one Decision ROI Case. A standalone Decision Ledger surface becomes valuable after enough approved, rejected, deferred and validated decisions exist to support audit and accumulated-value review.

### Business Value

Question:

**What economic value has IMPERATOR generated?**

Answers:
- recovered this year
- value by department
- value by category
- recovered time
- ROI of IMPERATOR

### Integrations

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

## Go Criteria

Proceed to Phase 1 product design if:

- at least one customer recognizes the four-domain reconstruction as painful
- a real decision can be correlated across the four MVP integrations
- one of the five recommendation families produces credible annual savings
- a technical owner trusts the evidence
- an economic buyer understands the ROI
- the buyer asks to review more decisions

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
