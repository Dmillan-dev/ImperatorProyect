import { z } from "zod";
import { ApiClient } from "./client";
import {
  businessValueSchema,
  decisionDetailSchema,
  decisionSummarySchema,
  evidenceSummarySchema,
  ledgerEntrySchema,
  pageSchema,
  recommendationSchema,
  roiSchema,
  timelineItemSchema,
  type BusinessValue,
  type DecisionDetail,
  type DecisionSummary,
  type EvidenceSummary,
  type LedgerEntry,
  type Page,
  type Recommendation,
  type Roi,
  type TimelineItem,
} from "./schemas";

export type ReviewAction = "approve" | "reject" | "defer";
export type AppendAction = "mark-implemented" | "validate-result";

export class WorkspaceApi {
  constructor(private readonly client: ApiClient) {}

  listDecisions(correlationId: string): Promise<Page<DecisionSummary>> {
    return this.client.request(
      "/api/v1/decisions?page=0&size=100&sort=updatedAt&direction=DESC",
      pageSchema(decisionSummarySchema),
      { correlationId },
    );
  }

  getDecision(id: string, correlationId: string): Promise<DecisionDetail> {
    return this.client.request(
      `/api/v1/decisions/${id}`,
      decisionDetailSchema,
      { correlationId },
    );
  }

  getTimeline(
    id: string,
    page: number,
    correlationId: string,
  ): Promise<Page<TimelineItem>> {
    return this.client.request(
      `/api/v1/decisions/${id}/timeline?page=${page}&size=100&sort=occurredAt&direction=ASC`,
      pageSchema(timelineItemSchema),
      { correlationId },
    );
  }

  getEvidence(
    id: string,
    page: number,
    correlationId: string,
  ): Promise<Page<EvidenceSummary>> {
    return this.client.request(
      `/api/v1/decisions/${id}/evidence?page=${page}&size=100&sort=timestamp&direction=ASC`,
      pageSchema(evidenceSummarySchema),
      { correlationId },
    );
  }

  getRoi(id: string, correlationId: string): Promise<Roi> {
    return this.client.request(`/api/v1/decisions/${id}/roi`, roiSchema, {
      correlationId,
    });
  }

  getRecommendation(
    id: string,
    correlationId: string,
  ): Promise<Recommendation> {
    return this.client.request(
      `/api/v1/recommendations/${id}`,
      recommendationSchema,
      { correlationId },
    );
  }

  getLedger(
    id: string,
    page: number,
    correlationId: string,
  ): Promise<Page<LedgerEntry>> {
    return this.client.request(
      `/api/v1/decisions/${id}/ledger?page=${page}&size=100&sort=occurredAt&direction=ASC`,
      pageSchema(ledgerEntrySchema),
      { correlationId },
    );
  }

  getBusinessValue(id: string, correlationId: string): Promise<BusinessValue> {
    return this.client.request(
      `/api/v1/business-value?decisionId=${id}`,
      businessValueSchema,
      { correlationId },
    );
  }

  review(
    id: string,
    action: ReviewAction,
    body: unknown,
    idempotencyKey: string,
  ) {
    return this.client.request(
      `/api/v1/decisions/${id}/ledger/${action}`,
      reviewResultSchema,
      { method: "POST", body, idempotencyKey },
    );
  }

  append(
    id: string,
    action: AppendAction,
    body: unknown,
    idempotencyKey: string,
  ) {
    return this.client.request(
      `/api/v1/decisions/${id}/ledger/${action}`,
      appendResultSchema,
      { method: "POST", body, idempotencyKey },
    );
  }
}

const reviewResultSchema = z
  .object({
    ledgerEntryId: z.string().uuid(),
    decisionId: z.string().uuid(),
    recommendationId: z.string().uuid(),
    status: z.string(),
    reviewerId: z.string().uuid(),
    reviewerRole: z.string(),
    reviewReason: z.string(),
    replayed: z.boolean(),
  })
  .strict();

const appendResultSchema = z
  .object({
    ledgerEntryId: z.string().uuid(),
    decisionId: z.string().uuid(),
    recommendationId: z.string().uuid().nullable(),
    entryType: z.string(),
    occurredAt: z.string(),
    evidenceSnapshotCount: z.number().int().nonnegative(),
    realizedSaving: z
      .object({ amount: z.string(), currency: z.string() })
      .strict()
      .nullable(),
    replayed: z.boolean(),
  })
  .strict();
