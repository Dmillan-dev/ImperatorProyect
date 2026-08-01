# 14 — Decision Log

## Purpose

Official record of strategic decisions.  
Format: one entry per decision with date, rationale, and impact.

---

### [2026-07-06] D001 — Core intelligence domain naming
- Decision: use **Enterprise Context Intelligence** instead of Decision Intelligence.
- Rationale: stronger enterprise clarity, less AI-hype ambiguity.
- Impact: updated positioning and capability language.

### [2026-07-06] D002 — ICP focus
- Decision: focus on Enterprise SaaS multi-cloud (100–500 employees, 50+ SaaS apps, active AI, Platform+DevOps+Security).
- Rationale: highest probability of structural pain.
- Impact: refined GTM and buyer mapping.

### [2026-07-06] D003 — Wedge prioritization
- Decision: prioritize Cross-platform Decision Traceability as entry wedge.
- Rationale: immediate pain, clear buyer comprehension.
- Impact: reordered painkiller priorities.

### [2026-07-06] D004 — Core Mission textual clarity
- Decision: rename `Mission` to `Core Mission` with focus on searchability, explainability, measurability, and optimizability of enterprise decisions.
- Rationale: clearer, more compelling positioning; aligns with Enterprise Context Intelligence; improves external and internal communication.
- Impact: updated Project Charter; establishes foundation for sales narrative and land-and-expand GTM strategy.

### [2026-07-06] D005 — Sales narrative + land-and-expand model
- Decision: adopt explicit "land and expand" commercial messaging; create detailed Sales Narrative document for internal alignment; launch with specific problem focus (decision traceability + AI governance + cost attribution) rather than horizontal coverage.
- Rationale: reduces buyer skepticism, increases credibility, matches B2B SaaS investor expectations, creates natural upsell path (Platform → Security → Compliance → FinOps → Legal → Exec).
- Impact: new document (docs/business/16_Sales_Narrative_and_Commercial_Case.md); updates to GTM hypotheses; refined commercial claims in core positioning documents.

### [2026-07-06] D006 — Entry point pivot: Decision Traceability (MTTR) over AI Cost Attribution
- Decision: primary entry point is **Operational Decision Traceability** (with measurable MTTR reduction); AI cost attribution becomes secondary painkiller (flows from traceability, not primary driver).
- Rationale: "AI Cost Attribution" creates perception of dashboard competition (AWS, OpenAI already show costs); "Decision Traceability" solves tangible pain (80% MTTR reduction, 7K€/month labor savings per customer). Easier to justify, stronger differentiation, better ROI story (100 hours/month saved = 84K€/year for 250-person company).
- Impact: reorder painkillers in docs/business/04_Value_Proposition.md; update entry point and ROI in docs/business/08_Go_To_Market_Hypotheses.md; maintain "land and expand" thesis but with clearer primary wedge.

### [2026-07-06] D007 — Core brand message evolution
- Decision: strengthen brand thesis from "We are a layer" to **"We transform operational chaos into business decisions"**; emphasize that IMPERATOR connects existing systems, not replaces them.
- Rationale: original framing is infrastructure-focused (inside-out); market-leading positioning must be buyer-centric (outside-in). New framing directly addresses CTO/CFO pain: chaos → clarity. Aligns with B2B software principle: companies buy outcome improvements, not features.
- Impact: updated docs/business/01_Vision_and_Positioning.md; strengthens external sales messaging; clearer positioning against "another tool" objection.

### [2026-07-06] D008 — Multi-stakeholder simultaneous value as competitive advantage
- Decision: explicitly design IMPERATOR so that each stakeholder (Platform Eng Manager, Finance, Security, CTO) sees **different primary benefit on the same platform** during Year 1; this multi-departmental adoption is a moat against replacement and drives land-and-expand expansion.
- Rationale: B2B SaaS products that create value for >1 department have 3× higher retention and faster expansion. Platform Eng Manager cares about MTTR; CFO cares about cost attribution; CISO cares about risk; CTO cares about strategic visibility. If IMPERATOR delivers all four simultaneously, switching cost increases dramatically.
- Impact: influences product design priorities; shapes GTM messaging (lead with MTTR, but ensure Finance/Security/CTO see value in 30-day pilot); updates docs/business/08_Go_To_Market_Hypotheses.md and docs/business/16_Sales_Narrative_and_Commercial_Case.md with multi-buyer narrative.

### [2026-07-06] D009 — Neutrality as irreplicable competitive moat
- Decision: explicitly position IMPERATOR's **neutrality across hyperscalers** (AWS, Azure, GCP, OpenAI, etc.) as the core competitive advantage; recognize that no hyperscaler can be truly neutral due to lock-in business model.
- Rationale: this is the deepest moat. AWS, Microsoft, and OpenAI are vendors; IMPERATOR is an independent layer. Over time, IMPERATOR builds an operational knowledge base (millions of enriched decisions with cost, risk, outcome patterns) that cannot be easily replicated. Hyperscalers lack both neutrality and cross-system context.
- Impact: updated docs/business/07_Differentiation_and_Moat.md; clarifies moat defensibility in investor conversations; differentiates from "another observability dashboard" perception; supports position that IMPERATOR can remain independent long-term.

### [2026-07-06] D010 — Product surface model: Dashboard-led SaaS + open API
- Decision: define customer-facing product model as a SaaS platform with a primary Dashboard Web surface, supported by an open Context API, SDKs, and connectors; formalize the internal context-engine cycle as the hardest-to-replicate capability.
- Rationale: enterprise buyers validate value through visibility and outcomes, while technical teams require integration speed and ecosystem fit. This dual-entry model improves adoption and supports land-and-expand without changing category, ICP, or wedge.
- Impact: new canonical document docs/product/17_Product_Surface_and_Context_Engine_Thesis.md; updates to README index; update to Phase-0 scope and core capabilities language to include customer interaction surfaces at conceptual level.

### [2026-07-07] D011 — Arquitectura (Tesis): responsabilidades separadas y capa de contexto
- Decision: adoptar una tesis arquitectónica conceptual que priorice responsabilidades (Operational Intelligence Layer, Context Layer, AI Intelligence Layer) y que recomiende, de forma no vinculante, ingesta optimizada para JVM (Java) y experimentación IA en Python. Registrar el documento canónico `docs/architecture/18_Architecture_Thesis.md`.
- Rationale: separando las responsabilidades (ingesta a escala vs razonamiento contextual) se reduce la complejidad operacional y se preserva la capacidad de experimentación IA; el Context Layer (modelo de grafo de decisiones) es el activo estratégico y debe ser tratado como tal.
- Impact: añade `docs/architecture/18_Architecture_Thesis.md` como documento de alineación; mantiene el foco en `Enterprise Context Intelligence` y `Cross-platform Decision Traceability`; cualquier cambio de arquitectura o expansión del ICP deberá registrarse en este log.

### [2026-07-07] D012 — Recomendación: gRPC + Protobuf desde Day 1
- Decision: recomendar el uso de **gRPC** como mecanismo RPC y **Protocol Buffers** como formato de contrato canónico (incluyendo mensajes en Kafka) para comunicación Java ⇄ Python desde la fase inicial, manteniéndolo como recomendación no vinculante hasta validación del MVP.
- Rationale: contratos tipados, interoperabilidad entre lenguajes, facilidad para evolucionar servicios sin romper integraciones, y coherencia de mensajes a través de la plataforma.
- Impact: crear ADR D012; proponer un `proto/Decision.proto` inicial y validar un ejemplo end-to-end ingestion → AI.

### [2026-07-07] D013 — Mandato: gRPC+Protobuf para comunicaciones internas (vinculante)
- Decision: establecer como decisión vinculante que todas las comunicaciones entre servicios internos usen gRPC con Protocol Buffers como formato canónico; no exponer gRPC como API pública del SaaS.
- Rationale: reduce la fricción entre equipos, garantiza contratos tipados y facilita la evolución independiente de motores internos (ingesta vs IA).
- Impact: requiere repositorio `proto/` mantenido, CI de generación de bindings, y gobernanza de cambios breaking sobre `.proto`.

### [2026-07-08] D014 — Executive dashboard hierarchy shift
- Decision: reframe the executive dashboard demo around one dominant hero KPI, a concise executive summary, and action-oriented cards instead of equal-weight charts and blocks.
- Rationale: the sales story is stronger when the first screen answers "what matters now" before showing supporting detail.
- Impact: updated `demos/executive_dashboard_demo/index.html` and `demos/executive_dashboard_demo/style.css` to emphasize narrative hierarchy and one-click action paths.

### [2026-07-08] D015 — Approval-first executive action model
- Decision: present executive actions as review-based approvals, not autonomous changes; use explicit decision states and evidence-backed confidence framing.
- Rationale: CTOs and CEOs want assistance with judgment, not unapproved automation.
- Impact: updated the executive dashboard demo to show interactive flow states, confidence evidence, and approval-oriented CTA language.

