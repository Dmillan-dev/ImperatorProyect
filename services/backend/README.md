# services/backend

Scaffold for Operational Intelligence Layer (Java / Spring Boot).

Phase 0 note: this folder is documentation/scaffold only. Do not add runnable backend services, production connector code, generated bindings or deployment configuration until Phase 1 is explicitly approved and logged.

Tasks:
- Document Protobuf generation approach (maven/gradle notes only).
- Describe future gRPC service boundaries; do not add server implementation.
- Document integration notes for future Kafka producers (Protobuf-encoded).
- Conceptual ingestion mapping for Jira, GitHub, AWS and OpenAI + Anthropic Claude.
- Decision ROI Case persistence notes for Decision Ledger.
- Target stack context: Java 21 and Spring Boot.
- Follow `docs/architecture/21_Technical_Architecture_Context.md` before proposing backend structure.
