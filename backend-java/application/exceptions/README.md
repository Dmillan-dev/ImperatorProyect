# exceptions

## Purpose

Definir la taxonomia de errores de aplicacion que los adapters podran mapear a
protocolos externos sin contaminar los casos de uso con HTTP, REST, JSON o
frameworks.

## Who Uses This Folder

- Application use cases when a business capability cannot be completed.
- Future REST, CLI or UI adapters when they need to translate failures.
- Architecture Guardian to verify application failures stay protocol-neutral.

## Contains

Taxonomy:

- `ApplicationException`
- `ValidationException`
- `NotFoundException`
- `ConflictException`
- `AuthorizationException`
- `BusinessRuleViolationException`

Concrete exceptions:

- `EvidenceNotFoundException`
- `DecisionNotFoundException`
- `RecommendationNotFoundException`
- `LedgerEntryNotFoundException`
- `InvalidDecisionTransitionException`
- `RecommendationOwnershipViolationException`
- `DuplicateEvidenceException`
- `DecisionAlreadyClosedException`
- `DecisionAlreadyHasRecommendationException`
- `EvidenceTraceabilityViolationException`
- `LedgerAppendRejectedException`

Sprint 2.6.2 status:
- Java exceptions only.
- Stable application error codes.
- No protocol mapping.

## Never Contains

- HTTP status codes.
- `ResponseEntity`.
- `ResponseStatusException`.
- `@ResponseStatus`.
- Spring annotations.
- REST adapter behavior.
- Persistence adapter behavior.
