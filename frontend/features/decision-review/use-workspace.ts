"use client";

import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { ApiClient, ApiClientError } from "@/services/api/client";
import type {
  BusinessValue,
  DecisionDetail,
  EvidenceSummary,
  LedgerEntry,
  Page,
  Recommendation,
  Roi,
  TimelineItem,
} from "@/services/api/schemas";
import {
  WorkspaceApi,
  type AppendAction,
  type ReviewAction,
} from "@/services/api/workspace";

export type Loadable<T> =
  | { status: "loading" }
  | { status: "ready"; data: T }
  | { status: "empty" }
  | { status: "not-ready" }
  | { status: "error"; error: ApiClientError };

type WorkspaceState = {
  decision: Loadable<DecisionDetail>;
  timeline: Loadable<Page<TimelineItem>>;
  evidence: Loadable<Page<EvidenceSummary>>;
  roi: Loadable<Roi>;
  recommendation: Loadable<Recommendation>;
  ledger: Loadable<Page<LedgerEntry>>;
  businessValue: Loadable<BusinessValue>;
};

type CommandState =
  | { status: "idle" }
  | { status: "pending" }
  | { status: "success"; message: string }
  | { status: "error"; error: ApiClientError };

const loadingState: WorkspaceState = {
  decision: { status: "loading" },
  timeline: { status: "loading" },
  evidence: { status: "loading" },
  roi: { status: "loading" },
  recommendation: { status: "loading" },
  ledger: { status: "loading" },
  businessValue: { status: "loading" },
};

export function useWorkspace(
  decisionId: string,
  token: string,
  onUnauthorized: () => void,
) {
  const [state, setState] = useState<WorkspaceState>(loadingState);
  const [command, setCommand] = useState<CommandState>({ status: "idle" });
  const generation = useRef(0);
  const api = useMemo(
    () => new WorkspaceApi(new ApiClient(token, onUnauthorized)),
    [token, onUnauthorized],
  );

  const load = useCallback(async () => {
    const current = ++generation.current;
    const correlationId = crypto.randomUUID();
    setState(loadingState);
    try {
      const decision = await api.getDecision(decisionId, correlationId);
      if (decision.caseId !== "DRC-AOA-001") {
        throw new ApiClientError(
          0,
          "CASE_SCOPE_MISMATCH",
          "This workspace supports DRC-AOA-001 only",
          correlationId,
        );
      }
      if (generation.current !== current) return;
      setState((previous) => ({
        ...previous,
        decision: { status: "ready", data: decision },
      }));

      const update = <K extends keyof WorkspaceState>(
        key: K,
        value: WorkspaceState[K],
      ) => {
        if (generation.current === current)
          setState((previous) => ({ ...previous, [key]: value }));
      };
      const capture = async <K extends keyof WorkspaceState, T>(
        key: K,
        request: Promise<T>,
        ready: (data: T) => WorkspaceState[K],
      ) => {
        try {
          update(key, ready(await request));
        } catch (caught) {
          const error = asApiError(caught, correlationId);
          if (
            key === "businessValue" &&
            error.status === 409 &&
            error.code === "BUSINESS_VALUE_NOT_READY"
          ) {
            update(key, { status: "not-ready" } as WorkspaceState[K]);
          } else {
            update(key, { status: "error", error } as WorkspaceState[K]);
          }
        }
      };

      const tasks = [
        capture(
          "timeline",
          api.getTimeline(decisionId, 0, correlationId),
          pageLoadable,
        ),
        capture(
          "evidence",
          api.getEvidence(decisionId, 0, correlationId),
          pageLoadable,
        ),
        capture("roi", api.getRoi(decisionId, correlationId), (data) => ({
          status: "ready",
          data,
        })),
        capture(
          "ledger",
          api.getLedger(decisionId, 0, correlationId),
          pageLoadable,
        ),
        capture(
          "businessValue",
          api.getBusinessValue(decisionId, correlationId),
          (data) => ({ status: "ready", data }),
        ),
      ];
      if (decision.recommendationId) {
        tasks.push(
          capture(
            "recommendation",
            api.getRecommendation(decision.recommendationId, correlationId),
            (data) => ({ status: "ready", data }),
          ),
        );
      } else {
        update("recommendation", { status: "empty" });
      }
      await Promise.allSettled(tasks);
    } catch (caught) {
      const error = asApiError(caught, correlationId);
      if (generation.current === current) {
        setState({ ...loadingState, decision: { status: "error", error } });
      }
    }
  }, [api, decisionId]);

  useEffect(() => {
    void load();
  }, [load]);

  const loadPage = useCallback(
    async (kind: "timeline" | "evidence" | "ledger", page: number) => {
      const correlationId = crypto.randomUUID();
      setState((previous) => ({ ...previous, [kind]: { status: "loading" } }));
      try {
        if (kind === "timeline") {
          const result = await api.getTimeline(decisionId, page, correlationId);
          setState((previous) => ({
            ...previous,
            timeline: pageLoadable(result),
          }));
        } else if (kind === "evidence") {
          const result = await api.getEvidence(decisionId, page, correlationId);
          setState((previous) => ({
            ...previous,
            evidence: pageLoadable(result),
          }));
        } else {
          const result = await api.getLedger(decisionId, page, correlationId);
          setState((previous) => ({
            ...previous,
            ledger: pageLoadable(result),
          }));
        }
      } catch (caught) {
        setState((previous) => ({
          ...previous,
          [kind]: { status: "error", error: asApiError(caught, correlationId) },
        }));
      }
    },
    [api, decisionId],
  );

  const submit = useCallback(
    async (
      action: ReviewAction | AppendAction,
      body: unknown,
      idempotencyKey: string,
    ) => {
      setCommand({ status: "pending" });
      try {
        if (["approve", "reject", "defer"].includes(action)) {
          await api.review(
            decisionId,
            action as ReviewAction,
            body,
            idempotencyKey,
          );
        } else {
          await api.append(
            decisionId,
            action as AppendAction,
            body,
            idempotencyKey,
          );
        }
        setCommand({
          status: "success",
          message: "Authoritative state updated",
        });
        await load();
      } catch (caught) {
        setCommand({
          status: "error",
          error: asApiError(caught, crypto.randomUUID()),
        });
        throw caught;
      }
    },
    [api, decisionId, load],
  );

  return { state, command, reload: load, loadPage, submit };
}

function pageLoadable<T>(page: Page<T>): Loadable<Page<T>> {
  return page.items.length === 0
    ? { status: "empty" }
    : { status: "ready", data: page };
}

function asApiError(caught: unknown, correlationId: string) {
  return caught instanceof ApiClientError
    ? caught
    : new ApiClientError(
        0,
        "UNEXPECTED_CLIENT_ERROR",
        "An unexpected client error occurred",
        correlationId,
      );
}
