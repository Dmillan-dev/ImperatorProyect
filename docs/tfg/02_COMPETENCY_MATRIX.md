# TFG Competency Matrix

## Current Owner Decision And Sprint — 2026-10-06

**A-S2 technical GO / formal GO for the exact approved local synthetic record. TFG-A remains IN PROGRESS. A-S3 is authorized for evidence/versioning only; A-S4 is later and AWS remains blocked.** R-20 is ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC for the current 23-CVE residual, not declared inexploitable. Exclusive expiry **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal or future-image transfer. R-23 disposition is independently approved while status remains **OPEN / NO FIX AVAILABLE**; it is not closed or included in R-20. Zero fixable High/Critical and zero secrets remain mandatory. No further upgrade is authorized. See the [registered decisions](evidence/2026-10-06-a-s2-acceptance/README.md), [security gate](TFG_A_SECURITY_CLOSURE_GATE.md) and [A-S3 work order](A_S3_EVIDENCE_VERSIONING.md).

Document status: **TFG-A IN PROGRESS / NOT A CERTIFICATION CLAIM**

This matrix identifies applied knowledge aligned with professional competency
domains. Formal AWS certifications are demonstrated only by their official
credentials, not by this project.

## DAM And Software Engineering

| ID       | Competency           | Repository application                                                                      | Evidence status     | Trace                                                                                                                                                                                             |
| -------- | -------------------- | ------------------------------------------------------------------------------------------- | ------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `DAM-01` | Java and Spring Boot | REST runtime, security, configuration and provider adapters                                 | `VALIDATED OFFLINE` | `TFG-RF-01`, `TFG-RF-02`, `TFG-RF-03`, `TFG-RF-04`, `TFG-RF-05`, `TFG-RF-06`, `TFG-RF-08` -> `E-CORE-01`, `E-CORE-02`, `E-CORE-03`, `E-CORE-04`, `E-GOV-01`, `E-GOV-02`, `E-AI-01`                |
| `DAM-02` | Web frontend         | Next.js workspace, Zod validation and role-aware presentation                               | `VALIDATED OFFLINE` | `TFG-RF-07` -> `E-UI-01`                                                                                                                                                                          |
| `DAM-03` | Relational data      | PostgreSQL repositories and Flyway V1-V3; current native/Docker evidence indexed separately | `VALIDATED OFFLINE` | `TFG-RF-05`, `TFG-NFR-04` -> `E-GOV-02`, `E-DATA-01`                                                                                                                                              |
| `DAM-04` | Architecture         | Domain/Application/Ports separated from adapters                                            | `VALIDATED OFFLINE` | `TFG-NFR-01` -> `E-ARCH-01`                                                                                                                                                                       |
| `DAM-05` | API design           | Versioned routes, errors, correlation and DTO validation                                    | `VALIDATED OFFLINE` | `TFG-RF-01`, `TFG-RF-02`, `TFG-RF-03`, `TFG-RF-04`, `TFG-RF-05`, `TFG-RF-06`, `TFG-RF-07`, `TFG-NFR-02` -> `E-CORE-01`, `E-CORE-02`, `E-CORE-03`, `E-CORE-04`, `E-GOV-01`, `E-GOV-02`, `E-SEC-01` |
| `DAM-06` | Testing              | Unit, contract, PostgreSQL, frontend and E2E suites                                         | `VALIDATED OFFLINE` | `TFG-NFR-07` -> `E-SC-02`                                                                                                                                                                         |
| `DAM-07` | Packaging            | Hardened backend, frontend and database containers                                          | `VALIDATED OFFLINE` | `TFG-NFR-06` -> `E-SC-01`                                                                                                                                                                         |

## AWS Cloud Practitioner-Aligned Knowledge

