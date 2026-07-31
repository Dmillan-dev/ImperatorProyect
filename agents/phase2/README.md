# Phase 2 - Platform Foundation Sprint Plan

## Purpose

Define the controlled sprint process for building IMPERATOR's technical foundation after explicit founder authorization.

This file does not authorize code generation by itself.

It explains how to ask agents to create one implementation module at a time while preserving the full project context.

Prompt artifact classification:

- `agents/phase2/SPRINT_ARTIFACT_INDEX.md`

## Authority

Every Phase 2 agent must use:

1. `docs/project/PROJECT_STATUS.md`
2. `docs/project/PHASE_AND_SPRINT_MAP.md`
3. `docs/decisions/14_Decision_Log.md`
4. the task-specific contracts listed in `docs/project/README.md`
5. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
6. `docs/architecture/37_Implementation_Contract.md`
7. `docs/rnd/30_RD_Activity_Evidence_Dossier.md` when real R&D evidence applies

`docs/architecture/38_Sprint_0_Contract_Gate_Report.md` is a historical entry
gate, not current authorization.

If any sprint prompt conflicts with current control or a frozen contract, stop
and request CTO/founder resolution.

## Non-Negotiable Rule

No agent may generate more than one module per iteration.

Do not ask an agent to generate "the backend", "the frontend", "the platform" or "the project".

Ask for one bounded module.

Good:

```text
Generate only the backend-java/domain/evidence module under documents 31-38.
Do not implement any other module.
```

Bad:

```text
Generate the entire MVP backend.
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
- autonomous, AI-driven or ROI-driven recommendation engine behavior,
- real AI calls,
- live Jira/GitHub/AWS/OpenAI/Claude connectors,
- approval workflow business logic,
- fake business evidence,
- fake ledger state,
- production deployment.

## Recommended Sprint Order

The current controlled order is:

1. Sprint 0 - Contract Gate
2. Sprint 1 - Repository and Project Shell
3. Sprint 2.1 - Domain Package Skeleton
4. Sprint 2.2 - Domain Value Objects
5. Sprint 2.3 - Domain Entities
6. Sprint 2.4 - Outbound Ports
7. Sprint 2.5 - Application Layer
8. Sprint 2.6 - Application Contracts
9. Sprint 2.7 - PostgreSQL Persistence Adapter Foundation
10. Sprint 2.8 - REST Adapter
11. Sprint 2.9 - JWT/RBAC Foundation
12. Sprint 2.10 - React Frontend Foundation
13. Sprint 2.11 - Docker Local Foundation
14. Sprint 2.12 - Observability Foundation
15. Sprint 2.13 - CI and R&D Evidence Foundation

Deferred Phase 2 capability:

- Python AI Provider Foundation remains defined by the architecture baseline, but it is not required before Sprint 2.7 persistence, Sprint 2.8 REST, Sprint 2.9 JWT/RBAC or Sprint 2.10 React. It requires explicit founder authorization because the Java application already owns the `ExplanationProvider` port and no real AI integration is allowed in Phase 2.

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

No standalone transaction-boundaries sprint is required before persistence.
Each application use case is reserved as one future transaction boundary.
Concrete transaction settings belong to Sprint 2.7, because PostgreSQL,
persistence adapters and Unit of Work behavior do not exist before then.

Current persistence roadmap:

1. Sprint 2.7.5.1 - Persistence Transaction Contract
2. Sprint 2.7.5.2 - Persistence Transaction Standardization
3. Sprint 2.7.6 - Database Schema and Migrations
4. Sprint 2.7.7 - Persistence Integration Tests
5. Sprint 2.8 - REST Adapter Foundation

Current execution state:

- Sprint 2.7.7 - PostgreSQL persistence certification: complete.
- Sprint 2.8.0 - Spring Boot Web Runtime Foundation: complete.
- Sprint 2.8.1 - REST Error and Correlation Contract: complete.
- Sprint 2.8.2 - Evidence Import Route Shell: complete.
- Sprint 2.8.3 - Decision Collection and Detail Route Shells: complete.
- Sprint 2.8.4 - Decision Context Route Shells: complete.
- Sprint 2.8.5 - Recommendation Detail Route Shell: complete.
- Sprint 2.8.6 - Ledger Read Route Shells: complete.
- Sprint 2.8.7 - Ledger Command Route Shells: complete.
- Sprint 2.8.8 - Business Value Route Shell: complete.
- Next authorized gate: Sprint 2.8.8.2 - REST Adapter Foundation Closure.

Important implementation note:

Some local JDBC transaction handling already exists in PostgreSQL repository
methods that persist more than one record. Sprint 2.7.5.2 must consolidate,
standardize and validate that behavior. It must not introduce Spring,
annotations, domain changes, port changes or a broader transaction framework.

## Sprint Agent Model

From Sprint 1 onward, every sprint uses five specialized roles:

| Role | Writes code | Authority |
|---|---|---|
| Architecture Guardian | No | Vetoes architecture, DDD, dependency or contract violations. |
| Implementation Agent | Yes | Implements only the single assigned deliverable/module. |
| Quality Agent | No product functionality | Reviews naming, complexity, duplication, coupling, dead code and TODOs. |
| Context Keeper | No implementation code | Synchronizes README, Decision Log and implementation-critical docs only. |
| CTO / Product Guardian | No implementation code | Rejects work that does not increase MVP product value. |

Only the Implementation Agent may create implementation files.

Every sprint produces exactly one deliverable.

Every sprint reports ASI using `docs/architecture/37_Implementation_Contract.md`.

Every sprint from Sprint 2.7 onward reports Domain Isolation Index (DII).

Target:

```text
DII: 100%
```

DII is 100% only when `backend-java/domain` imports no adapters, Spring, SQL,
PostgreSQL, JPA, REST, HTTP, provider SDKs or runtime infrastructure.

Every sprint reports Decision Stability.

Every sprint lasts at most one week.

Golden rule:

```text
If an agent wants to create or modify a file outside the sprint deliverable, stop and ask for authorization.
```

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
Review only the context required to authorize Phase 2 Platform Foundation.
Use docs/architecture/31_MVP_Implementation_Standard.md through docs/architecture/37_Implementation_Contract.md.
Do not generate code.
Return go/no-go, the first recommended module and scope risks.
```

