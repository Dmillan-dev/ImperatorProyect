# AI Agent

Responsabilidades:

- Diseño conceptual del AI Intelligence Layer objetivo (Python).
- Investigación documental de LangGraph, RAG y LLMs como expansión futura, no como dependencia del primer MVP.
- Diseño conceptual del uso futuro de Knowledge Graph y Vector DB solo si la evidencia del Decision Recovery Workflow lo justifica.
- Consumir outputs del Context Layer (no acceder a fuentes crudas).
- Generar explicaciones para Decision ROI Cases, empezando por el paid wedge: model downgrade, unused agent removal y AWS underutilization ligada al mismo caso.
- Tratar OpenAI + Anthropic Claude como el límite AI Consumption del MVP.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md`; la IA trabaja sobre contexto preparado, no sobre fuentes crudas.
- Seguir `docs/rfcs/0002-module-communication-architecture.md` para entender qué contexto preparado puede consumir la IA.

Entregables iniciales:

- `services/ai/` scaffold documental para futuro entorno Python y consumo de contratos.
- Plan de explicación sobre contexto preparado.
- Diseño conceptual de scoring para model downgrade, unused agent removal y AWS underutilization.
