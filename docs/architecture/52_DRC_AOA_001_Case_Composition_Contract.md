# 52 - DRC-AOA-001 Case Composition Contract

Decision authority: D093.

Status: Frozen pre-pilot architecture contract. Implementation is not
authorized by this document alone.

## 1. Objective

This contract closes the missing Application composition boundary between the
frozen Evidence import surface and the already certified Decision,
Recommendation and ROI capabilities for the single `DRC-AOA-001` case.

The required commercial flow is:

```text
Committed canonical Evidence
        |
        v
Explicit DRC-AOA-001 composition operation
        |
        v
CreateDecisionInputPort (D081)
        |
        v
GenerateRecommendationInputPort (D082)
        |
        v
Existing D086 read and governance surface
        |
        v
Decision Review Workspace (D091)
```

This contract does not rebuild Decision creation, Recommendation generation,
ROI, governance, Ledger or Business Value. It composes the existing
capabilities without making Evidence import responsible for business creation.

## 2. Current Gap

D092 requires a valid external D087 token to load the workspace after
certified data import through product APIs. The current runtime cannot produce
that state from an empty business database:

- R01 imports Evidence only;
- D086 deliberately leaves `CreateDecisionInputPort` and
  `GenerateRecommendationInputPort` internal;
- no REST, CLI, scheduler or connector boundary invokes those two ports;
- D091 can discover and govern an existing Decision but cannot originate one;
- startup seed data and direct SQL are forbidden; and
- D092 does not authorize a new business capability or route.

Therefore fixing the external PostgreSQL image vulnerability is necessary but
not sufficient to execute the complete D092 runtime E2E.

## 3. Authority And Precedence

This contract specializes the following authorities without editing them:

- D081 and `42_Deterministic_Decision_Creation_Contract.md`;
- D082 and `43_Deterministic_Recommendation_ROI_Contract.md`;
- D083 and `44_Review_Ledger_Result_Validation_Contract.md`;
- D086 and `45_Functional_REST_Application_Contract.md`;
- D087 and `46_JWT_Authentication_Contract.md`;
- D088 and `47_RBAC_Authorization_Contract.md`;
- D089 and `48_GitHub_Integration_Contract.md`;
- D090 and `49_AWS_Integration_Contract.md`;
- D091 and `50_Executive_Dashboard_Contract.md`; and
- D092 and `51_Docker_Production_Runtime_Contract.md`.

D093 is the sole authority for the future composition operation defined here.
It narrowly authorizes a future sixteenth REST route and the minimum
Application and persistence evolution needed to support it. It does not
modify any previous document and does not authorize implementation while the
Sprint 4.3 external blocker remains active.

Every behavior not explicitly changed here remains controlled by D081 through
D092.

## 4. Repository Inspection Findings

The contract is based on the current implementation, not a hypothetical
design:

| Boundary | Verified state | Consequence |
|---|---|---|
| `ImportEvidenceInputPort` | Persists one normalized Evidence per transaction | Import remains Evidence-only |
| R01 | Returns accepted/rejected line items and Evidence UUIDs | The operator can retain the imported Evidence identities |
| `CreateDecisionInputPort` | Exists, is wired and implements D081 | It is reused unchanged in responsibility |
| `GenerateRecommendationInputPort` | Exists, is wired and implements D082 | It is reused unchanged in responsibility |
| `TransactionRunner` | Provides local synchronous transaction boundaries | Each existing use case remains its own unit of work |
| Decision persistence | Idempotent by Decision UUID only | It does not prevent two UUIDs for one `caseId` |
| Recommendation persistence | Enforces one Recommendation per Decision | Existing ownership uniqueness is sufficient |
| D086 REST | Has no Decision creation or Recommendation generation command | One explicit inbound runtime boundary is required |
| D091 workspace | Blocks when more than one Decision has `DRC-AOA-001` | Case-level Decision uniqueness is mandatory |

The existing Application capabilities are sufficient for the two business
transitions. They are not sufficient for secure runtime invocation or for
concurrent one-Decision-per-case enforcement.

## 5. Exact Composition Trigger

Composition is an explicit authenticated Application operation initiated only
after the canonical Evidence import has completed.

The minimum future REST trigger is frozen as:

```text
R16  POST /api/v1/decisions
```

R16 creates or resumes only `DRC-AOA-001`. It is not a generic Decision CRUD
route, workflow engine, connector trigger or portfolio API.

No connector, importer, GET request, application startup, scheduler or
frontend page load may invoke composition automatically.

