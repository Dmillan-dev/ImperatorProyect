# Phase 1 - Stage 05: Evidence Intake Slice

## Purpose

Define the first evidence intake slice for IMPERATOR Phase 1.

This stage explains how evidence enters the MVP for `DRC-AOA-001` before any code, schema, connector automation or provider integration is created.

The goal is to prove that IMPERATOR can transform a small, trusted evidence pack into a Decision ROI Case.

## Authority Inputs

This stage is governed by:

1. `agents/phase1/00_context_control.md`
2. `agents/phase1/01_product_case_lock.md`
3. `agents/phase1/02_acceptance_contract.md`
4. `agents/phase1/04_data_persistence_slice.md`
5. `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
6. `docs/architecture/28_Per_Connector_MVP_Contracts.md`
7. `docs/architecture/29_Event_Evidence_Vocabulary.md`
8. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
9. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
10. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

If this stage conflicts with the Phase 1 scope contract, the Phase 1 scope contract wins.

## Stage Decision

Phase 1 uses manual/static or file/import evidence first.

Live Jira, GitHub, AWS, OpenAI and Anthropic Claude connectors remain deferred until the case can be proven from normalized evidence.

This is intentional. The first MVP must prove the business reasoning path, not the breadth of integrations.

All evidence intake must normalize into one canonical internal format:

```text
Enterprise Evidence Event
```

No connector may introduce a provider-specific evidence model into the domain.

## First Source Mode

Allowed source mode:

```text
Manual/static evidence pack
or
file/import evidence source
```

First file import encoding:

```text
JSONL, one Enterprise Evidence Event per line.
```

Not allowed in Stage 05:

- live provider polling,
- provider write operations,
- provider webhooks,
- background synchronization,
- connector scheduling,
- connector marketplace behavior,
- broad OAuth scopes,
- raw prompt or completion ingestion,
- connector-owned ROI calculation,
- connector-owned recommendation creation,
- connector-owned ledger writes.

## Evidence Intake Pipeline

The conceptual intake flow is:

```text
Source artifact
-> Enterprise Evidence Event
-> evidence classification
-> sensitivity check
-> freshness check
-> normalized evidence summary
-> Decision ROI Case evidence set
```

The intake slice does not decide.

It only prepares evidence so the domain and ROI stages can reason deterministically.

## Enterprise Evidence Event Contract

Every manual import, file import or future connector must produce the same evidence envelope.

| Field | Meaning |
| --- | --- |
| `id` | Stable evidence-event identifier. |
| `schema_version` | Version of the Enterprise Evidence Event contract. |
| `timestamp` | Source event time or observation time. |
| `ingested_at` | Time IMPERATOR accepted the event. |
| `source` | Concrete system name, for example Jira, GitHub, AWS, OpenAI, Claude or Manual Import. |
| `source_type` | Controlled source category such as `business_context`, `code`, `deployment`, `cloud_cost`, `ai_usage` or `manual`. |
| `source_object_ref` | Non-secret pointer to the original source object. |
| `entity` | Business or technical object observed by the event. |
| `event_type` | Controlled event type from `docs/architecture/29_Event_Evidence_Vocabulary.md`. |
| `severity` | `info`, `low`, `medium`, `high` or `critical` when applicable. |
| `actor` | Human, system or service actor associated with the event, if known. |
| `evidence_type` | Business context, implementation, cost, usage, quality, ownership, governance or security evidence. |
| `case_hint` | Optional Decision ROI Case hint such as `DRC-AOA-001`. |
| `sensitivity` | Public, Internal, Confidential or Restricted. |
| `confidence` | Low, Medium or High evidence confidence. |
| `freshness` | Fresh, stale, unknown or not applicable. |
| `metadata` | Sanitized key-value context needed for reasoning. |
| `raw_payload` | Controlled raw-payload descriptor. For Phase 1 it must not contain unrestricted provider payloads. |

Provider adapters translate source-specific objects into this envelope.

The Decision Engine never receives GitHub, AWS, Jira or AI-provider objects directly.

## MVP Evidence Set

The minimum evidence set for `DRC-AOA-001` is:

| Evidence ID | Source Domain | Required Fact |
| --- | --- | --- |
| `E-JIRA-001` | Jira | A business need or issue exists for the AI Onboarding Assistant review. |
| `E-GH-001` | GitHub | The relevant implementation or change context exists. |
| `E-DEPLOY-001` | Deployment | The reviewed capability is deployed or operationally active. |
| `E-AWS-001` | AWS | Monthly cloud cost for the capability is known. |
| `E-AI-001` | AI Usage | Monthly AI provider cost for the capability is known. |
| `E-AI-002` | AI Usage | Usage volume or usage pattern is known. |
| `E-AI-005` | Quality | Quality or fallback evidence exists for the proposed model change. |
| `E-OWNER-001` | Ownership | Business, technical, FinOps and approval owners are known. |

`E-AI-005` is important because the recommendation is low risk only if quality evidence supports the model change.

If `E-AI-005` is missing, the case may still be reviewable, but the recommendation risk must not be shown as Low.

## Source-To-Evidence Mapping

| Source Area | Example Source Object | Normalized Evidence Type | Normalized Event |
| --- | --- | --- | --- |
| Jira | issue, request or decision ticket | business context evidence | `business_need_identified` |
| GitHub | pull request, commit or repository reference | implementation evidence | `implementation_context_observed` |
| Deployment | deployment reference or release note | operational state evidence | `deployment_reference_observed` |
| AWS | monthly cost export or service cost summary | cost evidence | `cloud_cost_observed` |
| OpenAI / Anthropic Claude | billing summary or usage summary | AI cost evidence | `ai_usage_observed` |
| Usage / Product Ops | active-user count or adoption summary | usage evidence | `usage_signal_observed` |
| Manual Owner Input | named owner or approval path | governance evidence | `approval_path_observed` |

Source names may be provider-specific.

Evidence meaning must be provider-neutral.

## Evidence Summary Contract

Each evidence item should be representable with:

| Field | Meaning |
| --- | --- |
| `evidence_id` | Stable identifier used in ROI, recommendation, explanation and ledger references. |
| `case_id` | `DRC-AOA-001`. |
| `source_domain` | Jira, GitHub, AWS, AI Usage, Deployment, Manual or equivalent. |
| `source_object_ref` | Non-secret pointer to the source object. |
| `evidence_type` | Business context, implementation, cost, usage, quality, ownership or governance. |
| `fact_summary` | Short normalized claim supported by the source. |
| `period` | Review period, normally 2026-06 for the locked case. |
| `observed_at` | When the source fact was observed or exported. |
| `freshness_status` | Fresh, stale, unknown or not applicable. |
| `sensitivity` | Public, Internal, Confidential or Restricted. |
| `confidence` | Low, Medium or High evidence confidence. |
| `raw_payload.mode` | Must be `not_stored` for Phase 1 unless explicitly approved later. |

## Raw Payload Rule

The canonical model includes `raw_payload` as a controlled descriptor, not as permission to store source dumps.

For Phase 1:

```text
raw_payload.mode = "not_stored"
```

Allowed raw-payload metadata:

- `mode`,
- optional safe hash,
- optional future secure reference,
- redaction status.

Forbidden raw payload content remains forbidden even if a connector can technically read it.

## Sensitivity Rules

Allowed in Phase 1 evidence summaries:

- source references,
- monthly cost totals,
- usage aggregates,
- owner names or role labels when needed,
- implementation references,
- quality-summary facts.

Not allowed:

- raw prompts,
- raw completions,
- secrets,
- API keys,
- OAuth tokens,
- customer conversations,
- unrestricted stack traces,
- provider payload dumps,
- sensitive personal data,
- customer-identifying free text unless explicitly required and labeled.

Restricted information must not enter the AI explanation context.

## Freshness Rules

For `DRC-AOA-001`:

- review period: 2026-06,
- review date: 2026-07-18,
- monthly cost evidence should belong to the review period or be clearly labeled as an assumption,
- usage evidence should belong to the review period or be clearly labeled as stale,
- owner and approver evidence should be current enough for review.

If evidence freshness is unknown, the workspace should show a blocker or warning before approval.

## Evidence Failure States

| Failure | Required Behavior |
| --- | --- |
| Business context missing | Block recommendation readiness. |
| Cost evidence missing | Block ROI View. |
| AI usage evidence missing | Block AI-spend recovery calculation. |
| Quality evidence missing | Do not label recommendation as Low risk. |
| Owner evidence missing | Block approval action. |
| Sensitivity unknown | Block AI explanation context until reviewed. |
| Raw prompt or completion present | Reject from AI context and mark security issue. |
| Source reference missing | Allow only as manual assumption if clearly labeled. |
| Stale evidence | Allow review only with visible freshness warning. |

## Connector Boundary

For Phase 1, a connector or import adapter may:

- read or receive source artifacts,
- classify source facts,
- map source facts to normalized evidence summaries,
- label sensitivity,
- label freshness,
- report intake success or failure.

A connector or import adapter must not:

- calculate ROI,
- choose recommendation wording,
- approve,
- reject,
- defer,
- mark implementation,
- validate result,
- write ledger entries directly,
- call an LLM for decision logic,
- mutate provider systems.

## Acceptance Mapping

This stage supports:

- `AC-02`: one initial evidence source path,
- `AC-03`: one evidence chain,
- `AC-04`: one Decision ROI Case reconstruction,
- `AC-13`: minimal logs and observability,
- `AC-14`: sensitive data controls.

It also supports negative tests rejecting:

- broad connector automation,
- connector-owned ROI,
- connector-owned recommendations,
- raw prompt or completion ingestion,
- provider mutation.

## Stage Pass Criteria

Stage 05 is complete when:

1. the first source mode is manual/static or file/import evidence;
2. evidence IDs required for `DRC-AOA-001` are known;
3. every source fact maps to a normalized evidence type;
4. sensitivity and freshness rules are explicit;
5. connector authority is limited to intake and normalization;
6. raw prompts, completions, secrets and provider payload dumps are excluded;
7. the next stage can calculate ROI and recommendation from evidence summaries.

## Stop Conditions

Stop and return to CTO Agent if any proposal requires:

- a live connector before evidence-pack value is proven,
- more than one Decision ROI Case,
- provider write permissions,
- raw AI payload persistence,
- connector-owned ROI,
- connector-owned recommendation,
- ledger mutation from connector code,
- broad OAuth scope,
- Kafka or event streaming infrastructure.

## Handoff To Stage 06

Stage 06 may use this evidence set to define the deterministic domain, ROI and recommendation slice.

Stage 06 must not request extra evidence unless the request is necessary to prove `DRC-AOA-001`.
