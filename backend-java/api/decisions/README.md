# Decision REST Adapter

## Purpose

Own the HTTP entry-point shells for reading decisions.

## Who Uses This Folder

- Spring Boot component scanning registers the decision controller.
- HTTP clients call the versioned decision routes.
- Implementation and quality agents verify the REST boundary.

## Contains

- `DecisionController`.
- `DecisionContextController`.
- The `GET /api/v1/decisions` route mapping.
- The `GET /api/v1/decisions/{id}` route mapping.
- The `GET /api/v1/decisions/{id}/timeline` route mapping.
- The `GET /api/v1/decisions/{id}/evidence` route mapping.
- The `GET /api/v1/decisions/{id}/roi` route mapping.
- Delegation to the shared not-implemented error contract.

## Never Contains

- Request or response DTOs during Phase 2 route-shell sprints.
- Application use-case calls.
- Review commands or approval workflow behavior.
- Persistence, transactions or database access.
- Recommendation, ROI, ledger or AI behavior.

## Current Boundary

All routes always raise the existing controlled not-implemented exception. The
global REST error handler returns HTTP `501`, and the existing correlation
filter propagates `X-Correlation-ID`.

Review actions are intentionally absent. The canonical API assigns them to
future append-only ledger command routes.