| ID      | Competency            | Application                                                            | Evidence status     | Trace                                                                                                    |
| ------- | --------------------- | ---------------------------------------------------------------------- | ------------------- | -------------------------------------------------------------------------------------------------------- |
| `CP-01` | Service selection     | Terraform defines managed compute, data, registry, logging and secrets | `VALIDATED OFFLINE` | `TFG-CLOUD-01` -> `E-AWS-01`                                                                             |
| `CP-02` | Shared responsibility | Documented AWS, application, IAM and data duties                       | `IMPLEMENTED`       | `TFG-NFR-02`, `TFG-NFR-03`, `TFG-CLOUD-01` -> `E-SEC-01`, `E-SEC-02`, `E-AWS-01`                         |
| `CP-03` | Regions and AZs       | Region-bound target with two-AZ network/ALB source                     | `VALIDATED OFFLINE` | `TFG-CLOUD-01` -> `E-AWS-01`                                                                             |
| `CP-04` | Consumption and cost  | Pilot sizing, Budgets and teardown contract                            | `IMPLEMENTED`       | `TFG-CLOUD-02`, `TFG-CLOUD-04` -> `E-AWS-02`, `E-AWS-04`                                                 |
| `CP-05` | Security              | IAM, OIDC, encryption, private-workload and audit source               | `VALIDATED OFFLINE` | `TFG-NFR-02`, `TFG-NFR-03`, `TFG-CLOUD-01`, `TFG-ID-01` -> `E-SEC-01`, `E-SEC-02`, `E-AWS-01`, `E-ID-01` |

## AWS AI Practitioner-Aligned Knowledge

| ID      | Competency            | Application                                                    | Evidence status     | Trace                                              |
| ------- | --------------------- | -------------------------------------------------------------- | ------------------- | -------------------------------------------------- |
| `AI-01` | Foundation-model use  | Bedrock Runtime Converse adapter                               | `VALIDATED OFFLINE` | `TFG-RF-08` -> `E-AI-01`                           |
| `AI-02` | Prompt/context design | Bounded deterministic context and system instruction           | `VALIDATED OFFLINE` | `TFG-RF-08` -> `E-AI-01`                           |
| `AI-03` | Grounding             | Output references limited to supplied Evidence identifiers     | `VALIDATED OFFLINE` | `TFG-RF-08` -> `E-AI-01`                           |
| `AI-04` | Responsible AI        | Model cannot decide, calculate ROI or write Ledger             | `VALIDATED OFFLINE` | `TFG-RF-08`, `TFG-RF-04` -> `E-AI-01`, `E-GOV-01`  |
| `AI-05` | Output safety         | Strict JSON fields, size, reference and secret-like validation | `VALIDATED OFFLINE` | `TFG-RF-08`, `TFG-NFR-03` -> `E-AI-01`, `E-SEC-02` |
| `AI-06` | Failure behavior      | Recommendation survives timeout/provider failure               | `VALIDATED OFFLINE` | `TFG-RF-08` -> `E-AI-01`                           |

The project does not currently implement the managed Amazon Bedrock Guardrails
service. Application-level validation must not be labelled as that service.

## Solutions Architect Associate-Aligned Knowledge

| ID       | Competency             | Application                                                             | Evidence status     | Trace                                                                            |
| -------- | ---------------------- | ----------------------------------------------------------------------- | ------------------- | -------------------------------------------------------------------------------- |
| `SAA-01` | Secure architecture    | Terraform defines HTTPS ALB, private ECS/RDS, SG paths, IAM and secrets | `VALIDATED OFFLINE` | `TFG-CLOUD-01`, `TFG-NFR-02`, `TFG-NFR-03` -> `E-AWS-01`, `E-SEC-01`, `E-SEC-02` |
| `SAA-02` | Resilient architecture | Source defines two-AZ ALB/network, circuit breaker, backups and digests | `IMPLEMENTED`       | `TFG-CLOUD-01`, `TFG-CLOUD-04` -> `E-AWS-01`, `E-AWS-04`                         |
| `SAA-03` | Performance            | Source defines Fargate/RDS sizing, gp3 and bounded provider calls       | `IMPLEMENTED`       | `TFG-CLOUD-01` -> `E-AWS-01`                                                     |
| `SAA-04` | Cost optimization      | One task/service, Single-AZ RDS, one NAT and retention limits           | `IMPLEMENTED`       | `TFG-CLOUD-02`, `TFG-CLOUD-04` -> `E-AWS-02`, `E-AWS-04`                         |
| `SAA-05` | Operations             | Terraform defines CloudWatch, alarms and CloudTrail                     | `VALIDATED OFFLINE` | `TFG-NFR-05`, `TFG-CLOUD-04` -> `E-OPS-01`, `E-AWS-04`                           |
| `SAA-06` | Delivery               | Terraform, immutable ECR references and GitHub OIDC                     | `VALIDATED OFFLINE` | `TFG-CLOUD-01`, `TFG-CLOUD-03` -> `E-AWS-01`, `E-AWS-03`                         |

