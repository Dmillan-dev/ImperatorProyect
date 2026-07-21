# shared

## Purpose

Contener primitivas compartidas del dominio sin convertirlas en utilidades
tecnicas genericas.

## Who Uses This Folder

- Implementation Agent during Sprint 2.2 value-object work.
- Architecture Guardian to verify Java purity.
- Quality Agent to prevent framework or infrastructure leakage.

## Contains

- DecisionId.
- EvidenceId.
- RecommendationId.
- LedgerEntryId.
- UserId.
- Money.
- Currency.
- Timestamp.
- Duration.
- Severity.
- ROIAmount.
- ROIConfidence.
- RecommendationType.
- DecisionStatus.

Sprint 2.2 status:
- Java value objects only.
- Immutable records.
- Constructor validation.
- Value equality.
- No interfaces.
- No infrastructure dependencies.

## Never Contains

- Framework utilities.
- HTTP helpers.
- Serialization helpers.
- Database helpers.
- Logging infrastructure.
- Connector SDK abstractions.
- AI prompt utilities.
