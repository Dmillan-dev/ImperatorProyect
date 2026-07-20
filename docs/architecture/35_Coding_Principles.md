# 35 - Coding Principles

## Purpose

Define the coding rules that future IMPERATOR agents and developers must follow once implementation is explicitly authorized.

This document does not authorize code creation by itself.

It exists to prevent future agents from turning the MVP into an uncontrolled platform, mixing adapters with domain logic or placing AI behavior inside business rules.

## Authority

This document must respect:

1. `docs/decisions/14_Decision_Log.md`
2. `docs/architecture/31_MVP_Implementation_Standard.md`
3. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
4. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
5. `docs/architecture/34_MVP_Implementation_Blueprint.md`
6. `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`

Implementation mechanics and one-module agent execution are governed by:

- `docs/architecture/37_Implementation_Contract.md`
- `agents/phase2/README.md`

When this document conflicts with the Decision Log, the Decision Log wins.

## Core Rule

The domain must remain independent from frameworks, providers and persistence.

Canonical direction:

```text
Controller
-> Application Service
-> Domain
-> Port
-> Adapter
```

Examples:

- GitHub is an adapter.
- Jira is an adapter.
- AWS is an adapter.
- OpenAI is an adapter.
- Anthropic Claude is an adapter.
- PostgreSQL is an adapter.
- JWT is an adapter/boundary concern.
- React is a presentation layer, not domain.

The domain must not know which adapter exists behind a port.

## Global Principles

All implementation must be:

- deterministic where business state is produced;
- testable without real external providers;
- observable without leaking secrets or Restricted data;
- explicit about ownership, role and authority;
- small enough to serve one Decision ROI Case first;
- replaceable at provider boundaries;
- safe for future autonomous agents to extend.

## Always

Use this structure for backend use cases:

```text
HTTP request
-> Controller
-> Command or query DTO
-> Application Service
-> Domain object / domain rule
-> Port interface
-> Adapter implementation
-> Repository or provider
```

Always:

- keep controllers thin;
- put use-case orchestration in application services;
- keep domain objects free from framework annotations when possible;
- define ports before adapters;
- keep DTOs at boundaries;
- validate external input before it reaches domain rules;
- attach correlation IDs to request, import, AI explanation and ledger paths;
- log structured events, not free-form debugging noise;
- keep Restricted data out of logs, AI context and ledger snapshots;
- write tests around domain rules, ports and application flows.

## Never

Never place:

- business logic in controllers;
- SQL in controllers;
- SQL in application services;
- DTOs inside the domain model;
- Spring, FastAPI, React or provider SDK types inside domain objects;
- HTTP calls inside entities;
- provider-specific payloads as domain models;
- AI provider calls inside deterministic business rules;
- recommendation selection inside the AI layer;
- ROI calculation inside connectors;
- ledger writes inside connectors;
- approval authority inside connectors or AI;
- mutable history inside the Decision Ledger;
- raw prompts, completions, secrets or provider dumps in persistence;
- Kafka, Kubernetes, Terraform, Graph DB, vector DB or search infrastructure inside the MVP foundation unless a later decision explicitly authorizes it.

## Java Backend Rules

The Java backend direction is:

- Java 21;
- Spring Boot;
- Maven;
- Modular Monolith;
- Hexagonal Architecture / Ports and Adapters;
- PostgreSQL as persistence adapter;
- JWT-compatible auth boundary;
- Micrometer/Actuator observability boundary.

Controllers may:

- receive HTTP requests;
- validate syntactic request shape;
- map request DTOs to commands or queries;
- call application services;
- map results to response DTOs.

Controllers must not:

- calculate ROI;
- generate recommendations;
- call AI providers;
- perform SQL;
- decide approval rules directly;
- mutate ledger history directly.

Application services may:

- orchestrate one use case;
- call domain rules;
- call ports;
- manage transactions;
- emit safe structured logs.

Application services must not:

- contain provider-specific SDK code;
- expose raw provider payloads;
- invent missing evidence;
- treat AI text as business truth.

Domain objects may:

- express business concepts;
- enforce invariants;
- expose deterministic calculations through domain services when appropriate;
- reference other domain IDs.

Domain objects must not:

- call repositories;
- call HTTP clients;
- call AI providers;
- know PostgreSQL tables;
- know JWT claims;
- know React routes.

Repositories are ports from the application/domain side and adapters on the infrastructure side.

