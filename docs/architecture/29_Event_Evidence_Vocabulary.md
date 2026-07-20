# 29 - Event and Evidence Vocabulary

## Purpose

Define the controlled vocabulary for the first IMPERATOR MVP before writing implementation code.

This document closes shared language for:

- operational events,
- normalized events,
- evidence types,
- evidence IDs,
- lifecycle states,
- blocker states,
- freshness labels,
- sensitivity labels,
- confidence labels,
- ledger event names,
- future agent handoffs.
- Enterprise Evidence Event envelope.

It is a Phase 0 architecture artifact. It does not define code, queues, schemas, generated bindings, APIs, migrations, services, connectors, tests, Docker, Kubernetes, Terraform or runtime infrastructure.

## Architect Coherence Check

Verdict: this is the correct document after per-connector contracts.

The current Phase 0 chain is:

```text
Manual Evidence Pack
-> Security and Data Governance
-> Acceptance Test Plan
-> Identity and Approval Model
-> Decision Review Workspace Screen Contract
-> Quality Attributes
-> Per-Connector MVP Contracts
-> Event and Evidence Vocabulary
```

The project remains aligned with the MVP rule:

**One Decision. One Timeline. One ROI.**

This document does not expand scope. It prevents future agents and developers from inventing event names, evidence names or blocker language during implementation.

## Canonical References

- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
- `docs/product/24_MVP_Vertical_Slice.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/21_Technical_Architecture_Context.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`

## Vocabulary Doctrine

Vocabulary is a product and architecture contract.

Events and evidence must describe facts, not decisions.

Use this rule:

**Connectors observe. Context normalizes. Evidence proves. ROI calculates. Recommendation proposes. Ledger records. Humans decide.**

## Naming Rules

| Item | Format | Example |
|---|---|---|
| Normalized event | `lower_snake_case` | `cloud_cost_observed` |
| Ledger event | `lower_snake_case` | `approved` |
| Lifecycle state | `lower_snake_case` | `review_ready` |
| Blocker code | `B` + three digits | `B006` |
| Evidence ID | `E-AREA-###` | `E-AI-001` |
| Assumption ID | `A-AREA-###` | `A-ROI-001` |
| Decision ROI Case ID | `DRC-AREA-###` | `DRC-AOA-001` |
| Work package ID | `WP` + two digits | `WP03` |
| R&D activity ID | `RD-YYYY-MM-###` | `RD-2026-07-001` |

Rules:

- Do not use provider object names as domain event names.
- Do not encode implementation technology in event names.
- Do not use vague names such as `data_received`, `ai_processed` or `cost_found`.
- Do not create new names unless this document is updated or an RFC/decision explicitly permits it.

## Event Layers

| Layer | Meaning | Example | Owner |
|---|---|---|---|
| Source Signal | Provider-specific fact before normalization | Jira issue, GitHub PR, AWS cost export, AI usage export | Connector Agent |
| Operational Event | Captured source fact with metadata | Provider event or manual import source | Connector/Backend later |
| Normalized Event | Provider-neutral event name | `ai_cost_observed` | Connector + Context |
| Evidence Item | Trusted business-context fact derived from event | `E-AI-001` | Product + Connector |
| Decision ROI Case Fact | Evidence attached to one case | Cost, owner, risk, usage, timeline fact | Product/Backend later |
| Ledger Event | Human/system accountability record | `approved`, `deferred` | Ledger/Backend later |

## MVP Normalized Events

### Business Context Events

| Event | Meaning | Primary source | Evidence supported |
|---|---|---|---|
| `business_context_requested` | A business need or request exists | Jira | `E-JIRA-001` |
| `decision_created` | A decision/work item was created | Jira | `E-JIRA-001` |
| `decision_status_observed` | Current lifecycle/status was observed | Jira | `E-JIRA-002` |
| `business_owner_observed` | Business owner was identified | Jira/manual | `E-JIRA-003`, `E-OWNER-001` |
| `business_value_proxy_observed` | Business goal or value proxy exists | Jira/manual | `E-JIRA-004`, `E-USAGE-*` |

### Code and Deployment Events

