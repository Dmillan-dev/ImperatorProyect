# Phase 3 Sprint Artifact Index

Status: **Active index**

This file records accepted Phase 3 implementation evidence. An artifact entry
does not authorize a sprint by itself. Current authorization lives in
`docs/project/PROJECT_STATUS.md` and
`docs/project/PHASE_AND_SPRINT_MAP.md`.

## Current Gate

```text
Sprint 3.1: CERTIFIED / COMPLETE
Sprint 3.2: NEXT
```

Exactly one Phase 3 sprint is authorized as `NEXT`.

## Certified Sprint Artifacts

| Sprint | Integrated commit | Primary evidence | Status |
|---|---|---|---|
| 3.0 | `fe44353` | Runtime composition, PostgreSQL repository certification and Phase 3 plan | CERTIFIED / COMPLETE |
| 3.1 | `b8bd518` | D080, NDJSON evidence importer, HTTP contract tests, PostgreSQL HTTP integration test and certified fixture | CERTIFIED / COMPLETE |

### Sprint 3.0

Primary artifacts:

- `backend-java/bootstrap/PostgresRuntimeConfiguration.java`;
- `src/test/java/imperator/adapters/out/postgresql/PostgresRepositoryIT.java`;
- `agents/phase3/README.md`.

Certification:

- PostgreSQL 18.2: PASS;
- Flyway migrate, validate and second no-op migrate: PASS;
- repository and transaction behavior: PASS;
- Spring runtime composition: PASS.

### Sprint 3.1

Primary artifacts:

- D080 in `docs/decisions/14_Decision_Log.md`;
- `backend-java/api/evidence/EvidenceNdjsonImporter.java`;
- `backend-java/api/evidence/EvidenceController.java`;
- `backend-java/application/importevidence/ImportEvidenceUseCase.java`;
- `src/test/java/imperator/api/evidence/EvidenceImportRouteContractTest.java`;
- `src/test/java/imperator/api/evidence/EvidenceImportHttpIT.java`;
- `src/test/java/imperator/application/importevidence/ImportEvidenceUseCaseTest.java`;
- `src/test/resources/evidence/drc-aoa-001-sample.jsonl`.

Certification:

- Java 21 and Maven Enforcer: PASS;
- unit and HTTP contract tests: 61 passed;
- PostgreSQL integration tests: 15 passed;
- per-line partial import and stable UUID duplicate handling: PASS;
- normalized Evidence persistence through the application role: PASS;
- Domain, Ports, Flyway V1 and frozen contracts unchanged.

## Next Artifact Boundary

Sprint 3.2 has no accepted implementation artifact yet. Its prompt or plan may
guide work only after it is checked against the current gate, frozen contracts
and existing implementation.

Sprint 3.2 may implement deterministic Decision creation only. Recommendation,
ROI, Explanation Provider, Review, Ledger and Result Validation remain later
gates.

## Agent Rule

Before using any Phase 3 prompt:

1. confirm there is exactly one `NEXT` sprint;
2. confirm the prompt matches that sprint exactly;
3. read the relevant frozen contracts;
4. inspect the current implementation;
5. reject historical or later-sprint scope as current authorization;
6. stop if a contradiction requires changing a frozen decision.
