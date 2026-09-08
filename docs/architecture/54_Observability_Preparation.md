# 54 - Observability Preparation

Status: **SUPERSEDED AS AUTHORITY BY D096 / HISTORICAL PREPARATION**

This document is preserved as the design input inspected by the Sprint 4.4
Contract Gate. The authoritative frozen contract is
`56_Observability_Runtime_Contract.md`. D096 authorizes no implementation.

This document prepares an implementation-grade observability boundary. It
does not authorize Sprint 4.4, dependencies, endpoints, agents, collectors,
dashboards, runtime services or configuration changes.

## 1. Purpose

Define what IMPERATOR must be able to observe before Pilot Readiness while
preserving the product distinction between operational telemetry and the
append-only business Ledger.

The preparation answers:

- which signals are useful for one `DRC-AOA-001` value loop;
- which labels are safe and bounded;
- how correlation propagates;
- what health means;
- which alerts reduce time to diagnosis; and
- what evidence the future Sprint 4.4 must retain.

## 2. Current Baseline

| Existing capability | State |
|---|---|
| HTTP `X-Correlation-ID` validation and response propagation | Implemented and tested |
| Safe four-field API error envelope | Implemented and tested |
| Connector synchronization correlation ID | Present in Application contracts |
| Append-only governance Ledger | Implemented and certified |
| Docker service/process health checks | Certified under D092-D095 |
| Docker `json-file` container log boundary | Certified under D092-D095 |
| Actuator/Micrometer | Absent |
| Prometheus/Grafana | Absent |
| OpenTelemetry traces | Absent |
| Application `/health` and `/ready` routes | Absent by D092 |
| Alerts and operational dashboards | Absent |

This is an intentional gap, not architectural drift. D092 assigns application
observability to a later Sprint 4.4 contract.

## 3. Signal Ownership

| Signal | Purpose | Never substitutes for |
|---|---|---|
| Structured log | Diagnose one operation or failure | Ledger history |
| Metric | Detect rates, latency, saturation and availability | Business Value calculation |
| Trace | Follow one request across boundaries | Domain correlation or idempotency |
| Health | Determine process dependency readiness | Full business workflow proof |
| Ledger | Record immutable business governance facts | Debug logs or infrastructure audit |

No observability component may approve a Decision, calculate ROI, create
Evidence, mutate the Ledger or become a source of business truth.

## 4. Correlation Model

The future implementation should propagate one normalized UUID correlation ID:

```text
Browser or external caller
  -> X-Correlation-ID
  -> REST filter
  -> Application operation
  -> connector request and safe result
  -> logs and trace attributes
  -> response header/error envelope
```

Rules:

- a missing or invalid incoming value is replaced using the certified API rule;
- correlation ID is not Decision ID, Evidence ID, operation ID or case ID;
- idempotency identities remain separate;
- logs may include case ID only where already authorized and non-sensitive;
- metrics never use correlation, object, user, repository or account IDs as
  labels; and
- trace/log propagation into external connectors must not expose credentials.

## 5. Structured Logging Preparation

Minimum fields for application logs:

| Field | Rule |
|---|---|
| `timestamp` | UTC |
| `level` | `INFO`, `WARN` or `ERROR` |
| `event` | Stable bounded event name |
| `module` | Stable component name |
| `correlation_id` | Normalized UUID when request-scoped |
| `outcome` | Bounded safe value |
| `duration_ms` | When the operation has a duration |
| `error_code` | Stable safe IMP code when applicable |

Candidate event families:

- `http.request.completed`;
- `authentication.failed` and `authorization.denied`;
- `evidence.import.completed`;
- `connector.sync.completed`;
- `decision.composition.completed` after D093 implementation;
- `ledger.append.completed`;
- `business_value.projection.completed`;
- `database.operation.failed`; and
- `runtime.readiness.changed`.

Never log tokens, authorization headers, passwords, private keys, full claims,
raw provider payloads, prompts/completions, SQL text with values, Evidence
metadata, confidential summaries or financial request bodies.

## 6. Metrics Preparation

All labels must use bounded enumerations. Object IDs, correlation IDs, case IDs,
user IDs, URLs, repository names, account IDs and exception messages are
forbidden labels.

| Metric family | Suggested dimensions |
|---|---|
| HTTP request count and duration | route template, method, status family |
| Authentication failures | safe reason category |
| Authorization denials | route ID, role, command/read |
| Evidence import accepted/rejected | outcome, bounded rejection category |
| Connector sync count and duration | source type, outcome |
| Connector retry/page count | source type, bounded outcome |
| Decision composition | created/resumed/replayed/not-ready/conflict |
| Recommendation generation | outcome, bounded policy version |
| Ledger append | entry type, outcome |
| Business Value projection | ready/not-ready/error |
| Database operation/transaction | operation family, outcome |
| Runtime/JVM/process | standard bounded runtime metrics |

