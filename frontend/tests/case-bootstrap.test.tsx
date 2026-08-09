import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { beforeEach, describe, expect, it, vi } from "vitest";
import { CaseBootstrap } from "@/features/decision-review/case-bootstrap";
import { SessionProvider } from "@/services/auth/session";
import { decisionSummary, ids, json, page, token } from "./fixtures";

const replace = vi.fn();
const router = { replace };
vi.mock("next/navigation", () => ({ useRouter: () => router }));

async function authenticate() {
  fireEvent.change(screen.getByLabelText("Bearer token"), {
    target: { value: token("AUDITOR") },
  });
  fireEvent.click(screen.getByRole("button", { name: "Enter workspace" }));
}

describe("single-case bootstrap", () => {
  beforeEach(() => replace.mockReset());

  it("navigates only when exactly one DRC-AOA-001 Decision exists", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(json(page([decisionSummary]))),
    );
    render(
      <SessionProvider>
        <CaseBootstrap />
      </SessionProvider>,
    );
    await authenticate();
    await waitFor(() =>
      expect(replace).toHaveBeenCalledWith(`/decisions/${ids.decision}`),
    );
  });

  it("shows no-case and multiple-case blockers without selecting", async () => {
    const fetcher = vi
      .fn()
      .mockResolvedValueOnce(json(page([])))
      .mockResolvedValueOnce(
        json(
          page([
            decisionSummary,
            {
              ...decisionSummary,
              decisionId: "aaaaaaaa-aaaa-4aaa-8aaa-aaaaaaaaaaaa",
            },
          ]),
        ),
      );
    vi.stubGlobal("fetch", fetcher);
    const first = render(
      <SessionProvider>
        <CaseBootstrap />
      </SessionProvider>,
    );
    await authenticate();
    expect(
      await screen.findByText("DRC-AOA-001 is not available"),
    ).toBeInTheDocument();
    first.unmount();
    render(
      <SessionProvider>
        <CaseBootstrap />
      </SessionProvider>,
    );
    await authenticate();
    expect(
      await screen.findByText("Multiple DRC-AOA-001 decisions found"),
    ).toBeInTheDocument();
    expect(replace).not.toHaveBeenCalled();
  });

  it("shows a safe service error with its correlation handle", async () => {
    vi.stubGlobal(
      "fetch",
      vi.fn().mockResolvedValue(
        json(
          {
            code: "SERVICE_UNAVAILABLE",
            message: "Service unavailable",
            correlationId: ids.decision,
            details: {},
          },
          503,
        ),
      ),
    );
    render(
      <SessionProvider>
        <CaseBootstrap />
      </SessionProvider>,
    );
    await authenticate();
    expect(
      await screen.findByText("The Decision index could not be read"),
    ).toBeInTheDocument();
    expect(screen.getByText(ids.decision)).toBeInTheDocument();
  });
});
