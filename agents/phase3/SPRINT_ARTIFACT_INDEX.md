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
Sprint 3.4: CERTIFIED / COMPLETE; D084 DISCHARGED
Sprint 3.4.1: COMPLETE
Sprint 3.5: CERTIFIED / COMPLETE
Sprint 3.5.1: COMPLETE
Sprint 3.6: CERTIFIED / COMPLETE
Sprint 3.6.1: COMPLETE
D085: ACCEPTED / COMPLETE
D086: ACCEPTED / COMPLETE
Sprint 3.7: CERTIFIED / COMPLETE
Sprint 3.7.1: COMPLETE
D087: ACCEPTED / COMPLETE
Sprint 3.8: CERTIFIED / COMPLETE
Sprint 3.8.1: COMPLETE
D088: ACCEPTED / COMPLETE
Sprint 3.9: CERTIFIED / COMPLETE
Sprint 3.9.1: COMPLETE
D089: ACCEPTED / COMPLETE
Sprint 4.0: CERTIFIED / COMPLETE
Sprint 4.0.1: COMPLETE
D090: ACCEPTED / COMPLETE
Sprint 4.1: CERTIFIED / COMPLETE
Sprint 4.1.1: COMPLETE
D091: ACCEPTED / COMPLETE
Sprint 4.2: CERTIFIED / COMPLETE
Sprint 4.2.1: COMPLETE
D092: ACCEPTED / COMPLETE
D093: ACCEPTED / COMPLETE
D094: ACCEPTED / COMPLETE / PASS
D095: ACCEPTED / COMPLETE
Sprint 4.3: CERTIFIED / COMPLETE
Sprint 4.3.1: COMPLETE
D096: ACCEPTED / COMPLETE / FROZEN
Sprint 4.4: NEXT - IMPLEMENTATION AUTHORIZATION PENDING
```

Exactly one Phase 3 control gate is marked `NEXT`; implementation still
requires explicit founder authorization.

## Accepted Sprint Artifacts

| Sprint | Integrated commit | Primary evidence | Status |
|---|---|---|---|
| 3.0 | `fe44353` | Runtime composition, PostgreSQL repository certification and Phase 3 plan | CERTIFIED / COMPLETE |
| 3.1 | `b8bd518` | D080, NDJSON evidence importer, HTTP contract tests, PostgreSQL HTTP integration test and certified fixture | CERTIFIED / COMPLETE |
| 3.2 | `8bbbc19` | D081 contract, atomic Decision creation, immutable retry policy and PostgreSQL concurrency certification | CERTIFIED / COMPLETE |
| 3.3 | `087f94d` | D082 contract, deterministic Recommendation/ROI policy, atomic Recommendation creation and PostgreSQL concurrency certification | CERTIFIED / COMPLETE |
| 3.3.1 | `838f156` | Post-transaction provider invocation, bounded explanation context and provider-failure isolation | COMPLETE |
| 3.4 | Sprint 3.4 closure commit; hash intentionally not self-recorded | D083 contract, D084 historical process exception, atomic review and Ledger orchestration, strict Ledger sequence, deterministic result validation and PostgreSQL certification | CERTIFIED / COMPLETE; D084 DISCHARGED |
| 3.5 | `f67257d` | Deterministic 30-line NDJSON workflow, non-persisted Business Value projection, operational guide and local certification harness | CERTIFIED / COMPLETE |
| 3.5.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control and AI-context synchronization | COMPLETE |
| 3.6 | `c3bb8f6` | Java 21 GitHub Actions workflow and real run `30708049322` | CERTIFIED / COMPLETE |
| 3.6.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control and AI-context synchronization | COMPLETE |
| D085 | Current decision commit; hash intentionally not self-recorded | Frozen post-CI MVP delivery order and synchronized control documentation | ACCEPTED / COMPLETE |
| D086 | `d917006` | Frozen Functional REST Application contract, query boundaries, trusted actor transition and HTTP mapping | ACCEPTED / COMPLETE |
| 3.7 | `9ba2138` | Functional D086 REST adapter, Application queries, PostgreSQL read model and real-database certification | CERTIFIED / COMPLETE |
| 3.7.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control and AI-context synchronization | COMPLETE |
| D087 | `0f91778` | Frozen JWT Resource Server, claim, key, actor-translation and authentication-error contract | ACCEPTED / COMPLETE |
| 3.8 | `90e5644` | D087 Resource Server implementation, JWT actor translation and PostgreSQL runtime certification | CERTIFIED / COMPLETE |
| 3.8.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control, agent and AI-context synchronization | COMPLETE |
| D088 | `b8e66e8` | Frozen route authorization, governance preservation, read visibility, Evidence redaction and access-denied contract | ACCEPTED / COMPLETE |
| 3.9 | `66d0e31` | D088 route/method enforcement, Evidence authorization policy and PostgreSQL 18.x runtime certification | CERTIFIED / COMPLETE |
| 3.9.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control, agent, security and AI-context synchronization | COMPLETE |
| D089 | `5cfddd4` | Frozen one-repository GitHub REST synchronization, security, Evidence mapping and failure contract | ACCEPTED / COMPLETE |
| 4.0 | `674b0ba` | Read-only GitHub adapter, provider-neutral synchronization use case, offline contract suite and PostgreSQL certification | CERTIFIED / COMPLETE |
| 4.0.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control, agent, security and AI-context synchronization | COMPLETE |
| D090 | Current certification commit; hash intentionally not self-recorded | Frozen one-account AWS SDK synchronization, security, Evidence mapping and failure contract | ACCEPTED / COMPLETE |
| 4.1 | Current certification commit; hash intentionally not self-recorded | Read-only AWS adapter, offline protocol suite, deterministic Evidence mapping and PostgreSQL certification | CERTIFIED / COMPLETE |
| 4.1.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control, agent, security and AI-context synchronization | COMPLETE |
| D091 | `220c93b` | Frozen single-case Decision Review Workspace, frontend stack, transport, security, interaction and acceptance contract | ACCEPTED / COMPLETE |
| 4.2 | `abf10a9` | Decision Review Workspace, strict API boundary, role-aware actions, frontend quality gates and unchanged backend/PostgreSQL certification | CERTIFIED / COMPLETE |
| 4.2.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control, agent, security, frontend and AI-context synchronization | COMPLETE |
| D092 | Docker runtime contract commit; hash preserved in Git history | Frozen Compose topology, image, secret, network, hardening and persistence contract | ACCEPTED / COMPLETE |
| D093 | Contract and implementation history preserved in Git | R16 case composition, resumable D081/D082 orchestration and case-level uniqueness | ACCEPTED / COMPLETE |
| D094 | Contract and hosted Security evidence preserved in Git | Reproducible PostgreSQL 18.6/gosu remediation, SBOM, provenance and clean Trivy gate | ACCEPTED / COMPLETE / PASS |
| D095 | `31f6e51` | Runtime certification scope correction and mandatory pre-pilot external identity deferral | ACCEPTED / COMPLETE |
| 4.3 | `1613b5d` | Hardened Docker runtime, D093/R16, D094 images, local JWT/RBAC E2E and persistence/recreation | CERTIFIED / COMPLETE |
| 4.3.1 | Current synchronization commit; hash intentionally not self-recorded | Active project-control, agent, runtime, security, pilot, portfolio and AI-context synchronization | COMPLETE |
| D096 | Current contract commit; hash intentionally not self-recorded | Frozen safe logging, metrics, correlation, probes, alerts, retention, exposure and certification contract | ACCEPTED / COMPLETE / FROZEN |

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

### Sprint 3.4

Primary artifacts:

- D083 in `docs/decisions/14_Decision_Log.md`;
- D084 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/44_Review_Ledger_Result_Validation_Contract.md`;
- `backend-java/application/reviewdecision/ReviewDecisionUseCase.java`;
- `backend-java/application/appendledgerentry/AppendLedgerEntryUseCase.java`;
- `backend-java/application/ledger/LedgerChain.java`;
- `backend-java/domain/decision/DrcAoa001ResultValidationPolicy.java`;
- `backend-java/ports/out/DecisionRepository.java`;
- `backend-java/ports/out/LedgerRepository.java`;
- `backend-java/adapters/out/postgresql/PostgresDecisionRepository.java`;
- `backend-java/adapters/out/postgresql/PostgresLedgerRepository.java`;
- `src/test/java/imperator/application/reviewdecision/ReviewLedgerGovernanceTest.java`;
- `src/test/java/imperator/application/ledger/LedgerChainTest.java`;
- `src/test/java/imperator/domain/decision/DrcAoa001ResultValidationPolicyTest.java`;
- `src/test/java/imperator/adapters/out/postgresql/PostgresRepositoryIT.java`.

