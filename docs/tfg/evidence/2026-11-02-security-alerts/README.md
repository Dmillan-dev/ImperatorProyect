# IMPERATOR — corrección y control de seguridad, 02/11/2026

**Las nueve alertas GitHub quedan cubiertas por las versiones del candidato; no están cerradas en `main` hasta integrar y recalcular el grafo.** `main` sigue en `35e43ea`; la rama de revisión parte del candidato local no publicado del 27/10. SHA técnico actual: **`25396350c40ed7dc13b8aa136d85f809292d4519`**. Exclusivamente local/sintético; sin despliegue AWS, producción o datos reales.

## Alertas y parche mínimo

| Alertas | Paquete / candidato | Resultado |
| --- | --- | --- |
| #34–39 | Next 16.4.2 | Tres advisories, duplicados entre manifiesto y lockfile; versión fuera del rango afectado |
| #33 | undici 8.12.3, transitiva de jsdom | Parche de una entrada del lockfile, sin nueva dependencia directa |
| #30–31 | @vitest/mocker / Vitest 4.1.12 | Los dos avisos de desarrollo quedan fuera de su rango afectado |

[Mapa exacto de las nueve alertas](ALERT_REMEDIATION_MAP.json). Se conserva Next/configuración 16.4.2, Zod 4.6.7 y Vitest/coverage 4.1.12 del candidato anterior. Chai, React, Playwright, jsdom, Prettier, tipos y las demás dependencias directas no se actualizan. [Delta completo frente a main](LOCKFILE_VERSION_CHANGES.json).

Node incorpora exactamente el cambio a **24.22.2 Bookworm** de #85, con digest fijado en las tres etapas. Mantiene el mayor y la distribución; incluye las correcciones de seguridad de 24.22.1. No se describe 24.22.2 como una nueva release de seguridad independiente. [Release oficial](https://nodejs.org/en/blog/release/v24.22.2).

El escaneo de imágenes descubre dos bloqueos adicionales que las PR individuales dejan pendientes:

