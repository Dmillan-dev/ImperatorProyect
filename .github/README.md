# .github

## Purpose

GitHub configuration for repository automation.

## Who Uses This Folder

- Quality Agent during CI and verification sprints.
- Implementation Agent only when a sprint authorizes CI/CD changes.
- GitHub Actions runtime.

## Contains

- `workflows/java-ci.yml`, the implemented Java 21 and Maven verification
  workflow.
- `workflows/security.yml`, the fail-closed Trivy dependency, secret,
  configuration and application-image workflow plus the reproducible D094
  PostgreSQL supply-chain gate.
- `dependabot.yml`, the weekly Maven, npm, GitHub Actions and Docker update
  policy.
- `CODEOWNERS`, defining review ownership for the repository and high-impact
  architecture and security boundaries.
- `PULL_REQUEST_TEMPLATE.md`, enforcing scoped, evidence-backed changes.
- `ISSUE_TEMPLATE/bug_report.yml`, collecting reproducible, sanitized defect
  reports and routing security reports away from public issues.
- Future repository automation metadata only when a delivery gate authorizes
  it.

## Never Contains

- Application source code.
- Business logic.
- Secrets or credentials.
- Manual sprint evidence.
- Product or architecture decisions.

## Current Automation Boundary

Java CI is implemented. Hosted dependency, secret, configuration and
application-image scanning is implemented and passing. The hosted D094 gate
also verifies pinned PostgreSQL build inputs, two-build reproducibility, SBOM,
provenance, image behavior and zero fixable High/Critical or secret findings.
Together with the separately executed D093/R16, local JWT/RBAC, end-to-end and
persistence/recreation evidence, this supports the certified D092-D095 local
runtime.

Repository-configured CodeQL default setup is enabled and passing; no
repository-managed CodeQL workflow is stored in this directory. Operational
external Keycloak conformance remains a mandatory pre-Sprint-4.5 gate. Full
frontend CI, release automation and deployment workflows are not implemented
and must not be inferred from this directory. D096 authorizes no workflow
change before Sprint 4.4 implementation receives separate approval.
