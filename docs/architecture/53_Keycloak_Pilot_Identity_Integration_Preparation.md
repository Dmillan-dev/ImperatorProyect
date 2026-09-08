# 53 - Keycloak Pilot Identity Integration Preparation

Status: **PREPARATION ONLY / NON-AUTHORITATIVE / NOT IMPLEMENTED**

Owning future gate: **Pilot Identity Conformance, mandatory before Sprint 4.5**

This document creates no decision, contract, sprint authorization or runtime
capability. D087, D088, D091 and D092 remain unchanged; D095 governs the
certification sequencing between the local runtime and operational pilot IdP.

## 1. Purpose

Prepare the minimum external Keycloak configuration needed to prove that a
real Identity Provider can issue access tokens accepted by IMPERATOR during
the controlled pilot. Keycloak remains outside the IMPERATOR repository and
Docker Compose runtime.

The target is one proof:

```text
External Keycloak
  -> HTTPS issuer and JWKS
  -> RS256 access token with one IMPERATOR role
  -> IMPERATOR Resource Server validation
  -> D088 authorization
```

This preparation does not add login, SSO, refresh, user persistence or token
issuance to IMPERATOR.

## 2. Current Authentication Baseline

| Capability | Current state | Authority |
|---|---|---|
| OAuth2 Resource Server | Certified | D087 |
| RS256, `kid`, issuer, audience and time validation | Certified with controlled keys | D087 |
| Actor mapping from `sub` and `imperator_role` | Certified | D087 |
| Four-role route and Evidence authorization | Certified | D088 |
| Browser token handling | Volatile in-memory paste only | D091 |
| External HTTPS issuer/JWKS | Not provisioned | D095-deferred pre-Sprint-4.5 gate |
| Login or IdP inside IMPERATOR | Explicitly absent | D087, D091, D092 |

No Java change is currently justified. The missing element is an external
conforming issuer and an approved pilot token-acquisition procedure.

## 3. Proposed External Topology

```text
Pilot user
  -> external Keycloak authentication
  -> access token issued to an external pilot client
  -> token supplied to the D091 volatile session control
  -> /api/v1/**
  -> Spring Security Resource Server
  -> D087 validation
  -> D088 authorization
```

Keycloak is not added to `infra/docker/`, is not an IMPERATOR dependency and
does not share the IMPERATOR PostgreSQL database.

## 4. Keycloak Realm And Endpoint Profile

The proposed isolated pilot realm name is `imperator-pilot`. The actual host
must be selected and operated outside this repository.

| Item | Required pilot value |
|---|---|
| Issuer | `https://<keycloak-host>/realms/imperator-pilot` |
| Discovery | `{issuer}/.well-known/openid-configuration` |
| JWKS | `{issuer}/protocol/openid-connect/certs` |
| API audience | Exact `imperator-api` |
| Access-token algorithm | Exact `RS256` |
| JOSE key selector | Mandatory `kid` |
| Clock skew accepted by IMPERATOR | Exact 60 seconds |

The final URLs must use trusted HTTPS. Loopback HTTP, self-signed production
certificates and an issuer URL rewritten differently between browser and
backend are not acceptable for the pilot conformance evidence.

## 5. Token Contract Mapping

The Keycloak client scope used for the pilot must produce this D087-compatible
access token shape:

| Claim/header | Keycloak preparation | Verification |
|---|---|---|
| `alg` | Active realm RSA signing key configured for RS256 | Header equals `RS256` |
| `kid` | Emitted from the active realm key | Present and resolvable in JWKS |
| `iss` | Realm issuer | Exact configured issuer |
| `aud` | Audience protocol mapper | Collection contains `imperator-api` |
| `sub` | Keycloak subject | Canonical lower-case UUID |
| `exp` | Realm/client token lifetime | Present and not expired |
| `nbf` | Optional Keycloak time claim | Valid when present |
| `imperator_role` | User-attribute protocol mapper | One exact JSON String |

The `imperator_role` mapper must use:

- user attribute: `imperator_role`;
- token claim name: exact `imperator_role`;
- claim JSON type: `String`;
- multivalued output: disabled;
- add to access token: enabled; and
- no reliance on `realm_access.roles`, `resource_access`, groups or scopes.

An Audience mapper must add exact `imperator-api` to access tokens. The
generated token must be inspected before use because provider defaults do not
override D087.

## 6. Pilot Users And Roles

Create four enabled pilot users, each with exactly one allowed user attribute:

| Pilot identity | Exact `imperator_role` | Intended proof |
|---|---|---|
| Pilot Admin | `ADMIN` | Import, R16 composition, approve/reject/defer |
| Pilot Platform Engineer | `PLATFORM_ENGINEER` | Read, defer, mark implementation |
| Pilot Finance | `FINANCE` | Read, defer, validate result |
| Pilot Auditor | `AUDITOR` | Read-only and denied commands |

