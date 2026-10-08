# Explicit R-20 B Acceptance / TFG-A Security Preflight — 2026-10-02

**R-20 ACCEPTED TEMPORARILY; R-19 OPEN and unauthorized; Security Closure Gate
NO-GO; TFG-A IN PROGRESS.**

The [acceptance record](acceptance.json) records the founder's explicit affirmative
message and its conditions. Registration time: **2026-10-02T17:16:32Z**;
the exact client message-send timestamp is unavailable and is not invented.
Scope: TFG-A synthetic data, local loopback rehearsal only. AWS, production,
real data and public/pilot/customer exposure are excluded. Exclusive expiry:
**2026-10-09T00:00:00+02:00 Europe/Madrid (CEST)** =
**2026-10-08T22:00:00Z**, no automatic renewal.

The project-local criterion amendment applies only to the hash-bound 32-CVE
residual OS set and exact canonical images. No vulnerability is remediated,
suppressed or declared unaffected. Zero fixable High/Critical and zero secrets
remain mandatory closure gates; R-19 is not covered or authorized.
No tutor/institutional approval, full TFG-A closure or TFG-B/AWS work is implied.

## Approved Evidence Preserved

- [Approved decision snapshot](approved-decision-snapshot.txt): exact decision
  bytes reviewed before the explicit approval; SHA256 retained.
- [Preparation-index snapshot](preparation-index-before-approval.json) and
  [analysis index before approval](analysis-index-before-approval.json): exact
  original metadata. References to the former current decision/index paths
  resolve to these archived snapshots for historical verification.
- [Original approval proposal](../2026-10-02-r20-closure/approval-proposal.json):
  historical accepted=false remains intact; current acceptance authority is
  acceptance.json, not that historical proposal.
- [Original R-17 review bytes](r17-review-before-r20-approval.txt): historical
  pre-approval state preserved before adding its subsequent-state note.
- Canonical image manifests/configurations, raw scans, scanner/database hashes,
  32-CVE matrix and all 346 runtime/test input hashes remain unchanged and are
  bound by the acceptance record.

The current [decision document](../../R20_SECURITY_DECISION.md) records the
approval result separately from the approved original bytes.

## Security Closure Gate Preflight

[Gate report](../../TFG_A_SECURITY_CLOSURE_GATE.md) and
[machine index](../TFG_A_SECURITY_CLOSURE_GATE_2026-10-02.json): **NO-GO due to R-19**.

- [Read-only npm audit](npm-audit.json) and [summary](audit-summary.json): exit 1,
  one High aggregate package; package.json and lockfile unchanged.
- [Source Trivy raw report](source-with-dev-trivy.json) and
  [summary](source-scan-summary.json): existing fixable High/Critical policy,
  includes development dependencies, offline scanner with pinned current DB;
  four High package/CVE findings. No policy/suppression or dependency change.
- [Publication secret scan](publication-secrets-summary.json): zero findings,
  redacted reporting, unchanged Gitleaks rules; complete selected public-source
  snapshot includes current tracked files and new docs/scripts, excludes ignored
  build outputs and pre-existing untracked output/tfg.
- Existing canonical image scans are reused after hash verification; they are
  not newly executed image scans in this approval turn. Existing 174/35/41
  functional results remain dated R-17 evidence; no full regression rerun.

The final index records artifact hashes and the approval's immutable bindings.
No new commit, accepted release, hosted CI, live IdP or cloud execution occurred.

[Consistency verification](verification.json) records acceptance/binding,
expiry, unchanged inputs and expected security-gate failure checks. Hashes are
checked again after publishing that receipt. Documentation links, targeted
formatting and `git diff --check` are verified separately.
