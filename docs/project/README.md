# IMPERATOR Documentation Control Plane

Status: **Active**

This directory is the operational entry point for humans and AI agents. It
does not replace product or architecture contracts. It tells a reader which
phase is active, which sprint is authorized, which documents are authoritative,
and how much context must be loaded before acting.

## Start Here

Read these files in order:

1. [Project Status](PROJECT_STATUS.md)
2. [Phase and Sprint Map](PHASE_AND_SPRINT_MAP.md)
3. [Decision Log](../decisions/14_Decision_Log.md)
4. [Phase 3 Sprint Plan](../../agents/phase3/README.md)
5. The contracts required by the current sprint

For a compact project description, use
[IMPERATOR Project Context](../ai/IMPERATOR_Project_Context.md).

## Document Classes

| Class | Purpose | Mutation rule |
|---|---|---|
| Current control | Current phase, sprint and next gate | Update at every formal sprint closure |
| Accepted decision | Chronological record of approved choices | Append only; corrections require explicit approval |
| Frozen contract | Product, architecture or persistence authority | Do not edit without an evidenced contradiction and approval |
| Active execution plan | Current sprint sequence and agent boundaries | Update only when execution state or authorization changes |
| Historical record | Audit, closure, completed prompt or superseded plan | Preserve; do not use as the current gate |
| Reference | Supporting domain, API, security or quality context | Update only when its owning decision changes |
| Template | Reusable research or R&D format | Change independently when no contract semantics change |

## Authority Model

When two documents conflict, use this precedence:

1. Explicit founder authorization for the current task.
2. `docs/project/PROJECT_STATUS.md` for the current gate only.
3. `docs/decisions/14_Decision_Log.md` for accepted decisions.
4. Frozen product and architecture contracts for semantics.
5. the active phase plan for execution sequencing.
6. Supporting references.
7. Historical documents and prompts.

Current-state documents never override domain or architecture semantics. They
only identify which approved work may execute next.

## Canonical Language

English is the mandatory language for all new or modified human-readable
project material, including:

- current project control documents;
- AI-agent instructions;
- sprint scopes and gates;
- implementation-facing README files;
- architecture, product and technical decisions;
- source-code comments and test descriptions;
- commit messages and change summaries.

Legacy documents may retain their original language when translating them could
alter historical meaning. A translation is non-authoritative unless a decision
explicitly promotes it. External protocol names, database identifiers,
third-party terms and verbatim historical quotations are not translations and
may retain their required form.

Use the canonical terminology in
`docs/product/13_Glossary_and_Canonical_Language.md`.

## AI Context Loading Rule

Agents should load the smallest sufficient context.

Always load:

1. this control-plane README;
2. `PROJECT_STATUS.md`;
3. `PHASE_AND_SPRINT_MAP.md`;
4. the relevant Decision Log entries;
5. the current sprint section from the active phase plan.

Then load only the contracts required by the task:

| Task type | Additional required context |
|---|---|
| Domain | Core Domain Model, Coding Principles, Implementation Contract |
| Application | Domain contracts, application boundary policy, transaction contract |
| Persistence | Persistence contracts 39 and 40, V1 migration, repository ports |
| REST | API Specification, documents 34-37, D075 and D076 |
| Security | Security threat model, identity/approval model, documents 35-37 |
| Frontend | Screen contract, API Specification, identity/approval model |
| Phase 3 behavior | D079, Phase 3 Sprint Plan, MVP Blueprint, Vertical Slice and ROI Slice |

Do not load every project document by default. More context is not automatically
better context.

## Update Discipline

At sprint closure:

1. update `PROJECT_STATUS.md`;
2. update the affected row in `PHASE_AND_SPRINT_MAP.md`;
3. append to the Decision Log only if a decision was made;
4. update the active sprint plan if the next gate changed;
5. update entry-point README files;
6. preserve completed prompts and gate reports as historical records;
7. verify links, repository cleanliness and the relevant build/test gate.

Do not rewrite frozen contracts merely to report progress.