### [2026-07-08] D016 — MVP focus: information domains and Decision ROI Timeline
- Decision: reframe the MVP around four normalized information domains — Business Context, Code & Deployment, Infrastructure & Cost, and AI Consumption — with Jira, GitHub, AWS, and OpenAI + Anthropic Claude as the initial systems. Adopt **"One Decision. One Timeline. One ROI."** as the technical product rule for v1.
- Rationale: the strongest MVP is not broad connector coverage; it is one complete business story that links why a decision existed, who implemented it, what resources it created, what AI it consumed, and what ROI action is available now.
- Impact: updates canonical messaging, buyer map, GTM, validation, product surface, executive summary, and dashboard demo. Cross-platform Decision Traceability remains the foundation, but the MVP narrative is now the living ROI of a business decision.

### [2026-07-08] D017 — Executive decision dashboard: future-facing approval model
- Decision: evolve the executive dashboard from a backward-looking recovered-value report into a future-facing approval surface. The primary frame is now "If approved today" with annual savings, payback, risk and confidence, supported by a Why block, ownership, decision status and a Decision Lifecycle.
- Rationale: executives do not buy charts; they buy answers. IMPERATOR should help a CEO/CTO/CFO decide what to approve today, why it matters, who owns it and how much value will be recovered.
- Impact: updated the executive dashboard demo and executive summary. `Executive Operational Intelligence` describes the executive experience, while `Enterprise Context Intelligence` remains the canonical intelligence domain.

### [2026-07-08] D018 — Executive Decision Workspace value model
- Decision: evolve the dashboard demo into an **Executive Decision Workspace**. The workspace shows a large Projected Annual Savings number, a four-part Annual Business Value breakdown, a decision queue with monthly savings, a future-facing approval CTA, ROI/business impact, confidence based on event count, chronology and clickable lifecycle evidence.
- Rationale: the CFO buys ROI and the CEO buys fast executive answers. Monthly savings make the decision queue actionable, while annualized value and component breakdown explain total business value beyond cost reduction.
- Impact: updated the executive dashboard demo and supporting demo README. The workspace remains answer-first and chart-light.

### [2026-07-08] D019 — MVP dashboard density reduction
- Decision: simplify the primary executive workspace to six blocks: Summary, Decision Queue, Selected Decision, Evidence, Lifecycle and Review action. Move ownership, status, chronology, full financial impact and detailed lifecycle evidence into a separate decision detail page.
- Rationale: the MVP screen must make the most important answer obvious at a glance. Dense evidence belongs behind review, not on the executive summary surface.
- Impact: updated the dashboard demo with `index.html` as the glanceable workspace and `decision_detail.html` as the review/investigation page. The CTA changed from direct execution to `Review Decision`, preserving the principle that IMPERATOR recommends and the company decides.

### [2026-07-08] D020 — Workspace -> Decision -> Ledger product surface
- Decision: define the commercial product surface as `Workspace -> Decision -> Ledger` with navigation for Executive Workspace, Decisions, Decision Ledger, Business Value, Integrations, Policies and Settings.
- Rationale: IMPERATOR should feel like a decision operating system, not a monitoring dashboard. The Workspace prioritizes executive attention, the Decision Detail validates trust through evidence, and the Ledger records accountability and outcomes.
- Impact: added product navigation to the demo, introduced `decision_ledger.html`, and updated product-surface and agent-context guidance to separate prioritization, evidence review and audit record.

### [2026-07-08] D021 — Workspace lifecycle removal and dark executive visual language
- Decision: remove the Decision Lifecycle from the primary Executive Workspace and keep lifecycle evidence inside each Decision Detail. Shift the demo visual language toward a dark, minimal executive workspace with teal value accents and stronger card hierarchy.
- Rationale: the Workspace should answer what deserves attention now without repeating the evidence structure that already appears per decision. A darker, more shaped visual system makes the experience feel less like a generic dashboard and more like an executive decision workspace.
- Impact: updated `index.html`, `style.css`, demo README and product-surface guidance. The Workspace now centers on money, decision, evidence and action.

### [2026-07-09] D022 — Expanded executive product surface: Home, Decisions, Ledger, Business Value, Integrations
- Decision: evolve the HTML demo into five distinct surfaces: Executive Workspace as the company-status Home, Decisions as the evidence and approval workspace, Decision Ledger as the immutable record of all business decisions, Business Value as the economic proof page, and Integrations as the operating connectivity map.
- Rationale: executives need a simple Home, responsible teams need decision evidence, the company needs an audit ledger, the CIO needs renewal proof, and the MVP needs clear integration boundaries.
- Impact: removed the final Action rectangle from `index.html`, added a decision list and final approval action to `decision_detail.html`, rewrote `decision_ledger.html`, created `business_value.html` and `integrations.html`, and updated demo styling/documentation.

### [2026-07-09] D023 — Canonical MVP Blueprint and AI provider normalization
- Decision: add `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` as the canonical MVP blueprint and normalize the AI Consumption boundary to **OpenAI + Anthropic Claude**.
- Rationale: the project needed one authoritative MVP structure covering category, integrations, core object, priority recommendations, product surfaces and validation criteria. Normalizing the AI provider language prevents drift between OpenAI, Claude, Anthropic and Azure OpenAI wording.
- Impact: updated core strategy docs, sales narrative, validation plan, architecture thesis, RFC, demo copy and agent guidance around the Decision ROI Platform / Executive Operational Intelligence MVP.

### [2026-07-13] D024 — Canonical Technical Architecture Context
- Decision: add `docs/architecture/21_Technical_Architecture_Context.md` as the canonical target architecture context for IMPERATOR.
- Rationale: the project needed a controlled architecture map that connects the Decision ROI Platform strategy with future implementation responsibilities: product surfaces, bounded contexts, connector boundaries, Context Engine, ROI Engine, Recommendation Engine, AI layer, Decision Ledger, communication contracts and Phase 0 guardrails.
- Impact: updated architecture thesis, README, AI agent context pack, repo structure docs, Phase 0 guidelines, RFC guidance and agent/service READMEs. This does not start implementation; it prepares Phase 1 planning while preserving Phase 0 no-code boundaries.

### [2026-07-15] D025 — Context authority and Phase 0 interpretation
- Decision: establish an explicit context authority order: Decision Log -> MVP Blueprint -> Technical Architecture Context -> AI Agent Context Pack -> Glossary. Treat founder-mode prompts and older documents as ambition or historical context when they conflict with current canonical decisions.
- Rationale: the project now has a strong founder/CTO master prompt plus several evolved strategic documents. Without an authority order, agents can accidentally revive older framing such as AI Cost Attribution as the primary wedge, dashboard-led product language, Enterprise Decision Intelligence as the current category, or production-ready implementation during Phase 0.
- Impact: updated README, Project Charter, AI Agent Context Pack, Glossary, Context Boundaries, Executive Summary, Architecture Thesis, Technical Architecture Context, weekly prompt, docs/decisions/adr/RFC notes and agent/service READMEs. Phase 0 permits documentation, conceptual architecture, contracts and scaffolding, but not runnable production services.

### [2026-07-15] D026 — Documentation zoning and core domain contracts
- Decision: organize documentation under `docs/business`, `docs/product`, `docs/architecture`, `docs/ai`, `docs/rfcs`, `docs/decisions` and `docs/research`. Add `docs/product/CORE_DOMAIN_MODEL.md` as the canonical business domain model and `docs/product/API_SPECIFICATION.md` as the conceptual API surface before implementation.
- Rationale: the project is already large enough that mixing business, product and technical documents creates navigation and authority risk. The domain model is the shared business contract that Java, Python, PostgreSQL, React and AI agents must follow before code exists.
- Impact: moved strategic documents into zones, moved ADRs under `docs/decisions/adr`, renamed RFC location to `docs/rfcs`, added `docs/README.md`, updated root README and internal references, and established Core Domain Model + API Specification as product-level contracts.

### [2026-07-15] D027 — Future development context foundations
- Decision: add three Phase 0 theoretical foundations for future development: `docs/rfcs/0002-module-communication-architecture.md`, `docs/architecture/DATABASE_MODEL.md` and `docs/architecture/CONNECTOR_FRAMEWORK.md`.
- Rationale: before implementation, IMPERATOR needs explicit context for how modules communicate, how data is conceptually organized and how connectors can be added without modifying the core Decision ROI Case domain.
- Impact: clarified the five future-development foundations: Domain Model, API Specification, Architecture RFC, Database Model and Connector Framework. Updated documentation indexes, AI agent context, architecture context and agent READMEs to reference these foundations. No runnable code or implementation work is authorized.

