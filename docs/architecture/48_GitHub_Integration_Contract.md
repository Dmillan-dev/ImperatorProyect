# 48 - GitHub Integration Contract

Status: **FROZEN**

Decision authority: **D089 - GitHub Integration Contract**

Lifecycle phase: **Phase 3 - First Business Value Loop**

Owning gate: **Sprint 4.0 - GitHub Integration Contract Freeze**

## 1. Purpose

This document freezes the first external connector contract for IMPERATOR.
It answers one implementation question:

```text
How does one bounded GitHub source produce trustworthy Code and Deployment
Evidence for DRC-AOA-001 without acquiring domain, ROI or governance authority?
```

Sprint 4.0 creates no runtime behavior. The later Sprint 4 implementation may
realize only this contract.

## 2. Lifecycle And Scope Correction

The lifecycle phase remains **Phase 3 - First Business Value Loop**. `4.0` is
the sprint number frozen by D085; it does not open a new lifecycle Phase 4.

The Phase 0 connector documents describe four evidence domains. D085 now
authorizes only the GitHub gate. Jira, AWS and AI-provider connector
implementation remain outside this sprint.

The contract specializes the earlier conceptual documents without changing
their domain language, the D086 route inventory, D087 identity, D088 RBAC,
PostgreSQL V1 or the deterministic `DRC-AOA-001-v1` policy.

To preserve D085 numbering, the GitHub delivery sequence is:

```text
Sprint 4.0 contract freeze
-> Sprint 4.0 implementation
-> Sprint 4.0 runtime certification
-> Sprint 4.0.1 documentation synchronization
```

`Sprint 4.1` remains **AWS Integration**. D089 does not renumber or displace
that gate.

## 3. Sprint 4.0 Baseline

The contract-freeze gate was opened from this clean baseline:

- branch: `main`;
- working tree: clean;
- baseline commit: `980e6343ec5c6f2d8d8e435c1ecfdf5dc0ecf0d6`;
- baseline subject: `Sprint 3.9.1: synchronize project control documentation`;
- Sprint 3.9: `CERTIFIED / COMPLETE`;
- Sprint 3.9.1: `COMPLETE`;
- Java 21 default tests: 111 passed, 0 failed, 0 errors, 0 skipped; and
- PostgreSQL integration tests: 29 passed, 0 failed, 0 errors, 0 skipped.

SHA-256 baseline hashes recorded before D089 was appended:

| Authority | SHA-256 |
|---|---|
| `docs/architecture/28_Per_Connector_MVP_Contracts.md` | `262BC3E1A5614308FC7704393965BAD047EC0EC399A19BC9529BBC47FC44B66E` |
| `docs/architecture/29_Event_Evidence_Vocabulary.md` | `4F4E9ED143609A895C484A0E1ADD2CAFAD7FEE75E3EB001B29B107AA311E3505` |
| `docs/architecture/CONNECTOR_FRAMEWORK.md` | `25ECED62B72C7145C4487325CEF119FEC0D5A85284A557DE563BC195AFF7D62A` |
| `docs/architecture/26_Security_Data_Governance_Threat_Model.md` | `ED4671906114AD1743C778497504D9C54DFA9D1517D897ED671169F648377208` |
| `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` | `DDDA05F78E622ECED908BE6549ED3F8F26923137C81BE0DBBFE802EDA25D1086` |
| `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md` | `68F77671A571B9434DE9FA5EF1694DF89B31D236796FA5880BCF654F96F2E0CB` |
| `docs/architecture/47_RBAC_Authorization_Contract.md` | `15E9204AF809E0C13B0F8EF307BD93667699C300F9ED9FD48D12CA6DBB274F2D` |
| `docs/decisions/14_Decision_Log.md` | `D25B1D78BE0AE2278FFB72BAF73F8B21F9462782E17CB8AA6158435582E5A7BA` |

## 4. Authorities And Precedence

This contract specializes:

