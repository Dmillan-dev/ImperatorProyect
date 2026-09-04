# PostgreSQL Runtime Image

## Purpose

Produce the D094 PostgreSQL 18 runtime artifact from immutable, reviewable
inputs without changing PostgreSQL behavior or weakening D092.

## Used By

- `postgres` for the persistent database runtime.
- `postgres-permissions` for the existing idempotent grant verification.
- the D094 local and CI supply-chain gates.

## Contains

- one multi-stage Dockerfile;
- one machine-readable input lock;
- the Docker build-context boundary; and
- the existing database permission script owned by D092.

The final image contains the official PostgreSQL 18.6 Bookworm filesystem,
the rebuilt `gosu 1.19` binary and its Apache-2.0 license. The Go builder,
source archive and build cache do not enter the final stage.

## Never Contains

- database passwords, JWTs, cloud credentials or `.env` files;
- application, migration or customer data;
- an ignored vulnerability or scanner exception;
- a floating image/source reference; or
- PostgreSQL schema or product behavior.

## Verification

From a clean, committed revision at the repository root, build the image twice
and require the second build to reproduce the first runtime manifest digest:

```powershell
.\scripts\verify-postgres-runtime-image.ps1 `
    -EvidenceDirectory build/d094/first `
    -RequireCleanWorktree

$runtimeDigest = (Get-Content -Raw build/d094/first/provenance.json |
    ConvertFrom-Json).runtimeManifestDigest

.\scripts\verify-postgres-runtime-image.ps1 `
    -EvidenceDirectory build/d094/rebuild `
    -ExpectedRuntimeDigest $runtimeDigest `
    -RequireCleanWorktree
```

Generated SBOM, provenance and scan reports are written below the ignored
`build/d094/` directory. A candidate report from an uncommitted worktree is
useful for implementation validation but is not the final release artifact.
Only the second clean build can report `formalReleaseEligible: true`.
