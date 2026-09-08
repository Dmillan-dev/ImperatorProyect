# 56 - Minimum Observability Runtime Contract

Status: **FROZEN / D096 ACCEPTED / IMPLEMENTATION NOT AUTHORIZED**

Date: **2026-09-08**

## 1. Purpose

Freeze the smallest operational-observability boundary required between the
certified local Docker runtime and Pilot Readiness.

Sprint 4.4 must answer one operational question without creating a second
source of business truth:

> Can an operator detect, correlate and diagnose a failure in the certified
> `DRC-AOA-001` flow without exposing credentials, customer evidence or
> unbounded telemetry?

This contract authorizes no implementation. A separate founder authorization
is required before changing source, dependencies, tests, Docker or CI.

## 2. Authority And Supersession

This contract is governed by:

1. D059, D079, D085 and D096;
2. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`;
3. `docs/architecture/27_Quality_Attributes.md`;
4. `docs/architecture/35_Coding_Principles.md`;
5. `docs/architecture/37_Implementation_Contract.md`;
6. D087 and `46_JWT_Authentication_Contract.md`;
7. D088 and `47_RBAC_Authorization_Contract.md`;
8. D089 and `48_GitHub_Integration_Contract.md`;
9. D090 and `49_AWS_Integration_Contract.md`;
10. D091 and `50_Executive_Dashboard_Contract.md`;
11. D092-D095 and Documents 51, 52 and 55; and
12. `54_Observability_Preparation.md` as a non-authoritative design input.

D096 supersedes Document 54 only as the authoritative Sprint 4.4 contract.
Document 54 remains the preserved preparation record.

D096 may later supersede only these D092 runtime details during an explicitly
authorized Sprint 4.4 implementation:

- add one optional `observability` Compose profile;
- add one internal observability network and one bounded metrics volume;
- add Prometheus and Grafana services; and
- publish the Grafana operator UI on loopback only while that profile is
  active.

It does not modify the certified base runtime, product port, PostgreSQL volume,
JWT/RBAC behavior or default connector state.

## 3. Inspected Baseline

| Capability | Current state |
|---|---|
| `X-Correlation-ID` validation and response propagation | Implemented and tested |
| Safe four-field API error envelope | Implemented and tested |
| Connector correlation in Application contracts | Implemented |
| Append-only governance Ledger | Certified |
| Docker process/service healthchecks | Certified under D092-D095 |
| Docker `json-file` rotation | `10m`, three files per service |
| Spring Boot Actuator | Absent |
| Micrometer Prometheus registry | Absent |
| Structured application JSON logs | Absent |
| Application liveness/readiness endpoints | Absent |
| Prometheus, Grafana and alert rules | Absent |
| OpenTelemetry tracing | Absent |
| External log, metric or trace backend | Absent |

The existing Ledger is business audit history. It is not an operational log,
metric store, trace backend or alert source.

## 4. Contract Decision

Sprint 4.4 is limited to:

```text
Spring Boot application
  -> safe ECS JSON console events
  -> existing normalized correlation ID in MDC
  -> Actuator health and readiness
  -> bounded Micrometer metrics
  -> internal Prometheus scrape and rule evaluation
  -> one provisioned Grafana operator dashboard