R16 is objectively required because the production-like runtime has no other
authenticated inbound adapter capable of invoking the existing internal ports,
and D092 forbids direct SQL and startup seed data.

## 6. Authentication And Authorization

R16 requires a valid D087 JWT and is authorized only for exact role `ADMIN`.
There is no hierarchy or inherited permission.

The authenticated JWT subject must equal the request
`requiredApproverId`. This preserves the controlled MVP simplification in
D088: the initiating Admin is also the explicitly assigned final approver.
The later approval remains a separate human command and still requires all
D083 checks.

`PLATFORM_ENGINEER`, `FINANCE` and `AUDITOR` receive `403 ACCESS_DENIED` before
the composition input port executes. Missing or invalid authentication retains
D087 `401` behavior.

Route permission does not prove Evidence readiness, create approval authority
beyond the assigned subject, or bypass any Domain invariant.

## 7. REST Request Contract

R16 consumes and produces `application/json` and uses the existing D086 error
envelope and correlation behavior.

The request contains exactly:

| Field | Contract |
|---|---|
| `caseId` | Required exact string `DRC-AOA-001` |
| `decisionId` | Required canonical lower-case UUID v4; stable across every retry |
| `recommendationId` | Required canonical lower-case UUID v4; stable across every retry |
| `originatingEvidenceId` | Required UUID of the persisted `E-JIRA-001` fact |
| `evidenceIds` | Exactly 28 distinct predecision Evidence UUIDs, including the origin |
| `title` | Exact text in Section 8 |
| `businessNeed` | Exact text in Section 8 |
| `ownerId` | Required canonical lower-case UUID |
| `requiredApproverId` | Required canonical lower-case UUID equal to authenticated `sub` |
| `decisionCreatedAt` | Required explicit RFC 3339 UTC instant; stable across retries |
| `recommendationGeneratedAt` | Required explicit RFC 3339 UTC instant; not earlier than `decisionCreatedAt`; stable across retries |

Unknown fields, duplicate Evidence IDs, malformed UUIDs, unsupported case IDs,
blank text, a nonmatching approver or invalid chronology fail before an
Application transition.

`X-Correlation-ID` remains the request trace identity. It is not a Domain ID
or idempotency identity. The stable `decisionId` and `recommendationId` are the
business-operation replay identities under D081 and D082.

The caller may obtain Evidence UUIDs from the R01 per-line result. R16 never
accepts Evidence bodies, normalized facts, ROI inputs, provider payloads or
policy outputs.

## 8. Exact Decision Mapping

The future composition adapter maps the validated request to the existing
`CreateDecisionCommand` without creating a second Decision policy:

| `CreateDecisionCommand` field | Source |
|---|---|
| `decisionId` | Request `decisionId` |
| `originatingEvidenceId` | Request `originatingEvidenceId` |
| `title` | Request exact `Optimize AI onboarding assistant cost` |
| `businessNeed` | Request exact `Reduce recurring AI expenditure without losing exception-handling quality` |
| `ownerId` | Request `ownerId` |
| `requiredApproverId` | Request value already matched to JWT `sub` |
| `createdAt` | Request `decisionCreatedAt` |

`caseId` is not supplied to `CreateDecisionCommand`. D081 continues to derive
it from the persisted originating Evidence correlation key.

The originating Evidence must satisfy every D081 rule and must represent
conceptual reference `E-JIRA-001`, `evidenceType=business_context` and
`eventType=business_context_requested`. If it is absent or ineligible, no
Decision is created.

## 9. Exact Evidence Set

The canonical import contains 30 Evidence items. Composition and deterministic
Recommendation use exactly the first 28 predecision items:

| Group | Required conceptual references |
|---|---|
| Business context | `E-JIRA-001` through `E-JIRA-004` |
| Code and deployment | `E-GH-001` through `E-GH-004` |
| AWS cost and attribution | `E-AWS-001` through `E-AWS-004` |
| AI usage and quality | `E-AI-001` through `E-AI-005` |
| Usage and value | `E-USAGE-001` through `E-USAGE-003` |
| Ownership and approval | `E-OWNER-001` through `E-OWNER-003` |
| ROI assumptions | `A-ROI-001` through `A-ROI-004` |
| Policy provenance | `DRC-AOA-001-v1` |

The implementation Evidence and result-validation Evidence are deliberately
excluded from composition:

- Evidence 29 belongs only to later `implementation_marked`; and
- Evidence 30 belongs only to later `result_validated`.

