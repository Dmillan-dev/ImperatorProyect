# Phase 1 - Stage 11: Integrated Demo Acceptance

## Purpose

Define the integrated Phase 1 demo acceptance record for IMPERATOR.

This stage checks whether the future MVP implementation proves one complete Decision ROI Case end to end.

It does not create code, test automation, screenshots, services, data fixtures, CI configuration or deployment artifacts.

## Authority Inputs

This stage is governed by all previous Phase 1 stage files:

1. `agents/phase1/00_context_control.md`
2. `agents/phase1/01_product_case_lock.md`
3. `agents/phase1/02_acceptance_contract.md`
4. `agents/phase1/03_architecture_scaffolding_plan.md`
5. `agents/phase1/04_data_persistence_slice.md`
6. `agents/phase1/05_evidence_intake_slice.md`
7. `agents/phase1/06_domain_roi_recommendation_slice.md`
8. `agents/phase1/07_ai_explanation_slice.md`
9. `agents/phase1/08_ledger_approval_slice.md`
10. `agents/phase1/09_review_workspace_slice.md`
11. `agents/phase1/10_auth_observability_security_slice.md`

It is also governed by:

1. `docs/product/27_MVP_Acceptance_Test_Plan.md`
2. `docs/architecture/32_Phase_1_MVP_Scope_and_Exit_Criteria.md`
3. `docs/rnd/30_RD_Activity_Evidence_Dossier.md`
4. `docs/decisions/14_Decision_Log.md`

## Stage Decision

The Phase 1 integrated demo is accepted only if it proves the locked flow:

```text
Authenticated reviewer
-> one manual/static or file/import evidence source
-> one evidence chain
-> one Decision ROI Case
-> one ROI View
-> one deterministic recommendation
-> one AI explanation
-> one human review action
-> one ledger history
-> minimal health and observability
```

No additional platform capability can compensate for failure in this flow.

## Demo Scenario

| Field | Value |
| --- | --- |
| Case ID | `DRC-AOA-001` |
| Demo theme | AI Onboarding Assistant Recovery |
| Organization | PilotCo SaaS |
| Review period | 2026-06 |
| First source mode | Manual/static or file/import evidence |
| Recommendation | AI model downgrade/change with fallback |
| Expected reviewer | `ADMIN` demo role representing CTO/VP Engineering authority |
| Required action | Approve, reject or defer |
| Result validation | Representable, not required as real savings proof |

## Demo Walkthrough

The future demo should be explainable in this sequence:

1. A user authenticates with a role that can review the case.
2. The user opens `DRC-AOA-001`.
3. Evidence summaries are visible with source domains, freshness and sensitivity-safe references.
4. ROI View shows current monthly cost EUR2,340.
5. ROI View shows projected monthly cost EUR720.
6. ROI View shows estimated monthly recovery EUR1,620.
7. ROI View shows annualized recovery EUR19,440.
8. Exactly one recommendation is visible.
9. AI explanation explains the recommendation and cites evidence IDs.
10. Assumptions and risk are visible.
11. The reviewer approves, rejects or defers.
12. A ledger entry records the action with actor role, reason and reviewed snapshot.
13. `/health` and `/ready` are conceptually available.
14. Structured logs and minimal metrics can show the flow without sensitive data.

## Acceptance Checklist

| ID | Check | Required Result |
| --- | --- | --- |
| `DEMO-01` | Authenticated access | Anonymous review action is impossible. |
| `DEMO-02` | Case identity | `DRC-AOA-001` is the only demo case. |
| `DEMO-03` | Evidence source | Manual/static or file/import source is enough. |
| `DEMO-04` | Evidence traceability | Recommendation and explanation cite evidence IDs. |
| `DEMO-05` | ROI calculation | EUR2,340, EUR720, EUR1,620 and EUR19,440 are consistent. |
| `DEMO-06` | Recommendation count | Exactly one recommendation is shown. |
| `DEMO-07` | AI boundary | AI explains only; it does not decide or calculate. |
| `DEMO-08` | Human authority | Approve/reject/defer requires authorized role. |
| `DEMO-09` | Ledger | Review action creates append-only history. |
| `DEMO-10` | Result value | Realized saving is not claimed before validation. |
| `DEMO-11` | Sensitive data | Raw prompts, completions, secrets and Restricted evidence are hidden. |
| `DEMO-12` | Observability | Logs, metrics, `/health` and `/ready` support basic review. |

All checks must pass for Phase 1 implementation closure.

## Negative Demo Checks

The demo fails if it requires:

- more than one Decision ROI Case,
- more than one recommendation,
- ranking,
- learning systems,
- autonomous execution,
- live connector automation,
- provider write operations,
- public APIs,
- SDKs,
- Kafka,
- Kubernetes,
- Terraform,
- Graph DB/vector/search infrastructure,
- full SSO,
- full observability platform,
- raw prompts or completions,
- realized savings without validation.

## Evidence Of Completion

When implementation work eventually starts, the integrated demo record should capture:

| Evidence | Required Detail |
| --- | --- |
| Date | Exact date of demo run. |
| Contributor or agent | Who performed the work or validation. |
| Source documents used | Stage files and canonical docs referenced. |
| Implementation artifact | Files, modules or commits actually touched. |
| Test or check performed | Manual or automated checks actually executed. |
| Result | Pass, fail or partial. |
| Defect | Any unresolved issue. |
| Scope decision | Any change that requires Decision Log update. |
| R&D note | Actual time/uncertainty/result if known. |

Do not invent hours, tests or implementation results.

## Integrated Stop Conditions

Stop and return to CTO Agent if:

- the demo cannot be explained without external narration;
- acceptance requires post-MVP infrastructure;
- AI output is needed to make the business decision true;
- connector automation becomes necessary to prove value;
- the ledger cannot preserve the reviewed snapshot;
- sensitive evidence must be exposed to pass;
- no authorized human review action can be recorded.

## Pass/Fail Recommendation

Use this decision structure:

| Result | Meaning |
| --- | --- |
| Pass | All demo checks pass with the reduced MVP scope. |
| Conditional Pass | Core flow works, but a non-critical defect is documented and does not change acceptance truth. |
| Fail | Any core flow check fails or scope expands beyond Phase 1 boundaries. |
| Defer | External decision or missing evidence prevents responsible closure. |

Conditional Pass must not hide scope creep.

## Handoff To Stage 12

Stage 12 may close the Phase 1 stage dossier only after confirming that all stage contracts are coherent and implementation can start from them without inventing scope.

If software has not yet been implemented, Stage 12 must say so clearly.
