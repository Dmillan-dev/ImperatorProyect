# IMPERATOR Portfolio Evidence

## Purpose

Provide a short, evidence-backed entry point for recruiters, engineering
managers and security reviewers without duplicating the project's canonical
architecture and product contracts.

## Who Uses This Folder

- recruiters evaluating the project in a few minutes;
- engineering managers assessing design and delivery maturity;
- Cloud Security, DevSecOps and Platform engineers validating technical claims;
- the project owner when preparing CV, GitHub and interview material.

## Contains

- [Technical Evidence](technical-evidence.md): implementation inventory,
  diagrams, security controls, DevSecOps state and known gaps;
- [Recruiter Summary](recruiter-summary.md): concise pitches, CV bullets and
  technical interview talking points.

## Professional Documentation Map

| Review area | Authoritative source |
|---|---|
| Architecture | [Architecture Thesis](../architecture/18_Architecture_Thesis.md) and [Technical Architecture Context](../architecture/21_Technical_Architecture_Context.md) |
| Security and threat model | [Security, Data Governance and Threat Model](../architecture/26_Security_Data_Governance_Threat_Model.md) |
| DevSecOps and implementation rules | [Coding Principles](../architecture/35_Coding_Principles.md) and [Implementation Contract](../architecture/37_Implementation_Contract.md) |
| Engineering decisions | [Decision Log](../decisions/14_Decision_Log.md) |
| Data model | [Database Model](../architecture/DATABASE_MODEL.md) and [Persistence Schema Contract](../architecture/40_Persistence_Schema_Contract.md) |
| Deployment boundary | [Docker Production Runtime Contract](../architecture/51_Docker_Production_Runtime_Contract.md) |
| Observability | [Minimum Observability Runtime Contract](../architecture/56_Observability_Runtime_Contract.md) |
| Roadmap and current gate | [Phase and Sprint Map](../project/PHASE_AND_SPRINT_MAP.md) and [Pilot Readiness](../product/30_Pilot_Readiness_Preparation_Checklist.md) |

This map intentionally reuses maintained documents instead of creating a
second set of `architecture.md`, `security.md` or `roadmap.md` files that could
drift away from the project's frozen contracts.

## Never Contains

- product or runtime code;
- secrets, tokens or customer data;
- new architecture decisions;
- claims not supported by repository evidence;
- duplicated canonical contracts.

Canonical project state remains under `docs/project/`. Canonical architecture,
security, domain and decisions remain under `docs/architecture/`,
`docs/product/` and `docs/decisions/`.
