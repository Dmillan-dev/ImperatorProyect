# Current local security candidate — 11 November 2026

Source `e30c16a7b7d5741dc3626fe11f850b6e9366bfbc`, based on main `ec8e30f`. No merge, risk acceptance or MVP completion is implied by this record.

## Minimal changes

- Next and eslint-config-next 16.4.2 → 16.4.5, the official security release. Exactly 12 existing lock entries change version, all in that Next family; no unrelated dependency versions move.
- CVE-2026-109518: update only apt and its matching libapt runtime library, checksum-pinned from official distribution indices: Jammy 2.4.15ubuntu0.1, Noble 2.8.4ubuntu0.1, Bookworm 2.6.2. D094 checks both installed versions and the fixed CVE. The disposable backend builder sets Acquire::Retries=0 for both APT operations, removing the advisory's required retry condition before downloads.
- PostgreSQL 18.6, gosu/Go 1.26.10, Node 24.22.2, Java, Maven, Spring, base image digests, workflows, Terraform and application behavior are unchanged. No AWS operations or historical-image deletion.

Official sources: [Next security release](https://github.com/vercel/next.js/releases/tag/v16.4.5), [APT / Debian](https://security-tracker.debian.org/tracker/CVE-2026-109518), [APT / Ubuntu](https://ubuntu.com/security/CVE-2026-109518). Package checksums/URLs: [apt-packages.json](apt-packages.json).

## Exact artifacts and validation

[candidate-images.json](candidate-images.json) binds source, build command, OCI index, linux/amd64 runtime manifest and rootfs. Final images were built from an immutable git archive. Earlier builds remain diagnostic only outside this final evidence set. Final scans and repeated integration use the final archive-built IDs. [Test input correspondence](TEST_INPUT_CORRESPONDENCE.json) records that the later builder-only mitigation does not change the Java/frontend test inputs; it does not claim binary equality across source commits. CI images at a later documentation HEAD are separate artifacts.

174 unit + 35 PostgreSQL integration + 44 frontend tests PASS. Lint, types, production build PASS. Playwright 9 PASS / 6 intentional skips, with synthetic intercepted API; not a full Docker defence rehearsal. The first browser attempt lacked its required executable; it is excluded from PASS. A local log-name collision prevented the first production build from running; only frontend-production-build.log is accepted for that control. No application changes were made to make those tests pass.

Four final images: **0 fixable H/C and 0 secrets** with the frozen [database metadata](database-metadata.json). Full source scan includes development dependencies and misconfiguration checks, with 0 gate findings. Its POM parser warns that some inherited dependency versions cannot be resolved; compiled JAR/image scanning is a separate control and the filesystem result alone is not a complete Java dependency inventory. History Gitleaks: 0 secrets. Production npm audit: 0 findings. Full npm audit: 7 Moderate affected tooling entries associated with minimatch; no forced overrides or general upgrade. Full raw image reports retain all severities and unfixed findings. [Validation summary](VALIDATION_SUMMARY.json).

The integration database is synthetic, temporary and loopback-only; its container/password were removed. Historical volumes and the dirty original checkout are preserved. Complete current-set RBAC/persistence/recovery rehearsal is still pending. D094 no-cache/rebuild certification is required from publication CI, not inferred from these image scans.

## R-20 remains a separate decision

No current acceptance. Last approved expiry: 19 October 2026 00:00 Europe/Madrid. The November 2 proposal was not approved; its proposed November 9 expiry is not authorization.

Current reports contain **39 unique H/C CVEs / 144 H/C package rows**. The conservative decision set retains all 23 prior identifiers plus 18 new ones: **41 CVEs**. The full inventory is **219 CVEs plus 9 other identifiers**, not 41 total vulnerabilities. [Comparison](CVE_COMPARISON.json), [individual matrix](CVE_REVIEW_MATRIX.json), [new vendor records](new-residual-vendor-review.json).

The 18 additions are Kerberos (14), SQLite (2) and zlib (2). Debian has no Bookworm fix in the reviewed records. PostgreSQL links GSS/Kerberos and zlib, but no KDC/admin executables were found. SQLite is installed without an identified product execution path. These observations limit particular exploit paths; they do not declare the packages or product inexploitable. Trivy's webpack title for CVE-2026-76844 contradicts the vendor's zlib description; both are preserved. CVE-2026-85091 has an affected-range ambiguity in the vendor notes and is conservatively retained.

[R20_REVIEW_REQUIRED.json](R20_REVIEW_REQUIRED.json) stays `accepted: false`. Proposed exclusive expiry: **18 November 2026 00:00 Europe/Madrid (UTC+01:00) = 17 November 2026 23:00 UTC**. Local and synthetic only; no automatic renewal or future-image/CI transfer. Explicit owner review/approval is still required. R-23 remains independent; its historical advisory was withdrawn and no automatic closure is recorded.

## PR disposition and viable MVP route

[PR review](PR_REVIEW.json): replace #88 with this security candidate; close/defer the broad Maven #89 and npm #90 batches. Do not merge any merely because an old check is green. Existing PRs are not automatically closed by this report.

1. Review the minimal replacement diff and its final-HEAD CI; merge only after review and required checks pass.
2. Decide R-20 explicitly for these exact artifacts/current matrix, with a future expiry and the local/synthetic restrictions. A PR merge does not approve R-20.
3. Rehearse the exact Docker set: four roles, DRC-AOA-001, Flyway V1–V3, persistence, readiness and recovery; record digests/results. Close current A-S3 only after formal review.
4. Finish A-S4: update the memory, measure a real twelve-minute English rehearsal, produce a screen recording with human narration, inspect audio/readability/secrets and obtain academic review.

TFG-A remains IN PROGRESS. AWS, production, real data and public exposure remain blocked. The original checkout's 24 tracked modifications and two untracked entries are not part of this candidate.
