# Local MVP security candidate — 20 November 2026

Base main `3497859` includes merged PR #91. Technical source `b8bfe9ad388c5fad8300d21cb7495537f83db1b6`. No risk approval or MVP completion is implied by this record.

## Focused remediation

- Alert #41, CVE-2026-118764 / GHSA-g3hv-75wg-m249: TypeScript-ESLint 8.66.0 → 8.73.0 remains within eslint-config-next's existing ^8.56.0 range and explicitly supports minimatch ^11.0.1. Resolve the affected nested copy from 10.2.6 to the first fixed version 11.0.1. Its one required new transitive dependency is @isaacs/nonrecursive-glob-match 1.0.1. Exactly 13 lock entries change; preserve compatible ignore, brace-expansion and semver versions. No direct dependency, package.json or override changes. Minimatch 3.1.5 is outside this advisory's affected range and is retained.
- Node 24.22.2 → 24.22.3, exactly the checksum-pinned #94 Dockerfile change in all three frontend stages, still Bookworm. This is an official security release, not a general stack update. No Java/Spring/Maven/PostgreSQL/Go, workflow, Terraform, feature or architecture changes.

[Minimatch decision](MINIMATCH_REMEDIATION.json), [official advisory](https://github.com/advisories/GHSA-g3hv-75wg-m249), [Node review](NODE_SECURITY_REVIEW.json), [official Node release](https://nodejs.org/en/blog/release/v24.22.3). No alert dismissal was performed; GitHub should recalculate after merge.

## Exact artifacts and evidence

[candidate-images.json](candidate-images.json) binds the new archive-built frontend index/runtime/rootfs to its source. The other three IDs are retained unchanged and rescanned on 20 November. [Input correspondence](RETAINED_INPUT_CORRESPONDENCE.json) checks their relevant source/configuration inputs; it does not pretend they were rebuilt. CI-built images at a later HEAD are separate artifacts, with no automatic R-20 transfer.

174 unit and 44 frontend tests PASS; format/lint/types/build PASS with portable Node 24.22.3. Playwright 9 PASS / 6 intentional skips using intercepted synthetic API, not full Docker E2E. Complete and production npm audits: **0 vulnerabilities**. No installed minimatch/TypeScript-ESLint package was found under /app in the inspected production image; this statement concerns installed packages, not a proof about every bundled byte. The previous 35 PostgreSQL integration tests remain dated 11 November on the exact retained PG ID; they were not rerun today.

Four exact images: **0 fixable H/C and 0 secrets** with the [frozen database cutoff](database-metadata.json). Complete raw reports retain all severities and non-fixable findings. Source scan includes development dependencies, secrets and configuration; its inherited-Maven-version warning is retained, and compiled JAR/image scans are separate coverage. History Gitleaks: 0 secrets. Trivy's OS/npm gate is not a comprehensive audit of Node's native runtime; the official security-release check and exact runtime version are independent evidence. [Validation](VALIDATION_SUMMARY.json).

## Current R-20 review — not accepted

There are **39 unique H/C residual CVEs / 150 H/C package rows**, and a **41-CVE conservative review set**. Equal counts do not mean an unchanged inventory. [Comparison](CVE_COMPARISON.json) and [current individual bindings](CVE_REVIEW_MATRIX.json).

- Added CVE-2025-6020 (PAM). Debian states Bookworm 1.5.2-6+deb12u2's fix was not applied correctly; no Bookworm fix is published in the reviewed record. Local paths/symlinks/races through pam_namespace are required. The exact affected images have no active pam_namespace configuration line and run as UID 1000 / 999. Preserve non-root, read-only, no-new-privileges and dropped capabilities. No PAM/privileged namespace or untrusted shell configuration changes. These are conditional mitigations, not an inexploitable declaration.
- Removed CVE-2026-76844's previous zlib association: Debian now identifies webpack-dev-middleware and marks it NOT-FOR-US. This is vendor metadata correction, not package repair, an upgrade or a local suppression. Original reports remain intact.

[Vendor record provenance](VENDOR_RECORD_PROVENANCE.json), [PAM official record](https://security-tracker.debian.org/tracker/CVE-2025-6020), [corrected official record](https://security-tracker.debian.org/tracker/CVE-2026-76844).

[R20_REVIEW_REQUIRED.json](R20_REVIEW_REQUIRED.json) stays `accepted: false`, explicitly bound to these four IDs. The last approved acceptance expired 19 October. The old proposed 18 November deadline was never an approval. Proposed new exclusive expiry **27 November 2026 00:00 Europe/Madrid (UTC+01:00) = 26 November 23:00 UTC** requires explicit owner approval; no automatic renewal or transfer. Local/synthetic only. R-23 disposition remains independent after its advisory withdrawal.

## PR disposition and viable finish

[PR review](PR_REVIEW.json): do not merge the broad Maven #92 or npm #93 batches for the MVP. #94 is a relevant security patch; this replacement incorporates it exactly, so close #94 as superseded only after the replacement is reviewed and merged. No PR was automatically merged or closed.

1. Review the minimal replacement and its final-HEAD CI, then merge the security patch.
2. Decide R-20 explicitly for this current set and a future bounded expiry; a merge is not risk approval.
3. Rehearse those exact images: four roles, DRC-AOA-001, Flyway V1–V3, persistence/readiness/recovery. Freeze the source/build/scan/runtime correspondence and close A-S3 formally.
4. Finish A-S4: update the academic memory/security addendum, measure a real twelve-minute English human rehearsal, record screen plus narration, inspect audio/readability/secrets and obtain academic review. Memory/script practice on historical captures can proceed while the new live runtime gate is pending; date it honestly.

TFG-A remains IN PROGRESS. AWS, production, real data and public exposure remain blocked. The original dirty checkout is preserved. Historical evidence/receipts and the memory PDF are not rewritten; current-control summaries distinguish them from the new candidate.
