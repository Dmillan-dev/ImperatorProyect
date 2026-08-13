# 04 - Pilot Evidence Pack And Acceptance

Status: **PREPARATION TEMPLATE / NO RESULTS RECORDED**

## 1. Purpose

Define the minimum defensible evidence package for technical certification,
commercial acceptance and WP11 Pilot and Customer Validation. This file must
never claim an action, test, saving or customer approval that did not occur.

## 2. Storage Classes

| Class | Location | Repository content |
|---|---|---|
| Public project evidence | Git Markdown | Architecture/status without customer data |
| Internal sanitized evidence | Controlled IMPERATOR store | Hashes, pass/fail, safe versions and correlation IDs |
| Customer Confidential | Customer-approved encrypted store | Source approvals, reports and signed acceptance |
| Restricted/secret | Approved secret/incident system only | Never Git or pilot evidence pack |

Git records only artifact IDs and sanitized summaries for confidential items.

## 3. Pilot Manifest

| Field | Value |
|---|---|
| Pilot ID | `UNASSIGNED` |
| Mode | `REHEARSAL` or separately authorized `CUSTOMER_PILOT` |
| Customer alias | `UNASSIGNED` |
| Decision case | `DRC-AOA-001` |
| Start/end | `UNASSIGNED` |
| Git SHA | `UNASSIGNED` |
| Image digests | `UNASSIGNED` |
| Dataset hash | `UNASSIGNED` |
| IdP issuer host | `UNASSIGNED` without credentials |
| GitHub boundary | Controlled artifact ID only |
| AWS boundary | Controlled artifact ID only |
| Evidence register approval | Controlled artifact ID |
| DPA/NDA/order form | Controlled legal-system ID only |
| Final outcome | `GO`, `ITERATE`, `NO-GO` or `NOT RUN` |

## 4. Technical Evidence Inventory

| ID | Evidence | Required content |
|---|---|---|
| `PE-001` | Source baseline | Clean Git status, SHA and authorized gate |
| `PE-002` | Image security | Exact digests and zero fixable Critical Trivy result |
| `PE-003` | Runtime start | Compose validation/build/start and service health |
| `PE-004` | Database | PostgreSQL version, Flyway migrate/validate/no-op and grants |
| `PE-005` | Identity | Safe issuer/JWKS reachability and D087 positive/negative results |
| `PE-006` | Authorization | D088 role matrix with denied-call no-write proof |
| `PE-007` | Evidence import | R01 counts, IDs, replay and raw-payload mode |
| `PE-008` | Composition | R16 first/resume/replay/conflict and graph counts |
| `PE-009` | Recommendation/ROI | Policy, Evidence count, confidence, risk and exact estimate |
| `PE-010` | Governance | Actor roles, operation IDs and ordered Ledger entries |
| `PE-011` | Business Value | Baseline, post-action, transition, realized and variance values |
| `PE-012` | Persistence | Before/after runtime-recreation equality |
| `PE-013` | Backup/restore | Backup checksum and isolated restore verification |
| `PE-014` | Deletion | Revocation and environment/backup destruction evidence |
| `PE-015` | Observability | Health, safe logs, metrics and alert evidence after Sprint 4.4 |

No item includes tokens, passwords, private keys, raw payloads, SQL data dumps,
customer conversations or personal contact details.

## 5. Commercial Evidence Inventory

| ID | Evidence | Acceptance question |
|---|---|---|
| `CE-001` | Executive review | Was the Decision understood in under 15 minutes? |
| `CE-002` | Platform review | Is the technical lineage credible and actionable? |
| `CE-003` | Finance review | Are baseline, assumptions and outcomes acceptable? |
| `CE-004` | Security review | Were data and access boundaries respected? |
| `CE-005` | Action decision | Was the Recommendation approved, rejected or deferred? |
| `CE-006` | Result validation | Is realized value supported by post-action Evidence? |
| `CE-007` | Expansion signal | Did the customer request another measurable Decision? |

## 6. R&D WP11 Record

Record only actual activity:

| Field | Required value |
|---|---|
| Activity ID | Stable ID linked to WP11 |
| Date and actual hours | Contemporaneous record, never estimated later |
| Participants/roles | Role or approved alias |
| Technical uncertainty | What remained unknown before the activity |
| Method | Rehearsal, test, interview, validation or measurement |
| Artifact IDs | `PE-*`, `CE-*`, commits and reports |
| Result | Confirmed, rejected or unresolved hypothesis |
| Learning | Product/architecture/commercial implication |
| Reviewer | Named approved reviewer or role |

Customer value, hours and test results are not R&D evidence until observed and
supported by artifacts.

## 7. Acceptance Scorecard

| Criterion | Mandatory evidence | Status |
|---|---|---|
| Traceability | `PE-007` through `PE-011` | `NOT RUN` |
| Explainability | `PE-009`, `CE-001` | `NOT RUN` |
| Governance | `PE-006`, `PE-010`, `CE-005` | `NOT RUN` |
| Economic validity | `PE-011`, `CE-003`, `CE-006` | `NOT RUN` |
| Persistence/recovery | `PE-012`, `PE-013` | `NOT RUN` |
| Security/privacy | `PE-002`, `PE-005`, `PE-006`, `PE-014`, `CE-004` | `NOT RUN` |
| Operability | `PE-003`, `PE-004`, `PE-015` | `NOT RUN` |
| Commercial signal | `CE-001`, `CE-007` | `NOT RUN` |

One failed mandatory security, data-integrity, identity, Ledger, backup or
deletion criterion forces `NO-GO` until corrected and re-evidenced.

## 8. Final Acceptance Record

```text
Pilot ID:
Mode:
Decision case: DRC-AOA-001
Technical gate: PASS / FAIL / NOT RUN
Security gate: PASS / FAIL / NOT RUN
Commercial gate: GO / ITERATE / NO-GO / NOT RUN
Validated realized value: amount and currency, or NOT VALIDATED
Open risks:
Corrective actions:
Customer Executive Sponsor sign-off artifact ID:
Customer Finance sign-off artifact ID:
Customer Security sign-off artifact ID:
IMPERATOR Pilot Lead sign-off artifact ID:
Closeout/deletion artifact ID:
```

Signatures and confidential reports remain in the approved commercial/legal
system. This repository keeps only the sanitized artifact references.

## 9. Completion Rule

The package is complete only when every claimed result links to retained
evidence and every retained customer artifact follows the signed data terms.
Missing evidence is reported as `NOT RUN` or `UNVERIFIED`, never inferred.

