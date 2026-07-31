# 41 - REST Adapter Foundation Closure

Status: **PASS**

Closed: **2026-07-31**

Scope: **Sprint 2.8 - REST Adapter Foundation**

Classification: **Execution certification evidence; not a new semantic or
architecture contract**

## 1. Purpose

This document certifies that the frozen MVP REST route topology and its shared
HTTP infrastructure are fully present and verified.

The certification closes the REST adapter foundation only. It does not claim
that any product endpoint implements functional behavior.

## 2. Authorities

The closure was evaluated against:

- `docs/product/API_SPECIFICATION.md`;
- `docs/architecture/34_MVP_Implementation_Blueprint.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/36_Phase_2_Platform_Foundation_Blueprint.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- D065, D066, D070, D075, D076 and D078 in
  `docs/decisions/14_Decision_Log.md`;
- the accepted implementation at Git baseline `6a85797`.

Frozen contracts 34-40 were not modified by this closure.

## 3. Executive Certification

| Gate | Status |
|---|---|
| REST Route Topology | PASS |
| REST Error Contract | PASS |
| REST Correlation Contract | PASS |
| REST Versioning | PASS |
| REST Route Inventory | PASS |
| HTTP Contract Tests | PASS |
| Business Controllers | PASS |
| Architecture Isolation | PASS |
| REST Adapter Foundation | COMPLETE |
| Functional REST | NOT STARTED |

## 4. Frozen MVP Route Inventory

All 15 routes in the frozen MVP implementation subset are registered under
`/api/v1`.

| ID | Method | Route | Controller | Phase 2 behavior |
|---|---|---|---|---|
| R01 | POST | `/api/v1/evidence/import` | `EvidenceController` | Controlled `501` |
| R02 | GET | `/api/v1/decisions` | `DecisionController` | Controlled `501` |
| R03 | GET | `/api/v1/decisions/{id}` | `DecisionController` | Controlled `501` |
| R04 | GET | `/api/v1/decisions/{id}/timeline` | `DecisionContextController` | Controlled `501` |
| R05 | GET | `/api/v1/decisions/{id}/evidence` | `DecisionContextController` | Controlled `501` |
| R06 | GET | `/api/v1/decisions/{id}/roi` | `DecisionContextController` | Controlled `501` |
| R07 | GET | `/api/v1/recommendations/{id}` | `RecommendationController` | Controlled `501` |
| R08 | GET | `/api/v1/decisions/{id}/ledger` | `LedgerController` | Controlled `501` |
| R09 | POST | `/api/v1/decisions/{id}/ledger/approve` | `LedgerController` | Controlled `501` |
| R10 | POST | `/api/v1/decisions/{id}/ledger/reject` | `LedgerController` | Controlled `501` |
| R11 | POST | `/api/v1/decisions/{id}/ledger/defer` | `LedgerController` | Controlled `501` |
| R12 | POST | `/api/v1/decisions/{id}/ledger/mark-implemented` | `LedgerController` | Controlled `501` |
| R13 | POST | `/api/v1/decisions/{id}/ledger/validate-result` | `LedgerController` | Controlled `501` |
| R14 | GET | `/api/v1/business-value` | `BusinessValueController` | Controlled `501` |
| R15 | GET | `/api/v1/ledger` | `LedgerController` | Controlled `501` |

No route in this inventory invokes Application, Ports, repositories,
PostgreSQL, transactions or business rules.

## 5. Business Controller Inventory

| Controller | Package | Registered routes | Status |
|---|---|---:|---|
| `EvidenceController` | `imperator.api.evidence` | 1 | PASS |
| `DecisionController` | `imperator.api.decisions` | 2 | PASS |
| `DecisionContextController` | `imperator.api.decisions` | 3 | PASS |
| `RecommendationController` | `imperator.api.recommendations` | 1 | PASS |
| `LedgerController` | `imperator.api.ledger` | 7 | PASS |
| `BusinessValueController` | `imperator.api.businessvalue` | 1 | PASS |

Total business controllers: **6**.

Total frozen MVP routes: **15**.

## 6. Shared HTTP Contracts

### 6.1 Error Contract

Every controlled and supported framework error uses exactly:

```text
code
message
correlationId
details
```

`details` is always a non-null JSON object. Error responses expose no stack
trace, path, timestamp, internal exception name or sensitive data.

Certified status behavior:

- `400` - malformed request;
- `404` - unavailable resource or route;
- `405` - unsupported method on a mapped route;
- `500` - unexpected server failure;
- `501` - Phase 2 route shell not implemented.

### 6.2 Correlation Contract

The official header is `X-Correlation-ID`.

Certified behavior:

- one canonical UUID is accepted;
- accepted UUIDs are normalized to lowercase;
- missing, blank, invalid or multi-valued input produces a generated UUID v4;
- the response header equals the body `correlationId`;
- correlation remains an HTTP concern and does not enter the domain;
- no ThreadLocal, MDC, tracing or observability behavior was introduced.

### 6.3 Versioning And Content Negotiation

- all product routes use the `/api/v1` prefix;
- equivalent unversioned routes remain unavailable with `404`;
- unsupported methods return the shared `405` envelope;
- `application/json`, `text/html`, `application/xml` and `*/*` receive the
  same JSON error envelope from route shells;
- the response content type remains `application/json`.

## 7. Negative Route Boundaries

The contract suite confirms that the implementation does not expose broader
conceptual or post-MVP routes, including:

- `GET /api/v1/recommendations`;
- recommendation approve, reject and defer aliases;
- `GET /api/v1/ledger/{entryId}`;
- undefined Business Value detail routes;
- unversioned product routes.

## 8. Verification Evidence

Closure verification command:

```text
mvnw.cmd -o clean verify
```

Verified environment:

| Item | Result |
|---|---|
| Java | Eclipse Adoptium 21.0.12 |
| Maven Wrapper | 3.9.16 |
| Production compilation | 98 source files |
| Test compilation | 10 source files |
| Executable JAR | PASS |
| Build | SUCCESS |

Test evidence:

| Test class | Tests | Result |
|---|---:|---|
| `BusinessValueRouteContractTest` | 6 | PASS |
| `DecisionContextRouteContractTest` | 7 | PASS |
| `DecisionRouteContractTest` | 7 | PASS |
| `ApiErrorContractTest` | 7 | PASS |
| `EvidenceImportRouteContractTest` | 5 | PASS |
| `LedgerCommandRouteContractTest` | 8 | PASS |
| `LedgerReadRouteContractTest` | 6 | PASS |
| `RecommendationRouteContractTest` | 6 | PASS |
| `ImperatorApplicationTest` | 1 | PASS |
| **Total** | **53** | **PASS** |

The eight API contract classes contribute 52 real HTTP tests. The remaining
test certifies the Spring Boot runtime composition root.

The explicit PostgreSQL integration profile was not required or executed for
this REST-only closure. Its previous PostgreSQL 18.2 certification remains
unchanged.

## 9. Explicitly Not Implemented

| Capability | Status | Owning future gate |
|---|---|---|
| Business DTOs | DEFERRED | Phase 3 functional REST |
| Request and path validation | DEFERRED | Phase 3 functional REST |
| Pagination binding and validation | DEFERRED | Phase 3 functional list queries |
| Authentication | NOT STARTED | Sprint 2.9 JWT foundation |
| Authorization | NOT STARTED | Sprint 2.9 RBAC foundation |
| Application wiring | DEFERRED | Phase 3 functional REST |
| Persistence invocation from REST | DEFERRED | Phase 3 functional REST |
| Business logic | DEFERRED | Phase 3 functional implementation |
| ROI calculations | DEFERRED | Phase 3 functional implementation |
| Recommendation generation | DEFERRED | Phase 3 functional implementation |
| Evidence import behavior | DEFERRED | Phase 3 functional implementation |
| Decision execution | DEFERRED | Phase 3 functional implementation |

Pagination is not implemented on `501` route shells because they return no
collection representation. The frozen `page`, `size`, `sort` and `direction`
contract remains mandatory when functional list queries are introduced.

## 10. Architecture And Scope Integrity

| Gate | Status |
|---|---|
| Domain unchanged | PASS |
| Application unchanged | PASS |
| Ports unchanged | PASS |
| PostgreSQL adapters unchanged | PASS |
| Flyway unchanged | PASS |
| Spring configuration unchanged | PASS |
| Dependencies unchanged | PASS |
| Frozen contracts 34-40 unchanged | PASS |
| No business behavior in controllers | PASS |
| ASI | 100% |
| DII | 100% |
| Decision Stability | 100% |

## 11. Closure Status

```text
REST Adapter Foundation
STATUS: PASS

REST Route Topology
PASS

REST Error Contract
PASS

REST Correlation Contract
PASS

REST Versioning
PASS

REST Route Inventory
PASS

HTTP Contract Tests
PASS

Business Controllers
PASS

REST Adapter Foundation
COMPLETE

Functional REST
NOT STARTED
```

Sprint 2.8 is formally complete.

The next authorized Phase 2 gate is Sprint 2.9 - JWT/RBAC Foundation. Its first
implementation micro-sprint requires a separately frozen scope before any
security file is created.
