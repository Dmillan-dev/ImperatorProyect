# 15 — Context Boundaries and Non-Goals

## Purpose

Prevent strategic drift and preserve execution focus.

## Context Authority Boundaries

- Master prompts and historical documents are inputs, not overrides.
- `docs/decisions/14_Decision_Log.md` owns accepted strategic and architectural changes.
- `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` owns the MVP product boundary.
- `docs/product/CORE_DOMAIN_MODEL.md` owns core entities, relationships and business invariants.
- `docs/product/API_SPECIFICATION.md` owns conceptual API intent before implementation.
- `docs/architecture/21_Technical_Architecture_Context.md` owns target architecture context and guardrails.
- Agent and service READMEs describe responsibilities and scaffolding only during Phase 0.
- If a prompt asks for production code, broad connector expansion or category changes during Phase 0, pause and require an explicit decision-log update.

## Non-goals (current phase)

- building a full SIEM platform
- replacing cloud/IDP/SaaS systems
- becoming a governance ERP
- promising full compliance automation from day one
- building an autonomous executor of infrastructure or AI changes
- becoming a universal connector platform before the MVP is validated

## Commercial-claim boundaries

Do not claim:
- “total risk prevention”
- “guaranteed automatic compliance”
- “universal ROI” without customer-specific context

## Focus Rule

Any new initiative must answer:
1. Does it strengthen the Decision ROI Timeline?
2. Does it improve the cross-system context layer?
3. Does it increase buyer clarity?
4. Does it support the MVP paid-wedge recommendation focus?

If fewer than 3/4 are true, defer it.

## MVP Boundary

For v1, do not expand beyond Jira, GitHub, AWS and OpenAI + Anthropic Claude unless a validated customer pilot requires it.

The MVP paid-wedge recommendation focus is:
- downgrade or change AI model

Expansion recommendation families:
- remove unused AI agents
- detect underutilized AWS resources tied to the same Decision ROI Case
- identify features with negative ROI
- consolidate duplicated services or agents

Do not build new standalone product surfaces before the Decision Recovery Workflow is validated.