Every selected item is loaded through `EvidenceRepository`. D082 remains the
authority for correlation, acceptance, sensitivity, raw-payload, freshness,
reference, assumption, currency and policy validation. Array position and
provider response order have no business meaning.

R16 requires the complete 28-item Low-risk canonical pack. The internal D082
Medium-risk branch remains valid for other authorized Application callers but
is not sufficient for this canonical pilot E2E.

## 10. Recommendation And ROI Mapping

After Decision creation or idempotent resolution succeeds, composition maps to
the existing `GenerateRecommendationCommand`:

| `GenerateRecommendationCommand` field | Source |
|---|---|
| `recommendationId` | Request `recommendationId` |
| `decisionId` | Authoritative D081 Decision ID |
| `evidenceIds` | The exact 28 distinct request Evidence IDs |
| `generatedAt` | Request `recommendationGeneratedAt` |

D082 alone derives:

- Recommendation type and action;
- deterministic reason;
- `EUR 19,440.00` estimated annualized recovery;
- confidence `92`;
- risk `LOW`;
- policy version `DRC-AOA-001-v1`; and
- complete Evidence provenance.

R16, its controller and its mapper never calculate or accept ROI, confidence,
risk, policy version, Recommendation text or financial values.

An optional explanation remains advisory and outside deterministic business
truth. Explanation failure cannot change the R16 business outcome.

## 11. Application Composition Boundary

The minimum future Application evolution is exactly:

```text
ComposeDrcAoa001InputPort
        |
        v
ComposeDrcAoa001UseCase
        |
        +--> CreateDecisionInputPort
        |
        +--> GenerateRecommendationInputPort
```

The new use case owns orchestration only. It may:

- validate the composition command boundary;
- enforce the single case and complete-set shape;
- call the two existing input ports in the frozen order;
- classify first execution, resumed execution and replay; and
- return stable Decision and Recommendation identifiers and final readiness.

It must not:

- construct Domain entities directly;
- duplicate D081 or D082 business policies;
- call repositories to perform the business transitions;
- calculate ROI;
- invoke connectors;
- approve, reject, defer, mark implementation or validate results;
- append a Ledger entry;
- project or persist Business Value; or
- depend on HTTP, Spring, SQL, PostgreSQL, JSON or frontend types.

The REST adapter maps transport to the composition command and maps the result.
It never chains `CreateDecisionInputPort` and
`GenerateRecommendationInputPort` itself.

## 12. Transaction And Recovery Model

Composition is a deterministic, resumable two-step process. It is not one
transaction spanning both capabilities.

```text
T1: Create or resolve Decision under D081
COMMIT

T2: Generate or resolve Recommendation and attach it under D082
COMMIT

After T2: optional explanation attempt outside deterministic persistence
```

This model is frozen because:

- each existing use case already owns one certified transaction;
- Decision `CREATED` is an explicit valid recovery state;
- D081 and D082 already provide independent retry semantics;
- no distributed transaction or Unit of Work is required; and
- an external Explanation Provider must not be pulled into an outer database
  transaction.

Failure before T1 commit persists no Decision. Failure after T1 and before T2
commit leaves exactly one Decision in `CREATED`, without a Recommendation and
without a Ledger entry. Replaying the same request resumes T2 without resetting
the Decision.

No compensation deletes the Decision. Historical correction occurs through a
new valid Evidence import followed by an exact replay; persisted Evidence is
never overwritten to force readiness.

## 13. Idempotency And Duplicate Prevention

### 13.1 Equivalent replay

An equivalent replay uses the same complete immutable request tuple. It:

- resolves the same Decision;
- resolves the same Recommendation;
- preserves later Decision and Ledger state;
- creates no additional Evidence, Decision, Recommendation or Ledger row; and
- returns `200 OK` with `replayed=true`.

The first operation that completes both steps returns `201 Created` with
`replayed=false`. A request that resumes a previously created Decision and
completes Recommendation generation returns `200 OK` with `replayed=false` and
`resumed=true`.

### 13.2 Conflicting replay

The same `decisionId`, `recommendationId` or `caseId` with a different immutable
tuple returns `409` and changes nothing. A later Decision lifecycle state is
never reset merely because composition is replayed.

### 13.3 One Decision per case

The current schema does not enforce unique `decisions.case_id`. Application
prechecks alone cannot prevent two concurrent Decision UUIDs for
`DRC-AOA-001`.

The minimum future persistence evolution is therefore mandatory:

1. add `DecisionRepository.findByCaseId(String caseId)` because this existing
   composition use case objectively needs it;