### [2026-07-16] D028 — Decision Ledger v2 module contract
- Decision: add `docs/product/DECISION_LEDGER_V2.md` as the canonical functional contract for the Decision Ledger module.
- Rationale: the ledger is strategically important for trust and accountability, but it must stay narrower than a workflow engine, compliance suite or autonomous execution system. The module needs explicit use cases, domain model, conceptual API, events, risks and acceptance criteria before implementation.
- Impact: Decision Ledger v2 records immutable approval, rejection, deferral, implementation and result-validation history for Decision ROI Cases. It preserves evidence, ROI and assumptions snapshots, separates estimated from realized savings, feeds Business Value only through validated outcomes and remains subordinate to the MVP rule: One Decision. One Timeline. One ROI.

### [2026-07-16] D029 — Technical investor refactor: Decision Recovery Workflow
- Decision: refactor the current MVP interpretation around a narrower paid wedge: **Decision Recovery Workflow** for AI/cloud spend. Keep the long-term Decision ROI Platform vision, but treat broad surfaces, SDKs, public API, AI Advisor, policy engine, graph/vector infrastructure, multi-cloud expansion, negative-ROI feature analysis and duplicate service/agent consolidation as post-validation.
- Rationale: a technical investor would fund a repeatable paid workflow before a broad platform. The previous documentation correctly defined the strategic vision, but it risked overbuilding product surfaces, recommendation engines and architecture before proving that customers will approve and pay for one recovery action.
- Impact: updated MVP blueprint, value proposition, GTM, ICP subsegment, business model, risks, validation plan, sales narrative, API boundary, architecture context, AI agent guidance and context boundaries. Phase 1 should prove the Decision Recovery Workflow with a simple architecture before expanding platform scope.

### [2026-07-17] D030 — MVP context coherence audit
- Decision: preserve the Decision Recovery Workflow as the only MVP execution focus and clarify ambiguous wording that could imply five recommendation families or broad product surfaces in v1.
- Rationale: the documentation now contains the right foundations, but future readers and AI agents need a crisp distinction between the paid wedge and post-validation expansion areas.
- Impact: no strategic scope change. Updated product, business and investor-audit wording so AI model optimization, unused AI agent removal and underutilized AWS resource detection remain the MVP paid-wedge focus; negative-ROI feature analysis and duplicate service/agent consolidation remain deferred. This wording is superseded for implementation scope by D065, which narrows the MVP to one recommendation family first.

### [2026-07-17] D031 — Visual operational explainer
- Decision: add `docs/product/23_IMPERATOR_Visual_Operational_Explainer.md` as a derived teaching document for explaining IMPERATOR through diagrams, demo mapping and an operational walkthrough.
- Rationale: the project needs one didactic artifact that explains the product without forcing readers to traverse the full strategy, architecture, domain model and demo files.
- Impact: no change to MVP scope or authority order. The explainer must remain subordinate to the MVP Blueprint, Core Domain Model, API Specification, Decision Ledger v2 and Architecture Context.

### [2026-07-17] D032 — Operating System for Operational Intelligence positioning
- Decision: adopt **Operating System for Operational Intelligence** as the primary commercial metaphor and allow **Enterprise Decision Intelligence Platform** as the external category label when paired with the Decision ROI and operational intelligence narrative.
- Rationale: the ERP/CRM/SIEM/Observability comparison makes the category easier to understand: ERP manages resources, CRM manages customers, SIEM manages security, Observability manages systems, and IMPERATOR manages decisions.
- Impact: no change to MVP scope. The MVP still demonstrates the Decision Recovery Workflow through one end-to-end flow, later clarified in D036 as: Event -> Connector -> Decision Engine -> ROI Engine -> Recommendation -> Decision Ledger -> Decision Review Workspace.

### [2026-07-17] D033 — MVP vertical slice and project structure before code
- Decision: add `docs/product/24_MVP_Vertical_Slice.md` and `docs/architecture/24_MVP_Project_Structure.md` to define the first end-to-end MVP path and future project structure without creating services or code.
- Rationale: the project needs one defendable implementation target before Phase 1. The first structure should prove a single Decision ROI Case instead of creating premature microservices, infrastructure or broad platform surfaces.
- Impact: no runnable implementation added. The accepted next structuring step is to validate the AI Onboarding Assistant Recovery slice and prepare manual evidence/ROI/ledger artifacts before any source-code scaffolding.

### [2026-07-18] D034 — MVP ROI slice before implementation
- Decision: add `docs/product/25_MVP_ROI_Slice.md` to define how the MVP calculates, explains and validates ROI for one Decision ROI Case.
- Rationale: ROI is the executive proof point of IMPERATOR. Before implementation, the project needs a shared model for current cost, estimated recovery, assumptions, confidence, risk, approval snapshots and realized value validation.
- Impact: no code added. The ROI slice separates estimated recovery from validated recovered value and keeps Business Value dependent on ledger-backed result validation.

### [2026-07-18] D035 — Spanish one-page project prompt
- Decision: add `docs/ai/IMPERATOR_One_Page_Project_Prompt_ES.md` as a concise Spanish prompt for quickly explaining or resuming IMPERATOR context.
- Rationale: the project now has enough canonical material that future AI sessions and human collaborators need a short onboarding artifact that preserves MVP focus without reopening the whole documentation set.
- Impact: no scope change and no code added. The prompt summarizes the current positioning, MVP rule, vertical slice, ROI rules, product surface and context authority.

### [2026-07-18] D036 — Pre-code architecture readiness audit
- Decision: add `docs/architecture/25_Pre_Code_Architecture_Readiness_Audit.md` and clarify the canonical MVP flow as Event -> Connector -> Decision Engine -> ROI Engine -> Recommendation -> Decision Ledger -> Decision Review Workspace.
- Rationale: the ledger must preserve evidence, ROI and assumptions snapshots after a case is reviewable; it should not appear to calculate ROI or orchestrate workflow. Before code, the project also needs explicit gates for manual evidence, security/data boundaries, approval authority, acceptance tests and quality attributes.
- Impact: no code added. Updated MVP blueprint, vertical slice, visual explainer, architecture context and repository structure docs to keep the MVP coherent and documentation-only during Phase 0.

### [2026-07-18] D037 — Manual Evidence Pack for AI Onboarding Assistant Recovery
- Decision: add `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` as the first manual evidence pack for the MVP vertical slice.
- Rationale: before implementation, IMPERATOR must prove that Jira, GitHub, AWS and OpenAI + Anthropic Claude evidence can manually reconstruct one Decision ROI Case, feed ROI, justify one recommendation and create valid Decision Ledger snapshots.
- Impact: no code added. Updated product and architecture indexes, MVP vertical slice, ROI slice, project structure and AI prompt references. The next pre-code priority becomes security/data governance, approval authority and acceptance testing.

### [2026-07-18] D038 — Security, Data Governance and Threat Model
- Decision: add `docs/architecture/26_Security_Data_Governance_Threat_Model.md` as the canonical Phase 0 security and data-governance context for evidence, connectors, AI input, ledger snapshots and product surfaces.
- Rationale: IMPERATOR's value depends on trusted evidence. Before real connectors or customer data exist, the project needs explicit boundaries for sensitivity, least privilege, tenant isolation, raw-payload handling, prompt-injection risk, AI filtering, ledger integrity and manual pilot data handling.
- Impact: no code added. Updated README, documentation map, architecture structure, database model, connector framework, technical architecture context, pre-code audit, manual evidence pack and AI prompt authority order. The next pre-code priority becomes `docs/product/27_MVP_Acceptance_Test_Plan.md`.

### [2026-07-18] D039 — MVP Acceptance Test Plan
- Decision: add `docs/product/27_MVP_Acceptance_Test_Plan.md` as the canonical pre-code acceptance plan for the first MVP slice.
- Rationale: Phase 1 should not invent behavior during implementation. The project needs manual acceptance scenarios for evidence, Decision ROI Case reconstruction, ROI, recommendation readiness, approval/rejection/deferral, ledger snapshots, security boundaries, conceptual API intent and Decision Review Workspace behavior.
- Impact: no code added. Updated README, documentation map, architecture structure, technical architecture context, vertical slice, security model, pre-code audit and AI prompt authority order. The acceptance plan exposes the next P0 gap: `docs/product/28_Identity_Access_Approval_Model.md`.

