"use client";

import {
  Activity,
  BadgeEuro,
  BookOpenCheck,
  BrainCircuit,
  CircleGauge,
  Clock3,
  Database,
  FileCheck2,
  LogOut,
  RefreshCw,
  Scale,
  ShieldCheck,
} from "lucide-react";
import { useState } from "react";
import {
  CopyId,
  EmptyBlock,
  ErrorBlock,
  LoadingBlock,
  PageControls,
  SectionShell,
  StatusPill,
  humanize,
} from "@/components/ui/primitives";
import type {
  EvidenceSummary,
  LedgerEntry,
  Money,
  Page,
  TimelineItem,
} from "@/services/api/schemas";
import { SessionBootstrap, useSession } from "@/services/auth/session";
import { ActionDialog, type ActionKind, roleActions } from "./action-dialog";
import { Brand } from "./case-bootstrap";
import { formatMoney, formatTimestamp } from "./presenters";
import { type Loadable, useWorkspace } from "./use-workspace";

const canonicalUuid =
  /^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/;

export function DecisionReviewWorkspace({
  decisionId,
}: {
  decisionId: string;
}) {
  const { token } = useSession();
  if (!token) return <SessionBootstrap />;
  if (!canonicalUuid.test(decisionId)) return <InvalidDecisionId />;
  return <AuthenticatedWorkspace decisionId={decisionId} token={token} />;
}

function AuthenticatedWorkspace({
  decisionId,
  token,
}: {
  decisionId: string;
  token: string;
}) {
  const { claims, clearSession } = useSession();
  const { state, command, reload, loadPage, submit } = useWorkspace(
    decisionId,
    token,
    clearSession,
  );
  const [action, setAction] = useState<ActionKind | null>(null);

  if (state.decision.status === "loading") {
    return <FullPageState label="Loading authoritative decision" />;
  }
  if (state.decision.status === "error") {
    return (
      <main className="workspace-page">
        <AppBar
          role={claims?.role ?? null}
          actorId={claims?.actorId ?? null}
          clearSession={clearSession}
        />
        <div className="workspace-content">
          <ErrorBlock error={state.decision.error} onRetry={reload} />
        </div>
      </main>
    );
  }
  if (state.decision.status !== "ready") return null;

  const decision = state.decision.data;
  const authorizedActions = claims ? roleActions[claims.role] : [];
  const evidence =
    state.evidence.status === "ready" ? state.evidence.data.items : [];
  const ledger = state.ledger.status === "ready" ? state.ledger.data : null;
  const ledgerHead =
    ledger && ledger.page + 1 === ledger.totalPages
      ? (ledger.items.at(-1)?.ledgerEntryId ?? null)
      : null;

  return (
    <main className="workspace-page">
      <AppBar
        role={claims?.role ?? null}
        actorId={claims?.actorId ?? null}
        clearSession={clearSession}
      />
      <div className="workspace-content">
        <header className="decision-header">
          <div>
            <p className="eyebrow">{decision.caseId} / DECISION REVIEW</p>
            <div className="title-row">
              <h1>{decision.title}</h1>
              <StatusPill value={decision.status} />
            </div>
            <p className="business-need">{decision.businessNeed}</p>
          </div>
          <button
            className="button button-secondary"
            type="button"
            onClick={reload}
          >
            <RefreshCw size={16} /> Refresh
          </button>
        </header>

        <div className="decision-meta" aria-label="Decision metadata">
          <Meta label="Decision">
            <CopyId value={decision.decisionId} label="Decision ID" />
          </Meta>
          <Meta label="Owner">
            <CopyId value={decision.ownerId} label="owner ID" />
          </Meta>
          <Meta label="Required approver">
            <CopyId value={decision.requiredApproverId} label="approver ID" />
          </Meta>
          <Meta label="Updated">{formatTimestamp(decision.updatedAt)}</Meta>
        </div>

        <MetricStrip
          roi={state.roi}
          businessValue={state.businessValue}
          retry={reload}
        />

        <RecommendationSection
          recommendation={state.recommendation}
          retry={reload}
        />

        <section className="governance-band" aria-labelledby="governance-title">
          <div>
            <p className="section-eyebrow">HUMAN AUTHORITY</p>
            <h2 id="governance-title">Governance actions</h2>
            <p>
              The server validates role, transition, approver identity and
              Ledger sequence.
            </p>
          </div>
          <div className="action-bar">
            {authorizedActions.length ? (
              authorizedActions.map((item) => (
                <button
                  key={item}
                  className={`button action-${item}`}
                  type="button"
                  onClick={() => setAction(item)}
                >
                  <ActionIcon action={item} /> {humanize(item)}
                </button>
              ))
            ) : (
              <span className="read-only-note">
                <ShieldCheck size={16} /> Read-only workspace
              </span>
            )}
          </div>
        </section>

        <div className="command-announcement" aria-live="polite">
          {command.status === "success" ? (
            <p className="notice notice-success">{command.message}</p>
          ) : null}
          {command.status === "error" ? (
            <p className="notice notice-error">
              <strong>{command.error.code}</strong> {command.error.message}{" "}
              <code>{command.error.correlationId}</code>
            </p>
          ) : null}
        </div>

        <TimelineSection
          state={state.timeline}
          onPage={(page) => loadPage("timeline", page)}
          retry={reload}
        />
        <EvidenceSection
          state={state.evidence}
          onPage={(page) => loadPage("evidence", page)}
          retry={reload}
        />
        <LedgerSection
          state={state.ledger}
          onPage={(page) => loadPage("ledger", page)}
          retry={reload}
        />
        <BusinessValueSection state={state.businessValue} retry={reload} />
      </div>

      {action ? (
        <ActionDialog
          action={action}
          decisionId={decisionId}
          evidence={evidence}
          ledgerHead={ledgerHead}
          pending={command.status === "pending"}
          onClose={() => setAction(null)}
          onSubmit={(body, key) => submit(action, body, key)}
        />
      ) : null}
    </main>
  );
}

