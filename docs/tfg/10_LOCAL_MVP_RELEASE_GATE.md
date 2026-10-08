# Gate de entrega local — A-S3 y A-S4

Revisión del **07/10/2026**. Fuente de verdad: documentos y evidencias del repositorio. A-S2 formal GO continúa siendo una decisión local/sintética y temporal; no representa cero CVE absoluto ni una release operativa del producto.

## Interpretación de las dos fases

La instrucción actual sitúa el cierre documental/formal de TFG-A en A-S3 y la defensa académica en A-S4. Se conserva la [Definition of Done](00_TFG_MASTER_PLAN.md#7-tfg-a-definition-of-done), que exige evidencias y no permite cerrar por editar documentos. A-S3 debe resolver el versionado/revisión/CI y el cierre explícito del paquete; A-S4 debe resolver memoria, ensayo y grabación. No se elimina ningún requisito porque la fase cambie de nombre.

El MVP local puede demostrar requisitos implementados. La validez académica final depende del tutor y del centro. AWS obligatorio o dispensado, plantilla, fecha de entrega y criterios del tribunal no están confirmados en el repositorio. Una release/piloto del producto conserva el gate D101 y los criterios externos existentes.

## A-S3 — Evidencia y versión

| Criterio                                                          | Estado                            | Evidencia / siguiente resultado necesario                                                               |
| ----------------------------------------------------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------- |
| R-20 y R-23 registrados por separado                              | PASS histórico vigente por fecha  | [Decisiones explícitas](evidence/2026-10-06-a-s2-acceptance/README.md); mantener digests y caducidad    |
| Runtime/source/capturas vinculados                                | VERIFICADO sobre fuente histórica | 383 archivos y [manifest OCI](evidence/2026-10-06-r21-r22/docker/image-binding.json); no reconstrucción |
| Documentación coherente y discrepancias explicadas                | REVISIÓN LOCAL preparada          | [Disposiciones de revisión](evidence/2026-10-07-a-s3-a-s4/README.md); preservar snapshots anteriores    |
| Diff de candidato acotado a mantenimiento autorizado y evidencias | REVISIÓN LOCAL preparada          | Diff por grupos; bruto conserva hashes y advertencias de formato                                        |
| SHA revisado y aceptado por propietario                           | PENDIENTE                         | Candidato local no equivale a SHA aceptado; registrar decisión independiente                            |
| PR acotada                                                        | BORRADOR LOCAL                    | No publicada ni fusionada; PR #57–59 siguen fuera de alcance                                            |
| CI real asociada al SHA enviado                                   | PENDIENTE                         | URL, head SHA, checks y artefactos; sin heredar badges anteriores                                       |
| Imágenes de CI y aprobación                                       | PENDIENTE si CI reconstruye       | Digest nuevo necesita revisión; aceptación R-20 no transferida                                          |
| Cierre formal TFG-A documental/técnico                            | PENDIENTE                         | Registrar decisión y DoD cumplida; no cerrar por preparación de la memoria                              |

## A-S4 — Defensa local

| Criterio                                            | Estado                | Resultado necesario                                                       |
| --------------------------------------------------- | --------------------- | ------------------------------------------------------------------------- |
| Memoria con RF/RNF, diagramas, resultados y límites | BORRADOR DESARROLLADO | Ajustar plantilla e incorporar revisión académica                         |
| Trazabilidad de afirmaciones                        | PREPARADA             | Matriz, fuente, capturas y resultados de la versión aceptada              |
| Guion de doce minutos                               | PREPARADO             | Cronometrar con una persona; registrar duración real                      |
| Runtime local final                                 | PENDIENTE             | Docker disponible, SHA/digests y aceptación vigente antes de ensayo       |
| Fallback offline                                    | MATERIAL EXISTENTE    | Comprobar reproducción local; registrar evidencia histórica correctamente |
| Grabación narrada final                             | PENDIENTE             | Audio/pantalla, QA y hashes; vídeo de 27 segundos no sustituye defensa    |
| MVP TFG defendido/aceptado                          | PENDIENTE             | Revisión del tutor/centro, sin inferir aceptación desde PASS técnico      |

## Alcance que se mantiene

No nuevas dependencias, cambios de código, bases, arquitectura, funcionalidades, AWS, Keycloak o Bedrock. R-23 continúa abierto. La aceptación R-20 caduca el **09/10/2026 00:00 Europe/Madrid** y se invalida antes según las condiciones del registro. Una defensa posterior exige resolver su autorización runtime; reproducir evidencia histórica no la renueva.

Las comprobaciones de documentación/hash y del vídeo son locales; no se presentan como nueva regresión funcional, nueva consulta de vulnerabilidades del proveedor ni CI alojada. La indisponibilidad Docker actual queda registrada, no convertida en PASS.
