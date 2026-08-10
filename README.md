# IMPERATOR

Status: **Phase 3 First Business Value Loop active**

Current gate: **Sprint 4.3 - Docker Production Runtime**

IMPERATOR is an Operating System for Operational Intelligence and an Enterprise
Decision Intelligence Platform. The MVP proves one evidence-backed decision,
one timeline and one explainable ROI path.

## Start Here

Humans and AI agents should read:

1. [Documentation Control Plane](docs/project/README.md)
2. [Current Project Status](docs/project/PROJECT_STATUS.md)
3. [Phase and Sprint Map](docs/project/PHASE_AND_SPRINT_MAP.md)
4. [Decision Log](docs/decisions/14_Decision_Log.md)
5. [Current Phase 3 Sprint Plan](agents/phase3/README.md)

Compact AI context:

- [IMPERATOR Project Context](docs/ai/IMPERATOR_Project_Context.md)

Full documentation index:

- [Documentation Map](docs/README.md)

## Current Implementation

The Java backend currently contains:

- Java 21 and a reproducible Maven Wrapper build;
- framework-free Domain and Application layers;
- inbound and outbound ports;
- explicit transaction boundaries;
- JDBC PostgreSQL repository adapters;
- Flyway V1 with seven frozen application tables;
- PostgreSQL repository and transaction integration tests;
- certified and complete Spring Boot runtime composition;
- deterministic REST error handling;
- `X-Correlation-ID` validation and propagation;
- six REST controllers preserving the 15 frozen MVP routes;
- certified JSONL Evidence import with independent per-line processing;
- certified deterministic Decision creation with atomic retry and concurrency
  protection;
- certified deterministic Recommendation and ROI policy with atomic retry,
  immutable conflict and concurrency protection;
- completed provider-neutral explanation integration after deterministic
  Recommendation persistence, with bounded context and failure isolation;
- completed Human Review, append-only Ledger and Result Validation
  implementation with atomic Decision outcomes, idempotent replay and strict
  linear sequencing;
- certified deterministic local Business Value workflow over 30 normalized
  Evidence records, with an Application-only projection derived from validated
  Ledger facts;
- certified Java 21 GitHub Actions pipeline executing the complete Maven build,
  89 tests and executable JAR packaging from a clean checkout;
- certified Functional REST API with all 15 D086 routes mapped through DTOs,
  REST mappers and Application input ports;
- certified D087 JWT Resource Server perimeter with RS256/JWKS validation,
  stateless authentication and JWT-derived governance actor identity;
- certified D088 RBAC route/method enforcement and Evidence redaction for all
  four MVP roles without weakening Application governance authority;
- certified D089 GitHub REST evidence synchronization for one organization,
  one repository and `DRC-AOA-001`, with read-only access, deterministic
  Evidence identity and no Decision, ROI or Ledger authority;
- certified D090 AWS SDK evidence synchronization for one account, one Region
  and `DRC-AOA-001`, with default credential resolution, STS account
  verification, deterministic `E-AWS-001` through `E-AWS-004` Evidence and no
  cloud mutation or business authority;
- certified PostgreSQL 18.x runtime behavior, most recently PostgreSQL 18.4,
  with 139 default tests and 31 integration tests passing.

The frontend currently contains:

- the certified D091 single-case `DRC-AOA-001` Decision Review Workspace;
- Node.js 24 LTS, npm 11, Next.js 16.2, React 19.2, strict TypeScript and
  Tailwind CSS 4.3 with an exact npm lockfile;
- volatile in-memory JWT handling, same-origin `/api/v1/**` transport and
  strict runtime response validation;
- role-aware presentation for ADMIN, PLATFORM_ENGINEER, FINANCE and AUDITOR
  without replacing backend authorization or Application governance;
- Decision, Recommendation, ROI, Evidence, Timeline, Ledger and Business Value
  views with isolated loading, empty, redacted, not-ready and failure states;
- five existing governance command forms with confirmation and idempotency;
- 35 passing unit/component tests, certified coverage, a production build and
  Playwright acceptance across desktop, compact and mobile viewports.

The repository does not yet contain:

- additional live connectors beyond GitHub and AWS;
- a live vendor AI-provider adapter or real model calls;
- Docker local runtime;
- production deployment.

## Repository Map

```text
.github/        Certified Java 21 GitHub Actions automation
.mvn/           Maven Wrapper configuration
agents/          AI-agent roles, phase plans and sprint controls
backend-java/    Java domain, application, ports and adapters
backend-python/  Reserved Python provider boundary
database/        Versioned database migrations
demos/           Non-production static product demonstrations
docs/            Product, architecture, decisions and project control
frontend/        Certified Decision Review Workspace frontend
infra/           Reserved runtime infrastructure
proto/           Internal contract artifacts
samples/         Approved validation samples
scripts/         Repository scripts
services/        Legacy documentation placeholders; no new implementation
src/test/java/   Maven unit and integration test source root
target/          Generated, ignored Maven build output
```

See [Repository Structure](docs/architecture/structure.md) for ownership and
mutation rules.

## Build

Requirements:

- Java 21;
- Maven Wrapper from this repository.

Windows:

```text
mvnw.cmd clean verify
```

Unix-like environments:

```text
./mvnw clean verify
```

The PostgreSQL certification profile requires the environment variables
documented in [backend-java/README.md](backend-java/README.md).

## Product Boundary

Canonical product rule:

```text
One Decision. One Timeline. One ROI.
```

Initial paid workflow:

```text
Evidence
-> Decision
-> Recommendation
-> Human Review
-> Ledger
-> Result Validation
-> Realized Business Value
```

Phase 2 is complete under D079. Phase 3 is active and may implement only the
single currently authorized `DRC-AOA-001` business-value increment.

Sprint 4.2 is certified and complete under D091. The implemented product
surface is the thin, single-case Decision Review Workspace, not a broad
Executive Dashboard. Frontend format, lint, strict type checking, 35 tests,
coverage, production build, dependency audit and Playwright acceptance passed;
the unchanged backend also passed 139 default tests and 31 PostgreSQL 18.4
integration tests with Flyway migrate, validate and no-op migrate. Sprint 4.3
Docker Production Runtime is the sole next gate. The system remains non-pilot
and local-only until the later readiness gates pass.

## Documentation Rules

- English is mandatory for all new or modified human-readable project
  material. Historical records may retain their original language.
- Documents 34-40 are frozen unless implementation proves a contradiction and
  explicit approval is granted.
- Historical prompts and gate reports remain historical records.
- The Decision Log is append-only for accepted choices.
- Only one delivery control gate and one implementation module may be authorized at a time.
- Current state must report verified work, never planned work as complete.

## Architecture Rules

- Domain does not depend on frameworks or infrastructure.
- Application depends on ports, not concrete adapters.
- PostgreSQL conforms to the domain.
- REST code lives under `imperator.api.*`.
- Public product REST routes use `/api/v1`.
- AI explanation never becomes business truth.
- Ledger history is append-only.
- No speculative modules, dependencies, schema objects or product behavior.
