# IMPERATOR

## Current R-20 Applicability Review — 2026-10-06

The current23-CVE/115-row applicability review is complete:3affected optional subcomponents absent,1unused protocol,19conditional residuals with explicit prerequisites/uncertainties. Historical Perl probe-name discrepancy resolved without editing old evidence. `braces` is absent as a package from the inspected production image but present in development tooling. Current R20 proposal remains `accepted:false`; independent R23 remains **OPEN / NO FIX AVAILABLE / NOT ACCEPTED**. No upgrades or automatic advancement. A-S2 technical GO; formal closure awaits explicit owner disposition; original exclusive expiry2026-10-09T00:00:00+02:00 remains unchanged. See the [applicability review](docs/tfg/R20_APPLICABILITY_REVIEW.md), exact proposals and artifact index.

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

**2026-10-06: R-17/R-19 CLOSED; authorized R-21/R-22 patches applied and verified. A-S2 technical GO; formal security closure PENDING RESIDUAL DISPOSITION. TFG-A IN PROGRESS.**

Only source-map-js1.2.1→1.2.2, sharp0.35.4→0.35.5 and its mandatory native subtree, and checksum-pinned Bookworm PCRE2/Perl security patches were applied. Six files and28 lock entries; other dependencies, base digests, Java/Spring/Node/PostgreSQL versions, business logic and architecture are unchanged from the task-start local worktree. Braces3.0.3 is unchanged; PR #57–59 were not merged.

| Current local control                                                      | Result                                                                   |
| -------------------------------------------------------------------------- | ------------------------------------------------------------------------ |
| Backend / PostgreSQL integration / frontend                                | 174 / 35 / 41 PASS                                                       |
| Format, lint, types, coverage, build                                       | PASS                                                                     |
| Playwright                                                                 | 9 PASS / 6 intentional skips                                             |
| Exact-image Docker/API/UI/RBAC, Flyway/V3, persistence, readiness/recovery | PASS                                                                     |
| D094 lock/verifier and scoped cleanup                                      | PASS; original volumes preserved                                         |
| Detected secrets, all severities                                           | 0; source/history and all canonical images                               |
| Fixable High/Critical: backend / frontend / PostgreSQL                     | 0 / 0 / 0; A-S2 technical GO                                             |
| Production npm audit                                                       | 0                                                                        |
| Full npm audit                                                             | FAIL:5 High affected-package aggregates from one unfixed braces advisory |
| Full Trivy images                                                          | Frontend48High/1Critical; PostgreSQL63High/3Critical; backend0           |

The [current review](docs/tfg/R21_R22_SECURITY_REVIEW.md), [dated evidence](docs/tfg/evidence/2026-10-06-r21-r22/README.md) and [gate](docs/tfg/TFG_A_SECURITY_CLOSURE_GATE.md) bind tests, raw scans, source/DB hashes, OCI manifests/config/layers, five captures,27.16-second captioned technical fallback and owned-resource cleanup. Baseline `b602bbe1dde92a902c17272d59cd9f0c10b9efe5` identifies a dirty local worktree, not an accepted release SHA or hosted-CI pass. Local loopback/synthetic fixture is not external IdP/AWS certification or a narrated twelve-minute defense.

D101 remains OPERATOR-DEFERRED / BLOCKED_EXTERNAL, mandatory before pilot/non-loopback exposure or MVP Release. A-S3/A-S4, TFG-B/C/D and AWS remain blocked/unauthorized. No further dependency update is authorized.

## R-20 Approval And Current Security Gate

The [historical R-20 B approval](docs/tfg/evidence/2026-10-02-r20-acceptance/acceptance.json) is preserved unchanged. Local loopback TFG-A and synthetic data only; AWS/production/real data/pilot exposure excluded. Exclusive expiry **2026-10-09 00:00 Europe/Madrid (UTC+02:00) = 2026-10-08 22:00 UTC**, with no automatic renewal.

**Current version binding: REVIEW REQUIRED / NOT ACCEPTED.** Current image residual matrix115rows/23distinctCVEs has no new distinct CVE versus the historical32-CVE matrix, but image/lock/runtime/test/DB hashes and surviving Perl package-version bindings changed. A subset does not automatically transfer acceptance. The independent unfixed braces tooling residual is tracked **R-23 OPEN / NOT ACCEPTED**. No fixable vulnerability or detected secret is waived.

**A-S2 technical GO does not close TFG-A.** Formal security closure awaits explicit current-version residual disposition; this task stops here without A-S3 advancement or institutional acceptance claims.

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
