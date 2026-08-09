import type {
  BusinessValue,
  DecisionDetail,
  EvidenceSummary,
  LedgerEntry,
  Recommendation,
  Roi,
  TimelineItem,
} from "@/services/api/schemas";

export const ids = {
  decision: "11111111-1111-4111-8111-111111111111",
  recommendation: "22222222-2222-4222-8222-222222222222",
  evidence: "33333333-3333-4333-8333-333333333333",
  ledger: "44444444-4444-4444-8444-444444444444",
  actor: "55555555-5555-4555-8555-555555555555",
  owner: "66666666-6666-4666-8666-666666666666",
  approver: "77777777-7777-4777-8777-777777777777",
};

export const decision: DecisionDetail = {
  decisionId: ids.decision,
  caseId: "DRC-AOA-001",
  title: "Recover AI onboarding assistant spend",
  businessNeed:
    "Reduce recurring model spend while preserving onboarding quality.",
  status: "APPROVED",
  ownerId: ids.owner,
  requiredApproverId: ids.approver,
  recommendationId: ids.recommendation,
  createdAt: "2026-08-01T10:00:00Z",
  updatedAt: "2026-08-02T10:00:00Z",
  originatingEvidenceId: ids.evidence,
  evidenceIds: [ids.evidence],
  reviewedBy: ids.actor,
  reviewedAt: "2026-08-02T10:00:00Z",
  reviewReason: "The evidence supports a controlled model change.",
};

export const decisionSummary = {
  decisionId: decision.decisionId,
  caseId: decision.caseId,
  title: decision.title,
  businessNeed: decision.businessNeed,
  status: decision.status,
  ownerId: decision.ownerId,
  requiredApproverId: decision.requiredApproverId,
  recommendationId: decision.recommendationId,
  createdAt: decision.createdAt,
  updatedAt: decision.updatedAt,
};

export const timeline: TimelineItem = {
  type: "EVIDENCE",
  referenceId: ids.evidence,
  occurredAt: "2026-08-01T10:00:00Z",
  summary: "Monthly AI consumption was observed",
  source: "AWS",
  actor: null,
  confidenceLabel: "HIGH",
  confidencePercentage: 92,
  evidenceIds: [ids.evidence],
};

export const evidence: EvidenceSummary = {
  evidenceId: ids.evidence,
  timestamp: "2026-08-01T10:00:00Z",
  source: "AWS",
  sourceType: "CLOUD_COST",
  sourceObjectRef: "ce:2026-07",
  entity: "onboarding-assistant-prod",
  eventType: "monthly_cost_observed",
  severity: "MEDIUM",
  actor: "system",
  evidenceType: "cloud_cost",
  observedFact: "Monthly spend was EUR 2,340.00",
  businessMeaning: "The current model creates recoverable recurring spend.",
  correlationKey: "DRC-AOA-001",
  sensitivity: "CONFIDENTIAL",
  confidence: "HIGH",
  reviewStatus: "ACCEPTED",
};

const eur = (amount: string) => ({ amount, currency: "EUR" });

export const roi: Roi = {
  decisionId: ids.decision,
  recommendationId: ids.recommendation,
  currentMonthlyCost: eur("2340.00"),
  projectedMonthlyCost: eur("720.00"),
  transitionCost: eur("0.00"),
  estimatedMonthlyRecovery: eur("1620.00"),
  estimatedAnnualizedRecovery: eur("19440.00"),
  confidence: 92,
  risk: "LOW",
  policyVersion: "DRC-AOA-001-v1",
  assumptionEvidenceIds: [ids.evidence],
};

export const recommendation: Recommendation = {
  recommendationId: ids.recommendation,
  decisionId: ids.decision,
  type: "MODEL_CHANGE",
  suggestedAction:
    "Move onboarding explanations to the approved lower-cost model",
  deterministicReason: "Measured usage and quality satisfy DRC-AOA-001-v1.",
  estimatedSavings: eur("19440.00"),
  confidence: 92,
  risk: "LOW",
  ownerId: ids.owner,
  requiredApproverId: ids.approver,
  createdAt: "2026-08-01T11:00:00Z",
  evidenceIds: [ids.evidence],
};

export const ledger: LedgerEntry = {
  ledgerEntryId: ids.ledger,
  decisionId: ids.decision,
  recommendationId: ids.recommendation,
  actorId: ids.actor,
  actorRole: "ADMIN",
  occurredAt: "2026-08-02T10:00:00Z",
  entryType: "DECISION_APPROVED",
  changeSummary: "Decision approved",
  reason: "The evidence supports a controlled model change.",
  evidenceIds: [ids.evidence],
  estimatedSavings: eur("19440.00"),
  realizedSavings: null,
  confidence: 92,
  risk: "LOW",
  previousEntryId: null,
  metadata: {},
};

