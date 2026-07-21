# evidence

## Purpose

Representar la evidencia canonica que alimenta decisiones en IMPERATOR.

## Who Uses This Folder

- Implementation Agent when future evidence-domain classes are authorized.
- Architecture Guardian to verify connector-independent evidence modeling.
- Quality Agent to ensure evidence remains canonical and domain-oriented.

## Contains

- `Evidence` entity.
- Canonical evidence attributes.
- Evidence validation invariants.
- Evidence approval-readiness behavior.

Sprint 2.3.1 status:
- One Java entity only.
- Immutable after creation.
- Equality by `EvidenceId`.
- Raw payloads are not stored in the domain.
- No interfaces.
- No infrastructure dependencies.

## Never Contains

- GitHub-specific models.
- AWS-specific models.
- Jira-specific models.
- OpenAI or Claude payloads.
- Raw connector clients.
- Database mappings.
- API request or response DTOs.
