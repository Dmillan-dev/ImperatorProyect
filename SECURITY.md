# Security Policy

## Current Owner Decision And Sprint — 2026-10-06

**A-S2 technical GO / formal GO for the exact approved local synthetic record. TFG-A remains IN PROGRESS. A-S3 is authorized for evidence/versioning only; A-S4 is later and AWS remains blocked.** R-20 is ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC for the current 23-CVE residual, not declared inexploitable. Exclusive expiry **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal or future-image transfer. R-23 disposition is independently approved while status remains **OPEN / NO FIX AVAILABLE**; it is not closed or included in R-20. Zero fixable High/Critical and zero secrets remain mandatory. No further upgrade is authorized. See the [registered decisions](docs/tfg/evidence/2026-10-06-a-s2-acceptance/README.md), [security gate](docs/tfg/TFG_A_SECURITY_CLOSURE_GATE.md) and [A-S3 work order](docs/tfg/A_S3_EVIDENCE_VERSIONING.md).

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
  application images;
- GitHub-managed CodeQL default analysis for Java/Kotlin and
  JavaScript/TypeScript; and
- the D094 PostgreSQL supply-chain gate with pinned inputs, reproducible builds,
  SBOM, provenance and zero fixable High/Critical or secret findings.

The D094 PostgreSQL image-remediation gate and the complete D092-D095 local
Docker runtime are historical **PASS / CERTIFIED** evidence. D098 pins the
exact fixed Debian `libpcre2-8-0` package and passes local and hosted scans,
reproducibility, provenance, hardening and runtime checks. D093/R16, local JWT/RBAC, the
`DRC-AOA-001` end-to-end flow and persistence after recreation pass. There is
still no production deployment, public endpoint, connected operational IdP,
tenant isolation contract or security SLA. D095 requires external Keycloak
HTTPS conformance before Sprint 4.5, customer data or MVP Release. Passing CI
and local runtime checks does not override that pilot boundary. D096 is frozen
and D097 implements its application-native observability subset while deferring
Prometheus/Grafana services without a vulnerability waiver. D097 and Sprint 4.4
are COMPLETE / HOSTED PASS on merge `4fd18fa`. D099/D100 repository
implementation and required security checks are COMPLETE / HOSTED PASS on merge
`22a9917`. External Keycloak HTTPS conformance under D095 remains mandatory before
Pilot Readiness, non-loopback exposure or release. D101 is OPERATOR-DEFERRED /
BLOCKED_EXTERNAL; current work is TFG-A bounded maintenance and isolated local
evidence. AWS planning, publication, apply and deployment remain blocked.

See the canonical
[Security, Data Governance and Threat Model](docs/architecture/26_Security_Data_Governance_Threat_Model.md)
and the evidence-backed
[portfolio security review](docs/portfolio/technical-evidence.md#security-review).

## Secrets

Never commit passwords, tokens, private keys, cloud credentials, real JWTs,
customer data or Docker secret values. Use ignored local secret files or an
environment-specific secret manager. Revoke and rotate any secret that is
accidentally exposed, then report the incident privately.

## Public Repository Data Boundary

Committed examples and demonstrations use synthetic identifiers and values.
`infra/docker/.env.example` contains placeholders only. Local `.env` files,
Docker secret values, generated runtime evidence, database dumps, logs and
private commercial discovery records are excluded by `.gitignore` and must not
be attached to issues, pull requests or workflow artifacts. Curated sanitized
synthetic reports under `docs/tfg/evidence/` are separately reviewed publication
artifacts; preserve hashes and scan them before versioning. This does not authorize
raw credential/customer/runtime-dump publication.

## Historical Local Security Verification — Before Current Explicit Approval

**A-S2 technical GO; formal security closure PENDING RESIDUAL DISPOSITION.** Only the explicitly authorized source-map-js/sharp and Bookworm PCRE2/Perl patches were applied.174 unit+35 integration+41 frontend, quality, Docker/RBAC/FlywayV3/persistence/readiness/recovery, exact-image scan binding, D094 and scoped cleanup PASS; Playwright9pass/6intentional skips. Trivy source/images zero fixable High/Critical, source configuration zeroHigh/Critical, secrets zero at all severities. Production npm audit0.

Full scans retain frontend48High/1Critical and PostgreSQL63High/3Critical. Full npm5High package aggregates come from one unfixed braces development advisory. **R-23 OPEN / NOT ACCEPTED** independently tracks that residual; braces remains3.0.3. R-21's two authorized corrected advisories and R-22's authorized corrected OS findings are CLOSED. R-17/R-19 original closures remain dated.

See [current review](docs/tfg/R21_R22_SECURITY_REVIEW.md), [gate](docs/tfg/TFG_A_SECURITY_CLOSURE_GATE.md) and [bound evidence](docs/tfg/evidence/2026-10-06-r21-r22/README.md). Current R-20 acceptance is false despite historical approval. A-S3/A-S4/TFG-B/AWS remain blocked; no general update, risk waiver or merge was performed. Hosted release, external identity and institutional requirements remain intact.

## Historical Strict Image Scan Discrepancy â€” Before B Approval

The October2 fixable-only image policy passed. Its scan including unfixed findings
reports backend zero, frontend 53 High/4 Critical and PostgreSQL 81 High/15
Critical in inherited OS packages, with no corrected versions indicated by
this historical Trivy snapshot. Before explicit B approval, R-20 was OPEN alongside R-19;
TFG-A is IN PROGRESS. No suppression, blanket base upgrade or risk acceptance
was added. See [the security assessment](docs/tfg/R17_SECURITY_REVIEW.md).

## R-20 Historical Approval / Current Mandatory Gates

The [October2 explicit B acceptance](docs/tfg/evidence/2026-10-02-r20-acceptance/acceptance.json)
remains an immutable approval record for its reviewed local/synthetic evidence.
Exclusive expiry stays 2026-10-09T00:00:00+02:00 Europe/Madrid
(2026-10-08T22:00:00Z), with no automatic renewal. AWS, production and real
data are excluded. No institutional acceptance or remediation claim is implied.

**Current binding is REVIEW REQUIRED / accepted=false.** Changed runtime/image
and scanner hashes, newly available vendor fixes and other unreviewed findings
trigger the recorded early-invalidation rules. The old accepted=true is not
transferred or used to waive a now-fixable finding. Zero fixable High/Critical
and zero detected secrets remain mandatory. R-19 is independently CLOSED;
R-21/R-22 authorized fixes now pass. A-S2 technical GO is separate from formal
Security Closure, which remains pending current R-20 and R-23 residual dispositions.
TFG-A stays IN PROGRESS; TFG-B/AWS remain unauthorized.
