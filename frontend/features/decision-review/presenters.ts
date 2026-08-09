import type { Money } from "@/services/api/schemas";

const dateFormatter = new Intl.DateTimeFormat("en-GB", {
  dateStyle: "medium",
  timeStyle: "short",
  timeZone: "UTC",
});

export function formatTimestamp(value: string) {
  return `${dateFormatter.format(new Date(value))} UTC`;
}

export function formatMoney(value: Money) {
  return new Intl.NumberFormat("en-GB", {
    style: "currency",
    currency: value.currency,
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  }).format(Number(value.amount));
}

export function localDateTimeValue(date = new Date()) {
  const local = new Date(date.getTime() - date.getTimezoneOffset() * 60_000);
  return local.toISOString().slice(0, 16);
}

export function toUtcTimestamp(value: string) {
  const parsed = new Date(value);
  if (!value || Number.isNaN(parsed.getTime()))
    throw new Error("A valid occurrence time is required");
  return parsed.toISOString();
}

export function parseEvidenceIds(values: FormDataEntryValue[]) {
  const unique = new Set(values.map(String).filter(Boolean));
  if (unique.size === 0) throw new Error("Select at least one Evidence item");
  return [...unique];
}

export function money(amount: string) {
  if (!/^(?:0|[1-9]\d*)\.\d{2}$/.test(amount)) {
    throw new Error(
      "Money must be a non-negative EUR amount with two decimals",
    );
  }
  return { amount, currency: "EUR" } as const;
}
