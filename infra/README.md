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
- The D100 AWS deployment preparation and validated Terraform under `aws/`.
- Future observability configuration.
- Future local infrastructure files explicitly authorized by sprint scope.

Current status:
- Docker Production Runtime implemented under the Sprint 4.3 boundary.
- D098 final-image `libpcre2` security refresh certified.
- No Kubernetes manifests.
- Terraform bootstrap and pilot roots pass offline validation and mock plans;
  AWS apply remains blocked by D095 and explicit cost authorization.
- No runtime secrets.

## Never Contains

- Java, Python or React source code.
- Business rules.
- Domain objects.
- Production secrets.
- Kubernetes during MVP scope unless explicitly reauthorized.

## Runtime Boundary

`docker/` packages the certified backend, frontend and PostgreSQL runtime and
supports both Compose file secrets and ECS environment-secret injection. The
D096 Prometheus/Grafana profile remains deferred beyond MVP. `aws/` may contain
only the D100 pilot boundary; Kubernetes, production secrets and unapproved AWS
apply operations remain unauthorized.
