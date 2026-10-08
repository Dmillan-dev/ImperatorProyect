import { chromium } from "../../frontend/node_modules/playwright/index.mjs";
import assert from "node:assert/strict";
import { readFile, mkdir, writeFile } from "node:fs/promises";
import { resolve, dirname } from "node:path";
import { fileURLToPath } from "node:url";
import { randomUUID } from "node:crypto";

// Credentials arrive through stdin and are never written to the report or URL.
const root = resolve(dirname(fileURLToPath(import.meta.url)), "../..");
let input = "";
for await (const chunk of process.stdin) input += chunk;
const config = JSON.parse(input);
input = "";
const origin = new URL(config.origin);
assert(["127.0.0.1", "localhost"].includes(origin.hostname));
assert.equal(origin.protocol, "http:");
const out = resolve(root, config.outputDirectory);
assert(
  out.startsWith(resolve(root, "build") + "/") ||
    out.startsWith(resolve(root, "build") + "\\"),
);
await mkdir(out, { recursive: true });
const decisionId = config.decisionId;
const path = `/api/v1/decisions/${decisionId}`;
const roles = config.tokens;
const report = {
  executionClass: "EXECUTED OFFLINE",
  syntheticOnly: true,
  apiInterception: false,
  steps: [],
  environment:
    "Real Docker frontend/backend/PostgreSQL; local HTTPS/JWKS fixture",
  limitations:
    "Approval uses the UI. Implementation/result facts use authenticated API commands, followed by unmocked UI reads. Not external Keycloak or AWS acceptance.",
};
async function api(
  name,
  route,
  role,
  method = "GET",
  body,
  contentType = "application/json",
  expected = 200,
) {
  const headers = {
    "Content-Type": contentType,
    "X-Correlation-Id": randomUUID(),
  };
  if (role) headers.Authorization = `Bearer ${roles[role]}`;
  if (method === "POST") headers["Idempotency-Key"] = randomUUID();
  const response = await fetch(new URL(route, origin), {
    method,
    headers,
    body:
      body === undefined
        ? undefined
        : typeof body === "string"
          ? body
          : JSON.stringify(body),
  });
  const result = await response.json();
  report.steps.push({ name, status: response.status, expected, result });
  await writeFile(
    resolve(out, "rehearsal.json"),
    JSON.stringify(report, null, 2) + "\n",
  );
  assert.equal(response.status, expected, `${name}: ${JSON.stringify(result)}`);
  return result;
}
await api(
  "Unauthenticated read denied",
  "/api/v1/decisions",
  null,
  "GET",
  undefined,
  undefined,
  401,
);
const fixture = await readFile(
  resolve(
    root,
    "src/test/resources/evidence/drc-aoa-001-business-value-demo.jsonl",
  ),
  "utf8",
);
const imported = await api(
  "Import canonical 30-record synthetic pack",
  "/api/v1/evidence/import",
  "ADMIN",
  "POST",
  fixture,
  "application/x-ndjson",
);
assert(
  imported.accepted === 30 ||
    (imported.accepted === 0 && imported.rejected === 30),
);
const composition = {
  caseId: "DRC-AOA-001",
  decisionId,
  recommendationId: config.recommendationId,
  originatingEvidenceId: "00000000-0000-4000-8000-000000000201",
  evidenceIds: Array.from(
    { length: 28 },
    (_, i) => `00000000-0000-4000-8000-${String(201 + i).padStart(12, "0")}`,
  ),
  title: "Optimize AI onboarding assistant cost",
  businessNeed:
    "Reduce recurring AI expenditure without losing exception-handling quality",
  ownerId: config.actors.PLATFORM_ENGINEER,
  requiredApproverId: config.actors.ADMIN,
  decisionCreatedAt: "2026-07-01T09:00:00Z",
  recommendationGeneratedAt: "2026-07-01T10:00:00Z",
};
const composed = await api(
  "Compose existing canonical case",
  "/api/v1/decisions",
  "ADMIN",
  "POST",
  composition,
  undefined,
  config.replay ? 200 : 201,
);
assert.equal(composed.evidenceCount, 28);
assert.equal(composed.estimatedAnnualizedSavings.amount, "19440.00");
assert.equal(
  (
    await api(
      "Composition replay is idempotent",
      "/api/v1/decisions",
      "ADMIN",
      "POST",
      composition,
    )
  ).replayed,
  true,
);
await api(
  "Auditor cannot approve",
  path + "/ledger/approve",
  "AUDITOR",
  "POST",
  { reviewedAt: "2026-07-01T11:00:00Z", reason: "Must be denied" },
  undefined,
  403,
);
assert.equal(
  (
    await api(
      "Denied command appended no Ledger entry",
      path + "/ledger",
      "ADMIN",
    )
  ).totalItems,
  0,
);
await api(
  "Value is not realized before governance",
  `/api/v1/business-value?decisionId=${decisionId}`,
  "ADMIN",
  "GET",
  undefined,
  undefined,
  409,
);

