# Revisión local y limpieza — 26 noviembre 2026

Base revisada: **main `90a6af89b7759d9952ec76d2ab80ec0837256bf7`**, con [#95 integrada](https://github.com/Dmillan-dev/ImperatorProyect/pull/95). Esta publicación cambia documentación y reglas de exclusión; no modifica aplicación, dependencias, versiones de imágenes, workflows ni Terraform.

**Seguridad técnica de las cuatro imágenes: PASS. A-S3: BLOCKED. A-S4: PENDING. TFG-A: IN PROGRESS. AWS: NOT AUTHORIZED.** R-20 continúa sin aceptación vigente. La limpieza no certifica un runtime nuevo ni cierra el TFG.

## Seguridad e identidad

Los [cuatro índices/manifiestos y sus orígenes](canonical-images.json) son exactamente los conservados el 20/11. Se volvieron a inspeccionar y escanear el 26/11, con Trivy 0.74.0 fijado por digest y bases descargadas ese día. Los informes contienen todas las severidades, sin ignorar findings. ImageID y DiffIDs del informe se comprobaron contra Docker. No se reconstruyeron las imágenes; la nueva política de contexto se verifica aparte y las futuras construcciones/CI no heredan identidad ni aceptación.

| Componente | ID local abreviado | H/C corregibles en el informe | Secretos | Filas H/C residuales |
| --- | --- | ---: | ---: | ---: |
| Backend | `1de68d8ccd59` | 0 | 0 | 0 |
| Frontend | `9822adfced85` | 0 | 0 | 39 |
| Migration | `cb87d9906327` | 0 | 0 | 0 |
| PostgreSQL | `76e1ba4c8015` | 0 | 0 | 114 |

PostgreSQL está sin etiqueta en el daemon, pero su índice y manifiesto están presentes y protegidos. No se interpreta una imagen sin etiqueta como prescindible.

[Resumen](summary-baseline.json), [comandos](scan-commands.json), [bases/versiones](scan/database-metadata.json) e informes completos: [backend](scan/baseline-backend.json), [frontend](scan/baseline-frontend.json), [migration](scan/baseline-migration.json), [PostgreSQL](scan/baseline-postgres.json).

Hay **40 CVE H/C únicos / 153 asociaciones**. El conjunto conservador conserva dos CVE anteriores de severidad hoy inferior: **42 CVE / 221 asociaciones**. El inventario completo, que no equivale al conjunto solicitado para aceptación, contiene **253 CVE únicos y otros 16 identificadores**. No se borran residuales para producir un cero absoluto.

### Delta R-20 que exige decisión

La [comparación individual](residual-review.json) añade **CVE-2026-124851** respecto a los 39 H/C / 41 conservadores del 20/11; no retira ninguno. Afecta a `libzstd1` en frontend/PostgreSQL y `zstd` en PostgreSQL, versión `1.5.4+dfsg2-5`.

Existe corrección upstream en **Zstandard 1.6.0**; [Debian](https://security-tracker.debian.org/tracker/CVE-2026-124851) todavía registra Bookworm afectado sin paquete corregido publicado. Por tanto, «no corregible» aquí significa sin corrección disponible en la rama elegida según los datos fechados; no significa que no exista solución global. [Aviso del mantenedor](https://www.openwall.com/lists/oss-security/2026/11/24/1) y [release oficial](https://github.com/meta/zstd/releases/tag/v1.6.0).

La condición publicada requiere datos Zstd malformados y observar la salida descomprimida; puede revelar memoria del contexto. No se encontró uso explícito en las fuentes examinadas. Eso no demuestra que ningún binario o dependencia nativa lo ejecute: la alcanzabilidad sigue sin establecerse. PostgreSQL admite funcionalidades relacionadas con Zstd y no se inspeccionó ejecutando servicios la configuración nativa del candidato. Se propone excluir entradas comprimidas/restauraciones no confiables, además de las mitigaciones locales ya documentadas. No se declara inexplotable ni corregido.

La [revisión actual](R20_REVIEW_REQUIRED.json) mantiene **`accepted: false`**, sin aprobador y sin nueva caducidad asignada. La última aprobación expiró el 19/10. La fecha 27/11 de la propuesta anterior tampoco fue aprobación. El responsable debe decidir entre remediación acotada revisable o aceptación temporal explícita del conjunto actual, con timestamp exclusivo, zona y alcance local/sintético. No hay renovación automática ni transferencia a otras imágenes. R-23 permanece **OPEN / DISPOSITION REVIEW REQUIRED** por separado tras retirarse su advisory.

## GitHub y PR

[Consulta autenticada del 26/11](github-status.json): **0 alertas Dependabot abiertas y 0 CodeQL abiertas**. Son estados del repositorio, no escaneos del runtime. Se conserva la [lista de checks de main](main-checks.json), con sus SHA y conclusiones; contiene un check de imágenes fallido del 23/11 y no se presenta como CI completamente verde. El nuevo HEAD documental requiere su propio CI. El [log](main-image-failure.log) rechazó cuatro JAR por el mismo hash inesperado; la [verificación del 26/11](maven-artifact-verification.json) confirma las cuatro descargas contra los hashes fijados, un segundo endpoint y el SHA256 publicado. No se cambian pins ni versiones para superar ese control.

| PR | Hallazgo | Recomendación para congelar el MVP |
| --- | --- | --- |
| [#92](https://github.com/Dmillan-dev/ImperatorProyect/pull/92) | 17 actualizaciones Maven; wrapper 3.10.2 frente a Maven 3.9.16 exigido. Falla Java/build de backend. | Cerrar sin fusionar y diferir mantenimiento. No aflojar el enforcer/checksum para poner CI verde. |
| [#93](https://github.com/Dmillan-dev/ImperatorProyect/pull/93) | Lote de 12 dependencias frontend, CI verde. Se ha actualizado respecto al lote auditado el 20/11. | Diferir/cerrar sin fusionar durante el cierre; no hay alerta abierta que exija introducir ese lote completo. Si se retoma, regresión y certificación nuevas. |

[Checks #92](pr92-checks.json), [archivos #92](pr92-files.json), [checks #93](pr93-checks.json), [archivos #93](pr93-files.json). Esta operación no cierra ni fusiona esas PR. El diagnóstico del viejo #93 sobre minimatch se conserva como historia, sin atribuirlo automáticamente a su HEAD actual.

## Limpieza realizada

- **14 imágenes intermedias retiradas por ID completo**, con `docker image rm --no-prune <ID>`, sin `--force`. Se verificó antes cada etiqueta/ID, ausencia de contenedores asociados, separación de imágenes protegidas y recuperación OCI íntegra. [Plan exacto](DOCKER_CLEANUP_PLAN.json), [verificación de las 14 copias OCI](oci-recovery-verification.json), [recibo de eliminación](DOCKER_CLEANUP_RECEIPT.json).
- **23 IDs conservados**, incluidos cuatro actuales, imágenes de defensa históricas/rollback, bases, herramientas y los referenciados por contenedores. Sus etiquetas, digests y RootFS siguen iguales. No se eliminaron contenedores, volúmenes ni cachés de construcción.
- Docker informa **7.345 → 6.352 GB** en la categoría imágenes: aproximadamente **0.993 GB menos según el inventario del daemon**. Las capas se comparten; esto no mide reducción física del VHD de Windows. [Antes](docker-system-df-before.jsonl), [después](docker-system-df-after.jsonl).
- **10 directorios de caché Next**, 185 archivos y **421,879,985 bytes (402.34 MiB)** retirados de cinco worktrees antiguos limpios: rutas resueltas dentro del workspace, ignoradas, sin archivos versionados/reparse points ni procesos Next asociados. [Recibo](HOST_CACHE_CLEANUP_RECEIPT.json). No se retiraron worktrees ni `node_modules`.
- El checkout antiguo `D:/Imperator` conserva sus 24 modificaciones y dos entradas sin seguimiento, sin staging. No es el checkout de entrega. `tmp` contiene material de otros proyectos: preservado. Evidencias históricas, receipts, PDF, capturas, volúmenes y archivos OCI recuperables se conservan.

Docker Desktop ya estaba iniciado y se deja en ese estado. Solo permanece el contenedor BuildKit previamente detenido; no se levantó el MVP. Las comprobaciones de contexto generan una pequeña caché nueva: no se podó la caché para ocultar ese cambio.

## Protección de Git y contexto Docker

`.gitignore` excluye `/output/tfg/`, estado local de herramientas, claves y configuración privada, preservando ejemplos, fuentes, lockfiles y evidencias curadas. Los filtros Docker efectivos excluyen también material privado dentro de subárboles permitidos; no se cambian `FROM`, paquetes ni lógica.

[19 casos positivos/negativos de Git](gitignore-validation.json). [Seis contextos Docker sintéticos](docker-context-validation.json) comprueban exclusión de señuelos privados inocuos y conservación de entradas necesarias con `FROM scratch`, sin red, sin ejecutar contenedores ni cargar imágenes. [Comprobación reproducible](validation/validate_contexts.py). Los contextos se prueban aparte del escaneo de las cuatro imágenes conservadas. La revisión independiente confirma 20 entradas necesarias presentes y 158 señuelos privados excluidos.

## Verificación de publicación

Gitleaks sobre el worktree: **0 secretos** ([reporte](gitleaks-worktree.json)); [1.244 archivos históricos de evidencia](historical-evidence-preservation.json) conservan exactamente su hash Git original, incluidos receipts/PDF. Los [enlaces locales revisados](document-link-validation.json) resuelven. El check completo de whitespace devuelve **Exit 2**, con 18.439 avisos exclusivamente dentro de este nuevo paquete de evidencias cuyos bytes se preservan (18.435 trailing whitespace, incluidos CR de CRLF, y cuatro blank line EOF). Los 17 documentos/filtros ordinarios devuelven **Exit 0**; no se normalizan capturas para cambiar ese resultado. Las pruebas funcionales previas conservan sus fechas; este cambio se valida localmente por contexto/exclusiones/documentos, además del CI del nuevo commit.

## Ruta de cierre

1. Revisar/CI/merge de esta publicación y decidir R-20 para los cuatro IDs y 42 CVE conservadores; R-23 independiente. No se ejecuta el MVP por aprobar una PR documental.
2. Con decisión y preflight vigentes: ensayo completo del conjunto exacto `DRC-AOA-001`, cuatro roles/RBAC positivo y negativo, Flyway V1–V3, persistencia y readiness/recovery. Congelar SHA/digests/escaneos/resultados para cerrar A-S3 formalmente.
3. A-S4: memoria y trazabilidad coherentes con esa versión, ensayo humano de doce minutos en inglés, grabación íntegra con voz, revisión audio/imagen/secretos y revisión académica. La demo técnica antigua y Playwright con API interceptada no sustituyen estos resultados.
4. TFG-A COMPLETE únicamente tras comprobar todos los criterios y revisión humana. Después TFG-B AWS readiness offline: resolver R-03 ECR, R-04 IAM y R-05 vinculación aprobación/plan, D101/D095 y costes actuales. [Guía local → AWS](../../06_LOCAL_VS_AWS.md).
5. Autorización operativa acotada para preparación/bootstrap/publicación/plan y, por separado, autorización del piloto después de revisar el plan exacto, IAM, coste y teardown. Sin AWS/producción/datos reales ni exposición pública por esta operación.
