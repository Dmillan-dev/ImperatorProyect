# 12 — AI Agent Context Pack

## Purpose

Ensure consistent output when AI agents collaborate on strategy, documentation, analysis, or planning.

## Current State

Phase 0 (Idea).  
Agents must not assume undefined technical implementation.

## Canonical Mandates

1. Use term: **Enterprise Context Intelligence**.
2. Use core narrative: **"We transform operational chaos into business decisions."**
3. Keep ICP: Enterprise SaaS multi-cloud (100–500 employees, 50+ SaaS apps, active AI usage, Platform+DevOps+Security).
4. Keep wedge: Cross-platform Decision Traceability.
5. Emphasize: Multi-stakeholder simultaneous value (Platform Eng → MTTR; Finance → costs; Security → risk; CTO → strategic visibility).
6. Emphasize: Neutrality as competitive moat (vs hyperscalers who cannot be neutral).
7. Do not change category/ICP/wedge without logging in `14_Decision_Log.md`.

## Writing Style

- clear, professional, low-hype
- business and operations oriented
- measurable and actionable

## Guardrails

Do not introduce:
- unvalidated technical claims
- absolute compliance guarantees
- ICP expansion without explicit rationale

## Implementation references

For consistency between teams and agents, the following guidance applies for Phase 0. Note: the internal communication mandate D013 requires gRPC + Protocol Buffers for all internal service-to-service communication — this is binding for internal contracts. This mandate does **not** expose gRPC as a public SaaS API.

- Protocol Buffers (`.proto`) is the canonical contract format for internal messages. Maintain `proto/` as source of truth.
- gRPC is the required transport for internal RPCs between Operational, Context, AI and other internal services (see ADR D013 in `adr/`).
- Kafka messages used as event transport SHOULD be encoded in Protobuf to keep contracts consistent across brokers.
- The Context Layer is the canonical input for any AI agent; agents must not read directly from raw sources. Use the Context Engine outputs (Decision Ledger, enriched records) as the agent input.

Any further changes to this mandate or expansions must be recorded in `14_Decision_Log.md`.