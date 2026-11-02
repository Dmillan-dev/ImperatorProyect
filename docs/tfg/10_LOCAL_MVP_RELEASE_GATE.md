# Gate de entrega local — A-S3 y A-S4

## Control vigente — 02/11/2026

**MVP local todavía NO-GO formal.** El [candidato de seguridad](evidence/2026-11-02-security-alerts/README.md), fuente `2539635`, cubre las nueve alertas GitHub y los nuevos H/C corregibles de imagen, con cuatro IDs exactos 0 H/C corregibles / 0 secretos, 174 unitarios + 35 integración + 44 frontend PASS y auditoría de producción 0. Moderates de tooling permanecen documentados; no se persigue un cero absoluto mediante upgrades generales.

Faltan publicación/CI/revisión del reemplazo, aceptación explícita del [nuevo R-20](evidence/2026-11-02-security-alerts/R20_PROPOSED_DECISION.json) (`accepted: false`, 23 CVE conservadores / 21 H/C, nueva SQLite sin fix), disposición independiente R-23 y ensayo completo del conjunto Docker exacto. Después A-S3 formal y A-S4: memoria, ensayo humano medido de doce minutos en inglés, grabación narrada y revisión académica. No se presupone fecha/tutor ni aprobación del alcance por el centro. TFG-A IN PROGRESS; AWS/producción/datos reales bloqueados.

## Registro histórico — 27/10/2026

**Entrega local actual NO-GO formal.** El [candidato de seguridad](evidence/2026-10-27-local-security/README.md), SHA `64c38c2`, consigue 0 H/C corregibles y 0 secretos en cuatro imágenes exactas, D094 reproducible y regresión 174 unitarios + 35 integración + 44 frontend PASS. No hay CI de publicación nuevo ni aceptación vigente de R-20: la [propuesta](evidence/2026-10-27-local-security/R20_PROPOSED_DECISION.json) permanece `accepted: false`, con caducidad propuesta 03/11/2026 00:00 Europe/Madrid, sin renovación automática.

La auditoría de desarrollo mantiene undici High corregible como decisión separada, fuera de R-20. Pendientes también ensayo Docker conjunto/persistencia/recovery y A-S4 humana: doce minutos medidos en inglés, grabación narrada y revisión académica. La integración temporal no sustituye el ensayo de defensa. R-23 sigue OPEN; se preserva el cierre histórico A-S3 y los receipts anteriores. TFG-A IN PROGRESS; AWS/producción/datos reales bloqueados.

## Registro histórico — 22/10/2026

**Nueva ejecución local NO-GO: R-20 caducado.** El [candidato Go/Vite](evidence/2026-10-22-security-go-vite/README.md) tiene seguridad técnica PASS y una nueva propuesta `accepted: false`. Faltan decisión explícita vinculada a sus cuatro digests, integración completa y ensayo Docker del nuevo conjunto. El cierre histórico de A-S3 se conserva; no transfiere la aceptación a este candidato. A-S4 y TFG-A siguen IN PROGRESS, con ensayo humano, vídeo narrado y revisión académica pendientes. AWS BLOCKED. Los estados fechados del 16/10 siguientes son históricos.

## Registro histórico — 16/10/2026

Revisión 16/10/2026. **TFG-A IN PROGRESS.** [R-20 aceptado temporalmente](evidence/2026-10-16-r20-local-acceptance/R20_ACCEPTED_DECISION.json) para cuatro imágenes exactas y 26 CVE conservadores. A-S3 conserva su [acta de cierre histórico](evidence/2026-10-14-r20-acceptance/A_S3_CLOSURE_RECORD.md). Base documental `94ce879`, CI histórico 10/10 PASS; la publicación de esta actualización requiere CI de su SHA. Runtime real 14/10 y nueva revalidación de seguridad 16/10 son hechos separados.

## A-S3 — Evidencia y versión

| Criterio | Estado / evidencia |
| --- | --- |
| Fuente, construcción y runtime exacto | Correspondencia demostrada y orígenes preservados; no se infiere igualdad binaria por árbol/SBOM |
| CI de la base integrada | 10/10 PASS histórico `94ce879`; CI de la nueva publicación se comprueba por separado |
| Escaneos exactos 16/10 | 0 H/C corregibles y 0 secretos en las cuatro imágenes |
| Riesgo residual | 26 CVE conservadores / 137 identidades aceptados; 24 H/C y dos por debajo del umbral, sin declararlos corregidos ni inexplotables |
| R-20 | ACCEPTED TEMPORARY, local/sintético, hasta 19/10/2026 00:00 Europe/Madrid; cuatro índices exactos, sin transferencia/renovación automática |
| R-23 | OPEN / NO FIX AVAILABLE; disposición de tooling independiente |
| Evidencias originales / receipts | Preservados; decisiones posteriores registradas aparte |
| A-S3 formal | COMPLETE, gate local de evidencia/versionado |

Las imágenes construidas en CI quedan fuera de la aceptación local. D094 formalReleaseEligible es elegibilidad técnica, no aprobación académica ni autorización AWS.

## A-S4 — Defensa y memoria

| Criterio | Estado / salida exigida |
| --- | --- |
| Memoria | PDF de 29 páginas y texto preparados, sin tutor ni fecha de entrega; layout revisado; PDF congelado 14/10 con actualización de seguridad adjunta |
| Requisitos, diagramas, código, manual y bitácora | Incorporados en memoria y vinculados a evidencias; revisión académica pendiente |
| Demo técnica sintética real 14/10 | PASS con mismas imágenes, RBAC, Flyway, persistencia y recovery |
| Guion inglés | Preparado, diez bloques / 720 segundos planificados |
| Ensayo humano doce minutos | PENDING medición real |
| Grabación final narrada | PENDING archivo, duración/hash, QA audio/imagen/secretos |
| Revisión académica / encaje DAM | PENDING, no se presupone aceptación |
| TFG-A COMPLETE | PENDING todos los criterios DoD y revisión académica |

[Material A-S4](A_S4_LOCAL_DEFENSE.md), [mapa DAM](11_DAM_MEMORY_DELIVERY.md), [formulario humano](evidence/2026-10-14-a-s4-runtime/HUMAN_DEFENSE_REVIEW.md). El clip técnico sin audio y las cifras planificadas no cumplen el criterio de defensa humana. La [Definition of Done](00_TFG_MASTER_PLAN.md#7-tfg-a-definition-of-done) sigue vigente.

No nuevos upgrades, cambios funcionales, Docker/base, workflows, Terraform ni AWS en esta preparación documental. Una nueva ejecución requiere aceptación vigente; si caduca, mantener solo la revisión de materiales históricos hasta nueva decisión.
