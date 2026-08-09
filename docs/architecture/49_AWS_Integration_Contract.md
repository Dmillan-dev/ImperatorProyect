# 49 - AWS Integration Contract

Status: **FROZEN**

Decision authority: **D090 - AWS Integration Contract**

Authorized contract fix (2026-08-09): the deterministic observation anchor is
the final PostgreSQL-representable microsecond of the selected month
(`23:59:59.999999Z`), not its final nanosecond. This preserves the final instant
before the exclusive month boundary and guarantees lossless PostgreSQL replay.

Lifecycle phase: **Phase 3 - First Business Value Loop**

Owning gate: **Sprint 4.1.0 - AWS Integration Contract Freeze**

## 1. Purpose

This document freezes the second external connector contract for IMPERATOR.
It answers one implementation question:

```text
How does one bounded AWS account and Region produce trustworthy cost,
attribution, utilization and ownership Evidence for DRC-AOA-001 without
acquiring domain, ROI or governance authority?
```

Sprint 4.1.0 creates no runtime behavior. The later Sprint 4.1 implementation
may realize only this contract.

## 2. Lifecycle And Delivery Sequence

The lifecycle phase remains **Phase 3 - First Business Value Loop**. `4.1.0`
is a contract gate inside the D085 delivery sequence; it does not open a new
product lifecycle Phase 4.

Sprint 4.0 GitHub synchronization is already certified. D090 adds AWS as a
second independent source adapter; it does not replace, expand or reinterpret
D089.

The controlled delivery sequence is:

```text
Sprint 4.1.0 AWS contract freeze
-> Sprint 4.1 AWS implementation
-> Sprint 4.1 default and PostgreSQL/runtime certification
-> Sprint 4.1.1 documentation synchronization
```

Sprint 4.2 Executive Dashboard remains blocked until this sequence closes.

## 3. Sprint 4.1.0 Baseline

The contract-freeze gate was opened from this clean baseline:

- branch: `main`;
- working tree: clean;
- baseline commit: `adae3afee2af7eb016ca261382a34d38afcbb0b0`;
- baseline subject: `Sprint 4.0.1: synchronize project control documentation`;
- Sprint 4.0: `CERTIFIED / COMPLETE`;
- Sprint 4.0.1: `COMPLETE`;
- Java 21 default tests: 128 passed, 0 failed, 0 errors, 0 skipped; and
- PostgreSQL integration tests: 30 passed, 0 failed, 0 errors, 0 skipped.

SHA-256 baseline hashes recorded before this document and D090 were created:

