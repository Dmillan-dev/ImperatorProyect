# 58 - Runtime Supply Chain Refresh

Status: **FROZEN / D098 ACCEPTED / COMPLETE / HOSTED PASS**

Date: **2026-09-14**

Last verified: **2026-09-15**

## 1. Purpose

Restore the fail-closed application-image and D094 PostgreSQL supply-chain
gates before D097 implementation continues.

D098 is security maintenance inside the current Sprint 4.4 preflight. It adds
no product capability and does not authorize Sprint 4.5.

## 2. Trigger And Evidence

Nine automated dependency pull requests created on 2026-09-14 shared the same
two failures even when their own change did not touch Docker:

- `Application image scan` failed while scanning the frontend image; and
- `PostgreSQL D094 supply-chain gate` failed while certifying the final
  PostgreSQL image.

Current Trivy data identified the common fixable findings in the pinned Debian
Bookworm runtime layers:

| CVE | Package | Installed | Fixed |
|---|---|---|---|
| `CVE-2026-86145` | `libpcre2-8-0` | `10.42-1` | `10.42-1+deb12u1` |
| `CVE-2026-89161` | `libpcre2-8-0` | `10.42-1` | `10.42-1+deb12u1` |

The official Debian security package is fixed as this immutable input:

```text
package=libpcre2-8-0
version=10.42-1+deb12u1
architecture=amd64
url=https://security.debian.org/debian-security/pool/updates/main/p/pcre2/libpcre2-8-0_10.42-1+deb12u1_amd64.deb
sha256=81c5502941118a24d47af69a17b8b0b9548d75cc6d72b3eb3fe01047b46fa10e
```

The package fixes both findings according to the Debian security tracker. No
vulnerability is ignored, suppressed or downgraded.

The first local D098 build also proved that the D092 Node `24.18.0` image no
longer satisfies the repository's existing `>=24.19.0 <25` engine contract.
D098 therefore aligns the Docker build/runtime to exact Node `24.19.0` while
remaining inside the frozen Node 24 lane.

## 3. Decision And Supersession

D098 narrowly supersedes only:

- the D092 frontend runtime-image construction where the pinned Node 24
  Bookworm layer contains the vulnerable package; and
- the D094 final PostgreSQL runtime construction and version label where its
  pinned PostgreSQL 18.6 Bookworm layer contains the same package.

The exact PostgreSQL `18.6`, Go `1.26.6`, gosu `1.19`, product runtime behavior
and D094 reproducibility model remain unchanged. Node moves only from
`24.18.0` to exact `24.19.0` at digest
`sha256:a9f5f7c91a432850b2a8a7797adf5eadb6c733ceed61167806cee7ea7fbc29df`
to match the already frozen frontend engine range.

The fixed Debian package must be installed only in the shipped frontend and
PostgreSQL runtime stages through Dockerfile `ADD --checksum`. Build stages may
remain unchanged because they are not shipped artifacts. The package URL,
version and checksum are part of the certified input set.

The PostgreSQL patch layer must remove the regenerated
`/var/cache/ldconfig/aux-cache`. Docker 29.8/Buildx 0.37 proved that this
auxiliary cache embeds variable filesystem metadata; removing it preserves the
runtime linker cache while restoring identical final-layer digests.

D098 also authorizes a Dependabot version-update policy that:

- ignores automatic semantic-version major updates during the frozen MVP;
- groups compatible minor and patch version updates by ecosystem; and
- does not suppress security updates or Dependabot vulnerability alerts.

Major upgrades remain possible only through an explicit contract review.

## 4. Explicit Rejections

D098 does not authorize:

- Node 25 or 26, `@types/node` 25, ESLint 10 or Vitest 5;
- a PostgreSQL major change, distribution change or unpinned base image;
- replacing the zero High/Critical or zero-secret policy;
- Trivy ignore files, VEX waivers, `continue-on-error` or exit code zero;
- broad `apt-get upgrade`, mutable package selection or an unverified mirror;
- Maven, npm or GitHub Action version updates unrelated to this blocker;
- Domain, Application, Port, REST, schema, frontend behavior or connector
  changes; or
- Sprint 4.4 certification, external pilot identity, Sprint 4.5 or MVP Release.

## 5. Authorized Files

D098 may modify only:

- `infra/docker/frontend/Dockerfile`;
- `infra/docker/postgresql/Dockerfile`;
- `infra/docker/postgresql/supply-chain-lock.json`;
- `scripts/verify-postgres-runtime-image.ps1`;
- `.github/workflows/security.yml`;
- `.github/dependabot.yml`;
- focused supply-chain documentation and active project-control documents; and
- D098 test evidence under ignored `build/` paths.

## 6. Verification

The correction passes only when:

1. each final image reports `libpcre2-8-0=10.42-1+deb12u1`;
2. the frontend final image has zero fixable High/Critical findings and zero
   secrets;
3. the D094 PostgreSQL verifier passes twice with the same runtime manifest
   digest;
4. the PostgreSQL SBOM and provenance include the fixed package input;
5. backend, frontend and PostgreSQL runtime behavior remains unchanged;
6. source, dependency and configuration scanning passes;
7. Java CI and CodeQL remain green; and
8. no vulnerability exception or unsupported major update is introduced.

Local Docker verification on 2026-09-15 produced:

- Docker Desktop `4.91.0`, Engine `29.8.0` and Buildx `0.37.0`;
- frontend Node `24.19.0` and `libpcre2-8-0=10.42-1+deb12u1`;
- zero fixable High/Critical findings and zero secrets in backend, frontend and
  PostgreSQL final images;
- two PostgreSQL builds with the same runtime manifest
  `sha256:2b7cdecf1ebf5ba2eb7aacb828baf6bc18804d5932602509ce2a15cdaecd05b7`;
- D094 runtime, SBOM and provenance verification: PASS;
- PostgreSQL `18.6`, gosu `1.19`, Flyway migrate/validate, non-root/read-only
  hardening, loopback-only frontend and safe unauthenticated `401`: PASS; and
- obsolete pre-D098 application and deferred Prometheus/Grafana images removed
  without deleting persistent volumes.

Hosted Security and CodeQL checks remain the formal merge evidence.

## 7. Gate State

```text
D097 MVP OBSERVABILITY SCOPE: ACCEPTED / IMPLEMENTATION AUTHORIZED
D098 SUPPLY CHAIN REFRESH: ACCEPTED / IMPLEMENTED / LOCAL PASS
D098 CERTIFICATION: LOCAL PASS / HOSTED PASS
SPRINT 4.4 IMPLEMENTATION: D097 COMPLETE / HOSTED PASS ON `4fd18fa`
SPRINT 4.5: PENDING / NOT AUTHORIZED
```
