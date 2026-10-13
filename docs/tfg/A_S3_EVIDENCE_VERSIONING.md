# A-S3 — Local Evidence And Versioning

## Post-merge verification - 2026-10-13

PR64 is merged into main `0797671e06baa2b20f8ce5a842ac04140d84e591`; its tree equals the tested a931601 candidate. Fresh main CI:10/10 checks PASS. The four retained local image IDs/origins/rootfs match the runtime evidence dated 09/10; CI-built images remain separately identified.

Fresh full four-image scans:0 fixable High/Critical and0 secrets at all severities. Residuals now total25 unique CVEs/119 package rows, including two added GnuTLS CVEs. R-20's historical authorization expired 09/10; the23-CVE accepted:false proposal does not automatically cover this delta. R-23 remains OPEN independently; production npm audit 0 and full development audit 5 High. No upgrade or new functional runtime is claimed.

**A-S3 formal: PENDING applicable explicit risk decision and publication review; A-S4: PENDING; TFG-A: IN PROGRESS; AWS: BLOCKED.** The current record supersedes dated state claims below while preserving historical evidence and receipts. See [post-merge verification](evidence/2026-10-13-postmerge-certification/README.md).

**AUTHORIZED / ACTIVE. A-S2 formal GO is registered; TFG-A IN PROGRESS. No implementation or dependency update. A-S4 later; AWS blocked.**

## Version And Approval Boundaries

The approved runtime content fingerprint is `cbc6535ee56bfa991fb96551a90cda7dc35afd2204056f561900614ab0a5d0b7`, on source baseline `b602bbe1dde92a902c17272d59cd9f0c10b9efe5` plus the indexed local changes. Exact OCI index/platform/config/layer bindings are in [R20 acceptance](evidence/2026-10-06-a-s2-acceptance/r20-current-acceptance.json). Do not use mutable tags or a source commit as proof of a newly rebuilt image.

The prior local review candidate `8380e8d6e70ca4c858604ce4fcc0e0198fa162e1` freezes existing source and evidence in an isolated checkout.
The 2026-10-07 documentation review prepares a descendant candidate and a separate receipt; neither is owner-accepted automatically. Its candidate commit records source identity; it does not become an owner-accepted release SHA, a new image acceptance or a hosted CI pass automatically. The original worktree/index/branch stays unchanged apart from authorized documentation. Candidate images must not be rebuilt/deployed by inference; rebuilt digests invalidate current binding until reviewed.

## A-S3 Checklist

| Work                                                          | State / evidence                                                                               |
| ------------------------------------------------------------- | ---------------------------------------------------------------------------------------------- |
| Register exact R20 approval and separate OPEN R23 disposition | DONE — immutable approval pack                                                                 |
| Preserve old approval/proposals/38 reviewed versions          | DONE — hash-bound snapshots; no historical overwrite                                           |
| Freeze current image identity and runtime inputs              | DONE — same 3 indices/platforms/configs/rootfs and 383 source inputs                           |
| Synchronize README, risks, DoD, evidence and traceability     | DONE in current documentation; scoped diff indexed                                             |
| Audit project/image cleanup                                   | DONE — duplicate caches removed with identical-DB proof; image identities classified/preserved |
| Prepare isolated source version and PR description            | Prior candidate8380e8d prepared; documentation descendant receipt records actual result        |
| Associate required CI                                         | Workflow sources and expected checks identified; new hosted execution PENDING                  |
| Accepted release SHA / publish / merge                        | PENDING review and actual integration; not supplied by baseline or runtime fingerprint         |
| Final narrated local defense                                  | A-S4 later; existing silent technical fallback is not that defense                             |

## Claims For The Local MVP

Implemented and locally validated: Evidence → Decision → deterministic Recommendation → Human Review → append-only Ledger → Result Validation → Business Value; four roles; DRC-AOA-001; PostgreSQL/Flyway V3; Docker; RBAC; persistence/recovery. Regression174/35/41 and Playwright9/6 intentional skips are prior exact-image evidence, not rerun in A-S3.

Temporarily accepted: exact 23-CVE OS residual only, local synthetic until exclusive2026-10-09T00:00:00+02:00, no renewal/transfer. Independently documented open risk: braces development tooling with approved disposition. Full audit/image findings remain visible. No global safety or absolute-zero statement.

Implemented/tested offline, live execution unproved: AI explanation provider. Not validated here: live Bedrock, external IdP/D101, AWS deployment/cost/rollback/teardown, new hosted CI and institutional academic acceptance. Tutor requirements may make AWS mandatory; no such decision is inferred.

## CI And PR Preparation

See the [local PR draft](evidence/2026-10-06-a-s3/PR_DRAFT.md) and [CI association](evidence/2026-10-06-a-s3/ci-association.json). Java verification, source security, application-image scan and PostgreSQL D094 must be evaluated against the eventual submitted SHA; historical badges/screenshots and local PASS do not prove those hosted checks passed. Full residual report remains separate from the enforced zero-fixable gate. CI rebuilt images are not automatically covered by current R20 approval.

No remote push, PR publication, merge, branch-rule waiver or PR57–59 update is part of this local preparation. The expiry/early-invalidation check is mandatory before any later defense rehearsal. A-S3 remains active until source/review/CI requirements are resolved; A-S4 and TFG-A completion are not automatically executed.

## 2026-10-07 Review / Defense Preparation

The [dated review](evidence/2026-10-07-a-s3-a-s4/README.md) records documentation contradictions before synchronization,
source/diff review, exact binding and the local documentation-candidate receipt.
A-S4 [materials](A_S4_LOCAL_DEFENSE.md) can be prepared independently while A-S3 acceptance/integration/CI is pending.
The final runtime/defense remains pending; Docker daemon is currently stopped.
No original/historical approval, image, source dependency or required gate is changed.
