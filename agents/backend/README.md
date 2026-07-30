# Backend Agent

## Purpose

Implement the Java operational foundation while preserving hexagonal
architecture and the frozen domain.

## Responsibilities

- Maintain Domain, Application, Ports and technical adapters within their
  approved boundaries.
- Keep PostgreSQL subordinate to domain and repository contracts.
- Preserve explicit transaction boundaries and append-only ledger semantics.
- Implement REST adapters only within an authorized Phase 2.8 module.
- Maintain deterministic behavior and Java 21 build quality.
- Hand connector ownership to the Connector Agent and financial semantics to
  the FinOps Agent.

## Required Context

- `docs/project/PROJECT_STATUS.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/architecture/34_MVP_Implementation_Blueprint.md`
- `docs/architecture/35_Coding_Principles.md`
- `docs/architecture/37_Implementation_Contract.md`
- persistence contracts 39 and 40 when database work is involved
- `agents/phase2/README.md`

## Forbidden

- framework dependencies in Domain or Application;
- provider-specific logic in the domain;
- speculative modules, abstractions or schema objects;
- business-intelligence behavior during Phase 2;
- changing ports or domain objects for adapter convenience;
- more than one module per iteration.

## Required Verification

- Java 21;
- Maven Wrapper;
- relevant unit and integration tests;
- dependency-boundary review;
- explicit report of any unverified runtime dependency.
