# Phase 3 Sprint Artifact Index

Status: **Active index**

This file records accepted Phase 3 implementation evidence. An artifact entry
does not authorize a sprint by itself. Current authorization lives in
`docs/project/PROJECT_STATUS.md` and
`docs/project/PHASE_AND_SPRINT_MAP.md`.

## Current Gate

```text
Sprint 3.1: CERTIFIED / COMPLETE
Sprint 3.2: CERTIFIED / COMPLETE
Sprint 3.3: CERTIFIED / COMPLETE
Sprint 3.3.1: COMPLETE
Sprint 3.4: NEXT
```

Exactly one Phase 3 sprint is authorized as `NEXT`.

## Certified Sprint Artifacts

| Sprint | Integrated commit | Primary evidence | Status |
|---|---|---|---|
| 3.0 | `fe44353` | Runtime composition, PostgreSQL repository certification and Phase 3 plan | CERTIFIED / COMPLETE |
| 3.1 | `b8bd518` | D080, NDJSON evidence importer, HTTP contract tests, PostgreSQL HTTP integration test and certified fixture | CERTIFIED / COMPLETE |
| 3.2 | `8bbbc19` | D081 contract, atomic Decision creation, immutable retry policy and PostgreSQL concurrency certification | CERTIFIED / COMPLETE |
| 3.3 | `087f94d` | D082 contract, deterministic Recommendation/ROI policy, atomic Recommendation creation and PostgreSQL concurrency certification | CERTIFIED / COMPLETE |
| 3.3.1 | `838f156` | Post-transaction provider invocation, bounded explanation context and provider-failure isolation | COMPLETE |

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

### Sprint 3.2

Primary artifacts:

- D081 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/42_Deterministic_Decision_Creation_Contract.md`;
- `backend-java/application/createdecision/CreateDecisionUseCase.java`;
- `backend-java/application/exceptions/DecisionCreationConflictException.java`;
- `backend-java/ports/out/DecisionRepository.java`;
- `backend-java/adapters/out/postgresql/PostgresDecisionRepository.java`;
- `src/test/java/imperator/application/createdecision/CreateDecisionUseCaseTest.java`;
- `src/test/java/imperator/adapters/out/postgresql/PostgresRepositoryIT.java`.

Certification:

- Java 21 and Maven Enforcer: PASS;
- unit and HTTP contract tests: 67 passed;
- PostgreSQL 18.2 integration tests: 18 passed;
- atomic create-if-absent and one persisted identity: PASS;
- identical retry and immutable conflict behavior: PASS;
- progressed Decision replay protection: PASS;
- equivalent and conflicting concurrency: PASS;
- no Recommendation, ROI, Explanation, Review or Ledger behavior introduced;
- Domain, REST, Flyway V1 and frozen contracts unchanged.

### Sprint 3.3

Primary artifacts:

- D082 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/43_Deterministic_Recommendation_ROI_Contract.md`;
- `backend-java/domain/decision/DrcAoa001RecommendationPolicy.java`;
- `backend-java/application/generaterecommendation/GenerateRecommendationUseCase.java`;
- `backend-java/application/exceptions/RecommendationCreationConflictException.java`;
- `backend-java/application/exceptions/RecommendationNotReadyException.java`;
- `backend-java/ports/out/RecommendationRepository.java`;
- `backend-java/adapters/out/postgresql/PostgresRecommendationRepository.java`;
- `src/test/java/imperator/domain/decision/DrcAoa001RecommendationPolicyTest.java`;
- `src/test/java/imperator/adapters/out/postgresql/PostgresRepositoryIT.java`.

Certification:

- Java 21 and Maven Enforcer: PASS;
- unit and HTTP contract tests: 70 passed;
- PostgreSQL 18.2 integration tests: 22 passed;
- exact policy action, reason and `MODEL_CHANGE` type: PASS;
- monthly recovery `EUR 1620.00` and annualized savings `EUR 19440.00`: PASS;
- confidence/risk `92 / LOW` and `90 / MEDIUM`: PASS;
- missing mandatory Evidence persists no partial state: PASS;
- atomic create-if-absent, immutable retry and progressed-state protection: PASS;
- equivalent and conflicting concurrency: PASS;
- Explanation Provider invocation: none;
- Domain dependency isolation: PASS;
- REST, Flyway V1, schema, migrations and frozen contracts unchanged.

### Sprint 3.3.1

Primary artifacts:

- `backend-java/application/generaterecommendation/GenerateRecommendationUseCase.java`;
- `backend-java/application/generaterecommendation/GenerateRecommendationResult.java`;
- `backend-java/ports/out/RecommendationExplanationRequest.java`;
- `backend-java/bootstrap/PostgresRuntimeConfiguration.java`;
- `src/test/java/imperator/application/generaterecommendation/GenerateRecommendationExplanationTest.java`;
- `src/test/java/imperator/bootstrap/PostgresRuntimeConfigurationTest.java`.

Completion evidence:

- Java 21 and Maven Enforcer: PASS;
- unit and HTTP contract tests: 73 passed;
- provider invocation occurs after the deterministic transaction: PASS;
- prepared context uses persisted Recommendation outputs: PASS;
- Evidence ids, assumption ids and policy version remain visible: PASS;
- unavailable and failing providers preserve deterministic state: PASS;
- Recommendation type, action, reason, savings, confidence and risk unchanged:
  PASS;
- no provider SDK, live model call, schema, migration, REST or Domain change;
- status: **COMPLETE**.

## Next Artifact Boundary

Sprint 3.4 has no accepted implementation artifact yet. Its prompt or plan may
guide work only after it is checked against the current gate, frozen contracts
and existing implementation.

Sprint 3.4 may implement only the authorized Human Review, append-only Ledger
and Result Validation boundary. Its exact file and behavior scope requires a
pre-implementation review. It must preserve deterministic Recommendation and
Explanation truth and must not introduce later security, frontend, connector or
operational-hardening behavior.

## Agent Rule

Before using any Phase 3 prompt:

1. confirm there is exactly one `NEXT` sprint;
2. confirm the prompt matches that sprint exactly;
3. read the relevant frozen contracts;
4. inspect the current implementation;
5. reject historical or later-sprint scope as current authorization;
6. stop if a contradiction requires changing a frozen decision.
