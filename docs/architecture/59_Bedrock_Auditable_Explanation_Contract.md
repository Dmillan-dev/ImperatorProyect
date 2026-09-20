# 59 - Bedrock Auditable Explanation Contract

Status: **D099 ACCEPTED / IMPLEMENTATION COMPLETE / HOSTED REPOSITORY PASS on `22a9917`**

## Objective

Add one auditable Amazon Bedrock explanation to the existing deterministic
`DRC-AOA-001` Recommendation without giving the model business authority.
This is a portfolio and TFG extension delivered before MVP Release; it does not
reopen the deterministic policy, approval or Ledger contracts.

## Frozen Boundary

The runtime flow is:

```text
accepted Evidence summaries
-> deterministic Recommendation and ROI persisted
-> committed transaction
-> prepared explanation context
-> Bedrock Converse
-> validated untrusted output
-> separate explanation-attempt persistence
-> human review
```

Bedrock may explain only the Recommendation already persisted by D082. It may
not calculate or alter ROI, select an action, change confidence or risk, approve
a Decision, write a Ledger entry or mutate an external system.

## Provider And Configuration

- Use AWS SDK for Java 2.x `bedrock-runtime` and the Converse API.
- Use the SDK default credentials provider chain; never accept access keys as
  application properties.
- Configure enablement, Region and model ID outside Domain and Application.
- Keep the provider disabled by default.
- Apply a bounded request timeout, SDK retry limit, input length, output token
  limit and zero-temperature generation.
- Permit only `bedrock:InvokeModel` for the selected model or inference profile.

No AWS resource, model invocation or billable deployment is authorized by this
contract. A real invocation requires separate operator approval.

## Prepared Context

The provider receives only:

- Recommendation and Decision identifiers;
- case ID and normalized business need;
- deterministic action, reason, annualized saving, confidence and risk;
- accepted Evidence identifiers and short normalized facts;
- explicit assumption identifiers and the deterministic policy version.

Raw provider payloads, secrets, credentials, JWTs, personal data, raw prompts,
raw completions and Restricted Evidence are forbidden. Confidential Evidence
content is withheld from the model; only its identifier and withheld status may
be represented. Evidence content is delimited as untrusted data, never as
instructions.

## Output Contract

The model must return bounded JSON containing:

- summary;
- rationale;
- assumptions;
- limitations and uncertainty;
- evidence references; and
- required human review.

The adapter rejects malformed output, unsupported references, missing supplied
assumptions, control characters and oversized text. The UI treats all generated
text as untrusted plain text and labels it as AI-generated.

## Persistence And Audit

Explanation attempts are technical records separate from Recommendations and
the append-only business Ledger. Each attempt records:

- explanation and Recommendation identifiers;
- status (`GENERATED`, `UNAVAILABLE`, `FAILED` or `REJECTED`);
- provider, model and prompt version;
- request/completion timestamps;
- bounded token and latency metrics when available;
- a stable failure code without provider payloads; and
- the Evidence and assumption identifiers supplied to the provider.

The prompt and raw response are never stored. The current API exposes only the
latest attempt. The initial generation is idempotent per Recommendation,
provider, model and prompt version. Explicit regeneration is a later bounded
`ADMIN` command that creates another attempt; reads never invoke Bedrock.

Retain explanation attempts for 90 days in pilot environments unless an
approved legal or customer requirement sets a shorter period. Retention does
not alter Recommendation or Ledger history.

## Failure And Security Rules

- Provider failure, timeout, invalid output or audit-persistence failure cannot
  roll back or invalidate the deterministic Recommendation.
- Logs and metrics contain only bounded status, latency, model alias and stable
  identifiers; never prompt, completion or Evidence content.
- Existing D087 authentication, D088 RBAC, Evidence redaction and D083 human
  authority remain binding.
- `GET /api/v1/recommendations/{id}` remains a read and never calls Bedrock.

## Delivery Sequence

1. Java provider adapter, prepared-context validation and offline tests.
2. Separate PostgreSQL attempt persistence and read projection.
3. Explicit Recommendation API response and Decision Review Workspace state.
4. Synthetic template-versus-Bedrock evaluation harness.
5. Real Bedrock smoke evidence only after credentials, cost and operator
   approval.
6. AWS infrastructure and deployment only under a separate contract after D095
   external Keycloak HTTPS conformance.

## Excluded

Agents, RAG, embeddings, vector databases, Python services, model training,
autonomous execution, public exposure, customer data, Terraform deployment,
ECS/RDS creation and GitHub deployment credentials are excluded from this
implementation gate.