| Authority | SHA-256 |
|---|---|
| `docs/architecture/28_Per_Connector_MVP_Contracts.md` | `262BC3E1A5614308FC7704393965BAD047EC0EC399A19BC9529BBC47FC44B66E` |
| `docs/architecture/29_Event_Evidence_Vocabulary.md` | `4F4E9ED143609A895C484A0E1ADD2CAFAD7FEE75E3EB001B29B107AA311E3505` |
| `docs/architecture/CONNECTOR_FRAMEWORK.md` | `25ECED62B72C7145C4487325CEF119FEC0D5A85284A557DE563BC195AFF7D62A` |
| `docs/architecture/26_Security_Data_Governance_Threat_Model.md` | `ED4671906114AD1743C778497504D9C54DFA9D1517D897ED671169F648377208` |
| `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` | `DDDA05F78E622ECED908BE6549ED3F8F26923137C81BE0DBBFE802EDA25D1086` |
| `docs/architecture/35_Coding_Principles.md` | `5BF0B51B18E5ABEAFD2F17A4BD50B1566A085228EED52E9CB5A65E61CC0EA115` |
| `docs/architecture/37_Implementation_Contract.md` | `85182BBAE6DF3CB2154F32484CD94382B84AB166DEA28D1306FC65B90DB7BA29` |
| `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md` | `68F77671A571B9434DE9FA5EF1694DF89B31D236796FA5880BCF654F96F2E0CB` |
| `docs/architecture/47_RBAC_Authorization_Contract.md` | `15E9204AF809E0C13B0F8EF307BD93667699C300F9ED9FD48D12CA6DBB274F2D` |
| `docs/architecture/48_GitHub_Integration_Contract.md` | `05ABA607C0CA3E3B6448EAE528A8333B5DC376762065804BE3A91B8A4F0C693D` |
| `docs/decisions/14_Decision_Log.md` | `8FB323A61E15BD8A8A804CACB326E5CDD7AD75BCD1CFAD761EA9480E33D00FBE` |

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
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md`;
- `docs/architecture/45_Functional_REST_Application_Contract.md`;
- `docs/architecture/47_RBAC_Authorization_Contract.md`;
- `docs/architecture/48_GitHub_Integration_Contract.md`; and
- D080, D082, D083, D085, D086, D087, D088 and D089 in
  `docs/decisions/14_Decision_Log.md`.

This contract resolves AWS-specific implementation choices only. It cannot
weaken a higher-order domain, security, ROI, route, authorization, persistence
or GitHub invariant.

Where Section 25 of Document 48 freezes one GitHub `EvidenceSourcePort` at
runtime, D090 narrowly supersedes only that bootstrap cardinality so GitHub
and AWS can coexist as two independently named compositions. The existing
provider-neutral ports, commands, results and use case remain unchanged.

## 5. External Normative References

The outbound adapter must follow the official AWS documentation:

- [AWS SDK for Java 2.x credential provider chain](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/credentials-chain.html);
- [AWS SDK for Java 2.x retry strategies](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/retry-strategy.html);
- [AWS SDK for Java 2.x timeout configuration](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/timeouts.html);
- [AWS SDK for Java 2.x Maven BOM and service modules](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/setup-project-maven.html);
- [AWS SDK for Java 2.x release `2.49.6`](https://github.com/aws/aws-sdk-java-v2/releases/tag/2.49.6);
- [STS `GetCallerIdentity`](https://docs.aws.amazon.com/STS/latest/APIReference/API_GetCallerIdentity.html);
- [Cost Explorer `GetCostAndUsage`](https://docs.aws.amazon.com/aws-cost-management/latest/APIReference/API_GetCostAndUsage.html);
- [Cost Explorer API endpoint](https://docs.aws.amazon.com/cost-management/latest/userguide/ce-api.html);
- [Cost Explorer API best practices](https://docs.aws.amazon.com/cost-management/latest/userguide/ce-api-best-practices.html);
- [Resource Groups Tagging API `GetResources`](https://docs.aws.amazon.com/resourcegroupstagging/latest/APIReference/API_GetResources.html);
- [Tagging API authorization reference](https://docs.aws.amazon.com/service-authorization/latest/reference/list_resourcegroupstaggingapi.html);
- [CloudWatch `GetMetricData`](https://docs.aws.amazon.com/AmazonCloudWatch/latest/APIReference/API_GetMetricData.html);
- [CloudWatch authorization reference](https://docs.aws.amazon.com/service-authorization/latest/reference/list_cloudwatch.html); and
- [AWS Lambda metric definitions](https://docs.aws.amazon.com/lambda/latest/dg/monitoring-metrics-types.html).

AWS documentation controls provider protocol facts. D090 controls which of
those facts may enter IMPERATOR and how they are normalized.

## 6. Frozen MVP Summary

| Area | Frozen value |
|---|---|
| Connector | AWS commercial partition only |
| SDK | AWS SDK for Java `2.49.6`, synchronous clients |
| Authentication | SDK `DefaultCredentialsProvider`; no IMPERATOR secret setting |
| Account | Exactly one externally configured 12-digit account ID |
| Organization | None; AWS Organizations access is forbidden |
| Workload Region | Exactly one externally configured commercial AWS Region |
| Cost endpoint Region | Fixed `us-east-1` |
| Workload | Exact resource scope `onboarding-assistant-prod` |
| Case | `DRC-AOA-001` only |
| Source correlation token | Exact Jira key `IMP-214` |
| Entity | `ai-onboarding-assistant` |
| Synchronization | Explicit, synchronous, on-demand polling |
| Concurrency | One AWS synchronization at a time; no cross-source parallel orchestration |
| Cost period | Latest fully closed UTC calendar month inside the explicit window |
| Cost metric | `UnblendedCost` only |
| Automatic Evidence | `E-AWS-001`, `E-AWS-002`, `E-AWS-003`, `E-AWS-004` |
| Decision generation | None |
| Recommendation generation | None |
| Business Value generation | None |
| Public IMPERATOR route | None added |
| Persistence | Existing normalized Evidence only |
| Durable connector cursor | None in Sprint 4.1 |

## 7. Product Outcome And KPI

The integration replaces the manually prepared AWS portion of the first
evidence pack with source-backed facts:

```text
One scoped AWS account and Region
-> one finalized monthly cost observation
-> one exact resource-attribution snapshot
-> one bounded Lambda utilization summary
-> one unambiguous platform-owner observation
-> existing DRC-AOA-001 traceability improves
```

The connector-quality KPI is:

```text
AWS Evidence Completeness = accepted E-AWS-001/002/003/004 references / 4
```

The target is `4/4`. This is not ROI or realized Business Value. The current
policy becomes Recommendation-ready only when every D082 requirement also
passes, including exact EUR monetary semantics and all non-AWS Evidence.

## 8. Authentication Contract

### 8.1 Selected model

The adapter uses AWS SDK for Java 2.x `DefaultCredentialsProvider`. It does not
accept access keys, secret keys, session tokens or credentials through any
`IMPERATOR_*` property.

The selected deployment posture is:

- temporary role credentials supplied by the controlled runtime for deployed
  environments;
- an explicitly selected shared AWS profile or temporary environment
  credentials for an authorized local sandbox;
- AWS SDK SigV4 signing only;
- one STS identity preflight before evidence-source calls; and
- exact returned account equality with `IMPERATOR_AWS_EXPECTED_ACCOUNT_ID`.

The connector never calls `AssumeRole` itself. Credential acquisition and
rotation belong to the external AWS runtime or operator, not to IMPERATOR.

### 8.2 Why this model is selected

The SDK provider chain supports short-lived role credentials without adding a
secret store, login flow, cross-account broker or custom signing code. STS
account verification closes the main ambiguity introduced by a general
provider chain: credentials resolving successfully is not enough unless they
resolve to the one frozen account.

### 8.3 Runtime configuration

The later implementation may read exactly these IMPERATOR settings:

| Setting | Rule |
|---|---|
| `IMPERATOR_AWS_ENABLED` | Defaults to `false`; source calls are impossible unless exactly `true`. |
| `IMPERATOR_AWS_EXPECTED_ACCOUNT_ID` | Required 12-digit account ID when enabled; one value only. |
| `IMPERATOR_AWS_REGION` | Required one commercial AWS Region when enabled; one value only. |

Standard AWS SDK settings such as `AWS_PROFILE` and runtime-provided temporary
credential variables remain external SDK concerns. They are not copied into
IMPERATOR configuration, Evidence, logs or persistence.

Production endpoint overrides are forbidden. Test-scope endpoint injection is
permitted only for local protocol-faithful stubs. Case, entity, tag keys, tag
values, cost Region and metric inventory are frozen constants in this
contract, not open configuration.

### 8.4 Rejected authentication models

| Model | Decision |
|---|---|
| Hard-coded access key and secret | Forbidden. |
| Custom IMPERATOR credential properties | Rejected; duplicates the SDK security boundary. |
| Persisted credentials in PostgreSQL | Forbidden. |
| IAM user as the production target | Rejected; temporary role credentials are required. |
| Connector-managed `AssumeRole` | Deferred; cross-account access is outside the MVP. |
| AWS IAM Identity Center login flow inside IMPERATOR | Rejected; interactive identity is external. |
| AWS as IMPERATOR user authentication | Rejected; D087 remains the API identity authority. |
| Anonymous access | Rejected. |
| Static test credentials used outside local tests | Forbidden. |

## 9. Account, Region And Resource Boundary

1. Exactly one expected account ID and one workload Region are configured.
2. The AWS partition must be `aws`; GovCloud and China partitions are deferred.
3. `GetCallerIdentity.Account` must equal the expected account before any cost,
   tag or metric Evidence is accepted.
4. AWS Organizations enumeration, member-account discovery and organization
   billing aggregation are forbidden.
5. Cost Explorer additionally filters `LINKED_ACCOUNT` to the expected account
   even when the credentials could see broader payer data.
6. Resource Groups Tagging API and CloudWatch run only in the configured
   workload Region.
7. Cost Explorer runs only against its official `us-east-1` endpoint.
8. The exact workload boundary is the intersection of these tags:

```text
project=customer-onboarding
jira_ticket=IMP-214
resource_group=onboarding-assistant-prod
```

9. Only returned resource ARNs with service namespace `lambda` or `apigateway`
   qualify. Any other service carrying the three scope tags makes attribution
   incomplete; it is not silently ignored.
10. At most 25 scoped resources may qualify. More than 25 returns
    `INCOMPLETE` with `AWS_RESOURCE_SCOPE_TOO_BROAD`.
11. Resource discovery without all three exact tags, fuzzy values, name-only
    matching and account-wide inventory are forbidden.
12. `owner=platform-team` is the sole automatic ownership mapping. It is not a
    selection tag and grants no IMPERATOR role or business approval authority.

## 10. Allowed AWS API And Object Surface

The adapter may invoke exactly four AWS operations, all read-only:

| Order | Service | Operation | Objects read | Persisted output |
|---:|---|---|---|---|
| 1 | STS | `GetCallerIdentity` | account ID; principal identity used only for preflight | Account ID only in bounded Evidence lineage |
| 2 | Resource Groups Tagging API | `GetResources` | resource ARN and tags for the exact scope | Sanitized counts, exact approved tags and resource-set fingerprint |
| 3 | Cost Explorer | `GetCostAndUsage` | one closed monthly `UnblendedCost` total | Cost amount, unit and period |
| 4 | CloudWatch | `GetMetricData` | bounded Lambda invocation, error and duration metrics | Aggregated utilization summary |

No other AWS API is authorized. In particular, the connector must not call:

- `ListAccounts`, `DescribeOrganization` or any AWS Organizations operation;
- `ListFunctions`, `GetFunction`, Lambda invoke or Lambda mutation operations;
- API Gateway read or write operations;
- `ListMetrics`, CloudWatch Logs or CloudTrail;
- Cost and Usage Report, Data Exports, Budgets or Cost Forecast APIs;
- IAM, Secrets Manager, Systems Manager Parameter Store, KMS or STS assume-role
  operations; or
- any create, update, tag, untag, delete, execute or deployment operation.

## 11. IAM Permission Contract

The provisioned principal may have exactly these IMPERATOR connector
permissions:

| Permission | Resource boundary | Reason |
|---|---|---|
| `ce:GetCostAndUsage` | `*` for the one account, constrained by the frozen request filter | Read monthly cost. |
| `aws-portal:ViewBilling` | `*` | Permit the authorized Cost Explorer read. |
| `tag:GetResources` | `*`; the service does not support resource-level ARN scoping for this action | Read exact-tag resource mappings. |
| `cloudwatch:GetMetricData` | `*`; the action does not provide a metric ARN restriction | Read the exact metric queries frozen below. |

`GetCallerIdentity` is a mandatory preflight even though AWS documents that it
does not require an allow grant. Its result validates identity; it is not a
permission grant.

The broad IAM `Resource: *` required by these read APIs is compensated by all
of the following application controls:

- exact account preflight;
- exact account, Region, period and tag request filters;
- an allow-list of four SDK operations;
- no generic AWS client exposure outside the adapter;
- no provider payload persistence; and
- offline tests that reject any extra operation.

No wildcard action, managed administrator policy, write permission, secret
read, IAM mutation or resource mutation is permitted. An over-privileged
principal fails the provisioning review and cannot be certified for pilot use,
even though AWS does not expose enough runtime information for the adapter to
prove the complete attached policy.

## 12. Source Selection Contract

### 12.1 Review period

The synchronization command supplies the existing half-open UTC window
`[fromInclusive, untilExclusive)`. The adapter selects exactly the most recent
fully closed UTC calendar month whose complete interval is contained in that
window.

Before credential resolution, the adapter captures one `runStartedAt` from an
injected UTC `Clock`. `untilExclusive` must not be after `runStartedAt`, and the
selected month end must be no later than `runStartedAt`. A future boundary
returns `MISCONFIGURED` with `AWS_FUTURE_WINDOW_FORBIDDEN` and makes zero AWS
calls.

If no complete calendar month is contained, the run returns `MISCONFIGURED`
with `AWS_COMPLETE_BILLING_PERIOD_REQUIRED` and makes zero AWS calls. The
selected month is fixed before credential resolution and all provider calls.
It never uses the current partial month and never requests forecast data.

The business observation timestamp for all four candidates is the final
PostgreSQL-representable microsecond of the selected month. This is the
deterministic period anchor; it does not assert that current resource tags
existed throughout that month.

### 12.2 Cost selection

`GetCostAndUsage` is fixed as:

| Request field | Frozen value |
|---|---|
| `TimePeriod.Start` | selected month start, inclusive |
| `TimePeriod.End` | next month start, exclusive |
| `Granularity` | `MONTHLY` |
| `Metrics` | exactly `UnblendedCost` |
| `Filter` | `LINKED_ACCOUNT=<expected account>` AND the three exact cost-allocation tags from Section 9 |
| `GroupBy` | absent |
| `BillingViewArn` | absent |

Cost Explorer and all three user-defined cost-allocation tags must already be
active in the AWS account. Activation is an external provisioning precondition
and is not performed by IMPERATOR.

Exactly one `ResultsByTime` interval matching the request is accepted. Its
`Estimated` flag must be `false`. The adapter reads only
`Total.UnblendedCost.Amount` and `Total.UnblendedCost.Unit`.

The source amount is parsed as decimal text, never binary floating point.
`monthly_cost` is normalized to scale two with `HALF_EVEN`; the canonical
unrounded decimal is retained as bounded `source_amount` metadata using
`BigDecimal.stripTrailingZeros().toPlainString()` with zero represented as
`0`. Negative, non-finite, missing, multi-currency or malformed totals are
rejected.

### 12.3 Resource and tag selection

`GetResources` is fixed as:

- three `TagFilters`, one for each exact Section 9 key/value;
- `ResourcesPerPage=100`;
- no `ResourceARNList`;
- no `ResourceTypeFilters`;
- no tag-policy compliance fields; and
- serial `PaginationToken` traversal.

Every returned mapping is revalidated locally. Qualified ARNs are sorted by
their exact UTF-8 text. Raw ARNs are transient and are not persisted. The
persisted `resource_set_sha256` is lowercase SHA-256 over the newline-joined,
sorted ARN list encoded as UTF-8, with no terminal newline.

The connector creates `E-AWS-002` only when at least one scoped resource
qualifies and no unsupported scoped service is present.

### 12.4 Utilization selection

`E-AWS-003` is a factual Lambda activity summary, not a rightsizing decision.
For each qualifying Lambda function ARN, `GetMetricData` requests these exact
`AWS/Lambda` metrics with dimension `FunctionName`:

| Metric | Statistic | Meaning retained |
|---|---|---|
| `Invocations` | `Sum` | Total invocation count in the period. |
| `Errors` | `Sum` | Total invocation-error count in the period. |
| `Duration` | `Sum` | Total observed execution milliseconds in the period. |

The period is `86400` seconds, the request window is the selected month,
`ScanBy` is `TimestampAscending`, `MaxDatapoints` is `5000`, and Metrics
Insights and metric math are forbidden.

Results are accepted only when every expected query ID is present, status is
`Complete`, no truncation remains and each value is finite and non-negative.
SDK `Double` values enter decimal arithmetic through `BigDecimal.valueOf`.
Invocation and error totals must be integral and are serialized at scale zero;
duration milliseconds use scale three with `HALF_EVEN`. Zero is valid source
truth; missing datapoints are not converted to zero. API Gateway metrics,
provisioned capacity, utilization scoring and optimization inference are
deferred.

If no Lambda resource qualifies or any expected metric series is missing,
`E-AWS-003` is absent and the run is `PARTIAL` when another candidate exists.

### 12.5 Owner selection

`E-AWS-004` is produced only when every qualifying resource carries exact tag
`owner=platform-team`. Missing, blank, differently cased, conflicting or
different owner values make ownership ambiguous and leave `E-AWS-004` absent.

No principal ARN, GitHub actor, resource creator, billing contact or IAM role
is inferred as owner. The owner observation creates no IMPERATOR `User`,
`Role`, approver or authorization grant.

## 13. Synchronization Strategy

### 13.1 Polling and invocation

The MVP uses synchronous, pull-based SDK calls. It has no webhook, event
stream, queue, scheduler or startup synchronization.

Synchronization is `ON_DEMAND_ONLY`. One explicit internal invocation performs
one AWS run and returns one existing `SynchronizeEvidenceResult`. Sprint 4.1
adds no public REST route, UI action or recurring task.

Only one invocation of the AWS synchronization composition may run at a time.
A concurrent invocation returns the existing `SYNC_ALREADY_RUNNING` outcome;
it is neither queued nor executed in parallel.

### 13.2 Frequency and reconciliation

The operational cadence is at most one accepted reconciliation for the latest
closed month per explicit review cycle. Replays are allowed to prove
idempotency. The adapter does not poll the current month because Cost Explorer
can mark it estimated and may refresh it over time.

Incremental behavior is explicit-window reconciliation, not a cursor:

1. the caller supplies the complete bounded window;
2. the adapter deterministically selects its latest fully contained month;
3. all four source areas are reconciled for that month;
4. stable Evidence identity makes a repeat `UNCHANGED`;
5. no next window or checkpoint is calculated; and
6. failed or partial runs never claim progress.

No cursor, sync-run table or connector state is persisted.

## 14. Pagination, Limits And Ordering

Pagination is serial and service-token driven:

| Operation | Page setting | Maximum pages |
|---|---|---:|
| `GetResources` | `ResourcesPerPage=100` | 20 |
| `GetCostAndUsage` | provider `NextPageToken` | 5 |
| `GetMetricData` | `MaxDatapoints=5000` | 5 |

Rules:

- only a token returned by the immediately preceding response may be used;
- a repeated, expired, malformed or cycling token returns `INCOMPLETE`;
- request parameters other than the token remain byte-equivalent between
  pages;
- pages and AWS services execute sequentially;
- no speculative or parallel pagination is permitted;
- exceeding any page, resource, query or datapoint bound fails visibly; and
- pagination tokens remain in memory and are never logged or persisted.

Canonical candidate order is:

1. source observation timestamp ascending;
2. evidence reference `E-AWS-001` through `E-AWS-004`;
3. canonical source object reference ascending; and
4. deterministic Evidence UUID text ascending.

Provider response order and network completion time never define business
order.

## 15. Retry, Rate Limit And Timeout Policy

### 15.1 SDK retry standard

Every client uses the AWS SDK Java 2.x **standard** retry strategy with exactly
three total attempts per request. Adaptive and legacy retry modes are
forbidden. The SDK owns retry classification and jittered backoff; the adapter
must not add a second retry loop.

Throttling exhausted after SDK retries returns `RATE_LIMITED`. Retry metadata
may expose an AWS-provided retry time when present; it must not invent a
guaranteed reset time.

Validation, account mismatch, access denial, malformed source data, ambiguous
scope, identity conflict and pagination-contract failures are not retried.

### 15.2 Timeouts

| Boundary | Limit |
|---|---:|
| TCP/TLS connection | 3 seconds |
| One API attempt | 5 seconds |
| One logical AWS operation including retries | 15 seconds |
| Complete synchronization run | 120 seconds |

The complete-run budget includes SDK retries and every page. Once the budget
cannot accommodate another bounded call, the run stops as `DEGRADED` with
`AWS_RUN_TIMEOUT`. A timeout never changes existing Evidence.

### 15.3 Request accounting

`requestCount` counts logical SDK calls including pagination requests;
`retryCount` counts attempts after the initial attempt; and `pageCount` counts
successful provider pages across the three paginated operations. STS preflight
counts as one request and no page.

## 16. Connector Failure Semantics

| Failure | Existing source outcome | Safe failure code and behavior |
|---|---|---|
| Connector disabled | `DISABLED` | `AWS_DISABLED`; zero AWS calls and no Evidence change. |
| Missing or invalid setting | `MISCONFIGURED` | `AWS_CONFIGURATION_INVALID`; fail before credential resolution. |
| Future window boundary | `MISCONFIGURED` | `AWS_FUTURE_WINDOW_FORBIDDEN`; zero AWS calls. |
| No complete month | `MISCONFIGURED` | `AWS_COMPLETE_BILLING_PERIOD_REQUIRED`; zero AWS calls. |
| Credentials cannot resolve or are invalid | `UNAUTHORIZED` | `AWS_UNAUTHORIZED`; no credential detail returned. |
| STS account mismatch | `MISCONFIGURED` | `AWS_ACCOUNT_MISMATCH`; no later service call. |
| Access denied | `FORBIDDEN` | `AWS_FORBIDDEN`; no partial fact from the denied operation. |
| SDK throttling exhausted | `RATE_LIMITED` | `AWS_RATE_LIMITED`; preserve prior snapshots. |
| Network, timeout or retryable service failure exhausted | `DEGRADED` | `AWS_SERVICE_UNAVAILABLE`; preserve prior snapshots. |
| Unsupported protocol/service model response | `API_VERSION_UNSUPPORTED` | `AWS_API_VERSION_UNSUPPORTED`; no fallback client. |
| Page, resource, query or datapoint bound exceeded | `INCOMPLETE` | Specific bounded-limit code; never truncate silently. |
| No exact scoped resource and no scoped cost | `NO_MATCH` | `AWS_SCOPE_NOT_FOUND`; no AWS Evidence. |
| Unsupported scoped service | `PARTIAL` when `E-AWS-001` is valid, otherwise `INCOMPLETE` | Attribution, utilization and owner candidates are absent; nothing is silently excluded. |
| Conflicting or missing owner attribution | `PARTIAL` | `E-AWS-004` is absent; independent valid candidates remain. |
| Finalized cost absent or `Estimated=true` | `PARTIAL` | `E-AWS-001` absent; full ROI remains blocked. |
| Currency other than EUR or cost other than `410.00` | `COMPLETE` or `PARTIAL` according only to source-reference completeness | Truthful `E-AWS-001` is imported; D082 readiness remains blocked without misreporting connector completeness. |
| Required Lambda metrics absent | `PARTIAL` | `E-AWS-003` absent; no zero fabrication. |
| Stable duplicate replay | `COMPLETE` or `PARTIAL` | Existing Evidence is reported `UNCHANGED`. |
| Same UUID, different normalized fact | application identity-conflict result | Existing Evidence is not overwritten. |

`COMPLETE` requires all four valid candidates. `PARTIAL` requires at least one
valid candidate and one explicitly missing reference. An outcome other than
`COMPLETE` or `PARTIAL` imports no candidates under the existing synchronization
use case.

One run does not wrap AWS calls and PostgreSQL in one transaction. Each
candidate uses the existing one-Evidence import transaction. An earlier valid
candidate remains committed when a later independent candidate is rejected;
the run reports the truthful partial result and performs no compensating delete.

## 17. Idempotency And Source Identity

Every AWS-generated Evidence ID is deterministic UUIDv5 under namespace:

```text
c3747a5f-2576-45e2-a3ac-cbed72e7cf5c
```

Canonical names are:

```text
E-AWS-001|<account>|<yyyy-mm>|onboarding-assistant-prod
E-AWS-002|<account>|<region>|<yyyy-mm>|onboarding-assistant-prod
E-AWS-003|<account>|<region>|<yyyy-mm>|onboarding-assistant-prod|lambda-metrics-v1
E-AWS-004|<account>|<region>|<yyyy-mm>|onboarding-assistant-prod|platform-team
```

Account is the exact 12-digit value. Region and fixed text segments are lower
case ASCII. The month is `YYYY-MM`. UTF-8 encodes the exact canonical name.

Amount, resource fingerprint, metric values and runtime timestamps are not
part of identity. Repeating the same finalized monthly observation must yield
the same normalized candidate and become `UNCHANGED`. A later different month
gets a different identity.

If AWS later returns different normalized content for the same canonical
month identity, the connector must not overwrite history. It reports a source
identity conflict for explicit operator review. Correction-ledger behavior and
mutable Evidence are not authorized.

## 18. Correlation Contract

AWS does not discover or infer a new Decision ROI Case. The mapping is fixed:

- source tag `jira_ticket=IMP-214`;
- source tag `project=customer-onboarding`;
- source tag `resource_group=onboarding-assistant-prod`;
- product correlation key `DRC-AOA-001`;
- `case_hint=DRC-AOA-001`; and
- entity `ai-onboarding-assistant`.

Unknown tags, partial tag matches, fuzzy matching, resource-name heuristics,
AI correlation and account-wide allocation are forbidden.

The existing command correlation UUID identifies one synchronization run and
is propagated unchanged to source capture, result and structured logs. AWS
request IDs may be recorded as bounded provider diagnostics but never replace
the IMPERATOR correlation ID, business correlation key or Evidence UUID.

## 19. Enterprise Evidence Event Mapping

All automatic AWS outputs use:

| Field | Frozen value or source |
|---|---|
| `schema_version` | `1` |
| `source` | `AWS` |
| `source_type` | `cloud_cost` |
| `entity` | `ai-onboarding-assistant` |
| `severity` | `info` |
| `case_hint` | `DRC-AOA-001` |
| `correlation_key` | `DRC-AOA-001` |
| `confidence` | `high` only when the exact source conditions pass |
| `freshness` | `fresh` when selected period end is no more than 45 days before command `untilExclusive`; otherwise `stale` |
| `review_status` | `accepted` |
| `sensitivity` | `CONFIDENTIAL` |
| `raw_payload.mode` | `not_stored` |

The observation timestamp is the final PostgreSQL-representable microsecond of
the selected month. The adapter persists no ingestion timestamp, raw SDK
response or credential data.

### 19.1 `E-AWS-001` - finalized monthly cost

| Field | Mapping |
|---|---|
| `event_type` | `cloud_cost_observed` |
| `source_object_ref` | `aws:ce:<account>:<yyyy-mm>:onboarding-assistant-prod` |
| `actor` | `aws-account:<account>` |
| `evidence_type` | `cloud_cost` |
| `observed_fact` | Deterministic statement of finalized scoped monthly AWS cost, amount and source currency. |
| `business_meaning` | Provides the infrastructure-cost input for DRC-AOA-001. |
| required metadata | `evidence_ref=E-AWS-001`, freshness, account ID, billing start/end, `metric=UnblendedCost`, `monthly_cost`, `source_amount`, `currency`, `estimated=false`, resource scope and policy correlation tags |

### 19.2 `E-AWS-002` - resource attribution

| Field | Mapping |
|---|---|
| `event_type` | `cloud_resource_tag_observed` |
| `source_object_ref` | `aws:tag:<account>:<region>:<yyyy-mm>:onboarding-assistant-prod` |
| `actor` | `aws-account:<account>` |
| `evidence_type` | `cloud_cost` |
| `observed_fact` | Deterministic statement that the scoped resource set carried all three exact correlation tags when synchronized. |
| `business_meaning` | Connects AWS resources and attributed spend to `IMP-214` and `DRC-AOA-001`. |
| required metadata | `evidence_ref=E-AWS-002`, freshness, account ID, Region, period, exact three tags, resource count, sorted service set and `resource_set_sha256` |

`E-AWS-002` is explicitly a current tag snapshot used for the selected review
period. It does not claim historical tag continuity.

### 19.3 `E-AWS-003` - Lambda utilization summary

| Field | Mapping |
|---|---|
| `event_type` | `cloud_utilization_observed` |
| `source_object_ref` | `aws:cloudwatch:<account>:<region>:<yyyy-mm>:onboarding-assistant-prod` |
| `actor` | `aws-account:<account>` |
| `evidence_type` | `cloud_utilization` |
| `observed_fact` | Deterministic statement of aggregate invocation count, error count and execution milliseconds for the scoped Lambda set. |
| `business_meaning` | Supplies bounded workload context without making an optimization decision. |
| required metadata | `evidence_ref=E-AWS-003`, freshness, account ID, Region, period, Lambda count, invocation sum, error sum, duration-ms sum, metric namespace, period seconds, metric-set version and resource-set fingerprint |

### 19.4 `E-AWS-004` - platform ownership

| Field | Mapping |
|---|---|
| `event_type` | `cloud_owner_observed` |
| `source_object_ref` | `aws:tag:<account>:<region>:<yyyy-mm>:owner:platform-team` |
| `actor` | `aws-account:<account>` |
| `evidence_type` | `cloud_cost` |
| `observed_fact` | Deterministic statement that every scoped resource carried exact owner tag `platform-team`. |
| `business_meaning` | Establishes technical AWS cost accountability for the resource group. |
| required metadata | `evidence_ref=E-AWS-004`, freshness, account ID, Region, period, `owner=platform-team`, resource count and resource-set fingerprint |

## 20. Currency, Freshness And D082 Compatibility

The connector preserves AWS monetary truth and does not perform currency
conversion.

Rules:

1. `E-AWS-001.currency` is the uppercase `Unit` returned with
   `UnblendedCost`.
2. No exchange rate, configured multiplier, invoice estimate or manual EUR
   substitution is permitted inside the connector.
3. D082 continues to require exact `currency=EUR` and
   `monthly_cost=410.00` for `DRC-AOA-001-v1`.
4. A truthful non-EUR or different-cost Evidence item may remain valid source
   Evidence, but it cannot satisfy the frozen Recommendation policy.
5. The connector never changes its output to make D082 pass.
6. A future currency-conversion or variable-cost policy requires a new product
   decision outside Sprint 4.1.

Freshness is deterministic for the explicit review boundary: the age is the
duration between selected month end and command `untilExclusive`. At 45 days
or less it is `fresh`; above 45 days it is `stale`. D082 rejects stale Evidence
for Recommendation readiness. Existing Evidence is immutable and is not
rewritten merely because wall-clock time later advances.

## 21. Decision, Recommendation, Ledger And Business Value Boundaries

No AWS source object creates a Decision. D081 remains the only implemented
Decision-origin contract.

The connector never invokes or evaluates the Recommendation policy. It does
not calculate current cost, projected cost, ROI, savings, confidence, risk or
Business Value. It supplies normalized source Evidence only.

The existing Domain policy may later read accepted `E-AWS-001..004` together
with the complete canonical pack. The existing Business Value projection
continues to derive realized value only from validated Ledger history.

The connector does not approve, reject, defer, mark implementation, validate a
result or append a Ledger entry. AWS IAM identity, tags and account ownership
create no D083 or D088 authority.

## 22. Import And Transaction Semantics

The path is:

```text
AWS SDK source objects
-> provider-specific adapter records
-> canonical Evidence candidates
-> existing SynchronizeEvidenceUseCase
-> existing ImportEvidenceInputPort
-> existing one-Evidence transaction
-> existing EvidenceRepository
```

The connector must not call Decision, Recommendation, Ledger or Business Value
repositories. It must not expose AWS SDK objects outside its outbound adapter.

Provider calls do not execute inside a PostgreSQL transaction. Each accepted
candidate retains D080 one-item transaction behavior. Cross-source atomicity,
batch rollback and distributed transactions are forbidden.

## 23. GitHub And AWS Coexistence

D089 behavior remains unchanged. The two connectors coexist through minimal
bootstrap composition, not through a connector platform:

```text
githubSynchronizeEvidenceInputPort
-> one SynchronizeEvidenceUseCase
-> githubEvidenceSourcePort