| Event | Meaning | Primary source | Evidence supported |
|---|---|---|---|
| `code_change_merged` | A code change was merged | GitHub | `E-GH-001`, `E-GH-002` |
| `code_review_observed` | Review metadata or reviewer evidence exists | GitHub | `E-GH-002` |
| `deployment_reference_observed` | Deployment reference connects change to runtime | GitHub/manual | `E-GH-003` |
| `technical_owner_observed` | Technical owner was identified | GitHub/manual | `E-OWNER-002` |
| `implementation_feasibility_observed` | Technical feasibility note exists | GitHub/manual | `E-GH-004` |

### Infrastructure and Cost Events

| Event | Meaning | Primary source | Evidence supported |
|---|---|---|---|
| `cloud_cost_observed` | Cloud cost exists for a review period | AWS | `E-AWS-001` |
| `cloud_resource_tag_observed` | Resource tag supports attribution | AWS | `E-AWS-002` |
| `cloud_utilization_observed` | Utilization signal exists | AWS | `E-AWS-003` |
| `cloud_owner_observed` | Cloud/platform ownership signal exists | AWS/manual | `E-AWS-004` |
| `cloud_attribution_gap_observed` | Cost/resource cannot be cleanly attributed | AWS/manual | blocker or deferral |

### AI Consumption Events

| Event | Meaning | Primary source | Evidence supported |
|---|---|---|---|
| `ai_usage_observed` | AI request/token usage exists | OpenAI + Anthropic Claude | `E-AI-003` |
| `ai_cost_observed` | AI cost exists for a review period | OpenAI + Anthropic Claude | `E-AI-001` |
| `ai_model_observed` | Model usage was observed | OpenAI + Anthropic Claude | `E-AI-002` |
| `ai_application_observed` | Usage can be attributed to app/team | OpenAI + Anthropic Claude/manual | `E-AI-004` |
| `ai_quality_review_observed` | Human quality/risk note exists | Manual review | `E-AI-005` |
| `ai_sensitive_payload_rejected` | Raw prompt/completion or restricted data was rejected | Security review | security blocker |

### Usage and Value Signal Events

| Event | Meaning | Source | Evidence supported |
|---|---|---|---|
| `usage_signal_observed` | Usage exists but does not prove realized value | Product analytics/manual export | `E-USAGE-001` |
| `active_user_count_observed` | Active user count exists for period | Product analytics/manual export | `E-USAGE-002` |
| `value_proxy_observed` | Business proxy exists | Business owner/manual | `E-USAGE-003` |
| `usage_signal_missing` | Usage context is absent | Manual review | confidence reduction |

### ROI and Recommendation Events

| Event | Meaning | Source | Evidence supported |
|---|---|---|---|
| `roi_view_created` | ROI view was prepared from evidence and assumptions | ROI rules/manual | ROI snapshot later |
| `roi_assumption_observed` | Assumption exists and is reviewable | ROI slice/manual | `A-ROI-*` |
| `approval_path_observed` | Approval path and final authority are known | Identity model/manual | `E-OWNER-003` |
| `recommendation_created` | Recommendation is reviewable, not approved | Analyst/system later | ledger entry |
| `recommendation_risk_observed` | Risk level was assessed | Product/technical review | recommendation |
| `recommendation_confidence_observed` | Confidence score was assessed | Evidence model | recommendation |

### Ledger Events

| Event | Meaning | Authorized actor |
|---|---|---|
| `recommendation_created` | A recommendation is ready for review | Analyst/system later |
| `approved` | Authorized human approved | CTO or VP Engineering |
| `rejected` | Authorized human rejected | CTO or VP Engineering |
| `deferred` | Authorized reviewer deferred for evidence/time/risk | CTO, Business Owner, Platform, FinOps or Security |
| `evidence_requested` | Reviewer requested missing evidence | Required reviewer |
| `implementation_marked` | External implementation was marked complete | Platform Lead or delegate |
| `result_validated` | Realized result was validated | FinOps / Finance Owner |
| `case_closed` | Case was closed after rejection or validation | CTO or VP Engineering |

## Evidence Type Vocabulary

