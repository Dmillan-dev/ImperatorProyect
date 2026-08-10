# 51 - Docker Production Runtime Contract

Status: **FROZEN**

Decision authority: **D092 - Docker Production Runtime Contract**

Lifecycle phase: **Phase 3 - First Business Value Loop**

Owning gate: **Sprint 4.3.0 - Docker Production Runtime Contract Freeze**

Implementation gate after acceptance: **Sprint 4.3 - Docker Production Runtime Implementation**

Authorized contract fix (2026-08-10): Docker bridge networks configured with
`internal: true` have no connection to host interfaces, so a frontend attached
only to `application-internal` cannot satisfy the separately frozen loopback
publication requirement. Add the frontend-only, non-internal
`frontend-ingress` bridge. It exists solely for
`127.0.0.1:${IMPERATOR_HTTP_PORT:-3000} -> frontend:3000`; the frontend still
uses `application-internal` for backend transport, and the existing
`provider-egress` remains backend-only. This correction changes no product,
API, identity, provider or deployment behavior.

## 1. Purpose

This document freezes the implementation-grade contract for packaging the
currently certified IMPERATOR MVP as a reproducible, isolated and
production-like local Docker Compose runtime.

The runtime answers one operational question:

> Can the exact certified IMPERATOR MVP be built from a clean checkout and run
> as isolated containers without changing its product behavior?

Sprint 4.3 is a runtime and packaging sprint. It adds no product capability,
business rule, route, schema, connector behavior, identity model or deployment
platform.

## 2. Contract-Freeze Baseline

Sprint 4.3.0 began from:

| Check | Verified value |
|---|---|
| Branch | `main` |
| Working tree | Clean |
| Baseline commit | `bdb2bf822664bb948bb82f525f5d21bbdf8efeb0` |
| Sprint 4.2 | CERTIFIED / COMPLETE |
| Sprint 4.2.1 | COMPLETE |
| Java default tests | 139 passed, 0 failed |
| PostgreSQL integration tests | 31 passed, 0 failed |
| PostgreSQL | 18.4 PASS under the 18.x, 18.2-or-later gate |
| Flyway migrate/validate/idempotency | PASS |
| Frontend tests | 35 passed, 0 failed |
| Frontend production build and Playwright | PASS |
| Local Docker Engine | Not installed; implementation precondition |
| Local Docker Compose | Not installed; implementation precondition |
| Existing `infra/` | Documentation boundary only |

Docker absence does not block this documentation-only contract freeze. It
does block Sprint 4.3 implementation and certification until a supported
Docker Engine or Docker Desktop with Docker Compose v2 and Linux-container
support is available.

SHA-256 hashes recorded before Document 51 and D092 were created:

