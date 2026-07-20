# 00 - Context Control And Go/No-Go

## Purpose

Decide whether IMPERATOR Phase 1 may begin under the reduced MVP contract, and lock the minimum operating choices before any implementation scaffolding is created.

This is a control document for agentic Phase 1 work. It does not create code, services, connectors, database migrations, package manifests, generated bindings, credentials, Docker, Kubernetes, Terraform or cloud resources.

## Control Verdict

**Verdict: GO for limited Phase 1 MVP scaffolding.**

This is not approval to build the full IMPERATOR platform.

This is approval to start only the minimum Phase 1 work required to prove one Decision ROI Case end to end under:

- `docs/architecture/31_MVP_Implementation_Standard.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `agents/phase1/README.md`

Phase 1 remains constrained by:

```text
One Decision ROI Case
One evidence chain
One ROI view
One deterministic recommendation
One AI explanation
One review workspace
One ledger history
Minimal auth
Minimal observability
```

Controlling rule:

> Todo lo que no sea imprescindible para demostrar un unico Decision ROI Case queda automaticamente fuera del alcance.

## Decision Summary

| Control question | Formal decision |
|---|---|
| Is Phase 1 authorized? | Yes, limited Phase 1 MVP scaffolding is authorized. |
| Is full platform implementation authorized? | No. |
| First Decision ROI Case | `DRC-AOA-001` - AI Onboarding Assistant Recovery. |
| First recommendation family | AI model downgrade or model change. |
| First source mode | Manual/static evidence pack or file/import evidence source. |
| First live connector | Deferred until the import/manual evidence path proves the value loop. |
| First auth mode | Local/demo JWT-compatible auth with simple RBAC roles: `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR`. |
| OAuth providers | Deferred; one provider only if a real pilot requires it. |
| First UI surface | Decision Review Workspace only. |
| First persistence boundary | Minimal Decision ROI Case, evidence summaries, ROI assumptions, one recommendation, AI explanation and ledger entries. |
| AI role | Explanation only over prepared context. |
| Observability | Structured logs, correlation ID, `/health`, `/ready`, basic metrics. |
| R&D evidence capture | Required from first real implementation activity. |

## Authority Inputs

This stage is valid only because these documents already exist:

- `docs/decisions/14_Decision_Log.md`
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
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`
- `docs/architecture/31_MVP_Implementation_Standard.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `agents/README.md`
- `agents/phase1/README.md`

## Scope Lock

### In Scope

- create minimal implementation scaffolding when explicitly requested,
- preserve Hexagonal Architecture / Ports and Adapters,
- model only one Decision ROI Case,
- support one evidence source path first,
- calculate deterministic ROI,
- generate exactly one deterministic recommendation,
- generate AI explanation over prepared context,
- record review state in append-only ledger history,
- expose Decision Review Workspace only,
- expose minimal health and observability.

### Out Of Scope

- full platform build-out,
- multiple Decision ROI Cases,
- multiple recommendations,
- ranking,
- learning systems,
- autonomous execution,
- live automation of all connectors,
- provider write actions,
- public APIs,
- SDKs,
- Kafka,
- Kubernetes,
- Terraform,
- Graph DB,
- vector database,
- OpenSearch,
- full Grafana/OpenTelemetry platform,
- multiple OAuth providers,
- enterprise SSO,
- broad Executive Workspace,
- standalone Business Value suite,
- connector marketplace,
- policy engine.

## Initial Source Decision

### Selected Initial Source Mode

Use:

```text
Manual/static evidence pack or file/import evidence source
```

Canonical source:

- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`

Reason:

- it proves the Decision ROI Case without real provider credentials,
- it avoids premature connector complexity,
- it keeps Restricted data out,
- it allows Product, Connector, Security, FinOps and QA agents to validate the evidence chain before automation,
- it preserves the four-domain evidence story without requiring all four live connectors.

### First Live Connector Policy

No live connector is required before the manual/import path proves the value loop.

When a live connector becomes justified, the default first candidate should be the smallest read-only source needed to strengthen `DRC-AOA-001`.

Candidate order:

1. AI usage/cost export or import path.
2. AWS cost/utilization read-only source.
3. GitHub PR/deployment metadata.
4. Jira issue metadata.

This order may change only through CTO Agent review and Decision Log update.

## Authentication Decision

### Selected Auth Mode

Use:

```text
Local/demo JWT-compatible auth with simple RBAC role claims:

- `ADMIN`
- `PLATFORM_ENGINEER`
- `FINANCE`
- `AUDITOR`
```

Minimum required roles:

- CTO / VP Engineering
- Platform Lead
- FinOps / Finance Owner
- Security / Compliance Reviewer
- Viewer

Reason:

- Phase 1 must prove role-aware actions without implementing enterprise identity,
- approval authority is a product/domain rule, not generic login,
- OAuth2/OIDC compatibility is preserved,
- multiple providers are deferred until a real pilot requires them.

### Deferred Auth Work

Do not implement in the first Phase 1 pass:

- Google OAuth,
- Microsoft OAuth,
- GitHub OAuth,
- SCIM,
- full enterprise SSO,
- organization admin console,
- fine-grained policy builder.

## Observability Decision

### Selected Minimum

Phase 1 observability must include:

- structured logs,
- correlation/request ID,
- request log category,
- error log category,
- evidence import or connector log category,
- AI explanation log category,
- `/health`,
- `/ready`,
- basic request count,
- basic error count,
- request latency,
- recommendation generated count,
- AI explanation success/failure count,
- evidence import or source status count.

