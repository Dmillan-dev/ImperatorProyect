# AWS Deployment Boundary

Status: **IMPLEMENTED OFFLINE / NO RESOURCES CREATED**

The AWS pilot topology, identity prerequisites, least-privilege roles, cost
gate, rollback and teardown contract are defined in Documents
[60](../../docs/architecture/60_AWS_MVP_Deployment_Preparation.md) and
[61](../../docs/architecture/61_AWS_SAA_Portfolio_Deployment_Contract.md).

`terraform/bootstrap` prepares protected state, immutable image repositories
and GitHub OIDC roles. `terraform/pilot` prepares the billable runtime but keeps
services at zero tasks until one-shot migration and permission jobs pass.
`images/migration` packages the existing Flyway migrations for ECS.

Terraform `1.16.3` formatting, provider validation and mocked plans pass
offline. No command executed during D099/D100 authenticated to AWS, created a
resource, pushed an image or invoked a model. Follow the
[deployment runbook](../../docs/runbooks/aws-pilot-deployment.md); D095, a
current cost estimate and explicit apply approval remain mandatory.
