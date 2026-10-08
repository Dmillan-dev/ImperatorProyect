# A-S1 R-19 / A-S2 Evidence — 2026-10-06

**R-19 CLOSED for original brace-expansion finding. A-S2 NO-GO. TFG-A IN PROGRESS.**
The [review](../../R19_SECURITY_REVIEW.md), [gate](../../TFG_A_SECURITY_CLOSURE_GATE.md),
[index](../TFG_A_R19_2026-10-06.json) and
[gate index](../TFG_A_SECURITY_CLOSURE_GATE_2026-10-06.json) explain current limits.

Only two brace-expansion lock entries changed. The final local regression passes
174 backend unit, 35 PostgreSQL integration and 41 frontend tests, full frontend
quality, nine Chrome cases with six intentional fixture skips, actual Docker
RBAC/V3/persistence/readiness and exact manifest/image binding. Secrets detected:0.
Five screenshots and a 28.20-second silent captioned WebM are a technical fallback,
not a full oral defense. Owned clusters/volumes/private fixtures were removed;
normal volumes and user output remain. No new Git commit or AWS/IdP claim.

Current security is FAIL: npm8 High package aggregates from 3 advisories,
source1 fixable High, frontend8 and PostgreSQL29 fixable High/Critical; backend0.
R-21/R-22 proposals are **not applied or accepted**. Candidate npm has5 High
aggregates from the single unfixed braces chain; candidate source/images have
zero fixable High/Critical. Candidate full image scans still fail with49 frontend
and66 PostgreSQL residual rows. These are feasibility evidence, not release images.

R-20's [historical approved record](../2026-10-02-r20-acceptance/acceptance.json)
remains unchanged. Current binding is REVIEW REQUIRED/accepted=false under its
early-invalidation rules; expiry remains 2026-10-09T00:00:00+02:00 Europe/Madrid
(2026-10-08T22:00:00Z), without renewal. No risk is accepted by this review.

Raw canonical and proposal scan reports live in `scans/`, with exact policy and
command metadata in `scanner-summary.json` and `commands.json`. The initial
source scan predates current document publication; its selected source hashes
are retained explicitly. A final publication-only secret check is separately
recorded in `publication-verification.json` after all evidence/index bytes exist.
`pre-update-documents/` preserves previous current-document bytes; historical
October2 evidence is not rewritten. Runtime/test files bind separately.

The reviewable [R-21 diff](r21-security-only.patch) and [R-22 diff](r22-security-only.patch)
are against the current local worktree and remain unapplied. Pinned Debian
downloads, candidate package files, full OCI archives, temporary verifier copies
and initial failed-attempt logs remain in ignored task build output; archive and
proposal hashes bind those local inputs. Logs are sanitized; no JWT or private
fixture key is published.
