# mapper

## Purpose

Traducir entre el dominio existente y los records de persistencia PostgreSQL sin
introducir comportamiento de repositorio, SQL, frameworks o reglas de negocio.

## Who Uses This Folder

- Sprint 2.7.3 PostgreSQL Mapper Foundation.
- Future PostgreSQL repository implementations in Sprint 2.7.4.
- Architecture Guardian to verify the Domain Translation Rule.

## Contains

- `PostgresEvidenceMapper`
- `PostgresDecisionMapper`
- `PostgresRecommendationMapper`
- `PostgresLedgerEntryMapper`

## Mapper Purity Rule

Mappers may depend only on:

- domain objects and value objects;
- PostgreSQL persistence records;
- Java standard library conversion types.

Mappers must not depend on:

- repositories;
- ports;
- services;
- providers;
- HTTP;
- SQL;
- Spring;
- loggers.

Mappers must not:

- calculate ROI;
- choose recommendations;
- approve, reject or defer decisions;
- append ledger entries;
- call repositories;
- call providers;
- create REST DTOs;
- hide business rules.

## Decision Reconstitution Note

`PostgresDecisionMapper` reconstructs persisted Decision state through public
domain behavior. It does not use reflection, setters, persistence annotations or
domain changes for PostgreSQL convenience.

If this reconstitution becomes complex, the next review gate must decide from
domain evidence whether the domain needs an explicit reconstitution factory.
PostgreSQL alone is not a valid reason to change the domain.

## Never Contains

- SQL queries.
- Database clients.
- Transaction boundaries.
- JPA or Spring annotations.
- REST request or response DTOs.
- Repository implementation logic.
