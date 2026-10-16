# A-S4 — Memoria y defensa local

Revisión 16/10/2026. **IN PROGRESS**. A-S3 está cerrado y R-20 aceptado temporalmente para cuatro imágenes locales exactas. La aceptación cubre 26 CVE conservadores y no autoriza AWS. La memoria y el guion están preparados; la defensa humana medida, grabación con voz y revisión académica siguen pendientes.

## Material preparado

- [Memoria editable](08_LOCAL_MVP_MEMORY.md) y [PDF revisado de 29 páginas](evidence/2026-10-14-a-s4-runtime/IMPERATOR_Memoria_TFG_Local.pdf): autor Daniel Millan Perez, DAM; portada sin tutor ni fecha de entrega. El PDF conserva el corte 14/10; adjuntar la [decisión actual](evidence/2026-10-16-r20-local-acceptance/README.md).
- [Guion inglés](09_DEFENSE_SCRIPT_12_MIN.md): diez bloques, 720 segundos planificados. No acredita el tiempo oral de una persona.
- [Ensayo técnico real 14/10](evidence/2026-10-14-a-s4-runtime/README.md): API/UI, cuatro roles, Flyway V1–V3, persistencia y recuperación PASS; [limpieza específica](evidence/2026-10-14-a-s4-runtime/cleanup.json) completada conservando imágenes y volumen sintético.
- [Capturas y clip técnico](evidence/2026-10-14-a-s4-runtime/capture/rehearsal.json), [QA](evidence/2026-10-14-a-s4-runtime/capture/video-qa.json). Clip de 24,64 segundos **sin audio**; respaldo técnico, no defensa final narrada.
- [Trazabilidad](03_TRACEABILITY_MATRIX.md), [mapa DAM](11_DAM_MEMORY_DELIVERY.md), [gate de entrega](10_LOCAL_MVP_RELEASE_GATE.md).

## Preflight para otra ejecución

Consultar la [decisión R-20](evidence/2026-10-16-r20-local-acceptance/R20_ACCEPTED_DECISION.json): aceptación local/sintética hasta **19/10/2026 00:00 Europe/Madrid**, sin renovación automática. Verificar cuatro índices exactos, mitigaciones, 0 H/C corregibles y 0 secretos. Las imágenes de CI y las futuras imágenes no heredan la aceptación. Si caduca o cambia el alcance/artefacto/riesgo, detener la ejecución y solicitar una decisión válida; se puede revisar material histórico offline.

Usar un proyecto aislado, secretos efímeros privados, imágenes existentes, `--no-build --pull never`, loopback, proveedores externos desactivados y volumen sintético. No mostrar JWT, claves, contraseñas, terminal con secretos ni notificaciones. El ensayo 14/10 retiró sus contenedores; no hay una demo viva dejada en ejecución. La configuración genérica de Compose no sustituye el fixture y preflight documentados.

## Trabajo humano pendiente

1. Revisar la memoria y practicar el guion en inglés con cronómetro. Registrar duración real, pausas, desvíos y preguntas; no usar los 720 segundos planificados como medición.
2. Grabar pantalla y **tu narración** con un grabador local. Se puede narrar material técnico ya capturado, identificándolo claramente como reproducción del ensayo 14/10; no presentarlo como interacción nueva.
3. Reproducir íntegro el archivo final y comprobar voz inteligible, pantalla legible, coherencia de roles/cifras y ausencia de secretos. Registrar hash, duración, idioma, ejecución viva o evidencia reproducida y QA en el [formulario pendiente](evidence/2026-10-14-a-s4-runtime/HUMAN_DEFENSE_REVIEW.md).
4. Obtener revisión académica y resolver observaciones. Confirmar el encaje DAM local y los criterios del centro; no se presume aprobación.

La narración automática opcional no se generó: la política de ejecución de Windows rechazó el script y no se eludió. Crear voz automática tampoco habría demostrado un ensayo oral humano.

## Salida

| Control | Estado |
| --- | --- |
| A-S3 y R-20 para índices exactos | COMPLETE / ACCEPTED TEMPORARY |
| CI de la base documental | 10/10 PASS histórico sobre `94ce879`; nueva rama pendiente de CI |
| Memoria PDF y texto | Preparados y maquetación revisada; aprobación académica PENDING |
| Guion inglés | Preparado; duración planificada 12 minutos |
| Demo técnica real / persistencia / recovery | PASS medido 14/10 |
| Ensayo oral humano | PENDING |
| Grabación narrada / QA final | PENDING |
| Revisión académica | PENDING |
| TFG-A | IN PROGRESS |

R-23 sigue OPEN independiente. AWS, producción, datos reales y proveedores externos permanecen bloqueados. No se amplía el MVP para completar estos entregables.
