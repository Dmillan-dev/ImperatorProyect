# 30 - Pilot Readiness Preparation Checklist

Status: **NOT READY / PREPARATION ONLY / SPRINT 4.5 NOT OPEN**

This artifact reports readiness without changing project-control status,
architecture, product scope or runtime. It authorizes no pilot and no code.

## 1. Executive Readiness

IMPERATOR has a strong certified local product core, but cannot yet claim Pilot
Readiness. The blocking chain is:

```text
Official PostgreSQL image with zero fixable Critical vulnerabilities
  -> final image digest and D092 image gate
  -> explicit D093 implementation authorization and certification
  -> Flyway, PostgreSQL and persistent-data certification
  -> external Keycloak D087/D088 conformance
  -> full DRC-AOA-001 runtime E2E
  -> Sprint 4.3 CERTIFIED
  -> Sprint 4.3.1 documentation synchronization
  -> Sprint 4.4 observability
  -> Sprint 4.5 Pilot Readiness
```

## 2. Readiness Checklist

| Area | Status | Evidence or missing condition |
|---|---|---|
| IdP | `BLOCKED` | Keycloak selected for preparation; external HTTPS deployment absent |
| Tenant | `NOT APPLICABLE TO MVP` | One controlled organization; no Tenant model or claim is authorized |
| Users | `PREPARED` | Four identities defined; not provisioned |
| Roles | `PARTIAL` | D088 certified; real external tokens not yet proven |
| DRC-AOA-001 data | `READY` | Canonical 30-line NDJSON exists and local demo is certified |
| Evidence import | `CERTIFIED` | R01 and PostgreSQL behavior certified |
| GitHub/AWS Evidence | `CERTIFIED OFFLINE` | Adapters certified; live pilot sandbox checks remain separate |
| Decision | `PARTIAL` | D081 certified; runtime R16 composition not implemented |
| Recommendation/ROI | `PARTIAL` | D082 certified; runtime R16 entry not implemented |
| Approval | `CERTIFIED` | D083/D088 backend behavior certified |
| Implementation/result | `CERTIFIED` | Ledger commands and policy certified |
| Ledger | `CERTIFIED` | Ordered append-only governance chain certified |
| Business Value | `CERTIFIED LOCAL` | Projection certified; runtime E2E pending |
| Workspace | `CERTIFIED CONTROLLED DEMO` | D091 complete; no commercial login |
| Docker runtime | `BLOCKED_EXTERNAL` | Upstream PostgreSQL/gosu Critical CVE fails D092 |
| Flyway/persistence | `PASS BEFORE FINAL IMAGE` | Must rerun after final digest and D093 migration |
| Backup/restore | `NOT IMPLEMENTED` | D092 excludes it; volume persistence is not backup |
| Security | `PARTIAL` | JWT/RBAC certified; external IdP and image gate pending |
| Observability | `PREPARED ONLY` | Design ready; Sprint 4.4 not open |
| Runtime E2E | `BLOCKED` | Requires image gate, D093 implementation and Keycloak |
| Documentation | `PARTIAL` | Preparation artifacts exist; official sync waits for gate completion |

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

Review basis: current repository at the time this preparation was created,
frozen D081-D093 contracts and certified implementation through Sprint 4.2.

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

### Amber

1. D093 is frozen but not implemented. R16, `findByCaseId`, the unique case
   constraint and its tests are absent by design.
2. Project-control documents still identify Sprint 4.3 as `NEXT` and do not
   capture this temporary external blocker or D093 preparation. This must wait
   for an authorized synchronization gate; changing it here would misreport
   certification.
3. D091 provides controlled token paste, not commercial login or SSO.
4. No external HTTPS issuer/JWKS has passed D087 in the runtime.
5. No application observability dependencies or endpoints exist yet; this is
   deferred to Sprint 4.4.
6. Docker volume persistence is not backup/restore. D092 explicitly excludes
   backup, which must be resolved before real customer data if the pilot terms
   require recoverability beyond container recreation.
7. GitHub/AWS adapters are certified offline. Live read-only sandbox checks
   remain pre-pilot evidence, not a reason to expand connector scope.
8. The canonical pack includes Jira/manual and AI-usage facts not produced by
   the two live connectors. The pilot runbook must identify the legitimate
   source and accountable owner for each imported fact.
9. Multi-tenancy is intentionally absent. The pilot must remain one controlled
   organization and must not be represented as tenant isolation.

### Red

1. The currently pinned official PostgreSQL image contains one fixable
   Critical vulnerability in upstream `gosu`/Go stdlib according to the D092
   Trivy gate. Sprint 4.3 cannot be certified until an official clean image is
   selected and scanned.

No objective contradiction currently requires changing the Domain, D087,
D088, D091, D092 or D093. The outstanding work is delivery and external
conformance, not an architecture redesign.

## 7. Test Readiness

| Suite | Existing evidence | Required before pilot |
|---|---|---|
| Domain/Application unit | Certified deterministic policies and governance | Add focused D093 composition tests |
| REST contracts | R01-R15 certified | Add exact R16 contract tests |
| JWT/RBAC | Controlled RS256 and role matrix certified | Repeat with real Keycloak tokens |
| PostgreSQL | Repositories, Flyway and 31 integration tests certified | Add case uniqueness/recovery tests and rerun on final image |
| Ledger | Sequence, replay and concurrency certified | Execute through real runtime with real roles |
| Frontend | 35 tests and 9 Playwright acceptance tests certified | Load the runtime-composed case with a real token |
| Connectors | GitHub/AWS offline protocol tests certified | Controlled live read-only smoke evidence |
| Docker | Runtime implemented | Clean image gate and complete data recreation proof |
| Observability | None at runtime | Sprint 4.4 contract, implementation and certification |

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
- missing D093 implementation or duplicate case graph;
- direct SQL/fixture used to fabricate the commercial case;
- broken Ledger sequence or untraceable Business Value;
- missing data recovery commitment for real customer data;
- secrets/tokens/raw payloads in logs or evidence; or
- Sprint 4.4/4.5 started before their authorized gates.

## 10. Current Decision

```text
PILOT READINESS: NO-GO
ARCHITECTURE HEALTH: SOUND WITH EXPLICIT DELIVERY GAPS
SPRINT 4.3: BLOCKED_EXTERNAL
SPRINT 4.4: NOT OPEN
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
