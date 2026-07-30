# adapters

## Purpose

Contain technical implementations of application-defined ports. Adapters do
not define the domain; they remain subordinate to it.

## Who Uses This Folder

- Implementation Agent during adapter-specific sprints.
- Architecture Guardian to verify infrastructure depends inward through ports.
- Future runtime wiring when frameworks are explicitly authorized.

## Contains

- `out/` outbound adapter implementations.
- No parallel HTTP inbound tree. The accepted REST adapter location is
  `backend-java/api` with packages under `imperator.api`.

Sprint 2.7.7 status:
- PostgreSQL JDBC repositories implement the frozen outbound ports.
- Persistence records and mappers translate without leaking PostgreSQL into
  Domain or Application.
- `PostgresTransactionRunner` provides the explicit transaction port
  implementation.
- Repository behavior, schema constraints, aggregate atomicity and all five
  use-case transaction boundaries are certified against PostgreSQL 18.2.
- No Spring or framework runtime inside this adapter subtree.

## Never Contains

- Domain changes for adapter convenience.
- Business rules not already expressed by domain/application.
- REST controllers unless an inbound adapter sprint authorizes them.
- Provider SDK calls outside their specific adapter.
- Secrets or runtime credentials.
