import { spawn } from "node:child_process";
import { fileURLToPath } from "node:url";
import { dirname, resolve } from "node:path";
import { chromium } from "../../frontend/node_modules/playwright/index.mjs";
import {
  businessValue,
  decision,
  evidence,
  ids,
  ledger,
  page as pageData,
  recommendation,
  roi,
  timeline,
  token,
} from "../../frontend/tests/fixtures.ts";

const here = dirname(fileURLToPath(import.meta.url));
const root = resolve(here, "../..");
const frontend = resolve(root, "frontend");
const output = resolve(root, "tmp/commercial/linkedin-discovery-kit/workspace-production-auditor.png");
const nextBin = resolve(frontend, "node_modules/next/dist/bin/next");
const url = "http://127.0.0.1:3101";

const server = spawn(process.execPath, [nextBin, "start", "--hostname", "127.0.0.1", "--port", "3101"], {
  cwd: frontend,
  env: { ...process.env, IMPERATOR_API_ORIGIN: "http://127.0.0.1:8080" },
  stdio: "ignore",
  windowsHide: true,
});

async function waitForServer() {
  for (let attempt = 0; attempt < 60; attempt += 1) {
    try {
      const response = await fetch(url);
      if (response.ok) return;
    } catch {
      // The production server is still starting.
    }
    await new Promise((resolveDelay) => setTimeout(resolveDelay, 500));
  }
  throw new Error("Next.js production server did not become ready");
}

function fulfill(route, body, status = 200) {
  return route.fulfill({
    status,
    contentType: "application/json",
    body: JSON.stringify(body),
  });
}

function error(route, status, code, message) {
  return fulfill(route, { code, message, correlationId: ids.decision, details: {} }, status);
}

async function handleRoute(route) {
  const request = route.request();
  const path = new URL(request.url()).pathname;

  if (request.method() === "POST") {
    return error(route, 403, "ACCESS_DENIED", "Auditor is read only");
  }
  if (path === `/api/v1/decisions/${ids.decision}`) return fulfill(route, decision);
  if (path === "/api/v1/decisions") return fulfill(route, pageData([decision]));
  if (path.endsWith("/timeline")) return fulfill(route, pageData([timeline]));
  if (path.endsWith("/evidence")) {
    return fulfill(
      route,
      pageData([
        {
          ...evidence,
          sourceObjectRef: "[REDACTED]",
          entity: "[REDACTED]",
          actor: "[REDACTED]",
          observedFact: "[REDACTED]",
          businessMeaning: "[REDACTED]",
          correlationKey: "[REDACTED]",
        },
      ]),
    );
  }
  if (path.endsWith("/roi")) return fulfill(route, roi);
  if (path.startsWith("/api/v1/recommendations/")) return fulfill(route, recommendation);
  if (path.endsWith("/ledger")) return fulfill(route, pageData([ledger]));
  if (path === "/api/v1/business-value") {
    return error(route, 409, "BUSINESS_VALUE_NOT_READY", "Business Value is not ready");
  }
  return fulfill(route, businessValue);
}

let browser;
try {
  await waitForServer();
  browser = await chromium.launch({ headless: true });
  const page = await browser.newPage({ viewport: { width: 1440, height: 900 }, deviceScaleFactor: 1 });
  await page.route(/\/api\/v1\//, handleRoute);
  await page.goto(`${url}/decisions/${ids.decision}`, { waitUntil: "networkidle" });
  await page.getByLabel("Bearer token").fill(token("AUDITOR"));
  await page.getByRole("button", { name: "Enter workspace" }).click();
  await page.getByRole("heading", { name: decision.title }).waitFor();
  await page.getByText("Read-only workspace").waitFor();
  await page.getByText("Protected Evidence").waitFor();
  await page.screenshot({ path: output, fullPage: true });
  console.log(output);
} finally {
  if (browser) await browser.close();
  server.kill();
}
