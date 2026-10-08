# IMPERATOR

### DAM TFG MVP for operational decision traceability

> Transform operational evidence into explainable, measurable and auditable
> business decisions.

**Java 21 Â· Spring Boot 4.1 Â· Next.js 16 Â· PostgreSQL 18 Â· JWT/RBAC Â· Docker Â· AWS preparation Â· DevSecOps**

[![Java CI](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/java-ci.yml/badge.svg)](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/java-ci.yml)
[![Security](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/security.yml/badge.svg)](https://github.com/Dmillan-dev/ImperatorProyect/actions/workflows/security.yml)
![Lifecycle](https://img.shields.io/badge/lifecycle-pre--pilot-c58b00)
![AWS](https://img.shields.io/badge/AWS-offline_evidence_only-555555)

[TFG scope and evidence](docs/tfg/README.md) |
[Project status](docs/project/PROJECT_STATUS.md) |
[Project structure and work order](docs/project/PROJECT_STRUCTURE.md) |
[Technical evidence](docs/portfolio/technical-evidence.md) |
[Security](SECURITY.md) |
[Architecture decisions](docs/decisions/14_Decision_Log.md)

## What The MVP Does

IMPERATOR reconstructs which Evidence supported an operational decision, how
its Recommendation and ROI were calculated, who approved it, and whether its
expected value was subsequently validated. The implemented product is a Java
modular monolith, PostgreSQL persistence and a Next.js Decision Review
Workspace for exactly one synthetic case: `DRC-AOA-001`, AI Onboarding
Assistant Recovery.

```text
Evidence â†’ Decision â†’ deterministic Recommendation and ROI
â†’ optional AI explanation â†’ Human Review â†’ append-only Ledger
â†’ Result Validation â†’ Business Value
```

**Bedrock explains; it never decides.** Business rules, amounts, confidence,
risk and authority belong to the application. The model cannot approve a
Decision, modify its Recommendation, write the Ledger or execute a change.

The reference case has 30 synthetic Evidence records. Its fixed policy
estimates EUR 1,620 monthly recovery and EUR 19,440 annualized recovery. These
are synthetic business values, not measured AWS deployment costs or customer
savings. Business Value requires human implementation and result-validation
facts before it is projected.

![Synthetic Decision Review Workspace](output/commercial/linkedin-discovery-kit/IMPERATOR_Workspace_Captura_Limpia.png)

The screenshot is illustrative synthetic material. Final TFG captures must be
bound to the accepted implementation SHA and execution evidence.

## Current Delivery And Evidence Status

**2026-10-06: A-S1 R-19 CLOSED; A-S2 Security Closure Gate NO-GO. TFG-A IN PROGRESS.**
The owner authorized the isolated two-entry brace-expansion patch. Versions
1.1.18→1.1.21 and 5.0.9→5.0.12 are installed; direct dependencies and all other
lock entries remain unchanged from the starting local worktree.

| Current local control                                                   | Result                                                  |
| ----------------------------------------------------------------------- | ------------------------------------------------------- |
| Backend / PostgreSQL integration / frontend tests                       | 174 / 35 / 41 PASS                                      |
| Format, lint, types, build                                              | PASS                                                    |
| Chrome browser fixture suite                                            | 9 PASS; 6 intentional skips                             |
| Real Docker/API/UI, RBAC, V3, persistence and readiness/recovery        | PASS; exact runtime/image scan binding verified         |
| Detected secrets                                                        | 0 in source, history and canonical images               |
| Full npm audit                                                          | FAIL: 8 High package aggregates from 3 other advisories |
| Trivy source, development included                                      | FAIL: 1 fixable High; 2 including unfixed               |
| Canonical backend / frontend / PostgreSQL images: fixable High/Critical | 0 / 8 / 29; FAIL overall                                |

The [R-19 and current security review](docs/tfg/R19_SECURITY_REVIEW.md) records
the bounded patch, tool/database timestamps, new R-21/R-22 risks and PR
discrepancies. The [dated evidence pack](docs/tfg/evidence/2026-10-06-r19/README.md)
contains five new captures, a 28.20-second captioned technical fallback and
scoped cleanup. Results identify a dirty local worktree on baseline
`b602bbe1dde92a902c17272d59cd9f0c10b9efe5`; no new accepted Git commit,
hosted-CI pass, AWS deployment or external IdP certification is claimed.

R-17's original five findings remain CLOSED as a dated remediation. Fresh
vendor data now provides PCRE2/Perl patches; npm also reports source-map-js,
sharp and an unfixed braces development chain. These are separate current
findings; earlier passing scans cannot certify today's version.

Review-only candidates demonstrate a narrow next intervention: source-map-js
and sharp patches, plus Bookworm PCRE2/Perl security packages. They are not
applied to the repository or accepted as the release. Broad Dependabot group
updates and a Temurin base refresh are not required to remediate these findings.

D101 remains **OPERATOR-DEFERRED / BLOCKED_EXTERNAL**, mandatory before pilot,
non-loopback exposure or MVP Release. TFG-B/C/D and live AWS execution remain
unauthorized. See the [Evidence Register](docs/tfg/04_EVIDENCE_REGISTER.md)
for dated implementation, local and cloud boundaries.

## R-20 Approval And Current Security Gate

The [original R-20 B approval](docs/tfg/evidence/2026-10-02-r20-acceptance/acceptance.json)
is preserved unchanged for its bound 32-CVE decision and October 2 images/data.
Local loopback TFG-A and synthetic data only; AWS/production/real data excluded.
Exclusive expiry: **2026-10-09 00:00 Europe/Madrid (UTC+02:00)**,
equivalent to **2026-10-08 22:00 UTC**, without automatic renewal.

**Current version binding: REVIEW REQUIRED / NOT ACCEPTED.** Changed images,
lockfile and scanner data, provider fixes and new findings trigger the recorded
early-invalidation conditions. R-20 cannot waive fixable vulnerabilities or
cover the independent braces tooling advisory. Zero fixable High/Critical and
zero secrets remain mandatory. The [Security Closure Gate](docs/tfg/TFG_A_SECURITY_CLOSURE_GATE.md)
is **NO-GO due to R-21/R-22 and current R-20 binding**, rather than R-19.
TFG-A is not COMPLETE; no institutional approval or TFG-B opening is implied.

| Evidence label    | Meaning                                                    |
| ----------------- | ---------------------------------------------------------- |
| IMPLEMENTED       | Source or configuration exists                             |
| VALIDATED OFFLINE | Identified checks passed without live AWS deployment       |
| VALIDATED IN AWS  | Behavior executed against identified AWS resources         |
| OPERATIONAL       | Deployed pilot completed its acceptance window             |
| NOT IMPLEMENTED   | The specified capability or executed environment is absent |

`OPERATOR-DEFERRED` is scheduling; `BLOCKED_EXTERNAL` describes a dependency.
Neither discharges an acceptance gate. Source implementation and execution
evidence are reported separately throughout the TFG package.

## Local Architecture

```mermaid
flowchart LR
    Reviewer[Human reviewer] --> Web[Next.js workspace]
    Web -->|same-origin API proxy| API[Spring Boot REST and JWT/RBAC]
    API --> App[Application and ports]
    App --> Domain[Deterministic Domain policies]
    App --> JDBC[PostgreSQL adapters]
    JDBC --> DB[(PostgreSQL)]
    App --> Connectors[Read-only GitHub/AWS adapters]
    App -. after deterministic commit .-> Explain[ExplanationProvider]
    Explain --> Bedrock[Optional Bedrock adapter]
```

Domain, Application and Ports remain independent of Spring, JDBC, HTTP and AWS
SDKs. The backend uses explicit JDBC transactions. Business Value is a read
projection of persisted governance facts; it is not a second financial engine.

Compose runs frontend, backend and PostgreSQL plus one-shot migration,
validation and permission tasks. Only the frontend publishes a loopback port.
The management endpoint on port 9090 is internal. The browser holds a supplied
short-lived bearer token in memory; a commercial login/session system is outside
this MVP. `backend-python/` and `services/` are placeholders, not runtime services.

## AWS Target: The Same Product

The accepted [D100 target](docs/architecture/61_AWS_SAA_Portfolio_Deployment_Contract.md)
reuses the application and container build definitions:

```text
Route 53 + ACM â†’ public HTTPS ALB
â†’ private frontend/backend ECS/Fargate â†’ private Single-AZ RDS PostgreSQL

GitHub OIDC â†’ immutable ECR images â†’ controlled Terraform delivery
Secrets Manager â†’ ECS secret injection
CloudWatch + SNS â†’ operational evidence
CloudTrail â†’ encrypted S3 audit storage
Backend â†’ optional Bedrock explanation
```

The target has two-AZ subnets/ALB, one NAT gateway, one task per application
service and Single-AZ RDS. This demonstrates security and availability
trade-offs; it is not full high availability. The `bootstrap` Terraform root
owns protected state, ECR and GitHub roles; `pilot` owns the runtime and uses
S3 locking. Task services start inactive until database tasks pass.

AWS resources, publication, plan/apply, Bedrock calls, costs, rollback and
teardown remain unexecuted. ACM/DNS and the application secret also require
reviewed external inputs. See [local vs AWS](docs/tfg/06_LOCAL_VS_AWS.md) and
the [deployment runbook](docs/runbooks/aws-pilot-deployment.md).

## Security And DevSecOps

- D087: RS256/JWKS validation, exact audience, issuer, time, UUID subject,
  mandatory `kid` and one exact role.
- D088: explicit grants for `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE` and
  `AUDITOR`; server-side actor authority and domain invariants still apply.
- Confidential Evidence is redacted; Restricted handling fails closed.
- The database application role cannot update/delete Ledger history.
- Container hardening, pinned inputs, Trivy and Gitleaks are implemented;
  D094 includes PostgreSQL SBOM/provenance and reproducibility evidence.
- Java CI, Security and offline AWS IaC workflows are versioned. CodeQL is
  recorded as GitHub-managed analysis; current account settings require
  external inspection before making a new hosted-configuration claim.
- GitHub OIDC and ECS task roles avoid static AWS keys. Database secret values,
  bearer tokens, private keys and customer data do not belong in Git.

The AWS deploy role still needs least-privilege review. Publication protection,
accepted-SHA checks, exact-image scanning and approval of the plan actually
applied are TFG-B findings, not capabilities certified by this README.
D087, D088 and D095 remain unchanged.

## Observability And Responsible AI

Local diagnostics include bounded Micrometer metrics, correlation IDs,
Actuator probes and JSON logs in Elastic Common Schema format. `ecs` log
format does not imply execution on Amazon ECS. `/livez` checks application
liveness; `/readyz` also checks database readiness.

There is no automatic business-metric-to-CloudWatch exporter in the inspected
source. Prometheus/Grafana services remain deferred under D097. Existing safe
logs and internal metrics provide the bounded MVP evidence.

Bedrock Converse receives prepared context after Recommendation persistence.
Strict output validation limits fields, references and size. Explanation
attempts are stored separately with provider/model/prompt metadata, tokens,
latency and status. Reads do not invoke the provider. Failure cannot roll back
the deterministic business state. JSON/reference validity does not establish
narrative fidelity: synthetic human evaluation remains required.

See [D099](docs/architecture/59_Bedrock_Auditable_Explanation_Contract.md),
the [Bedrock runbook](docs/runbooks/bedrock-explanations.md) and
the [evaluation rubric](docs/ai/60_Bedrock_Explanation_Evaluation.md).

## TFG Scope And Risk Control

The [risk register](docs/tfg/00_TFG_MASTER_PLAN.md#6-risks-and-scope-control)
assigns reductions, tracks and exit evidence. A risk is closed by verification,
not by documenting a mitigation.

- Reuse one modular monolith, one workspace, one case and four roles.
- Keep synthetic business ROI separate from AWS deployment cost.
- Resolve the ECR mismatch before publication: bootstrap declares
  `imperator-pilot/*`, while workflows reference `imperator/pilot/*`.
- Review IAM, approved-plan binding, provider egress and runtime sizing before
  external execution.
- Use a current regional Pricing Calculator estimate, short demo windows,
  rollback and verified teardown; Budgets alerts are not a spending cap.
- Keep a local demo and sanitized recording as defense fallback.

The MVP excludes new cases, real customer data, multi-tenancy, commercial
login, extra microservices, Python AI service, RAG/Knowledge Bases, agents,
queues, cache, EKS/Kubernetes, Multi-AZ RDS, autoscaling and services added only
for certification coverage. No such feature is required to finish this TFG.

## Roadmap

| Track                    | Bounded outcome                                                                                                            | Current status                                     |
| ------------------------ | -------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------- |
| TFG-A                    | Scope, five-certification matrix, traceability, risk register, local evidence and defense rehearsal                        | IN PROGRESS                                        |
| TFG-B                    | Delivery findings, IAM/security review and current pricing; live plan only after external gates and separate authorization | NOT AUTHORIZED                                     |
| External D101 dependency | D095 Keycloak HTTPS conformance before pilot                                                                               | OPERATOR-DEFERRED / BLOCKED_EXTERNAL; not a sprint |
| TFG-C                    | Authorized publication and AWS deployment of the synthetic MVP                                                             | NOT AUTHORIZED                                     |
| TFG-D                    | Runtime/AI validation, measured cost, rollback, teardown and recreation                                                    | NOT AUTHORIZED                                     |

Applied knowledge maps to DAM, Cloud Practitioner, Solutions Architect
Associate, Developer Associate, AI Practitioner and GitHub Foundations in the
[competency matrix](docs/tfg/02_COMPETENCY_MATRIX.md). The project demonstrates
selected competencies; it neither covers every exam objective nor grants a
professional credential.

## Local Verification

Use Java 21 and the pinned Maven 3.9.16 wrapper:

```powershell
.\mvnw.cmd clean verify
.\mvnw.cmd -Ppostgresql-integration clean verify
```

The integration profile requires an empty isolated database and the six
`IMPERATOR_IT_*` variables documented in [backend-java/README.md](backend-java/README.md).
It runs Flyway migrate/validate/no-op and database integration tests. Earlier
Windows wrapper failures do not authorize changing the wrapper; any direct
cached-Maven invocation must be recorded as such.

With Node.js 24 LTS and npm 11:

```powershell
cd frontend
npm ci
npm run format:check
npm run lint
npm run typecheck
npm run test:coverage
$env:IMPERATOR_API_ORIGIN = "http://127.0.0.1:8080"
npm run build
npm run test:e2e
```

Browser acceptance uses intercepted synthetic API fixtures. It verifies UI
behavior and is separate from the real backend/database runtime rehearsal.
Docker prerequisites and verification are in [infra/docker/README.md](infra/docker/README.md).
Offline IaC commands are in [infra/aws/terraform/README.md](infra/aws/terraform/README.md).

## Repository Map

| Path                               | Purpose                                                  |
| ---------------------------------- | -------------------------------------------------------- |
| `backend-java/`                    | Domain, Application, ports, adapters, API and bootstrap  |
| `src/test/java/`                   | Backend unit, contract and PostgreSQL integration suites |
| `frontend/`                        | Single-case Next.js Decision Review Workspace            |
| `database/`                        | Flyway migrations                                        |
| `infra/docker/`                    | Local runtime and hardened images                        |
| `infra/aws/`                       | Offline Terraform, migration image and AWS preparation   |
| `.github/`                         | CI/security/IaC and manual AWS delivery workflows        |
| `docs/tfg/`                        | Academic scope, traceability, risks and evidence         |
| `docs/project/`, `docs/decisions/` | Current control and immutable accepted decisions         |
| `samples/`, `scripts/`             | Synthetic fixtures and verification tools                |

Follow the [current structure and verification order](docs/project/PROJECT_STRUCTURE.md)
to work from one case, its acceptance tests and its evidence. Keep existing
packages and historical decisions in place; remove only verified generated
residue after capture.

Local `output/tfg/` material is an auxiliary draft; it does not replace
versioned TFG or project-control authority.

## Security Disclosure And License

Report vulnerabilities privately following [SECURITY.md](SECURITY.md).
Credentials, JWTs, private keys, customer data, database dumps, local `.env`
files and raw generated runtime evidence are excluded from Git. Publish only
reviewed sanitized evidence.

Copyright (c) 2026 Daniel Millan Perez. Public portfolio review is governed by
the proprietary, all-rights-reserved [LICENSE](LICENSE).
