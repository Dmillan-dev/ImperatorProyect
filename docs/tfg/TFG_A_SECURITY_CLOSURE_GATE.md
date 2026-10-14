# TFG-A Security Closure Gate — Explicit Owner Decision 2026-10-06

## Estado vigente del 14 de octubre de 2026

**R-20 ACCEPTED TEMPORARILY; A-S3 COMPLETE local académico; A-S4 IN PROGRESS; TFG-A IN PROGRESS; AWS BLOCKED.** Aceptación explícita para los cuatro índices/manifiestos locales y el conjunto conservador de 25 CVE del 13/10, exclusivamente datos sintéticos, hasta 16/10/2026 00:00 Europe/Madrid; sin renovación ni transferencia. Escaneo independiente 14/10:0H/C corregibles y 0 secretos, 23 CVE H/C más dos GnuTLS conocidas ahora LOW y sin fix. R-23 OPEN independiente. [Acta y evidencia](evidence/2026-10-14-r20-acceptance/A_S3_CLOSURE_RECORD.md). Las secciones anteriores fechadas se conservan como historia y no sustituyen esta decisión.

## Estado actual tras #68 — 13/10/2026

Main `c69f682` integrado y CI 10/10 PASS. Cuatro imágenes locales identificadas contra el runtime del 09/10. Escaneos completos del 13/10: 0 H/C corregibles, 0 secretos y 25 CVE residuales / 119 filas. La aceptación R-20 anterior caducó; [propuesta actual](evidence/2026-10-13-main-closure/R20_CURRENT_PROPOSAL.md) pendiente, `accepted: false`. R-23 OPEN independiente. A-S3 formal pendiente de decisión; A-S4 ensayo/narración/tutor pendientes; TFG-A IN PROGRESS y AWS bloqueado. [Registro verificable](evidence/2026-10-13-main-closure/README.md). Las secciones fechadas anteriores se conservan como historia, sin aceptación vigente por inferencia.

## Post-merge verification - 2026-10-13

PR64 is merged into main `0797671e06baa2b20f8ce5a842ac04140d84e591`; its tree equals the tested a931601 candidate. Fresh main CI:10/10 checks PASS. The four retained local image IDs/origins/rootfs match the runtime evidence dated 09/10; CI-built images remain separately identified.

Fresh full four-image scans:0 fixable High/Critical and0 secrets at all severities. Residuals now total25 unique CVEs/119 package rows, including two added GnuTLS CVEs. R-20's historical authorization expired 09/10; the23-CVE accepted:false proposal does not automatically cover this delta. R-23 remains OPEN independently; production npm audit 0 and full development audit 5 High. No upgrade or new functional runtime is claimed.

**A-S3 formal: PENDING applicable explicit risk decision and publication review; A-S4: PENDING; TFG-A: IN PROGRESS; AWS: BLOCKED.** The current record supersedes dated state claims below while preserving historical evidence and receipts. See [post-merge verification](evidence/2026-10-13-postmerge-certification/README.md).

**A-S2 technical GO / formal GO for the exact local synthetic security record. TFG-A IN PROGRESS. A-S3 authorized for evidence/versioning only.**

| Control                                               | Result                                                                         |
| ----------------------------------------------------- | ------------------------------------------------------------------------------ |
| Backend / frontend / PostgreSQL fixable High/Critical | 0 / 0 / 0, exact frozen images                                                 |
| Secrets                                               | 0 source/history/images; new publication separately verified at all severities |
| Backend unit / integration / frontend                 | 174 / 35 / 41 PASS in bound prior regression                                   |
| Docker/RBAC/Flyway/V3/persistence/readiness/recovery  | PASS in bound exact-image regression                                           |
| Playwright                                            | 9 PASS / 6 intentional skips                                                   |
| R17 / R19 / authorized corrected R21 / R22            | CLOSED                                                                         |
| R20                                                   | ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC — 23 conditional OS CVEs                |
| R23                                                   | OPEN / NO FIX AVAILABLE — independent disposition APPROVED; not closed         |
| A-S2 technical / formal                               | GO / GO                                                                        |
| A-S3                                                  | ACTIVE — documentation, versioning, evidence, PR/CI preparation only           |
| A-S4 / TFG-A COMPLETE / AWS                           | Later / false / blocked                                                        |

The [owner authorization](evidence/2026-10-06-a-s2-acceptance/owner-authorization.json), [R20 acceptance](evidence/2026-10-06-a-s2-acceptance/r20-current-acceptance.json), [R23 disposition](evidence/2026-10-06-a-s2-acceptance/r23-approved-open-disposition.json) and [closure index](evidence/TFG_A_SECURITY_CLOSURE_GATE_ACCEPTED_2026-10-06.json) bind this gate. The earlier [R21/R22 pack](evidence/2026-10-06-r21-r22/README.md) supplies runtime/scanner evidence; [applicability](R20_APPLICABILITY_REVIEW.md) supplies the individual residual assessment. No new regression or image rebuild is claimed in this documentation-only closure.

Exclusive expiry **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal, extension or future-image transfer. Local synthetic only; no AWS, production, real data or public/pilot exposure. Zero fixable H/C and zero secrets plus the registered early-invalidation conditions remain mandatory. GO ceases to support operation at expiry/invalidation; the historical decision record remains preserved.

Full scans stay visible: frontend48High/1Critical, PostgreSQL63High/3Critical, source braces1High and full npm5High affected-package aggregates/one advisory. Formal GO is a bounded explicit decision, not absolute zero, universal non-exploitability, TFG-A completion, accepted release commit, hosted CI pass or institutional certification. No further upgrade or PR57–59 merge is authorized. [A-S3](A_S3_EVIDENCE_VERSIONING.md) records the next version/evidence boundary.
