# End-to-End Local Business Value Demo

Status: **CERTIFIED LOCAL APPLICATION HARNESS / NOT A PILOT RUNTIME E2E**

## Purpose

This operational guide executes the Sprint 3.5 local vertical slice for the frozen `DRC-AOA-001` AI Onboarding Assistant case. It demonstrates an auditable outcome from normalized NDJSON Evidence through a validated Business Value projection.

The guide is not an architecture contract. It does not authorize new product scope, persistence, REST behavior, or roadmap changes.

## Certified workflow

```text
deterministic NDJSON dataset
    -> Evidence import
    -> Decision creation
    -> deterministic Recommendation and estimated savings
    -> deterministic explanation provider
    -> approval
    -> implementation marker
    -> result validation
    -> non-persisted Business Value projection
```

The executable harness is `imperator.api.evidence.EndToEndBusinessValueDemoTest`. It invokes the existing Application use cases through their established boundaries. The Business Value projection reads authoritative repositories but is never stored.

## Prerequisites

- Java 21
- Maven Wrapper 3.9.16
- The repository dependencies already available to Maven

PostgreSQL is not required for this local harness. The complete Sprint 3.7
PostgreSQL 18.2 integration-profile run subsequently certified the Sprint 3.4
governance runtime and discharged the D084 obligation.

## Run

From the repository root on Windows:

```powershell
.\mvnw.cmd -Dtest=EndToEndBusinessValueDemoTest test
```

To execute the complete default verification gate:

```powershell
.\mvnw.cmd clean verify
```

Both commands must run with Java 21. The test uses fixed clocks, identifiers, actors, inputs, and explanation output.

## Deterministic input

The dataset is:

`src/test/resources/evidence/drc-aoa-001-business-value-demo.jsonl`

It contains exactly 30 independent NDJSON records:

- 23 canonical support Evidence records, including quality Evidence.
- Four explicit ROI assumption Evidence records.
- One policy-provenance Evidence record.
- One implementation Evidence record.
- One post-action validation Evidence record.

The importer must report 30 accepted records and zero rejected records. Raw payloads remain `not_stored`.

## Deterministic policy output

The canonical Recommendation is `MODEL_CHANGE` under policy version `DRC-AOA-001-v1`.

| Value | Authoritative output |
|---|---:|
| Current monthly cost | EUR 2,340.00 |
| Projected monthly cost | EUR 720.00 |
| Estimated monthly recovery | EUR 1,620.00 |
| Estimated annualized savings | EUR 19,440.00 |
| Confidence | 92% |
| Risk | LOW |

The Application harness uses a deterministic, replaceable explanation provider. The explanation is advisory and cannot change the action, reason, savings, confidence, risk, or policy version.

## Authoritative validated outcome

Result validation records the following fixed inputs in the immutable Ledger fact:

| Value | Validated amount |
|---|---:|
| Annualized baseline cost | EUR 28,080.00 |
| Annualized post-action cost | EUR 9,000.00 |
| Actual transition cost | EUR 120.00 |
| Realized annualized savings | EUR 18,960.00 |
| Variance from estimate | EUR -480.00 |

The Business Value projection copies realized savings and variance from the authoritative `result_validated` Ledger entry. It performs no secondary realized-savings calculation.

## Traceability output

The projection exposes:

- All 30 Evidence identifiers in deterministic order.
- Decision and Recommendation identifiers.
- Approval, implementation, and validation Ledger entry identifiers.
- The complete ordered Ledger history, including actors, roles, timestamps, predecessor links, and Evidence snapshots.
- Correlation key and case identifier.
- Policy version and the four assumption identifiers.
- Decision state, Recommendation type, action, deterministic reason, explanation availability, confidence, and risk.
- Estimated savings, realized savings, variance, baseline cost, post-action cost, and transition cost.

No calculation input or governance marker is hidden by the projection.

## Certification assertions

The harness passes only when:

1. The complete NDJSON dataset imports without rejection.
2. A Decision is created from the originating Evidence.
3. The deterministic policy creates one Recommendation with the frozen estimate.
4. Business Value is unavailable before result validation.
5. Approval, implementation, and validation form one linear Ledger chain.
6. The Decision remains `APPROVED` after implementation and validation facts.
7. Realized savings originate exclusively from the validation Ledger fact.
8. Identical review and Ledger command replays create no additional entries.
9. Re-projecting identical authoritative state returns an equal projection.
10. This harness stays inside Application boundaries and does not claim to
    exercise REST, PostgreSQL, JWT, Docker or the browser workspace. The
    separately certified `GET /api/v1/business-value` route is outside this
    harness.

## Explicit exclusions

This harness does not exercise functional REST, PostgreSQL, JWT, RBAC, the
frontend, external connectors, Docker or observability. Those capabilities
have their own certification boundaries. It does not add a Business Value
aggregate or repository, database objects, migrations or external AI vendors.
