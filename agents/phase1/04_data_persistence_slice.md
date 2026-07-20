# 04 - Data And Persistence Slice

## Purpose

Define the minimum data and persistence concept for Phase 1 before database schema, migrations or code exist.

This document locks what may be persisted later for `DRC-AOA-001` and what must remain deferred.

It does not create SQL, ORM models, migrations, repositories, services, fixtures, data loaders, package manifests, credentials, Docker, Kubernetes, Terraform or cloud resources.

## Stage Verdict

**Verdict: LOCKED for minimal Phase 1 data concept.**

Only data needed to prove one Decision ROI Case may be persisted later.

If a stored object does not directly support:

```text
DRC-AOA-001
Evidence
ROI View
one Recommendation
AI Explanation
Ledger Entry
basic identity/role
basic source/import metadata
basic observability
```

it is out of Phase 1 scope.

## Authority Inputs

- `agents/phase1/00_context_control.md`
- `agents/phase1/01_product_case_lock.md`
- `agents/phase1/02_acceptance_contract.md`
- `agents/phase1/03_architecture_scaffolding_plan.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
- `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

## Persistence Principle

Persist less than the conceptual product model.

Keep enough to prove:

- what was reviewed,
- which evidence supported it,
- how ROI was calculated,
- which recommendation was produced,
- how AI explained it,
- who acted,
- what the ledger recorded.

Do not persist broad platform structures before the first value loop works.

When implementation starts, PostgreSQL is the MVP source of truth.

Do not use in-memory storage, JSON files, SQLite, MongoDB or Redis as the MVP source of truth.

## Minimal Persistent Concepts

| Concept | Phase 1 purpose | Required? |
|---|---|---|
| Actor reference | Identify the authenticated reviewer. | Yes |
| Role reference | Enforce basic action/visibility rules. | Yes |
| Evidence source/import reference | Track manual/static or file/import origin. | Yes |
| Enterprise Evidence Event | Canonical connector/import event envelope. | Yes |
| Decision ROI Case | Central case state for `DRC-AOA-001`. | Yes |
| Evidence summary | Source-derived fact with lineage and sensitivity. | Yes |
| ROI View | Current cost, recovery, assumptions, confidence and risk. | Yes |
| ROI assumption | Explicit assumption used by ROI. | Yes |
| Recommendation | One deterministic model downgrade/change action. | Yes |
| AI Explanation | Natural-language explanation over prepared context. | Yes |
| Ledger Entry | Append-only review state and snapshots. | Yes |
| Decision Graph relationship | Relationship between evidence, case, recommendation, review, ledger and result. | Yes |
| Observation event summary | Minimal request/error/import/AI status if needed. | Optional |

## Minimal Relationships

| Relationship | Meaning |
|---|---|
| Decision ROI Case has Evidence summaries | Evidence supports case claims. |
| Evidence summary derives from Enterprise Evidence Event | Canonical event lineage is preserved. |
| Evidence summary has Evidence source/import reference | Lineage is preserved without raw payloads. |
| ROI View belongs to Decision ROI Case | ROI is case-specific. |
| ROI View uses ROI assumptions | Every number is explainable. |
| Recommendation belongs to Decision ROI Case | The recommendation is tied to one case. |
| Recommendation references Evidence and ROI View | Recommendation is evidence-backed. |
| AI Explanation references Recommendation and Evidence IDs | AI explains; it does not decide. |
| Ledger Entry belongs to Decision ROI Case | Ledger history is case-specific. |
| Ledger Entry snapshots Evidence, ROI and assumptions | Reviewed context is preserved. |
| Actor/Role creates Ledger Entry | Human accountability is recorded. |
| Decision Graph links Evidence, Case, Recommendation and Ledger Entry | Future correlation is preserved without Graph DB. |

## Required Data Invariants

| Invariant | Required behavior |
|---|---|
| One case | Phase 1 persists only `DRC-AOA-001` unless a later decision changes scope. |
| One recommendation | The case has exactly one active deterministic recommendation. |
| Evidence has lineage | Every evidence summary has source, source object, period/date and sensitivity. |
| ROI has assumptions | ROI cannot be approval-ready without assumptions. |
| Estimated and realized value are separate | Realized value appears only after result validation. |
| Ledger is append-only | Historical entries are never rewritten. |
| Decision Graph is relational in Phase 1 | Relationships are persisted in PostgreSQL, not in Graph DB. |
| AI is non-authoritative | AI explanation cannot create or modify business state. |
| Restricted data is excluded | Secrets, raw prompts, raw completions and customer conversations are not stored. |
| Role controls visibility | Confidential evidence is role-filtered. |

## Minimal Case Data Lock

| Field | Locked source |
|---|---|
| Case ID | `DRC-AOA-001` |
| Case name | AI Onboarding Assistant Recovery |
| Business decision | Jira/manual business evidence |
| Business owner | `E-OWNER-001` |
| Technical owner | `E-OWNER-002` |
| Approver | `E-OWNER-003` |
| Review period | 2026-06 |
| Review date | 2026-07-18 |
| Current monthly cost | ROI View |
| Recommendation family | AI model downgrade/change |
| Current state | review-ready estimate |

## Minimal Evidence Data Lock

Each evidence summary must preserve:

