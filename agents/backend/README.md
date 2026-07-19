# Backend Agent

Responsabilidades:

- Diseño conceptual del Operational Intelligence Layer objetivo (Java, Spring Boot).
- Diseño conceptual de pipelines futuros; Kafka queda diferido hasta que volumen o replay lo justifiquen.
- Diseñar contratos Protobuf y servicios gRPC solo como contratos futuros; Phase 1 puede empezar como modular monolith.
- Persistencia estratégica (PostgreSQL, Decision Ledger).
- Modelar el Decision ROI Case como objeto transversal del MVP.
- Priorizar integraciones MVP: Jira, GitHub, AWS y OpenAI + Anthropic Claude.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md` para capas, bounded contexts y responsabilidades.
- Seguir `docs/architecture/DATABASE_MODEL.md` antes de proponer persistencia.
- Seguir `docs/architecture/CONNECTOR_FRAMEWORK.md` antes de proponer integraciones.
- Seguir `docs/architecture/27_Quality_Attributes.md` antes de proponer targets de latencia, resiliencia u observabilidad.
- Seguir `docs/architecture/28_Per_Connector_MVP_Contracts.md` antes de proponer intake o read models por conector.
- Seguir `docs/architecture/31_MVP_Implementation_Standard.md` antes de proponer estructura, puertos, adapters, auth u observabilidad.
- Seguir `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` antes de proponer scaffolding, modelo de datos inicial, conectores o tests de cierre.

Entregables iniciales:

- `services/backend/` scaffold documental con README y enfoque de contract generation.
- Conjunto inicial de `.proto` y guía de generación (Java).
- Mapping conceptual de eventos a Decision Ledger y entidades relacionadas.
- RFCs para cualquier cambio de modelo o nuevo bounded context; no convertir bounded contexts conceptuales en microservicios por defecto.
- Plan hexagonal futuro: Controller/Application/Domain/Ports/Adapters sin crear código durante Phase 0.
- Plan Phase 1 limitado a un Decision ROI Case, una recomendacion determinista y persistencia minima.
- Handoff a Connector Agent para objetos fuente y a FinOps Agent para semántica de coste.