| Evidence type | ID prefix | Meaning | Required for MVP approval readiness |
|---|---|---|---|
| Business context evidence | `E-JIRA` | Why the decision exists | Yes |
| Code and deployment evidence | `E-GH` | What changed and when it shipped | Yes |
| Cloud cost evidence | `E-AWS` | Cloud cost and attribution | Yes |
| Cloud utilization evidence | `E-AWS` | Usage/utilization of cloud resources | Yes if underutilization is claimed |
| AI consumption evidence | `E-AI` | Model, usage, tokens, requests and cost | Yes |
| AI quality review evidence | `E-AI` | Risk/quality note for model change | Yes for low-risk claim |
| Usage or value signal evidence | `E-USAGE` | Usage signal or value proxy | Yes for confidence, not for realized value |
| Owner and approval evidence | `E-OWNER` | Business, technical, FinOps and final authority | Yes |
| ROI assumption | `A-ROI` | Financial assumption used by ROI | Yes |
| Labor assumption | `A-LABOR` | Labor recovery assumption | Optional/separate |
| Implementation evidence | `E-IMPL` | External action was implemented | Only after approval |
| Result validation evidence | `E-VAL` | Post-action result was measured | Only after implementation |

## Canonical Evidence IDs for DRC-AOA-001

| Area | Canonical IDs |
|---|---|
| Jira | `E-JIRA-001`, `E-JIRA-002`, `E-JIRA-003`, `E-JIRA-004` |
| GitHub | `E-GH-001`, `E-GH-002`, `E-GH-003`, `E-GH-004` |
| AWS | `E-AWS-001`, `E-AWS-002`, `E-AWS-003`, `E-AWS-004` |
| AI Consumption | `E-AI-001`, `E-AI-002`, `E-AI-003`, `E-AI-004`, `E-AI-005` |
| Usage | `E-USAGE-001`, `E-USAGE-002`, `E-USAGE-003` |
| Ownership | `E-OWNER-001`, `E-OWNER-002`, `E-OWNER-003` |
| ROI assumptions | `A-ROI-001`, `A-ROI-002`, `A-ROI-003`, `A-ROI-004` |
| Labor assumptions | `A-LABOR-001`, `A-LABOR-002` |

## Enterprise Evidence Event Vocabulary

Every connector/import adapter must produce an Enterprise Evidence Event before evidence is normalized for the domain.

Required conceptual fields:

| Field | Rule |
|---|---|
| `id` | Stable evidence-event identifier |
| `schema_version` | Contract version |
| `timestamp` | Source event time or observation time |
| `ingested_at` | IMPERATOR intake time |
| `source` | Concrete source system |
| `source_type` | Controlled source category |
| `source_object_ref` | Non-secret source reference |
| `entity` | Observed business or technical object |
| `event_type` | Controlled normalized event name |
| `severity` | `info`, `low`, `medium`, `high` or `critical` when applicable |
| `actor` | Human, system or service actor if known |
| `evidence_type` | Controlled evidence type |
| `case_hint` | Optional Decision ROI Case hint |
| `sensitivity` | Public, Internal, Confidential or Restricted |
| `confidence` | Low, Medium or High |
| `freshness` | Fresh, stale, unknown or not applicable |
| `metadata` | Sanitized reasoning context |
| `raw_payload` | Controlled descriptor; Phase 1 mode must be `not_stored` |

Provider-specific fields belong inside sanitized `metadata` only when safe and useful.

## Evidence Item Required Fields

Every evidence item must include:

| Field | Required | Rule |
|---|---|---|
| `evidence_id` | Yes | Use canonical ID format |
| `evidence_type` | Yes | Use type vocabulary |
| `source_system` | Yes | Jira, GitHub, AWS, OpenAI, Anthropic Claude or manual |
| `source_object` | Yes | Source reference or approved manual reference |
| `observed_fact` | Yes | Factual statement only |
| `business_meaning` | Yes | Why it matters for the Decision ROI Case |
| `period_or_timestamp` | Yes | Date, time or review period |
| `correlation_key` | Yes | Link to case, project, owner or source chain |
| `sensitivity` | Yes | Public, Internal, Confidential or Restricted |
| `confidence_contribution` | Yes | Contribution or qualitative effect |
| `raw_payload.mode` | Yes | Must be `not_stored` for MVP review evidence |
| `review_status` | Yes | accepted, rejected, missing, stale, disputed or needs_review |

