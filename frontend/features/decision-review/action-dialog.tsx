"use client";

import {
  AlertTriangle,
  CalendarClock,
  CheckCircle2,
  CircleDollarSign,
  X,
} from "lucide-react";
import {
  type FormEvent,
  type KeyboardEvent,
  useEffect,
  useRef,
  useState,
} from "react";
import type { EvidenceSummary } from "@/services/api/schemas";
import type { AppendAction, ReviewAction } from "@/services/api/workspace";
import { humanize } from "@/components/ui/primitives";
import {
  localDateTimeValue,
  money,
  parseEvidenceIds,
  toUtcTimestamp,
} from "./presenters";

export type ActionKind = ReviewAction | AppendAction;

export const roleActions = {
  ADMIN: ["approve", "reject", "defer"],
  PLATFORM_ENGINEER: ["defer", "mark-implemented"],
  FINANCE: ["defer", "validate-result"],
  AUDITOR: [],
} as const satisfies Record<string, readonly ActionKind[]>;

type Props = {
  action: ActionKind;
  decisionId: string;
  evidence: EvidenceSummary[];
  ledgerHead: string | null;
  pending: boolean;
  onClose: () => void;
  onSubmit: (body: unknown, idempotencyKey: string) => Promise<void>;
};

export function ActionDialog(props: Props) {
  const {
    action,
    decisionId,
    evidence,
    ledgerHead,
    pending,
    onClose,
    onSubmit,
  } = props;
  const panelRef = useRef<HTMLDivElement>(null);
  const restoreFocus = useRef<HTMLElement | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [confirmation, setConfirmation] = useState<{
    body: unknown;
    idempotencyKey: string;
  } | null>(null);
  const [deferMode, setDeferMode] = useState<"evidence" | "date">("evidence");

  useEffect(() => {
    restoreFocus.current = document.activeElement as HTMLElement | null;
    panelRef.current
      ?.querySelector<HTMLElement>("button, input, textarea")
      ?.focus();
    return () => restoreFocus.current?.focus();
  }, []);

  useEffect(() => {
    function dismiss(event: globalThis.KeyboardEvent) {
      if (event.key === "Escape" && !pending) onClose();
    }
    document.addEventListener("keydown", dismiss);
    return () => document.removeEventListener("keydown", dismiss);
  }, [onClose, pending]);

  function trapFocus(event: KeyboardEvent<HTMLDivElement>) {
    if (event.key !== "Tab") return;
    const focusable = [
      ...(panelRef.current?.querySelectorAll<HTMLElement>(
        "button:not(:disabled), input:not(:disabled), textarea:not(:disabled)",
      ) ?? []),
    ];
    if (focusable.length === 0) return;
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    if (event.shiftKey && document.activeElement === first) {
      event.preventDefault();
      last.focus();
    } else if (!event.shiftKey && document.activeElement === last) {
      event.preventDefault();
      first.focus();
    }
  }

  function prepare(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    try {
      const data = new FormData(event.currentTarget);
      setConfirmation({
        body: buildActionPayload(action, data, deferMode, ledgerHead),
        idempotencyKey: crypto.randomUUID(),
      });
      setError(null);
    } catch (caught) {
      setError(
        caught instanceof Error ? caught.message : "The command is invalid",
      );
    }
  }

  async function confirm() {
    if (!confirmation) return;
    try {
      await onSubmit(confirmation.body, confirmation.idempotencyKey);
      onClose();
    } catch {
      setConfirmation(null);
      setError(
        "The server rejected the command. Review the authoritative error above the workspace.",
      );
    }
  }

  return (
    <div className="dialog-backdrop" role="presentation">
      <div
        ref={panelRef}
        className="action-dialog"
        role="dialog"
        aria-modal="true"
        aria-labelledby="action-dialog-title"
        onKeyDown={trapFocus}
      >
        <header className="dialog-header">
          <div>
            <p className="section-eyebrow">GOVERNANCE COMMAND</p>
            <h2 id="action-dialog-title">{humanize(action)}</h2>
          </div>
          <button
            className="icon-button"
            type="button"
            onClick={onClose}
            disabled={pending}
            title="Close"
          >
            <X size={18} />
            <span className="sr-only">Close</span>
          </button>
        </header>

        {confirmation ? (
          <div className="confirmation-step">
            <div className="confirmation-icon" aria-hidden="true">
              <AlertTriangle size={24} />
            </div>
            <h3>Confirm authoritative change</h3>
            <p>
              {humanize(action)} will be submitted for Decision{" "}
              <code>{decisionId}</code>. The server remains responsible for
              accepting the transition and appending the audit fact.
            </p>
            <div className="dialog-actions">
              <button
                className="button button-secondary"
                type="button"
                onClick={() => setConfirmation(null)}
                disabled={pending}
              >
                Back
              </button>
              <button
                className="button button-primary"
                type="button"
                onClick={confirm}
                disabled={pending}
              >
                <CheckCircle2 size={16} />{" "}
                {pending ? "Submitting..." : `Confirm ${humanize(action)}`}
              </button>
            </div>
          </div>
        ) : (
          <form onSubmit={prepare} className="action-form">
            <label>
              Occurrence time
              <input
                name="occurredAt"
                type="datetime-local"
                defaultValue={localDateTimeValue()}
                required
              />
            </label>
            <label>
              Reason
              <textarea
                name="reason"
                rows={3}
                minLength={3}
                maxLength={1000}
                required
              />
            </label>

            {action === "defer" ? (
              <fieldset>
                <legend>Deferral requirement</legend>
                <div className="segmented-control">
                  <label>
                    <input
                      type="radio"
                      name="deferMode"
                      checked={deferMode === "evidence"}
                      onChange={() => setDeferMode("evidence")}
                    />
                    Required Evidence
                  </label>
                  <label>
                    <input
                      type="radio"
                      name="deferMode"
                      checked={deferMode === "date"}
                      onChange={() => setDeferMode("date")}
                    />
                    Review date
                  </label>
                </div>
                {deferMode === "evidence" ? (
                  <label>
                    Required Evidence
                    <textarea
                      name="requiredEvidence"
                      rows={2}
                      maxLength={500}
                      required
                    />
                  </label>
                ) : (
                  <label>
                    Review date
                    <input name="reviewDate" type="date" required />
                  </label>
                )}
              </fieldset>
            ) : null}

            {action === "mark-implemented" || action === "validate-result" ? (
              <>
                <label>
                  Period
                  <input
                    name="period"
                    type="text"
                    placeholder="2026-07"
                    maxLength={40}
                    required
                  />
                </label>
                <fieldset>
                  <legend>Supporting Evidence</legend>
                  <div className="evidence-picker">
                    {evidence.length ? (
                      evidence.map((item) => (
                        <label key={item.evidenceId}>
                          <input
                            type="checkbox"
                            name="evidenceIds"
                            value={item.evidenceId}
                          />
                          <span>
                            <strong>{item.evidenceType}</strong>
                            <small>{item.evidenceId}</small>
                          </span>
                        </label>
                      ))
                    ) : (
                      <p>No authorized Evidence is available.</p>
                    )}
                  </div>
                </fieldset>
                <label>
                  Current Ledger head
                  <input
                    name="expectedPreviousEntryId"
                    value={ledgerHead ?? ""}
                    readOnly
                    required
                  />
                </label>
              </>
            ) : null}

            {action === "validate-result" ? (
              <fieldset className="money-grid">
                <legend>Validated annualized EUR facts</legend>
                <MoneyInput
                  name="annualizedBaselineCost"
                  label="Baseline cost"
                />
                <MoneyInput
                  name="annualizedPostActionCost"
                  label="Post-action cost"
                />
                <MoneyInput
                  name="actualTransitionCost"
                  label="Transition cost"
                />
              </fieldset>
            ) : null}

            {error ? (
              <p className="field-error" role="alert">
                {error}
              </p>
            ) : null}
            <div className="dialog-actions">
              <button
                className="button button-secondary"
                type="button"
                onClick={onClose}
              >
                Cancel
              </button>
              <button className="button button-primary" type="submit">
                {action === "validate-result" ? (
                  <CircleDollarSign size={16} />
                ) : (
                  <CalendarClock size={16} />
                )}
                Review command
              </button>
            </div>
          </form>
        )}
      </div>
    </div>
  );
}

