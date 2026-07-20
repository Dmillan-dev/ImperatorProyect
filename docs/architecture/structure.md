# Estructura del repositorio

- `docs/` — sistema documental organizado por zonas.
  - `docs/business/` — negocio, mercado, ICP, GTM, ventas y validación.
  - `docs/product/` — producto, dominio, API conceptual, capacidades y lenguaje canónico.
  - `docs/architecture/` — arquitectura conceptual, datos, conectores, contexto técnico objetivo y reglas Phase 0.
  - `docs/ai/` — contexto operativo para agentes y prompts recurrentes.
  - `docs/rfcs/` — propuestas estilo RFC antes de cambios relevantes.
  - `docs/decisions/` — decision log y decisiones aceptadas.
  - `docs/decisions/adr/` — Architecture Decision Records.
  - `docs/research/` — hipótesis, experimentos y plantillas de validación.
  - `docs/rnd/` — evidencia documental de I+D/R&D: actividades, horas, objetos tecnicos, pruebas y dossier de desarrollo.
- `proto/` — archivos `.proto` canónicos como contratos internos futuros.
- `agents/` — agentes lógicos responsables de áreas (CTO, Product, Connector, Backend, Frontend, AI, Security, FinOps, QA).
  - `agents/phase1/` — guia documental para dividir Phase 1 en etapas con agentes autonomos; no es runtime ni codigo.
- `services/` — scaffolding documental por stack futuro durante Phase 0.
- `demos/` — demos HTML de experiencia y narrativa.
- `infra/` — reservado para infra-as-code, despliegue y scripts en fases posteriores.

Propósito: permitir que agentes trabajen de forma separada en sus áreas, manteniendo contratos claros, autoridad documental y handoffs explícitos.

Documento canónico de trabajo por agentes:

- `agents/README.md` — operating model para dividir trabajo futuro entre CTO, Product, Connector, Backend, Frontend, AI, Security, FinOps y QA sin crear implementación durante Phase 0.
- `agents/phase1/README.md` — proceso recomendado para crear Phase 1 por etapas con agentes autonomos, handoffs, gates y disciplina de evidencia.
- `agents/phase1/12_phase1_closure.md` — cierre del dossier documental Phase 1: contexto listo para futuro scaffolding limitado, sin afirmar que exista software implementado.

Documento canónico del MVP:

- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` — blueprint de la Decision ROI Platform: integraciones MVP, objeto central, recomendaciones prioritarias, superficies de producto y criterios de validación.

Documento canónico del dominio:

- `docs/product/CORE_DOMAIN_MODEL.md` — entidades, relaciones e invariantes de negocio que deben guiar Java, Python, PostgreSQL, React y futuros agentes.

Documento canónico de API conceptual:

- `docs/product/API_SPECIFICATION.md` — superficie API antes de OpenAPI, protobuf o implementación.

Documento canónico del Decision Ledger:

- `docs/product/DECISION_LEDGER_V2.md` — contrato funcional del ledger: casos de uso, modelo, API conceptual, eventos, riesgos y criterios de aceptación.

Documento canónico de datos:

- `docs/architecture/DATABASE_MODEL.md` — modelo conceptual de datos y responsabilidades de almacenamiento sin SQL detallado.

Documento canónico de conectores:

- `docs/architecture/CONNECTOR_FRAMEWORK.md` — framework conceptual para añadir integraciones sin modificar el núcleo del dominio.

Documento canónico de contratos MVP por conector:

- `docs/architecture/28_Per_Connector_MVP_Contracts.md` — contratos pre-code para Jira, GitHub, AWS y OpenAI + Anthropic Claude: objetos fuente, evidencia, permisos, frescura, sensibilidad, fallos y ownership por agente.

Documento canónico de vocabulario evento/evidencia:

- `docs/architecture/29_Event_Evidence_Vocabulary.md` — vocabulario controlado de eventos normalizados, tipos de evidencia, estados, bloqueos, frescura, sensibilidad y confianza.

Documento canónico de cierre Phase 0:

- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` — auditoría final de coherencia, gates, riesgos residuales y trabajo por agentes antes de autorizar Phase 1.

Documento canónico de implementación MVP:

- `docs/architecture/31_MVP_Implementation_Standard.md` — estándar futuro para aplicar alcance reducido, arquitectura hexagonal, auth JWT/OAuth2 compatible, observabilidad mínima y separación de adapters.

Documento canónico de alcance y cierre Phase 1:

- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` — contrato exacto de objetivo Phase 1: un Decision ROI Case, una recomendacion determinista, modelo de datos mínimo, conectores limitados, IA solo explicativa, scaffolding controlado y criterios de salida.
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` — decisiones finales antes de codigo: Enterprise Evidence Event, PostgreSQL, Explanation Provider, JWT/RBAC simple y Decision Graph interno sobre PostgreSQL.
- `docs/architecture/34_MVP_Implementation_Blueprint.md` — contrato final de implementacion MVP: flujo, componentes, objetos, repositorios, servicios, API, tablas, seguridad, NFRs, out-of-scope y plan de 6 semanas.
- `docs/architecture/35_Coding_Principles.md` — reglas de implementacion para agentes: arquitectura hexagonal, capas, adapters, DTOs, seguridad, observabilidad y limites de IA.
- `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` — contrato de Phase 2 Platform Foundation: fundacion tecnica sin ROI, recomendaciones, IA real, conectores live ni reglas de negocio.
- `agents/phase1/12_phase1_closure.md` — contrato de cierre operativo para agentes antes de iniciar cualquier scaffolding futuro de Phase 1.

