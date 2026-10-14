# Gate de entrega local — A-S3 y A-S4

Revisión 14/10/2026. [A-S3 cerrado](evidence/2026-10-14-r20-acceptance/A_S3_CLOSURE_RECORD.md), [R-20 aceptado](evidence/2026-10-14-r20-acceptance/R20_ACCEPTED_DECISION.json), **TFG-A IN PROGRESS**. Integración documental #72: `adbe45eabb3a79807ec0e55c791006927dcb3f64`, CI main 10/10 PASS; [registro](evidence/2026-10-14-a-s4-runtime/main-ci-after-as3.json). El runtime 14/10 ejecutó las imágenes conservadas y fuente de entrada `30a03da`; esta integración solo cambia documentos.

## A-S3 — Evidencia y versión

| Criterio | Estado / evidencia |
| --- | --- |
| Fuente, construcción y runtime exacto | Correspondencia demostrada y orígenes preservados; no se infiere igualdad binaria por árbol/SBOM |
| CI del main integrado | 10/10 PASS `adbe45e`; CI no renueva decisiones de riesgo |
| Escaneos exactos 14/10 | 0 H/C corregibles y 0 secretos en las cuatro imágenes |
| Riesgo residual | Matriz conservadora de 25 CVE aceptada; escaneo actual 23 H/C + dos LOW, no corregidos ni declarados inexplotables |
| R-20 | ACCEPTED TEMPORARY, local/sintético, hasta 16/10/2026 00:00 Europe/Madrid; cuatro índices exactos, sin transferencia/renovación automática |
| R-23 | OPEN / NO FIX AVAILABLE; disposición de tooling independiente |
| Evidencias originales / receipts | Preservados; decisiones posteriores registradas aparte |
| A-S3 formal | COMPLETE, gate local de evidencia/versionado |

Las imágenes construidas en CI quedan fuera de la aceptación local. D094 formalReleaseEligible es elegibilidad técnica, no aprobación académica ni autorización AWS.

## A-S4 — Defensa y memoria

| Criterio | Estado / salida exigida |
| --- | --- |
| Memoria | PDF de 29 páginas y texto preparados, sin tutor ni fecha de entrega; layout revisado |
| Requisitos, diagramas, código, manual y bitácora | Incorporados en memoria y vinculados a evidencias; revisión académica pendiente |
| Demo técnica sintética real 14/10 | PASS con mismas imágenes, RBAC, Flyway, persistencia y recovery |
| Guion inglés | Preparado, diez bloques / 720 segundos planificados |
| Ensayo humano doce minutos | PENDING medición real |
| Grabación final narrada | PENDING archivo, duración/hash, QA audio/imagen/secretos |
| Revisión académica / encaje DAM | PENDING, no se presupone aceptación |
| TFG-A COMPLETE | PENDING todos los criterios DoD y revisión académica |

[Material A-S4](A_S4_LOCAL_DEFENSE.md), [mapa DAM](11_DAM_MEMORY_DELIVERY.md), [formulario humano](evidence/2026-10-14-a-s4-runtime/HUMAN_DEFENSE_REVIEW.md). El clip técnico sin audio y las cifras planificadas no cumplen el criterio de defensa humana. La [Definition of Done](00_TFG_MASTER_PLAN.md#7-tfg-a-definition-of-done) sigue vigente.

No nuevos upgrades, cambios funcionales, Docker/base, workflows, Terraform ni AWS en esta preparación documental. Una nueva ejecución requiere aceptación vigente; si caduca, mantener solo la revisión de materiales históricos hasta nueva decisión.