- **CVE-2026-111497 / libbrotli1:** se instala solo el paquete corregido Jammy `1.0.9-2ubuntu0.1` en backend y Noble `1.1.0-2ubuntu0.1` en migración, con SHA-256 oficial y comprobación de versión instalada. Las bases Temurin/Flyway y Java no cambian. El paquete no está instalado en frontend/PostgreSQL, por lo que no se añade allí. [Ubuntu](https://ubuntu.com/security/CVE-2026-111497), [paquetes verificados](image-security-patches.json).
- **CVE-2026-102554 / Guava:** está en `/flyway/drivers/gcp/guava-33.5.0-jre.jar`. Se retira únicamente el driver GCP sin uso del MVP PostgreSQL; no se actualizan Guava, Maven o Flyway. El build comprueba su ausencia y conserva el driver PostgreSQL. El scanner lo califica High; el advisory del proveedor lo califica Moderate, diferencia que se conserva sin relajar el gate. [Advisory del proveedor](https://github.com/google/guava/security/advisories/GHSA-xxph-c9ww-hj94).

OpenSSL Bookworm, las protecciones de lectura por stream y el límite de página de Zod permanecen como fueron revisados el 27/10. No se modifica código funcional, arquitectura, workflows, Terraform, Java/Spring/Maven, PostgreSQL mayor, Keycloak o Bedrock. El Dockerfile de migración está bajo `infra/aws/images`, pero su construcción/uso aquí es local; no ejecuta recursos ni proveedores AWS.

## Validación local actual

| Control | Evidencia / resultado |
| --- | --- |
| Backend | 174 unitarios PASS |
| Integración | 35 PASS; Flyway migrate/validate/idempotencia y REST/repositorios/RBAC sobre PostgreSQL exacto, base sintética temporal en tmpfs y solo loopback |
| Frontend | 44 tests, lint, tipos y build PASS con Node **24.22.2** portátil y checksum oficial |
| Playwright | 9 PASS / 6 omisiones previstas; API interceptada, no ensayo completo Docker |
| npm audit producción | **0 vulnerabilidades** |
| npm audit completo | **0 High/Critical**; 16 entradas Moderate por un único advisory minimatch del tooling |
| Trivy fuentes | Política CI PASS; el inventario con desarrollo tampoco tiene High/Critical |
| Trivy imágenes | **0 H/C corregibles y 0 secretos en cada una de las cuatro imágenes exactas** |
| Gitleaks | Snapshot e historial: 0; 171 commits antes de esta publicación documental |
| Ensayo Compose completo / persistencia / recovery | PENDING para este conjunto; no se hereda el ensayo histórico |
| CI GitHub | PENDING publicación del nuevo HEAD; el PASS local no sustituye los checks de GitHub |

[Resumen verificable](VALIDATION_SUMMARY.json). El equipo tiene Node global 26.13.0, incompatible con el rango existente; el primer ensayo de herramientas se conserva fuera de Git como diagnóstico y no se utiliza para certificar. Se repitió la validación con [Node 24 portátil verificado](node-tool-receipt.json), sin modificar la instalación global. `next-env.d.ts` generado por Next se restaura en este worktree; no se cambia el directorio original con 24 modificaciones del usuario.

Los [IDs, manifiestos y orígenes](candidate-images.json) distinguen tres imágenes construidas desde `2539635` y PostgreSQL conservado, construido desde `4e038a1`, con D094 reproducible documentado el 27/10. PostgreSQL no se reconstruye hoy: sus inputs no cambiaron; se vuelve a escanear su **mismo ID** y se usa ese ID en integración. Las bases de vulnerabilidades y Java se actualizan al 02/11, manteniendo Trivy 0.74.0 fijado. Informes completos de todas las severidades en `scan/`; sin supresiones de findings ni transferencia de PASS entre imágenes.

## Residual y nueva propuesta R-20

**R-20 no tiene aceptación vigente.** La aceptación del 19/10 caducó; las propuestas posteriores nunca fueron aprobadas. La [nueva propuesta](R20_PROPOSED_DECISION.json) mantiene **`accepted: false`**, con caducidad propuesta **09/11/2026 00:00 Europe/Madrid (UTC+01:00) = 08/11/2026 23:00 UTC**. No es una fecha académica ni una autorización. Sin renovación automática ni transferencia a futuras imágenes/CI.

El [conjunto conservador actual](CVE_COMPARISON.json) contiene **23 CVE / 21 H/C**, más dos GnuTLS previamente seguidas por debajo de ese umbral. No representa el inventario completo: hay 192 CVE distintos de todas las severidades y nueve identificadores adicionales de advisories/aliases. Los informes raw siguen visibles.

La reducción desde 25 no se atribuye a upgrades inexistentes: Debian declara **no afectados** los tres avisos util-linux anteriores sobre el Bookworm 2.38 instalado. [Clasificaciones oficiales](VENDOR_APPLICABILITY_CHANGES.json). Se añade **CVE-2026-119373**, High en `libsqlite3-0` de PostgreSQL, sin fix Bookworm publicado. Requiere SQLite con FTS5, controlar una tabla con `detail=none`/rowids negativos y ejecutar una consulta prefijo MATCH. La API revisada utiliza PostgreSQL y no ofrece entradas SQLite/SQL crudo; `ldd postgres` no muestra SQLite y no se encuentran CLI sqlite3/python3 en esta inspección. La biblioteca sí está instalada: **no se declara inexplotable ni se descartan rutas nativas/de operador**. [Debian](https://security-tracker.debian.org/tracker/CVE-2026-119373), [matriz individual](CVE_APPLICABILITY_REVIEW.json), [inspección](postgres-sqlite-inspection.txt), [versión del paquete](postgres-sqlite-package.txt).

Toda aceptación exige decisión explícita del propietario para estos cuatro índices, alcance local/sintético y revisión de las condiciones. Cualquier fix publicado para un residual H/C, cambio de inputs/exposición/datos o nuevo hallazgo invalida el corte. El agente no aprueba el riesgo ni cierra A-S3.

## Dos disposiciones de desarrollo separadas

- **R-23 / braces:** el advisory oficial fue retirado el 23/10. El registro histórico OPEN/NO FIX AVAILABLE permanece intacto; se propone disposición de cierre **por advisory retirado, no por paquete corregido**, sin actualización especulativa. [Revisión oficial y propuesta independiente](R23_REVIEW.json). No se marca automáticamente cerrado. [Advisory](https://github.com/advisories/GHSA-vfj7-8cjw-p6xm).
- **Minimatch:** un advisory Moderate aparece propagado a 16 entradas de tooling ESLint. Producción: 0; H/C: 0. No se aplica la sugerencia de npm que cambiaría mayores/downgradearía herramientas. Se conserva el detalle en `npm-audit-full.json`, con revisión de uso de globs confiables en el mantenimiento posterior; no se incluye como residual aceptado de R-20 ni se oculta con una allowlist. [Advisory](https://github.com/advisories/GHSA-g3hv-75wg-m249).

## Qué hacer con las cuatro PR

[Revisión por SHA y check](PR_REVIEW.json). **No fusionar ninguna por separado ahora.** Preparar una única PR acotada de esta rama, revisarla y exigir sus checks actuales. #83, #84 y #85 quedan cubiertas por este reemplazo; #81 añade un lote general innecesario y su verde corresponde al 26/10. Después de integrar y verificar el reemplazo, cerrar las cuatro como sustituidas, sin fusionar sus ramas. No se han cerrado, fusionado o descartado alertas por herramienta durante esta revisión.

## Recorrido viable hasta el MVP local

1. Revisar/publicar el reemplazo, comprobar CI del HEAD exacto y fusionar por el procedimiento habitual; verificar SHA/árbol de main y el recálculo de las nueve alertas.
2. Resolver expresamente R-20 para los cuatro artefactos conservados, incluida SQLite, y la disposición independiente R-23. La aprobación residual no es un nuevo escaneo ni autorización AWS.
3. Con decisión aplicable y preflight, ensayar el conjunto Docker exacto: cuatro roles, DRC-AOA-001, Flyway V1–V3, persistencia y readiness/recovery. La integración actual no sustituye ese ensayo de defensa.
4. Congelar SHA, digests, evidencias/capturas y trazabilidad; revisión formal de cierre A-S3 para esta versión.
5. A-S4: memoria final con actualización de seguridad, doce minutos medidos de ensayo humano en inglés, grabación narrada con duración/hash/QA y revisión académica. Comprobar DoD antes de declarar TFG-A COMPLETE. AWS queda como fase posterior independiente, bloqueada.

Se preservan todas las evidencias y receipts anteriores. No se realiza otra limpieza de imágenes, volúmenes o cachés en esta operación; los artefactos anteriores se conservan como evidencia/rollback hasta integrar el candidato y decidir su retención.