export const businessValue: BusinessValue = {
  caseId: "DRC-AOA-001",
  correlationKey: "DRC-AOA-001",
  decisionId: ids.decision,
  decisionTitle: decision.title,
  businessNeed: decision.businessNeed,
  decisionStatus: "APPROVED",
  decisionCreatedAt: decision.createdAt,
  recommendationId: ids.recommendation,
  recommendationType: "MODEL_CHANGE",
  recommendedAction: recommendation.suggestedAction,
  deterministicReason: recommendation.deterministicReason,
  explanation: null,
  recommendationCreatedAt: recommendation.createdAt,
  estimatedSavings: eur("19440.00"),
  realizedSavings: eur("18120.00"),
  variance: "-1320.00",
  annualizedBaselineCost: eur("28080.00"),
  annualizedPostActionCost: eur("8640.00"),
  actualTransitionCost: eur("1320.00"),
  confidence: 92,
  risk: "LOW",
  policyVersion: "DRC-AOA-001-v1",
  evidenceIds: [ids.evidence],
  assumptionIds: ["DRC-AOA-001-v1"],
  approvalEntryId: ids.ledger,
  implementationEntryId: "88888888-8888-4888-8888-888888888888",
  validationEntryId: "99999999-9999-4999-8999-999999999999",
  ledgerHistory: [
    {
      id: ids.ledger,
      type: "DECISION_APPROVED",
      actorId: ids.actor,
      actorRole: "ADMIN",
      occurredAt: ledger.occurredAt,
      previousEntryId: null,
      evidenceIds: [ids.evidence],
    },
  ],
};

export function page<T>(items: T[]) {
  return {
    items,
    page: 0,
    size: 100,
    totalItems: items.length,
    totalPages: items.length ? 1 : 0,
  };
}

export function token(role = "ADMIN", expiresInSeconds = 3600) {
  const encode = (value: unknown) =>
    Buffer.from(JSON.stringify(value)).toString("base64url");
  return `${encode({ alg: "RS256", typ: "JWT" })}.${encode({
    sub: ids.actor,
    imperator_role: role,
    exp: Math.floor(Date.now() / 1000) + expiresInSeconds,
  })}.signature`;
}

export function json(value: unknown, status = 200) {
  return new Response(JSON.stringify(value), {
    status,
    headers: { "content-type": "application/json" },
  });
}

export function workspaceFetch(
  options: { businessNotReady?: boolean; redacted?: boolean } = {},
) {
  const evidenceValue = options.redacted
    ? {
        ...evidence,
        sourceObjectRef: "[REDACTED]",
        entity: "[REDACTED]",
        actor: "[REDACTED]",
        observedFact: "[REDACTED]",
        businessMeaning: "[REDACTED]",
        correlationKey: "[REDACTED]",
      }
    : evidence;
  return async (input: RequestInfo | URL, init?: RequestInit) => {
    const url = String(input);
    if (init?.method === "POST") {
      if (
        url.endsWith("/approve") ||
        url.endsWith("/reject") ||
        url.endsWith("/defer")
      ) {
        return json(
          {
            ledgerEntryId: ids.ledger,
            decisionId: ids.decision,
            recommendationId: ids.recommendation,
            status: "APPROVED",
            reviewerId: ids.actor,
            reviewerRole: "ADMIN",
            reviewReason: "Authorized review",
            replayed: false,
          },
          201,
        );
      }
      return json(
        {
          ledgerEntryId: ids.ledger,
          decisionId: ids.decision,
          recommendationId: ids.recommendation,
          entryType: url.endsWith("/validate-result")
            ? "RESULT_VALIDATED"
            : "IMPLEMENTATION_MARKED",
          occurredAt: "2026-08-02T10:00:00Z",
          evidenceSnapshotCount: 1,
          realizedSaving: null,
          replayed: false,
        },
        201,
      );
    }
    if (url.includes("/timeline")) return json(page([timeline]));
    if (url.includes("/evidence")) return json(page([evidenceValue]));
    if (url.includes("/roi")) return json(roi);
    if (url.includes("/recommendations/")) return json(recommendation);
    if (url.includes("/ledger")) return json(page([ledger]));
    if (url.includes("/business-value")) {
      return options.businessNotReady
        ? json(
            {
              code: "BUSINESS_VALUE_NOT_READY",
              message: "Business Value is not ready",
              correlationId: ids.decision,
              details: {},
            },
            409,
          )
        : json(businessValue);
    }
    if (url === `/api/v1/decisions/${ids.decision}`) return json(decision);
    if (url.startsWith("/api/v1/decisions?"))
      return json(page([decisionSummary]));
    return json(
      {
        code: "RESOURCE_NOT_FOUND",
        message: "Not found",
        correlationId: ids.decision,
        details: {},
      },
      404,
    );
  };
}
