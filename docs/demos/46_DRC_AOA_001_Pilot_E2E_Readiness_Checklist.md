# DRC-AOA-001 Pilot E2E Readiness Checklist

## Purpose

This preparation artifact defines the evidence required to run the canonical
`DRC-AOA-001` pilot through the real Docker runtime. It does not authorize a
new sprint, route, adapter, identity provider deployment, schema change, seed,
fixture loader or product capability.

The checklist remains subordinate to D083, D086, D087, D088, D091 and D092.
It must not be reported as executed while Sprint 4.3 is blocked.

## Current Status

```text
Sprint 4.3                 BLOCKED_EXTERNAL / TEMPORARY NO-GO
Docker implementation     PASS
Frontend image fix        PASS
D092 vulnerability gate   FAIL: official PostgreSQL/gosu upstream image
External D087 IdP          NOT PROVISIONED
Runtime E2E composition   NOT EXPOSED
```

Sprint 4.3.1, Sprint 4.4 and Sprint 4.5 remain unauthorized until every D092
certification condition passes.

## Authority Set

| Concern | Authority |
|---|---|
| JWT contract | `docs/architecture/46_JWT_Authentication_Contract.md` |
| RBAC matrix | `docs/architecture/47_RBAC_Authorization_Contract.md` |
| REST inventory | `docs/architecture/45_Functional_REST_Application_Contract.md` |
| Workspace | `docs/architecture/50_Executive_Dashboard_Contract.md` |
| Docker runtime | `docs/architecture/51_Docker_Production_Runtime_Contract.md` |
| Canonical local demo | `docs/demos/45_End_To_End_Business_Value_Demo.md` |
| Canonical dataset | `src/test/resources/evidence/drc-aoa-001-business-value-demo.jsonl` |

## Pilot Identity Provider Compatibility

### Frozen token shape

The external IdP must issue an access token containing exactly:

| Item | Required value |
|---|---|
| Signature | `RS256` |
| JOSE key identifier | One mandatory `kid` |
| `iss` | Exact configured HTTPS issuer |
| `aud` | Contains exact `imperator-api` |
| `sub` | Canonical lower-case UUID |
| `exp` | Present and valid |
| `nbf` | Valid when present |
| `imperator_role` | One exact string, not an array |

Allowed role values remain exactly `ADMIN`, `PLATFORM_ENGINEER`, `FINANCE` and
`AUDITOR`. No hierarchy, groups, scopes or provider role arrays confer product
authority.

### Provider assessment

| Candidate | Result | Reason |
|---|---|---|
| Auth0 | `NO-GO` under frozen D087 | Auth0 custom-API access tokens do not accept a private non-namespaced custom claim such as literal `imperator_role`. A namespaced claim would require a D087 and runtime Contract Fix. |
| Okta custom authorization server | Compatible with additional mapping | It supports custom access-token claims, but the default subject is not the D087 UUID shape and production custom authorization servers may require a commercial capability. |
| External Keycloak | Selected for pilot preparation | OIDC protocol mappers can emit a named, single-valued String claim, an Audience mapper can emit `imperator-api`, and the realm publishes HTTPS issuer and JWKS endpoints. |

Official compatibility references:

- Auth0 custom claim restrictions:
  `https://auth0.com/docs/secure/tokens/json-web-tokens/create-custom-claims`
- Keycloak protocol mapper settings:
  `https://www.keycloak.org/admin-api/protocol-mappers`
- Keycloak OIDC and JWKS endpoints:
  `https://www.keycloak.org/docs/latest/server_admin/`

### Selected Keycloak preparation profile

Keycloak is the selected pilot IdP. It remains external to IMPERATOR and is
not deployed by this artifact. The future pilot configuration must prove:

- [ ] One HTTPS realm issuer is reachable by the backend.
- [ ] The realm JWKS endpoint is HTTPS and exposes the active RSA key.
- [ ] Access-token signing is `RS256` and the JOSE header contains `kid`.
- [ ] An Audience mapper adds exact `imperator-api` to the access token.
- [ ] A user-attribute protocol mapper emits exact `imperator_role`.
- [ ] The mapper JSON type is `String` and `Multivalued` is disabled.
- [ ] The claim is included in the access token, not only the ID token.
- [ ] Each pilot user has exactly one allowed `imperator_role` value.
- [ ] Each emitted `sub` is a canonical lower-case UUID.
- [ ] Issuer and JWKS values remain external configuration.
- [ ] Tokens, private keys, credentials and tenant values remain uncommitted.

The runtime settings remain:

```text
IMPERATOR_JWT_ISSUER_URI=<external HTTPS realm issuer>
IMPERATOR_JWT_JWK_SET_URI=<external HTTPS realm JWKS URI>
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_AUDIENCES=imperator-api
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_JWS_ALGORITHMS=RS256
```

