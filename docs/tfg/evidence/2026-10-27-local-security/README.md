# Candidato de seguridad local — 27/10/2026

Parche autorizado desde `main` limpio `35e43ea2829f43c4f48bd80a40a1b62bdf57129a`, fijado en **`64c38c22a04f63d5cf38712064a040db0cc8bdf1`**. Exclusivamente local y sintético; AWS, producción y datos reales siguen bloqueados. Esta evidencia no renueva R-20 ni cierra A-S3, A-S4 o TFG-A.

## Cambios acotados

- Next 16.3.8 → 16.4.2 y eslint-config-next compatible; Zod 4.6.5 → 4.6.7; Vitest/coverage 4.1.11 → 4.1.12. [Cambios completos del lockfile](lockfile-changes.json): únicamente familias Next/Vitest/Zod; Chai se conserva en 6.2.2, igual que main, y no se incorpora el lote general #81.
- Node 24.20.0 → 24.22.1 Bookworm, mismo cambio y digest de #82 en las tres etapas. [Release de seguridad oficial](https://nodejs.org/en/blog/release/v24.22.1). No se ha fusionado ni cerrado #82.
- PostgreSQL 18.6 y gosu Go 1.26.10 se mantienen. Solo `libssl3`/`openssl` 3.0.22-1~deb12u2, fijados por checksum, con comprobación de instalación y D094. [Estado Debian de CVE-2026-84782](https://security-tracker.debian.org/tracker/CVE-2026-84782).
- La revisión de Zod verifica que el arreglo upstream evalúa límites declarados antes de parsear elementos. Las páginas se limitan a 100, igual que `PageRequest` del backend. El cliente cancela el stream al superar 1 MiB, incluso sin Content-Length fiable. Las demás colecciones no reciben máximos de negocio inventados: siguen limitadas por ese presupuesto de bytes, sin afirmar ausencia universal de agotamiento de recursos. [Cambio upstream](https://github.com/colinhacks/zod/pull/6652).

No se modifica Maven, Java, Spring, PostgreSQL mayor, arquitectura, workflows, Terraform, AWS, Keycloak o Bedrock. Se preservan las decisiones, evidencias y receipts anteriores.

## Validación de este candidato

| Control | Resultado y alcance |
| --- | --- |
| Backend unitario | 174 PASS |
| Integración | 35 PASS; Flyway migrate/validate/idempotencia y pruebas REST/RBAC/repositorios sobre el nuevo ID PostgreSQL, base temporal en tmpfs, solo loopback |
| Frontend | 44 PASS; lint, tipos y build PASS |
| Playwright | 9 PASS / 6 omisiones previstas; API interceptada, no ensayo completo del conjunto Docker |
| npm audit producción | 0 vulnerabilidades |
| npm audit completo / Trivy con desarrollo | High de undici pendiente, separado; no se declara PASS global |
| Trivy filesystem, política CI | PASS: 0 H/C corregibles, secretos y misconfiguraciones H/C; política predeterminada no incluye dependencias de desarrollo |
| Gitleaks snapshot Git / historial | 0; 169 commits revisados, no se presupone CI remota |
| Cuatro imágenes candidatas | 0 H/C corregibles y 0 secretos en cada una, con Trivy 0.74.0 y bases actuales verificadas |
| D094 | Primera construcción y repetición sin caché PASS; mismo manifiesto runtime `sha256:da702e73fa2ca2388321f90903ff640e938092283afb636f3a3a71fd22453034`; los índices de attestations difieren y se conservan separados |
| CI GitHub de la nueva rama | PENDING publicación y ejecución; los resultados locales no son un PASS de GitHub |
| Ensayo completo Docker del conjunto | PENDING; no se transfieren persistencia/recovery ni capturas del ensayo histórico |

El intento inicial de integración falló por el nombre de base temporal `imperator_it`, que no cumplía el prefijo `imperator_it_` requerido. Se conservan [el fallo inicial](integration-initial/integration-result.json) y [la repetición correcta](integration-retry/integration-result.json), sin cambios de código. Los contenedores y contraseñas temporales se retiraron; ningún volumen histórico fue usado o borrado.

Gitleaks sobre todo el directorio generado encontró 18 coincidencias en claves de desarrollo/build de Next dentro de `.next`, fuera de Git. El snapshot versionado y el historial pasan por separado. Los informes redactados del directorio completo permanecen como diagnóstico local en `output/tfg/security-20261027`; no se declara que los cachés de desarrollo sean material publicable. Las imágenes exactas pasan su propio escaneo de secretos.

## Identidad de los artefactos

Los [cuatro índices, manifiestos runtime y capas exactos](candidate-images.json) se relacionan con el SHA en [SOURCE_ARTIFACT_CORRESPONDENCE.json](SOURCE_ARTIFACT_CORRESPONDENCE.json). Frontend se construye desde el snapshot Git final `64c38c2`. PostgreSQL conserva el índice construido y validado en el padre `4e038a1`, con sus dos construcciones D094: la corrección posterior solo conserva Chai en el lockfile frontend y no cambia ningún input PostgreSQL. Los 174 unitarios y 35 de integración se ejecutaron en `4e038a1`, con el mismo código backend y el mismo ID PostgreSQL del candidato final; el frontend se revalidó tras conservar Chai. Backend y migration conservan sus IDs históricos: sus fuentes ejecutables, pom, wrapper, migraciones y Dockerfiles coinciden; solo hay diferencias documentales en el README backend. Reutilizarlos no significa haberlos reconstruido con el nuevo SHA.

Los informes completos [Trivy por imagen](summary-candidate.json) retienen todas las severidades y se vinculan al ID y capas inspeccionados. La base de vulnerabilidades se obtuvo de GHCR tras un fallo DNS del mirror, con digest OCI verificado desde el host; [receipts de descarga](database-download-receipts.json). No se actualiza el scanner ni se utilizan supresiones para conseguir el resultado.

## Nueva decisión R-20: propuesta, no aceptación

[R20_PROPOSED_DECISION.json](R20_PROPOSED_DECISION.json) permanece **`accepted: false`**, propietario Daniel Millan Perez. Propone caducidad exclusiva **03/11/2026 00:00 Europe/Madrid (UTC+01:00)**, equivalente a **02/11/2026 23:00 UTC**, sin renovación automática ni transferencia a futuras imágenes. Es una ventana propuesta de revisión, no una fecha de entrega ni una aprobación vigente.

La [comparación](CVE_COMPARISON.json) registra 25 CVE conservadores / 137 asociaciones de paquete: 23 H/C y dos CVE previamente seguidos por debajo del umbral. El inventario completo contiene 189 CVE distintos de todas las severidades; **25 no es el total de Trivy**. CVE-2026-84782 sale del conjunto; no hay CVE H/C nuevos. Aparecen dos asociaciones Medium de CVE-2026-88648 con GnuTLS en los mismos IDs Ubuntu de backend/migration, que requieren consideración explícita. La [matriz individual](CVE_APPLICABILITY_REVIEW.json) conserva hipótesis, límites e incertidumbres; ningún residual se declara inexplotable ni aceptado por el agente. Los 38 archivos de una revisión anterior no se presentan como 38 inspecciones nuevas.

R-23 sigue OPEN / NO FIX AVAILABLE como disposición independiente. No se actualiza `braces` ni se cierra el riesgo por la ausencia de una alerta en este corte.

## Hallazgo independiente de desarrollo

[undici 8.10.2](DEVELOPMENT_FINDING.json), transitiva de jsdom, ya estaba en `main`. El advisory High [CVE-2026-109869](https://github.com/advisories/GHSA-xjj6-f33q-929c) permite provocar un error no manejado desde una respuesta HTTP maliciosa. No figura como paquete en el runtime frontend inspeccionado; el tooling de pruebas sí puede ejecutarlo. Los fixtures locales reducen exposición, sin declarar seguro cualquier uso futuro.

Existe fix **8.12.2** dentro del rango actual de jsdom. Se deja preparado un parche de solo esa entrada del lockfile fuera del worktree, `output/tfg/security-20261027/UNAPPLIED-undici-8.12.2.patch`; **no se aplica**, no se amplía el lote autorizado y no se incluye en la aceptación residual R-20. Requiere una decisión separada. Aplicarlo después cambiaría el SHA/lockfile y obligaría a revalidar los artefactos afectados antes de aceptar sus digests.

## Limpieza y siguiente gate

Se elimina únicamente el índice intermedio `eece53ba…`, primer build PostgreSQL del 22/10, después de demostrar iguales capas, configuración y manifiesto runtime que el índice conservado `19891312…`. [Resultado](docker-cleanup-result.json): por ID completo, sin fuerza/prune, sin borrar volúmenes, cachés, bases o herramientas. Se conservan todos los IDs de defensa histórica, la propuesta anterior y el candidato actual; no se promete espacio recuperado por capas compartidas. Las construcciones nuevas aumentan el almacenamiento total aunque se retire ese intermedio.

El siguiente gate es resolver la disposición de desarrollo, revisar/publicar la rama y obtener CI del SHA, decidir explícitamente R-20 para sus artefactos exactos y completar el ensayo Docker local de cuatro roles, persistencia y recovery. Después procederá la revisión formal A-S3 y A-S4 humana en inglés; **TFG-A IN PROGRESS, AWS BLOCKED**.
