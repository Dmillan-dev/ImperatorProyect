# A-S3 Local Evidence / Version Preparation — 2026-10-06

Use the [work order](../../A_S3_EVIDENCE_VERSIONING.md), [PR draft](PR_DRAFT.md), [CI association](ci-association.json) and [A-S2 acceptance](../2026-10-06-a-s2-acceptance/README.md). This package prepares a local candidate source snapshot; the post-commit version receipt records its actual candidate SHA separately to avoid a self-referential commit hash.

No code/dependency change is introduced by A-S3. Candidate source reuses the existing working tree and exact current runtime inputs; original branch/index are preserved. Candidate commit identity is not an owner-approved release, new hosted CI or approval of rebuilt images. New publication/source checks do not rerun the complete functional suite. A-S3 remains active, A-S4 later, TFG-A IN PROGRESS and AWS blocked.

## Prepared Local Version

Candidate source commit: `8380e8d6e70ca4c858604ce4fcc0e0198fa162e1`, in a separate local checkout; original repository branch/index unchanged. The [version receipt](version-receipt.json), [Git normalization record](git-normalization.json), [versioning notes](VERSIONING_NOTES.md) and [candidate changes](candidate-changes.json) record actual source/bundle/ZIP identities. These post-commit metadata files are intentionally outside the candidate commit; its exact-byte archive and manifest support reproduction.

Ordinary source/docs diff checks pass. Full candidate whitespace checking retains warnings in33raw evidence files (377locations), not source changes; preserve raw log/patch bytes and indexed hashes. Full result is recorded in the version receipt. No scanner suppression or blanket full-diff PASS is claimed. Candidate secret scan0; new hosted CI, submitted PR and accepted release SHA remain pending.
