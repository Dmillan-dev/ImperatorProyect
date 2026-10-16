# R-20 — Revalidación exacta y propuesta de decisión

Fecha: 16/10/2026, Europe/Madrid. Fuente local congelada: `94ce8799812ecde83ebb4e0b2698bfab5e9091d8`. Operación autorizada: revalidar cuatro índices locales, comparar residuales y presentar decisión. **No es una aprobación, renovación ni ejecución del MVP.**

## Resultado técnico medido

| Imagen | CVE H/C únicos | Filas H/C sin fix de rama | Filas H/C corregibles | Secretos |
| --- | ---: | ---: | ---: | ---: |
| migration | 0 | 0 | 0 | 0 |
| backend | 0 | 0 | 0 | 0 |
| frontend | 12 | 57 | 0 | 0 |
| postgres | 24 | 74 | 0 | 0 |

Agregado sin duplicar CVE: **24 H/C**, 131 filas, **0 H/C corregibles y 0 secretos**. Gitleaks sobre el checkout actual: **0**. PASS técnico; el gate formal para otra ejecución sigue **NO-GO hasta decisión explícita**. Los CVE por imagen no se suman para obtener únicos globales.

Trivy 0.74.0, imagen fijada por digest, con bases descargadas el 16/10. DB de vulnerabilidades actualizada a 01:13:54 UTC; Java DB a 01:00:20 UTC. Escaneos completos de vulnerabilidades y secretos, todas las severidades, ignore file vacío, sin ignore-unfixed ni supresiones nuevas. El análisis define corregible mediante FixedVersion de la rama instalada; no significa que una corrección upstream sea imposible. [Informes y deltas](SECURITY_REVALIDATION.json), [comandos/horas/exit codes](scan-commands.json), [metadatos DB](database-metadata.json).

## Identidad exacta

| Componente | Índice OCI / ID local |
| --- | --- |
| migration | `sha256:c53c77aeba010ddfa1538aa75a62b20e5d826baba2869cf50b06c86512a2b8b4` |
| backend | `sha256:298f4094799ca9c95e0a72ee65c04d19d42950cddcda2c0f5e9810c1bcc44757` |
| frontend | `sha256:ec95fa1037974181cc6cb30041d82f4d888a056605c9a405899112c3842a9a78` |
| postgres | `sha256:3cb24d97e30a53f79de131660d8f0fcdb62f3cbc205c7b3f1710a0415834cfd9` |

IDs, origen declarado y RootFS coinciden con la entrega del 14/10. Los manifiestos de plataforma quedan ligados por la evidencia previa de esos índices inmutables y constan en la propuesta. No se han descargado, reconstruido, etiquetado ni eliminado imágenes. No se han sustituido por imágenes de CI. [Inventario](image-inventory.json).

## Comparación con el 14/10

- Los **119 bindings originales de los 25 CVE** siguen reportados; no se eliminan de la matriz por un cambio de severidad o de fecha.
- **CVE-2026-53615** ya estaba en los informes con severidad Medium. Ahora Trivy lo clasifica High en frontend y PostgreSQL: ocho paquetes del source package util-linux por imagen, dieciséis filas. Es un CVE reclasificado, no dieciséis CVE nuevos.
- La propuesta conservadora pasa de **25 a 26 CVE**. Los informes actuales tienen 24 H/C; los otros dos quedan por debajo del umbral H/C. CVE-2026-88647 es Low en Debian pero también aparece ahora como Medium en backend/migration Ubuntu; CVE-2026-88648 conserva sus observaciones Low en Debian. No se describen ambos como Low globalmente.
- La matriz candidata recoge **137 identidades actuales** de paquete/versión para esos 26 CVE: 119 originales, dieciséis de la reclasificación y dos observaciones Medium adicionales del CVE GnuTLS ya conocido.
- También hay **32 nuevas filas por debajo de High** en los informes completos: dieciséis Medium y dieciséis Low. Se conservan en [el delta de severidad inferior](BELOW_GATE_DELTA_REVIEW.json); no se declara revisada individualmente la explotabilidad de cada una, ni se ocultan por el PASS técnico. Dos de esas filas son las observaciones GnuTLS incluidas en las 137 identidades.
- No se observa una nueva versión corregida para los bindings originales aceptados. Los paquetes y capas de las imágenes permanecen iguales; cambia la información de la base del escáner.

