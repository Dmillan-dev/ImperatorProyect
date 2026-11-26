# A-S3 Evidencia y versión

## Control vigente — 26/11/2026

**PASS técnico de seguridad; NO-GO formal.** Main `90a6af8` integra #95. La [revisión y limpieza del 26/11](evidence/2026-11-26-local-hygiene/README.md) confirma cuatro IDs conservados con **0 H/C corregibles y 0 secretos**, GitHub con 0 Dependabot / 0 CodeQL abiertos y limpieza acotada de imágenes/cachés. No se reconstruyó el runtime ni se ejecutó un ensayo funcional nuevo.

El nuevo inventario contiene **40 CVE H/C / 42 CVE conservadores**: se añade Zstandard CVE-2026-124851, corregido upstream pero sin paquete corregido publicado en Bookworm. [R-20 actual](evidence/2026-11-26-local-hygiene/R20_REVIEW_REQUIRED.json) sigue `accepted: false`, sin nueva caducidad asignada ni aceptación transferida. R-23 es independiente. Los lotes #92/#93 se recomiendan diferir/cerrar sin fusionar; los checks y el riesgo se valoran sobre sus HEAD actuales.

La regresión previa conserva su fecha: 174 unitarios y 44 frontend el 20/11, 35 integración el 11/11; Playwright con API interceptada no sustituye el ensayo Docker. Faltan decisión explícita, ensayo completo del conjunto exacto y cierre formal A-S3; después defensa humana A-S4. **A-S3 BLOCKED; A-S4 PENDING; TFG-A IN PROGRESS; AWS NOT AUTHORIZED.** La nueva publicación requiere CI propio, sin trasladar identidad de imágenes locales a las construidas por CI.

## Registro histórico — 20/11/2026

**Candidato actual NO-GO formal; cierre histórico preservado.** [Candidato de seguridad](evidence/2026-11-20-minimatch-node/README.md) desde main `3497859`, fuente `b8bfe9a`: un frontend nuevo y tres IDs exactos retenidos con escaneos actuales y correspondencia de entradas. No se atribuyen nuevas construcciones a los componentes retenidos ni igualdad binaria a imágenes de CI. Regresión actual y pruebas históricas se distinguen por fecha.

Pendientes: publicación/CI/revisión del parche acotado, [R-20 explícito](evidence/2026-11-20-minimatch-node/R20_REVIEW_REQUIRED.json) para los digests y el nuevo inventario de 41 CVE conservadores / 39 H/C, disposición R-23 independiente y ensayo completo de esos mismos artefactos. Después: congelar SHA/digests/escaneos/runtime, completar A-S4 humana y revisión académica. No nuevos productos o AWS para cerrar el MVP.

## Registro histórico — 11/11/2026

**A-S3 actual NO-GO formal; cierre histórico preservado.** [Fuente `49c8b67`, construcción y cuatro imágenes exactas](evidence/2026-11-11-d094-reproducibility/candidate-images.json) se registran junto con escaneos, 174 unitarios + 35 integración + 44 frontend PASS y límites de Playwright. R-20 no tiene aceptación vigente: [41 CVE conservadores / 39 H/C y 18 nuevas observaciones](evidence/2026-11-11-d094-reproducibility/README.md), propuesta `accepted: false`, sin transferencia desde imágenes anteriores o hacia CI.

Pendientes: CI/revisión de la publicación mínima, decisión explícita R-20 sobre los digests, disposición R-23 independiente y ensayo del conjunto exacto con RBAC, Flyway, persistencia y recovery. Después A-S4 humana: memoria, doce minutos reales en inglés, grabación narrada y revisión académica. TFG-A IN PROGRESS; AWS bloqueado.

## Registro histórico — 02/11/2026

Se conserva el cierre histórico; **el candidato actual no está formalmente cerrado**. [Código, tres nuevos índices y PostgreSQL retenido](evidence/2026-11-02-security-alerts/candidate-images.json), fuente `2539635`, con escaneos actuales 0 H/C corregibles / 0 secretos y regresión 174 unitarios + 35 integración + 44 frontend PASS. No se atribuye una nueva construcción a PostgreSQL ni se transfieren los resultados a imágenes de CI.

Pendientes: CI/revisión y merge del reemplazo acotado, [R-20 explícito](evidence/2026-11-02-security-alerts/R20_PROPOSED_DECISION.json) para 23 CVE conservadores incluyendo la nueva SQLite, disposición R-23 por advisory retirado y ensayo completo Docker/persistencia/recovery de esos mismos artefactos. Las nueve alertas de main se cierran por recálculo tras integrar, no por descartar manualmente findings. [Ruta al MVP local](evidence/2026-11-02-security-alerts/README.md). A-S4/TFG-A IN PROGRESS; AWS BLOCKED.

