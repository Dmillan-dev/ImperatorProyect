# Phase 2 - Platform Foundation Sprint Plan

## Purpose

Define the controlled sprint process for building IMPERATOR's technical foundation after explicit founder authorization.

This file does not authorize code generation by itself.

It explains how to ask agents to create one implementation module at a time while preserving the full project context.

## Authority

Every Phase 2 agent must use:

1. `docs/decisions/14_Decision_Log.md`
2. `docs/architecture/31_MVP_Implementation_Standard.md`
3. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
4. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
5. `docs/architecture/34_MVP_Implementation_Blueprint.md`
6. `docs/architecture/35_Coding_Principles.md`
7. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
8. `docs/architecture/37_Implementation_Contract.md`
9. `docs/architecture/38_Sprint_0_Contract_Gate_Report.md`
10. `docs/rnd/30_RD_Activity_Evidence_Dossier.md`

If any sprint prompt conflicts with these documents, stop and request CTO/founder resolution.

## Non-Negotiable Rule

No agent may generate more than one module per iteration.

Do not ask an agent to generate "the backend", "the frontend", "the platform" or "the project".

Ask for one bounded module.

Good:

```text
Genera unicamente el modulo backend-java/domain/evidence respetando los documentos 31-38.
No implementes ningun otro modulo.
```

Bad:

```text
Genera todo el backend del MVP.
```

## Phase 2 Boundary

Phase 2 may create:

- project structure,
- build configuration,
- package/module skeletons,
- route shells,
- persistence foundation,
- auth foundation,
- observability foundation,
- Docker local foundation,
- CI foundation.

Phase 2 must not create:

- ROI calculation,
- recommendation generation,
- real AI calls,
- live Jira/GitHub/AWS/OpenAI/Claude connectors,
- approval workflow business logic,
- fake business evidence,
- fake ledger state,
- production deployment.

## Recommended Sprint Order

The corrected order is:

1. Sprint 0 - Contract Gate
2. Sprint 1 - Repository and Project Shell
3. Sprint 2 - Java Domain Foundation
4. Sprint 3 - Application Layer Foundation
5. Sprint 4 - PostgreSQL Foundation
6. Sprint 5 - API Route Shells
7. Sprint 6 - JWT/RBAC Foundation
8. Sprint 7 - Python AI Provider Foundation
9. Sprint 8 - React Frontend Foundation
10. Sprint 9 - Docker Local Foundation
11. Sprint 10 - Observability Foundation
12. Sprint 11 - CI and R&D Evidence Foundation

This order is domain-first.

IMPERATOR is not a CRUD SaaS. Its value is the core flow:

```text
Evidence
-> Decision
-> Recommendation
-> Human Approval
-> Ledger
-> ROI / Business Value
```

The implementation must shape PostgreSQL, API, security and UI around the domain, not the other way around.

## Sprint 0 - Contract Gate

Goal:

Confirm that implementation is authorized and bounded.

Allowed output:

- no code,
- explicit go/no-go note,
- selected first module,
- confirmation that documents 31-37 are authority.

Forbidden:

- creating project files,
- creating package manifests,
- creating Docker files,
- generating source code.

Example agent prompt:

```text
Revisa unicamente el contexto para autorizar Phase 2 Platform Foundation.
Usa docs/architecture/31_MVP_Implementation_Standard.md hasta docs/architecture/37_Implementation_Contract.md.
No generes codigo.
Devuelve go/no-go, primer modulo recomendado y riesgos de alcance.
```

## Sprint 1 - Repository and Project Shell

Goal:

Create only the future top-level implementation structure.

Allowed modules, one per iteration:

- `backend-java/`
- `backend-python/`
- `frontend/`
- `database/`
- `infra/`
- `scripts/`
- `proto/` review/alignment only if needed

Forbidden:

- source logic,
- real services,
- business data,
- Docker runtime before Sprint 8 unless explicitly scoped to a single shell file.

