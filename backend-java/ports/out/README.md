# out

## Purpose

Definir lo que la aplicacion necesitara del exterior sin decidir como se
implementa.

## Who Uses This Folder

- Application layer use cases.
- Architecture Guardian to verify ports stay provider-neutral.
- Persistence adapters in future sprints.

## Contains

- Repository interfaces for domain persistence and retrieval needs.
- Append-only ledger port.
- Explanation provider port for bounded natural-language explanation.

Sprint 2.5.3 status:
- `EvidenceRepository`.
- `DecisionRepository`.
- `RecommendationRepository`.
- `LedgerRepository`.
- `ExplanationProvider`.
- No `BusinessValueProjectionPort` until application code proves it is needed.

## Never Contains

- PostgreSQL.
- Spring Data.
- JPA entities.
- SQL queries.
- REST DTOs.
- Provider SDKs.
- Implementations.
- Provider-specific model names.
- Raw prompts, completions, secrets or payloads.