- `docs/architecture/28_Per_Connector_MVP_Contracts.md`;
- `docs/architecture/29_Event_Evidence_Vocabulary.md`;
- `docs/architecture/CONNECTOR_FRAMEWORK.md`;
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`;
- `docs/architecture/27_Quality_Attributes.md`;
- `docs/product/24_MVP_Vertical_Slice.md`;
- `docs/product/25_MVP_ROI_Slice.md`;
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`;
- `docs/product/CORE_DOMAIN_MODEL.md`;
- `docs/product/API_SPECIFICATION.md`;
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md`;
- `docs/architecture/45_Functional_REST_Application_Contract.md`;
- `docs/architecture/47_RBAC_Authorization_Contract.md`; and
- D080, D082, D085, D086, D087 and D088 in
  `docs/decisions/14_Decision_Log.md`.

This contract resolves GitHub-specific implementation choices only. It cannot
weaken a higher-order domain, security, ROI, route or persistence invariant.

## 5. External Normative References

The outbound adapter must follow the official GitHub REST documentation:

- [REST API versions](https://docs.github.com/en/rest/about-the-rest-api/api-versions);
- [fine-grained token permissions](https://docs.github.com/en/rest/authentication/permissions-required-for-fine-grained-personal-access-tokens);
- [REST pagination](https://docs.github.com/en/rest/using-the-rest-api/using-pagination-in-the-rest-api);
- [REST rate limits](https://docs.github.com/en/rest/using-the-rest-api/rate-limits-for-the-rest-api);
- [REST integration best practices](https://docs.github.com/en/rest/using-the-rest-api/best-practices-for-using-the-rest-api);
- [pull request endpoints](https://docs.github.com/en/rest/pulls/pulls);
- [pull request review endpoints](https://docs.github.com/en/rest/pulls/reviews);
- [deployment endpoints](https://docs.github.com/en/rest/deployments/deployments).

GitHub documentation controls provider protocol facts. D089 controls how those
facts may enter IMPERATOR.

## 6. Frozen MVP Summary

| Area | Frozen value |
|---|---|
| Connector | GitHub.com only |
| Protocol | GitHub REST API over HTTPS |
| Authentication | One fine-grained personal access token |
| Organization | Exactly one configured organization |
| Repository | Exactly one selected repository |
| Branch | The repository's one discovered default branch |
| Environment | Exact deployment environment `production` |
| Case | `DRC-AOA-001` only |
| Source correlation token | Exact Jira key `IMP-214` |
| Entity | `ai-onboarding-assistant` |
| Synchronization | Explicit, synchronous, on-demand polling |
| Concurrency | One synchronization at a time |
| API version | `2026-03-10` |
| Automatic Evidence | `E-GH-001`, `E-GH-002`, `E-GH-003` |
| Manual Evidence | `E-GH-004` remains manual |
| Decision generation | None |
| Recommendation generation | None |
| Business Value generation | None |
| Public IMPERATOR route | None added |
| Persistence | Existing normalized Evidence only |
| Durable connector cursor | None in Sprint 4 |

## 7. Product Outcome

The integration exists to replace the manually prepared GitHub portion of the
first evidence pack with source-backed facts.

The successful product outcome is:

```text
One merged, correlated pull request
-> one accepted implementation Evidence
-> one accepted review Evidence
-> one accepted production deployment Evidence
-> existing DRC-AOA-001 traceability improves
```

The connector quality KPI is:

```text
GitHub Evidence Completeness = accepted E-GH-001/002/003 references / 3
```

The target is `3/3`. This is a connector-quality KPI, not ROI, savings or
Business Value. `E-GH-004` remains the approved manual technical-feasibility
note required by D082.

## 8. Authentication Contract

### 8.1 Selected model

Sprint 4 uses one externally supplied **fine-grained personal access token**.
The token must:

- be owned by an authorized technical operator;
- target the one configured organization;
- be restricted to the one configured repository;
- be approved by the organization when organization policy requires it;
- grant repository `Metadata: read`;
- grant repository `Pull requests: read`;
- grant repository `Deployments: read`; and
- grant no write or organization-administration permission.

The adapter sends it only as:

```text
Authorization: Bearer <token>
```

### 8.2 Why this model is selected

The fine-grained token is the smallest reversible MVP mechanism that supports
a private organization repository, explicit repository selection and
read-only endpoint permissions without building installation, callback, login
or tenant lifecycle behavior.

It is an MVP credential mechanism, not the target enterprise installation
model. Moving later to a GitHub App must replace only authentication and
configuration adapters; it must not change Evidence, Decision, ROI or Ledger.

### 8.3 Runtime configuration

The later implementation may read exactly these external settings:

| Setting | Rule |
|---|---|
| `IMPERATOR_GITHUB_ENABLED` | Defaults to `false`; GitHub calls are impossible unless exactly `true`. |
| `IMPERATOR_GITHUB_TOKEN` | Required secret when enabled; never persisted or logged. |
| `IMPERATOR_GITHUB_ORGANIZATION` | Required exact organization login. |
| `IMPERATOR_GITHUB_REPOSITORY` | Required exact repository name. |

Production API base URL is fixed to `https://api.github.com`. An injected local
base URL is permitted in test scope only. Case, source token, entity and
deployment environment are frozen product constants in Section 6, not
multi-case configuration.

