# IMPERATOR Project Context

Status: **Canonical English context for AI agents**

## Product

IMPERATOR is an Operating System for Operational Intelligence and an Enterprise
Decision Intelligence Platform. It manages decisions across cloud, code and AI
systems and helps leadership identify, explain and approve actions that recover
operational value.

The MVP rule is:

```text
One Decision. One Timeline. One ROI.
```

The initial paid wedge is the Decision Recovery Workflow for AI and cloud
spend. The first case is `DRC-AOA-001`, AI Onboarding Assistant Recovery.

## Current Lifecycle State

- Phase 0 strategy and architecture readiness: complete.
- Phase 1 limited-MVP contract dossier: complete.
- Phase 2 Platform Foundation: complete under D079.
- PostgreSQL persistence: certified.
- Spring Boot web runtime: complete.
- REST error and HTTP correlation contract: complete.
- REST adapter foundation: complete with 15 preserved MVP routes.
- Phase 3 First Business Value Loop: active.
- Functional runtime composition: certified against PostgreSQL 18.2 and complete.
- JSONL Evidence Import: certified and complete with independent per-line processing.
- Remaining functional REST route shells: implemented and controlled.
- Current gate: Sprint 3.2 - Deterministic Decision Creation.
- Sprints 2.9 through 2.13: deferred, not completed.

The live state is maintained in:

- `docs/project/PROJECT_STATUS.md`;
- `docs/project/PHASE_AND_SPRINT_MAP.md`.

## Core Product Flow

```text
Operational Event
-> Normalized Evidence
-> Decision ROI Case
-> ROI View
-> Deterministic Recommendation
-> AI Explanation
-> Human Review
-> Append-only Decision Ledger
-> Result Validation
-> Realized Business Value
```

Phase 2 built the technical foundation for this flow. Phase 3 now implements
the single authorized business value loop under D079.

## Canonical Boundaries

- Java owns domain and application truth.
- PostgreSQL is an outbound adapter.
- Python may later implement the explanation-provider boundary.
- AI explains prepared deterministic context; it does not decide or mutate.
- React calls REST APIs, not providers.
- Connectors normalize evidence; they do not calculate ROI or recommend.
- Ledger history is append-only.
- Estimated value is not realized Business Value.
- Human authority controls approve, reject, defer and result validation.

## Current Java Stack

- Java 21;
- Maven Wrapper;
- Hexagonal Architecture;
- Domain-Driven Design;
- pure JDBC PostgreSQL adapters;
- Flyway migrations;
- Spring Boot web runtime;
- no JPA;
- six product REST controllers preserving 15 controlled routes;
- functional JSONL Evidence import wired through Application and PostgreSQL;
- remaining Decision, Recommendation, ROI, Review, Ledger and Result Validation
  business behavior not implemented yet;
- no security runtime yet.

The implemented Java source root is `backend-java`, with packages under
`imperator.*`. REST packages live under `imperator.api.*`. Public product REST
routes use `/api/v1`.

## MVP Evidence Domains

The product story uses four evidence domains:

1. Business Context - Jira.
2. Code and Deployment - GitHub.
3. Infrastructure and Cost - AWS.
4. AI Consumption - OpenAI and Anthropic Claude.

The first MVP does not require all four as live connectors. Manual, static or
imported evidence may prove the value loop first.

## Agent Operating Rules

Before acting:

1. read `docs/project/README.md`;
2. confirm the current gate;
3. read only the contracts required by the task;
4. inspect the existing implementation;
5. stop on a real contradiction;
6. modify only the authorized module;
7. verify the required build and tests;
8. update current-state documentation only when the gate changes.

Do not:

- generate more than one module per iteration;
- reopen frozen decisions without implementation evidence;
- modify Domain or Ports for adapter convenience;
- introduce business behavior outside the single authorized Phase 3 sprint;
- add speculative abstractions, tables, routes or dependencies;
- claim progress that was not executed and verified;
- treat historical prompts as current authorization.

## Authority

Use this order:

1. explicit founder authorization;
2. current gate in `docs/project/PROJECT_STATUS.md`;
3. accepted decisions in `docs/decisions/14_Decision_Log.md`;
4. product and architecture contracts;
5. active Phase 3 sprint plan;
6. supporting references;
7. historical documents.

The Spanish one-page prompt is retained only as legacy context. This English
document is the canonical compact prompt for AI agents.
