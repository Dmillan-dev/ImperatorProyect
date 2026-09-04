# 55 - PostgreSQL Runtime Supply Chain Remediation Contract

Decision authority: D094.

Status: **FROZEN CONTRACT FIX / IMPLEMENTATION AUTHORIZED**

Owning gate: **Sprint 4.3 - PostgreSQL Runtime Supply Chain Remediation**

## 1. Objective

Replace only the vulnerable PostgreSQL runtime input frozen by D092 with an
input-reproducible, evidence-backed PostgreSQL 18 artifact. The correction
must preserve the existing database behavior while proving the security and
runtime properties of the final image.

The certification object is the complete chain:

```text
Pinned source and images
        |
        v
Versioned build process
        |
        v
Final image digest
        |
        +--> SBOM
        +--> build provenance record
        +--> Trivy vulnerability and secret scan
        +--> PostgreSQL/Flyway/permission/runtime tests
```

Rebuilding `gosu` is not itself a correction. D094 passes only when the final
artifact identified by digest satisfies every gate in this contract.

## 2. Trigger And Evidence

The D092 image
`postgres:18.4-bookworm@sha256:882236b897e39051d2368c5ccc6cda944904723506b2dfc97f2a8f5bc9afa382`
and the official `postgres:18.6-bookworm` candidate both contain `gosu 1.19`
compiled with Go `1.24.6`. An updated Trivy database reports the fixable
Critical `CVE-2025-68121` in that Go standard library.

The finding is in an upstream image component and not in IMPERATOR Java,
PostgreSQL server code, migrations or data. D092 nevertheless requires a
formal NO-GO for a fixable Critical image finding, so the official image
cannot be certified unchanged.

## 3. Authority And Narrow Precedence

D094 specializes D092 without editing D092 or D093. It supersedes only:

- the D092 database image row, from official PostgreSQL 18.4 to the D094
  derived PostgreSQL 18.6 artifact;
- the two Compose services that previously referenced that image directly;
- the D092 PostgreSQL runtime assertion from exact patch `18.4` to `18.6`;
  and
- the image-security gate by requiring zero fixable High or Critical findings
  for the D094 image, which is stricter than D092's Critical-only minimum.

PostgreSQL major version 18, database behavior, schema, Flyway, grants,
networks, secrets, volume ownership, ports, health checks and all other D092
requirements remain frozen.

D094 does not authorize D093/R16 implementation, an external IdP, a real JWT,
the full DRC-AOA-001 E2E or Sprint 4.3 certification. Those gates remain
separate and ordered after D094.

## 4. Frozen Inputs

The certified target is `linux/amd64`. Every network-resolved build input is
immutable and recorded below.

| Input                  | Frozen value                                                                                     |
| ---------------------- | ------------------------------------------------------------------------------------------------ |
| PostgreSQL base        | `postgres:18.6-bookworm@sha256:1c59e2c3c818eaa0f0628f695b36e7c9e362d6b219b36a54a32df645cbd7e1af` |
| Go builder             | `golang:1.26.6-bookworm@sha256:433f9dc4f8ea3a1ce4e28f9f15d0f7c056b10475307f886d6f1ac1ccc4abd976` |
| `gosu` version         | `1.19`                                                                                           |
| `gosu` source commit   | `6456aaa0f3c854d199d0f037f068eb97515b7513`                                                       |
| Source archive SHA-256 | `33d7537d588ea49458b9509bcf4554bdf5ceacc66da71e5caa1058ea3b689c3b`                               |
| Source URL             | `https://github.com/tianon/gosu/archive/6456aaa0f3c854d199d0f037f068eb97515b7513.tar.gz`         |
| Target OS/architecture | `linux/amd64`                                                                                    |
| CGO                    | disabled                                                                                         |

The Dockerfile must enforce the archive checksum before extraction. A branch,
floating tag, unverified source archive, mutable builder or mutable base is a
hard failure.

## 5. Input Reproducibility

Input reproducibility means that a reviewer can reconstruct the candidate from
the committed Dockerfile, lock manifest, exact source archive, exact builder
manifest and exact PostgreSQL base manifest. It does not claim byte-identical
output across Docker/BuildKit versions or CPU architectures.

