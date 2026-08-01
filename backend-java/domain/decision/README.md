# decision

## Purpose

Represent the core of the IMPERATOR decision lifecycle.

## Who Uses This Folder

- Implementation Agent when future decision-domain classes are authorized.
- Architecture Guardian to verify decision lifecycle boundaries.
- Quality Agent to prevent anemic DTO-style modeling.

## Contains

- `Decision` entity.
- `Recommendation` entity.
- `DrcAoa001RecommendationPolicy` deterministic Domain policy.
- Decision lifecycle state.
- Evidence and recommendation references by domain ID.
- Review outcome state: approved, rejected or deferred.

Current status:
- `Decision` and `Recommendation` entities only.
- Equality by `DecisionId`.
- Equality by `RecommendationId`.
- State transitions protected by entity invariants.
- No interfaces.
- No infrastructure dependencies.
- No ledger write behavior.
- No authorization policy enforcement.
- Recommendations propose, estimate and explain only.
- D082 freezes the first `MODEL_CHANGE` action and reason.
- The policy validates the canonical Evidence and assumption pack.
- The policy calculates EUR annualized estimated savings with decimal
  `HALF_EVEN` arithmetic.
- Confidence is `92`/Low risk with quality Evidence or `90`/Medium risk when
  only the optional quality Evidence is absent.
- No caller, adapter, repository or AI provider chooses policy outputs.

## Never Contains

- Controllers.
- REST DTOs.
- Persistence entities.
- AI provider calls.
- configurable or speculative ROI policies.
- approval, rejection or execution behavior inside `Recommendation`.
- Connector-specific objects.
- Frontend state.