Example prompt:

```text
Genera unicamente el modulo backend-java/project-shell respetando docs/architecture/31_MVP_Implementation_Standard.md a docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
No implementes dominio, API, persistencia, auth, observabilidad ni Docker.
No modifiques otros modulos.
```

## Sprint 2 - Java Domain Foundation

Goal:

Create the first stable Java domain skeleton before database, API or framework behavior.

Allowed modules, one per iteration:

- `backend-java/domain/shared`
- `backend-java/domain/evidence`
- `backend-java/domain/decision`
- `backend-java/domain/recommendation`
- `backend-java/domain/ledger`
- `backend-java/domain/business-value`
- `backend-java/domain/identity`
- `backend-java/domain/exceptions`

Forbidden:

- controllers,
- API route shells,
- database/JPA/persistence annotations,
- repositories,
- adapters,
- Spring dependencies inside domain objects,
- ROI rules,
- recommendation generation,
- ledger workflow behavior,
- connector logic,
- real provider calls.

Example prompt:

```text
Genera unicamente el modulo backend-java/domain/evidence como skeleton de dominio de Phase 2.
Respeta docs/decisions/14_Decision_Log.md y docs/architecture/31_MVP_Implementation_Standard.md a docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Puedes crear entidades estructurales o interfaces vacias necesarias para compilar.
No implementes controllers, API, persistencia, JPA, Spring annotations, ROI, recomendaciones, ledger workflow, conectores ni IA.
No modifiques ningun otro modulo.
```

## Sprint 3 - Application Layer Foundation

Goal:

Create application use-case shells after the domain exists and before persistence/API adapters.

Allowed modules, one per iteration:

- `backend-java/application/import-evidence`
- `backend-java/application/create-decision`
- `backend-java/application/review-decision`
- `backend-java/application/approve-decision`
- `backend-java/application/reject-decision`
- `backend-java/application/defer-decision`
- `backend-java/application/shared`
- `backend-java/ports/in`
- `backend-java/ports/out`

Forbidden:

- controllers,
- HTTP request handling,
- PostgreSQL/JPA,
- provider SDKs,
- ROI calculation,
- recommendation generation,
- approval workflow business behavior,
- real ledger mutation.

Example prompt:

```text
Genera unicamente el modulo backend-java/application/import-evidence como use-case shell de Phase 2.
Respeta docs/product/CORE_DOMAIN_MODEL.md, docs/product/API_SPECIFICATION.md y docs/architecture/31_MVP_Implementation_Standard.md a docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Crea solo contratos, comandos o interfaces necesarias para compilar si el dominio existe.
No implementes API, base de datos, ROI, recomendaciones, conectores, IA ni ledger real.
No modifiques ningun otro modulo.
```

## Sprint 4 - PostgreSQL Foundation

Goal:

Create database migration foundation and V1 schema boundary without seed business data.

Allowed modules, one per iteration:

- `database/migration-foundation`
- `database/schema-v1-evidence`
- `database/schema-v1-decisions`
- `database/schema-v1-ledger`
- `database/schema-v1-business-value`
- `database/schema-v1-identity`
- `database/schema-v1-decision-graph`

Forbidden:

- sample business records,
- fake ledger entries,
- stored secrets,
- denormalized analytics tables not needed by the MVP contract,
- Graph DB infrastructure.

Example prompt:

```text
Genera unicamente el modulo database/schema-v1-evidence.
Usa PostgreSQL y respeta docs/architecture/DATABASE_MODEL.md, docs/architecture/34_MVP_Implementation_Blueprint.md, docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md y docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
No crees datos seed.
No implementes repositories ni servicios.
```

## Sprint 5 - API Route Shells

Goal:

Expose documented REST route shells that return controlled not-implemented responses.

Allowed modules, one per iteration:

