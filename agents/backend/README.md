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

Entregables iniciales:

- `services/backend/` scaffold documental con README y enfoque de contract generation.
- Conjunto inicial de `.proto` y guía de generación (Java).
- Mapping conceptual de eventos a Decision Ledger y entidades relacionadas.
- RFCs para cualquier cambio de modelo o nuevo bounded context; no convertir bounded contexts conceptuales en microservicios por defecto.