Monetary values, Evidence counts per customer and user behavior are product
data, not Prometheus labels. Business Value stays in the authorized projection.

## 7. Trace Preparation

OpenTelemetry is a candidate implementation mechanism, not authorized here.
If selected in Sprint 4.4, spans should cover:

- incoming HTTP request;
- Application use case;
- PostgreSQL adapter operation;
- external GitHub/AWS request; and
- frontend server rewrite where technically justified.

Span attributes must be allow-listed and low-cardinality. Token content,
Evidence payloads, SQL parameters and provider responses are forbidden. The
optional Explanation Provider is non-blocking and must remain visibly separate
from deterministic Recommendation/ROI work.

## 8. Health And Readiness Preparation

The future contract should distinguish:

| Probe | Meaning | Candidate dependencies |
|---|---|---|
| Liveness | Process can continue serving | Process/runtime only |
| Readiness | Required local dependencies permit safe service | Database connectivity, schema compatibility, mandatory config |
| Connector status | Last synchronization condition | GitHub/AWS status, never liveness |
| IdP status | Token validation dependency health | Safe issuer/JWKS status, exact behavior to freeze |

Connector unavailability must not make historical Decisions or Ledger reads
unavailable. IdP/JWKS outage must never bypass authentication; whether cached
key availability affects readiness must be frozen against Spring Security's
actual behavior during Sprint 4.4.

## 9. Service Objectives And Alerts

Candidate pilot objectives inherited from D027:

| Operation | Candidate target |
|---|---|
| Prepared workspace/case read | p95 <= 2 seconds |
| Evidence/ROI detail read | p95 <= 2 seconds |
| Approve/reject/defer | p95 <= 3 seconds |
| Mark implementation/validate result | p95 <= 3 seconds |

Recommended alerts for the one-case pilot:

- backend or database not ready;
- sustained API 5xx/error-rate increase;
- repeated authentication failures or authorization denials;
- any Ledger append failure;
- connector synchronization failure or stale required Evidence;
- D093 composition conflict/not-ready burst after implementation;
- Flyway/schema validation failure; and
- persistent volume or container restart instability.

Thresholds, windows and notification routes remain decisions for the Sprint
4.4 contract. Alerting must avoid one alert per object or user.

## 10. Dashboard Preparation

The minimum operational dashboard set is:

1. runtime: service state, HTTP rate/error/latency, JVM and database;
2. evidence/connectors: sync outcome, duration, retries, rejected Evidence and
   freshness;
3. decision flow: composition outcomes, review commands and Ledger failures;
4. security: authentication failures and authorization denials;
5. pilot evidence: E2E timestamps and gate outcomes without customer payloads.

These are operator dashboards. They do not replace the D091 Executive
Decision Workspace or expose business data through Grafana.

## 11. Future Test Matrix

| Test | Required proof |
|---|---|
| Unit | Event/field allow-list, label bounding and sensitive-data redaction |
| API integration | Correlation preserved in logs, headers and errors |
| Security | Tokens, claims and confidential Evidence absent from telemetry |
| Metrics | One increment per operation and no object-ID labels |
| Health | Liveness/readiness change only for frozen dependencies |
| Connector | Failure visible while historical reads remain available |
| Ledger | Business entry persists once; telemetry failure cannot change it |
| Runtime | Collector/dashboard failure cannot break core MVP workflow |

## 12. Evidence Package

Future Sprint 4.4 evidence should record:

- dependency and configuration versions;
- sanitized metric names and bounded label sets;
- sample structured events with synthetic values;
- health transition results;
- alert firing and recovery evidence;
- latency observations for the canonical flow;
- proof of token/secret/payload absence; and
- dashboard screenshots without credentials or customer-sensitive data.

## 13. Questions The Sprint 4.4 Contract Must Freeze

1. Which minimal dependency set implements metrics, health and tracing?
2. Are Prometheus, Grafana and an OpenTelemetry collector all required for the
   pilot, or can the minimum runtime use fewer components?
3. Which exact application probe paths and response disclosure rules apply?
4. How does JWKS cache state affect readiness without weakening D087?
5. Which metric/event names and bounded labels are stable?
6. Which retention, scrape and alert windows are sufficient for one pilot?
7. Which Docker files and ports may Sprint 4.4 modify?
8. Which observability services are internal-only?

Until those answers are frozen, no dependency or runtime service is authorized.

## 14. Preparation Exit

```text
PREPARATION: COMPLETE
SPRINT 4.4 CONTRACT: FROZEN BY D096
SPRINT 4.4 IMPLEMENTATION: NOT AUTHORIZED
RUNTIME CHANGE: NONE
```
