# TFG Demo And Defense Guide

## Current Owner Decision And Sprint — 2026-10-06

**A-S2 technical GO / formal GO for the exact approved local synthetic record. TFG-A remains IN PROGRESS. A-S3 is authorized for evidence/versioning only; A-S4 is later and AWS remains blocked.** R-20 is ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC for the current 23-CVE residual, not declared inexploitable. Exclusive expiry **2026-10-09T00:00:00+02:00 Europe/Madrid = 2026-10-08T22:00:00Z**; no renewal or future-image transfer. R-23 disposition is independently approved while status remains **OPEN / NO FIX AVAILABLE**; it is not closed or included in R-20. Zero fixable High/Critical and zero secrets remain mandatory. No further upgrade is authorized. See the [registered decisions](evidence/2026-10-06-a-s2-acceptance/README.md), [security gate](TFG_A_SECURITY_CLOSURE_GATE.md) and [A-S3 work order](A_S3_EVIDENCE_VERSIONING.md).

Document status: **TFG-A IN PROGRESS / CURRENT TECHNICAL REHEARSAL CAPTURED; R-19/R-21/R-22 CORRECTED FINDINGS CLOSED; A-S2 TECHNICAL AND FORMAL GO; A-S3 ACTIVE**

## Defense Objective

In approximately twelve minutes, demonstrate that IMPERATOR solves one
traceability problem, has defensible engineering boundaries and is prepared
for a controlled AWS evolution without overstating unexecuted work.

## Recommended Sequence

|        Time | Demonstration                            | Principal claim                                                    |
| ----------: | ---------------------------------------- | ------------------------------------------------------------------ |
|   0:00-1:00 | Problem and `DRC-AOA-001`                | Decisions need traceable Evidence, authority and value             |
|   1:00-2:00 | Local/AWS architecture                   | The same modular application supports both environments            |
|   2:00-4:00 | Import and compose the synthetic case    | Evidence, Decision and Recommendation are linked and deterministic |
|   4:00-5:30 | Inspect Recommendation, ROI and Evidence | Business calculation is code-owned, not model-owned                |
|   5:30-7:00 | Perform human review                     | Role and assigned actor retain authority                           |
|   7:00-8:00 | Mark implementation and validate result  | Ledger ordering controls the value loop                            |
|   8:00-9:00 | Inspect Business Value and Ledger        | Expected and realized values remain traceable                      |
|  9:00-10:00 | Explain Bedrock boundary                 | Implemented offline; live result shown only if later executed      |
| 10:00-11:00 | Show CI/security evidence                | Quality and supply-chain claims are objective                      |
| 11:00-12:00 | AWS target, cost and limitations         | Prepared architecture is separated from live validation            |

## Local Demo Preconditions

- accepted source SHA and clean worktree recorded;
- canonical synthetic dataset only;
- exact runtime versions and image digests recorded;
- no customer data or external credential displayed;
- local JWT/RBAC procedure available without persisted bearer tokens;
- application flow pre-rehearsed from a clean database; and
- final screenshots and recording contain no secret or personal metadata.

## Required Demonstration Assertions

- the Recommendation and ROI are deterministic;
- Bedrock cannot approve, calculate ROI or write the Ledger;
- an unauthorized role is denied and creates no write;
- the assigned human actor performs governance commands;
- the Ledger is ordered and append-only;
- Business Value depends on validated implementation/result facts;
- repeated reads do not invoke an explanation provider; and
- every AWS statement uses the correct evidence status.

## Claim Language

Use:

- "implemented and validated offline" for Terraform and Bedrock adapter work;
- "target architecture" for resources not deployed;
- "synthetic estimate" for the canonical ROI values;
- "pilot constraint" for Single-AZ RDS and one task/service; and
- "aligned with AWS competency domains" rather than "certifies SAA skills".

Do not use:

- "deployed on AWS" before an accepted live run;
- "highly available" for the current target;
- "Bedrock Guardrails" for application validation;
- "real savings" for synthetic Business Value; or
- "production ready" for a disposable pilot architecture.

