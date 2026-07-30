# appendledgerentry

## Purpose

Record a historical fact in the Decision Ledger without modifying Decision
state or executing additional business rules.

## Who Uses This Folder

- Implementation Agent during Sprint 2.5.5.
- Future inbound adapters that need to append audited decision history.
- Architecture Guardian to verify the review and ledger capabilities remain
  separated.

## Contains

- `AppendLedgerEntryCommand`
- `AppendLedgerEntryResult`
- `AppendLedgerEntryUseCase`
- Traceability checks against Decision, Recommendation, Evidence and previous
  LedgerEntry references.

## Never Contains

- Decision state transitions.
- Recommendation mutation.
- Business Value calculation.
- AI provider calls.
- REST, JSON, HTTP, Spring, JPA or SQL.
- Connector-specific logic.