## Sprint 1 - Repository and Project Shell

Goal:

Create only the future top-level implementation structure.

Deliverable:

```text
Repository Shell
```

Maximum duration:

```text
One week
```

Allowed modules, one per iteration:

- `repository/root-files`
- `backend-java/`
- `backend-python/`
- `frontend/`
- `database/`
- `infra/`
- `scripts/`
- `proto/` review/alignment only if needed

Allowed files:

- `.gitignore`
- `.editorconfig`
- `LICENSE` with founder-approved text or conservative proprietary placeholder only
- root `README.md` update only if needed to reference the physical structure
- folder-level `README.md` files

Forbidden:

- source logic,
- real services,
- business data,
- Maven,
- Gradle,
- Spring Boot,
- Java source,
- Python source,
- React app,
- package manifests,
- PostgreSQL runtime,
- migrations,
- Docker runtime before Sprint 2.11 unless explicitly scoped to a single shell file.

Example prompt:

```text
Generate only the repository/project-shell module under docs/architecture/31_MVP_Implementation_Standard.md through docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Create only the physical structure and boundary README files.
You may create `.gitignore`, `.editorconfig` and `LICENSE` only when no unapproved open-source license is invented.
Do not implement Maven, Gradle, Spring Boot, Java, Python, React, Docker, PostgreSQL, domain, API, persistence, authentication or observability.
Do not modify other modules.
If you need to create a file that is not explicitly allowed, stop and request authorization.
```

## Sprint 2.1-2.3 - Java Domain Foundation

Status: **COMPLETE**

Goal:

Create the first stable Java domain skeleton before database, API or framework behavior.

Deliverable:

```text
Domain Layer
```

Allowed modules, one per iteration:

- `backend-java/domain/shared`
- `backend-java/domain/evidence`
- `backend-java/domain/decision`
- `backend-java/domain/ledger`
- `backend-java/domain/businessvalue`

`Recommendation` belongs to `imperator.domain.decision`; no parallel
`domain/recommendation` package exists. Identity and application exceptions are
not domain modules.

