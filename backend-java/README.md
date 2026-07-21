# backend-java

## Purpose

Motor principal del dominio y la aplicacion Java de IMPERATOR.

## Who Uses This Folder

- Implementation Agent during Java backend sprints.
- Architecture Guardian to verify hexagonal boundaries.
- Quality Agent to review domain, application and adapter code once authorized.

## Contains

- Java domain model.
- Application use cases.
- Inbound and outbound ports.
- Application exceptions and data-boundary policy.
- PostgreSQL outbound adapter skeleton.
- Future adapter implementations when explicitly authorized by sprint scope.
- Future API layer when explicitly authorized by sprint scope.

Current Phase 2 status:
- Pure Java domain foundation exists.
- Application use cases exist.
- Inbound and outbound ports exist.
- Application exceptions exist.
- Application data-boundary policy exists.
- PostgreSQL outbound adapter skeleton exists.
- PostgreSQL persistence records exist.
- PostgreSQL mapper foundation exists.
- No Maven or Gradle build files.
- No Spring Boot application.
- No controllers.
- No PostgreSQL implementation behavior yet.
- No SQL, JPA, JDBC or migrations yet.

## Never Contains

- Python services.
- React frontend code.
- Docker or infrastructure runtime files.
- Prompt templates or AI provider prompts.
- Database migrations.
- Connector secrets or credentials.

## Authorized Next Use

Next Sprint 2.7 microtask may create PostgreSQL repository behavior only after
mapper review passes. PostgreSQL must conform to the existing domain and ports;
the domain must not be modified to accommodate PostgreSQL.
