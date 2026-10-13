# Minimum remediation proposal — NOT APPLIED

**No patch, image pull, build or runtime execution is authorized by this document.** This review makes the next decision concrete; no additional risk ID or open-ended update chain is introduced.

Two fresh HIGH CVEs affect Go 1.26.6's stdlib in PostgreSQL's rebuilt gosu helper. The official advisories [GO-2026-6609](https://pkg.go.dev/vuln/GO-2026-6609) and [GO-2026-6607](https://pkg.go.dev/vuln/GO-2026-6607) provide fixes in 1.26.9 and 1.27.2. The same-line 1.26.9 patch is the smallest identified compiler change.

## Proposed change, requiring independent authorization

| File/field                                                       | Current                                                                   | Proposed                                                                  |
| ---------------------------------------------------------------- | ------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| `infra/docker/postgresql/Dockerfile` gosu-build FROM             | `golang:1.26.6-bookworm`                                                  | `golang:1.26.9-bookworm`                                                  |
| Same FROM digest; supply-chain lock builder digest               | `sha256:433f9dc4f8ea3a1ce4e28f9f15d0f7c056b10475307f886d6f1ac1ccc4abd976` | `sha256:d9c68c2c51161e12fd77e4c6320687c9cd86e1af1e3ad6e6cd63ff970641453c` |
| `infra/docker/postgresql/supply-chain-lock.json` builder.image   | `golang:1.26.6-bookworm`                                                  | `golang:1.26.9-bookworm`                                                  |
| Dockerfile version label and lock acceptance.runtimeVersionLabel | `18.6-gosu1.19-go1.26.6-pcre2deb12u2-perldeb12u4`                         | `18.6-gosu1.19-go1.26.9-pcre2deb12u2-perldeb12u4`                         |

The [registry metadata](go-builder-proposal-metadata.json) records the official Docker Hub OCI index response, verified response digest and linux/amd64 descriptor `sha256:bcef992b77b1e2031aaaa51da75cebc32da8e9c182e12e633ea79023c79d9eff`. Only manifest metadata was retrieved; the image was not pulled. Recheck availability/identity before an authorized build.

Keep gosu 1.19 and its exact source commit/hash, PostgreSQL 18.6 and its Bookworm runtime base digest, existing PCRE2/Perl package pins, all three other defense images, Java, Spring and Node unchanged. Do not change product behavior, architecture, workflows, Terraform, AWS, Bedrock or Keycloak. No application/library upgrade is proposed. Recompiling gosu changes its binary and the PostgreSQL image identity.

## Required verification if authorized

1. Apply only the specified Dockerfile and supply-chain-lock fields; inspect the diff and pin the new source revision. No bulk updates.
2. Rebuild the PostgreSQL derivative with D094 provenance and repeat-build verification; check gosu source/license/binary/config/layer evidence. Preserve the existing protected images until the new candidate is reviewed.
3. Refresh scanner databases and scan the exact rebuilt PostgreSQL plus the three retained images. Require fixable H/C = 0 and file/config secrets = 0; retain full residual reports.
4. Run appropriate complete regression: 174 unit, 35 integration and 41 frontend tests, lint/types/build, plus local synthetic Docker readiness, RBAC, Flyway, persistence, recovery and Playwright. Bind actual results to the same images, not separately rebuilt substitutes. Runtime/regression requires its own explicit authorization with the narrowly bounded security conditions; this review gives none.
5. Review the residual CVE matrix and hashes for the resulting exact digests. Seek a new explicit R-20 decision with an agreed future expiry. No historical acceptance transfer and no R-23 closure.
6. Stop if other fixable findings remain; classify them and seek a separately bounded decision. A-S3, A-S4 and AWS do not advance automatically.

This recommendation reduces the identified fixable exposure without widening the MVP. Limited source inspection of gosu does not establish that either CVE is universally inexploitable and is not grounds to suppress the strict security gate.

COMMIT NOT PERFORMED. PUSH NOT PERFORMED. PATCH NOT APPLIED.
