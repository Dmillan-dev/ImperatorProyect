# Exact-image security review — 2026-10-09

**Security: NO-GO. A-S3: BLOCKED. A-S4: PENDING. TFG-A: IN PROGRESS. AWS: BLOCKED.**

The four retained local images were scanned by immutable repository/index digest, with `--image-src docker --platform linux/amd64`. The native Trivy executable was already installed at version 0.75.0; this operation did not install or upgrade it. Historical scans used 0.74.0. A fresh isolated vulnerability database and Java index were downloaded, then pinned for all twelve scan commands. No images were downloaded, rebuilt, retagged or removed and no containers were started.

## Fresh security result

| Component | Fixable High/Critical | Unfixed H/C package records | Secrets, all severities | Fixable gate exit |
| --------- | --------------------: | --------------------------: | ----------------------: | ----------------: |
| migration |                     0 |                           0 |                       0 |                 0 |
| backend   |                     0 |                           0 |                       0 |                 0 |
| frontend  |                     0 |                          49 |                       0 |                 0 |
| postgres  |                     2 |                          66 |                       0 |                 1 |

Secrets scan includes image files and image configuration. The full vulnerability reports retain all severities and unfixed findings; no VEX or advisory suppression was used. The fixable-only H/C control is a separate report. A zero fixable count is not a claim of zero vulnerabilities overall.

The historical **23 distinct residual CVEs / 115 H/C package records remain unchanged** when comparing service, CVE, package, installed/fixed version, status, severity and target type against the October 8 scans. Two additional HIGH findings bring the total to **25 distinct H/C CVEs / 117 records**. The 23 residuals still have no published Bookworm correction in the fresh official Debian dataset. Fixes in other suites are not authorization to migrate Bookworm. The individual [current matrix](current-cve-applicability-matrix.json) preserves each conditional assessment and records current package bindings and vendor status. It does not declare the product inexploitable or accept the risk.

## New blocking findings

Both findings are Go `stdlib@v1.26.6` compiled into PostgreSQL's `usr/local/bin/gosu`, not vulnerabilities in PostgreSQL SQL code.

