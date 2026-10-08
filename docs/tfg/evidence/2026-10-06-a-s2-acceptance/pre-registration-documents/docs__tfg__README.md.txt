# IMPERATOR TFG Academic Evidence

## Current R-20 Applicability Review — 2026-10-06

The current23-CVE/115-row applicability review is complete:3affected optional subcomponents absent,1unused protocol,19conditional residuals with explicit prerequisites/uncertainties. Historical Perl probe-name discrepancy resolved without editing old evidence. `braces` is absent as a package from the inspected production image but present in development tooling. Current R20 proposal remains `accepted:false`; independent R23 remains **OPEN / NO FIX AVAILABLE / NOT ACCEPTED**. No upgrades or automatic advancement. A-S2 technical GO; formal closure awaits explicit owner disposition; original exclusive expiry2026-10-09T00:00:00+02:00 remains unchanged. See the [applicability review](R20_APPLICABILITY_REVIEW.md), exact proposals and artifact index.

Document status: **TFG-A IN PROGRESS / A-S2 TECHNICAL GO / FORMAL SECURITY CLOSURE PENDING RESIDUAL DISPOSITION**

Implementation baseline for this iteration: `b602bbe1dde92a902c17272d59cd9f0c10b9efe5`

Started by founder instruction on `2026-10-01`, then extended to bounded
test/format maintenance, Docker rehearsal, capture and verified residue cleanup.
On 2026-10-02 the five-finding R-17 patch and complete functional/runtime rerun
pass; all three fixable-only image vulnerability/secret gates are zero.
Current local assessment after explicitly authorized R-21/R-22: **2026-10-06**. R-17/R-19 and the authorized R-21/R-22 corrected findings are CLOSED. **A-S2 technical GO; formal security closure PENDING RESIDUAL DISPOSITION.** Exact-image regression174/35/41, Docker/RBAC/V3/persistence/recovery, zero fixable High/Critical and zero secrets PASS. Full scans retain non-fixable residuals. Historical R-20 approval does not transfer; current binding accepted=false / REVIEW REQUIRED. Braces is separately tracked R-23 OPEN / NOT ACCEPTED. TFG-A IN PROGRESS; A-S3/A-S4/TFG-B/AWS blocked. See the [current review](R21_R22_SECURITY_REVIEW.md) and [gate](TFG_A_SECURITY_CLOSURE_GATE.md).

This directory explains how the existing IMPERATOR implementation supports a
DAM final project focused on software engineering, cloud architecture,
responsible generative AI and DevSecOps. It is an academic evidence view. It
does not replace project status, accepted decisions or frozen contracts, and it
authorizes no implementation or external execution.

## Central Thesis

> The same MVP is first designed as a cloud-native application that can run
> locally and is then prepared for deployment on AWS through a managed, secure
> and reproducible architecture.

The academic case remains exactly `DRC-AOA-001` and uses synthetic data only:

```text
Evidence
-> Decision
-> Deterministic Recommendation and ROI
-> Optional AI explanation
-> Human Review
-> Append-only Ledger
-> Business Value
```

## Package

| Document                                         | Question answered                                                  |
| ------------------------------------------------ | ------------------------------------------------------------------ |
| [Master Plan](00_TFG_MASTER_PLAN.md)             | How will the TFG and MVP evidence be completed?                    |
| [Academic Scope](01_ACADEMIC_SCOPE.md)           | What is included, excluded and objectively successful?             |
| [Competency Matrix](02_COMPETENCY_MATRIX.md)     | What DAM, cloud, AI and DevSecOps knowledge is demonstrated?       |
| [Traceability Matrix](03_TRACEABILITY_MATRIX.md) | Which requirement maps to which implementation, test and evidence? |
| [Evidence Register](04_EVIDENCE_REGISTER.md)     | What has actually been executed and what remains pending?          |
| [Architecture](05_ARCHITECTURE.md)               | How are the local and AWS designs structured and justified?        |
| [Local vs AWS](06_LOCAL_VS_AWS.md)               | How does the same application move to managed AWS services?        |
| [Demo and Defense](07_DEMO_AND_DEFENSE.md)       | How will the implemented result be demonstrated and defended?      |

## Capability Evidence Status

