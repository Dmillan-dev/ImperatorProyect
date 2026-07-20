# AI Agent

Responsabilidades:

- Diseño conceptual del AI Intelligence Layer objetivo (Python).
- Investigación documental de LangGraph, RAG y LLMs como expansión futura, no como dependencia del primer MVP.
- Diseño conceptual del uso futuro de Knowledge Graph y Vector DB solo si la evidencia del Decision Recovery Workflow lo justifica.
- Consumir outputs del Context Layer, no acceder a fuentes crudas.
- Generar explicaciones para Decision ROI Cases, empezando por una sola recomendacion MVP: AI model downgrade/change para `DRC-AOA-001`.
- Tratar unused agent removal, AWS underutilization, negative-ROI analysis y duplicated service/agent consolidation como expansion posterior.
- Tratar OpenAI + Anthropic Claude como el limite AI Consumption del MVP.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md`; la IA trabaja sobre contexto preparado, no sobre fuentes crudas.
- Seguir `docs/rfcs/0002-module-communication-architecture.md` para entender que contexto preparado puede consumir la IA.
- Seguir `docs/architecture/26_Security_Data_Governance_Threat_Model.md` para limites de datos crudos, prompts y completions.
- Seguir `docs/architecture/27_Quality_Attributes.md` para explicacion, evidencia citada y no bloqueo de aprobacion.
- Seguir `docs/architecture/28_Per_Connector_MVP_Contracts.md` para tratar OpenAI + Anthropic Claude como fuente de consumo, no como autoridad de negocio.
- Seguir `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`; la IA solo explica contexto preparado y no decide, persiste, ejecuta, aprueba, rechaza, difiere, marca implementacion ni valida resultados.
- Seguir `docs/architecture/37_Implementation_Contract.md` y `agents/phase2/README.md` antes de cualquier implementacion Python/AI. Un agente no puede generar mas de un modulo por iteracion.
- Seguir `agents/README.md` para handoffs con Connector, FinOps, Security y QA.

Entregables iniciales:

- `services/ai/` scaffold documental para futuro entorno Python y consumo de contratos.
- Plan de explicacion sobre contexto preparado.
- Diseño conceptual de explicacion para una recomendacion determinista de model downgrade/change.
- Handoff a Security Agent si una explicacion requeriria datos Restricted.
- Handoff a FinOps Agent si una explicacion toca ROI.
