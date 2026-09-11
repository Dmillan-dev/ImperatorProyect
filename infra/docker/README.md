# Docker Production Runtime

## Purpose

Runs the certified IMPERATOR MVP as a loopback-only, production-like local
Docker Compose deployment under D092.

## Certification Status

Sprint 4.3 is **CERTIFIED / COMPLETE** under D092-D095. PostgreSQL 18.6 supply
chain, SHA-tagged images, hardening, Flyway, least-privilege grants, local
JWT/RBAC E2E and persistence after recreation pass. D095 defers operational
external Keycloak HTTPS conformance to a mandatory gate before Sprint 4.5; the
runtime remains fail-closed and stores no IdP credentials.

The current migration image is built locally from the digest-pinned official
Flyway 13.5.0 image. It retains only the PostgreSQL driver and matching Flyway
database module, runs as UID/GID `65532`, and passes the same zero-fixable-
High/Critical and zero-secret policy as the application images.

## Preconditions

- Docker Engine or Docker Desktop in Linux-container mode.
- Docker Compose v2.
- Required secret files documented in `secrets/README.md`.
- A local `.env` based on `.env.example`.
- External HTTPS D087 issuer/JWKS configuration.

Set `IMPERATOR_IMAGE_TAG` to the full output of `git rev-parse HEAD`.

## Runtime

From the repository root:

```powershell
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml config --quiet

docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml build

docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml up --detach --wait
```

The workspace is available only at `http://127.0.0.1:3000` unless the local
loopback port is changed in `.env`.

Stop without deleting data:

```powershell
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml down
```

Do not add `--volumes` during normal operation.

## Optional Existing Connectors

The base runtime disables GitHub and AWS. To supply the already-frozen D089 and
D090 configuration:

```powershell
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml `
  -f infra/docker/compose.connectors.yaml up --detach --wait
```

This overlay does not invoke synchronization and adds no route or scheduler.

## Optional Observability Profile

The D096 profile adds internal Prometheus scraping and one loopback-only
Grafana operator dashboard. It is not required by the product runtime:

```powershell
docker compose --env-file infra/docker/.env `
  -f infra/docker/compose.yaml `
  --profile observability up --detach --wait --build
```

Grafana is available at `http://127.0.0.1:3001` by default. Its admin password
comes only from `infra/docker/secrets/grafana-admin-password`. Backend
management port `9090` and Prometheus remain internal and are not published.

Run the local technical gate from the repository root:

```powershell
./scripts/verify-observability-runtime.ps1 -KeepRuntime
```

Pass `-DashboardScreenshot <sanitized-png>` to include the required visual
evidence. Without it, the evidence manifest reports
`PASS_WITH_SCREENSHOT_PENDING`. Prometheus retains at most seven days or 256
MiB; Grafana runtime state is disposable. Neither store is audit evidence or a
source of truth.

## Contains

- Backend and frontend multi-stage Dockerfiles.
- PostgreSQL-only Flyway migration image.
- Compose runtime and optional connector overlay.
- Read-only migration and least-privilege database setup.
- Local secret boundary and safe examples.
- Optional D096 Prometheus rules and Grafana provisioning.

## Never Contains

- Product or business logic.
- Real secrets or JWTs.
- Public ports for backend or PostgreSQL.
- An IdP, reverse proxy or provider emulator.
- A public monitoring endpoint, log aggregation platform or Alertmanager.
- Destructive database reset automation.
