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
  configuration and application-image security workflow.
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

Java CI is implemented. Automated dependency/configuration and
application-image scanning are configured and pass locally; their first hosted
workflow run remains pending. Full frontend CI, SAST, release automation and
deployment workflows are not implemented and must not be inferred from this
directory. Infrastructure-image certification remains governed separately by
D092.

