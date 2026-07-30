# importevidence

## Purpose

Import normalized evidence as canonical proof for future decision cases.

## Who Uses This Folder

- Future inbound ports or adapters that submit normalized evidence.
- Architecture Guardian to verify import orchestration remains thin.
- Quality Agent to detect provider-specific leakage.

## Contains

- `ImportEvidenceUseCase`.
- `ImportEvidenceCommand`.
- `ImportEvidenceResult`.

Sprint 2.5.1 status:
- One use case only.
- Saves evidence through `EvidenceRepository`.
- Returns import result.
- Does not create decisions, recommendations or ledger entries.

## Never Contains

- Connector-specific payload models.
- REST controllers.
- Database adapters.
- SQL.
- Decision creation.
- Recommendation generation.
- Ledger append orchestration.
- ROI calculation.