No value above authorizes storing a working URL, token or key in Git.

## Runtime Composition Gap

The current API cannot create the complete value loop from an empty database:

| Capability | Application capability | REST entry point | Current pilot usability |
|---|---|---|---|
| Import Evidence | `ImportEvidenceInputPort` | `POST /api/v1/evidence/import` | Available |
| Create Decision | `CreateDecisionInputPort` | None by D086 | Blocked |
| Generate Recommendation | `GenerateRecommendationInputPort` | None by D086 | Blocked |
| Read Decision context | Query input ports | D086 GET routes | Available only after state exists |
| Human review | `ReviewDecisionInputPort` | D086 Ledger commands | Available only after Recommendation exists |
| Mark implementation | `AppendLedgerEntryInputPort` | D086 command | Available only after approval |
| Validate result | `AppendLedgerEntryInputPort` | D086 command | Available only after implementation |
| Project Business Value | Query input port | `GET /api/v1/business-value` | Available only after validated state exists |

`POST /api/v1/evidence/import` persists Evidence only. It does not create a
Decision or generate a Recommendation. D086 deliberately keeps those two
capabilities internal, and D092 forbids SQL insertion and startup seed data.

Therefore the full pilot E2E remains `NO-GO` until a future, separately
authorized contract defines one deterministic application entry point that
composes the existing internal capabilities. This checklist does not choose a
route, scheduler, CLI, bootstrap mechanism or event handler.

## Future Composition Contract Freeze Preparation

### Status and boundary

```text
STATUS: DRAFT PREPARATION / NOT AUTHORIZED / NOT A DECISION
```

This section prepares the questions and inherited constraints for a future
`DRC-AOA-001` composition Contract Freeze. It does not create that contract,
assign a decision number, authorize implementation or modify D081, D082, D083,
D086, D087, D088, D091 or D092.

The future contract is necessary because the currently frozen boundaries have
one objective execution gap:

- D092 requires a valid external D087 token to load the workspace after
  certified data import through existing APIs;
- R01 in D086 imports Evidence only;
- D086 deliberately keeps Decision creation and Recommendation generation
  outside REST and forbids adding either as an Evidence-import side effect;
- the workspace can read and govern an existing case, but cannot originate it;
  and
- D092 forbids SQL seed data, startup fixtures and new product capabilities.

Consequently, a clean runtime cannot reach a workspace-loadable
`DRC-AOA-001` using only the currently exposed APIs. Fixing the PostgreSQL
image vulnerability will not, by itself, close this separate composition gap.
No D092 change is authorized here.

### Inherited facts that must not be redesigned

| Concern | Already frozen behavior |
|---|---|
| Evidence intake | R01 imports the canonical 30 normalized Evidence records and remains side-effect-free with respect to Decision and Recommendation. |
| Case correlation | Every canonical input retains correlation key `DRC-AOA-001`. |
| Decision origin | D081 requires one persisted, eligible `business_context_requested` Evidence to originate the Decision. |
| Decision identity | The initiating Application boundary supplies a stable UUID v4 and retries reuse it. |
| Decision case ID | D081 derives `caseId` from the originating Evidence correlation key. |
| Decision creation | `CreateDecisionInputPort` and its deterministic idempotency rules remain authoritative. |
| Recommendation and ROI | D082 selects persisted accepted Evidence and produces exactly one deterministic `MODEL_CHANGE` Recommendation under `DRC-AOA-001-v1`. |
| Recommendation identity | The initiating Application boundary supplies a stable UUID v4 and retries reuse it. |
| AI authority | AI never creates the business truth, selects the action, calculates ROI or controls the workflow. |
| Governance | D083 and D088 continue to govern approval, rejection, deferral, implementation marking and result validation. |
| Business Value | It remains a non-persisted projection available only after the authoritative result-validation state. |
| Ledger | Governance facts remain immutable, ordered and append-only. |
| Workspace | D091 consumes existing read and governance contracts; it must not orchestrate case creation in the browser. |

### Questions the future contract must freeze

The future Contract Freeze must provide exactly one unambiguous answer for
each question below before any adapter or endpoint is implemented:

1. Which authenticated business actor may initiate composition, and which
   frozen role or roles may perform it?
2. Does the operation act on exactly the already imported canonical 30-record
   Evidence set, and how is that complete set selected without trusting a
   transport payload as business truth?
3. Which eligible `business_context_requested` Evidence is the authoritative
   origin of `DRC-AOA-001`?
4. How are stable Decision and Recommendation UUIDs created and preserved
   across retries, process restarts and equivalent concurrent requests?
5. Which fields required by D081 supply title, business need, owner, required
   approver and stable creation time, and which persisted Evidence corroborates
   them?
