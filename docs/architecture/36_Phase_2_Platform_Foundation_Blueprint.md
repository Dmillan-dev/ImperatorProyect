# 36 - Phase 2 Platform Foundation Blueprint

Baseline: **v1.0 - Phase 2 Authority**

Freeze rule: this document must not change during Phase 2 unless implementation
proves an objective contradiction.

## Purpose

Define the exact scope of Phase 2 after Phase 1 documentation closure.

Phase 2 is renamed:

```text
Phase 2 - Platform Foundation
```

This name is intentional. Phase 2 is more than folder scaffolding, but it is still not business implementation.

It builds the SaaS technical foundation that future IMPERATOR development will live on.

## Non-Authorization Notice

This document does not create code, schemas, Docker files, services, API handlers, migrations, CI pipelines or infrastructure.

It defines what may be created later if the founder explicitly authorizes Phase 2 implementation.

## Authority

Phase 2 must follow:

1. `docs/decisions/14_Decision_Log.md`
2. `docs/architecture/31_MVP_Implementation_Standard.md`
3. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
4. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
5. `docs/architecture/34_MVP_Implementation_Blueprint.md`
6. `docs/architecture/35_Coding_Principles.md`
7. `docs/architecture/37_Implementation_Contract.md`
8. `agents/phase2/README.md`

If a Phase 2 task needs product behavior, it belongs to Phase 3.

Implementation control:

- `docs/architecture/37_Implementation_Contract.md` governs how implementation is written.
- `agents/phase2/README.md` governs sprint sequencing and one-module agent execution.
- No agent may generate more than one module per iteration.

## Phase 2 Goal

Build the infrastructure of the SaaS without implementing operational intelligence.

Allowed:

- create project structure;
- create architecture boundaries;
- create modules;
- create structural entities;
- create interfaces and ports;
- create API route shells;
- create persistence foundation;
- create auth foundation;
- create observability foundation;
- create local Docker foundation;
- create CI foundation.

Forbidden:

- calculating ROI;
- generating recommendations;
- integrating real AI providers;
- implementing business rules;
- implementing live provider connectors;
- mutating external systems;
- generating fake business data;
- treating stubs as real product behavior.

Canonical Phase 2 rule:

```text
Build the platform foundation, not the intelligence.
```

## Corrections To The Proposed Scope

The founder proposal is directionally correct with these corrections:

| Topic | Correction |
| --- | --- |
| Phase name | Use `Phase 2 - Platform Foundation`, not only Technical Scaffolding. |
| Repository root | The repository already is `imperator/`; do not create a nested duplicate root. |
| Infrastructure folder | Prefer existing `infra/` convention over adding both `infra/` and `infrastructure/`. |
| Database admin UI | Use one DB admin tool in Docker, preferably PgAdmin for PostgreSQL. Do not run PgAdmin and Adminer by default. |
| API stubs | Empty endpoints should return controlled `Not implemented` responses, preferably HTTP `501` with safe JSON. |
| Observability | Local/dev OpenTelemetry, Prometheus and Grafana wiring is allowed, but no production observability platform or business dashboards. |
| AI | Create provider interfaces only. No real OpenAI, Claude, Ollama, Gemini or Azure OpenAI calls in Phase 2. |
| Java/Python communication | FastAPI may exist as Python foundation, but internal Java/Python product calls remain governed by gRPC/Protobuf decisions unless changed later. |
| Entities | Structural domain/persistence types are allowed, but no domain behavior or decision rules. |
| PostgreSQL | Migration framework and V1 schema are allowed when Phase 2 starts; no seed business data. |

## 2.1 Project Scaffolding

Allowed future structure:

```text
imperator/
  backend-java/
  backend-python/
  frontend/
  database/
  infra/
  docs/
  agents/
  scripts/
  proto/
```

Notes:

- `imperator/` means the repository root, not a nested directory.
- `infra/` keeps the existing repository language.
- `proto/` is retained because internal service contracts already have gRPC/Protobuf context.
- `docs/` and `agents/` already exist and remain documentation/control areas.

Allowed:

