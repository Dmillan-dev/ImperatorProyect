# services/ai

Scaffold for AI services (Python).

Phase 0 note: this folder is documentation/scaffold only. Do not add runnable AI services, notebooks, generated bindings, requirements files or provider integrations until Phase 1 is explicitly approved and logged.

Tasks:
- Document future consumption of `proto/Decision.proto` from Python.
- Describe future RAG and LangGraph pipeline options only after the Decision Recovery Workflow is validated.
- Document future virtual environment and dependency approach.
- Define recommendation reasoning assumptions for model downgrade, unused agent removal and AWS underutilization tied to one Decision ROI Case.
- Provider boundary for MVP: OpenAI + Anthropic Claude.
- Target stack context: Python and FastAPI.
- Follow `docs/architecture/21_Technical_Architecture_Context.md`; AI must consume prepared context, not raw external sources.
- Follow `docs/architecture/31_MVP_Implementation_Standard.md`; AI explanation is non-authoritative for the first MVP.
- Follow `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`; AI may explain only and must not decide, persist, execute, approve, reject, defer, mark implementation or validate results.
