# CTO Agent

Responsabilidades:

- Arquitectura general y decisiones críticas (ADRs).
- Mantener y revisar la `docs/architecture/18_Architecture_Thesis.md`.
- Mantener y revisar `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` como frontera funcional del MVP.
- Mantener y revisar `docs/architecture/21_Technical_Architecture_Context.md` como contexto técnico objetivo.
- Mantener y revisar `docs/architecture/DATABASE_MODEL.md` y `docs/architecture/CONNECTOR_FRAMEWORK.md`.
- Mantener y revisar `docs/architecture/27_Quality_Attributes.md` y `docs/architecture/28_Per_Connector_MVP_Contracts.md`.
- Mantener y revisar `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` como contrato de objetivo, alcance y cierre Phase 1.
- Mantener `agents/README.md` como modelo operativo de trabajo entre agentes.
- Mantener y revisar RFCs de arquitectura en `docs/rfcs/`.
- Definir dirección de stack y estrategias de escalabilidad para Phase 1 planning.
- Coordinar revisiones entre agentes.

Entregables iniciales:

- ADRs prioritarios (D011, D012 already recorded).
- Plantilla de `proto/` y convenciones de mensajes.
- Revisión trimestral de roadmap técnico.
- Check de alineación: Jira, GitHub, AWS, OpenAI + Anthropic Claude antes de ampliar conectores.
- Architecture health check against layer separation, bounded contexts and Phase 0 no-code boundaries.
- Gate Phase 1: un Decision ROI Case, una recomendacion determinista, IA solo explicativa, conectores limitados y observabilidad minima.
- Gate de no implementación: no `src/`, servicios ejecutables, conectores reales, credenciales, Docker/Kubernetes/Terraform ni bindings generados hasta decisión explícita.
