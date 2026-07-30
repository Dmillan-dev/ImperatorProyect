# model

## Purpose

Represent the PostgreSQL persistence model separately from the domain.

These records describe adapter-side storage shapes only. They are not domain
entities, REST DTOs, query models, commands or results.

## Who Uses This Folder

- Sprint 2.7.2 Persistence Model.
- Future PostgreSQL mappers in Sprint 2.7.3.
- Future PostgreSQL repository implementations in Sprint 2.7.4.

## Contains

- `PostgresEvidenceRecord`
- `PostgresDecisionRecord`
- `PostgresDecisionEvidenceRecord`
- `PostgresRecommendationRecord`
- `PostgresRecommendationEvidenceRecord`
- `PostgresLedgerEntryRecord`
- `PostgresLedgerEvidenceSnapshotRecord`

## Never Contains

- Domain imports.
- Application imports.
- REST DTOs.
- SQL queries.
- Migration DDL.
- Spring annotations.
- JPA annotations.
- JDBC clients.
- Business rules.
- Mapper logic.
- Repository logic.

## Domain Translation Rule

Forbidden:

```text
Persistence Record -> REST DTO
Persistence Record -> Domain -> REST DTO
```

Required:

```text
Persistence Record -> Mapper -> Domain Entity
Domain Entity -> Mapper -> Persistence Record
```

The mapper belongs to the PostgreSQL adapter and will be created only in Sprint
2.7.3.
