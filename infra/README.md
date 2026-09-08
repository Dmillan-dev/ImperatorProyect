# infra

## Purpose

Owns the local infrastructure and operational foundation for running
IMPERATOR without allowing infrastructure to drive the domain.

## Who Uses This Folder

- Implementation Agent during Docker and observability foundation sprints.
- Architecture Guardian to verify infrastructure does not drive the domain.
- Quality Agent to review local runtime configuration once authorized.

## Contains

- The D092 Docker Production Runtime under `docker/`.
- Future observability configuration.
- Future local infrastructure files explicitly authorized by sprint scope.

Current status:
- Docker Production Runtime implemented under the Sprint 4.3 boundary.
- No Kubernetes manifests.
- No Terraform modules.
- No runtime secrets.

## Never Contains

- Java, Python or React source code.
- Business rules.
- Domain objects.
- Production secrets.
- Kubernetes or Terraform during MVP Phase 2 unless explicitly reauthorized.

## Runtime Boundary

`docker/` may package only the certified backend, frontend and PostgreSQL
runtime. D096 freezes a future optional observability profile, but Sprint 4.4
implementation remains unauthorized. Kubernetes, Terraform, cloud deployment
and production secrets remain unauthorized.
