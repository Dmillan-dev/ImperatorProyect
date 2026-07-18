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
- `proto/` — archivos `.proto` canónicos como contratos internos futuros.
- `agents/` — agentes lógicos responsables de áreas (CTO, Backend, AI, Frontend, Product, Security).
- `services/` — scaffolding documental por stack futuro durante Phase 0.
- `demos/` — demos HTML de experiencia y narrativa.
- `infra/` — reservado para infra-as-code, despliegue y scripts en fases posteriores.

Propósito: permitir que 'agentes' trabajen de forma separada en sus áreas, manteniendo contratos claros (`proto/`) y ADRs para decisiones técnicas.

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

- `docs/product/24_MVP_Vertical_Slice.md` — definición del primer recorrido end-to-end defendible: Event -> Connector -> Decision Engine -> ROI Engine -> Recommendation -> Decision Ledger -> Decision Review Workspace.

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

Estructura de control:

- `docs/decisions/14_Decision_Log.md` — cambios estratégicos y arquitectónicos relevantes.
- `docs/decisions/adr/` — decisiones de arquitectura aceptadas o propuestas.
- `docs/rfcs/` — propuestas de evolución técnica antes de implementar.
- `proto/` — contratos internos canónicos.
- `services/` — solo scaffolding/documentación durante Phase 0.
