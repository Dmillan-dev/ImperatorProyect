# Estructura del repositorio (sugerida)

- `docs/` — RFCs, ADRs, documentación de producto y arquitectura.
  - `docs/rfc/` — propuestas y RFCs.
  - `adr/` — Architectural Decision Records.
- `proto/` — archivos `.proto` canónicos (Decision.proto).
- `agents/` — agentes lógicos responsables de áreas (CTO, Backend, AI, Frontend, Product, Security).
- `services/` — implementaciones por stack (backend, ai, frontend).
- `infra/` — infra-as-code, despliegue y scripts.

Propósito: permitir que 'agentes' trabajen de forma separada en sus áreas, manteniendo contratos claros (`proto/`) y ADRs para decisiones técnicas.

Documento canónico del MVP:

- `20_MVP_Decision_ROI_Platform_Blueprint.md` — blueprint de la Decision ROI Platform: integraciones MVP, objeto central, recomendaciones prioritarias, superficies de producto y criterios de validación.
