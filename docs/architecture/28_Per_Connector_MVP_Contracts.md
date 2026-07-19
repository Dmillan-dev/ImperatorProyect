# 28 - Per-Connector MVP Contracts

## Purpose

Define the MVP connector contracts for Jira, GitHub, AWS and OpenAI + Anthropic Claude before writing implementation code.

This document closes:

- source objects,
- evidence produced,
- normalized events,
- correlation keys,
- permissions,
- freshness rules,
- sensitivity expectations,
- failure modes,
- role and agent ownership per connector.

It is a Phase 0 architecture artifact. It does not define connector code, OAuth apps, provider credentials, API clients, sync jobs, queues, webhooks, database schema, migrations, generated bindings, Docker, Kubernetes, Terraform or production integration configuration.

## Architect Coherence Check

Verdict: this document is the correct next step after quality attributes.

The current pre-code chain is:

```text
Manual Evidence Pack
-> Security and Data Governance
-> Acceptance Test Plan
-> Identity and Approval Model
-> Decision Review Workspace Screen Contract
-> Quality Attributes
-> Per-Connector MVP Contracts
```

The project remains aligned with the MVP rule:

**One Decision. One Timeline. One ROI.**

These connector contracts do not expand the MVP. They make the four existing MVP sources precise enough for future agents to work independently without changing the core domain.

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
- `docs/architecture/24_MVP_Project_Structure.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`

## Connector Doctrine

Connectors are adapters.

They do not own:

- business meaning,
- ROI calculation,
- recommendation logic,
- approval authority,
- ledger mutation,
- provider-side execution.

They do own:

- source access boundary,
- source object mapping,
- source metadata preservation,
- freshness indicators,
- sensitivity hints,
- failure state reporting,
- evidence candidates for the Context/Evidence layer.

Core rule:

**A connector may bring facts into IMPERATOR. It must not decide what the company should do.**

## MVP Connector Set

| Domain | Connector | MVP purpose | Primary evidence |
|---|---|---|---|
| Business Context | Jira | Explain why the decision exists and who owns it | Jira issue, project, status, owner, business goal |
| Code and Deployment | GitHub | Explain who implemented it and what shipped | PR, review, merge, deployment reference |
| Infrastructure and Cost | AWS | Explain resources, cost, utilization and attribution | Cost export, tags, utilization, resource owner |
| AI Consumption | OpenAI + Anthropic Claude | Explain model, usage, requests, tokens, application and cost | Usage export, model summary, cost, quality note |

## Shared Connector Contract

Every future connector must define:

| Contract area | Required content |
|---|---|
| Source objects | Provider objects or exports used by the MVP |
| Evidence produced | Evidence IDs or evidence types the source can support |
| Normalized events | Product-level events emitted after normalization |
| Correlation keys | Fields used to connect the source to one Decision ROI Case |
| Permissions | Minimum read-only permissions; explicit exclusions |
| Freshness | How current the source must be for approval readiness |
| Sensitivity | Public, Internal, Confidential or Restricted handling |
| Failure modes | Missing, stale, ambiguous, sensitive or unavailable states |
| Blocker behavior | Whether the screen blocks approval, lowers confidence or allows deferral |
| Owning agent | Which AI workstream maintains the contract |

## Cross-Connector Correlation Keys

For `DRC-AOA-001`, the minimum correlation keys are:

- Jira issue key: `IMP-214`
- project key/name: `customer-onboarding`
- application name: `onboarding-assistant`
- AWS resource group/tag: `onboarding-assistant-prod`
- AWS tag: `jira_ticket=IMP-214`
- GitHub PR/title/branch reference to `IMP-214`
- deployment reference: `deploy-2026-04-05`
- AI usage application/team metadata: `onboarding-assistant`
- review period: `2026-06`

Correlation rule:

If a source cannot connect to at least one accepted correlation key, its evidence must not be attached to the Decision ROI Case without manual review.

## Jira Connector Contract

### Product Role

Jira provides business context.

It answers:

- Why does the decision exist?
- Which business need or request created it?
- Which project owns it?
- Who is the business owner?
- What is the lifecycle state?

### Source Objects

| Source object | MVP use | Required? |
|---|---|---|
| Issue | Decision origin and business need | Yes |
| Epic or project | Business grouping and project context | Yes if available |
| Status | Lifecycle state | Yes |
| Owner/assignee/custom owner field | Business ownership | Yes |
| Created/updated/resolved dates | Timeline | Yes |
| Labels/components | Correlation support | Optional |
| Comments/attachments | Avoid by default | No |