2. add one Flyway migration enforcing `UNIQUE (case_id)` on `decisions`;
3. make Decision create-if-absent resolve the authoritative row by ID or case;
4. return the authoritative Decision to D081 comparison; and
5. translate a different immutable tuple into
   `DECISION_CREATION_CONFLICT` without overwrite.

This is not a generic case registry, query dashboard or repository search API.
No additional repository method is authorized.

### 13.4 One Recommendation per Decision

The existing unique Recommendation ownership constraint and D082
create-if-absent behavior remain sufficient. No new Recommendation repository
method, table or constraint is authorized.

Equivalent concurrent requests converge on one Decision and one
Recommendation. Conflicting concurrent requests produce one authoritative
winner and safe conflict outcomes; no silent overwrite is permitted.

## 14. Failure Semantics

| Condition | Required outcome |
|---|---|
| Invalid transport, unsupported case, duplicate IDs or actor mismatch | `422`, no Application transition |
| Originating Evidence absent | `404 EVIDENCE_NOT_FOUND`, no Decision |
| Originating Evidence ineligible | `409 EVIDENCE_TRACEABILITY_VIOLATION`, no Decision |
| Supporting Evidence absent after T1 | `404 EVIDENCE_NOT_FOUND`, Decision remains `CREATED` |
| Partial provider/import result | No Recommendation; missing canonical item is reported through the safe existing error family and Decision remains `CREATED` when T1 committed |
| Stale, non-accepted, Restricted or raw-stored Evidence | `409 RECOMMENDATION_NOT_READY`; no Recommendation |
| Missing, malformed, conflicting or non-EUR monetary policy input | `409 RECOMMENDATION_NOT_READY`; no currency conversion or fabricated value |
| Decision immutable tuple conflict | `409 DECISION_CREATION_CONFLICT`; no overwrite |
| Recommendation immutable tuple conflict | `409 RECOMMENDATION_CREATION_CONFLICT`; no overwrite |
| Infrastructure failure before one transaction commits | That transaction rolls back; prior committed step remains authoritative |
| Optional explanation unavailable | Composition still succeeds with explanation absent |

Every failure uses the safe four-field D086 error envelope. It exposes no
token, secret, raw provider payload, SQL, stack trace or confidential Evidence
content.

## 15. Response Contract

A successful R16 response contains exactly:

| Field | Meaning |
|---|---|
| `caseId` | Exact `DRC-AOA-001` |
| `decisionId` | Authoritative Decision UUID |
| `decisionStatus` | Authoritative current Decision status |
| `recommendationId` | Authoritative Recommendation UUID |
| `recommendationType` | Exact `MODEL_CHANGE` |
| `estimatedAnnualizedSavings` | Existing D082 Money representation |
| `confidence` | Existing D082 percentage |
| `risk` | Existing D082 severity |
| `evidenceCount` | Exact `28` |
| `workspacePath` | `/decisions/{decisionId}` |
| `replayed` | True only when complete state already existed unchanged |
| `resumed` | True only when R16 completed T2 for a previously committed D081 Decision |

The response contains no Domain entity, persistence record, raw metadata,
provider response, explanation prompt or approval result.

## 16. Ledger And Audit

Composition appends no Ledger entry. D081 Decision creation and D082
Recommendation generation remain pre-governance facts represented through the
existing Decision, Recommendation and timeline read models.

The first immutable Ledger entry remains an explicit D083 human-governance
fact. Composition cannot approve its own Recommendation.

Structured operational logging for future implementation records only:

- correlation ID;
- case ID;
- Decision and Recommendation IDs;
- outcome `created`, `resumed`, `replayed`, `not_ready` or `conflict`;
- elapsed time; and
- safe Application error code.

It never records JWTs, secrets, request bodies, Evidence text, provider
payloads or financial source metadata.

## 17. Workspace Loadability

The case becomes review-workspace-ready only after T2 commits and the
authoritative Decision references the authoritative Recommendation.

At that point the existing D091 bootstrap behavior must find exactly one
Decision whose `caseId` is `DRC-AOA-001`, navigate to its canonical path and
load Decision, timeline, Evidence, ROI, Recommendation and Ledger through the
unchanged D086 reads.

A Decision left in `CREATED` after a not-ready result is recoverable but does
not satisfy the canonical E2E readiness gate. Business Value remains
unavailable until the later D083 approval, implementation and result-validation
facts are complete.

No frontend change is required for successful composition. R16 is an operator
or certification setup operation, not a browser-owned workflow.

