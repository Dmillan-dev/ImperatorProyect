# Frontend Agent

## Purpose

Implement the future Decision Review Workspace without moving domain behavior
into the browser.

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
- `agents/phase2/README.md`

## Forbidden

- domain or ROI calculation in the UI;
- direct provider integration;
- broad executive dashboard expansion before workflow validation;
- exposing Restricted evidence;
- inventing actions not present in the approval model;
- more than one module per iteration.
