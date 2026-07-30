# Recommendation REST Adapter

## Purpose

Own the HTTP entry-point shell for reading one recommendation.

## Who Uses This Folder

- Spring Boot component scanning registers the recommendation controller.
- HTTP clients call the versioned recommendation detail route.
- Implementation and quality agents verify the REST boundary.

## Contains

- `RecommendationController`.
- The `GET /api/v1/recommendations/{id}` route mapping.
- Delegation to the shared not-implemented error contract.

## Never Contains

- The post-MVP recommendation collection route.
- Recommendation creation or review commands.
- Request or response DTOs during Phase 2 route-shell sprints.
- Application use-case or port calls.
- Persistence, transactions or database access.
- Recommendation generation, ROI, ledger or AI behavior.

## Current Boundary

The detail route always raises the existing controlled not-implemented
exception. The global REST error handler returns HTTP `501`, and the existing
correlation filter propagates `X-Correlation-ID`.

The collection route remains intentionally unmapped and returns HTTP `404`.