## Lifecycle State Vocabulary

| State | Meaning | Can approve? |
|---|---|---|
| `draft_case` | Case exists but evidence or ROI is incomplete | No |
| `not_reviewable` | Required evidence, owner, approver, assumption or safety classification is missing | No |
| `review_ready` | Evidence, ROI, assumptions, owner and approver are present | Yes, if actor is authorized |
| `deferred` | Reviewer needs more evidence, review date or risk resolution | No |
| `approved` | Authorized human approved recommendation | No further approval needed |
| `rejected` | Authorized human rejected recommendation | No |
| `implemented` | Approved action was implemented outside IMPERATOR | No |
| `validated` | Result was validated and realized value can be reported | No |
| `closed` | Case is no longer active | No |

State rule:

The state belongs to the Decision ROI Case or recommendation lifecycle. It does not belong to a connector.

## Blocker Vocabulary

| Code | Blocker | Default behavior | Owner |
|---|---|---|---|
| `B001` | `business_owner_missing` | Block approval | Product |
| `B002` | `technical_owner_missing` | Block approval | Product/Backend later |
| `B003` | `final_approver_missing` | Block approval | Security/Product |
| `B004` | `required_jira_evidence_missing` | Block business context | Connector/Product |
| `B005` | `required_github_evidence_missing` | Block technical trust | Connector/Backend |
| `B006` | `aws_cost_missing` | Block full ROI | Connector/FinOps |
| `B007` | `ai_cost_or_usage_missing` | Block model-change recommendation | Connector/FinOps |
| `B008` | `roi_assumptions_missing` | Block approval readiness | FinOps |
| `B009` | `quality_review_note_missing` | Raise risk or defer | Product/Platform |
| `B010` | `sensitivity_unknown` | Treat as Confidential; review required | Security |
| `B011` | `restricted_data_present` | Exclude and require security review | Security |
| `B012` | `user_lacks_authority` | Hide/disable action | Security |
| `B013` | `approval_note_missing` | Disable approval | Product/Frontend later |
| `B014` | `snapshot_unavailable` | Block ledger action | Backend/Ledger later |
| `B015` | `estimated_and_realized_value_mixed` | Block Business Value claim | FinOps/Product |
| `B016` | `implementation_before_approval` | Block mark-implemented | Ledger |
| `B017` | `validation_without_post_action_evidence` | Block validate-result | FinOps |
| `B018` | `missing_correlation_key` | Manual review required | Connector |
| `B019` | `stale_cost_or_usage_period` | Warning or deferral | FinOps |
| `B020` | `provider_unavailable` | Use snapshot only; no fabricated data | Connector |
| `B021` | `permission_too_broad` | Reject connector posture | Security |

## Freshness Vocabulary

| Label | Meaning | MVP behavior |
|---|---|---|
| `current_period` | Evidence belongs to accepted review period | Can support approval |
| `historical_stable` | Historical fact remains valid, such as PR merge date | Can support approval with source reference |
| `stale_warning` | Evidence is older than target freshness | Show warning; may reduce confidence |
| `stale_blocker` | Evidence is too old for approval | Defer or request evidence |
| `period_mismatch` | Cost, usage or validation periods do not match | FinOps review required |
| `unknown_period` | Date/period is missing | Not approval-ready |

MVP default:

Cost and usage evidence older than 45 days should show stale warning and may defer approval unless explicitly accepted as a pilot assumption.

## Sensitivity Vocabulary

| Label | Meaning | MVP handling |
|---|---|---|
| `Public` | Safe for public/anonymized demo | Can be shown broadly |
| `Internal` | Internal operational data | Visible to assigned roles |
| `Confidential` | Cost, owner, usage, PR or operational evidence | Role-filtered |
| `Restricted` | Secrets, credentials, raw prompts, raw completions, customer conversations | Excluded from MVP evidence |

