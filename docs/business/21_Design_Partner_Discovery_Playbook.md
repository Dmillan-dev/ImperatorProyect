# 21 - Design Partner Discovery Playbook

Status: **ACTIVE COMMERCIAL PREPARATION / DISCOVERY ALLOWED / PILOT NOT AUTHORIZED**

## 1. Objective

This playbook answers one question:

> Does the problem IMPERATOR targets exist inside a real company, and would
> that company commit to testing a bounded solution?

The immediate goal is one qualified Design Partner, not an immediate software
sale and not a large contact count. Sprint 4.3 is certified, so discovery may
continue; customer connection, data ingestion and pilot execution remain
forbidden until their later gates are authorized and certified.

## 2. What Is Being Validated

The commercial hypothesis is:

> AWS-first engineering organizations struggle to reconstruct why an
> operational Decision was made, connect it to code and cloud cost, identify
> accountable approvers and prove the economic result after action.

A conversation is useful only when it tests at least one real, recent Decision
with economic or governance impact. General enthusiasm for AI, dashboards or
cost optimization is not problem validation.

## 3. Target Account Prioritization

### Tier A - First Choice

AWS-first B2B SaaS companies with approximately 100-500 employees that:

- use GitHub for a production codebase;
- operate at least one costly AI-enabled workflow or cloud capability;
- have Platform/DevOps and Finance/FinOps ownership;
- face SOC 2, ISO 27001 or comparable audit pressure; and
- can isolate one repository, one AWS account/Region and one Decision.

This tier has the closest fit with D089, D090 and the current one-case MVP.

### Tier B - Strong Regulated Candidate

FinTech or regulated SaaS organizations in the same size range, provided that:

- a security owner will participate early;
- the pilot can use normalized, aggregated Evidence;
- payment, account and customer financial data remain outside IMPERATOR; and
- one bounded engineering-cost Decision can be isolated.

Compliance pressure can strengthen the Ledger proposition, but procurement and
security review may lengthen the path.

### Tier C - Conditional Candidate

HealthTech organizations qualify only when the complete Decision can be tested
without PHI, clinical records or patient identifiers. The first pilot should
not use health data merely to enter an attractive vertical.

### Sourcing Signals

Prioritize public or referral-based signals such as:

- a visible Platform Engineering, Cloud Infrastructure or FinOps function;
- AWS and GitHub engineering job descriptions;
- public discussion of AI features, agents or model usage;
- recent SOC 2/ISO 27001 work;
- engineering leadership discussing cloud cost, AI cost or audit burden; and
- a warm introduction to CTO, VP Engineering or Platform leadership.

Do not scrape private data, infer confidential spend or store personal contact
details in Git.

## 4. Anti-ICP And Early Disqualification

Do not progress an account when the first validation requires:

- multi-tenancy or public SaaS availability;
- Azure/GCP instead of the bounded AWS-first path;
- connectors other than the currently certified GitHub and AWS integrations;
- direct ingestion of raw prompts, conversations, source code or Restricted
  customer data;
- autonomous execution or AI approval;
- broad portfolio analytics rather than one Decision;
- custom SSO/login implementation before discovery;
- guaranteed savings; or
- a production SLA before Pilot Readiness.

An account may remain a future opportunity without being a valid first Design
Partner.

## 5. Buying And Validation Group

| Role | Discovery contribution | Required before Design Partner status |
|---|---|---:|
| CTO or VP Engineering | Confirms strategic pain and sponsors one case | Yes |
| Platform Engineering owner | Reconstructs technical timeline and verifies feasibility | Yes |
| Finance or FinOps | Validates cost baseline, assumptions and outcome source | Yes |
| Security/Privacy | Confirms read-only access and data boundary | Before pilot preparation |
| Business owner | Confirms why the capability exists and its value boundary | Before pilot preparation |

A single enthusiastic engineer is a useful champion, not a qualified Design
Partner by themselves.

## 6. Positioning

### Thirty-Second Description

> We are building a platform that reconstructs operational Decisions from
> Evidence in systems such as GitHub and AWS, calculates their economic impact
> and preserves an auditable trace of who decided what, why and what result the
> action produced.

### Discovery Invitation

> I am looking for two or three companies to validate the product against one
> real operational case. I am not asking you to buy a platform today. I want to
> learn whether this problem exists in your environment and whether solving it
> would have measurable economic value.

### What Not To Lead With

