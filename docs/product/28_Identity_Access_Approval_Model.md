# 28 - Identity, Access and Approval Model

## Purpose

Define the MVP identity, access and approval model for IMPERATOR before writing implementation code.

This document answers:

- who can view evidence,
- who can approve, reject or defer a recommendation,
- who can mark implementation,
- who can validate realized value,
- what happens when owner, approver or required reviewer is missing.

It is a Phase 0 product and governance artifact. It does not define authentication code, OAuth, SSO, IAM policies, database schema, RBAC implementation, API handlers, services, connectors, migrations, generated bindings or infrastructure.

## Canonical References

- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
- `docs/product/24_MVP_Vertical_Slice.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/architecture/DATABASE_MODEL.md`

## Product Principle

IMPERATOR recommends. The company decides.

The MVP should not let infrastructure, AI, connectors or generic admin permissions approve business decisions.

Approval must be:

- human,
- role-aware,
- evidence-backed,
- ROI-aware,
- ledger-recorded,
- separated from execution.

## Scope

### In Scope

- MVP roles,
- evidence access rules,
- approval authority,
- rejection authority,
- deferral authority,
- implementation marking authority,
- result validation authority,
- missing-owner and missing-approver behavior,
- ledger consequences,
- acceptance rules for the first Decision ROI Case.

### Out Of Scope

- SSO,
- OAuth,
- SCIM,
- enterprise directory sync,
- production RBAC implementation,
- password or session management,
- legal compliance workflow,
- full GRC approvals,
- provider-side execution permission,
- multi-tenant admin console,
- fine-grained policy engine.

## MVP Roles

| Role | Meaning | MVP responsibility |
|---|---|---|
| Executive | Senior reader of decision impact | Sees summaries, priorities and business impact; not automatically an approver |
| CTO / VP Engineering | Final technical/business approver for the MVP | Approves, rejects or defers recovery recommendation |
| Business Owner | Owner of the business decision | Confirms whether the capability still matters and whether risk is acceptable |
| Platform Lead | Technical owner of implementation and resources | Confirms feasibility, marks implementation complete after external action |
| FinOps / Finance Owner | Financial reviewer | Confirms cost evidence, ROI assumptions and validates realized savings |
| Security / Compliance Reviewer | Evidence and data exposure reviewer | Reviews sensitivity, data access and AI-boundary concerns |
| Engineer | Implementation contributor | Provides technical evidence; may be assigned implementation evidence tasks |
| Admin | Organization/configuration administrator | Manages configuration later; not automatically a business approver |
| Viewer | Limited reader | Views allowed summaries only |
| AI Component | Future explanation/summarization helper | Reads prepared context only; never approves, rejects, defers, implements or validates |
| IMPERATOR Analyst / System Later | Manual analyst now, system capability later | Creates or prepares recommendations; not final business authority |

## MVP Approval Doctrine

For **AI Onboarding Assistant Recovery**, final approval belongs to:

**CTO or VP Engineering**

Required review before final approval:

1. Business Owner confirms the assistant still supports a real workflow.
2. Platform Lead confirms the model change is technically feasible.
3. FinOps / Finance Owner confirms cost evidence and ROI assumptions.
4. CTO or VP Engineering approves, rejects or defers.

These reviews can be represented as evidence, notes or required assumptions in Phase 0. They do not require a full workflow engine.

## Authority Matrix

| Action | Executive | CTO / VP Eng | Business Owner | Platform Lead | FinOps | Security | Engineer | Admin | Viewer | AI |
|---|---|---|---|---|---|---|---|---|---|---|
| View decision summary | Yes | Yes | Yes | Yes | Yes | Yes | Assigned | Configurable | Limited | Prepared only |
| View Internal evidence | Yes | Yes | Yes | Yes | Yes | Yes | Assigned | Configurable | Limited | Filtered |
| View Confidential evidence | Summary | Yes | Own case | Relevant technical | Cost/ROI | Review/audit | Assigned limited | No by default | No | Filtered summary only |
| View Restricted data | No | No | No | No | No | Case-by-case later | No | No | No | No |
| Create recommendation | No | Request | Request | Request | Request | Request | No | No | No | No |
| Mark recommendation review-ready | No | Yes | No | No | No | No | No | No | No | No |
| Approve recommendation | No by default | Yes | No | No | No | No | No | No | No | No |
| Reject recommendation | No by default | Yes | Business objection only | Technical objection only | Financial objection only | Security objection only | No | No | No | No |
| Defer recommendation | No by default | Yes | Yes, for business evidence | Yes, for technical evidence | Yes, for cost/ROI evidence | Yes, for safety evidence | No | No | No | No |
| Mark implementation complete | No | Acknowledge | No | Yes | No | No | Assigned evidence only | No | No | No |
| Validate realized value | No | Acknowledge | Business impact note | Technical confirmation | Yes | Review/audit | No | No | No | No |
| Manage roles/configuration later | No | No by default | No | No | No | No | No | Yes | No | No |

Admin is operational authority, not business authority. Admin permissions must not imply approval authority.

## Evidence Access Rules

