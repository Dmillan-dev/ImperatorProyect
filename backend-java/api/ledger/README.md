# Ledger REST Adapter

## Purpose

Own the HTTP entry-point shells for reading Decision Ledger history.

## Who Uses This Folder

- Spring Boot component scanning registers the Ledger controller.
- HTTP clients call the versioned Ledger read routes.
- Implementation and quality agents verify the REST boundary.

## Contains

- `LedgerController`.
- The `GET /api/v1/ledger` route mapping.
- The `GET /api/v1/decisions/{id}/ledger` route mapping.
- Delegation to the shared not-implemented error contract.

## Never Contains

- The broader Ledger entry-detail route.
- Ledger approval, rejection, deferral, implementation or validation commands.
- Request or response DTOs during Phase 2 route-shell sprints.
- Path-variable binding, parsing or validation.
- Application use-case or port calls.
- Persistence, transactions or database access.
- Ledger retrieval, mutation or append behavior.

## Current Boundary

Both read routes always raise the existing controlled not-implemented
exception. The global REST error handler returns HTTP `501`, and the existing
correlation filter propagates `X-Correlation-ID`.

The broader `GET /api/v1/ledger/{entryId}` route remains intentionally
unmapped. The five MVP Ledger commands remain reserved for Sprint 2.8.7.