## Revisión individual de CVE-2026-53615

El [advisory upstream](https://github.com/util-linux/util-linux/security/advisories/GHSA-h4rw-gv36-wmp5) identifica un desbordamiento en el procesamiento DOS/EBR de libblkid y una corrección en 2.41.4. El [tracker Debian](https://security-tracker.debian.org/tracker/CVE-2026-53615) mantiene Bookworm 2.38.1-5+deb12u3 vulnerable; no lista una versión corregida de Bookworm. Trixie tiene versión corregida. Esto no autoriza cambiar distribución ni importar paquetes de otra rama.

La explotación requiere hacer que el parser examine una imagen de particiones manipulada. Un fichero regular puede bastar. La alteración directa es de metadatos detectados; el impacto destructivo requiere un consumidor posterior. No se ejecutó PoC ni se procesaron discos.

En el código/configuración revisados, la importación recibe UTF-8 NDJSON/JSON y metadatos acotados. No se han identificado llamadas directas de aplicación a herramientas de particionado. Compose no pasa dispositivos de bloque ni usa modo privilegiado; frontend carece de capacidades y PostgreSQL recibe solo las cinco ya documentadas, sin SYS_ADMIN/SYS_RAWIO. Estos controles limitan rutas ordinarias e impacto posterior; **no impiden por sí mismos el parser vulnerable**, que un actor local o comprometido podría invocar sobre un archivo manipulado. No existe prueba completa de ausencia de llamadas transitivas nativas.

Resultado: **riesgo residual condicionado, paquete afectado presente, no declarado inexplotable**. [Revisión estática/hash de fuentes](CVE_2026_53615_SOURCE_REVIEW.json), [matriz de aplicabilidad](CVE_APPLICABILITY_COMPARISON.json), [estado del proveedor](VENDOR_REVIEW.json). No se ha identificado un parche normal de Bookworm que aplicar en esta revisión. Un backport propio, rebuild o cambio de rama requiere una intervención y autorización separadas; no se ha aplicado.

## Decisión propuesta — pendiente del responsable

La [propuesta estructurada](R20_PROPOSED_DECISION.json) tiene **accepted: false**, aprobador y fecha de aprobación nulos.

Se propone aceptar temporalmente los **26 CVE conservadores** documentados para estos **cuatro índices y manifiestos exactos**, exclusivamente **TFG-A local y datos sintéticos**, hasta **19/10/2026 00:00 Europe/Madrid (UTC+02:00), equivalente a 18/10/2026 22:00 UTC**.

Condiciones: mantener 0 H/C corregibles y 0 secretos; no exposición pública; ninguna transferencia a otras imágenes ni renovación automática. AWS, producción y datos reales quedan excluidos. Cambios de digest/configuración/datos/exposición, nuevos hallazgos, disponibilidad de fix invalidante o caducidad exigen parar y revisar. R-23 sigue independiente y OPEN. Los hallazgos inferiores al umbral permanecen visibles bajo la política existente; no existe una declaración general de inocuidad.

La aprobación anterior de 25 CVE está caducada y permanece intacta como registro histórico. Para esta propuesta es necesaria **otra decisión explícita** que incluya el CVE reclasificado. Si no se aprueba, se mantiene NO-GO para nueva ejecución; se puede continuar revisando offline el material académico registrado.

## Efecto sobre el cierre local

A-S3 conserva su cierre histórico. A-S4 y TFG-A permanecen IN PROGRESS: faltan ensayo oral humano medido en inglés, grabación de pantalla y voz, QA y revisión académica. La eventual aprobación R-20 solo desbloquea su gate acotado; no cierra por sí sola el TFG ni autoriza AWS.

## Alcance y conservación

Solo se ejecutaron contenedores de la herramienta de escaneo, retirados con --rm; el BuildKit antiguo detenido se preservó. No se ejecutaron servicios de IMPERATOR. No hubo cambios de código, dependencias, Dockerfiles, workflows, infraestructura, staging, commit ni push. Las cachés previas y decisiones/evidencias originales se conservan. El nuevo caché del escáner permanece en build y no se incluye en este paquete.
