# TFG Local Capture

`capture-docker-demo.mjs` records the existing synthetic value loop against a
running, isolated local Docker runtime. It uses the real API and production
frontend without route interception or alternate business logic.

Preconditions: an empty rehearsal database, migrated V1-V3 schema, the
30-record versioned fixture, four short-lived D087-compatible local tokens,
frontend dependencies and a Playwright-compatible browser. Only a loopback
HTTP frontend is accepted. The HTTPS/JWKS test fixture is verification tooling,
not an application service or external Keycloak conformance evidence.

The script receives one JSON object through stdin:

```text
origin: local frontend URL
outputDirectory: path below ignored build/
decisionId, recommendationId: synthetic UUID v4 identifiers
actors: ADMIN, PLATFORM_ENGINEER, FINANCE, AUDITOR synthetic UUIDs
tokens: matching short-lived local tokens for those four roles
replay: false for the first composition; true only for an equivalent retry
```

Do not persist this credential-bearing input or place it in a command line.
Output contains synthetic API results, five screenshots and a WebM recording.
The token field is masked; tokens remain in process/tab memory.

Approval uses the UI. Implementation and validation use authenticated API
commands to link the canonical post-action Evidence, followed by unmocked UI
reads. The captions and report identify those commands. The UI Evidence picker
lists already linked records; the capture does not claim that it selected the
two previously unlinked post-action records through that picker.

Assertions cover 401, auditor 403 without a write, import, composition/replay,
the three authorized actors, ordered Ledger links and EUR 18,960 realized
synthetic value against EUR 19,440 estimated value. Persistence after container
recreation and observability are separate checks.

`TFG_BROWSER_CHANNEL` defaults to `chrome`. Recording captions are capture
annotations, not new product UI. Review artifacts before copying sanitized
evidence into `docs/tfg/evidence/`. Never reset an existing database to rerun.
