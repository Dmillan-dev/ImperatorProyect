# scripts

## Purpose

Auxiliary repository automation when a verified need exists.

## Who Uses This Folder

- Implementation Agent only when a sprint authorizes automation.
- Quality Agent to review scripts for safety and repeatability.
- Context Keeper to document supported commands once they exist.

## Contains

- `verify-docker-runtime.ps1`, the local D092 runtime verification helper.
- `commercial/capture_linkedin_workspace.mjs`, which captures the synthetic
  commercial workspace.
- `commercial/generate_discovery_interview_guide.py`, which generates the
  interview guide artifact.
- `commercial/generate_linkedin_discovery_kit.py`, which generates the
  commercial discovery kit.
- Future validation or evidence helpers only when explicitly authorized.

Current status:
- Executable local verification and commercial artifact helpers exist.
- There is no general environment setup automation.
- There are no CI helper scripts.

## Never Contains

- Business logic.
- Hidden implementation shortcuts.
- Secrets or credentials.
- Destructive commands without explicit founder approval.
- Scripts that bypass tests, security or architecture gates.

Generated caches such as `__pycache__/` are ignored and are not source or
evidence. New scripts must support a concrete implemented module, verification
workflow or approved portfolio artifact.