6. What single operation initiates the commercial case-composition cycle?
7. Is one new Application orchestration capability required, or can an
   existing authorized Application boundary compose the flow without changing
   its responsibility?
8. Which part remains Evidence import, and which part is explicit business
   case creation?
9. Does the composition invoke D081 and D082 as one atomic unit, or is it a
   deterministic, resumable two-step operation with an explicit recovery
   state?
10. What happens when Decision creation succeeds but Recommendation generation
    fails or is temporarily not ready?
11. What exact result is returned for first execution, equivalent replay,
    conflicting replay and concurrent execution?
12. How are duplicate Decisions and duplicate Recommendations prevented
    without weakening the existing D081 and D082 idempotency contracts?
13. Which inbound adapter may invoke the operation for the pilot: REST, an
    authenticated administrative CLI or another explicitly authorized
    boundary?
14. If REST is selected, what route, method, request, response, errors and D088
    authorization are frozen? No route is implied by this preparation.
15. How does the completed graph become immediately readable through the
    existing D086 queries and D091 Decision Review Workspace without adding
    browser-owned business behavior?
16. Is the D092 certification clause corrected or scoped separately from the
    later full pilot E2E, and which exact evidence belongs to each gate?

### Recommended composition shape, not frozen

The smallest coherent candidate is one explicit, operator-initiated
Application composition operation after successful Evidence import:

```text
Authenticated operator
        |
        v
Explicit case-composition Application boundary
        |
        +--> load and validate persisted canonical Evidence
        |
        +--> invoke the existing deterministic Decision capability (D081)
        |
        +--> invoke the existing deterministic Recommendation/ROI capability (D082)
        |
        v
Existing D086 read model and D091 workspace
```

This shape is preferred because it preserves the separation between ingestion
and business creation, keeps orchestration in Application and reuses already
certified capabilities. The exact port, use-case, command, result, adapter,
route and transaction mechanism remain deliberately unfrozen.

### Composition invariants to preserve

- Importing or re-importing Evidence never creates a Decision implicitly.
- One logical composition identity produces at most one Decision and one
  Recommendation.
- Equivalent replay returns the same authoritative identities and does not
  reset later Decision, Ledger or Business Value state.
- A conflicting immutable tuple fails closed and overwrites nothing.
- Concurrent equivalent attempts converge on the same authoritative graph.
- Every selected Evidence item is loaded from persistence and satisfies D082;
  caller-supplied Evidence content is never treated as truth.
- The Decision retains one traceable originating Evidence and the
  Recommendation retains the complete supporting Evidence set.
- The Recommendation and ROI remain deterministic and independent of an AI
  provider.
- Owner and required approver identities remain explicit UUIDs and must be
  compatible with the human-governance rules in D083 and D088.
- The workspace only reads and governs the resulting graph; it does not create
  Domain objects or calculate business value.
- No direct SQL, fixture, startup seed or manual database correction may
  substitute for the authorized composition operation.

### Patterns explicitly rejected for the future contract

- Decision or Recommendation creation as an Evidence-import side effect;
- controller or frontend chaining of multiple Application use cases;
- state mutation hidden behind a GET route;
- connector-owned Decision, Recommendation or ROI business logic;
- direct repository calls from REST, CLI or UI adapters;
- SQL seed data, manual inserts or persistence-record construction outside the
  PostgreSQL adapter;
- browser-generated business identity without an authorized stable operation
  identity contract;
- AI-generated recommendation type, deterministic reason, confidence, risk or
  monetary value;
- scheduler, event bus, Kafka or background workflow introduced only to bridge
  the pilot gap; and
- a second case, Recommendation family, ranking policy or multi-tenant scope.

### Contract Freeze acceptance gate

The future contract is ready for authorization only when:

- every question above has one answer and no adapter is forced to interpret
  missing business behavior;
- D081 and D082 remain authoritative unless an objective contradiction is
  proved and separately approved;
- actor, authorization, identity, idempotency, concurrency, failure recovery
  and transaction semantics are explicit;
- the chosen inbound boundary and its allowed files are frozen before code;
- D086, D088, D091 and D092 impacts are listed exactly;
- no endpoint, migration, dependency, scheduler or provider is added by
  implication; and
- Architecture, Security, Quality, Product and Context guardians all return
  `PASS`.

## E2E Preconditions

- [ ] Sprint 4.3 D092 vulnerability gate passes for all runtime images.
- [ ] The exact final image digests are recorded.
- [ ] PostgreSQL, Flyway, grants and health checks pass unchanged.
- [ ] An external D087-compatible IdP passes live issuer/JWKS validation.
- [ ] Four test identities exist, each with one exact MVP role.
- [ ] The missing Decision/Recommendation composition entry point has a
      separately frozen and certified contract.
