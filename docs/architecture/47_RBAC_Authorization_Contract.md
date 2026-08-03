# 47 - RBAC Authorization Contract

Status: **FROZEN**

Decision authority: **D088 - RBAC Authorization Contract**

Owning gate: **Sprint 3.9 - RBAC Authorization**

## 1. Purpose

This document freezes what an already authenticated IMPERATOR caller may do
through the certified D086 REST surface. It is the implementation contract for
Sprint 3.9 and answers one question only:

```text
What may this authenticated MVP role read or invoke?
```

Authentication remains governed by D087 and Document 46. Business authority,
state transitions and Ledger invariants remain governed by D083 and Document
44. This contract adds no route, role, business action or persistence concept.

## 2. Sprint 3.9.0 Baseline

The contract-freeze gate was opened from this clean baseline:

- branch: `main`;
- working tree: clean;
- baseline commit: `a073b624da3a2bad47ea3a0188d4d88d0e2f1bde`;
- baseline subject: `Sprint 3.8.1: synchronize project control documentation`;
- D087 commit: `0f9177898c0144e4a71c0a89b7fed39075703eb1`;
- Sprint 3.8: `CERTIFIED / COMPLETE`; and
- Sprint 3.8.1: `COMPLETE`.

SHA-256 baseline hashes recorded before D088 was appended:

| Authority | SHA-256 |
|---|---|
| `docs/architecture/44_Review_Ledger_Result_Validation_Contract.md` | `E4FC873298AD09C95FBCF51CB8AFD70F83DECB4AE760A8A31BFCE05AFDBB0C4C` |
| `docs/architecture/45_Functional_REST_Application_Contract.md` | `65F78475C1683BA5464932AFED919F431550B32EAA705347E75E198111FB5F3A` |
| `docs/architecture/46_JWT_Authentication_Contract.md` | `DE4CDD04505DB7D0DA5ECDA574DF79D0E4495EC2E06595B6DCF99A495AA940E3` |
| `docs/product/28_Identity_Access_Approval_Model.md` | `3D38292F5D1894601E7E93ACC8B9ECA02D0771CB757209DD4D645FC949A94F14` |
| `docs/architecture/26_Security_Data_Governance_Threat_Model.md` | `ED4671906114AD1743C778497504D9C54DFA9D1517D897ED671169F648377208` |
| `docs/decisions/14_Decision_Log.md` | `22311818372C07578382C75A7F67C213A212F92E00FDA1344F18A6D7A9FD9FB1` |

## 3. Authorities And Precedence

This contract specializes:

- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`;
- `docs/product/28_Identity_Access_Approval_Model.md`;
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- `docs/architecture/44_Review_Ledger_Result_Validation_Contract.md`;
- `docs/architecture/45_Functional_REST_Application_Contract.md`;
- `docs/architecture/46_JWT_Authentication_Contract.md`; and
- D083, D085, D086 and D087 in `docs/decisions/14_Decision_Log.md`.

D088 changes none of those authorities. It resolves only the RBAC questions
explicitly deferred by D087.

Where older product language says that an Auditor may defer a security issue
"if enabled", D083 controls the implemented MVP: `AUDITOR` is read-only.
Where older product language says that a generic Admin is not automatically a
business approver, D083 controls the reduced demo role while preserving the
existing identity check: `ADMIN` may reach approve or reject, but the caller's
validated subject must still be the Decision and Recommendation required
approver. Route permission never bypasses Application authority.

## 4. Frozen Role Model

The complete Sprint 3.9 role inventory is exactly:

```text
ADMIN
PLATFORM_ENGINEER
FINANCE
AUDITOR
```

The role source is the one validated `imperator_role` claim frozen by D087.

Rules:

1. One JWT represents exactly one role.
2. Role spelling and comparison are exact and case-sensitive after D087 claim
   validation.
3. There is no role hierarchy, inheritance or implication.
4. `ADMIN` does not inherit `PLATFORM_ENGINEER`, `FINANCE` or `AUDITOR`.
5. `PLATFORM_ENGINEER`, `FINANCE` and `AUDITOR` do not inherit one another.
6. Multiple role claims, arrays, delimited role strings and secondary role
   claims remain invalid authentication input under D087.
7. No group, scope, permission, tenant, organization or ownership claim grants
   authority in Sprint 3.9.
8. No role can delegate authority through an HTTP request.

The implementation may translate the one validated claim into one framework
authority, but framework authority naming is an adapter detail. It must not
create additional roles or implied permissions.

## 5. Authorization Principles

Sprint 3.9 follows these mandatory principles:

- authenticate first, authorize second;
- deny a recognized route/action when its explicit role grant is absent;
- preserve the exact D086 route and method inventory;
- keep business authority in Application and Domain;
- apply least privilege to Evidence import and governance commands;
- keep read access explicit rather than inherited from write access;
- filter Evidence before REST serialization;
- expose no Restricted content;
- perform no write, Ledger append or partial mutation after authorization
  denial; and
- add no speculative policy engine, database model or identity store.

Passing the route gate means only that the role may invoke the existing
Application boundary. It does not mean that the requested transition is valid,
that the caller is the required approver or that Evidence/readiness invariants
have passed.

## 6. Frozen D086 Route Authorization Matrix

`ALLOW` is an explicit role grant for that exact method and route. `DENY`
means an authenticated caller receives the route-level authorization failure
defined in Section 11 before the controller or Application input port runs.

| ID | Method and route | `ADMIN` | `PLATFORM_ENGINEER` | `FINANCE` | `AUDITOR` |
|---|---|---:|---:|---:|---:|
| R01 | `POST /api/v1/evidence/import` | ALLOW | DENY | DENY | DENY |
| R02 | `GET /api/v1/decisions` | ALLOW | ALLOW | ALLOW | ALLOW |
| R03 | `GET /api/v1/decisions/{id}` | ALLOW | ALLOW | ALLOW | ALLOW |
| R04 | `GET /api/v1/decisions/{id}/timeline` | ALLOW | ALLOW | ALLOW | ALLOW |
| R05 | `GET /api/v1/decisions/{id}/evidence` | ALLOW | ALLOW | ALLOW | ALLOW |
| R06 | `GET /api/v1/decisions/{id}/roi` | ALLOW | ALLOW | ALLOW | ALLOW |
| R07 | `GET /api/v1/recommendations/{id}` | ALLOW | ALLOW | ALLOW | ALLOW |
| R08 | `GET /api/v1/decisions/{id}/ledger` | ALLOW | ALLOW | ALLOW | ALLOW |
| R09 | `POST /api/v1/decisions/{id}/ledger/approve` | ALLOW | DENY | DENY | DENY |
| R10 | `POST /api/v1/decisions/{id}/ledger/reject` | ALLOW | DENY | DENY | DENY |
| R11 | `POST /api/v1/decisions/{id}/ledger/defer` | ALLOW | ALLOW | ALLOW | DENY |
| R12 | `POST /api/v1/decisions/{id}/ledger/mark-implemented` | DENY | ALLOW | DENY | DENY |
| R13 | `POST /api/v1/decisions/{id}/ledger/validate-result` | DENY | DENY | ALLOW | DENY |
| R14 | `GET /api/v1/business-value?decisionId={id}` | ALLOW | ALLOW | ALLOW | ALLOW |
| R15 | `GET /api/v1/ledger` | ALLOW | ALLOW | ALLOW | ALLOW |

This is the only Sprint 3.9 route matrix. Query parameters do not alter role
authority. Path IDs do not create ownership authority. All object existence,
pagination, state and business checks remain downstream behavior.

## 7. HTTP Method Contract

The method inventory is frozen as follows:

| Method | Sprint 3.9 authorization behavior |
|---|---|
| `GET` | Granted only for R02-R08 and R14-R15 according to Section 6. |
| `POST` | Granted only for R01 and R09-R13 according to Section 6. |
| `PATCH` | No D086 endpoint exists; no role receives a grant. |
| `PUT` | No D086 endpoint exists; no role receives a grant. |
| `DELETE` | No D086 endpoint exists; no role receives a grant. |
| `HEAD` | No independently authorized D086 endpoint exists. |
| `OPTIONS` | No product capability or CORS policy is introduced. |
| `TRACE` | No product capability is authorized. |

After successful authentication, an unsupported method on a known D086 path
must preserve D086 `405 METHOD_NOT_ALLOWED` behavior. An unknown route must
preserve D086 `404` behavior. Missing or invalid authentication still produces
the D087 `401` before route resolution.

The `404`/`405` preservation rule is not a role grant. No unlisted handler may
be added or deployed. Any future route or method requires a new accepted
contract and route-inventory update; it acquires no permission from this
matrix.

## 8. D083 Governance Preservation

The route matrix preserves exactly this Application authority:

| Governance fact | `ADMIN` | `PLATFORM_ENGINEER` | `FINANCE` | `AUDITOR` |
|---|---:|---:|---:|---:|
| Approve | ALLOW | DENY | DENY | DENY |
| Reject | ALLOW | DENY | DENY | DENY |
| Defer | ALLOW | ALLOW | ALLOW | DENY |
| `implementation_marked` | DENY | ALLOW | DENY | DENY |
| `result_validated` | DENY | DENY | ALLOW | DENY |

Mandatory downstream checks remain unchanged:

- approve/reject requires the authenticated `sub` to equal both frozen
  required-approver identities;
- transition, readiness, Evidence, snapshot and Recommendation invariants
  remain mandatory;
- defer still requires exactly one frozen follow-up form;
- implementation still requires a prior approved Decision and implementation
  Evidence;
- result validation still requires implementation history, post-action
  Evidence and deterministic cost inputs;
- idempotency and concurrency semantics remain those certified under D083; and
- every successful governance fact retains the JWT-derived actor UUID and role.

RBAC cannot create, rewrite or weaken these business rules.

## 9. Read Visibility Contract

The four MVP roles are internal participants in the one `DRC-AOA-001` slice.
Their read grants are explicit:

| Information | Authorized roles | Representation rule |
|---|---|---|
| Decision summaries and detail | All four roles | Exact D086 Decision projections. |
| Recommendation | All four roles | Exact deterministic D086 Recommendation projection. |
| Decision timeline | All four roles | Exact D086 timeline, with Evidence-derived items filtered by Section 10. |
| Decision ROI | All four roles | Exact D086 ROI projection; no HTTP financial input. |
| Per-Decision Ledger | All four roles | Ordered D086 Ledger projection. |
| Cross-Decision Ledger | All four roles | Paged D086 projection; the MVP remains single-tenant and single-case bounded. |
| Business Value | All four roles | Existing validated, non-persisted D086 projection only. |
| Public and Internal Evidence summaries | All four roles | Full normalized D086 summary; no raw payload or persistence metadata. |
| Confidential Evidence summaries | Role-dependent | Section 10 is authoritative. |
| Restricted Evidence content | No role | Redacted fail-closed representation only if invalid persisted data is encountered. |
| Business Decision audit information | All four roles | Ledger facts, chain references, actor, role, reason and already-safe metadata. |
| Authentication/security audit records | No role through D086 | No route exists; Sprint 3.9 adds none. |
| Raw provider payloads | No role | Not stored and never returned. |
| Persistence metadata | No role | Excluded by D086 and never returned. |

Read access never implies command access. In particular, `AUDITOR` may read
Decision, Recommendation, Evidence, ROI, Ledger and Business Value views but
cannot invoke any D086 POST route.

## 10. Evidence Sensitivity And Redaction

### 10.1 Common rules

Evidence authorization uses the persisted normalized `sensitivity` and
`evidenceType`. Unknown sensitivity is treated as `CONFIDENTIAL`. A missing,
unknown or non-canonical Evidence type never broadens access.

No role receives:

- `rawPayload` content;
- raw prompts, completions or customer conversations;
- secrets, credentials or provider tokens;
- persistence metadata; or
- the Domain Evidence `metadata` map through R05.

The existing D086 Evidence summary remains the maximum possible response.

### 10.2 Public and Internal

All four roles receive the complete normalized D086 Evidence summary for
`PUBLIC` and `INTERNAL` Evidence. Existing no-raw-payload and no-persistence-
metadata rules still apply.

### 10.3 Confidential

| Role | Confidential Evidence behavior |
|---|---|
| `ADMIN` | Full normalized D086 summary for the controlled demo approver/operator context. |
| `PLATFORM_ENGINEER` | Full normalized D086 summary for the technical, implementation, cost and ownership context of `DRC-AOA-001`. |
| `FINANCE` | Full normalized summary for `business_context`, `cloud_cost`, `cloud_utilization`, `ai_consumption`, `ai_quality_review`, `usage_value_signal`, `owner_approval` and `roi_assumption`; redacted summary for `code_deployment`, unknown types and `ai_sensitive_payload_rejected`. |
| `AUDITOR` | Full normalized D086 summary in read-only mode for evidence-lineage, security and governance audit. |

This grant is limited to the current single-case, single-tenant MVP. It does
not establish a future enterprise entitlement or cross-tenant policy.

### 10.4 Redacted Confidential representation

A redacted D086 Evidence summary preserves only:

- `evidenceId`;
- `timestamp`;
- `source`;
- `sourceType`;
- `eventType`;
- `severity`;
- `evidenceType`;
- `sensitivity`;
- `confidence`; and
- `reviewStatus`.

The following fields contain the exact display value `[REDACTED]`:

- `sourceObjectRef`;
- `entity`;
- `actor`;
- `observedFact`;
- `businessMeaning`; and
- `correlationKey`.

No response field is added. The placeholder preserves the frozen D086 DTO
shape and makes hidden Evidence explicit.

### 10.5 Restricted fail-closed representation

`RESTRICTED` Evidence remains prohibited from MVP ingestion and storage. If a
persisted row is nevertheless encountered, no role receives its content.

The response preserves only `evidenceId`, `timestamp`, `sensitivity` and
`reviewStatus`. Every other textual Evidence-summary field uses
`[REDACTED]`; severity and confidence remain their normalized labels. The item
remains in ordering and pagination so the API does not falsely imply that no
Evidence exists.

This behavior does not repair, mutate or delete the persisted row and does not
authorize schema, encryption or retention work.

### 10.6 Related read projections

Evidence identifiers remain visible in Decision, Recommendation, ROI and
Ledger projections because they preserve traceability and reveal no Evidence
content by themselves.

For a timeline item derived from Evidence that is redacted for the caller:

- `type`, `referenceId`, `occurredAt`, `confidenceLabel` and ordered
  `evidenceIds` remain visible;
- `summary` is `[REDACTED]`; and
- optional `source` and `actor` are absent.

Decision, Recommendation and Ledger facts are not silently removed. Display
redaction never rewrites stored Evidence or immutable Ledger history.

## 11. Authorization Failure Contract

Authorization happens after successful D087 authentication and before a
denied controller or Application boundary can execute.

Frozen route-level denial:

| Condition | HTTP | Envelope code | Safe message |
|---|---:|---|---|
| Valid JWT, recognized D086 route and method, role is `DENY` in Section 6 | 403 | `ACCESS_DENIED` | `Access denied` |

The response uses the exact D075/D086 four-field JSON envelope:

```text
code
message
correlationId
details
```

Rules:

- `details` is a non-null empty object;
- content type is `application/json` for every `Accept` value;
- `X-Correlation-ID` is present and equals the body `correlationId`;
- no `WWW-Authenticate` header is added to a valid-token `403`;
- the response exposes no required role, actual role, token claim, object
  existence, stack trace, matcher or policy detail; and
- the denied request produces no persistence or Ledger effect.

An authenticated caller that passes the route gate but fails an existing
Application authority or business rule keeps the existing Application code
and safe message, including `GOVERNANCE_ACTION_FORBIDDEN`. Route-level RBAC
must not replace that downstream distinction.

## 12. Enforcement Boundary

Sprint 3.9 implementation must enforce:

1. route and HTTP-method grants at the API security boundary;
2. Evidence redaction before any REST response is serialized;
3. unchanged Application governance checks after an allowed command reaches
   its input port; and
4. unchanged Domain, transaction and persistence invariants.

Spring Security and Servlet types remain confined to API/security and
bootstrap adapter code. Domain, Application and Ports remain independent of
JWT, Spring Security and HTTP.

The exact Spring matcher API, authorization class names and internal policy
object shape are deliberately deferred to Sprint 3.9 implementation review.
The implementation must realize this behavior without changing the contract.

## 13. Explicit Exclusions

Sprint 3.9.0 and D088 do not authorize:

- Java, tests, SQL, Flyway or dependency changes;
- Spring Security runtime configuration;
- an `AuthorizationManager` or custom policy engine;
- new or changed routes, methods, DTO fields or query parameters;
- Domain, Application, Port, repository or transaction changes;
- role hierarchy, multiple roles, scopes, groups or permission claims;
- users, role tables, identity persistence or an Authorization Server;
- tenant, organization, ownership or assignment authorization;
- OAuth login, SSO-provider selection or token issuance;
- database encryption, row-level security or schema changes;
- live connectors, frontend work or external exposure;
- security-audit persistence or a security-audit API; or
- real customer data or Pilot Readiness.

## 14. Sprint 3.9 Implementation Boundary

After Sprint 3.9.0 is accepted, Sprint 3.9 may implement only what is required
to realize this contract:

- one-role authority translation from the already validated D087 claim;
- the exact Section 6 route/method policy;
- the Section 10 read redaction behavior;
- the Section 11 access-denied response; and
- focused authorization, redaction and non-mutation tests.

Any need to change Domain, Application contracts, Ports, D086 DTO schemas,
PostgreSQL V1, Flyway, dependencies or the route topology is a contradiction
and requires a new explicit architecture decision before work continues.

## 15. Certification Contract

Sprint 3.9 cannot be certified unless all of the following pass:

1. every one of the 15 D086 routes is tested for all four roles;
2. all matrix `ALLOW` outcomes reach the existing behavior;
3. all matrix `DENY` outcomes return the exact route-level `403` contract;
4. missing and invalid credentials retain D087 `401` behavior;
5. authenticated unknown routes retain `404` and unsupported methods retain
   `405`;
6. D083 required-approver and governance checks remain active after route
   authorization;
7. denied commands create no Decision transition or Ledger entry;
8. Evidence visibility and redaction pass for every sensitivity and role;
9. raw payload, Restricted content and persistence metadata are never exposed;
10. all baseline tests remain green and focused Sprint 3.9 tests pass;
11. Java 21 `clean verify` produces the executable JAR;
12. PostgreSQL 18.2, Flyway validation and the full integration profile pass;
13. Domain Isolation Index remains 100%;
14. no route, DTO, schema, migration, dependency or business rule drifts; and
15. Sprint 3.9.1 synchronizes active documentation only after certification.

## 16. Contract Freeze Gate

Sprint 3.9.0 is accepted only when:

- one complete route matrix covers all D086 endpoints;
- GET and POST grants are exact and all other methods are resolved;
- all four roles have explicit, non-inherited permissions;
- D083 governance is preserved without expansion;
- every MVP read resource has explicit visibility;
- Confidential and Restricted Evidence behavior is deterministic;
- route-level and Application-level `403` behavior is distinguishable;
- implementation details remain deferred;
- no runtime, Java, tests, SQL, dependencies or previous decision changed; and
- D088 records this contract in the Decision Log.

When these conditions pass, Sprint 3.9 implementation is the sole next gate.
