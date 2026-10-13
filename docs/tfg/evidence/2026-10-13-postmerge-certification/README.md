# IMPERATOR post-merge verification - 13/10/2026

Source main: `0797671e06baa2b20f8ce5a842ac04140d84e591`. PR64 merged; 10/10 fresh main checks PASS. Published evidence branch starts at that exact commit and changes documentation/evidence only. This record is not owner risk acceptance or formal A-S3 closure.

## Exact retained images, fresh full scans

| Component | Fixable H/C | Non-fixable H/C package rows | Secrets (all severities) |
|---|---:|---:|---:|
| backend | 0 | 0 | 0 |
| frontend | 0 | 51 | 0 |
| migration | 0 | 0 | 0 |
| postgres | 0 | 68 | 0 |

Total residual: 25 unique CVEs / 119 package rows, compared with 23 / 115. See [delta and applicability limits](RESIDUAL_REVIEW.md), [structured delta](residual-comparison.json), full reports and database metadata. Existing local scanner image 0.74.0 was used without pulling or changing any product image. Source Gitleaks: 0; production npm audit: 0; full audit: 5 High development findings, exit 1 retained. No dependency was updated; lockfile bytes are unchanged.

## Source, build, scan and runtime correspondence

Main's tree equals a931601 exactly. Backend/frontend/migration sources and build inputs equal6e79e83; their images retain that build origin. Local PostgreSQL is the a931601 repeat index. All four local IDs and rootfs match the exact runtime evidence dated 09/10. Fresh scans here reference those same immutable IDs.

The [integration record](integration/VERSION_RECORD.md) distinguishes the newly built CI PostgreSQL index/manifest from the retained local index. CI's two builds reproduce their runtime manifest; their SBOM matches153 components but their digest is different from the local image. Neither CI nor a new documentation commit replaces the historical runtime or transfers approval.

174 unit+35 integration+41 frontend, RBAC/Flyway/persistence/recovery and Playwright 9 PASS / 6 intentional skips remain the bound regression from 09/10. No new functional runtime was executed under the expired R-20 decision.

## Publication and workspace review

238 historical evidence files from the two canonical 09/10 security/regression packs are copied byte-for-byte, as catalogued in historical-publication-byte-proof.json. Existing approved receipts are not modified. The old root directory is retained: it includes stale Next 16.3.6 / Go 1.26.6 and old Flyway input differences relative to main; staging it wholesale would regress merged patches. Publication therefore uses an isolated branch from main and an explicit path list.

Docker cleanup is restricted to audited obsolete tfg-a intermediate images. Defense artifacts, secondary PostgreSQL provenance images, bases/tools, containers, volumes and caches are protected. Cleanup completed: 15 obsolete intermediate IDs deleted; user-visible index IDs 28 -> 13. All six protected IDs and every non-candidate image remain present. The 17 volumes, stopped builder container and caches remain intact. No force or prune; no physical-space reclamation claim is made. See docker-cleanup-verification.json.

## Gates

R-20: no current acceptance registered; historical expiry09/10; the prior23-CVE proposal does not cover the current25-CVE residual. R-23: OPEN/NO FIX AVAILABLE disposition remains independent. A-S3: PENDING formal decision/publication review. A-S4: PENDING. TFG-A: IN PROGRESS. AWS: BLOCKED.

Next: review both added GnuTLS findings and approve an exact-digest local/synthetic residual decision with an explicit expiry; then complete formal A-S3 review and academic A-S4 materials. No open upgrade chain is started.
