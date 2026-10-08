# A-S4 — Preparación y defensa local

**PREPARACIÓN DOCUMENTAL. Ensayo final y grabación narrada pendientes.** A-S3 continúa con revisión, aceptación de SHA y CI pendientes. Preparar estos materiales no salta ese gate, completa TFG-A ni autoriza AWS.

## Material preparado

- [Memoria desarrollada](08_LOCAL_MVP_MEMORY.md): requisitos, arquitectura, implementación, evaluación y limitaciones, con enlaces a evidencias.
- [Guion de doce minutos](09_DEFENSE_SCRIPT_12_MIN.md): tiempos, narración, pantallas, acciones y respuestas al tribunal.
- [Matriz de trazabilidad](03_TRACEABILITY_MATRIX.md) y [criterios de entrega](10_LOCAL_MVP_RELEASE_GATE.md).
- [Pack actual API/UI](evidence/2026-10-06-r21-r22/docker/capture/rehearsal.json), cinco capturas y [WebM local](evidence/2026-10-06-r21-r22/docker/capture/tfg-a-docker-demo.webm).

El WebM existente dura 27,16 segundos y no tiene audio. Sirve como respaldo técnico, no como ensayo oral. No se ha fabricado una narración para marcar esta obligación como cumplida.

## Identidad y preflight

Antes de una demo ejecutada deben constar: SHA aceptado, huella de entrada, imágenes reales inspeccionadas, fecha, vigencia de R-20, proyecto Docker exclusivo y datos sintéticos. Los tres OCI índices aprobados son:

| Servicio   | Índice OCI aprobado                                                       |
| ---------- | ------------------------------------------------------------------------- |
| Backend    | `sha256:681e6e7b77b73170c959ed04697302f6a21d8db98edb3a2b0f3354a44387096d` |
| Frontend   | `sha256:5cd649ff9a460b2abdc8315da2e7568c5a2c2138d99815db89c5c583f88c18b1` |
| PostgreSQL | `sha256:2b73847d07d6cb7da2ace20426de036fa0278feca0bb6520aca62588e9b3f101` |

Plataforma, configuración y capas se verifican contra [image-binding.json](evidence/2026-10-06-r21-r22/docker/image-binding.json). En este Docker Desktop se registró `Image.Id` como índice OCI; no se confunde con el config digest. Comparar solo una etiqueta o el número de versión es insuficiente.

La caducidad es exclusiva: **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**. Después de ese instante, o si ocurre una invalidación anticipada, no ejecutar amparándose en R-20. La revisión del 07/10/2026 no vuelve a certificar el advisory ni renueva el scanner; verifica los registros aprobados y sus hashes. No existe renovación automática.

La consulta Docker del 07/10/2026 encontró el daemon detenido. El inventario histórico del 06/10/2026 conserva 25 imágenes y 10 volúmenes; ese dato no es un inventario vivo del día 7. La disponibilidad actual y la validación runtime siguen pendientes.

## Procedimiento del ensayo vivo, pendiente de ejecutar

1. Resolver el cierre A-S3: revisión humana, SHA aceptado y checks requeridos asociados. Si ese cierre falta, preparar el material y no registrar una defensa final ejecutada.
2. Arrancar Docker Desktop y consultar `docker info`. Inspeccionar las tres imágenes y la disponibilidad local de herramientas y mounts; no construir, descargar ni retaggear por iniciativa propia. Si la imagen exacta no está disponible, detener el ensayo vivo.
3. Reutilizar la configuración de ensayo y el fixture JWT/HTTPS local ya validados, con un nombre de proyecto nuevo y un volumen exclusivo. La configuración ordinaria contiene build contexts y dependencias de autenticación: `docker compose up` aislado no es un procedimiento completo para esta defensa. Usar un override revisado con imágenes exactas, `--no-build --pull never` y la misma frontera loopback. No inventar que un comando de arranque genérico resuelve estas precondiciones.
4. Crear credenciales efímeras en el espacio privado del ensayo. No persistir tokens en repositorio, argumentos, grabación o informe. Comprobar permisos V3 y salud antes de introducir datos.
5. Importar la fixture versionada de treinta registros y componer un caso nuevo con identificadores sintéticos. No truncar ni reinicializar una base normal del proyecto. El [helper existente](../../scripts/tfg/README.md) documenta el contrato y recibe la entrada con tokens por stdin.
6. Seguir el guion: evidencia, recomendación, rechazo seguro al auditor, aprobación, implementación, validación financiera, Ledger y Business Value. Identificar los comandos API y las lecturas UI; no usar mocks como prueba del backend real.
7. Verificar 30 evidencias, tres hechos del Ledger, 18.960 EUR realizados sintéticos y variación −480 EUR. Comprobar persistencia y recovery mediante el procedimiento ya validado, preservando el volumen exclusivo hasta acabar las aserciones.
8. Ensayar con una persona durante doce minutos. Registrar tiempo real, desviaciones, modo vivo/grabado y SHA/digests; los 720 segundos del guion son planificación, no tiempo medido.
9. Grabar la explicación y la pantalla con medios locales. Revisar reproducción, audio, legibilidad y ausencia de secretos, notificaciones o datos personales. Hash del vídeo final y capturas; no publicar por inferencia.
10. Retirar únicamente recursos creados por ese ensayo, tras verificar propiedad y paths. No utilizar `docker system prune`, borrar las diez bases anteriores ni eliminar imágenes de evidencias históricas.

No se ejecutó este procedimiento en la preparación documental; necesita recuperar el daemon y cumplir los gates anteriores.

## Respaldo sin servicios externos

Copiar o abrir desde disco la memoria, el guion, las cinco capturas, el informe del ensayo y el WebM. El paquete debe reproducirse sin GitHub, AWS, IdP externo, Bedrock o red. La instalación previa de Docker, browser y dependencias del ensayo vivo no se presenta como independiente de descargas si aún no está disponible.

Usar el vídeo grabado acompañado de narración en directo es válido como explicación de un ensayo histórico. Identificarlo como tal. Si caduca R-20, los archivos siguen describiendo la ejecución fechada, pero no demuestran una autorización runtime vigente. No efectuar nuevas llamadas de proveedor para completar la defensa.

## Cierre A-S4

| Criterio                                 | Estado actual                                           |
| ---------------------------------------- | ------------------------------------------------------- |
| Memoria con evidencia y diagramas        | BORRADOR DESARROLLADO; revisión institucional pendiente |
| Guion y preparación de preguntas         | PREPARADOS; ensayo real pendiente                       |
| SHA aceptado / CI efectiva               | PENDIENTES A-S3                                         |
| Demo final local bajo aceptación vigente | PENDIENTE; daemon detenido al comprobarlo               |
| Vídeo técnico previo verificable         | EXISTENTE; sin audio y no defensa oral                  |
| Ensayo de doce minutos y grabación final | PENDIENTES                                              |
| Aprobación académica                     | PENDIENTE tutor/centro; no inferida de tests            |

La finalización académica local no descarga D101, autoriza una release operativa o abre TFG-B/C/D.