Forbidden:

- controllers,
- API route shells,
- database/JPA/persistence annotations,
- repositories,
- adapters,
- Spring dependencies inside domain objects,
- ROI rules,
- recommendation engine behavior,
- ledger workflow behavior,
- connector logic,
- real provider calls.

Example prompt:

```text
Generate only the backend-java/domain/evidence module as a Phase 2 domain skeleton.
Follow docs/decisions/14_Decision_Log.md and docs/architecture/31_MVP_Implementation_Standard.md through docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
You may create structural entities or empty interfaces required for compilation.
Do not implement controllers, API, persistence, JPA, Spring annotations, ROI, recommendations, ledger workflow, connectors or AI.
Do not modify any other module.
```

## Sprint 2.4 - Outbound Ports

Status: **COMPLETE**

Goal:

Define the capabilities required from persistence and external providers
without choosing concrete technology.

Implemented module:

- `backend-java/ports/out`

Current contents include repository ports for Evidence, Decision,
Recommendation and Ledger, the Explanation Provider boundary, and the explicit
transaction runner contract introduced by the certified persistence flow.

Forbidden:

- JDBC or PostgreSQL types;
- Spring or framework annotations;
- REST DTOs;
- provider SDKs;
- adapter implementation;
- business behavior.

## Sprint 2.5 - Application Layer Foundation

Status: **COMPLETE**

Goal:

Create application use-case shells after the domain exists and before persistence/API adapters.

Deliverable:

```text
Application Layer
```

Allowed modules, one per iteration:

- `backend-java/application/importevidence`
- `backend-java/application/createdecision`
- `backend-java/application/generaterecommendation`
- `backend-java/application/reviewdecision`
- `backend-java/application/appendledgerentry`
- `backend-java/application/exceptions`

Forbidden:

- controllers,
- HTTP request handling,
- PostgreSQL/JPA,
- provider SDKs,
- ROI calculation,
- autonomous, AI-driven or ROI-driven recommendation engine behavior,
- approval workflow business behavior,
- ledger mutation from adapters or infrastructure.

Example prompt:

```text
Generate only the backend-java/application/importevidence module as a Phase 2 use-case shell.
Follow docs/product/CORE_DOMAIN_MODEL.md, docs/product/API_SPECIFICATION.md and docs/architecture/31_MVP_Implementation_Standard.md through docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Create only contracts, commands or interfaces required for compilation when the domain exists.
Do not implement API, database behavior, ROI, recommendations, connectors, AI or a real ledger.
Do not modify any other module.
```

## Sprint 2.6 - Application Contracts

Status: **COMPLETE**

Goal:

Close the public application contract before adapters.

Deliverable:

```text
Application Contracts
```

Allowed modules, one per iteration:

- `backend-java/ports/in`
- `backend-java/application/exceptions`
- `backend-java/application/APPLICATION_DATA_BOUNDARY_POLICY.md`

Forbidden:

- REST controllers,
- HTTP request or response DTOs,
- mappers,
- PostgreSQL/JPA,
- transaction framework,
- provider SDKs,
- domain changes for adapter convenience.

Example prompt:

```text
Generate only the backend-java/ports/in module.
Follow docs/architecture/35_Coding_Principles.md, docs/architecture/37_Implementation_Contract.md and backend-java/application/APPLICATION_DATA_BOUNDARY_POLICY.md.
Do not implement REST, HTTP DTOs, mappers, persistence, Spring, JPA or domain changes.
Do not modify any other module.
```

## Sprint 2.7 - PostgreSQL Persistence Adapter Foundation

Status: **CERTIFIED**

Goal:

Create PostgreSQL persistence adapter foundation after domain and application ports exist.

This is the first sprint where concrete transaction behavior may be decided,
because PostgreSQL and persistence adapters finally exist.

Deliverable:

```text
Persistence Adapter
```

Allowed modules, one per iteration:

- `backend-java/adapters/out/postgresql`
- `backend-java/adapters/out/postgresql/model`
- `backend-java/adapters/out/postgresql/mapper`
- repository and transaction classes directly under
  `backend-java/adapters/out/postgresql`
