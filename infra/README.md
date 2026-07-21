# infra

## Purpose

Infraestructura local y futura base operacional para ejecutar IMPERATOR.

## Who Uses This Folder

- Implementation Agent during Docker and observability foundation sprints.
- Architecture Guardian to verify infrastructure does not drive the domain.
- Quality Agent to review local runtime configuration once authorized.

## Contains

- Future Docker Compose assets.
- Future observability configuration.
- Future local infrastructure files explicitly authorized by sprint scope.

Sprint 1 status:
- Repository shell only.
- No Dockerfiles.
- No docker-compose file.
- No Kubernetes manifests.
- No Terraform modules.
- No runtime secrets.

## Never Contains

- Java, Python or React source code.
- Business rules.
- Domain objects.
- Production secrets.
- Kubernetes or Terraform during MVP Phase 2 unless explicitly reauthorized.

## Authorized Next Use

A later sprint may add Docker Compose only when the platform foundation has
code modules to run.
