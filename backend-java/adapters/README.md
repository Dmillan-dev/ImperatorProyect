# adapters

## Purpose

Contener implementaciones tecnicas que se adaptan a los puertos definidos por
la aplicacion. Los adapters no definen el dominio; se subordinan a el.

## Who Uses This Folder

- Implementation Agent during adapter-specific sprints.
- Architecture Guardian to verify infrastructure depends inward through ports.
- Future runtime wiring when frameworks are explicitly authorized.

## Contains

- `out/` outbound adapter implementations.
- Future `in/` inbound adapters only when REST, CLI or other entry points are
  explicitly authorized.

Sprint 2.7.7 status:
- PostgreSQL JDBC repositories implement the frozen outbound ports.
- Persistence records and mappers translate without leaking PostgreSQL into
  Domain or Application.
- `PostgresTransactionRunner` provides the explicit transaction port
  implementation.
- Repository behavior, schema constraints, aggregate atomicity and all five
  use-case transaction boundaries are certified against PostgreSQL 18.2.
- No Spring or framework runtime.

## Never Contains

- Domain changes for adapter convenience.
- Business rules not already expressed by domain/application.
- REST controllers unless an inbound adapter sprint authorizes them.
- Provider SDK calls outside their specific adapter.
- Secrets or runtime credentials.
