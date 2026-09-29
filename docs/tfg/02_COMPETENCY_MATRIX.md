# TFG Competency Matrix

Document status: **TFG-A FINAL REVIEW CANDIDATE / NOT A CERTIFICATION CLAIM**

This matrix identifies applied knowledge aligned with professional competency
domains. Formal AWS certifications are demonstrated only by their official
credentials, not by this project.

## DAM And Software Engineering

| ID       | Competency           | Repository application                                        | Evidence status     | Trace                                                                                                                                                                                             |
| -------- | -------------------- | ------------------------------------------------------------- | ------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `DAM-01` | Java and Spring Boot | REST runtime, security, configuration and provider adapters   | `VALIDATED OFFLINE` | `TFG-RF-01`, `TFG-RF-02`, `TFG-RF-03`, `TFG-RF-04`, `TFG-RF-05`, `TFG-RF-06`, `TFG-RF-08` -> `E-CORE-01`, `E-CORE-02`, `E-CORE-03`, `E-CORE-04`, `E-GOV-01`, `E-GOV-02`, `E-AI-01`                |
| `DAM-02` | Web frontend         | Next.js workspace, Zod validation and role-aware presentation | `VALIDATED OFFLINE` | `TFG-RF-07` -> `E-UI-01`                                                                                                                                                                          |
| `DAM-03` | Relational data      | PostgreSQL repositories, transactions and Flyway V1-V3        | `VALIDATED OFFLINE` | `TFG-RF-05`, `TFG-NFR-04` -> `E-GOV-02`, `E-DATA-01`                                                                                                                                              |
| `DAM-04` | Architecture         | Domain/Application/Ports separated from adapters              | `VALIDATED OFFLINE` | `TFG-NFR-01` -> `E-ARCH-01`                                                                                                                                                                       |
| `DAM-05` | API design           | Versioned routes, errors, correlation and DTO validation      | `VALIDATED OFFLINE` | `TFG-RF-01`, `TFG-RF-02`, `TFG-RF-03`, `TFG-RF-04`, `TFG-RF-05`, `TFG-RF-06`, `TFG-RF-07`, `TFG-NFR-02` -> `E-CORE-01`, `E-CORE-02`, `E-CORE-03`, `E-CORE-04`, `E-GOV-01`, `E-GOV-02`, `E-SEC-01` |
| `DAM-06` | Testing              | Unit, contract, PostgreSQL, frontend and E2E suites           | `VALIDATED OFFLINE` | `TFG-NFR-07` -> `E-SC-02`                                                                                                                                                                         |
| `DAM-07` | Packaging            | Hardened backend, frontend and database containers            | `VALIDATED OFFLINE` | `TFG-NFR-06` -> `E-SC-01`                                                                                                                                                                         |

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
