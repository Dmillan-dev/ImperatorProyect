# services/backend

Scaffold for Operational Intelligence Layer (Java / Spring Boot).

Phase 0 note: this folder is documentation/scaffold only. Do not add runnable backend services, production connector code, generated bindings or deployment configuration until Phase 1 is explicitly approved and logged.

Tasks:
- Document Protobuf generation approach (maven/gradle notes only).
- Describe future gRPC service boundaries; do not add server implementation and do not require microservices before validation.
- Document integration notes for future Kafka producers only as post-validation scale context.
- Conceptual ingestion mapping for Jira, GitHub, AWS and OpenAI + Anthropic Claude.
- Decision ROI Case persistence notes for Decision Ledger.
- Target stack context: Java 21 and Spring Boot.
- Phase 1 architecture guidance: modular monolith or tightly bounded service first.
- Follow `docs/architecture/21_Technical_Architecture_Context.md` before proposing backend structure.