### Deferred Observability Work

Do not require before the first MVP proof:

- full OpenTelemetry deployment,
- Grafana dashboards,
- Prometheus deployment,
- alerting platform,
- distributed tracing across services,
- high-volume observability analytics.

Micrometer, OpenTelemetry and Grafana remain future-compatible direction, not first-pass prerequisites.

## AI Decision

AI explanation is included in Phase 1.

AI may:

- explain prepared evidence,
- summarize the Decision ROI Case,
- improve human-readable wording,
- cite evidence IDs,
- explain ROI assumptions.

AI must not:

- modify persistent data,
- execute business rules,
- calculate financial truth,
- replace the Decision Engine,
- approve,
- reject,
- defer,
- mark implementation,
- validate result,
- create ledger entries,
- access raw provider payloads,
- access raw prompts or completions,
- access credentials, secrets or customer conversations.

## R&D Evidence Decision

R&D evidence capture starts with the first real implementation activity.

Use:

- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/rnd/templates/01_ACTIVITY_LOG_TEMPLATE.md`
- `docs/rnd/templates/02_TECHNICAL_OBJECT_REGISTER_TEMPLATE.md`
- `docs/rnd/templates/03_EXPERIMENT_TEST_RECORD_TEMPLATE.md`
- `docs/rnd/templates/04_MONTHLY_RD_SUMMARY_TEMPLATE.md`

Minimum evidence to capture per real work session:

| Evidence item | Required |
|---|---|
| Date | Yes |
| Contributor or agent | Yes |
| Stage | Yes |
| Objective | Yes |
| Source documents used | Yes |
| Technical object touched or produced | Yes |
| Time spent | Yes, only if known truthfully |
| Tests or checks executed | Yes, if executed |
| Result | Yes |
| Uncertainty or blocker | Yes, if present |
| Next action | Yes |

Do not invent:

- hours,
- implementation results,
- test results,
- technical uncertainty,
- development activity,
- costs.

## Agent Assignments

| Area | Lead agent | Supporting agents |
|---|---|---|
| Context control | CTO Agent | QA, Product |
| Product case | Product Agent | FinOps, Connector, Security, QA |
| Acceptance | QA Agent | Product, Security, FinOps, Backend, Frontend |
| Architecture plan | CTO Agent | Backend, Frontend, Security, QA |
| Data and persistence | Backend Agent | FinOps, Security, QA |
| Evidence intake | Connector Agent | Backend, Security, FinOps, Product, QA |
| ROI and recommendation | Backend Agent | FinOps, Product, QA |
| AI explanation | AI Agent | Security, Product, FinOps, QA |
| Ledger and approval | Backend Agent | Security, Product, FinOps, QA |
| Review workspace | Frontend Agent | Product, Security, QA, Backend |
| Auth and observability | Security Agent | Backend, QA, CTO |
| Integrated demo | QA Agent | All |

## Mandatory Stop Conditions

Any agent must stop and hand off to CTO Agent if work requires:

- more than one Decision ROI Case,
- more than one recommendation,
- live automation of all connectors,
- provider write permission,
- raw prompts,
- raw completions,
- secrets,
- customer conversations,
- AI as decision-maker,
- ledger mutation,
- realized Business Value before result validation,
- public API,
- SDK,
- Kafka,
- Kubernetes,
- Terraform,
- Graph DB/vector/search infrastructure,
- full observability platform,
- multiple OAuth providers.

## Phase 1 Entry Checklist

| Entry item | Status |
|---|---|
| First Decision ROI Case selected | Pass - `DRC-AOA-001` |
| First recommendation family selected | Pass - AI model downgrade or model change |
| First source mode selected | Pass - manual/static or file/import evidence source |
| First live connector required | No |
| Auth mode selected | Pass - local/demo JWT-compatible RBAC role claims |
| Observability minimum selected | Pass |
| AI boundary selected | Pass - explanation only |
| R&D evidence model selected | Pass |
| Full platform scope rejected | Pass |
| Deferred infrastructure rejected | Pass |

## Go/No-Go Record

### Go

Phase 1 may proceed to:

```text
agents/phase1/01_product_case_lock.md
```

Status: created.

After that gate passes, continue through the staged process in `agents/phase1/README.md`.

### No-Go Conditions Still Active

Phase 1 must pause if:

- `DRC-AOA-001` is no longer accepted as the first sample,
- the first source mode changes to broad live connector automation,
- the first recommendation family changes,
- auth requires enterprise SSO,
- observability requires full platform deployment,
- evidence cannot exclude Restricted data,
- ROI cannot be explained with assumptions,
- the Decision Review Workspace cannot answer the trust-and-approval question.

## Handoff To Stage 01

Next stage:

```text
01 - Product Case Lock
```

Lead:

- Product Agent

Stage 01 objective:

Create the final Phase 1 case brief for `DRC-AOA-001`, including one-page user story, evidence-to-claim table, owner, approver, reviewer path and final recommendation wording.

Inputs for Stage 01:

- this document,
- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`,
- `docs/product/24_MVP_Vertical_Slice.md`,
- `docs/product/25_MVP_ROI_Slice.md`,
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`,
- `docs/product/CORE_DOMAIN_MODEL.md`,
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

Exit condition for Stage 01:

A reviewer can explain `DRC-AOA-001` without external narration and without adding scope beyond Phase 1.
