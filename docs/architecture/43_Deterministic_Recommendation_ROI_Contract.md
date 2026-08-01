# 43 - Deterministic Recommendation And ROI Contract

Sprint: 3.3.0 - Deterministic Recommendation And ROI Contract Freeze

Status: Frozen functional contract for Sprint 3.3.

Decision authority: D082.

## Scope

This document freezes the functional boundary for the first deterministic
Decision-to-Recommendation transition in Phase 3. It applies only to the locked
`DRC-AOA-001` AI Onboarding Assistant Recovery slice and to the existing
Decision, Evidence, Recommendation, Application and PostgreSQL boundaries.

This contract defines required business outcomes. It does not select the
technical mechanism used to make Recommendation creation atomic under retries
or concurrency.

Sprint 3.3 may implement only:

```text
Persisted Decision in CREATED state
        |
        v
Eligible persisted Evidence and ROI assumptions
        |
        v
Deterministic Recommendation and ROI policy
        |
        v
One Recommendation
        |
        v
Annualized Estimated Savings + Confidence + Risk
        |
        v
Existing persistence boundary
        |
        v
PostgreSQL
```

The existing Domain and V1 schema remain the architectural baseline. This
contract does not authorize a new aggregate, table, route, controller, DTO,
provider integration, schema migration or Recommendation family.

## Authority And Precedence

This contract specializes the first value loop defined by:

- D079 through D081 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/34_MVP_Implementation_Blueprint.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/40_Persistence_Schema_Contract.md`;
- `docs/architecture/42_Deterministic_Decision_Creation_Contract.md`;
- `docs/product/24_MVP_Vertical_Slice.md`;
- `docs/product/25_MVP_ROI_Slice.md`;
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`;
- `docs/product/27_MVP_Acceptance_Test_Plan.md`; and
- `agents/phase1/06_domain_roi_recommendation_slice.md`.

Broader conceptual examples remain useful context, but the exact functional
values and outcomes in this contract govern Sprint 3.3.

## Motivation

The repository already contains a Recommendation aggregate, ROI value objects,
a generation use case, repository adapters and the V1 Recommendation tables.
That foundation does not yet implement the Phase 3 policy:

- the current generation command accepts action, reason, savings, confidence
  and risk from its caller;
- the current use case validates traceability but does not calculate ROI or
  select the Recommendation deterministically;
- the current use case invokes `ExplanationProvider`, even though explanation
  belongs exclusively to Sprint 3.3.1;
- `Recommendation.estimatedSavings` stores one monetary value without an
  explicit period; and
- repository `save()` behavior alone does not define semantic retry or
  concurrency outcomes for the one-Recommendation-per-Decision invariant.

Without this contract, deterministic business truth could be supplied by a
controller or caller, the stored monetary horizon could become ambiguous, and
AI explanation could be coupled to Recommendation creation.

## Policy Ownership

The responsibility boundary is frozen as:

```text
Transport
-> supplies transport syntax only

Application
-> validates the operation boundary and orchestrates one transaction

Domain policy
-> evaluates readiness and derives Recommendation, ROI, confidence and risk

Persistence
-> stores and reconstructs the resulting state without business decisions
```

The inbound operation may supply only the stable Recommendation identity, the
Decision identity, the selected Evidence identities and the stable generation
timestamp. It must not supply authoritative Recommendation type, action,
reason, savings, confidence or risk.

Controllers, importers, repositories, PostgreSQL, provider adapters and AI must
not calculate or choose any policy output.

This contract freezes responsibility and observable behavior, not the name or
shape of a future Java policy class.

## Policy Identity And Version

The only policy authorized by Sprint 3.3 is:

```text
DRC-AOA-001-v1
```

Policy version is immutable business provenance. It must not be inferred from
runtime time, deployment version, source-code revision, Recommendation text or
AI output.

V1 records the policy version without a schema change:

1. one accepted `roi_assumption` Evidence item uses event type
   `roi_assumption_observed`;
2. its flat metadata contains exactly
   `policy_version = DRC-AOA-001-v1` for the policy-version field;
3. it has correlation key `DRC-AOA-001`; and
4. its Evidence ID is included in `Recommendation.evidenceIds`.

The linked policy Evidence is the structured V1 provenance record. The policy
version must not be hidden inside or parsed from the human-readable action or
reason. No new Recommendation column, metadata field, table or migration is
authorized.

The policy Evidence and `DRC-AOA-001-v1` value are part of the immutable
Recommendation creation tuple used for retry comparison.

## Creation Trigger

Recommendation creation is an explicit Application operation after the
Decision and all selected Evidence have been committed successfully.

