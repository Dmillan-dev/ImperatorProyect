# R-20 Current Security Decision

## Current review — 22 October 2026

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
