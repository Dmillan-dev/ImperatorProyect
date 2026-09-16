# IMPERATOR Observability Runtime Runbook

Status: **D097 IMPLEMENTED / LOCAL PASS / HOSTED PENDING**

This runbook covers the application-native observability retained for the MVP.
There is no Prometheus, Grafana, Alertmanager, collector, dashboard or alerting
service in the active runtime.

Use only correlation IDs, normalized route IDs, bounded outcomes and aggregate
metrics during diagnosis. Never copy JWTs, credentials, request bodies,
Evidence, customer identifiers, provider values, financial values or raw
database content into logs, evidence or incident records.

## Runtime Access

Start the base runtime from the repository root with an immutable image tag:

```powershell
$env:IMPERATOR_IMAGE_TAG = git rev-parse HEAD
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml up --detach --wait --build
```

Only the frontend is published, on loopback. Backend application port `8080`
and management port `9090` are available only inside Compose networks and are
not proxied by the frontend.

The allowed operational endpoints are:

| Endpoint | Port | Meaning |
|---|---:|---|
| `/livez` | 8080 | Process liveness only |
| `/readyz` | 8080 | PostgreSQL-aware readiness |
| `/actuator/health/liveness` | 9090 | Internal process liveness |
| `/actuator/health/readiness` | 9090 | Internal PostgreSQL-aware readiness |
| `/actuator/prometheus` | 9090 | Internal bounded metric exposition |

All other Actuator endpoints are disabled. Health responses never expose
details or components.

## Standard Triage

1. Record UTC detection time and a non-sensitive incident identifier.
2. Check container health, then `/livez` and `/readyz`.
3. Correlate safe ECS JSON events using one canonical `X-Correlation-ID`.
4. Inspect only normalized HTTP, security, database and operation metrics.
5. Restore the failed dependency or capacity without changing product data.
6. Confirm readiness recovery and record the bounded corrective action.

Telemetry is diagnostic and non-authoritative. It must never retry a command,
change an HTTP result, mutate a transaction, append a Ledger entry or alter a
Business Value projection.

## Readiness Failure

If `/livez` returns `200` while `/readyz` returns `503`, inspect PostgreSQL
container health and connectivity. This is the expected state when the process
is alive but cannot serve database-backed traffic. Do not delete or recreate
the persistent volume during triage.

After PostgreSQL recovers, confirm both `/readyz` and the internal readiness
endpoint return `200`. Readiness transition logs contain only bounded state and
event fields.

## HTTP And Security Signals

Use normalized route templates, methods, status groups and outcomes. Raw paths,
query strings, subjects and request bodies are prohibited metric labels.

Authentication counters distinguish only bounded reasons such as missing or
invalid tokens. Authorization counters use the allow-listed role and route ID.
Never record bearer material, JWT claims, email addresses or key data. Repeated
unexplained failures remain a security incident and must not be resolved by
weakening D087 or D088.

## Database And Connector Signals

Database metrics use bounded operation and outcome values. Connector metrics
identify only the allow-listed source (`github` or `aws`) and outcome. Provider
account, repository, resource, payload and credential values are prohibited.

Product state must be reviewed through authorized APIs. Observability cannot
repair Decisions, Recommendations, Evidence or Ledger history.

## Verification And Evidence

Run the complete controlled runtime check from the repository root:

```powershell
.\scripts\verify-observability-runtime.ps1 `
  -ImageTag (git rev-parse --short HEAD)
```

The verifier proves endpoint allow-listing, host non-publication, ECS JSON,
sentinel redaction, correlation, required metrics, bounded series, telemetry
failure isolation and readiness loss/recovery. Sanitized local evidence is
written only below ignored `build/d097`.

Stop the runtime without deleting persistent data:

```powershell
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml down
```

Do not add `--volumes` during normal operation or incident response.