The following rules are frozen:

- Evidence import does not generate a Recommendation.
- Decision creation does not generate a Recommendation.
- A repository read or REST `GET` never generates a Recommendation.
- No new public Recommendation command route is introduced by Sprint 3.3.
- The Decision must exist, belong to `DRC-AOA-001`, remain in `CREATED` state
  and have no Recommendation when first evaluated.
- Generation and persistence occur inside one explicit transaction.

A retry after Recommendation creation resolves through the idempotency rules
in this contract. It is not rejected merely because the authoritative Decision
already contains the same Recommendation.

## Evidence Selection Contract

The initiating Application boundary supplies a set of Evidence IDs. The policy
loads every item through `EvidenceRepository`; it never accepts transport DTOs,
raw JSONL fields or provider payloads as business truth.

Every selected Evidence item must:

- already exist in PostgreSQL;
- have correlation key exactly `DRC-AOA-001`;
- have review status `ACCEPTED`;
- have sensitivity other than `RESTRICTED`;
- have raw payload mode `not_stored`;
- carry `freshness = fresh` in normalized metadata; and
- preserve its source and conceptual reference through existing Evidence
  fields and flat metadata.

Evidence with another correlation key, missing persistence, stale or non-
accepted review state, `RESTRICTED` sensitivity, or a stored raw payload cannot
satisfy Recommendation readiness.

`Evidence.confidence` remains the confidence classification of one Evidence
item. It is not converted directly into the Recommendation ROI confidence
percentage.

## Canonical Evidence Pack

Sprint 3.3 supports only the canonical `DRC-AOA-001` evidence pack. Runtime
Evidence IDs remain UUIDs; the conceptual references below are carried in the
existing `metadata.evidence_ref` value.

The following support areas are mandatory:

| Area | Required conceptual Evidence references | Policy meaning |
| --- | --- | --- |
| Business context | `E-JIRA-001` through `E-JIRA-004` | Origin, goal, owner context and value boundary. |
| Code and deployment | `E-GH-001` through `E-GH-004` | Implementation, deployment and model-change feasibility. |
| AWS cost and attribution | `E-AWS-001` through `E-AWS-004` | Monthly cloud cost, attribution and ownership. |
| AI usage and cost base | `E-AI-001` through `E-AI-004` | AI cost, model usage, workload shape and attribution. |
| Usage or value signal | `E-USAGE-001` through `E-USAGE-003` | Active use and bounded business significance. |
| Owner and approval | `E-OWNER-001` through `E-OWNER-003` | Business owner, technical owner and approval path. |

Accepted quality Evidence `E-AI-005` is optional only for the explicitly
defined Medium-risk branch. Its presence is mandatory for the canonical
Low-risk output.

Missing any other mandatory support reference makes the policy not ready and
no Recommendation is created.

## ROI Assumption Evidence

ROI assumptions are persisted as normalized Evidence, not as hidden constants
and not in a new assumptions table. Each item must use:

- `evidenceType = roi_assumption`;
- `eventType = roi_assumption_observed`;
- `correlationKey = DRC-AOA-001`;
- `reviewStatus = ACCEPTED`;
- non-`RESTRICTED` sensitivity;
- `rawPayloadMode = not_stored`; and
- flat string metadata.

The required assumptions are:

| Assumption | Frozen value or meaning | Required metadata |
| --- | --- | --- |
| `A-ROI-001` | Lower-cost model supports routine onboarding work. | `assumption_id=A-ROI-001`, `currency=EUR`, `projected_monthly_cost=720.00` |
| `A-ROI-002` | High-capability model remains available for exceptions. | `assumption_id=A-ROI-002`, `fallback=high_capability_model` |
| `A-ROI-003` | No recurring monthly transition cost. | `assumption_id=A-ROI-003`, `currency=EUR`, `monthly_transition_cost=0.00` |
| `A-ROI-004` | Review period `2026-06` is representative for the first estimate. | `assumption_id=A-ROI-004`, `review_period=2026-06` |
| Policy provenance | Policy version used to derive the Recommendation. | `policy_version=DRC-AOA-001-v1` |

Every assumption Evidence ID and the policy-provenance Evidence ID must be
included in `Recommendation.evidenceIds`.

Missing, conflicting, duplicated or malformed authoritative monetary
assumptions make the policy not ready. The policy must not parse monetary
values from `observedFact`, `businessMeaning`, action text or reason text.

## Monetary Policy

The monetary policy is frozen as:

