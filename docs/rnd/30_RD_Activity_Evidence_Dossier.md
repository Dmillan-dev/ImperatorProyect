# 30 - I+D Activity Evidence Dossier

## Purpose

Create a documentation system for IMPERATOR project development evidence.

This dossier defines how to record:

- architecture work,
- future code work,
- hours and people,
- technical objects,
- experiments,
- tests,
- decisions,
- failures,
- prototype evidence,
- development milestones,
- costs and resources.

It is designed for future startup diligence and possible I+D+i accreditation support.

It is not legal, accounting or tax advice. Formal use for incentives, grants, certification or informes motivados should be reviewed with qualified advisors.

## Reference Baseline

This dossier is aligned with three practical ideas from public references:

1. R&D should be systematic, evidence-backed and aimed at new knowledge, new applications or substantial improvement.
2. In Spain, software-related R&D or technological innovation analysis should distinguish advanced or substantially improved software from routine maintenance.
3. For tax or official review, activities and expenses should be specifically individualized by project, with technical evidence and traceability.

Reference links:

- OECD Frascati Manual 2015: https://www.oecd.org/en/publications/frascati-manual-2015_9789264239012-en.html
- OECD Oslo Manual 2018: https://www.oecd.org/en/publications/2018/10/oslo-manual-2018_g1g9373b/full-report/component-10.html
- Ministerio de Ciencia, Innovacion y Universidades - Innovar: https://www.ciencia.gob.es/Innovar.html
- BOE Ley 27/2014, articulo 35: https://boe.es/buscar/act.php?id=BOE-A-2014-12328

## IMPERATOR Project Summary

IMPERATOR is an Operating System for Operational Intelligence.

It manages decisions across cloud, code and AI systems.

The MVP is a Decision Recovery Workflow for AI/cloud spend.

Phase 1 scope, exit criteria and allowed future scaffolding are controlled by `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`.

The first proof is:

```text
Operational Event
-> Connector Intake
-> Normalized Evidence
-> Decision ROI Case
-> ROI View
-> Recommendation
-> Decision Ledger Entry
-> Decision Review Workspace
-> Result Validation
```

The central research and development question is:

Can a system reconstruct one business technology decision across Jira, GitHub, AWS and OpenAI + Anthropic Claude, transform source signals into trusted evidence, calculate explainable ROI, create an approval-ready recommendation and preserve an immutable ledger history without exposing restricted data or executing provider-side changes?

## I+D Thesis

The possible I+D/i value of IMPERATOR should be documented around technical uncertainty and systematic resolution, not around normal SaaS delivery.

Candidate technical uncertainties:

| Area | Uncertainty |
|---|---|
| Cross-system correlation | Can heterogeneous business, code, cloud and AI signals be correlated into one Decision ROI Case reliably enough for executive review? |
| Evidence normalization | Can raw provider facts become provider-neutral evidence without losing lineage, freshness, sensitivity or confidence? |
| Explainable ROI | Can cost, usage and assumptions produce decision-grade ROI without becoming a black box? |
| Ledger integrity | Can approval, rejection, deferral, implementation and result validation be preserved as immutable, reviewable business history? |
| AI boundaries | Can AI explain prepared context without raw prompts, completions, secrets or direct source access? |
| Connector replaceability | Can Jira, GitHub, AWS and AI providers be replaced or disabled without breaking the core domain? |
| Decision workspace | Can one screen make evidence, ROI, risk, blockers and authority understandable enough for human approval? |

## What Must Not Be Claimed Without Evidence

Do not claim:

- software implementation exists before code exists,
- tests passed before they were run,
- hours were worked if not tracked,
- production readiness,
- legal/fiscal eligibility,
- autonomous AI decision-making,
- realized customer value before validation,
- proprietary algorithms before they are designed and evidenced,
- I+D qualification without external review.

## Work Package Model

Use work packages to group hours, artifacts and evidence.

| WP | Name | Scope | Evidence examples |
|---|---|---|---|
| `WP00` | Project Context and Strategy | Vision, MVP scope, ICP, market thesis | Business docs, decision log |
| `WP01` | Domain Model | Decision ROI Case, Evidence, Recommendation, Ledger | Core Domain Model, glossary |
| `WP02` | Architecture and Boundaries | Technical architecture, module boundaries, RFCs | Architecture context, ADRs, RFCs |
| `WP03` | Connector and Evidence Model | Jira, GitHub, AWS, OpenAI + Anthropic Claude source contracts | Connector framework, per-connector contracts |
| `WP04` | ROI and FinOps Model | Cost model, assumptions, confidence, realized value | ROI slice, evidence pack |
| `WP05` | Ledger and Governance | Append-only ledger, approval model, identity | Decision Ledger v2, identity model |
| `WP06` | Security and AI Data Governance | Sensitivity, least privilege, AI boundaries, threat model | Security model, quality attributes |
| `WP07` | Product Workspace | Decision Review Workspace behavior | Screen contract, demo references |
| `WP08` | Vocabulary and Contracts | Event/evidence vocabulary, API intent, database model | API specification, database model, vocabulary |
| `WP09` | Tests and Validation | Acceptance scenarios, manual tests, future automated tests | Acceptance plan, experiment/test records |
| `WP10` | Future Prototype Implementation | Code, commits, PRs, object register, test reports | Future source code and CI evidence |
| `WP11` | Pilot and Customer Validation | Pilot data, feedback, outcomes, measured value | Future pilot reports |