```

The implementation must use:

- Spring Boot's managed `spring-boot-starter-actuator`;
- Spring Boot's managed `micrometer-registry-prometheus`;
- Spring Boot's built-in ECS structured console logging;
- the existing SLF4J/Logback stack;
- one digest-pinned official Prometheus image; and
- one digest-pinned official Grafana OSS image.

No third-party Logback encoder is permitted. Exact managed library versions
must come from the existing Spring Boot BOM. Exact Prometheus and Grafana tags,
platform digests and vulnerability database metadata must be recorded by the
implementation evidence before certification.

## 5. Explicit Non-Goals

Sprint 4.4 must not add:

- OpenTelemetry SDKs, agents, collectors or distributed traces;
- Loki, Elasticsearch, OpenSearch or another log aggregator;
- Alertmanager, email, Slack, PagerDuty or another notification channel;
- a PostgreSQL exporter, node exporter or Docker socket collector;
- JMX exposure, a remote profiling endpoint or continuous profiling;
- Kubernetes, Terraform, cloud deployment or a managed monitoring service;
- a public observability endpoint or non-loopback operator port;
- business analytics, financial amounts or customer reporting in Grafana;
- a new product REST route or frontend observability page;
- a new database table, migration, repository or telemetry outbox;
- changes to Domain, Application commands, business rules, ROI, Recommendation,
  Ledger or Business Value semantics; or
- external Keycloak, real customer data or pilot exposure.

Correlation plus structured events is the Sprint 4.4 trace mechanism.
Distributed tracing remains deferred.

## 6. Signal Ownership

| Signal | Owns | Must never own |
|---|---|---|
| Structured event | Diagnostic account of one operation | Business audit history |
| Metric | Rate, duration, availability or bounded outcome | ROI or realized value |
| Health probe | Current ability to serve safely | Historical workflow truth |
| Prometheus alert | Operational symptom requiring review | Decision approval or execution |
| Grafana dashboard | Operator visibility | Executive Decision Workspace |
| Ledger | Immutable human-governance fact | Runtime diagnostics |

Telemetry loss, duplication, delay or failure must never mutate a Decision,
Recommendation, Evidence record, Ledger entry or Business Value result.

## 7. Correlation Contract

The existing normalized UUID correlation identifier remains authoritative for
one HTTP operation:

```text
caller
  -> X-Correlation-ID validation or generation
  -> request-scoped MDC correlation_id
  -> REST and adapter telemetry
  -> response header and controlled error envelope
  -> MDC removal in a finally boundary