| Authority or certified artifact | SHA-256 |
|---|---|
| `docs/project/PROJECT_STATUS.md` | `11D17D4C3FF13DF6D2FA36DECDFFE9CDD71CD0CBD1885161045DDE9DF1B08379` |
| `docs/project/PHASE_AND_SPRINT_MAP.md` | `892E171BF375EF07DCD38F2438260F534182BFA7048AFFA80548B43F1E98BD9A` |
| `docs/decisions/14_Decision_Log.md` | `FC37D43C733F6B79260204906A72C453B76ECADC0AC7581960F814A41858ACF7` |
| `docs/architecture/27_Quality_Attributes.md` | `4FA936E16AB00BBBEA56C7B53AFD8A7C1AAD79571CAB6D183F4C626F47ED6F8E` |
| `docs/architecture/31_MVP_Implementation_Standard.md` | `472FFEC46059D4DB36887521402BAC5A274BA58B5A3BC92C44CF82FA243AAEFE` |
| `docs/architecture/35_Coding_Principles.md` | `5BF0B51B18E5ABEAFD2F17A4BD50B1566A085228EED52E9CB5A65E61CC0EA115` |
| `docs/architecture/37_Implementation_Contract.md` | `85182BBAE6DF3CB2154F32484CD94382B84AB166DEA28D1306FC65B90DB7BA29` |
| `docs/architecture/45_Functional_REST_Application_Contract.md` | `65F78475C1683BA5464932AFED919F431550B32EAA705347E75E198111FB5F3A` |
| `docs/architecture/46_JWT_Authentication_Contract.md` | `DE4CDD04505DB7D0DA5ECDA574DF79D0E4495EC2E06595B6DCF99A495AA940E3` |
| `docs/architecture/47_RBAC_Authorization_Contract.md` | `15E9204AF809E0C13B0F8EF307BD93667699C300F9ED9FD48D12CA6DBB274F2D` |
| `docs/architecture/48_GitHub_Integration_Contract.md` | `05ABA607C0CA3E3B6448EAE528A8333B5DC376762065804BE3A91B8A4F0C693D` |
| `docs/architecture/49_AWS_Integration_Contract.md` | `C73FE88E0427600B18F8EF6EB071B5322E18F5D06C1F18B40BBB0FBEA6B6C141` |
| `docs/architecture/50_Executive_Dashboard_Contract.md` | `424FFD376A8C7DB63D075A8E5478A58AD106C65D7D406D3458CCDAB8EC8F2CD7` |
| `pom.xml` | `23C9411E52B7CF3730175F3C0195E55754318758630321F0FD4F0E9DBD6F25E2` |
| `database/migrations/V1__initial_schema.sql` | `A8270BEA7E5ED2AE7143BFE7B1D66B8FDF04A8CE22D13020F6F3C0C1DD772775` |
| `frontend/package.json` | `B78A55638DF3C5547202860EE1F371741843964F5330F885D3850188D07CB32B` |
| `frontend/package-lock.json` | `7075DEF09F624611E902A7C87D7432C805081B987D08E8A8EF66FA8FA52C2C6B` |
| `agents/phase3/README.md` | `77920D286ABE0358E4C24DA62C6FC71474E15EF831D040BC9E5CDD9DFA6522FB` |

## 3. Authority And Precedence

Implementation must read authorities in this order:

1. D085 through D092 in `docs/decisions/14_Decision_Log.md`;
2. this document;
3. Documents 45 through 50;
4. `docs/architecture/27_Quality_Attributes.md`;
5. Documents 31, 35 and 37;
6. `docs/architecture/26_Security_Data_Governance_Threat_Model.md`;
7. the certified implementation; and
8. current project-control documentation.

D086 remains the REST authority. D087 remains the authentication authority.
D088 remains the route authorization and Evidence-redaction authority. D089
and D090 remain the connector authorities. D091 remains the product and
frontend authority except for the single internal transport clarification in
Section 12.

## 4. Frozen Runtime Boundary

The authorized runtime is exactly:

```text
Clean source checkout
-> immutable third-party base images
-> reproducible backend and frontend image builds
-> Docker Compose
   -> PostgreSQL 18.4
   -> Flyway 13.0.0 migration and validation jobs
   -> least-privilege database grant job
   -> Java 21 backend
   -> Next.js Decision Review Workspace
-> loopback-only browser access
```

The images contain only the already-certified behavior:

```text
PostgreSQL Evidence and Decision Ledger
-> Java Domain/Application/Ports/Adapters
-> D086 REST API
-> D087 JWT and D088 RBAC
-> D089 GitHub and D090 AWS adapters, disabled by default
-> D091 Decision Review Workspace
```

Packaging cannot create or alter Evidence, Decision, Recommendation, ROI,
review, Ledger or Business Value semantics.

## 5. Runtime Services

The Compose project name is exactly `imperator`.

| Service | Lifecycle | Purpose | Must never do |
|---|---|---|---|
| `postgres` | Long-running | Own PostgreSQL 18.4 and its persistent volume | Publish a host port, run product logic or accept trust authentication |
| `flyway-migrate` | One-shot | Apply existing immutable migrations as owner | Run `clean`, `repair`, automatic baseline or edit migrations |
| `flyway-validate` | One-shot | Validate applied migrations after migrate | Mutate schema or suppress checksum failures |
| `postgres-permissions` | One-shot | Create/update the runtime role and exact grants | Change schema, seed data or grant ownership/superuser rights |
| `backend` | Long-running | Run the existing Spring Boot JAR on `8080` | Migrate schema, mint tokens, expose PostgreSQL or add routes |
| `frontend` | Long-running | Run the Next.js standalone server on `3000` | Become security authority, contact providers or expose backend credentials |

No other runtime service is authorized.

Startup order is mandatory:

```text
postgres healthy
-> flyway-migrate completes
-> flyway-validate completes
-> postgres-permissions completes
-> backend starts
-> frontend starts
```

Failure of any mandatory predecessor prevents its dependent service from being
ready. No job may fail open.

## 6. Explicitly Excluded Services

Sprint 4.3 must not add a container for:

- Python or FastAPI;
- OpenAI, Claude, Ollama, Gemini or another AI provider;
- Redis, Kafka, RabbitMQ or another broker/cache;
- PgAdmin, Adminer or another database UI;
- Keycloak, OAuth server, token minting or mock IdP;
- Nginx, Caddy, Traefik or another ingress proxy;
- Prometheus, Grafana, Loki, Tempo or OpenTelemetry Collector;
- GitHub, AWS or provider emulators;
- a scheduler or background worker;
- backup, restore, high availability or replication; or
- Kubernetes, Terraform or cloud deployment tooling.

Observability belongs to Sprint 4.4. Pilot identity, TLS termination, backup
and public deployment belong to separately authorized later gates.

## 7. Runtime Image Freeze

| Use | Approved image/tag |
|---|---|
| Backend build | `eclipse-temurin:21-jdk-jammy` |
| Backend runtime | `eclipse-temurin:21-jre-jammy` |
| Frontend build/runtime | `node:24.18.0-bookworm-slim` |
| Database | `postgres:18.4-bookworm` |
| Migration CLI | `flyway/flyway:13.0.0` |

Rules:

- every `FROM` and third-party Compose `image` includes an immutable OCI
  digest resolved during implementation;
- the human-readable semantic tag remains beside the digest;
- `latest`, floating major-only tags and unpinned references are forbidden;
- the certified target platform is `linux/amd64` only;
- changing an image family or frozen runtime lane requires a Contract Fix;
- custom images are tagged with the current Git SHA, never `latest`;
- OCI source, revision, title and version labels contain no secrets or dynamic
  build timestamp; and
- source lockfiles and the Maven Wrapper remain authoritative.

The backend build uses committed `mvnw`, Maven 3.9.16, Java 21 and the root
`pom.xml`. The frontend uses `npm ci` and the committed lockfile. Neither build
may mutate a manifest or lockfile.

This contract requires input-reproducible builds from pinned sources. It does
not claim byte-identical output across Docker engines or CPU architectures.

## 8. Build And Runtime Image Content

### 8.1 Backend

The backend uses a multi-stage build:

1. build the executable JAR with the committed Maven Wrapper;
2. copy only the JAR and runtime entrypoint into the final JRE image; and
3. run as a dedicated non-root numeric user.

The final image contains no Maven, source, tests, `.git`, credentials,
migrations, npm artifacts or JDK.

### 8.2 Frontend

The frontend uses a multi-stage build and Next.js `output: "standalone"`.
Only the standalone server, `.next/static`, present `public` assets and a
minimal Node runtime enter the final image. It runs as the official non-root
`node` user and contains no development dependencies, Playwright browsers,
source maps, npm credentials or repository.

Setting standalone output is an authorized packaging-only change to
`frontend/next.config.ts`. It cannot change a route, screen, schema, action
or business behavior.

### 8.3 Build contexts

- backend context: repository root, constrained by root `.dockerignore`;
- frontend context: `frontend/`, constrained by its `.dockerignore`; and
- neither context includes `.git`, secrets, `.env`, reports, local database
  data, `target`, `.next` or `node_modules`.

## 9. Compose File Contract

The canonical entrypoint is `infra/docker/compose.yaml`. It uses the current
Compose Specification without the obsolete top-level `version` key. Legacy
`docker-compose` v1 is unsupported.

It must:

- use long-form dependency conditions for health and one-shot completion;
- use named resources without `container_name`;
- reject missing mandatory values;
- use restart `unless-stopped` only for long-running services;
- use restart `no` for one-shot jobs;
- allow 30 seconds for graceful backend/frontend stop;
- rotate logs at 10 MiB with three files per service;
- define CPU, memory and PID ceilings; and
- pass `docker compose config --quiet` without exposing secrets.

Frozen MVP ceilings:

