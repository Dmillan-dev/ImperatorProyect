# exceptions

## Purpose

Define the application error taxonomy that adapters may map to external
protocols without contaminating use cases with HTTP, REST, JSON or frameworks.

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
- `DecisionCreationConflictException`
- `RecommendationCreationConflictException`
- `RecommendationNotReadyException`
- `DecisionAlreadyClosedException`
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
