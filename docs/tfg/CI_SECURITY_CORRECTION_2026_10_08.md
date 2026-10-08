# TFG-A: CI security correction — 2026-10-08

## Problem and scope

PR #60 and #61 both publish 69e4b90. Their documentation commit did not include
the local R-17/R-19/R-21/R-22 security patches or three V3 test cleanup changes.
The actual CI therefore scanned the old dependencies and OS packages. Creating
a second branch at the same SHA did not repair that source tree.

The owner's subsequent instruction authorizes fixing the identified security
issues and preparing one corrective PR. It does not authorize general upgrades,
AWS deployment, merging Dependabot #57–59 or closing the TFG gates automatically.

## Integrated changes

- Jackson BOM 3.1.6 → 3.1.7; checksum-pinned Jammy OpenSSL patch .29 → .30.
- Next 16.3.5 → 16.3.8 and its required internal packages; sharp 0.35.5,
  source-map-js 1.2.2 and the previously approved brace-expansion fixes.
- Bookworm PCRE2 deb12u2 and the four PostgreSQL Perl packages deb12u4;
  supply-chain lock, verifier and frontend CI version assertion aligned.
- Migration image: Noble OpenSSL .15 → .16, Jackson 2.22.3 / 3.1.7,
  checksum-pinned artifacts and removal of unused AWS Secrets Manager/Couchbase
  driver directories containing affected shaded Jackson copies. Flyway 13.7.0,
  its base digest and PostgreSQL JDBC 42.7.12 are retained. The four downloaded
  Jackson JARs are explicitly mode 0644 so user 10001 can load them; the first
  real migration reproduced a 0600 remote-ADD permission failure before this fix.
- Local Compose uses this existing patched migration image for migrate/validate
  instead of an independently pinned Flyway 13.0 image. This makes the local
  migration runtime and the image scanned by CI the same artifact. No AWS is used.
- Three PostgreSQL integration tests truncate the three V3 explanation tables
  in their existing cleanup, fixing reproduced foreign-key errors.
- Generated Next metadata is excluded from formatting/generated-file tracking;
  the missing local Docker capture script is included.

No Java/Spring/Node/PostgreSQL version or pinned application/database base digest
change, new package family, business behavior or architecture change is included.

## Local checks before publication

174 unit + 35 PostgreSQL integration tests and 41 frontend tests pass.
Lint, typecheck, format and production build pass. Playwright: 9 pass, 6
intentional skips. npm ci validates the lockfile. Production npm audit is zero;
full npm audit retains five affected-package entries propagated by the unfixed
braces advisory. These entries remain visible and are not waived as production findings.

Pre-commit backend/frontend/migration images each have zero fixable High/Critical
and zero secrets with the downloaded Trivy databases. These are preparation
checks, not a claim that hosted CI or exact-commit certification has completed.
Final SHA, exact image manifests, runtime checks, D094 reproducibility and the
hosted check links belong to the corrective PR and its new certification record.
No original evidence or receipt is regenerated to manufacture that result.

## Unpublished-item inventory and cleanup

All pending tracked files were inspected. The original semantic security/test
changes are included, together with the scoped additional fixes. Other apparent
modified files have identical Git content or differ only by checkout line endings;
they do not require new source edits. The generated next-env.d.ts is not accepted
as a handwritten change. Next-generated AGENTS.md / CLAUDE.md are ignored locally.
No broad cache/image prune, volume removal or deletion of user work is performed.

The untracked output/tfg/IMPERATOR_TFG_MVP_MASTER_PLAN.md is a 2026-09-28 draft
referencing another checkout and an AWS/Keycloak-dependent MVP. It conflicts with
the later approved local-only defense sequence. Its bytes are preserved locally;
it is deliberately not published as the current plan. Use docs/tfg/README.md,
A_S3_EVIDENCE_VERSIONING.md, A_S4_LOCAL_DEFENSE.md and the release gate as the
current work order. This discrepancy is reported, not silently rewritten.

## Remaining decisions

R-20 is a historical, temporary acceptance for exact approved digests, local
synthetic data only. The 23 residual CVEs were not declared inexploitable.
Its exclusive expiry is 2026-10-09T00:00:00+02:00 Europe/Madrid
(2026-10-08T22:00:00Z); no automatic renewal or transfer to these new images.
R-23 stays OPEN / NO FIX AVAILABLE; braces is not speculatively upgraded.

The corrective PR needs hosted CI and review. New image digests need an explicit
R-20 applicability/acceptance decision before they can serve as accepted release
artifacts. A-S3 and the twelve-minute A-S4 human defense remain pending. Neither
this patch nor green technical scans authorize AWS, production or real data.

After the replacement PR is available, #60 and #61 can be closed as superseded,
without merging them or deleting local changes. Dependabot #57–59 remain separate.

## Primary diagnosis sources

- [Failing original CI run](https://github.com/Dmillan-dev/ImperatorProyect/actions/runs/37758993254)
- [Next SSRF advisory and minimum fixed version](https://github.com/advisories/GHSA-cjq9-62q9-8jv4)
- [Sharp advisory](https://github.com/advisories/GHSA-wq5f-xc86-pv6w)
- [OpenSSL Ubuntu security status](https://ubuntu.com/security/CVE-2026-84782)
- [Unfixed braces advisory](https://github.com/advisories/GHSA-vfj7-8cjw-p6xm)
