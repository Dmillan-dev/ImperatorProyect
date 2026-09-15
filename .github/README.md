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

Java CI is implemented. Hosted dependency, secret and configuration scanning
is implemented. D098's pinned `libpcre2` refresh passes local application-image
and D094 PostgreSQL certification, including two-build reproducibility, SBOM,
provenance, image behavior and zero fixable High/Critical or secret findings.
The equivalent hosted jobs remain fail-closed and pending; no finding is
waived and the historical D092-D095 runtime evidence remains valid.

Repository-configured CodeQL default setup is enabled and passing; no
repository-managed CodeQL workflow is stored in this directory. Operational
external Keycloak conformance remains a mandatory pre-Sprint-4.5 gate. Full
frontend CI, release automation and deployment workflows are not implemented
and must not be inferred from this directory. Sprint 4.4 application-native
observability is authorized by D097 but blocked until hosted D098 passes.
Prometheus/Grafana image gates are outside the MVP because those services are
deferred, not because their findings are waived.
