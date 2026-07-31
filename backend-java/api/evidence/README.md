# Evidence REST Adapter

## Purpose

Own the HTTP adapter for bounded JSONL evidence import.

## Who Uses This Folder

- Spring Boot component scanning registers the evidence controller.
- HTTP clients call the versioned evidence route.
- Implementation and quality agents verify the REST boundary.

## Contains

- `EvidenceController`.
- The `POST /api/v1/evidence/import` route mapping.
- Strict NDJSON parsing, transport validation and normalization.
- Safe per-line accepted or rejected results.
- Mapping into the existing `ImportEvidenceInputPort`.

## Never Contains

- Domain entities created directly by the REST adapter.
- Batch aggregates or batch transaction behavior.
- Repository, JDBC or SQL access.
- Raw event, raw prompt or raw completion persistence.
- Decision, recommendation, ROI, review or ledger behavior.

## Current Boundary

The route consumes only `application/x-ndjson`. Each non-terminal line is one
independent event and executes one `ImportEvidenceInputPort` call and one
transaction. Valid lines remain committed when another line is rejected.

Stable event UUIDs are the per-line idempotency identifiers. Replays return a
safe `DUPLICATE` line result and do not create another Evidence row.

Request limits are:

- 5 MiB per request;
- 10,000 lines per request;
- 64 KiB per line;
- 32 flat string metadata entries per line.

Request-wide failures retain the four-field REST error envelope and
`X-Correlation-ID`. Per-line failures return HTTP `200` with aggregate counts
and safe item reasons. Only normalized Evidence is persisted.
