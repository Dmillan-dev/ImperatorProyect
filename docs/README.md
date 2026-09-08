# IMPERATOR Documentation Map

Status: **Active index**

This directory contains the project knowledge system. Use the project control
plane to determine what is current before reading historical or supporting
documents.

## AI-Optimized Entry Point

Read in this order:

1. [Documentation Control Plane](project/README.md)
2. [Current Project Status](project/PROJECT_STATUS.md)
3. [Phase and Sprint Map](project/PHASE_AND_SPRINT_MAP.md)
4. [Decision Log](decisions/14_Decision_Log.md)
5. [Phase 3 Sprint Plan](../agents/phase3/README.md)
6. Only the contracts required by the current task

Do not load all documentation by default.

## Documentation Zones

| Directory | Ownership | Typical document class |
|---|---|---|
| `project/` | Current state, lifecycle and documentation governance | Current control |
| `business/` | Commercial thesis, ICP, GTM and validation | Reference |
| `product/` | MVP boundary, domain, API, ledger and user experience | Frozen contract or reference |
| `architecture/` | Architecture, security, quality and implementation contracts | Frozen contract, reference or historical gate |
| `decisions/` | Accepted decisions and ADRs | Append-only decision history |
| `ai/` | Canonical AI context and recurring collaboration guidance | Active agent context |
| `rfcs/` | Proposed substantial changes | Proposal |
| `research/` | Hypothesis and experiment formats | Template |
| `rnd/` | R&D activity and evidence control | Evidence model and template |
| `demos/` | Certified demonstrations, demo readiness and buyer-validation scripts | Evidence-backed operational guide |
| `pilot/` | Commercial-pilot preparation, runbooks and acceptance evidence | Non-authoritative preparation until Pilot Readiness |
| `portfolio/` | Recruiter-facing technical evidence and concise project positioning | Non-authoritative evidence view |

Agent execution plans live under `../agents/`, not inside `docs/`.

For a short, evidence-backed view of the repository, use the
[Portfolio Evidence entry point](portfolio/README.md). It summarizes current
implementation without replacing canonical contracts or project status.
The stable recruiter link is [Recruiter Summary](recruiter-summary.md).

## Lifecycle Map

### Phase 0 - Strategy and Architecture Readiness

Status: **Complete**

Primary material:

- `business/`;
- `product/05_*` through `product/29_*`;
- `architecture/18_*` through `architecture/30_*`;
- `architecture/DATABASE_MODEL.md`;
- `architecture/CONNECTOR_FRAMEWORK.md`;
- early Decision Log entries and RFCs.

These documents define intent and historical readiness. They do not authorize
the current implementation sprint.

### Phase 1 - Limited MVP Contract

Status: **Complete documentation dossier**

Primary contracts:

- `architecture/31_MVP_Implementation_Standard.md`;
- `architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`;
- `architecture/33_Phase_1_Foundational_Implementation_Decisions.md`;
- `architecture/34_MVP_Implementation_Blueprint.md`;
- `architecture/35_Coding_Principles.md`;
- `../agents/phase1/`.

### Phase 2 - Platform Foundation

Status: **Complete under D079**

Primary controls:

- `architecture/36_Phase_2_Platform_Foundation_Blueprint.md`;
- `architecture/37_Implementation_Contract.md`;
- `architecture/38_Sprint_0_Contract_Gate_Report.md` as historical entry gate;
- `architecture/39_Persistence_Transaction_Contract.md`;
- `architecture/40_Persistence_Schema_Contract.md`;
- `../agents/phase2/README.md`;
- `project/PROJECT_STATUS.md`;
- `project/PHASE_AND_SPRINT_MAP.md`.

### Phase 3 - MVP Business Value Loop

Status: **Active**

Primary controls and semantic inputs:

- D079 in `decisions/14_Decision_Log.md`;
- `../agents/phase3/README.md`;
- `product/20_MVP_Decision_ROI_Platform_Blueprint.md`;
- `product/24_MVP_Vertical_Slice.md`;
- `product/25_MVP_ROI_Slice.md`;
- `architecture/34_MVP_Implementation_Blueprint.md`.

