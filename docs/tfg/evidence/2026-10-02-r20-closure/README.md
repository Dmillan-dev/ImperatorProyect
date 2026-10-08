# R-20 Closure Preparation — 2026-10-02

State: **TECHNICAL PREPARATION VERIFIED / EXPLICIT APPROVAL PENDING**.
**accepted=false; R-20 OPEN; R-19 OPEN and unauthorized; TFG-A IN PROGRESS.**

See the [decision](../../R20_SECURITY_DECISION.md),
[approval proposal](approval-proposal.json) and
[closure index](../TFG_A_R20_CLOSURE_PREPARATION_2026-10-02.json).

## Proposed Decision Boundary

Synthetic data and local loopback academic rehearsal only. No public, pilot,
AWS or customer exposure. The founder/project owner (user in this conversation)
is the project approver and risk owner; Codex is the technical evidence reviewer
and cannot accept risk. The tutor/supervisor reviews institutional criteria;
no personal tutor identity or institutional approval is recorded.

Expiry, if explicitly approved: **2026-10-09T00:00:00+02:00 Europe/Madrid**
(CEST), exactly **2026-10-08T22:00:00Z**. The interval starts only at the
actual recorded UTC approval instant and excludes the expiry boundary.
No renewal by silence. Earlier evidence/exposure changes require renewed review.

Approval would record **ACCEPTED TEMPORARILY**, with the 32-CVE residual
register still visible. It would not remediate vulnerabilities or close R-19,
the full TFG-A Security Closure Gate or institutional assessment requirements.
The current master-plan criterion remains unchanged until explicit approval.

## Image Discrepancy Resolved

[Binding resolution](image-binding-resolution.json) records original and final
OCI index, linux/amd64 manifest and configuration digests, layer comparisons
and archived actual Compose container image labels.

- Backend: identical platform manifest, configuration and 13 layers; provenance
  index changes only.
- Frontend: same runtime configuration and first eight layers; the two Next
  standalone/static output layers differ. The earlier exercised build is canonical.
- PostgreSQL: identical 18 layers and effective runtime configuration; the sole
  label difference is Compose service postgres versus postgres-permissions.

Canonical manifests/configurations/layers were recovered without a rebuild
from BuildKit and local image exports. Every recovered blob was verified against
its original descriptor. Single-platform OCI scan layouts have their own wrapper
indexes; provenance indexes remain separately identified. Later frontend
observability evidence is not transferred to the earlier build's different layers.
Original containers are gone; archived image labels/build records support identity,
with the persistence report's tag-query limitation explicitly retained.

## R-20-Only Revalidation

[Canonical scan evidence](canonical-scans.json) includes exact commands,
identities and raw-report hashes. Trivy **0.74.0**, scanner OCI digest
`sha256:62b1e65e8869bc4b4c6aa4fa2b21595256c7c2f6018a9d9ad61caf87187c1969`.
[Database identities](scan-db.json): vulnerability DB updated
2026-10-02T12:48:00.080865328Z; Java artifact index is the separately recorded
still-current cached snapshot. All three scans use the same pinned DB bytes.
No ignore-unfixed, suppression, dependency change or full regression rerun.

| Canonical image | High | Critical | Fixable High/Critical | Secrets | Strict exit |
| --------------- | ---: | -------: | --------------------: | ------: | ----------: |
| Backend         |    0 |        0 |                     0 |       0 |           0 |
| Frontend        |   53 |        4 |                     0 |       0 |           1 |
| PostgreSQL      |   81 |       15 |                     0 |       0 |           1 |

Language-package High/Critical: zero. All **153 package/CVE/version/status rows**
match the [reviewed 32-CVE matrix](../2026-10-02-r20/cve-matrix.json).
Strict exits 1 retain the absolute-zero failure; they are not reported as PASS.
Raw reports: [backend](backend-canonical-trivy.json),
[frontend](frontend-canonical-trivy.json), [PostgreSQL](postgres-canonical-trivy.json).

## Historical Preservation And Hashes

The previous [analysis index](historical-analysis-index.json) and
[decision bytes](historical-decision-before-preparation.txt) are archived unchanged.
Its old decision-document path resolves to the archived decision bytes for
historical hash verification, rather than to the subsequently edited current document.
Original R-17 and R-20 analysis artifacts are unchanged. The current analysis
index retains its historical scans and links this preparation; it does not
relabel those old scans as canonical regression evidence.

The closure index binds the current decision/proposal, raw scans, manifest/config
bytes, reviewed matrix and all 346 unchanged runtime/test input hashes. No new
commit, accepted release, hosted CI or AWS result is claimed.

[Consistency verification](verification.json) records the preparation checks;
[publication secret scan](publication-secrets-summary.json) records zero findings
without scanner-rule changes. Final index/artifact hashes were checked again
after adding those receipts. Markdown links, targeted formatting and
`git diff --check` also pass.
