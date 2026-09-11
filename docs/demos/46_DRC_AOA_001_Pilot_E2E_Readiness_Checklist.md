# 46 - DRC-AOA-001 Pilot E2E Readiness Checklist

Status: **LOCAL CERTIFICATION REHEARSAL PASS / PILOT CONFORMANCE PENDING**

This artifact records the certified local rehearsal and prepares the future
pilot proof. It authorizes no endpoint, code, migration, test, IdP, Docker
change or sprint transition.

## 1. Current Boundary

```text
Sprint 4.3                  CERTIFIED under D095
Docker runtime             PASS
D094 supply-chain gate     PASS
D093/R16 implementation    PASS
Local D087/D088 conformance PASS
Local runtime E2E          PASS
External Pilot Identity    NOT PROVISIONED / REQUIRED BEFORE SPRINT 4.5
```

Sprint 4.3.1 is complete and D096 freezes the observability contract. Sprint
4.4 implementation is present and certification closure is the current gate.
Sprint 4.5 remains unauthorized until its preceding gates, including external
Pilot Identity Conformance, pass.

## 2. Authority Set

| Concern | Authority |
|---|---|
| Decision and Recommendation policies | D081 and D082 |
| Review, Ledger and result validation | D083 |
| REST R01-R15 | D086 |
| JWT and actor identity | D087 |
| RBAC and Evidence visibility | D088 |
| Workspace | D091 |
| Docker certification | D092 |
| R16 case composition | D093 |
| Canonical local outcome | `45_End_To_End_Business_Value_Demo.md` |
| Canonical data | `src/test/resources/evidence/drc-aoa-001-business-value-demo.jsonl` |

D093 resolves the earlier composition ambiguity. It freezes R16 as
`POST /api/v1/decisions`, `ADMIN` only, after R01. Its implementation and local
runtime certification are complete without changing D081 or D082.

## 3. Domain Interpretation

The pilot proves this chain:

```text
30 normalized Evidence facts
  -> case correlation DRC-AOA-001
  -> one Decision
  -> one deterministic Recommendation and ROI
  -> human approval
  -> external implementation fact
  -> result validation
  -> non-persisted Business Value projection
  -> immutable ordered Ledger trace
```

`Case` is not a new aggregate or table. It is the canonical `caseId` and
correlation context carried by Evidence and Decision. The E2E must not invent a
Case entity, workflow service or generic case-management API.

## 4. Canonical Data And Identities

The NDJSON file contains exactly 30 accepted Evidence items:

| Stage | Conceptual references | Use |
|---|---|---|
| Predecision | `E-JIRA-001` through `E-JIRA-004` | Business origin/context |
| Predecision | `E-GH-001` through `E-GH-004` | Code and deployment |
| Predecision | `E-AWS-001` through `E-AWS-004` | Cost and attribution |
| Predecision | `E-AI-001` through `E-AI-005` | AI usage and quality |
| Predecision | `E-USAGE-001` through `E-USAGE-003` | Usage and value |
| Predecision | `E-OWNER-001` through `E-OWNER-003` | Ownership and approval |
| Predecision | `A-ROI-001` through `A-ROI-004` plus policy provenance | ROI and policy |
| Postdecision | Evidence 29 | `implementation_marked` only |
| Postdecision | Evidence 30 | `result_validated` only |

R16 receives exactly the first 28 imported Evidence UUIDs. `E-JIRA-001` is the
originating Evidence. Evidence 29 and 30 must not influence Recommendation or
estimated ROI.

Runtime Decision, Recommendation, Ledger and actor UUIDs are generated or
obtained for the live operation and then reused exactly for retries. Fixed UUIDs
in the local demo are test fixtures, not an IdP or runtime identity contract.

## 5. Exact Runtime Sequence

| Step | Actor | Interface | Expected business result |
|---|---|---|---|
| 1 | `ADMIN` | R01 Evidence import | 30 accepted, 0 rejected |
| 2 | `ADMIN` | R16 | One Decision and one Recommendation |
| 3 | Any read role | R02-R08 | Complete review context is readable |
| 4 | Assigned `ADMIN` | R09 approve | One immutable `approved` entry |
| 5 | `PLATFORM_ENGINEER` | R12 mark implemented | One immutable `implementation_marked` entry using Evidence 29 |
| 6 | `FINANCE` | R13 validate result | One immutable `result_validated` entry using Evidence 30 |
| 7 | Any read role | R14 | Realized Business Value available |
| 8 | Any read role | R08/R15 | Ordered trace reconstructable |
| 9 | Operator | Docker recreation | Same graph and projection recovered |