### Evidence Produced

| Evidence | Meaning |
|---|---|
| `E-JIRA-001` | Decision origin and business request |
| `E-JIRA-002` | Project and lifecycle state |
| `E-JIRA-003` | Business owner |
| `E-JIRA-004` | Value proxy boundary or business goal note |

### Normalized Events

- `business_context_requested`
- `decision_created`
- `decision_status_observed`
- `business_owner_observed`
- `business_value_proxy_observed`

### Correlation Keys

- issue key,
- project key,
- issue title,
- branch/PR reference to issue key,
- deployment reference,
- owner/team,
- date range.

### Permissions

Minimum future permission posture:

- read issue metadata,
- read project metadata,
- read owner/status fields required for the Decision ROI Case.

Explicitly avoid:

- write access,
- issue mutation,
- broad attachment ingestion,
- unrestricted comment ingestion,
- secret-bearing custom fields.

### Freshness

| Field | Freshness rule |
|---|---|
| Issue creation date | Historical; must remain source-referenced |
| Status | Should be verified at review time or marked stale |
| Owner | Must be current enough for approval path |
| Business goal | Can be historical if accepted as original decision intent |

If owner or status is unknown, the case cannot be approval-ready.

### Sensitivity

| Data | Sensitivity |
|---|---|
| Issue key/status | Internal |
| Business goal summary | Internal |
| Business owner | Confidential |
| Comments/attachments | Confidential or Restricted; avoid by default |

### Failure Modes

| Failure | MVP behavior |
|---|---|
| Issue missing | Block business context |
| Owner missing | Block approval readiness |
| Status unknown | Lower confidence or defer |
| Issue key not correlated to GitHub/AWS/AI | Manual review required |
| Sensitive comments required for context | Replace with approved summary |
| Jira unavailable later | Use existing evidence snapshot with freshness warning |

### Owning Agents

- Product Agent owns business meaning.
- Connector Agent owns source mapping.
- Security Agent owns sensitivity handling.
- QA Agent owns acceptance scenarios.

## GitHub Connector Contract

### Product Role

GitHub provides implementation and deployment context.

It answers:

- Who implemented the decision?
- What code or PR references the Jira decision?
- Was it reviewed?
- When did it ship?
- Is model/resource change technically feasible?

### Source Objects

| Source object | MVP use | Required? |
|---|---|---|
| Pull request metadata | Implementation evidence | Yes |
| PR title/body reference to Jira key | Correlation | Yes |
| Merge metadata | Timeline and actor | Yes |
| Review metadata | Review confidence | Yes if available |
| Commit metadata | Implementation chain | Optional |
| Deployment reference | Shipped state | Yes |
| Source code | Avoid by default | No |

### Evidence Produced

| Evidence | Meaning |
|---|---|
| `E-GH-001` | PR references `IMP-214` and implementation path |
| `E-GH-002` | PR merged by reviewed actor |
| `E-GH-003` | Deployment reference |
| `E-GH-004` | Technical feasibility note for model change |

### Normalized Events

- `code_change_merged`
- `code_review_observed`
- `deployment_reference_observed`
- `technical_owner_observed`
- `implementation_feasibility_observed`

### Correlation Keys

- Jira issue key in PR title/body/branch,
- repository name,
- PR number,
- deployment ID,
- application/service name,
- merge date,
- technical owner/team.

### Permissions

Minimum future permission posture:

- read PR metadata,
- read commit metadata,
- read review metadata,
- read deployment references.

Explicitly avoid:

- repository write access,
- workflow mutation,
- secrets access,
- full source ingestion by default,
- security scan findings as broad evidence without review.

### Freshness

| Field | Freshness rule |
|---|---|
| PR merge date | Historical; stable if source reference exists |
| Deployment reference | Must support shipped state |
| Technical feasibility note | Must be current enough for recommendation review |

If deployment reference is missing, the case can still exist but technical trust is incomplete.

### Sensitivity

| Data | Sensitivity |
|---|---|
| PR metadata | Confidential |
| Deployment reference | Internal or Confidential |
| Technical owner | Confidential |
| Source code | Confidential or Restricted; avoid by default |
| Secrets | Restricted; never evidence |

### Failure Modes

| Failure | MVP behavior |
|---|---|
| PR missing | Block implementation chain |
| Jira key absent from PR | Manual correlation required |
| Deployment reference missing | Lower confidence or defer |
| Source code required to prove claim | Prefer manual technical summary |
| Secrets or sensitive code appear | Exclude and require security review |
| GitHub unavailable later | Use existing evidence snapshot with freshness warning |

