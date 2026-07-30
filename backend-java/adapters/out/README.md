# out

## Purpose

Implement outbound ports for external technology without changing Domain or
Application contracts.

## Who Uses This Folder

- Persistence, provider and future connector adapter sprints.
- Architecture Guardian to verify outbound dependencies point inward to ports.

## Contains

- Adapter modules grouped by technology.
- Implementations of `backend-java/ports/out` interfaces.

## Never Contains

- Inbound REST controllers.
- Domain entities rewritten as persistence models.
- Application use-case orchestration.
- Business decisions.
- Secrets.
