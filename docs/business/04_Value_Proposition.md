# 04 — Value Proposition

## Core Value Proposition

IMPERATOR turns cross-platform operational context into a **Decision ROI Timeline**: one business decision, one accountable timeline, one measurable ROI view.

The MVP does not compete on connecting everything. It proves value by normalizing four information domains:
- Business Context: Jira explains why the decision exists.
- Code & Deployment: GitHub explains who implemented it and what changed.
- Infrastructure & Cost: AWS explains which resources were created and what they cost.
- AI Consumption: OpenAI + Anthropic Claude explains model, token, request, user and application cost.

## Primary MVP Question

**This business decision costs X today, has Y observable usage or value signal, and has an action that can recover Z per year.**

## Paid MVP Wedge

The first paid motion is the **Decision Recovery Workflow** for AI/cloud spend.

It should prove one expensive shipped decision before presenting IMPERATOR as a broad platform.

## MVP Paid-Wedge Recommendations

| Priority | Recommendation | MVP role |
|---|---|---|
| 5/5 | Downgrade or change AI model | Primary paid wedge |
| 5/5 | Remove unused AI agents | Primary paid wedge |
| 4/5 | Detect underutilized AWS resources tied to the same decision | Supporting |

Deferred until the first wedge is repeatable:
- identify features with negative ROI,
- consolidate duplicated services or agents.

This paid-wedge focus supports the commercial message:

**IMPERATOR finds hidden AI/cloud waste inside shipped decisions and explains exactly which action leadership should approve to recover it.**

## Painkillers (Prioritized by Buying Urgency)

### 1) Decision ROI Timeline (Primary Entry Wedge)
Problem: "We approved work weeks ago, but we cannot see what it costs today, who owns it, whether it is used, or whether it is worth keeping."

Outcomes:
- link Jira intent, GitHub implementation, AWS cost and AI consumption in one timeline
- expose current monthly cost, owner, usage signal and recommended action
- create CFO-ready and CTO-ready ROI evidence for one decision
- reduce investigation effort because the traceability path is already assembled

**Why first:** It turns traceability into an executive outcome. Platform teams still get the technical timeline, while Finance/FinOps and CTO leadership get a quantified business decision.

### 2) Cross-platform Decision Traceability
Problem: "When an incident, audit request or cost spike happens, we spend hours reconstructing what happened across multiple systems."

Outcomes:
- answer who changed what, when, why and with whose approval
- preserve audit-ready evidence across Jira, GitHub, AWS and AI platforms
- shorten incident or cost-spike investigation time
- reduce repeated manual reconstruction

**Why second:** Traceability is the foundation, but the MVP should not stop at "what happened." It should convert that trace into cost, value and recommendation.

### 3) AI and Cloud Cost Optimization
Problem: "We see the bills, but cannot attribute spend to decisions, features, teams or AI usage patterns."

Outcomes:
- cost visibility by decision, project, owner and AI model
- detection of low-usage or inefficient AI features
- optimization recommendations such as model downgrade, quota changes or retirement
- annualized recovery estimate for each action

**Why third:** Optimization becomes credible once the decision timeline shows the business reason, implementation path, infrastructure footprint and AI consumption.

## Recommended MVP Object

The canonical MVP object is the **Decision ROI Case**:

Business Decision -> Technical Change -> Infrastructure -> AI Consumption -> ROI View -> Recommendation -> Approval/Rejection/Deferral -> Decision Ledger -> Result Validation.

Each case must include current cost, usage/value proxy, owner, approver, confidence and annualized recovery.

For MVP, this object should be reviewed inside one **Decision Review Workspace**, not spread across a full platform navigation model.

## Canonical ROI Assumptions (Use Consistently)

| Assumption | Canonical value |
|---|---:|
| Fully loaded engineering cost | €70/hour |
| Baseline investigation effort | 6 people × 2 hours = 12 hours |
| Baseline investigation cost | €840 per investigation |
| Pilot target assessment effort | 1 person × 30 minutes = 0.5 hours |
| Pilot target assessment cost | €35 per investigation |
| Savings per reconstructed decision | €805 |
| Example volume | 10 investigations/month |
| Monthly labor recovery | €8,050/month |
| Annual labor recovery | €96,600/year |

Use these numbers as validation assumptions, not guaranteed customer outcomes.

## Recommended North-Star KPI

**Validated Recovered Operational Value**: realized money saved, time recovered or risk avoided after a recommendation is approved and later validated.

Estimated recovery may be shown during review, but it must remain separate from validated value.
