import { act, renderHook, waitFor } from "@testing-library/react";
import { describe, expect, it, vi } from "vitest";
import { useWorkspace } from "@/features/decision-review/use-workspace";
import { decision, ids, json, page, token, workspaceFetch } from "./fixtures";

describe("workspace orchestration branches", () => {
  it("loads pages independently and submits an append capability", async () => {
    const fetcher = vi.fn(workspaceFetch());
    vi.stubGlobal("fetch", fetcher);
    const onUnauthorized = vi.fn();
    const { result } = renderHook(() =>
      useWorkspace(ids.decision, token("PLATFORM_ENGINEER"), onUnauthorized),
    );
    await waitFor(() =>
      expect(result.current.state.ledger.status).toBe("ready"),
    );

    await act(async () => {
      await result.current.loadPage("timeline", 0);
      await result.current.loadPage("evidence", 0);
      await result.current.loadPage("ledger", 0);
    });
    await act(async () => {
      await result.current.submit(
        "mark-implemented",
        { occurredAt: "2026-08-02T10:00:00Z" },
        ids.ledger,
      );
    });
    expect(result.current.command.status).toBe("success");
    expect(fetcher.mock.calls.some(([, init]) => init?.method === "POST")).toBe(
      true,
    );
  });

  it("represents empty pages and a missing Recommendation truthfully", async () => {
    const base = workspaceFetch({ businessNotReady: true });
    vi.stubGlobal(
      "fetch",
      vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
        const url = String(input);
        if (url === `/api/v1/decisions/${ids.decision}`) {
          return json({ ...decision, recommendationId: null });
        }
        if (
          url.includes("/timeline") ||
          url.includes("/evidence") ||
          url.includes("/ledger")
        ) {
          return json(page([]));
        }
        return base(input, init);
      }),
    );
    const onUnauthorized = vi.fn();
    const { result } = renderHook(() =>
      useWorkspace(ids.decision, token("AUDITOR"), onUnauthorized),
    );
    await waitFor(() =>
      expect(result.current.state.businessValue.status).toBe("not-ready"),
    );
    expect(result.current.state.timeline.status).toBe("empty");
    expect(result.current.state.evidence.status).toBe("empty");
    expect(result.current.state.ledger.status).toBe("empty");
    expect(result.current.state.recommendation.status).toBe("empty");
  });

  it("fails closed for a different case and for a rejected command", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
        if (init?.method === "POST") {
          return json(
            {
              code: "LEDGER_OPERATION_CONFLICT",
              message: "Ledger operation conflicts with the current head",
              correlationId: ids.decision,
              details: {},
            },
            409,
          );
        }
        if (String(input) === `/api/v1/decisions/${ids.decision}`) {
          return json({ ...decision, caseId: "OTHER-CASE" });
        }
        return workspaceFetch()(input, init);
      }),
    );
    const mismatchedUnauthorized = vi.fn();
    const mismatched = renderHook(() =>
      useWorkspace(ids.decision, token("ADMIN"), mismatchedUnauthorized),
    );
    await waitFor(() =>
      expect(mismatched.result.current.state.decision.status).toBe("error"),
    );
    expect(mismatched.result.current.state.decision).toMatchObject({
      error: { code: "CASE_SCOPE_MISMATCH" },
    });
    mismatched.unmount();

    vi.stubGlobal(
      "fetch",
      vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
        if (init?.method === "POST") {
          return json(
            {
              code: "LEDGER_OPERATION_CONFLICT",
              message: "Ledger operation conflicts with the current head",
              correlationId: ids.decision,
              details: {},
            },
            409,
          );
        }
        return workspaceFetch()(input, init);
      }),
    );
    const rejectedUnauthorized = vi.fn();
    const rejected = renderHook(() =>
      useWorkspace(ids.decision, token("ADMIN"), rejectedUnauthorized),
    );
    await waitFor(() =>
      expect(rejected.result.current.state.decision.status).toBe("ready"),
    );
    await act(async () => {
      await expect(
        rejected.result.current.submit("approve", {}, ids.ledger),
      ).rejects.toMatchObject({
        code: "LEDGER_OPERATION_CONFLICT",
      });
    });
    expect(rejected.result.current.command.status).toBe("error");
  });
});