Composition itself appends no Ledger entry. Decision creation and
Recommendation generation are pre-governance timeline facts. The Ledger begins
with the first human governance outcome.

## 6. Canonical Assertions

| Assertion | Exact expected value |
|---|---|
| Case | `DRC-AOA-001` |
| Recommendation type | `MODEL_CHANGE` |
| Policy | `DRC-AOA-001-v1` |
| Current monthly cost | EUR 2,340.00 |
| Projected monthly cost | EUR 720.00 |
| Estimated monthly recovery | EUR 1,620.00 |
| Estimated annualized savings | EUR 19,440.00 |
| Confidence | 92% |
| Risk | `LOW` |
| Annualized baseline cost | EUR 28,080.00 |
| Annualized post-action cost | EUR 9,000.00 |
| Actual transition cost | EUR 120.00 |
| Realized annualized savings | EUR 18,960.00 |
| Variance | EUR -480.00 |
| Ledger order | `approved`, `implementation_marked`, `result_validated` |

Business Value remains unavailable before the final validation entry and is
derived from authoritative Decision, Recommendation and Ledger state rather
than persisted as an independent entity.

## 7. Permissions Matrix

| Capability | ADMIN | PLATFORM_ENGINEER | FINANCE | AUDITOR |
|---|---:|---:|---:|---:|
| Import Evidence R01 | Allow | Deny | Deny | Deny |
| Compose R16 | Allow | Deny | Deny | Deny |
| Read R02-R08/R14-R15 | Allow | Allow | Allow | Allow |
| Approve/reject | Allow when assigned | Deny | Deny | Deny |
| Defer | Allow | Allow | Allow | Deny |
| Mark implementation | Deny | Allow | Deny | Deny |
| Validate result | Deny | Deny | Allow | Deny |

Every denied command must return the frozen error without changing Decision,
Recommendation, Evidence, Ledger or Business Value state.

## 8. Positive Scenarios

| ID | Scenario | Required assertion |
|---|---|---|
| P01 | Valid Keycloak token for each role | D087 accepts and D088 applies exact role |
| P02 | First R01 import | 30 accepted and stored once |
| P03 | Equivalent R01 replay | No duplicate Evidence |
| P04 | First R16 composition | `201`, one Decision and Recommendation |
| P05 | Equivalent R16 replay | `200`, `replayed=true`, same identities |
| P06 | Resume after T1-only state | T2 completes, `resumed=true`, no duplicate Decision |
| P07 | Assigned Admin approval | Decision approved and one Ledger append |
| P08 | Platform implementation mark | Evidence 29 linked and ordered |
| P09 | Finance result validation | Evidence 30 linked and ordered |
| P10 | Business Value read | Exact canonical values |
| P11 | Workspace load | Same authoritative case visible through D091 |
| P12 | Docker down/up without volume deletion | Equal graph, Ledger and projection |

## 9. Negative And Recovery Scenarios

| ID | Condition | Expected result | Write invariant |
|---|---|---|---|
| N01 | Missing/invalid JWT | `401` | No write |
| N02 | Valid role without route permission | `403` | No write |
| N03 | R16 approver differs from JWT `sub` | D093 validation failure | No write |
| N04 | Missing/ineligible origin Evidence | `404` or frozen `409` | No Decision |
| N05 | Missing, stale, Restricted or invalid supporting Evidence | `409 RECOMMENDATION_NOT_READY` | At most one recoverable `CREATED` Decision |
| N06 | Evidence 29/30 included in R16 | Validation failure | No Recommendation |
| N07 | Same ID/case with conflicting immutable tuple | `409` | No overwrite |
| N08 | Concurrent equivalent R16 requests | One authoritative graph | No duplicate |
| N09 | Explanation Provider unavailable | Composition succeeds without explanation | Deterministic result unchanged |
| N10 | Approval by non-assigned Admin | Frozen business-rule failure | No Ledger append |
| N11 | Implementation before approval | Frozen transition failure | No Ledger append |
| N12 | Result validation before implementation | Frozen transition failure | No Ledger append |
| N13 | Ledger operation replay | `replayed=true` | No second entry |
| N14 | Stale expected predecessor | Conflict | Linear Ledger preserved |
| N15 | Database failure inside one transaction | That transaction rolls back | Prior committed step remains authoritative |
| N16 | Restart after T1 only | Same request resumes T2 | No compensation delete |

