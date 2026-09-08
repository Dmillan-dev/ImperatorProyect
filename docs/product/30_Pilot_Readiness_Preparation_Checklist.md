# 30 - Pilot Readiness Preparation Checklist

Status: **NOT READY / PREPARATION ONLY / SPRINT 4.5 NOT OPEN**

This artifact reports readiness without changing project-control status,
architecture, product scope or runtime. It authorizes no pilot and no code.

## 1. Executive Readiness

IMPERATOR has a strong certified local product core, but cannot yet claim Pilot
Readiness. The blocking chain is:

```text
Sprint 4.3 CERTIFIED under D095
  -> Sprint 4.3.1 documentation synchronization COMPLETE
  -> Sprint 4.4 observability implementation and certification under D096
  -> external Keycloak D087/D088 conformance
  -> controlled live-connector and operational readiness evidence
  -> Sprint 4.5 Pilot Readiness
```

## 2. Readiness Checklist

| Area | Status | Evidence or missing condition |
|---|---|---|
| IdP | `BLOCKED` | Keycloak selected for preparation; external HTTPS deployment absent |
| Tenant | `NOT APPLICABLE TO MVP` | One controlled organization; no Tenant model or claim is authorized |
| Users | `PREPARED` | Four identities defined; not provisioned |
| Roles | `LOCAL PASS / EXTERNAL PENDING` | D088 and local real-token conformance pass; operational external tokens remain unproven |
| DRC-AOA-001 data | `READY` | Canonical 30-line NDJSON exists and local demo is certified |
| Evidence import | `CERTIFIED` | R01 and PostgreSQL behavior certified |
| GitHub/AWS Evidence | `CERTIFIED OFFLINE` | Adapters certified; live pilot sandbox checks remain separate |
| Decision | `CERTIFIED LOCAL RUNTIME` | D081 plus D093/R16 composition pass through the local API runtime |
| Recommendation/ROI | `CERTIFIED LOCAL RUNTIME` | D082 plus D093/R16 deterministic generation and replay pass |
| Approval | `CERTIFIED` | D083/D088 backend behavior certified |
| Implementation/result | `CERTIFIED` | Ledger commands and policy certified |
| Ledger | `CERTIFIED` | Ordered append-only governance chain certified |
| Business Value | `CERTIFIED LOCAL RUNTIME` | Exact projection and complete DRC-AOA-001 runtime E2E pass |
| Workspace | `CERTIFIED CONTROLLED DEMO` | D091 complete; no commercial login |
| Docker runtime | `CERTIFIED` | D092-D095 image, hardening, runtime and recreation gates pass |
| Flyway/persistence | `CERTIFIED` | PostgreSQL 18.6, V1/V2 migrate/validate/no-op and persisted graph recreation pass |
| Backup/restore | `NOT IMPLEMENTED` | D092 excludes it; volume persistence is not backup |
| Security | `PARTIAL FOR PILOT` | Local runtime and JWT/RBAC pass; external IdP and pilot operations remain pending |
| Observability | `AUTHORIZED / NOT IMPLEMENTED` | D096 is frozen and bounded Sprint 4.4 implementation is current |
| Runtime E2E | `CERTIFIED LOCAL` | R01-R16, governance, Business Value and recreation pass under D095 |
| Documentation | `SYNCHRONIZED` | Sprint 4.3.1 records the certified local runtime and deferred pilot identity gate |

## 3. Pilot Commercial Definition

The pilot demonstrates one case only:

> IMPERATOR converts operational Evidence into a justified Decision, a
> deterministic Recommendation, governed action and measurable economic value.

The customer must be able to answer in less than 15 minutes:

1. What happened?
2. Why does it matter?
3. What does IMPERATOR recommend?
4. What value is expected?
5. Who approved and implemented it?
6. What value was actually realized?
7. Can every claim be traced to Evidence and immutable history?

Commercial success is not the number of connectors or dashboards. It is one
credible, auditable value loop whose financial meaning the customer accepts.

## 4. Pilot Success Criteria

| Criterion | Acceptance |
|---|---|
| Traceability | Evidence -> Decision -> Recommendation -> Ledger -> Business Value is reconstructable |
| Explainability | Deterministic reason, policy, inputs and ROI are visible |
| Governance | Exact human role and actor perform each authorized action |
| Economic value | Estimated and realized EUR values match the canonical policy |
| Persistence | The complete graph survives certified Docker recreation |
| Security | Real JWT/RBAC works and no secret or restricted payload leaks |
| Operability | Required health, logs, metrics and alerts exist before pilot |
| Comprehension | Customer understands the value in under 15 minutes |

## 5. Pilot Environment Preparation

- [ ] One controlled organization and one repository/account boundary agreed.
- [ ] No multi-tenant promise or tenant isolation claim made.
- [ ] External Keycloak owner, host, certificate and maintenance policy agreed.
- [ ] Four pilot users provisioned with exactly one role each.
- [ ] Token acquisition procedure approved and non-persistent.
- [ ] Canonical Evidence source ownership and collection window agreed.
- [ ] GitHub/AWS pilot permissions verified read-only.
- [ ] No customer secret or raw payload enters the Evidence Store.
- [ ] Pilot data retention and deletion procedure agreed.
- [ ] Backup/restore requirement explicitly resolved before real customer data.
- [ ] Incident contact, rollback and stop conditions agreed.

## 6. Architecture Health Review

Review basis: current repository at the authorized Sprint 4.4 pre-implementation
boundary, frozen D081-D096 contracts and certified implementation through
Sprint 4.3.

