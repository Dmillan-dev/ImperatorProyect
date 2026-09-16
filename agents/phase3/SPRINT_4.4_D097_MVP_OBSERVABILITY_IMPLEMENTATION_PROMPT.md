# Sprint 4.4 - D097 MVP Observability Implementation Prompt

Status: **EXECUTED / LOCAL PASS / HOSTED D097 GATES PENDING**

## Objective

Rework the proposed D096 implementation into the minimum application-native
observability required for the MVP and certify it under D097 without weakening
any supply-chain or security gate.

## Mandatory Reading

1. `docs/project/PROJECT_STATUS.md`;
2. D095-D098 in `docs/decisions/14_Decision_Log.md`;
3. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`;
4. `docs/architecture/56_Observability_Runtime_Contract.md`;
5. `docs/architecture/57_MVP_Observability_Scope_Correction.md`;
6. `docs/architecture/58_Runtime_Supply_Chain_Refresh.md`;
7. `docs/architecture/51_Docker_Production_Runtime_Contract.md`; and
8. the current implementation diff against `main`.

Document 57 has narrow precedence over Document 56 for the external monitoring
stack and its certification evidence. All retained D096 data-minimization and
failure-isolation rules remain binding.

Do not execute this prompt until the hosted D098 application-image and D094
supply-chain jobs pass on `main`.

## Allowed Files

- `pom.xml`;
- `backend-java/api/observability/**`;
- narrowly required `backend-java/api/errors/CorrelationIdFilter.java`;
- narrowly required `backend-java/api/security/**` instrumentation;
- narrowly required `backend-java/bootstrap/**` configuration and decorators;
- `src/test/java/imperator/api/observability/**` and focused existing security
  or correlation tests;
- `infra/docker/compose.yaml`, `infra/docker/.env.example` and
  `infra/docker/secrets/README.md` only within the D097 boundary;
- removal of proposed `infra/docker/observability/**` assets;
- `scripts/verify-observability-runtime.ps1` and `scripts/README.md`;
- `.github/workflows/security.yml` only within the D097 boundary;
- `docs/runbooks/observability-runtime.md`;
- `docs/project/SECURITY_AUDIT_2026-09-09.md` only as historical evidence;
- `agents/phase3/SPRINT_4.4.1_D096_CERTIFICATION_CLOSURE_PROMPT.md` only to mark
  it superseded by D097; and
- active control documentation after the implementation passes.

## Required Implementation

1. Retain managed Actuator and Micrometer Prometheus dependencies.
2. Retain safe one-line ECS JSON logging and bounded MDC correlation.
3. Retain `/livez`, `/readyz`, PostgreSQL-aware readiness and process-only
   liveness.
4. Retain bounded, allow-listed custom meters and label/cardinality tests.
5. Keep management port `9090` internal, unpublished and unproxied.
6. Remove Prometheus and Grafana services, images, profile, network, volume,
   secret, provisioning, dashboards and alert rules from the proposed MVP.
7. Replace the external observability image gate with a fail-closed assertion
   that those deferred runtime inputs are absent.
8. Preserve every existing maintained-image, dependency, source, CodeQL and
   secret-scanning gate.
9. Remove unrelated changes from the earlier D096 attempt unless they are
   independently required and explicitly authorized.
10. Preserve the failed image audit as history and mark the obsolete D096
    certification-closure prompt superseded by D097.

## Forbidden Changes

- vulnerability ignores, waivers, suppressions, severity reduction or
  `continue-on-error`;
- Prometheus, Grafana, Alertmanager or another telemetry service;
- Domain, Application, Ports, SQL, Flyway, schema or business semantics;
- product routes, DTOs, frontend features or connector behavior;
- JWT claims, issuer, JWKS, role mappings or D088 grants;
- external IdP, TLS, public exposure, customer data or cloud deployment; and
- Sprint 4.5 or MVP Release claims.

## Verification

Run and record:

1. Java 21 default verification with every retained observability test;
2. PostgreSQL 18.6 integration verification and readiness loss/recovery;
3. frontend format, lint, strict TypeScript, tests, build and audit;
4. D092-D095 base Compose and persistence/recreation regression;
5. ECS JSON, redaction, correlation, metrics, cardinality, endpoint and failure
   isolation checks;
6. maintained final-image Trivy scans at zero fixable High/Critical and zero
   secrets;
7. source/dependency/configuration scanning and full-history secret scanning;
8. a repository assertion proving no active Prometheus/Grafana runtime asset;
   and
9. hosted Java CI, Security and CodeQL.

Do not require a Grafana screenshot, alert transition or scan of an image that
is no longer an MVP runtime input.

## Acceptance

Sprint 4.4 passes only when application-native observability is implemented,
all D097 verification is green, no security control is bypassed and active
documentation reports only verified behavior. Sprint 4.5 remains closed.

## Ready-To-Run Prompt

```text
After D098 is green on main, implement Sprint 4.4 strictly under D097. Read
project status, D095-D098, Documents 32, 51 and 56-58, then inspect the current
branch diff against main.
Keep managed Actuator, Micrometer Prometheus registry, safe ECS JSON logs,
bounded MDC correlation, liveness, PostgreSQL-aware readiness, bounded metrics
and internal unpublished management endpoints. Remove Prometheus and Grafana
services, images, profile, network, volume, secret, provisioning, dashboards,
alerts and their obsolete certification gate. Do not ignore or waive any CVE;
prove the deferred images are absent and retain all maintained-image, source,
dependency, CodeQL and secret-scanning gates. Remove unrelated changes from the
earlier attempt. Run complete Java, PostgreSQL, frontend, Docker runtime,
telemetry, redaction, cardinality, exposure, persistence and hosted CI/security
verification. Preserve Domain, Application, Ports, REST, schema, Ledger,
Business Value, JWT/RBAC, connectors, frontend behavior, external identity and
public-exposure boundaries. Certify only when every D097 gate is green. Do not
start Sprint 4.5.
```
