# IMPERATOR Demonstration Control

Status: **ACTIVE DEMO INDEX / COMMERCIAL EXECUTION NOT AUTHORIZED**

## Rule Zero

### Why This Folder Exists

It separates demonstrable product facts from commercial claims, future pilot
steps and historical visual concepts.

### Who Uses It

- Product and CTO Guardians preparing a buyer conversation;
- the Demo Operator executing a certified proof;
- Design Partner interviewers recording value signals; and
- Architecture, Security and Context Guardians checking claims.

### What It Contains

- executable demonstration instructions;
- future E2E readiness checks;
- time-bounded buyer narratives;
- evidence-backed claim boundaries; and
- value-validation scorecards.

### What It Never Contains

- customer data, credentials, tokens or secrets;
- authorization to connect a customer;
- invented savings, outcomes, testimonials or test results;
- production runtime configuration;
- a replacement for D087-D093; or
- authorization to open Sprint 4.4 or Sprint 4.5.

## Demonstration Levels

| Level | What it proves | Current state |
|---|---|---|
| L1 - Local product proof | Deterministic Evidence-to-Business-Value behavior inside Application boundaries | Available and certified |
| L2 - Runtime certification rehearsal | Same canonical case through REST, local real-token JWT/RBAC conformance, PostgreSQL, Docker and the workspace | Certified under D092-D095; operational external identity remains a pre-Sprint-4.5 gate |
| L3 - Design Partner pilot | Value and trust with approved customer facts | Not authorized before Pilot Readiness |

Never describe L1 or L2 as L3. Passing technical tests proves behavior; only a
customer validation can prove willingness to act or pay.

The first prospect meeting is discovery-first. Use
`../business/21_Design_Partner_Discovery_Playbook.md`; show L1 only after the
buyer identifies a concrete relevant Decision.

## Artifact Map

| Document | Role |
|---|---|
| `45_End_To_End_Business_Value_Demo.md` | Certified local Application harness and exact canonical outcome |
| `46_DRC_AOA_001_Pilot_E2E_Readiness_Checklist.md` | Future runtime E2E preconditions, scenarios and assertions |
| `47_15_Minute_Design_Partner_Demo_and_Value_Assessment.md` | Current-state evaluation, 15-minute script and buyer-value scorecard |

The historical static prototype under `../../demos/executive_dashboard_demo/`
is not an executable or contract-conformant MVP demonstration. Its README
records the restrictions.