function AppBar({
  role,
  actorId,
  clearSession,
}: {
  role: string | null;
  actorId: string | null;
  clearSession: () => void;
}) {
  return (
    <header className="app-bar">
      <Brand />
      <div className="session-identity">
        {actorId ? (
          <span className="actor-id" title={actorId}>
            {actorId.slice(0, 8)}
          </span>
        ) : null}
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
  );
}

function MetricStrip({
  roi,
  businessValue,
  retry,
}: {
  roi: Loadable<import("@/services/api/schemas").Roi>;
  businessValue: Loadable<import("@/services/api/schemas").BusinessValue>;
  retry: () => void;
}) {
  const realized =
    businessValue.status === "ready"
      ? businessValue.data.realizedSavings
      : null;
  return (
    <section className="metric-strip" aria-label="Decision value summary">
      <Metric
        label="Current monthly cost"
        value={
          roi.status === "ready"
            ? formatMoney(roi.data.currentMonthlyCost)
            : "Unavailable"
        }
        icon={<CircleGauge size={18} />}
      />
      <Metric
        label="Projected monthly cost"
        value={
          roi.status === "ready"
            ? formatMoney(roi.data.projectedMonthlyCost)
            : "Unavailable"
        }
        icon={<Activity size={18} />}
      />
      <Metric
        label="Estimated annual recovery"
        value={
          roi.status === "ready"
            ? formatMoney(roi.data.estimatedAnnualizedRecovery)
            : "Unavailable"
        }
        accent
        icon={<BadgeEuro size={18} />}
      />
      <Metric
        label="Realized business value"
        value={realized ? formatMoney(realized) : "Not yet realized"}
        icon={<BookOpenCheck size={18} />}
      />
      {roi.status === "error" ? (
        <div className="metric-strip-error">
          <ErrorBlock error={roi.error} onRetry={retry} />
        </div>
      ) : null}
    </section>
  );
}

function Metric({
  label,
  value,
  icon,
  accent = false,
}: {
  label: string;
  value: string;
  icon: React.ReactNode;
  accent?: boolean;
}) {
  return (
    <article className={`metric ${accent ? "metric-accent" : ""}`}>
      <span>{icon}</span>
      <div>
        <p>{label}</p>
        <strong>{value}</strong>
      </div>
    </article>
  );
}

function RecommendationSection({
  recommendation,
  retry,
}: {
  recommendation: Loadable<import("@/services/api/schemas").Recommendation>;
  retry: () => void;
}) {
  return (
    <SectionShell
      title="Recommendation"
      eyebrow="DETERMINISTIC POLICY"
      icon={<BrainCircuit size={19} />}
    >
      {recommendation.status === "loading" ? (
        <LoadingBlock label="Recommendation" />
      ) : null}
      {recommendation.status === "error" ? (
        <ErrorBlock error={recommendation.error} onRetry={retry} />
      ) : null}
      {recommendation.status === "empty" ? (
        <EmptyBlock>No Recommendation is linked to this Decision.</EmptyBlock>
      ) : null}
      {recommendation.status === "ready" ? (
        <div className="recommendation-layout">
          <div className="recommendation-main">
            <StatusPill value={recommendation.data.type} />
            <h3>{recommendation.data.suggestedAction}</h3>
            <p>{recommendation.data.deterministicReason}</p>
          </div>
          <dl className="fact-list">
            <Fact
              label="Estimated savings"
              value={formatMoney(recommendation.data.estimatedSavings)}
            />
            <Fact
              label="Confidence"
              value={`${recommendation.data.confidence}%`}
            />
            <Fact label="Risk" value={recommendation.data.risk} />
            <Fact
              label="Created"
              value={formatTimestamp(recommendation.data.createdAt)}
            />
          </dl>
        </div>
      ) : null}
    </SectionShell>
  );
}

