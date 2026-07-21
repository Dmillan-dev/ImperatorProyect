# createdecision

## Purpose

Crear una decision trazable a partir de evidencia ya importada.

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

## Never Contains

- REST controllers.
- Database adapters.
- SQL.
- Recommendation generation.
- Review workflow.
- Ledger append orchestration.
- ROI calculation.
- Framework annotations.

