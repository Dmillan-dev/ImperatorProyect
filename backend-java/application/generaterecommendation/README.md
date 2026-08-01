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
- No `ExplanationProvider` is injected or invoked.

## Never Contains

- OpenAI, Claude, Ollama, Gemini or Azure OpenAI clients.
- Prompt templates with raw evidence.
- REST controllers.
- Database adapters.
- SQL.
- Human review actions.
- Ledger append orchestration.
- Caller-owned Recommendation or ROI outputs.
- AI explanation or provider calls.

