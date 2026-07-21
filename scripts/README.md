# scripts

## Purpose

Automatizacion auxiliar del repositorio cuando exista una necesidad verificable.

## Who Uses This Folder

- Implementation Agent only when a sprint authorizes automation.
- Quality Agent to review scripts for safety and repeatability.
- Context Keeper to document supported commands once they exist.

## Contains

- Future local helper scripts.
- Future validation or evidence capture scripts tied to implemented modules.
- Future CI helper scripts when explicitly authorized.

Sprint 1 status:
- Repository shell only.
- No executable scripts.
- No setup automation.
- No CI helper scripts.

## Never Contains

- Business logic.
- Hidden implementation shortcuts.
- Secrets or credentials.
- Destructive commands without explicit founder approval.
- Scripts that bypass tests, security or architecture gates.

## Authorized Next Use

A later sprint may add scripts only when they support a concrete implemented
module or verification workflow.