- [ ] The canonical 30-record NDJSON file is unchanged.
- [ ] No SQL seed, direct table write or manual database correction is used.
- [ ] The test starts from an empty IMPERATOR business dataset.
- [ ] Correlation IDs, operation IDs, image digests and start time are recorded.

## Canonical E2E Checklist

### A. Authentication and authorization

- [ ] Obtain four short-lived RS256 access tokens externally.
- [ ] Verify exact issuer, audience, UUID subject, expiry, `kid` and one role.
- [ ] Verify a missing token returns `401`.
- [ ] Verify invalid issuer, audience, signature, expiry and role return `401`.
- [ ] Verify each valid role can read the routes granted by D088.
- [ ] Verify `AUDITOR` cannot invoke any POST route.
- [ ] Verify forbidden role/action combinations return `403` without writes.
- [ ] Keep every token out of console output, reports and persistent files.

### B. Evidence intake

- [ ] As `ADMIN`, import the canonical NDJSON through R01.
- [ ] Assert 30 accepted records and zero rejected records.
- [ ] Confirm raw payloads remain `not_stored`.
- [ ] Confirm all records retain correlation key `DRC-AOA-001`.
- [ ] Re-import the same dataset and verify no duplicate persisted Evidence.

### C. Decision and Recommendation

- [ ] Invoke only the future authorized composition entry point.
- [ ] Confirm exactly one `DRC-AOA-001` Decision exists.
- [ ] Confirm the originating Evidence is traceable from the Decision.
- [ ] Confirm exactly one `MODEL_CHANGE` Recommendation exists.
- [ ] Confirm policy version `DRC-AOA-001-v1`.
- [ ] Confirm current monthly cost is EUR 2,340.00.
- [ ] Confirm projected monthly cost is EUR 720.00.
- [ ] Confirm estimated monthly recovery is EUR 1,620.00.
- [ ] Confirm estimated annualized savings is EUR 19,440.00.
- [ ] Confirm confidence is 92% and risk is `LOW`.
- [ ] Repeat the composition request and verify no duplicate graph objects.

### D. Human governance

- [ ] Open the Decision Review Workspace with a valid token.
- [ ] Confirm Business Value is unavailable before result validation.
- [ ] As the assigned `ADMIN`, approve the Decision.
- [ ] Confirm one immutable `approved` Ledger entry.
- [ ] As `PLATFORM_ENGINEER`, mark implementation.
- [ ] Confirm one immutable `implementation_marked` Ledger entry.
- [ ] As `FINANCE`, validate the result.
- [ ] Confirm one immutable `result_validated` Ledger entry.
- [ ] Replay each operation ID and verify no additional Ledger entry.

### E. Realized Business Value and traceability

- [ ] Confirm annualized baseline cost is EUR 28,080.00.
- [ ] Confirm annualized post-action cost is EUR 9,000.00.
- [ ] Confirm actual transition cost is EUR 120.00.
- [ ] Confirm realized annualized savings is EUR 18,960.00.
- [ ] Confirm variance from estimate is EUR -480.00.
- [ ] Confirm the ordered Ledger chain has no missing predecessor.
- [ ] Trace Business Value to validation, implementation, approval,
      Recommendation, Decision and all 30 Evidence identifiers.
- [ ] Confirm no raw payload, secret or persistence record leaks through REST.

### F. Docker persistence

- [ ] Record Decision, Recommendation, Evidence and Ledger identifiers.
- [ ] Record Business Value output before shutdown.
- [ ] Run normal `docker compose down` without `--volumes`.
- [ ] Recreate the same certified runtime against `postgres-data`.
- [ ] Confirm Flyway validate and permission provisioning pass.
- [ ] Retrieve the same Decision and 30 Evidence records.
- [ ] Retrieve the same Recommendation and ordered Ledger chain.
- [ ] Retrieve an equal Business Value projection.
- [ ] Confirm no duplicates appeared after recreation.

### G. Evidence package

- [ ] Record Git SHA and clean/authorized working-tree state.
- [ ] Record Docker, Compose, Java, Node, PostgreSQL and Flyway versions.
- [ ] Record every final image digest and clean Trivy report.
- [ ] Record issuer and JWKS reachability without recording their secret data.
- [ ] Record route status, object IDs, counts and deterministic financial values.
- [ ] Record negative JWT/RBAC tests without storing tokens.
- [ ] Record pre/post-recreation equality and Ledger ordering.
- [ ] Record Architecture, Security, Quality and Product Guardian results.

## Completion Rule

This checklist passes only when every item is evidenced through authorized
runtime interfaces. A local Application test, direct repository call, SQL
insert, startup fixture or manually assembled token cannot substitute for the
pilot E2E.

Until the PostgreSQL image, external IdP and runtime-composition blockers are
resolved, the checklist status is:

```text
STATUS: NOT EXECUTABLE / NO-GO
```
