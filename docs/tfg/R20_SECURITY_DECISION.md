# R-20 Current Security Decision

## Current review — 11 November 2026

**NO CURRENT ACCEPTANCE.** The [current artifact review](evidence/2026-11-11-pr-security/README.md) records the minimal Next/APT candidate from source `e30c16a`. Its four exact images have 0 fixable H/C and 0 secrets with the recorded 11 November databases. There are **39 unique H/C residual CVEs**, compared with 21 on 2 November; the conservative review set retains all 23 earlier identifiers and adds 18, giving **41 CVEs**. These are distinct from the complete all-severity inventory.

The [decision draft](evidence/2026-11-11-pr-security/R20_REVIEW_REQUIRED.json) remains `accepted: false`. Its proposed exclusive expiry is **18 November 2026 00:00 Europe/Madrid (UTC+01:00)**, a proposal requiring explicit owner approval, not authorization. No historical acceptance is renewed, transferred or inferred from passing CI. New Kerberos, SQLite and zlib observations and vendor discrepancies remain visible in the individual matrix. R-23 disposition is independent. Current A-S3 remains NO-GO formal; complete candidate Docker rehearsal and human A-S4 remain pending. Local/synthetic scope only; AWS, production, real data and public exposure remain blocked.

## Historical review — 2 November 2026

**NO CURRENT ACCEPTANCE.** The [new proposal](evidence/2026-11-02-security-alerts/R20_PROPOSED_DECISION.json), source `2539635`, remains `accepted: false`, bound to three newly built image indices and the exact retained PostgreSQL index. It proposes exclusive expiry **9 November 2026 00:00 Europe/Madrid (UTC+01:00)**; no previous unapproved proposal became authorization.

The current conservative set is **23 CVEs, 21 H/C**. Debian reclassifies three historical util-linux CVEs as not affecting installed Bookworm code; new High **CVE-2026-119373 / SQLite** has no Bookworm fix and requires explicit review. Four exact images have 0 fixable H/C and 0 secrets; no residual is declared inexploitable. The GitHub/undici/Vitest fixes and Brotli/unused-driver remediation pass local regression. R-23 has an official withdrawn advisory and a separate disposition proposal, not automatic closure; Moderate minimatch tooling findings remain visible. Publication CI, owner decision and full candidate Docker rehearsal remain pending. [Evidence and viable local MVP route](evidence/2026-11-02-security-alerts/README.md). TFG-A IN PROGRESS; AWS BLOCKED.

## Historical review — 27 October 2026

**NO CURRENT ACCEPTANCE / PROPOSAL ONLY.** The [new exact-artifact proposal](evidence/2026-10-27-local-security/R20_PROPOSED_DECISION.json) remains `accepted: false`, for source `64c38c2` and four explicitly listed image indices/runtime manifests. It proposes exclusive expiry **3 November 2026 00:00 Europe/Madrid (UTC+01:00)**; that date is not authorization. The October 19 acceptance expired and the October 26 proposal was never approved.

Fresh image scans: 0 fixable H/C and 0 secrets per image. OpenSSL CVE-2026-84782 is repaired; the conservative subset is now 25 CVEs / 137 package bindings, 23 H/C, plus two newly observed Medium GnuTLS bindings requiring explicit review. The complete all-severity inventory is retained separately. Integration 35 PASS, unit 174 PASS, frontend 44 PASS; the full current Docker rehearsal remains pending. A separate development undici High has a published fix and is **not** part of the R-20 non-fixable proposal. R-23 remains independent and OPEN. [Evidence, scope and limitations](evidence/2026-10-27-local-security/README.md). A-S3 current candidate NO-GO, TFG-A IN PROGRESS, AWS BLOCKED.

## Historical review — 22 October 2026

**EXPIRED / NEW PROPOSAL PENDING.** The latest registered acceptance expired on 19 October at 00:00 Europe/Madrid. [The new proposal](evidence/2026-10-22-security-go-vite/R20_PROPOSED_DECISION.json) remains `accepted: false`, with four exact candidate image identities, 26 conservative residual CVEs / 137 package bindings, and proposed exclusive expiry 26 October 2026 00:00 Europe/Madrid (UTC+01:00). That date is a proposal, not authorization.

Fresh candidate scans report 0 fixable H/C and 0 secrets; PostgreSQL gosu is rebuilt with Go 1.26.10 and the Vite lockfile is corrected. A live candidate rehearsal and the 35 integration tests remain pending. No acceptance transfers from historical images. R-23 remains OPEN separately. TFG-A remains IN PROGRESS and AWS BLOCKED. [Evidence and limits](evidence/2026-10-22-security-go-vite/README.md).

## Historical decision — 6 October 2026

The acceptance and GO statements below describe the expired October 6 record only.

**ACCEPTED / TEMPORARY / LOCAL-SYNTHETIC. A-S2 technical and formal GO for the exact current record.**

The owner explicitly approved [the current proposal](evidence/2026-10-06-r20-applicability/r20-current-version-proposal.json). The [registered acceptance](evidence/2026-10-06-a-s2-acceptance/r20-current-acceptance.json) records authority/time, exact image indices/platform/configs, runtime fingerprint, matrix/vendor/scanner hashes, original proposal and 38 reviewed file snapshots. Historical October 2 acceptance and original proposal files are unchanged; this is a new explicit decision, not automatic transfer.

- Scope: local loopback TFG-A only, synthetic data only. AWS/production/real data/public or pilot exposure excluded.
- Residual: 23 OS CVEs / 115 package rows accepted conditionally, not repaired or declared inexploitable. Per-CVE applicability and native SQL/XML/operator uncertainties remain visible.
- Mandatory gates: backend/frontend/PostgreSQL fixable High/Critical = 0; secrets at all severities = 0. No scanner suppressions or gate changes.
- Exclusive expiry: **2026-10-09 00:00 Europe/Madrid (UTC+02:00) = 2026-10-08 22:00 UTC**. No renewal/extension/future-image transfer.
- Early invalidation: vendor fix, changed runtime/base/image/test/DB inputs, exposure/data/features, fixable H/C, secrets or unreviewed finding. Reassessment and explicit decision required.
- Owner authority is project-local. Codex verifies evidence; tutor/institutional approval is not claimed.

R23 is independent: [approved open disposition](evidence/2026-10-06-a-s2-acceptance/r23-approved-open-disposition.json), **OPEN / NO FIX AVAILABLE**, not closed or included in R20. Braces remains a development dependency; inspected production package absence does not mean arbitrary developer/CI input is safe.

See [applicability review](R20_APPLICABILITY_REVIEW.md), [current gate](TFG_A_SECURITY_CLOSURE_GATE.md), [A-S3](A_S3_EVIDENCE_VERSIONING.md), immutable [historical approval](evidence/2026-10-02-r20-acceptance/acceptance.json) and [approved review snapshots](evidence/2026-10-06-a-s2-acceptance/approved-38-files.json). No upgrade is authorized; TFG-A IN PROGRESS, A-S4 later and AWS blocked.
