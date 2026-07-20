# Connector Framework

## Purpose

Define how future integrations should be added without modifying IMPERATOR's core domain.

This document is conceptual. It does not define connector code, SDKs, deployment, credentials, queues or production sync logic.

## Source Documents

- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/architecture/31_MVP_Implementation_Standard.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`
- `docs/architecture/34_MVP_Implementation_Blueprint.md`
- `docs/architecture/35_Coding_Principles.md`
- `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`
- `docs/architecture/37_Implementation_Contract.md`
- `docs/rfcs/0002-module-communication-architecture.md`
- `docs/architecture/21_Technical_Architecture_Context.md`

## Connector Philosophy

Connectors are adapters.

They bring external signals into IMPERATOR, but they do not own business meaning.

In the MVP implementation standard, connectors sit behind ports. Jira, GitHub, AWS and OpenAI + Anthropic Claude can change without changing the Decision ROI Case domain.

Phase 2 may create connector interfaces and adapter skeletons only, under the one-module-per-iteration rule in `docs/architecture/37_Implementation_Contract.md`.

Phase 3 MVP implementation may start with one or two narrow read-only adapters plus approved manual/static or imported evidence for the remaining domains. Full connector automation is not required to prove the first Decision ROI Case.

Every connector or import adapter must output the same internal envelope:

```text
Enterprise Evidence Event
```

GitHub must not create a GitHub-shaped domain model, AWS must not create an AWS-shaped domain model, and AI providers must not create provider-shaped domain models inside the core.

The core product should understand:
- Decision,
- Evidence,
- Timeline,
- ROI,
- Recommendation,
- Approval,
- Business Value.

The connector should understand:
- provider authentication,
- provider API shape,
- rate limits,
- pagination,
- webhooks or polling,
- provider-specific metadata,
- sync health.

## Core Rule

Adding a new connector must not require changing the core Decision ROI Case model.

Hexagonal rule:

The domain asks for evidence through ports. Provider-specific adapters satisfy those ports.

The adapter path is:

```text
Provider object
-> adapter parser
-> Enterprise Evidence Event
-> Evidence Normalizer
-> Evidence Store
```

If adding a connector requires changing the core domain, one of two things is true:

1. the connector is leaking provider-specific concepts into the product, or
2. the domain model is missing a real business concept and must be updated through decision log and RFC.

## Connector Boundary

### Connector as adapter

A connector is an adapter for a source system.

It may implement a future evidence/intake port, but it must not become a domain service.

### Connector owns

- provider authentication mechanics,
- provider API calls or webhook intake,
- source payload preservation,
- pagination and cursors,
- rate-limit handling,
- retry and backoff strategy,
- sync status,
- provider-specific error mapping,
- source object IDs,
- source timestamps,
- mapping provider objects into Enterprise Evidence Events.

### Connector does not own

- Decision ROI Case lifecycle,
- ROI calculation,
- recommendation generation,
- approval workflow,
- ledger writes,
- business policy meaning,
- AI reasoning,
- product surface logic.

## Connector Lifecycle

### 1. Configure

The organization enables an Integration and provides the minimum required authorization.

Conceptual outputs:
- Integration configured,
- Connector Configuration created,
- Policy and permission scope recorded.

### 2. Discover

The connector identifies available source objects and sync boundaries.

Examples:
- Jira projects,
- GitHub repositories,
- AWS accounts or services,
- OpenAI + Anthropic Claude usage scopes.

### 3. Capture

The connector fetches or receives source data.

Conceptual output:
- Enterprise Evidence Event with source metadata.

### 4. Preserve

The connector preserves enough source metadata to prove lineage.

Must preserve:
- source system,
- source object ID,
- source timestamp,
- source actor when available,
- sync run,
- original meaning summary,
- sensitivity hint when available.

### 5. Normalize

Provider-specific data is mapped into normalized event concepts.

Examples:
- Jira issue -> Business Context event,
- GitHub pull request -> Code & Deployment event,
- AWS cost metric -> Infrastructure & Cost event,
- AI token usage -> AI Consumption event.

### 6. Correlate

The Context Engine, not the connector, links normalized events to Decision ROI Cases.

Correlation signals may include:
- ticket IDs,
- branch names,
- commit messages,
- deployment names,
- resource tags,
- team ownership metadata,
- timestamps,
- application names,
- user or team identifiers.

### 7. Monitor

The connector reports health and freshness.

Conceptual health states:
- healthy,
- degraded,
- stale,
- unauthorized,
- rate-limited,
- failing,
- disabled.

### 8. Retire

The connector can be disabled without destroying historical Decision Ledger entries.

Historical evidence must remain understandable after connector removal.

## Normalized Event Categories

Every connector should map provider data into one or more information domains.

| Domain | Meaning | MVP examples |
|---|---|---|
| Business Context | why the decision exists | Jira ticket, epic, owner, priority |
| Code & Deployment | who changed what | GitHub PR, commit, review, deploy |
| Infrastructure & Cost | what resources and cost exist | AWS resource, usage, cost |
| AI Consumption | which AI usage and cost exist | model, tokens, requests, agent, user |
| Policy and Governance | which rules apply | approval policy, AI policy, spend policy |
| Usage and Value Signal | whether value appears present | active users, request volume, adoption trend |

## Connector Contract Concept

Every future connector should be described by a connector contract before implementation.

The contract should answer:

- Which Integration does it serve?
- Which information domain does it feed?
- Which source objects does it read?
- Which Operational Events can it emit?
- Which Evidence types can it support?
- Which correlation keys can it provide?
- Which ownership signals can it expose?
- Which cost or usage signals can it expose?
- Which permissions are required?
- Which data is sensitive?
- Which failure states are expected?
- Which MVP paid-wedge recommendation focus or validated expansion family can it support?

## MVP Connector Contracts

### Jira Connector

Domain:
- Business Context.

Signals:
- project,
- issue,
- epic,
- status,
- priority,
- owner,
- created/updated timestamps,
- business goal.

Supports:
- Decision origin,
- owner evidence,
- project context,
- lifecycle status.

### GitHub Connector

Domain:
- Code & Deployment.

Signals:
- repository,
- branch,
- pull request,
- commit,
- review,
- author,
- merge timestamp,
- deployment reference.

Supports:
- implementation evidence,
- ownership evidence,
- technical change timeline.

### AWS Connector

Domain:
- Infrastructure & Cost.

Signals:
- resource,
- service,
- tags,
- usage,
- cost,
- utilization,
- account or environment.

Supports:
- current cost,
- underutilization,
- resource ownership,
- annualized recovery.

### OpenAI + Anthropic Claude Connector

Domain:
- AI Consumption.

Signals:
- provider,
- model,
- requests,
- tokens,
- cost,
- user/team/application,
- agent or workflow when available.

Supports:
- model downgrade recommendation,
- unused agent detection,
- AI cost attribution to Decision ROI Case.

Post-validation only:
- duplicated agent or service analysis.

Detailed MVP contracts for Jira, GitHub, AWS and OpenAI + Anthropic Claude are now defined in `docs/architecture/28_Per_Connector_MVP_Contracts.md`.

Canonical normalized event names, evidence types, blocker states and labels are defined in `docs/architecture/29_Event_Evidence_Vocabulary.md`.

## Connector Addition Process

Before adding a connector:

1. Confirm it strengthens the Decision ROI Timeline.
2. Confirm it supports one of the MVP information domains or a validated expansion domain.
3. Write a connector contract.
4. Identify evidence types and correlation keys.
5. Identify ownership, cost, usage and policy signals.
6. Define sensitivity, least-privilege permissions and raw-payload exclusions.
7. Confirm no provider-specific concept leaks into the core domain.
8. Update RFC or architecture context if a new domain concept is required.
9. Record significant boundary changes in `docs/decisions/14_Decision_Log.md`.

Default MVP permission posture:

- read-only,
- least privilege,
- no autonomous execution scopes,
- no raw prompt or completion capture,
- no secrets in evidence, logs or ledger.

## Expansion Guardrails

Do not add connectors just because customers use the tool.

Add a connector only when it improves:
- Decision ROI Case completeness,
- evidence quality,
- ROI confidence,
- recommendation accuracy,
- approval readiness,
- business value proof.

## Connector Quality Checklist

- Replaceable without rewriting core modules.
- Emits normalized events, not product decisions.
- Preserves source lineage.
- Separates sync health from business truth.
- Exposes freshness and confidence impact.
- Avoids hardcoded provider business logic.
- Supports tenant isolation.
- Supports sensitivity classification.
- Degrades gracefully when provider data is incomplete.
- Does not allow AI to read raw source data directly.

## What Happens When Data Is Missing

Connector data may be incomplete. The platform should respond with confidence changes, not false certainty.

Examples:
- missing owner -> recommendation requires owner review,
- stale sync -> decision confidence decreases,
- missing usage signal -> ROI is assumption-heavy,
- missing cost signal -> recommendation cannot claim savings,
- missing evidence lineage -> not approval-ready.

## Connector Candidate Next Steps

1. Use `docs/architecture/28_Per_Connector_MVP_Contracts.md` as the source of truth for Jira, GitHub, AWS and OpenAI + Anthropic Claude.
2. Use `docs/architecture/29_Event_Evidence_Vocabulary.md` for normalized event vocabulary and evidence labels.
3. Align connector contracts with `docs/architecture/DATABASE_MODEL.md`.
4. Map connector outputs to `docs/product/CORE_DOMAIN_MODEL.md`.
5. Use `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` before creating connector skeletons in Phase 2.
6. Use `docs/architecture/37_Implementation_Contract.md` and `agents/phase2/README.md` before assigning connector skeleton work to agents.
7. Create RFC before adding any new integration domain.
