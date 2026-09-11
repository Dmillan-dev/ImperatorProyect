# Sprint 4.4.1 - D096 Certification Closure Prompt

Status: **PROPOSED / NOT AUTHORIZED**

## Purpose

Use this prompt only after explicit founder authorization. Its sole objective
is to close the remaining D096 certification evidence. It does not authorize
Sprint 4.5, a product change or a waiver.

## Preconditions

Start only when all of the following are true:

1. Official stable Prometheus and Grafana candidates are available that can
   pass zero fixable High/Critical vulnerabilities and zero secrets.
2. The in-app browser is available for a sanitized dashboard screenshot.
3. GitHub-hosted Java CI, Security and CodeQL runs can be executed and inspected.

If the image gate still fails, stop and report the exact findings. Do not add an
ignore list, suppress a CVE, lower severity, adopt an unstable image or claim a
pass.

## Mandatory Reading

1. `docs/project/PROJECT_STATUS.md`;
2. `docs/project/SECURITY_AUDIT_2026-09-09.md`;
3. D095 and D096 in `docs/decisions/14_Decision_Log.md`;
4. `docs/architecture/56_Observability_Runtime_Contract.md`;
5. `docs/runbooks/observability-runtime.md`;
6. `infra/docker/compose.yaml` and `.github/workflows/security.yml`; and
7. `scripts/verify-observability-runtime.ps1`.

## Allowed Change Boundary

- exact Prometheus/Grafana digest pins in Compose and the Security workflow;
- observability-only provisioning, tests and verifier evidence when required to
  support a compatible stable image;
- D096 runbook, security audit and active status/agent evidence; and
- no other files unless a verified compatibility failure makes one necessary
  and it remains inside Document 56.

## Forbidden Changes

- Domain, Application, Ports, business rules, Ledger or Business Value;
- product REST routes, response semantics, RBAC/JWT authority or schema/Flyway;
- frontend features, login/session behavior or provider connectors;
- external IdP provisioning, public exposure, customer data or cloud deployment;
- vulnerability waivers, ignore lists, severity reduction or false certification;
- Sprint 4.5 implementation; and
- rewriting frozen contracts or historical evidence to fit an implementation.

## Execution

1. Check the official stable Prometheus and Grafana releases and resolve their
   immutable image digests.
2. Pull and scan each candidate before changing Compose. Record occurrence and
   unique finding counts without exposing secrets.
3. Adopt only candidates that pass the exact D096 zero/zero policy. Keep Compose
   and the Security workflow pins identical.
4. Run `promtool check config` and the full synthetic alert transition suite.
5. Run Java default and PostgreSQL integration verification plus all frontend
   quality gates.
6. Build and scan backend, frontend, PostgreSQL-only Flyway and PostgreSQL
   images; require zero fixable High/Critical findings and zero secrets.
7. Recreate the base and optional observability runtime and run
   `scripts/verify-observability-runtime.ps1`.
8. Capture one sanitized Grafana dashboard screenshot through the in-app
   browser and rerun the verifier with `-DashboardScreenshot`.
9. Run and record hosted Java CI, Security, CodeQL and full-history Gitleaks.
10. Update active status to certified only after every required gate passes.

## Acceptance Evidence

Required final evidence:

- exact source revision and immutable image digests;
- Trivy version/database metadata and zero/zero summaries;
- `promtool` configuration and 11-rule transition results;
- 161 or more Java default tests and all PostgreSQL integration tests passing;
- frontend format, lint, types, tests, build and npm audit passing;
- D096 verifier `PASS`, including the sanitized screenshot manifest;
- monitoring outage isolation and PostgreSQL readiness loss/recovery passing;
- hosted Java CI, Security, CodeQL and full-history Gitleaks passing; and
- zero secrets, customer data, public monitoring ports or contract drift.

## Stop Conditions

Stop without certifying if any candidate image finding remains, any hosted gate
is unavailable or failing, screenshot sanitization cannot be proven, base
runtime behavior regresses, or closure requires leaving the allowed boundary.

## Ready-To-Run Prompt

```text
Execute Sprint 4.4.1 strictly as D096 certification closure. Read the current
project status, the 2026-09-09 security audit, D095/D096, Document 56, the
observability runbook, Compose, Security workflow and verifier first. Recheck
official stable Prometheus and Grafana releases and immutable digests. Adopt an
image only if Trivy reports zero fixable High/Critical findings and zero
secrets; no waiver, ignore list or severity reduction is allowed. Keep Compose
and CI pins identical. Run promtool, complete Java/PostgreSQL/frontend
regression, all maintained-image scans, the full local runtime verifier with a
sanitized in-app-browser dashboard screenshot, and hosted Java CI, Security,
CodeQL and complete-history Gitleaks. Preserve Domain, Application, Ports, REST,
schema, Ledger, Business Value, JWT/RBAC, connectors, frontend behavior,
external identity and public exposure. Report exact evidence and certify Sprint
4.4 only when every D096 gate is green. Do not start Sprint 4.5.
```
