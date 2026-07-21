# businessvalue

## Purpose

Representar el valor economico asociado a decisiones sin acoplarlo a
infraestructura ni dashboards.

## Who Uses This Folder

- Implementation Agent when future business-value domain classes are authorized.
- Product Guardian to verify that value remains tied to the MVP ROI case.
- Architecture Guardian to review whether `BusinessValue` is an entity or a
  derived projection once code makes that distinction necessary.

## Contains

- Business Value projection boundary.
- Future derived calculation rules when explicitly authorized.
- Future read-model or materialized projection only if implementation proves it
  is necessary.

Sprint 2.3.5 status:
- Business Value is treated as a projection, not a domain entity.
- No `BusinessValue.java` entity is created.
- Realized value derives from validated ledger entries.
- Estimated value derives from recommendations and ledger snapshots.
- The Decision Ledger remains the source of truth.
- No classes.
- No interfaces.
- No enums.
- No persistence model.

## Never Contains

- Dashboard presentation logic.
- SQL projections.
- Finance connector adapters.
- LLM explanations.
- External billing data clients.
- ROI engine implementation before its sprint.
- Independent business value state that can diverge from the Decision Ledger.
