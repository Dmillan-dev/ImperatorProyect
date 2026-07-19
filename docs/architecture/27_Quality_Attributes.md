# 27 - Quality Attributes

## Purpose

Define the quality attributes and non-functional expectations for the first IMPERATOR MVP before writing implementation code.

This document closes the architecture quality bar for:

- explainability,
- auditability,
- evidence freshness,
- traceability,
- latency expectations,
- reliability and graceful degradation,
- observability,
- security and data minimization,
- reversibility boundaries,
- performance non-goals.

It is a Phase 0 architecture artifact. It does not define code, services, connectors, APIs, database schema, migrations, tests, dashboards, OpenAPI, generated bindings, Docker, Kubernetes, Terraform, CI/CD or runtime infrastructure.

## Architect Coherence Check

Verdict: the project remains logically aligned for a no-code MVP definition.

The current Phase 0 sequence is now:

```text
Vision and Positioning
-> MVP Blueprint
-> MVP Vertical Slice
-> MVP ROI Slice
-> Manual Evidence Pack
-> Security and Data Governance
-> MVP Acceptance Test Plan
-> Identity, Access and Approval Model
-> Decision Review Workspace Screen Contract
-> Quality Attributes
-> Per-Connector MVP Contracts
```

This document does not expand the MVP. It constrains future implementation so the first build proves one trustworthy Decision ROI Case instead of becoming a broad platform.

## Canonical References

- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md`
- `docs/product/24_MVP_Vertical_Slice.md`
- `docs/product/25_MVP_ROI_Slice.md`
- `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
- `docs/product/27_MVP_Acceptance_Test_Plan.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/product/CORE_DOMAIN_MODEL.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/DECISION_LEDGER_V2.md`
- `docs/architecture/21_Technical_Architecture_Context.md`
- `docs/architecture/24_MVP_Project_Structure.md`
- `docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/architecture/28_Per_Connector_MVP_Contracts.md`
- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/architecture/DATABASE_MODEL.md`
- `docs/architecture/CONNECTOR_FRAMEWORK.md`

## Quality Thesis

For the first MVP, quality does not mean high scale, many connectors or production-grade automation.

Quality means:

**A reviewer can trust one recovery recommendation because every claim is explainable, evidence-backed, role-safe, freshness-aware and ledger-recorded.**

The MVP quality bar is therefore:

- one Decision ROI Case is understandable,
- one ROI view is explainable,
- one recommendation is reviewable,
- one approval, rejection or deferral is auditable,
- one result validation path is possible later,
- no raw sensitive provider data is required to trust the case.

## Scope

### In Scope

- Quality attributes for the AI Onboarding Assistant Recovery slice.
- Manual acceptance of non-functional expectations before code.
- Future Phase 1 candidate targets for review-screen latency and reliability.
- Evidence quality, freshness and traceability rules.
- Ledger integrity expectations.
- Observability requirements for future implementation.
- Performance limits and explicit non-goals.

### Out Of Scope

- Production SLAs.
- SOC2, ISO 27001 or legal compliance certification.
- Cloud architecture for high availability.
- Autoscaling and capacity planning.
- Synthetic monitoring implementation.
- Load-test scripts.
- Logging framework choice.
- Metrics backend choice.
- Final API contracts.
- Connector implementation details.

## Quality Attribute Summary

| Attribute | MVP quality rule | Phase 1 candidate target | Anti-pattern |
|---|---|---|---|
| Explainability | Every ROI and recommendation claim cites evidence or assumptions. | A reviewer can trace any number or recommendation to source evidence in one screen. | Black-box ROI or AI-generated financial proof. |
| Auditability | Every business action is ledger-recorded with actor, role, reason and snapshots. | Historical review remains understandable after a connector is disabled. | Mutable approval history or missing snapshots. |
| Traceability | Evidence links source system, source object, period, observed fact and Decision ROI Case. | Jira -> GitHub -> AWS -> AI chain is reconstructable without raw payloads. | Isolated cost or usage rows with no decision context. |
| Evidence freshness | Review period and source date are visible for every evidence item. | Cost and usage evidence use the latest accepted billing/review period. | Treating stale evidence as current without warning. |
| Security and privacy | Restricted data is excluded; Confidential evidence is role-filtered. | AI receives prepared summaries only. | Raw prompts, completions, secrets or provider payloads in UI/ledger/AI context. |
| Reliability | Missing data creates blockers, not fabricated values. | Review screen degrades to known snapshots and visible gaps. | Silent failure or invented evidence. |
| Latency | Screen should support one focused review session. | Prepared case read p95 <= 2s; ledger command p95 <= 3s; AI explanation optional and non-blocking. | Making LLM calls or provider sync a blocking dependency for approval. |
| Observability | Future implementation must expose evidence, sync, blocker and ledger health. | Operators can see stale evidence, connector failures, authorization denials and ledger write failures. | Invisible ingestion or review failures. |
| Reversibility | IMPERATOR records approval; it does not execute provider changes. | External action should have fallback or implementation note when risk requires it. | One-click irreversible provider mutation. |
| Maintainability | Future build starts modular and domain-first. | Modules map to intake, evidence, decision case, ROI, recommendation and ledger. | Premature microservices or connector-owned business logic. |
| Testability | Quality rules map to manual acceptance scenarios. | Every P0 quality gate has an acceptance test or review checklist. | Non-functional behavior invented during coding. |

## Attribute Details

### 1. Explainability

Quality goal:

The reviewer must understand why the recommendation exists and where every number comes from.

Minimum MVP expectations:

- ROI must show current cost, projected cost, estimated recovery and assumptions.
- Recommendation must cite evidence IDs and assumption IDs.
- Confidence must mean evidence completeness, not AI certainty.
- Risk must be described in business language.
- AI output, if used later, must cite evidence IDs and remain advisory.

Pass condition:

A CTO, Platform Lead or FinOps reviewer can challenge a number and find the evidence or assumption behind it without reading raw provider payloads.

### 2. Auditability

Quality goal:

The company must be able to reconstruct what was reviewed, who acted and why.

Minimum MVP expectations:

- Approval records actor, role, timestamp, evidence snapshot, ROI snapshot and assumptions snapshot.
- Rejection records reason and reviewed evidence/ROI.
- Deferral records missing evidence or review date.
- Implementation marking links back to prior approval.
- Result validation records validation period, evidence, realized value and variance.
- Ledger entries are append-only.

Pass condition:

The Decision ROI Case remains understandable even if Jira, GitHub, AWS or AI-provider access is later unavailable.

### 3. Traceability

Quality goal:

A reviewer can follow the chain from business decision to cost, usage, recommendation and ledger.

Minimum MVP trace:

```text
Jira decision
-> GitHub implementation
-> AWS resource/cost
-> OpenAI + Anthropic Claude usage/cost
-> ROI view
-> Recommendation
-> Review action
-> Decision Ledger entry
-> Result validation later
```

Each evidence item must carry:

- evidence ID,
- source system,
- source object reference,
- observed fact,
- date or period,
- correlation key,
- sensitivity,
- confidence contribution.

Pass condition:

Removing any required source must create a visible blocker or lower confidence. It must not silently leave the case review-ready.

### 4. Evidence Freshness

Quality goal:

The screen must make it obvious whether evidence is current enough for review.

MVP freshness rules:

| Evidence type | Freshness expectation | Stale behavior |
|---|---|---|
| Jira business context | Historical source date is acceptable if source reference exists | Warn only if owner/status changed or source is disputed |
| GitHub implementation evidence | Historical source date is acceptable if deployment reference exists | Warn if deployment state is unknown |
| AWS cost evidence | Latest accepted billing or review period | If older than 45 days, show stale warning and consider deferral |
| AI usage/cost evidence | Latest accepted billing or review period | If older than 45 days, show stale warning and consider deferral |
| Usage/value signal | Same review period as cost where possible | Lower confidence if usage period is mismatched |
| Quality review note | Must be tied to model-change assumption | Raise risk if missing or stale |
| Result validation evidence | Must come after implementation period | Block realized value if missing |

The 45-day rule is a Phase 1 design target, not a contractual SLA. During manual Phase 0 review, older evidence may be accepted only if the assumption is visible and the reviewer explicitly accepts the risk.

### 5. Security And Data Minimization

Quality goal:

The MVP must be trusted because it avoids unnecessary sensitive data.

Minimum expectations:

- Restricted data is not accepted as MVP evidence.
- Raw prompts, raw completions, secrets and customer conversations are excluded.
- Confidential evidence is role-filtered.
- AI receives prepared context only.
- Ledger snapshots preserve summaries and references, not raw provider payloads.
- Unknown sensitivity is treated as Confidential until reviewed.

Pass condition:

The AI Onboarding Assistant Recovery case can be reviewed without exposing raw provider payloads or Restricted data.

### 6. Reliability And Graceful Degradation

Quality goal:

Failure should reduce confidence or block unsafe actions. It should not create false certainty.

Minimum expectations:

- Missing evidence produces visible blockers.
- Provider unavailability does not erase existing snapshots.
- Connector failure later should show last successful evidence period.
- Approval cannot proceed when required snapshots are unavailable.
- Ledger command failure must be visible and must not create partial approval.
- Duplicate approval attempts must not create ambiguous business history.

Pass condition:

When data is incomplete, the screen can still explain what is known, what is missing and what action is blocked.

### 7. Latency Expectations

Quality goal:

The review experience should feel usable without forcing real-time infrastructure before the MVP is proven.

Phase 1 candidate targets for one prepared Decision ROI Case:

| Operation | Candidate target | Notes |
|---|---:|---|
| Open Decision Review Workspace | p95 <= 2 seconds | Uses prepared case view, not live provider calls |
| Open evidence detail summary | p95 <= 2 seconds | Role-filtered summaries |
| Read ROI view | p95 <= 2 seconds | Deterministic calculation or prepared read model |
| Record approve/reject/defer | p95 <= 3 seconds | Ledger command must be atomic in future implementation |
| Mark implementation | p95 <= 3 seconds | Records external action only |
| Validate result | p95 <= 3 seconds | Requires post-action evidence |
| AI explanation | Non-blocking | Included as advisory language; must not block approval readiness |

These are design targets, not production SLAs.

Non-goal:

The MVP does not need real-time streaming, sub-second analytics, global multi-region availability or high-volume event processing.

### 8. Observability

Quality goal:

Future operators must know whether the evidence chain is healthy enough to support decisions.

Future implementation should expose:

- structured logs,
- request/correlation ID,
- health endpoint,
- basic request, error and latency metrics,
- last source sync or manual import period,
- stale evidence count,
- missing required evidence,
- correlation failures,
- sensitivity classification failures,
- role-denied evidence access attempts,
- AI context filtering status if AI explanations exist,
- ledger command success/failure,
- duplicate command detection,
- review-screen read latency,
- connector health once connectors exist.

Future-compatible tooling:

- Spring Boot Actuator,
- Micrometer,
- OpenTelemetry when useful,
- Prometheus/Grafana after runtime exists and the MVP needs dashboards.

MVP reduction:

Do not make a full observability platform a prerequisite for proving one Decision ROI Case.

Phase 0 note:

This document defines observability expectations only. It does not create monitoring dashboards, logs, metrics, alerts or telemetry code.

### 9. Reversibility

Quality goal:

The MVP should not create irreversible operational risk.

Minimum expectations:

- IMPERATOR records approval but does not execute provider-side change.
- Recommended action should include fallback or rollback idea when quality risk exists.
- Model downgrade/change should preserve high-cost model fallback when documented.
- Implementation marker must state that action happened outside IMPERATOR.
- Result validation must not rewrite original approval estimate.

Pass condition:

The company can approve a recovery action while understanding how external implementation risk is controlled.

### 10. Maintainability And Modularity

Quality goal:

Future code should stay simple enough to prove the value loop.

Minimum expectations:

- Build from Decision ROI Case, not from connector-specific features.
- Keep connector logic outside business rules.
- Keep ROI deterministic and assumptions-visible.
- Keep ledger append-only and separate from workflow orchestration.
- Start with modular monolith or tightly bounded service.
- Do not create microservices only because bounded contexts are documented.

Pass condition:

The future structure can implement one case without requiring Kafka, Kubernetes, graph database, vector database, policy engine or public API.

## Quality Scenarios

| ID | Scenario | Given | When | Expected quality behavior | Priority |
|---|---|---|---|---|---|
| QA01 | ROI explainability | Current monthly cost is EUR2,340 | Reviewer opens ROI | AWS EUR410 and AI EUR1,930 are shown separately with evidence IDs | P0 |
| QA02 | Recommendation explainability | Model-change recommendation exists | Reviewer asks why | Recommendation cites AI cost, model usage, quality note, assumptions, risk and confidence | P0 |
| QA03 | Approval auditability | Case is review-ready | CTO approves | Ledger records actor, role, note, evidence snapshot, ROI snapshot and assumptions snapshot | P0 |
| QA04 | Rejection auditability | CTO rejects | Rejection reason is submitted | Ledger records immutable rejected entry with reviewed evidence/ROI | P0 |
| QA05 | Deferral clarity | Quality note is missing | Platform or FinOps reviews | Screen raises risk and allows deferral with required evidence | P0 |
| QA06 | Freshness warning | AWS or AI cost evidence is older than 45 days | Reviewer opens case | Screen shows stale warning and approval readiness is reduced or deferred | P0 |
| QA07 | Missing source | AI usage evidence is missing | Recommendation is evaluated | Model-change recommendation is not approval-ready | P0 |
| QA08 | Unauthorized view | Viewer opens Confidential evidence | Evidence is role-filtered | Viewer sees limited summary or hidden evidence notice | P0 |
| QA09 | Restricted data | Raw prompt or secret appears in input | Evidence is reviewed | Item is excluded and security review is required | P0 |
| QA10 | Provider unavailable | Future connector cannot reach AWS | Screen opens existing case | Last snapshot remains visible with freshness warning; no fabricated data | P1 |
| QA11 | Ledger integrity | Approval command fails | Actor submits approval | Screen shows failure and no partial approval appears | P1 |
| QA12 | AI explanation | AI summary is requested later | Prepared context exists | AI cites evidence IDs and cannot approve or execute | P1 |
| QA13 | Result validation | Implementation is marked | FinOps validates result | Realized saving is recorded separately from original estimate | P0 |
| QA14 | Screen usability | Reviewer opens `DRC-AOA-001` | First minute of review | Decision, owner, cost, recommendation, confidence and blockers are understandable | P0 |
| QA15 | Performance boundary | Multiple cases are requested | MVP is still one-case focused | Broad portfolio analytics are deferred | P0 |

## Quality Gates Before Code

Do not begin implementation until these quality gates are accepted or explicitly deferred:

| Gate | Required answer |
|---|---|
| QG1 - Explainability | Can every ROI and recommendation claim be traced to evidence or assumptions? |
| QG2 - Auditability | Can approval, rejection, deferral, implementation and validation be ledger-recorded? |
| QG3 - Freshness | Does every evidence item show source date or period? |
| QG4 - Sensitivity | Does every evidence item have Public, Internal, Confidential or Restricted classification? |
| QG5 - Role safety | Are screen actions and evidence visibility role-aware? |
| QG6 - No raw sensitive data | Can the case be reviewed without raw prompts, completions, secrets or customer conversations? |
| QG7 - No fabricated data | Are missing providers and missing evidence shown as blockers or warnings? |
| QG8 - Latency expectation | Can the future screen use prepared reads instead of live provider calls? |
| QG9 - Ledger integrity | Are ledger writes append-only and snapshot-based? |
| QG10 - Observability | Are future failure modes visible enough to operate the MVP? |
| QG11 - Reversibility boundary | Does IMPERATOR record external action instead of executing it? |
| QG12 - Performance non-goals | Are real-time streaming, broad analytics and scale infrastructure still deferred? |

## Acceptance Alignment

This document extends `docs/product/27_MVP_Acceptance_Test_Plan.md` without replacing it.

| Acceptance area | Quality attributes |
|---|---|
| Suite A - Evidence | Traceability, freshness, security, testability |
| Suite B - Decision ROI Case | Explainability, traceability, maintainability |
| Suite C - ROI | Explainability, freshness, auditability |
| Suite D - Recommendation | Explainability, role safety, reversibility |
| Suite E - Approval and Ledger | Auditability, integrity, reliability |
| Suite F - Security and Governance | Security, privacy, role filtering |
| Suite G - Conceptual API | Latency, reliability, maintainability |
| Suite H - Decision Review Workspace | Usability, explainability, blockers, observability |
| Negative tests | Performance non-goals, no autonomous execution, no raw sensitive data |

## Future Module Implications

| Future module | Quality responsibility |
|---|---|
| `intake` | Preserve source identity, period, freshness and sensitivity hints |
| `evidence` | Normalize facts, classify sensitivity, keep lineage and confidence contribution |
| `decision_case` | Assemble one coherent Decision ROI Case and expose blockers |
| `roi` | Calculate deterministic ROI with visible assumptions |
| `recommendation` | Produce evidence-backed, risk-aware, non-autonomous recommendation |
| `ledger` | Append immutable entries and preserve snapshots |
| `decision-review` | Show role-filtered evidence, blockers, actions and ledger history |
| `ai/explanation` | Explain prepared context only and cite evidence IDs |

These are future responsibilities. They do not authorize implementation during Phase 0.

## Performance Non-Goals

The MVP does not need:

- real-time streaming ingestion,
- Kafka-backed event processing,
- high-volume observability analytics,
- portfolio-wide decision ranking,
- sub-second graph traversal,
- vector search,
- AI Advisor orchestration,
- multi-region availability,
- automated remediation,
- provider-side execution,
- large-scale tenant administration,
- board-grade financial reporting.

If a future proposal requires any of these to prove `DRC-AOA-001`, it is too broad for the MVP.

## Manual Quality Review Checklist

Before Phase 1 design, verify:

1. The AI Onboarding Assistant Recovery case is explainable without code.
2. Every ROI number points to evidence or assumptions.
3. Every evidence item has date or period.
4. Every evidence item has sensitivity.
5. Every reviewer role has clear visibility and action limits.
6. Approval cannot happen through missing evidence.
7. Ledger history can preserve the reviewed state.
8. Result validation is separate from estimated recovery.
9. Provider unavailability has a visible degradation path.
10. AI explanation is included as advisory language only.
11. Future screen reads can use prepared context.
12. Performance non-goals are still deferred.

## Negative Rules

Reject any future MVP design that:

- optimizes throughput before trust,
- hides evidence freshness,
- makes AI explanation a blocking approval dependency,
- uses raw provider payloads as the primary UI contract,
- treats connector availability as required to read past ledger history,
- counts unvalidated estimates as realized Business Value,
- writes approval without complete snapshots,
- requires Kafka, Kubernetes, Terraform, graph database or vector database for the first proof,
- introduces public APIs or SDKs before the Decision Recovery Workflow is validated.

## Companion Documents

The companion event/evidence vocabulary and Phase 0 control artifacts are now created:

- `docs/architecture/29_Event_Evidence_Vocabulary.md`
- `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
- `docs/architecture/30_Phase_0_Closure_Readiness_Review.md`

Reason:

Quality attributes and per-connector MVP contracts are now defined. The event/evidence vocabulary now locks the names future tests, agents and code should use.