- AI platform;
- architecture, DDD or hexagonal design;
- Java, React, Docker, Flyway, JWT or Keycloak;
- number of tests, documents, connectors or endpoints; or
- future platform breadth.

Technology is supporting evidence only when a buyer asks how trust, security
or implementation works.

## 7. Outreach Templates

### Warm Introduction Request

```text
I am validating IMPERATOR, a product for reconstructing one operational
decision across engineering and cloud evidence, showing its economic impact
and preserving the approval/result trace.

Could you introduce me to a CTO, VP Engineering or Platform leader at an
AWS-first SaaS company who has dealt with cloud/AI cost attribution or manual
audit reconstruction? I am looking for a 30-minute problem interview, not a
sales presentation.
```

### Direct Message

```text
I am researching how engineering leaders reconstruct why a costly operational
decision was made, who approved it and what economic result it produced.

IMPERATOR is a working one-case prototype that links operational evidence,
recommendation, approval, ledger and business value. I am looking for a small
number of AWS-first teams to test whether the underlying problem is real.

Would you be open to a 25-30 minute discovery conversation? No system access or
customer data is required.
```

Do not attach the pilot agreement, request credentials or promise a live
integration in initial outreach.

## 8. First Meeting - 20 To 30 Minutes

The first meeting is discovery-first. The L1 demonstration is optional and is
shown only after the buyer provides a concrete relevant case.

### 00:00-05:00 - Current Problem

Ask:

- How do you reconstruct why an important operational Decision was made?
- Who must approve material engineering or cloud changes?
- What happens when the reason, owner or Evidence is unclear months later?
- How do you attribute AWS or AI cost to a shipped capability?
- How do you prove who approved an action during audit or incident review?

Listen for process, delay, rework, risk and cross-team handoffs. Do not explain
IMPERATOR after every answer.

### 05:00-10:00 - One Recent Decision

Ask:

> Tell me about one recent operational Decision that had measurable cost,
> usage, risk or audit impact.

Then establish:

- why it existed;
- where intent and implementation Evidence lived;
- when cost or negative value became visible;
- who owned and approved it;
- how long reconstruction took;
- whether an action was considered; and
- whether its outcome was ever measured.

If no concrete case appears, do not force the demo. Continue discovery or end
with `NO-GO`/future fit.

### 10:00-20:00 - Optional Compressed L1 Demo

Use only the synthetic `DRC-AOA-001` proof:

```text
Evidence -> Decision -> Recommendation -> ROI -> Human Review
         -> Ledger -> Result Validation -> Business Value
```

Emphasize:

- EUR 19,440 annualized estimate under explicit canonical assumptions;
- deterministic Recommendation and ROI, independent of an AI provider;
- human approval and role separation;
- EUR 18,960 realized annualized value only after validation; and
- full traceability back to the original Evidence.

State clearly that these are synthetic canonical values, not customer results.
Use the detailed guardrails in
`../demos/47_15_Minute_Design_Partner_Demo_and_Value_Assessment.md`.

### 20:00-30:00 - Validation And Commitment

Ask:

- Does this represent a real and recurring problem in your environment?
- Which part would need stronger Evidence before you trusted it?
- What would solving one case be worth in time, avoided cost or control?
- Which systems and approved aggregate facts would be needed?
- Who would sponsor, implement, approve and financially validate the case?
- What security or procurement constraints would stop a pilot?
- If the product were adapted to this bounded case, would you commit to a
  pilot-readiness workshop?

The last answer must name a next action, owner and date to count as pilot
interest. Polite enthusiasm is not commitment.

## 9. Evidence To Capture

Record facts, not impressions:

| Area | Minimum discovery evidence |
|---|---|
| Problem | Concrete recent Decision and current reconstruction method |
| Frequency | How often a comparable problem occurs |
| Impact | Time, cost, risk or audit burden stated by the buyer |
| Systems | GitHub, AWS and approved manual/aggregate sources involved |
| Data feasibility | Required facts available without Restricted data |
| Ownership | Sponsor, Platform, Finance and approver roles identified |
| Trust | Evidence or controls required before action |
| Value | Buyer's method for measuring baseline and result |
| Commitment | Named next step, owner and date |
| Commercial signal | Willingness to discuss a paid pilot or budget test |

Do not record customer credentials, source data, raw prompts, confidential
payloads or personal contact information in this repository.

## 10. Funnel And Exit Criteria

Progress is measured by evidence-backed transitions:

```text
TARGET ACCOUNT
  -> CONTACTED
  -> DISCOVERY HELD
  -> PROBLEM CONFIRMED
  -> CASE IDENTIFIED
  -> DATA FEASIBLE
  -> OWNERS IDENTIFIED
  -> PILOT INTEREST
  -> DESIGN PARTNER QUALIFIED
  -> PILOT AUTHORIZED
  -> PAID
```

| Stage | Exit criterion |
|---|---|
| `TARGET ACCOUNT` | Fits one target tier and has a plausible public/referral signal |
| `CONTACTED` | Relevant role receives a bounded discovery request |
| `DISCOVERY HELD` | Current process and one case are discussed |
| `PROBLEM CONFIRMED` | Buyer states a recurring material problem in their own words |
| `CASE IDENTIFIED` | One bounded Decision with economic/governance impact exists |
| `DATA FEASIBLE` | Required normalized facts can be sourced without prohibited data |
| `OWNERS IDENTIFIED` | Sponsor, Platform, Finance and approval responsibilities are clear |
| `PILOT INTEREST` | Named next action, accountable person and date exist |
| `DESIGN PARTNER QUALIFIED` | Every mandatory gate in Section 11 passes |
| `PILOT AUTHORIZED` | Product, runtime, security, legal and data gates all pass |
| `PAID` | Executed commercial agreement and payment commitment exist |

Do not skip stages merely because an account has a strong brand or senior
contact.

## 11. Design Partner Qualification Gate

`DESIGN PARTNER QUALIFIED` requires all of the following:

- [ ] the problem is recurring and material;
- [ ] one Decision comparable to `DRC-AOA-001` is identified;
- [ ] CTO/VP Engineering sponsorship exists;
- [ ] Platform and Finance/FinOps will participate;
- [ ] one GitHub repository and one AWS account/Region are sufficient;
- [ ] read-only access is acceptable in principle;
- [ ] cost baseline and post-action measurement sources exist;
- [ ] raw prompts, Restricted data and customer secrets can stay outside;
- [ ] the customer accepts human approval and external execution;
- [ ] the expected outcome is measurable without guaranteed savings;
- [ ] a pilot-readiness workshop has an owner and date; and
- [ ] no request requires bypassing the current gate order, connecting a
      customer or ingesting customer data early.

Failing a gate produces `DISCOVERY CONTINUE`, `FUTURE FIT` or `NO-GO`, not an
architecture exception.

## 12. Metrics

Track weekly:

- target accounts contacted;
- discovery conversations completed;
- problems confirmed;
- concrete cases identified;
- data-feasible cases;
- complete owner groups;
- pilot-interest commitments;
- qualified Design Partners;
- authorized pilots; and
- paid conversions.

Also calculate conversion between every adjacent stage and median days spent
per stage. Do not report meetings as commercial validation.

### First Milestones

1. Three independent companies confirm the problem using their own recent
   cases.
2. At least one reaches `DESIGN PARTNER QUALIFIED`.
3. Only after technical and Pilot Readiness gates pass may that partner move
   toward `PILOT AUTHORIZED`.

One qualified Design Partner is the first commercial execution goal. Three
independent problem confirmations reduce the risk of adapting the product to a
single unusual organization.

## 13. Discovery Record

Store the actual record in an approved CRM or controlled commercial system.
Only sanitized aliases and aggregate R&D observations may enter this
repository.

```text
Discovery ID:
Date UTC:
Account alias:
Target tier:
Participant roles:
Concrete Decision:
Current reconstruction process:
Frequency:
Impact stated by buyer:
Systems involved:
Data feasibility:
Named responsibility gaps:
Trust/security requirements:
Economic measurement method:
Pilot commitment (owner/date):
Willingness-to-pay signal:
Current funnel stage:
Disqualification or blocker:
Next action:
```

## 14. Current Technical Boundary

```text
Sprint 4.3: CERTIFIED / COMPLETE
Sprint 4.3.1: COMPLETE
Sprint 4.4: NEXT
External Pilot Identity Conformance: DEFERRED / REQUIRED BEFORE 4.5
Sprint 4.5: NOT OPEN
Discovery: ALLOWED
L1 synthetic demo: ALLOWED
Customer connection: FORBIDDEN
Customer data ingestion: FORBIDDEN
Pilot execution: FORBIDDEN
```

Discovery findings may justify future product decisions after review. They do
not directly modify D087-D093, expand the MVP or authorize implementation.