| Service class | CPU | Memory | PIDs |
|---|---:|---:|---:|
| `postgres` | 1.0 | 1024 MiB | 200 |
| `backend` | 1.0 | 1024 MiB | 160 |
| `frontend` | 0.5 | 512 MiB | 100 |
| each one-shot job | 0.5 | 512 MiB | 100 |

These are safety ceilings, not performance guarantees.

## 10. Network And Exposure Contract

Exactly four user-defined bridge networks are authorized:

| Network | Members | Egress | Purpose |
|---|---|---|---|
| `application-internal` | frontend, backend | No | Frontend-to-backend transport |
| `data-internal` | backend, postgres, database jobs | No | Database isolation |
| `provider-egress` | backend only | Yes | HTTPS JWKS and optional providers |
| `frontend-ingress` | frontend only | Yes | Loopback host publication |

The first two use `internal: true`. `provider-egress` publishes no port.
`frontend-ingress` is a standard bridge because Docker cannot publish a port
from a container attached only to internal networks. It binds only the frozen
frontend loopback port, carries no frontend-to-backend traffic and exposes no
other service. The frontend receives no runtime secret or provider credential;
its application behavior continues to address only the backend over
`application-internal`.

Host exposure is exactly:

```text
127.0.0.1:${IMPERATOR_HTTP_PORT:-3000} -> frontend:3000
```

PostgreSQL `5432` and backend `8080` are never published. Binding to
`0.0.0.0`, LAN/public interfaces or host networking is forbidden. No service
uses privileged mode, host PID/IPC or the Docker socket.

This is a local production-like runtime, not production deployment authority.

## 11. Browser And API Path

```text
Browser http://127.0.0.1:3000/api/v1/**
-> Next.js same-origin rewrite
-> http://backend:8080/api/v1/** on application-internal
-> D087 authentication
-> D088 authorization
-> existing D086 REST adapter
```

No CORS change is authorized. The backend hostname is never browser-visible and
no backend URL enters client-side JavaScript.

## 12. Narrow D091 Internal-Transport Clarification

D091 requires HTTPS outside local loopback. Docker service discovery cannot
address a separate backend container through the frontend's loopback.

D092 narrowly authorizes:

```text
IMPERATOR_API_ORIGIN=http://backend:8080
```

only when:

- `IMPERATOR_RUNTIME_PROFILE=docker-compose`;
- hostname is exactly `backend`, port `8080`, scheme `http`;
- the origin has no credentials, path, query or fragment;
- both services share `application-internal`;
- that network is internal; and
- port `8080` is not published.

`frontend/next.config.ts` may recognize only that exact tuple and standalone
output. Every other non-loopback origin still requires HTTPS. A general
`ALLOW_INSECURE_HTTP`, wildcard or disabled validation is forbidden.

This changes packaging transport only, not D087, D088 or same-origin browser
behavior.

## 13. PostgreSQL And Persistence

| Setting | Frozen value |
|---|---|
| Image | PostgreSQL `18.4-bookworm`, digest-pinned |
| Database | `imperator` |
| Migration owner | `imperator_owner` |
| Runtime role | `imperator_app` |
| Host authentication | SCRAM-SHA-256; `trust` forbidden |
| Volume | `postgres-data` at `/var/lib/postgresql` |
| JDBC URL | `jdbc:postgresql://postgres:5432/imperator` |

The parent volume path is mandatory for PostgreSQL 18 official-image layout.
Anonymous database volumes are forbidden.

Normal stop, recreation and rebuild preserve the volume. `docker compose down`
preserves data unless `--volumes` is explicitly used. No normal script may
delete it. Startup inserts no seed, fixture, demo Decision or customer data.
V1 remains the only schema migration absent a later schema decision.

Backup, recovery, replication and major-version upgrade remain out of scope.

## 14. Migration And Database Permissions

Flyway uses 13.0.0, the owner role and a read-only mount of
`database/migrations/`. Migrate follows database health; validate follows
migrate; backend follows validate and permissions.

`clean`, `repair`, automatic baseline, ignored checksum failure and
out-of-order migration are forbidden. Owner password cannot appear on a
command line.

The idempotent permission job may create/rotate only `imperator_app` and grant:

