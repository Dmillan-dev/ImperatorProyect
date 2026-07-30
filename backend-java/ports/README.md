# ports

## Purpose

Define the hexagonal contracts that separate Domain and Application from
external adapters.

## Who Uses This Folder

- Implementation Agent when ports are explicitly authorized.
- Architecture Guardian to verify dependency direction.
- Future adapters that call inbound contracts or implement outbound contracts.

## Contains

- `out/` outbound ports.
- `in/` inbound ports.

Sprint 2.6.1 status:
- Outbound ports.
- Inbound ports.
- Java interfaces only.
- No implementations.
- No frameworks.

## Never Contains

- Spring annotations.
- JPA annotations.
- PostgreSQL code.
- HTTP controllers.
- Adapter implementations.
- Business logic not already expressed by domain/application.