## Current Phase 0 Evidence Inventory

| Evidence area | Current artifact |
|---|---|
| Strategic baseline | `docs/business/00_Project_Charter.md`, `docs/business/01_Vision_and_Positioning.md` |
| MVP scope | `docs/product/20_MVP_Decision_ROI_Platform_Blueprint.md` |
| Vertical slice | `docs/product/24_MVP_Vertical_Slice.md` |
| ROI model | `docs/product/25_MVP_ROI_Slice.md` |
| Manual evidence pack | `docs/product/26_Manual_Evidence_Pack_AI_Onboarding_Assistant.md` |
| Acceptance plan | `docs/product/27_MVP_Acceptance_Test_Plan.md` |
| Identity and approval | `docs/product/28_Identity_Access_Approval_Model.md` |
| Screen behavior | `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` |
| Domain model | `docs/product/CORE_DOMAIN_MODEL.md` |
| API intent | `docs/product/API_SPECIFICATION.md` |
| Ledger contract | `docs/product/DECISION_LEDGER_V2.md` |
| Architecture context | `docs/architecture/21_Technical_Architecture_Context.md` |
| Security and governance | `docs/architecture/26_Security_Data_Governance_Threat_Model.md` |
| Quality attributes | `docs/architecture/27_Quality_Attributes.md` |
| Connector contracts | `docs/architecture/28_Per_Connector_MVP_Contracts.md` |
| Event/evidence vocabulary | `docs/architecture/29_Event_Evidence_Vocabulary.md` |
| Decisions | `docs/decisions/14_Decision_Log.md` |
| Agent work model | `agents/README.md` |

## Future Code Evidence Rules

When implementation is explicitly approved, every code-related activity should be traceable to one or more documents.

For every meaningful code change, capture:

| Evidence | Required content |
|---|---|
| Branch or PR | Work package, objective, linked decision/RFC/doc |
| Commit message | Small technical intent, not generic wording |
| Code object | Module, domain object, API contract, algorithm or test |
| Test result | What was tested, input, expected output, actual output |
| Review note | Why the approach was accepted or changed |
| Technical uncertainty | What was unknown before the work |
| Artifact link | File path, PR, screenshot, log or report |

Recommended future branch naming:

```text
wp03-connector-evidence-vocabulary
wp04-roi-calculation-rules
wp05-ledger-snapshot-contract
wp09-acceptance-tests-drc-aoa-001
```

Recommended future commit message shape:

```text
WP04: add deterministic ROI assumption mapping for DRC-AOA-001
WP05: preserve evidence snapshot metadata in ledger model
WP09: add negative test for restricted prompt evidence
```

## Technical Object Register

Every significant object should be registered when it becomes real.

Object categories:

| Category | Examples |
|---|---|
| Domain object | Decision ROI Case, Evidence, ROI View, Recommendation, Ledger Entry |
| Event object | `ai_cost_observed`, `approved`, `result_validated` |
| Connector object | Jira source contract, AWS cost source, AI usage source |
| API object | conceptual endpoint, future DTO, future command |
| Database object | conceptual entity, future table, edge relation, snapshot |
| Algorithm object | ROI calculation, confidence scoring, correlation rule |
| Security object | sensitivity classifier, role filter, AI context filter |
| Test object | acceptance scenario, fixture, negative test, performance check |
| UI object | Decision Header, Evidence Chain, ROI Summary, Action Bar |

Each object should link to:

- owner,
- work package,
- status,
- source document,
- code file later,
- test evidence later,
- decision/RFC if relevant.

## Hours and Personnel Documentation

Do not reconstruct hours loosely.

Use `docs/rnd/templates/01_ACTIVITY_LOG_TEMPLATE.md` for daily or weekly logs.

Minimum fields:

| Field | Rule |
|---|---|
| Date | Actual date of work |
| Person | Real contributor |
| Hours | Actual hours spent |
| Work package | Use WP model |
| Activity type | Research, architecture, code, test, review, management, routine |
| Technical uncertainty | Required for I+D candidate work |
| Output | Document, code, test, decision, failed attempt |
| Evidence link | Repo path, PR, test report, screenshot or note |
| Reviewer | Person who reviewed or accepted |

