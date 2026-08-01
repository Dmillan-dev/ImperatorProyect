# createdecision

## Purpose

Create a traceable decision from previously imported evidence.

## Who Uses This Folder

- Future inbound ports or adapters that request decision creation.
- Architecture Guardian to verify traceability starts from evidence.
- Quality Agent to keep use-case orchestration focused.

## Contains

- `CreateDecisionUseCase`.
- `CreateDecisionCommand`.
- `CreateDecisionResult`.

Sprint 2.5.2 status:
- One use case only.
- Reads existing evidence through `EvidenceRepository`.
- Creates a `Decision` using the evidence correlation key as case ID.
- Saves the decision through `DecisionRepository`.
- Does not create recommendations or ledger entries.

Sprint 3.2 behavior:

- accepts only the frozen eligible originating Evidence for `DRC-AOA-001`;
- creates the initial aggregate in `CREATED` state through atomic
  `DecisionRepository.createIfAbsent`;
- returns the authoritative persisted state for an identical retry;
- rejects a reused identity with different immutable creation attributes;
- never resets a Decision that has already progressed;
- still creates no Recommendation, ROI, Review or Ledger behavior.

## Never Contains

- REST controllers.
- Database adapters.
- SQL.
- Recommendation generation.
- Review workflow.
- Ledger append orchestration.
- ROI calculation.
- Framework annotations.

