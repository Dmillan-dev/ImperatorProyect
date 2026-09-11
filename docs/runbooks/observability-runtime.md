# IMPERATOR Observability Runtime Runbook

Status: **SPRINT 4.4 IMPLEMENTED / CERTIFICATION BLOCKED**

This runbook covers the local D096 Prometheus alerts. Use only correlation IDs,
normalized route IDs, bounded outcomes and aggregate metrics during diagnosis.
Never paste JWTs, credentials, request bodies, Evidence, customer identifiers,
provider values or raw database content into incident records.

## Runtime Access

Start the optional profile from the repository root:

```powershell
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml `
  --profile observability up --detach --wait --build
```

Grafana is loopback-only at `http://127.0.0.1:3001` by default. Prometheus and
backend management port `9090` are internal. Stop the profile without deleting
product or metric data:

```powershell
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml `
  --profile observability down
```

Do not add `--volumes` during normal operation. Prometheus retention is bounded
to seven days or 256 MiB, whichever occurs first. Its data and alert state are
diagnostic only; PostgreSQL and the append-only Ledger remain authoritative.

## Common Triage

1. Record UTC detection time, alert name, severity and a non-sensitive incident
   identifier.
2. Check `/livez` and `/readyz` through the internal backend network, then the
   Prometheus target and Grafana dashboard.
3. Correlate only bounded route, outcome and correlation fields. Confirm no
   sensitive values are copied into notes.
4. Restore the affected dependency or capacity. Never retry a command or alter
   product data solely because an alert fired.
5. Confirm the alert recovers after its configured window and record the safe
   corrective action.

## Backend Unavailable

For `ImperatorBackendUnavailable`, check backend container health and whether
`/actuator/prometheus` is reachable from the Prometheus container. Confirm the
application `/livez` and `/readyz` states before restarting anything. A scrape
network/configuration failure can leave the product healthy; treat it separately
from an actual backend outage.

## HTTP 5xx Rate

For `ImperatorHttp5xxRateHigh`, compare request count and 5xx ratio by normalized
`method`, `uri`, `status` and `outcome`. Use correlation IDs from the affected
window to inspect safe application events. Check readiness and database-failure
counters. Do not inspect or export request bodies.

## Read Latency

For `ImperatorReadLatencyHigh`, identify the contracted GET route template with
elevated p95 latency. Compare request rate, JVM state, readiness and database
failure counters. Confirm recovery using aggregate latency only; do not add
object identifiers or raw paths as labels.

## Command Latency

For `ImperatorCommandLatencyHigh`, identify the contracted POST route template
with elevated p95 latency. Check database readiness and the matching bounded
operation outcome. Do not replay, approve or retry a command as an observability
action; follow the existing product workflow.

## Authentication Failures

For `ImperatorAuthenticationFailureBurst`, compare only `missing_token` and
`invalid_token` counts. Check issuer/JWKS availability and clock/configuration
outside logs. Never capture tokens, JWT claims, subjects, emails or key material.
Escalate repeated unexplained failures as a security incident.

## Authorization Denials

For `ImperatorAuthorizationDenialBurst`, compare the bounded role and R01-R16
route IDs. Verify D088 role assignment at the external identity boundary. Do
not weaken authorization or add subject identifiers to telemetry.

## Ledger Command Failure

For `ImperatorLedgerCommandFailure`, check bounded command and outcome values,
database readiness and safe correlation events. Preserve D083 transaction and
append-only rules. Product state must be reviewed through the authorized API;
telemetry cannot repair or append Ledger entries.

## Connector Sync Failure

For `ImperatorConnectorSyncFailure`, identify only the bounded `github` or `aws`
source and outcome. Verify connector enablement, read-only credentials and
provider availability outside telemetry. Never record repository, account,
resource, credential or Evidence values in the incident.

## Composition Conflicts

For `ImperatorCompositionConflictBurst`, compare `conflict` and `not_ready`
outcomes and database health. Review the case through existing authorized
product routes. Do not change idempotency, uniqueness or transaction behavior
to clear the alert.

## Database Operation Failure

For `ImperatorDatabaseOperationFailure`, check `/readyz`, PostgreSQL container
health and the bounded operation label. Restore database availability using the
existing D092-D095 runtime procedure. Liveness should remain healthy while
readiness fails; do not delete or recreate volumes during triage.

## JVM Heap High

For `ImperatorJvmHeapHigh`, compare heap used/max, process CPU, request rate and
latency over the full alert window. Capture aggregate metrics and safe event
counts before a controlled backend restart. Escalate repeat growth for heap
analysis outside customer-bearing environments.

## Recovery And Evidence

Run `scripts/verify-observability-runtime.ps1` for controlled local validation.
Sanitized evidence belongs only in ignored `build/d096` locally and is retained
for 14 days in CI. The optional profile is healthy only when monitoring starts,
but monitoring failure must never change product readiness or product data.