Missing, blank or list-valued organization/repository configuration fails
closed before any Evidence is created. Token kind, selected-repository scope
and absence of extra grants are provisioning controls verified when the token
is issued and in the authorized sandbox smoke check; the runtime must not claim
that GitHub exposes enough information to prove those properties. Automated
secret rotation is deferred. Replacing the external token and restarting the
controlled runtime is sufficient for this MVP boundary.

### 8.4 Rejected authentication models

| Model | Decision |
|---|---|
| Personal access token (classic) | Rejected because broad `repo` scope is not least privilege. |
| GitHub App | Deferred; correct future enterprise direction but installation and key lifecycle exceed this gate. |
| OAuth App or OAuth Login | Rejected; user login is not connector authorization. |
| GitHub as IMPERATOR identity provider | Rejected; D087 owns API identity and D089 must not alter it. |
| Unauthenticated API access | Rejected; it cannot support the private-repository contract. |
| SSH key or deploy key | Rejected; the connector does not clone or write repository content. |
| Username/password | Rejected and unsupported. |
| GitHub Actions token | Rejected; connector lifetime must not depend on one workflow run. |

## 9. Organization, Repository And Branch Scope

1. The configured repository owner must be the one configured organization.
2. Repository discovery and organization-wide enumeration are forbidden.
3. The connector validates the exact repository through
   `GET /repos/{owner}/{repo}`.
4. The repository response must confirm the configured owner login and owner
   type `Organization`, and supplies one `default_branch`.
5. Only pull requests whose base branch equals that default branch qualify.
6. Head branch names may be inspected only as correlation candidates attached
   to a qualifying pull request. They are not independently enumerated.
7. Fork discovery, submodules, tags, releases and additional repositories are
   outside scope.
8. Source code, file contents, diffs, patches and blobs are never requested.
9. Only deployment environment `production` qualifies for `E-GH-003`.
10. GitHub Enterprise Server and custom API hosts are deferred.

## 10. Allowed GitHub REST Surface

The adapter may issue only HTTPS `GET` requests to this endpoint inventory:

| Purpose | Endpoint |
|---|---|
| Validate repository and discover default branch | `GET /repos/{owner}/{repo}` |
| List closed pull requests into the default branch | `GET /repos/{owner}/{repo}/pulls` |
| Read one qualifying pull request | `GET /repos/{owner}/{repo}/pulls/{pull_number}` |
| Read review metadata | `GET /repos/{owner}/{repo}/pulls/{pull_number}/reviews` |
| Read commit metadata for the qualifying PR | `GET /repos/{owner}/{repo}/pulls/{pull_number}/commits` |
| Read deployments for the qualifying merge SHA | `GET /repos/{owner}/{repo}/deployments` |
| Read deployment status history | `GET /repos/{owner}/{repo}/deployments/{deployment_id}/statuses` |

Collection queries are fixed as follows:

- pull requests: `state=closed`, `base=<default_branch>`, `sort=updated`,
  `direction=desc` and `per_page=100`;
- pull-request reviews and commits: `per_page=100`;
- deployments: `sha=<merge_sha>`, `environment=production` and
  `per_page=100`; and
- deployment statuses: `per_page=100`.

Every request includes:

```text
Accept: application/vnd.github+json
X-GitHub-Api-Version: 2026-03-10
User-Agent: IMPERATOR/<build-version>
Authorization: Bearer <token>
```

No unversioned request or silent fallback is permitted. HTTP `410` means
`API_VERSION_UNSUPPORTED` and stops the run. A future version upgrade requires
an explicit contract review and provider contract tests.

Redirects are followed at most three times, only to the same configured API
origin and without changing `GET`. Production redirects must remain HTTPS.
Cross-origin redirects and HTTPS downgrades fail closed so the bearer token is
never forwarded to another authority.

Forbidden endpoints include comments, source contents, diffs, patches,
Actions, workflows, checks, security alerts, secrets, organization members,
collaborators and all mutating operations.

## 11. Source Object Selection

### 11.1 Qualifying pull request

A pull request qualifies only when all conditions hold:

- state is closed;
- `merged_at` is present;
- base branch equals the discovered default branch;
- owner and repository equal the configured boundary;
- `merged_at` precedes the run's fixed `untilExclusive`; and
- exact token `IMP-214` appears in the PR title, PR body or head branch name.

Correlation matching is case-sensitive and token-bounded. Text such as
`IMP-2140` does not match. PR text is untrusted input: only the correlation
match result is retained, never the raw title or body.

Exactly one pull request must qualify. Zero matches returns `NO_MATCH`. More
than one returns `AMBIGUOUS_CORRELATION`. Neither outcome invents or overwrites
Evidence.

### 11.2 Qualifying review

`E-GH-002` requires at least one review that:

