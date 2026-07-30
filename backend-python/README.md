# backend-python

## Purpose

Auxiliary AI explanation-provider boundary, decoupled from the Java domain.

## Who Uses This Folder

- Implementation Agent during the Python AI provider foundation sprint.
- Architecture Guardian to verify the AI Provider Interface boundary.
- Quality Agent to review provider abstractions once authorized.

## Contains

- Future Python service shell.
- Future explanation provider interfaces.
- Future provider adapters when explicitly authorized.
- Future request and response models for natural-language explanations.

Current status:
- Documentation boundary only.
- No Python source files.
- No FastAPI application.
- No dependencies or provider implementations.
- No calls to OpenAI, Claude, Ollama, Gemini or Azure OpenAI.

## Never Contains

- Java domain logic.
- Decision rules.
- ROI calculations.
- Persistent data writes.
- Direct modification of Ledger or Evidence Store.
- Real provider credentials.

## Authorized Next Use

A later sprint may define provider interfaces without real external AI calls.