### [2026-07-18] D040 — Identity, Access and Approval Model
- Decision: add `docs/product/28_Identity_Access_Approval_Model.md` as the canonical MVP product-governance model for roles, evidence access, approval, rejection, deferral, implementation marking and result validation.
- Rationale: the acceptance plan defined what must happen, but the MVP still needed explicit human authority. For the AI Onboarding Assistant Recovery slice, Business Owner confirms impact, Platform Lead confirms feasibility and marks implementation, FinOps validates cost and realized value, Security can defer for data risk, and CTO or VP Engineering owns final approval or rejection. AI and connectors have no decision authority.
- Impact: no code added. Updated README, documentation map, architecture structure, technical architecture context, API specification, Decision Ledger v2, security model, pre-code audit, acceptance plan and AI prompt authority order. The next product contract is `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`.

### [2026-07-18] D041 — Decision Review Workspace Screen Contract
- Decision: add `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` as the canonical MVP screen contract for the first operational review surface.
- Rationale: the project already defined what must be proven and who may act. The first MVP screen now needs exact rules for what it shows, hides, blocks and records so Phase 1 does not invent UI behavior during implementation.
- Impact: no code added. Updated README, documentation map, architecture structure, technical architecture context, MVP project structure, vertical slice, security model, pre-code audit, acceptance plan, identity model, AI context prompts, glossary and secondary product/business docs. The next architecture artifact is `docs/architecture/27_Quality_Attributes.md`.

### [2026-07-18] D042 — MVP Quality Attributes
- Decision: add `docs/architecture/27_Quality_Attributes.md` as the canonical MVP non-functional quality contract before implementation.
- Rationale: after defining evidence, ROI, ledger, security, acceptance, identity and first-screen behavior, the project needed explicit quality expectations for explainability, auditability, evidence freshness, traceability, latency, reliability, graceful degradation, observability, reversibility and performance non-goals.
- Impact: no code added. Updated README, documentation map, architecture structure, technical architecture context, MVP project structure, security model, pre-code audit, acceptance plan, identity model and AI context prompts. The next architecture artifacts are per-connector MVP contracts and event/evidence vocabulary.

### [2026-07-19] D043 — Per-Connector MVP Contracts and AI Agent Work Partition
- Decision: add `docs/architecture/28_Per_Connector_MVP_Contracts.md` as the canonical Phase 0 contract for Jira, GitHub, AWS and OpenAI + Anthropic Claude, and add `agents/README.md` plus Connector, FinOps and QA agent areas to separate future AI-agent work.
- Rationale: before implementation, the project must define exactly what each MVP connector contributes and which agent owns product meaning, source mapping, cost validation, security, quality and architecture coherence. This prevents provider logic from leaking into ROI, recommendation, approval or ledger authority.
- Impact: no code added. Updated documentation maps, architecture context, readiness audit, connector framework, database model, product contracts and AI prompts so future work is organized by agent ownership. The next Phase 0 artifact is `docs/architecture/29_Event_Evidence_Vocabulary.md`.

### [2026-07-19] D044 — Event/Evidence Vocabulary and I+D Evidence Dossier
- Decision: add `docs/architecture/29_Event_Evidence_Vocabulary.md` as the canonical vocabulary for MVP normalized events, evidence types, lifecycle states, blockers, freshness, sensitivity and confidence labels; add `docs/rnd/` as the project-development evidence area for future architecture, code, hours, technical objects, experiments, tests and monthly summaries.
- Rationale: before code, future agents and developers need stable names for evidence and events. If IMPERATOR later becomes a startup or seeks I+D/i support, the project also needs systematic evidence discipline from day one instead of reconstructing activity, hours and technical outputs after the fact.
- Impact: no code added. Updated README, documentation map, architecture structure, architecture context, readiness audit, product contracts, AI prompts and agent operating model. Future implementation should use the vocabulary before naming events/tests and use `docs/rnd/` to record real development evidence without inventing hours or results.

### [2026-07-19] D045 — Phase 0 Closure Readiness Review and canonical lifecycle cleanup
- Decision: add `docs/architecture/30_Phase_0_Closure_Readiness_Review.md` as the final Phase 0 readiness gate and align the canonical MVP lifecycle across domain, product, ledger, connector vocabulary, prompts and demo context.
- Rationale: the project had enough Phase 0 foundations, but several older phrases still skipped ROI View, Decision Review Workspace or Result Validation, placed Business Value too early, used an unregistered approval event name or retained broad post-MVP recommendation language. These small inconsistencies could cause independent AI agents to implement divergent versions of the MVP.
- Impact: no code added. The current MVP flow is now `Operational Event -> Connector Intake -> Normalized Evidence -> Decision ROI Case -> ROI View -> Recommendation -> Decision Ledger Entry -> Decision Review Workspace -> Result Validation`. Realized Business Value remains valid only after `result_validated`. Future Phase 1 work requires a recorded go/no-go decision using the closure review.

### [2026-07-19] D046 — Reduced MVP Implementation Standard
- Decision: add `docs/architecture/31_MVP_Implementation_Standard.md` as the future Phase 1 implementation standard for a reduced MVP using Hexagonal Architecture / Ports and Adapters, Java/Spring backend, Next.js frontend, PostgreSQL persistence, JWT/OAuth2-compatible auth direction and minimal observability.
- Rationale: the MVP gains value by proving one Decision ROI Case with the fewest components possible. Hexagonal boundaries keep Jira, GitHub, AWS, OpenAI + Anthropic Claude, PostgreSQL and OAuth providers replaceable adapters instead of domain dependencies. JWT/OAuth2 and observability are important for SaaS credibility, but should be introduced minimally, not as broad platform scope.
- Impact: no code added. Kafka, Kubernetes, Terraform, OpenSearch, graph/vector databases, AI Advisor, public APIs, SDKs, full observability platform, multiple OAuth providers and broad platform surfaces remain deferred. Future agents must use the implementation standard before proposing Phase 1 scaffolding, auth, connector adapters or observability work.

### [2026-07-19] D047 — Phase 1 MVP Scope, AI Boundary and Exit Criteria
- Decision: add `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` as the canonical Phase 1 execution contract. Phase 1 must prove one functional Decision ROI Case end to end, with a minimal data model, one deterministic recommendation, one or two narrow source adapters at most, AI used only for natural-language explanation, minimal auth and minimal observability.
- Rationale: the project is now mature enough to avoid additional broad context expansion before implementation. The first build should prove value with the smallest possible surface: authenticated user, connected or imported evidence source, deterministic rules, one recommendation, AI explanation, human review, ledger state, logs, metrics, `/health` and `/ready`.
- Impact: no code added and Phase 1 is not automatically started. Future limited scaffolding is allowed only under the boundaries in `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`. Everything not essential to one Decision ROI Case remains out of scope, including multiple recommendations, ranking, learning systems, full connector automation, full observability platform, Kafka, Kubernetes, Terraform, graph/vector stores, public APIs, SDKs and autonomous execution. AI cannot modify persistent data, execute rules, replace the Decision Engine, approve, reject, defer, mark implementation or validate results.

### [2026-07-19] D048 — Phase 1 Agentic MVP Creation Guide
- Decision: add `agents/phase1/README.md` as the staged operating guide for creating Phase 1 with autonomous agents.
- Rationale: the project now has a clear Phase 1 scope contract, but autonomous agents need an execution process that preserves authority, handoffs, acceptance gates and R&D evidence discipline. The guide translates the existing context into staged agent work without creating implementation.
- Impact: no code added and Phase 1 is not automatically started. Future agent work should use the guide to move through context control, product case lock, acceptance, scaffolding plan, data, evidence intake, deterministic ROI/recommendation, AI explanation, ledger/approval, review workspace, auth/observability/security, integrated demo and closure. Any agent that needs broader scope must hand off to CTO Agent and update the Decision Log before proceeding.

### [2026-07-20] D049 — Phase 1 Context Control Go/No-Go
- Decision: add `agents/phase1/00_context_control.md` as the formal Stage 00 control artifact. Phase 1 receives a GO only for limited MVP scaffolding under `docs/architecture/31_MVP_Implementation_Standard.md`, `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` and `agents/phase1/README.md`.
- Rationale: before any implementation scaffolding, the project needs a recorded control decision for the first case, first source mode, first auth mode, observability minimum and R&D evidence capture. This prevents autonomous agents from treating Phase 1 as full platform construction.
- Impact: no code added. `DRC-AOA-001` remains the first case, AI model downgrade/change remains the first recommendation family, the first source mode is manual/static or file/import evidence, the first auth mode is local/demo JWT-compatible role claims, observability is limited to structured logs, correlation ID, `/health`, `/ready` and basic metrics, and R&D evidence capture starts with the first real implementation activity. Live connectors, enterprise SSO, full observability, public APIs, SDKs, Kafka, Kubernetes, Terraform, graph/vector/search infrastructure and full platform surfaces remain deferred.

