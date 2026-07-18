# 12 — AI Agent Context Pack

## Purpose

Ensure consistent output when AI agents collaborate on strategy, documentation, analysis, or planning.

## Current State

Phase 0 (Idea).  
Agents must not create runnable implementation or infer product decisions that are not present in the canonical documents.

## Authority Order

When context conflicts, agents must use this order:

1. `docs/decisions/14_Decision_Log.md` for accepted decisions and chronology.
2. `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` for MVP product boundary.
3. `docs/product/24_MVP_Vertical_Slice.md` for the first end-to-end MVP path.
4. `docs/product/25_MVP_ROI_Slice.md` for ROI calculation, assumptions and validation rules.
5. `docs/product/CORE_DOMAIN_MODEL.md` for domain entities, relationships and invariants.
6. `docs/product/API_SPECIFICATION.md` for conceptual API surface.
7. `docs/product/DECISION_LEDGER_V2.md` for Decision Ledger module behavior.
8. `docs/architecture/21_Technical_Architecture_Context.md` for target architecture context.
9. `docs/architecture/DATABASE_MODEL.md` for conceptual data model.
10. `docs/architecture/CONNECTOR_FRAMEWORK.md` for integration boundaries.
11. `docs/architecture/26_Security_Data_Governance_Threat_Model.md` for evidence sensitivity, AI boundaries and security threat model.
12. `docs/product/28_Identity_Access_Approval_Model.md` for roles, permissions and approval authority.
13. `docs/product/27_MVP_Acceptance_Test_Plan.md` for pre-code acceptance gates.
14. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for first MVP screen behavior.
15. `docs/architecture/27_Quality_Attributes.md` for MVP non-functional quality expectations.
16. `docs/rfcs/0002-module-communication-architecture.md` for module communication rationale.
17. This file for agent behavior and response consistency.
18. `docs/product/13_Glossary_and_Canonical_Language.md` for terms and wording.

Founder-mode or master-prompt guidance sets ambition and quality bar. It does not override current decisions when it uses older framing such as AI Cost Attribution as the primary wedge, dashboard-led language, generic Decision Intelligence without the operating-system and Decision ROI framing, or production-ready implementation during Phase 0.

## Canonical Mandates

1. Use commercial positioning: **Operating System for Operational Intelligence** / **Enterprise Decision Intelligence Platform**.
2. Use intelligence-domain term: **Enterprise Context Intelligence**.
3. Use core narrative: **"We transform operational chaos into business decisions."**
4. Keep long-term ICP: Enterprise SaaS multi-cloud (100–500 employees, 50+ SaaS apps, active AI usage, Platform+DevOps+Security). For v1 paid validation, prefer AWS-first B2B SaaS using Jira, GitHub and OpenAI or Anthropic Claude in production.
5. Keep wedge: Cross-platform Decision Traceability, now expressed through a **Decision ROI Timeline** for the MVP.
6. Emphasize: Multi-stakeholder simultaneous value (Platform Eng → traceability; Finance/FinOps → ROI and costs; Security → risk; CTO → strategic visibility).
7. Emphasize: Neutrality as competitive moat (vs hyperscalers who cannot be neutral).
8. Use MVP rule: **"One Decision. One Timeline. One ROI."**
9. For v1, prioritize information domains over connector breadth:
   - Business Context: Jira
   - Code & Deployment: GitHub
   - Infrastructure & Cost: AWS
   - AI Consumption: OpenAI + Anthropic Claude
