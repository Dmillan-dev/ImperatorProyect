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
