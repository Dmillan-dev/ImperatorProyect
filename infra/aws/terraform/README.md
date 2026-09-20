# IMPERATOR AWS Terraform

Status: **OFFLINE VALIDATED / NO AWS APPLY EXECUTED**

## Roots

- `bootstrap/`: protected S3 state, immutable ECR repositories and GitHub OIDC
  build/deploy roles. Apply once with an approved bootstrap identity.
- `pilot/`: the billable runtime. Its backend is configured from the bootstrap
  output and its services default to zero tasks.

Both roots require Terraform `1.16.3` and `hashicorp/aws` `6.65.0`. Commit their
lock files. Local `.terraform/`, state, plans and real `.tfvars` remain ignored.

## Offline Gate

```powershell
terraform fmt -check -recursive infra/aws/terraform
terraform -chdir=infra/aws/terraform/bootstrap init -backend=false -lockfile=readonly
terraform -chdir=infra/aws/terraform/bootstrap validate
terraform -chdir=infra/aws/terraform/bootstrap test
terraform -chdir=infra/aws/terraform/pilot init -backend=false -lockfile=readonly
terraform -chdir=infra/aws/terraform/pilot validate
terraform -chdir=infra/aws/terraform/pilot test
```

The tests use Terraform mock providers. A pass proves configuration graph and
provider-schema validity, not AWS permissions, quotas, availability or cost.

## Apply Boundary

Do not run `plan` with live credentials until D095 passes and the account,
Region, DNS, ACM certificate, Bedrock resources, Pricing Calculator estimate
and budget are approved. Do not apply merely because a plan succeeds. Follow
[`docs/runbooks/aws-pilot-deployment.md`](../../../docs/runbooks/aws-pilot-deployment.md).