- `database/migrations`
- `src/test/java/imperator/adapters/out/postgresql`

Forbidden:

- sample business records,
- fake ledger entries,
- stored secrets,
- denormalized analytics tables not needed by the MVP contract,
- database-first domain changes,
- domain changes for PostgreSQL convenience,
- Graph DB infrastructure.

Example prompt:

```text
Generate only the backend-java/adapters/out/postgresql/model module.
Use PostgreSQL as an adapter and follow docs/architecture/DATABASE_MODEL.md, docs/architecture/34_MVP_Implementation_Blueprint.md, docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md, backend-java/application/APPLICATION_DATA_BOUNDARY_POLICY.md and docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Do not modify Domain, Application or Ports to accommodate PostgreSQL.
No crees SQL, migraciones, consultas, JPA, JDBC, Spring, repositorios reales, mappers ni datos seed.
```

### Sprint 2.7.4 - PostgreSQL Repository Implementations

Goal:

Implement repository adapters in separate microdeliverables without changing
the domain for PostgreSQL.

Microdeliverables:

1. Sprint 2.7.4.1 - `PostgresEvidenceRepository`
2. Sprint 2.7.4.2 - `PostgresDecisionRepository`
3. Sprint 2.7.4.3 - `PostgresRecommendationRepository`
4. Sprint 2.7.4.4 - `PostgresLedgerRepository`

Repository Minimalism Rule:

- repositories may only persist, retrieve or answer exact existence checks;
- repositories must not calculate ROI, validate business rules, create entities
  except by mapper reconstruction from persisted records, execute use cases,
  generate recommendations, call AI providers, publish events or construct
  DTOs;
- query behavior must be limited to exact queries already required by ports;
- broad search, filtering, dashboard aggregation and analytics belong to future
  query/projection ports, not MVP repositories.

Ledger Append Rule:

- `LedgerRepository` uses `append`;
- `LedgerRepository` must not expose `save`, `update` or `delete`;
- ledger corrections must be modeled as new appended entries.

Example prompt:

```text
Generate only `backend-java/adapters/out/postgresql/PostgresEvidenceRepository.java`.
Follow the Repository Minimalism Rule, Mapper Purity Rule, Domain Translation Rule and DII 100%.
Implement only save, findById and existsById as defined by the existing port.
Do not implement DecisionRepository, RecommendationRepository, LedgerRepository, REST, Spring controllers, JWT, ROI, recommendations, AI, events, DTOs or domain changes.
If PostgreSQL would require a domain change, stop and request authorization.
```

### Sprint 2.7.5 - Persistence Transactions

Goal:

Define and implement persistence transaction handling in controlled
microdeliverables after repository foundation is complete.

Microdeliverables:

1. Sprint 2.7.5.1 - Persistence Transaction Contract
2. Sprint 2.7.5.2 - Persistence Transaction Standardization

Sprint 2.7.5.1 status:

- `docs/architecture/39_Persistence_Transaction_Contract.md`
- no code,
- no domain changes,
- no port changes,
- no adapter changes,
- logical atomicity only.

Sprint 2.7.5.2 allowed scope:

- standardize local JDBC transaction handling inside the PostgreSQL adapter;
- ensure multi-record aggregate writes are atomic;
- ensure single-record writes use the same local transaction boundary where they
  represent a write use case;
- preserve the logical boundaries in
  `docs/architecture/39_Persistence_Transaction_Contract.md`;
- avoid changing domain, application use cases, ports or mapper contracts.

Sprint 2.7.5.2 forbidden scope:

- Spring,
- `@Transactional`,
- isolation-level policy,
- propagation policy,
- new Unit of Work abstraction,
- database schema changes,
- migrations,
- integration tests,
- REST adapter work,
- domain changes,
- port changes.

Next order after Sprint 2.7.5:

1. Sprint 2.7.6 - Database Schema and Migrations
2. Sprint 2.7.7 - Persistence Integration Tests
3. Sprint 2.8 - REST Adapter Foundation

## Sprint 2.8 - REST Adapter

Goal:

Expose documented REST route shells that return controlled not-implemented responses.

Deliverable:

```text
API Route Shells
```

Allowed modules, one per iteration:

- `backend-java/api/errors` - completed in Sprint 2.8.1
- `backend-java/api/evidence` - completed in Sprint 2.8.2
- `backend-java/api/decisions` - collection/detail completed in Sprint 2.8.3;
  context routes completed in Sprint 2.8.4
- `backend-java/api/recommendations` - detail route completed in Sprint 2.8.5
- `backend-java/api/ledger` - read routes completed in Sprint 2.8.6;
  command routes completed in Sprint 2.8.7
- `backend-java/api/businessvalue` - completed in Sprint 2.8.8
- `backend-java/api/pagination` - pending explicit closure disposition

Java module and package names must be valid identifiers. Therefore the module
uses `businessvalue`, while the public HTTP route remains `/business-value`.
All REST packages use the accepted `imperator.api.*` namespace and all Phase
2.8 product routes are mounted under `/api/v1`.

Forbidden:

- business orchestration,
- repository calls that imply behavior,
- ROI computation,
- approval workflow behavior,
- autonomous, AI-driven or ROI-driven recommendation engine behavior.

Example prompt:

```text
Certify the completed Sprint 2.8 REST Adapter Foundation without modifying
Java, tests, SQL, dependencies or frozen contracts.
Follow docs/product/API_SPECIFICATION.md and docs/architecture/34_MVP_Implementation_Blueprint.md through docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Verify the 15-route MVP inventory, error and correlation contracts, /api/v1
versioning, HTTP tests and negative boundaries. Record Functional REST, DTOs,
validation, pagination, security and Application wiring as not started or
explicitly deferred. Do not implement product behavior.
```

## Sprint 2.9 - JWT/RBAC Foundation

Goal:

Create authentication and role middleware foundation without OAuth providers.

Deliverable:

```text
Auth Foundation
```

Allowed modules, one per iteration:

- `backend-java/security/jwt`
- `backend-java/security/rbac`
- `backend-java/security/errorhandling`
- `backend-java/security/audit`
- `frontend/auth-shell`

Forbidden:

- Google/Microsoft/GitHub OAuth,
- approval authority changes,
- hardcoded production credentials,
- storing secrets,
- business workflow permissions beyond MVP role shell.

Example prompt:

```text
Generate only the backend-java/security/rbac module.
Follow docs/product/28_Identity_Access_Approval_Model.md and docs/architecture/31_MVP_Implementation_Standard.md through docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Implement only the Phase 2 RBAC foundation.
Do not implement business approval or ledger workflow behavior.
```

## Deferred - Python AI Provider Foundation

Goal:

Create FastAPI and Explanation Provider foundation without real AI integration.

Deliverable:

```text
AI Provider Foundation
```

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
Generate only the backend-python/explanation-provider-port module.
Follow docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md, docs/architecture/35_Coding_Principles.md, docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md and docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Do not make real calls to any AI provider.
Do not implement business prompts.
```

## Sprint 2.10 - React Frontend Foundation

Goal:

Create the Next.js/React application shell without product logic.

Deliverable:

```text
Frontend Shell
```

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
Generate only the frontend/layout module.
Follow docs/product/29_Decision_Review_Workspace_Screen_Contract.md and docs/architecture/35_Coding_Principles.md through docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Create only layout, sidebar/navigation and slots.
Do not implement business data, real API calls or ROI logic.
```

## Sprint 2.11 - Docker Local Foundation

Goal:

Create local developer orchestration for foundation services.

Deliverable:

```text
Docker Local Foundation
```

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
Generate only the infra/docker/postgres module.
Follow docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md and docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Do not generate Kubernetes, Terraform, seed data or real credentials.
Do not modify other Docker modules.
```

## Sprint 2.12 - Observability Foundation

Goal:

Create minimal observability wiring from day one.

Deliverable:

```text
Observability Foundation
```

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
Generate only the backend-java/observability/health module.
Follow docs/architecture/27_Quality_Attributes.md, docs/architecture/35_Coding_Principles.md, docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md and docs/architecture/38_Sprint_0_Contract_Gate_Report.md.
Implement only `/health` and `/ready` foundation when the backend exists.
Do not implement business metrics.
```

