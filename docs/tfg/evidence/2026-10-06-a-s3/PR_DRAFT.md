# TFG-A: bounded security remediation, local evidence and explicit residual disposition

The local MVP had fixable security findings despite passing functional checks. The scoped R19/R21/R22 patches correct the authorized dependencies and Bookworm PCRE2/Perl packages; complete regression and scans on the same canonical images now show zero fixable High/Critical and zero secrets. No additional upgrade is included.

Owner approval records the exact current 23-CVE OS residual as temporary local/synthetic risk until 2026-10-09T00:00:00+02:00 Europe/Madrid; this is not an absolute-zero or universal non-exploitability claim. R23 braces tooling remains OPEN / NO FIX AVAILABLE under a separate approved disposition. A-S2 formal GO is registered; TFG-A, A-S3 version/review/CI and narrated defense remain separate obligations.

## Review Scope

Includes the already-existing local TFG-A source/test/runtime maintenance plus synchronized evidence, hashes, traceability and owner decisions. A-S3 adds documentation/versioning only; source changes in the candidate precede this task and match the approved runtime manifest. Preserve architecture/business behavior, current base digests and Java/Spring/Node/PostgreSQL versions. No AWS, production, real data, speculative braces update or Dependabot #57–59 merge.

Evidence versioning also adds `docs/tfg/evidence/** -text` to `.gitattributes` so approved raw artifact hashes survive checkout. Ordinary source uses standard Git line-ending normalization; exact runtime file bytes are retained separately. See `VERSIONING_NOTES.md`. This is VCS metadata only, not a runtime/source/dependency update.

## Validation

- 174 backend unit, 35 native PostgreSQL integration, 41 frontend tests PASS; format/lint/types/coverage/build PASS.
- Docker/RBAC/Flyway V1–V3/append-only privileges/persistence/readiness/recovery PASS on the exact canonical images.
- Playwright 9 PASS, 6 intentional skips; five captures and silent captioned technical fallback. No narrated defense or external IdP certification.
- Backend/frontend/PostgreSQL fixable H/C = 0; source/history/image secrets = 0. Full residual scans/audit remain visible.
- A-S3 checks unchanged runtime inputs and frozen image identities, document links/diff, publication secrets and isolated candidate source integrity. These are not fresh functional tests or hosted checks.

## Required Follow-up Before Release

Review candidate diff/source SHA, submit PR explicitly, obtain actual required hosted checks on submitted SHA, and review any resulting rebuilt image binding. Do not treat current image approval as covering a CI rebuild. A-S4 narrated defense and academic completion are later; AWS remains blocked. Reject scope expansion instead of updating more dependencies to hide a scanner total.
