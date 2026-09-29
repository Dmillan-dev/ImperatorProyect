# TFG Evidence Register

Document status: **TFG-A FINAL REVIEW CANDIDATE / FINAL CAPTURE PENDING**

## Execution Classification

| Class                    | Use in this register                                                                   |
| ------------------------ | -------------------------------------------------------------------------------------- |
| `EXECUTED OFFLINE`       | A local or hosted check has actually run without a live AWS/IdP runtime.               |
| `PLANNED OFFLINE`        | The evidence can be produced without deployment but has not yet been captured.         |
| `EXTERNAL GATE REQUIRED` | The evidence needs an unavailable IdP or separately authorized live AWS activity.      |
| `EXECUTED EXTERNALLY`    | The evidence ran against identified external resources; no current row has this class. |

## Baseline Evidence

| Evidence ID | Claim                                                  | Current evidence                            | Capability status   | Execution class          | Final action                       |
| ----------- | ------------------------------------------------------ | ------------------------------------------- | ------------------- | ------------------------ | ---------------------------------- |
| `E-CORE-01` | Evidence import is validated and idempotent            | Java/API/PostgreSQL suites                  | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Export final report by SHA         |
| `E-CORE-02` | DRC-AOA-001 composition is deterministic and resumable | D093 tests and certified local runtime      | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Capture final composition result   |
| `E-CORE-03` | Recommendation/ROI values are deterministic            | Domain policy tests                         | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Include exact expected-value table |
| `E-CORE-04` | Business Value follows validated Ledger facts          | Query and E2E tests                         | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Capture final projection           |
| `E-GOV-01`  | Human role and actor authority are enforced            | D083/D088 tests                             | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Include negative authority cases   |
| `E-GOV-02`  | Ledger is append-only and ordered                      | Domain/PostgreSQL tests                     | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Capture final entry sequence       |
| `E-UI-01`   | Workspace presents the complete case by role           | Frontend and Playwright evidence            | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Refresh final screenshots          |
| `E-AI-01`   | Bedrock explanation path is bounded and fail-safe      | D099 adapter and offline tests              | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Live smoke remains pending         |
| `E-ARCH-01` | Business layers remain provider-independent            | Source/import inspection                    | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Add dependency diagram             |
| `E-SEC-01`  | JWT/RBAC fail closed locally                           | D087/D088 contract tests                    | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | External IdP is separate `E-ID-01` |
| `E-SEC-02`  | Sensitive Evidence and secrets are protected           | Redaction tests, Trivy and Gitleaks         | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Export final scan summaries        |
| `E-DATA-01` | PostgreSQL migrations and persistence pass             | D094/Flyway/integration evidence            | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Bind final versions and SHA        |
| `E-OPS-01`  | Logs, correlation, metrics and probes are bounded      | D097 tests and hosted gate                  | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Capture final local transitions    |
| `E-SC-01`   | Runtime images meet the project policy                 | Application image and D094 hosted gates     | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Preserve final digests/SBOM        |
| `E-SC-02`   | CI and security controls pass                          | GitHub checks on `4dbf4fb` and `22a9917`    | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Link final release checks          |
| `E-AWS-01`  | AWS architecture is reproducible offline               | Terraform source, tests and hosted IaC gate | `VALIDATED OFFLINE` | `EXECUTED OFFLINE`       | Preserve final test output         |
| `E-AWS-02`  | Pilot cost is current and approved                     | No current Calculator export                | `NOT IMPLEMENTED`   | `PLANNED OFFLINE`        | Execute only in authorized TFG-B   |
| `E-AWS-03`  | Application runs on AWS                                | No resource has been deployed               | `NOT IMPLEMENTED`   | `EXTERNAL GATE REQUIRED` | Execute only in authorized TFG-C   |
| `E-AWS-04`  | Rollback and teardown remove the pilot safely          | Runbook only                                | `NOT IMPLEMENTED`   | `EXTERNAL GATE REQUIRED` | Execute only in TFG-D              |
| `E-ID-01`   | External Keycloak satisfies D095                       | No external IdP is available                | `BLOCKED_EXTERNAL`  | `EXTERNAL GATE REQUIRED` | Mandatory before Pilot Readiness   |

## Latest Non-Canonical Audit Observation

The TFG-A preflight executed the following offline on `2026-09-28` against
baseline `4dbf4fb`:

- 174 Maven default tests passed;
- 41 frontend tests passed;
- frontend lint, TypeScript and production build passed;
- npm audit reported zero vulnerabilities; and
- a current Trivy filesystem scan reported no fixable High/Critical finding,
  secret or scanned Docker/Terraform misconfiguration.

These observations support planning but are not the final academic evidence
pack because their complete machine-readable reports were not committed. The
final run must produce one dated, sanitized evidence index bound to the final
release SHA.

## Evidence Capture Schema

Each final evidence item records:

```text
evidenceId
requirementIds
sourceRevision
executedAtUtc
environmentClass
toolAndVersion
commandOrWorkflow
result
artifactDigestOrUrl
redactions
limitations
reviewer
```

## Redaction Rules

Evidence must never contain bearer tokens, passwords, private keys, AWS access
keys, raw prompts/completions, Restricted Evidence, customer data or secret
values. Cloud screenshots must hide account identifiers where unnecessary.
Issuer host, Region, model alias, resource type, status, duration and cost may
be recorded when they do not expose credentials or personal information.

## Claim Rules

- Repository source proves `IMPLEMENTED`, not live operation.
- Unit or mocked-provider tests prove `VALIDATED OFFLINE`.
- A Terraform plan does not prove deployment.
- A deployed resource does not prove application acceptance.
- A screenshot without an immutable execution reference is illustrative only.
- Estimated savings and AWS prices are not realized Business Value or cost.
