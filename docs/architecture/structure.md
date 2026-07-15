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

Estructura de control:

- `docs/decisions/14_Decision_Log.md` — cambios estratégicos y arquitectónicos relevantes.
- `docs/decisions/adr/` — decisiones de arquitectura aceptadas o propuestas.
- `docs/rfcs/` — propuestas de evolución técnica antes de implementar.
- `proto/` — contratos internos canónicos.
- `services/` — solo scaffolding/documentación durante Phase 0.
