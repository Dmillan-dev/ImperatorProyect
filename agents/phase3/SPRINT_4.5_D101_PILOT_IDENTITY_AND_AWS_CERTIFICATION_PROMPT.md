# Sprint 4.5 D101 - Pilot Identity And AWS Certification Prompt

Status: **STAGE A AUTHORIZED / STAGES B AND C BLOCKED**

Authorization basis: D099/D100 repository implementation merge `22a9917`, with
all required hosted Java CI, CodeQL, source/dependency/configuration,
full-history secret, application/migration image, PostgreSQL D094 and AWS IaC
checks passing.

Only Stage A is currently executable. Stop after producing its redacted
identity-conformance evidence. Do not start Stage B or Stage C, authenticate to
AWS, run `terraform plan` or `terraform apply`, publish images, invoke Bedrock,
create cloud resources or deploy the pilot. A later explicit authorization is
required even if Stage A passes.

## Objective

Certify the external Keycloak HTTPS boundary required by D095, produce a
reviewable AWS pilot plan and, only after a separate explicit cost/apply
approval, deploy and validate the synthetic IMPERATOR pilot.

## Mandatory Context

Read before acting:

1. `docs/architecture/46_JWT_Authentication_Contract.md`;
2. `docs/architecture/47_RBAC_Authorization_Contract.md`;
3. `docs/architecture/59_Bedrock_Auditable_Explanation_Contract.md`;
4. `docs/architecture/61_AWS_SAA_Portfolio_Deployment_Contract.md`;
5. `docs/runbooks/aws-pilot-deployment.md`;
6. D095, D099 and D100 in `docs/decisions/14_Decision_Log.md`.

Do not use customer data, static AWS keys, exported bearer tokens, wildcard
Bedrock resources or a mutable container tag.

## Stage A - D095 External Identity

Authorization: **CURRENT / AUTHORIZED**

Against the frozen external Keycloak HTTPS issuer/JWKS:

- verify certificate trust, exact issuer, audience `imperator-api`, RS256 and
  mandatory `kid`;
- prove accepted ADMIN, PLATFORM_ENGINEER, FINANCE and AUDITOR tokens;
- prove rejection of wrong issuer/audience/algorithm, expired/not-yet-valid
  token, unknown key, missing/invalid role and malformed subject;
- prove the D088 route matrix and D083 human-authority rules end to end;
- record only redacted metadata and results, never tokens or private keys.

Stop on any failure. Do not work around D095 in ALB, Spring or the frontend.
After the complete positive and negative matrix passes, stop and present the
redacted Stage A report. Stage A completion does not authorize Stage B.

## Stage B - Costed AWS Plan

Authorization: **BLOCKED / NOT AUTHORIZED**

The following section is retained as future scope only. Do not execute any of
its preparation, AWS authentication, pricing, publication or Terraform steps
under the current authorization.

- prepare the account, Region and resource inventory without changing AWS;
- attach a current AWS Pricing Calculator export and identify bootstrap state,
  ECR, NAT, ALB, RDS, Fargate, public IPv4, logs, secrets, S3 and Bedrock
  assumptions;
- present the proposed bootstrap apply, secret creation and image publication
  as explicit AWS writes with their expected cost and teardown behavior.

**Stop here and request explicit bootstrap/publication authorization.** After
that separate approval only:

- verify the exact AWS account and Region before every Terraform command;
- apply the reviewed bootstrap stack and create the application password secret
  without exposing or reading its value;
- run the image publish workflow from the accepted `main` SHA;
- run the deployment workflow in `plan` mode with the resulting digests;
- inspect IAM, security groups, public exposure, deletion behavior and budget;
- present the plan, expected recurring cost and teardown consequences.

**Stop again and request explicit pilot apply approval.** Architecture,
implementation or bootstrap/publication approval is not pilot deployment
approval.

## Stage C - Authorized Deployment Only

Authorization: **BLOCKED / NOT AUTHORIZED**

After current explicit approval:

1. run the protected deploy workflow with the reviewed digests;
2. require migration and database-role tasks to pass before service activation;
3. verify HTTPS redirect, headers, probes and no direct task/RDS exposure;
4. execute the complete D095 role matrix;
5. execute one synthetic Evidence-to-Recommendation case;
6. generate one Bedrock explanation and verify repeated GET does not reinvoke;
7. complete human review and append-only Ledger verification;
8. replace tasks and prove persistence;
9. inspect CloudWatch/CloudTrail redaction and alarm delivery;
10. roll back to the prior digest manifest and prove recovery;
11. destroy the pilot and inspect every residual billable asset.

## Required Report

For the currently authorized Stage A, report the identity evidence and mark all
Stage B/C fields explicitly as `NOT EXECUTED / NOT AUTHORIZED`; never fabricate
live AWS, Bedrock, cost or deployment evidence.

Separate the report into:

- pre-existing certified IMPERATOR baseline;
- D099/D100 implementation evidence;
- D095 external identity evidence;
- live AWS resources and exact lifetime;
- simulated versus real Bedrock/AWS calls;
- measured cost and residual cost;
- failures, limitations and deferred production hardening;
- Sprint 4.5 and MVP Release recommendation.

Do not mark Sprint 4.5 or MVP Release complete unless every mandatory item has
objective evidence and the operator accepts the closure.
