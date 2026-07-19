# RFC 0001 — Knowledge Graph model (mapping from canonical protos)

Date: 2026-07-07
Status: Proposed

Phase 0 note: this RFC is conceptual planning. It does not authorize runnable ETL, RAG pipelines, generated bindings, database migrations or production services during Phase 0.

Canonical data model note: `docs/architecture/DATABASE_MODEL.md` owns the conceptual database model. This RFC remains a proposal for graph/storage mapping and evolution.

Investor refactor note: this RFC is **post-validation architecture context**. The first MVP should not require a graph database, vector database, RAG pipeline or semantic search to prove the Decision Recovery Workflow.

Current MVP authority note: the active MVP flow, lifecycle and recommendation scope are governed by `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`, `docs/product/CORE_DOMAIN_MODEL.md`, `docs/architecture/29_Event_Evidence_Vocabulary.md` and `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`. This RFC must not broaden Phase 1 implementation.

## Purpose

Definir el modelo conceptual inicial del Knowledge Graph (KG) para IMPERATOR, y mapear los `.proto` canónicos a un esquema inicial en PostgreSQL (con opción de migrar a DB de grafos). Este RFC guía el diseño futuro del Context Layer y sirve como contrato conceptual entre Backend, AI y Product.

## Goals

- Representar entidades y relaciones clave: Person, Team, Resource, Agent, Model, Policy, Cost, Incident, Decision.
- Permitir consultas relacionales y traversals para análisis de impacto, riesgo y coste.
- Soportar RAG/embeddings para preguntas de negocio y búsqueda semántica.
- Mantener privacidad y sensibilidad de datos para RAG.
- Expresar la Decision ROI Timeline del MVP conectando Business Context, Code & Deployment, Infrastructure & Cost y AI Consumption.

## MVP information domains

El modelo debe priorizar cuatro dominios de información antes de ampliar conectores:

- Business Context: Jira aporta proyecto, ticket, epic, prioridad, responsable y estado.
- Code & Deployment: GitHub aporta pull requests, commits, reviews, deploys, autor y fecha.
- Infrastructure & Cost: AWS aporta Cost Explorer, CloudWatch, Lambda, ECS, EC2 y EKS.
- AI Consumption: OpenAI + Anthropic Claude aporta modelos, tokens, coste, usuario y aplicación.

La pregunta de producto que debe soportar el grafo es:

> Esta decisión cuesta X hoy, tiene Y señal observable de uso o valor y tiene una acción que puede recuperar Z al año.

El objeto narrativo canónico es el **Decision ROI Case**:

Business Decision -> Technical Change -> Infrastructure -> AI Consumption -> ROI View -> Recommendation -> Approval/Rejection/Deferral -> Decision Ledger -> Result Validation.

El grafo debe poder soportar primero el foco MVP pagable:

- downgrade o cambio de modelo IA,
- eliminación de agentes IA sin uso,
- detección de recursos AWS infrautilizados ligados al mismo Decision ROI Case.

Quedan como expansión post-validación:

- identificación de funcionalidades con ROI negativo,
- consolidación de servicios o agentes duplicados.

## Mapping protos → esquema inicial (Postgres)

Design principle: modelar entidades como tablas y relaciones explícitas en tablas de enlace; usar una tabla `edges` simple para acelerar traversals si se necesita.

- `persons` (from `Person.proto`)
  - id (pk), name, email, department_id, roles (jsonb), metadata (jsonb)

- `teams` (from `Team.proto`)
  - id (pk), name, parent_team_id, metadata (jsonb)
  - members: association table `team_members(team_id, person_id)`

- `resources` (from `Resource.proto`)
  - id, name, provider, type, owner_id (nullable), monthly_cost, metadata

- `agents` (from `Agent.proto`)
  - id, name, type, owner_id, metadata

- `models` (from `Model.proto`)
  - id, name, provider, version, metadata

- `policies` (from `Policy.proto`)
  - id, name, description, metadata

- `costs` (from `Cost.proto`)
  - id, resource_id, amount, currency, period_start, period_end, metadata

- `incidents` (from `Incident.proto`)
  - id, title, description, started_at, resolved_at, metadata

- `decisions` (from `Decision.proto`)
  - id, organization, project, owner, source, cost, risk (jsonb), timestamp, summary, metadata (jsonb), created_by, sensitivity
  - `decision_entities(decision_id, entity_type, entity_id)` to link to persons/teams/resources/incidents/etc
  - `metadata` should include MVP domain hints where needed (business_context, code_deployment, infrastructure_cost, ai_consumption) until a later contract formalizes them.

Optional general edge table for faster graph traversals:

- `edges`:
  - id, from_type, from_id, to_type, to_id, relation, metadata (jsonb)

## Indexes and performance

- Index primary ids and frequent lookup fields (e.g., `resources(provider)`, `decisions(timestamp)`).
- GIN indexes on `jsonb` fields used for filters.
- Maintain materialized views for heavy cross-joins (e.g., cost per owner per month).

## Queries (examples)

- Which resources without owner generate highest cost:

  SELECT r.id, r.name, SUM(c.amount) as total_cost
  FROM resources r
  JOIN costs c ON c.resource_id = r.id
  WHERE r.owner_id IS NULL
  GROUP BY r.id, r.name
  ORDER BY total_cost DESC;

- Which AI agents interacted with sensitive data and increased cost:

  SELECT a.id, a.name, SUM(c.amount) total_cost
  FROM agents a
  JOIN edges e ON e.from_type='agent' AND e.from_id = a.id AND e.to_type='resource'
  JOIN costs c ON c.resource_id = e.to_id
  WHERE a.metadata->>'sensitivity_flag' = 'true'
  GROUP BY a.id, a.name;

## RAG and embeddings

- Store semantic embeddings for `decisions.summary` and selected `incidents`/`policies` in a Vector DB (e.g., Postgres+pgvector or a dedicated vector DB).
- For RAG, enforce redaction/PII stripping on content before indexing. Use `sensitivity` field on `decisions` to gate RAG inputs.

## Privacy and access control

- Decisions and related entities must include `sensitivity` and `created_by` fields.
- Enforce RBAC at query layer: Decision Ledger access controlled by role and tenant mapping.

## Migration path to graph DB

Phase 1 candidate: start with PostgreSQL plus explicit relationship/edge modeling if pilot query patterns justify it.
Phase 2 candidate: if traversals/graph algorithms dominate, consider migrating hot subgraphs to a graph DB while keeping the canonical store stable.

## Operational notes

- Keep `.proto` as source of truth; generate regular dumps to validate DB schema mapping.
- Add retention and TTL policies for cost/metrics if needed.

## Post-Validation Candidate Next Steps

1. Validate at least 3 real Decision ROI Cases without graph/vector infrastructure.
2. Identify traversal or similarity queries that cannot be handled by simple relational modeling.
3. Only then draft SQL/edge modeling or embedding storage plans.
