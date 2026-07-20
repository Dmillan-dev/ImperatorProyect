# Phase 1 - Stage 07: AI Explanation Slice

## Purpose

Define how AI is used in Phase 1 to explain the deterministic recommendation for `DRC-AOA-001`.

AI is a language layer.

AI is not the Decision Engine.

AI is not the ROI Engine.

AI is not an approver.

This stage does not define code, provider SDK usage, model selection, prompt templates for production, secrets, embeddings, vector search or autonomous agents.

## Authority Inputs

This stage is governed by:

1. `agents/phase1/02_acceptance_contract.md`
2. `agents/phase1/05_evidence_intake_slice.md`
3. `agents/phase1/06_domain_roi_recommendation_slice.md`
4. `docs/ai/12_AI_Agent_Context_Pack.md`
5. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
6. `docs/architecture/27_Quality_Attributes.md`
7. `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md`
8. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
9. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
10. `docs/architecture/33_Phase_1_Foundational_Implementation_Decisions.md`

If there is conflict, the security model and Phase 1 scope contract win.

## Stage Decision

Phase 1 may use an LLM only to generate a human-readable explanation of an already-created recommendation.

The explanation must be based on prepared context.

The explanation must cite evidence IDs and assumptions.

The explanation must not introduce new business facts.

The implementation boundary is:

```text
Explanation Provider
```

Conceptual method:

```text
generateExplanation(preparedContext)
```

The Decision Engine must not depend on OpenAI, Anthropic Claude, Ollama, Azure OpenAI, Google Gemini or any specific provider.

## Explanation Position In The Flow

The AI explanation happens here:

```text
Evidence
-> Deterministic ROI View
-> Deterministic Recommendation
-> Prepared Explanation Context
-> AI Explanation
-> Human Review
```

Any flow that asks AI to choose the recommendation is invalid for Phase 1.

## Prepared Context Contract

The prepared context may include:

| Field | Allowed |
| --- | --- |
| `case_id` | `DRC-AOA-001` |
| `case_title` | AI Onboarding Assistant Recovery |
| `review_period` | 2026-06 |
| `review_date` | 2026-07-18 |
| `business_context_summary` | Normalized non-sensitive summary only. |
| `evidence_ids` | Stable evidence IDs used by the case. |
| `evidence_summaries` | Short normalized facts, not raw payloads. |
| `roi_view` | Current monthly cost, projected cost, estimated recovery and assumptions. |
| `recommendation` | The already-created deterministic recommendation. |
| `confidence` | Existing deterministic confidence label or score. |
| `risk` | Existing deterministic risk label. |
| `human_action_needed` | Approve, reject or defer for evidence. |

The prepared context must be built from accepted evidence summaries and deterministic ROI output.

## Forbidden AI Context

The AI must not receive:

- raw prompts,
- raw completions,
- secrets,
- API keys,
- OAuth tokens,
- customer conversations,
- full provider payloads,
- unrestricted stack traces,
- unredacted personal data,
- Restricted evidence,
- hidden financial assumptions,
- source data whose sensitivity is unknown.

If forbidden context is required to produce an explanation, the explanation must be blocked and the security issue recorded.

## Explanation Output Contract

The AI explanation should produce concise natural language with these sections:

| Section | Required Meaning |
| --- | --- |
| Decision summary | What is being recommended. |
| Business reason | Why the decision matters operationally. |
| Financial impact | Current cost, projected cost and estimated recovery. |
| Evidence cited | Evidence IDs that support the explanation. |
| Assumptions | Explicit assumptions used by ROI or risk. |
| Risk note | Low, Medium or Blocked as already determined. |
| Human next step | What the authorized reviewer should do next. |

The explanation must not hide assumptions.

The explanation must not claim realized savings.

## Required Explanation For The Locked Case

For `DRC-AOA-001`, the explanation should help a reviewer understand:

- AI spend is the primary recovery target;
- current monthly cost is EUR2,340;
- projected monthly cost after action is EUR720;
- estimated monthly recovery is EUR1,620;
- annualized recovery is EUR19,440;
- quality/fallback evidence affects risk;
- approval is a human decision;
- realized saving remains unavailable until result validation.

## AI Authority Rules

AI may:

- rewrite deterministic output into clearer language,
- cite provided evidence IDs,
- summarize assumptions already present,
- explain why a human review action is needed,
- make the recommendation easier to understand.

AI must not:

- calculate ROI,
- modify ROI,
- choose recommendation,
- generate alternate recommendations,
- rank actions,
- create evidence,
- change confidence,
- change risk,
- approve,
- reject,
- defer,
- mark implementation,
- validate result,
- write ledger entries,
- mutate persistent state,
- mutate provider systems.

## Failure Behavior

If AI explanation succeeds:

- the recommendation remains unchanged;
- the explanation is attached as explanatory text;
- evidence IDs and assumptions must remain visible.

If AI explanation fails:

- the deterministic recommendation remains valid;
- ROI remains valid;
- the case may show an explanation-unavailable state;
- logs should record explanation failure without sensitive content;
- no recommendation, ROI, ledger or approval state may be changed by the failure.

For the Phase 1 acceptance demo, at least one successful AI explanation should be shown.

## Provider Boundary

Phase 1 does not lock a permanent AI provider.

OpenAI and Anthropic Claude remain valid provider directions, but provider choice is an adapter concern.

Other future adapters may include Ollama, Azure OpenAI and Google Gemini.

The domain must not depend on provider-specific response formats.

The MVP must not require:

- model training,
- fine-tuning,
- embeddings,
- vector database,
- autonomous planning,
- multi-agent reasoning,
- prompt-chain orchestration,
- provider-specific business logic.

## Observability Requirements

Minimum AI explanation observability:

- explanation requested,
- explanation succeeded,
- explanation failed,
- provider unavailable,
- prepared context rejected by sensitivity rule,
- correlation ID,
- case ID.

Logs must not contain raw prompts, raw completions, secrets or Restricted evidence.

## Acceptance Mapping

This stage supports:

- `AC-07`: AI-generated explanation exists for the deterministic recommendation;
- `AC-11`: explanation cites evidence and assumptions;
- `AC-13`: AI explanation success/failure is minimally observable;
- `AC-14`: AI context excludes sensitive forbidden data.

It also supports negative tests rejecting:

- AI decisioning,
- AI-calculated ROI,
- raw prompt/completion storage,
- Restricted evidence exposure,
- model training,
- vector search,
- autonomous execution.

## Stage Pass Criteria

Stage 07 is complete when:

1. AI is positioned after deterministic recommendation creation;
2. prepared context fields are defined;
3. forbidden context is explicit;
4. explanation output structure is defined;
5. AI authority is limited to language generation;
6. AI failure cannot mutate business state;
7. the Phase 1 demo can show one successful explanation with evidence IDs.

## Stop Conditions

Stop and return to CTO Agent if any proposal requires:

- AI to calculate ROI,
- AI to choose the recommendation,
- AI to rank recommendations,
- AI to read raw prompts or completions,
- AI to persist business data,
- AI to mutate provider systems,
- AI to approve or reject,
- embeddings or vector stores,
- model training or fine-tuning,
- broad AI-agent automation.

## Handoff To Stage 08

Stage 08 may record that an explanation was generated, unavailable or rejected by sensitivity rules.

Stage 08 must not treat AI output as the source of approval authority or financial truth.
