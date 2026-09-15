# 57 - MVP Observability Scope Correction

Status: **FROZEN / D097 ACCEPTED / IMPLEMENTATION AUTHORIZED**

Date: **2026-09-11**

## 1. Purpose

Reduce Sprint 4.4 to the observability that is necessary to complete and
operate the MVP without weakening any security or supply-chain gate.

The MVP must answer this bounded question:

> Can an operator determine whether the application is serving, correlate a
> failed request and inspect safe runtime metrics without introducing another
> deployable monitoring platform?

D097 authorizes the implementation correction defined here. It does not
certify the implementation and does not authorize Sprint 4.5.

## 2. Trigger And Evidence

The first D096 implementation attempt in pull request `#32` produced passing
application checks but could not certify the added Prometheus and Grafana
images under the repository's fail-closed policy.

The hosted D096 supply-chain gate reported:

| Candidate | Fixable High/Critical result | Secret result |
|---|---:|---:|
| Official Prometheus `v3.13.3-distroless` candidate | 4 High occurrences, 2 CVE IDs | 0 |
| Official Grafana `13.2.1` candidate | 176 High occurrences, 33 CVE IDs | 0 |

Additional stable official candidates did not produce a compatible zero/zero
combination. The compatible Grafana image variants still contained fixable
High findings; the lower-finding distroless variant did not support the
file-backed secret and health mechanisms frozen by D096.

The repository must not solve this by ignoring findings, lowering severity,
marking findings as false positives without evidence, accepting unstable
images or making the gate non-blocking.

Document 32 already permits basic health, logs and metrics while explicitly
deferring a full observability platform. Prometheus, Grafana, dashboards and
alert evaluation are therefore not required to prove the first Decision ROI
Case or release the MVP.

## 3. Authority And Narrow Supersession

D097 is governed by D079, D085, D087-D095, Document 32, Document 56 and the
accepted security and implementation contracts.

D097 narrowly supersedes D096 only where D096 requires or authorizes:

- Prometheus or Grafana services and runtime images;
- an `observability` Compose profile, observability-only network or metrics
  volume;
- a Grafana administrator Docker secret;
- Prometheus scrape configuration, recording or alert rules and rule tests;
- Grafana provisioning, data sources and dashboards;
- monitoring-service outage, alert-transition or dashboard screenshot proof;
- image-pin synchronization for Prometheus and Grafana; and
- a D096 evidence gate whose pass depends on those deferred components.

Those items are deferred beyond MVP. They may return only through a separate
decision based on a concrete operational need and compatible, immutable image
inputs that pass the repository's then-current supply-chain policy.

Every other D096 safety constraint remains authoritative unless this document
states otherwise. In particular, D097 does not weaken telemetry minimization,
correlation cleanup, bounded labels, health semantics, endpoint exposure,
failure isolation or the prohibition on customer data and secrets.

Document 56 remains the historical D096 contract and must not be rewritten to
hide the reason for this correction.

## 4. Authorized MVP Observability

Sprint 4.4 is now limited to application-native observability:

```text
Spring Boot application
  -> safe ECS JSON console events
  -> normalized X-Correlation-ID in bounded MDC scope
  -> liveness and PostgreSQL-aware readiness
  -> bounded in-process Micrometer metrics
  -> internal, unpublished Actuator health and Prometheus endpoints
```

The implementation must retain:

- Spring Boot's managed `spring-boot-starter-actuator`;
- Spring Boot's managed `micrometer-registry-prometheus`;
- Spring Boot's built-in ECS structured console logging;
- the existing SLF4J/Logback stack, with no third-party encoder;
- the D096 safe field, event, redaction and cardinality rules;
- request correlation in MDC with unconditional cleanup;
- `/livez` for process liveness and `/readyz` for PostgreSQL-aware readiness;
- `/actuator/health/liveness`, `/actuator/health/readiness` and
  `/actuator/prometheus` on management port `9090`;
- `show-details=never`, `show-components=never` and an empty JMX exposure;
- no host publication or frontend proxy for management port `9090`;
- metrics that cannot affect an Application result or transaction; and
- the certified Docker `json-file` rotation already owned by D092-D095.

The metrics endpoint exists for bounded local diagnosis, certification and a
future authorized collector. D097 does not authorize a collector in the MVP.

## 5. Security And Data Rules

The D096 telemetry prohibitions remain mandatory. Logs and metrics must never
contain credentials, JWT material, actor identity, customer data, Evidence
content, business identifiers, provider payloads, repository/account names,
financial values, request or response bodies, raw paths, SQL values, exception
messages or uncontrolled stack traces.

The management endpoint allow-list contains only health and Prometheus.
Dangerous or diagnostic Actuator endpoints remain disabled and inaccessible,
including `env`, `configprops`, `beans`, `mappings`, `loggers`, `heapdump`,
`threaddump` and `shutdown`.

Removing Prometheus and Grafana from the active runtime is a scope reduction,
not a vulnerability waiver. Certification must prove that:

- no Prometheus or Grafana image is referenced by active Compose or CI;
- no observability image finding is ignored or suppressed;
- every maintained application/runtime image still passes the existing zero
  fixable High/Critical and zero-secret policy; and