| Object | Privileges |
|---|---|
| database `imperator` | `CONNECT` |
| schema `public` | `USAGE` |
| `evidence` | `SELECT`, `INSERT` |
| `decisions` | `SELECT`, `INSERT`, `UPDATE` |
| `decision_evidence` | `SELECT`, `INSERT`, `DELETE` |
| `recommendations` | `SELECT`, `INSERT` |
| `recommendation_evidence` | `SELECT`, `INSERT` |
| `ledger_entries` | `SELECT`, `INSERT` |
| `ledger_evidence_snapshots` | `SELECT`, `INSERT` |

It revokes existing table privileges before granting. It cannot grant
`CREATE`, `TRUNCATE`, ownership, role administration, superuser, replication,
bypass-RLS or update/delete on append-only history.

The permission script is infrastructure under `infra/docker/`, not a Flyway
migration or domain schema. It must safely quote fixed values and pass reruns.

## 15. Configuration

Mandatory non-secret settings:

| Setting | Rule |
|---|---|
| `IMPERATOR_HTTP_PORT` | Optional loopback port; default `3000` |
| JWT issuer URI | Required external D087 HTTPS issuer |
| JWT JWKS URI | Required external D087 HTTPS JWKS |
| JWT audience | Fixed `imperator-api` |
| JWT algorithm | Fixed `RS256` |
| PostgreSQL enabled | Fixed `true` |
| PostgreSQL URL/user | Section 13 internal URL and `imperator_app` |
| frontend API origin | Section 12 exact internal origin |

Missing JWT values or secret sources fail configuration. There is no fallback
to trusted headers, anonymous API, memory or default password.

Only an `.env.example` with safe placeholders may be committed. It contains
no working issuer, customer identifier, token, password, private key or copied
local path.

## 16. Secret Handling

Compose file-backed secrets are mandatory. Actual files live under ignored
`infra/docker/secrets/` and mount read-only only where needed.

| Secret | Allowed readers |
|---|---|
| `postgres-owner-password` | postgres, Flyway jobs, permission job |
| `postgres-app-password` | permission job, backend |
| `github-token` | backend only, optional overlay |
| `aws-credentials` | backend only, optional overlay |

PostgreSQL uses its `_FILE` convention. Narrow entrypoint wrappers may read a
secret and export the existing application/Flyway variable immediately before
`exec`. They cannot print, recopy, command-line-pass or persist the value.

Secrets are forbidden in Dockerfiles, build args, labels, Compose literals,
committed environment files, browser variables, image history, logs and test
reports. Private JWT signing material never enters IMPERATOR.

Compose secrets are not a production secret manager. Managed rotation remains
deferred.

## 17. Authentication Runtime

IMPERATOR remains a Resource Server only. Docker packages no Authorization
Server, login UI, token issuer, private key or test IdP.

The operator supplies D087 HTTPS issuer/JWKS values and obtains a short-lived
RS256 token externally. The frontend retains it only in volatile memory. The
backend alone has JWKS egress; failure remains fail-closed.

## 18. Connector Preservation

D089 GitHub and D090 AWS code is present in the backend image, but both are
disabled in the base runtime.

An optional `infra/docker/compose.connectors.yaml` may only:

- mount `github-token` and supply the frozen organization/repository;
- mount short-lived standard AWS shared credentials and supply the frozen
  expected account/Region; and
- enable existing connector properties.

It cannot add a route, scheduler, startup sync, worker, webhook, emulator,
selector or permission. Incomplete configuration fails closed.

The certified application has no public connector trigger. Docker preserves
the adapters but does not invent autonomous invocation.

## 19. Container Hardening

Custom backend/frontend containers must:

- run as non-root numeric users;
- drop all Linux capabilities;
- set `no-new-privileges`;
- use read-only root filesystems;
- use bounded `tmpfs` only where required;
- expose no debug port; and
- receive only required networks, secrets and files.

Database/jobs use official entrypoints with least privilege, no privileged
mode or broad added capabilities, read-only migrations and only the named
database volume for persistent writes. Any official bootstrap starting as root
must drop to its documented service user before serving.

## 20. Browser Security Headers

The frontend may add only packaging-level protections:

- `X-Content-Type-Options: nosniff`;
- `Referrer-Policy: no-referrer`;
- `X-Frame-Options: DENY`;
- `Cross-Origin-Opener-Policy: same-origin`;
- `Permissions-Policy: camera=(), microphone=(), geolocation=()`; and
- this exact Content Security Policy:

