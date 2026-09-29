# Local MVP To AWS MVP

Document status: **TFG-A FINAL REVIEW CANDIDATE**

The AWS target is a second runtime environment for the same application, not a
parallel product or a rewrite of business logic.

## Runtime Mapping

| Concern            | Local MVP                                | AWS target                              | Current AWS status  |
| ------------------ | ---------------------------------------- | --------------------------------------- | ------------------- |
| Entry              | Loopback frontend port                   | Route 53, ACM and public HTTPS ALB      | `VALIDATED OFFLINE` |
| Frontend           | Next.js Docker container                 | Private ECS/Fargate task                | `VALIDATED OFFLINE` |
| Backend            | Spring Boot Docker container             | Private ECS/Fargate task                | `VALIDATED OFFLINE` |
| Database           | Docker PostgreSQL volume                 | Private RDS PostgreSQL                  | `VALIDATED OFFLINE` |
| Migration          | Compose one-shot Flyway service          | One-shot Fargate migration task         | `VALIDATED OFFLINE` |
| DB privilege setup | Compose one-shot task                    | One-shot Fargate permissions task       | `VALIDATED OFFLINE` |
| Images             | Local SHA tags/digests                   | Immutable ECR digest references         | `VALIDATED OFFLINE` |
| Authentication     | Controlled local RS256 issuer fixture    | External Keycloak HTTPS                 | `BLOCKED_EXTERNAL`  |
| Authorization      | Same D088 route matrix                   | Same D088 route matrix                  | `VALIDATED OFFLINE` |
| Secrets            | Local ignored/file-backed values         | Secrets Manager and task roles          | `VALIDATED OFFLINE` |
| Logs               | ECS JSON to container output             | ECS JSON to CloudWatch Logs             | `VALIDATED OFFLINE` |
| Metrics/health     | Internal Actuator and probes             | ALB/ECS probes and CloudWatch           | `VALIDATED OFFLINE` |
| Explanation        | Disabled or mocked provider              | Optional Bedrock Converse               | `VALIDATED OFFLINE` |
| Audit              | Application Ledger and local CI evidence | Ledger plus CloudTrail management audit | `VALIDATED OFFLINE` |
| Cost               | Developer workstation                    | Metered AWS pilot                       | `NOT IMPLEMENTED`   |

## Invariants Across Environments

- the `DRC-AOA-001` policy and exact deterministic values;
- Domain/Application/Port behavior;
- REST contracts and error model;
- D087 JWT claims and D088 role grants;
- D083 human authority and append-only Ledger;
- Evidence sensitivity and redaction;
- Bedrock's optional, non-authoritative role;
- PostgreSQL migrations and least-privilege application access; and
- correlation, safe logs, metrics and health semantics.

## Environment-Specific Responsibilities

Local Docker demonstrates reproducibility, integration and a reliable defense
fallback. AWS must additionally prove account/Region identity, DNS/TLS,
network paths, IAM, secret delivery, image publication, managed-database
behavior, alarms, cost, rollback and teardown.

## Transition Gates

1. TFG-A freezes documentation and the local evidence baseline.
2. TFG-B requires explicit authority for current pricing and Terraform plan.
3. D101 external Keycloak conformance must pass before a live pilot.
4. Image publication and bootstrap writes require separate authorization.
5. Pilot apply requires a second explicit authorization.
6. TFG-D must verify runtime acceptance, cost and teardown.

No gate may infer success from the existence of the next gate's source files.
