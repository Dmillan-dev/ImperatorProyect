# appendledgerentry

## Purpose

Record authorized implementation and result-validation facts in the Decision
Ledger without modifying Decision state or Recommendation truth.

## Who Uses This Folder

- Future inbound adapters that need to append audited decision history.
- Architecture Guardian to verify external implementation and validation
  remain append-only Ledger facts.
- Quality Agent to verify replay, authorization and linear sequencing.

## Contains

- `AppendLedgerEntryCommand`
- `AppendLedgerEntryResult`
- `AppendLedgerEntryUseCase`
- Traceability checks against Decision, Recommendation, Evidence and previous
  LedgerEntry references.
- Application authorization for `PLATFORM_ENGINEER` implementation markers and
  `FINANCE` result validation.
- Deterministic realized-recovery validation through the Domain policy.
- Idempotent operation replay and strict current-head enforcement.

## Never Contains

- Decision state transitions.
- Recommendation mutation.
- Business Value aggregation.
- AI provider calls.
- REST, JSON, HTTP, Spring, JPA or SQL.
- Connector-specific logic.
