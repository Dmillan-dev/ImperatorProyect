# Security Policy

IMPERATOR is a pre-pilot portfolio MVP. Security reports are welcome, but the
project does not currently provide a production support SLA.

## Supported Version

Security fixes are evaluated against the current `main` branch. Historical
commits, generated artifacts and static demonstration files are not supported
release channels.

## Reporting a Vulnerability

Do not disclose a suspected vulnerability in a public issue, pull request,
discussion or social-media post.

1. Use GitHub private vulnerability reporting for this repository when it is
   available.
2. Otherwise, contact the repository owner through
   [the GitHub profile](https://github.com/Dmillan-dev) and request a private
   reporting channel without including exploit details.
3. Include the affected component and commit, reproduction conditions, impact
   and any suggested mitigation in the private report.

Never include real credentials, personal data or customer data in a report.
Test only against systems and data you are authorized to use.

## Response Process

The maintainer will acknowledge the report when possible, validate scope and
severity, record remediation evidence and disclose only after an appropriate
fix or mitigation exists. Response times are best-effort while the project is
pre-pilot.

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
- SHA-pinned GitHub Actions and Docker image inputs.

The Docker runtime is **not certified** while its D092 vulnerability and
external pilot gates remain open. There is no production deployment, public
endpoint, connected external IdP, tenant isolation contract or security SLA.

See the canonical
[Security, Data Governance and Threat Model](docs/architecture/26_Security_Data_Governance_Threat_Model.md)
and the evidence-backed
[portfolio security review](docs/portfolio/technical-evidence.md#security-review).

## Secrets

Never commit passwords, tokens, private keys, cloud credentials, real JWTs or
Docker secret values. Use ignored local secret files or environment-specific
secret management. Revoke and rotate any secret that is accidentally exposed,
then report the incident privately.