## Sprint 2.13 - CI and R&D Evidence Foundation

Goal:

Create build verification and development evidence discipline.

Deliverable:

```text
CI and R&D Evidence Foundation
```

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
Generate only the `.github/workflows/backend-java.yml` workflow.
Follow docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md, docs/architecture/37_Implementation_Contract.md, docs/architecture/38_Sprint_0_Contract_Gate_Report.md and docs/rnd/30_RD_Activity_Evidence_Dossier.md.
Include only build, test and lint foundation.
Do not generate deployment behavior or credentials.
```

## Standard Agent Prompt Template

Use this pattern for every implementation iteration:

```text
Generate only the <MODULE> module.

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
- No recommendations.
- No IA real.
- No live connectors.
- No datos fake de negocio.

Restricciones:
- Do not modify other modules.
- Do not generate more than one module.
- Do not change product or architecture decisions.
- No anadas dependencias innecesarias.
- No guardes secretos.

Entrega:
- cambios realizados,
- ficheros tocados,
- pruebas o checks ejecutados,
- riesgos pendientes,
- R&D evidence that must be recorded when applicable.
```

## Sprint Acceptance Gate

A sprint is accepted only when:

1. every module was generated in a separate iteration,
2. exactly one sprint deliverable was produced,
3. the one-module rule was respected,
4. Architecture Guardian did not veto,
5. Quality Agent did not find blocking debt,
6. CTO / Product Guardian accepted MVP value alignment,
7. ASI target was met,
8. Decision Stability target was met,
9. sprint duration was one week or less,
10. the sprint created foundation only,
11. checks passed or failures are documented,
12. no authority document was contradicted,
13. R&D evidence is updated only for real work performed,
14. next sprint can start without agents needing to invent context.

Every sprint must end with:

| Criterion | Status |
|---|---|
| Builds/compiles or valid no-code equivalent | Pending |
| Tests/checks pass or valid no-test justification | Pending |
| Architecture respected | Pending |
| No critical technical debt | Pending |
| No dead code | Pending |
| No unresolved TODOs | Pending |
| Documentation synchronized | Pending |
| ASI target met | Pending |
| DII target met | Pending |
| Decision Stability target met | Pending |
| Sprint duration within one week | Pending |

No sprint may begin unless the previous sprint is fully green or explicitly waived by founder/CTO.

Mandatory final status:

```text
STATUS: PASS

Architecture Guardian: PASS
Quality Agent: PASS
Context Keeper: PASS
Product Guardian: PASS
Implementation Agent: PASS
ASI: <score>%
DII: <score>%
Decision Stability: <number of prior decisions modified>
```

If any line fails, the sprint is not complete.

## Executability Gate

Every sprint that creates or modifies executable code must pass this gate before
it can be accepted.

| Criterion | Required status |
|---|---|
| Compiles/builds | PASS |
| Tests pass | PASS when a test harness exists; otherwise explicit founder/CTO waiver required |
| Module coverage minimum | PASS once coverage tooling exists; otherwise explicit founder/CTO waiver required |
| No critical warnings | PASS |
| No new critical technical debt | PASS |
| Architecture respected | PASS |

Rules:

- compilation is mandatory for every executable Java sprint;
- test and coverage gaps must be visible, not hidden;
- from Sprint 2.7.7 onward, persistence code must have integration-test
  coverage before the persistence foundation can close;
- a sprint cannot be marked PASS if an executable-code failure is unresolved;
- waivers must be exceptional and recorded in the sprint closeout.

## Phase 2 Completion Gate

Phase 2 is complete only when:

- Java domain foundation exists,
- application layer foundation exists,
- PostgreSQL persistence adapter foundation exists,
- API route shells exist,
- React shell exists,
- JWT/RBAC foundation exists,
- Python provider foundation exists or has explicit founder/CTO deferral,
- Docker local foundation exists,
- observability foundation exists,
- CI foundation exists,
- no ROI or recommendation logic exists,
- no real AI/provider connector exists,
- no business data has been invented.

After this gate, Phase 3 may implement the single `DRC-AOA-001` Decision ROI Case.
