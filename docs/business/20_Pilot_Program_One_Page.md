# IMPERATOR Pilot Charter

Status: **INTERNAL COMMERCIAL PREPARATION / NOT OFFERABLE / NOT SIGNED**

This one-page charter becomes customer-facing only after Sprint 4.5 Pilot
Readiness is certified and commercial/legal review is complete.

## Objective And Business Question

Demonstrate that IMPERATOR can turn approved operational facts into one
explainable Decision, one deterministic Recommendation, one governed action
and measurable economic value.

> Is one shipped AI/cloud Decision costing more than the value it creates, and
> can leadership approve a traceable action that credibly recovers value?

The customer should understand the answer, Evidence, economic impact and
required action in less than 15 minutes.

## Scope

| Item | Pilot boundary |
|---|---|
| Decision ROI Case | `DRC-AOA-001` - AI Onboarding Assistant Recovery |
| Organization | Exactly one controlled customer organization |
| GitHub | One organization, repository and default branch |
| AWS | One commercial-partition account and one Region |
| Recommendation | One deterministic model downgrade/change |
| Workspace | One Decision Review Workspace |
| Duration | Proposed 30 calendar days plus an agreed measurement extension if the outcome period is incomplete |
| Delivery mode | Dedicated, non-public pilot environment; no shared multi-tenant service |

The first authorized rehearsal uses the synthetic canonical 30-Evidence pack.
Substitution with real customer facts and values requires separate pilot
authorization because D082 and D093 currently freeze the canonical policy.

## Participants

| Participant | Accountability |
|---|---|
| Customer Executive Sponsor | Owns commercial outcome and final pilot decision |
| Customer Business Owner | Confirms business need and value boundary |
| Customer Platform Engineer | Validates GitHub/AWS lineage and implements any approved action outside IMPERATOR |
| Customer Finance/FinOps | Accepts baseline, assumptions and realized-value calculation |
| Customer Security/Privacy Contact | Approves access, data handling and incident path |
| IMPERATOR Pilot Lead | Coordinates scope, evidence, reviews and acceptance |
| IMPERATOR Security/Operations Owner | Controls environment, access, backup, incidents and deletion evidence |

## Deliverables And Success Criteria

| Criterion | Acceptance evidence |
|---|---|
| Traceability | Evidence -> Decision -> Recommendation -> Ledger -> Business Value is reconstructable |
| Explainability | Reason, confidence, risk, policy and assumptions are understandable |
| Governance | Approval, implementation and validation are attributable and ordered |
| Value | Finance accepts estimated ROI and later validated realized value |
| Persistence | The complete case survives certified runtime recreation without duplication |
| Security | Read-only provider access, real JWT/RBAC and no Restricted data or secret leakage |
| Commercial comprehension | Executive Sponsor understands the value in under 15 minutes |

Outputs are one review session, one immutable governance history, one
Business Value result and one signed `GO`, `ITERATE` or `NO-GO` report.

## Data And Security Boundary

- No raw prompts, completions, customer conversations, credentials, secrets or
  unnecessary personal data.
- GitHub and AWS permissions are read-only and limited to the agreed boundary.
- Keycloak is external; IMPERATOR stores no passwords, tokens or IdP sessions.
- No customer data enters Git, screenshots, public issue trackers or R&D logs.
- Processing purpose, retention, deletion, hosting location, subprocessors,
  incident notification and international transfers must be agreed in writing.
- A dedicated environment is destroyed after the agreed retention period;
  append-only Ledger semantics do not override contractual deletion duties.

## Support And Stop Conditions

Pilot support is limited to agreed business hours and named contacts. It is not
a 24/7 availability SLA. The proposed response objectives are:

| Severity | Meaning | Response objective |
|---|---|---|
| `P0` | Suspected disclosure, unauthorized access or Ledger/data-integrity risk | Stop processing immediately; acknowledge within 4 business hours |
| `P1` | Pilot flow unavailable with no safe workaround | Acknowledge within 1 business day |
| `P2` | Non-blocking defect or documentation issue | Triage within 2 business days |

The pilot stops on a fixable Critical image vulnerability, invalid identity
trust, write-capable connector permission, Restricted data, unverifiable ROI,
broken Ledger lineage or any use outside the signed scope.

## Exclusions

No autonomous decision/execution, guaranteed savings, financial advice,
multi-tenancy, public production exposure, broad connector coverage, ranking,
portfolio analytics, production SLA or feature expansion is included.

## Exit

| Outcome | Meaning |
|---|---|
| `GO` | Every mandatory criterion passes and the customer requests another measurable Decision |
| `ITERATE` | Value is credible but one bounded Evidence, ownership or measurement issue must be corrected |
| `NO-GO` | The customer cannot identify measurable value or cannot trust the evidence-to-value chain |

The signed order form or pilot agreement must name the parties, dates,
environment, contacts, data terms, support window and commercial terms. This
charter is not a substitute for legal review.
