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

| Check                  | Evidence                                                                                                                                           |
| ---------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- |
| Unit/component quality | 20 November: 44 tests and format/lint/type/build checks PASS with portable Node 24.22.3.                                                           |
| Browser acceptance     | 20 November: Playwright 9 passed / 6 intentional skips with intercepted synthetic API. This is not a complete Docker rehearsal.                    |
| Dependency security    | Complete and production npm audits: 0 findings. The affected minimatch development chain is corrected through compatible TypeScript-ESLint 8.73.0. |
| Production image       | Exact Node 24.22.3 candidate: 0 fixable H/C and 0 secrets; installed affected tooling packages absent from inspected /app.                         |
| Real services          | The historical 14 October Docker rehearsal remains dated evidence; full current-set rehearsal is pending.                                          |
| Academic delivery      | Memory and English script prepared; measured human defence, narrated recording and academic review pending.                                        |

The previous R-20 acceptance **expired on 19 October 2026 at 00:00 Europe/Madrid**. The [current review](../docs/tfg/evidence/2026-11-20-minimatch-node/README.md) remains `accepted: false` for the four exact local/synthetic image identities. The unapproved 18 November deadline was not authorization. Passing scans or merging a patch does not renew acceptance. R-23 disposition remains separate after its advisory withdrawal. AWS, production and real data remain outside scope.

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
