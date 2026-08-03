# Synchronize Evidence Application Capability

## Purpose

Orchestrate one bounded external Evidence reconciliation through ports.

## Used By

- internal runtime composition;
- connector contract and persistence certification tests; and
- future authorized operational adapters.

## Contains

- the synchronization command and safe result;
- one provider-neutral use case;
- execution serialization; and
- Evidence replay and source-identity conflict handling.

## Never Contains

- provider HTTP or JSON;
- GitHub credentials or endpoint knowledge;
- Decision, Recommendation, ROI or Ledger behavior;
- REST controllers; or
- persistence implementation.
