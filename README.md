# IMPERATOR

### Operational decisions, backed by evidence.

**Understand why a change was proposed, who approved it and what value it delivered.** IMPERATOR connects operational facts, deterministic recommendations, human review and a persistent Decision Ledger in one workspace.

[![Java CI](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/java-ci.yml/badge.svg)](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/java-ci.yml)
[![Security](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/security.yml/badge.svg)](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/security.yml)

[Workflow](#the-workflow) · [Local demo](#the-local-demo) · [Architecture](#architecture) · [Current stage](#current-stage) · [Documentation](#documentation)

> **Local academic MVP:** Java 21, Spring Boot, Next.js and PostgreSQL. One synthetic case, four roles and an auditable path from evidence to validated simulated value. AWS deployment is a separate, blocked phase.

## What problem does it solve?

Technical records explain what happened. The evidence, assumptions, approval and outcome of a change often live in different places. IMPERATOR keeps those relationships together so a reviewer can reconstruct the decision.

| For             | The useful outcome                                                                 |
| --------------- | ---------------------------------------------------------------------------------- |
| Engineering     | Inspect the recommendation and its assumptions; record implementation.             |
| Finance         | Validate an outcome against post-action evidence and compare it with the estimate. |
| Decision owners | Approve, reject or defer with an assigned actor and a recorded reason.             |
| Audit           | Follow the evidence, responsible actors and ordered governance history.            |

## The workflow

```mermaid
flowchart LR
    E[Evidence] --> D[Decision]
    D --> R[Deterministic recommendation]
    R --> H[Human review]
    H --> I[Implementation]
    I --> V[Result validation]
    V --> B[Business value]
    H -.-> L[(Decision Ledger)]
    I -.-> L
    V -.-> L
```

**Application code calculates the recommendation and amounts. People retain approval authority.** The assigned actor, role and decision state are checked on the server. The application database role can append governance facts, but cannot update or delete Ledger history.

An optional AI adapter can explain an already persisted recommendation. It cannot change the business result, approve a decision or execute a change. Live provider validation remains pending.

## The local demo

**`DRC-AOA-001` — optimize the cost of an AI onboarding assistant.** Thirty synthetic records support a `MODEL_CHANGE` recommendation: use a lower-cost model for routine requests and preserve a capable fallback for exceptions.

| Estimated annual recovery | Validated simulated annual recovery | Variance |
| ------------------------: | ----------------------------------: | -------: |
|                   €19,440 |                             €18,960 |    −€480 |

![IMPERATOR Decision Review Workspace with the synthetic recommendation](docs/tfg/evidence/2026-10-14-a-s4-runtime/capture/01-deterministic-recommendation.png)

_Real local Docker rehearsal, 14 October 2026. Dates inside the workspace belong to the synthetic fixture. The browser actions were automated against real services._

Four roles participate: **ADMIN, PLATFORM_ENGINEER, FINANCE and AUDITOR**. Approval is exercised through the UI; implementation and financial validation use authenticated API commands followed by UI reads. Twenty-eight records support the recommendation and two support post-action validation. [Rehearsal and limits](docs/tfg/evidence/2026-10-14-a-s4-runtime/README.md).

## Architecture

| Layer                    | Implementation                                                                  |
| ------------------------ | ------------------------------------------------------------------------------- |
| Workspace                | Next.js, React and TypeScript; one decision-review experience.                  |
| Backend                  | Java 21 / Spring Boot modular monolith with hexagonal boundaries.               |
| Persistence              | PostgreSQL, explicit JDBC transactions and Flyway V1–V3.                        |
| Authorization            | RS256 JWT, four-role RBAC and actor/state checks.                               |
| Local delivery           | Hardened Docker images, isolated networks and readiness/recovery probes.        |
| Quality and supply chain | Maven, frontend checks, Playwright, GitHub Actions, Trivy, Gitleaks and CodeQL. |

Read-only GitHub/AWS evidence adapters and the optional Bedrock explanation adapter are implemented. Terraform and OIDC workflows prepare a possible ECS/Fargate and RDS pilot. Their presence does not demonstrate a live deployment or provider validation. [Architecture](docs/tfg/05_ARCHITECTURE.md) · [Local versus AWS](docs/tfg/06_LOCAL_VS_AWS.md).

## Current stage

**Local MVP implemented and demonstrated. Academic delivery remains in progress.** Status reviewed 26 November 2026. The [current review and release limits](docs/tfg/evidence/2026-11-26-local-hygiene/README.md) distinguish fresh security checks from the historical demo. Main includes the focused minimatch/Node fixes; GitHub has zero open Dependabot and CodeQL alerts at this review.

| Area                  | Evidence and limit                                                                                                                                                                         |
| --------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Product and runtime   | One synthetic case, four roles, Flyway, persistence after recreation and recovery demonstrated locally on 14 October.                                                                      |
| Functional evaluation | 20 November: 174 unit and 44 frontend tests, format/lint/types/build PASS; Playwright 9 passed / 6 intentional skips with intercepted API. PostgreSQL integration: 35 PASS on 11 November, on the exact retained image. |
| Exact-image security  | 26 November: **0 fixable High/Critical and 0 secrets** across four exact retained image IDs. There are 40 unique H/C residual CVEs (42 in the conservative review set); R-20 is not approved for this set. The last full/production npm audits are dated 20 November, with 0 findings. |
| TFG delivery          | Current A-S3 formal closure remains blocked by the security decision and complete candidate runtime rehearsal. Memory and English script prepared; measured human rehearsal, narrated recording and academic review pending. |
| AWS and providers     | Live AWS deployment, external identity conformance and real provider validation remain gated.                                                                                              |

> [!IMPORTANT]
> **R-20 has no current acceptance.** The last approved decision expired on 19 October 2026 at 00:00 Europe/Madrid. The unapproved 18 November proposal did not renew it. The new review remains `accepted: false`, adds the Zstandard residual and keeps R-23 disposition separate. [Exact artifacts and release conditions](docs/tfg/evidence/2026-11-26-local-hygiene/README.md).

**Next:** approve a digest-bound local security decision, rehearse the exact runtime, then complete the English narrated defence and academic review. [Local MVP → AWS gates](docs/tfg/06_LOCAL_VS_AWS.md#sprints-de-cierre-y-transición). AWS readiness follows local closure; deployment needs separate authorization.

## Explore locally

Use the [backend guide](backend-java/README.md), [frontend guide](frontend/README.md) and [runtime preflight](docs/tfg/A_S4_LOCAL_DEFENSE.md#preflight-para-otra-ejecución). The accepted rehearsal uses exact retained images, isolated synthetic data and a local HTTPS/JWKS fixture. A generic Compose build does not reproduce that evidence chain.

Backend verification, from the repository root with Java 21:

```powershell
.\mvnw.cmd clean verify
```

Frontend checks, with Node.js 24 and npm 11:

```powershell
cd frontend
npm ci
npm run format:check
npm run lint
npm run typecheck
npm run test:coverage
```

Build, database integration, browser tests and runtime execution require their documented inputs. Follow the [persistence certification](backend-java/README.md#persistence-certification), [frontend quality gates](frontend/README.md#quality-gates) and runtime preflight. AWS remains blocked.

## Documentation

| Start here                                                                              | What you will find                                                                |
| --------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| [TFG guide](docs/tfg/README.md)                                                         | Scope, evidence, memory and the remaining local closure gates.                    |
| [Architecture](docs/tfg/05_ARCHITECTURE.md)                                             | Components, data flow and technical boundaries.                                   |
| [Traceability](docs/tfg/03_TRACEABILITY_MATRIX.md)                                      | Requirements, implementation and validation.                                      |
| [Security decision](docs/tfg/evidence/2026-10-16-r20-local-acceptance/README.md)        | Exact digests, residual risk, expiry and scan results.                            |
| [Memory PDF](docs/tfg/evidence/2026-10-14-a-s4-runtime/IMPERATOR_Memoria_TFG_Local.pdf) | Frozen academic draft; attach the current security decision as its update.        |
| [English defence script](docs/tfg/09_DEFENSE_SCRIPT_12_MIN.md)                          | Planned twelve-minute walkthrough; human rehearsal remains pending.               |
| [Repository map](docs/tfg/05_ARCHITECTURE.md)                                           | `backend-java/`, `frontend/`, `database/`, `infra/` and supporting documentation. |

[Featured Project](docs/portfolio/featured-project.md) · [Project status](docs/project/PROJECT_STATUS.md) · [Security reporting](SECURITY.md) · [License](LICENSE)
