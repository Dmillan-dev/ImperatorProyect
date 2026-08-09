import { describe, expect, it } from "vitest";
import {
  buildActionPayload,
  roleActions,
} from "@/features/decision-review/action-dialog";
import { ids } from "./fixtures";

function data(values: Record<string, string | string[]>) {
  const form = new FormData();
  for (const [key, value] of Object.entries(values)) {
    for (const item of Array.isArray(value) ? value : [value])
      form.append(key, item);
  }
  return form;
}

const base = {
  occurredAt: "2026-08-02T10:00",
  reason: "Verified by the authorized reviewer",
};

describe("governance command builders", () => {
  it("freezes the role presentation matrix", () => {
    expect(roleActions.ADMIN).toEqual(["approve", "reject", "defer"]);
    expect(roleActions.PLATFORM_ENGINEER).toEqual([
      "defer",
      "mark-implemented",
    ]);
    expect(roleActions.FINANCE).toEqual(["defer", "validate-result"]);
    expect(roleActions.AUDITOR).toEqual([]);
  });

  it("builds review and defer commands without technical fields", () => {
    expect(
      buildActionPayload("approve", data(base), "evidence", ids.ledger),
    ).toMatchObject({
      reason: base.reason,
      expectedPreviousEntryId: ids.ledger,
    });
    expect(
      buildActionPayload(
        "defer",
        data({ ...base, requiredEvidence: "Post-action quality report" }),
        "evidence",
        null,
      ),
    ).toMatchObject({ requiredEvidence: "Post-action quality report" });
    expect(
      buildActionPayload(
        "defer",
        data({ ...base, reviewDate: "2026-09-01" }),
        "date",
        null,
      ),
    ).toMatchObject({ reviewDate: "2026-09-01" });
  });

  it("builds append and financial validation commands", () => {
    const append = buildActionPayload(
      "mark-implemented",
      data({ ...base, period: "2026-07", evidenceIds: [ids.evidence] }),
      "evidence",
      ids.ledger,
    );
    expect(append).toMatchObject({
      evidenceIds: [ids.evidence],
      expectedPreviousEntryId: ids.ledger,
    });

    const validate = buildActionPayload(
      "validate-result",
      data({
        ...base,
        period: "2026-07",
        evidenceIds: [ids.evidence],
        annualizedBaselineCost: "28080.00",
        annualizedPostActionCost: "8640.00",
        actualTransitionCost: "1320.00",
      }),
      "evidence",
      ids.ledger,
    );
    expect(validate).toMatchObject({
      annualizedBaselineCost: { amount: "28080.00", currency: "EUR" },
    });
  });

  it("requires Evidence, Ledger head and scale-two money", () => {
    expect(() =>
      buildActionPayload(
        "mark-implemented",
        data({ ...base, period: "2026-07" }),
        "evidence",
        null,
      ),
    ).toThrow(/Ledger head/);
    expect(() =>
      buildActionPayload(
        "validate-result",
        data({
          ...base,
          period: "2026-07",
          evidenceIds: ids.evidence,
          annualizedBaselineCost: "1",
          annualizedPostActionCost: "0.00",
          actualTransitionCost: "0.00",
        }),
        "evidence",
        ids.ledger,
      ),
    ).toThrow(/two decimals/);
  });
});
