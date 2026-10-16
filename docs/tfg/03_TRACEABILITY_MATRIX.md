# TFG Requirement Traceability Matrix

## Estado local autorizado — 16/10/2026

**R-20 ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC; A-S3 cerrado; A-S4 y TFG-A IN PROGRESS; AWS BLOCKED.** Aprobación explícita de 26 CVE conservadores / 137 identidades actuales para los cuatro índices y manifiestos locales exactos, con datos sintéticos, hasta **19/10/2026 00:00 Europe/Madrid**; sin renovación ni transferencia automática. Escaneo 16/10: 0 H/C corregibles y 0 secretos. Hay 24 CVE H/C; los otros dos quedan por debajo de ese umbral, con severidad distinta por distribución. R-23 sigue OPEN independiente. [Decisión y evidencia](evidence/2026-10-16-r20-local-acceptance/README.md).

La aprobación no es un escaneo nuevo ni corrige vulnerabilidades. El ensayo técnico conserva su fecha 14/10; el PDF de memoria está congelado en ese corte y se acompaña de la decisión actual. Guion inglés actualizado; ensayo oral humano, grabación narrada y revisión académica pendientes. Las secciones fechadas siguientes son historia. No se autoriza AWS ni una ampliación del producto.


## Registro histórico del 14 de octubre de 2026

**R-20 ACCEPTED TEMPORARILY; A-S3 COMPLETE local académico; A-S4 IN PROGRESS; TFG-A IN PROGRESS; AWS BLOCKED.** Aceptación explícita para los cuatro índices/manifiestos locales y el conjunto conservador de 25 CVE del 13/10, exclusivamente datos sintéticos, hasta 16/10/2026 00:00 Europe/Madrid; sin renovación ni transferencia. Escaneo independiente 14/10: 0 H/C corregibles y 0 secretos, 23 CVE H/C más dos GnuTLS conocidas ahora LOW y sin fix. R-23 OPEN independiente. [Acta y evidencia](evidence/2026-10-14-r20-acceptance/A_S3_CLOSURE_RECORD.md). Las secciones históricas siguientes se conservan y no sustituyen esta decisión.


Preparación A-S4: [memoria PDF de 29 páginas](evidence/2026-10-14-a-s4-runtime/IMPERATOR_Memoria_TFG_Local.pdf), [guion en inglés](09_DEFENSE_SCRIPT_12_MIN.md) y [ensayo técnico real 14/10](evidence/2026-10-14-a-s4-runtime/README.md). API/UI, Flyway, RBAC, persistencia y recovery PASS; contenedores del ensayo retirados, imágenes/volumen conservados. CI main tras #72: 10/10 PASS sobre `adbe45e`. Ensayo oral humano, grabación narrada y revisión académica PENDING. La preparación de materiales no cierra A-S4 ni TFG-A.

## Registro histórico tras #68 — 13/10/2026

Main `c69f682` integrado y CI 10/10 PASS. Cuatro imágenes locales conservadas; 0 H/C corregibles y 0 secretos enescaneos 13/10, 25 CVEresiduales/119 filas. R-20 caducado; propuesta actual`accepted: false`. R-23 OPEN independiente. A-S3 formal pendiente R-20; A-S4: memoria/ensayo/narraciónpendientes; TFG-A IN PROGRESS y AWS bloqueado. [Registroactual](evidence/2026-10-13-main-closure/README.md). Las secciones fechadas posteriores son históricas y no renuevan aceptación.

## Post-merge verification - 2026-10-13

PR64 is merged into main `0797671e06baa2b20f8ce5a842ac04140d84e591`; its tree equals the tested a931601 candidate. Fresh main CI:10/10 checks PASS. The four retained local image IDs/origins/rootfs match the runtime evidence dated 09/10; CI-built images remain separately identified.

Fresh full four-image scans: 0 fixable High/Critical and 0 secrets at all severities. Residuals now total 25 unique CVEs/119 package rows, including two added GnuTLS CVEs. R-20's historical authorization expired 09/10; the 23-CVE `accepted: false` proposal does not automatically cover this delta. R-23 remains OPEN independently; production npm audit 0 and full development audit 5 High. No upgrade or new functional runtime is claimed.

