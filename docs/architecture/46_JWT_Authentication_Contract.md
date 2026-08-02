# 46 - JWT Authentication Contract

Status: **FROZEN**

Decision authority: **D087 - JWT Authentication Contract**

Owning gate: **Sprint 3.8 - JWT Authentication**

## 1. Purpose

This document freezes the authentication boundary for the certified IMPERATOR
Functional REST API before Sprint 3.8 changes Java or dependencies.

Sprint 3.8 answers one question only:

```text
Who is the caller?
```

Sprint 3.9 owns the separate question:

```text
What may the authenticated caller do?
```

No implementation decision required by Sprint 3.8 remains open after this
contract.

## 2. Authorities And Narrow Supersession

This contract follows:

- `docs/architecture/26_Security_Data_Governance_Threat_Model.md`;
- `docs/product/28_Identity_Access_Approval_Model.md`;
- `docs/architecture/35_Coding_Principles.md`;
- `docs/architecture/37_Implementation_Contract.md`;
- `docs/architecture/45_Functional_REST_Application_Contract.md`;
- D083, D085 and D086 in `docs/decisions/14_Decision_Log.md`; and
- the certified Sprint 3.7 implementation.

D087 and this document supersede only the temporary trusted-actor mechanism
defined by D086. Every D086 route, DTO, status, idempotency, Application
boundary, transaction boundary, error-envelope field and correlation rule
remains frozen.

The schema remains the frozen single-tenant V1 schema. The future tenant
direction in the threat model does not authorize an `organization_id`, tenant
claim, tenant filter or identity table in Sprint 3.8.

## 3. Authentication Architecture

IMPERATOR is an OAuth2 Resource Server that validates JWT access tokens issued
by an external authority.

Sprint 3.8 must use Spring Security OAuth2 Resource Server support. It must not
implement a custom JWT parser, custom Bearer filter, signature algorithm,
cryptographic primitive, JWK client or token cache.

The authenticated request flow is:

```text
Authorization: Bearer <JWT>
        |
        v
Spring Security Resource Server
        |
        v
Signature + issuer + audience + time + claim validation
        |
        v
Authenticated JWT principal
        |
        v
API security actor mapper
        |
        v
UserId + active MVP role
        |
        v
Existing REST mapper -> existing Application input port
```

Spring Security types may exist only in the API/security and bootstrap adapter
boundary. Domain, Application and Ports must not depend on Spring Security,
Servlet, OAuth2, JWT or JOSE types.

## 4. Resource Server Only

Sprint 3.8 does not make IMPERATOR an identity provider.

Explicitly forbidden:

- Authorization Server;
- login endpoint or page;
- user registration;
- user, credential or session persistence;
- password handling;
- access-token or refresh-token issuance;
- token refresh;
- OAuth2 client login;
- provider-specific identity integration;
- SSO, SCIM or directory synchronization; and
- token revocation or deny-list infrastructure.

IMPERATOR consumes authenticated identity. It does not create identity.

## 5. Dependency Contract

Sprint 3.8 may add exactly these Spring-managed dependencies:

- `org.springframework.boot:spring-boot-starter-oauth2-resource-server`;
- `org.springframework.security:spring-security-test`, test scope only, if used
  by the HTTP security tests.

Versions remain managed by the existing Spring Boot dependency BOM. No direct
version is added for either dependency.

Forbidden JWT or crypto dependencies include JJWT, Auth0 JWT, direct Nimbus
configuration as a separate application library, or any second JWT stack. The
Nimbus implementation transitively managed by Spring Security is an internal
framework detail and must not become an IMPERATOR abstraction.

## 6. Protected HTTP Surface

Every request under `/api/v1/**` requires a valid Bearer JWT, including all 15
routes frozen by D086.

There is no public `/api/v1` route in Sprint 3.8. No health, login, token,
metadata or discovery endpoint may be added.

Namespace behavior is frozen:

| Request | Result |
|---|---|
| Existing `/api/v1/**` route without a token | `401` |
| Unknown `/api/v1/**` route without a token | `401` |
| Unknown `/api/v1/**` route with a valid token | Existing `404` envelope |
| Unversioned or non-API route | Existing behavior, including `404` |
| Existing route with a valid token | Existing D086 behavior |

No CORS policy or anonymous preflight exception is introduced. CORS remains
deferred until its owning product surface requires an explicit contract.

## 7. Bearer Token Transport

The only accepted credential transport is exactly one HTTP header:

```text
Authorization: Bearer <token>
```

Tokens in cookies, query parameters, form fields, request bodies or custom
headers are never accepted.

Missing, blank, malformed or multi-valued Bearer credentials fail closed with
HTTP `401`. Authentication never falls back to remote address, correlation ID,
trusted metadata, a default actor or an anonymous actor.

## 8. JWT Claim Contract

Sprint 3.8 consumes exactly these claims for authentication semantics:

