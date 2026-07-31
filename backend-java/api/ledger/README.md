# Ledger REST Adapter

## Purpose

Own the HTTP entry-point shells for reading Decision Ledger history and
addressing its canonical commands.

## Who Uses This Folder

- Spring Boot component scanning registers the Ledger controller.
- HTTP clients call the versioned Ledger read and command routes.
- Implementation and quality agents verify the REST boundary.

## Contains

- `LedgerController`.
- The `GET /api/v1/ledger` route mapping.
- The `GET /api/v1/decisions/{id}/ledger` route mapping.
- The five canonical command mappings under
  `POST /api/v1/decisions/{id}/ledger`.
- Delegation to the shared not-implemented error contract.

## Never Contains

- The broader Ledger entry-detail route.
- Request or response DTOs during Phase 2 route-shell sprints.
- Request-body binding, parsing or validation.
- Path-variable binding, parsing or validation.
- Idempotency or authorization behavior.
- Application use-case or port calls.
- Persistence, transactions or database access.
- Ledger retrieval, mutation, append, snapshot or state-transition behavior.

## Current Boundary

All read and command routes always raise the existing controlled
not-implemented exception. The global REST error handler returns HTTP `501`,
and the existing correlation filter propagates `X-Correlation-ID`.

The broader `GET /api/v1/ledger/{entryId}` route remains intentionally
unmapped. Recommendation review aliases also remain outside the MVP route
surface.
