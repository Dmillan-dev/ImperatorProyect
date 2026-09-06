# Security Policy

IMPERATOR is a pre-pilot portfolio MVP. Security reports are welcome, but the
project does not currently provide a production support SLA.

## Supported Version

Security fixes are evaluated against the latest commit on `main`. Historical
commits, generated artifacts, local environments, downstream forks and static
demonstration files are not supported release channels.

## Reporting A Vulnerability

Do not disclose a suspected vulnerability in a public issue, pull request,
discussion or social-media post.

1. Use GitHub Private Vulnerability Reporting when it is available for this
   repository.
2. If that channel is unavailable, email
   `dmillan.evidence262@slmails.com` without including secrets or personal data.
3. Include the affected component and commit, reproduction conditions,
   expected and observed impact, and any suggested mitigation.

Test only systems and data you own or are authorized to use. Do not degrade
services, access third-party data or perform denial-of-service testing.

## Response Process

The maintainer will acknowledge and assess reports on a best-effort basis,
record remediation evidence, and coordinate disclosure after an appropriate
fix or mitigation exists. No response-time commitment applies while the
project remains pre-pilot.

## Current Security Boundary

Implemented controls include:

- RS256 JWT validation with explicit issuer, JWKS, audience, time, `kid`,
  subject and role checks;
- four-role RBAC without hierarchy, followed by Application-level business
  authorization;
- sensitivity-aware Evidence responses, including fail-closed handling;
- read-only, bounded GitHub and AWS connector surfaces;
- append-only Ledger behavior and database integrity constraints;
- non-root application containers, read-only filesystems, dropped Linux
  capabilities and file-backed local secrets;
- SHA-pinned GitHub Actions and Docker image inputs;
- fail-closed hosted Trivy checks for dependencies, secrets, configuration and
  application images; and
- the D094 PostgreSQL supply-chain gate with pinned inputs, reproducible builds,
  SBOM, provenance and zero fixable High/Critical or secret findings.

The D094 PostgreSQL image-remediation gate is **PASS**. The complete Docker
runtime is still **not certified** while D093/R16, external JWT/RBAC, the
`DRC-AOA-001` end-to-end flow and persistence-after-recreation gates remain
open. There is no production deployment, public endpoint, connected external
IdP, tenant isolation contract or security SLA. Passing CI security checks does
not override this release boundary.

See the canonical
[Security, Data Governance and Threat Model](docs/architecture/26_Security_Data_Governance_Threat_Model.md)
and the evidence-backed
[portfolio security review](docs/portfolio/technical-evidence.md#security-review).

## Secrets

Never commit passwords, tokens, private keys, cloud credentials, real JWTs,
customer data or Docker secret values. Use ignored local secret files or an
environment-specific secret manager. Revoke and rotate any secret that is
accidentally exposed, then report the incident privately.
