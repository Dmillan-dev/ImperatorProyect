# R-17 Local Verification — 2026-10-02

Status: **R-17 CLOSED FOR THE FIVE ORIGINAL IMAGE FINDINGS; TFG-A IN PROGRESS**.
The repeated npm audit has an additional development-only transitive finding;
its bounded patch is awaiting a scope decision. The strict image scan also
keeps R-20 open; the absolute-zero goal is not met. No blanket upgrade or waiver
was applied. See the [security assessment](../../R17_SECURITY_REVIEW.md).

| Gate                                   | Current result                                                                                            |
| -------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| Backend unit / PostgreSQL integration  | 174 / 35 PASS, zero failures/errors/skips                                                                 |
| Flyway and permissions                 | V1-V3 migrate/validate/no-op PASS; append-only privileges and persistence verified                        |
| Frontend                               | 41 tests, format, lint, types, build PASS                                                                 |
| Chrome browser acceptance              | Nine PASS, six existing intentional skips; intercepted API fixtures                                       |
| Real Docker/API governance             | PASS; 401, auditor 403 without write, pre-governance 409, three ordered actors                            |
| Deterministic synthetic result         | EUR 19,440 estimated; EUR 18,960 realized; variance EUR -480                                              |
| Recreation / readiness                 | Three containers replaced without lost facts; DB outage readiness 503, liveness 200, recovery 200         |
| Backend / frontend / PostgreSQL images | Each zero fixable High/Critical and zero detected secrets, Trivy 0.74.0                                   |
| Images including unfixed               | Backend 0; frontend 53 High/4 Critical; PostgreSQL 81 High/15 Critical; R-20 OPEN                         |
| Source Trivy default policy            | Zero High/Critical, secrets and failed High/Critical misconfigurations; excludes development dependencies |
| Source Trivy with include-dev-deps     | Four High brace-expansion package/CVE findings; R-19 OPEN                                                 |
| Gitleaks 8.30.1                        | History and final public-source snapshot: zero detected secrets                                           |
| npm audit                              | FAIL: brace-expansion in two existing development-only transitive paths; separate proposed patch          |

Only the necessary original patch was applied: Jackson 3.1.7, Next 16.3.6
and checksum-pinned libssl3/openssl 3.0.2-0ubuntu1.30. Java and all other OS
packages were compared and remain identical. Spring Boot, Java base digests,
Node, React, PostgreSQL, Terraform/workflows and business code remain unchanged.
`eslint-config-next` remains 16.3.5 and passes lint/type/build verification.

## Durable Evidence

- [Machine-readable index](../TFG_A_R17_2026-10-02.json): baseline Git commit,
  SHA256 runtime/test input fingerprint, commands, dates, versions and hashes.
- [Quality and exact runtime facts](quality-summary.json),
  [Jackson dependency path](jackson-dependency-tree.txt),
  [Next-only lockfile changes](npm-lock-diff.json).
- [Fixable-policy security scan summaries](security-summary.json),
  [complete image High/Critical results including unfixed](all-image-severities.json)
  and [npm audit](npm-audit.json).
- [Actual API/UI workflow](rehearsal.json), [replacement and V3 privileges](persistence.json),
  [readiness recovery](observability.json), [build inputs](docker-source-manifest.json).
- [New technical fallback WebM](tfg-a-docker-demo.webm) and five numbered PNGs:
  [Recommendation](01-deterministic-recommendation.png),
  [approval](02-human-approval.png), [implementation](03-implementation.png),
  [Business Value](04-realized-business-value.png), [Ledger](05-auditor-ledger.png).
- [False-positive hash dispositions](hash-finding-dispositions.json): each of
  20 Gitleaks generic-key alerts was verified as the actual SHA256 of a public
  repository file. Publication metadata now uses explicit path/sha256 records;
  data, input digests and historical execution results are preserved. Scanner
  rules/thresholds are unchanged. The final complete source scan passes.
- [Additional patch proposal](additional-audit-patch-proposal.json): only
  brace-expansion 1.1.18→1.1.21 and 5.0.9→5.0.12; no direct dependency change.
  Its [isolated candidate audit](additional-audit-candidate.json) is zero.
- [Owned-environment cleanup](cleanup.json).

The initial source Trivy attempt hit Maven Central HTTP 429 before producing
a report. The complete rerun mounted the already resolved Maven repository
read-only and passed. That tool failure is not a vulnerability finding. Trivy's
default policy and the full npm development audit have different scopes;
zero image/source policy findings do not erase the npm result.

The runtime is the separate `imperator-r17-20261002` Docker project, loopback
port 3008, with ephemeral RS256/HTTPS JWKS tooling. It proves local behavior,
not external Keycloak conformance. Approval uses the real UI;
implementation/validation use authenticated API commands to link the two
post-action records, followed by unmocked UI reads. No alternate business
logic, extra service or live Bedrock invocation is part of the product.

The silent captioned recording is a short technical fallback, not the full
twelve-minute oral defense. The input fingerprint is a SHA256 of an identified
worktree, not a new Git commit SHA. No commit, publication, hosted check or AWS
execution is claimed. Final accepted-release evidence remains a later delivery
obligation. Normal project volumes and pre-existing output are preserved.
