# IMPERATOR Pilot Preparation

Status: **PREPARATION ONLY / PILOT NOT AUTHORIZED**

## Rule Zero

### Why This Folder Exists

It turns the certified IMPERATOR product contracts into a controlled,
reviewable commercial-pilot operating package while runtime certification is
blocked.

### Who Uses It

- IMPERATOR Pilot Lead;
- Security and Operations owner;
- customer Executive Sponsor, Platform, Finance and Security contacts;
- Architecture, Security, Quality, Product and Context Guardians; and
- future legal/privacy advisers reviewing the pilot agreement.

### What It Contains

- environment and access preparation;
- Evidence-source ownership and collection register;
- execution and incident runbook;
- data, security, backup and legal-readiness checklists; and
- acceptance and R&D evidence-pack controls.

### What It Never Contains

- secrets, passwords, tokens, private keys or working credentials;
- customer data, raw provider payloads or personal data;
- a Keycloak realm export with users or credentials;
- executable runtime configuration, Docker services or application code;
- invented test results, hours, commercial signatures or legal conclusions;
- authorization to use real customer data; or
- a replacement for D087-D093 or Sprint 4.5 certification.

## Authority And Precedence

1. `docs/project/PROJECT_STATUS.md` controls the active gate.
2. D087-D093 control identity, RBAC, connectors, workspace, runtime and case
   composition.
3. `docs/business/20_Pilot_Program_One_Page.md` is the commercial charter.
4. `docs/product/30_Pilot_Readiness_Preparation_Checklist.md` controls the
   readiness view.
5. This folder prepares execution without overriding any item above.

## Package Map

| Document | Purpose |
|---|---|
| `01_Controlled_Environment_and_Evidence_Register.md` | Freeze the proposed single-customer environment, access worksheet and ownership of all 30 Evidence facts |
| `02_Pilot_Execution_Runbook.md` | Define preflight, rehearsal, future customer-pilot sequence, incidents and closeout |
| `03_Security_Data_and_Operations_Readiness.md` | Prepare security questionnaire, retention, deletion, backup/restore, DNS/TLS, secrets, DPA and NDA inputs |
| `04_Pilot_Evidence_Pack_and_Acceptance.md` | Define the evidence manifest, acceptance report and WP11 R&D traceability |

## Two Different Milestones

| Milestone | Data | Current authority |
|---|---|---|
| Commercial rehearsal | Canonical synthetic 30-Evidence dataset and fixed D082 values | Completed locally for Sprint 4.3 with D093/R16, local JWT/RBAC and persistence/recreation; operational external IdP conformance remains separate under D095 |
| Customer pilot | Customer-approved normalized facts and customer-specific values | Not authorized; requires explicit pilot/data/policy approval |

Passing the rehearsal proves runtime and workflow integrity. It does not prove
customer value, customer data compliance or general product readiness.

## Current Gate

```text
Sprint 4.3: CERTIFIED / COMPLETE
Sprint 4.3.1: COMPLETE
Sprint 4.4: NEXT
External Pilot Identity Conformance: DEFERRED / REQUIRED BEFORE 4.5
Sprint 4.5: NOT OPEN
Customer data: FORBIDDEN
Public exposure: FORBIDDEN
```