The MVP uses the sensitivity model from `docs/architecture/26_Security_Data_Governance_Threat_Model.md`.

| Sensitivity | MVP access rule |
|---|---|
| Public | Visible to all users and public demo contexts if anonymized |
| Internal | Visible to organizational reviewers and assigned participants |
| Confidential | Visible only to roles with a decision-relevant reason |
| Restricted | Not ingested into MVP evidence, not shown in workspace, not sent to AI, not stored in ledger |

### Confidential Evidence Examples

| Evidence | Default access |
|---|---|
| AWS cost | CTO/VP Eng, Platform Lead, FinOps, Security if reviewing |
| AI usage cost | CTO/VP Eng, FinOps, Platform Lead, Security if reviewing |
| PR metadata | CTO/VP Eng, Platform Lead, assigned Engineer, Security if reviewing |
| Business owner identity | CTO/VP Eng, Business Owner, Platform Lead, FinOps |
| Usage active-user count | CTO/VP Eng, Business Owner, FinOps, Platform Lead |
| Quality review note | CTO/VP Eng, Platform Lead, Business Owner, FinOps if ROI depends on it |

## Approval Readiness Gates

A recommendation is approval-ready only when these gates pass:

| Gate | Required owner | Required evidence |
|---|---|---|
| G1 - Business context known | Business Owner | Jira/manual business evidence |
| G2 - Technical implementation known | Platform Lead | GitHub/deployment evidence |
| G3 - Cost evidence known | FinOps | AWS and AI usage/cost evidence |
| G4 - ROI assumptions visible | FinOps | ROI assumptions and formulas |
| G5 - Quality/business risk reviewed | Business Owner + Platform Lead | Quality note and usage/value signal |
| G6 - Security boundary safe | Security / Compliance if needed | Sensitivity labels and no Restricted data |
| G7 - Final approver assigned | CTO or VP Engineering | Approval path evidence |

If any gate fails, the recommendation may remain draft or be deferred. It must not be approved.

## Approval Flow

```text
Recommendation created
-> Business impact confirmed
-> Technical feasibility confirmed
-> FinOps ROI review completed
-> Security review completed if sensitivity requires it
-> CTO / VP Engineering approves, rejects or defers
-> Platform Lead marks implementation if company acts externally
-> FinOps validates realized value after validation period
```

This is a governance flow, not a workflow-engine requirement.

## Command Authority

### Create Recommendation

Allowed:

- IMPERATOR Analyst during manual Phase 0 validation,
- future system capability after evidence and ROI gates exist,
- CTO, Platform Lead, FinOps or Business Owner may request a recommendation.

Not allowed:

- AI Component as autonomous decision-maker,
- Connector,
- Viewer.

Ledger effect:

- creates or supports `recommendation_created` only when evidence and ROI assumptions exist.

### Approve Recommendation

Allowed:

- CTO or VP Engineering,
- explicitly assigned delegated approver only if recorded in approval-path evidence.

Required:

- evidence snapshot,
- ROI snapshot,
- assumptions snapshot,
- owner,
- approver,
- approval note.

Not allowed:

- AI Component,
- Connector,
- Admin by role alone,
- FinOps by default,
- Platform Lead by default,
- Business Owner by default,
- Viewer.

Ledger effect:

- creates append-only `approved` entry.
- does not execute provider-side changes.

### Reject Recommendation

Allowed:

- CTO or VP Engineering as final rejection authority.

Domain reviewers may raise objections:

- Business Owner may object on business impact,
- Platform Lead may object on technical risk,
- FinOps may object on cost or assumption quality,
- Security may object on sensitivity or governance risk.

Required:

- rejection reason,
- current evidence/ROI reviewed,
- actor and role.

Ledger effect:

- creates append-only `rejected` entry.

### Defer Recommendation

Allowed:

- CTO or VP Engineering,
- Business Owner for business evidence gaps,
- Platform Lead for technical feasibility gaps,
- FinOps for cost/ROI gaps,
- Security / Compliance for sensitivity or governance gaps.

Required:

- deferral reason,
- required evidence or review date,
- current confidence,
- current assumptions.

Ledger effect:

- creates append-only `deferred` entry.

### Mark Implementation Complete

Allowed:

- Platform Lead,
- assigned technical owner delegated by Platform Lead.

Required:

- prior approved ledger entry,
- implementation note,
- implementation date or period,
- implementation evidence.

Not allowed:

- IMPERATOR autonomous execution,
- AI Component,
- Connector,
- FinOps alone,
- Viewer.

Ledger effect:

- creates append-only `implementation_marked` entry.

### Validate Result

Allowed:

- FinOps / Finance Owner as primary validator.

Supporting reviewers:

- Business Owner may confirm business impact did not degrade,
- Platform Lead may confirm technical state,
- CTO/VP Engineering may acknowledge outcome.

Required:

- validation period,
- post-action cost evidence,
- realized saving,
- variance versus estimate,
- outcome note.

Ledger effect:

- creates append-only `result_validated` entry.
- makes realized value eligible for Business Value reporting.

## Missing Actor Rules