**A-S3 formal: PENDING applicable explicit risk decision and publication review; A-S4: PENDING; TFG-A: IN PROGRESS; AWS: BLOCKED.** The current record supersedes dated state claims below while preserving historical evidence and receipts. See [post-merge verification](evidence/2026-10-13-postmerge-certification/README.md).

## Current Owner Decision And Sprint — 2026-10-06

**A-S2 technical GO / formal GO for the exact approved local synthetic record. TFG-A remains IN PROGRESS. A-S3 is authorized for evidence/versioning only; A-S4 is later and AWS remains blocked.** R-20 is ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC for the current 23-CVE residual, not declared inexploitable. Exclusive expiry **2026-10-09T00:00: 00+02:00 Europe/Madrid = 2026-10-08T22:00: 00Z**; no renewal or future-image transfer. R-23 disposition is independently approved while status remains **OPEN / NO FIX AVAILABLE**; it is not closed or included in R-20. Zero fixable High/Critical and zero secrets remain mandatory. No further upgrade is authorized. See the [registered decisions](evidence/2026-10-06-a-s2-acceptance/README.md), [security gate](TFG_A_SECURITY_CLOSURE_GATE.md) and [A-S3 work order](A_S3_EVIDENCE_VERSIONING.md).

Document status: **TFG-A IN PROGRESS**

The initial mapping used `4dbf4fbe89dad0c6fcfd906aac7624075846c37a`.
This TFG-A iteration inspects implementation SHA
`b602bbe1dde92a902c17272d59cd9f0c10b9efe5`. Historical validation labels below
retain their original scope; current runs are indexed in the Evidence Register.
Final academic evidence must bind to the accepted release SHA.

