import { fireEvent, render, screen } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { describe, expect, it, vi } from "vitest";
import {
  ActionDialog,
  type ActionKind,
} from "@/features/decision-review/action-dialog";
import { evidence, ids } from "./fixtures";

function renderDialog(
  action: ActionKind,
  overrides: Partial<React.ComponentProps<typeof ActionDialog>> = {},
) {
  const props = {
    action,
    decisionId: ids.decision,
    evidence: [evidence],
    ledgerHead: ids.ledger,
    pending: false,
    onClose: vi.fn(),
    onSubmit: vi.fn().mockResolvedValue(undefined),
    ...overrides,
  };
  return { ...render(<ActionDialog {...props} />), props };
}

function fillReason() {
  fireEvent.change(screen.getByLabelText("Reason"), {
    target: { value: "Authorized operational review" },
  });
}

describe("ActionDialog", () => {
  it("confirms an approval, supports back and restores caller focus", async () => {
    const trigger = document.createElement("button");
    document.body.append(trigger);
    trigger.focus();
    const { props, unmount } = renderDialog("approve");
    fillReason();
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    expect(
      screen.getByText("Confirm authoritative change"),
    ).toBeInTheDocument();
    await userEvent.click(screen.getByRole("button", { name: "Back" }));
    fillReason();
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Confirm Approve" }),
    );
    expect(props.onSubmit).toHaveBeenCalledOnce();
    expect(props.onClose).toHaveBeenCalledOnce();
    unmount();
    expect(trigger).toHaveFocus();
    trigger.remove();
  });

  it("switches deferral from required Evidence to review date", async () => {
    const { props } = renderDialog("defer");
    fillReason();
    fireEvent.change(
      screen.getByLabelText("Required Evidence", { selector: "textarea" }),
      {
        target: { value: "Post-action quality report" },
      },
    );
    await userEvent.click(screen.getByRole("radio", { name: "Review date" }));
    await userEvent.click(
      screen.getByRole("radio", { name: "Required Evidence" }),
    );
    await userEvent.click(screen.getByRole("radio", { name: "Review date" }));
    fireEvent.change(
      screen.getByLabelText("Review date", { selector: "input[type=date]" }),
      {
        target: { value: "2026-09-01" },
      },
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Confirm Defer" }),
    );
    expect(props.onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({ reviewDate: "2026-09-01" }),
      expect.any(String),
    );
  });

  it("traps forward and reverse tab navigation inside the dialog", () => {
    renderDialog("approve");
    const dialog = screen.getByRole("dialog");
    const close = screen.getByRole("button", { name: "Close" });
    const review = screen.getByRole("button", { name: "Review command" });
    review.focus();
    fireEvent.keyDown(dialog, { key: "Tab" });
    expect(close).toHaveFocus();
    close.focus();
    fireEvent.keyDown(dialog, { key: "Tab", shiftKey: true });
    expect(review).toHaveFocus();
  });

  it("closes with Escape after the confirmation step changes focus", async () => {
    const { props } = renderDialog("approve");
    fillReason();
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    fireEvent.keyDown(document, { key: "Escape" });
    expect(props.onClose).toHaveBeenCalledOnce();
  });

  it("shows a local blocker when append prerequisites are absent", async () => {
    const { props } = renderDialog("mark-implemented", {
      evidence: [],
      ledgerHead: null,
    });
    expect(
      screen.getByText("No authorized Evidence is available."),
    ).toBeInTheDocument();
    fillReason();
    fireEvent.change(screen.getByLabelText("Period"), {
      target: { value: "2026-07" },
    });
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    expect(screen.getByRole("alert")).toHaveTextContent("Ledger head");
    fireEvent.keyDown(screen.getByRole("dialog"), { key: "Escape" });
    expect(props.onClose).toHaveBeenCalledOnce();
  });

  it("builds validation fields and reports a server rejection safely", async () => {
    const { props } = renderDialog("validate-result", {
      onSubmit: vi.fn().mockRejectedValue(new Error("rejected")),
    });
    fillReason();
    fireEvent.change(screen.getByLabelText("Period"), {
      target: { value: "2026-07" },
    });
    await userEvent.click(screen.getByRole("checkbox"));
    fireEvent.change(screen.getByRole("textbox", { name: /Baseline cost/ }), {
      target: { value: "28080.00" },
    });
    fireEvent.change(
      screen.getByRole("textbox", { name: /Post-action cost/ }),
      { target: { value: "8640.00" } },
    );
    fireEvent.change(screen.getByRole("textbox", { name: /Transition cost/ }), {
      target: { value: "1320.00" },
    });
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    await userEvent.click(
      screen.getByRole("button", { name: "Confirm Validate Result" }),
    );
    expect(props.onSubmit).toHaveBeenCalledWith(
      expect.objectContaining({
        evidenceIds: [ids.evidence],
        annualizedBaselineCost: { amount: "28080.00", currency: "EUR" },
      }),
      expect.any(String),
    );
    expect(screen.getByRole("alert")).toHaveTextContent("server rejected");
  });

  it("disables dismissal while a confirmed command is pending", async () => {
    const first = renderDialog("reject");
    fillReason();
    await userEvent.click(
      screen.getByRole("button", { name: "Review command" }),
    );
    first.rerender(<ActionDialog {...first.props} pending />);
    expect(
      screen.getByRole("button", { name: "Submitting..." }),
    ).toBeDisabled();
    expect(screen.getByRole("button", { name: "Close" })).toBeDisabled();
  });
});