```text
default-src 'self'; base-uri 'self'; object-src 'none';
frame-ancestors 'none'; form-action 'self';
script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';
img-src 'self' data:; font-src 'self'; connect-src 'self'
```

The two inline allowances are limited to the current Next.js runtime. Remote
scripts, fonts, analytics, images and provider origins remain forbidden. HSTS
is forbidden over loopback HTTP and awaits real TLS.

## 21. Health, Startup And Shutdown

Sprint 4.3 adds no `/health`, `/ready`, Actuator or API route. Those belong
to the separate Sprint 4.4 observability gate.

| Component | Check |
|---|---|
| PostgreSQL | `pg_isready` for `imperator` as owner |
| Flyway jobs | process exits `0` |
| permission job | grants verified and process exits `0` |
| backend | Java alive and TCP loopback listener `8080` reachable |
| frontend | Node fetch to loopback `3000` returns HTTP |

Backend TCP health is liveness, not business readiness. Certification also
requires an unauthenticated request through the frontend proxy to return D087's
safe `401`.

SIGTERM reaches Java/Node as PID 1 with a 30-second grace period. Shutdown
cannot delete the volume or mutate Ledger history.

## 22. Logging And Observability Boundary

Services log to stdout/stderr with Docker rotation. Logs may include lifecycle,
migration version, HTTP status, correlation ID and bounded category. They never
include tokens, credential values, request/response bodies, raw Evidence,
Authorization headers, credential files or full provider errors.

Sprint 4.3 adds no metrics, traces, dashboard, collector or alerting and cannot
claim Sprint 4.4 completion.

## 23. Failure Semantics

| Failure | Required behavior |
|---|---|
| Docker/Compose absent | Implementation BLOCKED; no speculative changes |
| Secret absent | Dependent service does not start |
| PostgreSQL unhealthy | Migration/backend do not start |
| Migration failure | Validate/backend do not start; no repair/clean |
| Permission failure | Backend does not start |
| Backend crash | Frontend shows existing API failure; no fabricated data |
| Frontend crash | Backend/database stay internal |
| JWKS unavailable | Protected API fails closed |
| GitHub/AWS unavailable | Persisted workspace remains readable; no fabricated Evidence |
| Container restart | Volume and Ledger remain intact |
| Port collision | Explicit failure; no random/public port |

The runtime never substitutes memory, bypasses JWT, skips validation or enables
connectors as recovery.

## 24. Implementation File Boundary

Sprint 4.3 may create or modify only:

```text
.dockerignore
.gitignore                         # secret/runtime exclusions only
frontend/.dockerignore
frontend/next.config.ts            # Sections 8, 12 and 20 only
infra/README.md
infra/docker/**
scripts/verify-docker-runtime.ps1
```

Focused tests for authorized Next.js configuration may be added inside
`frontend/` only if needed. Verification scripts cannot implement product
behavior.

It must not modify:

- Java source/tests, `pom.xml`, Maven Wrapper or dependencies;
- Domain, Application, Ports, adapters or REST contracts;
- JWT/RBAC or GitHub/AWS behavior;
- `database/migrations/` or V1;
- frontend product components, schemas, actions or presentation;
- `package.json` or `package-lock.json`;
- D001-D092 or frozen contracts; or
- project-control/AI context before Sprint 4.3.1.

Any objectively required file outside this list stops for Contract Fix.

## 25. Implementation Sequence

1. Verify clean baseline, Docker, Compose v2, Linux containers and resources.
2. Resolve and record immutable image digests.
3. Create ignore boundaries.
4. Build and inspect backend image.
5. Enable standalone packaging and build/inspect frontend image.
6. Create database, Flyway and least-privilege assets.
7. Create networks, secrets, volume and dependency graph.
8. Add the disabled-by-default connector overlay.
9. Add bounded runtime verification.
10. Run unchanged application regressions.
11. Run Docker runtime/security certification.
12. Stop without commit, push or Sprint 4.3.1.

No step may begin Sprint 4.4.

## 26. Certification Contract

Certification starts from a clean checkout and clean worktree after
implementation.

### 26.1 Tool preflight

```text
docker --version
docker compose version
docker info
```

