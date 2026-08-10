import { afterEach, describe, expect, it, vi } from "vitest";

const originalApiOrigin = process.env.IMPERATOR_API_ORIGIN;
const originalRuntimeProfile = process.env.IMPERATOR_RUNTIME_PROFILE;

async function loadConfig(apiOrigin: string, runtimeProfile?: string) {
  process.env.IMPERATOR_API_ORIGIN = apiOrigin;
  if (runtimeProfile) {
    process.env.IMPERATOR_RUNTIME_PROFILE = runtimeProfile;
  } else {
    delete process.env.IMPERATOR_RUNTIME_PROFILE;
  }
  vi.resetModules();
  return (await import("../next.config")).default;
}

afterEach(() => {
  if (originalApiOrigin === undefined) {
    delete process.env.IMPERATOR_API_ORIGIN;
  } else {
    process.env.IMPERATOR_API_ORIGIN = originalApiOrigin;
  }
  if (originalRuntimeProfile === undefined) {
    delete process.env.IMPERATOR_RUNTIME_PROFILE;
  } else {
    process.env.IMPERATOR_RUNTIME_PROFILE = originalRuntimeProfile;
  }
  vi.resetModules();
});

describe("Docker runtime Next.js configuration", () => {
  it("accepts only the frozen internal Compose backend over HTTP", async () => {
    const config = await loadConfig("http://backend:8080", "docker-compose");

    await expect(config.rewrites?.()).resolves.toEqual([
      {
        source: "/api/v1/:path*",
        destination: "http://backend:8080/api/v1/:path*",
      },
    ]);
    expect(config.output).toBe("standalone");
  });

  it.each([
    ["http://backend:8080", undefined],
    ["http://backend:8081", "docker-compose"],
    ["http://database:8080", "docker-compose"],
    ["http://10.0.0.10:8080", "docker-compose"],
  ])(
    "rejects non-authorized internal HTTP origin %s",
    async (origin, profile) => {
      await expect(loadConfig(origin, profile)).rejects.toThrow(
        "IMPERATOR_API_ORIGIN must use HTTPS outside local loopback",
      );
    },
  );

  it("freezes the production-like browser security headers", async () => {
    const config = await loadConfig("http://backend:8080", "docker-compose");
    const rules = await config.headers?.();
    const headers = Object.fromEntries(
      (rules?.[0]?.headers ?? []).map(({ key, value }) => [key, value]),
    );

    expect(headers).toMatchObject({
      "Cross-Origin-Opener-Policy": "same-origin",
      "Permissions-Policy": "camera=(), microphone=(), geolocation=()",
      "Referrer-Policy": "no-referrer",
      "X-Content-Type-Options": "nosniff",
      "X-Frame-Options": "DENY",
    });
    expect(headers["Content-Security-Policy"]).toContain(
      "frame-ancestors 'none'",
    );
  });
});
