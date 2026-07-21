# decision

## Purpose

Representar el nucleo del ciclo de decision de IMPERATOR.

## Who Uses This Folder

- Implementation Agent when future decision-domain classes are authorized.
- Architecture Guardian to verify decision lifecycle boundaries.
- Quality Agent to prevent anemic DTO-style modeling.

## Contains

- `Decision` entity.
- `Recommendation` entity.
- Decision lifecycle state.
- Evidence and recommendation references by domain ID.
- Review outcome state: approved, rejected or deferred.

Sprint 2.3.3 status:
- `Decision` and `Recommendation` entities only.
- Equality by `DecisionId`.
- Equality by `RecommendationId`.
- State transitions protected by entity invariants.
- No interfaces.
- No infrastructure dependencies.
- No ledger write behavior.
- No authorization policy enforcement.
- Recommendations propose, estimate and explain only.

## Never Contains

- Controllers.
- REST DTOs.
- Persistence entities.
- AI provider calls.
- ROI calculation implementation.
- approval, rejection or execution behavior inside `Recommendation`.
- Connector-specific objects.
- Frontend state.