Completion evidence:

- Java 21 and Maven Enforcer: PASS in the verified offline build;
- unit and HTTP contract tests: 88 passed;
- atomic review and matching Ledger construction: PASS;
- identical replay, immutable conflict and duplicate-business-action
  protection: PASS;
- strict linear chain and no-fork validation: PASS;
- deterministic realized recovery and variance: PASS;
- PostgreSQL rollback and concurrency certification tests: implemented and
  compiled;
- REST, Flyway V1, schema, dependencies and frozen contracts unchanged;
- PostgreSQL 18.2 runtime certification: PASS through the complete Sprint 3.7
  integration-profile execution on 2026-08-02;
- atomicity, replay, rollback, concurrency, Ledger linearity and fork
  prevention: PASS;
- D084 deferred obligation: **DISCHARGED**;
- status: **CERTIFIED / COMPLETE**.

### Sprint 3.5

Primary artifacts:

- `backend-java/application/businessvalue/BusinessValueProjection.java`;
- `backend-java/application/businessvalue/ProjectBusinessValueUseCase.java`;
- `backend-java/ports/in/ProjectBusinessValueInputPort.java`;
- `src/test/resources/evidence/drc-aoa-001-business-value-demo.jsonl`;
- `src/test/java/imperator/api/evidence/EndToEndBusinessValueDemoTest.java`;
- `docs/demos/45_End_To_End_Business_Value_Demo.md`.

