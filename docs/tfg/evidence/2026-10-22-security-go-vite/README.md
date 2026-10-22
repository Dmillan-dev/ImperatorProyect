# Candidato local Go/gosu + Vite — 22/10/2026

**Seguridad técnica PASS; R-20 pendiente de aprobación explícita. TFG-A IN PROGRESS; AWS BLOCKED.** La [propuesta](R20_PROPOSED_DECISION.json) sigue `accepted: false`. No se renueva ni modifica ninguna aceptación histórica y no se ha iniciado el MVP Docker.

Base `ff3181c` (main tras #67). Parche `641c09e011e0183bb2267bb0e9c027fc2dc76752`, [PR #79 en borrador](https://github.com/Dmillan-dev/ImperatorProyect/pull/79). El commit documental posterior conserva el SHA de construcción real. Las imágenes de CI son artefactos independientes de los IDs locales siguientes.

## Intervención acotada

- Go builder 1.26.9 → 1.26.10, con digest oficial fijado; mismo gosu 1.19 y commit de fuente. Dockerfile, lock D094 y aserción existente actualizados coherentemente. PostgreSQL 18.6, Bookworm, Java y Spring permanecen iguales.
- Lockfile exacto de la PR #78: Vite 8.2.2 → 8.3.5 y transitivas identificadas en [LOCKFILE_VERSION_CHANGES.json](LOCKFILE_VERSION_CHANGES.json). `package.json` y la base Node 24.20.0 no cambian. `npm ci` conserva los bytes del lockfile.
- Sin cambios funcionales, arquitectura, workflows, Terraform, AWS ni upgrades adicionales. #67 ya estaba en main; #75–78 no se han fusionado ni cerrado durante este trabajo.

La evidencia inicial de PostgreSQL histórico detecta CVE-2026-111943 en Go 1.26.9. El [aviso oficial](https://pkg.go.dev/vuln/GO-2026-6809) publica corrección en 1.26.10. No aparece en el nuevo informe; tampoco se declara que el uso previo de gosu fuese explotable o inexplotable.

## Cuatro artefactos propuestos

| Componente | Índice OCI exacto | SHA de construcción |
| --- | --- | --- |
| migration, conservado | `sha256:c53c77aeba010ddfa1538aa75a62b20e5d826baba2869cf50b06c86512a2b8b4` | `6e79e83` |
| backend, conservado | `sha256:298f4094799ca9c95e0a72ee65c04d19d42950cddcda2c0f5e9810c1bcc44757` | `6e79e83` |
| frontend, nuevo | `sha256:1303dd6967f86cad6340cbcaff5088e69d8925a693b64c22596a7ad2684b2530` | `641c09e` |
| PostgreSQL, repetición canónica | `sha256:198913121a1f94475136857bdbf640ad94bc8d11b182abba044bbffad3502d5e` | `641c09e` |

Los manifiestos linux/amd64 y orígenes completos constan en [candidate-images.json](candidate-images.json). [Correspondencia de fuentes/artefactos](SOURCE_ARTIFACT_CORRESPONDENCE.json): los inputs ejecutables de backend y los de migration se conservan; backend-java/README.md es la única diferencia documental en el contexto copiado del backend. No se afirma que esas dos imágenes hayan sido reconstruidas desde 641c09e.

D094 pasa desde worktree limpio y se repite sin caché: **mismo manifest de runtime** `sha256:5c52cfdda11a2f10f4c76b993f013ecaa58665733d437444e4f30001b6a9b5c1`. Los índices completos son distintos por las atestaciones: primera construcción `eece53b…`, repetición `1989131…`. La propuesta fija exclusivamente la repetición como PostgreSQL canónico. [Primera provenance](d094-first/provenance.json) y [repetición](d094-repeat/provenance.json). formalReleaseEligible es una comprobación D094, no aceptación de riesgo ni aprobación académica.

## Validación observada

| Control | Resultado del 22/10 |
| --- | --- |
| Backend unitario | 174 PASS, 0 fallos, 0 omisiones |
| Frontend | 41 PASS; lint, tipos y build configurado PASS |
| Playwright | 9 PASS / 6 omisiones previstas; API sintética interceptada, sin backend ni base real |
| npm audit producción | 0 vulnerabilidades |
| Gitleaks de fuentes | 0 secretos en instantánea de fuentes 641c09e |
| Trivy fuentes, configuración de producción | 0 vulnerabilidades, 0 misconfiguraciones H/C fallidas y 0 secretos |
| Trivy con devDependencies | braces High sin fix y dos avisos Medium de @vitest/mocker, visibles y separados |
| Trivy cuatro IDs exactos | 0 H/C corregibles y 0 secretos en cada imagen; inventario completo sin ignore-unfixed |
| D094 | Primera y repetición PASS; gosu 1.19 compilado con Go 1.26.10 |
| CI del commit de construcción | 9 success / 1 neutral, 0 fallos; [snapshot](ci-code-candidate.json). El HEAD documental final requiere sus propios checks. |
| Integración completa de 35 pruebas | PENDING; no se ha iniciado PostgreSQL para esta suite |
| Docker MVP/RBAC/Flyway/persistencia/recovery | PENDING para el nuevo conjunto; el ensayo del 14/10 es histórico |

[Resumen](VALIDATION_SUMMARY.json), [comandos exactos de escaneo](scan/commands.json), [metadatos de DB](scan/database-metadata.json) y logs originales en `validation/`. Trivy 0.74.0 fijado por digest; DB actualizada 22/10 07:10 UTC y Java DB 22/10 01:08 UTC, descargadas nuevamente para este trabajo. Los informes de imágenes incluyen todas las severidades; el gate corregible se calcula aparte. Trivy fs omite devDependencies por defecto: el segundo informe usa explícitamente `--include-dev-deps`.

Los intentos iniciales fallidos se conservan: faltaba IMPERATOR_API_ORIGIN para el build; Chromium 1243 no estaba instalado; D094 rechazó el primer directorio de evidencias antes de construir por estar fuera de su build ignorado. Se corrigieron los parámetros/herramienta de ensayo, sin relajar controles ni cambiar código funcional. Host: Node 24.20.0 / npm 11.19.0, dentro del rango declarado; packageManager declara npm 11.17.0. No se instaló otra versión de npm. Los tests unitarios locales usan el Java 21 existente; CI verifica Temurin 21 por separado.

## Residual y nueva decisión R-20

El conjunto conservador sigue siendo **26 CVE / 137 identidades**; 24 CVE únicos tienen severidad H/C (131 filas) y dos quedan por debajo del umbral. [Comparación](CVE_COMPARISON.json) y [revisión individual](CVE_APPLICABILITY_REVIEW.json): ninguna fila residual, severidad o versión de paquete cambia en ese conjunto; ninguna nueva fila H/C. Se comparan también los 13 archivos de frontera identificados en el registro histórico: diez iguales; cambian Dockerfile PostgreSQL, lockfile frontend y el SHA de upload-artifact de #67. No se afirma una nueva inspección completa de todos los archivos históricos ni un nuevo pentest.

La propuesta conserva mitigaciones condicionadas a loopback, datos sintéticos, roles, restricciones de privilegios/importación y configuración canónica. No convierte el riesgo en inexplotabilidad. Los paquetes siguen presentes y algunos se enlazan/ejecutan; las condiciones por CVE y sus incertidumbres constan en la matriz. Las inspecciones antiguas de runtime son referencias condicionadas, no ejecución nueva.

Respecto al inventario completo del 16/10 no hay nuevas identidades de vulnerabilidad; desaparece una observación Medium de zlib Ubuntu y aparecen versiones corregidas para ocho CVE LOW de curl, en 16 filas. Se muestran en la comparación; no se actualiza curl ni se incorporan esos hallazgos a una aceptación implícita. La ausencia de FixedVersion del residual en la DB actual no significa que sea universalmente imposible remediarlo.

**Caducidad propuesta, todavía no aprobada:** 26/10/2026 00:00 Europe/Madrid (**UTC+01:00**, equivalente 25/10 23:00 UTC; cambio horario el 25/10). Sin renovación ni transferencia automática. Solo estos cuatro índices y manifiestos, entorno local y datos sintéticos; AWS, producción, datos reales y exposición pública excluidos. Ante caducidad, cambios de artefactos/condiciones o hallazgos nuevos/corregibles que invaliden la revisión, detener la ejecución y reevaluar.

La autorización para preparar Go/Vite y una PR **no equivale a aprobar esta propuesta R-20**. Se requiere decisión explícita de Daniel Millan Perez sobre el documento, sus digests, residual, condiciones y caducidad. La aceptación no cerraría por sí sola la regresión, A-S4 ni TFG-A.

## Tooling independiente

R-23 continúa **OPEN / NO FIX AVAILABLE**. [El advisory de braces](https://github.com/advisories/GHSA-vfj7-8cjw-p6xm) sigue sin versión corregida. La inspección del archivo OCI verifica 70 manifiestos de paquete bajo `/app`: no hay paquetes braces, Vite ni Vitest. [Prueba](FRONTEND_OCI_AND_PACKAGES.json). Esa inspección no demuestra ausencia universal de código empaquetado/incrustado ni seguridad de inputs de desarrollo.

Dos advisories Medium de @vitest/mocker 4.1.11 siguen abiertos: [registro manual de mocks](https://github.com/advisories/GHSA-qxg6-89mg-rqv8) y [traversal Windows](https://github.com/advisories/GHSA-xp26-3454-mjgp); ambos publican fix 4.1.12. La configuración del proyecto usa `vitest run`/jsdom y no declara mockerPlugin/interceptorPlugin; no se ha demostrado un endpoint vulnerable expuesto en este flujo. La condición oficial requiere un servidor Vite con esos handlers y acceso atacante a su WebSocket. No se declara inexplotabilidad ni se acepta riesgo automáticamente. Mantener tooling de confianza en entorno local, sin servidor de mocker expuesto; cualquier uso distinto requiere revisión. No se aplica el lote #76 ni un upgrade adicional de Vitest.

La consulta posterior de GitHub devuelve seis alertas Vite (una High, cuatro Medium y una Low) y dos Medium Vitest en main. El parche Vite elegido ya está fuera de todos esos rangos; no se añadió otra actualización al descubrir más alertas. El cierre de alertas de main se verifica tras integrar el parche, no se presume por publicar la rama.

## Lo que falta para terminar

1. Revisión de CI del último SHA de la PR #79 y de esta evidencia; mantenerla en borrador mientras corresponda.
2. Decisión R-20 explícita para los cuatro artefactos de esta propuesta y preflight del ensayo.
3. Completar integración y ensayo real local del nuevo conjunto, verificando RBAC, Flyway, persistencia y recovery; no sustituirlos por Playwright con API interceptada.
4. Congelar la versión/evidencia revisada. Concluir memoria, ensayo humano de doce minutos en inglés, grabación narrada con hash/QA y revisión académica antes de TFG-A COMPLETE.

Las cuatro imágenes históricas conservan ID y RootFS: [verificación](PROTECTED_IMAGES_VERIFICATION.json). No se podaron imágenes, cachés, contenedores ni volúmenes. No se modificaron receipts ni evidencias anteriores. AWS sigue bloqueado.

La comprobación completa del staging devuelve **Exit 2** por CRLF/espacios de informes y logs nuevos preservados byte a byte; **0 avisos fuera de esta carpeta de evidencias** y check de los nueve documentos ordinarios **Exit 0**. No se presentan estos avisos nuevos como históricos ni se normalizan para obtener un cero artificial. El [registro de publicación](PUBLICATION_VALIDATION.json) incluye el diagnóstico y la prueba de 959 evidencias históricas sin cambios.
