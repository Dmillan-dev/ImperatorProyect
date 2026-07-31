# Phase 2 Sprint Artifact Index

Status: **Active index**

This file classifies sprint prompt artifacts. A prompt file is evidence of a
planned or completed sprint; it is never authorization to execute that sprint.
Current authorization lives in `docs/project/PROJECT_STATUS.md`.

## Current Gate

```text
Sprint 2.8.6 - Ledger Route Shell
```

No dedicated execution prompt has been created for this gate.

## Completed Prompt Artifacts

| Sprint | Artifact | Classification |
|---|---|---|
| 2.7.6.0 | `SPRINT_2.7.6.0_BUILD_FOUNDATION_PROMPT.md` | Historical completed prompt |
| 2.7.6.1 | `SPRINT_2.7.6.1_PERSISTENCE_CONTRACT_PROMPT.md` | Historical completed prompt |
| 2.7.6.2 | `SPRINT_2.7.6.2_FLYWAY_V1_PROMPT.md` | Historical completed prompt |
| 2.7.7 | `SPRINT_2.7.7_POSTGRESQL_INTEGRATION_PROMPT.md` | Historical completed prompt |

Sprints 2.8.0 through 2.8.5 were executed and accepted without adding
dedicated prompt files to this directory. Their governing decisions and current
state are recorded in:

- `docs/decisions/14_Decision_Log.md`;
- `docs/project/PROJECT_STATUS.md`;
- `docs/project/PHASE_AND_SPRINT_MAP.md`.

## Agent Rule

Before using any prompt:

1. confirm the current gate;
2. confirm the prompt matches that gate exactly;
3. read the relevant frozen contracts;
4. inspect current code;
5. reject historical scope as current authorization;
6. stop if the prompt would touch more than one module.

Completed prompts must not be edited to describe newer implementation state.
Use current-control documents for progress and preserve prompt artifacts for
audit history.