| Rule | Value |
| --- | --- |
| Currency | `EUR` |
| Output scale | `2` |
| Calculation rounding | `HALF_EVEN` |
| AWS monthly cost | `410.00` |
| AI monthly cost | `1930.00` |
| Current monthly cost | `2340.00` |
| Projected monthly cost after action | `720.00` |
| Monthly transition cost | `0.00` |
| Estimated monthly recovery | `1620.00` |
| Annualization factor | `12` |
| Estimated annualized recovery | `19440.00` |

The policy reads source monetary inputs from accepted Evidence metadata and
calculates:

```text
current_monthly_cost =
  aws_monthly_cost
  + ai_monthly_cost

estimated_monthly_recovery =
  current_monthly_cost
  - projected_monthly_cost_after_action
  - monthly_transition_cost

estimated_annualized_recovery =
  estimated_monthly_recovery
  * 12
```

For `DRC-AOA-001-v1`:

```text
410.00 + 1930.00 = 2340.00

2340.00 - 720.00 - 0.00 = 1620.00

1620.00 * 12 = 19440.00
```

All arithmetic uses decimal values. Values are normalized to scale two with
`HALF_EVEN` at the policy boundary before construction of the existing Money
value object. Binary floating-point arithmetic is forbidden.

The frozen meaning of `Recommendation.estimatedSavings` is:

```text
estimated annualized recovery = EUR 19440.00
```

It never means monthly recovery, realized saving, labor recovery, revenue,
guaranteed saving or a blended business-value total.

Monthly recovery remains a deterministic calculation output used by the
reason and future ROI read model. Labor recovery `EUR 805.00` remains separate
and is not added to `Recommendation.estimatedSavings` in Sprint 3.3.

If estimated monthly recovery is zero or negative, the policy is not ready and
no Recommendation is created. The policy must not clamp a non-positive result
to zero.

## Deterministic Recommendation

Sprint 3.3 produces exactly one Recommendation family and type:

```text
MODEL_CHANGE
```

The exact suggested action is:

```text
Change the AI Onboarding Assistant to a lower-cost model for standard onboarding requests, with a high-capability fallback for exceptions.
```

The exact deterministic reason is:

```text
Current monthly cost is EUR 2340.00 and projected monthly cost after the model change is EUR 720.00, yielding estimated monthly recovery of EUR 1620.00 and estimated annualized recovery of EUR 19440.00 under the accepted assumptions.
```

These values are Domain policy outputs. The caller cannot replace, rephrase or
override them. Natural-language variation belongs only to the later
Explanation Provider and must never change the stored deterministic reason.

The Recommendation copies `ownerId` and `requiredApproverId` from the
authoritative Decision. Owner and approval Evidence must corroborate those
values; it cannot replace them.

## Confidence Policy

Recommendation confidence is an integer percentage from `0` through `100`, as
already expressed by `ROIConfidence`. It measures evidence completeness, not
AI certainty, probability of financial success or provider confidence.

The canonical score is derived from fixed policy contributions:

| Evidence area | Canonical contribution |
| --- | ---: |
| Business context | 15 |
| Code and deployment | 14 |
| AWS cost and attribution | 24 |
| AI usage and cost without quality review | 22 |
| Accepted quality review `E-AI-005` | 2 |
| Usage or value signal | 10 |
| Owner and approval clarity | 5 |
| Complete total | 92 |

The only Sprint 3.3 confidence outcomes are:

| Condition | Confidence |
| --- | ---: |
| Complete canonical pack including accepted `E-AI-005` | 92 |
| Complete mandatory pack with only `E-AI-005` missing | 90 |
| Any other mandatory input missing or invalid | No Recommendation |

The policy does not accept a caller-supplied percentage and does not sum
untrusted numeric metadata. Additional Evidence cannot raise the score above
the frozen result in this MVP policy version.

## Risk Policy

The only Recommendation risk outcomes in Sprint 3.3 are:

| Condition | Risk |
| --- | --- |
| Complete canonical pack, accepted quality Evidence and accepted fallback assumption | `LOW` |
| Only quality Evidence `E-AI-005` is missing | `MEDIUM` |
| Any other mandatory input is missing or invalid | No Recommendation |

Sprint 3.3 does not introduce additional risk levels or a configurable risk
matrix. It does not convert missing mandatory evidence into a `HIGH` or
`CRITICAL` Recommendation.

The Medium-risk Recommendation remains a prepared deterministic action, not an
approval. Human deferral and required-evidence recording belong to Sprint 3.4.

## Deterministic Identity

A deterministic Recommendation creation means that the same authoritative
Decision, selected Evidence set, policy version and stable creation inputs
always produce the same Recommendation identity and immutable creation state,
regardless of timing, retries or equivalent concurrent execution.

The identity rules are:

