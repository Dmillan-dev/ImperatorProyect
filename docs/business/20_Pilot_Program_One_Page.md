# IMPERATOR Pilot Program

> Internal commercial draft. Do not present as generally available until Pilot
> Readiness is certified.

## Pilot Objective

Demonstrate that IMPERATOR can turn approved operational evidence into one
explainable business Decision, one deterministic Recommendation, one governed
action and measurable economic value.

```text
Operational events
-> Evidence
-> Decision
-> Recommendation
-> Human action
-> Validated outcome
-> Verifiable Business Value
```

The pilot succeeds when the customer understands the Decision, its evidence,
its economic impact and the required action in less than 15 minutes.

## One Pilot, One Case

| Item | Pilot boundary |
|---|---|
| Decision ROI Case | `DRC-AOA-001` - AI Onboarding Assistant Recovery |
| Business question | Is the current AI operating model costing more than the value it creates? |
| Recommendation family | One deterministic model downgrade/change Recommendation |
| Business owner | Customer business owner for the selected AI workflow |
| Technical owner | Platform Engineering |
| Economic reviewer | Finance or FinOps |
| Approver | CTO or VP Engineering |

IMPERATOR does not attempt to demonstrate multiple cases, recommendation
ranking, autonomous execution or a general analytics platform during this
pilot.

## What The Customer Sees

```text
What happened?
-> Why does it matter?
-> What does IMPERATOR recommend?
-> How much value could be recovered?
-> Who must approve the action?
-> What happened after approval?
-> What value was actually realized?
```

The Decision Review Workspace connects the answer to its Evidence, ROI,
Recommendation, human review and immutable Ledger history. Business Value is
reported as estimated until the customer validates the actual outcome.

## Required Pilot Inputs

- One customer-approved AI/cloud operating Decision.
- Approved read-only evidence or exports from the systems needed by the case.
- Cost baseline and post-action measurement period.
- A named business owner, technical owner, financial reviewer and approver.
- Customer confirmation of quality, risk and acceptable implementation limits.
- No raw prompts, raw conversations, credentials, secrets or unnecessary PII.

The canonical `DRC-AOA-001` dataset is a demonstration template. Customer
outcomes must use customer-approved evidence and assumptions; template values
are never presented as guaranteed savings.

## Five Success Criteria

| Criterion | Pilot objective | Evidence of success |
|---|---|---|
| Traceability | Reconstruct Evidence -> Decision -> Recommendation -> Ledger | Every conclusion links back to approved operational evidence |
| Explainability | Produce one deterministic and auditable Recommendation | The reviewer understands the reason, confidence, risk and assumptions |
| Action | Complete the governed human workflow | Approval, implementation and result validation are attributable and ordered |
| Value | Show estimated ROI and then validated realized value | Finance/FinOps accepts the inputs, calculation and outcome |
| Persistence | Preserve the complete case across runtime recreation | Decision, Evidence, Recommendation, Ledger and Business Value are recovered without duplication |

## Primary Commercial Validation

The pilot must answer one question:

> Can the customer identify an operational Decision whose cost or value can be
> measured credibly in euros and improved through an approval-ready action?

Positive commercial evidence means:

- the CTO or VP Engineering considers the Recommendation reviewable;
- Platform Engineering accepts the technical Evidence chain;
- Finance or FinOps accepts the economic assumptions;
- the customer can identify an owner and a feasible action; and
- the customer asks to evaluate another Decision after the first case.

## Pilot Sequence

1. Select one eligible customer Decision and confirm owners.
2. Collect only approved Evidence and establish the economic baseline.
3. Reconstruct `DRC-AOA-001` and present its deterministic Recommendation.
4. Review ROI, risk, assumptions and Evidence with the customer.
5. Record the authorized human Decision in the Ledger.
6. If the customer acts, record implementation and the agreed validation period.
7. Validate the outcome and present realized Business Value.
8. Confirm traceability, persistence and the next commercial Decision.

## Pilot Boundaries

The pilot does not promise:

- autonomous business decisions or automated execution;
- universal ROI, guaranteed savings or financial advice;
- broad connector coverage, multi-tenancy or platform-scale deployment;
- access to raw customer prompts, conversations or credentials;
- AI authority over Recommendation, approval or Business Value; or
- production availability before security and Pilot Readiness gates pass.

IMPERATOR is sold here as a governed Decision ROI workflow, not as another AI
platform, generic dashboard or observability product.

## Pilot Exit

| Outcome | Meaning |
|---|---|
| `GO` | All five success criteria pass and the customer wants to review another measurable Decision |
| `ITERATE` | The problem is valuable but Evidence, ownership or measurement needs a bounded correction |
| `NO-GO` | The customer cannot identify a measurable Decision or does not trust the evidence-to-value chain |

The commercial pilot is complete only when value is understood, governed and
traceable. Feature count is not a success metric.
