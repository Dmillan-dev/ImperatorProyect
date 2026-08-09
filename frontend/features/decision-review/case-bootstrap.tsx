"use client";

import { useEffect, useMemo, useState } from "react";
import { useRouter } from "next/navigation";
import { AlertCircle, Database, LogOut, RefreshCw } from "lucide-react";
import { ApiClient, ApiClientError } from "@/services/api/client";
import { WorkspaceApi } from "@/services/api/workspace";
import { SessionBootstrap, useSession } from "@/services/auth/session";

export function CaseBootstrap() {
  const { token, claims, clearSession } = useSession();
  if (!token) return <SessionBootstrap />;
  return (
    <AuthenticatedCaseBootstrap
      token={token}
      role={claims?.role ?? null}
      clearSession={clearSession}
    />
  );
}

function AuthenticatedCaseBootstrap({
  token,
  role,
  clearSession,
}: {
  token: string;
  role: string | null;
  clearSession: () => void;
}) {
  const router = useRouter();
  const [state, setState] = useState<
    "loading" | "empty" | "multiple" | "error"
  >("loading");
  const [error, setError] = useState<ApiClientError | null>(null);
  const api = useMemo(
    () => new WorkspaceApi(new ApiClient(token, clearSession)),
    [token, clearSession],
  );

  useEffect(() => {
    let active = true;
    api
      .listDecisions(crypto.randomUUID())
      .then((page) => {
        if (!active) return;
        const matches = page.items.filter(
          (item) => item.caseId === "DRC-AOA-001",
        );
        if (matches.length === 1)
          router.replace(`/decisions/${matches[0].decisionId}`);
        else setState(matches.length === 0 ? "empty" : "multiple");
      })
      .catch((caught) => {
        if (!active) return;
        setError(caught instanceof ApiClientError ? caught : null);
        setState("error");
      });
    return () => {
      active = false;
    };
  }, [api, router]);

  return (
    <main className="bootstrap-page">
      <header className="app-bar bootstrap-bar">
        <Brand />
        <div className="session-identity">
          {role ? (
            <span className="role-chip">{role.replaceAll("_", " ")}</span>
          ) : null}
          <button
            className="icon-button"
            type="button"
            onClick={clearSession}
            title="Clear session"
          >
            <LogOut size={17} />
            <span className="sr-only">Clear session</span>
          </button>
        </div>
      </header>
      <section className="bootstrap-state" aria-live="polite">
        {state === "loading" ? (
          <>
            <span
              className="loading-ring loading-ring-large"
              aria-hidden="true"
            />
            <p className="eyebrow">CASE RESOLUTION</p>
            <h1>Locating DRC-AOA-001</h1>
            <p>Reading the certified Decision index.</p>
          </>
        ) : null}
        {state === "empty" ? (
          <>
            <Database size={28} aria-hidden="true" />
            <p className="eyebrow">NO CASE</p>
            <h1>DRC-AOA-001 is not available</h1>
            <p>
              Import and process the authorized Evidence pack before opening the
              workspace.
            </p>
          </>
        ) : null}
        {state === "multiple" ? (
          <>
            <AlertCircle size={28} aria-hidden="true" />
            <p className="eyebrow">DATA CONSISTENCY BLOCKER</p>
            <h1>Multiple DRC-AOA-001 decisions found</h1>
            <p>
              No Decision was selected. Resolve the authoritative data conflict
              first.
            </p>
          </>
        ) : null}
        {state === "error" ? (
          <>
            <AlertCircle size={28} aria-hidden="true" />
            <p className="eyebrow">SERVICE UNAVAILABLE</p>
            <h1>The Decision index could not be read</h1>
            <p>
              {error?.message ?? "The server returned an unexpected response."}
            </p>
            {error ? <code>{error.correlationId}</code> : null}
            <button
              className="button button-secondary"
              type="button"
              onClick={() => window.location.reload()}
            >
              <RefreshCw size={16} /> Retry
            </button>
          </>
        ) : null}
      </section>
    </main>
  );
}

export function Brand() {
  return (
    <div className="brand" aria-label="IMPERATOR">
      <span className="brand-mark" aria-hidden="true">
        I
      </span>
      <span>
        <strong>IMPERATOR</strong>
        <small>Decision intelligence</small>
      </span>
    </div>
  );
}
