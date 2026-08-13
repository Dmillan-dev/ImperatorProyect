# 02 - Pilot Execution Runbook

Status: **PREPARATION ONLY / DO NOT EXECUTE**

This runbook describes the future controlled operation. It does not authorize
runtime changes, customer data, D093 implementation, Sprint 4.4 or Sprint 4.5.

## 1. Execution Modes

| Mode | Purpose | Data | Authorization |
|---|---|---|---|
| Rehearsal | Prove the exact technical/commercial story and supply the E2E/persistence evidence needed by D092 | Canonical synthetic 30-Evidence pack | Only after the D092 image-vulnerability subgate, D093 implementation and external IdP gates |
| Customer pilot | Validate actual customer value | Approved customer normalized facts | Separate pilot, data and policy authorization required |

Never turn a rehearsal into a customer pilot by replacing values in-place.

## 2. RACI

| Activity | Accountable | Responsible | Consulted | Informed |
|---|---|---|---|---|
| Pilot scope and exit | Executive Sponsor | IMPERATOR Pilot Lead | Finance, Platform | All participants |
| Evidence approval | Business Owner | Source-system owners | Security, Finance | Pilot Lead |
| Connector permissions | Customer Security | Platform/Cloud Owner | IMPERATOR Security | Executive Sponsor |
| Keycloak users/claims | Customer Security | IdP Operator | IMPERATOR Security | Pilot Lead |
| ROI assumptions | Finance/FinOps | Finance Analyst | Business, Platform | Executive Sponsor |
| Recommendation review | Executive Sponsor | Assigned Admin | Finance, Platform | Auditor |
| External implementation | Customer Platform Owner | Platform Engineer | Business Owner | Pilot Lead |
| Result validation | Finance/FinOps | Finance validator | Platform, Business | Executive Sponsor |
| Runtime and backup | IMPERATOR Security/Operations | Pilot Operator | Customer Security | Pilot Lead |
| Incident decision | Customer Security + IMPERATOR Security | Incident Lead | Executive Sponsor | Affected participants |

## 3. Stage 0 - Authorization Preflight

### 3.1 Certification Rehearsal

- [ ] D092 image reports contain zero fixable Critical vulnerabilities.
- [ ] D093 implementation is authorized and its default/PostgreSQL gates pass.
- [ ] The external Keycloak issuer/JWKS and D087 role probes are ready.
- [ ] Only the approved canonical synthetic dataset will be used.
- [ ] No customer data, public exposure or production provider access is used.

This rehearsal supplies the E2E and persistence evidence required to complete
D092. It therefore does not wait for Sprint 4.3, Sprint 4.4 or Sprint 4.5 to be
certified.

### 3.2 Customer Pilot

- [ ] Signed Charter, NDA and applicable DPA/order form exist.
- [ ] Controller/processor roles and hosting location are confirmed.
- [ ] One organization, one case and named participants are fixed.
- [ ] Sprint 4.3, Sprint 4.3.1, Sprint 4.4 and Sprint 4.5 are complete.
- [ ] D093 implementation and certification are complete.
- [ ] Trivy reports zero fixable Critical vulnerabilities for final images.
- [ ] Dedicated host, host encryption and patch ownership are verified.
- [ ] Backup restore and full-environment deletion have been rehearsed.
- [ ] Incident and support contacts completed a tabletop review.
- [ ] No public IMPERATOR exposure is required by the agreed pilot method.

Any unchecked customer-pilot item is `NO-GO` for customer data. Passing the
rehearsal never authorizes customer data by itself.

## 4. Stage 1 - Identity And Access Preflight

1. Verify external Keycloak discovery and JWKS over trusted HTTPS.
2. Verify issuer, audience `imperator-api`, RS256 and active `kid`.
3. Verify four short-lived pilot identities with one exact role each.
4. Confirm the Admin `sub` will equal the R16 `requiredApproverId`.
5. Run D087 negative probes without recording tokens.
6. Run D088 role probes and confirm denied calls write nothing.
7. Record only sanitized pass/fail evidence and correlation IDs.

Stop immediately for token leakage, non-UUID `sub`, role arrays, wrong
audience, weak TLS or authorization ambiguity.

## 5. Stage 2 - Provider And Evidence Preflight

1. Review GitHub fine-grained token scope against D089.
2. Review AWS principal policy and STS account equality against D090.
3. Confirm one repository, one account, one Region and exact correlation.
4. Validate the owner and approval status of every Evidence register row.
5. Hash the canonical rehearsal file and record the approved hash.
6. Scan input for Restricted data before R01.
7. Confirm all raw payload modes are `not_stored`.

Live provider smoke checks remain separately authorized and must use a
non-customer sandbox until real-customer access is approved.

