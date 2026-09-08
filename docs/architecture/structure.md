# Repository Structure

Status: **Operational reference**

This document defines the physical ownership of the repository. Current phase
and sprint authorization live in `docs/project/`.

## Top-Level Structure

```text
.github/
.mvn/
agents/
backend-java/
backend-python/
database/
demos/
docs/
frontend/
infra/
output/
proto/
samples/
scripts/
services/
src/test/java/
target/
```

## Ownership

### `.github/`

Contains the implemented Java CI and fail-closed Security workflows, review
ownership and pull-request template. GitHub-managed CodeQL default setup is
enabled separately from repository workflow files. Frontend CI, release
automation and deployment workflows are not implemented.

### `.mvn/`

Contains the Maven Wrapper configuration used by the reproducible Java 21
build.

### `agents/`

Contains AI-agent roles, execution rules, phase plans and sprint prompts.

- `agents/README.md` defines the operating model.
- `agents/phase1/` is the completed Phase 1 stage dossier.
- `agents/phase2/` is the completed Phase 2 plan and historical prompt set.
- `agents/phase3/` is the active Phase 3 vertical-slice plan.
- role directories define bounded responsibilities.

It never contains product runtime code, secrets or generated output.

### `backend-java/`

Contains the Java modular-monolith foundation:

```text
backend-java/
  api/
  application/
  bootstrap/
  domain/
  ports/
  adapters/
```

The accepted Maven source root is `backend-java`. Java packages use
`imperator.*`.

Dependency direction:

```text
API / Bootstrap / Adapters
        -> Ports / Application
        -> Domain
```

Domain never depends on outer layers.

### `backend-python/`

Reserved for the future explanation-provider boundary. It is not authorized to
own domain truth, calculate ROI, mutate the ledger or call real providers until
an explicit sprint allows it.

### `database/`

Contains versioned database migration artifacts.

Current migration:

```text
database/migrations/V1__initial_schema.sql
```

The physical schema is governed by
`docs/architecture/40_Persistence_Schema_Contract.md`.

### `docs/`

Contains the project knowledge system:

```text
docs/
  project/       current status, phases, sprints and governance
  business/      market and commercial context
  product/       product, domain and API contracts
  architecture/  architecture and implementation contracts
  decisions/     accepted decisions and ADRs
  ai/            canonical AI context
  rfcs/          proposed changes
  research/      research templates
  rnd/           R&D evidence model and templates
  demos/         demo readiness and value-validation guides
  pilot/         controlled commercial-pilot preparation
  portfolio/     recruiter-facing technical evidence
```

The canonical documentation entry point is `docs/README.md`.

### `demos/`

Contains static, non-production product demonstrations. Demo files are
historical narrative artifacts and must not be treated as the React product
runtime or as evidence that an MVP feature is implemented.

### `frontend/`

Contains the implemented Next.js, React and TypeScript Decision Review
Workspace, its unit/component tests and Playwright acceptance test. It is not
evidence of a public or production deployment.

### `infra/`

Contains the D092-D095 certified production-like local Docker Compose runtime,
hardened application Dockerfiles and ignored file-backed secret boundary.
D096 freezes an optional observability profile whose Sprint 4.4 implementation
is now authorized but not yet present. No cloud deployment, Kubernetes or
Terraform exists.

### `output/`

Contains generated commercial and PDF artifacts. These are presentation
outputs, not runtime source, canonical contracts or customer evidence.

### `proto/`

Contains internal contract artifacts. Protobuf presence does not imply a
microservice boundary or authorize generated runtime bindings.

### `samples/`

Contains approved, non-secret validation material for the first Decision ROI
Case. Samples must not be represented as production customer data.

### `scripts/`

Contains the local Docker runtime verifier and commercial artifact generators.
Scripts must not bypass build, migration, security or sprint gates.

### `services/`

Contains legacy Phase 0 documentation placeholders. New implementation belongs
under `backend-java/`, `backend-python/` or `frontend/`; no new Phase 2 source
belongs under `services/`.

### `src/test/java/`

Contains Maven test sources, including the Spring Boot HTTP contract tests and
the PostgreSQL repository integration suite.

### `target/`

Contains generated Maven build output. It is ignored, non-authoritative and
must never be committed or referenced as source.

## REST Structure

REST implementation uses:

```text
backend-java/api/errors
backend-java/api/evidence
backend-java/api/decisions
backend-java/api/recommendations
backend-java/api/ledger
backend-java/api/businessvalue
backend-java/api/pagination
```

Java packages use `imperator.api.*`. Public paths use `/api/v1` and retain
documented HTTP naming, including `/business-value`.

Do not create a parallel `adapters/in` HTTP hierarchy.

## Documentation Structure

Documents are organized by ownership, not by execution order. Execution order
is maintained in:

- `docs/project/PHASE_AND_SPRINT_MAP.md`;
- `agents/phase2/README.md`.

Frozen or historical documents remain in place to preserve links and audit
history. New documents must use English and must declare their purpose or
status.

## Mutation Rules

- Modify only files authorized by the current sprint.
- Do not move frozen contracts for cosmetic organization.
- Do not create duplicate package roots or parallel module hierarchies.
- Do not place source code under `docs/` or `agents/`.
- Do not place documentation inside build-output directories.
- Update project status only after verified execution.
- Record semantic changes in the Decision Log before changing contracts.
