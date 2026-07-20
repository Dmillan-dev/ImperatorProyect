# 22 — Technical Investor Audit and Refactor

## Purpose

Audit IMPERATOR as a technical investor would: what is investable, what should be removed or deferred, what is missing, and what is overengineered.

This document is not an implementation plan. It is an execution-focus refactor for Phase 0 and Phase 1 planning.

## Investor Verdict

IMPERATOR has a strong insight:

**companies lose the economic thread after technical decisions ship.**

The investable product is not a broad enterprise context platform yet. The investable wedge is:

**Decision Recovery for AI and cloud spend.**

The first paid product should reconstruct one expensive AI/cloud decision, prove why it exists, show what it costs, recommend one approval-ready action, and preserve the result.

## What Is Investable

### Strong

- Decision ROI Case as the central product object.
- Jira -> GitHub -> AWS -> OpenAI + Anthropic Claude as a complete business story.
- Approval-first recommendation model.
- Evidence-backed ROI with explicit assumptions.
- Decision Ledger as accountability record.
- Neutrality as a long-term moat.

### Weak Until Validated

- Broad product surface.
- Five recommendation families at once.
- Public API and SDK story.
- Multi-cloud as a v1 buying requirement.
- AI Advisor, semantic search and Graph DB/vector architecture.
- Full policy/governance surface.

## What I Would Eliminate From MVP

Eliminate from MVP scope, not from long-term vision:

- standalone Business Value page,
- standalone Decision Ledger page before there is enough decision history,
- Policies and Settings as product navigation,
- SDKs,
- public Context API,
- AI Advisor,
- multi-cloud promise,
- broad connector roadmap,
- full policy engine,
- generic Knowledge Graph positioning,
- Kafka, Kubernetes, OpenSearch and data lake planning as near-term work,
- duplicate service/agent detection as a first recommendation family,
- negative-ROI feature analysis as a first recommendation family.

These create investor concern because they make the product look like a suite before it has a paid wedge.

## What Is Missing

### 1. A Narrow Paid Wedge

The docs say "One Decision. One Timeline. One ROI", but still keep many MVP modules. The paid wedge should be explicit:

**AI/cloud spend recovery for one shipped decision.**

### 2. A Concierge Pilot Workflow

The project needs a crisp pilot workflow:

1. customer selects one expensive AI/cloud decision,
2. IMPERATOR reconstructs the Jira -> GitHub -> AWS -> AI chain,
3. customer validates cost and usage evidence,
4. IMPERATOR proposes one action,
5. CTO or VP Engineering approves, rejects or defers after FinOps review,
6. realized value is validated later.

### 3. Proof Thresholds

The MVP needs thresholds before product expansion:

- 3 real customer decisions reconstructed,
- 2 recommendations judged approval-ready,
- 1 recommendation approved by CTO or VP Engineering after FinOps review,
- one measurable monthly saving or avoided cost,
- evidence trusted by a technical owner.

### 4. Simpler Phase 1 Architecture

Phase 1 should not start as many services. It should start as a modular monolith or tightly bounded service with clear internal modules.

D013 still applies when internal services exist. It should not force premature microservices.

### 5. Pricing Hypothesis

The docs need a sharper value unit:

**price against validated recovered value or reviewed Decision ROI Cases, not connectors, seats or APIs.**

## What Is Overengineered

### Product

Too many surfaces before the first repeatable paid use case:

- Executive Workspace,
- Decisions,
- Decision Ledger,
- Business Value,
- Integrations,
- Policies,
- Settings.

Investor refactor:

For MVP, ship one surface:

**Decision Review Workspace**

It contains:
- decision summary,
- timeline,
- evidence,
- ROI,
- recommendation,
- approval controls,
- ledger history.

### API

The conceptual API exposes a platform too early.

Investor refactor:

For MVP, anchor API intent around:
- `DecisionRoiCase`,
- `Evidence`,
- `Recommendation`,
- `LedgerEntry`.

Everything else is internal or future.

### Architecture

The current architecture names many bounded contexts before implementation.

Investor refactor:

Phase 1 modules:
- Integration Intake,
- Context Builder,
- ROI Calculator,
- Recommendation Rules,
- Ledger,
- Review UI.

Defer:
- AI Intelligence Layer as autonomous service,
- event broker,
- vector store,
- Graph DB,
- public API gateway,
- policy engine,
- multi-tenant enterprise admin surface.

### AI

AI should not be the core dependency for the first proof.

Investor refactor:

Use deterministic rules and explicit assumptions first. Use LLMs later for explanation, summarization and similarity once evidence quality is proven.

## Refactored MVP Definition

### MVP Name

**Decision Recovery Workflow**

### MVP Promise

In 30 days, reconstruct one expensive AI/cloud decision and produce one approval-ready recovery action.

### MVP User

Primary:
- CTO or VP Engineering

Required validators:
- Platform owner,
- FinOps or Finance owner.

### MVP Object

One Decision ROI Case:

Business Decision -> Technical Change -> Cost and Usage Evidence -> ROI -> Recommendation -> Approval -> Ledger -> Result Validation

### MVP Paid-Wedge Recommendation Focus

Primary paid wedge:
- AI model downgrade or model change,
- unused AI agent removal,
- underutilized AWS resource tied to the same decision.

Deferred:
- negative-ROI feature analysis,
- duplicated service or agent consolidation.

## Phase 1 Build Order

1. Manual/concierge Decision ROI Case reconstruction.
2. Decision Review Workspace mock/data-backed prototype.
3. Minimal connector intake for Jira, GitHub, AWS and AI usage exports.
4. ROI calculator with explicit assumptions.
5. Rule-based recommendation generator for the primary paid wedge.
6. Decision Ledger v2 append-only records.
7. Result validation loop.

Do not build public APIs, SDKs, AI Advisor, policy engine or broad connector framework before this loop works.

## Kill Criteria

Stop or pivot if:

- customers do not recognize the reconstruction problem,
- customers cannot provide enough data to estimate ROI,
- technical owners do not trust the evidence,
- FinOps does not care about decision-level attribution,
- no recommendation is approval-ready after 3 real decisions,
- the value recovered is too small to justify workflow adoption.

## Refactor Rules

- If it does not strengthen the first Decision ROI Case, defer it.
- If it does not help approval, defer it.
- If it does not improve evidence trust, defer it.
- If it requires a new platform surface, defer it.
- If it exists mainly to support future scale, defer it.
- If it helps sell the first paid pilot, keep it.