## Registro histórico — 27/10/2026

El cierre histórico se preserva; el nuevo candidato **no está formalmente cerrado**. SHA de código `64c38c2`, [digests/capas/orígenes exactos](evidence/2026-10-27-local-security/candidate-images.json), D094 repetible, escaneos de cuatro imágenes 0 H/C corregibles / 0 secretos y regresión 174 unitarios + 35 integración + 44 frontend PASS. Backend/migration se reutilizan con correspondencia de fuentes ejecutables, sin atribuirles una nueva construcción.

Faltan CI de la publicación, disposición independiente de undici de desarrollo, aprobación vigente de [R-20](evidence/2026-10-27-local-security/R20_PROPOSED_DECISION.json) y ensayo Docker completo del conjunto. El escaneo con política CI pasa, pero la auditoría de desarrollo no está limpia. No se transfiere runtime histórico, aceptación o cierre a este candidato. A-S3 actual NO-GO; A-S4/TFG-A IN PROGRESS y AWS BLOCKED. [Evidencia y límites](evidence/2026-10-27-local-security/README.md).

## Candidato en revisión — 22/10/2026

El cierre histórico de A-S3 se conserva. La versión `641c09e` incorpora únicamente Go/gosu y Vite, con [identidad de artefactos, escaneos y propuesta R-20](evidence/2026-10-22-security-go-vite/README.md). R-20 está caducado; la propuesta nueva sigue `accepted: false`. El PASS de seguridad no equivale a aceptación, regresión Docker completa ni cierre de TFG-A. La publicación documental posterior referencia el SHA de construcción original y no cambia los binarios. AWS continúa bloqueado.

## Registro histórico — 16/10/2026

**R-20 ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC; A-S3 cerrado; A-S4 y TFG-A IN PROGRESS; AWS BLOCKED.** Aprobación explícita de 26 CVE conservadores / 137 identidades actuales para los cuatro índices y manifiestos locales exactos, con datos sintéticos, hasta **19/10/2026 00:00 Europe/Madrid**; sin renovación ni transferencia automática. Escaneo 16/10: 0 H/C corregibles y 0 secretos. Hay 24 CVE H/C; los otros dos quedan por debajo de ese umbral, con severidad distinta por distribución. R-23 sigue OPEN independiente. [Decisión y evidencia](evidence/2026-10-16-r20-local-acceptance/README.md).

La aprobación no es un escaneo nuevo ni corrige vulnerabilidades. El ensayo técnico conserva su fecha 14/10; el PDF de memoria está congelado en ese corte y se acompaña de la decisión actual. Guion inglés actualizado; ensayo oral humano, grabación narrada y revisión académica pendientes. Las secciones fechadas siguientes son historia. No se autoriza AWS ni una ampliación del producto.


**COMPLETE para la versión local académica, 14/10/2026.** Fuente revisada `30a03dab7d26c1ddf3b30c4ea052614efe39c405` y CI 10/10 PASS. [Acta](evidence/2026-10-14-r20-acceptance/A_S3_CLOSURE_RECORD.md) y [registro exacto](evidence/2026-10-14-r20-acceptance/A_S3_CLOSURE_RECORD.json).

R-20 queda **ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC**, exclusivamente para cuatro índices/manifiestos, datos sintéticos y entorno local, hasta **16/10/2026 00:00 Europe/Madrid**. Sin renovación automática ni transferencia a imágenes futuras/CI. [Decisión](evidence/2026-10-14-r20-acceptance/R20_ACCEPTED_DECISION.json). R-23 continúa OPEN independiente; no hay nuevos upgrades autorizados.

La aceptación se basa en 25 CVE / 119 bindings del 13/10. El escaneo independiente 14/10 mantiene 0 corregibles H/C y 0 secretos: 23 CVE H/C más las dos GnuTLS conocidas ahora LOW, todavía afectadas y sin fix. Todos los bindings aprobados están presentes; la matriz conservadora aprobada no se reemplaza. [Revisión del delta](evidence/2026-10-14-r20-acceptance/scan-delta-review.json).

La fuente de entrega solo cambia documentación respecto a los inputs del producto validados; no demuestra por sí sola identidad de imágenes. Los cuatro artefactos locales mantienen sus propios orígenes 6e79e83/a931601, índice/plataforma/rootfs y runtime 09/10; las construcciones CI se identifican aparte. Los receipts/evidencias históricas permanecen intactos.

A-S4 está **IN PROGRESS**: memoria local, defensa en inglés, ensayo narrado de 12 minutos, grabación y revisión académica. **TFG-A IN PROGRESS; AWS BLOCKED.** La publicación de este registro se revisa por CI de la rama; no se anticipa un PASS de ese nuevo SHA.
