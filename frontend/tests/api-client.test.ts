import { describe, expect, it, vi } from "vitest";
import { z } from "zod";
import { ApiClient, ApiClientError } from "@/services/api/client";
import { ids, json } from "./fixtures";

describe("ApiClient", () => {
  it("sends auth, correlation and idempotency without caching", async () => {
    const fetcher = vi.fn().mockResolvedValue(json({ ok: true }));
    const client = new ApiClient("secret-token", vi.fn(), fetcher);
    await client.request(
      "/api/v1/test",
      z.object({ ok: z.boolean() }).strict(),
      {
        method: "POST",
        body: { value: 1 },
        correlationId: ids.decision,
        idempotencyKey: ids.ledger,
      },
    );
    const [, request] = fetcher.mock.calls[0];
    expect(request.cache).toBe("no-store");
    expect(request.headers.get("Authorization")).toBe("Bearer secret-token");
    expect(request.headers.get("X-Correlation-ID")).toBe(ids.decision);
    expect(request.headers.get("Idempotency-Key")).toBe(ids.ledger);
  });

  it("fails closed on an invalid success response", async () => {
    const client = new ApiClient(
      "token",
      vi.fn(),
      vi.fn().mockResolvedValue(json({ extra: true })),
    );
    await expect(
      client.request("/api/v1/test", z.object({ ok: z.boolean() }).strict()),
    ).rejects.toMatchObject({
      code: "RESPONSE_CONTRACT_INVALID",
    });
  });

  it("clears the session on 401 and preserves the safe envelope", async () => {
    const clear = vi.fn();
    const response = json(
      {
        code: "INVALID_TOKEN",
        message: "Invalid token",
        correlationId: ids.decision,
        details: {},
      },
      401,
    );
    const client = new ApiClient(
      "token",
      clear,
      vi.fn().mockResolvedValue(response),
    );
    await expect(client.request("/api/v1/test", z.object({}))).rejects.toEqual(
      expect.objectContaining({
        status: 401,
        code: "INVALID_TOKEN",
        correlationId: ids.decision,
      }),
    );
    expect(clear).toHaveBeenCalledOnce();
  });

  it("rejects oversized and malformed error responses", async () => {
    const oversized = new Response("{}", {
      status: 200,
      headers: { "content-length": String(1024 * 1024 + 1) },
    });
    const client = new ApiClient(
      "token",
      vi.fn(),
      vi.fn().mockResolvedValue(oversized),
    );
    await expect(
      client.request("/api/v1/test", z.object({})),
    ).rejects.toBeInstanceOf(ApiClientError);

    const malformed = new Response("not-json", { status: 500 });
    const second = new ApiClient(
      "token",
      vi.fn(),
      vi.fn().mockResolvedValue(malformed),
    );
    await expect(
      second.request("/api/v1/test", z.object({})),
    ).rejects.toMatchObject({
      code: "RESPONSE_CONTRACT_INVALID",
    });
  });

  it("maps network, timeout and actual body-size failures", async () => {
    const network = new ApiClient(
      "token",
      vi.fn(),
      vi.fn().mockRejectedValue(new Error("offline")),
    );
    await expect(
      network.request("/api/v1/test", z.object({})),
    ).rejects.toMatchObject({
      code: "NETWORK_UNAVAILABLE",
    });

    const body = JSON.stringify({ value: "x".repeat(1024 * 1024) });
    const large = new ApiClient(
      "token",
      vi.fn(),
      vi.fn().mockResolvedValue(new Response(body)),
    );
    await expect(
      large.request("/api/v1/test", z.object({})),
    ).rejects.toMatchObject({
      code: "RESPONSE_TOO_LARGE",
    });

    vi.useFakeTimers();
    const timeoutFetch = vi.fn(
      (_input: RequestInfo | URL, init?: RequestInit) =>
        new Promise<Response>((_resolve, reject) => {
          init?.signal?.addEventListener("abort", () =>
            reject(new DOMException("Aborted", "AbortError")),
          );
        }),
    );
    const timeout = new ApiClient("token", vi.fn(), timeoutFetch);
    const request = timeout.request("/api/v1/test", z.object({}));
    const rejection = expect(request).rejects.toMatchObject({
      code: "REQUEST_TIMEOUT",
    });
    await vi.advanceTimersByTimeAsync(10_001);
    await rejection;
    vi.useRealTimers();
  });
});
