# Verificación de main tras merge de #68 — 13/10/2026

Main: `c69f682e190e887549a5a864ff688c6d53941fa7`. [PR68](https://github.com/Dmillan-dev/ImperatorProyect/pull/68) fusionada. [CI](main-ci.json): **10/10 success**, incluidas Maven, CodeQL, imágenes, D094 y secretos del historial. Árbol idéntico al candidato documental 10dd707. Los cambios frente a main `0797671` están exclusivamente en README/docs: **0 cambios de entradas del producto**.

[Correspondencia](source-ci-correspondence.json): PostgreSQL se reconstruye reproduciblemente dentro de la CI de c69f682, pero su manifiesto runtime difiere del local validado el 09/10. Se conservan ambos índices de CI, manifests, SBOM y provenance con el [hash verificado del artefacto descargado](artifact-download.json). La bandera técnica formalReleaseEligible del artefacto no aprueba R-20 ni la entrega académica.

[Inventario local](local-images.json): cuatro IDs, orígenes y capas coinciden con las evidencias del 09/10; **0 contenedores en ejecución**. No se ha iniciado, reconstruido, descargado, etiquetado ni eliminado ninguna imagen durante esta revisión. La limpieza de 15 imágenes intermedias ya está [registrada](../2026-10-13-postmerge-certification/docker-cleanup-verification.json); no se repite ni se borran volúmenes/cachés.

[Escaneos del 13/10](../2026-10-13-postmerge-certification/security-summary.json): 0 H/C corregibles y 0 secretos, 25 CVE residuales /119 filas. La [propuesta R-20](R20_CURRENT_PROPOSAL.md) permanece `accepted: false`; R-23 OPEN independiente. CI se refiere al SHA exacto, no demuestra un runtime nuevo ni transfiere riesgo entre imágenes.

## Discrepancias resueltas documentalmente

- Las instrucciones activas A-S3/A-S4 seguían citando tres índices tfg-a-cbc ya retirados y un daemon detenido el 07/10. Se sustituyen por el procedimiento actual y las cuatro identidades realmente conservadas. Los snapshots históricos permanecen intactos.
- El guion/memoria seguían describiendo 23 CVE como aceptados vigentes. Se distingue la aceptación caducada del residuo actual de 25 y su decisión pendiente.
- D:/Imperator raíz sigue en 69e4b90 con cambios locales. Se utiliza un checkout aislado desde c69f682; no se añade, resetea ni publica el worktree antiguo.

## Gates

| Gate | Estado |
| --- | --- |
| Integración de #68 y CI main | PASS |
| Identidad de las cuatro imágenes locales | VERIFICADA contra runtime histórico |
| Cero H/C corregibles y secretos | PASS en escaneos del 13/10 |
| R-20 actual: 25 CVE | PENDIENTE decisión explícita |
| R-23 | OPEN / NO FIX AVAILABLE |
| A-S3 formal | PENDING R-20 y registro final de entrega |
| A-S4 | Preparación documental; ensayo/narración/revisión pendientes |
| TFG-A | IN PROGRESS |
| AWS | BLOCKED |

La [guía académica DAM](../../11_DAM_MEMORY_DELIVERY.md) separa tareas documentales preparables de ejecución y aprobación humana pendientes. Ningún test funcional se ha repetido en esta revisión; los resultados 174/35/41 y 9 Playwright/6 skips siguen fechados el 09/10.

## Verificación de publicación

213 enlaces locales sin errores; 834 evidencias anteriores byte-identical; bundle/zip y receipt de b02e3e2 intactos; Gitleaks de esta preparación: 0 findings. Diff de documentos ordinarios: exit0. El check global del staging da exit2: 201 avisos derivados de CRLF en tres capturas JSON nuevas y dos líneas vacías EOF originales en informes de CI. Los 203 están exclusivamente en evidence/** y conservan los bytes capturados/descargados; no se califican como presentes en HEAD ni se normalizan para forzar exit0. .gitattributes permanece intacto con docs/tfg/evidence/** -text.