## Python Foundation Rules

The Python direction is:

- FastAPI;
- provider interfaces;
- Explanation Provider boundary;
- no real AI calls during Phase 2;
- no business decision authority.

Python may eventually host AI explanation adapters.

FastAPI does not override the existing internal gRPC/Protobuf mandate.

If Java must call Python as an internal product service, that contract must follow the recorded gRPC/Protobuf decisions unless a later decision changes them.

Python must not:

- calculate ROI as source of truth;
- select recommendations;
- approve, reject or defer;
- mutate Decision Ledger entries;
- access raw prompts or raw completions from evidence;
- become required for deterministic recommendation generation.

The conceptual AI boundary remains:

```text
Recommendation
-> Prepared context
-> Explanation Provider
-> Human-readable explanation
```

## Frontend Rules

The frontend direction is:

- React;
- Next.js if the implementation phase confirms it;
- TypeScript;
- one Decision Review Workspace first;
- role-aware UI states.

Frontend may:

- render decisions, evidence summaries, ROI view, recommendation, AI explanation and ledger history;
- show role-aware action availability;
- display safe errors and blockers.

Frontend must not:

- calculate authoritative ROI;
- generate recommendations;
- invent evidence;
- bypass server-side authorization;
- show Restricted data;
- treat hidden buttons as security;
- become a broad Executive Dashboard before the MVP flow works.

## Database Rules

PostgreSQL is the source of truth from the first implementation day.

Database implementation must support:

- Enterprise Evidence Events;
- evidence summaries;
- Decision ROI Case;
- deterministic ROI outputs;
- one recommendation;
- AI explanation output;
- append-only ledger entries;
- Business Value only after result validation;
- Decision Graph nodes and edges as relational records.

Database implementation must not use:

- in-memory store as source of truth;
- JSON files as source of truth;
- SQLite;
- MongoDB;
- Redis;
- Graph DB for MVP source of truth.

The Decision Ledger must be append-only in behavior.

Historical ledger rows must not be updated or deleted by normal application flows.

## API Rules

Public MVP REST endpoints are high-level product contracts, not internal domain structures.

For Phase 2 Platform Foundation, endpoints may exist as empty controlled stubs only.

Empty endpoints must:

- return a safe `Not implemented` response;
- enforce basic route shape;
- avoid business logic;
- avoid fake ROI, fake recommendation or fake ledger state.

Do not expose gRPC as the public SaaS API.

Internal service-to-service communication guidance remains governed by the existing gRPC/Protobuf decisions and RFC context.

Do not introduce ad hoc internal HTTP contracts between Java and Python when a product service call is required.

## Security Rules

Phase 2 and Phase 3 must preserve:

- JWT-compatible auth;
- simple RBAC;
- roles `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE`, `AUDITOR`;
- audit logging for review actions;
- correlation ID in logs;
- no persisted secrets;
- no Restricted evidence sent to AI;
- no raw prompts or completions in evidence, ledger or logs.

Authentication identifies the actor.

Authorization controls action.

Approval authority is a product rule, not a generic login side effect.

## Observability Rules

Observability must be useful from day one but not overbuilt.

Required minimum:

- structured logs;
- request logs;
- error logs;
- import/connector boundary logs;
- AI explanation boundary logs;
- `/health`;
- `/ready`;
- minimal metrics.

Phase 2 may include local development wiring for:

- Micrometer;
- OpenTelemetry;
- Prometheus;
- Grafana.

This does not authorize a full production observability platform, SIEM integration or business KPI dashboards.

## AI-Agent Coding Rules

Future AI agents must:

- read the authority documents before coding;
- stay inside the assigned phase;
- make the smallest change that satisfies the stage;
- update tests with behavior, not just snapshots;
- update the Decision Log for architecture or product changes;
- update R&D evidence only for real work performed;
- stop when a task requires a new product decision.

Future AI agents must not:

- infer missing architecture;
- expand connector scope;
- introduce a new database;
- convert conceptual bounded contexts into microservices by default;
- add OAuth provider matrices;
- add real AI provider calls during Phase 2;
- add business logic to satisfy an empty endpoint;
- claim implementation, hours or tests that did not happen.

## Phase Fit

Phase 2 uses this document to create the technical foundation without business intelligence.

Phase 3 uses this document to implement the `DRC-AOA-001` MVP value loop safely.

Later phases may extend the platform only through recorded decisions, RFCs or ADRs.
