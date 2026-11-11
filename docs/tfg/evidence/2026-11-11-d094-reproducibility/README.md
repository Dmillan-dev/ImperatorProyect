# PostgreSQL D094 reproducibility correction — 11 November 2026

**Current artifact set; no current R-20 acceptance.** This follow-up supersedes the unapproved four-image proposal in the [first candidate record](../2026-11-11-pr-security/README.md). Its raw reports, receipts and manifests remain intact.

## Observed failure and minimal correction

The first PR #91 HEAD `695f092` passed ten checks, including image security and all CodeQL analyses, but failed D094's repeated-build runtime-manifest comparison. [Captured failure](ci-original-d094-failure.txt), [first-head CI](ci-first-head.json).

Two local no-cache reproductions isolated exactly one differing file in the APT layer: `/var/cache/ldconfig/aux-cache`. It was absent from the final filesystem but had already introduced variable bytes in an earlier layer. [Layer/entry comparison](REPRODUCIBILITY_DIAGNOSIS.json). The correction removes that temporary cache in the same RUN that installs APT. It changes no package/base version, application behavior or gate criteria.

Technical source `49c8b677ad18b07404b708ea83608201f6baa203`. Two independent no-cache builds, using the source commit's SOURCE_DATE_EPOCH and timestamp rewriting, now produce the same linux/amd64 runtime manifest:

`sha256:d50f6b7acdaf691acb84ea308a0fb3b7dbbf9981a4ddd1e409e7dd35431efd5e`

[Equality receipt](REPRODUCIBILITY_FIXED.json) and both full metadata/manifests/logs are retained. OCI attestation indices may differ; equality is asserted for the runtime manifest, not the provenance index. No ignore rule, expected-digest relaxation or scanner suppression was added. Publication CI must still pass for the final documentation HEAD.

## Exact set and repeated validation

[candidate-images.json](candidate-images.json) identifies three unchanged retained images built from `e30c16a` and the new PostgreSQL index built from `49c8b67`. Only the PostgreSQL Dockerfile changed after the first candidate; the Java/frontend executable inputs remain unchanged. CI-built images are separate artifacts and are not covered by this local draft.

The new PostgreSQL ID is rescanned with the same frozen November 11 vulnerability/Java databases. It has **0 fixable H/C and 0 secrets**, and its complete security-row inventory is identical to the prior PostgreSQL report. The other three retained IDs keep their exact previous scan bindings. [Correspondence](ARTIFACT_CORRESPONDENCE.json), [raw PostgreSQL report](postgres-trivy.json), [database cutoff](../2026-11-11-pr-security/database-metadata.json).

The 35 PostgreSQL integration tests pass again on the new exact ID, with Flyway migration/validation/idempotency, a temporary synthetic loopback database and cleanup of the test container/password. The profile also reruns the unchanged 174 unit tests. Frontend 44 tests, format/lint/types/build and Playwright 9 PASS / 6 intentional skips are retained from the unchanged frontend inputs. [Initial validation](../2026-11-11-pr-security/VALIDATION_SUMMARY.json). This does not substitute a complete live MVP RBAC/persistence/recovery rehearsal.

## Security decision and next action

**39 unique residual H/C CVEs; 41-CVE conservative review set**, with the complete all-severity inventory and 18 new individual vendor reviews preserved in the [comparison](../2026-11-11-pr-security/CVE_COMPARISON.json) and [matrix](../2026-11-11-pr-security/CVE_REVIEW_MATRIX.json). They are not fixed or declared inexploitable. The APT/Next remediation still reaches 0 fixable H/C and 0 secrets for this exact set.

[R20_REVIEW_REQUIRED.json](R20_REVIEW_REQUIRED.json) remains `accepted: false`, explicitly bound to these four IDs. Proposed exclusive expiry **18 November 2026 00:00 Europe/Madrid (UTC+01:00) = 17 November 23:00 UTC** is not approval. Local/synthetic only, no renewal or future-image transfer. The original approved acceptance expired on October 19; neither PR merge nor identical vulnerability rows renews it. R-23 remains independent.

Review final-HEAD CI and the replacement #91; after merge, close #88 as superseded and defer/close broad maintenance #89/#90. Then obtain an explicit R-20 decision, rehearse the exact full Docker set, close current A-S3 formally and finish A-S4: memory, a measured twelve-minute English human rehearsal, narrated recording and academic review. TFG-A remains IN PROGRESS; AWS, production, public exposure and real data remain blocked.
