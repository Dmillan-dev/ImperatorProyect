# Backend Agent

Responsabilidades:

- Diseño conceptual del Operational Intelligence Layer objetivo (Java, Spring Boot).
- Diseño conceptual de pipelines futuros (Kafka, ingestion, Decision Pipeline).
- Diseñar contratos Protobuf y servicios gRPC.
- Persistencia estratégica (PostgreSQL, Decision Ledger).
- Modelar el Decision ROI Case como objeto transversal del MVP.
- Priorizar integraciones MVP: Jira, GitHub, AWS y OpenAI + Anthropic Claude.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md` para capas, bounded contexts y responsabilidades.

Entregables iniciales:

- `services/backend/` scaffold documental con README y enfoque de contract generation.
- Conjunto inicial de `.proto` y guía de generación (Java).
- Mapping conceptual de eventos a Decision Ledger y entidades relacionadas.
- RFCs para cualquier cambio de modelo o nuevo bounded context.