| ID             | Objective / competency                                                                                        | Requirement                                   | Implementation                      | Validation                                      | Evidence    | Status              |
| -------------- | ------------------------------------------------------------------------------------------------------------- | --------------------------------------------- | ----------------------------------- | ----------------------------------------------- | ----------- | ------------------- |
| `TFG-RF-01`    | `TFG-O-02`; `DAM-01`, `DAM-05`                                                                                | Import normalized synthetic Evidence          | R01 import workflow and API         | Java/API/PostgreSQL tests                       | `E-CORE-01` | `VALIDATED OFFLINE` |
| `TFG-RF-02`    | `TFG-O-02`; `DAM-01`, `DAM-05`                                                                                | Create one traceable Decision                 | D081 and D093 composition           | Composition and replay tests                    | `E-CORE-02` | `VALIDATED OFFLINE` |
| `TFG-RF-03`    | `TFG-O-02`; `DAM-01`                                                                                          | Generate deterministic Recommendation and ROI | D082 policy                         | Exact-value policy tests                        | `E-CORE-03` | `VALIDATED OFFLINE` |
| `TFG-RF-04`    | `TFG-O-02`, `TFG-O-04`; `DAM-01`, `AI-04`                                                                     | Require human governance                      | D083 review flows                   | Role, actor and sequence tests                  | `E-GOV-01`  | `VALIDATED OFFLINE` |
| `TFG-RF-05`    | `TFG-O-03`, `TFG-O-04`; `DAM-03`                                                                              | Preserve append-only history                  | Ledger domain/repository            | Replay, fork and concurrency tests              | `E-GOV-02`  | `VALIDATED OFFLINE` |
| `TFG-RF-06`    | `TFG-O-02`, `TFG-O-03`; `DAM-01`, `DAM-05`                                                                    | Project realized Business Value               | Business Value query                | Complete/incomplete flow tests                  | `E-CORE-04` | `VALIDATED OFFLINE` |
| `TFG-RF-07`    | `TFG-O-02`; `DAM-02`                                                                                          | Present one review workspace                  | Next.js application                 | Unit/component and Playwright evidence          | `E-UI-01`   | `VALIDATED OFFLINE` |
| `TFG-RF-08`    | `TFG-O-07`; `AI-01`, `AI-02`, `AI-03`, `AI-04`, `AI-05`, `AI-06`                                              | Explain without delegating decisions          | Bedrock explanation provider        | Provider/output/failure tests                   | `E-AI-01`   | `VALIDATED OFFLINE` |
| `TFG-NFR-01`   | `TFG-O-01`; `DAM-04`                                                                                          | Preserve hexagonal dependency direction       | Module/package boundaries           | Architecture inspection and compilation         | `E-ARCH-01` | `VALIDATED OFFLINE` |
| `TFG-NFR-02`   | `TFG-O-04`; `CP-02`, `CP-05`, `SAA-01`, `DEVSEC-07`                                                           | Authenticate and authorize fail closed        | D087/D088 Resource Server and RBAC  | JWT negatives and route matrix                  | `E-SEC-01`  | `VALIDATED OFFLINE` |
| `TFG-NFR-03`   | `TFG-O-04`; `CP-02`, `CP-05`, `AI-05`, `DEVSEC-03`                                                            | Protect sensitive Evidence and secrets        | Redaction and scanner controls      | Visibility and secret scans                     | `E-SEC-02`  | `VALIDATED OFFLINE` |
| `TFG-NFR-04`   | `TFG-O-03`; `DAM-03`                                                                                          | Persist and migrate safely                    | JDBC, transactions and Flyway       | PostgreSQL integration profile                  | `E-DATA-01` | `VALIDATED OFFLINE` |
| `TFG-NFR-05`   | `TFG-O-05`; `SAA-05`                                                                                          | Expose bounded operational signals            | Actuator, ECS logs, MDC and metrics | D097 observability tests                        | `E-OPS-01`  | `VALIDATED OFFLINE` |
| `TFG-NFR-06`   | `TFG-O-05`; `DAM-07`, `DEVSEC-04`, `DEVSEC-05`                                                                | Ship hardened reproducible containers         | D092-D098 Docker assets             | Hosted image and D094 gates                     | `E-SC-01`   | `VALIDATED OFFLINE` |
| `TFG-NFR-07`   | `TFG-O-06`, `TFG-O-09`; `DAM-06`, `DEVSEC-01`, `DEVSEC-02`, `DEVSEC-03`, `DEVSEC-04`, `DEVSEC-05`             | Enforce software supply-chain gates           | CI, CodeQL, Trivy and Gitleaks      | Hosted checks on accepted SHA                   | `E-SC-02`   | `VALIDATED OFFLINE` |
| `TFG-CLOUD-01` | `TFG-O-08`, `TFG-O-09`; `CP-01`, `CP-03`, `CP-05`, `SAA-01`, `SAA-02`, `SAA-03`, `SAA-04`, `SAA-05`, `SAA-06` | Define a secure AWS target                    | D100 Terraform roots                | fmt/validate/test and misconfiguration scan     | `E-AWS-01`  | `VALIDATED OFFLINE` |
| `TFG-CLOUD-02` | `TFG-O-08`, `TFG-O-09`; `CP-04`, `SAA-04`                                                                     | Estimate and constrain pilot cost             | Planning contract only              | Not executed; TFG-B review required             | `E-AWS-02`  | `NOT IMPLEMENTED`   |
| `TFG-CLOUD-03` | `TFG-O-08`, `TFG-O-09`; `SAA-06`, `DEVSEC-06`                                                                 | Deploy the same application to AWS            | Publish/deploy workflows only       | Not executed; plan/apply authorization required | `E-AWS-03`  | `NOT IMPLEMENTED`   |
| `TFG-CLOUD-04` | `TFG-O-08`, `TFG-O-09`; `CP-04`, `SAA-02`, `SAA-04`, `SAA-05`                                                 | Prove cloud rollback and teardown             | D100 runbook only                   | Not executed; live AWS required                 | `E-AWS-04`  | `NOT IMPLEMENTED`   |
| `TFG-ID-01`    | `TFG-O-04`, `TFG-O-09`; `CP-05`, `SAA-01`                                                                     | Prove external IdP conformance                | D101 procedure only                 | Not executed; external IdP unavailable          | `E-ID-01`   | `BLOCKED_EXTERNAL`  |

## Traceability Rule

The Developer Associate and GitHub Foundations rows extend the same academic
requirements, not the business scope. Their evidence links are:

| Requirement                | Additional competencies             | Evidence / limitation                                                    |
| -------------------------- | ----------------------------------- | ------------------------------------------------------------------------ |
| `TFG-RF-08`                | `DVA-01`                            | `E-AI-01`; provider and failure tests, real Bedrock pending              |
| `TFG-NFR-02`, `TFG-NFR-03` | `DVA-02`, `GH-03`                   | `E-SEC-01`, `E-SEC-02`; cloud permissions and external settings unproved |
| `TFG-NFR-05`               | `DVA-04`                            | `E-OPS-01`; local signals, AWS diagnosis pending                         |
| `TFG-NFR-07`               | `DVA-05`, `GH-01`, `GH-02`, `GH-03` | `E-SC-02`, `E-GIT-01`; each result retains its revision                  |
| `TFG-CLOUD-01`             | `DVA-01`, `DVA-02`                  | `E-AWS-01`; offline configuration validation only                        |
| `TFG-CLOUD-03`             | `DVA-03`                            | `E-AWS-03`; delivery source exists, execution remains `NOT IMPLEMENTED`  |
| `TFG-CLOUD-04`             | `DVA-04`                            | `E-AWS-04`; AWS recovery still unexecuted                                |
| `TFG-O-09`                 | `GH-01`, `GH-02`, `GH-04`           | `E-GIT-01`; history, planning and documentation inspection               |

`TFG-NFR-04` includes V3 as implemented migration source. The earlier
`E-DATA-01` PostgreSQL certification covers V1/V2. Current V3 execution is
recorded separately as `E-DATA-02`: all 35 native integration tests, Docker
migration/persistence and append-only privileges now pass. The current image
October 2 fixable-only image gate passed after R-17 remediation. On October 6
R-19 and authorized R-21/R-22 corrected findings are CLOSED. A-S2 technical GO
now has formal GO through explicit current R-20 acceptance and the independent approved open R-23 disposition; passing tests alone never replace security acceptance. No status in this table certifies
the entire current release by inheriting an earlier result.

Every final-memory claim must include at least one requirement ID and evidence
ID. Screenshots alone are supporting material, not proof of behavior; they must
be paired with a command result, test report, cloud record or immutable commit.

See the [current security review](R21_R22_SECURITY_REVIEW.md) before claiming full closure.

## Current A-S2 / A-S3 Traceability

| Requirement / evidence        | Current demonstrated result                                                    | Binding / limitation                                                                      |
| ----------------------------- | ------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------- |
| TFG-NFR-03 / E-SEC-02         | Zero source/history/image secrets; new decision publication separately scanned | Exact prior runtime plus current publication receipt; no exposure/real-data certification |
| TFG-NFR-04 / E-DATA-02        | V1–V3, 35 integration tests, persistence and recovery PASS                     | Current frozen R21/R22 images; no new regression claimed                                  |
| TFG-NFR-06 / E-SC-01          | Zero fixable H/C; 23 OS CVEs conditionally accepted                            | Exact current R20 approval; exclusive expiry and early invalidation                       |
| TFG-NFR-07 / E-SC-02          | A-S2 formal GO; R23 tooling disposition approved, risk remains OPEN            | Full audit findings retained; hosted checks for new candidate remain PENDING              |
| TFG-O-09 / E-GIT-01           | A-S3 source/evidence/PR candidate preparation                                  | Accepted runtime fingerprint distinct from Git release approval/hosted CI                 |
| TFG-RF-08 / E-AI-01           | Explanation provider implemented/tested offline                                | No live Bedrock call or autonomous approval claim                                         |
| TFG-ID-01 / E-ID-01           | External IdP remains BLOCKED_EXTERNAL                                          | Local synthetic JWT fixture does not discharge D101                                       |
| TFG-CLOUD-02–04 / E-AWS-02–04 | Live cost/deployment/rollback remain NOT IMPLEMENTED                           | AWS blocked; not a prerequisite claimed automatically for local academic rehearsal        |

See [current owner approval](evidence/2026-10-06-a-s2-acceptance/README.md) and [A-S3](A_S3_EVIDENCE_VERSIONING.md).

## Final Memory / Defense Mapping — 2026-10-07

The [memory](08_LOCAL_MVP_MEMORY.md) maps each evaluated result to requirement/evidence IDs.
The [script](09_DEFENSE_SCRIPT_12_MIN.md) names the API/UI distinction, synthetic values, exact security disposition and offline/live boundaries.
Their 2026-10-07 [review pack](evidence/2026-10-07-a-s3-a-s4/README.md) preserves source and current image bindings.
A candidate SHA is not owner acceptance; hosted CI and final defense remain PENDING.
