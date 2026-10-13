# IMPERATOR - versión post-merge de PR #64

Revisión: 13/10/2026, Europe/Madrid. Registro técnico; no aprobación de release ni renovación de R-20.

## Fuente y checkout

- PR #64 ya fusionada por Dmillan-dev a las 12:57 Europe/Madrid. No se efectuó una segunda fusión.
- Main/origin/main: `0797671e06baa2b20f8ce5a842ac04140d84e591`.
- Checkout aislado: `D:/Imperator/build/worktrees/tfg-a-main-20261013`, detached HEAD fijado al SHA de main. Status y staging vacíos.
- Árbol Git: `ac2179bdecd690a374b60fdef468196032178474`, idéntico al candidato probado `a931601102ee19a90b3dad8477408ac0d1854669`.
- Diff de integración: únicamente PostgreSQL Dockerfile, supply-chain-lock.json y scripts/verify-postgres-runtime-image.ps1; 6 inserciones/6 eliminaciones; diff --check exit 0.
- Código, dependencias e inputs de backend/frontend/migration coinciden con 6e79e83. Los cambios de construcción afectan PostgreSQL; su imagen local conservada procede de a931601.

## CI nueva del SHA de main

Los 10 checks recuperados están completed/success, ejecutados el 13/10. No se reutiliza el PASS del 09/10 ni se cuentan checks inexistentes.

- [Java CI](https://github.com/Dmillan-dev/ImperatorProyect/actions/runs/38386844673)
- [Security](https://github.com/Dmillan-dev/ImperatorProyect/actions/runs/38386844306)
- [CodeQL](https://github.com/Dmillan-dev/ImperatorProyect/actions/runs/38386844372)

Artefacto D094 11807416272 recuperado: SHA-256 `a6fe8b9262a83f102977fd55090929f7177729bd5a6a98eacdbb877fc63cf2f7`, coincide con GitHub. Los 10 hashes internos de primera/rebuild coinciden.

## Conjunto local conservado para revisión de defensa

| Componente | ID/índice local exacto | Origen de construcción | ID, origen y capas frente al runtime histórico |
|---|---|---|---|
| migration | `sha256:c53c77aeba010ddfa1538aa75a62b20e5d826baba2869cf50b06c86512a2b8b4` | `6e79e83` | PASS |
| backend | `sha256:298f4094799ca9c95e0a72ee65c04d19d42950cddcda2c0f5e9810c1bcc44757` | `6e79e83` | PASS |
| frontend | `sha256:ec95fa1037974181cc6cb30041d82f4d888a056605c9a405899112c3842a9a78` | `6e79e83` | PASS |
| postgres | `sha256:3cb24d97e30a53f79de131660d8f0fcdb62f3cbc205c7b3f1710a0415834cfd9` | `a931601` | PASS |

Se inició únicamente Docker Desktop para el inventario. No se ejecutaron contenedores ni se descargaron/reconstruyeron/re-etiquetaron imágenes; cero contenedores en ejecución al consultar. Estos artefactos mantienen el ensayo histórico del 09/10, no una nueva ejecución funcional.

Los 190/190 archivos del catálogo del candidato conservan sus hashes. Se mantienen separados el código versionado, las imágenes conservadas y las evidencias locales todavía no consolidadas en main.

## Diferencia de artefactos de CI

PostgreSQL de CI no es la imagen local del ensayo:

- Índice local: `sha256:3cb24d97e30a53f79de131660d8f0fcdb62f3cbc205c7b3f1710a0415834cfd9`.
- Manifest runtime local: `sha256:c22d3892c45e412fbdb36d4e17d4580359be3bba87873710cceeeaf777b68717`.
- Índice CI (rebuild): `sha256:e2823c6a61ee528b766e7781b38bc958ba64e522e781f7b881d9f36508d35364`.
- Manifest runtime CI: `sha256:3900d5a0f3511bc05775e8a84eaca4f06cd32c3fbeaeabefc0429db8c418be4b`.

Las dos construcciones de CI reproducen el mismo manifest runtime entre sí; sus índices difieren. Los inputs fijados coinciden con la construcción local y el SBOM coincide en 153/153 componentes comparados por nombre, versión y PURL. Esto no demuestra igualdad binaria o de todas las capas. Revision y SOURCE_DATE_EPOCH cambian; los hashes de Dockerfile/lock coinciden al distinguir blob LF de checkout Windows CRLF. No se modifican bytes para ocultar estas diferencias.

El escaneo D094 de CI presenta cero hallazgos bajo HIGH/CRITICAL y --ignore-unfixed. No acredita ausencia de CVE residuales ni cero secretos en todas las severidades. Su indicador formalReleaseEligible es elegibilidad técnica D094; no aprobación académica/R-20.

## Estado y siguiente control

Correspondencia fuente -> candidato probado: PASS. Identidad de cuatro imágenes locales -> runtime histórico: PASS. Equivalencia binaria imagen CI -> runtime local: NO, identidades diferentes documentadas. Nueva validación funcional de las imágenes de CI: no realizada.

Siguiente paso: revalidar Trivy completo y secretos sobre los cuatro IDs conservados; comparar residuos con la matriz anterior; presentar una decisión R-20 vigente para esos digests. R-20 sigue accepted:false en su propuesta del candidato; la antigua autorización caducó el 09/10 y el 16/10 es una propuesta, no aprobación. R-23 sigue abierto independientemente.

A-S3 no se declara cerrado. A-S4 permanece pendiente y AWS bloqueado.

D:/Imperator conserva HEAD 69e4b90, rama docs/tfg-a-s3-ci-review, sus 24 entradas de archivos modificados en status y dos entradas sin seguimiento. Git diff distingue 15 cambios de contenido; esa diferencia de recuento no significa que se hayan limpiado archivos. El staging continúa vacío. No hubo reset, stash, rebase, limpieza ni commit adicional.

Resultados estructurados: current-main.json, main-ci.json, artifact-download.json, source-artifact-correspondence.json, local-image-inventory.json y final-verification.json, en este mismo directorio.
