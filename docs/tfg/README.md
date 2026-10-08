# IMPERATOR TFG Academic Evidence

## Delivery Materials — 2026-10-07

[Memory draft](08_LOCAL_MVP_MEMORY.md), [twelve-minute script](09_DEFENSE_SCRIPT_12_MIN.md),
[A-S4 procedure](A_S4_LOCAL_DEFENSE.md) and [release gate](10_LOCAL_MVP_RELEASE_GATE.md) are prepared.
A-S3 source review/version/CI remains active; A-S4 final runtime and narration remain unexecuted.
The [dated review](evidence/2026-10-07-a-s3-a-s4/README.md) records stale-document discrepancies, preserved history and current Docker unavailability.
No accepted SHA, automatic risk extension or new CI/runtime result is claimed.

## Current Owner Decision And Sprint — 2026-10-06

**A-S2 technical GO / formal GO for the exact approved local synthetic record. TFG-A remains IN PROGRESS. A-S3 is authorized for evidence/versioning only; A-S4 is later and AWS remains blocked.** R-20 is ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC for the current 23-CVE residual, not declared inexploitable. Exclusive expiry **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal or future-image transfer. R-23 disposition is independently approved while status remains **OPEN / NO FIX AVAILABLE**; it is not closed or included in R-20. Zero fixable High/Critical and zero secrets remain mandatory. No further upgrade is authorized. See the [registered decisions](evidence/2026-10-06-a-s2-acceptance/README.md), [security gate](TFG_A_SECURITY_CLOSURE_GATE.md) and [A-S3 work order](A_S3_EVIDENCE_VERSIONING.md).

Document status: **TFG-A IN PROGRESS / A-S2 TECHNICAL AND FORMAL GO / A-S3 EVIDENCE-VERSIONING ACTIVE**

Implementation baseline for this iteration: `b602bbe1dde92a902c17272d59cd9f0c10b9efe5`

Started by founder instruction on `2026-10-01`, then extended to bounded
test/format maintenance, Docker rehearsal, capture and verified residue cleanup.
On 2026-10-02 the five-finding R-17 patch and complete functional/runtime rerun
pass; all three fixable-only image vulnerability/secret gates are zero.

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

## Earlier A-S1 / A-S2 Checkpoint (Superseded)

The [original R-20 approval](evidence/2026-10-02-r20-acceptance/acceptance.json)
remains a preserved historical record. Expiry stays 2026-10-09T00:00:00+02:00
Europe/Madrid, no renewal. Its early-invalidation conditions now apply to the
changed version and provider fixes; no automatic transfer or new acceptance.

Read the [current runtime evidence pack](evidence/2026-10-06-r21-r22/README.md) and
[current formal acceptance](evidence/2026-10-06-a-s2-acceptance/README.md) before
claiming local certification. Earlier dated packs retain their historical states.