function TimelineSection({
  state,
  onPage,
  retry,
}: {
  state: Loadable<Page<TimelineItem>>;
  onPage: (page: number) => void;
  retry: () => void;
}) {
  return (
    <SectionShell
      title="Decision timeline"
      eyebrow="CAUSAL HISTORY"
      icon={<Clock3 size={19} />}
    >
      {state.status === "loading" ? <LoadingBlock label="Timeline" /> : null}
      {state.status === "error" ? (
        <ErrorBlock error={state.error} onRetry={retry} />
      ) : null}
      {state.status === "empty" ? (
        <EmptyBlock>No timeline facts are available.</EmptyBlock>
      ) : null}
      {state.status === "ready" ? (
        <>
          <ol className="timeline-list">
            {state.data.items.map((item) => (
              <TimelineRow
                key={`${item.type}-${item.referenceId}`}
                item={item}
              />
            ))}
          </ol>
          <PageControls
            page={state.data.page}
            totalPages={state.data.totalPages}
            onPage={onPage}
          />
        </>
      ) : null}
    </SectionShell>
  );
}

function TimelineRow({ item }: { item: TimelineItem }) {
  return (
    <li>
      <span className="timeline-dot" aria-hidden="true" />
      <div className="timeline-time">{formatTimestamp(item.occurredAt)}</div>
      <div className="timeline-body">
        <div>
          <StatusPill value={item.type} />
          {item.source ? (
            <span className="source-label">{item.source}</span>
          ) : null}
        </div>
        <h3>{item.summary}</h3>
        <CopyId value={item.referenceId} label="timeline reference" />
      </div>
    </li>
  );
}

function EvidenceSection({
  state,
  onPage,
  retry,
}: {
  state: Loadable<Page<EvidenceSummary>>;
  onPage: (page: number) => void;
  retry: () => void;
}) {
  return (
    <SectionShell
      title="Operational Evidence"
      eyebrow="SOURCE TRUTH"
      icon={<Database size={19} />}
    >
      {state.status === "loading" ? <LoadingBlock label="Evidence" /> : null}
      {state.status === "error" ? (
        <ErrorBlock error={state.error} onRetry={retry} />
      ) : null}
      {state.status === "empty" ? (
        <EmptyBlock>No authorized Evidence is available.</EmptyBlock>
      ) : null}
      {state.status === "ready" ? (
        <>
          <div className="evidence-grid">
            {state.data.items.map((item) => (
              <EvidenceCard key={item.evidenceId} item={item} />
            ))}
          </div>
          <PageControls
            page={state.data.page}
            totalPages={state.data.totalPages}
            onPage={onPage}
          />
        </>
      ) : null}
    </SectionShell>
  );
}

function EvidenceCard({ item }: { item: EvidenceSummary }) {
  const redacted = [
    item.observedFact,
    item.businessMeaning,
    item.source,
  ].includes("[REDACTED]");
  return (
    <article className={`evidence-card ${redacted ? "evidence-redacted" : ""}`}>
      <header>
        <div>
          <StatusPill value={item.evidenceType} />
          <span className="sensitivity-label">{item.sensitivity}</span>
        </div>
        <CopyId value={item.evidenceId} label="Evidence ID" />
      </header>
      <h3>{redacted ? "Protected Evidence" : item.observedFact}</h3>
      <p>
        {redacted
          ? "Content is redacted by the server authorization policy."
          : item.businessMeaning}
      </p>
      <dl className="evidence-facts">
        <Fact label="Source" value={item.source} />
        <Fact label="Event" value={item.eventType} />
        <Fact label="Severity" value={item.severity} />
        <Fact label="Observed" value={formatTimestamp(item.timestamp)} />
      </dl>
    </article>
  );
}