Certification evidence:

- Java 21 and Maven Enforcer: PASS;
- default unit, HTTP contract and local demo tests: 89 passed;
- deterministic import: 30 accepted, 0 rejected;
- complete local Evidence-to-Business-Value workflow: PASS;
- Explanation Provider isolation: PASS;
- Business Value unavailable before Result Validation: PASS;
- realized savings sourced only from `result_validated`: PASS;
- identical governance replay and deterministic re-projection: PASS;
- `GET /api/v1/business-value` remains controlled `501`;
- no schema, Flyway, dependency, frozen-contract or Decision Log changes;
- status: **CERTIFIED / COMPLETE**.

### Sprint 3.6

Primary artifacts:

- `.github/workflows/java-ci.yml`;
- executable Git mode for `mvnw`.

Certification evidence:

- integrated commit: `c3bb8f6`;
- GitHub Actions run `30708049322`: success;
- all eight job lifecycle steps: success;
- Maven Wrapper `3.3.4`: PASS;
- Apache Maven `3.9.16`: PASS;
- Eclipse Adoptium Java 21: PASS;
- 89 tests passed with no failures, errors or skips;
- executable JAR packaging and `BUILD SUCCESS`: PASS;
- no Java, test, dependency, schema, Flyway, documentation, contract or
  roadmap changes;
- status: **CERTIFIED / COMPLETE**.

### Sprint 3.7

Primary artifacts:

- D086 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/45_Functional_REST_Application_Contract.md`;
- `backend-java/adapters/out/postgresql/PostgresMvpReadModelQueryAdapter.java`;
- the query input ports and use cases under `backend-java/application/query`;
- functional controllers and REST mappers under `backend-java/api`;
- `src/test/java/imperator/api/FunctionalRestApiContractTest.java`;
- `src/test/java/imperator/api/FunctionalRestPostgresIT.java`.

Certification evidence:

- implementation commit `9ba2138`;
- Java 21, Maven Wrapper and Maven Enforcer: PASS;
- 98 default tests and 29 PostgreSQL integration tests: PASS;
- PostgreSQL 18.2: PASS;
- Flyway V1 migrate, validate and second no-op migrate: PASS;
- all 15 D086 routes through real Application and PostgreSQL composition: PASS;
- governance atomicity, replay, rollback, concurrency and Ledger linearity:
  PASS;
- executable JAR and `BUILD SUCCESS`: PASS;
- no Domain, write-repository, schema, migration, dependency, frozen-contract
  or Decision Log modification during certification;
- status: **CERTIFIED / COMPLETE**.

### Sprint 3.8

Primary artifacts:

- D087 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/46_JWT_Authentication_Contract.md`;
- `backend-java/api/security`;
- `src/test/java/imperator/api/security/JwtAuthenticationContractTest.java`;
- JWT-enabled Functional REST and PostgreSQL integration tests.

Certification evidence:

- implementation commit `90e5644`;
- Java 21, 105 default tests and executable JAR: PASS;
- PostgreSQL 18.2 and 29 integration tests: PASS;
- Flyway V1 migrate, validate and second no-op migrate: PASS;
- all 15 D086 routes authenticated through the D087 perimeter: PASS;
- JWT-derived governance actor UUID and role persistence: PASS;
- trusted actor production mechanism absent: PASS;
- no Domain, Application business behavior, Ports, schema, migration, route,
  frozen-contract or Decision Log modification;
