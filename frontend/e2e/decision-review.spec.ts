import { expect, test, type Page, type Route } from "@playwright/test";
import {
  businessValue,
  decision,
  decisionSummary,
  evidence,
  ids,
  ledger,
  page as pageResult,
  recommendation,
  roi,
  timeline,
  token,
} from "../tests/fixtures";

type RouteMode = {
  businessNotReady?: boolean;
  redacted?: boolean;
  partialFailures?: boolean;
  unauthorizedDecision?: boolean;
};

test("renders the complete Decision Review Workspace without viewport overflow", async ({
  page,
}, testInfo) => {
  await installRoutes(page);
  await openWorkspace(page, "ADMIN");

  await expect(
    page.getByRole("heading", { name: decision.title }),
  ).toBeVisible();
  await expect(page.getByText("€19,440.00").first()).toBeVisible();
  await expect(page.getByText("€18,120.00").first()).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Approve", exact: true }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Reject", exact: true }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Defer", exact: true }),
  ).toBeVisible();

  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= window.innerWidth,
    ),
  ).toBe(true);
  expect(await page.evaluate(allInteractiveElementsFitViewport)).toBe(true);
  await page.screenshot({
    path: testInfo.outputPath(`workspace-${testInfo.project.name}.png`),
    fullPage: true,
  });

  await page.getByRole("button", { name: "Approve", exact: true }).click();
  const dialog = page.getByRole("dialog");
  await expect(dialog).toBeVisible();
  await dialog
    .getByLabel("Reason")
    .fill("Evidence reviewed by the authorized approver");
  await dialog.getByRole("button", { name: "Review command" }).click();
  await expect(dialog.getByText("Confirm authoritative change")).toBeVisible();
  await page.keyboard.press("Escape");
  await expect(dialog).toBeHidden();
});

test("keeps AUDITOR read-only, redacts Evidence and separates unrealized value", async ({
  page,
}, testInfo) => {
  await installRoutes(page, { businessNotReady: true, redacted: true });
  await openWorkspace(page, "AUDITOR");

  await expect(page.getByText("Read-only workspace")).toBeVisible();
  await expect(page.getByText("Protected Evidence")).toBeVisible();
  await expect(page.getByText("Not yet realized").first()).toBeVisible();
  await expect(page.getByText("€0.00")).toHaveCount(0);
  await expect(
    page.getByRole("button", { name: "Approve", exact: true }),
  ).toHaveCount(0);
  expect(
    await page.evaluate(
      () => document.documentElement.scrollWidth <= window.innerWidth,
    ),
  ).toBe(true);
  await page.screenshot({
    path: testInfo.outputPath(`auditor-${testInfo.project.name}.png`),
    fullPage: true,
  });
});

test("presents PLATFORM_ENGINEER and FINANCE actions exactly", async ({
  page,
}, testInfo) => {
  test.skip(
    testInfo.project.name !== "desktop",
    "One browser project proves the complete role matrix",
  );
  await installRoutes(page, { businessNotReady: true });

  await openWorkspace(page, "PLATFORM_ENGINEER");
  await expect(
    page.getByRole("button", { name: "Defer", exact: true }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Mark Implemented" }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Approve", exact: true }),
  ).toHaveCount(0);
  await page.getByRole("button", { name: "Clear session" }).click();

  await enterToken(page, "FINANCE");
  await expect(
    page.getByRole("button", { name: "Defer", exact: true }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Validate Result" }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Mark Implemented" }),
  ).toHaveCount(0);
});

test("contains secondary 403, 404, malformed, empty and timeout failures", async ({
  page,
}, testInfo) => {
  test.skip(
    testInfo.project.name !== "desktop",
    "The error matrix is viewport-independent",
  );
  await installRoutes(page, { partialFailures: true });
  await openWorkspace(page, "FINANCE");

  await expect(page.getByText("Access denied")).toBeVisible();
  await expect(page.getByText("Resource not found")).toBeVisible();
  await expect(
    page.getByText("The server response did not match the certified contract"),
  ).toBeVisible();
  await expect(
    page.getByText("No timeline facts are available."),
  ).toBeVisible();
  await expect(
    page.getByText("No governance fact has been appended yet."),
  ).toBeVisible();
  await expect(page.getByText("The request timed out")).toBeVisible({
    timeout: 15_000,
  });
  await expect(
    page.getByRole("heading", { name: decision.title }),
  ).toBeVisible();
});

