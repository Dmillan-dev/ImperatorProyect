# domain

## Purpose

Expresar el lenguaje central de IMPERATOR como dominio Java puro.

## Who Uses This Folder

- Implementation Agent during Sprint 2 domain microtasks.
- Architecture Guardian to enforce Domain Purity Score.
- Quality Agent to verify that domain concepts are not DTOs or framework models.

## Contains

- `shared/`
- `evidence/`
- `decision/`
- `ledger/`
- `businessvalue/`

Current Sprint 2 status:
- Domain package skeleton exists.
- Shared value objects exist.
- Evidence, Decision, Recommendation and LedgerEntry domain objects exist.
- BusinessValue remains a projection decision, not a domain entity.
- No framework annotations.
- No persistence mappings.
- No provider integrations.

## Never Contains

- Spring.
- JPA.
- Lombok.
- Jackson.
- REST, HTTP or API controllers.
- SQL or persistence mappings.
- Kafka or messaging adapters.
- Docker or infrastructure files.
- External connector SDKs.
- AI prompts or provider clients.

## Domain Purity Score

Target for Sprint 2: 100%.
