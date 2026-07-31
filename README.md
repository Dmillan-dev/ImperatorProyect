# IMPERATOR

Status: **Phase 3 First Business Value Loop active**

Current gate: **Sprint 3.1 - JSONL Evidence Import, Validation And Normalization**

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
- Spring Boot executable web runtime;
- deterministic REST error handling;
- `X-Correlation-ID` validation and propagation;
- six REST route-shell controllers exposing 15 frozen MVP routes.

The repository does not yet contain:

- functional REST-to-Application wiring;
- JWT/RBAC runtime;
- React application runtime;
- live connectors;
- real AI-provider calls;
- Docker local runtime;
- production deployment.

## Repository Map

```text
.github/        Repository automation; currently contains legacy Proto CI
.mvn/           Maven Wrapper configuration
agents/          AI-agent roles, phase plans and sprint controls
backend-java/    Java domain, application, ports and adapters
backend-python/  Reserved Python provider boundary
database/        Versioned database migrations
demos/           Non-production static product demonstrations
docs/            Product, architecture, decisions and project control
frontend/        Reserved React frontend boundary
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

## Documentation Rules

- English is mandatory for all new or modified human-readable project
  material. Historical records may retain their original language.
- Documents 34-40 are frozen unless implementation proves a contradiction and
  explicit approval is granted.
- Historical prompts and gate reports remain historical records.
- The Decision Log is append-only for accepted choices.
- Only one sprint and one implementation module may be authorized at a time.
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
