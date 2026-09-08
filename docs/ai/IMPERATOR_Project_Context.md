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
- D090 - AWS Integration Contract: accepted, complete and frozen with the
  authorized final-microsecond PostgreSQL precision correction.
- AWS Integration: certified and complete with one disabled-by-default,
  read-only AWS SDK adapter for one verified account, one Region and the exact
  `onboarding-assistant-prod` scope. It produces only deterministic
  `E-AWS-001` through `E-AWS-004` Evidence and has no Decision,
  Recommendation, ROI, Ledger, Business Value or cloud-mutation authority.
  Java 21 passed 139 default tests and PostgreSQL 18.4 passed 31 integration
  tests.
- D091 - Decision Review Workspace Contract: accepted, complete and frozen.
- Decision Review Workspace: certified and complete as the thin, single-case
  `DRC-AOA-001` product surface. Node.js 24.19.0, npm 11.17.0, format, lint,
  strict TypeScript, 35 unit/component tests, contractual coverage, production
  build, dependency audit and Playwright acceptance passed. The unchanged Java
  backend passed 139 default tests and 31 PostgreSQL 18.4 integration tests.
- Sprint 4.2.1 documentation synchronization: complete.
- D092 - Docker Production Runtime Contract: accepted and complete.
- D093 - DRC-AOA-001 Case Composition Contract: accepted, implemented and
  certified through R16, case-level uniqueness and resumable D081/D082 steps.
- D094 - PostgreSQL Runtime Supply Chain Remediation: accepted and certified
  with PostgreSQL 18.6, pinned build inputs, SBOM, provenance and zero fixable
  High/Critical or secret findings.
- D095 - D092 Runtime Certification Scope Correction: accepted and complete.
- Sprint 4.3 Docker Production Runtime: certified and complete. Java 21 passed
  151 default tests, PostgreSQL 18.6 and Flyway passed 34 integration tests,
  final SHA-tagged images passed security/hardening gates, and local JWT/RBAC,
  `DRC-AOA-001` E2E and persistence-after-recreation evidence passed.
- Sprint 4.3.1 documentation synchronization: complete.
- Current gate: Sprint 4.4 - Observability.
- External Keycloak HTTPS conformance: deferred by D095 and mandatory before
  Sprint 4.5, real customer data or MVP Release.
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
- React presents role-aware controls but never replaces D087 authentication,
  D088 authorization or D083 Application governance.
- Connectors normalize evidence; they do not calculate ROI or recommend.
- GitHub and AWS are the only certified live connectors; D089 and D090 remain
  frozen.
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
- `ADMIN`-only R16 case composition wired through Application and PostgreSQL;
- deterministic Decision creation wired through Application and PostgreSQL;
- deterministic Recommendation and ROI policy wired through Domain,
  Application and PostgreSQL;
- optional provider-neutral Recommendation explanation wired after
  deterministic persistence; no live vendor adapter or model call;
- functional Review, Ledger and Result Validation REST commands wired through
  Application and PostgreSQL;
- JWT authentication and RBAC authorization complete; D083 business authority
  remains in Application and Restricted Evidence remains fail-closed redacted.
- hardened D092-D095 Docker Compose runtime certified with PostgreSQL 18.6,
  non-root custom images, read-only filesystems and loopback-only publication.

The implemented Java source root is `backend-java`, with packages under
`imperator.*`. REST packages live under `imperator.api.*`. Public product REST
routes use `/api/v1`.

## Current Frontend Stack

- Node.js 24 LTS and npm 11;
- Next.js 16.2 and React 19.2;
- strict TypeScript 5 and Tailwind CSS 4.3;
- Lucide icons, Zod runtime schemas, Vitest and Playwright Chromium;
- exact npm lockfile and same-origin `/api/v1/**` rewrite;
- volatile in-memory bearer token with no browser persistence;
- one D091 Decision Review Workspace and no broader Executive Workspace.

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
