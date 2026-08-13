# 01 - Controlled Environment And Evidence Register

Status: **PREPARATION TEMPLATE / NO CUSTOMER DATA / NO RUNTIME CHANGE**

## 1. Environment Boundary

The proposed pilot uses one dedicated environment for one organization and one
case. It is not a shared SaaS tenant model.

```text
External Keycloak over HTTPS
        |
        | RS256 access tokens
        v
Dedicated pilot workstation/host
  -> browser on loopback
  -> IMPERATOR frontend on loopback
  -> backend and PostgreSQL on private Docker network
        |
        +-> outbound read-only GitHub
        +-> outbound read-only AWS
        +-> approved normalized manual imports
```

The IMPERATOR runtime remains non-public. Remote browser access, reverse proxy,
VPN exposure or public DNS for IMPERATOR requires a separate contract. Only
the external Keycloak issuer requires trusted HTTPS and DNS in this plan.

## 2. Environment Worksheet

Complete this outside Git before pilot authorization:

| Field | Required value | Status |
|---|---|---|
| Customer organization legal name | One organization | `UNASSIGNED` |
| Executive Sponsor | Named CTO/VP Engineering | `UNASSIGNED` |
| Business Owner | Named workflow owner | `UNASSIGNED` |
| Platform Owner | Named engineer | `UNASSIGNED` |
| Finance/FinOps reviewer | Named reviewer | `UNASSIGNED` |
| Security/privacy contact | Named contact | `UNASSIGNED` |
| Dedicated host owner | Customer or IMPERATOR, explicitly agreed | `UNASSIGNED` |
| Hosting country/region | Exact location | `UNASSIGNED` |
| Pilot start/end | Proposed 30-day window | `UNASSIGNED` |
| Measurement extension | Exact additional period or none | `UNASSIGNED` |
| Support timezone | Proposed `Europe/Madrid` | `TO AGREE` |
| Data-deletion date | Contractual date | `UNASSIGNED` |

No environment may be provisioned from this template while D092 is blocked.

## 3. Keycloak Realm Worksheet

Authority: `docs/architecture/53_Keycloak_Pilot_Identity_Integration_Preparation.md`.

| Item | Prepared value | Status |
|---|---|---|
| Realm | `imperator-pilot` | `DEFINED / NOT PROVISIONED` |
| Issuer | `https://<keycloak-host>/realms/imperator-pilot` | `UNASSIGNED` |
| Discovery | `{issuer}/.well-known/openid-configuration` | `UNASSIGNED` |
| JWKS | `{issuer}/protocol/openid-connect/certs` | `UNASSIGNED` |
| Signing | `RS256`, mandatory `kid` | `DEFINED` |
| API audience | Exact `imperator-api` | `DEFINED` |
| Role claim | Exact single String `imperator_role` | `DEFINED` |
| Human client flow | Authorization Code + PKCE `S256` | `DEFINED / NOT PROVISIONED` |
| TLS certificate owner/expiry | External IdP operator | `UNASSIGNED` |
| Token lifetime | Short-lived, exact value to agree | `UNASSIGNED` |
| Realm backup/operator | External IdP responsibility | `UNASSIGNED` |

Pilot identities:

| Identity | Claim value | Named person | Provisioned | Conformance |
|---|---|---|---|---|
| Pilot Admin | `ADMIN` | `UNASSIGNED` | No | Not tested |
| Pilot Platform Engineer | `PLATFORM_ENGINEER` | `UNASSIGNED` | No | Not tested |
| Pilot Finance | `FINANCE` | `UNASSIGNED` | No | Not tested |
| Pilot Auditor | `AUDITOR` | `UNASSIGNED` | No | Not tested |

Each user receives one claim only. Names, email addresses, UUID subjects,
passwords and tokens remain in the controlled IdP/customer register, never in
this repository.

## 4. GitHub Boundary

Authority: D089.

| Item | Requirement | Status |
|---|---|---|
| Organization | Exactly one | `UNASSIGNED` |
| Repository | Exactly one private repository | `UNASSIGNED` |
| Branch | Discovered default branch only | `UNASSIGNED` |
| Correlation | Exact bounded `IMP-214` token for rehearsal | `DEFINED` |
| Authentication | Fine-grained token, selected repository only | `NOT PROVISIONED` |
| Permission | Pull requests read-only plus exact D089 metadata surface | `NOT REVIEWED` |
| Secret owner | Customer repository/security owner | `UNASSIGNED` |
| Expiry/revocation | Exact date and owner | `UNASSIGNED` |
| Live sandbox check | Non-customer or separately authorized scope | `NOT EXECUTED` |

The connector never reads source contents, diffs, comments, secrets, members
or write APIs. A GitHub App remains future work.

## 5. AWS Boundary

Authority: D090.

| Item | Requirement | Status |
|---|---|---|
| Partition | Commercial `aws` only | `DEFINED` |
| Account | Exactly one 12-digit account | `UNASSIGNED` |
| Workload Region | Exactly one | `UNASSIGNED` |
| Cost endpoint | `us-east-1` | `DEFINED` |
| Resource scope | Exact `onboarding-assistant-prod` tags | `TO MAP` |
| Authentication | Temporary SDK provider-chain credentials | `NOT PROVISIONED` |
| Operations | STS identity, cost, tags and CloudWatch reads only | `DEFINED` |
| IAM policy owner | Customer cloud/security owner | `UNASSIGNED` |
| Credential expiry/revocation | External operator responsibility | `UNASSIGNED` |
| Live sandbox check | Separate non-customer/authorized proof | `NOT EXECUTED` |

No static credential belongs in IMPERATOR settings, Git, Evidence or logs.