## 10. Test Inventory

No test is created by this document. The following inventory records the
coverage required by the owning implementation and certification gates.

| Test layer | Required coverage |
|---|---|
| Unit | R16 input validation, exact mapping, 28/2 Evidence split, replay classification and explanation independence |
| Application | Two-step orchestration, T1/T2 recovery, no Ledger side effect and use of ports only |
| Repository | `findByCaseId`, unique case constraint, concurrent create resolution and no overwrite |
| API contract | Exact R16 request/response, `201/200/401/403/404/409/422`, safe four-field errors and correlation |
| Authorization | Real D087 tokens for all roles, Admin-subject equality and denied-call no-write proof |
| Ledger | Strict sequence, append-only behavior, replay, predecessor conflict and evidence links |
| Persistence | Flyway migrate/validate/no-op, rollback, restart recovery, counts and referential integrity |
| Frontend | Existing D091 workspace loads composed state; no new composition behavior in browser |
| Runtime E2E | R01 -> R16 -> R09 -> R12 -> R13 -> R14 -> Docker recreation |
| Security | No token, secret, raw payload, SQL detail or confidential Evidence leak |

Existing suites remain regression prerequisites. The focused D093 tests augment
rather than replace JWT, RBAC, connector, repository, REST, frontend or
PostgreSQL certification suites.

## 11. E2E Preconditions

- [x] Final PostgreSQL image reports zero fixable High/Critical vulnerabilities.
- [x] Final image inputs and SHA-tagged outputs are pinned and recorded.
- [x] D093 implementation is authorized, implemented and locally certified.
- [x] Flyway migrate, validate and no-op migrate pass on the final runtime.
- [x] PostgreSQL grants, health and persistent volume pass.
- [ ] External Keycloak passes the D087 conformance matrix.
- [ ] Four real pilot identities exist with one exact role each.
- [ ] Canonical NDJSON hash is recorded and starts from an empty business set.
- [ ] No SQL seed, direct table write or startup fixture is used.
- [ ] Correlation IDs, operation IDs, Git SHA and start time are recorded.

## 12. Future Pilot Execution Checklist

### Authentication

- [ ] Validate issuer, JWKS, RS256, `kid`, audience, UUID `sub` and one role.
- [ ] Prove invalid token variants return `401` without leaking token data.
- [ ] Prove D088 positive and negative permissions with real tokens.

### Evidence And Composition

- [ ] Import 30 Evidence records through R01 as Admin.
- [ ] Re-import and prove idempotency.
- [ ] Compose through R16 using 28 predecision UUIDs.
- [ ] Verify one Decision, one Recommendation and exact ROI.
- [ ] Replay R16 and prove stable identities and no duplicates.

### Governance And Value

- [ ] Approve as the assigned Admin.
- [ ] Mark implementation as Platform Engineer using Evidence 29.
- [ ] Validate result as Finance using Evidence 30.
- [ ] Prove exact Ledger order and immutable replay behavior.
- [ ] Read exact Business Value and complete traceability.
- [ ] Read the same case as Auditor without command authority.

### Persistence

- [ ] Record object IDs, row counts, Ledger chain and Business Value.
- [ ] Run normal Compose down without deleting volumes.
- [ ] Recreate the exact certified runtime.
- [ ] Re-run Flyway validation and permission provisioning.
- [ ] Retrieve equal Evidence, Decision, Recommendation, Ledger and projection.
- [ ] Prove no duplicate or missing object after recreation.

### Evidence Package

- [ ] Record Git SHA, final image digests and clean Trivy reports.
- [ ] Record safe route outcomes and correlation IDs.
- [ ] Record deterministic financial assertions and object counts.
- [ ] Record negative JWT/RBAC outcomes without token text.
- [ ] Record pre/post-recreation equality.
- [ ] Record all guardian results.

## 13. Completion Rule

This checklist passes only through authorized runtime interfaces. Repository
calls, SQL inserts, startup fixtures or manually assembled tokens cannot
substitute for either the certified local rehearsal or the future pilot E2E.

The local runtime rehearsal is certified under D095. Operational external
identity and customer-pilot execution remain pending:

```text
LOCAL RUNTIME REHEARSAL: PASS
PILOT IDENTITY CONFORMANCE: PENDING
PILOT EXECUTION: NO-GO
```
