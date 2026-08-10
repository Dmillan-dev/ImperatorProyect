# Docker Production Runtime

## Purpose

Runs the certified IMPERATOR MVP as a loopback-only, production-like local
Docker Compose deployment under D092.

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

## Contains

- Backend and frontend multi-stage Dockerfiles.
- Compose runtime and optional connector overlay.
- Read-only migration and least-privilege database setup.
- Local secret boundary and safe examples.

## Never Contains

- Product or business logic.
- Real secrets or JWTs.
- Public ports for backend or PostgreSQL.
- An IdP, reverse proxy, observability stack or provider emulator.
- Destructive database reset automation.
