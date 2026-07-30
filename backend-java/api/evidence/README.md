# Evidence REST Adapter

## Purpose

Own the HTTP entry-point shell for evidence import.

## Who Uses This Folder

- Spring Boot component scanning registers the evidence controller.
- HTTP clients call the versioned evidence route.
- Implementation and quality agents verify the REST boundary.

## Contains

- `EvidenceController`.
- The `POST /api/v1/evidence/import` route mapping.
- Delegation to the shared not-implemented error contract.

## Never Contains

- Request DTOs or payload parsing during Sprint 2.8.2.
- Application use-case calls.
- Persistence, transactions or database access.
- Evidence normalization, idempotency or business rules.

## Current Boundary

The route always raises the existing controlled not-implemented exception. The
global REST error handler returns HTTP `501`, and the existing correlation
filter propagates `X-Correlation-ID`.