- belongs to the qualifying pull request;
- has state `APPROVED`;
- was submitted at or before `merged_at`;
- targets the merge lineage returned for that pull request; and
- was authored by an actor different from the pull-request author.

When several reviews qualify, the latest by `submitted_at`, then numeric review
ID, is the representative source object. The total qualifying approval count
may be preserved as sanitized metadata. Review bodies and comments are never
retained.

### 11.3 Qualifying deployment

`E-GH-003` requires a deployment that:

- belongs to the configured repository;
- references the qualifying pull request merge SHA;
- uses exact environment `production`; and
- has a deployment status with state `success`.

When several successful statuses qualify, the earliest success after the
merge, then numeric status ID, represents the initial shipped fact. Deployment
payloads and environment secrets are never requested or retained.

### 11.4 Commit metadata

Commit metadata supports lineage validation only. The connector may retain
commit SHA, author login and authored/committed timestamps as sanitized
metadata. It must not create a separate canonical Evidence reference, infer a
technical owner, read file changes or store commit-message text.

## 12. Synchronization Strategy

### 12.1 Polling decision

The MVP uses synchronous, pull-based REST polling. It does not use webhooks,
streaming, a queue, an event bus or a scheduler.

Reasons:

- the one-case demo needs historical reconstruction, not real-time delivery;
- polling can recover a bounded historical window;
- no public callback endpoint or webhook-secret lifecycle is needed;
- execution remains deterministic and easy to certify; and
- it preserves a replaceable connector adapter.

### 12.2 Invocation and frequency

Synchronization is `ON_DEMAND_ONLY`. One explicit internal invocation performs
one bounded run and returns one result. Sprint 4 adds no public IMPERATOR REST
route, UI action, startup side effect or recurring schedule.

Only one run may execute at a time. A concurrent invocation fails with
`SYNC_ALREADY_RUNNING`; it is never queued or executed in parallel.

### 12.3 Synchronization window

Each invocation supplies:

- `fromInclusive` in UTC;
- `untilExclusive` in UTC; and
- one correlation ID.

Rules:

- `fromInclusive` must precede `untilExclusive`;
- the maximum window is 180 days;
- `untilExclusive` is fixed before the first GitHub request;
- provider response order is never trusted;
- the source occurrence timestamp for `E-GH-001`, `E-GH-002` and `E-GH-003`
  is respectively PR `merged_at`, review `submitted_at` and successful
  deployment-status `created_at`; and
- no runtime clock value changes the window after the run starts.

### 12.4 Incremental synchronization

Sprint 4 incremental behavior is bounded, idempotent **explicit-window
reconciliation**, not a persisted delta cursor:

1. the caller supplies one window that contains the complete GitHub history
   to reconcile for `DRC-AOA-001`;
2. that window must start no later than the qualifying PR `merged_at` and end
   after every source occurrence the caller expects to reconcile;
3. closed pull requests are requested in descending `updated` order until the
   exact correlated PR is resolved, the `Link` chain ends or the page limit is
   reached;
4. associated review and deployment facts are then resolved from that PR;
5. only facts with source occurrence timestamps inside the half-open window
   become Evidence candidates;
6. rerunning or widening the window safely reconciles late deployment facts
   because Evidence identity is stable; and
7. a successful result returns the fixed window but never claims or advances
   a durable checkpoint.

An advancing incremental cursor, narrow tail window or automatic next-window
calculation is explicitly deferred. This prevents a later deployment from
being missed merely because its originating PR merged in an earlier window.

Sprint 4 creates no checkpoint table, sync-run table or durable scheduler
state. A `PARTIAL`, `FAILED` or `RATE_LIMITED` run does not claim a new
checkpoint. Durable scheduling and checkpoint ownership require a later
decision.

## 13. Pagination And Ordering

Pagination rules:

- request `per_page=100` whenever the endpoint supports it;
- follow only the server-provided `Link` relation `next`;
- execute pages sequentially;
- permit at most 20 pages per collection in one run;
- never construct GraphQL cursors or parallel page requests; and
- stop with `PAGE_LIMIT_EXCEEDED` rather than silently truncate.

Canonical candidate order is:

1. source occurrence timestamp ascending;
2. evidence reference order `E-GH-001`, `E-GH-002`, `E-GH-003`;
3. canonical source object reference ascending; and
4. deterministic Evidence UUID text ascending.

This order controls import and reporting only. Existing D086 timeline ordering
continues to control REST presentation.

## 14. Rate Limit Contract

The adapter reads these response headers on every call when present:

- `x-ratelimit-limit`;
- `x-ratelimit-remaining`;
- `x-ratelimit-used`;
- `x-ratelimit-reset`;
- `x-ratelimit-resource`;
- `retry-after`; and
- `x-github-request-id`.

