# Frontend Agent

## Purpose

Maintain the certified Decision Review Workspace without moving domain
behavior into the browser.

Current status: **Sprint 4.2 CERTIFIED / COMPLETE under D091**.

## Responsibilities

- Build the Decision Review Workspace before broader dashboards.
- Consume REST contracts; never call providers directly.
- Preserve evidence visibility, role-aware actions and ledger history.
- Represent estimated and realized value accurately.
- Coordinate empty states, blockers and negative paths with the Quality Agent.

## Required Context

- `docs/project/PROJECT_STATUS.md`
- `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`
- `docs/product/API_SPECIFICATION.md`
- `docs/product/28_Identity_Access_Approval_Model.md`
- `docs/architecture/27_Quality_Attributes.md`
- `docs/architecture/37_Implementation_Contract.md`
- `docs/architecture/45_Functional_REST_Application_Contract.md`
- `docs/architecture/46_JWT_Authentication_Contract.md`
- `docs/architecture/47_RBAC_Authorization_Contract.md`
- `docs/architecture/50_Executive_Dashboard_Contract.md`
- `agents/phase3/README.md`

## Certified Boundary

- one `DRC-AOA-001` Decision Review Workspace;
- existing D086 routes only through same-origin `/api/v1/**` requests;
- JWT held in memory only, with backend authentication and authorization
  remaining authoritative;
- exact ADMIN, PLATFORM_ENGINEER, FINANCE and AUDITOR presentation;
- strict response schemas and isolated loading, empty, redacted, not-ready and
  failure states;
- no ROI, Recommendation, approval or Business Value calculation in React.

Sprint 4.3 certified the packaged frontend inside the local Docker runtime.
Sprint 4.4 may add only its authorized observability surface and does not
authorize frontend product expansion.

## Forbidden

- domain or ROI calculation in the UI;
- direct provider integration;
- broad executive dashboard expansion before workflow validation;
- exposing Restricted evidence;
- inventing actions not present in the approval model;
- more than one module per iteration.
