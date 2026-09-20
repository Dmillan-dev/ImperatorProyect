# 60 - AWS MVP Deployment Preparation

Status: **IMPLEMENTED OFFLINE / APPLY BLOCKED BY D095 AND COST APPROVAL**

## Purpose

Define the smallest AWS deployment that reuses the certified containers after
D099. D100 now implements this preparation as validated Terraform and manual
OIDC workflows. This document is not evidence of a deployed environment and is
not authorization to create billable resources.

## Proposed Pilot Topology

```text
Internet
  -> Route 53 + ACM certificate
  -> public Application Load Balancer (HTTPS only)
       -> /api/v1/* backend target group
       -> all other paths frontend target group
  -> private ECS/Fargate tasks
       -> backend task role: exact Bedrock model/inference profile only
       -> CloudWatch Logs
       -> Secrets Manager references
  -> private Single-AZ RDS PostgreSQL for the bounded pilot
  -> external Keycloak issuer/JWKS over trusted HTTPS
```

The frontend and backend remain separate containers but one public HTTPS origin.
Neither PostgreSQL nor the backend receives a public IP. Security groups allow
ALB-to-task and backend-to-RDS traffic only. The backend uses the existing JWT
Resource Server and never delegates business approval to ALB, ECS or Bedrock.

Single-AZ RDS and one task per service are acceptable only for a disposable TFG
or design-partner sandbox. MVP Release with availability commitments requires a
separate Multi-AZ and minimum-task decision.

## Identity And TLS Gate

D095 must pass before deployment implementation:

- trusted HTTPS issuer and JWKS;
- exact audience `imperator-api`, RS256, `kid` rotation and role claim;
- positive and negative ADMIN, PLATFORM_ENGINEER, FINANCE and AUDITOR checks;
- no ALB authentication feature that bypasses the existing JWT contract.

The ALB accepts HTTPS only with an ACM certificate. HTTP may redirect to HTTPS
but never forwards application requests. The frontend CSP retains same-origin
API access.

## Secrets And Database Bootstrap

- RDS owner and application credentials live in separate Secrets Manager
  secrets encrypted by KMS.
- ECS injects secret values at task start; values never enter image layers,
  Terraform variables, state outputs or GitHub logs.
- A one-shot migration task runs Flyway as the owner, then provisions/verifies
  the least-privilege application role before services start.
- The long-running backend receives only application credentials.
- Migration and role-provision jobs must be made ECS-compatible before IaC is
  accepted; current Compose scripts intentionally depend on file secrets and
  the `postgres` service DNS name.

## Bedrock Boundary

The backend task role grants only `bedrock:InvokeModel` against one approved
model or application inference profile in the configured Region. The profile is
preferred for cost attribution. No wildcard model resource, Bedrock management
permission or static AWS key is allowed.

Private-subnet egress must be chosen explicitly:

- a Bedrock Runtime interface endpoint plus the endpoints required by ECS image
  pull, logs and secrets; or
- a tightly controlled NAT path when endpoint hourly cost is not justified.

Both options are billable. The cost comparison is required before apply.

## Delivery With GitHub OIDC

The future workflow uses GitHub OIDC and a protected `pilot` environment. It
contains separate roles:

- build role: push only immutable SHA-tagged images to exact ECR repositories;
- plan role: read infrastructure plus write a plan artifact;
- deploy role: update only the approved stack and ECS services after manual
  environment approval.

No long-lived AWS key is stored in GitHub. Trust conditions bind repository,
branch or environment and audience. Actions and Terraform providers must be
commit/version pinned and pass the existing source, dependency, CodeQL, secret
and image gates before deployment.

## Post-Deploy And Rollback

Post-deploy checks are:

1. TLS chain, redirect and security headers;
2. liveness/readiness through the ALB;
3. Keycloak positive and negative JWT/RBAC matrix;
4. API composition of a synthetic DRC-AOA-001 case;
5. one explicitly authorized Bedrock explanation plus stable repeated GET;
6. human review and Ledger evidence;
7. persistence after task replacement; and
8. CloudWatch log/metric redaction checks.

Rollback pins the prior backend/frontend image digests and task definitions.
Database migrations remain forward-only; a migration requiring destructive
rollback is not admissible for this pilot.

## Cost And Teardown Gate

Before apply, capture an AWS Pricing Calculator estimate for the selected
Region covering Fargate vCPU/memory, RDS instance/storage/backups, ALB hours and
LCUs, public IPv4, CloudWatch, ECR, Secrets Manager/KMS, data transfer and either
NAT or interface endpoints. Bedrock cost is measured separately from token use.

The operator must approve a monthly budget and alarm threshold. Teardown deletes
ECS services, ALB, endpoints/NAT and RDS after an explicitly chosen final
snapshot policy, then verifies that snapshots, ECR images, public IPv4 addresses
and Secrets Manager recovery windows do not continue billing unnoticed.

## Remaining Authorization

AWS apply begins only after:

1. D099 local and hosted gates pass;
2. D095 external Keycloak HTTPS conformance passes;
3. Region, DNS name, model/inference profile and monthly budget are frozen; and
4. the user explicitly authorizes the concrete resource plan and estimated
   recurring cost.

The implementation and operating sequence are frozen in
[`61_AWS_SAA_Portfolio_Deployment_Contract.md`](61_AWS_SAA_Portfolio_Deployment_Contract.md)
and [`../runbooks/aws-pilot-deployment.md`](../runbooks/aws-pilot-deployment.md).
