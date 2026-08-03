# GitHub Outbound Adapter

## Purpose

Read one bounded GitHub repository and translate approved source facts into
canonical Evidence candidates under D089.

## Used By

- `EvidenceSourcePort` runtime composition;
- Sprint 4 offline provider-contract tests; and
- PostgreSQL Evidence synchronization certification.

## Contains

- read-only GitHub REST transport;
- exact repository, pull request, review and deployment mapping;
- pagination, retry, timeout, rate-limit and redirect controls;
- deterministic UUIDv5 Evidence identity; and
- provider-specific parsing and failure semantics.

## Never Contains

- Domain decisions or provider-shaped Domain entities;
- Recommendation, ROI, Business Value or Ledger behavior;
- repository writes, webhooks, GraphQL or background scheduling;
- persisted raw GitHub payloads; or
- credentials, source code, diffs, comments or prompt instructions.