```

Rules:

- the existing validation, normalization and response contract is unchanged;
- MDC contains only `correlation_id` from request context;
- MDC state must be removed even when authentication, authorization,
  Application or persistence fails;
- no `ThreadLocal` may be introduced outside the logging framework's bounded
  MDC lifecycle;
- correlation ID is not a Decision, Evidence, Recommendation, case, actor,
  idempotency or tenant identifier;
- a correlation value must not be emitted as a metric label;
- connectors may include correlation in local telemetry but must not add a new
  provider header unless their frozen connector contract already allows it;
  and
- background or startup events without request context omit correlation rather
  than inventing a product identity.

## 8. Structured Logging Contract

The production-like container profile must write one ECS JSON object per line
to stdout. Files remain owned by Docker's certified `json-file` rotation.

The framework-managed ECS envelope may contain only its standard timestamp,
level, logger, thread, process, service, ECS-version and message fields. The
normal production-like profile must exclude structured exception messages and
stack traces. Every field added by IMPERATOR code or MDC uses this allow-list:

| Field | Rule |
|---|---|
| `@timestamp` | Framework-generated UTC timestamp |
| `log.level` | `INFO`, `WARN` or `ERROR` |
| `service.name` | Constant `imperator-backend` |
| `event` | Stable event name from this contract |
| `module` | Bounded component name |
| `correlation_id` | Canonical UUID when request-scoped |
| `outcome` | Bounded value defined for that event |
| `duration_ms` | Non-negative duration when applicable |
| `route_id` | `R01` through `R16` where known |
| `source_type` | `github` or `aws` only for connector events |
| `command` | Bounded governance command where applicable |
| `actor_role` | One D087 role or `unknown`; never actor subject |
| `error_code` | Safe stable IMP code where applicable |

Exact event names:

- `http.request.completed`;
- `authentication.failed`;
- `authorization.denied`;
- `evidence.import.completed`;
- `connector.sync.completed`;
- `decision.composition.completed`;
- `ledger.command.completed`;
- `business_value.projection.completed`;
- `database.operation.failed`; and
- `runtime.readiness.changed`.

An implementation may emit fewer fields when they do not apply. Application
messages are constant safe summaries, not interpolated product data. It may
not invent dynamic field names or serialize a DTO, command, entity, provider
response or exception as structured context.

The following values are forbidden in all IMPERATOR-owned application events
and dashboard logs, and the final log stream must pass sentinel redaction
tests for them:

- `Authorization` headers, JWTs, claims, `kid` values and token fragments;
- passwords, Docker secrets, API keys, GitHub tokens or AWS credentials;
- actor `sub`, email, username, IP address or user-agent;
- raw URI, query string or path parameters containing object identifiers;
- request or response bodies;
- Evidence identifiers, summaries, observed facts, metadata or raw payloads;
- Decision, Recommendation, Ledger or case identifiers;
- monetary amounts, costs, savings, confidence inputs or customer counts;
- SQL text with values, JDBC URLs containing credentials or database
  passwords;
- provider request/response payloads, repository names or AWS account IDs;
- prompts, completions or prepared AI context; and
- uncontrolled exception messages or stack traces in normal runtime events.

Expected failures are logged without a stack trace. Unexpected IMPERATOR-owned
events expose only the safe error category and correlation ID. Framework
startup and shutdown output is included in the final redaction test. Debug
logging is disabled by default and cannot be activated through an HTTP
endpoint.

## 9. Metrics Contract

Metrics are in-process and non-transactional. Recording failure is swallowed
or isolated after the authoritative outcome has been determined.

Required framework metric families:

- HTTP server request count and duration;
- JVM memory, garbage collection and thread metrics;
- process CPU, uptime and file-descriptor metrics where supported; and
- application startup and availability metrics supplied by Spring Boot.

The HTTP metric may retain only bounded framework tags for method, normalized
route template, status and outcome. Its route-template values are limited to
the frozen D086/D093 paths, the contracted operational paths and bounded
framework fallbacks such as `NOT_FOUND` or `UNKNOWN`; a raw request path or
identifier is forbidden. Exception class/message tags must be removed or
normalized to one constant value. No custom HTTP tag may be derived from a
header, claim, query string, path value or request body.

Required IMPERATOR meter names and dimensions:

| Micrometer name | Type | Allowed tags |
|---|---|---|
| `imperator.evidence.import.lines` | Counter | `outcome=accepted|rejected` |
| `imperator.connector.sync` | Timer | `source=github|aws`, bounded `outcome` |
| `imperator.decision.composition` | Timer | bounded `outcome` |
| `imperator.ledger.command` | Timer | bounded `command`, bounded `outcome` |
| `imperator.business.value.projection` | Timer | `outcome=ready|not_ready|error` |
| `imperator.security.authentication.failure` | Counter | `reason=missing_token|invalid_token` |
| `imperator.security.authorization.denial` | Counter | `role`, `route_id` |
| `imperator.database.operation.failure` | Counter | bounded `operation` |

Allowed command values are `approve`, `reject`, `defer`, `mark_implemented`
and `validate_result`. Allowed role values are the four D087 roles and
`unknown`. Route identifiers are the frozen R01-R16 identifiers, not URL paths.

Connector and operation outcomes must map from existing bounded results. They
must not contain exception types, HTTP messages or provider strings.

Forbidden metric labels include:

- correlation, Evidence, Decision, Recommendation, Ledger, case or command
  UUIDs;
- actor subject, username, email, IP address or user-agent;
- repository, organization, account, Region, URL or source-object names;
- exception class, exception message, error detail or raw status text;
- JWT claims, token metadata, financial values or Evidence content; and
- arbitrary caller-supplied strings.

The focused certification fixture must produce no more than 256 custom
IMPERATOR time series. A changed label set or new custom meter requires a
contract review.

## 10. Health And Readiness Contract

Actuator uses its standard endpoints:

| Endpoint | Meaning | Dependencies |
|---|---|---|
| `/actuator/health/liveness` | Process lifecycle can continue | `livenessState` only |
| `/actuator/health/readiness` | Backend can accept authoritative work | `readinessState`, PostgreSQL `db` |
| `/actuator/prometheus` | Prometheus scrape | In-process metric registry |

The application also exposes `/livez` and `/readyz` on its existing internal
application port so the Docker healthcheck verifies the same server that
handles product requests. These paths are operational endpoints, not additions
to the D086 `/api/v1/**` product API.

Rules:

- liveness must never depend on PostgreSQL, Keycloak, JWKS, GitHub, AWS,
  Prometheus or Grafana;
- readiness includes PostgreSQL connectivity because PostgreSQL is the source
  of truth for every authoritative operation;
- connector disabled, stale or failed state never makes historical reads
  unavailable and therefore is not backend readiness;
- an IdP or JWKS network probe is forbidden; existing cached-key and fail-closed
  D087 behavior remains authoritative;
- missing mandatory configuration must fail startup rather than disclose
  configuration through health;
- health details and component data use `show-details=never` and
  `show-components=never`;
- `UP` returns HTTP 200 and a non-serving readiness state returns HTTP 503;
  and
- the Docker backend healthcheck must use `/readyz`, not a TCP-open check.

## 11. Actuator Exposure And Security

Actuator must run on management port `9090`. The management web exposure
allow-list contains only `health` and `prometheus`. JMX exposure is empty.

All other endpoints, including `env`, `configprops`, `beans`, `mappings`,
`loggers`, `heapdump`, `threaddump`, `shutdown`, `caches`, `conditions`,
`sessions` and `scheduledtasks`, are disabled and inaccessible.

The management port:

- is never published to the host;
- is never proxied by the frontend;
- is scraped only across Docker networking;
- carries no authentication cookie or product JWT; and
- returns no customer or configuration detail.

Because a container port is shared by every network namespace attached to the
backend container, the implementation must not claim that Compose networking
alone makes port `9090` reachable only from Prometheus. Safety instead depends
on the strict endpoint allow-list, data-minimized metrics and absence of host
publication. This residual local-container trust boundary must be recorded in
the certification evidence and reviewed again before cloud deployment.

The existing `/api/v1/**` D087/D088 security chain remains unchanged. Health
and scrape access cannot authenticate a product caller or bypass RBAC.

## 12. Optional Compose Observability Profile

The future implementation may add an `observability` profile containing:

```text
backend:9090
    -> Prometheus
        -> Grafana
```

Profile rules:

- the base Compose runtime starts and serves the product without the profile;
- Prometheus and Grafana never become dependencies of backend, frontend,
  PostgreSQL, Flyway or permission provisioning;
- the profile adds one `observability-internal` network;
- Prometheus is not published to the host and has no provider egress;
- Grafana is the only new host publication and binds to
  `127.0.0.1:${IMPERATOR_GRAFANA_PORT:-3001}`;
- Grafana anonymous access, self-registration and external plugins are
  disabled;
- the Grafana administrator password comes from a file-backed Docker secret;
- provisioning files contain no secret and are mounted read-only;
- both services use read-only root filesystems, non-root users, dropped
  capabilities, `no-new-privileges`, resource limits and bounded logs where
  supported by their official images; and
- every image is an exact version plus `linux/amd64` digest; `latest` is
  forbidden.

Stopping or corrupting either observability service must leave the certified
product flow available. Starting the profile with invalid monitoring
configuration must fail the affected monitoring service without changing
product data.

## 13. Retention And Data Lifecycle

| Data | Retention | Persistence |
|---|---|---|
| Container stdout/stderr | Existing `10m` x three files | Docker-managed only |
| Prometheus samples | Seven days or 256 MiB, whichever occurs first | Dedicated local named volume |
| Grafana dashboards and data sources | Source-controlled provisioning | Runtime state is disposable |
| Prometheus alert state | Same as Prometheus samples | Not audit evidence |
| Sprint certification evidence | 14 days in CI; ignored `build/d096` locally | Sanitized artifacts only |

Prometheus storage is not backed up and is not a source of truth. Removing the
metrics volume loses telemetry only. PostgreSQL backup/restore remains a later
pilot operations gate and is unaffected.

No telemetry is sent to a third party. Before real customer data, Sprint 4.5
must revisit retention, lawful basis, access, deletion and incident handling.

## 14. Dashboard Contract

Grafana provisions exactly one dashboard named `IMPERATOR Operations` with
these rows:

1. runtime availability, request rate, 5xx ratio and latency;
2. JVM/process state and PostgreSQL-related failures;
3. Evidence import, connector synchronization and composition outcomes;
4. governance command, Ledger failure and Business Value readiness outcomes;
5. authentication failures, authorization denials and active alerts.

The dashboard must not show:

- Decision titles, case identifiers or Evidence text;
- users, subjects, emails, repository names or AWS account data;
- costs, savings, ROI or realized Business Value;
- raw log lines, request bodies or error messages; or
- product controls or approval actions.

It is an operator dashboard and must remain visually and semantically distinct
from the D091 Decision Review Workspace.

## 15. Alert Rule Contract

Prometheus evaluates the following local rules. Alertmanager and external
notification delivery remain deferred.

| Alert | Condition | Window | Severity |
|---|---|---|---|
| `ImperatorBackendUnavailable` | backend scrape `up == 0` | 2 minutes | critical |
| `ImperatorHttp5xxRateHigh` | 5xx ratio > 5% with at least 20 requests | 5 minutes | warning |
| `ImperatorReadLatencyHigh` | read-route p95 > 2 seconds | 10 minutes | warning |
| `ImperatorCommandLatencyHigh` | command-route p95 > 3 seconds | 10 minutes | warning |
| `ImperatorAuthenticationFailureBurst` | at least 10 failures | 5 minutes | warning |
| `ImperatorAuthorizationDenialBurst` | at least 10 denials | 5 minutes | warning |
| `ImperatorLedgerCommandFailure` | at least one failed Ledger command | 5 minutes | critical |
| `ImperatorConnectorSyncFailure` | at least one attempted sync failure | 15 minutes | warning |
| `ImperatorCompositionConflictBurst` | at least three conflict/not-ready outcomes | 10 minutes | warning |
| `ImperatorDatabaseOperationFailure` | at least one database operation failure | 5 minutes | critical |
| `ImperatorJvmHeapHigh` | heap usage > 85% | 10 minutes | warning |

Absence of a connector metric while a connector is disabled is normal and
must not alert. Every rule must include a stable summary and runbook reference
without embedding dynamic customer values.

Read and command latency rules select the exact frozen method and normalized
route-template sets from the HTTP metric. They must not introduce a dynamic
`route_group`, object identifier or raw-path label.

Certification must prove at least one warning and one critical rule enter
pending/firing state and recover using synthetic traffic or a controlled local
failure. No production incident is simulated.

## 16. Failure Isolation

The following invariants are mandatory:

- instrumentation cannot begin, commit, roll back or retry a business
  transaction;
- a metrics or logging exception cannot change an HTTP status or Application
  result;
- a missing Prometheus scrape does not change backend readiness;
- Grafana and Prometheus failure cannot block startup or product requests;
- a database failure changes readiness and authoritative operations continue
  to fail safely under existing contracts;
- alert evaluation cannot invoke a connector, command, Decision transition or
  Ledger write; and
- telemetry replay or duplicate observation cannot create product data.

## 17. Authorized Future File Boundary

After separate implementation authorization, Sprint 4.4 may modify only:

- `pom.xml` for the two managed observability dependencies;
- `backend-java/api/observability/**`;
- narrowly required `backend-java/api/errors/CorrelationIdFilter.java` wiring;
- narrowly required `backend-java/api/security/**` instrumentation for the two
  frozen security counters;
- narrowly required `backend-java/bootstrap/**` configuration and input-port
  decorators for operational endpoints and telemetry;
- `src/test/java/imperator/api/observability/**` and focused existing security
  or correlation tests;
- `infra/docker/compose.yaml` and `infra/docker/.env.example`;
- new `infra/docker/observability/**` configuration and provisioning files;
- one ignored file-backed Grafana secret path;
- a bounded `scripts/verify-observability-runtime.ps1` verifier;
- `.github/workflows/security.yml` only to scan added images and retain a
  sanitized D096 evidence artifact; and
- current status, architecture, security and runbook documentation after the
  implementation evidence exists.

Any change outside that boundary stops implementation for review.

Explicitly forbidden files and semantics include:

- `backend-java/domain/**`;
- business behavior in `backend-java/application/**`;
- `backend-java/ports/**` contracts;
- `database/**`, SQL and Flyway;
- `frontend/**`;
- D080-D095 and all frozen product/architecture contracts;
- D086 route/method pairs and response DTOs;
- D087/D088 JWT claims, issuer, JWKS, role mapping and grants;
- D089/D090 provider calls and Evidence meaning;
- D083 Ledger entry meaning or transaction boundaries; and
- D082 ROI, Recommendation or Business Value calculation.

If safe instrumentation requires changing an Application signature or business
transaction, the sprint stops and requests a separate Contract Fix.

## 18. Test Matrix

| Gate | Required proof |
|---|---|
| Dependency | Only the two managed Java dependencies and two pinned runtime images are added |
| Logging | Every emitted IMPERATOR event is valid one-line ECS JSON with allow-listed fields |
| Redaction | Sentinel tokens, credentials, claims, bodies, Evidence and provider values are absent |
| Correlation | Valid/generated IDs reach response and logs once; MDC is cleared after success and failure |
| Metrics | One authoritative operation produces the expected bounded meter observation |
| Cardinality | Forbidden labels are absent and custom fixture series remain <= 256 |
| Liveness | Process-only state; external dependency failure does not report broken liveness |
| Readiness | PostgreSQL loss produces 503 and recovery returns 200 |
| Security | Only health and Prometheus are exposed; every other Actuator endpoint is inaccessible |
| Network | Management and Prometheus ports are not host-published; Grafana is loopback-only |
| Alerts | One warning and one critical rule fire and recover from synthetic evidence |
| Dashboard | Provisioned dashboard loads without customer data or manual setup |
| Isolation | Stopping Prometheus/Grafana leaves DRC-AOA-001 reads and commands available |
| Regression | Default Java, PostgreSQL, frontend and D092-D095 runtime gates remain green |
| Supply chain | Added final images have zero fixable High/Critical findings and zero secrets |

Tests must inspect actual emitted logs and scrape output. Merely checking bean
existence, configuration text or dashboard JSON is insufficient.

## 19. Certification Sequence

Sprint 4.4 may be declared `CERTIFIED` only after this sequence passes:

```text
frozen D096 inputs
  -> default Java 21 clean verify
  -> PostgreSQL 18.6 integration profile
  -> frontend unchanged regression
  -> base Compose runtime regression
  -> observability-profile build and startup
  -> log/redaction/correlation proof
  -> health/readiness transition proof
  -> metric/cardinality proof
  -> Prometheus config and rule validation
  -> warning and critical alert fire/recovery proof
  -> Grafana provisioning and sanitized screenshot
  -> observability outage isolation
  -> Trivy image and filesystem gates
  -> ignored local `build/d096` evidence manifest
  -> hosted Java CI and Security PASS
```

Evidence must record source commit, image identities, dependency tree,
sanitized sample events, metric names and labels, endpoint matrix, alert state
transitions, dashboard screenshot, scan database metadata and test summaries.
It must contain no environment dump, secret, JWT, private key or customer data.

## 20. Stop Conditions

Stop and request founder/CTO review if implementation would require:

- a new product route, schema object, migration or persisted telemetry record;
- Domain, Application, Port, Ledger, ROI or Business Value changes;
- authentication weakening or a public management endpoint;
- raw logs, unbounded metric labels or customer identifiers;
- a third-party telemetry destination or customer data;
- OpenTelemetry, a log platform, Alertmanager or cloud infrastructure;
- modifying a frozen D080-D095 contract; or
- making observability a product-runtime dependency.

## 21. Gate State

```text
D096 CONTRACT: FROZEN / ACCEPTED
SPRINT 4.4 IMPLEMENTATION: NOT AUTHORIZED
SPRINT 4.5: PENDING
EXTERNAL PILOT IDENTITY: STILL MANDATORY BEFORE SPRINT 4.5
```

## 22. Primary Technical References

- Spring Boot 4.1 structured logging:
  `https://docs.spring.io/spring-boot/reference/features/logging.html`
- Spring Boot Actuator endpoints and probes:
  `https://docs.spring.io/spring-boot/4.1/reference/actuator/endpoints.html`
- Spring Boot Micrometer/Prometheus integration:
  `https://docs.spring.io/spring-boot/reference/actuator/metrics.html`
- Prometheus local retention:
  `https://prometheus.io/docs/prometheus/latest/storage/`
- Grafana Docker secret configuration:
  `https://grafana.com/docs/grafana/latest/setup-grafana/configure-docker/`
