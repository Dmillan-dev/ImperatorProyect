# TFG-A Local Maintenance Evidence — 2026-10-01

Status: **IN PROGRESS; FUNCTIONAL CHECKS PASS, IMAGE SECURITY GATE FAILS**.

Baseline HEAD: `b602bbe1dde92a902c17272d59cd9f0c10b9efe5`, with the explicitly
authorized maintenance worktree. This is a reviewable local execution pack,
not a new accepted release, hosted certification, AWS deployment or external
Keycloak conformance. The [machine-readable index](../TFG_A_2026-10-01_MAINTENANCE.json)
records commands, dates, tools, source inputs, artifact hashes and limitations.
The earlier [initial attempt](../TFG_A_2026-10-01.json) remains historical.

## Bounded Corrections

- The three integration cleanup lists now truncate all V3 explanation tables,
  matching PostgresRepositoryIT. No CASCADE, schema or permission weakening.
- Prettier normalizes the eight tracked frontend files to LF; there is no
  content diff after Git normalization. Package/lockfile JSON and versions are
  unchanged. Frontend attributes enforce LF on Windows; generated metadata is
  excluded from formatting.
- Three Linux entrypoints are normalized to LF; `.gitattributes` preserves LF
  on Windows checkout. The actual first Docker attempt failed on CRLF.
- The database README now describes the existing V1-V3 migrations. The
  [structure guide](../../../project/PROJECT_STRUCTURE.md) orders the reading,
  working locations, verification and safe generated cleanup.

## Executed Verification

| Check                              | Result                                                                                                                |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| Backend default suites             | 174 PASS, no failures/errors/skips                                                                                    |
| Native PostgreSQL 18.6 integration | 35 PASS, no failures/errors/skips; Flyway V1-V3 migrate/validate/no-op PASS                                           |
| Frontend                           | Format, lint, types, 41 tests, production build PASS                                                                  |
| Chrome fixture-browser tests       | Nine PASS, six intentional skips; intercepted API fixtures                                                            |
| Real isolated Docker workflow      | PASS; no API interception; 30 imports, 28 composition references plus two post-action references                      |
| Negative and governance cases      | 401 unauthenticated; 403 auditor approval without write; 409 value before governance; three ordered authorized actors |
| Synthetic Business Value           | Estimated EUR 19,440; realized EUR 18,960; variance EUR -480                                                          |
| Container replacement              | Three containers recreated; 30 Evidence and three Ledger entries unchanged                                            |
| V3 application permissions         | SELECT/INSERT allowed, UPDATE/DELETE denied on Ledger and all explanation tables                                      |
| Runtime/probes                     | Non-root/read-only; only loopback frontend; DB outage liveness 200, readiness 503, recovery 200                       |
| Image vulnerability policy         | Backend FAIL (four High package findings); frontend FAIL (one Critical); PostgreSQL PASS                              |

The Docker project is `imperator-tfg-a-20261001`, frontend
`http://127.0.0.1:3007`. Ephemeral credentials and an unpublished HTTPS/JWKS
fixture were used only for this rehearsal. D087 RS256 validation and role
authority were exercised without bypassing validation; this does not discharge
the external Keycloak/D095 gate. Local Compose keeps Bedrock disabled.

Approval uses the real UI. Implementation and result validation use
authenticated API commands to attach the two canonical post-action records,
followed by real UI reads. The picker currently lists already linked Evidence;
this pack does not claim that it selected previously unlinked records in the UI.
No extra GUI workflow was added to meet the MVP boundary.

## Capture And Durable Reports

- [Technical fallback recording](tfg-a-docker-demo.webm): short, silent,
  captioned WebM at 1440×900; five workflow states. It is not the twelve-minute
  narrated defense. Its duration is recorded in the evidence index.
- [Deterministic Recommendation](01-deterministic-recommendation.png).
- [Human approval](02-human-approval.png).
- [Implementation fact](03-implementation.png).
- [Realized Business Value](04-realized-business-value.png).
- [Auditor / ordered Ledger](05-auditor-ledger.png).
- [Real API/UI assertions](rehearsal.json),
  [recreation and V3 privileges](persistence.json),
  [observability transitions](observability.json) and
  [frontend/backend test summary](quality-summary.json).
- [Image scan summary](image-scans.json) and
  [Docker build input fingerprint](docker-source-manifest.json).
- [Scoped cleanup inventory](cleanup.json).

Captures and reports were inspected for credential disclosure. UUIDs, dates
and amounts belong to the versioned synthetic scenario. Tokens, passwords,
private keys, raw provider prompts and customer data are not included. Full
sanitized test logs and scanner reports remain local under ignored
`build/tfg-a/`; selected summaries, captures and their hashes are durable here.

The build fingerprint includes the inputs at image build time. Later README
changes do not rebuild the images; the index lists input mismatches explicitly.
Image IDs are local content identities, not published ECR manifest digests or
fresh two-build reproducibility proof. Human review and an accepted commit
remain pending.

## R-17: Minimal Security Patch Proposal

The current scan uses Trivy 0.74.0 and the repository's fixable HIGH/CRITICAL
vulnerability policy. Historical D098 success cannot certify today's images.
Package findings do not establish an exploitable project route; no suppression
or policy waiver was introduced.

| Image / package              | Current           | Proposed minimum fix                                                | Primary advisory                                                                                                                                                                                                   |
| ---------------------------- | ----------------- | ------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| Backend `libssl3`, `openssl` | 3.0.2-0ubuntu1.29 | 3.0.2-0ubuntu1.30 in an inspected, digest-pinned Java runtime base  | [Ubuntu USN-8847-1](https://ubuntu.com/security/notices/USN-8847-1)                                                                                                                                                |
| Backend Jackson Databind     | 3.1.6             | Jackson BOM 3.1.7                                                   | [FasterXML advisory 1](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-wv8q-qhhj-9h54), [advisory 2](https://github.com/FasterXML/jackson-databind/security/advisories/GHSA-cxp5-3px4-pw24) |
| Frontend Next.js             | 16.3.5            | `next` and `eslint-config-next` 16.3.6, with the generated lockfile | [Vercel GHSA-vcvr-r3jv-pc5j](https://github.com/vercel/next.js/security/advisories/GHSA-vcvr-r3jv-pc5j)                                                                                                            |

The Next.js maintainer advisory concerns attacker-controlled SVG reaching
`next/og` ImageResponse. Inspection found no `next/og`/ImageResponse usage in
the production frontend, and no JsonIdentityInfo/default-typing usage in the
production backend. This bounds the present observation; it is not a waiver
of the image policy and does not prove absence of every transitive call path.

The proposed increment changes patch versions/base identity only. Inspect
the replacement base's actual OpenSSL package, preserve digest pins and
provenance, update lockfiles deterministically, rerun backend/native integration
and frontend gates, rebuild/rescan all images, and repeat affected Docker
workflow/persistence/capture checks. Close R-17 and TFG-A only after passing
evidence and review. Do not add a service, change a frozen contract, enable
live Bedrock or advance to TFG-B/C/D as part of this patch.

## Cleanup Boundary

Stopped PostgreSQL clusters created by these verification attempts, generated
Next metadata, ephemeral rehearsal keys/passwords, obsolete capture retries
and the owned Docker project's containers/network/volume are disposable after
evidence capture. The normal `imperator_postgres-data` and
`imperator_prometheus-data` volumes, user `output/tfg/`, accepted decisions,
deferred placeholders, tool installations and useful dependency/scanner caches
are preserved. No global prune or production reset is part of this task.