function MoneyInput({ name, label }: { name: string; label: string }) {
  return (
    <label>
      {label}
      <span className="money-input">
        <span>EUR</span>
        <input
          name={name}
          inputMode="decimal"
          placeholder="0.00"
          pattern="(?:0|[1-9][0-9]*)\.[0-9]{2}"
          required
        />
      </span>
    </label>
  );
}

export function buildActionPayload(
  action: ActionKind,
  data: FormData,
  deferMode: "evidence" | "date",
  ledgerHead: string | null,
) {
  const occurredAt = toUtcTimestamp(String(data.get("occurredAt") ?? ""));
  const reason = requiredText(data, "reason", "A reason is required");
  const optionalHead = ledgerHead
    ? { expectedPreviousEntryId: ledgerHead }
    : {};

  if (action === "approve" || action === "reject") {
    return { reviewedAt: occurredAt, reason, ...optionalHead };
  }
  if (action === "defer") {
    return {
      reviewedAt: occurredAt,
      reason,
      ...optionalHead,
      ...(deferMode === "evidence"
        ? {
            requiredEvidence: requiredText(
              data,
              "requiredEvidence",
              "Required Evidence is required",
            ),
          }
        : {
            reviewDate: requiredText(
              data,
              "reviewDate",
              "A review date is required",
            ),
          }),
    };
  }
  if (!ledgerHead)
    throw new Error("An authoritative Ledger head is required for this action");
  const base = {
    occurredAt,
    reason,
    evidenceIds: parseEvidenceIds(data.getAll("evidenceIds")),
    expectedPreviousEntryId: ledgerHead,
    period: requiredText(data, "period", "A period is required"),
  };
  if (action === "mark-implemented") return base;
  return {
    ...base,
    annualizedBaselineCost: money(
      requiredText(data, "annualizedBaselineCost", "Baseline cost is required"),
    ),
    annualizedPostActionCost: money(
      requiredText(
        data,
        "annualizedPostActionCost",
        "Post-action cost is required",
      ),
    ),
    actualTransitionCost: money(
      requiredText(data, "actualTransitionCost", "Transition cost is required"),
    ),
  };
}

function requiredText(data: FormData, name: string, message: string) {
  const value = String(data.get(name) ?? "").trim();
  if (!value) throw new Error(message);
  return value;
}