The lock manifest and Dockerfile must agree on every value in Section 4. Any
drift fails before build. The build records:

- source Git revision and dirty-worktree state;
- Dockerfile and lock-manifest SHA-256;
- Docker, Buildx and Trivy versions;
- BuildKit metadata;
- final OCI index digest and runtime manifest digest;
- OCI labels and runtime command;
- Trivy database metadata; and
- hashes of the generated evidence files.

Formal release certification requires a clean committed source revision.
Implementation-time candidate checks may run from a dirty worktree but cannot
be represented as the final D094 release artifact.

The certification gate performs two builds with `--no-cache`, uses the source
commit time as `SOURCE_DATE_EPOCH` and rewrites exporter timestamps to that
value. Both builds must produce the same `linux/amd64` runtime manifest digest,
independent of a developer or runner's pre-existing Docker cache.

## 6. Build Contract

The build is a two-stage Docker build:

1. the pinned Go builder verifies and extracts the pinned source archive;
2. it compiles only `gosu` with `CGO_ENABLED=0`, `GOOS=linux`,
   `GOARCH=amd64`, `-trimpath`, disabled VCS stamping and an empty build ID;
3. the pinned PostgreSQL image remains the final base; and
4. only the rebuilt `/usr/local/bin/gosu` crosses from builder to final image.

The final stage must preserve the upstream `docker-entrypoint.sh`, default
`postgres` command, PostgreSQL user/group `999:999`, filesystem layout and
volume semantics. It must not install a compiler, package manager payload,
source tree, certificate, credential, shell profile or build cache.

The final image declares `USER 999:999`; the default runtime never starts as
root. The corrected `gosu` binary remains available for explicit root-start
compatibility and is tested as root during certification.

The builder is not part of the final SBOM. It remains a recorded supply-chain
input and must itself be pinned by platform digest.

## 7. Image Identity And Labels

The Compose image name is `imperator/postgres:${IMPERATOR_IMAGE_TAG}` where
`IMPERATOR_IMAGE_TAG` is the full source Git SHA for formal certification.
`latest` is forbidden.

Required OCI labels contain no secret or dynamic timestamp:

- title and description;
- source repository;
- source revision;
- version `18.6-gosu1.19-go1.26.6`;
- base image name and digest; and
- licenses inherited from PostgreSQL/gosu source terms.

The `linux/amd64` runtime manifest digest extracted from the OCI archive is the
stable executable-artifact identity. The loaded OCI index digest additionally
binds the runtime to the BuildKit attestation for that build. Registry
publication remains out of scope.

## 8. SBOM And Provenance

The certification process creates an ignored local evidence directory. It
must contain at least:

- CycloneDX JSON SBOM for the final image;
- Trivy JSON vulnerability/secret result;
- Trivy human-readable report;
- BuildKit metadata;
- normalized provenance JSON; and
- SHA-256 manifest covering those evidence files.

CI retains the same files as a bounded artifact. The evidence contains no
secret values, environment dumps, credentials, JWTs or customer data.

The SBOM names the scanned loaded OCI index. The provenance binds that index to
the stable `linux/amd64` runtime manifest digest extracted from the OCI archive.
A missing artifact, binding mismatch or unbound report fails certification.
Attestation index digests may differ because they describe distinct build
executions; the executable runtime manifest digest must match.

## 9. Security Gate

Use an updated and recorded Trivy vulnerability database. Scan the final image
with vulnerability and secret scanners and `--ignore-unfixed`.

D094 fails if:

- `CVE-2025-68121` is present;
- any fixable High or Critical vulnerability is present;
- a secret is detected;
- a report was generated for a different image ID;
- the database metadata is absent; or
- any finding was ignored, suppressed or reclassified locally.

No `.trivyignore`, VEX exception, scanner downgrade or contract weakening is
authorized.

## 10. Functional And Hardening Gate

The exact final image must prove:

- `gosu --version` reports `1.19` built by Go `1.26.6`;
- `gosu nobody true` succeeds;
- `postgres --version` reports PostgreSQL `18.6`;
- upstream entrypoint and default command remain unchanged;
- the PostgreSQL server becomes healthy;
- server processes run as UID `999` after privilege drop;
- a clean database initializes on the D092 parent volume;
- Flyway migrate, validate and second migrate/no-op pass;
- the existing permission job passes and remains idempotent;
- `imperator_app` cannot update or delete append-only Ledger tables;
- the PostgreSQL cluster system identifier and complete Flyway schema history
  survive container recreation without deleting the volume; and
- teardown leaves no running D094 container or unexpected published port.

No backend, frontend, external issuer or DRC-AOA-001 E2E is required to close
D094. Those remain mandatory for the later complete D092/Sprint 4.3 gate.

## 11. Compose Contract Fix

Only `postgres` and `postgres-permissions` switch from the official image
reference to the same D094 custom image/build definition. They must not diverge
by tag, image ID or build input.

The build context is exactly `infra/docker/postgresql/`. It must not include
Docker secrets, `.env`, Git history, application source or customer data.

No port, network, volume, secret, health check, capability, read-only setting,
resource ceiling or startup dependency may change.

## 12. CI Contract Fix

The existing Security workflow gains one isolated PostgreSQL image job. It:

1. builds the D094 image from pinned inputs;
2. validates runtime identity and labels;
3. scans the final image with the same High/Critical and secret policy;
4. generates SBOM, scan and provenance evidence; and
5. uploads only those sanitized files with a pinned artifact action.

`GITHUB_TOKEN` remains read-only. No image is pushed, no registry login occurs,
no Docker Hub authentication is requested and no project secret is consumed.

## 13. Implementation File Boundary

D094 may create or modify only:

```text
docs/architecture/55_PostgreSQL_Runtime_Supply_Chain_Remediation_Contract.md
docs/decisions/14_Decision_Log.md        # append D094 only
infra/docker/compose.yaml                # two PostgreSQL image/build blocks
infra/docker/postgresql/Dockerfile
infra/docker/postgresql/Dockerfile.dockerignore
infra/docker/postgresql/supply-chain-lock.json
infra/docker/postgresql/README.md
scripts/verify-postgres-runtime-image.ps1
.github/workflows/security.yml           # isolated D094 job only
.gitignore                               # generated D094 evidence only
```

It must not modify Java, tests, Maven, Domain, Application, Ports, adapters,
REST, JWT/RBAC, connectors, SQL, Flyway, frontend, package manifests, D001-D093
or project-control/AI context.

Any required file outside this boundary stops for a new authorization.

## 14. Explicitly Out Of Scope

- suppressing or accepting the upstream CVE;
- publishing an image or authenticating with Docker Hub;
- changing PostgreSQL major version;
- changing schema, migration or database behavior;
- replacing `gosu` with another privilege tool;
- application, connector, identity or product changes;
- D093 implementation;
- external Keycloak/JWKS configuration;
- full Sprint 4.3 E2E or certification;
- observability, backup platform, cloud deployment or pilot data.

## 15. D094 Acceptance Gate

D094 is `PASS` only when:

- this contract and one D094 Decision Log append are the only governance
  changes;
- all frozen inputs and their checks agree;
- the process is committed and the formal build starts from a clean worktree;
- two uncached builds produce the same SHA-256 runtime manifest digest;
- SBOM and provenance are present and bound to that digest;
- Trivy reports zero fixable High/Critical findings and zero secrets;
- `CVE-2025-68121` is absent from the final report;
- all Section 10 checks pass;
- unchanged Java, PostgreSQL integration and frontend regressions pass;
- D001-D093 and forbidden implementation areas are unchanged;
- Architecture, Security, Quality, Product and Context Guardians pass;
- ASI and DII remain 100%; and
- Decision Stability records one authorized additive correction, D094, with
  zero unauthorized prior-decision edits.

Until then the status is `D094 CANDIDATE / SPRINT 4.3 NO-GO`.

After D094 PASS, D093 implementation still requires its separate explicit
authorization. Sprint 4.3 remains uncertified until D093/R16, real external
D087/D088 JWT/RBAC, DRC-AOA-001 E2E and D092 persistence/recreation gates pass.
