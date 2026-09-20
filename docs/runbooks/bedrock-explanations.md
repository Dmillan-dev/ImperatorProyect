# Bedrock Explanation Runbook

Status: **IMPLEMENTED OFFLINE / DISABLED BY DEFAULT / LIVE SMOKE PENDING**

## Runtime Configuration

The runtime creates the Bedrock adapter only when all required values are
provided and enablement is explicit:

```text
IMPERATOR_BEDROCK_ENABLED=true
IMPERATOR_BEDROCK_REGION=eu-west-1
IMPERATOR_BEDROCK_MODEL_ID=<approved model or inference-profile id>
IMPERATOR_BEDROCK_MAX_OUTPUT_TOKENS=700
IMPERATOR_BEDROCK_TEMPERATURE=0.0
```

Do not configure AWS access-key or secret-key application properties. The Java
adapter uses the AWS SDK default credentials provider chain. ECS must use a task
role. A local operator may use an existing short-lived AWS profile or SSO
session without copying credentials into the repository or Compose secrets.

The task role is limited to `bedrock:InvokeModel` on the selected model or
inference-profile ARN. Model access, Region support and account policy must be
verified immediately before the first live run.

## Safe Demonstration

1. Run Java and frontend tests with Bedrock disabled.
2. Start the existing PostgreSQL runtime and apply Flyway migration V3.
3. Compose the synthetic `DRC-AOA-001` case through the existing authenticated
   API. The deterministic Recommendation commits before provider invocation.
4. With no Bedrock authorization, verify the Recommendation response contains
   an `UNAVAILABLE` explanation attempt and remains usable.
5. After explicit live-call authorization, enable Bedrock with a cost-capped
   role and synthetic data only. Compose a fresh Recommendation identifier.
6. Read `GET /api/v1/recommendations/{id}` repeatedly and verify the explanation
   ID is stable and no additional model invocation occurs.
7. Review the Decision manually. Bedrock must not create a Ledger entry.

## Failure Interpretation

- `GENERATED`: validated text is available and labeled non-authoritative.
- `UNAVAILABLE`: the configured provider returned no explanation.
- `FAILED`: timeout, SDK or provider failure; the Recommendation is intact.
- `REJECTED`: input classification or model-output validation failed.

Stable failure codes may be exposed. Prompts, raw completions, Evidence content,
credentials and provider exception details must not be logged or persisted.

## Retention And Removal

Pilot explanation attempts have a 90-day target retention. The application role
has no update/delete permission. A separately authorized owner operation may
delete expired explanation child rows and parents during a maintenance window;
it must never delete Recommendations, Evidence or Ledger history. No automated
retention job is enabled in the MVP.

Disable the adapter by setting `IMPERATOR_BEDROCK_ENABLED=false`. This stops new
calls and keeps existing audited attempts readable. Removing the feature does
not require rewriting the business Ledger.

## External Evidence Still Required

- D095 Keycloak issuer/JWKS conformance over trusted HTTPS.
- An explicitly approved Bedrock synthetic smoke call.
- Captured model ID, Region, IAM policy, usage, latency and contemporaneous
  public pricing.
- Hosted Java CI, CodeQL and supply-chain gates for the final commit.
