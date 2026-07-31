# Business Value REST Adapter

## Purpose

Own the HTTP entry-point shell for the Business Value read surface.

## Who Uses This Folder

- Spring Boot component scanning registers the Business Value controller.
- HTTP clients call the versioned Business Value route.
- Implementation and quality agents verify the REST boundary.

## Contains

- `BusinessValueController`.
- The `GET /api/v1/business-value` route mapping.
- Delegation to the shared not-implemented error contract.

## Never Contains

- Request or response DTOs during Phase 2 route-shell sprints.
- Pagination or filtering.
- Application use-case or port calls.
- Persistence, transactions or database access.
- ROI or realized-value calculation.
- Fake Business Value output.

## Current Boundary

The route always raises the existing controlled not-implemented exception. The
global REST error handler returns HTTP `501`, and the existing correlation
filter propagates `X-Correlation-ID`.

No Business Value detail route is part of the frozen MVP route surface.