- `RecommendationId` is a UUID version 4 created by the initiating Application
  boundary before Domain construction.
- The same logical generation operation reuses the same Recommendation ID on
  every retry.
- `DecisionId` is the semantic one-Recommendation ownership boundary.
- The use case and persistence preserve the supplied Recommendation ID
  unchanged.
- Runtime clock time, transaction timing, thread scheduling, retry count and
  AI availability do not contribute to identity.

This contract does not choose how an initiating boundary preserves the stable
Recommendation ID.

## Immutable Creation Attributes

The immutable Recommendation creation tuple is:

| Attribute | Source or rule |
| --- | --- |
| `id` | Stable Application-supplied UUID v4. |
| `decisionId` | Authoritative persisted Decision. |
| `policyVersion` | `DRC-AOA-001-v1` from linked policy Evidence. |
| `type` | `MODEL_CHANGE`. |
| `suggestedAction` | Exact deterministic action in this contract. |
| `reason` | Exact deterministic reason in this contract. |
| `evidenceIds` | Complete semantic set of supporting, assumption and policy Evidence IDs. |
| `estimatedSavings` | `EUR 19440.00`, meaning annualized estimated recovery. |
| `confidence` | `92` or the single authorized `90` branch. |
| `risk` | `LOW` or the single authorized `MEDIUM` branch. |
| `ownerId` | Copied from the Decision. |
| `requiredApproverId` | Copied from the Decision. |
| `createdAt` | Stable explicit generation timestamp. |

Evidence order is not meaningful. Equality uses the complete Evidence ID set,
not caller iteration order.

Later Decision review state, Ledger entries, explanation text and realized
value are not part of this creation tuple and must never be overwritten while
resolving a retry.

## Idempotency Contract

The required outcomes are:

| Condition | Required outcome |
| --- | --- |
| Decision has no Recommendation and all policy inputs are ready | Persist exactly one Recommendation and attach it to the Decision atomically. |
| Existing Recommendation has the same immutable tuple | Return the existing Recommendation-equivalent result without changing persisted state. |
| Same identity or Decision is presented with a different immutable tuple | Reject as a Recommendation creation conflict; overwrite nothing. |
| Identical retry occurs after the Decision has progressed | Return the existing Recommendation-equivalent result and preserve all later state. |
| Decision already owns another Recommendation identity | Reject as a conflict; never replace it. |

An idempotent retry must not:

- create a second Recommendation for the Decision;
- replace the stored Recommendation or its Evidence set;
- reset or otherwise change Decision status;
- replace Decision evidence, review fields or timestamps;
- append a Ledger entry;
- invoke AI;
- recalculate from a different policy version; or
- report success after silently changing immutable attributes.

No HTTP status or REST error mapping is frozen because Sprint 3.3 does not
introduce functional Recommendation REST behavior.

## Concurrency Expectations

Concurrency must preserve the sequential idempotency outcomes:

- equivalent concurrent commands result in exactly one persisted
  Recommendation identity and semantically equivalent successful outcomes;
- conflicting concurrent commands for the same Decision never overwrite each
  other silently;
- at most one immutable Recommendation tuple wins for one Decision;
- Recommendation persistence and Decision attachment succeed or fail together;
  and
- a retry racing with later Decision state cannot reset or delete that state.

Sprint 3.3 certification must exercise equivalent and conflicting concurrent
attempts against real PostgreSQL.

## Transaction Boundary

One Recommendation generation operation must atomically:

1. load the authoritative Decision;
2. load and validate the complete selected Evidence set;
3. evaluate `DRC-AOA-001-v1` readiness;
4. calculate the deterministic ROI outputs;
5. construct or resolve the authoritative Recommendation;
6. attach the Recommendation to the Decision when it is first created; and
7. persist Recommendation and Decision state.

No partial state is allowed:

- a Recommendation row without the matching Decision reference is invalid;
- a Decision reference without the Recommendation row is invalid; and
- policy failure, conflict or infrastructure failure persists neither partial
  effect.

## AI Separation

Sprint 3.3 must not know or invoke `ExplanationProvider`.

Recommendation creation produces only deterministic:

- Recommendation type;
- action;
- reason;
- Evidence references;
- estimated annualized savings;
- confidence; and
- risk.

The following boundary belongs exclusively to Sprint 3.3.1:

```text
Persisted deterministic Recommendation
        |
        v
Prepared bounded context
        |
        v
ExplanationProvider
        |
        v
Natural-language explanation
```

AI unavailability cannot prevent, alter, retry or roll back deterministic
Recommendation creation. Explanation text is never part of Recommendation
identity, ROI, confidence, risk or persistence truth.

## Repository Considerations