## 6. Stage 3 - Runtime Start

Future authorized operator sequence:

1. Record Git SHA, image digests, Docker/Compose versions and UTC start time.
2. Verify secret files exist without displaying their values.
3. Validate Compose configuration.
4. Start the certification-candidate runtime for the rehearsal, or the exact
   certified runtime for a customer pilot, and wait for all frozen health gates.
5. Verify Flyway migrate/validate/no-op and least-privilege provisioning.
6. Verify backend/PostgreSQL are not externally exposed.
7. Verify workspace is reachable only through the approved loopback method.

No seed, SQL insert, fixture loader or authentication bypass is allowed.

## 7. Stage 4 - Rehearsal Evidence And Composition

1. As `ADMIN`, import the exact 30 Evidence records through R01.
2. Assert 30 accepted, zero rejected and no stored raw payload.
3. Replay R01 and prove no duplicate Evidence.
4. Collect the 28 predecision UUIDs from the authoritative result.
5. Invoke implemented R16 with stable Decision/Recommendation UUIDs.
6. Assert exactly one Decision and one `MODEL_CHANGE` Recommendation.
7. Verify EUR 19,440 estimated annualized savings, confidence 92 and `LOW`
   risk under `DRC-AOA-001-v1`.
8. Replay R16 and prove stable identities and no duplicate graph.
9. Load the same Decision through the D091 workspace.

Composition appends no Ledger entry. Failure after T1 leaves one recoverable
`CREATED` Decision and must be resumed with the exact request.

## 8. Stage 5 - Human Governance

1. Assigned `ADMIN` reviews Evidence, assumptions, ROI and risk.
2. Admin approves, rejects or defers through the frozen command.
3. For the positive rehearsal, verify one immutable `approved` entry.
4. In rehearsal mode, use only the canonical synthetic implementation and
   result facts represented by Evidence 29 and Evidence 30; perform no real
   customer action.
5. In an explicitly authorized customer pilot, the Customer Platform Engineer
   performs the approved action outside IMPERATOR and the approved actual facts
   replace the rehearsal facts through the separately authorized policy.
6. `PLATFORM_ENGINEER` marks implementation using the applicable Evidence 29.
7. After the agreed measurement period, `FINANCE` validates the applicable
   Evidence 30.
8. Verify Ledger order:
   `approved -> implementation_marked -> result_validated`.
9. Replay operation IDs and prove no additional Ledger entries.

IMPERATOR never changes the AI model, AWS resource or GitHub repository.

## 9. Stage 6 - Business Value And Persistence

Verify the canonical rehearsal result:

| Metric | Expected |
|---|---:|
| Annualized baseline | EUR 28,080.00 |
| Annualized post-action cost | EUR 9,000.00 |
| Actual transition cost | EUR 120.00 |
| Realized annualized savings | EUR 18,960.00 |
| Variance from estimate | EUR -480.00 |

Then:

1. Record object IDs, counts, Ledger order and Business Value.
2. Stop Compose without deleting volumes.
3. Recreate the same certified runtime.
4. Re-run Flyway validation and permissions.
5. Retrieve equal Evidence, Decision, Recommendation, Ledger and Business
   Value.
6. Prove no duplicates or missing links.

This proves volume persistence. It does not replace backup/restore testing.

## 10. Incident And Stop Procedure

Stop processing and preserve safe evidence for:

- suspected unauthorized access or secret/token exposure;
- Restricted/customer content in input, output, logs or screenshots;
- write-capable provider permission;
- fixable Critical vulnerability;
- broken Ledger ordering, duplicate case graph or unexplained data mutation;
- invalid ROI provenance or customer dispute of an authoritative input;
- unavailable backup or failed restore; or
- scope, hosting or legal-term deviation.

Response:

1. stop imports and governance commands;
2. do not delete or edit database rows manually;
3. revoke affected external credentials;
4. isolate the dedicated host when security is involved;
5. record UTC time, safe correlation IDs, affected scope and owner;
6. notify named customer/IMPERATOR contacts under the agreed terms;
7. decide resume, restore, iterate or terminate; and
8. preserve no token, raw payload or personal data in the incident report.

## 11. Closeout

- [ ] Customer reviews the final Evidence, Ledger and Business Value.
- [ ] Acceptance report records `GO`, `ITERATE` or `NO-GO`.
- [ ] No guaranteed-savings statement is made.
- [ ] Source credentials and pilot users are revoked or disabled.
- [ ] Customer data and backups are returned/deleted on the agreed date.
- [ ] Deletion evidence is recorded without retaining deleted content.
- [ ] Anonymized learning is added to WP11 only with customer approval.
- [ ] Actual hours and artifacts are recorded; nothing is reconstructed.