Rules:

1. Requests are always serial.
2. `403` or `429` with `x-ratelimit-remaining: 0` is a primary rate limit.
3. `403` or `429` with `retry-after` or a provider secondary-limit message is
   a secondary rate limit.
4. `retry-after` takes precedence over `x-ratelimit-reset`.
5. A secondary limit without `retry-after` and with remaining quota uses one
   deterministic 60-second delay.
6. At most one rate-limit wait and one retry are permitted for one request.
7. The adapter waits only when the required delay fits inside the remaining
   120-second run budget and is at most 60 seconds.
8. Otherwise the run stops as `RATE_LIMITED` and exposes a safe retry time.
9. A repeated rate-limit response stops the run as `RATE_LIMITED`.
10. Continuing to call GitHub while rate-limited is forbidden.
11. The token limit, remaining count and reset time may be observed; the token
   itself and response body must not be logged.

## 15. Retry And Timeout Policy

### 15.1 Timeouts

| Boundary | Limit |
|---|---:|
| TCP/TLS connection | 5 seconds |
| One HTTP request including body read | 15 seconds |
| Complete synchronization run | 120 seconds |

Response bodies larger than 2 MiB for one page are rejected as
`RESPONSE_TOO_LARGE`. The connector does not stream provider payloads into
persistence.

### 15.2 Retryable failures

Connection failures, request timeouts and HTTP `408`, `500`, `502`, `503` or
`504` permit at most three total attempts for that request: the initial attempt
plus two retries after deterministic delays of one and two seconds.

Rate-limit retries follow Section 14 instead of this generic schedule.

### 15.3 Non-retryable failures

HTTP `400`, `401`, ordinary `403`, `404`, `410` and `422` are not retried.
Parsing, validation, scope, correlation and identity conflicts are not retried.
No fallback credential, repository, API version or endpoint is permitted.

## 16. Connector Failure Semantics

| Failure | Run status | Product behavior |
|---|---|---|
| Connector disabled | `DISABLED` | No GitHub call and no Evidence change. |
| Missing/blank configuration | `MISCONFIGURED` | Fail closed before the first call. |
| `401` | `UNAUTHORIZED` | No Evidence; token is never echoed. |
| Ordinary `403` | `FORBIDDEN` | No Evidence; report insufficient permission safely. |
| Rate-limit `403`/`429` | `RATE_LIMITED` | Stop according to Section 14. |
| `404` | `REPOSITORY_UNAVAILABLE` | Do not reveal whether private repository absence or authorization caused it. |
| `410` | `API_VERSION_UNSUPPORTED` | Stop; no unversioned fallback. |
| Exhausted timeout/5xx retries | `DEGRADED` | Preserve existing Evidence snapshots; do not fabricate new facts. |
| Page or response limit exceeded | `INCOMPLETE` | No silent truncation; run is not complete. |
| No correlated PR | `NO_MATCH` | Produce no GitHub Evidence; manual review remains required. |
| More than one correlated PR | `AMBIGUOUS_CORRELATION` | Produce no GitHub Evidence; do not select by guess. |
| Review missing | `PARTIAL` | `E-GH-001` may exist; `E-GH-002` is absent. |
| Deployment missing | `PARTIAL` | Existing PR/review Evidence may exist; `E-GH-003` is absent. |
| Duplicate Evidence replay | `COMPLETE` or `PARTIAL` | Report `UNCHANGED`; never insert or update a second fact. |
| Same identity, different normalized fact | `SOURCE_IDENTITY_CONFLICT` | Fail the item; never overwrite existing Evidence. |

A run never spans a GitHub request and a PostgreSQL transaction. Each accepted
Evidence item uses the existing one-item import transaction from D080. Earlier
accepted Evidence remains committed when a later independent item fails. The
run reports `PARTIAL`; it does not execute a compensating delete.

Provider unavailability never deletes or mutates historical Evidence, Decision
or Ledger data. Existing snapshots remain readable and may be marked stale by
later product behavior; the connector does not rewrite them.

## 17. Idempotency And Source Identity

Every generated Evidence ID is a deterministic UUIDv5 under namespace:

```text
f83b8d46-0f43-4ff5-9a70-92067ce4bd47
```

Canonical names are:

```text
E-GH-001|<organization>/<repository>|<pull-node-id>|<merge-commit-sha>
E-GH-002|<organization>/<repository>|<pull-node-id>|<review-id>|<commit-id>
E-GH-003|<organization>/<repository>|<deployment-id>|<status-id>|success
```

Organization and repository segments are normalized to lower case only for ID
derivation. Original provider spelling remains in safe lineage metadata.

