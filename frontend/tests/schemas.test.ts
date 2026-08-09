import { describe, expect, it } from "vitest";
import {
  businessValueSchema,
  decisionDetailSchema,
  evidenceSummarySchema,
  ledgerEntrySchema,
  moneySchema,
  pageSchema,
  recommendationSchema,
  roiSchema,
  timelineItemSchema,
} from "@/services/api/schemas";
import {
  businessValue,
  decision,
  evidence,
  ledger,
  page,
  recommendation,
  roi,
  timeline,
} from "./fixtures";

describe("certified REST schemas", () => {
  it("accepts every consumed response shape", () => {
    expect(decisionDetailSchema.parse(decision)).toEqual(decision);
    expect(
      pageSchema(timelineItemSchema).parse(page([timeline])).items,
    ).toHaveLength(1);
    expect(
      pageSchema(evidenceSummarySchema).parse(page([evidence])).items,
    ).toHaveLength(1);
    expect(roiSchema.parse(roi).policyVersion).toBe("DRC-AOA-001-v1");
    expect(recommendationSchema.parse(recommendation).type).toBe(
      "MODEL_CHANGE",
    );
    expect(
      pageSchema(ledgerEntrySchema).parse(page([ledger])).items,
    ).toHaveLength(1);
    expect(
      businessValueSchema.parse(businessValue).realizedSavings.amount,
    ).toBe("18120.00");
  });

  it("rejects unknown fields, noncanonical UUIDs and malformed money", () => {
    expect(() =>
      decisionDetailSchema.parse({ ...decision, invented: true }),
    ).toThrow();
    expect(() =>
      decisionDetailSchema.parse({
        ...decision,
        decisionId: "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa".toUpperCase(),
      }),
    ).toThrow();
    expect(() =>
      moneySchema.parse({ amount: "12.1", currency: "EUR" }),
    ).toThrow();
  });
});