### [2026-07-20] D050 — Phase 1 Product Case Lock
- Decision: add `agents/phase1/01_product_case_lock.md` as the locked operational product brief for `DRC-AOA-001` before any Phase 1 scaffolding.
- Rationale: autonomous agents need one fixed case narrative, evidence-to-claim map, ROI lock, recommendation wording, review path, workspace expectations and acceptance seeds before they can safely plan implementation. This prevents drift into alternate MVP stories, multiple recommendations or broader platform surfaces.
- Impact: no code added. `DRC-AOA-001` is locked as AI Onboarding Assistant Recovery for PilotCo SaaS using manual/static or file/import evidence first. The locked recommendation is a deterministic AI model downgrade/change with high-cost fallback for exceptions. Current monthly cost remains EUR2,340, estimated monthly recovery EUR1,620, estimated annualized recovery EUR19,440, confidence 92/100 and realized saving unavailable until result validation. Stage 02 should create the acceptance contract from this case lock.

### [2026-07-20] D051 — Phase 1 Acceptance Contract
- Decision: add `agents/phase1/02_acceptance_contract.md` as the acceptance contract for `DRC-AOA-001` before implementation scaffolding.
- Rationale: future implementation tasks need explicit pass/fail criteria, negative tests and traceability to the locked product case. Acceptance must prove one authenticated reviewer, one source path, one Decision ROI Case, one ROI View, one deterministic recommendation, one AI explanation, one review action, one ledger history and minimal health/observability.
- Impact: no code added. The acceptance contract rejects multiple cases, multiple recommendations, ranking, learning, AI decision-making, connector-owned ROI, ledger mutation, raw prompts/completions, public APIs, SDKs, Kafka, Kubernetes, Terraform, graph/vector/search infrastructure, full observability and multiple OAuth providers. Stage 03 should create the architecture scaffolding plan without creating code.

### [2026-07-20] D052 — Phase 1 Architecture Scaffolding Plan
- Decision: add `agents/phase1/03_architecture_scaffolding_plan.md` as the minimal architecture plan before any implementation files are created.
- Rationale: the project needs one shared technical shape for autonomous agents: modular monolith or tightly bounded service, Hexagonal Architecture, conceptual ports/adapters, manual/import evidence first, deterministic recommendation, AI explanation adapter, append-only ledger and minimal auth/observability. This prevents agents from turning conceptual bounded contexts into microservices.
- Impact: no code added. The plan permits only reduced architecture planning and keeps Kafka, Kubernetes, Terraform, graph/vector/search, public APIs, SDKs, broad live connectors, multiple OAuth providers, full SSO and full observability deferred. Stage 04 should define the data and persistence slice without schema or migrations.

### [2026-07-20] D053 — Phase 1 Data And Persistence Slice
- Decision: add `agents/phase1/04_data_persistence_slice.md` as the minimal data and persistence concept for Phase 1.
- Rationale: before scaffolding, autonomous agents need a small persistence boundary that supports only `DRC-AOA-001`, evidence summaries, ROI assumptions, one recommendation, AI explanation, actor/role references and append-only ledger entries.
- Impact: no code added, no SQL and no migrations. Full organization hierarchy, enterprise tenant admin, policy engine, connector marketplace, broad sync history, event sourcing, analytics warehouse, graph/vector/search projections, multiple recommendation queues and model training data remain deferred. Stage 05 should define evidence intake using manual/static or file/import evidence first.

### [2026-07-20] D054 — Phase 1 Evidence Intake Slice
- Decision: add `agents/phase1/05_evidence_intake_slice.md` as the evidence intake contract for the first Phase 1 MVP source path.
- Rationale: `DRC-AOA-001` must be proven from normalized, sensitivity-checked and freshness-labeled evidence before live connector automation is introduced. Manual/static or file/import evidence is enough to demonstrate the business reasoning path without confusing provider integration with product value.
- Impact: no code added. Connectors or import adapters may read, classify and normalize evidence only. They must not calculate ROI, create recommendations, approve, reject, defer, mark implementation, validate results, write ledger entries directly, persist raw AI prompts/completions, store secrets or mutate provider systems. Stage 06 should define deterministic ROI and recommendation behavior from evidence summaries.

### [2026-07-20] D055 — Phase 1 Domain, ROI And Recommendation Slice
- Decision: add `agents/phase1/06_domain_roi_recommendation_slice.md` as the deterministic domain, ROI and recommendation contract for `DRC-AOA-001`.
- Rationale: Phase 1 must prove IMPERATOR's core value with evidence, deterministic rules, one ROI View and one recommendation before any AI explanation or human approval action. This keeps the LLM outside business decision authority and prevents recommendation ranking, learning or autonomous execution from entering the MVP.
- Impact: no code added. The locked calculation remains AWS cost EUR410 plus AI cost EUR1,930 equals current monthly cost EUR2,340; projected monthly cost after action EUR720; estimated monthly recovery EUR1,620; annualized recovery EUR19,440. Exactly one recommendation is allowed: AI model downgrade/change with fallback. Realized saving remains unavailable until result validation. Stage 07 should define AI explanation using prepared context only.

### [2026-07-20] D056 — Phase 1 AI Explanation Slice
- Decision: add `agents/phase1/07_ai_explanation_slice.md` as the AI explanation contract for Phase 1.
- Rationale: IMPERATOR may use AI to make a deterministic recommendation understandable, but not to decide, calculate ROI, create evidence, mutate persistence, approve, reject, defer, mark implementation or validate results. A prepared-context contract is needed so future agents do not convert the MVP into an AI-first autonomous decision system.
- Impact: no code added. AI receives only normalized evidence summaries, ROI output, assumptions, risk, confidence and the already-created recommendation. Raw prompts, raw completions, secrets, provider payload dumps, Restricted evidence and unknown-sensitivity data are excluded. Stage 08 should define append-only ledger and human approval behavior without treating AI output as authority.

### [2026-07-20] D057 — Phase 1 Ledger And Approval Slice
- Decision: add `agents/phase1/08_ledger_approval_slice.md` as the append-only ledger and human review contract for Phase 1.
- Rationale: after evidence, ROI, recommendation and AI explanation are defined, IMPERATOR needs auditable human authority. The MVP must preserve what was reviewed, who acted, why they acted and which evidence, ROI, assumptions and recommendation snapshot were visible at the time.
- Impact: no code added. The ledger records history but does not calculate ROI or create recommendations. AI, connectors and import adapters have no approval authority. Approval and rejection belong to CTO or VP Engineering; deferral can be performed by the relevant authorized role; implementation marking belongs to Technical Owner; result validation belongs to FinOps or authorized business reviewer. Realized Business Value remains blocked until `result_validated`. Stage 09 should define the Decision Review Workspace around these states.

### [2026-07-20] D058 — Phase 1 Decision Review Workspace Slice
- Decision: add `agents/phase1/09_review_workspace_slice.md` as the first-screen contract for Phase 1 implementation planning.
- Rationale: after the case, ROI, recommendation, AI explanation and ledger rules are locked, the MVP needs one operational screen that lets an authorized reviewer decide whether to approve, reject or defer the recovery action. The first screen must not become a broad executive dashboard or AI chat surface.
- Impact: no code added. The workspace must show one case, one recommendation, ROI View, evidence summaries, assumptions, AI explanation, review actions and ledger timeline. It must hide raw prompts, raw completions, secrets, provider payloads and Restricted evidence content. Realized Business Value remains hidden until result validation. Stage 10 should define minimal auth, security and observability for this workspace.

### [2026-07-20] D059 — Phase 1 Auth, Security And Observability Slice
- Decision: add `agents/phase1/10_auth_observability_security_slice.md` as the minimum auth, security and observability contract for Phase 1.
- Rationale: the MVP needs enough trust and traceability to be credible without turning into an enterprise identity or observability platform. Local/demo JWT-compatible role claims, protected actions, safe AI context, structured logs, correlation ID, `/health`, `/ready` and minimal metrics are sufficient for the first Decision ROI Case.
- Impact: no code added. Full enterprise SSO, multiple OAuth providers, SAML, SCIM, Grafana dashboards, full OpenTelemetry deployment, SIEM integration, tenant administration, policy engine implementation and compliance reporting remain deferred. Stage 11 should define the integrated demo acceptance record using these controls.

### [2026-07-20] D060 — Phase 1 Integrated Demo Acceptance
- Decision: add `agents/phase1/11_integrated_demo_acceptance.md` as the integrated demo acceptance record for Phase 1.
- Rationale: the MVP must be accepted by proving one end-to-end Decision ROI Case, not by accumulating platform features. The demo acceptance record converts evidence, ROI, recommendation, AI explanation, human review, ledger, auth, security and observability into a single pass/fail structure.
- Impact: no code added. Future Phase 1 implementation must demonstrate authenticated access, one evidence source path, one evidence chain, one Decision ROI Case, one ROI View, one deterministic recommendation, one AI explanation, one human review action, one ledger history and minimal health/observability. Stage 12 should close the Phase 1 stage dossier while clearly distinguishing documentation readiness from implemented software.