| Status              | Meaning                                                                  |
| ------------------- | ------------------------------------------------------------------------ |
| `IMPLEMENTED`       | Source or configuration exists in the repository.                        |
| `VALIDATED OFFLINE` | Tests or static checks passed without a live AWS deployment.             |
| `VALIDATED IN AWS`  | The behavior was executed against identified AWS resources.              |
| `OPERATIONAL`       | A deployed pilot completed its runtime acceptance period.                |
| `NOT IMPLEMENTED`   | No implementation or objective runtime evidence exists.                  |
| `BLOCKED_EXTERNAL`  | A mandatory external dependency is unavailable, so the gate cannot pass. |

An implementation is never described as validated in AWS merely because
Terraform, an SDK adapter or a workflow exists.

## Evidence Execution Class

| Class                    | Meaning                                                                       |
| ------------------------ | ----------------------------------------------------------------------------- |
| `EXECUTED OFFLINE`       | The check ran locally or in hosted CI without a live AWS/IdP runtime.         |
| `PLANNED OFFLINE`        | The evidence is designed but has not yet been executed or captured.           |
| `EXTERNAL GATE REQUIRED` | Execution needs a separately authorized or unavailable AWS/IdP environment.   |
| `EXECUTED EXTERNALLY`    | The evidence ran against identified external resources. None exists in TFG-A. |

Capability status and execution class are independent. For example, Terraform
can be `VALIDATED OFFLINE` with `EXECUTED OFFLINE` evidence while the AWS
deployment remains `NOT IMPLEMENTED` and requires an external gate.

## Current Answer

IMPERATOR is a modular Java and Next.js decision-governance application that
turns normalized operational Evidence into one deterministic Recommendation,
estimated ROI, human-governed Ledger history and projected Business Value. It
demonstrates DAM engineering through a hexagonal modular monolith, PostgreSQL,
REST, JWT/RBAC, tests and Docker; cloud architecture through offline-validated
AWS Terraform; responsible AI through a bounded Bedrock explanation adapter;
and DevSecOps through pinned CI, CodeQL, dependency, secret, image and IaC
controls.

The project control plane records the local MVP as certified. Relative to the
TFG cloud claim, its evidence is `VALIDATED OFFLINE`. AWS Terraform source and
the Bedrock adapter are implemented and validated offline only. No live AWS
resource, real Bedrock invocation or AWS cost has been validated. External
Keycloak conformance is `BLOCKED_EXTERNAL` because no operational IdP exists,
and remains mandatory before Pilot Readiness.

## Governance Boundary

```text
D087 / D088 / D095: unchanged
D101 scheduling: OPERATOR-DEFERRED
D101 capability evidence status: BLOCKED_EXTERNAL
Pilot consequence: mandatory before Pilot Readiness
AWS / Terraform plan/apply / Bedrock live execution: EXTERNAL GATE REQUIRED
```

TFG-A includes only founder-authorized maintenance, evidence capture and
documentation. It introduces no new product feature, architecture decision or
release claim. Historical certification is separate from the current
image/security audit results; all AWS/IdP dependencies retain their gates.

## Current A-S1 / A-S2 Status

Current local assessment after explicitly authorized R-21/R-22: **2026-10-06**. R-17/R-19 and the authorized R-21/R-22 corrected findings are CLOSED. **A-S2 technical GO; formal security closure PENDING RESIDUAL DISPOSITION.** Exact-image regression174/35/41, Docker/RBAC/V3/persistence/recovery, zero fixable High/Critical and zero secrets PASS. Full scans retain non-fixable residuals. Historical R-20 approval does not transfer; current binding accepted=false / REVIEW REQUIRED. Braces is separately tracked R-23 OPEN / NOT ACCEPTED. TFG-A IN PROGRESS; A-S3/A-S4/TFG-B/AWS blocked. See the [current review](R21_R22_SECURITY_REVIEW.md) and [gate](TFG_A_SECURITY_CLOSURE_GATE.md).

The [original R-20 approval](evidence/2026-10-02-r20-acceptance/acceptance.json)
remains a preserved historical record. Expiry stays 2026-10-09T00:00:00+02:00
Europe/Madrid, no renewal. Its early-invalidation conditions now apply to the
changed version and provider fixes; no automatic transfer or new acceptance.

Read the [current evidence pack](evidence/2026-10-06-r19/README.md) before
claiming local certification. Earlier dated packs retain their historical states.
