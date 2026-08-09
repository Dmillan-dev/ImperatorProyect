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
- Deterministic Decision creation: certified and complete with atomic retry and
  concurrency protection.
- Deterministic Recommendation and ROI policy: certified and complete with
  atomic retry, immutable conflict and concurrency protection.
- Provider-neutral Explanation integration: complete with bounded context,
  post-transaction invocation and failure isolation.
- Human Review, Ledger and Result Validation implementation: complete with
  atomic review outcomes, immutable replay and strict linear sequencing.
- Sprint 3.4 PostgreSQL 18.2 runtime certification: passed during the complete
  Sprint 3.7 integration-profile run; D084 obligation discharged.
- End-to-End Local Business Value Demo: certified and complete with a
  deterministic 30-line NDJSON dataset, 89 passing Java 21 tests and a
  non-persisted Application projection sourced from validated Ledger facts.
- Basic Java CI: certified and complete through GitHub Actions run
  `30708049322`, with Maven Wrapper 3.3.4, Apache Maven 3.9.16, Eclipse Adoptium
  Java 21, 89 passing tests and executable JAR packaging.
- Functional REST API: certified and complete under D086 with all 15 routes
  proven through real Application and PostgreSQL 18.2 composition.
- D085 - MVP Delivery Roadmap Evolution: accepted and complete.
- D086 - Functional REST Application Contract: accepted and complete.
- D087 - JWT Authentication Contract: accepted and complete.
- JWT Authentication: certified and complete with all 15 D086 routes protected
  by a stateless RS256/JWKS Resource Server and governance identity derived from
  validated JWT claims. Java 21 passed 105 default tests and PostgreSQL 18.2
  passed 29 integration tests.
- D088 - RBAC Authorization Contract: accepted and complete.
- RBAC Authorization: certified and complete with the exact 15-route/four-role
  matrix, unchanged D083 governance authority and role/type-based Evidence
  filtering. Java 21 passed 111 default tests and PostgreSQL 18.4 passed 29
  integration tests under the PostgreSQL 18.x (18.2+) gate.
- D089 - GitHub Integration Contract: accepted, complete and frozen.
- GitHub Integration: certified and complete with one disabled-by-default,
  read-only REST adapter for one organization and repository. It produces only
  deterministic `E-GH-001`, `E-GH-002` and `E-GH-003` Evidence and has no
  Decision, Recommendation, ROI, Ledger or Business Value authority. Java 21
  passed 128 default tests and PostgreSQL 18.4 passed 30 integration tests.
- Current gate: Sprint 4.1 - AWS Integration; implementation has not started.
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
- GitHub is the only certified live connector; D089 remains frozen.
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
- Spring Security OAuth2 Resource Server with RS256/JWKS authentication;
- D088 route/method authorization and Evidence response filtering;
- no JPA;
- six product REST controllers preserving 15 controlled routes;
- functional JSONL Evidence import wired through Application and PostgreSQL;
- deterministic Decision creation wired through Application and PostgreSQL;
- deterministic Recommendation and ROI policy wired through Domain,
  Application and PostgreSQL;
- optional provider-neutral Recommendation explanation wired after
  deterministic persistence; no live vendor adapter or model call;
- functional Review, Ledger and Result Validation REST commands wired through
  Application and PostgreSQL;
- JWT authentication and RBAC authorization complete; D083 business authority
  remains in Application and Restricted Evidence remains fail-closed redacted.

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