### [2026-07-20] D061 — Phase 1 Stage Dossier Closure
- Decision: add `agents/phase1/12_phase1_closure.md` as the closure artifact for the Phase 1 documentation and control dossier.
- Rationale: all natural pre-implementation Phase 1 stages are now defined. The project needs a formal closure that says the context is ready for future limited MVP scaffolding while clearly avoiding any claim that software has already been built.
- Impact: no code added. Phase 1 is closed as context and agent-control documentation only. Future implementation may start only within the locked flow: one Decision ROI Case, one evidence chain, one ROI View, one deterministic recommendation, one AI explanation, one human review action, one append-only ledger history and minimal auth/security/observability. Remaining founder implementation choices at this point included first import format, exact demo auth mechanism, first AI provider and whether to begin with an in-memory demo store or PostgreSQL directly; these are closed by D062.

### [2026-07-20] D062 — Phase 1 Foundational Implementation Decisions
- Decision: add `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md` to close the five remaining foundational decisions before code: Canonical Evidence Model, PostgreSQL from day one, AI Provider Interface, JWT plus simple RBAC and Decision Graph as internal relationship model.
- Rationale: future agents need precise boundaries before scaffolding so GitHub, AWS, Jira and AI providers all produce one normalized evidence shape; the Decision Ledger starts persistent; OpenAI, Claude, Ollama, Azure OpenAI and Gemini remain replaceable explanation adapters; auth stays simple; and decision relationships are preserved from the beginning without adding Graph DB complexity.
- Impact: no code added. The canonical intake model is `Enterprise Evidence Event`; the first file import encoding is JSONL with one Enterprise Evidence Event per line; Phase 1 auth uses JWT-compatible login with `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE` and `AUDITOR`; AI uses an `Explanation Provider` port with conceptual `generateExplanation(preparedContext)`; PostgreSQL is the first source of truth for evidence, ledger and graph relationships; Graph DB, vector DB, Redis, MongoDB, SQLite, JSON-file store, in-memory source of truth, OAuth provider matrix and full policy engine remain out of Phase 1.

### [2026-07-20] D063 — MVP Implementation Blueprint
- Decision: add `docs/architecture/34_MVP_Implementation_Blueprint.md` as the final Phase 1 implementation blueprint and contract between architecture and development.
- Rationale: after closing product scope, stage dossier and foundational decisions, future agents need one executable specification that tells a five-person development team exactly what to build in the first six weeks without making architecture decisions independently.
- Impact: no code added. The blueprint keeps the MVP to one Decision ROI Case and corrects the implementation surface to Decision Review Workspace rather than broad Executive Dashboard. It locks the implementation sequence for evidence import, validation, PostgreSQL persistence, Decision Engine, ROI Engine, Explanation Provider, human review, append-only ledger, Business Value boundary and workspace. After this document, Phase 1 documentation is considered complete; the next named phase was Phase 2 - Technical Scaffolding, followed by Phase 3 - MVP Implementation. The Phase 2 name and scope are superseded by D064.

### [2026-07-20] D064 — Phase 2 Platform Foundation and Coding Principles
- Decision: rename the next phase from `Phase 2 - Technical Scaffolding` to `Phase 2 - Platform Foundation`, add `docs/architecture/35_Coding_Principles.md` and add `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`.
- Rationale: the next implementation phase should create the SaaS foundation that IMPERATOR will grow on, not just folders. At the same time, it must remain strictly separated from business intelligence so future agents do not implement ROI, recommendations, AI calls or connector logic before the foundation is controlled.
- Impact: no code added. Phase 2 may later create project structure, Java/Spring/Maven modular monolith foundation, Python/FastAPI explanation-provider foundation, PostgreSQL migration/schema foundation, empty REST route shells, React navigation shell, JWT/RBAC middleware, local Docker Compose, minimal observability wiring and no-deploy CI. FastAPI does not override the existing internal gRPC/Protobuf mandate for Java/Python product calls. Phase 2 must not calculate ROI, generate recommendations, integrate real AI providers, implement live connectors, execute business rules, create fake business data, use PgAdmin and Adminer together by default, duplicate `infra/` and `infrastructure/`, or treat route stubs as product behavior. Phase 3 remains the first phase that implements the `DRC-AOA-001` MVP value loop.

### [2026-07-20] D065 — MVP API Route and Recommendation Focus Cleanup
- Decision: normalize the MVP route convention to plural REST resources such as `/decisions/{id}` and ledger review commands such as `/decisions/{id}/ledger/approve`, `/reject` and `/defer`. Confirm that the MVP implements one recommendation family only: AI model downgrade/change for `DRC-AOA-001`.
- Rationale: older product/API documents and the final blueprint used slightly different route shapes and recommendation wording. Without a cleanup, future agents could create both `/decision/{id}` and `/decisions/{id}` routes, or treat unused-agent removal and underutilized AWS resources as simultaneous MVP recommendation families.
- Impact: no code added. `docs/product/API_SPECIFICATION.md`, `docs/architecture/34_MVP_Implementation_Blueprint.md` and `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md` now align around the same MVP route subset, including timeline, evidence, ROI, recommendation and ledger route shells. README, AI prompts, domain model, product blueprint, visual explainer and business summaries now state that unused AI agent removal, underutilized AWS resource detection, negative-ROI analysis and duplicated service/agent consolidation are expansion candidates after the first value loop works.

### [2026-07-20] D066 — Implementation Contract and Phase 2 Sprint Control
- Decision: add `docs/architecture/37_Implementation_Contract.md` as the mandatory implementation contract for all future AI agents and add `agents/phase2/README.md` as the controlled Phase 2 sprint plan.
- Rationale: the project has enough product and architecture context. Future work must now be governed by rules for how code is written: layer boundaries, dependency direction, package structure, naming, API behavior, database rules, events, logging, AI integration, security, tests, Git process, agent behavior and done definitions. The central control rule is that no agent may generate more than one module per iteration.
- Impact: no code added. Phase 2 remains Platform Foundation only: project shell, Java foundation, PostgreSQL foundation, API route shells, JWT/RBAC foundation, Python AI-provider foundation, React shell, Docker local foundation, observability foundation and CI/R&D evidence foundation. ROI, recommendation generation, real AI calls, live connectors, approval workflow business logic and fake business data remain forbidden until the appropriate later phase. Repository maps, AI prompts and agent READMEs now point to the Implementation Contract and Phase 2 sprint plan; Product/AI agent context remains narrowed to one MVP recommendation family first.

### [2026-07-20] D067 — Sprint 0 Contract Gate and Domain-First Phase 2 Order
- Decision: add `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` as the Sprint 0 GO/NO-GO report and update `agents/phase2/README.md` to a domain-first Phase 2 sprint order.
- Rationale: the previous Phase 2 order was too platform-oriented. IMPERATOR's core value is not CRUD infrastructure; it is the flow `Evidence -> Decision -> Recommendation -> Human Approval -> Ledger -> ROI / Business Value`. Domain and application foundations must shape PostgreSQL, API, security and UI, not the other way around.
- Impact: no code added. Sprint 0 status is `GO`, but only for Sprint 1 - Repository and Project Shell. The first code-bearing module after Sprint 1 acceptance is `backend-java/domain`. The revised Phase 2 order is Contract Gate, Repository and Project Shell, Java Domain Foundation, Application Layer Foundation, PostgreSQL Persistence Adapter Foundation, API Route Shells, JWT/RBAC Foundation, Python AI Provider Foundation, React Frontend Foundation, Docker Local Foundation, Observability Foundation and CI/R&D Evidence Foundation.

### [2026-07-20] D068 — Documentation Freeze and Sprint Green-Gate Discipline
- Decision: from Sprint 1 onward, new documents may be created only when they justify a technical decision required to implement code. Every sprint must close with an explicit green-gate table covering build/checks, architecture, critical technical debt, dead code, TODOs and documentation synchronization.
- Rationale: IMPERATOR now has enough product and architecture context. The next risk is not lack of documentation; it is accumulating code debt or scope drift while agents start implementation. Documentation must now serve implementation decisions, and sprint progress must be gated by working, reviewed increments.
- Impact: no code added. `docs/architecture/37_Implementation_Contract.md`, `agents/phase2/README.md`, `docs/architecture/38_Sprint_0_Contract_Gate_Report.md`, agent rules and AI prompts now enforce the documentation creation rule and the sprint green-gate rule. Sprint 1 remains limited to physical repository structure plus root/folder boundary files only.