- create empty project areas;
- add README files explaining ownership;
- add build-system placeholders only when implementation is authorized;
- preserve documentation zones.

Forbidden:

- creating business logic;
- creating live connectors;
- creating demo data that looks like validated evidence;
- creating production deployment assumptions.

## 2.2 Backend Java Foundation

Target:

- Java 21;
- Spring Boot;
- Maven;
- Modular Monolith;
- Hexagonal Architecture / Ports and Adapters.

Allowed modules/boundaries:

- controllers;
- application services;
- domain;
- ports;
- adapters;
- repositories;
- config;
- DTOs;
- security boundary;
- observability boundary.

Allowed behavior:

- application starts;
- health/readiness endpoints exist;
- routes exist as controlled stubs;
- dependency injection works;
- database connectivity can be checked;
- RBAC middleware shape exists.

Forbidden behavior:

- ROI calculation;
- recommendation generation;
- evidence normalization logic;
- approval workflow logic;
- ledger append behavior;
- connector calls;
- AI calls.

## 2.3 Python Foundation

Target:

- FastAPI;
- Explanation Provider interface;
- provider adapter skeletons;
- safe model/DTO boundaries.

Contract note:

- FastAPI is allowed for Python service shell, health/dev surface and provider-boundary organization.
- Internal Java/Python product communication must follow existing gRPC/Protobuf decisions if a real internal call is introduced.
- Phase 2 should avoid product-level Java/Python calls unless needed for foundation verification.

Allowed:

- `ExplanationProvider` conceptual interface;
- stub implementations for future providers;
- endpoint or internal service shell if selected by implementation architecture;
- health endpoint;
- structured logging foundation.

Forbidden:

- real OpenAI calls;
- real Claude calls;
- real Ollama/Gemini/Azure OpenAI calls;
- prompt engineering as product logic;
- ROI calculation;
- recommendation selection;
- persistence mutation of business records.

Phase 2 Python should prove replaceability, not intelligence.

## 2.4 PostgreSQL Foundation

Allowed:

- migration tool setup;
- Migration V1 when implementation is authorized;
- tables from `docs/architecture/34_MVP_Implementation_Blueprint.md`;
- indexes;
- constraints;
- append-only ledger constraints where possible;
- relationship tables for Decision Graph nodes and edges;
- local PostgreSQL container configuration.

Not allowed:

- SQL in this documentation phase;
- seed business data;
- raw prompt or completion tables;
- provider payload dump tables;
- Graph DB replacement;
- Redis/Mongo/SQLite source of truth.

Expected table families:

- identity and roles;
- evidence imports;
- Enterprise Evidence Events;
- evidence summaries;
- Decision ROI Case;
- ROI views and assumptions;
- recommendations;
- AI explanations;
- ledger entries;
- Business Value;
- Decision Graph nodes and edges.

## 2.5 API Foundation

Phase 2 creates route shells only.

Route convention uses plural REST resources, for example `/decisions/{id}`.

Required MVP route shells:

- `POST /evidence/import`
- `GET /decisions`
- `GET /decisions/{id}`
- `GET /decisions/{id}/timeline`
- `GET /decisions/{id}/evidence`
- `GET /decisions/{id}/roi`
- `GET /recommendations/{id}`
- `GET /decisions/{id}/ledger`
- `POST /decisions/{id}/ledger/approve`
- `POST /decisions/{id}/ledger/reject`
- `POST /decisions/{id}/ledger/defer`
- `POST /decisions/{id}/ledger/mark-implemented`
- `POST /decisions/{id}/ledger/validate-result`
- `GET /business-value`
- `GET /ledger`

Expected stub response:

```json
{
  "status": "Not implemented"
}
```

Recommended status:

```text
501 Not Implemented
```

Allowed:

- route registration;
- auth middleware wiring;
- DTO shells;
- safe error format;
- correlation ID propagation.

Forbidden:

- fake ROI values;
- fake recommendation generation;
- fake ledger state;
- hidden business rules;
- OpenAPI expansion beyond the MVP route shell unless explicitly authorized.

## 2.6 React Frontend Foundation

Target:

- React;
- TypeScript;
- Next.js if selected during implementation;
- routing shell;
- layout shell;
- role-aware navigation skeleton.

