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
- Impact: no strategic scope change. Updated product, business and investor-audit wording so AI model optimization, unused AI agent removal and underutilized AWS resource detection remain the MVP paid-wedge focus; negative-ROI feature analysis and duplicate service/agent consolidation remain deferred.

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