## 6. Evidence Collection Rule

Every item must have:

- one accountable customer source owner;
- one approved source object or aggregate export;
- a period/date and freshness decision;
- `Internal` or `Confidential` sensitivity;
- raw payload mode `not_stored`;
- no Restricted data; and
- customer approval before normalized import.

The conceptual references are frozen by D082. Customer source object IDs and
values may not silently replace the canonical rehearsal policy. Such
substitution requires separate pilot authorization and explicit economic
validation.

## 7. Thirty-Evidence Ownership Register

| Ref | Source path | Accountable owner | Collection/normalization | Sensitivity | Current status |
|---|---|---|---|---|---|
| `E-JIRA-001` | Jira issue or approved decision record | Business Owner | Approved normalized export/manual R01 | Internal | Template only |
| `E-JIRA-002` | Jira project/status history | Business Owner | Approved normalized export/manual R01 | Internal | Template only |
| `E-JIRA-003` | Jira owner field | Business Owner | Minimized owner summary | Confidential | Template only |
| `E-JIRA-004` | Business value-boundary note | Business Owner | Signed manual summary | Internal | Template only |
| `E-GH-001` | Qualifying PR | Platform Owner | D089 connector or approved export | Confidential | Sandbox pending |
| `E-GH-002` | Qualifying approval review | Platform Owner | D089 connector or approved export | Confidential | Sandbox pending |
| `E-GH-003` | Production deployment status | Platform Owner | D089 connector or approved export | Internal | Sandbox pending |
| `E-GH-004` | Model-change feasibility review | Platform Owner | Signed manual summary | Confidential | Template only |
| `E-AWS-001` | Closed-month Cost Explorer total | Finance/FinOps | D090 connector aggregate | Confidential | Sandbox pending |
| `E-AWS-002` | Exact approved tags/resource fingerprint | Platform Owner | D090 connector aggregate | Confidential | Sandbox pending |
| `E-AWS-003` | Bounded Lambda utilization | Platform Owner | D090 connector aggregate | Confidential | Sandbox pending |
| `E-AWS-004` | Platform ownership mapping | Platform Owner | D090 connector aggregate | Confidential | Sandbox pending |
| `E-AI-001` | Aggregate AI monthly cost | Finance/AI Platform Owner | Approved aggregate export/manual R01 | Confidential | No connector |
| `E-AI-002` | Aggregate model mix | AI Platform Owner | Approved aggregate export/manual R01 | Confidential | No connector |
| `E-AI-003` | Aggregate request/token workload shape | AI Platform Owner | Approved aggregate export/manual R01 | Confidential | No connector |
| `E-AI-004` | Application/team attribution | AI Platform Owner | Approved minimized summary | Confidential | No connector |
| `E-AI-005` | Quality review | Business + Platform Owner | Signed sampled summary, no conversations | Confidential | Template only |
| `E-USAGE-001` | Aggregate active-user signal | Product/Business Owner | Approved aggregate export | Confidential | Template only |
| `E-USAGE-002` | Aggregate usage trend | Product/Business Owner | Approved aggregate export | Confidential | Template only |
| `E-USAGE-003` | Accepted value proxy | Business + Finance | Signed manual summary | Internal | Template only |
| `E-OWNER-001` | Business ownership confirmation | Business Owner | Signed manual summary | Confidential | Template only |
| `E-OWNER-002` | Technical ownership confirmation | Platform Owner | Signed manual summary | Confidential | Template only |
| `E-OWNER-003` | Approval path | Executive Sponsor | Signed governance summary | Internal | Template only |
| `A-ROI-001` | Projected monthly cost | Finance/FinOps | Signed EUR assumption | Confidential | Template only |
| `A-ROI-002` | High-capability fallback | Platform + Business | Signed quality/risk assumption | Internal | Template only |
| `A-ROI-003` | Transition cost | Finance/FinOps | Signed EUR assumption | Confidential | Template only |
| `A-ROI-004` | Representative review period | Finance/FinOps | Signed period assumption | Internal | Template only |
| `DRC-AOA-001-v1` | Policy provenance | IMPERATOR Product Guardian | Frozen policy reference | Internal | Defined |
| Evidence 29 | Approved implementation/deployment fact | Platform Owner | Normalized postdecision import | Internal | Future runtime |
| Evidence 30 | Post-action cost/result fact | Finance/FinOps | Normalized postdecision import | Confidential | Future runtime |

## 8. Secret And Access Register

The controlled register is stored outside Git and records only ownership and
lifecycle here:

| Secret/access item | Storage authority | Repository value allowed? |
|---|---|---|
| Keycloak admin credential | External IdP secret manager | No |
| Pilot user password/MFA recovery | External IdP | No |
| JWT access token | Volatile user session only | No |
| GitHub fine-grained token | Approved external secret boundary | No |
| AWS temporary credentials/profile | AWS SDK/external operator | No |
| PostgreSQL owner/app passwords | Docker secret files on dedicated host | No |
| Backup encryption key | External secret manager, separate from backup | No |
| TLS private key | External Keycloak/TLS operator | No |

Every access item needs owner, issue date, expiry, last review and revocation
evidence before the pilot begins.

## 9. Environment GO Gate

`GO` requires every worksheet row to be resolved and all of the following:

- D092 and D093 runtime gates pass;
- Keycloak conformance passes without weakening D087;
- connector policies are reviewed as read-only;
- the exact 30-item rehearsal pack is approved;
- real customer substitution remains disabled unless separately authorized;
- no Restricted data is present;
- backup/restore and deletion are tested; and
- commercial, security/privacy and support contacts sign the pilot boundary.