The target is a disposable pilot. It does not currently implement ECS
autoscaling, Multi-AZ RDS, multi-region recovery or full production HA.

## Developer Associate-Aligned Knowledge

| ID       | Knowledge demonstrated                               | Component / source                                                                                                                                                             | Evidence and current level                                                                                                                  | Live evidence still required                                                |
| -------- | ---------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------- |
| `DVA-01` | SDK use, bounded calls and error handling            | [AWS adapter](../../backend-java/adapters/out/aws/AwsSdkEvidenceSourceAdapter.java), [Bedrock gateway](../../backend-java/adapters/out/bedrock/AwsBedrockConverseGateway.java) | `TFG-RF-08`, `TFG-CLOUD-01` -> `E-AI-01`, `E-AWS-01`; `VALIDATED OFFLINE` for tested boundaries                                             | Approved SDK calls, Region/model availability and failure cases             |
| `DVA-02` | Credential separation and protected application data | [Runtime IAM](../../infra/aws/terraform/pilot/iam.tf), D087/D088 and secret references                                                                                         | `TFG-NFR-02`, `TFG-NFR-03`, `TFG-CLOUD-01` -> `E-SEC-01`, `E-SEC-02`, `E-AWS-01`; local controls `VALIDATED OFFLINE`, cloud access unproved | Task credentials, secret delivery and permission denials                    |
| `DVA-03` | Reproducible application delivery                    | [Publish](../../.github/workflows/aws-pilot-publish.yml), [deploy](../../.github/workflows/aws-pilot-deploy.yml), Terraform roots                                              | `TFG-CLOUD-03` -> `E-AWS-03`; workflow source `IMPLEMENTED`, deployed capability `NOT IMPLEMENTED`                                          | Resolve delivery findings, publish digests, reviewed apply and verification |
| `DVA-04` | Diagnosis, latency and recovery                      | [Telemetry](../../backend-java/api/observability/ImperatorTelemetry.java), [deployment runbook](../runbooks/aws-pilot-deployment.md)                                           | `TFG-NFR-05`, `TFG-CLOUD-04` -> `E-OPS-01`, `E-AWS-04`; local `VALIDATED OFFLINE`, AWS recovery pending                                     | CloudWatch diagnosis, replacement and rollback                              |
| `DVA-05` | Automated verification and secure artifacts          | [Java CI](../../.github/workflows/java-ci.yml), [Security](../../.github/workflows/security.yml), PostgreSQL profile                                                           | `TFG-NFR-07` -> `E-SC-02`; historical hosted/offline checks; final SHA gates pending                                                        | Exact published-image checks and accepted release evidence                  |

## GitHub Foundations-Aligned Knowledge