The same source fact always yields the same UUID, Evidence reference and
normalized fact. Replay is `UNCHANGED`. A different immutable source fact gets
a different UUID. Existing Evidence is never updated, replaced or deleted.

## 18. Correlation Contract

The connector observes correlation signals but does not infer a new Decision
ROI Case.

For this one-case MVP:

- source correlation token is exact `IMP-214`;
- product correlation key is exact `DRC-AOA-001`;
- `case_hint` is exact `DRC-AOA-001`;
- entity is exact `ai-onboarding-assistant`;
- repository, PR number and merge SHA preserve technical lineage; and
- deployment must trace to the same merge SHA.

The mapping from `IMP-214` to `DRC-AOA-001` is a frozen product configuration,
not a heuristic. Unknown keys, fuzzy text similarity, AI matching and
cross-repository inference are forbidden.

One canonical UUID correlation ID identifies the complete sync invocation. It
appears in the sync result and structured logs. GitHub's
`x-github-request-id` may be logged as provider diagnostics but never replaces
the IMPERATOR correlation ID or the business correlation key.

## 19. Enterprise Evidence Event Mapping

All automatic GitHub outputs use:

| Field | Frozen value or source |
|---|---|
| `schema_version` | `1` |
| `source` | `GitHub` |
| `source_type` | `code` for PR/review; `deployment` for deployment status |
| `entity` | `ai-onboarding-assistant` |
| `severity` | `info` |
| `evidence_type` | `code_deployment` |
| `case_hint` | `DRC-AOA-001` |
| `correlation_key` | `DRC-AOA-001` |
| `confidence` | `high` only when the exact source conditions pass |
| `freshness` | `fresh` at accepted synchronization time |
| `review_status` | `accepted` |
| `raw_payload.mode` | `not_stored` |

`ingested_at` exists only in the transient Enterprise Evidence Event. The
existing Domain and V1 schema remain unchanged.

### 19.1 `E-GH-001` - implementation path

| Field | Mapping |
|---|---|
| `event_type` | `code_change_merged` |
| `timestamp` | PR `merged_at` |
| `source_object_ref` | `github:<org>/<repo>:pull:<number>` |
| `actor` | `merged_by.login`, otherwise `unknown` |
| `sensitivity` | `CONFIDENTIAL` |
| `observed_fact` | Deterministic statement that PR number, merge SHA and `IMP-214` were correlated and merged into the default branch. |
| `business_meaning` | Establishes the implementation chain for `DRC-AOA-001`. |
| required metadata | `evidence_ref=E-GH-001`, `freshness=fresh`, PR number, PR node ID, base branch, merge SHA and repository reference |

### 19.2 `E-GH-002` - reviewed merge

| Field | Mapping |
|---|---|
| `event_type` | `code_review_observed` |
| `timestamp` | representative approved review `submitted_at` |
| `source_object_ref` | `github:<org>/<repo>:review:<review-id>` |
| `actor` | representative reviewer login |
| `sensitivity` | `CONFIDENTIAL` |
| `observed_fact` | Deterministic statement that the qualifying PR had an independent approved review before merge. |
| `business_meaning` | Establishes review evidence for the implementation chain. |
| required metadata | `evidence_ref=E-GH-002`, `freshness=fresh`, PR number, review ID, approval count, reviewed commit ID and merge SHA |

### 19.3 `E-GH-003` - shipped state

| Field | Mapping |
|---|---|
| `event_type` | `deployment_reference_observed` |
| `timestamp` | qualifying success status `created_at` |
| `source_object_ref` | `github:<org>/<repo>:deployment-status:<status-id>` |
| `actor` | deployment-status creator login, otherwise `unknown` |
| `sensitivity` | `INTERNAL` |
| `observed_fact` | Deterministic statement that the merge SHA reached successful `production` deployment. |
| `business_meaning` | Establishes the shipped date and runtime lineage. |
| required metadata | `evidence_ref=E-GH-003`, `freshness=fresh`, deployment ID, status ID, environment, merge SHA and repository reference |

### 19.4 Explicit non-output

`E-GH-004` is not generated by GitHub. It remains a manual, approved technical
feasibility note because this connector does not inspect source code or infer
whether model routing is safely configurable.

The connector does not generate `E-OWNER-002`; a PR author, merger or reviewer
is not automatically the technical owner.

## 20. Decision, Recommendation And Business Value Boundaries

### 20.1 Decision generation

No GitHub event generates a Decision.

The only implemented Decision origin remains eligible
`business_context_requested` Evidence under D081. GitHub events are supporting
Evidence for an existing `DRC-AOA-001` case.

### 20.2 Recommendation generation

