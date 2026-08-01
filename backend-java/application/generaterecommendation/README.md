# generaterecommendation

## Purpose

Orchestrate deterministic Recommendation creation for the frozen
`DRC-AOA-001-v1` policy.

## Who Uses This Folder

- Future authorized initiating adapter or system-triggered flow.
- Architecture Guardian to verify Application orchestrates but does not own
  Recommendation policy.
- Quality Agent to protect use-case purity.

## Contains

- `GenerateRecommendationUseCase`.
- `GenerateRecommendationCommand`.
- `GenerateRecommendationResult`.

Sprint 3.3 behavior:
- The command supplies only stable Recommendation identity, Decision identity,
  Evidence identities and generation timestamp.
- The use case executes one explicit transaction.
- It loads the authoritative Decision and all selected Evidence.
- The Domain policy derives type, action, reason, annualized estimated savings,
  confidence and risk.
- `RecommendationRepository.createIfAbsent` resolves retries and concurrency.
- Immutable tuple comparison remains Application behavior.
- First creation attaches the Recommendation to a `CREATED` Decision.
- Identical replay returns the stored Recommendation without modifying later
  Decision state.

Sprint 3.3.1 behavior:
- The deterministic transaction completes before `ExplanationProvider` is
  invoked.
- The provider receives a prepared immutable context built from the persisted
  Recommendation, its Decision, Evidence ids, assumption ids and policy
  version.
- Provider output contributes optional natural-language explanation text only.
- Provider absence or failure returns no explanation and cannot roll back or
  alter the persisted Recommendation or Decision.
- Provider choice remains an outbound-adapter concern.

## Never Contains

- OpenAI, Claude, Ollama, Gemini or Azure OpenAI clients.
- Prompt templates with raw evidence.
- REST controllers.
- Database adapters.
- SQL.
- Human review actions.
- Ledger append orchestration.
- Caller-owned Recommendation or ROI outputs.
- Provider-specific SDKs, credentials or model selection.
- AI-owned Recommendation, ROI, confidence, risk or action decisions.

