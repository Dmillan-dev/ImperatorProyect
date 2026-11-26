# Local MVP To AWS MVP

[Auditoría vigente, inventario residual y recibos de limpieza](evidence/2026-11-26-local-hygiene/README.md): **40 CVE H/C / 42 conservadores**, 0 H/C corregibles / 0 secretos; Zstandard nuevo requiere revisión explícita. No se amplía la aceptación ni se autoriza AWS.

## Current control — 2026-11-26

**TFG-A IN PROGRESS / A-S3 BLOCKED / A-S4 PENDING / AWS NOT AUTHORIZED.** Verified `main` is `90a6af89b7759d9952ec76d2ab80ec0837256bf7` with #95 merged. GitHub queried on 26 November reports zero open Dependabot and CodeQL alerts. The four retained exact local image IDs were scanned on 26 November: zero fixable High/Critical findings and zero secrets. Repository checks and image scans do not establish a new full runtime rehearsal.

R-20 has **NO CURRENT ACCEPTANCE**. Its last approval expired on 19 October. The 20 November proposal remains `accepted: false`; the proposed exclusive expiry of 27 November 2026 00:00 Europe/Madrid (UTC+01:00) is not authorization. Review the changed residual inventory against the exact digests before any new explicit decision. R-23 remains **OPEN / DISPOSITION REVIEW REQUIRED** independently following advisory withdrawal.

Close the local track first: applicable security decision, complete same-image runtime rehearsal, formal A-S3 evidence freeze, then the human A-S4 defense and academic review. The historical October acceptance below cannot open any current gate. AWS readiness must address the existing R-03 ECR, R-04 IAM and R-05 plan/publication findings offline, followed by D101/D095, current costs and separate operational approvals under the [deployment runbook](../runbooks/aws-pilot-deployment.md).

## Sprints de cierre y transición

| Orden | Trabajo acotado | Evidencia de salida / gate |
| --- | --- | --- |
| A-S3 seguridad y versión | Decisión R-20 local/sintética para cuatro IDs y 42 CVE conservadores, caducidad explícita y R-23 separado; revisión/CI de la publicación. | Decisión vigente y SHA aceptado; 0 H/C corregibles / 0 secretos. |
| A-S3 runtime | Ensayar DRC-AOA-001 con esas imágenes, cuatro roles, RBAC positivo/negativo, Flyway V1–V3, persistencia y recovery. | Correspondencia SHA → construcción → digests → escaneos → runtime demostrada; acta A-S3. |
| A-S4 entrega | Memoria/diagramas/trazabilidad de la versión, ensayo humano de doce minutos en inglés, vídeo con voz y QA íntegro, revisión académica. | Criterios de terminado verificados; TFG-A COMPLETE solo con revisión favorable. |
| TFG-B readiness offline | Resolver R-03 ECR, R-04 IAM mínimo y R-05 plan/aprobación; D101/D095 para identidad externa; región/cuenta/DNS/TLS, coste vigente, límites y teardown. | Revisión offline aprobada; no despliegue ni credenciales AWS por este sprint. |
| Preparación autorizada | Tras autorización operativa acotada: bootstrap/publicación/secretos/plan según los gates existentes. | Plan real revisable, digests fijados, IAM/coste/rollback documentados; no apply del piloto todavía. |
| TFG-C piloto | Autorización separada sobre el plan exacto y su coste; aplicar ese mismo plan y validar entorno sintético. | Pruebas/seguridad/costes/teardown del piloto, con evidencias propias; R-20 local no cubre este entorno. |

R-03: bootstrap crea `imperator-pilot/*`, pero publicación/despliegue esperan `imperator/pilot/*`. R-04: políticas con `ecs:*`, `elasticloadbalancing:*` y alcance PassRole requieren revisión. R-05: la aprobación debe vincularse al plan aplicado y a la publicación; los workflows existentes no prueban esa correspondencia. Son bloqueos ya registrados, no autorización para modificarlos o ejecutar AWS en esta operación. Un `terraform validate` offline no valida cuotas, permisos ni gasto. Las estimaciones históricas no son un presupuesto actual ni una garantía de coste.

## Historical Owner Decision And Sprint — 2026-10-06

**A-S2 technical GO / formal GO for the exact approved local synthetic record. TFG-A remains IN PROGRESS. A-S3 is authorized for evidence/versioning only; A-S4 is later and AWS remains blocked.** R-20 is ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC for the current 23-CVE residual, not declared inexploitable. Exclusive expiry **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal or future-image transfer. R-23 disposition is independently approved while status remains **OPEN / NO FIX AVAILABLE**; it is not closed or included in R-20. Zero fixable High/Critical and zero secrets remain mandatory. No further upgrade is authorized. See the [registered decisions](evidence/2026-10-06-a-s2-acceptance/README.md), [security gate](TFG_A_SECURITY_CLOSURE_GATE.md) and [A-S3 work order](A_S3_EVIDENCE_VERSIONING.md).