Monthly summaries should not replace detailed logs. They summarize detailed logs.

## Cost and Resource Documentation

Future cost evidence should be grouped by work package:

| Cost type | Evidence |
|---|---|
| Personnel | Activity logs, employment/contractor records later |
| Cloud | AWS invoices or tagged cost exports |
| AI usage | OpenAI/Anthropic usage and billing export |
| Software tools | invoices, subscriptions, allocation note |
| External services | contract, invoice, deliverable |
| Hardware | invoice, allocation, asset record |

Rule:

Costs must be connected to actual work packages and artifacts. Do not allocate broad company spend to I+D without a documented basis.

## Experiment and Test Documentation

Use `docs/rnd/templates/03_EXPERIMENT_TEST_RECORD_TEMPLATE.md` for:

- architecture experiments,
- prototype comparisons,
- connector mapping tests,
- ROI calculation checks,
- AI explanation evaluations,
- security/redaction tests,
- acceptance scenario runs,
- performance checks later.

Each experiment should capture:

1. hypothesis,
2. uncertainty,
3. method,
4. input data,
5. expected result,
6. actual result,
7. failure or limitation,
8. decision,
9. next action.

Failed experiments are valuable evidence when they show technical uncertainty and systematic learning.

## Test Evidence Model

Future test evidence should be organized by level:

| Level | Examples | Evidence |
|---|---|---|
| Manual acceptance | P0 scenarios from acceptance plan | signed checklist or run note |
| Unit test | ROI calculation, parser, policy rule | test output later |
| Integration test | prepared evidence to Decision ROI Case | test report later |
| Security test | Restricted data rejection, role filtering | negative test result |
| Regression test | ledger immutability and snapshots | test run history |
| Performance test | Decision Review Workspace prepared read target | benchmark note later |
| AI evaluation | explanation cites evidence IDs and refuses raw data | evaluation record |

## Milestone Evidence

Use milestones to freeze proof points:

| Milestone | Evidence required |
|---|---|
| `M0` Phase 0 Context Complete | Canonical docs, decision log, vocabulary, R&D dossier |
| `M1` Manual Case Reconstructed | Completed evidence table for `DRC-AOA-001` |
| `M2` Prototype Skeleton Approved | Explicit decision authorizing code scaffolding |
| `M3` First End-to-End Prototype | Code, test output, screenshots, manual run |
| `M4` First Acceptance Pass | Acceptance plan run with results |
| `M5` First Pilot Review | Customer/anonymized pilot evidence and findings |
| `M6` First Result Validation | Post-action evidence and realized value |

## Routine vs I+D Candidate Work

Not all work should be treated as I+D candidate work.

| Activity | Likely classification for evidence purposes |
|---|---|
| Defining novel Decision ROI Case model | I+D/i candidate |
| Designing evidence lineage and correlation | I+D/i candidate |
| Testing explainable ROI under uncertainty | I+D/i candidate |
| Building a normal login form | Routine/product engineering |
| Styling a standard dashboard | Routine/product engineering |
| Updating dependencies | Routine maintenance |
| Fixing a simple typo | Non-I+D |
| Writing acceptance tests for uncertain behavior | I+D/i candidate if tied to uncertainty |
| Refactoring without new knowledge or substantial improvement | Usually routine |

This table is an internal documentation guide, not a legal conclusion.

## Monthly I+D Review

At the end of each month:

1. Review all activity logs.
2. Confirm hours are supported by artifacts.
3. Map artifacts to work packages.
4. Record experiments and failures.
5. Update technical object register.
6. Record new architecture decisions in the Decision Log.
7. Mark routine work separately.
8. Produce monthly summary from template.
9. Store supporting evidence links.
10. Identify next month uncertainties.

## Annual or Funding Dossier Structure

If IMPERATOR later needs an annual dossier, structure it as:

1. Executive technical summary.
2. Project objective and technical uncertainty.
3. State of the art and differentiation.
4. Work packages.
5. Chronology of activities.
6. Personnel and hours.
7. Technical objects produced.
8. Architecture decisions.
9. Experiments and test results.
10. Failed paths and learnings.
11. Costs and resources.
12. Current prototype/product status.
13. Future work.
14. Appendix with evidence links.

## Current No-Code Statement

As of 2026-07-19:

- no production source code is authorized,
- no connectors are implemented,
- no runtime services are created,
- no database migrations exist,
- no OpenAPI/protobuf generation work is authorized,
- no infrastructure runtime configuration is authorized,
- all current evidence is conceptual, manual or documentary.

This is correct for Phase 0.

## Next Operational Step

When real tracked work begins, create the first monthly activity log from:

`docs/rnd/templates/01_ACTIVITY_LOG_TEMPLATE.md`

Suggested future path:

`docs/rnd/logs/2026-07_ACTIVITY_LOG.md`

Only create it when there are real activities and real hours to record.