### [2026-07-20] D069 — Phase 2 Specialized Sprint Agents and ASI
- Decision: Phase 2 implementation must use five separated sprint roles: Architecture Guardian, Implementation Agent, Quality Agent, Context Keeper and CTO/Product Guardian. Only the Implementation Agent may write implementation files. Architecture Guardian and CTO/Product Guardian have veto power. Every sprint must report Architectural Stability Index (ASI).
- Rationale: from Sprint 1 onward, the main risk is uncontrolled AI implementation. IMPERATOR needs one role that writes code and separate roles that protect architecture, quality, context synchronization and product focus. ASI detects whether implementation is forcing architecture churn.
- Impact: no code added. `docs/architecture/37_Implementation_Contract.md`, `agents/phase2/README.md`, `agents/README.md`, `docs/architecture/38_Sprint_0_Contract_Gate_Report.md` and AI prompts now require specialized sprint roles, one deliverable per sprint and ASI targets: Sprint 1 = 100%, Sprint 2 = 100%, Sprint 3 >= 95%, Sprint 4+ >= 95%.

### [2026-07-20] D070 — Design Freeze, Golden File Rule and Sprint Delivery Discipline
- Decision: close the design phase. From Sprint 1 onward, architecture changes are allowed only when implementation evidence demonstrates a real need. If an agent wants to create, modify, move or delete a file outside the sprint deliverable, it must stop and request authorization. No sprint may last more than one week. Every sprint must report Decision Stability and finish with the exact PASS status block.
- Rationale: IMPERATOR is ready to move from architecture design to CTO execution. The main risk is no longer missing context; it is uncontrolled helpfulness, scope expansion and architecture churn introduced by AI agents. The Golden File Rule, Decision Stability and one-week sprint limit force delivery discipline.
- Impact: no code added. `docs/architecture/37_Implementation_Contract.md`, `agents/phase2/README.md`, `docs/architecture/38_Sprint_0_Contract_Gate_Report.md`, `agents/README.md` and AI prompts now enforce design freeze, file-boundary authorization, sprint duration, Decision Stability targets and mandatory `STATUS: PASS` closure.

### [2026-07-21] D071 — Phase 2 Application Use Case Scope Clarification
- Decision: clarify that Phase 2 still forbids ROI calculation, autonomous/AI/ROI-driven recommendation engine behavior, real AI calls, live connectors and infrastructure-owned ledger mutation. Explicitly authorized application-layer use cases may create, link, review or append domain objects through ports when they are pure Java, deterministic, framework-free and bounded to the sprint.
- Rationale: implementation evidence showed that the previous phrase "no recommendation generation" was too broad and could be read as forbidding the already accepted `GenerateRecommendationUseCase` contract, even though it does not calculate ROI, rank alternatives, call AI or own the recommendation engine. The same clarification protects `AppendLedgerEntryUseCase`: ledger history may be represented through an application port, but adapters and infrastructure must not own ledger semantics.
- Impact: active control documents now distinguish "Recommendation object/application contract" from "Recommendation Engine". The next authorized step is Sprint 2.7 - PostgreSQL Persistence Adapter Foundation, with the rule that PostgreSQL adapts to the existing domain and must not force domain changes.

### [2026-07-22] D072 — Persistence Model Isolation and Domain Translation Rule
- Decision: introduce PostgreSQL persistence records as adapter-owned models and require all translation between persistence and domain to happen through adapter mappers. Persistence records must never be returned as REST/CLI/UI/SDK responses or application results.
- Rationale: Sprint 2.7 starts persistence work, which is where infrastructure can accidentally reshape the domain. The domain remains the contract; PostgreSQL adapts to it through records and mappers. A Domain Isolation Index (DII) is added from Sprint 2.7 onward to prove that domain code imports no adapters, Spring, SQL, PostgreSQL, JPA, REST, HTTP, provider SDKs or runtime infrastructure.
- Impact: `backend-java/adapters/out/postgresql/model` contains PostgreSQL persistence records only. No mappers, SQL, migrations, JPA, JDBC, Spring or repository behavior are introduced in Sprint 2.7.2. The next microtask is Sprint 2.7.3 - PostgreSQL Mappers.

### [2026-07-22] D073 — PostgreSQL Mapper Purity Rule
- Decision: PostgreSQL mappers must translate only between existing domain objects and PostgreSQL persistence records. They must not depend on repositories, ports, services, providers, HTTP, SQL, Spring or loggers.
- Rationale: mappers are the first place where persistence can start leaking into the rest of the application. Keeping one mapper per aggregate and forbidding side effects preserves the Domain Translation Rule and keeps PostgreSQL subordinate to the domain.
- Impact: Sprint 2.7.3 may create `Evidence`, `Decision`, `Recommendation` and `LedgerEntry` mappers only. No SQL, repository behavior, transaction behavior, REST DTOs, domain changes or persistence queries are authorized.

### [2026-07-22] D074 — Repository Minimalism and Ledger Append Rule
- Decision: PostgreSQL repository adapters must remain narrow persistence adapters. They may save, find, answer exact existence checks or delete only if the domain permits deletion. `LedgerRepository` is append-only and must expose `append`, not `save`.
- Rationale: repositories are the next point where business behavior can accidentally move into infrastructure. Keeping them minimal preserves hexagonal architecture, keeps ROI/recommendation/review logic in application/domain, and protects the Decision Ledger as historical truth.
- Impact: Sprint 2.7.4 must be split into `PostgresEvidenceRepository`, `PostgresDecisionRepository`, `PostgresRecommendationRepository` and `PostgresLedgerRepository` microdeliverables. Repositories must not calculate ROI, validate business rules, create business decisions, execute use cases, generate recommendations, call AI providers, publish events or construct DTOs.

### [2026-07-29] D075 — REST Error Envelope and HTTP Correlation Contract
- Decision: all application-controlled REST errors use exactly `code`, `message`, `correlationId` and non-null object `details`. The official HTTP header is `X-Correlation-ID`; one canonical UUID is normalized to lower case, while a missing, blank, invalid or multi-valued header is replaced with a generated UUID v4.
- Rationale: route shells and framework errors need one deterministic, safe contract before product controllers exist. HTTP correlation is transport trace context, not the domain `Evidence.correlationKey`, and must not introduce `ThreadLocal`, MDC, tracing or observability behavior during Sprint 2.8.1.
- Impact: Sprint 2.8.1 may create only `imperator.api.errors` and real HTTP contract tests for `400`, `404`, `405`, `500` and `501`. Error responses expose no stack trace, path, timestamp, internal exception detail or sensitive data. Security-specific `401` and `403` integration remains deferred to Sprint 2.9, and product route shells remain deferred to later Sprint 2.8 iterations under `/api/v1`.

### [2026-07-30] D076 — Implemented Java Source Root and REST Module Convention
- Decision: preserve the already accepted Maven source root `backend-java`, the Java namespace `imperator.*` and REST packages under `imperator.api.*`. Do not create a parallel `src/main/java/com/imperator` or `adapters/in` HTTP tree. Java module names use valid identifiers such as `businessvalue`, while public HTTP paths retain their documented form such as `/business-value`.
- Rationale: the Java foundation through Sprint 2.8.1 compiles and is certified with this physical structure. Recording the implemented convention resolves the obsolete future-layout examples in the frozen baseline without rewriting contracts 34-40 or changing architecture.
- Impact: operational documentation and the active Phase 2 roadmap use the implemented namespace consistently. Sprint 2.8.2 may create only `backend-java/api/evidence` under `imperator.api.evidence` and expose the authorized route shell under `/api/v1`; the canonical recommendations route requires a later `backend-java/api/recommendations` module.

### [2026-07-30] D077 — English Documentation Control Plane and Lifecycle Index
- Decision: use English as the canonical language for new current-state, sprint-control, AI-agent and implementation-facing documentation. Maintain `docs/project/` as the operational control plane for current status and phase/sprint sequencing. Preserve frozen contracts and historical records in place; legacy translations remain non-authoritative.
- Rationale: implementation progress had become distributed across chronological architecture documents, long context packs, historical prompts and README files. AI agents need a small deterministic reading path that separates current authorization from semantic contracts and audit history.
- Impact: agents must start with `docs/project/README.md`, `PROJECT_STATUS.md` and `PHASE_AND_SPRINT_MAP.md`, then load only task-specific contracts. Sprint prompt files are classified through `agents/phase2/SPRINT_ARTIFACT_INDEX.md` and never authorize execution by themselves. Contracts 34-40 remain unchanged.

### [2026-07-30] D078 — English-Only Forward Project Language
- Decision: all new or semantically modified human-readable project material must be written in English. This includes documentation, agent instructions, sprint gates, decisions, implementation README files, source-code comments, test descriptions, commit messages and change summaries. Existing frozen contracts and historical records are not mass-translated; required external names, protocol fields, database identifiers and verbatim historical quotations may retain their original form.
- Rationale: a single forward language reduces ambiguity, context duplication and translation drift for AI agents and human reviewers while preserving historical audit meaning.
- Impact: any future non-English project change must fail review unless it is an explicitly identified historical quotation or externally mandated identifier. The legacy Spanish one-page prompt remains non-authoritative and points to `docs/ai/IMPERATOR_Project_Context.md`.

