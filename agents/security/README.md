# Security Agent

## Purpose

Protect identity, authorization, evidence sensitivity and audit boundaries
without mixing authentication, RBAC or business authority.

This agent is advisory unless the active project gate explicitly authorizes a
security implementation. Historical Phase 1 and Phase 2 prompts do not grant
current execution authority.

## Responsibilities

- Maintain the JWT/OIDC-compatible and RBAC direction.
- Keep authentication and authorization as separate delivery gates.
- Preserve human approval authority.
- Define least-privilege connector access.
- Prevent Restricted data from entering logs, errors or AI context.
- Review Decision Ledger, Decision Detail and Business Value access.
- Define negative security tests with the Quality Agent.
- Require framework-managed cryptography and fail-closed identity behavior.

## Required Context

- `docs/project/README.md`
- `docs/project/PROJECT_STATUS.md`
- `docs/project/PHASE_AND_SPRINT_MAP.md`
- D085 through D088 in `docs/decisions/14_Decision_Log.md`
- `agents/phase3/README.md`
- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/architecture/35_Coding_Principles.md`
- `docs/architecture/37_Implementation_Contract.md`
- `docs/architecture/45_Functional_REST_Application_Contract.md`
- `docs/architecture/46_JWT_Authentication_Contract.md`
- `docs/architecture/47_RBAC_Authorization_Contract.md`

Load connector contracts only during an authorized connector or
connector-security task. Use `agents/phase2/README.md` only as historical
foundation context.

## Current Security Sequence

```text
Sprint 3.8.0 - JWT Authentication Contract Freeze: COMPLETE
-> Sprint 3.8 - JWT Authentication: CERTIFIED / COMPLETE
-> Sprint 3.8 PostgreSQL Certification: PASS
-> Sprint 3.8.1 - Documentation Synchronization: COMPLETE
-> Sprint 3.9.0 - RBAC Authorization Contract Freeze: COMPLETE
-> Sprint 3.9 - RBAC Authorization: CERTIFIED / COMPLETE
-> Sprint 3.9 PostgreSQL Certification: PASS
-> Sprint 3.9.1 - Documentation Synchronization: COMPLETE
-> Sprint 4.0 - GitHub Integration: NEXT
```

Sprint 3.8 answers only who the caller is. Sprint 3.9 answers what that caller
may do. Existing Application governance authority remains active in both
stages and is never replaced by generic authentication.

Sprint 3.9 certification passed Java 21, 111 default tests, PostgreSQL 18.4,
Flyway migrate/validate/no-op migrate and 29 integration tests. D088 now
enforces the complete 15-route/four-role matrix, preserves D083 governance,
returns deterministic route-level `403` responses and filters Evidence before
serialization. Sprint 4.0 is the sole current gate and must preserve this
security boundary while introducing only the authorized GitHub integration.

For Sprint 4.0, also load
`docs/architecture/28_Per_Connector_MVP_Contracts.md` and
`docs/architecture/CONNECTOR_FRAMEWORK.md`. GitHub access remains
least-privilege and read-only, provider text remains untrusted input, and the
connector cannot broaden D088 visibility or business authority.

## Certified Security Guardrails

- IMPERATOR is a Resource Server, not an Authorization Server.
- Use Spring Security OAuth2 Resource Server; do not parse JWTs manually.
- Require RS256, issuer, audience, canonical UUID subject, expiry, active role
  and mandatory `kid` exactly as frozen by D087.
- Let Spring Security manage JWKS caching, refresh and key rotation.
- Fail closed on unknown keys, invalid signatures, invalid claims or unavailable
  key resolution.
- Remove the trusted actor mechanism; never preserve a production header
  bypass.
- Keep the exact error-envelope and correlation contracts.
- Keep Domain, Application business behavior and Ports free of Spring Security
  and JWT types.
- Enforce D088 route-level RBAC without moving business authority out of
  Application or Domain.
- Apply Confidential Evidence visibility by role and evidence type; always
  redact Restricted Evidence and never expose raw payloads.
- Preserve `401`, route-level `403`, Application-level `403`, `404`, `405` and
  correlation behavior as distinct contracts.
- Keep the MVP single-role and single-tenant bounded; do not infer future
  enterprise entitlements.

## Forbidden

- business approval by AI or infrastructure;
- broad enterprise SSO before the authorized provider gate;
- secrets or sensitive evidence in source, logs, errors or AI context;
- access tokens, private keys or complete claim sets in persistence or logs;
- custom JWT libraries, parsers, cryptography, JWKS clients or caches;
- trusted actor headers after Sprint 3.8;
- authentication used as implicit business approval;
- provider write permissions for the MVP;
- security behavior outside the authorized module;
- more than one module per iteration.
