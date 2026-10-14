# A-S4 — Ensayo técnico local y memoria, 14/10/2026

Fuente ejecutada: `30a03dab7d26c1ddf3b30c4ea052614efe39c405`. La PR [#72](https://github.com/Dmillan-dev/ImperatorProyect/pull/72) integra exclusivamente documentos y registra R-20/A-S3 en `adbe45eabb3a79807ec0e55c791006927dcb3f64`; no reconstruye estas imágenes.

## Evidencia medida

- [Flujo real API/UI](rehearsal-result.json): caso sintético, cuatro roles, 30 evidencias, aprobación UI, implementación/validación mediante API y lectura UI, tres hechos Ledger; 19.440 EUR estimados, 18.960 EUR realizados simulados y −480 EUR de variación.
- [Identidad inicial](runtime-image-bindings.json) y [persistencia](persistence.json): mismos índices aceptados y capas, recreación de tres contenedores, 30 evidencias y tres hechos conservados. Flyway V1–V3 y SELECT/INSERT permitidos, UPDATE/DELETE denegados para Ledger y tablas V3.
- [Recuperación](readiness-recovery.json): readiness 503 durante caída de PostgreSQL, liveness 200, recuperación a 200 sin pérdida de datos.
- [Capturas](capture/rehearsal.json) y [clip técnico](capture/tfg-a-docker-demo.webm), con [QA](capture/video-qa.json). El clip es breve y **sin audio**: no constituye una defensa humana de doce minutos.
- [Memoria PDF](IMPERATOR_Memoria_TFG_Local.pdf), 29 páginas, portada sin tutor ni fecha de entrega; [texto editable](../../08_LOCAL_MVP_MEMORY.md) y [guion inglés](../../09_DEFENSE_SCRIPT_12_MIN.md). La maquetación está revisada; la aprobación académica está pendiente.

R-20 está aceptado para los cuatro índices del [registro independiente](../2026-10-14-r20-acceptance/R20_ACCEPTED_DECISION.json), hasta 16/10/2026 00:00 Europe/Madrid, exclusivamente local/sintético. Los escaneos están en ese pack: 0 H/C corregibles y 0 secretos. Se conserva la matriz aceptada de 25 CVE; el escaneo nuevo clasifica 23 H/C y dos LOW. No se declaran corregidos ni inexplotables. R-23 sigue OPEN independiente. Las imágenes de CI no heredan esta aceptación.

Las acciones de navegador fueron automatizadas con Playwright contra servicios reales y actores sintéticos. La aprobación UI representa el comando del rol asignado en la prueba; no acredita participación manual de una persona ni un ensayo oral humano.

## Controles de publicación

Se han seleccionado únicamente resultados públicos y material sintético. No se publican contraseñas, claves RSA, truststores, JWT, configuración privada ni bases de datos. El ensayo utilizó un proyecto/volumen nuevos, proveedores desactivados y `--no-build --pull never`. Se retiraron exclusivamente sus seis contenedores Compose y el fixture identificado por ID/montaje; el volumen sintético y las imágenes se conservaron. [Verificación de limpieza](cleanup.json). No hubo cambios de código, dependencias, Docker, workflows ni AWS.

La generación opcional de narración automática no se ejecutó: Windows rechazó el script por política de ejecución. No se eludió la política y no existe una grabación narrada terminada.

## Pendientes humanos

Medir la presentación oral en inglés, grabar pantalla y voz, verificar audio/imagen/ausencia de secretos, anexar duración/hash y obtener revisión académica. A-S4 y TFG-A permanecen **IN PROGRESS**; AWS permanece **BLOCKED**. La duración planificada de 720 segundos del guion no es una duración medida.