10. Defer Slack, Microsoft 365, Salesforce, Azure OpenAI, Google Gemini, Mistral, Azure DevOps, ServiceNow, Azure and GCP as expansion systems unless explicitly needed for a validated pilot.
11. Treat the MVP product surface as **Decision Review Workspace**. The broader `Workspace -> Decision -> Ledger` model is expansion after multiple Decision ROI Cases exist.
12. Do not require full navigation for MVP. Executive Workspace, standalone Ledger, Business Value, Policies and Settings are expansion surfaces.
13. Decision Review Workspace answers: can the company trust and approve this recovery action? It needs decision summary, timeline, evidence, ROI assumptions, recommendation, owner, approver, approve/reject/defer action and ledger history.
14. Decision Detail and Decision Review Workspace are the same MVP product idea unless a later product decision separates them.
15. Decision Ledger answers: what has the company decided over time? It is an immutable ledger of all business decisions, not only recommendations. Ledger v2 records approval, rejection, deferral, implementation and result-validation history without becoming a workflow engine.
16. Business Value answers: what economic value has IMPERATOR generated? It should use validated ledger outcomes and remain separate from estimated recovery.
17. Integrations answers: what operating systems are connected? For MVP, focus on AWS, GitHub, Jira and OpenAI + Anthropic Claude.
18. Use monthly savings in decision queues and annualized value for executive summaries.
19. Prioritize the MVP paid wedge: AI model downgrade/change, unused AI agent removal and underutilized AWS resource detection tied to the same Decision ROI Case. Defer negative-ROI feature analysis and duplicated service/agent consolidation.
20. Do not let product surface work drift into chart-heavy analytics or evidence overload.
21. Use `Review Decision` on the executive workspace; use `Approve Recommendation` only inside the decision detail page.
22. Use `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` as the canonical MVP blueprint.
23. Use `docs/product/CORE_DOMAIN_MODEL.md` as the canonical domain model.
24. Use `docs/product/API_SPECIFICATION.md` as the conceptual API contract before implementation.
25. Use `docs/product/DECISION_LEDGER_V2.md` as the Decision Ledger module contract.
26. Use `docs/product/24_MVP_Vertical_Slice.md` as the first end-to-end MVP path.
27. Use `docs/product/25_MVP_ROI_Slice.md` for ROI calculations, assumptions, confidence, risk and result validation.
28. Use `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` as the first manual evidence proof.
29. Use `docs/architecture/26_Security_Data_Governance_Threat_Model.md` for evidence visibility, AI filtering and Restricted-data boundaries.
30. Use `docs/product/27_MVP_Acceptance_Test_Plan.md` as the pre-code acceptance gate.
31. Use `docs/product/28_Identity_Access_Approval_Model.md` for roles, permissions and approval authority.
32. Use `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` for what the first MVP screen shows, hides, blocks and records.
33. Use `docs/architecture/27_Quality_Attributes.md` for explainability, auditability, freshness, traceability, latency, resilience, observability and performance non-goals.
34. Use `docs/architecture/DATABASE_MODEL.md` as the conceptual database model.
35. Use `docs/architecture/CONNECTOR_FRAMEWORK.md` as the connector expansion model.
36. Use `docs/rfcs/0002-module-communication-architecture.md` for communication design rationale.
37. Use `docs/architecture/21_Technical_Architecture_Context.md` as the canonical architecture context.
38. Do not change category/ICP/wedge/domain/API/database/connector/architecture boundary without logging in `docs/decisions/14_Decision_Log.md`.

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
- product surfaces that expand before the Decision Recovery Workflow is validated
- architecture proposals that bypass `docs/architecture/21_Technical_Architecture_Context.md`
- architecture proposals that treat conceptual bounded contexts as mandatory Phase 1 microservices
- implementation details that violate Phase 0 no-code boundaries

## Implementation references

For consistency between teams and agents, the following guidance applies for Phase 0. Note: the internal communication mandate D013 requires gRPC + Protocol Buffers for all internal service-to-service communication — this is binding for internal contracts. This mandate does **not** expose gRPC as a public SaaS API.

- Protocol Buffers (`.proto`) is the canonical contract format for internal messages. Maintain `proto/` as source of truth.
- gRPC is the required transport for internal RPCs between Operational, Context, AI and other internal services (see ADR D013 in `docs/decisions/adr/`).
- Kafka messages used as event transport SHOULD be encoded in Protobuf if and when broker-based ingestion is introduced.
- The Context Layer is the canonical input for any AI agent; agents must not read directly from raw sources. Use the Context Engine outputs (Decision Ledger, enriched records) as the agent input.
- Target architecture context lives in `docs/architecture/21_Technical_Architecture_Context.md`. It is authoritative for layer responsibilities, bounded contexts, product surface mapping and future stack direction.

Any further changes to this mandate or expansions must be recorded in `docs/decisions/14_Decision_Log.md`.
