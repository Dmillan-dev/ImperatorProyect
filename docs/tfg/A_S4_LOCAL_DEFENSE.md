# A-S4 — Preparación y defensa local

Revisión: **13/10/2026**. **PREPARACIÓN DOCUMENTAL; ensayo final y grabación narrada PENDIENTES.** A-S3 espera la decisión R-20 actual. Se puede preparar memoria, guion y respaldo sin ejecutar servicios. No se da por cumplida una defensa humana por crear estos documentos.

## Material de la versión actual

- [Memoria](08_LOCAL_MVP_MEMORY.md), [guion 12 minutos](09_DEFENSE_SCRIPT_12_MIN.md), [trazabilidad](03_TRACEABILITY_MATRIX.md), [entrega DAM](11_DAM_MEMORY_DELIVERY.md).
- [Ensayo real 09/10](evidence/2026-10-09-gosu-security-closure/runtime/capture/rehearsal.json), cinco capturas y [vídeo técnico](evidence/2026-10-09-gosu-security-closure/runtime/capture/tfg-a-docker-demo.webm), [QA](evidence/2026-10-09-gosu-security-closure/runtime/capture/video-qa.json).
- [Registro main / CI/artefactos](evidence/2026-10-13-main-closure/README.md) y [propuesta R-20](evidence/2026-10-13-main-closure/R20_CURRENT_PROPOSAL.md).

El vídeo técnico 09/10 dura 24,88 segundos y no tiene audio. No sustituye una defensa narrada de 12 minutos. El clip 06/10 es histórico y no representa el runtime actual.

## Preflight obligatorio antes de ejecutar

1. A-S3 formal cerrado con una decisión vigente para **los cuatro digests locales** fijados en el [JSON de propuesta](evidence/2026-10-13-main-closure/R20_CURRENT_PROPOSAL.json). Hasta que se apruebe, `accepted: false`. Comprobar caducidad, matriz residual, 0 H/C corregibles y 0 secretos. El merge/CI no acepta el riesgo.
2. Inspeccionar IDs/orígenes/capas y fixture HTTPS/JWKS local. Backend/frontend/migración tienen origen 6e79e83; PostgreSQL a931601. No usar los antiguos tfg-a-cbc ni reemplazarlos por las imágenes nuevas de CI.
3. Revisar un harness aislado basado en las [entradas del ensayo](evidence/2026-10-09-gosu-security-closure/runtime/runtime-inputs-summary.json), imágenes exactas, `--no-build --pull never`, solo loopback, proveedores externos desactivados y volumen sintético nuevo. La configuración genérica de Compose contiene build contexts; no es un preflight completo.
4. Tokens efímeros por stdin/espacio privado; nunca en repositorio, argumentos visibles, informe o vídeo. No limpiar bases normales. Verificar Flyway V1–V3, privilegios de aplicación y salud antes de importar la fixture 30 registros.

El inventario 13/10 confirma disponibilidad y coincidencia de las cuatro imágenes; 0 contenedores activos. No es una ejecución nueva. La propuesta de caducidad 16/10 tampoco es una autorización de arranque.

## Ensayo y grabación pendientes

Después del gate, medir 12 minutos con una persona y ejecutar solo el caso `DRC-AOA-001` sintético: 30 evidencias, recomendación `MODEL_CHANGE`, 19.440 EUR estimados, aprobación humana, implementación y validación financiera por API autenticada con lectura UI, 3 hechos Ledger, 18.960 EUR realizados simulados y −480 EUR de variación. No afirmar un flujo completo exclusivamente por interfaz ni un ahorro empresarial real.

Verificar 401/403 y asignación de actor; Flyway, permisos append-only de la cuenta de aplicación, persistencia y ready 503/live 200/recovery 200. Registrar modo vivo/grabado, hora, SHA, digests, duración medida y desviaciones. La evidencia 09/10 no sustituye las mediciones del ensayo final.

Grabar pantalla y narración con medios locales. Reproducir el vídeo final, comprobar audio comprensible, pantallas legibles y ausencia de secretos/notificaciones. Calcular hash y anexar resultados reales. Si hay un fallo, registrarlo; no rellenar un PASS ni una aprobación del tutor por inferencia.

Retirar únicamente recursos propiedad de ese ensayo con autorización y control de alcance. La limpieza 13/10 ya retiró 15 intermedias; volúmenes/cachés/bases/herramientas permanecen. No ejecutar prune ni borrar recursos antiguos para preparar la defensa.

## Respaldo offline

Abrir desde disco memoria, diagramas, capturas y vídeo técnico, identificando siempre la ejecución histórica 09/10. Preparar narración humana y archivos locales; no depender de GitHub, AWS, Keycloak externo o Bedrock. La disponibilidad de archivos no demuestra una nueva ejecución autorizada. Si R-20 caduca, usar solo material histórico hasta nueva decisión.

## Gate de salida

| Criterio | Estado 13/10 |
| --- | --- |
| Memoria y guion | BORRADOR preparado; ajuste DAM y tutor pendientes |
| CI del main integrado | 10/10 PASS sobre `c69f682` |
| R-20 vigente/A-S3 formal | PENDIENTES |
| Demo final autorizada | PENDIENTE |
| Ensayo cronometrado y narración | PENDIENTES |
| Vídeo final audio / pantalla/hash | PENDIENTE |
| Tutor/centro | PENDIENTE; criterios y fecha deben confirmarse |

TFG-A solo se declara COMPLETE cuando se cumplen DoD y revisión académica; A-S3 no permite saltar A-S4. AWS sigue bloqueado y R-23 OPEN. No se añaden funcionalidades para cerrar estos gates.
