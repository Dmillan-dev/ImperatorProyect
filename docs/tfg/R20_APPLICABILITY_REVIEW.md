# R-20 Applicability Review And Independent R-23 Decision — 2026-10-06

## Current Owner Decision And Sprint — 2026-10-06

**A-S2 technical GO / formal GO for the exact approved local synthetic record. TFG-A remains IN PROGRESS. A-S3 is authorized for evidence/versioning only; A-S4 is later and AWS remains blocked.** R-20 is ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC for the current 23-CVE residual, not declared inexploitable. Exclusive expiry **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal or future-image transfer. R-23 disposition is independently approved while status remains **OPEN / NO FIX AVAILABLE**; it is not closed or included in R-20. Zero fixable High/Critical and zero secrets remain mandatory. No further upgrade is authorized. See the [registered decisions](evidence/2026-10-06-a-s2-acceptance/README.md), [security gate](TFG_A_SECURITY_CLOSURE_GATE.md) and [A-S3 work order](A_S3_EVIDENCE_VERSIONING.md).

**Review complete and current-version R20 acceptance explicitly registered. A-S2 technical/formal GO; R23 remains independently OPEN / NO FIX AVAILABLE with approved disposition. A-S3 evidence/versioning authorized; TFG-A IN PROGRESS.**

This is the authorized technical stop: evidence inspection and decision preparation only. The review changed no dependency, base image, runtime code, architecture or vulnerability filter; its subsequent owner acceptance is registered separately. Historical artifacts remain byte-identical. The source of truth is the repository and the inspected frozen images, not prior narrative assumptions.

## Measured Residual And Applicability

The [23-CVE matrix](evidence/2026-10-06-r20-applicability/CVE_APPLICABILITY_MATRIX.md) classifies all115 package/CVE scanner rows from the exact R21/R22 images. Backend0, frontend48High/1Critical, PostgreSQL63High/3Critical. Counts are scanner severities, distinct CVE identities and package rows; they are not115 independent attack paths.

| Distinct image CVEs | Classification                                    | Meaning                                                                                                                                              |
| ------------------: | ------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
|                   3 | Affected optional subcomponent not present        | MiniZip, systemd-homed and Python/libxml2 SAX binding; source packages/shared libraries can still be installed.                                      |
|                   1 | Required protocol not used                        | OpenSSL DTLS issue; PostgreSQL TCP/TLS and the reviewed services do not enable DTLS.                                                                 |
|                  19 | Conditional residual; no product entry identified | Vulnerable tools/libraries are present. Advisory prerequisites, execution evidence, current limits and unresolved reachability are recorded per CVE. |

