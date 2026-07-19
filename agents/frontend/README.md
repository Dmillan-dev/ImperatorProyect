# Frontend Agent

Responsabilidades:

- Diseño conceptual del Decision Review Workspace como MVP; Executive Workspace, standalone Ledger, Business Value e Integrations quedan como expansión.
- UX para priorización ejecutiva, revisión de evidencia, ledger inmutable y prueba de valor económico.
- Diseño de integración futura con APIs gRPC/HTTP (adaptadores si es necesario).
- Mantener frontend sin lógica de dominio; la UI consume Decision ROI Cases y evidencias preparadas.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md`.
- Seguir `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` como contrato principal de pantalla.
- Seguir `docs/architecture/27_Quality_Attributes.md` para expectativas de usabilidad, latencia, degradación y blockers.
- Seguir `docs/architecture/31_MVP_Implementation_Standard.md` para mantener el primer frontend limitado al Decision Review Workspace.
- Seguir `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`; la primera pantalla debe demostrar un Decision ROI Case, una recomendacion y los criterios de salida Phase 1.

Entregables iniciales:

- `services/frontend/` scaffold documental con design tokens y component library plan.
- Low-fidelity mockup for the Decision Review Workspace first; broader surfaces only after the workflow is validated.
- Handoff a QA Agent para empty states, blockers y acciones role-aware.
