# 08 — Go-To-Market Hypotheses

## Land Strategy (Land and Expand Model)

### Primary Entry Point
**Decision Recovery Workflow for one high-cost AI/cloud decision.**

The launch wedge should answer:

**"This decision is costing X today, appears to create Y estimated value, and can recover Z per year through this action."**

### Why This Wedge

- CTO / VP Engineering sees which approved work is now creating cost, risk or operational drag.
- Platform Engineering gets the technical evidence path without manual reconstruction.
- CFO / FinOps sees decision-level cost and recovery potential.
- Security and Compliance can reuse the same timeline as evidence.
- The story is narrower than "connect everything" and more valuable than "show another dashboard."

### MVP System Boundary

The v1 story uses four systems because each answers a different information question:

| Domain | Initial system | Question answered |
|---|---|---|
| Business Context | Jira | Why was this decision made? |
| Code & Deployment | GitHub | Who implemented it and what changed? |
| Infrastructure & Cost | AWS | What resources does it consume and what does it cost? |
| AI Consumption | OpenAI + Anthropic Claude | Which models, tokens, users and applications drive AI spend? |

Expansion systems such as Slack, Microsoft 365, Salesforce, Azure OpenAI, Google Gemini, Mistral, Azure DevOps, ServiceNow, Azure and GCP should remain future roadmap items until the four-domain story is validated.

### Entry Point Messaging (Launch)

**Not:** "We connect all your tools."
**This:** "In 30 days, we will reconstruct one expensive business decision across Jira, GitHub, AWS and AI consumption, then show what it costs today and what you can recover."

### Initial 30-Day Value Promise

In 30 days, measure:
- one real decision timeline from Jira intent to GitHub change to AWS cost to OpenAI + Anthropic Claude consumption
- current monthly cost of that decision
- usage or value signal available from existing customer data
- recommended action and annualized recovery estimate
- manual reconstruction effort avoided using the canonical ROI assumptions in `docs/business/04_Value_Proposition.md`

### Priority Recommendation Wedge

The first commercial motion should test a narrow AI/cloud spend recovery wedge:

1. Downgrade or change AI model.
2. Remove unused AI agents.
3. Detect underutilized AWS resources tied to the same decision.

Deferred until repeatable ROI is proven:
- identify features with negative ROI,
- consolidate duplicated services or agents.

## Expand Strategy (Months 2–12)

### Phase 1 (Month 1–2): CTO + Platform + FinOps
Entry wedge: Decision ROI Timeline for one high-cost feature, AI workflow or infrastructure change.

### Phase 2 (Month 2–4): Finance + FinOps
Expand into decision-level cost attribution, AI model optimization and recurring recovery reviews.

### Phase 3 (Month 4–6): Security + Compliance
Reuse decision timelines and ledger history for audit evidence and risk review.

### Phase 4 (Month 6+): Executive Operating System
Expose recovered operational value, top negative-ROI decisions and approved optimization actions at executive level.

**Land and Expand Logic:** Each phase uses the same context engine and decision timeline, but different stakeholders extract different value.

## Commercial Thesis

IMPERATOR is purchased for operational outcomes:
- recovered operational value,
- clearer decision accountability,
- lower AI and cloud waste,
- faster investigation when incidents, audits or cost spikes occur,
- better executive control over operational ROI.

## Recommended Sales Message

"We help leadership see the live ROI of decisions after they ship. IMPERATOR links Jira, GitHub, AWS and OpenAI + Anthropic Claude so you know why a decision exists, who implemented it, what it costs today, whether it is being used, and what action can recover money."

## Expansion Gate

Do not expand the product surface, connector roadmap or recommendation families until:
- 3 real customer decisions have been reconstructed,
- 2 recommendations are judged approval-ready,
- 1 recommendation is approved by CTO or VP Engineering after FinOps review,
- 1 measurable monthly saving or avoided cost is identified.