- source, dependency, configuration, CodeQL and secret-scanning gates remain
  blocking.

## 6. Runtime And Product Boundary

Application observability is non-authoritative. A logging or metrics failure
must not change an HTTP result, transaction, Decision, Recommendation,
Evidence, Ledger entry or Business Value projection.

The following remain outside Sprint 4.4:

- OpenTelemetry, traces, collectors and telemetry export;
- Prometheus, Grafana, Alertmanager and external notification;
- log aggregation, profiling and cloud monitoring;
- a frontend observability page or any new product route;
- external identity provisioning, TLS termination or public exposure;
- real customer data, pilot authorization or MVP Release authorization;
- Domain, Application, Port, persistence, schema or business-rule changes; and
- new connectors, schedulers, provider behavior or autonomous action.

External Keycloak HTTPS conformance remains mandatory before Sprint 4.5 under
D095.

## 7. Authorized Implementation Boundary

Sprint 4.4 may modify only:

- `pom.xml` for the two Spring Boot-managed observability dependencies;
- `backend-java/api/observability/**`;
- narrowly required correlation wiring in
  `backend-java/api/errors/CorrelationIdFilter.java`;
- narrowly required authentication and authorization counters in
  `backend-java/api/security/**`;
- narrowly required configuration and telemetry decorators in
  `backend-java/bootstrap/**`;
- `src/test/java/imperator/api/observability/**` and focused existing
  correlation or security tests;
- `infra/docker/compose.yaml` only for backend health/readiness and removal of
  the deferred observability profile;
- `infra/docker/.env.example` and `infra/docker/secrets/README.md` only to
  remove deferred Grafana settings;
- removal of `infra/docker/observability/**` from the proposed implementation;
- `scripts/verify-observability-runtime.ps1` and `scripts/README.md` for the
  reduced application-native verification;
- `.github/workflows/security.yml` only to remove the deferred image gate,
  prohibit those images from active runtime inputs and retain existing
  application-image security gates;
- `docs/runbooks/observability-runtime.md` for the reduced operator procedure;
- `docs/project/SECURITY_AUDIT_2026-09-09.md` only as a preserved historical
  record of the failed image gate;
- `agents/phase3/SPRINT_4.4.1_D096_CERTIFICATION_CLOSURE_PROMPT.md` only to mark
  it superseded by D097; and
- active project-control, security and architecture documents after evidence
  exists.

Unrelated Dockerfile, runtime-image lock, package-manifest, frontend, schema or
application changes from the earlier implementation attempt must be removed
unless a separately evidenced blocker receives explicit authorization.

## 8. Required Verification

Sprint 4.4 may be certified only after all of the following pass:

| Gate | Required proof |
|---|---|
| Java | Java 21 default suite, including all retained observability tests |
| PostgreSQL | Full PostgreSQL 18.6 integration profile and readiness loss/recovery |
| Frontend | Unchanged format, lint, type, test, build and audit regression |
| Logging | Valid one-line ECS JSON, safe allow-listed fields and sentinel redaction |
| Correlation | Valid/generated IDs propagate once and MDC clears after success/failure |
| Metrics | Required bounded meters appear; forbidden labels do not; fixture remains <= 256 custom series |
| Health | Process-only liveness and PostgreSQL-aware readiness return the contracted status codes |
| Exposure | Only health and Prometheus Actuator endpoints are enabled; port `9090` is unpublished and unproxied |
| Isolation | Logging or metrics failure cannot alter product behavior or persistence |
| Compose | Certified D092-D095 base runtime and persistence/recreation remain green |
| Supply chain | Maintained final images report zero fixable High/Critical findings and zero secrets |
| Deferred stack | No active Prometheus/Grafana service, image, profile, volume, secret, dashboard or alert rule remains |
| Hosted gates | Java CI, Security, CodeQL and full-history secret scanning pass without bypass |

No Grafana screenshot, Prometheus rule transition or monitoring-service image
scan is required because those components are not part of the authorized MVP
runtime.

Evidence must be sanitized and must record the source revision, dependency
tree, test summaries, endpoint matrix, safe log samples, metric names and
labels, maintained image identities and scanner metadata.

## 9. Stop Conditions

Stop and request a new decision if implementation requires:

- an exception, ignore rule or non-blocking security result;
- Prometheus, Grafana or another external telemetry service;
- a public management endpoint or product authentication weakening;
- a new product route, schema object, migration or persistent telemetry store;
- Domain, Application, Port, Ledger, ROI or Business Value changes;
- customer data, external exposure, cloud deployment or an operational IdP;
- rewriting D096 or another frozen historical contract; or
- claiming Sprint 4.4 certification before every D097 gate passes.

## 10. Gate State

```text
D096 CONTRACT: FROZEN / PARTIALLY SUPERSEDED BY D097
D097 SCOPE CORRECTION: FROZEN / ACCEPTED
SPRINT 4.4 MVP OBSERVABILITY IMPLEMENTATION: AUTHORIZED / CURRENT
SPRINT 4.4 CERTIFICATION: PENDING
EXTERNAL PILOT IDENTITY: STILL MANDATORY BEFORE SPRINT 4.5
SPRINT 4.5: PENDING / NOT AUTHORIZED
```
