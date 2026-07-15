# services/ai

Scaffold for AI services (Python).

Phase 0 note: this folder is documentation/scaffold only. Do not add runnable AI services, notebooks, generated bindings, requirements files or provider integrations until Phase 1 is explicitly approved and logged.

Tasks:
- Document future consumption of `proto/Decision.proto` from Python.
- Describe prototype plan for RAG and LangGraph pipelines.
- Document future virtual environment and dependency approach.
- Define recommendation reasoning assumptions for the five MVP recommendation families.
- Provider boundary for MVP: OpenAI + Anthropic Claude.
- Target stack context: Python and FastAPI.
- Follow `docs/architecture/21_Technical_Architecture_Context.md`; AI must consume prepared context, not raw external sources.
