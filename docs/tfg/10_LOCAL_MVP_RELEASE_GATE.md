# Gate de entrega local — A-S3 y A-S4

Estado 13/10/2026: main `c69f682` integrado, CI 10/10 PASS, **TFG-A IN PROGRESS**. [Registro post-merge](evidence/2026-10-13-main-closure/README.md). R-20 anterior caducado; propuesta 25 CVE `accepted: false`. R-23 OPEN independiente.

## A-S3 — Evidencia y versión

| Criterio | Estado / evidencia |
| --- | --- |
| Fuente integrada y revisión | #68 fusionada; árbol del candidato documental idéntico |
| CI efectiva exactoSHA | 10/10 success sobre `c69f682`; URLs archivadas |
| Fuente/construcción/escaneo/runtime | Cadena histórica identificada; cuatro locales inspeccionados y CI separado |
| Escaneos producción completos | 0 H/C corregibles y 0 secretos;25 CVE residuales/119 filas conservados |
| R-20 aplicable y vigente | PENDIENTE aprobación explícita de la propuesta 25 CVE y cuatro índices |
| R-23 | OPEN / NO FIX AVAILABLE; tooling independiente |
| Evidencias/receipts | No sobrescribir; decisiones posteriores como nuevos registros |
| Cierre formal A-S3 | PENDIENTE R-20 y acta de versión de entrega |

Las imágenes nuevas de CI quedan fuera de la propuesta de aceptación local. No se afirma igualdad binaria por igualdad de fuente/SBOM. D094 formalReleaseEligible es elegibilidad técnica, no aprobación académica.

## A-S4 — Defensa y memoria

| Criterio | Estado / salida exigida |
| --- | --- |
| Memoria ajustada al centro | BORRADOR; [mapa DAM](11_DAM_MEMORY_DELIVERY.md), plantilla/maquetación/revisión pendientes |
| Requisitos/diagramas/manual/resultados | Material existente; revisión final y trazabilidad de cada afirmación |
| Preflight de ejecución final | PENDIENTE decisión vigente antes de iniciar servicios |
| Demo local sintética | PENDIENTE final; ensayo histórico 09/10 no se cuenta como ensayo nuevo |
| Ensayo 12 minutos | PENDIENTE duración medida con una persona |
| Grabación final narrada | PENDIENTE QA audio / pantalla/hash; clip 24,88 s sin audio es técnico |
| Revisión tutor/centro | PENDIENTE; confirmar perfil/plantilla/calendario y si AWS es obligatorio |
| TFG-A COMPLETE / MVP académico aprobado | PENDIENTE DoD y revisión académica |

Preparar A-S4 documentalmente es posible durante el bloqueo R-20; no permite ejecutar el MVP sin aceptación ni cerrar la fase por editar estados. La validez como DAM debe decidirla el tutor; arquitectura web puede justificarse por persistencia/API/seguridad/pruebas, pero no se presupone equivalencia con requisitos de móvil/escritorio.

No upgrades, cambios funcionales, Docker/imágenes base, workflows, Terraform, AWS, Bedrock ni Keycloak externo en este cierre documental. La [Definition of Done](00_TFG_MASTER_PLAN.md#7-tfg-a-definition-of-done) continúa vigente. La aceptación anterior caducó 09/10; 16/10 es solo una fecha propuesta y no se renueva automáticamente.
