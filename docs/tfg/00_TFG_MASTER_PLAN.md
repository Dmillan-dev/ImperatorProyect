# IMPERATOR DAM TFG Master Plan

Document status: **TFG-A FINAL REVIEW CANDIDATE / ACADEMIC PACKAGING ONLY**

Planning baseline: `4dbf4fbe89dad0c6fcfd906aac7624075846c37a`
Planning date: `2026-09-29`

## 1. Academic Positioning

Recommended title:

> Design and implementation of a secure cloud-native platform for operational
> decision traceability and analysis, deployable on AWS and integrated with
> responsible generative AI.

The academic value is not the number of technologies used. It is the ability
to justify one coherent system from requirements through architecture,
implementation, security, testing, cloud preparation, cost and limitations.

Four evidence pillars organize the defense:

1. DAM and software engineering: Java 21, Spring Boot, Next.js, PostgreSQL,
   REST, hexagonal architecture, tests and Docker.
2. AWS architecture knowledge: VPC, ALB, ECS/Fargate, RDS, ECR, IAM/OIDC,
   secrets, observability, Terraform, availability and cost trade-offs.
3. Responsible generative AI: Bedrock Converse, bounded context, output
   validation, failure isolation and human authority.
4. DevSecOps and cloud security: CodeQL, Trivy, Gitleaks, immutable images,
   provenance, least privilege and supply-chain gates.

These pillars demonstrate applied knowledge aligned with AWS certification
domains. They do not claim or replace an AWS certification.

## 2. Delivery Tracks

| Track | Purpose                                                               | Current status   | External execution                    |
| ----- | --------------------------------------------------------------------- | ---------------- | ------------------------------------- |
| TFG-A | Freeze scope, competencies, evidence, architecture and local demo     | `IN PROGRESS`    | Forbidden                             |
| TFG-B | Review architecture, current pricing and an authorized Terraform plan | `NOT AUTHORIZED` | Separate authorization required       |
| TFG-C | Deploy the synthetic AWS MVP                                          | `NOT AUTHORIZED` | Separate apply authorization required |
| TFG-D | Validate runtime, cost, rollback and teardown                         | `NOT AUTHORIZED` | Runs only after TFG-C approval        |

These labels are academic delivery tracks and must not be confused with D101
Stages A-C. D101 remains the external identity gate defined by the project
control plane.

`IN PROGRESS` and `NOT AUTHORIZED` describe academic-track workflow only. They
are not product sprint, capability or evidence statuses.

## 3. TFG-A Deliverables

- frozen academic title, problem, objective and exclusions;
- one canonical `DRC-AOA-001` value loop;
- competency and requirement traceability matrices;
- evidence register with unambiguous execution status;
- local and AWS architecture diagrams;
- local-to-AWS comparison and trade-off record;
- local demonstration and defense script;
- final-memory structure and evidence capture rules; and
- explicit account of every external item that remains unexecuted.

## 4. Proposed Final Memory

| Chapter                      | Target content                                               |
| ---------------------------- | ------------------------------------------------------------ |
| Abstract                     | Problem, method, implemented result and explicit limitations |
| 1. Introduction              | Motivation, context, users and academic relevance            |
| 2. Objectives and scope      | General/specific objectives, MVP and exclusions              |
| 3. Requirements and planning | RF/RNF, methodology, schedule, risks and budget              |
| 4. Analysis and architecture | Domain, data, local architecture and AWS target              |
| 5. Security and DevSecOps    | JWT/RBAC, secrets, supply chain and threat controls          |
| 6. Implementation            | Java, Next.js, PostgreSQL, Docker and provider adapters      |
| 7. Responsible AI            | Bedrock boundary, context, validation and human review       |
| 8. AWS design                | Networking, compute, data, IAM, operations and trade-offs    |
| 9. Verification              | Unit, integration, E2E, security and IaC evidence            |
| 10. Cost and operations      | Estimates, measured cost, rollback and teardown              |
| 11. Results                  | Local result and, only if executed, AWS result               |
| 12. Conclusions              | Objective assessment, limitations and future work            |

The final institution template and submission date must be confirmed with the
tutor. No date in this plan is represented as an official academic deadline.

## 5. Relative Schedule

| Cycle | Outcome                                                                  |
| ----- | ------------------------------------------------------------------------ |
| A1    | TFG-A package reviewed and scope frozen                                  |
| A2    | Memory skeleton, diagrams and RF/RNF table populated                     |
| A3    | Local demo rehearsed and final local evidence captured by SHA            |
| B1    | TFG-B architecture and current Pricing Calculator review authorized      |
| B2    | Terraform plan and security review completed without apply               |
| C1    | D101 external identity gate completed or AWS pilot remains `NO-GO`       |
| C2    | AWS bootstrap/publication separately authorized and verified             |
| C3    | AWS MVP apply separately authorized and executed with synthetic data     |
| D1    | E2E, Bedrock, replacement, rollback, cost and teardown evidence captured |
| Final | Release evidence frozen; memory, annexes and defense completed           |

TFG-A through the local defense can proceed while D101 is operator-deferred.
Live Pilot Readiness and AWS deployment cannot use that deferral as a bypass.

## 6. Principal Risks

| Risk                                    | Impact         | Treatment                                                     |
| --------------------------------------- | -------------- | ------------------------------------------------------------- |
| Scope expansion                         | High           | One use case, four roles and no new platform capability       |
| Confusing implementation with execution | High           | Apply the evidence vocabulary in every chapter and table      |
| External Keycloak unavailable           | High for pilot | Keep D101 deferred; prohibit Pilot Readiness claims           |
| AWS cost overrun                        | High           | Pricing review, budget, short runtime and mandatory teardown  |
| Live model unavailable                  | Medium         | Keep Bedrock optional; preserve deterministic recommendation  |
| Secret or token disclosure              | Critical       | OIDC, redaction, scanners and no persisted bearer token       |
| Cloud failure during defense            | High           | Maintain local demo and a sanitized recorded fallback         |
| Inconsistent test counts                | Medium         | Produce one final report tied to the release SHA              |
| Overstating availability                | High           | State one NAT, one task/service and Single-AZ RDS limitations |

## 7. TFG-A Definition Of Done

TFG-A is complete only when:

- every document in this package is internally consistent;
- every claim links to repository evidence or is marked pending;
- no AWS, Terraform plan/apply, Bedrock or external IdP execution occurred;
- no functional source, workflow, dependency or frozen contract changed;
- the branch diff contains documentation only;
- documentation validation passes; and
- a reviewer can answer what was built, why, how, what it demonstrates and
  what remains pending without inspecting implementation history.

TFG-A completion does not open TFG-B, change D101 or authorize AWS.

## 8. Final MVP Release Boundary

The local academic MVP is demonstrable before live AWS. The AWS MVP and project
MVP Release require separate objective evidence: reviewed cost and plan,
external identity conformance, immutable published images, HTTPS, private
tasks/data, successful migrations, synthetic E2E, one audited Bedrock call,
safe telemetry, persistence after task replacement, rollback, teardown and all
required hosted checks passing on the accepted SHA.