The connector never invokes, evaluates or changes the deterministic
Recommendation policy. A separate existing Application capability may evaluate
`DRC-AOA-001-v1` only after all mandatory Evidence, including manual
`E-GH-004`, exists and passes D082.

### 20.3 Business Value generation

The connector produces no ROI amount, saving, avoided cost or Business Value.
GitHub has no financial authority.

Business Value remains the existing projection from a validated Ledger result
after implementation and `result_validated`. A complete GitHub evidence set
improves traceability only. It never converts estimated recovery into realized
value.

## 21. Event Ordering And Import Semantics

Provider payload order, page order and HTTP completion time never define
business order. The connector derives source occurrence times, applies Section
13 ordering and imports candidates one at a time.

The path is:

```text
GitHub REST source objects
-> provider-specific adapter models
-> Enterprise Evidence Event candidates
-> canonical validation and ordering
-> existing ImportEvidenceInputPort
-> existing one-Evidence transaction
-> existing EvidenceRepository
```

The connector must not call Decision, Recommendation, Ledger or Business Value
repositories and must not append a Ledger entry.

## 22. Security And Data Governance

1. Token values never enter source control, documentation, HTTP errors,
   Evidence, metadata, logs or the Ledger.
2. PR titles, bodies, branch names, review bodies and commit messages are
   untrusted source text, never instructions.
3. Raw source payloads are not persisted.
4. PR body, review body, commit message, diffs, patches, file names, source code,
   email addresses and avatar URLs are not Evidence.
5. Secrets, security findings and Actions data are never requested.
6. Metadata remains flat, bounded and limited to lineage fields listed in
   Section 19.
7. Unknown sensitivity is `CONFIDENTIAL`; it never broadens access.
8. D088 filters persisted Evidence when it is later returned through REST.
9. Connector configuration grants no D083 business action.
10. No GitHub actor becomes an IMPERATOR user, approver or owner by inference.
11. The MVP remains single-case and single-tenant bounded; D089 creates no
    future enterprise entitlement model.

## 23. Audit Behavior

Evidence is the durable source-lineage artifact. Connector execution is not a
business decision and creates no Ledger entry.

Each run returns a non-persisted result containing:

- run correlation ID;
- status;
- fixed window;
- organization/repository reference;
- API version;
- request, retry and page counts;
- qualifying source-object count;
- accepted, unchanged, rejected and missing Evidence counts;
- resulting Evidence IDs and references; and
- safe failure code and retry time when applicable.

Structured logs may record the same safe operational fields plus
`x-github-request-id`. They must not record tokens, raw provider bodies, PR or
review text, commit messages, email addresses or stack traces containing
provider data.

No sync-run table or security-audit API is authorized. R&D evidence may record
executed test identifiers, timings and sanitized outcomes, never credentials or
real repository content.

## 24. Observability Contract

Sprint 4 implementation may emit these technical events through existing
structured logging only:

- `connector.github.sync.started.v1`;
- `connector.github.sync.completed.v1`;
- `connector.github.sync.partial.v1`;
- `connector.github.sync.failed.v1`;
- `connector.github.sync.rate_limited.v1`;
- `connector.github.source.rejected.v1`;
- `connector.github.evidence.accepted.v1`; and
- `connector.github.evidence.unchanged.v1`.

Required fields are timestamp, event, module, correlation ID, safe run status,
duration and bounded counts. WARN covers rate limit, partial data, stale source,
scope rejection and retry exhaustion. ERROR covers unexpected adapter failure
without provider payload disclosure.

Prometheus, Grafana, OpenTelemetry exporters, dashboards and alerting remain
Sprint 4.4 concerns. D089 freezes event meaning, not an observability stack.

## 25. Architecture And Extensibility

The later implementation must preserve this dependency direction:

```text
Explicit internal invocation
-> SynchronizeEvidenceInputPort
-> SynchronizeEvidenceUseCase
-> EvidenceSourcePort
-> GitHubRestAdapter
-> canonical Evidence candidates
-> existing ImportEvidenceInputPort
```

Responsibilities:

- Application owns synchronization orchestration and result semantics.
- `EvidenceSourcePort` exposes canonical candidate capture, not GitHub
  JSON or HTTP types.
- `GitHubRestAdapter` owns token use, REST calls, provider DTOs, pagination,
  rate limits, retries, timeouts and source mapping.
- the existing import use case owns Evidence validation and persistence.
- Domain remains unaware of GitHub.

`SynchronizeEvidenceCommand` contains the fixed window and correlation ID;
`SynchronizeEvidenceResult` contains only the safe run outcome from Section
23. Neither contains a provider selector. Runtime composition wires exactly
one `EvidenceSourcePort` implementation: GitHub.

