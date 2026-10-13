# Technical validation of the Go security patch — 2026-10-09

This document records measured technical results only. It does not record or change an owner risk decision, formal A-S3 closure, release approval or hosted CI result.

Candidate source: `a931601102ee19a90b3dad8477408ac0d1854669`, based on `main` `3aca0389431b077ddee0b20621f6ccac26238e9f`. The three-file patch changes Go builder1.26.6 to1.26.9, its matching Dockerfile/lock labels and D094's hard-coded expected compiler assertion. PostgreSQL18.6/Bookworm, gosu1.19, PCRE2/Perl, Java/Spring/Node and application behavior are unchanged.

## Exact image scans

| Component | Fixable H/C | Unfixed H/C package records | File/config secrets |
| --------- | ----------: | --------------------------: | ------------------: |
| migration |           0 |                           0 |                   0 |
| backend   |           0 |                           0 |                   0 |
| frontend  |           0 |                          49 |                   0 |
| postgres  |           0 |                          66 |                   0 |

[Commands/timestamps](scans/scan-commands.json), [tool identity](scans/scanner-tool.json), [databases](scans/scanner-databases.json) and full raw reports are retained. The pre-existing native Trivy0.75.0 refreshed databases, then pinned their state across twelve scans of immutable index digests/linux-amd64. No VEX or advisory suppression was applied.

The Go stdlib CVEs [CVE-2026-78667](https://pkg.go.dev/vuln/GO-2026-6609) and [CVE-2026-97031](https://pkg.go.dev/vuln/GO-2026-6607) are removed. The [comparison](applicability/residual-comparison.json) shows 23 unchanged residual CVEs/115 records, two fixed records removed and no new H/C records. The [vendor snapshot](applicability/debian-vendor-current.json) still has no published Bookworm corrections for those23 residuals. This result is not a declaration of inexploitable components or zero vulnerabilities overall.

## Build and runtime binding

Two clean D094 builds reproduce linux/amd64 runtime manifest `sha256:c22d3892c45e412fbdb36d4e17d4580359be3bba87873710cceeeaf777b68717`. The canonical repeat OCI index is `sha256:3cb24d97e30a53f79de131660d8f0fcdb62f3cbc205c7b3f1710a0415834cfd9`.

[First provenance](d094/first/provenance.json) and [repeat provenance](d094/repeat/provenance.json) retain their distinct attestation/index identities. Equal runtime manifests do not imply equal index digests. D094's technical eligibility flag is not an academic or owner approval.

Backend/frontend/migration retain their6e79e83 artifacts. Only the PostgreSQL image was rebuilt. [Runtime bindings](runtime/runtime-image-bindings.json) and [recreation evidence](runtime/persistence.json) establish that scans and functional runtime use the same exact indices/rootfs. The canonical Compose harness uses immutable overrides, `--no-build --pull never`, new synthetic secrets/volume and local HTTPS/JWKS. Only127.0.0.1:3011 frontend is exposed; external providers/AWS/Bedrock/GitHub adapters remain disabled. The component build origins are not relabelled as a single new build SHA.

## Fresh regression

| Control                                        | Result                                                                           | Evidence                                                                        |
| ---------------------------------------------- | -------------------------------------------------------------------------------- | ------------------------------------------------------------------------------- |
| Java unit/integration                          | 174+35 PASS,0 failures/errors/skips                                              | [Maven result](backend/backend-result.json)                                     |
| Frontend                                       | 41 PASS; lint/types/build/format PASS                                            | [Tests](frontend-tests.log), [configured results](frontend-resume-results.json) |
| Production npm audit                           | 0 vulnerabilities                                                                | [Audit](frontend-npm-audit-production.log)                                      |
| Full npm audit                                 | Five High development-tooling entries around braces; exit1 retained              | [Audit](frontend-npm-audit-full.log)                                            |
| Playwright contracts/viewports                 | 9 PASS/6 intentional skips                                                       | [Log](frontend-playwright-configured.log)                                       |
| Unmocked Docker four-role DRC-AOA-001 workflow | PASS                                                                             | [Rehearsal](runtime/capture/rehearsal.json)                                     |
| Flyway/append-only SQL privileges              | V1–V3 successful; SELECT/INSERT allowed,UPDATE/DELETE denied                     | [Database checks](runtime/persistence.json)                                     |
| Persistence                                    | New containers retain30 evidence rows,3 ledger entries and identical projections | [Persistence](runtime/persistence.json)                                         |
| Readiness/recovery                             | Healthy200; PG stopped ready503/live200; recovery200 with same artifacts/facts   | [Recovery](runtime/readiness-recovery.json)                                     |
| Current source Gitleaks                        | 0 findings across655 tracked source/config/doc files                             | [Scope/result](source-secret-check.json)                                        |

The integration suite uses the same PostgreSQL image in a separate temporary loopback JDBC harness on55439; canonical Compose does not expose a database port. Initial frontend invocations correctly failed due to a missing API-origin input and a missing pinned browser. Both initial logs are retained; configuring the documented local API origin and installing the browser required by the existing lockfile resolves the checks. No dependency upgrade or code workaround was applied. Contract Playwright intercepts API routes; the separate real Docker rehearsal does not.

[Fresh component/file probes](applicability/runtime-inspection.json), [13 source/config files](applicability/source-boundary-current.json), [individual CVE matrix](applicability/current-cve-applicability-matrix.json) and official advisory snapshots support bounded applicability. These are not penetration tests or universal non-exploitability proof. Frontend braces cannot be resolved and is not identified as a production package; bundled picomatch is separate. The fresh official braces advisory still publishes no patched version. Mitigations remain conditional and do not repair residuals.

The short technical recording is not the twelve-minute narrated A-S4 defense. Existing evidence/receipts and both prior October9 packs remain historical and byte-preserved. Private fixture keys/passwords/JWTs/truststores and scanner/browser caches are excluded from this package. Source and documentary CI publication/review remain separate from these local measurements.

[Runtime cleanup](runtime-cleanup.json) records removal of only the test application/JWKS containers and stopping the owned builder. All30 existing images,13 existing volumes and the original four defense image identities are preserved; two new PostgreSQL build indices are retained. No prune or volume removal occurred.