### [2026-07-31] D079 — Phase 3 Vertical-Slice Acceleration
- Decision: close Phase 2 at the certified Java, domain, application, persistence, Spring Boot runtime and REST adapter foundation boundary, and authorize Phase 3 as the active lifecycle phase. Begin with Sprint 3.0 - Functional Runtime Composition, then implement the single `DRC-AOA-001` business-value loop before broader platform hardening. Planned Phase 2 Sprints 2.9 through 2.13 are deferred, not deleted or reported as complete. Their capabilities are resequenced after the first local functional business-value demonstration.
- Rationale: the repository already has a mature architecture, five application use cases, certified PostgreSQL persistence and a complete 15-route REST shell. The highest remaining risk is no longer foundation construction; it is whether imported evidence can produce one deterministic, reviewable and economically meaningful recommendation. Implementing that loop first creates earlier product learning while preserving the accepted architecture.
- Impact: Phase 3 is authorized for the locked `DRC-AOA-001` AI Onboarding Assistant Recovery slice only. The execution order is Functional Runtime Composition, JSONL Evidence Import and normalization, Deterministic Decision Creation, Recommendation and ROI Policy, Explanation Provider integration, Review and append-only Ledger with Result Validation, and an end-to-end local Business Value demo. Basic Java CI, minimum JWT/RBAC, a thin Decision Review Workspace, pilot readiness, Docker and operational hardening, and observability expansion follow in that order. Sprint 3.5 is an internal local demonstration, not full MVP acceptance. JWT/RBAC and safe secret handling remain mandatory before real customer data, an externally reachable environment or a pilot. Live connectors, additional cases, additional recommendation families, autonomous execution and schema changes remain unauthorized unless a later evidenced decision approves them.

### [2026-07-31] D080 - JSONL Evidence Import Runtime Contract
- Decision: treat JSONL as an inbound transport concern, not as a domain or persistence model. `POST /api/v1/evidence/import` consumes `application/x-ndjson`; each non-terminal line is one independent Enterprise Evidence Event and is parsed, validated, normalized, mapped to `ImportEvidenceCommand`, executed through `ImportEvidenceInputPort` and persisted as one normalized `Evidence`. Each line has its own transaction. Valid lines remain committed when other lines are rejected, and there is no batch rollback, `EvidenceBatch`, raw-event store or import-attempt table.
- Rationale: the frozen V1 schema contains exactly seven tables and persists normalized Evidence, not transport payloads. The existing application port and use case already express one evidence import at a time. Per-line partial success matches the accepted/rejected import result required by the MVP blueprint and prevents one malformed, duplicate, stale-policy, unsupported-source or restricted line from discarding independent valid evidence.
- Impact: stable line UUIDs are the equivalent idempotency identifiers. A repeated UUID is a per-line `REJECTED` result with reason `DUPLICATE`, never a request-wide `409` when other lines can be processed. Per-line validation and business rejections are returned in a successful JSON import result. HTTP errors are reserved for request-wide failures such as unsupported media type, empty or invalidly encoded body, exceeded request limits, unavailable runtime infrastructure or database failure, and they retain the four-field REST error envelope and `X-Correlation-ID`. Only normalized Evidence is persisted; JSONL content disappears after processing. No Domain, V1, repository-contract, Decision, Recommendation, ROI, Review or Ledger change is authorized by this decision.

### [2026-08-01] D081 - Deterministic Decision Creation Contract
- Decision: add `docs/architecture/42_Deterministic_Decision_Creation_Contract.md` as the frozen functional contract for Sprint 3.2. Decision creation is an explicit Application operation through `CreateDecisionInputPort`: one eligible, persisted `business_context_requested` Evidence for `DRC-AOA-001` originates one Decision in the existing `CREATED` state. The same eligible originating Evidence, correlation key and creation inputs must preserve the same Decision identity and immutable creation state regardless of timing, retries or equivalent concurrent execution.
- Rationale: `CreateDecisionUseCase` currently constructs a new `CREATED` aggregate for every invocation, while `DecisionRepository.save()` is implemented in PostgreSQL as a full upsert. That combination does not guarantee creation idempotency and could overwrite immutable inputs, later evidence relationships, Recommendation or review state when creation is replayed. The functional outcomes for identical retries, conflicting retries and concurrent creation must therefore be frozen before Sprint 3.2 changes code.
- Impact: Sprint 3.2 must preserve one stable application-supplied UUID v4, derive `caseId` from the originating Evidence correlation key, keep the existing creation tuple immutable and prevent replay from resetting a progressed Decision. D081 does not choose or authorize a repository-port extension, optimistic locking, a new SQL constraint, a schema migration or any other technical mechanism. Sprint 3.2 must review the smallest correct mechanism and request explicit approval before changing a port, schema, migration or frozen contract. No Domain, REST, Recommendation, ROI, Explanation Provider, Review or Ledger behavior is authorized by this decision.

### [2026-08-01] D082 - Deterministic Recommendation And ROI Contract
- Decision: add `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md` as the frozen functional contract for Sprint 3.3. The only authorized policy is `DRC-AOA-001-v1`: a ready `DRC-AOA-001` Decision and its accepted canonical Evidence produce exactly one `MODEL_CHANGE` Recommendation with a fixed lower-cost-model action and fallback, deterministic ROI, annualized `Recommendation.estimatedSavings`, evidence-based confidence and deterministic risk. Policy outputs belong to Domain policy, Application orchestrates them, and transport, persistence and AI have no decision authority.
- Rationale: the current Recommendation model stores one monetary estimate without an explicit period, while the current generation command accepts action, reason, savings, confidence and risk from its caller and the use case invokes `ExplanationProvider`. Without a frozen contract, the caller could become the source of commercial truth, monthly and annual savings could be confused, retries could conflict with the one-Recommendation-per-Decision invariant, and AI explanation could leak into deterministic generation.
- Impact: Sprint 3.3 must use EUR, scale two and `HALF_EVEN`; calculate `2340.00 - 720.00 - 0.00 = 1620.00` monthly recovery and `1620.00 * 12 = 19440.00` annualized recovery; persist `EUR 19440.00` as annualized estimated savings; produce `92/LOW` for the complete pack or `90/MEDIUM` only when quality Evidence is missing; and create no Recommendation when another mandatory input is absent or invalid. Assumptions and policy version are accepted linked Evidence, with `policy_version=DRC-AOA-001-v1`; no table, column or migration is added. D082 freezes idempotent and concurrent outcomes but deliberately defers the repository mechanism. Sprint 3.3 must not call or depend on `ExplanationProvider`; AI explanation remains exclusively Sprint 3.3.1.

### [2026-08-01] D083 - Atomic Human Review, Monotonic Ledger And Result Validation Contract
- Decision: freeze the Sprint 3.4 human-governance lifecycle for `DRC-AOA-001`. Approve, reject and defer must persist the Decision transition and matching immutable Ledger entry in one atomic transaction. D083 supersedes only the transactional-separation requirement in Document 39 for those MVP review operations; every other Document 39 boundary remains intact. Ledger history begins with the first real human governance outcome, never with a synthetic `recommendation_created` entry, and forms one immutable, strictly monotonic per-Decision chain through `previous_entry_id`.
- Rationale: an auditable system cannot expose a reviewed Decision without its audit fact, the inverse state, or a branched or ambiguous history. Freezing authority, transitions, snapshots, logical ordering, replay and concurrency before implementation prevents split truth and prevents transport, persistence or AI from acquiring governance authority.
- Impact: the Decision state inventory remains `CREATED`, `UNDER_REVIEW`, `APPROVED`, `REJECTED` and `DEFERRED`. `implementation_marked` and `result_validated` are Ledger facts and leave the Decision `APPROVED`. Phase 1 authority is limited to `ADMIN` for approve/reject, `ADMIN`, `PLATFORM_ENGINEER` or `FINANCE` for defer, `PLATFORM_ENGINEER` for implementation and `FINANCE` for validation; `AUDITOR` remains read-only. Identical UUID-and-payload replay resolves the existing outcome, conflicting replay fails, contradictory concurrency has one atomic winner, and each new entry must name the immediate current head with a strictly later occurrence time. Result validation requires post-action Evidence and deterministically calculates annualized EUR realized recovery from annualized baseline cost, annualized post-action cost and actual transition cost. No Java, SQL, Flyway, REST, schema, control-document or Explanation Provider change is authorized by D083; the exact atomic implementation mechanism remains deferred to the Sprint 3.4 pre-implementation review.