- `backend-java/api/evidence`
- `backend-java/api/decisions`
- `backend-java/api/ledger`
- `backend-java/api/business-value`
- `backend-java/api/errors`
- `backend-java/api/pagination`

Forbidden:

- business orchestration,
- repository calls that imply behavior,
- ROI computation,
- approval workflow behavior,
- recommendation generation.

Example prompt:

```text
Genera unicamente el modulo backend-java/api/decisions con route shells.
Respeta docs/product/API_SPECIFICATION.md y docs/architecture/34_MVP_Implementation_Blueprint.md a docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Los endpoints deben devolver respuesta controlada Not implemented.
No llames repositorios, motores ROI, IA ni ledger real.
```

## Sprint 6 - JWT/RBAC Foundation

Goal:

Create authentication and role middleware foundation without OAuth providers.

Allowed modules, one per iteration:

- `backend-java/security/jwt`
- `backend-java/security/rbac`
- `backend-java/security/error-handling`
- `backend-java/security/audit-shell`
- `frontend/auth-shell`

Forbidden:

- Google/Microsoft/GitHub OAuth,
- approval authority changes,
- hardcoded production credentials,
- storing secrets,
- business workflow permissions beyond MVP role shell.

Example prompt:

```text
Genera unicamente el modulo backend-java/security/rbac.
Respeta docs/product/28_Identity_Access_Approval_Model.md y docs/architecture/31_MVP_Implementation_Standard.md a docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Implementa solo foundation RBAC de Phase 2.
No implementes aprobacion de negocio ni workflow de ledger.
```

## Sprint 7 - Python AI Provider Foundation

Goal:

Create FastAPI and Explanation Provider foundation without real AI integration.

Allowed modules, one per iteration:

- `backend-python/project-foundation`
- `backend-python/explanation-provider-port`
- `backend-python/provider-placeholder-openai`
- `backend-python/provider-placeholder-claude`
- `backend-python/provider-placeholder-ollama`
- `backend-python/api-health`
- `backend-python/observability-shell`

Forbidden:

- real OpenAI/Claude/Ollama/Gemini/Azure OpenAI calls,
- prompt logic that decides recommendations,
- raw evidence access,
- persistent data mutation,
- business rule execution.

Example prompt:

```text
Genera unicamente el modulo backend-python/explanation-provider-port.
Respeta docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md, docs/architecture/35_Coding_Principles.md, docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md y docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
No hagas llamadas reales a ningun proveedor IA.
No implementes prompts de negocio.
```

## Sprint 8 - React Frontend Foundation

Goal:

Create the Next.js/React application shell without product logic.

Allowed modules, one per iteration:

- `frontend/project-foundation`
- `frontend/layout`
- `frontend/navigation`
- `frontend/theme`
- `frontend/routes-dashboard`
- `frontend/routes-decisions`
- `frontend/routes-ledger`
- `frontend/routes-business-value`
- `frontend/api-client-shell`

Forbidden:

- ROI calculations in UI,
- recommendation selection,
- fake business state,
- direct provider calls,
- direct database calls,
- approval workflow mutation logic.

Example prompt:

```text
Genera unicamente el modulo frontend/layout.
Respeta docs/product/29_Decision_Review_Workspace_Screen_Contract.md y docs/architecture/35_Coding_Principles.md a docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Crea solo layout, sidebar/navbar y slots.
No implementes datos de negocio, llamadas reales API ni logica ROI.
```

## Sprint 9 - Docker Local Foundation

Goal:

Create local developer orchestration for foundation services.

Allowed modules, one per iteration:

- `infra/docker/backend-java`
- `infra/docker/backend-python`
- `infra/docker/frontend`
- `infra/docker/postgres`
- `infra/docker/pgadmin`
- `infra/docker/prometheus`
- `infra/docker/grafana`
- `infra/docker/compose-root`

Forbidden:

- Kubernetes,
- Terraform,
- cloud deployment,
- production secrets,
- live provider connectors,
- business seed data.

Example prompt:

