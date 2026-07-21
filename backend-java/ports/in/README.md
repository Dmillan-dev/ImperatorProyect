# in

## Purpose

Definir como entra el mundo exterior a la capa de aplicacion de IMPERATOR sin
exponer REST, HTTP, JSON, CLI, UI ni detalles de adapters.

## Who Uses This Folder

- Future inbound adapters that call application capabilities.
- Application use cases that implement these contracts.
- Architecture Guardian to verify adapters depend on contracts, not concrete
  use case classes.

## Contains

- Input port interfaces for authorized MVP capabilities.
- Method signatures using application Commands and Results.
- Contracts governed by `backend-java/application/APPLICATION_DATA_BOUNDARY_POLICY.md`.

Sprint 2.6.1 status:
- `ImportEvidenceInputPort`.
- `CreateDecisionInputPort`.
- `GenerateRecommendationInputPort`.
- `ReviewDecisionInputPort`.
- `AppendLedgerEntryInputPort`.
- No implementations.

## Never Contains

- REST controllers.
- HTTP request or response DTOs.
- JSON annotations.
- Spring annotations.
- CLI parsing.
- React state.
- Repository implementations.
- Business logic.
