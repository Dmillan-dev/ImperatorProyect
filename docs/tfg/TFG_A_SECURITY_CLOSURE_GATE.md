# TFG-A Security Closure Gate — Explicit Owner Decision 2026-10-06

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