| Claim | Presence | Frozen rule |
|---|---|---|
| `iss` | Required | Exact configured issuer value |
| `aud` | Required | Decoded audience collection contains `imperator-api` |
| `sub` | Required | Canonical lower-case UUID; maps to Domain `UserId` |
| `exp` | Required | Token must not be expired after permitted clock skew |
| `nbf` | Optional | When present, token must be active after permitted clock skew |
| `imperator_role` | Required | One exact upper-case MVP role string |

The accepted `imperator_role` values remain exactly:

```text
ADMIN
PLATFORM_ENGINEER
FINANCE
AUDITOR
```

The claim is one string, not an array. This is the active role used for the
current command and preserves the single-role actor contract already certified
by D083 and D086. Multi-role selection is not invented in Sprint 3.8.

Additional token claims may exist, but they are ignored and confer no identity,
role, tenant, route or business authority. Sprint 3.8 assigns no semantic
meaning to `scope`, `groups`, `authorities`, `email`, `name`, `jti` or any
provider-specific claim.

Missing, malformed or unsupported required claims make the token invalid.

## 9. Signature And Key Policy

The MVP accepts only asymmetrically signed RS256 JWTs.

Frozen JOSE rules:

- the JOSE `alg` header must be `RS256`;
- `none`, HMAC and every other algorithm are rejected;
- signature verification uses the configured HTTPS JWKS endpoint;
- the JOSE `kid` header is mandatory;
- the selected `kid` must resolve to an eligible RSA signing key;
- public signing keys are never stored in Domain, Application, PostgreSQL,
  Flyway or source-controlled secrets; and
- private signing keys never enter the IMPERATOR runtime.

Key rotation behavior is frozen:

```text
Known kid
-> use framework-managed JWKS cache
-> verify signature

Unknown kid
-> allow Spring Security's bounded JWKS refresh behavior
-> resolve the refreshed key set
-> verify if the key becomes authoritative
-> otherwise fail closed with 401
```

JWKS caching, cache expiry and refresh mechanics remain framework-managed. No
custom cache, refresh scheduler or cache-duration policy is introduced.
Network, parsing or key-resolution failure never accepts a token and never
falls back to a stale unverified identity.

## 10. Issuer, Audience And Time Validation

Runtime configuration uses Spring Boot Resource Server properties for:

- issuer URI;
- JWKS URI;
- required audience `imperator-api`; and
- allowed JWS algorithm `RS256`.

Configuration is external. No real issuer, JWKS URL, key or token is committed
to the repository.

Issuer and audience comparison are exact. The accepted clock skew is exactly
60 seconds for `exp` and `nbf` validation.

Outside explicit test configuration, missing or blank required JWT
configuration prevents a usable authenticated API. The runtime must fail
closed; it must not silently start an anonymous or trusted-header mode.

Tests may install a test-only decoder, public key or local JWKS source. Test
configuration must exercise real RS256 signing and validation and must never
become production configuration.

## 11. Authenticated Actor Translation

The API security adapter translates a fully validated JWT into exactly:

- `UserId` from `sub`; and
- the active role from `imperator_role`.

Only those translated values may reach existing REST mappers and Application
commands. Controllers and mappers must not receive raw token text, a JWK,
issuer metadata or an unvalidated claim map.

This translation is an adapter responsibility. It contains no business rule,
repository access, transaction, evidence lookup or role authorization policy.

## 12. Trusted Actor Removal

Sprint 3.8 must remove the production `TrustedActorContextResolver` and the
property `imperator.security.trusted-actor.enabled`.

These headers become inert and can never authenticate or override a JWT:

```text
X-Imperator-Actor-ID
X-Imperator-Actor-Role
```

Frozen negative behavior:

- trusted-actor headers without a valid JWT return `401`;
- trusted-actor headers with a valid JWT do not change JWT `sub` or
  `imperator_role`; and
- no test-only trusted-header bypass may remain in production source.

The headers need not produce a special error when a valid JWT is present; they
are ignored and have no authority.

## 13. Authentication Versus Authorization

Sprint 3.8 authenticates every `/api/v1/**` caller and provides the validated
actor attributes required by the already implemented Application commands.

It does not add URL-, route-, method-, evidence- or sensitivity-level RBAC.
Every valid token carrying one accepted MVP role may reach every existing
route during this local certification stage. Existing Application governance
checks remain active and may still return `403` for an invalid business actor.

Sprint 3.9 exclusively owns:

- route and HTTP-method role policy;
- read-route visibility policy;
- evidence sensitivity filtering by role;
- role hierarchy or multiple-role behavior; and
- Spring Security authorization rules beyond `authenticated()`.

Sprint 3.8 does not authorize external exposure, a pilot or real customer data.
Those remain prohibited until Sprint 3.9 and later readiness gates pass.

## 14. Stateless Security Contract

The `/api/v1/**` security chain is stateless:

- session creation policy is stateless;
- no HTTP session stores authentication;
- no session cookie is created;
- form login is disabled;
- HTTP Basic is disabled;
- server-side logout is disabled;
- request caching must not create a login flow; and
- CSRF is disabled only for this Bearer-token API chain.

