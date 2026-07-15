# Phase 0 Guidelines — No code, documentation and structure only

Scope: Phase 0 is focused on validation, positioning and documentation. No production code or connectors should be implemented in this repository during Phase 0. The repo may contain:

- Documentation: RFCs, ADRs, architectural thesis, agent guides.
- Schemas/contracts: canonical `.proto` files as source of truth for future implementation.
- Scaffolding: README templates, folder structure and examples of CI (workflows) for governance; these are documentation artifacts, not production services.
- Architecture context documents such as `docs/architecture/18_Architecture_Thesis.md` and `docs/architecture/21_Technical_Architecture_Context.md`; these define target responsibilities and guardrails, not runnable implementation.

Rules:

- Do not add runnable service implementations or production configuration under `services/` or `infra/` in Phase 0. Placeholders and README scaffolds are allowed.
- Protobufs are accepted as design artifacts. Do not generate or commit compiled binaries/artifacts for production (e.g., Java .class, compiled python packages) in Phase 0.
- Any change that would introduce executable code must be approved and recorded in `docs/decisions/14_Decision_Log.md`.
- Any architecture change that alters MVP integration boundaries, target layers or communication contracts must update `docs/architecture/21_Technical_Architecture_Context.md` and be recorded in `docs/decisions/14_Decision_Log.md`.

Purpose: keep the repository lightweight, focused on alignment, and avoid premature technical debt or commitments before market validation.
