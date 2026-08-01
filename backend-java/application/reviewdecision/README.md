# reviewdecision

## Purpose

Apply a human review action to a decision with an existing recommendation.

## Who Uses This Folder

- Future inbound ports or adapters that submit review actions.
- Architecture Guardian to verify review orchestration stays outside REST.
- Quality Agent to verify the Decision transition and matching Ledger append
  remain atomic.

## Contains

- `ReviewDecisionUseCase`.
- `ReviewDecisionCommand`.
- `ReviewDecisionResult`.
- `ReviewDecisionAction`.

Sprint 3.4 status:
- Accepts only `approve`, `reject` or `defer` as public Application actions.
- Treats entry into `UNDER_REVIEW` as an internal Domain transition.
- Locks and loads the authoritative Decision through
  `DecisionRepository.findByIdForUpdate`.
- Loads the persisted Recommendation and derives the review snapshot from
  authoritative persisted objects.
- Authorizes the Phase 1 role and assigned approver at the Application
  boundary.
- Persists the Decision outcome and appends its immutable Ledger entry in one
  transaction.
- Resolves identical operation replay and rejects conflicting or duplicate
  governance commands.
- Preserves one strictly linear Ledger history per Decision.

## Never Contains

- REST controllers.
- Database adapters.
- SQL.
- JWT or RBAC implementation.
- Recommendation generation.
- ROI calculation.
- Framework annotations.

