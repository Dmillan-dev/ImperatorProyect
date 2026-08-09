import type { ZodType } from "zod";
import { errorEnvelopeSchema, type ApiErrorEnvelope } from "./schemas";

const MAX_RESPONSE_BYTES = 1024 * 1024;
const REQUEST_TIMEOUT_MS = 10_000;

export class ApiClientError extends Error {
  constructor(
    public readonly status: number,
    public readonly code: string,
    message: string,
    public readonly correlationId: string,
  ) {
    super(message);
    this.name = "ApiClientError";
  }
}

type RequestOptions = {
  method?: "GET" | "POST";
  body?: unknown;
  correlationId?: string;
  idempotencyKey?: string;
};

export class ApiClient {
  constructor(
    private readonly token: string,
    private readonly onUnauthorized: () => void,
    private readonly fetcher: typeof fetch = (input, init) =>
      fetch(input, init),
  ) {}

  async request<T>(
    path: string,
    schema: ZodType<T>,
    options: RequestOptions = {},
  ): Promise<T> {
    const correlationId = options.correlationId ?? crypto.randomUUID();
    const controller = new AbortController();
    const timeout = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);
    const headers = new Headers({
      Accept: "application/json",
      Authorization: `Bearer ${this.token}`,
      "X-Correlation-ID": correlationId,
    });
    if (options.body !== undefined)
      headers.set("Content-Type", "application/json");
    if (options.idempotencyKey)
      headers.set("Idempotency-Key", options.idempotencyKey);

    try {
      const response = await this.fetcher(path, {
        method: options.method ?? "GET",
        headers,
        body:
          options.body === undefined ? undefined : JSON.stringify(options.body),
        cache: "no-store",
        signal: controller.signal,
      });
      const declaredSize = Number(
        response.headers.get("content-length") ?? "0",
      );
      if (declaredSize > MAX_RESPONSE_BYTES) {
        throw new ApiClientError(
          0,
          "RESPONSE_TOO_LARGE",
          "Response exceeds 1 MiB",
          correlationId,
        );
      }
      const text = await response.text();
      if (new TextEncoder().encode(text).byteLength > MAX_RESPONSE_BYTES) {
        throw new ApiClientError(
          0,
          "RESPONSE_TOO_LARGE",
          "Response exceeds 1 MiB",
          correlationId,
        );
      }

      if (!response.ok) {
        const envelope = parseError(text, response.status, correlationId);
        if (response.status === 401) this.onUnauthorized();
        throw new ApiClientError(
          response.status,
          envelope.code,
          envelope.message,
          envelope.correlationId,
        );
      }

      try {
        return schema.parse(JSON.parse(text));
      } catch {
        throw new ApiClientError(
          response.status,
          "RESPONSE_CONTRACT_INVALID",
          "The server response did not match the certified contract",
          correlationId,
        );
      }
    } catch (caught) {
      if (caught instanceof ApiClientError) throw caught;
      if (caught instanceof DOMException && caught.name === "AbortError") {
        throw new ApiClientError(
          0,
          "REQUEST_TIMEOUT",
          "The request timed out",
          correlationId,
        );
      }
      throw new ApiClientError(
        0,
        "NETWORK_UNAVAILABLE",
        "The service could not be reached",
        correlationId,
      );
    } finally {
      clearTimeout(timeout);
    }
  }
}

function parseError(
  text: string,
  status: number,
  correlationId: string,
): ApiErrorEnvelope {
  try {
    return errorEnvelopeSchema.parse(JSON.parse(text));
  } catch {
    return {
      code: "RESPONSE_CONTRACT_INVALID",
      message: `The service returned an invalid error response (${status})`,
      correlationId,
      details: {},
    };
  }
}
