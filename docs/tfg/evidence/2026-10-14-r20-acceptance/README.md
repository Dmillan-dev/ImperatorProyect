# R-20 Aceptación temporal del 14 de octubre de 2026

El propietario Daniel Millan Perez ha aprobado explícitamente la propuesta vigente. Estado **ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC**. [Decisión exacta](R20_ACCEPTED_DECISION.json) y [mensaje de aprobación](owner-approval.txt). El mensaje confirma la propuesta presentada en la conversación, incluida su caducidad; no se inventa una hora de aprobación.

## Condiciones vinculantes

- Exclusivamente TFG-A local con datos sintéticos. AWS, producción, datos reales, proveedores AWS, exposición pública, clientes y ampliaciones permanecen fuera de alcance.
- **25 CVE residuales / 119 filas** de los informes completos del **13/10/2026**. Aceptados como riesgo residual condicionado, sin declararlos corregidos o inexplotables.
- **0 High/Critical corregibles y 0 secretos** siguen siendo gates obligatorios. R-23 sigue **OPEN / NO FIX AVAILABLE**, con su disposición independiente.
- Caducidad exclusiva **16/10/2026 00:00 Europe/Madrid (UTC+02:00)** = **15/10/2026 22:00 UTC**. Sin renovación automática ni transferencia a imágenes futuras o de CI.
- Se mantienen las invalidaciones de la propuesta: cambio de digest/configuración/exposición/datos, nuevos findings o fix disponible, secretos, incumplimiento de gates o caducidad.

## Identidad aceptada

| Servicio | Índice OCI exacto | Origen de construcción |
| --- | --- | --- |
| migration | `sha256:c53c77aeba010ddfa1538aa75a62b20e5d826baba2869cf50b06c86512a2b8b4` | `6e79e83` |
| backend | `sha256:298f4094799ca9c95e0a72ee65c04d19d42950cddcda2c0f5e9810c1bcc44757` | `6e79e83` |
| frontend | `sha256:ec95fa1037974181cc6cb30041d82f4d888a056605c9a405899112c3842a9a78` | `6e79e83` |
| postgres | `sha256:3cb24d97e30a53f79de131660d8f0fcdb62f3cbc205c7b3f1710a0415834cfd9` | `a931601` |

Los manifiestos de plataforma y límites M1-M6 están en el JSON. La fuente de entrega `30a03da` solo difiere documentalmente de la fuente de la propuesta `c69f682`: no se relabelan las imágenes como construidas desde main ni se incorporan nuevos artefactos de CI.

## Qué registra esta decisión

La aprobación **no constituye un nuevo escaneo** ni una corrección de CVE. Conserva la [propuesta original accepted:false](../2026-10-13-main-closure/R20_CURRENT_PROPOSAL.json) y las aceptaciones históricas intactas. Una revalidación posterior, si se ejecuta, se registrará separadamente con su fecha y resultados reales. Cierre A-S3, ensayo final A-S4 y aprobación académica requieren sus propios resultados. TFG-A permanece **IN PROGRESS**.