| Missing item | MVP behavior |
|---|---|
| Business Owner unknown | Recommendation cannot be approval-ready |
| Platform Lead unknown | Recommendation cannot be approval-ready |
| FinOps owner unknown | ROI can remain draft, but cannot be approval-ready |
| CTO / VP Engineering approver unknown | Recommendation cannot be approved |
| Security reviewer missing when Restricted risk exists | Recommendation must be deferred |
| Evidence sensitivity unknown | Treat as Confidential until reviewed |
| Required reviewer unavailable | Defer with review date or required evidence |

## Delegation Rules

Delegation is allowed only if it is explicit.

Delegation must record:

- delegating role,
- delegated actor,
- scope,
- duration or case,
- reason,
- source evidence or policy note.

Delegation must not grant:

- AI approval authority,
- connector execution authority,
- Admin business approval authority by default,
- access to Restricted data for MVP.

## Separation Of Duties

MVP principle:

The same actor should not silently create, approve, mark implemented and validate value for the same case.

Recommended separation:

| Responsibility | Preferred actor |
|---|---|
| Prepare recommendation | IMPERATOR Analyst / future system |
| Confirm business impact | Business Owner |
| Confirm technical feasibility | Platform Lead |
| Confirm ROI | FinOps |
| Final approval | CTO or VP Engineering |
| Mark implementation | Platform Lead |
| Validate result | FinOps |

For a very small pilot, one person may hold multiple business roles, but the ledger must record which role they acted as.

## Ledger Mapping

| Ledger entry | Authorized actor | Required reason/snapshot |
|---|---|---|
| `recommendation_created` | IMPERATOR Analyst / future system | Evidence + ROI + assumptions |
| `approved` | CTO or VP Engineering | Evidence + ROI + assumptions + approval note |
| `rejected` | CTO or VP Engineering | Rejection reason + reviewed evidence/ROI |
| `deferred` | CTO, Business Owner, Platform Lead, FinOps or Security depending on gap | Deferral reason + required evidence or review date |
| `implementation_marked` | Platform Lead or delegated technical owner | Implementation note + implementation evidence |
| `result_validated` | FinOps / Finance Owner | Validation evidence + realized saving + variance |
| `evidence_requested` | Any required reviewer | Evidence gap |
| `case_closed` | CTO or VP Engineering after validation/rejection | Closure reason |

## API Implications

The conceptual API must enforce these product rules later:

| API command | Required authority |
|---|---|
| `POST /decisions/{decisionId}/ledger/approve` | CTO / VP Engineering or explicit delegated approver |
| `POST /decisions/{decisionId}/ledger/reject` | CTO / VP Engineering or explicit delegated approver |
| `POST /decisions/{decisionId}/ledger/defer` | Any required reviewer with reason and evidence gap |
| `POST /decisions/{decisionId}/ledger/mark-implemented` | Platform Lead or delegated technical owner |
| `POST /decisions/{decisionId}/ledger/validate-result` | FinOps / Finance Owner |

Future implementation must return role-filtered evidence from:

- `GET /decisions/{decisionId}`,
- `GET /decisions/{decisionId}/evidence`,
- `GET /decisions/{decisionId}/timeline`,
- `GET /decisions/{decisionId}/roi`,
- `GET /decisions/{decisionId}/ledger`.

## Acceptance Alignment

This document resolves the open P0 approval questions from `docs/product/27_MVP_Acceptance_Test_Plan.md`.

| Acceptance question | MVP answer |
|---|---|
| Can FinOps approve direct-spend recommendations? | No by default. FinOps validates cost, ROI and realized value. |
| Must the Business Owner confirm impact before CTO approval? | Yes for this slice; confirmation can be evidence/assumption, not a workflow engine. |
| Can Platform Lead defer without CTO approval? | Yes, for technical feasibility or implementation evidence gaps. |
| Who can mark implementation complete? | Platform Lead or delegated technical owner. |
| Who can validate realized value? | FinOps / Finance Owner. |
| Who can view Confidential evidence? | Roles with decision-relevant reason; Viewer and AI get no raw Confidential data. |

## Negative Rules

Reject any future proposal that:

- lets AI approve, reject, defer, implement or validate,
- treats Admin as business approver by default,
- allows connector write permissions for MVP,
- exposes Restricted data in workspace, AI context or ledger,
- counts estimated value as realized value,
- approves a recommendation without owner, approver, evidence, ROI and assumptions,
- marks implementation before approval,
- validates result without post-action evidence.

## Manual Validation Checklist

Before Phase 1, this model is acceptable only if:

1. Every MVP action has an authorized role.
2. Every approval requires a human final approver.
3. Every deferral requires reason or missing evidence.
4. Every implementation marker requires prior approval.
5. Every result validation requires FinOps and post-action evidence.
6. Confidential evidence access is role-filtered.
7. Restricted data is out of MVP evidence, AI context and ledger.
8. Admin does not automatically become business approver.
9. AI has no decision authority.
10. Ledger entries record actor and role.

## Companion Documents

The companion screen, quality, connector, vocabulary and control artifacts are now created:

- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`

Reason:

The project now knows who may act, what the first review surface shows, hides, blocks and records, how MVP connectors provide evidence and which event/evidence labels future agents should use.
