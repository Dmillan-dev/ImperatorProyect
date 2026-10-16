# Decision Review Workspace

The Next.js interface for IMPERATOR's single synthetic case, `DRC-AOA-001`. Review evidence and policy output, exercise assigned human approval and inspect the Decision Ledger and validated simulated value.

[Project overview](../README.md) · [Local TFG guide](../docs/tfg/README.md) · [Runtime preflight](../docs/tfg/A_S4_LOCAL_DEFENSE.md#preflight-para-otra-ejecución)

## What is implemented?

- Next.js 16, React 19 and strict TypeScript.
- One workspace with evidence, recommendation, review, Ledger and Business Value.
- Relative `/api/v1/**` requests and runtime response-schema validation.
- Role-aware presentation; actor, role and state enforcement remains on the backend.
- Optional explanation rendering with an explicit unavailable-provider state.

Application code calculates the amounts. The interface presents certified backend results; it does not recalculate ROI or create financial truth.

## Evidence and current limits

| Check                  | Evidence                                                                                           |
| ---------------------- | -------------------------------------------------------------------------------------------------- |
| Unit/component quality | 41 tests and coverage/format/lint/type/build checks passed in the historical 9 October evaluation. |
| Browser acceptance     | Playwright: 9 passed / 6 intentional skips in that baseline. Skips are not passes.                 |
| Real services          | 14 October Docker rehearsal: UI approval, API implementation/validation and unmocked UI reads.     |
| Production image       | 16 October exact-image scan: 0 fixable H/C and 0 secrets; bounded residual risk accepted.          |
| Academic delivery      | Memory and English script prepared; measured human defence and academic review pending.            |

The [current R-20 decision](../docs/tfg/evidence/2026-10-16-r20-local-acceptance/README.md) covers four immutable local images and synthetic data until **19 October 2026 00:00 Europe/Madrid**. It does not cover other builds, AWS, production or real data. R-23 development tooling remains OPEN independently.

## Local development

Requirements: Node.js 24 and npm 11. From this directory:

```powershell
npm ci
$env:IMPERATOR_API_ORIGIN = "http://127.0.0.1:8080"
npm run dev
```

The browser calls relative `/api/v1/**` paths. `IMPERATOR_API_ORIGIN` is a server-side rewrite target and must never use a public client environment name. Authentication and synthetic fixtures must be configured through the documented runtime preflight.

## Quality gates

```powershell
npm run format:check
npm run lint
npm run typecheck
npm run test:coverage
npm run build
npm run test:e2e
```

Browser tests require their documented harness. [Rehearsal and limitations](../docs/tfg/evidence/2026-10-14-a-s4-runtime/README.md).

## Security boundary

JWTs remain in memory and are cleared on reload, logout, expiry or `401`. Tokens and credentials must not be logged or persisted in the browser. UI visibility does not replace server authorization. No additional product case or live provider validation is claimed by these frontend checks.