| ID      | Knowledge demonstrated                            | Component / source                                                                                            | Evidence and current level                                                                                   | Limit                                                                        |
| ------- | ------------------------------------------------- | ------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------- |
| `GH-01` | Repositories, commits and versioned documentation | Local Git history, [README](../../README.md), [contribution guide](../../CONTRIBUTING.md)                     | `TFG-O-09`, `TFG-NFR-07` -> `E-GIT-01`; repository artifacts `IMPLEMENTED`, local history inspected          | Does not prove current remote settings                                       |
| `GH-02` | Branch/PR delivery traceability                   | Merge subjects and source revisions linked by the evidence register                                           | `TFG-O-09`, `TFG-NFR-07` -> `E-GIT-01`, `E-SC-02`; local history and historical check references             | No claim of a human review or protection merely from a merge subject         |
| `GH-03` | Automation and security practices                 | Versioned Actions, Dependabot configuration and [SECURITY.md](../../SECURITY.md)                              | `TFG-NFR-03`, `TFG-NFR-07` -> `E-SEC-02`, `E-SC-02`; `IMPLEMENTED` with recorded offline/hosted verification | GitHub environment reviewers and permissions need external inspection        |
| `GH-04` | Planning and project communication                | [Master Plan](00_TFG_MASTER_PLAN.md), [traceability](03_TRACEABILITY_MATRIX.md), risk IDs and exit conditions | `TFG-O-09` -> `E-GIT-01`; documentation `IMPLEMENTED`                                                        | No GitHub Projects board, community contribution or full exam-coverage claim |

These matrices demonstrate selected applied competencies. Neither AWS
deployment nor additional exam-listed services are required to manufacture
coverage. Professional credentials remain separate from project evidence.

## DevSecOps And Cloud Security

| ID          | Competency                 | Repository control                                          | Evidence status     | Trace                                                                            |
| ----------- | -------------------------- | ----------------------------------------------------------- | ------------------- | -------------------------------------------------------------------------------- |
| `DEVSEC-01` | CI and SAST                | Pinned GitHub Actions and CodeQL                            | `VALIDATED OFFLINE` | `TFG-NFR-07` -> `E-SC-02`                                                        |
| `DEVSEC-02` | Dependency/config scanning | Trivy source scan and npm audit                             | `VALIDATED OFFLINE` | `TFG-NFR-07` -> `E-SC-02`                                                        |
| `DEVSEC-03` | Secret prevention          | Gitleaks history scan, ignore rules and redaction           | `VALIDATED OFFLINE` | `TFG-NFR-03`, `TFG-NFR-07` -> `E-SEC-02`, `E-SC-02`                              |
| `DEVSEC-04` | Image security             | Non-root/read-only hardening and Trivy image gates          | `VALIDATED OFFLINE` | `TFG-NFR-06`, `TFG-NFR-07` -> `E-SC-01`, `E-SC-02`                               |
| `DEVSEC-05` | Supply chain               | Digest pins, checksums, provenance and immutable ECR design | `VALIDATED OFFLINE` | `TFG-NFR-06`, `TFG-NFR-07` -> `E-SC-01`, `E-SC-02`                               |
| `DEVSEC-06` | Cloud identity             | GitHub OIDC and separated build/deploy/runtime roles        | `VALIDATED OFFLINE` | `TFG-CLOUD-01`, `TFG-CLOUD-03` -> `E-AWS-01`, `E-AWS-03`                         |
| `DEVSEC-07` | Least privilege            | Read-only connectors and exact Bedrock resources            | `VALIDATED OFFLINE` | `TFG-NFR-02`, `TFG-NFR-03`, `TFG-CLOUD-01` -> `E-SEC-01`, `E-SEC-02`, `E-AWS-01` |

## Deliberately Rejected Service Accumulation

Lambda, SQS, DynamoDB, OpenSearch, Step Functions, EventBridge, API Gateway,
CloudFront, EKS and ElastiCache are not added without a demonstrated
requirement. S3 is used only for Terraform state and CloudTrail audit storage,
not as an artificial application feature.

Current image acceptance is separate from competency demonstration: the
2026-10-01 scan fails for backend/frontend (R-17), despite passing historical
D098 evidence. See the [current verification register](04_EVIDENCE_REGISTER.md).

The dated 2026-10-01 R-17 image failure above is superseded by the minimal
2026-10-02 patch passed its dated image checks. On 2026-10-06 R-19 is CLOSED;
the authorized R-21/R-22 corrected findings now pass A-S2 technically,
with formal GO registered through the explicit current R-20 acceptance and independently approved OPEN R-23 disposition. Read the
[current security review](R21_R22_SECURITY_REVIEW.md) before claiming full closure.