Allowed:

- app layout;
- sidebar;
- navbar;
- routing;
- theme tokens;
- empty pages;
- placeholder states that clearly say `Not implemented`;
- local API client shell.

Forbidden:

- fake business dashboards;
- calculated ROI in the browser;
- fake recommendations;
- fake evidence timelines;
- broad Executive Dashboard implementation;
- connector administration UX beyond placeholder navigation.

The first real product surface remains Decision Review Workspace in Phase 3.

## 2.7 Authentication Foundation

Target:

- JWT-compatible authentication;
- simple RBAC;
- static MVP roles.

Roles:

- `ADMIN`
- `PLATFORM_ENGINEER`
- `FINANCE`
- `AUDITOR`

Allowed:

- login route shell or demo login boundary;
- JWT parsing/signing foundation;
- role claim shape;
- middleware/filter structure;
- policy constants;
- protected route checks.

Forbidden:

- OAuth provider matrix;
- Google/Microsoft/GitHub login integration;
- Azure AD/Okta/Keycloak integration;
- SAML;
- SCIM;
- policy builder;
- tenant admin console.

The future SSO path must remain replaceable without changing the domain.

## 2.8 Docker Foundation

Docker is allowed from day one of Phase 2 implementation.

Recommended local containers:

- Java backend;
- Python service shell;
- PostgreSQL;
- PgAdmin;
- frontend;
- Prometheus;
- Grafana.

Optional but not default:

- Adminer, only if PgAdmin is removed.

Forbidden:

- Kubernetes;
- Terraform;
- production cloud deployment;
- managed cloud resources;
- secrets committed to repository;
- fake production environments.

Docker Compose is a local developer foundation, not a production architecture decision.

## 2.9 Observability Foundation

Allowed:

- structured logs;
- request logs;
- error logs;
- health checks;
- readiness checks;
- Micrometer/Actuator in Java;
- OpenTelemetry wiring;
- Prometheus scrape configuration;
- Grafana local dashboard shell.

Forbidden:

- full production observability platform;
- SIEM integration;
- business KPI dashboards;
- tracing every internal detail before the MVP flow exists;
- storing sensitive evidence in logs or traces.

Required attitude:

```text
Observable enough to develop and debug; not a platform product yet.
```

## 2.10 CI/CD Foundation

Allowed:

- GitHub Actions workflow skeletons;
- lint;
- unit test command;
- build command;
- Docker build check;
- no-deploy pipeline.

Forbidden:

- production deployment;
- cloud credentials;
- environment promotion;
- release automation;
- infrastructure provisioning.

CI/CD in Phase 2 proves that the foundation builds.

It does not prove the MVP business case.

## Phase 2 Acceptance Gate

Phase 2 is complete only when the foundation exists and still contains no business intelligence.

Acceptance checks:

1. Repository structure exists without nested duplicate root.
2. Java backend starts.
3. Python foundation starts if included.
4. Frontend starts.
5. PostgreSQL starts locally.
6. Migration framework is ready.
7. Route shells exist and return `Not implemented`.
8. JWT/RBAC middleware shape exists.
9. Docker Compose starts local foundation services.
10. Health/readiness endpoints exist.
11. Structured logs and correlation ID foundation exist.
12. Minimal metrics/Prometheus/Grafana local wiring exists.
13. CI can lint/test/build without deployment.
14. No ROI calculation exists.
15. No recommendation generation exists.
16. No real AI provider call exists.
17. No live provider connector exists.
18. No fake ledger, fake evidence or fake Business Value is presented as real.

## Handoff To Phase 3

Phase 3 begins only after Phase 2 foundation passes the gate.

Phase 3 implements the `DRC-AOA-001` value loop from `docs/architecture/34_MVP_Implementation_Blueprint.md`:

```text
Evidence Import
-> Evidence Validation
-> Evidence Store
-> Decision Engine
-> ROI Engine
-> AI Explanation
-> Human Review
-> Decision Ledger
-> Decision Review Workspace
```

Phase 2 creates the stage.

Phase 3 makes the first business case real.