```text
Genera unicamente el modulo infra/docker/postgres.
Respeta docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md y docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
No generes Kubernetes, Terraform, datos seed ni credenciales reales.
No modifiques otros modulos Docker.
```

## Sprint 10 - Observability Foundation

Goal:

Create minimal observability wiring from day one.

Allowed modules, one per iteration:

- `backend-java/observability/logging`
- `backend-java/observability/metrics`
- `backend-java/observability/tracing`
- `backend-java/observability/health`
- `backend-python/observability`
- `infra/observability/prometheus`
- `infra/observability/grafana`

Forbidden:

- business dashboards,
- analytics claims,
- fake ROI metrics,
- alerting platforms beyond local foundation,
- PII in logs.

Example prompt:

```text
Genera unicamente el modulo backend-java/observability/health.
Respeta docs/architecture/27_Quality_Attributes.md, docs/architecture/35_Coding_Principles.md, docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md y docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Implementa solo /health y /ready foundation si el backend existe.
No implementes metricas de negocio.
```

## Sprint 11 - CI and R&D Evidence Foundation

Goal:

Create build verification and development evidence discipline.

Allowed modules, one per iteration:

- `.github/workflows/backend-java`
- `.github/workflows/backend-python`
- `.github/workflows/frontend`
- `.github/workflows/docker-build`
- `scripts/verification`
- `docs/rnd/logs/phase2-foundation`

Forbidden:

- production deployment,
- cloud credentials,
- synthetic accreditation claims,
- invented hours,
- invented test results.

Example prompt:

```text
Genera unicamente el modulo .github/workflows/backend-java.
Respeta docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md, docs/architecture/38_Sprint_0_Contract_Gate_Report.md y docs/rnd/30_RD_Activity_Evidence_Dossier.md.
Incluye solo build/test/lint foundation.
No generes despliegue ni credenciales.
```

## Standard Agent Prompt Template

Use this pattern for every implementation iteration:

```text
Genera unicamente el modulo <MODULE>.

Autoridad obligatoria:
- docs/decisions/14_Decision_Log.md
- docs/architecture/31_MVP_Implementation_Standard.md
- docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md
- docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md
- docs/architecture/34_MVP_Implementation_Blueprint.md
- docs/architecture/35_Coding_Principles.md
- docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md
- docs/architecture/37_Implementation_Contract.md
- docs/architecture/38_Sprint_0_Contract_Gate_Report.md

Alcance:
- Phase 2 Platform Foundation.
- No business intelligence.
- No ROI.
- No recomendaciones.
- No IA real.
- No conectores live.
- No datos fake de negocio.

Restricciones:
- No modifiques otros modulos.
- No generes mas de un modulo.
- No cambies decisiones de producto o arquitectura.
- No anadas dependencias innecesarias.
- No guardes secretos.

Entrega:
- cambios realizados,
- ficheros tocados,
- pruebas o checks ejecutados,
- riesgos pendientes,
- evidencia R&D que debe registrarse si aplica.
```

## Sprint Acceptance Gate

A sprint is accepted only when:

1. every module was generated in a separate iteration,
2. the one-module rule was respected,
3. the sprint created foundation only,
4. checks passed or failures are documented,
5. no authority document was contradicted,
6. R&D evidence is updated only for real work performed,
7. next sprint can start without agents needing to invent context.

## Phase 2 Completion Gate

Phase 2 is complete only when:

- Java domain foundation exists,
- application layer foundation exists,
- Python provider foundation exists,
- PostgreSQL foundation exists,
- API route shells exist,
- React shell exists,
- JWT/RBAC foundation exists,
- Docker local foundation exists,
- observability foundation exists,
- CI foundation exists,
- no ROI or recommendation logic exists,
- no real AI/provider connector exists,
- no business data has been invented.

After this gate, Phase 3 may implement the single `DRC-AOA-001` Decision ROI Case.
