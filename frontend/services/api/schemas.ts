import { z } from "zod";

export const canonicalUuidSchema = z
  .string()
  .uuid()
  .refine(
    (value) => value === value.toLowerCase(),
    "UUID must be canonical lower-case",
  );
export const timestampSchema = z
  .string()
  .regex(/^\d{4}-\d{2}-\d{2}T.*Z$/, "UTC timestamp required");
export const nullableUuidSchema = canonicalUuidSchema.nullable();

export const moneySchema = z
  .object({
    amount: z.string().regex(/^-?(?:0|[1-9]\d*)\.\d{2}$/),
    currency: z.string().min(3).max(3),
  })
  .strict();

export const errorEnvelopeSchema = z
  .object({
    code: z.string().min(1),
    message: z.string().min(1),
    correlationId: z.string().min(1),
    details: z.record(z.string(), z.unknown()),
  })
  .strict();

export const decisionSummarySchema = z
  .object({
    decisionId: canonicalUuidSchema,
    caseId: z.string(),
    title: z.string(),
    businessNeed: z.string(),
    status: z.enum([
      "CREATED",
      "UNDER_REVIEW",
      "APPROVED",
      "REJECTED",
      "DEFERRED",
    ]),
    ownerId: canonicalUuidSchema,
    requiredApproverId: canonicalUuidSchema,
    recommendationId: nullableUuidSchema,
    createdAt: timestampSchema,
    updatedAt: timestampSchema,
  })
  .strict();

export const decisionDetailSchema = decisionSummarySchema
  .extend({
    originatingEvidenceId: canonicalUuidSchema,
    evidenceIds: z.array(canonicalUuidSchema),
    reviewedBy: nullableUuidSchema,
    reviewedAt: timestampSchema.nullable(),
    reviewReason: z.string().nullable(),
  })
  .strict();

export const timelineItemSchema = z
  .object({
    type: z.string(),
    referenceId: canonicalUuidSchema,
    occurredAt: timestampSchema,
    summary: z.string(),
    source: z.string().nullable(),
    actor: z.string().nullable(),
    confidenceLabel: z.string().nullable(),
    confidencePercentage: z.number().int().min(0).max(100).nullable(),
    evidenceIds: z.array(canonicalUuidSchema),
  })
  .strict();

export const evidenceSummarySchema = z
  .object({
    evidenceId: canonicalUuidSchema,
    timestamp: timestampSchema,
    source: z.string(),
    sourceType: z.string(),
    sourceObjectRef: z.string(),
    entity: z.string(),
    eventType: z.string(),
    severity: z.string(),
    actor: z.string(),
    evidenceType: z.string(),
    observedFact: z.string(),
    businessMeaning: z.string(),
    correlationKey: z.string(),
    sensitivity: z.string(),
    confidence: z.string(),
    reviewStatus: z.string(),
  })
  .strict();

export const roiSchema = z
  .object({
    decisionId: canonicalUuidSchema,
    recommendationId: canonicalUuidSchema,
    currentMonthlyCost: moneySchema,
    projectedMonthlyCost: moneySchema,
    transitionCost: moneySchema,
    estimatedMonthlyRecovery: moneySchema,
    estimatedAnnualizedRecovery: moneySchema,
    confidence: z.number().int().min(0).max(100),
    risk: z.string(),
    policyVersion: z.string(),
    assumptionEvidenceIds: z.array(canonicalUuidSchema),
  })
  .strict();

export const recommendationSchema = z
  .object({
    recommendationId: canonicalUuidSchema,
    decisionId: canonicalUuidSchema,
    type: z.string(),
    suggestedAction: z.string(),
    deterministicReason: z.string(),
    estimatedSavings: moneySchema,
    confidence: z.number().int().min(0).max(100),
    risk: z.string(),
    ownerId: canonicalUuidSchema,
    requiredApproverId: canonicalUuidSchema,
    createdAt: timestampSchema,
    evidenceIds: z.array(canonicalUuidSchema),
  })
  .strict();

export const ledgerEntrySchema = z
  .object({
    ledgerEntryId: canonicalUuidSchema,
    decisionId: canonicalUuidSchema,
    recommendationId: nullableUuidSchema,
    actorId: canonicalUuidSchema,
    actorRole: z.string(),
    occurredAt: timestampSchema,
    entryType: z.string(),
    changeSummary: z.string(),
    reason: z.string(),
    evidenceIds: z.array(canonicalUuidSchema),
    estimatedSavings: moneySchema.nullable(),
    realizedSavings: moneySchema.nullable(),
    confidence: z.number().int().min(0).max(100).nullable(),
    risk: z.string().nullable(),
    previousEntryId: nullableUuidSchema,
    metadata: z.record(z.string(), z.string()),
  })
  .strict();

const ledgerFactSchema = z
  .object({
    id: canonicalUuidSchema,
    type: z.string(),
    actorId: canonicalUuidSchema,
    actorRole: z.string(),
    occurredAt: timestampSchema,
    previousEntryId: nullableUuidSchema,
    evidenceIds: z.array(canonicalUuidSchema),
  })
  .strict();

export const businessValueSchema = z
  .object({
    caseId: z.string(),
    correlationKey: z.string(),
    decisionId: canonicalUuidSchema,
    decisionTitle: z.string(),
    businessNeed: z.string(),
    decisionStatus: z.string(),
    decisionCreatedAt: timestampSchema,
    recommendationId: canonicalUuidSchema,
    recommendationType: z.string(),
    recommendedAction: z.string(),
    deterministicReason: z.string(),
    explanation: z.string().nullable(),
    recommendationCreatedAt: timestampSchema,
    estimatedSavings: moneySchema,
    realizedSavings: moneySchema,
    variance: z.string(),
    annualizedBaselineCost: moneySchema,
    annualizedPostActionCost: moneySchema,
    actualTransitionCost: moneySchema,
    confidence: z.number().int().min(0).max(100),
    risk: z.string(),
    policyVersion: z.string(),
    evidenceIds: z.array(canonicalUuidSchema),
    assumptionIds: z.array(z.string()),
    approvalEntryId: canonicalUuidSchema,
    implementationEntryId: canonicalUuidSchema,
    validationEntryId: canonicalUuidSchema,
    ledgerHistory: z.array(ledgerFactSchema),
  })
  .strict();

export function pageSchema<T extends z.ZodType>(item: T) {
  return z
    .object({
      items: z.array(item),
      page: z.number().int().nonnegative(),
      size: z.number().int().positive().max(100),
      totalItems: z.number().int().nonnegative(),
      totalPages: z.number().int().nonnegative(),
    })
    .strict();
}

export type Money = z.infer<typeof moneySchema>;
export type ApiErrorEnvelope = z.infer<typeof errorEnvelopeSchema>;
export type DecisionSummary = z.infer<typeof decisionSummarySchema>;
export type DecisionDetail = z.infer<typeof decisionDetailSchema>;
export type TimelineItem = z.infer<typeof timelineItemSchema>;
export type EvidenceSummary = z.infer<typeof evidenceSummarySchema>;
export type Roi = z.infer<typeof roiSchema>;
export type Recommendation = z.infer<typeof recommendationSchema>;
export type LedgerEntry = z.infer<typeof ledgerEntrySchema>;
export type BusinessValue = z.infer<typeof businessValueSchema>;
export type Page<T> = {
  items: T[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
};
