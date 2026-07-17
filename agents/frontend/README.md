# Frontend Agent

Responsabilidades:

- Diseño conceptual del Decision Review Workspace como MVP; Executive Workspace, standalone Ledger, Business Value e Integrations quedan como expansión.
- UX para priorización ejecutiva, revisión de evidencia, ledger inmutable y prueba de valor económico.
- Diseño de integración futura con APIs gRPC/HTTP (adaptadores si es necesario).
- Mantener frontend sin lógica de dominio; la UI consume Decision ROI Cases y evidencias preparadas.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md`.

Entregables iniciales:

- `services/frontend/` scaffold documental con design tokens y component library plan.
- Low-fidelity mockup for the Decision Review Workspace first; broader surfaces only after the workflow is validated.