### Green

- Domain, Application and Ports contain no Spring, JDBC, JPA, REST or
  PostgreSQL dependency leakage.
- Decision and Recommendation/ROI policies remain deterministic.
- AI explanation remains optional and non-authoritative.
- PostgreSQL adapters remain outside the Domain and the Ledger is append-only.
- JWT actor identity and RBAC enforcement are separate and fail closed.
- GitHub and AWS adapters produce normalized Evidence without business-rule
  authority.
- Frontend remains a strict API consumer and does not calculate or authorize.
- Correlation ID propagation already exists across HTTP and connector work.
- D093/R16 composes one idempotent case graph through the existing D081/D082
  boundaries without introducing a workflow engine.
- D094 certifies the PostgreSQL 18.6 derived image supply chain, and D095 keeps
  operational external identity mandatory without coupling it to local runtime
  certification.

### Amber

1. D091 provides controlled token paste, not commercial login or SSO.
2. No operational external HTTPS issuer/JWKS has passed the mandatory Pilot
   Identity Conformance Gate.
3. D096 freezes application observability, but its dependencies, endpoints and
   optional runtime profile do not exist until Sprint 4.4 implementation.
4. Docker volume persistence is not backup/restore. D092 explicitly excludes
   backup, which must be resolved before real customer data if the pilot terms
   require recoverability beyond container recreation.
5. GitHub/AWS adapters are certified offline. Live read-only sandbox checks
   remain pre-pilot evidence, not a reason to expand connector scope.
6. The canonical pack includes Jira/manual and AI-usage facts not produced by
   the two live connectors. The pilot runbook must identify the legitimate
   source and accountable owner for each imported fact.
7. Multi-tenancy is intentionally absent. The pilot must remain one controlled
   organization and must not be represented as tenant isolation.

### Red

No Red finding remains in the certified local Sprint 4.3 runtime. Pilot
Readiness remains `NO-GO` until its later external identity, observability,
data-protection and operational gates pass.

No objective contradiction currently requires changing the Domain or D087-D095.
The outstanding work is pilot preparation and external conformance, not an
architecture redesign.

## 7. Test Readiness

| Suite | Existing evidence | Required before pilot |
|---|---|---|
| Domain/Application unit | 151 default tests, including deterministic policies, governance and D093 composition | Preserve as regression evidence |
| REST contracts | D086 R01-R15 plus D093 R16 certified | Preserve exact contract and negative-path coverage |
| JWT/RBAC | Controlled RS256 and role matrix certified | Repeat with real Keycloak tokens |
| PostgreSQL | PostgreSQL 18.6, Flyway V1/V2 and 34 integration tests certified | Repeat relevant evidence in the pilot environment |
| Ledger | Sequence, replay, concurrency and local API E2E certified | Execute through operational external identities |
| Frontend | 41 tests and 9 Playwright acceptance tests certified | Load the runtime-composed case with an operational external token |
| Connectors | GitHub/AWS offline protocol tests certified | Controlled live read-only smoke evidence |
| Docker | D092-D095 runtime, clean images and complete data recreation certified | Preserve exact certified inputs and evidence |
| Observability | D096 contract only; none at runtime | Sprint 4.4 implementation and certification |

## 8. Evidence Required For Pilot Authorization

- final clean image digests and Trivy reports;
- Git SHA and clean working tree;
- Flyway migrate, validate and no-op results;
- PostgreSQL permissions, transaction and persistence results;
- sanitized Keycloak issuer/JWKS and token-conformance results;
- R01/R16/governance/Business Value runtime assertions;
- pre/post-Docker-recreation object equality;
- security and confidential-Evidence checks;
- observability dashboard and alert evidence; and
- Architecture, Security, Quality, Product and Context Guardian approvals.

## 9. Stop Conditions

The pilot remains `NO-GO` if any of these occurs:

- one fixable Critical image vulnerability;
- invalid or unavailable external JWT trust configuration;
- bypass, mock token or manually assembled production token;
- D093/R16 regression or duplicate case graph;
- direct SQL/fixture used to fabricate the commercial case;
- broken Ledger sequence or untraceable Business Value;
- missing data recovery commitment for real customer data;
- secrets/tokens/raw payloads in logs or evidence; or
- Sprint 4.4/4.5 started before their authorized gates.

## 10. Current Decision

```text
PILOT READINESS: NO-GO
ARCHITECTURE HEALTH: SOUND WITH EXPLICIT DELIVERY GAPS
SPRINT 4.3: CERTIFIED
SPRINT 4.3.1: COMPLETE
D096 CONTRACT: FROZEN / ACCEPTED
SPRINT 4.4 IMPLEMENTATION: AUTHORIZED / CURRENT
EXTERNAL PILOT IDENTITY: MANDATORY BEFORE SPRINT 4.5
SPRINT 4.5: NOT OPEN
```

## 11. Prepared Commercial Operating Package

The non-executable pilot package is indexed in `docs/pilot/README.md` and now
covers:

- the controlled single-customer environment and all 30 Evidence owners;
- external Keycloak, GitHub and AWS access preparation;
- rehearsal and future customer-pilot runbooks;
- retention, deletion, backup/restore, incidents, DNS/TLS and secrets;
- customer security questionnaire and DPA/NDA requirements; and
- technical, commercial and WP11 acceptance evidence.

These artifacts reduce Pilot Readiness uncertainty but do not change any
status in Section 2.