## 18. Connector And Provider Boundaries

GitHub and AWS remain Evidence sources only. Their partial, failed or degraded
synchronization results never create a Decision and never trigger R16.

R16 treats only persisted normalized Evidence as truth. It contains no GitHub,
AWS, Jira, OpenAI, Claude or Keycloak branching.

The canonical E2E may use the existing 30-line import dataset. Live GitHub and
AWS calls are not required to certify composition. Future replacement of
manual canonical facts by connector-generated facts must retain the same D082
conceptual references and obtain separate pilot authorization.

## 19. Minimum Future Implementation Boundary

The later implementation gate may authorize only:

- `ComposeDrcAoa001InputPort`;
- one composition command and one result;
- `ComposeDrcAoa001UseCase`;
- R16 controller, request/response DTOs and mapper;
- exact `ADMIN` R16 authorization;
- `DecisionRepository.findByCaseId(String caseId)`;
- the narrow PostgreSQL adapter behavior needed for case resolution;
- one Flyway migration adding Decision `case_id` uniqueness;
- focused unit, REST security, idempotency, concurrency and PostgreSQL tests;
  and
- runtime E2E evidence under D087, D088, D091, D092 and D093.

The implementation gate must not add:

- another route;
- another case or Recommendation family;
- a generic workflow engine, case registry or orchestration framework;
- connector triggers, scheduler, event bus, Kafka or background worker;
- provider-specific business branching;
- a Business Value entity, table or repository;
- AI decision authority;
- frontend case-creation UI;
- multi-account, multi-repository, multi-tenant or portfolio behavior; or
- direct SQL, startup seed or fixture-loader product behavior.

## 20. Relationship To Sprint 4.3

Sprint 4.3 remains `BLOCKED_EXTERNAL / TEMPORARY NO-GO` until the official
PostgreSQL image satisfies the D092 vulnerability gate. This contract does not
certify Sprint 4.3 and does not permit 4.3.1, 4.4 or 4.5 to start.

When the external image blocker clears, D093 implementation still requires a
separate explicit authorization. After that implementation passes its own
default and PostgreSQL gates, the D092 runtime E2E can use R01 followed by R16,
then load D091 with a real D087-compatible external token.

D092 and D093 must then be certified together for runtime behavior. No D092
criterion is waived.

## 21. Certification Contract For Future Implementation

The later implementation cannot pass unless tests prove:

1. R01 remains Evidence-only;
2. R16 is `ADMIN`-only and required approver equals JWT subject;
3. all 28 predecision Evidence items compose one Low-risk case;
4. Evidence 29 and 30 are excluded from Recommendation creation;
5. D081 creates or resolves exactly one Decision;
6. D082 creates or resolves exactly one Recommendation and ROI;
7. equivalent retry and restart preserve identities and later state;
8. conflicting retry changes nothing;
9. concurrent different Decision UUIDs cannot create two rows for one case;
10. partial, missing, stale and invalid-currency inputs produce the frozen
    recoverable outcomes;
11. no composition Ledger entry exists;
12. D091 finds exactly one case and loads through unchanged read routes;
13. Business Value remains blocked before result validation;
14. default Java, PostgreSQL integration, JWT, RBAC, frontend and Docker gates
    remain green; and
15. ASI, DII and Decision Stability remain 100%.

## 22. Contract Freeze Validation

This contract is accepted only when:

- D093 is the only new Decision Log entry;
- documents and decisions D001 through D092 remain byte-unchanged;
- D086, D089, D090, D091 and D092 files remain byte-unchanged;
- no Java, frontend, SQL, Flyway, Maven, runtime or test file changes;
- no endpoint implementation exists;
- the composition trigger, actor, inputs, evidence set, mappings, transactions,
  recovery, idempotency, concurrency, failures and workspace handoff are exact;
- no unresolved TBD or competing composition alternative remains;
- Markdown links are valid;
- `git diff --check` passes; and
- Architecture, Security, Product, Quality and Context guardians pass.

## 23. Decision

`DRC-AOA-001` will be composed by one explicit, `ADMIN`-only R16 Application
operation that reuses D081 and D082 in two committed, resumable steps. Evidence
import stays side-effect-free. A unique Decision `caseId` constraint and one
case lookup repository operation are the minimum required persistence
evolution. Recommendation uniqueness remains unchanged. No Ledger or Business
Value behavior is added.

The architecture is therefore capable of supporting the commercial E2E
cleanly, but implementation remains a separate gate.