### Owning Agents

- Backend Agent owns future ingestion/read model implications.
- Connector Agent owns source mapping.
- Product Agent owns product meaning.
- Security Agent owns sensitive code and secret boundaries.

## AWS Connector Contract

### Product Role

AWS provides infrastructure, cost, utilization and attribution context.

It answers:

- What resources exist because of the decision?
- What do they cost?
- How are they tagged?
- Are they used enough to justify cost?
- Who technically owns them?

### Source Objects

| Source object | MVP use | Required? |
|---|---|---|
| Cost Explorer export or billing export | Current monthly cost | Yes |
| Resource tags | Attribution to project/Jira decision | Yes |
| Resource inventory summary | Resource identity | Yes |
| Utilization summary | Risk and optimization context | Yes if available |
| Billing owner note | Technical ownership | Yes if not present in tags |
| CloudWatch raw logs | Avoid by default | No |

### Evidence Produced

| Evidence | Meaning |
|---|---|
| `E-AWS-001` | AWS monthly cost |
| `E-AWS-002` | Resource tags connect spend to `IMP-214` |
| `E-AWS-003` | Utilization summary |
| `E-AWS-004` | Platform ownership |

### Normalized Events

- `cloud_cost_observed`
- `cloud_resource_tag_observed`
- `cloud_utilization_observed`
- `cloud_owner_observed`
- `cloud_attribution_gap_observed`

### Correlation Keys

- `jira_ticket=IMP-214`,
- `project=customer-onboarding`,
- resource group/name,
- application name,
- AWS account/environment,
- billing period,
- owner/team tag.

### Permissions

Minimum future permission posture:

- read cost and billing summaries,
- read tags,
- read resource inventory summaries,
- read utilization summaries.

Explicitly avoid:

- infrastructure write permissions,
- account administrator permissions,
- secret access,
- IAM mutation,
- runtime log ingestion by default.

### Freshness

Cost and utilization evidence should come from the latest accepted billing/review period.

MVP default:

- review period must be visible,
- evidence older than 45 days should show stale warning,
- stale cost evidence should defer approval unless explicitly accepted as a pilot assumption.

### Sensitivity

| Data | Sensitivity |
|---|---|
| Cost export | Confidential |
| Resource tags | Confidential |
| Utilization summary | Confidential |
| Resource owner | Confidential |
| Secrets/IAM credentials | Restricted; never evidence |

### Failure Modes

| Failure | MVP behavior |
|---|---|
| Cost missing | Block full ROI |
| Tags missing | Manual attribution required |
| Shared resource cost unclear | Lower confidence or defer |
| Billing period mismatched | Show freshness/period blocker |
| Currency mismatch | Require FinOps review |
| Write permission requested | Reject as out of MVP scope |
| AWS unavailable later | Use snapshot with freshness warning |

### Owning Agents

- FinOps Agent owns cost interpretation and currency/period consistency.
- Connector Agent owns AWS source mapping.
- Backend Agent owns future intake/read model implications.
- Security Agent owns permission and secret boundaries.

## OpenAI + Anthropic Claude Connector Contract

### Product Role

OpenAI + Anthropic Claude provide AI consumption context.

They answer:

- Which model is used?
- How many requests/tokens are consumed?
- Which application/team drives usage?
- What does AI usage cost?
- Is a lower-cost model plausible for routine tasks?

### Source Objects

| Source object | MVP use | Required? |
|---|---|---|
| Usage export | Request/token volume | Yes |
| Cost export | AI monthly cost | Yes |
| Model usage summary | Model-change rationale | Yes |
| Application/team metadata | Attribution | Yes |
| Quality review note | Risk reduction | Yes for low-risk claim |
| Raw prompts/completions | Excluded | No |

### Evidence Produced

| Evidence | Meaning |
|---|---|
| `E-AI-001` | AI monthly cost |
| `E-AI-002` | High-cost model usage |
| `E-AI-003` | Token/request concentration in routine tasks |
| `E-AI-004` | AI usage metadata links to application/team |
| `E-AI-005` | Quality review note |

### Normalized Events

- `ai_usage_observed`
- `ai_cost_observed`
- `ai_model_observed`
- `ai_application_observed`
- `ai_quality_review_observed`
- `ai_sensitive_payload_rejected`

### Correlation Keys

- application name,
- team/project tag,
- billing period,
- model name,
- provider,
- owner/team metadata,
- optional Jira or deployment reference.

### Permissions

Minimum future permission posture:

- read usage summaries,
- read cost summaries,
- read model metadata,
- read application/team metadata when available.