## Failure-Safe Defense

Plan A is the reproducible local runtime. Plan B is a sanitized recording and
evidence pack from the accepted SHA. A future AWS demonstration is additive;
the defense must remain complete if AWS, Keycloak, internet or Bedrock is
unavailable.

## Final Rehearsal Checklist

Current unit, integration and fixture-browser checks must be read from the
[Evidence Register](04_EVIDENCE_REGISTER.md). Playwright intercepts API calls
with synthetic fixtures; it does not prove the live backend/database flow.
Native PostgreSQL V3 verification does not replace final Docker packaging,
image scans, API-composed rehearsal or persistence after recreation.

The [current local pack](evidence/2026-10-06-r21-r22/README.md) includes the executed
isolated Docker workflow, five screenshots and a short readable WebM. This is
the technical fallback capture, not a twelve-minute narrated oral rehearsal.
Approval was performed through the UI; implementation/result validation used
authenticated API commands to link the two post-action Evidence records,
followed by unmocked UI reads. The current picker lists already linked records;
do not claim that this capture selected the two previously unlinked records in
the UI. No UI expansion is needed for the MVP defense.

The current 2026-10-06 rehearsal repeats functional acceptance after authorized R-21/R-22 remediation:
five captures and a 27.16-second silent captioned fallback, plus persistence/readiness
on the same canonical images scanned. R-19/R-21/R-22 corrected findings are CLOSED;
A-S2 has technical and formal GO through the explicit current R20/R23 owner decisions.
The earlier R-19 28.20-second capture remains historical, not the current image binding. Bind the final
release pack to its accepted SHA. The present worktree pack retains its actual
input fingerprint. Owned scratch resources and secrets are cleaned only after
capture/persistence checks; normal project databases are preserved.

- [x] Canonical case startup/import/composition was rehearsed in isolated Docker.
- [x] Negative RBAC case denied the auditor without appending a fact.
- [x] Three ordered actors and synthetic realized value were verified.
- [x] Five readable screenshots and an offline WebM were captured and reviewed.
- [x] Container replacement preserved the complete case and V3 permissions.
- [x] Current fixable-only image vulnerability gate passes: authorized R-22 findings CLOSED; all-severity secrets zero.
- [x] R-19 original brace-expansion copies are authorized, patched and independently CLOSED; authorized R-21 source-map-js/sharp findings CLOSED; braces tracked separately R-23 OPEN.
- [x] Current R-20 binding is explicitly approved in the 2026-10-06 decision; historical approvals remain preserved without automatic transfer.
- [ ] Before any future runtime, verify expiry, image identity and early-invalidation conditions again.
- [ ] Accepted release SHA and human review bind the final evidence pack.
- [ ] Complete the twelve-minute oral rehearsal using the sequence above.

- [ ] Memory terminology matches the evidence vocabulary.
- [ ] Demo identifiers and values match the accepted release.
- [ ] Local reset and startup were rehearsed.
- [ ] Negative RBAC case is safe and repeatable.
- [ ] Every screenshot has a source SHA and date.
- [ ] Cloud diagrams label target and live components separately.
- [ ] A current cost figure is shown only if TFG-B executed.
- [ ] Live Bedrock output is shown only if explicitly authorized and captured.
- [ ] Questions on trade-offs, security, cost and limitations have concise answers.
- [ ] The fallback recording opens without external services.

## A-S4 Material Prepared — 2026-10-07

Use the [developed memory](08_LOCAL_MVP_MEMORY.md), [timed spoken script](09_DEFENSE_SCRIPT_12_MIN.md) and
[preflight/run procedure](A_S4_LOCAL_DEFENSE.md). The 720-second agenda is planning, not a timed human rehearsal.
The current technical recording is27.16seconds with no audio; it is not the narrated defense.
A-S3 accepted SHA/CI and current Docker availability remain pending. Keep final rehearsal boxes unchecked.
