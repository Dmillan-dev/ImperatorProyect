# A-S3 Evidencia y versión

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
