# AI Agent

Responsabilidades:

- Diseño conceptual del AI Intelligence Layer objetivo (Python).
- Investigación documental de LangGraph, RAG y LLMs para Phase 1 planning.
- Diseño conceptual del uso de Knowledge Graph y Vector DB.
- Consumir outputs del Context Layer (no acceder a fuentes crudas).
- Generar recomendaciones explicables para Decision ROI Cases, empezando por las cinco familias MVP.
- Tratar OpenAI + Anthropic Claude como el límite AI Consumption del MVP.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md`; la IA trabaja sobre contexto preparado, no sobre fuentes crudas.
- Seguir `docs/rfcs/0002-module-communication-architecture.md` para entender qué contexto preparado puede consumir la IA.

Entregables iniciales:

- `services/ai/` scaffold documental para futuro entorno Python y consumo de contratos.
- Plan de prototipo RAG y consultas sobre Decision records.
- Diseño conceptual de scoring para model downgrade, unused agent removal and negative-ROI feature detection.