The current `RecommendationRepository` exposes `save`, `findById` and
`existsById`. The V1 schema already enforces a unique Recommendation ownership
row for each Decision.

Those facts do not by themselves define the required retry and concurrency
behavior. A check followed by the current plain insert is not sufficient to
prove atomic create-or-resolve semantics under concurrency.

This document freezes the problem and required outcomes, not the technical
solution. It deliberately does not choose or authorize `createIfAbsent`,
`findByDecisionId`, a lock, an additional constraint, a schema migration or
any other repository mechanism.

Sprint 3.3 must review the smallest correct mechanism before changing code. If
that mechanism requires a repository-port change, schema change, migration,
new table or frozen-contract modification, implementation must stop and
request explicit architectural approval.

## Existing Implementation Corrections Required By The Contract

Sprint 3.3 must correct behavior, not redesign architecture:

- policy outputs must no longer be accepted as authoritative caller inputs;
- Recommendation and ROI calculation must execute in the Domain policy
  boundary and be orchestrated by Application;
- Recommendation creation must not depend on or invoke
  `ExplanationProvider`;
- `Recommendation.estimatedSavings` must always mean annualized estimated
  recovery for this policy; and
- persistence must satisfy the idempotency, concurrency and atomicity outcomes
  above.

The exact Java file boundary remains a Sprint 3.3 pre-implementation decision.
D082 does not authorize code merely by recording these gaps.

## Explicit Exclusions

Sprint 3.3.0 and this contract do not authorize:

- Java, SQL, test or runtime changes;
- a new aggregate, table, column, migration, route or controller;
- a second Decision case or Recommendation family;
- multiple Recommendations, ranking, learning or configurable policies;
- caller-, controller-, importer-, repository- or provider-owned ROI logic;
- AI explanation or any `ExplanationProvider` invocation;
- natural-language variation of deterministic business truth;
- human review, approval, rejection or deferral;
- Ledger append, implementation marking or result validation;
- realized Business Value;
- labor recovery blended into direct-spend savings;
- security, frontend, live connectors, pilot data or observability expansion;
  or
- modification of frozen contracts 34 through 42 or Flyway V1.

## Consequences

### Positive consequences

- Recommendation existence has one exact deterministic readiness boundary.
- The stored savings horizon is unambiguous.
- Every monetary input is traceable to accepted Evidence or an explicit
  assumption.
- Policy provenance is queryable through linked Evidence without schema drift.
- Confidence and risk cannot be supplied or altered by AI or adapters.
- Retry and concurrency cannot create two Recommendations or erase later
  business state.
- Sprint 3.3.1 can explain a stable Recommendation without becoming business
  authority.

### Accepted limitations

- The policy supports only `DRC-AOA-001-v1` and the canonical evidence pack.
- The canonical monetary values are fixed for the first local value-loop
  demonstration.
- The Recommendation stores annualized savings only; monthly inputs and
  assumptions are derived from linked Evidence until a later authorized read
  model exists.
- Missing quality Evidence has exactly one reduced-confidence Medium-risk
  branch.
- Technical repository mechanics remain deferred to the Sprint 3.3
  pre-implementation review.

## Required Sprint 3.3 Evidence

Sprint 3.3 may be certified only when automated tests prove:

1. a ready `DRC-AOA-001` Decision and complete Evidence pack produce one
   `MODEL_CHANGE` Recommendation;
2. the exact action and deterministic reason are preserved;
3. monetary inputs produce `EUR 1620.00` monthly recovery and
   `EUR 19440.00` annualized recovery;
4. `Recommendation.estimatedSavings` is exactly `EUR 19440.00`;
5. the complete pack produces confidence `92` and risk `LOW`;
6. only missing `E-AI-005` produces confidence `90` and risk `MEDIUM`;
7. any other missing or invalid mandatory input persists no Recommendation;
8. the Recommendation references all support, assumption and policy Evidence;
9. policy version `DRC-AOA-001-v1` is preserved through linked Evidence;
10. no AI provider is invoked;
11. identical retry returns the existing Recommendation without mutation;
12. conflicting retry cannot overwrite the existing Recommendation;
13. equivalent and conflicting concurrency result in one authoritative
    Recommendation;
14. Recommendation persistence and Decision attachment are atomic against real
    PostgreSQL; and
15. no Review, Ledger, realized value, REST behavior or schema change is
    introduced.

### Deliberately deferred implementation decision

The exact atomic Recommendation-creation technique remains deferred to the
Sprint 3.3 pre-implementation review. This is not an unresolved functional
requirement: every observable outcome is frozen above. Only the internal
technical means of producing those outcomes remains unselected.
