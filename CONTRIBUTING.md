# Contributing to IMPERATOR

IMPERATOR is currently a tightly scoped portfolio MVP with frozen product and
architecture contracts. Contributions should preserve its evidence-based
scope and are best coordinated with the maintainer before implementation.

## Before a Change

1. Read [the current project status](docs/project/PROJECT_STATUS.md),
   [the Decision Log](docs/decisions/14_Decision_Log.md) and only the contracts
   relevant to the proposed change.
2. State one bounded deliverable and the files it is allowed to modify.
3. Classify affected capabilities as `IMPLEMENTED`,
   `PARTIALLY IMPLEMENTED`, `PLANNED` or `FUTURE`.
4. Stop and request review if the change needs a new public contract,
   dependency, module or architecture decision.

Do not rewrite frozen contracts to make an implementation pass. Do not add
technology or product scope for portfolio appearance.

## Architecture Rules

- Domain and Application remain independent of Spring, HTTP, JDBC, SQL and
  provider SDKs.
- Application use cases orchestrate ports; adapters implement technical
  details.
- Persistence records and external DTOs never cross into Domain.
- Write repositories stay minimal; read projections use separate query ports.
- Ledger history is append-only.
- AI explanation is optional and cannot decide, calculate ROI or mutate data.
- New public port or repository methods require an existing use case.

The authoritative rules are in
[Coding Principles](docs/architecture/35_Coding_Principles.md) and the
[Implementation Contract](docs/architecture/37_Implementation_Contract.md).

## Security Rules

- Never commit secrets, real access tokens, customer payloads or personal
  data.
- Keep connector access read-only and bounded to its frozen contract.
- Preserve server-side authentication, authorization and Evidence redaction.
- Report vulnerabilities using [SECURITY.md](SECURITY.md), not a public issue.

## Verification

Run only the gates relevant to the changed area. Core commands are:

```powershell
.\mvnw.cmd clean verify

Set-Location frontend
npm ci
npm run format:check
npm run lint
npm run typecheck
npm test
npm run build
```

The PostgreSQL profile and Docker certification require their documented local
preconditions. Do not weaken or bypass a failing gate.

## Pull Requests

- Keep one deliverable per pull request.
- Explain purpose, scope, architectural impact, security impact and evidence.
- Include exact commands and results, not only "tests pass".
- Update active navigation only when behavior or repository structure changed.
- Do not claim a capability beyond its repository evidence.
- Use concise imperative commits such as `docs: add portfolio evidence map`.

A change is complete only when its scoped checks pass, documentation remains
truthful and no critical security or architecture issue is knowingly hidden.