test("clears the volatile session on a 401 Decision response", async ({
  page,
}, testInfo) => {
  test.skip(
    testInfo.project.name !== "desktop",
    "Authentication behavior is viewport-independent",
  );
  await installRoutes(page, { unauthorizedDecision: true });
  await page.goto(`/decisions/${ids.decision}`);
  await enterToken(page, "ADMIN");
  await expect(
    page.getByRole("heading", { name: "Open decision workspace" }),
  ).toBeVisible();
});

async function openWorkspace(page: Page, role: string) {
  await page.goto(`/decisions/${ids.decision}`);
  await enterToken(page, role);
  await expect(
    page.getByRole("heading", { name: decision.title }),
  ).toBeVisible();
}

async function enterToken(page: Page, role: string) {
  await page.getByLabel("Bearer token").fill(token(role));
  await page.getByRole("button", { name: "Enter workspace" }).click();
}

async function installRoutes(page: Page, mode: RouteMode = {}) {
  await page.route(/\/api\/v1\//, async (route) => handleRoute(route, mode));
}

async function handleRoute(route: Route, mode: RouteMode) {
  const request = route.request();
  const url = new URL(request.url());
  const path = url.pathname;

  if (request.method() === "POST") {
    const isReview = ["/approve", "/reject", "/defer"].some((suffix) =>
      path.endsWith(suffix),
    );
    return fulfill(
      route,
      isReview
        ? {
            ledgerEntryId: ids.ledger,
            decisionId: ids.decision,
            recommendationId: ids.recommendation,
            status: "APPROVED",
            reviewerId: ids.actor,
            reviewerRole: "ADMIN",
            reviewReason: "Authorized review",
            replayed: false,
          }
        : {
            ledgerEntryId: ids.ledger,
            decisionId: ids.decision,
            recommendationId: ids.recommendation,
            entryType: path.endsWith("/validate-result")
              ? "RESULT_VALIDATED"
              : "IMPLEMENTATION_MARKED",
            occurredAt: "2026-08-02T10:00:00Z",
            evidenceSnapshotCount: 1,
            realizedSaving: null,
            replayed: false,
          },
      201,
    );
  }

  if (path === `/api/v1/decisions/${ids.decision}`) {
    if (mode.unauthorizedDecision)
      return error(route, 401, "INVALID_TOKEN", "Invalid authentication token");
    return fulfill(route, decision);
  }
  if (path === "/api/v1/decisions")
    return fulfill(route, pageResult([decisionSummary]));
  if (path.endsWith("/timeline"))
    return fulfill(route, pageResult(mode.partialFailures ? [] : [timeline]));
  if (path.endsWith("/evidence")) {
    if (mode.partialFailures)
      return error(route, 403, "ACCESS_DENIED", "Access denied");
    const item = mode.redacted
      ? {
          ...evidence,
          sourceObjectRef: "[REDACTED]",
          entity: "[REDACTED]",
          actor: "[REDACTED]",
          observedFact: "[REDACTED]",
          businessMeaning: "[REDACTED]",
          correlationKey: "[REDACTED]",
        }
      : evidence;
    return fulfill(route, pageResult([item]));
  }
  if (path.endsWith("/roi"))
    return fulfill(route, mode.partialFailures ? { broken: true } : roi);
  if (path.startsWith("/api/v1/recommendations/")) {
    if (mode.partialFailures)
      return error(route, 404, "RESOURCE_NOT_FOUND", "Resource not found");
    return fulfill(route, recommendation);
  }
  if (path.endsWith("/ledger"))
    return fulfill(route, pageResult(mode.partialFailures ? [] : [ledger]));
  if (path === "/api/v1/business-value") {
    if (mode.partialFailures) {
      await new Promise((resolve) => setTimeout(resolve, 10_500));
      try {
        return await fulfill(route, businessValue);
      } catch {
        return;
      }
    }
    if (mode.businessNotReady)
      return error(
        route,
        409,
        "BUSINESS_VALUE_NOT_READY",
        "Business Value is not ready",
      );
    return fulfill(route, businessValue);
  }
  return error(route, 404, "RESOURCE_NOT_FOUND", "Resource not found");
}

function fulfill(route: Route, body: unknown, status = 200) {
  return route.fulfill({
    status,
    contentType: "application/json",
    body: JSON.stringify(body),
  });
}

function error(route: Route, status: number, code: string, message: string) {
  return fulfill(
    route,
    { code, message, correlationId: ids.decision, details: {} },
    status,
  );
}

function allInteractiveElementsFitViewport() {
  return [
    ...document.querySelectorAll<HTMLElement>("button, input, textarea, a"),
  ].every((element) => {
    const box = element.getBoundingClientRect();
    return (
      box.left >= 0 &&
      box.right <= window.innerWidth &&
      box.width > 0 &&
      box.height > 0
    );
  });
}