Explicitly avoid:

- raw prompt capture,
- raw completion capture,
- customer conversation ingestion,
- provider write actions,
- model configuration mutation,
- secret or API key exposure.

### Freshness

AI usage and cost evidence should use the latest accepted billing/review period.

MVP default:

- cost and usage periods must match or be explained,
- evidence older than 45 days should show stale warning,
- missing quality note raises risk and should defer low-risk approval.

### Sensitivity

| Data | Sensitivity |
|---|---|
| AI cost | Confidential |
| Model usage | Confidential |
| Token/request counts | Confidential |
| Application/team metadata | Confidential |
| Quality review summary | Confidential |
| Raw prompts/completions | Restricted; never MVP evidence |

### Failure Modes

| Failure | MVP behavior |
|---|---|
| AI cost missing | Block model-change recommendation |
| Model unknown | Lower confidence or defer |
| Usage period mismatched with cost | Show period blocker |
| Raw prompts required to justify claim | Reject; use approved summary or quality note |
| Quality note missing | Risk becomes Medium/High; defer low-risk claim |
| Multi-provider attribution unclear | Require manual review |
| Provider unavailable later | Use snapshot with freshness warning |

### Owning Agents

- AI Agent owns explanation and future prepared-context usage.
- FinOps Agent owns cost interpretation.
- Connector Agent owns source mapping.
- Security Agent owns prompt/completion boundaries.
- Product Agent owns recommendation meaning.

## Connector Readiness Matrix

| Connector | Approval-ready minimum | If missing |
|---|---|---|
| Jira | Issue, business need, project/status, owner | Block business context and approval readiness |
| GitHub | PR reference, merge/review evidence, deployment reference or technical summary | Block or defer technical trust |
| AWS | Current cost, tags/attribution, utilization or owner summary | Block full ROI |
| OpenAI + Anthropic Claude | Cost, model usage, token/request summary, app/team metadata, quality note for low risk | Block model-change recommendation or raise risk |

## Shared Failure Policy

| Failure class | Product behavior |
|---|---|
| Missing required source | Not review-ready |
| Missing correlation key | Manual review required |
| Stale evidence | Freshness warning; possible deferral |
| Sensitivity unknown | Treat as Confidential |
| Restricted data present | Exclude and require security review |
| Permission too broad | Reject connector posture for MVP |
| Provider unavailable | Use existing snapshots only; do not fabricate data |
| Cost period mismatch | Require FinOps review |
| Owner missing | Block approval readiness |

## Agent Work Partitioning

| Workstream | Owns | Must not own |
|---|---|---|
| Product Agent | business meaning, Decision ROI Case interpretation, recommendation family | provider permission design |
| Connector Agent | source object mapping, normalized events, freshness and failure states | ROI math or approval authority |
| Backend Agent | future intake/read-model implications | connector-specific business decisions |
| FinOps Agent | cost interpretation, currency, billing period, ROI evidence quality | final business approval |
| Security Agent | sensitivity, permissions, raw data exclusion | ROI or recommendation ownership |
| AI Agent | prepared-context explanation and evidence citation | raw source access or decision authority |
| QA Agent | acceptance scenarios and quality gates | product strategy changes |
| CTO Agent | architecture consistency and decision log | unlogged scope expansion |

## Manual Review Checklist

Before implementation, verify:

1. Each connector has source objects defined.
2. Each connector maps to evidence types from the Manual Evidence Pack.
3. Each connector has normalized event names.
4. Each connector has correlation keys.
5. Each connector has read-only permission posture.
6. Each connector excludes raw Restricted data.
7. Each connector defines freshness behavior.
8. Each connector defines failure modes.
9. Each failure mode maps to blocker, warning, lower confidence or deferral.
10. Agent ownership is clear for every connector.

## Negative Rules

Reject any future connector design that:

- requires provider write permissions for the MVP,
- sends raw provider payloads directly to AI,
- stores raw prompts or completions as evidence,
- makes connector availability required to read historical ledger entries,
- lets connector logic calculate ROI,
- lets connector logic approve or execute recommendations,
- adds new MVP integrations before Jira, GitHub, AWS and OpenAI + Anthropic Claude are validated,
- requires Kafka, Kubernetes or Terraform before the first Decision ROI Case is proven.

## Companion Documents

The companion event/evidence vocabulary and Phase 0 control artifacts are now created:

- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`

Reason:

The connector contracts define source objects and failure behavior. The event/evidence vocabulary defines normalized event names, evidence types, lifecycle labels and blocker states so future agents use the same language.
