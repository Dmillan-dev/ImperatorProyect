# 16 — Sales Narrative and Commercial Case

## Purpose

This document is the definitive commercial narrative for salespeople, customers, investors and internal alignment.

It explains:
- why companies need IMPERATOR,
- what problem the MVP solves,
- who benefits and how,
- how value is measured.

## Core Narrative

**One Decision. One Timeline. One ROI.**

Companies approve business decisions in one system, implement them in another, pay for them through cloud and AI providers, and then lose the economic thread after launch.

IMPERATOR rebuilds that thread.

The MVP links:

Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude -> Decision Review Workspace -> Decision Ledger history -> Result Validation.

The product should always answer:

**"This decision costs X today, appears to create Y estimated value, and has an action that can recover Z per year."**

## The Problem: A Real Decision After Launch

Forty-two days ago, Product approved a Jira ticket:

**"Create an AI assistant for customer onboarding."**

Engineering implemented it through GitHub. The feature was deployed on AWS Lambda. It uses a high-cost model through OpenAI + Anthropic Claude.

Today, Finance asks:

> Is this assistant worth what it costs?

Without IMPERATOR, the team must manually reconstruct:
- the Jira ticket, epic, owner, priority and status,
- the GitHub pull requests, commits, reviews and deploys,
- the AWS services and monthly cost,
- the OpenAI + Anthropic Claude models, tokens, users and applications,
- the usage or value signal that proves whether the feature is working.

The data exists, but the business answer does not.

## What IMPERATOR Shows

For that single decision, IMPERATOR should produce a Decision ROI Timeline:

> "Aprobaste esta funcionalidad hace 42 días.
>
> Actualmente cuesta €2.340/mes.
>
> Solo la usan 17 personas.
>
> Coste alto frente al uso observable.
>
> Recomendación: cambiar a un modelo de menor coste con fallback.
>
> Ahorro estimado: €1.620/mes."

This is the commercial product moment. The customer does not buy another dashboard; they buy a business answer that connects approval, implementation, cost, usage and action.

## MVP Information Domains

| Domain | Initial system | What it answers |
|---|---|---|
| Business Context | Jira | Why was this decision made? |
| Code & Deployment | GitHub | Who implemented it and what changed? |
| Infrastructure & Cost | AWS | What resources does it consume and what does it cost? |
| AI Consumption | OpenAI + Anthropic Claude | Which models, tokens, users and applications drive AI spend? |

Slack, Microsoft 365, Salesforce, Azure OpenAI, Google Gemini, Mistral, Azure DevOps, ServiceNow, Azure and GCP are expansion candidates, not MVP dependencies.

## MVP Paid-Wedge Recommendation

The first commercial version should focus on a narrow AI/cloud spend recovery wedge:

| Priority | Recommendation | MVP role |
|---|---|---|
| 5/5 | Downgrade or change AI model | Primary |

Expansion candidates after the first wedge is repeatable:
- remove unused AI agents,
- detect underutilized AWS resources tied to the same decision,
- identify features with negative ROI,
- consolidate duplicated services or agents.

This creates the core sales message:

> IMPERATOR finds hidden money in daily operations and explains exactly which decision to approve to recover it.

## Why This Matters

### For CTO / VP Engineering

**Problem:** Shipped decisions create hidden operational cost, but ownership and value are hard to inspect later.

**With IMPERATOR:**
- see the complete technical and business timeline,
- identify owners and implementation evidence,
- approve optimization actions with context,
- defend operational decisions with data.

### For Platform Engineering

**Problem:** Reconstructing what changed across tools is slow and repetitive.

**With IMPERATOR:**
- Jira, GitHub, AWS and AI consumption are linked into one evidence path,
- incident, audit and cost-spike investigations start from assembled context,
- Platform becomes the trusted source for operational truth.

### For CFO / FinOps

**Problem:** Cloud and AI spend grows faster than attribution and ROI visibility.

**With IMPERATOR:**
- cost is tied to a decision, owner, project and usage signal,
- recommendations include annualized recovery estimates,
- finance can review decisions instead of only reviewing bills.

### For Security / Compliance

**Problem:** Audit evidence is spread across work tracking, code, deployment and operational systems.

**With IMPERATOR:**
- each decision carries approval, implementation and ownership evidence,
- policy/risk review can reuse the same timeline,
- compliance work becomes easier to validate.

## Canonical ROI Assumptions

Use these as pilot assumptions, not guaranteed outcomes:

| Assumption | Value |
|---|---:|
| Fully loaded engineering cost | €70/hour |
| Baseline manual reconstruction | 6 people × 2 hours = 12 hours |
| Baseline cost per reconstruction | €840 |
| Target assessment effort in pilot | 1 person × 30 minutes = 0.5 hours |
| Target cost per reconstruction | €35 |
| Savings per reconstructed decision | €805 |
| Example volume | 10 reconstructions/month |
| Monthly labor recovery | €8,050/month |
| Annual labor recovery | €96,600/year |

Separate from labor recovery, each recommendation should include a decision-specific cost recovery estimate, such as €1.620/month from model optimization.

## ROI Summary by Use Case

| Use Case | Value type | Pilot evidence |
|---|---|---|
| Decision ROI Timeline | current cost + recovery action | one Jira -> GitHub -> AWS -> AI chain |
| Manual reconstruction reduction | labor recovery | baseline effort vs pilot effort |
| AI model optimization | monthly savings | current model cost vs recommended model cost |
| Cloud resource optimization | monthly savings | AWS cost and utilization signal |
| Audit evidence reuse | time saved | decision evidence exported for review |

## Anticipated Objections

### CTO: "Is this just another dashboard?"

No. The MVP is not a generic dashboard. It is a decision reconstruction layer that answers one business question with evidence from Jira, GitHub, AWS and OpenAI + Anthropic Claude.

### CFO: "Where is the ROI?"

The pilot ROI is measured in two parts:
- labor recovery from reducing manual reconstruction,
- cost recovery from decision-specific recommendations.

The customer must validate both with their own data during the pilot.

### Engineering Manager: "Will this slow down my team?"

The Phase 0 / pilot framing should use selected decisions, approved exports or read-only access patterns. Do not claim automatic capture until implementation and deployment model are validated.

### CTO: "Will this integrate with our stack?"

The MVP starts with Jira, GitHub, AWS and OpenAI + Anthropic Claude. Other systems are roadmap expansion, not a promise for the first pilot.

## 30-Day Pilot

1. Select one high-cost or high-visibility decision.
2. Reconstruct the Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude timeline.
3. Measure current monthly cost.
4. Capture an available usage or value signal.
5. Produce one recommendation with annualized recovery from the AI/cloud spend recovery wedge.
6. Compare manual reconstruction effort with pilot reconstruction effort.
7. Record approval, rejection or deferral.
8. Validate realized value if the customer implements the recommendation.

## Success Looks Like

**Month 1:**
"We can finally see why this AI feature exists, who shipped it, what it costs, who uses it and what action saves money."

**Month 3:**
"We are reviewing decision-level ROI instead of only reviewing cloud and AI bills."

**Month 6:**
"Decision timelines are now reused by Platform, Finance, Security and leadership."

**Year 1:**
"IMPERATOR became the operational context layer for shipped decisions and their ongoing ROI."
