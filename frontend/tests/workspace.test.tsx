import { fireEvent, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import { DecisionReviewWorkspace } from "@/features/decision-review/decision-review-workspace";
import { SessionProvider } from "@/services/auth/session";
import { ids, json, token, workspaceFetch } from "./fixtures";

async function renderWorkspace(
  role: string,
  options: { businessNotReady?: boolean; redacted?: boolean } = {},
) {
  const fetcher = vi.fn(workspaceFetch(options));
  vi.stubGlobal("fetch", fetcher);
  render(
    <SessionProvider>
      <DecisionReviewWorkspace decisionId={ids.decision} />
    </SessionProvider>,
  );
  fireEvent.change(screen.getByLabelText("Bearer token"), {
    target: { value: token(role) },
  });
  fireEvent.click(screen.getByRole("button", { name: "Enter workspace" }));
  await screen.findByRole("heading", {
    name: "Recover AI onboarding assistant spend",
  });
  return fetcher;
}

describe("Decision Review Workspace", () => {
  it("renders authoritative value, traceability and exact ADMIN actions", async () => {
    await renderWorkspace("ADMIN");
    expect(
      screen.getByText("Estimated annual recovery").closest("article"),
    ).toHaveTextContent("€19,440.00");
    expect(screen.getAllByText("€18,120.00")).toHaveLength(2);
    expect(
      screen.getByText("Monthly spend was EUR 2,340.00"),
    ).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Approve" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Reject" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Defer" })).toBeInTheDocument();
    expect(
      screen.queryByRole("button", { name: "Validate Result" }),
    ).not.toBeInTheDocument();
  });

  it("keeps AUDITOR read-only and Business Value not-ready distinct from zero", async () => {
    await renderWorkspace("AUDITOR", {
      businessNotReady: true,
      redacted: true,
    });
    expect(screen.getByText("Read-only workspace")).toBeInTheDocument();
    expect(screen.getAllByText("Not yet realized").length).toBeGreaterThan(0);
    expect(screen.getByText("Protected Evidence")).toBeInTheDocument();
    expect(screen.queryByText("€0.00")).not.toBeInTheDocument();
  });

  it("submits an idempotent review and reloads authoritative state", async () => {
    const fetcher = await renderWorkspace("ADMIN");
    await userEvent.click(screen.getByRole("button", { name: "Approve" }));
    fireEvent.change(screen.getByLabelText("Reason"), {
      target: { value: "Evidence reviewed by the approver" },
    });
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Confirm Approve" }),
    );
    await screen.findByText("Authoritative state updated");
    const postCall = fetcher.mock.calls.find(
      ([, init]) => init?.method === "POST",
    );
    expect(postCall).toBeDefined();
    expect(new Headers(postCall?.[1]?.headers).get("Idempotency-Key")).toMatch(
      /^[0-9a-f-]{36}$/,
    );
    expect(JSON.parse(String(postCall?.[1]?.body))).toMatchObject({
      reason: "Evidence reviewed by the approver",
    });
  });

  it("shows safe partial errors without erasing the Decision", async () => {
    const base = workspaceFetch();
    vi.stubGlobal(
      "fetch",
      vi.fn(async (input: RequestInfo | URL, init?: RequestInit) => {
        if (String(input).includes("/evidence")) {
          return json(
            {
              code: "SERVICE_UNAVAILABLE",
              message: "Service unavailable",
              correlationId: ids.decision,
              details: {},
            },
            503,
          );
        }
        return base(input, init);
      }),
    );
    render(
      <SessionProvider>
        <DecisionReviewWorkspace decisionId={ids.decision} />
      </SessionProvider>,
    );
    fireEvent.change(screen.getByLabelText("Bearer token"), {
      target: { value: token("FINANCE") },
    });
    fireEvent.click(screen.getByRole("button", { name: "Enter workspace" }));
    await screen.findByRole("heading", {
      name: "Recover AI onboarding assistant spend",
    });
    expect(await screen.findByText("Service unavailable")).toBeInTheDocument();
    expect(
      screen.getByRole("button", { name: "Validate Result" }),
    ).toBeInTheDocument();
  });

  it("rejects a noncanonical route before making an API request", () => {
    const fetcher = vi.fn();
    vi.stubGlobal("fetch", fetcher);
    render(
      <SessionProvider>
        <DecisionReviewWorkspace decisionId="NOT-A-UUID" />
      </SessionProvider>,
    );
    fireEvent.change(screen.getByLabelText("Bearer token"), {
      target: { value: token("ADMIN") },
    });
    fireEvent.click(screen.getByRole("button", { name: "Enter workspace" }));
    expect(
      screen.getByText("Decision identifier is not canonical"),
    ).toBeInTheDocument();
    expect(fetcher).not.toHaveBeenCalled();
  });
});