| CVE            | Official Go advisory                                 | Affected mechanism                            | Minimum same-line fix |
| -------------- | ---------------------------------------------------- | --------------------------------------------- | --------------------- |
| CVE-2026-78667 | [GO-2026-6609](https://pkg.go.dev/vuln/GO-2026-6609) | HTTP Range-header CPU exhaustion              | Go 1.26.9             |
| CVE-2026-97031 | [GO-2026-6607](https://pkg.go.dev/vuln/GO-2026-6607) | TLS malformed ECH-extension memory exhaustion | Go 1.26.9             |

The scanner lists publication times after the dated October 8 scan. The installed scanner version also differs; the comparison records both tool and database identity rather than attributing every result change solely to the database.

Pinned gosu source inspection found no direct HTTP/TLS imports in its reviewed source files. Gosu is a privilege-switch/exec helper; no normal product network entry to these mechanisms was identified. This is a limited source inference, not binary callgraph analysis, a penetration test or proof of non-exploitability. Both findings have fixes and continue to fail the user's strict gate. No suppression was applied.

The [minimum remediation proposal](MINIMUM_REMEDIATION_PROPOSAL.md) is prepared for independent authorization. Nothing in the proposal has been applied.

## Artifact and source correspondence

Integrated `main`: `3aca0389431b077ddee0b20621f6ccac26238e9f`.
Recorded build/test source: `6e79e83e909995c50a44aba3608b6ca25a0e4602`.
Their Git trees match as recorded in the [post-merge audit](../2026-10-09-a-s3-post-merge/A_S3_POST_MERGE_AUDIT.md). The retained images were built on the latter source SHA; do not relabel them as builds of `main`.

All four local OCI indices, linux/amd64 manifests, source labels and rootfs layers match the historical scan/build bindings and the recorded October 8 runtime. Migration corresponds to the `flyway-migrate` runtime record. Config digests are extracted from the original build logs; CLI index and platform IDs are explicitly distinguished from image config digests. [Artifact correspondence](artifact-correspondence.json) supplies the complete binding and post-scan inspection.

| Component | Retained OCI index digest                                                 | linux/amd64 manifest digest                                               |
| --------- | ------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| migration | `sha256:c53c77aeba010ddfa1538aa75a62b20e5d826baba2869cf50b06c86512a2b8b4` | `sha256:a4b1f621b007dca0d3cf099bd4d1e3246bffccddb240ffba7ee0b567ac6f1227` |
| backend   | `sha256:298f4094799ca9c95e0a72ee65c04d19d42950cddcda2c0f5e9810c1bcc44757` | `sha256:645e2aa935ab08a3765f8b62c81b7c210c27867435efa22145b12aaba179a67f` |
| frontend  | `sha256:ec95fa1037974181cc6cb30041d82f4d888a056605c9a405899112c3842a9a78` | `sha256:85decf17bb4f50e1115839c3dbb0dfac31aafaafb567f50212598d08ce5f3daa` |
| postgres  | `sha256:aa6f9e2344c82cddfeef9c2bca2f0145aab30e88b5305b0648c3dc27eddb7fc6` | `sha256:abe13e246dba26ebf75ac3c0b04eb4ed8ce6814b940bac423c5a72bc19fbd2b3` |

**Artifact identity is demonstrated; new runtime execution is not claimed.** The daemon contains 30 images and zero containers. No runtime was started with expired R-20. Main CI generated separate artifacts; equal source does not substitute those artifacts for the retained local ones. Hosted Java CI's 174 unit tests and the dated 35 integration / 41 frontend / Playwright results retain their original dates and scope. No functional suite was rerun in this scan-only operation.

## R-20 decision boundary

Historical acceptance expired exclusively **2026-10-09 00:00 Europe/Madrid (UTC+02:00), equivalent to 2026-10-08 22:00 UTC**. It remains historical and has not been renewed or transferred.

The [current review record](r20-current-review-not-accepted.json) has **`accepted: false` and `approvalReady: false`**. The October 16 00:00 Europe/Madrid date is an unapproved expiry proposal only, not a valid decision or permission to run the MVP.

A future residual decision must identify the exact eligible image digests, retain zero fixable H/C and zero secrets, cover only local synthetic-data TFG-A use, and exclude AWS, production, real data and public exposure. It needs explicit owner approval and has no automatic renewal or transfer to future images. A targeted PostgreSQL rebuild would require new bindings and a fresh decision after scans and regression.

Existing M1–M6 mitigations remain documented in the individual matrix: loopback/internal networks, synthetic data, non-root/capability/read-only controls, restricted input/protocol paths and resource limits. These are configuration assumptions to enforce on an authorized future run. They neither repair affected components nor guarantee availability or absence of exploitation. Static/historical subcomponent absence claims remain scoped to their inspected artifacts; no new filesystem or exploitation test is inferred from package-version equality.

**Residual acceptance cannot substitute for the failed fixable gate. R-20 approval and A-S3 closure are not ready.**

## Independent R-23 disposition

The [fresh official advisory snapshot](braces-advisory-current.json) still publishes no corrected braces version. The [current R-23 review](r23-current-review.json) records installed dev-tooling braces 3.0.3 and no braces package in the exact frontend's fresh scanner inventory. This inventory is not a complete live-filesystem proof of absence of embedded code. Historical production inspection remains historical. Trusted checkout/fixed glob inputs and isolation of untrusted tooling remain documented mitigations. R-23 stays **OPEN / NO FIX AVAILABLE**, independently of R-20; no speculative upgrade or renewed owner disposition was performed.

## Evidence, preservation and next gate

[Scan commands](scan-commands.json), [scanner executable identity](scanner-tool.json), [databases](scanner-databases.json), [summary](scan-summary.json), [comparison](residual-comparison.json), official vendor/advisory snapshots and twelve raw JSON reports accompany this review. Raw scanner outputs are byte-copied; no evidence is reformatted. Database binaries and registry credentials are excluded.

Current documentation is synchronized in an isolated post-merge worktree. The original dirty working copy and index are preserved; historical evidence, receipts, decisions and audit packs are not rewritten. New validation and a self-excluding SHA-256 catalogue record the documentary delta and its limits. This is an uncommitted review package, not an accepted release or new receipt.

Next: authorize the narrowly specified compiler/security patch and necessary rebuild/regression if desired. After zero fixable H/C and zero secrets are re-established, review residual applicability for the resulting exact artifacts and obtain explicit R-20 approval. Only then consider formal A-S3 review. A-S4 still requires the full narrated twelve-minute local defense, memory/traces and tutor review; the 27.44-second silent clip is insufficient.

COMMIT NOT PERFORMED. PUSH NOT PERFORMED. No code, dependencies, image build files, workflows, infrastructure or historic risk decisions changed by this operation.
