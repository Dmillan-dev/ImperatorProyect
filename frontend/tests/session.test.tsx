import { act, fireEvent, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import {
  SessionBootstrap,
  SessionProvider,
  decodePresentationClaims,
  useSession,
} from "@/services/auth/session";
import { token } from "./fixtures";

function Probe() {
  const session = useSession();
  return (
    <div>
      {session.token ? (
        `${session.claims?.role ?? "NO_ROLE"}:${session.token}`
      ) : (
        <SessionBootstrap />
      )}
    </div>
  );
}

function ExpiryProbe() {
  const session = useSession();
  return (
    <>
      <button
        type="button"
        onClick={() => session.startSession(token("ADMIN", -1))}
      >
        Start expired
      </button>
      <span>{session.token ? "ACTIVE" : "CLEARED"}</span>
    </>
  );
}

describe("volatile JWT session", () => {
  it("strips Bearer, decodes presentation claims and never uses persistent storage", async () => {
    const localSpy = vi.spyOn(Storage.prototype, "setItem");
    render(
      <SessionProvider>
        <Probe />
      </SessionProvider>,
    );
    await userEvent.type(
      screen.getByLabelText("Bearer token"),
      `Bearer ${token("FINANCE")}`,
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Enter workspace" }),
    );
    expect(screen.getByText(/FINANCE:/)).toBeInTheDocument();
    expect(localSpy).not.toHaveBeenCalled();
  });

  it("keeps malformed claims non-authoritative", () => {
    expect(decodePresentationClaims("invalid.token")).toBeNull();
    expect(decodePresentationClaims(token("UNKNOWN"))).toBeNull();
  });

  it("rejects empty and oversized bootstrap tokens", () => {
    render(
      <SessionProvider>
        <Probe />
      </SessionProvider>,
    );
    fireEvent.submit(
      screen.getByRole("button", { name: "Enter workspace" }).closest("form")!,
    );
    expect(screen.getByRole("alert")).toHaveTextContent("required");
    fireEvent.change(screen.getByLabelText("Bearer token"), {
      target: { value: "x".repeat(16 * 1024 + 1) },
    });
    fireEvent.submit(
      screen.getByRole("button", { name: "Enter workspace" }).closest("form")!,
    );
    expect(screen.getByRole("alert")).toHaveTextContent("16 KiB");
  });

  it("clears an expired session", async () => {
    vi.useFakeTimers();
    render(
      <SessionProvider>
        <ExpiryProbe />
      </SessionProvider>,
    );
    fireEvent.click(screen.getByRole("button", { name: "Start expired" }));
    expect(screen.getByText("ACTIVE")).toBeInTheDocument();
    await act(async () => vi.runAllTimers());
    expect(screen.getByText("CLEARED")).toBeInTheDocument();
    vi.useRealTimers();
  });
});