const browser = await chromium.launch({
  channel: process.env.TFG_BROWSER_CHANNEL || "chrome",
  headless: true,
  slowMo: 120,
});
const context = await browser.newContext({
  viewport: { width: 1440, height: 900 },
  timezoneId: "UTC",
  recordVideo: { dir: out, size: { width: 1440, height: 900 } },
});
const page = await context.newPage();
async function enter(role) {
  if (await page.getByRole("button", { name: "Clear session" }).isVisible())
    await page.getByRole("button", { name: "Clear session" }).click();
  await page.getByLabel("Bearer token").fill(roles[role]);
  await page.getByRole("button", { name: "Enter workspace" }).click();
  await page
    .getByRole("heading", { name: composition.title, exact: true, level: 1 })
    .waitFor();
  await page.waitForLoadState("networkidle");
}
async function capture(name, text, section) {
  await page
    .getByRole("heading", { name: composition.title, exact: true, level: 1 })
    .waitFor();
  await page.waitForFunction(
    () =>
      !/Loading (authoritative decision|ROI|Recommendation|timeline|evidence|Ledger|Business Value)/i.test(
        document.body.innerText,
      ),
  );
  await page.evaluate((value) => {
    let node = document.getElementById("tfg-recording-caption");
    if (!node) {
      node = document.createElement("div");
      node.id = "tfg-recording-caption";
      node.style.cssText =
        "position:fixed;bottom:8px;left:12px;z-index:9999;background:#111827;color:white;padding:10px 16px;border-radius:8px;font:14px sans-serif;pointer-events:none";
      document.body.appendChild(node);
    }
    node.textContent = "TFG-A / SYNTHETIC LOCAL DOCKER DEMO / " + value;
  }, text);
  if (section) {
    await page
      .getByRole("heading", { name: section, exact: true })
      .evaluate((heading) => {
        window.scrollTo({
          top: heading.getBoundingClientRect().top + window.scrollY - 100,
          behavior: "instant",
        });
      });
  } else {
    await page.evaluate(() => window.scrollTo({ top: 0, behavior: "instant" }));
  }
  await page.waitForTimeout(500);
  await page.screenshot({ path: resolve(out, name + ".png") });
  await page.waitForTimeout(2500);
}
try {
  await page.goto(new URL(`/decisions/${decisionId}`, origin).href);
  await enter("ADMIN");
  await capture(
    "01-deterministic-recommendation",
    "30 imported records; 28 Recommendation records; estimated EUR 19,440",
  );
  await page.getByRole("button", { name: "Approve", exact: true }).click();
  const dialog = page.getByRole("dialog");
  await dialog.getByLabel("Occurrence time").fill("2026-07-01T11:00");
  await dialog
    .getByLabel("Reason", { exact: true })
    .fill("Authorized synthetic approval for the TFG rehearsal");
  await dialog.getByRole("button", { name: "Review command" }).click();
  const responsePromise = page.waitForResponse(
    (response) =>
      response.url().endsWith("/ledger/approve") &&
      response.request().method() === "POST",
  );
  await dialog.getByRole("button", { name: /Confirm Approve/i }).click();
  const response = await responsePromise;
  assert.equal(response.status(), 201, await response.text());
  const approval = await response.json();
  report.steps.push({
    name: "Human ADMIN approval through real UI",
    status: 201,
    result: approval,
  });
  await dialog.waitFor({ state: "hidden" });
  await capture(
    "02-human-approval",
    "ADMIN approves in the UI; approval is appended to the Ledger",
    "Decision Ledger",
  );

  await enter("PLATFORM_ENGINEER");
  const implementation = await api(
    "Platform engineer records implementation",
    path + "/ledger/mark-implemented",
    "PLATFORM_ENGINEER",
    "POST",
    {
      occurredAt: "2026-07-15T10:30:00Z",
      reason: "Canonical synthetic model-routing deployment",
      evidenceIds: ["00000000-0000-4000-8000-000000000229"],
      expectedPreviousEntryId: approval.ledgerEntryId,
      period: "2026-07",
    },
    undefined,
    201,
  );
  await page.getByRole("button", { name: "Refresh", exact: true }).click();
  await page.waitForLoadState("networkidle");
  await capture(
    "03-implementation",
    "PLATFORM_ENGINEER API command; unmocked UI shows the implementation fact",
    "Decision Ledger",
  );
  await enter("FINANCE");
  await api(
    "Finance validates the result",
    path + "/ledger/validate-result",
    "FINANCE",
    "POST",
    {
      occurredAt: "2026-08-01T00:00:00Z",
      reason: "Canonical synthetic financial validation",
      evidenceIds: ["00000000-0000-4000-8000-000000000230"],
      expectedPreviousEntryId: implementation.ledgerEntryId,
      period: "2026-07",
      annualizedBaselineCost: { amount: "28080.00", currency: "EUR" },
      annualizedPostActionCost: { amount: "9000.00", currency: "EUR" },
      actualTransitionCost: { amount: "120.00", currency: "EUR" },
    },
    undefined,
    201,
  );
  await page.getByRole("button", { name: "Refresh", exact: true }).click();
  await page.waitForLoadState("networkidle");
  const value = await api(
    "Business Value derives from validated facts",
    `/api/v1/business-value?decisionId=${decisionId}`,
    "FINANCE",
  );
  assert.equal(value.realizedSavings.amount, "18960.00");
  assert.equal(value.variance, "-480.00");
  assert.equal(value.evidenceIds.length, 30);
  report.businessValue = value;
  await capture(
    "04-realized-business-value",
    "FINANCE API validation; synthetic realized EUR 18,960; variance EUR -480",
    "Business Value",
  );
  await enter("AUDITOR");
  assert.equal(
    await page.getByRole("button", { name: "Approve", exact: true }).count(),
    0,
  );
  await capture(
    "05-auditor-ledger",
    "AUDITOR is read-only; ordered approval, implementation and validation",
    "Decision Ledger",
  );
  const ledger = await api(
    "Three ordered authority-bound Ledger entries",
    path + "/ledger?sort=occurredAt&direction=ASC",
    "AUDITOR",
  );
  assert.equal(ledger.totalItems, 3);
  assert.deepEqual(
    ledger.items.map((item) => item.actorRole),
    ["ADMIN", "PLATFORM_ENGINEER", "FINANCE"],
  );
  assert.equal(ledger.items[1].previousEntryId, ledger.items[0].ledgerEntryId);
  assert.equal(ledger.items[2].previousEntryId, ledger.items[1].ledgerEntryId);
  report.result = "PASS";
  report.executedAtUtc = new Date().toISOString();
} catch (error) {
  report.result = "FAIL";
  report.error = String(error);
  throw error;
} finally {
  const video = page.video();
  await context.close();
  await video.saveAs(resolve(out, "tfg-a-docker-demo.webm"));
  await video.delete();
  await browser.close();
  await writeFile(
    resolve(out, "rehearsal.json"),
    JSON.stringify(report, null, 2) + "\n",
  );
}
console.log("Unmocked Docker rehearsal and sanitized recording: PASS");
