# frontend

## Purpose

Operational web interface for the single DRC-AOA-001 Decision Review Workspace
defined by D091.

## Who Uses This Folder

- Authorized reviewers evaluating evidence, recommendations and business value.
- Implementation Agent for frontend-only delivery.
- Architecture, Security, Quality and Product Guardians during certification.

## Contains

- Next.js 16, React 19 and strict TypeScript application code.
- Decision Review Workspace routes, components and presentation logic.
- Relative `/api/v1/**` client calls and runtime response validation.
- Unit, component, coverage and Playwright E2E tests.

## Current Status

Sprint 4.2 is **CERTIFIED / COMPLETE** under D091.

- 41 unit/component tests pass.
- Contractual coverage thresholds pass.
- Format, lint, strict TypeScript, production build and dependency audit pass.
- Playwright acceptance passes at 1440x900, 1024x768 and 390x844.
- The backend passes 151 default tests and 34 PostgreSQL 18.6
  integration tests.

Sprint 4.3 certified this frontend as a non-root, read-only, SHA-tagged image in
the loopback-only D092-D095 runtime. Sprint 4.4 implementation is authorized
under D096, which explicitly forbids frontend changes.

## Local Development

Requirements: Node.js 24 LTS and npm 11.

```powershell
npm ci
$env:IMPERATOR_API_ORIGIN = "http://127.0.0.1:8080"
npm run dev
```

The browser only calls relative `/api/v1/**` paths. `IMPERATOR_API_ORIGIN` is a
server-side rewrite target and must never use a public client environment name.

## Quality Gates

```powershell
npm run format:check
npm run lint
npm run typecheck
npm run test:coverage
npm run build
npm run test:e2e
```

## Security Boundary

- JWTs exist in memory only and are cleared on reload, logout, expiry or `401`.
- No token, credential or secret may be logged or stored in browser persistence.
- The frontend presents D088 permissions; backend authorization remains
  authoritative.
- ROI and Business Value are rendered from certified backend responses and are
  never recalculated here.

## Never Contains

- Backend business or authorization logic.
- Java or Python source code.
- Database migrations or infrastructure manifests.
- Secrets, persisted tokens or provider credentials.
- Additional product cases outside DRC-AOA-001 without a new frozen contract.
