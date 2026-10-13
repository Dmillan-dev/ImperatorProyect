# Entrega DAM — IMPERATOR y cierre académico

Autor: **Daniel Millan Perez**. Proyecto académico con datos sintéticos. Revisión 13/10/2026, scope local. Este mapa procede de las diapositivas aportadas por el usuario `20260311 PI- 10ª sesión.pptx`, slides 2–7, y las plantillas institucionales consultadas. Son criterios del material entregado, pendientes de confirmación final por tutor; no una aprobación de IMPERATOR.

## Encaje y alcance defendible

IMPERATOR demuestra programación Java, acceso a datos PostgreSQL/JDBC/Flyway, interfaz, API, estados, transacciones, roles, pruebas y planificación. Puede plantearse como gestión y trazabilidad de decisiones operativas con un único caso sintético. El tutor debe confirmar que una aplicación web cumple el perfil DAM de esta convocatoria y que cloud/IA/móvil/escritorio no son obligatorios. No se modifica el producto para satisfacer requisitos no confirmados.

Incluido: DRC-AOA-001, 30 hechos, 4 roles, recomendación determinista, aprobación humana, implementación/validación vía API con lectura UI, Ledger 3 eventos y valoración simulada. Excluido: AWS real, IdP externo, Bedrock real, clientes, producción, datos reales y más casos/funcionalidades.

## Mapa de entregas y lo que falta

| Entrega institucional | Material IMPERATOR existente | Revisión pendiente |
| --- | --- | --- |
| Tarea 0: propuesta obligatoria no evaluable | Alcance/objetivos en 01_ACADEMIC_SCOPE | Confirmar propuesta IMPERATOR aceptada y requisitos de esta convocatoria con el tutor |
| Tarea 1: resumen/abstract/contexto/usuarios/objetivo/legal | Memoria 08, cuatro roles y caso local | Incorporar abstract y marco legal revisado; no inventar cliente/empresa |
| Tarea 2: RF/RNF, tareas, método, temporalización/Gantt, presupuesto/licencia/riesgos | Matriz 03, plan 00, registro de riesgos del plan 00, LICENSE existente | Gantt planificado/real con fechas verificadas; presupuesto de desarrollo documentado, licencia fiel al repositorio |
| Tarea 3: ER/relacional/SQL, UML/estados/actividad, UI y componentes | Memoria 08, arquitectura 05, Flyway V1–V3 y capturas del 09/10 | Diagramas exportables consistentes con migraciones; distinguir capturas implementadas de mockups |
| Tarea 4: código propio relevante, fuentes externas y tests | Implementación del repositorio, informes 174/35/41, Playwright 9+6 skips | Seleccionar fragmentos propios y explicar decisiones; atribuir librerías/código ajeno; indicar fechas/baselines |
| Tarea 5: instalación/manual/conclusiones/bitácora semanal | scripts/tfg, guía A-S4 y memoria 08 | Manual reproducible con preflight vigente; bitácora de hechos reales, sin inventar horas o retrospectiva |
| Tarea 6: memoria final | Borrador 08 | Arial 11, interlineado 1,5 y 20–40 páginas sin anexos; maquetar/renderizar y revisar tutor |

La licencia del repositorio reserva derechos y permite evaluación según sus términos; no se describe como MIT/opensource ni se cambia. No confundir 19.440/18.960 EUR simulados con presupuesto de proyecto, ingresos o beneficios reales. El presupuesto debe especificar horas demostrables, tarifa como hipótesis, hardware existente, costes locales; no se han contratado recursos ni medido costes AWS; no introducir importes sin fuente / criterio.

## Abstract preparado para revisar

IMPERATOR is a local academic application for tracing operational decisions from supporting evidence to human approval and a validated outcome. The implementation combines a Java modular backend, a web interface and PostgreSQL persistence. One synthetic case with thirty evidence records and four roles demonstrates deterministic recommendations, authenticated governance commands, an append-only application ledger and simulated business value. Evaluation includes unit, integration and frontend tests, Docker persistence and recovery checks, and supply-chain scans. The current integrated source has passing CI, while formal delivery still requires an applicable residual-risk decision, a narrated demonstration and academic review. No live AWS deployment or generative-AI provider is claimed.

## Estructura propuesta de memoria, 30 páginas orientativas

| Capítulo | Páginas orientativas | Contenido |
| --- | ---: | --- |
| Resumen/abstract/índice | 2 | Problema, resultado y límites |
| Contexto, usuarios, objetivos y legal | 3 | Académico/sintético, roles, alcance y normativa revisada |
| Acuerdo/planificación | 4 | RF/RNF, método, sprints/Gantt, costes/licencia/riesgos |
| Análisis/diseño | 6 | ER/relacional, SQL, UML/estados, UI, componentes/tecnologías |
| Implementación | 5 | Fragmentos propios relevantes, validación/permiso/transacción, fuentes externas |
| Evaluación | 4 | Tests, RBAC, Flyway, persistencia/recovery, seguridad y límites de evidencias |
| Instalación/manual | 3 | Entorno, preflight, flujo por roles, errores y respaldo offline |
| Conclusiones/cierre | 3 | Objetivos realmente cubiertos, pendientes y bitácora resumida |

El objetivo 30 páginas es planificación, no extensión comprobada. Anexos: informes íntegros/hash, scripts, bitácora ampliada, capturas y matriz completa. Comprobar referencias/figuras y paginación en el documento finalmente renderizado.

## Sprint final sin ampliar producto

1. **Gate R-20/A-S3:** aprobar o rechazar propuesta exacta 25 CVE, cuatro digests y caducidad; registrar decisión y versión/CI/evidencias. Sin ejecución antes del gate.
2. **A-S4a — Memoria:** confirmar perfil/plantilla/calendario con tutor; completar huecos de tabla, maquetar, revisar y entregar borrador. Se puede preparar durante R-20 pendiente.
3. **A-S4b — Defensa:** preflight bajo aceptación vigente, ensayo 12 minutos medidos, grabación narrada, QA audio/imagen/secretos/hash y respaldo offline.
4. **Cierre TFG-A:** revisar DoD/matriz, feedback del tutor, documentación final y entrega aceptada. No declarar COMPLETE antes de esos resultados. AWS permanece bloqueado.

## Registro a completar con hechos

| Dato | Estado |
| --- | --- |
| Tutor/centro y calendario definitivo | PENDIENTE confirmación |
| Propuesta IMPERATOR aceptada en DAM | PENDIENTE confirmación |
| Memoria final 20–40 páginas revisadas | PENDIENTE |
| Ensayo humano: fecha/duración/desviaciones | PENDIENTE |
| Grabación narrada: archivo/hash/QA | PENDIENTE |
| Evaluación tutor/centro | PENDIENTE |

La elección formal de IMPERATOR y los requisitos de la convocatoria deben confirmarse con el tutor. Este mapa no sustituye esa revisión ni una propuesta aceptada.