Documento canónico de arquitectura:

- `docs/architecture/21_Technical_Architecture_Context.md` — contexto técnico objetivo: capas, bounded contexts, responsabilidades, stack direction, control model y guardrails.

Documento de auditoría técnica:

- `docs/architecture/22_Technical_Investor_Audit.md` — revisión tipo inversor técnico: qué eliminar, qué falta, qué está sobreingenierizado y cómo enfocar Phase 1.

Documento de estructura MVP:

- `docs/architecture/24_MVP_Project_Structure.md` — estructura futura recomendada para el primer MVP sin crear servicios, código ni infraestructura durante Phase 0.

Documento de auditoria pre-code:

- `docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md` — revision Phase 0 antes de escribir codigo: coherencia, huecos, gates y proximos documentos recomendados.

Documento de seguridad y gobierno de datos:

- `docs/architecture/26_Security_Data_Governance_Threat_Model.md` — modelo Phase 0 de boundaries, sensibilidad, IA, permisos minimos de conectores, amenazas y controles antes de datos reales.

Documento de vertical slice MVP:

- `docs/product/24_MVP_Vertical_Slice.md` — definición del primer recorrido end-to-end defendible: Event -> Connector -> Decision Engine -> ROI Engine -> Recommendation -> Decision Ledger -> Decision Review Workspace -> Result Validation.

Documento de ROI slice MVP:

- `docs/product/25_MVP_ROI_Slice.md` — modelo conceptual de coste actual, recuperación estimada, supuestos, confianza, riesgo y valor realizado para un Decision ROI Case.

Documento de evidencia manual MVP:

- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` — pack manual de evidencia para probar que Decision ROI Case, ROI, Recommendation, Ledger y conectores encajan antes de escribir codigo.

Documento de acceptance tests MVP:

- `docs/product/27_MVP_Acceptance_Test_Plan.md` — escenarios de aceptacion pre-code para evidencia, ROI, recomendacion, ledger, aprobacion, seguridad y Decision Review Workspace.

Documento de identidad y aprobacion MVP:

- `docs/product/28_Identity_Access_Approval_Model.md` — modelo pre-code de roles, acceso a evidencia, autoridad de aprobacion, rechazo, diferimiento, implementacion y validacion.

Documento de contrato de pantalla MVP:

- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` — contrato pre-code de lo que la primera pantalla operativa del MVP muestra, oculta, bloquea y registra.

Documento de atributos de calidad MVP:

- `docs/architecture/27_Quality_Attributes.md` — expectativas no funcionales pre-code para explicabilidad, auditabilidad, frescura, trazabilidad, latencia, resiliencia, observabilidad y limites de rendimiento.

Documento de evidencia I+D/R&D:

- `docs/rnd/30_RD_Activity_Evidence_Dossier.md` — sistema para documentar arquitectura, futuro codigo, horas, objetos tecnicos, experimentos, pruebas, costes y actividad de desarrollo.

Documento de estándar MVP:

- `docs/architecture/31_MVP_Implementation_Standard.md` — contrato práctico para que agentes de Backend, Frontend, Connectors, Security, FinOps, QA y AI planifiquen Phase 1 sin sobredimensionar el MVP.

Documento de contrato Phase 1:

- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` — referencia obligatoria antes de crear scaffolding, data model inicial, conectores, flujo IA explicativo o tests de salida Phase 1.
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` — referencia obligatoria antes de elegir formato de evidencia, persistencia, auth, proveedor IA o modelo relacional del Decision Graph.
- `docs/architecture/34_MVP_Implementation_Blueprint.md` — referencia obligatoria antes de iniciar Phase 2 Platform Foundation o Phase 3 MVP Implementation.
- `docs/architecture/35_Coding_Principles.md` — referencia obligatoria antes de crear codigo de backend, Python, frontend, base de datos, API, auth, observabilidad o CI.
- `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` — referencia obligatoria antes de iniciar Phase 2 Platform Foundation; no autoriza logica de negocio.
- `agents/phase1/12_phase1_closure.md` — referencia obligatoria para confirmar que el dossier Phase 1 esta cerrado como contexto y que el software aun no se ha construido.

Estructura de control:

- `docs/decisions/14_Decision_Log.md` — cambios estratégicos y arquitectónicos relevantes.
- `docs/decisions/adr/` — decisiones de arquitectura aceptadas o propuestas.
- `docs/rfcs/` — propuestas de evolución técnica antes de implementar.
- `proto/` — contratos internos canónicos.
- `services/` — solo scaffolding/documentación durante Phase 0.
- `docs/rnd/` — evidencia de desarrollo e I+D/R&D, sin inventar horas, codigo ni resultados no ejecutados.
