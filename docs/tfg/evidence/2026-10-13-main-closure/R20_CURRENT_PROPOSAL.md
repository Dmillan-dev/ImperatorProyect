# R-20 — Propuesta actual, pendiente de decisión explícita

Fecha de revisión: **13/10/2026**. Responsable de decidir: **Daniel Millan Perez**, propietario del proyecto. La revisión académica del tutor es independiente. Estado: **PROPOSED / accepted: false**. [Registro exacto](R20_CURRENT_PROPOSAL.json).

El merge de #68 confirma la integración documental; no renueva la aceptación que caducó el 09/10/2026 a las 00:00 Europe/Madrid. Tampoco la propuesta anterior, relativa a 23 CVE, acepta los 25 actuales.

## Artefactos cubiertos por la propuesta

Fuente integrada: `c69f682e190e887549a5a864ff688c6d53941fa7`. Runtime histórico validado: 09/10/2026 sobre candidato a931601 y los artefactos con sus orígenes indicados. No se relabelan como compilados desde el nuevo main. Los cuatro manifiestos de plataforma están fijados en el JSON.

| Servicio | Índice OCI local exacto | SHA de construcción |
| --- | --- | --- |
| migration | `sha256:c53c77aeba010ddfa1538aa75a62b20e5d826baba2869cf50b06c86512a2b8b4` | `6e79e83` |
| backend | `sha256:298f4094799ca9c95e0a72ee65c04d19d42950cddcda2c0f5e9810c1bcc44757` | `6e79e83` |
| frontend | `sha256:ec95fa1037974181cc6cb30041d82f4d888a056605c9a405899112c3842a9a78` | `6e79e83` |
| postgres | `sha256:3cb24d97e30a53f79de131660d8f0fcdb62f3cbc205c7b3f1710a0415834cfd9` | `a931601` |

Las imágenes construidas por CI sobre c69f682 son artefactos diferentes y **quedan fuera de esta propuesta**. La igualdad de entradas/SBOM no transfiere su aceptación ni demuestra igualdad binaria.

## Criterios medidos y límites

Escaneos completos conservados del 13/10: **0 High/Critical corregibles, 0 secretos de cualquier severidad, 25 CVE residuales / 119 filas paquete-imagen**. Backend y migración no tienen residuo H/C; frontend presenta 13 CVE y PostgreSQL 25, con solapamiento. Trivy 0.74.0 y sus bases del 13/10 están identificados; el motor histórico era 0.75.0. No se ocultaron findings ni se inventó un fix.

Los 23 anteriores conservan sus bindings; la [revisión por CVE](current-cve-review.json) enlaza sus evaluaciones históricas, sin presentar los probes del 09/10 como nuevos. Los dos añadidos afectan `libgnutls30` `3.7.9-2+deb12u7` en frontend y PostgreSQL:

- [CVE-2026-88647](https://security-tracker.debian.org/tracker/CVE-2026-88647): validación del nombre del certificado; requeriría alcanzar la ruta afectada con un certificado controlado por el atacante.
- [CVE-2026-88648](https://security-tracker.debian.org/tracker/CVE-2026-88648): restricciones X.509 entre dominios; requeriría alcanzar la ruta afectada con una CA subordinada controlada por el atacante.

Debian sigue indicando Bookworm vulnerable y sin fix, con prioridad unimportant y una discrepancia de política upstream. Esta calificación no elimina la observación High de Trivy. El paquete está instalado; su ruta vulnerable ejecutable desde IMPERATOR **no está demostrada ni descartada**.

Mitigaciones revisadas: exposición loopback, backend/base internos, usuarios restringidos, límites de recursos, RBAC, datos sintéticos y proveedores externos desactivados. No son parches y no cubren un host comprometido ni garantizan inexplotabilidad. Los detalles M1–M6 y sus límites se conservan en el JSON.

## Decisión propuesta

Aceptar **temporalmente** ese riesgo residual condicionado exclusivamente para **TFG-A local y datos sintéticos**, vinculado a los cuatro índices y manifiestos. Excluir AWS, producción, datos reales, exposición pública, clientes y ampliación del producto. R-23 sigue **OPEN / NO FIX AVAILABLE** y no se cierra ni se integra en R-20. No se autoriza ningún upgrade.

Caducidad exclusiva propuesta: **16/10/2026 00:00 Europe/Madrid (UTC+02:00)**, equivalente a **15/10/2026 22:00 UTC**. Es una propuesta, no una autorización vigente. Sin renovación automática ni transferencia a otras imágenes. Si el ensayo no se completa antes, hará falta otra decisión explícita; no se desplazará la fecha por iniciativa del agente.

Invalidación anticipada: cambio de digests/configuración/exposición/alcance; cualquier H/C corregible o secreto; cambio en la matriz residual o publicación de un fix aplicable. Verificar vigencia y condiciones antes del ensayo, no solo antes de editar documentos.

## Resultado que falta

La revisión técnica está preparada. La decisión del propietario, con alcance y caducidad explícitos, sigue **PENDIENTE**. No existe aprobación vigente por inferencia de “continuar”, merge, CI o este documento. A-S3 permanece pendiente de ese gate; A-S4 no tiene ensayo final autorizado por esta propuesta.
