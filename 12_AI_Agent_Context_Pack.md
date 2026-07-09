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
4. Keep wedge: Cross-platform Decision Traceability, now expressed through a **Decision ROI Timeline** for the MVP.
5. Emphasize: Multi-stakeholder simultaneous value (Platform Eng → traceability; Finance/FinOps → ROI and costs; Security → risk; CTO → strategic visibility).
6. Emphasize: Neutrality as competitive moat (vs hyperscalers who cannot be neutral).
7. Use MVP rule: **"One Decision. One Timeline. One ROI."**
8. For v1, prioritize information domains over connector breadth:
   - Business Context: Jira
   - Code & Deployment: GitHub
   - Infrastructure & Cost: AWS
   - AI Consumption: OpenAI / Azure OpenAI
9. Defer Slack, Microsoft 365, Salesforce, Anthropic, Azure DevOps, ServiceNow, Azure and GCP as expansion systems unless explicitly needed for a validated pilot.
10. Treat the product surface as `Workspace -> Decision -> Ledger`, not as a generic dashboard.
11. Navigation should use: Executive Workspace, Decisions, Decision Ledger, Business Value, Integrations, Policies, Settings.
12. Executive Workspace answers: which decision deserves attention now? It should show Summary -> Decision Queue -> Selected Decision -> Evidence -> Review action. Lifecycle belongs inside each Decision Detail.
13. Decision Detail answers: can the company trust this recommendation enough to approve it? Put ownership, status, chronology, historical outcomes and deep lifecycle evidence there.
14. Decision Ledger answers: what was recommended, approved, implemented and validated over time?
15. Use monthly savings in decision queues and annualized value for executive summaries.
16. Do not let product surface work drift into chart-heavy analytics or evidence overload.
17. Use `Review Decision` on the executive workspace; use `Approve Recommendation` only inside the decision detail page.
18. Do not change category/ICP/wedge without logging in `14_Decision_Log.md`.

## Writing Style

- clear, professional, low-hype
- business and operations oriented
- measurable and actionable

## Guardrails

Do not introduce:
- unvalidated technical claims
- absolute compliance guarantees
- ICP expansion without explicit rationale
- broad connector-roadmap promises before MVP validation
- ROI claims that are not tied to explicit assumptions
- dashboards that prioritize charts over executive decisions
- product screens that repeat the same KPI summary instead of separating prioritization, evidence and audit record

## Implementation references

For consistency between teams and agents, the following guidance applies for Phase 0. Note: the internal communication mandate D013 requires gRPC + Protocol Buffers for all internal service-to-service communication — this is binding for internal contracts. This mandate does **not** expose gRPC as a public SaaS API.

- Protocol Buffers (`.proto`) is the canonical contract format for internal messages. Maintain `proto/` as source of truth.
- gRPC is the required transport for internal RPCs between Operational, Context, AI and other internal services (see ADR D013 in `adr/`).
- Kafka messages used as event transport SHOULD be encoded in Protobuf to keep contracts consistent across brokers.
- The Context Layer is the canonical input for any AI agent; agents must not read directly from raw sources. Use the Context Engine outputs (Decision Ledger, enriched records) as the agent input.

Any further changes to this mandate or expansions must be recorded in `14_Decision_Log.md`.