Unknown sensitivity is treated as `Confidential` until reviewed.

## Confidence Vocabulary

| Label | Meaning |
|---|---|
| `high_confidence` | Required evidence exists, periods align and assumptions are explicit |
| `medium_confidence` | Evidence exists but has gaps, stale elements or manual assumptions |
| `low_confidence` | Missing source, poor attribution or weak usage/value signal |
| `not_assessable` | Required evidence is absent or unsafe |

Confidence must represent evidence completeness and assumption quality. It must not mean AI certainty.

## Event to Evidence Mapping for MVP

| Normalized event | Evidence type | Case claim supported |
|---|---|---|
| `business_context_requested` | Business context evidence | Why the decision exists |
| `code_change_merged` | Code and deployment evidence | What implementation happened |
| `deployment_reference_observed` | Code and deployment evidence | Whether change shipped |
| `cloud_cost_observed` | Cloud cost evidence | AWS cost part of ROI |
| `cloud_utilization_observed` | Cloud utilization evidence | Underutilization claim |
| `ai_cost_observed` | AI consumption evidence | AI cost part of ROI |
| `ai_model_observed` | AI consumption evidence | Model-change rationale |
| `ai_usage_observed` | AI consumption evidence | Usage concentration |
| `ai_quality_review_observed` | AI quality review evidence | Low-risk support |
| `usage_signal_observed` | Usage/value evidence | Confidence and business context |
| `roi_assumption_observed` | ROI assumption | Explainable ROI |
| `approval_path_observed` | Owner and approval evidence | Human authority and review path |
| `recommendation_created` | Ledger event | Reviewable recommendation |
| `approved` | Ledger event | Human approval |
| `result_validated` | Ledger event | Realized value eligibility |

## Agent Ownership

| Vocabulary area | Owner | Supporting agents |
|---|---|---|
| Product meaning | Product Agent | CTO |
| Connector events | Connector Agent | Backend, Security |
| Evidence type mapping | Product + Connector | QA |
| Cost and ROI terms | FinOps Agent | Product |
| Security labels | Security Agent | QA |
| Lifecycle and ledger events | Backend/Ledger later | Product, CTO |
| Screen blockers | Frontend later | QA, Security |
| Vocabulary governance | CTO Agent | All |

## Change Control

Add a new vocabulary item only when:

1. It supports the current Decision ROI Case or an approved expansion.
2. Its owner is clear.
3. Its behavior is testable.
4. Its sensitivity and freshness implications are known.
5. It does not move ROI, recommendation, approval or ledger authority into connectors.
6. The change is recorded in `docs/decisions/14_Decision_Log.md` if it affects product, domain, API, architecture or MVP scope.

## Negative Rules

Reject any future vocabulary proposal that:

- uses provider-specific names as domain events,
- lets connectors emit recommendations,
- lets AI-generated explanations become evidence without source references,
- treats missing evidence as zero cost or zero risk,
- mixes estimated and realized value,
- stores raw provider payload names as canonical domain objects,
- creates lifecycle states that bypass human approval,
- introduces events for autonomous execution in the MVP,
- creates broad portfolio event names before one Decision ROI Case is validated.

## Manual Review Checklist

Before Phase 2 or Phase 3 implementation planning, verify:

1. Every MVP event has one canonical name.
2. Every evidence item has an accepted ID format.
3. Every blocker has a code and owner.
4. Every lifecycle state matches the screen contract.
5. Ledger event names match Decision Ledger v2.
6. Cost and usage freshness labels are understood by FinOps.
7. Security labels match the threat model.
8. Connector events do not contain business decisions.
9. AI consumes prepared evidence context only.
10. Future tests can reference these names without inventing labels.

## Companion Control Artifact

The companion I+D/R&D evidence artifact is:

`docs/rnd/30_RD_Activity_Evidence_Dossier.md`

Status: created.

Reason:

The MVP pre-code architecture vocabulary is now closed. The I+D/R&D dossier defines how to document architecture work, future code, hours, objects, experiments, tests, decisions and technical uncertainty for possible future accreditation or startup diligence.

The final operational control is `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`, the manual go/no-go review before authorizing implementation scaffolding.