No hierarchy, composite role or inherited group may grant IMPERATOR authority.
The actual `sub` emitted by Keycloak is authoritative. It must first pass the
D087 UUID check, then the R16 `requiredApproverId` must be set to the Pilot
Admin `sub`. Runtime tests must not assume the fixed UUIDs used by local test
fixtures.

The realm is an identity isolation boundary for the controlled pilot. It does
not introduce an IMPERATOR Tenant aggregate, tenant claim or multi-tenancy.

## 7. Client And Token Acquisition Boundary

Keycloak should expose one external public pilot client for human token
acquisition using Authorization Code with PKCE `S256`. That client is not the
IMPERATOR backend and does not make IMPERATOR an OAuth2 client.

The exact external token-acquisition procedure must be approved before the
live E2E. It must keep the access token in volatile memory and make it available
only to the authorized operator for D091 paste-in testing.

Rejected for the pilot:

- Resource Owner Password Credentials/direct access grants;
- implicit flow;
- a service account representing a human approver;
- client credentials for governance commands;
- offline tokens;
- committing a realm export containing credentials; and
- adding Keycloak adapters, login callbacks or refresh tokens to IMPERATOR.

A commercial self-service browser login is a later product capability and
requires its own contract. D091 remains the only authorized frontend behavior.

## 8. IMPERATOR Runtime Inputs

Only external configuration is expected:

```text
IMPERATOR_JWT_ISSUER_URI=<exact HTTPS realm issuer>
IMPERATOR_JWT_JWK_SET_URI=<exact HTTPS realm JWKS URI>
```

The already frozen values remain:

```text
audience=imperator-api
algorithm=RS256
```

Issuer/JWKS URLs are configuration, not credentials, but they must still be
environment-specific. Tokens, passwords, private keys and client secrets must
never enter Git, Compose files, logs, screenshots or certification reports.

## 9. Live Conformance Matrix

| Scenario | Expected result |
|---|---|
| Discovery and JWKS reachable over trusted HTTPS | Proceed |
| Valid token for each exact role | Authentication succeeds |
| `ADMIN` calls R01/R16 | Route reaches Application boundary |
| `AUDITOR` calls any command | `403 ACCESS_DENIED`, no write |
| Missing token | `401 AUTHENTICATION_REQUIRED` |
| Wrong issuer or audience | `401 INVALID_TOKEN` |
| Expired or premature token | `401 INVALID_TOKEN` |
| Missing/unknown `kid` or non-RS256 token | `401 INVALID_TOKEN` |
| Missing, array-valued or unsupported role | `401 INVALID_TOKEN` |
| Non-UUID `sub` | `401 INVALID_TOKEN` |
| Approver ID differs from Admin `sub` on R16 | Frozen D093 validation failure, no write |
| JWKS unavailable with no eligible cached key | Fail closed; no authorization |

The evidence package records only safe status, correlation ID, issuer host,
JWKS reachability, role name and pass/fail. It records no token or claim dump.

## 10. Security And Operations

- Keycloak administration must not be exposed through IMPERATOR.
- Realm administration and pilot user administration remain separate from
  product roles.
- Brute-force protection, password/MFA policy and administrative audit belong
  to the external IdP operating procedure.
- Access tokens should be short-lived; the exact lifetime is an external IdP
  setting and must be recorded before the pilot.
- Key rotation must preserve JWKS overlap long enough for already issued
  short-lived tokens; unknown keys still fail closed.
- IMPERATOR stores no password, access token, refresh token or IdP session.

## 11. Required Future Evidence

- [ ] Maintained Keycloak version and deployment owner recorded.
- [ ] Trusted HTTPS issuer and JWKS reachable from the backend container.
- [ ] Discovery metadata matches the configured issuer.
- [ ] Audience and role mappers inspected through a sanitized configuration
      summary.
- [ ] Four real access tokens pass D087 without being persisted.
- [ ] D088 positive and negative role probes pass with real tokens.
- [ ] R16 approver-subject equality passes after D093 implementation.
- [ ] Expired, wrong-audience, wrong-issuer and unauthorized-role probes fail.
- [ ] No token or secret appears in logs, shell history or evidence artifacts.

## 12. Preconditions And Decision Boundary

The preparation is complete. Sprint 4.3 local runtime certification and D093
implementation now pass, but operational pilot identity remains `NO-GO` until:

1. an external Keycloak deployment owner, URL and certificate exist;
2. the token-acquisition procedure is approved; and
3. the live conformance matrix can run without modifying D087 or D088.

Any need to change the exact claim name, UUID subject, audience, algorithm or
role model is an objective contract conflict and requires explicit approval.
It must not be solved inside the IdP configuration work by weakening IMPERATOR.

## 13. Official Keycloak References

- [OpenID Connect endpoints](https://www.keycloak.org/securing-apps/oidc-layers)
- [Server Administration Guide](https://www.keycloak.org/docs/latest/server_admin/)
- [Protocol mapper reference](https://www.keycloak.org/admin-api/protocol-mappers)
