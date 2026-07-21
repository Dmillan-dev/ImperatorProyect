# reviewdecision

## Purpose

Aplicar una accion humana de revision sobre una decision con recomendacion
existente.

## Who Uses This Folder

- Future inbound ports or adapters that submit review actions.
- Architecture Guardian to verify review orchestration stays outside REST.
- Quality Agent to ensure ledger append remains a separate capability.

## Contains

- `ReviewDecisionUseCase`.
- `ReviewDecisionCommand`.
- `ReviewDecisionResult`.
- `ReviewDecisionAction`.

Sprint 2.5.4 status:
- One review capability only.
- Loads an existing decision and recommendation through ports.
- Applies `start_review`, `approve`, `reject` or `defer`.
- Saves the updated decision through `DecisionRepository`.
- Returns whether a ledger entry is required next.
- Does not append ledger entries.

## Never Contains

- REST controllers.
- Database adapters.
- SQL.
- JWT or RBAC implementation.
- Ledger append orchestration.
- Recommendation generation.
- ROI calculation.
- Framework annotations.

