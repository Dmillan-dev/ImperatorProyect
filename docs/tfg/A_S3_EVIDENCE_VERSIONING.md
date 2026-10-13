# A-S3 — Evidencia y versión tras merge

Revisión: **13/10/2026**. Main integrado: `c69f682e190e887549a5a864ff688c6d53941fa7`, PR68 fusionada y 10/10 checks success. [Registro verificable](evidence/2026-10-13-main-closure/README.md). **A-S3 formal: PENDING R-20; TFG-A: IN PROGRESS.**

## Fuente, construcción y runtime

El árbol integrado coincide con el candidato documental 10dd707. Frente a main `0797671` no cambia ninguna entrada del producto. Los cuatro artefactos locales conservados mantienen IDs, origen y capas del ensayo del 09/10; PostgreSQL procede de a931601 y backend/frontend/migración de 6e79e83. No se cambia su origen a c69f682 por un merge documental.

Los nuevos artefactos PostgreSQL de CI están identificados por separado: reconstrucción reproducible dentro de ese SHA, pero manifiesto runtime distinto al local. Entradas y SBOM coincidentes no demuestran igualdad de imágenes. La defensa prevista usa los artefactos locales históricamente validados, condicionados a una decisión vigente; no pretende ejecutar imágenes nuevas de CI sin review.

## Checklist actual

| Control | Resultado |
| --- | --- |
| #64 y #68 integradas en main | DONE |
| CI efectiva del SHA c69f682 | 10/10 PASS |
| Fuente y entradas versionadas | VERIFICADAS; sin cambios funcionales en #68 |
| Cuatro imágenes locales ↔ runtime 09/10 | IDs/origen/rootfs coincidentes |
| Escaneos completos del 13/10 sobre esas mismas imágenes | 0 H/C corregibles; 0 secretos;25 CVE residuales/119 filas |
| Decisión vigente R-20 para esos 25 CVE y digests | PENDIENTE; `accepted: false` |
| R-23 | OPEN independiente; no inventar una actualización |
| Evidencias originales/receipts | PRESERVADOS; nuevos registros separados |
| Cierre formal A-S3 | PENDIENTE decisión y registro de versión revisada |
| A-S4 | Guion/memoria preparados; ejecución/narración/tutor pendientes |

El merge del propietario confirma integración documental, no renovación del riesgo. La [propuesta actual](evidence/2026-10-13-main-closure/R20_CURRENT_PROPOSAL.md) fija caducidad propuesta 16/10/2026 00:00 Europe/Madrid, todavía no autorizada. La aceptación anterior expiró 09/10; la propuesta de 23 CVE no se transfiere a 25.

## Qué permite cerrar A-S3

Registrar una decisión explícita válida para los 25 residuos, los cuatro índices/manifiestos, scope local/sintético y caducidad. Verificar que continúan 0 H/C corregibles y 0 secretos, preservar los informes completos y R-23 abierto, y revisar el paquete de entrega con el SHA integrado y origen de cada artefacto. Si cambia el residuo, un digest o una condición, parar y decidir; no abrir otra cadena de upgrades.

La futura aprobación se añadirá como registro nuevo: no sobrescribir aceptación caducada, b02e3e2 ni receipts. El cierre técnico/documental A-S3 no sustituye el [gate A-S4](A_S4_LOCAL_DEFENSE.md) ni la aprobación académica. D101/IdP externo, AWS, Bedrock real y release operativa quedan pendientes.

## Historia preservada

[Aceptación 06/10](evidence/2026-10-06-a-s2-acceptance/README.md), [review 07/10](evidence/2026-10-07-a-s3-a-s4/README.md), [ensayo del 09/10](evidence/2026-10-09-gosu-security-closure/TECHNICAL_VALIDATION.md) y [escaneos 13/10](evidence/2026-10-13-postmerge-certification/README.md) mantienen sus fechas y bytes. Las instrucciones activas actuales sustituyen sus procedimientos antiguos, no sus evidencias.
