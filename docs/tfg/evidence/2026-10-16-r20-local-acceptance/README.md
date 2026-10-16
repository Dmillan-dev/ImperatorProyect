# R-20 — Aceptación temporal local del 16/10/2026

**ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC.** El responsable ha aprobado la propuesta de 26 CVE para los cuatro índices y manifiestos exactos. Caducidad exclusiva: **19/10/2026 00:00 Europe/Madrid (UTC+02:00), 18/10/2026 22:00 UTC**. Sin renovación ni transferencia automática.

- [Decisión registrada](R20_ACCEPTED_DECISION.json), [aprobación del usuario](owner-approval.txt) y [propuesta original, accepted:false](scan/R20_PROPOSED_DECISION.json).
- [Revalidación independiente 16/10](scan/README.md): 0 H/C corregibles y 0 secretos en las cuatro imágenes; Gitleaks del checkout 0. La aprobación no ejecuta otro escaneo.
- 26 CVE conservadores / 137 identidades actuales: 24 CVE H/C y dos bajo ese umbral. El GnuTLS conocido aparece Low en Debian y Medium en Ubuntu; no se reduce artificialmente su clasificación. Los 32 nuevos bindings Medium/Low del informe completo permanecen visibles.
- CVE-2026-53615 se reclasificó Medium → High. La revisión individual y el estado vulnerable sin paquete corregido de Bookworm están documentados; existe una corrección upstream. Riesgo condicionado, no declarado inexplotable.

Alcance exclusivamente TFG-A local y datos sintéticos. Se excluyen AWS, producción, datos reales, exposición pública y ampliación del producto. Mantener 0 H/C corregibles y 0 secretos. Cambios de digest, configuración/exposición, datos o hallazgos/fixes invalidantes requieren nueva revisión y decisión.

R-23 sigue OPEN y separado. A-S3 conserva su cierre histórico; A-S4 y TFG-A continúan IN PROGRESS por ensayo oral humano, grabación narrada y revisión académica. Esta decisión no autoriza AWS ni declara el TFG terminado.

La propuesta y los informes de scan/ se copian byte por byte, junto con su manifiesto original. Los recibos y decisiones anteriores permanecen intactos. La fecha exacta de mensaje del usuario no se inventa: se conoce el día local y se registra por separado el momento UTC de inscripción.