Document status: **TFG-A IN PROGRESS**

The AWS target is a second runtime environment for the same application, not a
parallel product or a rewrite of business logic.

## Runtime Mapping

All `VALIDATED OFFLINE` entries below refer to source/configuration evidence,
not deployed AWS resources. The live AWS runtime remains `NOT IMPLEMENTED` and
has no `VALIDATED IN AWS` or `OPERATIONAL` evidence. Historical local tests must
not certify V3 or the current release without its dated result. Original
R-17/R-19/R-21/R-22 corrected findings and historical A-S2 results remain
indexed in the [Evidence Register](04_EVIDENCE_REGISTER.md). They do not
renew R-20, decide R-23 or replace the pending full rehearsal of the current
four-image set. The control above governs present acceptance and closure.

| Concern            | Local MVP                                | AWS target                              | Offline source evidence |
| ------------------ | ---------------------------------------- | --------------------------------------- | ----------------------- |
| Entry              | Loopback frontend port                   | Route 53, ACM and public HTTPS ALB      | `VALIDATED OFFLINE`     |
| Frontend           | Next.js Docker container                 | Private ECS/Fargate task                | `VALIDATED OFFLINE`     |
| Backend            | Spring Boot Docker container             | Private ECS/Fargate task                | `VALIDATED OFFLINE`     |
| Database           | Docker PostgreSQL volume                 | Private RDS PostgreSQL                  | `VALIDATED OFFLINE`     |
| Migration          | Compose one-shot Flyway service          | One-shot Fargate migration task         | `VALIDATED OFFLINE`     |
| DB privilege setup | Compose one-shot task                    | One-shot Fargate permissions task       | `VALIDATED OFFLINE`     |
| Images             | Local SHA tags/digests                   | Immutable ECR digest references         | `VALIDATED OFFLINE`     |
| Authentication     | Controlled local RS256 issuer fixture    | External Keycloak HTTPS                 | `BLOCKED_EXTERNAL`      |
| Authorization      | Same D088 route matrix                   | Same D088 route matrix                  | `VALIDATED OFFLINE`     |
| Secrets            | Local ignored/file-backed values         | Secrets Manager and task roles          | `VALIDATED OFFLINE`     |
| Logs               | ECS JSON to container output             | ECS JSON to CloudWatch Logs             | `VALIDATED OFFLINE`     |
| Metrics/health     | Internal Actuator and probes             | ALB/ECS probes and CloudWatch           | `VALIDATED OFFLINE`     |
| Explanation        | Disabled or mocked provider              | Optional Bedrock Converse               | `VALIDATED OFFLINE`     |
| Audit              | Application Ledger and local CI evidence | Ledger plus CloudTrail management audit | `VALIDATED OFFLINE`     |
| Cost               | Developer workstation                    | Metered AWS pilot                       | `NOT IMPLEMENTED`       |

## Invariants Across Environments

- the `DRC-AOA-001` policy and exact deterministic values;
- Domain/Application/Port behavior;
- REST contracts and error model;
- D087 JWT claims and D088 role grants;
- D083 human authority and append-only Ledger;
- Evidence sensitivity and redaction;
- Bedrock's optional, non-authoritative role;
- PostgreSQL migrations and least-privilege application access; and
- correlation, safe logs, metrics and health semantics.

## Environment-Specific Responsibilities

Local Docker demonstrates reproducibility, integration and a reliable defense
fallback. AWS must additionally prove account/Region identity, DNS/TLS,
network paths, IAM, secret delivery, image publication, managed-database
behavior, alarms, cost, rollback and teardown.

## Transition Gates

1. TFG-A freezes documentation and the local evidence baseline.
2. TFG-B delivery/IAM/pricing review requires separate authority; resolve its
   source findings before publication.
3. Operator-deferred D101 external Keycloak conformance must pass before live
   AWS authentication, bootstrap/publication, plan or pilot execution under
   the existing deployment runbook. It is an external dependency, not a sprint.
4. Image publication, bootstrap writes and a real plan require separate authorization.
5. Pilot apply requires a second explicit authorization.
6. TFG-D must verify runtime acceptance, cost and teardown.

No gate may infer success from the existence of the next gate's source files.

The canonical business policy and fixed synthetic amounts are reused unchanged.
Real AWS deployment costs are operations evidence, not replacement Evidence for
the synthetic ROI pack. No Lambda/DynamoDB implementation or parallel business
logic is required for the transition.

See the [current R-20 decision review](R20_SECURITY_DECISION.md) and [local release gate](10_LOCAL_MVP_RELEASE_GATE.md) before claiming closure.
