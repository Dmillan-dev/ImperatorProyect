# R-20 residual delta - review required, not acceptance

Review date: 13/10/2026. Four unchanged local digests now produce 25 unique High/Critical CVEs / 119 package records, compared with 23 / 115 on 09/10. Zero fixable H/C and zero secrets remain measured across the four full reports. The scanner is the existing Trivy 0.74.0 image with October13 vulnerability/Java databases; the historical scan used Trivy 0.75.0. Engine identity is recorded and equivalence is not inferred from counts alone.

## Two additions

Both CVE-2026-88647 and CVE-2026-88648 identify libgnutls30 3.7.9-2+deb12u7 in frontend and PostgreSQL, yielding four new package/CVE rows. No previous H/C package row disappears. Trivy classifies them HIGH, affected, with no FixedVersion.

- [Debian CVE-2026-88647](https://security-tracker.debian.org/tracker/CVE-2026-88647): concerns certificate hostname verification. Bookworm's package remains listed vulnerable, no fixed version is published; urgency is unimportant. Debian notes that GNU TLS upstream does not consider it a security issue within its policy.
- [Debian CVE-2026-88648](https://security-tracker.debian.org/tracker/CVE-2026-88648): concerns certificate name constraints across domains. The same Bookworm status and upstream-policy qualification apply.

The vendor/scanner difference is retained. No CVE is suppressed, declared inexploitable or accepted by this report. Vendor HTML snapshots and exact scanner rows are preserved alongside this review.

## IMPERATOR applicability limits

The affected package is present in both images. The reviewed entrypoints launch node/server.js and PostgreSQL, and the retained Compose exposes frontend only on loopback, keeps database/backend internal and disables external providers in the synthetic rehearsal. These constrain exposure but do not prove that GnuTLS cannot execute. No new dynamic linkage/runtime probe or penetration test was performed. Reachability of the affected certificate-validation paths remains unproven; an attacker-controlled certificate/CA path is a relevant precondition, not evidence of exploitability or of safety.

The existing individual review of 23 residual CVEs remains historical; the two new entries require inclusion in the formal applicability/owner decision. No upgrade, package removal, base switch or patch invention is performed.

## Decision boundary

Historical R-20 expired 09/10/2026 00:00 Europe/Madrid. The proposal from 09/10 is accepted:false, refers to23 CVEs, and proposes expiry 16/10; it cannot automatically accept the25-CVE current residual. No new expiry or owner acceptance is recorded here. R-23 stays OPEN independently: production npm audit 0; full audit 5 High in the development-tooling chain. Original R-20/R-23 records and receipts are untouched.

Technical scanner gate: PASS. Formal A-S3: PENDING explicit applicable risk decision and review of the published evidence. A-S4: PENDING; TFG-A: IN PROGRESS; AWS: BLOCKED.