awsSynchronizeEvidenceInputPort
-> another SynchronizeEvidenceUseCase
-> awsEvidenceSourcePort

both
-> existing ImportEvidenceInputPort
-> existing EvidenceRepository
```

The following is frozen:

- `EvidenceSourcePort`, `SynchronizeEvidenceInputPort`,
  `SynchronizeEvidenceCommand`, `SynchronizeEvidenceResult` and
  `SynchronizeEvidenceUseCase` do not change;
- each source port and input port is explicitly named/qualified in bootstrap;
- no provider selector is added to the command;
- no map, list, registry, factory, plugin system or generic connector manager
  is introduced;
- GitHub and AWS have independent enabled settings and failure results;
- enabling, disabling or failing AWS does not alter GitHub behavior;
- enabling, disabling or failing GitHub does not alter AWS behavior;
- no invocation automatically calls both sources; and
- any future caller that needs both must invoke them sequentially with the same
  business window and may use distinct run correlation IDs.

Parallel cross-source synchronization, aggregate connector status and one
combined transaction are deferred.

## 24. Security And Data Governance

1. Credential values never enter source control, IMPERATOR configuration,
   Evidence, metadata, logs, errors, tests or Ledger.
2. AWS tags and provider strings are untrusted input, never instructions.
3. Raw AWS responses, full ARN lists and pagination tokens are not persisted.
4. Every AWS Evidence item is `CONFIDENTIAL`; D088 redaction applies on read.
5. Credentials, session tokens, IAM policy documents and secret values are
   `RESTRICTED` and are never Evidence.
6. STS principal ARN and user ID are used transiently for diagnostics only and
   are not Evidence or business actors.
7. Metadata is flat, bounded and limited to fields in Section 19.
8. Provider messages and exception text are not returned to callers.
9. Resource tags outside the allow-list `project`, `jira_ticket`,
   `resource_group` and `owner` are not retained.
10. CloudWatch logs, dimensions other than exact `FunctionName`, payloads and
    traces are never requested.
11. AWS data cannot change JWT identity, RBAC, approval authority or tenant
    scope.
12. The MVP remains a controlled single-case environment and is not authorized
    for real-customer AWS access by this contract alone.

## 25. Audit Behavior

Evidence is the durable source-lineage artifact. Synchronization is an
operational read, not a business decision, and creates no Ledger entry.

One non-persisted run result may expose:

- IMPERATOR run correlation ID;
- safe status and failure code;
- fixed window and selected billing month;
- masked account reference using only final four digits;
- workload Region and fixed resource scope;
- SDK/service model version label;
- request, retry, page and qualifying-object counts;
- accepted, unchanged, rejected and missing Evidence counts;
- resulting Evidence IDs and references; and
- retry time when supplied by the provider.

Structured logs may also record AWS request IDs. They must not record
credentials, principal ARN, raw tags, raw ARNs, raw metric series, provider
response bodies or exception text containing provider data.

No sync-run table, credential audit table or new security API is authorized.
R&D evidence may contain sanitized test identifiers, timings and aggregate
results only.

## 26. Observability Contract

Sprint 4.1 implementation may emit these technical events through existing
structured logging only:

- `connector.aws.sync.started.v1`;
- `connector.aws.sync.completed.v1`;
- `connector.aws.sync.partial.v1`;
- `connector.aws.sync.failed.v1`;
- `connector.aws.sync.rate_limited.v1`;
- `connector.aws.scope.rejected.v1`;
- `connector.aws.evidence.accepted.v1`; and
- `connector.aws.evidence.unchanged.v1`.

Required fields are timestamp, event, module, correlation ID, masked account,
Region, safe status, duration and bounded counts. WARN covers stale data,
partial data, currency mismatch, ambiguous ownership, scope rejection,
throttling and retry exhaustion. ERROR covers unexpected adapter failure
without provider payload disclosure.

Prometheus, Grafana, OpenTelemetry exporters, dashboards and alerting remain
Sprint 4.4 concerns. D090 freezes event meaning only.

## 27. SDK, Dependency And API Version Policy

The later implementation may add AWS SDK for Java BOM version `2.49.6` and
exactly these runtime modules:

```text
software.amazon.awssdk:sts
software.amazon.awssdk:costexplorer
software.amazon.awssdk:resourcegroupstaggingapi
software.amazon.awssdk:cloudwatch
software.amazon.awssdk:url-connection-client
```

No aggregate `aws-sdk-java`, AWS SDK v1, Lambda client, IAM client, S3 client,
async Netty client, CRT client, third-party cloud abstraction or LocalStack
dependency is authorized.

All service modules resolve exact version `2.49.6` through the BOM. Floating
ranges, `LATEST`, `RELEASE` and per-module versions are forbidden. A later
patch/minor upgrade within 2.x requires an explicit dependency-maintenance
change and the full connector and integration gates, but not a new product
decision unless observable behavior changes.

The service protocol models are the SDK models for STS `2011-06-15`, Cost
Explorer `2017-10-25`, Resource Groups Tagging API `2017-01-26` and CloudWatch
`2010-08-01`. The capture version label is:

```text
aws-sdk-java-v2=2.49.6;sts=2011-06-15;ce=2017-10-25;tag=2017-01-26;cw=2010-08-01
```

No manual SigV4 implementation, protocol downgrade or alternate API fallback
is permitted.

## 28. Architecture And Extensibility

The implementation must preserve this dependency direction:

```text
Explicit internal AWS invocation
-> named SynchronizeEvidenceInputPort
-> existing SynchronizeEvidenceUseCase
-> named EvidenceSourcePort
-> AwsSdkEvidenceSourceAdapter
-> canonical Evidence candidates
-> existing ImportEvidenceInputPort
```

Responsibilities:

- Application keeps generic synchronization and import orchestration.
- `EvidenceSourcePort` exposes provider-neutral candidates only.
- the AWS adapter owns SDK clients, credentials resolution, account preflight,
  service calls, retries, timeouts, pagination, source validation and mapping.
- bootstrap owns explicit source qualification.
- the existing import use case owns Evidence validation and persistence.
- Domain remains unaware of AWS.

Provider-specific classes, SDK types and errors remain under
`backend-java/adapters/out/aws`. Spring configuration and external properties
remain under `backend-java/bootstrap`. No AWS type may enter Domain,
Application, ports, REST, PostgreSQL records or frontend.

A future second account, Region, workload, metric family, Organizations view,
currency conversion, schedule or public trigger requires a new accepted
decision.

## 29. Explicitly Deferred Or Forbidden

The following remain outside Sprint 4.1:

- AWS Organizations and multi-account access;
- more than one workload Region;
- GovCloud and China partitions;
- cross-account assume-role orchestration;
- Cost and Usage Reports, Data Exports and billing files;
- Budgets, forecasts, anomaly detection, Compute Optimizer and Trusted Advisor;
- current-month or estimated cost Evidence;
- currency conversion or exchange-rate sourcing;
- API Gateway metrics and generic CloudWatch metric discovery;
- CloudWatch Logs, CloudTrail, X-Ray and raw telemetry ingestion;
- EC2, ECS, EKS, RDS, S3 or other resource-specific adapters;
- writes, tagging, remediation or resource execution;
- webhooks, EventBridge, streaming, Event Bus or Kafka;
- background scheduling and parallel synchronization;
- durable cursors, connector tables and sync-run tables;
- connector registry, marketplace, plugin runtime or provider selector;
- secret rotation and credential administration;
- Jira, GitLab, Azure DevOps, Bitbucket, OpenAI and Anthropic connectors;
- new REST routes, UI or connector administration;
- Decision, Recommendation, ROI, Ledger or Business Value logic;
- schema, Flyway or repository-port changes;
- Docker, LocalStack or production deployment; and
- real-customer AWS data or external exposure.

## 30. Future Sprint 4.1 Implementation Boundary

After Sprint 4.1.0 is accepted, the separately authorized implementation may
add only:

- the AWS SDK BOM and five exact modules from Section 27;
- one bounded adapter under `backend-java/adapters/out/aws`;
- fail-closed AWS external properties and bootstrap configuration;
- minimal bean naming/qualification changes needed for Section 23 coexistence;
- mapping into the existing Evidence candidates and import orchestration;
- focused offline configuration, IAM-surface, request, mapping, pagination,
  failure, idempotency, security and coexistence tests; and
- PostgreSQL certification through the existing integration profile.

It may not change Domain, Application logic, ports, existing commands/results,
D086 routes or DTOs, D087/D088 behavior, the GitHub adapter, V1 schema, Flyway,
write repository contracts, deterministic policy, Ledger, Business Value,
frontend, Docker or any frozen contract.

If implementation demonstrates that an existing port, Domain object, schema,
GitHub adapter or frozen decision must change, the agent must stop and return a
NO-GO report. It may not solve that contradiction by improvisation.

## 31. Future Certification Contract

Sprint 4.1 cannot be certified unless all of the following pass:

1. disabled, invalid and future-window configuration make zero AWS calls;
2. the four-operation allow-list and read-only request inventory are exact;
3. default credentials are external and account preflight fails closed;
4. one account, one Region and exact tag scope are enforced;
5. Cost Explorer uses the exact closed-month request and rejects estimated
   cost;
6. Tagging pagination, resource limits and local scope validation pass;
7. CloudWatch uses only the exact Lambda metric set and bounded queries;
8. `E-AWS-001..004` mappings and UUIDv5 identities pass;
9. currency mismatch remains truthful and cannot bypass D082;
10. no AWS object generates Decision, Recommendation, Ledger or Business Value;
11. stable replay creates no duplicate or update;
12. conflicting source identity fails without overwrite;
13. retry, throttling, timeout, pagination and provider-failure behavior pass;
14. credentials, principal identity, raw ARN lists and provider payloads do not
    leak;
15. GitHub and AWS compositions coexist without selector, registry or behavior
    drift;
16. D086, D087, D088, D089 and every baseline test remain green;
17. Java 21 `clean verify` produces the executable JAR;
18. PostgreSQL 18.x, Flyway validation and the full integration profile pass;
19. Domain Isolation Index remains 100%; and
20. no route, schema, migration, frozen contract or business rule drifts.

Automated tests must use local protocol-faithful AWS service stubs with dummy
test credentials and endpoint overrides confined to tests. Live AWS automated
tests, real credentials, real account IDs and chargeable provider calls are
forbidden.

Before Pilot Readiness, a separately authorized manual smoke check against a
non-customer sandbox AWS account must prove the effective account, Region,
least-privilege posture, activated cost-allocation tags and four source reads.
Its record must contain no credentials, full ARN list or raw provider data.

## 32. Sprint 4.1.0 Contract Freeze Gate

Sprint 4.1.0 is accepted only when:

- this document is the sole AWS implementation authority;
- D090 is appended without modifying D001-D089;
- exactly one account, one Region, one resource scope and one case are frozen;
- authentication, IAM, APIs, source selection, period, currency, freshness,
  pagination, retries, timeouts, failures, identity and Evidence mapping are
  deterministic;
- GitHub coexistence requires no Domain, Application or port change;
- AWS produces Evidence only;
- Decision, Recommendation, Ledger and Business Value authority remain
  unchanged;
- no Java, Spring, REST, SQL, Flyway, PostgreSQL, dependency, test, Maven,
  Docker, runtime or project-status file changes; and
- no duplicate agent ownership folder is created.

When these conditions pass, **Sprint 4.1 - AWS Integration Implementation** is
the sole next gate. Sprint 4.1.1 Documentation Synchronization remains blocked
until implementation and default plus PostgreSQL/runtime certification pass.