Provider-specific types, headers, endpoints and errors remain inside
`backend-java/adapters/out/github`. The later implementation may add the two
minimal provider-neutral ports, the one Application use case and its command
and result named above, but no generic connector platform, registry,
marketplace or plugin runtime.

The adapter must use Java 21 HTTP facilities and the project's existing JSON
tooling. No GitHub SDK or new dependency is authorized by this contract.

A future GitHub App, webhook or multi-repository implementation must adapt to
the same canonical Evidence boundary and requires a new accepted decision.

## 26. Explicitly Deferred Or Forbidden

The following remain outside Sprint 4:

- AWS;
- GitLab;
- Azure DevOps;
- Bitbucket;
- Jira;
- Slack;
- Teams;
- OpenAI or Anthropic connector work;
- GraphQL;
- webhooks;
- streaming;
- Event Bus;
- Kafka;
- multi-account;
- multi-repository;
- multi-organization;
- multi-branch scanning;
- multi-tenant behavior;
- automatic or background scheduling;
- parallel synchronization;
- durable connector cursors;
- connector or sync-run database tables;
- GitHub Apps;
- OAuth Login;
- classic PATs;
- automated secret rotation;
- GitHub Enterprise Server;
- repository writes;
- source cloning;
- source-code, diff or patch ingestion;
- Actions, workflow or security-alert ingestion;
- new public IMPERATOR routes;
- UI or connector administration;
- Decision, Recommendation, ROI, Ledger or Business Value logic;
- AI source interpretation; and
- real customer data or external exposure.

## 27. Future Sprint 4 Implementation Boundary

After Sprint 4.0 is accepted, the separately authorized implementation may add
only:

- the minimal provider-neutral input and output ports named in Section 25;
- one synchronization Application use case with the command and result named
  in Section 25;
- the bounded GitHub REST adapter under
  `backend-java/adapters/out/github`;
- fail-closed external configuration and bootstrap wiring;
- mapping into existing import commands;
- focused offline contract, mapping, failure and idempotency tests; and
- PostgreSQL certification through the existing integration profile.

It may not change Domain, D086 routes or DTOs, D087/D088 behavior, V1 schema,
Flyway, write repository contracts, deterministic policy, Ledger, Business
Value, frontend, Docker or dependency declarations.

Implementation must proceed in bounded micro-deliverables. No agent may add a
second connector or an operational trigger while implementing GitHub.

## 28. Future Certification Contract

Sprint 4 implementation cannot be certified unless all of the following pass:

1. disabled and invalid configuration make zero GitHub calls;
2. the allowed endpoint inventory is exact and GET-only;
3. API version and mandatory headers are present on every request;
4. one repository/default-branch boundary is enforced;
5. exact `IMP-214` correlation and ambiguity handling pass;
6. `E-GH-001`, `E-GH-002` and `E-GH-003` mappings pass;
7. `E-GH-004`, Decision, Recommendation, Ledger and Business Value are never
   generated by the connector;
8. stable replay creates no duplicate or update;
9. conflicting source identity fails without overwrite;
10. pagination is serial, Link-driven and bounded;
11. retry, timeout and rate-limit behavior pass with deterministic clocks;
12. provider unavailability preserves existing Evidence;
13. token and raw text leakage tests pass;
14. D086, D087, D088 and all baseline tests remain green;
15. Java 21 `clean verify` produces the executable JAR;
16. PostgreSQL 18.x, Flyway validation and the full integration profile pass;
17. Domain Isolation Index remains 100%; and
18. no route, schema, migration, dependency, frozen contract or business rule
   drifts.

Automated tests must use a protocol-faithful local HTTP server. Document 37
forbids tests that call live GitHub. Before Pilot Readiness, a separate authorized
manual smoke check against a non-customer sandbox repository must prove the
external permission posture and be recorded without credentials or raw data.

## 29. Sprint 4.0 Contract Freeze Gate

Sprint 4.0 is accepted only when:

- this document is the one GitHub implementation authority;
- D089 is appended without modifying D001-D088;
- exactly one connector, organization, repository, branch and case are frozen;
- authentication, endpoint, synchronization, pagination, rate, retry, timeout,
  failure, idempotency, ordering and observability behavior are deterministic;
- GitHub produces Evidence only;
- Decision and Business Value boundaries remain unchanged;
- no Java, Spring, REST, SQL, Flyway, PostgreSQL, dependency, test, Maven,
  Docker or runtime file changes; and
- no duplicate agent ownership folder is created.

When these conditions pass, **Sprint 4 - GitHub Integration Implementation** is
the sole next gate. Sprint 4.0.1 Documentation Synchronization remains blocked
until implementation and PostgreSQL/runtime certification pass.