- status: **CERTIFIED / COMPLETE**.

### Sprint 3.9

Primary artifacts:

- D088 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/47_RBAC_Authorization_Contract.md`;
- `backend-java/api/security/JwtResourceServerConfiguration.java`;
- `backend-java/api/security/JwtAccessDeniedHandler.java`;
- `backend-java/api/decisions/DecisionEvidenceAuthorizationPolicy.java`;
- `src/test/java/imperator/api/security/RbacAuthorizationContractTest.java`;
- `src/test/java/imperator/api/decisions/RbacEvidenceVisibilityContractTest.java`.

Certification evidence:

- implementation and certification commit `66d0e31`;
- Java 21, 111 default tests and executable JAR: PASS;
- PostgreSQL 18.4 and 29 integration tests: PASS under the PostgreSQL 18.x
  (18.2+) gate;
- Flyway V1 migrate, validate and second no-op migrate: PASS;
- complete 15-route/four-role matrix: PASS;
- route denial non-mutation and exact `403` envelope: PASS;
- D083 governance and JWT-derived actor identity preservation: PASS;
- role/type-based Confidential Evidence handling and fail-closed Restricted
  Evidence redaction: PASS;
- no Domain, Application business behavior, Ports, schema, migration,
  dependency, route, frozen-contract or Decision Log modification;
- status: **CERTIFIED / COMPLETE**.

### Sprint 4.0

Primary artifacts:

- D089 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/48_GitHub_Integration_Contract.md`;
- `backend-java/application/synchronizeevidence`;
- `backend-java/ports/in/SynchronizeEvidenceInputPort.java`;
- provider-neutral Evidence source types under `backend-java/ports/out`;
- `backend-java/adapters/out/github`;
- `backend-java/bootstrap/GitHubRuntimeConfiguration.java`;
- `backend-java/bootstrap/GitHubRuntimeProperties.java`;
- `src/test/java/imperator/adapters/out/github/GitHubRestAdapterTest.java`;
- `src/test/java/imperator/adapters/out/github/GitHubEvidenceSyncIT.java`;
- `src/test/java/imperator/application/synchronizeevidence/SynchronizeEvidenceUseCaseTest.java`.

Certification evidence:

- contract-freeze commit `5cfddd4` and implementation commit `674b0ba`;
- Java 21, 128 default tests and executable JAR: PASS;
- PostgreSQL 18.4 and 30 integration tests: PASS under the PostgreSQL 18.x
  (18.2+) gate;
- Flyway V1 migrate, validate and second no-op migrate: PASS;
- exact GET-only GitHub REST surface and mandatory headers: PASS;
- single organization, repository, default branch and explicit window: PASS;
- exact `IMP-214` correlation, ambiguity rejection and Evidence completeness:
  PASS;
- deterministic UUIDv5 identity for `E-GH-001`, `E-GH-002` and `E-GH-003`:
  PASS;
- disabled and invalid configuration produce zero provider calls: PASS;
- stable replay, identity conflict, pagination, retry, rate-limit, timeout and
  response-bound behavior: PASS;
- local protocol-faithful HTTP stub to existing Evidence import and certified
  PostgreSQL persistence: PASS;
- token, raw title, body and review text leakage: absent;
- no public route, scheduler, schema, migration, dependency or business
  authority added;
- ASI, DII and Decision Stability: 100%;
- status: **CERTIFIED / COMPLETE**.

### Sprint 4.1

Primary artifacts:

- D090 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/49_AWS_Integration_Contract.md`;
- `backend-java/adapters/out/aws`;
- `backend-java/bootstrap/AwsRuntimeConfiguration.java`;
- `backend-java/bootstrap/AwsRuntimeProperties.java`;
- `src/test/java/imperator/adapters/out/aws/AwsSdkEvidenceSourceAdapterTest.java`;
- `src/test/java/imperator/adapters/out/aws/AwsEvidenceSyncIT.java`;
- `src/test/java/imperator/adapters/out/aws/AwsContractStubServer.java`.

Certification evidence:

- Java 21, 139 default tests and executable JAR: PASS;
- PostgreSQL 18.4 and 31 integration tests: PASS under the PostgreSQL 18.x
  (18.2+) gate;
- Flyway V1 migrate, validate and second no-op migrate: PASS;
- exact AWS SDK `2.49.6` read-only API and URL Connection Client surface: PASS;
- exact account verification, Region, resource scope and finalized-month
  selection: PASS;
- deterministic UUIDv5 identity for `E-AWS-001` through `E-AWS-004`: PASS;
- authorized final-microsecond D090 precision correction and stable PostgreSQL
  replay: PASS;
- disabled and invalid configuration produce zero provider calls: PASS;
- source-identity conflict, pagination, retry, throttling, timeout,
  access-denied and unsupported-service behavior: PASS;
- protocol-faithful local AWS stub to existing Evidence import and certified
  PostgreSQL persistence: PASS;
- real credentials, raw provider payload and secret leakage: absent;
- no public route, scheduler, schema, migration or business authority added;
- D089 GitHub behavior unchanged;
- ASI and DII: 100%; Decision Stability: PASS;
- status: **CERTIFIED / COMPLETE**.

### Sprint 4.2

Primary artifacts:

- D091 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/50_Executive_Dashboard_Contract.md`;
- `frontend/app`;
- `frontend/features/decision-review`;
- `frontend/services/api` and `frontend/services/auth`;
- `frontend/components/ui` and `frontend/styles`;
- `frontend/tests` and `frontend/e2e`;
- exact `frontend/package-lock.json`.

Certification evidence:

- contract-freeze commit `220c93b` and implementation commit `abf10a9`;
- Node.js 24.19.0 and npm 11.17.0: PASS;
- format, lint, strict TypeScript and Next.js production build: PASS;
- 35 unit/component tests and all D091 coverage thresholds: PASS;
- npm audit at moderate severity: 0 vulnerabilities;
- 9 Playwright scenarios passed across 1440x900, 1024x768 and 390x844;
  6 viewport-independent cases were intentionally skipped outside desktop;
- no horizontal overflow or interactive controls outside the viewport;
- exact four-role action presentation, Confidential Evidence redaction,
  not-ready Business Value and isolated failure behavior: PASS;
- volatile token session, same-origin API rewrite, strict schemas, response
  bounds, correlation and idempotency: PASS;
- Java 21 backend regression: 139 default tests passed;
- PostgreSQL 18.4, Flyway migrate/validate/no-op migrate and 31 integration
  tests: PASS;
- Java, SQL, Flyway, routes, schema, Domain, Application, Ports, connectors,
  security configuration, D001-D091 and frozen contracts unchanged;
- status: **CERTIFIED / COMPLETE**.

### Sprint 4.3

Primary artifacts:

- D092-D095 in `docs/decisions/14_Decision_Log.md`;
- `docs/architecture/51_Docker_Production_Runtime_Contract.md`;
- `docs/architecture/52_DRC_AOA_001_Case_Composition_Contract.md`;
- `docs/architecture/55_PostgreSQL_Runtime_Supply_Chain_Remediation_Contract.md`;
- `infra/docker` and `scripts/verify-docker-runtime.ps1`;
- `scripts/verify-postgres-runtime-image.ps1` and `.github/workflows/security.yml`;
- D093 Application, REST, PostgreSQL and Flyway V2 implementation plus focused
  tests.

Certification evidence:

- Java 21 default suite: 151 passed;
- PostgreSQL 18.6 integration suite: 34 passed;
- hosted Java CI, Security and D094 supply-chain jobs: PASS;
- final SHA-tagged images: zero fixable High/Critical findings and zero secrets;
- Flyway, database grants, runtime health, hardening and loopback exposure:
  PASS;
- local JWT/RBAC, API-composed E2E and persistence after recreation: PASS;
- D095 defers external pilot Keycloak conformance without changing D087/D088;
- status: **CERTIFIED / COMPLETE**.

## Next Artifact Boundary

Sprint 4.4 is the sole next gate. D096 is frozen and accepted; implementation
still requires separate explicit authorization. It may address only the exact
Observability boundary in Document 56 and must preserve business Ledger,
Business Value, connector, JWT/RBAC and base Docker behavior. External
identity, pilot behavior, real customer data and public exposure remain
prohibited.

## Agent Rule

Before using any Phase 3 prompt:

1. confirm there is exactly one `NEXT` control gate;
2. confirm the prompt matches that gate exactly;
3. read the relevant frozen contracts;
4. inspect the current implementation;
5. reject historical or later-sprint scope as current authorization;
6. stop if a contradiction requires changing a frozen decision.