The engine must run Linux containers and Compose must be v2.

### 26.2 Unchanged application regression

```text
mvnw.cmd clean verify
mvnw.cmd -Ppostgresql-integration clean verify

cd frontend
npm ci
npm run format:check
npm run lint
npm run typecheck
npm run test:coverage
npm run build
npm run test:e2e
npm audit --audit-level=moderate
```

Baseline remains 139 default Java, 31 PostgreSQL integration and 35 frontend
tests. A focused packaging test may increase frontend count; no existing test
may be weakened or deleted.

### 26.3 Compose and image certification

The gate proves:

- valid Compose with no secret value;
- digest-pinned third-party images and `FROM` lines;
- clean backend/frontend image builds;
- no source, tests, build tools, credentials or caches in final images;
- non-root custom runtimes and active hardening;
- no fixable Critical image vulnerability without formal NO-GO;
- only loopback frontend port published;
- PostgreSQL 18.4 internal reachability;
- Flyway migrate/validate and second no-op migrate PASS;
- idempotent permission job and exact grants;
- append-only update/delete denied to `imperator_app`;
- all defined service health checks PASS;
- proxy request without JWT returns safe `401`;
- valid external D087 token can load the workspace after certified data import
  through existing APIs;
- restart preserves schema history and imported MVP data;
- connectors disabled by default make zero provider calls;
- no secret in logs, history or rendered Compose;
- recreation without `--volumes` preserves data; and
- teardown leaves no unexpected port or orphan.

No live GitHub/AWS call is required. Existing offline/PostgreSQL suites remain
authoritative.

### 26.4 Required report

Record Git SHA, Docker/Compose versions, image digests, builds, test totals,
Flyway results, service health, ports, persistence, secret/hardening checks,
all Guardian results, ASI, DII, Decision Stability and deviations.

## 27. Explicitly Out Of Scope

- new product capability, recommendation family or business behavior;
- new route, connector trigger, scheduler, schema or migration;
- live provider certification;
- OAuth login, SSO, IdP or persistent browser session;
- public/LAN/cloud exposure and TLS termination;
- production secret manager or rotation;
- backup, restore, replication, HA or database major upgrade;
- scaling or multiple backend/frontend instances;
- multi-account, multi-repository or multi-tenant behavior;
- Kubernetes, Helm, Terraform or cloud infrastructure;
- CI image publication or registry deployment;
- Prometheus, Grafana, OpenTelemetry or centralized logs;
- Python/FastAPI/AI runtime; and
- Pilot Readiness or real customer data.

## 28. Normative External References

External references establish tool behavior only:

- [Docker Compose file reference](https://docs.docker.com/reference/compose-file/)
- [Compose service dependencies and hardening](https://docs.docker.com/reference/compose-file/services/)
- [Compose networking](https://docs.docker.com/compose/how-tos/networking/)
- [Compose secrets](https://docs.docker.com/compose/how-tos/use-secrets/)
- [PostgreSQL Official Image](https://hub.docker.com/_/postgres)
- [Flyway Docker](https://documentation.red-gate.com/flyway/reference/usage/flyway-docker)
- [Next.js standalone output](https://nextjs.org/docs/app/api-reference/config/next-config-js/output)
- [Eclipse Temurin Official Image](https://hub.docker.com/_/eclipse-temurin)
- [Node Official Image](https://hub.docker.com/_/node)

## 29. Sprint 4.3.0 Acceptance Gate

Sprint 4.3.0 is accepted only when:

- this is the sole new architecture document;
- D092 is the sole Decision Log append;
- D001-D091 are unchanged;
- the runtime packages only the certified MVP;
- services, images, files, networks, ports, volume, migrations, grants,
  secrets, hardening, health and failures are unambiguous;
- D091 internal transport is reconciled without broad insecure HTTP;
- Docker absence is recorded as implementation precondition;
- no executable/runtime file is created or modified;
- local Markdown links are valid;
- `git diff --check` passes;
- all five Guardian roles PASS;
- ASI is 100%;
- DII is 100%; and
- Decision Stability is 100%.

After acceptance, the sole next gate is Sprint 4.3 implementation. Sprint 4.3.1
may synchronize documentation only after implementation and certification.
Sprint 4.4 remains blocked until Sprint 4.3.1 completes.