Disabling CSRF here does not authorize cookie-based authentication now or
later. Any future browser credential model requires a separate decision.

## 15. Error And Correlation Contract

Authentication failures occur before controller advice, so Sprint 3.8 must
provide a Spring Security authentication entry point that emits the exact D075
four-field JSON envelope:

```text
code
message
correlationId
details
```

`details` is always a non-null empty object for authentication failures.

Frozen mapping:

| Condition | HTTP | Envelope code | Safe message |
|---|---:|---|---|
| Missing Bearer credential | 401 | `AUTHENTICATION_REQUIRED` | `Authentication required` |
| Malformed, invalid, expired, premature or untrusted token | 401 | `INVALID_TOKEN` | `Invalid authentication token` |
| Existing Application authority failure | 403 | Existing Application code | Existing safe Application message |

Every `401` response:

- uses `application/json` for every `Accept` value;
- includes `WWW-Authenticate: Bearer`;
- includes `X-Correlation-ID`;
- has the same correlation ID in header and body; and
- exposes no token, claim value, signature, `kid`, key, issuer URL, exception,
  stack trace or decoder detail.

`CorrelationIdFilter` remains the highest-precedence request filter and must
establish correlation before Spring Security can reject a request. Correlation
remains HTTP-only and is not identity.

## 16. Sensitive Data And Logging

JWTs and authentication material are Restricted security data.

Sprint 3.8 must not persist or log:

- Bearer token text;
- JWT header or complete claim sets;
- JWK material beyond framework-internal memory;
- issuer or decoder exception detail in client responses;
- credentials, private keys or real test tokens; or
- authentication data as Evidence or Ledger snapshot content.

Existing governance persistence continues to store only the authoritative
actor UUID and active role already required by the Application and Ledger
contracts.

## 17. Sprint 3.8 Implementation Boundary

Sprint 3.8 may modify only what is required to implement this contract:

- `pom.xml` for the two authorized dependencies;
- a new `backend-java/api/security` package;
- the Ledger API actor-resolution boundary needed to remove trusted headers;
- existing REST error infrastructure only for `INVALID_TOKEN` compatibility;
- bootstrap wiring required for the Resource Server;
- HTTP, application-startup and PostgreSQL integration tests; and
- implementation-facing package README files when required by project
  convention.

It must not modify Domain, Application business behavior, input or output
ports, repositories, SQL, V1, Flyway, business DTO schemas, routes, financial
policy, Recommendation policy, Ledger semantics or frozen documents.

## 18. Required Verification

Default Java 21 verification must prove:

1. all 15 D086 routes reject a missing token with `401`;
2. valid RS256 tokens reach the existing route behavior;
3. invalid signature, issuer, audience, expiry and `nbf` fail;
4. missing or malformed `sub` and `imperator_role` fail;
5. missing `kid`, unknown `kid`, `alg=none` and HMAC tokens fail;
6. trusted-actor headers cannot authenticate or override JWT identity;
7. security failures preserve the exact error and correlation contracts;
8. `WWW-Authenticate: Bearer` is present on `401`;
9. no session or session cookie is created;
10. authenticated unknown API routes remain `404`;
11. unauthenticated unknown API routes return `401`;
12. no route, DTO or business response contract changes; and
13. all existing tests pass after using valid test JWTs where authentication is
    required.

Tests must include real RS256 signing and decoding. Mocked JWT principals may
support focused controller tests but cannot be the sole authentication
evidence.

PostgreSQL runtime certification must execute:

```text
mvnw.cmd -Ppostgresql-integration clean verify
```

It must additionally prove that governance commands persist the actor UUID and
active role derived from the signed JWT, never from trusted headers.

## 19. Explicit Exclusions

Sprint 3.8 must not implement:

- RBAC route matrices;
- sensitivity-aware reads;
- tenant isolation;
- identity or user persistence;
- Authorization Server or token minting;
- login, password, session or refresh workflows;
- provider-specific identity integration;
- CORS policy;
- rate limiting;
- token revocation;
- audit-log expansion;
- REST route or DTO changes;
- Domain or business-policy changes;
- PostgreSQL or Flyway changes;
- live connectors, frontend, Docker, observability or pilot behavior.

## 20. Completion Gate

Sprint 3.8 may be declared `CERTIFIED` only when:

- this contract is implemented without reinterpretation;
- Java 21 `clean verify` passes;
- the PostgreSQL 18.2 integration profile passes;
- all existing and new authentication tests pass;
- the temporary trusted actor mechanism is absent from production code;
- all 15 routes require valid JWT authentication;
- authentication errors preserve D075 and D076;
- Domain Isolation Index remains 100%;
- Architecture Stability Index remains 100%;
- Decision Stability remains 100%; and
- Sprint 3.8.1 synchronizes active documentation before Sprint 3.9 begins.

If implementation requires changing Domain, Application business behavior,
Ports, V1, Flyway, route topology, DTO schemas or the RBAC boundary, Sprint 3.8
must stop and request explicit architectural approval.
