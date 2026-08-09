"use client";

import { Check, Clipboard, RefreshCw } from "lucide-react";
import { useState, type ReactNode } from "react";
import type { ApiClientError } from "@/services/api/client";

export function CopyId({
  value,
  label = "identifier",
}: {
  value: string;
  label?: string;
}) {
  const [copied, setCopied] = useState(false);

  async function copy() {
    await navigator.clipboard.writeText(value);
    setCopied(true);
    window.setTimeout(() => setCopied(false), 1400);
  }

  return (
    <span className="copy-id">
      <code title={value}>{abbreviateId(value)}</code>
      <button
        type="button"
        className="icon-button"
        onClick={copy}
        title={`Copy ${label}`}
      >
        {copied ? <Check size={14} /> : <Clipboard size={14} />}
        <span className="sr-only">Copy {label}</span>
      </button>
    </span>
  );
}

export function StatusPill({ value }: { value: string }) {
  const normalized = value.toLowerCase().replaceAll("_", "-");
  return (
    <span className={`status-pill status-${normalized}`}>
      {humanize(value)}
    </span>
  );
}

export function SectionShell({
  title,
  eyebrow,
  icon,
  actions,
  children,
  className = "",
}: {
  title: string;
  eyebrow: string;
  icon: ReactNode;
  actions?: ReactNode;
  children: ReactNode;
  className?: string;
}) {
  return (
    <section
      className={`section-shell ${className}`.trim()}
      aria-labelledby={`${slug(title)}-title`}
    >
      <header className="section-heading">
        <div className="section-heading-main">
          <span className="section-icon" aria-hidden="true">
            {icon}
          </span>
          <div>
            <p className="section-eyebrow">{eyebrow}</p>
            <h2 id={`${slug(title)}-title`}>{title}</h2>
          </div>
        </div>
        {actions}
      </header>
      {children}
    </section>
  );
}

export function LoadingBlock({ label }: { label: string }) {
  return (
    <div className="state-block" role="status">
      <span className="loading-ring" aria-hidden="true" />
      <p>Loading {label.toLowerCase()}</p>
    </div>
  );
}

export function EmptyBlock({ children }: { children: ReactNode }) {
  return <div className="state-block state-empty">{children}</div>;
}

export function ErrorBlock({
  error,
  onRetry,
}: {
  error: ApiClientError;
  onRetry: () => void;
}) {
  return (
    <div className="state-block state-error" role="alert">
      <div>
        <strong>
          {error.code === "REQUEST_TIMEOUT"
            ? "Request timed out"
            : "Data unavailable"}
        </strong>
        <p>{error.message}</p>
        <code>{error.correlationId}</code>
      </div>
      <button
        type="button"
        className="button button-secondary"
        onClick={onRetry}
      >
        <RefreshCw size={16} /> Retry
      </button>
    </div>
  );
}

export function PageControls({
  page,
  totalPages,
  onPage,
}: {
  page: number;
  totalPages: number;
  onPage: (page: number) => void;
}) {
  if (totalPages <= 1) return null;
  return (
    <nav className="page-controls" aria-label="Section pagination">
      <button
        type="button"
        disabled={page === 0}
        onClick={() => onPage(page - 1)}
      >
        Previous
      </button>
      <span>
        Page {page + 1} of {totalPages}
      </span>
      <button
        type="button"
        disabled={page + 1 >= totalPages}
        onClick={() => onPage(page + 1)}
      >
        Next
      </button>
    </nav>
  );
}

export function abbreviateId(value: string) {
  return value.length <= 13
    ? value
    : `${value.slice(0, 8)}...${value.slice(-4)}`;
}

export function humanize(value: string) {
  return value
    .toLowerCase()
    .replaceAll("_", " ")
    .replaceAll("-", " ")
    .replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function slug(value: string) {
  return value.toLowerCase().replace(/[^a-z0-9]+/g, "-");
}
