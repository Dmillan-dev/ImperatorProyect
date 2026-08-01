# 45 - Functional REST Application Contract

Sprint: 3.7.0 - Functional REST Application Contract Freeze

Status: Frozen implementation contract for Sprint 3.7.

Decision authority: D086.

## Scope

This document closes the gap between the frozen 15-route REST topology and the
Application capabilities implemented through Sprint 3.6. It authorizes only
the ports, query use cases, transport mappings and error semantics required to
make those routes functional in Sprint 3.7.

The target dependency path is:

```text
HTTP
-> REST request DTO or query parameters
-> REST mapper
-> Application input port
-> Application use case
-> Domain and output ports
-> PostgreSQL adapter
-> Application result or projection
-> REST mapper
-> REST response DTO
```

Controllers translate and delegate. They never query repositories, calculate
ROI, select recommendations, interpret evidence, authorize governance actions
or construct Domain entities directly.

## Authority And Precedence

This contract specializes:

- D075, D079 through D085 in `docs/decisions/14_Decision_Log.md`;
- `docs/product/API_SPECIFICATION.md`;
- `docs/architecture/34_MVP_Implementation_Blueprint.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- `docs/architecture/39_Persistence_Transaction_Contract.md`;
- `docs/architecture/40_Persistence_Schema_Contract.md`;
- `docs/architecture/41_REST_Adapter_Foundation_Closure.md`;
- `docs/architecture/42_Deterministic_Decision_Creation_Contract.md`;
- `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md`; and
- `docs/architecture/44_Review_Ledger_Result_Validation_Contract.md`.

The route inventory in Document 41 remains frozen. D086 adds no route and does
not authorize a post-MVP alias.

## Narrow Supersession Of D085

D085 limited Sprint 3.7 to the existing Application input ports. Repository
inspection proved that eight frozen GET routes had no query input boundary, so
that restriction would force controllers to call repositories or leave the
routes at `501`.

D086 supersedes only that phrase in D085 and authorizes the eight read-only
input ports and corresponding query use cases named below. It does not
authorize a new command port, route, business rule or write capability. Every
other D085 boundary remains intact.

## Frozen Route-To-Application Mapping

| ID | HTTP route | Application boundary | Sprint 3.7 behavior |
|---|---|---|---|
| R01 | `POST /api/v1/evidence/import` | Existing `ImportEvidenceInputPort` | Preserve the certified NDJSON partial-success behavior unchanged |
| R02 | `GET /api/v1/decisions` | New `ListDecisionsInputPort` | Return a paged Decision summary projection |
| R03 | `GET /api/v1/decisions/{id}` | New `GetDecisionInputPort` | Return one Decision detail projection |
| R04 | `GET /api/v1/decisions/{id}/timeline` | New `GetDecisionTimelineInputPort` | Return the ordered case timeline |
| R05 | `GET /api/v1/decisions/{id}/evidence` | New `GetDecisionEvidenceInputPort` | Return safe, paged Evidence summaries |
| R06 | `GET /api/v1/decisions/{id}/roi` | New `GetDecisionRoiInputPort` | Return the persisted Recommendation ROI view and frozen assumptions |
| R07 | `GET /api/v1/recommendations/{id}` | New `GetRecommendationInputPort` | Return one persisted Recommendation projection |
| R08 | `GET /api/v1/decisions/{id}/ledger` | New `GetDecisionLedgerInputPort` | Return the Decision's ordered immutable Ledger chain |
| R09 | `POST /api/v1/decisions/{id}/ledger/approve` | Existing `ReviewDecisionInputPort` | Record an atomic approval outcome |
| R10 | `POST /api/v1/decisions/{id}/ledger/reject` | Existing `ReviewDecisionInputPort` | Record an atomic rejection outcome |
| R11 | `POST /api/v1/decisions/{id}/ledger/defer` | Existing `ReviewDecisionInputPort` | Record an atomic deferral outcome |
| R12 | `POST /api/v1/decisions/{id}/ledger/mark-implemented` | Existing `AppendLedgerEntryInputPort` | Append the external implementation fact |
| R13 | `POST /api/v1/decisions/{id}/ledger/validate-result` | Existing `AppendLedgerEntryInputPort` | Append the result-validation fact |
| R14 | `GET /api/v1/business-value?decisionId={id}` | Existing `ProjectBusinessValueInputPort` | Return one validated, non-persisted Business Value projection |
| R15 | `GET /api/v1/ledger` | New `ListLedgerEntriesInputPort` | Return a paged cross-Decision Ledger projection |

Every mapped route must stop returning `501`. If its required Application bean
or PostgreSQL runtime is unavailable, it returns the existing JSON error
envelope with HTTP `503`; it must not fall back to fake data or controller-owned
logic.

## Capabilities Deliberately Not Exposed Through REST

`CreateDecisionInputPort` and `GenerateRecommendationInputPort` remain internal
Application capabilities in Sprint 3.7. The frozen topology contains no
Decision-creation or Recommendation-generation command route, and D085 forbids
adding one.

Therefore Sprint 3.7 must not add:

- `POST /api/v1/decisions`;
- `POST /api/v1/recommendations`;
- a command hidden inside any GET route;
- Decision or Recommendation creation as a side effect of Evidence import; or
- controller orchestration across Import, Decision and Recommendation use
  cases.

Functional REST means that every frozen route performs its declared read or
governance capability. It does not mean that every Application input port is a
public HTTP operation. A later connector or workflow gate must receive separate
authorization before it may orchestrate Decision and Recommendation creation.

## Query Input Ports

Sprint 3.7 may add exactly these inbound query capabilities under
`imperator.ports.in`:

| Input port | Operation | Result |
|---|---|---|
| `ListDecisionsInputPort` | `listDecisions(ListDecisionsQuery)` | `PageResult<DecisionSummary>` |
| `GetDecisionInputPort` | `getDecision(GetDecisionQuery)` | `DecisionDetail` |
| `GetDecisionTimelineInputPort` | `getDecisionTimeline(GetDecisionTimelineQuery)` | `PageResult<DecisionTimelineItem>` |
| `GetDecisionEvidenceInputPort` | `getDecisionEvidence(GetDecisionEvidenceQuery)` | `PageResult<EvidenceSummary>` |
| `GetDecisionRoiInputPort` | `getDecisionRoi(GetDecisionRoiQuery)` | `DecisionRoiView` |
| `GetRecommendationInputPort` | `getRecommendation(GetRecommendationQuery)` | `RecommendationView` |
| `GetDecisionLedgerInputPort` | `getDecisionLedger(GetDecisionLedgerQuery)` | `PageResult<LedgerEntryView>` |
| `ListLedgerEntriesInputPort` | `listLedgerEntries(ListLedgerEntriesQuery)` | `PageResult<LedgerEntryView>` |

`ProjectBusinessValueInputPort` already exists and must retain its current
signature. No second Business Value input port is permitted.

Input query objects use Domain identifier value objects, Application pagination
objects and no Spring, Servlet, JSON, JDBC or persistence-record types.

## Query Use Cases

Sprint 3.7 may add exactly one query use case for each new input port:

- `ListDecisionsUseCase`;
- `GetDecisionUseCase`;
- `GetDecisionTimelineUseCase`;
- `GetDecisionEvidenceUseCase`;
- `GetDecisionRoiUseCase`;
- `GetRecommendationUseCase`;
- `GetDecisionLedgerUseCase`; and
- `ListLedgerEntriesUseCase`.

These use cases may:

- validate Application query objects;
- call the read-model output port;
- translate a missing aggregate into the existing typed not-found exception;
- assemble safe Application projections;
- apply the frozen deterministic ordering rules; and
- verify that persisted ROI provenance is consistent with D082.

They must not:

- mutate Domain state;
- call write operations;
- invoke the Explanation Provider;
- create Decisions, Recommendations, Evidence or Ledger entries;
- calculate a new recommendation or alter persisted financial truth;
- accept transport DTOs; or
- depend on Spring, HTTP, JDBC or PostgreSQL classes.

The existing command use cases and their input-port signatures remain
unchanged.

## Read-Model Output Port

Write repositories remain frozen. `DecisionRepository`, `EvidenceRepository`,
`RecommendationRepository` and `LedgerRepository` must not acquire list,
filter, dashboard or projection methods.

Sprint 3.7 may add exactly one outbound read boundary:

```text
MvpReadModelQueryPort
```

It supports only the eight new query use cases listed above. Its PostgreSQL
adapter may execute read-only SQL against the seven V1 tables and map rows into
Application read snapshots. It must not:

- write or lock rows;
- change V1 or add a migration, view, function, trigger or index;
- return PostgreSQL persistence records to REST;
- calculate ROI, Recommendation policy, confidence, risk or Business Value;
- filter according to invented RBAC rules; or
- become a general search or analytics interface.

This is the light CQRS read boundary required by Documents 37, 39 and 40. It is
not a second source of truth. PostgreSQL V1 remains authoritative.

## Application Projection Contract

Application projections are immutable records. They are not Domain entities,
persistence records or REST DTOs.

### Decision summary

`DecisionSummary` contains exactly:

- `decisionId`;
- `caseId`;
- `title`;
- `businessNeed`;
- `status`;
- `ownerId`;
- `requiredApproverId`;
- optional `recommendationId`;
- `createdAt`; and
- `updatedAt`.

### Decision detail

`DecisionDetail` contains the Decision summary fields plus:

- `originatingEvidenceId`;
- ordered `evidenceIds`;
- optional `reviewedBy`;
- optional `reviewedAt`; and
- optional `reviewReason`.

It references related resources by identifier. It does not embed the complete
Evidence, Recommendation, ROI or Ledger responses.

### Timeline item

`DecisionTimelineItem` contains exactly:

- `type`: `DECISION`, `EVIDENCE`, `RECOMMENDATION` or `LEDGER`;
- `referenceId`;
- `occurredAt`;
- `summary`;
- optional `source`;
- optional `actor`;
- optional `confidenceLabel`;
- optional `confidencePercentage`; and
- ordered `evidenceIds`.

The timeline contains the real Decision creation fact, linked Evidence facts,
the persisted Recommendation fact when present and real Ledger facts. A
Recommendation timeline item is not a synthetic Ledger entry.

Timeline order is `occurredAt ASC`, then the fixed type order `DECISION`,
`EVIDENCE`, `RECOMMENDATION`, `LEDGER`, then canonical UUID text. No item is
invented when its persisted source does not exist.

### Evidence summary

`EvidenceSummary` contains exactly:

- `evidenceId`;
- `timestamp`;
- `source`;
- `sourceType`;
- `sourceObjectRef`;
- `entity`;
- `eventType`;
- `severity`;
- `actor`;
- `evidenceType`;
- `observedFact`;
- `businessMeaning`;
- `correlationKey`;
- `sensitivity`;
- `confidence`; and
- `reviewStatus`.

It excludes raw payload content and persistence metadata. Sprint 3.7 remains
local-only until JWT and RBAC exist; this temporary sequencing does not permit
external exposure of these summaries.

### Decision ROI view

`DecisionRoiView` contains exactly:

- `decisionId`;
- `recommendationId`;
- `currentMonthlyCost`;
- `projectedMonthlyCost`;
- `transitionCost`;
- `estimatedMonthlyRecovery`;
- `estimatedAnnualizedRecovery`;
- `confidence`;
- `risk`;
- `policyVersion`; and
- ordered `assumptionEvidenceIds`.

All money is EUR with scale two. The values must be reconstructed only from the
persisted Recommendation and its linked, accepted D082 assumption Evidence,
using the already frozen `DRC-AOA-001-v1` policy. The query must verify that the
annualized result equals persisted `Recommendation.estimatedSavings`; it must
never accept financial inputs from HTTP. A missing or inconsistent policy pack
is `ROI_NOT_READY`, not a partial or fabricated view.

### Recommendation view

`RecommendationView` contains exactly:

- `recommendationId`;
- `decisionId`;
- `type`;
- `suggestedAction`;
- `deterministicReason`;
- `estimatedSavings`;
- `confidence`;
- `risk`;
- `ownerId`;
- `requiredApproverId`;
- `createdAt`; and
- ordered `evidenceIds`.

Natural-language explanation is not persisted and therefore is not returned by
this read route. The deterministic reason remains authoritative. Sprint 3.7
must not regenerate an explanation during a GET request or accept explanation
text from the client.

### Ledger entry view

`LedgerEntryView` contains exactly:

- `ledgerEntryId`;
- `decisionId`;
- optional `recommendationId`;
- `actorId`;
- `actorRole`;
- `occurredAt`;
- `entryType`;
- `changeSummary`;
- `reason`;
- ordered `evidenceIds`;
- optional `estimatedSavings`;
- optional `realizedSavings`;
- optional `confidence`;
- optional `risk`;
- optional `previousEntryId`; and
- safe string `metadata`.

Per-Decision Ledger results must preserve the authoritative chain in
`occurredAt ASC` order. Global Ledger results default to newest first but do not
alter chain references.

## Pagination And Sorting

All list routes use query parameters `page`, `size`, `sort` and `direction`.

Frozen defaults and bounds:

- `page=0`;
- `size=20`;
- maximum `size=100`;
- `direction` is `ASC` or `DESC`, case-insensitive at HTTP and normalized in the
  Application query;
- negative page, size outside `1..100`, unknown sort field or unsupported
  direction returns HTTP `400`.

The page response contains exactly:

- `items`;
- `page`;
- `size`;
- `totalItems`; and
- `totalPages`.

Allowlisted ordering:

| Route | Allowed sort | Default |
|---|---|---|
| `GET /decisions` | `createdAt`, `updatedAt` | `updatedAt DESC` |
| `GET /decisions/{id}/timeline` | `occurredAt` only | `occurredAt ASC` |
| `GET /decisions/{id}/evidence` | `timestamp`, `source`, `severity` | `timestamp ASC` |
| `GET /decisions/{id}/ledger` | `occurredAt` only | `occurredAt ASC` |
| `GET /ledger` | `occurredAt` only | `occurredAt DESC` |

Timeline and per-Decision Ledger requests must use `ASC`; requesting `DESC`
returns HTTP `400` because those representations preserve causal order.

## Business Value Query Semantics

The frozen path remains `GET /api/v1/business-value`. Sprint 3.7 requires one
query parameter:

```text
decisionId=<canonical UUID>
```

This is the minimum selector compatible with the existing single-Decision
`ProjectBusinessValueInputPort`; it does not create a new route or a company
portfolio aggregation.

The controller passes the parsed `DecisionId` and `Optional.empty()` for the
advisory explanation. A caller may not submit explanation text. The response is
the complete existing `BusinessValueProjection`, mapped to REST DTOs without
recalculation. Money uses the representation frozen below.

The route returns:

- HTTP `200` when the Decision has the exact approved, implemented and
  result-validated chain required by Sprint 3.5;
- HTTP `404` when the Decision does not exist; and
- HTTP `409` with `BUSINESS_VALUE_NOT_READY` when the authoritative state is
  incomplete or inconsistent.

The existing input port and use case remain unchanged. Sprint 3.7 may add only
their missing Spring runtime wiring and REST adapter mapping.

## REST DTO And Mapper Rules

REST DTOs live only under the existing `imperator.api.*` feature packages.
Application projections never serialize directly.

Every feature uses explicit mapper methods for:

- path, header, query and request DTO to Application command/query;
- Application result/projection to response DTO; and
- value-object unwrapping at the REST boundary.

Frozen JSON representation:

- property names use lower camel case;
- UUIDs use canonical lower-case text;
- timestamps use UTC RFC 3339 text;
- optional values use JSON `null`, never sentinel strings;
- enums use their canonical uppercase domain values; and
- monetary values use `{ "amount": "19440.00", "currency": "EUR" }`, with
  amount encoded as a scale-two string to avoid binary floating-point loss.

No mapper may call a repository, input port, output port, provider or Domain
mutation method.

## Ledger Command Transport Contract

All five Ledger command routes require a canonical UUID `Idempotency-Key`
header. The REST mapper converts it to `LedgerEntryId`. Missing, blank,
multi-valued or non-canonical values return HTTP `400`; the server never
generates a replacement identifier.

### Approve and reject request

The JSON body contains exactly:

- `reviewedAt`;
- `reason`;
- optional `expectedPreviousEntryId`.

The route supplies `APPROVE` or `REJECT`. The trusted actor context supplies
the reviewer identity and role.

### Defer request

The JSON body contains exactly:

- `reviewedAt`;
- `reason`;
- optional `expectedPreviousEntryId`;
- optional `requiredEvidence`; and
- optional `reviewDate`.

Exactly one of `requiredEvidence` and `reviewDate` is required by the existing
Application use case.

### Mark implemented request

The JSON body contains exactly:

- `occurredAt`;
- `reason`;
- `evidenceIds`;
- `expectedPreviousEntryId`; and
- `period`.

The REST mapper supplies `IMPLEMENTATION_MARKED`. Financial fields are not
accepted.

### Validate result request

The JSON body contains exactly:

- `occurredAt`;
- `reason`;
- `evidenceIds`;
- `expectedPreviousEntryId`;
- `period`;
- `annualizedBaselineCost`;
- `annualizedPostActionCost`; and
- `actualTransitionCost`.

The REST mapper supplies `RESULT_VALIDATED`. Every money object must use EUR and
scale two.

Successful new commands return HTTP `201` and the mapped existing Application
result. An identical replay returns HTTP `200` with `replayed=true`. Neither
response implies external provider execution.

## Temporary Trusted Actor Context

JWT and permanent RBAC belong to Sprints 3.8 and 3.9. Sprint 3.7 may use one
temporary local/test actor source so governance routes can exercise the already
implemented authorization rules without changing their request DTOs later.

The mechanism is frozen as:

- explicitly enabled property:
  `imperator.security.trusted-actor.enabled=true`;
- default value: `false`;
- actor header: `X-Imperator-Actor-ID` containing one canonical UUID;
- role header: `X-Imperator-Actor-Role` containing exactly one of `ADMIN`,
  `PLATFORM_ENGINEER`, `FINANCE` or `AUDITOR`;
- both headers must contain exactly one value;
- no default actor or role;
- no actor or role in a command request body; and
- no trust derived from correlation ID, remote address or arbitrary metadata.

When the mechanism is disabled or either header is missing, HTTP `401` uses
`AUTHENTICATION_REQUIRED`. Malformed, blank, multi-valued or unsupported actor
headers use HTTP `401` and `ACTOR_CONTEXT_INVALID`. A syntactically valid actor
that lacks Application authority uses the existing `AuthorizationException`
code with HTTP `403`.

This temporary adapter is not authentication and must never be enabled for real
customer data, an externally reachable runtime or a pilot. Sprint 3.8 must
replace the actor identity source with verified JWT claims, and Sprint 3.9 must
enforce route-level RBAC. Application command signatures and request bodies do
not change when the source is replaced.

## HTTP Status And Exception Mapping

The D075 four-field envelope remains exact:

```text
code
message
correlationId
details
```

`details` remains a non-null JSON object. Sprint 3.7 returns `{}` unless an
already frozen safe field-error contract exists; it never exposes exception
class names, causes, stack traces, SQL, paths, timestamps, secrets or raw
Evidence.

| Condition | HTTP | Envelope code |
|---|---:|---|
| Successful read or Evidence import | 200 | Not applicable |
| New Ledger governance fact | 201 | Not applicable |
| Identical Ledger command replay | 200 | Not applicable |
| Malformed JSON, UUID, timestamp, money, header or pagination | 400 | Stable transport code |
| Missing or invalid temporary actor context | 401 | `AUTHENTICATION_REQUIRED` or `ACTOR_CONTEXT_INVALID` |
| `AuthorizationException` | 403 | Exact Application exception code |
| Missing route or `NotFoundException` | 404 | Route code or exact Application exception code |
| Unsupported method | 405 | `METHOD_NOT_ALLOWED` |
| Unsupported `Accept` for a success response | 406 | `NOT_ACCEPTABLE` |
| `ConflictException` | 409 | Exact Application exception code |
| `BUSINESS_VALUE_METADATA_INVALID` | 500 | `INTERNAL_SERVER_ERROR` |
| `BusinessRuleViolationException` | 409 | Exact Application exception code |
| `ValidationException` | 422 | Exact Application exception code |
| Payload too large | 413 | `PAYLOAD_TOO_LARGE` |
| Unsupported media type | 415 | `UNSUPPORTED_MEDIA_TYPE` |
| Missing Application bean or unavailable PostgreSQL runtime | 503 | `SERVICE_UNAVAILABLE` |
| Unhandled or internal invariant failure | 500 | `INTERNAL_SERVER_ERROR` |

The explicit error-code row takes precedence over its exception-family row.
Known client-facing Application exceptions expose their stable code and safe
message. The generic `500` and `503` responses never expose the internal
exception message.
All error responses remain `application/json` even when the request asks for
HTML or XML.

## Runtime And Transaction Boundaries

- Read routes use query input ports and execute no write transaction.
- `ProjectBusinessValueUseCase` retains its existing coherent read transaction.
- Review and Ledger commands retain the transactions certified by Sprint 3.4
  implementation.
- Controllers do not create or nest transactions.
- REST success handlers produce only `application/json`.
- The existing `X-Correlation-ID` contract remains unchanged and HTTP-only.
- Missing runtime composition returns `503`, never `501` or fake success.

## Explicit Exclusions

Sprint 3.7.0 and D086 do not authorize:

- Java, SQL, Flyway, test, dependency, controller or runtime implementation;
- route additions, aliases or removals;
- changes to Domain entities, value objects, states or policies;
- changes to existing command input ports or use cases;
- changes to write repository ports;
- schema objects, indexes, read-model tables or materialized views;
- Decision or Recommendation creation through REST;
- AI invocation during a GET request;
- explanation persistence;
- permanent authentication or authorization;
- external exposure or real customer data;
- live connectors, frontend, Docker or observability work; or
- control-document synchronization.

## Sprint 3.7 Certification Contract

Sprint 3.7 may pass only when automated verification proves:

1. all 15 frozen routes are registered and none returns `501`;
2. R01 preserves its certified D080 behavior;
3. R02 through R08, R14 and R15 call only their mapped query input ports;
4. R09 through R11 call only `ReviewDecisionInputPort`;
5. R12 and R13 call only `AppendLedgerEntryInputPort`;
6. controllers never call repositories or concrete use cases;
7. DTOs and mappers do not enter Domain, Application or persistence;
8. all read responses follow the frozen projection and JSON representation;
9. pagination, allowlisted sorting and causal order are deterministic;
10. Business Value requires `decisionId` and validated Ledger truth;
11. temporary actor context is disabled by default and its failure modes pass;
12. idempotency produces one Ledger fact and the correct `201` or replay `200`;
13. all Application exception families map to the frozen status and envelope;
14. undefined and post-MVP routes remain `404`;
15. no write repository, Domain, schema, migration or dependency changes;
16. existing unit, HTTP contract and local Business Value tests pass;
17. Java 21 `mvnw.cmd -o clean verify` passes; and
18. PostgreSQL integration certification covers query mapping and all five
    command routes before Sprint 3.7 is certified.

If any implementation needs a route, Domain state, business rule, schema,
write-repository method or permanent security decision outside this contract,
Sprint 3.7 must fail and request explicit architectural approval.
