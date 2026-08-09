"use client";

import { jwtDecode } from "jwt-decode";
import {
  createContext,
  type FormEvent,
  type ReactNode,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useState,
} from "react";
import { KeyRound, ShieldCheck } from "lucide-react";
import { z } from "zod";

export const roles = [
  "ADMIN",
  "PLATFORM_ENGINEER",
  "FINANCE",
  "AUDITOR",
] as const;
export type Role = (typeof roles)[number];

const presentationClaimsSchema = z
  .object({
    sub: z.string().uuid(),
    imperator_role: z.enum(roles),
    exp: z.number().int().positive(),
  })
  .passthrough();

export type PresentationClaims = {
  actorId: string;
  role: Role;
  expiresAt: number;
};

export function decodePresentationClaims(
  token: string,
): PresentationClaims | null {
  try {
    const parsed = presentationClaimsSchema.safeParse(jwtDecode(token));
    if (!parsed.success) return null;
    return {
      actorId: parsed.data.sub,
      role: parsed.data.imperator_role,
      expiresAt: parsed.data.exp * 1000,
    };
  } catch {
    return null;
  }
}

type SessionContextValue = {
  token: string | null;
  claims: PresentationClaims | null;
  startSession: (rawToken: string) => void;
  clearSession: () => void;
};

const SessionContext = createContext<SessionContextValue | null>(null);

export function SessionProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);

  const clearSession = useCallback(() => setToken(null), []);
  const startSession = useCallback((rawToken: string) => {
    const normalized = rawToken.trim().replace(/^Bearer\s+/i, "");
    if (!normalized) throw new Error("A bearer token is required");
    if (normalized.length > 16 * 1024)
      throw new Error("Token exceeds the 16 KiB limit");
    setToken(normalized);
  }, []);

  const claims = useMemo(
    () => (token ? decodePresentationClaims(token) : null),
    [token],
  );

  useEffect(() => {
    if (!claims) return;
    const remaining = claims.expiresAt - Date.now();
    const timer = window.setTimeout(
      clearSession,
      Math.max(0, Math.min(remaining, 2_147_000_000)),
    );
    return () => window.clearTimeout(timer);
  }, [claims, clearSession]);

  const value = useMemo(
    () => ({ token, claims, startSession, clearSession }),
    [token, claims, startSession, clearSession],
  );

  return (
    <SessionContext.Provider value={value}>{children}</SessionContext.Provider>
  );
}

export function useSession() {
  const value = useContext(SessionContext);
  if (!value) throw new Error("useSession must be used inside SessionProvider");
  return value;
}

export function SessionBootstrap() {
  const { startSession } = useSession();
  const [error, setError] = useState<string | null>(null);

  function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const form = new FormData(event.currentTarget);
    try {
      startSession(String(form.get("token") ?? ""));
      setError(null);
    } catch (caught) {
      setError(
        caught instanceof Error
          ? caught.message
          : "Token could not be accepted",
      );
    }
  }

  return (
    <main className="session-shell">
      <section className="session-panel" aria-labelledby="session-title">
        <div className="session-mark" aria-hidden="true">
          <ShieldCheck size={28} />
        </div>
        <p className="eyebrow">IMPERATOR / CONTROLLED DEMO</p>
        <h1 id="session-title">Open decision workspace</h1>
        <p className="session-copy">
          Use a short-lived bearer token issued for the local environment. It is
          held in memory for this tab only.
        </p>
        <form className="session-form" onSubmit={submit}>
          <label htmlFor="token">Bearer token</label>
          <div className="token-input-wrap">
            <KeyRound size={17} aria-hidden="true" />
            <input
              id="token"
              name="token"
              type="password"
              autoComplete="off"
              maxLength={16 * 1024}
              required
              aria-describedby={error ? "token-error" : "token-hint"}
            />
          </div>
          <p id="token-hint" className="field-hint">
            The token is never persisted or included in the URL.
          </p>
          {error ? (
            <p id="token-error" className="field-error" role="alert">
              {error}
            </p>
          ) : null}
          <button
            className="button button-primary session-submit"
            type="submit"
          >
            Enter workspace
          </button>
        </form>
      </section>
    </main>
  );
}