function LedgerSection({
  state,
  onPage,
  retry,
}: {
  state: Loadable<Page<LedgerEntry>>;
  onPage: (page: number) => void;
  retry: () => void;
}) {
  return (
    <SectionShell
      title="Decision Ledger"
      eyebrow="APPEND-ONLY RECORD"
      icon={<FileCheck2 size={19} />}
    >
      {state.status === "loading" ? <LoadingBlock label="Ledger" /> : null}
      {state.status === "error" ? (
        <ErrorBlock error={state.error} onRetry={retry} />
      ) : null}
      {state.status === "empty" ? (
        <EmptyBlock>No governance fact has been appended yet.</EmptyBlock>
      ) : null}
      {state.status === "ready" ? (
        <>
          <div className="ledger-list">
            {state.data.items.map((item, index) => (
              <LedgerRow
                key={item.ledgerEntryId}
                item={item}
                index={index + state.data.page * state.data.size + 1}
              />
            ))}
          </div>
          <PageControls
            page={state.data.page}
            totalPages={state.data.totalPages}
            onPage={onPage}
          />
        </>
      ) : null}
    </SectionShell>
  );
}

function LedgerRow({ item, index }: { item: LedgerEntry; index: number }) {
  return (
    <article className="ledger-row">
      <div className="ledger-index">{String(index).padStart(2, "0")}</div>
      <div className="ledger-main">
        <header>
          <StatusPill value={item.entryType} />
          <time>{formatTimestamp(item.occurredAt)}</time>
        </header>
        <h3>{item.changeSummary}</h3>
        <p>{item.reason}</p>
        <div className="ledger-trace">
          <span>{item.actorRole}</span>
          <CopyId value={item.actorId} label="actor ID" />
          {item.previousEntryId ? (
            <>
              <span>Previous</span>
              <CopyId
                value={item.previousEntryId}
                label="previous Ledger entry"
              />
            </>
          ) : (
            <span>Chain origin</span>
          )}
        </div>
      </div>
      {item.realizedSavings || item.estimatedSavings ? (
        <div className="ledger-value">
          <small>{item.realizedSavings ? "Realized" : "Estimated"}</small>
          <strong>
            {formatMoney(
              (item.realizedSavings ?? item.estimatedSavings) as Money,
            )}
          </strong>
        </div>
      ) : null}
    </article>
  );
}

function BusinessValueSection({
  state,
  retry,
}: {
  state: Loadable<import("@/services/api/schemas").BusinessValue>;
  retry: () => void;
}) {
  return (
    <SectionShell
      title="Business Value"
      eyebrow="VALIDATED OUTCOME"
      icon={<Scale size={19} />}
      className="business-value-section"
    >
      {state.status === "loading" ? (
        <LoadingBlock label="Business Value" />
      ) : null}
      {state.status === "error" ? (
        <ErrorBlock error={state.error} onRetry={retry} />
      ) : null}
      {state.status === "not-ready" ? (
        <div className="value-not-ready">
          <span>Not yet realized</span>
          <h3>Estimated value remains separate from outcome</h3>
          <p>
            Business Value will appear only after implementation Evidence and
            financial validation are accepted by the server.
          </p>
        </div>
      ) : null}
      {state.status === "ready" ? (
        <div className="realized-value">
          <div>
            <p>Annualized realized savings</p>
            <strong>{formatMoney(state.data.realizedSavings)}</strong>
            <span>Variance {state.data.variance}</span>
          </div>
          <dl className="fact-list">
            <Fact
              label="Baseline cost"
              value={formatMoney(state.data.annualizedBaselineCost)}
            />
            <Fact
              label="Post-action cost"
              value={formatMoney(state.data.annualizedPostActionCost)}
            />
            <Fact
              label="Transition cost"
              value={formatMoney(state.data.actualTransitionCost)}
            />
            <Fact
              label="Validation entry"
              value={state.data.validationEntryId}
            />
          </dl>
        </div>
      ) : null}
    </SectionShell>
  );
}

function Meta({
  label,
  children,
}: {
  label: string;
  children: React.ReactNode;
}) {
  return (
    <div>
      <span>{label}</span>
      <strong>{children}</strong>
    </div>
  );
}
function Fact({ label, value }: { label: string; value: string }) {
  return (
    <div>
      <dt>{label}</dt>
      <dd>{value}</dd>
    </div>
  );
}
function ActionIcon({ action }: { action: ActionKind }) {
  return action === "validate-result" ? (
    <BadgeEuro size={16} />
  ) : action === "mark-implemented" ? (
    <FileCheck2 size={16} />
  ) : (
    <Scale size={16} />
  );
}

function FullPageState({ label }: { label: string }) {
  return (
    <main className="full-page-state" role="status">
      <span className="loading-ring loading-ring-large" />
      <p className="eyebrow">IMPERATOR</p>
      <h1>{label}</h1>
    </main>
  );
}
function InvalidDecisionId() {
  return (
    <main className="full-page-state">
      <p className="eyebrow">INVALID ROUTE</p>
      <h1>Decision identifier is not canonical</h1>
      <p>No API request was made.</p>
    </main>
  );
}
