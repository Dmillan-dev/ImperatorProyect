# AWS Pilot Deployment Runbook

Status: **PREPARED / EXECUTION BLOCKED BY D095, COST APPROVAL AND AWS ACCESS**

## 1. Freeze The Inputs

Do not continue until these values are reviewed:

- dedicated sandbox AWS account ID and one Region, initially `eu-west-1`;
- public Route 53 zone, final hostname and validated same-Region ACM certificate;
- D095-approved Keycloak issuer and JWKS HTTPS URIs;
- approved Bedrock model or application inference profile and exact invoke ARNs;
- monthly Pricing Calculator estimate, budget and notification address;
- final-snapshot choice and synthetic-data confirmation.

Install AWS CLI v2 and Terraform `1.16.3`. Use an authorized short-lived local
identity for bootstrap only. Never place AWS keys or password values in a
`.tfvars` file, shell history, GitHub variable or workflow log.

## 2. Validate Offline

From the repository root:

```powershell
terraform -chdir=infra/aws/terraform/bootstrap fmt -check -recursive
terraform -chdir=infra/aws/terraform/bootstrap init -backend=false -lockfile=readonly
terraform -chdir=infra/aws/terraform/bootstrap validate
terraform -chdir=infra/aws/terraform/bootstrap test

terraform -chdir=infra/aws/terraform/pilot fmt -check -recursive
terraform -chdir=infra/aws/terraform/pilot init -backend=false -lockfile=readonly
terraform -chdir=infra/aws/terraform/pilot validate
terraform -chdir=infra/aws/terraform/pilot test
```

## 3. Bootstrap Once

Create an ignored `terraform.tfvars` from the example, inspect the plan and
apply only after explicit cost/account approval:

```powershell
terraform -chdir=infra/aws/terraform/bootstrap init
terraform -chdir=infra/aws/terraform/bootstrap plan -out=bootstrap.tfplan
terraform -chdir=infra/aws/terraform/bootstrap apply bootstrap.tfplan
```

Store the bootstrap state as a protected operator artifact. Record the output
role ARNs, ECR URLs and state bucket. If the account already has the GitHub OIDC
provider, pass its ARN instead of creating a duplicate.

Create the `imperator/pilot/database-app` secret directly in Secrets Manager
with a generated high-entropy password. Terraform receives only the secret ARN.
Confirm that the secret policy and optional KMS key permit only the ECS
execution role and the authorized secret administrators.

## 4. Configure GitHub

Protect an environment named `pilot` with required reviewers and prevent
self-approval where the repository plan permits it. Define these repository or
environment variables; none contains a credential value:

```text
AWS_ACCOUNT_ID
AWS_REGION
AWS_BUILD_ROLE_ARN
AWS_DEPLOY_ROLE_ARN
TERRAFORM_STATE_BUCKET
ALLOWED_HTTPS_EGRESS_CIDRS_JSON
IMPERATOR_DOMAIN_NAME
ROUTE53_ZONE_ID
ACM_CERTIFICATE_ARN
APPLICATION_DATABASE_SECRET_ARN
JWT_ISSUER_URI
JWT_JWK_SET_URI
BEDROCK_MODEL_ID
BEDROCK_INVOKE_RESOURCE_ARNS_JSON
MONTHLY_BUDGET_USD
BUDGET_ALERT_EMAIL
```

`BEDROCK_INVOKE_RESOURCE_ARNS_JSON` is a JSON list of exact ARNs, never `*`.
`ALLOWED_HTTPS_EGRESS_CIDRS_JSON` is a reviewed JSON list for the approved
Keycloak and required AWS API endpoints; `0.0.0.0/0` is rejected. Re-evaluate
provider address changes before every plan.

## 5. Publish And Plan

After D099 is merged and every hosted gate is green:

1. Run **AWS Pilot Image Publish** from `main` with the full commit SHA and the
   exact confirmation string.
2. Download and retain its `image-manifest.json`; verify all four ECR references
   use immutable digests.
3. Run **AWS Pilot Plan or Deploy** in `plan` mode with those four digests.
4. Review the plan, account, Region, IAM changes, resource count and monthly
   estimate. A plan is not deployment authorization.

## 6. Deploy

Run the same workflow in `deploy` mode only after approval. It applies services
at desired count zero, executes the Flyway task, executes the application-role
task, then activates one backend and one frontend task. Either database task
failing leaves the services stopped.

The workflow verifies HTTPS, security headers, liveness and readiness. Complete
the remaining manual checks with a synthetic case and short-lived Keycloak
tokens:

1. positive and negative D095 JWT/RBAC matrix;
2. Evidence import through deterministic Recommendation and ROI;
3. one Bedrock explanation, stable repeated GET and explanation audit metadata;
4. human approval and append-only Ledger evidence;
5. task replacement and persisted data;
6. CloudWatch redaction and CloudTrail delivery;
7. alarm notification confirmation.

## 7. Roll Back

For an application regression, rerun the plan/deploy workflow with the prior
known-good image manifest. ECS uses digest-pinned task definitions and its
deployment circuit breaker. Do not reverse a Flyway migration: migrations are
forward-only, so ship a corrective migration or restore the approved snapshot
only under a separate data-recovery decision.

## 8. Tear Down

Export required synthetic evidence, set services inactive, review a destroy
plan and destroy the `pilot` root. Then verify RDS snapshots, NAT gateway, ALB,
public IPv4 addresses, CloudWatch logs, CloudTrail bucket objects, Secrets
Manager recovery windows and ECR images for residual cost. Keep or remove the
bootstrap state bucket and OIDC roles only through a separate reviewed action;
the state bucket has deletion protection in Terraform.
