# TFG-A Academic Scope

Document status: **TFG-A FINAL REVIEW CANDIDATE**

## Problem

Organizations accumulate operational facts but often cannot reconstruct why a
decision was proposed, which Evidence supported it, who authorized it or
whether its expected economic value was later observed.

## Proposed Solution

IMPERATOR is a modular decision-governance platform that normalizes Evidence,
creates one deterministic Decision and Recommendation, estimates ROI, supports
human review, records append-only governance events and projects Business
Value. An optional generative provider explains an already persisted
Recommendation but has no decision, approval or execution authority.

## General Objective

Design, implement and objectively evaluate a secure cloud-native application
that is reproducible locally and prepared for a controlled AWS deployment,
while preserving deterministic business behavior, human authority and
traceable technical evidence.

## Specific Objectives

1. `TFG-O-01`: Apply hexagonal boundaries to keep business policy independent of Spring,
   PostgreSQL, AWS and HTTP.
2. `TFG-O-02`: Expose the value loop through a validated REST API and Next.js workspace.
3. `TFG-O-03`: Persist the decision graph and append-only Ledger in PostgreSQL.
4. `TFG-O-04`: Enforce fail-closed JWT authentication, RBAC and Evidence redaction.
5. `TFG-O-05`: Package, observe and verify the runtime through hardened containers.
6. `TFG-O-06`: Demonstrate automated testing and software supply-chain controls.
7. `TFG-O-07`: Integrate a bounded Bedrock explanation without delegating business
   authority to the model.
8. `TFG-O-08`: Design a reproducible AWS target and justify its security, availability,
   operations and cost trade-offs.
9. `TFG-O-09`: Distinguish all offline evidence from live AWS evidence.

## Canonical Use Case

Only `DRC-AOA-001` is included. It evaluates a synthetic cost-recovery decision
for an onboarding assistant. Its fixed flow is:

```text
30 synthetic Evidence records
-> one Decision
-> one deterministic MODEL_CHANGE Recommendation and estimated ROI
-> optional bounded explanation
-> assigned human review
-> implementation and result-validation Ledger entries
-> projected realized Business Value
```

## Included

- Java 21 and Spring Boot modular monolith;
- Next.js Decision Review Workspace;
- PostgreSQL and Flyway migrations;
- REST, JWT resource-server validation and four-role RBAC;
- GitHub and AWS read-only Evidence adapters;
- Docker Compose local runtime and observability;
- optional Bedrock explanation adapter, disabled by default;
- Terraform and GitHub workflows for the AWS target, validated offline;
- CI, CodeQL, dependency, secret, image and IaC controls; and
- synthetic data, academic evidence and defense material.

## Explicitly Excluded

- real customer, personal or production data;
- multi-tenancy, organization administration or tenant claims;
- a second business case or generic workflow engine;
- autonomous actions, agents, RAG, embeddings or vector databases;
- model training or AI ownership of Recommendation, ROI or approval;
- microservice decomposition, EKS, Kafka, Redis or service mesh;
- services added solely to list AWS products;
- production SLA, multi-region recovery or claimed full high availability;
- commercial login, token persistence or an IdP packaged by IMPERATOR;
- Prometheus/Grafana runtime services; and
- any live AWS action during TFG-A.

## Identity Status

D087, D088 and D095 remain unchanged. No external Keycloak endpoint is
available. D101 scheduling is `OPERATOR-DEFERRED`; its capability evidence
status is `BLOCKED_EXTERNAL`. It remains mandatory before Pilot Readiness,
non-loopback customer exposure or MVP Release. Local authentication and
authorization evidence must never be misrepresented as external IdP
conformance.

## Success Statement

TFG-A succeeds when the implemented local product, target AWS architecture,
competencies and evidence can be understood and audited without changing code.
It does not require or imply an AWS deployment.
