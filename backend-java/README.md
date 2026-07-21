# backend-java

## Purpose

Motor principal del dominio y la aplicacion Java de IMPERATOR.

## Who Uses This Folder

- Implementation Agent during Java backend sprints.
- Architecture Guardian to verify hexagonal boundaries.
- Quality Agent to review domain, application and adapter code once authorized.

## Contains

- Future Java domain model.
- Future application use cases.
- Future ports and adapters.
- Future API layer when explicitly authorized by sprint scope.

Sprint 1 status:
- Repository shell only.
- No Java source files.
- No Maven or Gradle build files.
- No Spring Boot application.
- No controllers, services, repositories, adapters or domain implementation.

## Never Contains

- Python services.
- React frontend code.
- Docker or infrastructure runtime files.
- Prompt templates or AI provider prompts.
- Database migrations.
- Connector secrets or credentials.

## Authorized Next Use

Sprint 2 may create the pure domain layer only after Sprint 1 is accepted.
