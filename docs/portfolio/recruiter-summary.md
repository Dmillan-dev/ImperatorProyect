# IMPERATOR Recruiter Summary

## Professional Positioning

Early-career Cloud Security / DevSecOps Engineer with a strong software
engineering foundation and experience designing a security-oriented,
auditable enterprise system.

## 30-Second Pitch

IMPERATOR is a portfolio MVP that connects operational evidence to a business
decision, deterministic recommendation, human approval, immutable history and
validated economic outcome. I built it as a Java 21/Spring Boot modular
monolith with hexagonal boundaries, PostgreSQL/Flyway, JWT/RBAC, read-only AWS
and GitHub integrations, a Next.js workspace and a hardened Docker Compose
runtime. The project is deliberately pre-pilot and clearly separates
implemented, planned and future capabilities.

## 60-Second Pitch

Engineering teams often have the technical records of a change but cannot
reconstruct why a costly operational decision was made or whether it delivered
the expected value. IMPERATOR models that lifecycle from normalized Evidence
through Decision, Recommendation, ROI, human review and an append-only Ledger
to result validation.

I used the project to demonstrate production-minded engineering practices:
framework-independent Domain and Application layers, minimal ports, adapter-
owned JDBC persistence, deterministic policy before AI, RS256 JWT validation,
explicit RBAC, sensitive evidence redaction, read-only cloud connectors,
database integration testing and hardened containers. I also keep fail-closed
delivery gates: the certified local runtime uses pinned images, reproducible
PostgreSQL supply-chain remediation, SBOM/provenance evidence and blocking
Trivy checks, while operational external identity remains explicitly deferred.

## CV Bullet Points

- Designed and implemented a Java 21/Spring Boot modular monolith using
  Hexagonal Architecture, Domain boundaries and explicit inbound/outbound ports.
- Built a deterministic Evidence-to-Decision workflow with auditable human
  governance, append-only Ledger history and estimated-versus-realized value.
- Secured 15 D086 route/method contracts plus the D093 R16 composition route
  with RS256 JWT validation, four-role RBAC, server-side authorization and
  sensitivity-based evidence redaction.
- Implemented bounded read-only GitHub REST and AWS SDK evidence adapters with
  timeout, retry, rate-limit, account and Region controls.
- Established PostgreSQL/Flyway integration gates, 151 default tests, 34
  PostgreSQL integration tests, 41 frontend tests, pinned CI actions and a
  certified hardened Docker Compose runtime.

## Demonstrated Skills

| Area | Evidence |
|---|---|
| Software architecture | Hexagonal boundaries, modular monolith, Domain/Application separation, light CQRS |
| Secure API engineering | OAuth2 Resource Server, JWT RS256, RBAC, typed errors, correlation IDs |
| Cloud security | Read-only AWS permissions, expected-account validation, bounded Region/workload scope |
| DevSecOps | Reproducible toolchains, SHA-pinned actions/images, release-blocking vulnerability gate |
| Data engineering | Provider-neutral evidence model, PostgreSQL constraints, Flyway and transaction handling |
| Auditability | Append-only Ledger, immutable snapshots and actor/time/reason traceability |
| Frontend engineering | Next.js/React/TypeScript, schema validation, role-aware presentation and Playwright |
| Testing | Unit, contract, security, repository, PostgreSQL integration and UI acceptance testing |
| Technical governance | Decision Log, frozen contracts, scoped agents and explicit GO/NO-GO gates |

## What This Project Proves

- I can keep business rules independent from frameworks and infrastructure.
- I understand that authentication, route authorization and business authority
  are different controls.
- I can integrate cloud/provider APIs without giving adapters business
  authority or write permissions.
- I use real database tests and constraints instead of relying only on mocks.
- I can explain trade-offs and defer infrastructure that does not yet add MVP
  value.
- I can remediate an upstream image finding through a pinned, reproducible and
  independently certified supply-chain process instead of weakening a gate.

It does **not** prove production Kubernetes, Terraform, multi-tenancy, an AWS
deployment, operational external Keycloak conformance, a live AI service, a
production security operations programme or customer adoption.

## Possible Interview Questions

1. Why did you choose a modular monolith instead of microservices?
2. How do the ports prevent PostgreSQL or AWS from shaping the Domain?
3. Why is Business Value a projection rather than an entity?
4. How do you guarantee that Ledger history is append-only?
5. What is the difference between RBAC and Application governance here?
6. Why can the frontend decode a JWT but never use decoded claims as authority?
7. How do GitHub and AWS data become provider-neutral Evidence?
8. What happens if a connector times out or returns partial data?
9. Why is the AI provider optional and outside deterministic persistence?
10. How did D094 remediate the upstream PostgreSQL image finding without
    weakening the vulnerability policy?
11. Which controls are still required before connecting a real customer?
12. What would justify introducing Kubernetes, Kafka or multi-tenancy later?

## Technical Talking Points

### Architecture

The Domain is plain Java. Application use cases orchestrate ports. REST, JDBC,
GitHub and AWS are adapters. The composition root is the only place that knows
the concrete implementations.

### Security

JWT validation checks signature algorithm, `kid`, issuer, audience, expiry,
canonical subject and one exact role. Route RBAC is followed by Application
governance, while evidence sensitivity is filtered before responses reach the
browser.

### Data Integrity

Use cases define transaction boundaries. PostgreSQL constraints protect
identity, ownership and ordering. Ledger corrections append a new fact instead
of rewriting history.

### Cloud

The AWS adapter is a read-only evidence collector, not an infrastructure
controller. It verifies account identity and limits the Region, workload and
SDK surface. No AWS deployment is claimed.

### DevSecOps

Build tools and actions are pinned, compiler warnings fail the build and real
PostgreSQL behavior has a separate integration gate. Trivy blocks fixable
High/Critical findings. D094 replaced one vulnerable upstream `gosu` binary
through pinned inputs and certified the final PostgreSQL image with two-build
reproducibility, SBOM, provenance and functional evidence.

## GitHub Profile Project Block

```markdown
### IMPERATOR

Enterprise Decision Intelligence Platform

`Java 21` `Spring Boot` `PostgreSQL` `AWS SDK` `GitHub REST`
`JWT/RBAC` `Docker` `Next.js` `DevSecOps`

> A security-oriented decision intelligence platform designed to make
> operational decisions traceable, explainable and measurable.

[Architecture](https://github.com/Dmillan-dev/ImperatorProyect#architecture) ·
[Security](https://github.com/Dmillan-dev/ImperatorProyect#security-architecture) ·
[DevSecOps](https://github.com/Dmillan-dev/ImperatorProyect#devsecops-evidence) ·
[Synthetic demo](https://github.com/Dmillan-dev/ImperatorProyect/tree/main/output/commercial/linkedin-discovery-kit)
```

Do not add Python, FastAPI, Kubernetes, Terraform, Kafka, Redis, production AWS
deployment, customer savings or production-ready claims to the profile until
repository evidence changes their status.
