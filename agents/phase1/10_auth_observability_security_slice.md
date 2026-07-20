# Phase 1 - Stage 10: Auth, Security And Observability Slice

## Purpose

Define the minimum authentication, authorization, security and observability needed to make the Phase 1 MVP safe, explainable and demoable.

This stage does not create code, identity providers, secrets, dashboards, Grafana stacks, OpenTelemetry collectors, cloud resources or production security configuration.

## Authority Inputs

This stage is governed by:

1. `agents/phase1/00_context_control.md`
2. `agents/phase1/02_acceptance_contract.md`
3. `agents/phase1/05_evidence_intake_slice.md`
4. `agents/phase1/07_ai_explanation_slice.md`
5. `agents/phase1/08_ledger_approval_slice.md`
6. `agents/phase1/09_review_workspace_slice.md`
7. `docs/product/28_Identity_Access_Approval_Model.md`
8. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
9. `docs/architecture/27_Quality_Attributes.md`
10. `docs/architecture/31_MVP_Implementation_Standard.md`
11. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
12. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

If there is conflict, the Security Threat Model and Phase 1 scope contract win.

## Stage Decision

Phase 1 uses local/demo JWT-compatible authentication with simple RBAC role claims.

The design remains compatible with future providers such as Azure AD, Okta, Keycloak, Google and GitHub, but Phase 1 does not implement OAuth, full enterprise SSO or multiple identity providers.

Conceptual flow:

```text
Login
-> JWT-compatible token
-> Roles
-> Static policies
```

## Auth Scope

Allowed for Phase 1:

- one authenticated reviewer session,
- JWT-compatible token shape or equivalent demo session,
- role claims,
- static RBAC policies,
- role-aware actions,
- basic access denial for unauthorized actions,
- no anonymous access to review actions.

Deferred:

- enterprise SSO,
- multiple OAuth providers,
- SCIM,
- SAML,
- organization-wide identity administration,
- tenant administration,
- fine-grained policy engine,
- cross-tenant access controls beyond conceptual safeguards.

## Minimum Roles

The MVP must support the meaning of these roles:

| Role | Required Meaning |
| --- | --- |
| `ADMIN` | Demo approver and local system operator for Phase 1. Represents CTO/VP Engineering authority in the controlled demo. |
| `PLATFORM_ENGINEER` | Reviews technical evidence, can defer for feasibility and mark implementation after approval. |
| `FINANCE` | Reviews cost evidence, ROI assumptions and validates realized result when available. |
| `AUDITOR` | Reviews ledger, evidence traceability and security/data-governance posture; read-only by default with optional audit/security deferral. |

`ADMIN` must not become a permanent enterprise business-approver concept. Future SSO identities should map into IMPERATOR roles and policies without changing the domain.

## Static Policy Matrix

| Policy | `ADMIN` | `PLATFORM_ENGINEER` | `FINANCE` | `AUDITOR` |
| --- | --- | --- | --- | --- |
| View case | Yes | Yes | Yes | Yes |
| View safe evidence summaries | Yes | Yes | Yes | Yes |
| View cost/ROI | Yes | Limited | Yes | Yes |
| Approve recommendation | Yes | No | No | No |
| Reject recommendation | Yes | No | No | No |
| Defer recommendation | Yes | Technical only | Cost only | Audit/security only if enabled |
| Mark implementation | No | Yes | No | No |
| Validate result | No | No | Yes | No |
| View ledger | Yes | Yes | Yes | Yes |
| Manage demo users/imports | Yes | No | No | No |

This is RBAC, not a full policy engine.

## Authorization Rules

Authorization must protect:

- approve,
- reject,
- defer,
- mark implementation,
- validate result,
- view restricted references,
- access AI explanation context,
- read ledger history containing sensitive references.

Unauthorized actions must fail without mutating the ledger.

## Data Security Rules

Phase 1 must protect against:

- raw prompt exposure,
- raw completion exposure,
- secret exposure,
- provider token exposure,
- API key exposure,
- customer conversation exposure,
- Restricted evidence exposure to AI,
- unknown-sensitivity evidence reaching AI,
- logs containing sensitive payloads.

Evidence summaries may carry references to sensitive material, but must not duplicate sensitive content into explanation, logs or broad UI surfaces.

## AI Security Rules

AI explanation must use prepared context only.

Before AI explanation:

1. evidence sensitivity must be known;
2. Restricted evidence must be excluded or summarized safely;
3. raw prompts and completions must be absent;
4. secrets and provider payloads must be absent;
5. the deterministic recommendation must already exist.

If the AI context fails these checks, explanation must be blocked and the case should remain reviewable without AI text if business rules allow it.

## Minimum Health Endpoints

Phase 1 should conceptually expose:

| Endpoint | Meaning |
| --- | --- |
| `/health` | Process is alive. |
| `/ready` | Minimum dependencies for demo flow are ready. |

No broad operational API surface is required.

## Minimum Metrics

Required metric categories:

- request count,
- error count,
- latency summary,
- evidence import success/failure,
- recommendation generated count,
- AI explanation success/failure,
- review action success/failure,
- ledger entry created count.

Metrics should help validate the MVP flow.

They do not need a full observability platform in Phase 1.

## Minimum Structured Logs

Required log categories:

| Category | Required Fields |
| --- | --- |
| request | timestamp, correlation ID, path or action, result. |
| error | timestamp, correlation ID, error category, safe message. |
| evidence | case ID, evidence ID, source domain, intake result. |
| recommendation | case ID, recommendation generated, risk, confidence. |
| ai_explanation | case ID, requested, succeeded, failed or blocked. |
| ledger | case ID, event type, actor role, result. |
| auth | actor role, action, authorized or denied. |

Logs must not contain:

- secrets,
- tokens,
- raw prompts,
- raw completions,
- provider payload dumps,
- Restricted evidence content,
- customer conversations.

## Correlation Rule

The same correlation ID should allow a reviewer or developer to trace:

```text
evidence intake
-> ROI/recommendation generation
-> AI explanation
-> review action
-> ledger entry
```

This is enough for Phase 1 traceability.

Distributed tracing infrastructure is deferred.

## Security Failure Behavior

| Failure | Required Behavior |
| --- | --- |
| Unauthorized action | Deny action; do not create approval ledger entry. |
| Unknown sensitivity | Block AI explanation and approval until reviewed. |
| Restricted evidence requested for AI | Exclude or block explanation. |
| Secret detected | Reject evidence context and record security issue. |
| Invalid role | Deny protected action. |
| Invalid transition | Deny action and keep ledger unchanged. |
| Logging risk | Log safe error category only. |

## Deferred Security And Observability

Deferred until after Phase 1:

- full enterprise SSO,
- SAML,
- SCIM,
- multi-provider OAuth configuration,
- full tenant administration,
- full OpenTelemetry deployment,
- Grafana dashboards,
- SIEM integration,
- alerting platform,
- centralized log aggregation,
- secrets rotation automation,
- full audit module outside the MVP ledger,
- compliance reporting.

These may be future platform capabilities, but they are not required to prove the first Decision ROI Case.

## Acceptance Mapping

This stage supports:

- `AC-01`: authenticated reviewer;
- `AC-08`: role-aware review action;
- `AC-09`: ledger entry authorization;
- `AC-13`: minimal health, readiness, logs and metrics;
- `AC-14`: sensitive data handling.

It also supports negative tests rejecting:

- anonymous approval,
- AI access to Restricted evidence,
- secret logging,
- raw prompt or completion logging,
- full SSO as Phase 1 prerequisite,
- full observability platform as Phase 1 prerequisite.

## Stage Pass Criteria

Stage 10 is complete when:

1. local/demo JWT-compatible RBAC auth is locked as Phase 1 minimum;
2. role meanings are clear;
3. protected actions are listed;
4. AI and evidence security checks are explicit;
5. `/health` and `/ready` are defined conceptually;
6. minimum metrics are listed;
7. structured log categories are listed;
8. full SSO and full observability platforms remain deferred.

## Stop Conditions

Stop and return to CTO Agent if any proposal requires:

- multiple OAuth providers before the demo,
- enterprise SSO before the demo,
- SCIM or SAML before the demo,
- full Grafana/OpenTelemetry stack as prerequisite,
- SIEM integration,
- broad tenant administration,
- policy engine implementation,
- logging secrets or raw AI data,
- exposing Restricted evidence to AI.

## Handoff To Stage 11

Stage 11 may use these controls to define the integrated demo acceptance record.

Stage 11 must verify that the MVP can be observed and reviewed without adding platform infrastructure.
