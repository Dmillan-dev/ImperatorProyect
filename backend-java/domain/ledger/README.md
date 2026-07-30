# ledger

## Purpose

Represent the immutable record of decisions, reviews and relevant changes.

## Who Uses This Folder

- Implementation Agent when future ledger-domain classes are authorized.
- Architecture Guardian to enforce append-only domain semantics.
- Quality Agent to verify traceability-oriented naming.

## Contains

- `LedgerEntry` entity.
- `LedgerEntryType` domain value object.
- Append-only historical fact invariants.
- Conservative metadata boundary.

Sprint 2.3.4 status:
- Ledger entries are immutable after creation.
- Equality by `LedgerEntryId`.
- Event type is not a free string.
- No update or delete behavior.
- No cryptographic sealing.
- No infrastructure dependencies.
- No current-state ownership; current state belongs to `Decision`.

## Never Contains

- SQL append implementation.
- Database transaction code.
- Audit logging infrastructure.
- Controllers.
- External event brokers.
- Mutable history rewriting behavior.
- Raw provider payloads, prompts, completions, credentials or secrets.