### Pre-Pilot Preparation

Sprint 4.3 and its documentation synchronization are complete. D096 freezes
the Sprint 4.4 observability contract in
`architecture/56_Observability_Runtime_Contract.md`. A separate authorization
now permits its bounded implementation. The following artifacts remain
non-authoritative preparation: they do not implement or certify Sprint 4.4,
pass the deferred Pilot Identity Conformance Gate, authorize Sprint 4.5,
permit customer data or create a new decision:

- `architecture/53_Keycloak_Pilot_Identity_Integration_Preparation.md`;
- `business/21_Design_Partner_Discovery_Playbook.md`;
- `demos/46_DRC_AOA_001_Pilot_E2E_Readiness_Checklist.md`;
- `demos/47_15_Minute_Design_Partner_Demo_and_Value_Assessment.md`;
- `architecture/54_Observability_Preparation.md`;
- `product/30_Pilot_Readiness_Preparation_Checklist.md`; and
- `pilot/README.md` and its controlled commercial operating package.

## Canonical Product Contracts

| Concern | Document |
|---|---|
| MVP product boundary | `product/20_MVP_Decision_ROI_Platform_Blueprint.md` |
| First vertical slice | `product/24_MVP_Vertical_Slice.md` |
| ROI semantics | `product/25_MVP_ROI_Slice.md` |
| Core domain | `product/CORE_DOMAIN_MODEL.md` |
| Conceptual API | `product/API_SPECIFICATION.md` |
| Decision Ledger | `product/DECISION_LEDGER_V2.md` |
| Identity and approval | `product/28_Identity_Access_Approval_Model.md` |
| First workspace | `product/29_Decision_Review_Workspace_Screen_Contract.md` |
| Canonical language | `product/13_Glossary_and_Canonical_Language.md` |

## Canonical Technical Contracts

| Concern | Document |
|---|---|
| Target architecture | `architecture/21_Technical_Architecture_Context.md` |
| Security and data governance | `architecture/26_Security_Data_Governance_Threat_Model.md` |
| Quality attributes | `architecture/27_Quality_Attributes.md` |
| Connector contracts | `architecture/28_Per_Connector_MVP_Contracts.md` |
| Event and evidence vocabulary | `architecture/29_Event_Evidence_Vocabulary.md` |
| Implementation blueprint | `architecture/34_MVP_Implementation_Blueprint.md` |
| Coding principles | `architecture/35_Coding_Principles.md` |
| Phase 2 boundary | `architecture/36_Phase_2_Platform_Foundation_Blueprint.md` |
| Implementation mechanics | `architecture/37_Implementation_Contract.md` |
| Transaction boundary | `architecture/39_Persistence_Transaction_Contract.md` |
| Physical schema contract | `architecture/40_Persistence_Schema_Contract.md` |
| Minimum observability runtime | `architecture/56_Observability_Runtime_Contract.md` |

## Conflict Resolution

Use this precedence:

1. explicit founder authorization;
2. `project/PROJECT_STATUS.md` for the current gate;
3. `decisions/14_Decision_Log.md` for accepted decisions;
4. frozen product and architecture contracts;
5. active phase/sprint execution plan;
6. supporting references;
7. historical records.

Current-state documents identify authorized work but do not override domain or
architecture semantics.

## Language Policy

English is mandatory for all new or modified human-readable project material,
including:

- project control documents;
- sprint definitions;
- AI-agent instructions;
- implementation-facing README files;
- decisions, contracts and technical guidance;
- source-code comments, test descriptions, commit messages and change
  summaries.

Historical documents may remain in their original language to preserve audit
meaning. They must not be treated as current execution instructions.

The canonical compact AI context is
[IMPERATOR Project Context](ai/IMPERATOR_Project_Context.md). The Spanish
one-page prompt is retained as a legacy, non-authoritative artifact.

## Change Rule

When a sprint closes:

1. update project status;
2. update the phase/sprint map;
3. append a decision only if a decision was made;
4. update active agent guidance;
5. preserve completed prompts and reports;
6. validate links and the relevant build/test gate.

Do not rewrite frozen contracts solely to reflect progress.