- Enterprise Evidence Event ID,
- evidence ID,
- source domain,
- source system,
- source object reference,
- period or timestamp,
- observed fact,
- business meaning,
- related Decision ROI Case,
- confidence contribution,
- sensitivity,
- raw payload descriptor mode.

Raw payload descriptor mode should be `not_stored` for Phase 1 evidence.

## Minimal ROI Data Lock

ROI View must preserve:

- AWS monthly cost: EUR410,
- AI monthly cost: EUR1,930,
- current monthly cost: EUR2,340,
- projected monthly cost after action: EUR720,
- monthly transition cost: EUR0,
- estimated monthly recovery: EUR1,620,
- estimated annualized recovery: EUR19,440,
- labor recovery per reconstructed decision: EUR805,
- confidence: 92/100,
- risk: Low if quality evidence is accepted; Medium if missing,
- realized saving: unavailable until validation.

## Minimal Recommendation Data Lock

The recommendation must preserve:

- recommendation family,
- recommended action,
- rationale,
- evidence IDs,
- ROI reference,
- assumptions reference,
- risk,
- confidence,
- owner,
- approver,
- review state,
- result-validation requirement.

It must not preserve:

- multiple alternatives,
- ranking position,
- AI-generated decision score,
- provider execution command,
- autonomous action plan.

## Minimal AI Explanation Data Lock

AI Explanation may preserve:

- generated explanation text,
- evidence IDs cited,
- assumptions cited,
- model/provider metadata if safe and useful,
- generation timestamp,
- success/failure status.

AI Explanation must not preserve:

- raw prompts,
- raw completions,
- raw provider payloads,
- secrets,
- customer conversations,
- decision authority,
- ledger mutation.

## Minimal Ledger Data Lock

Ledger Entry must preserve:

- entry type,
- state,
- actor reference,
- actor role,
- timestamp,
- reason or note,
- Decision ROI Case reference,
- recommendation reference,
- evidence snapshot reference,
- ROI snapshot reference,
- assumptions snapshot reference,
- risk/confidence snapshot when relevant.

## Minimal Decision Graph Data Lock

Decision Graph relationship records must preserve:

- source node type,
- source node reference,
- relationship type,
- target node type,
- target node reference,
- confidence when relevant,
- evidence reference when relevant,
- timestamp,
- case reference.

Allowed Phase 1 relationship types:

- `derived_from`,
- `supports`,
- `correlates_with`,
- `implemented_by`,
- `costs`,
- `explained_by`,
- `reviewed_by`,
- `recorded_as`,
- `validated_by`,
- `realizes`.

Use `correlates_with` when causality is not proven.

Minimum entry types:

- `recommendation_created`,
- `approved`,
- `rejected`,
- `deferred`,
- `implementation_marked`,
- `result_validated`.

For first demo acceptance, one of `approved`, `rejected` or `deferred` is enough to prove review-state behavior.

## Deferred Data Concepts

Do not model or persist in Phase 1:

- full organization hierarchy,
- departments beyond locked case context,
- enterprise tenant admin,
- full permission policy engine,
- connector marketplace,
- long-running sync history,
- provider cursors for all connectors,
- full audit event stream,
- complex event sourcing,
- analytics warehouse,
- Business Value portfolio aggregates,
- Graph DB/vector/search projections,
- multiple recommendation queues,
- model training data.

## Data Security Rules

Required:

- no Restricted data,
- no secrets,
- no credentials,
- no raw prompts,
- no raw completions,
- no customer conversations,
- evidence summaries only,
- role-filtered Confidential evidence,
- AI prepared context only,
- ledger snapshots without raw payloads.

If a data item cannot be safely summarized, it must be excluded or deferred.

## Data Acceptance Mapping

| Data concept | Acceptance gates supported |
|---|---|
| Actor and Role | AC-07, H1, H2 |
| Evidence source/import reference | AC-02, AC-03, A1 |
| Decision ROI Case | AC-01, B1 |
| Evidence summary | AC-03, A1-A7 |
| ROI View | AC-04, C1-C7 |
| Recommendation | AC-05, D1-D5 |
| AI Explanation | AC-06, E1-E5 |
| Ledger Entry | AC-09, F1-F6 |
| Observation event summary | AC-13, H3-H8 |

## Stage 04 Pass Criteria

This stage passes when:

1. every persistent concept supports `DRC-AOA-001`,
2. every ROI number remains traceable,
3. every ledger entry is append-only,
4. AI explanation cannot mutate data,
5. Restricted data is excluded,
6. no broad platform data model is introduced,
7. Stage 05 can define evidence intake without changing the domain.

## Handoff To Stage 05

Next stage:

```text
05 - Evidence Intake Slice
```

Lead:

- Connector Agent

Stage 05 objective:

Define how manual/static or file/import evidence becomes normalized evidence for `DRC-AOA-001`, including source mapping, sensitivity, freshness, failures and connector non-goals.

Inputs for Stage 05:

- this document,
- `agents/phase1/01_product_case_lock.md`,
- `agents/phase1/02_acceptance_contract.md`,
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`,
- `docs/architecture/CONNECTOR_FRAMEWORK.md`,
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`,
- `docs/architecture/29_Event_Evidence_Vocabulary.md`,
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`.

Exit condition for Stage 05:

Evidence can support the case without raw prompts, raw completions, secrets, customer conversations or provider mutation.
