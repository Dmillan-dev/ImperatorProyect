# 17 — Product Surface and Context Engine Thesis

## Purpose

Define what customers use, where business value is perceived, and where the technical moat is created.

## Phase-0 Scope Note

This document defines product thesis and conceptual scope only.
It does not define implementation architecture, production connector design, or stack decisions.

## What Customers Use

Not all buyers interact with IMPERATOR in the same way.

## 1) Context API (Integration Entry Point)

Primary user: developers and platform teams.

Example interaction:

POST /decision

IMPERATOR registers an operational decision with business context metadata.

## 2) Dashboard Web (Primary Commercial Surface)

Primary users: CTO, CIO, CISO, CFO, Platform leaders.

Main visibility domains:
- costs
- risks
- decisions
- KPIs
- investigations
- AI usage and governance
- audit context

Commercial thesis: the dashboard proves business value quickly and improves buyer clarity.

## 3) SDKs (Adoption Accelerators)

Goal: reduce integration friction and time-to-value.

Initial language candidates:
- Java
- Python
- Go
- JavaScript

## 4) Connectors (Scalable Value Distribution)

Goal: ingest cross-system signals without custom one-off integration work by each customer.

Priority connector families:
- cloud: AWS, Azure
- engineering: GitHub, Jira
- collaboration: Slack, Microsoft 365
- AI platforms: OpenAI, Anthropic
- business systems: Salesforce

## Where the Deep Technical Moat Lives

The strategic moat is not the API alone and not the dashboard alone.
The hardest-to-replicate capability is the internal context engine.

Conceptual processing cycle:

Event -> Normalization -> Enrichment -> Correlation -> Context -> Evaluation -> Ledger -> Analysis -> Recommendation

Why this matters:
- converts isolated technical events into accountable business decisions
- creates reusable organizational decision history over time
- improves traceability, risk detection, and optimization quality as data depth grows

## Strategic Framing for Market Positioning

Recommended framing:
IMPERATOR is a SaaS platform with an open API, not only an API with a UI.

Reasoning:
- executive buyers need visible business outcomes (dashboard)
- technical teams need embedability and automation (API, SDKs, connectors)
- dual entry model improves adoption and expansion potential

## Alignment Check (Current Canonical Strategy)

- Preserves canonical intelligence domain: Enterprise Context Intelligence
- Preserves wedge: Cross-platform Decision Traceability
- Preserves principle: augment existing systems, do not replace them
- Supports land-and-expand by serving multiple stakeholders on one platform
