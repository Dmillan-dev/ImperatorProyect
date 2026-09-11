# Local Docker Secrets

## Purpose

Holds local file-backed secrets consumed by the Sprint 4.3 Docker Compose
runtime.

## Required Local Files

- `postgres-owner-password`
- `postgres-app-password`

The optional `observability` profile additionally requires:

- `grafana-admin-password`

The optional connector overlay additionally requires:

- `github-token`
- `aws-credentials`

Create these files manually with non-empty values. The AWS file must use the
standard shared-credentials INI format and short-lived read-only credentials.

Every file except this README is ignored by Git. Never commit real values,
reuse customer credentials, print file contents or place JWT tokens here.
