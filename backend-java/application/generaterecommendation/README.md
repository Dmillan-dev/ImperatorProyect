# generaterecommendation

## Purpose

Generate a traceable recommendation for an existing decision.

## Who Uses This Folder

- Future scheduler or system-triggered application flows.
- Architecture Guardian to verify AI remains explanatory.
- Quality Agent to protect use-case purity.

## Contains

- `GenerateRecommendationUseCase`.
- `GenerateRecommendationCommand`.
- `GenerateRecommendationResult`.

Sprint 2.5.3 status:
- One system-initiated use case only.
- Reads an existing decision.
- Verifies referenced evidence exists and belongs to the same case.
- Calls `ExplanationProvider` through a port only.
- Saves `Recommendation` through `RecommendationRepository`.
- Links the recommendation back to the decision through `DecisionRepository`.

## Never Contains

- OpenAI, Claude, Ollama, Gemini or Azure OpenAI clients.
- Prompt templates with raw evidence.
- REST controllers.
- Database adapters.
- SQL.
- Human review actions.
- Ledger append orchestration.
- ROI calculation.

