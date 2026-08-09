import { describe, expect, it } from "vitest";
import {
  formatMoney,
  formatTimestamp,
  money,
  parseEvidenceIds,
  toUtcTimestamp,
} from "@/features/decision-review/presenters";

describe("presentation-only formatters", () => {
  it("formats but never converts authoritative money", () => {
    expect(formatMoney({ amount: "19440.00", currency: "EUR" })).toContain(
      "19,440.00",
    );
    expect(money("12.00")).toEqual({ amount: "12.00", currency: "EUR" });
    expect(() => money("12.0")).toThrow();
  });

  it("normalizes timestamps and unique Evidence selections", () => {
    expect(toUtcTimestamp("2026-08-02T10:00:00Z")).toBe(
      "2026-08-02T10:00:00.000Z",
    );
    expect(formatTimestamp("2026-08-02T10:00:00Z")).toContain("UTC");
    expect(parseEvidenceIds(["one", "one", "two"])).toEqual(["one", "two"]);
    expect(() => parseEvidenceIds([])).toThrow();
  });
});
