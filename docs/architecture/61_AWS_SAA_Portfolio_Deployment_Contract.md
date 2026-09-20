# 61 - AWS SAA Portfolio Deployment Contract

Status: **D100 ACCEPTED / IMPLEMENTED OFFLINE / AWS APPLY NOT AUTHORIZED**

Repository implementation evidence: **HOSTED REPOSITORY PASS on merge
`22a9917`**. This satisfies the source, image and offline IaC gate only; it does
not satisfy the live acceptance evidence below or authorize an AWS action.

## Decision Scope

D100 turns the existing IMPERATOR runtime into a reproducible AWS pilot design
that can demonstrate Solutions Architect Associate and Developer Associate
competencies. It authorizes repository code, offline validation and delivery
workflows. It does not authorize an AWS login, image publication, Bedrock call
or creation of a billable resource.

The business path remains unchanged:

```text
Evidence
  -> deterministic Recommendation and ROI
  -> optional Bedrock explanation
  -> human review
  -> append-only Ledger and explanation audit
```

AWS hosts and observes this path. It does not become a source of recommendation
or approval authority.

## Reference Architecture

```text
Route 53 -> ACM -> public ALB (HTTP redirect, HTTPS routing)
                    |-> private frontend ECS/Fargate task
                    |-> private backend ECS/Fargate task
                            |-> private Single-AZ RDS PostgreSQL 18.6
                            |-> external Keycloak over trusted HTTPS
                            |-> Bedrock Runtime through the task IAM role
                            |-> read-only AWS Evidence APIs

GitHub Actions OIDC -> immutable ECR images -> Terraform pilot deployment
ECS/ALB/RDS -> CloudWatch logs, Container Insights, metrics and alarms
AWS management events -> Region-scoped CloudTrail -> encrypted S3, 90 days
AWS Budgets -> 80% actual and 100% forecast notifications
```

The ALB and private subnets span two Availability Zones. One NAT gateway keeps
the bounded pilot simpler and cheaper than a complete interface-endpoint set,
but it is an accepted single-AZ egress dependency. RDS is Single-AZ and each
service starts with one task. These are deliberate disposable-pilot choices,
not production availability claims.

The public ALB is the intentional HTTPS product boundary, so Terraform carries
one narrowly scoped Trivy `AWS-0053` suppression on that resource. Only ports
80 and 443 reach the ALB; port 80 redirects to HTTPS, ECS targets remain in
private subnets and their security groups accept traffic only from the ALB.
This is a documented architecture exception, not a vulnerability waiver. No
other D100 High/Critical misconfiguration suppression is authorized.

## SAA And Developer Evidence

| Competency | Repository evidence | Remaining live proof |
|---|---|---|
| Secure design | HTTPS-only ALB, private tasks/RDS, explicit security-group paths, Secrets Manager references, task/execution role separation, exact Bedrock ARNs and GitHub OIDC subject restrictions | D095 Keycloak matrix, IAM Access Analyzer review and deployed negative tests |
| Resilient design | Multi-AZ ALB/subnets, ECS deployment circuit breaker, RDS backups, immutable image digests and rerunnable one-shot migrations | Task-replacement, rollback and restore evidence |
| High-performing design | Managed ALB/Fargate/RDS, bounded task sizes, gp3 storage autoscaling and Bedrock Converse time/token limits | Load observations and right-sizing evidence |
| Cost-optimized design | One task per service, Single-AZ RDS, one NAT, retention limits, ECR lifecycle, monthly budget and teardown contract | Region-specific Pricing Calculator export and measured Cost Explorer evidence |
| Application integration | AWS SDK default credential chain, read-only AWS Evidence adapter and audited Bedrock provider behind hexagonal ports | One authorized sandbox synchronization and one Bedrock explanation |
| Delivery automation | Checksum-pinned Terraform, locked provider, offline plans, digest-pinned images, protected environment and short-lived OIDC sessions | Hosted publish, plan, approval and deployment runs |
| Operations | ECS JSON logs, correlation IDs, probes, metrics, alarms, CloudTrail and bounded retention | Alarm transition, redaction and incident/rollback drill |

## Infrastructure Boundaries

The IaC has two independent roots:

- `bootstrap`: S3 state, immutable ECR repositories, GitHub OIDC provider and
  separate build/deploy roles. It is applied manually once with an already
  authorized bootstrap principal and retains local state securely.
- `pilot`: VPC, subnets, one NAT, ALB, ECS/Fargate, RDS, runtime roles,
  CloudWatch, SNS, AWS Budgets, CloudTrail and DNS. Its remote state uses the
  bootstrap bucket and native S3 locking.

Images are accepted only as `repository@sha256:digest`, and each digest must
match the immutable ECR tag for the exact accepted `main` source revision. The
frontend image is built for the final HTTPS origin. The first pilot apply leaves
both services at zero tasks. The delivery workflow must then run Flyway and
application-role provisioning as separate Fargate tasks and activate services
only after both return exit code zero.

Terraform never receives a database password. RDS manages the owner secret;
the application password is created out of band in Secrets Manager and only
its ARN enters Terraform. The owner credential is available solely to the
one-shot database tasks. The long-running backend receives only the
least-privilege application credential.

## Identity And AI Gates

D095 is still mandatory before apply. The configured issuer and JWKS must be
the exact externally verified Keycloak HTTPS endpoints, with audience
`imperator-api`, RS256, `kid` rotation and all positive/negative role cases.

Bedrock remains optional and non-authoritative. Its task policy permits only
`bedrock:InvokeModel` for the explicitly supplied model or inference-profile
ARNs. A real smoke test uses synthetic Evidence and records the explanation
attempt; it does not use customer data and does not change the Recommendation
or Ledger.

## Cost And Availability Decision

For `eu-west-1`, the planning envelope is **USD 90-130 per continuously running
month**, excluding tax, domain registration, significant data transfer and
Bedrock usage. NAT gateway, ALB, RDS and public IPv4 time dominate this small
environment; Fargate, logs, secrets and S3 are secondary at pilot volume. This
is an engineering envelope, not an AWS quote.

Before any apply, the operator must attach a current AWS Pricing Calculator
estimate, approve `monthly_budget_usd`, confirm the notification address and
choose whether synthetic data permits `database_skip_final_snapshot=true`.
Short demonstrations should be torn down promptly. The state bucket and ECR
bootstrap survive pilot teardown until explicitly reviewed.

## Acceptance Evidence

D100 can become `HOSTED PASS` only when all of the following exist:

1. D099 hosted Java, frontend, security, image and CodeQL gates pass.
2. D095 external Keycloak HTTPS conformance passes.
3. Terraform plan is reviewed against the approved account, Region and cost.
4. Immutable images are published from an exact `main` commit through OIDC.
5. Migration and privilege tasks pass before services activate.
6. TLS, headers, probes, JWT/RBAC, persistence and task replacement pass.
7. One synthetic Bedrock explanation and one AWS Evidence sync are audited.
8. Logs contain no secret, raw restricted Evidence or bearer token.
9. Rollback and teardown are exercised and residual billable assets checked.

Until then, the exact claim is **implemented and tested offline; not deployed or
verified against live AWS services**.
