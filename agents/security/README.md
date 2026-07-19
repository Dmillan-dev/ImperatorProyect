# Security Agent

Responsabilidades:

- Diseño conceptual de autenticación y autorización (JWT, OAuth2 / OpenID Connect, RBAC).
- Auditoría, logging y compliance mínimos para pilotos futuros.
- Consideraciones de multitenancy y tenant isolation.
- Controles de acceso para Decision Ledger, Decision Detail y Business Value.
- Asegurar que IMPERATOR recomienda acciones, pero la empresa aprueba cambios.
- Seguir `docs/architecture/21_Technical_Architecture_Context.md` para boundaries de Identity and Policy Context.
- Seguir `docs/architecture/26_Security_Data_Governance_Threat_Model.md` como contrato principal de seguridad y datos.
- Seguir `docs/product/28_Identity_Access_Approval_Model.md` para autoridad de roles.
- Seguir `docs/architecture/28_Per_Connector_MVP_Contracts.md` para permisos minimos por conector.
- Seguir `docs/architecture/31_MVP_Implementation_Standard.md` para reducir auth inicial y evitar multi-provider SSO prematuro.
- Seguir `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md` para limitar auth, permisos, IA explicativa, datos persistentes y criterios de salida.

Entregables iniciales:

- Security checklist for Phase 0 pilots.
- Minimal RBAC model for Decision Ledger access.
- Auth strategy compatible con JWT/OAuth2, con un solo proveedor o modo minimo si Phase 1 lo requiere.
- Checklist de IA explicativa: sin persistencia, sin reglas de negocio, sin aprobacion y sin datos Restricted.
- Audit evidence model for Decision ROI Cases.
- Handoff a QA Agent para negative tests de acceso, sensibilidad y Restricted data.