These classifications do not hide, waive or erase findings and are not a VEX suppression. PostgreSQL directly loads libxml2, OpenSSL and OpenLDAP. Its SQL/XML functions mean native parsing cannot be called universally unreachable. No business XML/raw SQL route was identified; prepared statements and fixed sort mappings reduce an identified entry. SQL injection, stolen credentials or an operator invoking other installed tools remain residual scenarios. Specific libxml2 functions were not exhaustively traced or attacked. [PostgreSQL SQL/XML documentation](https://www.postgresql.org/docs/18/functions-xml.html).

Canonical mitigations are verified separately from stricter offline inspection flags: loopback frontend, internal/unpublished PostgreSQL, non-root users, read-only root, no-new-privileges, no SYS_ADMIN and bounded resources. PostgreSQL retains five allowlisted capabilities, writable data/tmpfs and SUID files. Its entrypoint can gunzip initialization files; the inspected init directory is empty and Compose/Flyway provides no untrusted archive path. These are scoped controls, not memory-corruption fixes.

## Why No Further Upgrade Is Recommended In This Task

The fresh [Debian vendor selection](evidence/2026-10-06-r20-applicability/vendor-status.json) lists all23 reviewed Bookworm source records open without a fixed Bookworm version. Source-level vendor status does not imply every binary is affected: the [MiniZip note](https://security-tracker.debian.org/tracker/CVE-2023-45853) explains that Bookworm's zlib does not build the affected optional writer.

Upstream/new-suite fixes exist for several items; no isolated, tested, authorized same-suite intervention covering the remaining set is demonstrated. ACL's fix adds a [new ABI](https://security-tracker.debian.org/tracker/CVE-2026-54369); the [OpenSSL DTLS advisory](https://openssl-library.org/news/secadv/20260929.txt) lists a3.0.23 premium-support fix, while Debian's public Bookworm candidates remain affected. Switching suite/base or rebuilding upstream libraries requires a separate decision/regression and is outside this technical stop. Do not update the whole stack, invent a braces fix, remove packages indiscriminately or open R24/R25 by inertia. Reassess a concrete vendor fix when it becomes available.

## Historical Comparison And Resolved Discrepancy

The immutable [October2 approval](evidence/2026-10-02-r20-acceptance/acceptance.json) covers its original exact evidence; it does not authorize current images. The [comparison](evidence/2026-10-06-r20-applicability/historical-comparison.json) shows32→23 distinct CVEs and153→115 package rows, no new distinct image CVE. Eight removed identities were repaired by the authorized R22 patch; one libxml2 identity disappeared through vendor classification, not a code fix. Five surviving Archive::Tar bindings changed Perl u3→u4; this CVE remains unfixed.

One historical absence claim was too broad: the original Perl probe required `IO::Compress`, not `IO::Compress::Gzip`. Correct read-only probes against the retained historical canonical PostgreSQL payload and current payload find Gzip2.106 and Archive::Tar2.40 in both; the umbrella module lookup fails in both. This resolves a probe-name discrepancy, not a newly introduced Gzip dependency. Old logs/approval are preserved, and the corrected observation is recorded only in this new review. The repaired IO::Compress CVEs are not among the current23.

Source/Compose inspection identifies no new product entry or exposure boundary from R21/R22. The smaller residual is conditionally equivalent or narrower for this local synthetic scope, with native SQL/XML/operator uncertainties expressly retained. This supports offering a fresh bounded decision; it does not prove universal non-exploitability or transfer acceptance across changed OCI, lockfile/runtime, regression and database hashes.

## R-23 Remains Independent

`braces3.0.3` is a transitive development dependency. Full npm audit reports5High affected-package aggregates from one advisory, [GHSA-vfj7-8cjw-p6xm / CVE-2026-93687](https://github.com/advisories/GHSA-vfj7-8cjw-p6xm), with no published patched version. Deep attacker-controlled glob patterns can exhaust recursive AST traversal.

Production absence is supported by exact standalone-image package inventory, failed braces/micromatch/fast-glob/ESLint resolutions, all-app JS/JSON token review and production audit0. All three lexical-hit files were resolved: Picomatch parser counters, Next comments and a React diagnostic. Picomatch is a [separate dependency-free matcher](https://github.com/micromatch/picomatch), not proof that the braces package is bundled. The result is scoped to this traced image, not every possible unlabelled code fragment. See the [bundle review](evidence/2026-10-06-r20-applicability/frontend-bundle-review.json).

The code remains present in developer tooling. Current local patterns are repository-defined; an untrusted checkout/PR or modified config can change them. `dev:true` does not make developer/CI denial of service harmless. No new pattern-depth limit, sandbox or hosted frontend gate is implemented. Proposed disposable-tooling conditions are labelled as proposals, not existing enforced controls.

Recommended status: **OPEN / NO FIX AVAILABLE**, with bounded tooling mitigation documented and an independent explicit owner disposition. It is not CLOSED; the independent disposition has now been explicitly approved. The original [R23 proposal](evidence/2026-10-06-r20-applicability/r23-disposition-proposal.json) retains its historical false/unapproved fields; the new independent disposition records owner approval without closing it. OS-only R20 approval cannot decide it.

## Explicit Current Owner Dispositions

The [current R20 acceptance](evidence/2026-10-06-a-s2-acceptance/r20-current-acceptance.json) binds the original proposal and exact current image/matrix/runtime/scanner evidence. The [independent R23 disposition](evidence/2026-10-06-a-s2-acceptance/r23-approved-open-disposition.json) is approved while the risk remains OPEN / NO FIX AVAILABLE, not closed or included in R20. Original proposal files retain false approval fields as historical preparation artifacts; current decisions are new immutable records.

Scope is exclusively local loopback TFG-A with synthetic data. AWS, production, real data and public/pilot exposure remain excluded. Exclusive expiry is unchanged: **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal, extension or future-image transfer. Zero fixable H/C, zero secrets and early-invalidation triggers remain mandatory. Owner approval is project-local, not tutor/institutional certification. A-S2 formal GO does not complete TFG-A; A-S3 evidence/versioning is separately authorized by the same message, with no upgrades or implementation changes.

## Verification Boundary

The [new review pack](evidence/2026-10-06-r20-applicability/README.md) adds read-only offline image inspection and fresh official vendor checks. Prior exact-image regression174unit/35integration/41frontend, Playwright9pass/6intentional skips, RBAC/Flyway/persistence/readiness/recovery and image scans remain the bound runtime evidence; they were not rerun or relabelled as fresh tests in this documentation-only review. Runtime/test inputs, original artifact bytes and all digests are rechecked. New documentation receives formatting/link/diff, source-secret and evidence/hash checks. Zero fixable H/C and zero secrets remain the technical gates; full residual reports remain visible.
