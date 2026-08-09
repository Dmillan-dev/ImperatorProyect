# 50 - Executive Dashboard Contract

Status: **FROZEN**

Decision authority: **D091 - Decision Review Workspace Contract**

Lifecycle phase: **Phase 3 - First Business Value Loop**

Owning gate: **Sprint 4.2.0 - Executive Dashboard Contract Freeze**

Implementation gate after acceptance: **Sprint 4.2 - Decision Review Workspace Implementation**

## 1. Purpose

This document freezes the implementation contract for the first real IMPERATOR
frontend. The D085 sprint label is `Executive Dashboard`; the canonical MVP
product surface is **Decision Review Workspace**.

The workspace answers one question:

> Can an authorized reviewer trust and act on this one recovery decision?

It presents the already-certified `DRC-AOA-001` Decision, Evidence, ROI,
Recommendation, Ledger and validated Business Value. It creates no second
source of business truth and performs no provider operation.

## 2. Contract-Freeze Baseline

Sprint 4.2.0 began from:

| Check | Verified value |
|---|---|
| Branch | `main` |
| Working tree | Clean |
| Baseline commit | `16bf6ffbe808626404aaf1c5c39d0e5638121032` |
| Sprint 4.1 | CERTIFIED / COMPLETE |
| Sprint 4.1.1 | COMPLETE |
| Java default tests | 139 passed, 0 failed |
| PostgreSQL integration tests | 31 passed, 0 failed |
| PostgreSQL | 18.4 PASS |
| Flyway migrate/validate/idempotency | PASS |
| Existing frontend | Documentation boundary only |
| Local Node.js/npm | Not installed; implementation precondition, not a contract-gate blocker |

SHA-256 hashes recorded before Document 50 and D091 were created:

| Authority | SHA-256 |
|---|---|
| `docs/project/PROJECT_STATUS.md` | `F2D2ED648B83C0B6A9EC7BF71CD86BF9B8F871D891AFE37C1385C9A338D48835` |
| `docs/project/PHASE_AND_SPRINT_MAP.md` | `C898D5E197E413EF8820F5687C589E54285D3AA32E4006E8C9B834B7A5DAC55C` |
| `docs/decisions/14_Decision_Log.md` | `BF20FA88A1F969FF3F2420E9F78A24391E856395E399877D3152E830C7446A8E` |
| `docs/architecture/27_Quality_Attributes.md` | `4FA936E16AB00BBBEA56C7B53AFD8A7C1AAD79571CAB6D183F4C626F47ED6F8E` |
| `docs/architecture/31_MVP_Implementation_Standard.md` | `472FFEC46059D4DB36887521402BAC5A274BA58B5A3BC92C44CF82FA243AAEFE` |
| `docs/architecture/35_Coding_Principles.md` | `5BF0B51B18E5ABEAFD2F17A4BD50B1566A085228EED52E9CB5A65E61CC0EA115` |
| `docs/architecture/37_Implementation_Contract.md` | `85182BBAE6DF3CB2154F32484CD94382B84AB166DEA28D1306FC65B90DB7BA29` |
| `docs/architecture/45_Functional_REST_Application_Contract.md` | `65F78475C1683BA5464932AFED919F431550B32EAA705347E75E198111FB5F3A` |
| `docs/architecture/46_JWT_Authentication_Contract.md` | `DE4CDD04505DB7D0DA5ECDA574DF79D0E4495EC2E06595B6DCF99A495AA940E3` |
| `docs/architecture/47_RBAC_Authorization_Contract.md` | `15E9204AF809E0C13B0F8EF307BD93667699C300F9ED9FD48D12CA6DBB274F2D` |
| `docs/architecture/48_GitHub_Integration_Contract.md` | `05ABA607C0CA3E3B6448EAE528A8333B5DC376762065804BE3A91B8A4F0C693D` |
| `docs/architecture/49_AWS_Integration_Contract.md` | `C73FE88E0427600B18F8EF6EB071B5322E18F5D06C1F18B40BBB0FBEA6B6C141` |
| `docs/product/29_Decision_Review_Workspace_Screen_Contract.md` | `B030B9FDF5ACF737D77ACF1E68E4BE1FB6FD47505BBF81ACE38FBFF9F7983F7F` |
| `agents/phase3/README.md` | `C3EA2DC67173457FA1AB2FD9313811535001CF977274C55709D469B4177722B4` |
| `frontend/README.md` | `FDEA59B6319D3AF27788BDF9037A5F55C0D7B9531E5DA7F52D4648A56368CF4F` |

## 3. Authority And Precedence

Implementation must read authorities in this order:

1. D085 through D091 in `docs/decisions/14_Decision_Log.md`;
2. this document;
3. `docs/architecture/45_Functional_REST_Application_Contract.md`;
4. `docs/architecture/46_JWT_Authentication_Contract.md`;
5. `docs/architecture/47_RBAC_Authorization_Contract.md`;
6. `docs/product/29_Decision_Review_Workspace_Screen_Contract.md`;
7. `docs/architecture/27_Quality_Attributes.md`;
8. Documents 31, 35 and 37; and
9. the certified implementation.

Document 29 remains authoritative for product intent, information hierarchy,
value separation, no-autonomy rules and evidence trust. This document narrowly
supersedes only its pre-implementation assumptions where later certified facts
exist:

- the four runtime roles are exactly D088 roles;
- the route and response inventory is exactly D086;
- `Request evidence` is represented by the existing defer command with
  `requiredEvidence`, not by a new route;
- `ownerId` and `requiredApproverId` replace speculative owner variants;
- the persisted deterministic reason replaces a non-persisted AI explanation;
- frontend states use certified Decision and Ledger facts, not a second state
  machine; and
- frontend visibility consumes D088-filtered responses instead of defining a
  parallel data policy.

No earlier document is edited by this narrow supersession.

## 4. Frozen Product Boundary

The implementation contains one product experience:

```text
Externally issued Bearer JWT
-> one DRC-AOA-001 Decision
-> Decision Review Workspace
-> existing D086 reads and governance commands
-> authoritative server response
```

The workspace may display:

- Decision identity, title, business need, status and timestamps;
- owner and required approver identifiers;
- ordered timeline;
- role-filtered normalized Evidence;
- persisted Recommendation and deterministic rationale;
- current and projected monthly cost;
- estimated monthly and annualized recovery;
- confidence, risk, policy version and assumption Evidence identifiers;
- approval, rejection, deferral, implementation and result-validation facts;
- immutable per-Decision Ledger history; and
- realized Business Value only after server-side result validation.

It does not display a multi-Decision portfolio, accumulated company value,
connector administration, infrastructure inventory or generic analytics.

## 5. Frontend Technology Freeze

### 5.1 Runtime lane

| Area | Frozen choice |
|---|---|
| Runtime | Node.js `24.x`, minimum `24.18.0`, less than `25` |
| Package manager | npm `11.x` |
| Lockfile | committed `frontend/package-lock.json`, lockfile version 3 |
| Install command | `npm ci` after the initial approved scaffold |
| Framework | Next.js Active LTS `16.2.x`, minimum security level `16.2.11`, less than `16.3` |
| UI runtime | React and React DOM `19.2.x`, minimum `19.2.1`, less than `19.3` |
| Language | TypeScript `5.x`, strict mode, no emit type check |
| Routing | Next.js App Router |
| Styling | Tailwind CSS `4.3.x` and one small project token layer |
| Icons | `lucide-react` only |
| Runtime validation | Zod `4.x` at every external JSON boundary |
| JWT presentation decode | `jwt-decode` `4.x`; decode only, never validation or authorization |
| Unit/component tests | Vitest `4.x`, jsdom and Testing Library |
| Browser acceptance | `@playwright/test` `1.62.x`, Chromium only in Sprint 4.2 |

Patch releases inside these lanes are permitted only when the lockfile records
the exact resolved version and all gates pass. `latest`, caret, tilde and
unbounded dependency declarations are forbidden in the committed manifest.
Major or minor-lane changes require a later decision.

The chosen Node line is the current LTS line. Next.js 16.2 is the current Active
LTS line and its official July 2026 security floor is `16.2.11`. React must not
use a version below the published 19.2 security fix.

### 5.2 Explicit dependency exclusions

Sprint 4.2 must not add:

- Redux, Zustand or another global state store;
- TanStack Query, SWR or another server-state framework;
- Axios or another HTTP client;
- a chart library;
- a date-time framework;
- a generic dashboard or grid framework;
- a form framework;
- a CSS-in-JS runtime;
- shadcn/ui or a second component system;
- analytics, telemetry or session-replay SDKs;
- provider SDKs; or
- JWT signature-verification or OAuth client libraries.

React state, the standard Fetch API, `AbortController`, native form controls
and focused feature hooks are sufficient for this one screen.

### 5.3 Normative external references

- [Node.js 24 LTS releases](https://nodejs.org/en/download/archive/v24)
- [Next.js 16 release](https://nextjs.org/blog/next-16)
- [Next.js 16.2 release](https://nextjs.org/blog/next-16-2)
- [Next.js July 2026 security release](https://nextjs.org/blog)
- [Next.js App Router installation](https://nextjs.org/docs/app/getting-started/installation)
- [Next.js rewrites](https://nextjs.org/docs/app/api-reference/config/next-config-js/rewrites)
- [React 19.2 release](https://react.dev/blog/2025/10/01/react-19-2)
- [Tailwind CSS 4.3 release](https://tailwindcss.com/blog/tailwindcss-v4-3)
- [Zod documentation](https://zod.dev/)
- [Playwright release notes](https://playwright.dev/docs/release-notes)

External references establish tool facts only. D091 and this document control
which capabilities IMPERATOR may use.

## 6. Official Frontend Shape

Sprint 4.2 may create only this bounded structure under `frontend/`:

```text
frontend/
  app/
    decisions/[decisionId]/
      page.tsx
    global-error.tsx
    layout.tsx
    not-found.tsx
    page.tsx
  components/
    ui/
  features/
    decision-review/
  services/
    api/
    auth/
  styles/
  types/
  tests/
  e2e/
  public/
  package.json
  package-lock.json
  tsconfig.json
  next.config.ts
  eslint.config.mjs
  postcss.config.mjs
  vitest.config.ts
  playwright.config.ts
  .nvmrc
  README.md
```

Directory names in this tree are exact boundaries. The named root and route
files are mandatory. The implementation may create only supporting source,
style, test and framework-configuration files inside these groups when they
directly implement this contract; it may not create another product feature,
route family or repository module.

Ownership rules:

| Group | Owns | Never owns |
|---|---|---|
| `app/` | route composition, layout and route-level states | API schemas, business calculations, auth authority |
| `components/ui/` | small reusable visual primitives | Decision semantics, HTTP calls |
| `features/decision-review/` | screen blocks, forms and local orchestration | backend DTO invention, provider logic |
| `services/api/` | Fetch client, Zod schemas, DTO types, errors, correlation | UI components, ROI calculation |
| `services/auth/` | volatile token session and presentation-only claim decode | token issuance, signature validation, persisted session |
| `types/` | frontend-only presentation types when needed | copied Domain entities or persistence records |
| `tests/`, `e2e/` | frontend verification | production fixtures or real secrets |

## 7. Route And Selection Contract

The canonical product URL is:

```text
/decisions/{canonical-decision-uuid}
```

The route must validate UUID syntax before making a Decision request. It must
reject a loaded Decision whose `caseId` is not exactly `DRC-AOA-001`.

The root `/` is a bootstrap alias for the same product surface, not a dashboard
home. After a volatile token is supplied, it calls the existing paged Decision
route and selects exactly one Decision whose `caseId` is `DRC-AOA-001`:

- zero matches: render the no-case state;
- one match: navigate to its canonical Decision URL; and
- more than one match: render a data-consistency blocker and select none.

The root never displays a Decision list, ranking, portfolio or aggregate.

No additional frontend product route is authorized. Next.js route handlers,
Server Actions, `proxy.ts`, API routes and backend-for-frontend endpoints are
forbidden.

## 8. Browser Transport And CORS Contract

The browser calls relative `/api/v1/**` paths only. `next.config.ts` uses one
transport-only external rewrite:

```text
/api/v1/:path* -> {IMPERATOR_API_ORIGIN}/api/v1/:path*
```

Rules:

- `IMPERATOR_API_ORIGIN` is a server-side runtime setting;
- it must not use a `NEXT_PUBLIC_` name;
- it contains an origin only, never credentials or path-specific authority;
- missing, invalid, credential-bearing or non-HTTP(S) values fail startup;
- HTTPS is required outside an explicitly local loopback environment;
- the rewrite forwards method, body, `Authorization`, `Idempotency-Key`,
  `X-Correlation-ID`, content type and response status without interpretation;
- no business data is cached by Next.js;
- no token or provider response is logged by frontend code; and
- the rewrite adds no authentication, authorization, retry or mutation logic.

This same-origin transport satisfies the Sprint 4.2 browser need without a Java
CORS change. D087's deferred CORS boundary remains unchanged.

## 9. Controlled Demo Authentication Contract

Sprint 4.2 implements no login and no identity provider. A valid JWT is issued
outside IMPERATOR under D087.

The local-only bootstrap behavior is:

1. the workspace initially holds no token;
2. the operator may paste one Bearer JWT into a masked session control;
3. the `Bearer ` prefix, if pasted, is removed before volatile storage;
4. a maximum token length of 16 KiB is enforced;
5. the token exists only in one in-memory React session context;
6. page refresh, tab close, explicit clear, expiry or backend `401` removes it;
7. every API request supplies exactly `Authorization: Bearer <token>`; and
8. no request occurs before a token exists.

The token must never enter:

- source code or test snapshots;
- `.env` files or `NEXT_PUBLIC_*` settings;
- URL, query, fragment or navigation state;
- localStorage, sessionStorage, IndexedDB, Cache Storage or service workers;
- cookies;
- console, application logs, error text or analytics; or
- server components, generated HTML or Next.js persisted cache.

`jwt-decode` may read only `sub`, `imperator_role` and `exp` for presentation.
This is unverified browser state and may only:

- display a masked actor identifier;
- select the D088 action-control presentation;
- display the active role; and
- clear an expired local session before a request.

It never proves identity or grants authority. Spring Security validates every
request and remains the sole security authority. Malformed decoded claims show
no actions and still require backend validation.

This bootstrap is authorized only for local demo and internal validation. It
is not Pilot Readiness, real-customer authentication or production login.

## 10. Existing API Composition

Sprint 4.2 adds no backend route, response field, DTO, query port, read model,
schema, migration or controller. The screen uses only:

| Purpose | Existing route |
|---|---|
| Bootstrap exact case | `GET /api/v1/decisions` |
| Decision header | `GET /api/v1/decisions/{id}` |
| Timeline | `GET /api/v1/decisions/{id}/timeline` |
| Evidence | `GET /api/v1/decisions/{id}/evidence` |
| ROI | `GET /api/v1/decisions/{id}/roi` |
| Recommendation | `GET /api/v1/recommendations/{recommendationId}` |
| Ledger | `GET /api/v1/decisions/{id}/ledger` |
| Validated value | `GET /api/v1/business-value?decisionId={id}` |
| Approve | `POST /api/v1/decisions/{id}/ledger/approve` |
| Reject | `POST /api/v1/decisions/{id}/ledger/reject` |
| Defer | `POST /api/v1/decisions/{id}/ledger/defer` |
| Mark implementation | `POST /api/v1/decisions/{id}/ledger/mark-implemented` |
| Validate result | `POST /api/v1/decisions/{id}/ledger/validate-result` |

The global Ledger route and Evidence import route are not used by this screen.

Load order:

1. fetch and validate Decision detail;
2. reject a non-`DRC-AOA-001` Decision;
3. use the returned Recommendation ID when present;
4. load timeline, Evidence, ROI, Recommendation and Ledger independently;
5. request Business Value and interpret only its frozen response semantics;
6. render each secondary block from its own authoritative result; and
7. after a successful command, invalidate local views and refetch all affected
   resources instead of mutating business state optimistically.

Decision detail is required for the workspace. A secondary read failure must
not fabricate data or erase successfully loaded authoritative blocks.

## 11. API Client Contract

The API client uses the standard Fetch API and Zod 4 exact schemas.

Every request:

- uses `Accept: application/json`;
- sends one canonical UUID `X-Correlation-ID`;
- sends the volatile Bearer token;
- uses `cache: no-store`;
- has a ten-second client deadline through `AbortController`;
- accepts at most 1 MiB of response content;
- parses the frozen four-field error envelope when present; and
- exposes only a safe client error with status, code, message and correlation
  ID.

One screen-load intent uses one correlation ID across its fan-out reads. A
manual retry receives a new correlation ID. One command intent receives one
new correlation ID and one new canonical UUID `Idempotency-Key`.

An `Idempotency-Key` is retained in memory only while the exact request may be
retried. Editing any request field creates a new intent and key. The browser
never asks the server to generate one.

Zod failure is `RESPONSE_CONTRACT_INVALID`. The UI must not render a partial
object from an invalid response and must not expose the rejected payload.

Automatic retries are forbidden for commands. Reads may be retried once only
after a network failure or timeout and only by explicit user action; the
frontend adds no background polling.

## 12. Exact Display Contract

### 12.1 Decision header

Display only:

- `decisionId`, `caseId`, `title`, `businessNeed`, `status`;
- `ownerId`, `requiredApproverId`;
- `createdAt`, `updatedAt`;
- optional `reviewedBy`, `reviewedAt`, `reviewReason`; and
- optional `recommendationId` as a traceability reference.

Do not invent business owner, technical owner, department, organization,
tenant, approver name or user profile. UUIDs may be visually abbreviated but
the complete value must be available through an accessible copy action.

### 12.2 Timeline

Display server-ordered items using only:

- type, reference ID, occurrence timestamp and summary;
- optional source, actor, confidence label and percentage; and
- ordered Evidence IDs.

The client does not regroup, infer or synthesize lifecycle events.

### 12.3 Evidence

Display returned D086 Evidence summary fields. `source`, `sourceType`,
`sourceObjectRef` and `timestamp` provide provenance when present.

Rules:

- Evidence remains provider-neutral;
- no AWS- or GitHub-specific decision rule exists in React;
- null fields in a D088 redacted response remain hidden and are not replaced;
- a visibly redacted item preserves only the fields returned by D088;
- raw payload and Domain metadata never exist in the frontend contract;
- no hidden Evidence count is invented when the API does not return one;
- timestamp and `reviewStatus` are displayed as supplied; and
- the client does not calculate freshness, confidence or review readiness.

A stale warning may appear only if an authoritative API field or canonical
Evidence fact explicitly states staleness. Age derived in the browser is not a
business freshness decision.

### 12.4 ROI

Display exactly:

- current monthly cost;
- projected monthly cost;
- transition cost;
- estimated monthly recovery;
- estimated annualized recovery;
- confidence;
- risk;
- policy version; and
- assumption Evidence IDs.

Money is rendered from `{amount, currency}` using decimal text. The frontend
may localize separators for display but must preserve the exact amount and
currency and must not convert currencies.

`EUR 19,440.00` may appear only when the ROI response contains that exact
annualized amount and currency. It is never a frontend constant.

### 12.5 Recommendation

Display exactly:

- type and suggested action;
- deterministic reason;
- estimated savings;
- confidence and risk;
- owner and required approver IDs;
- creation timestamp; and
- Evidence IDs.

The workspace does not request or regenerate an AI explanation. It must not
label `deterministicReason` as AI-generated text.

### 12.6 Ledger

Display the immutable ordered per-Decision chain with:

- entry ID and type;
- actor ID and role;
- occurrence timestamp;
- change summary and reason;
- Evidence IDs;
- optional estimated or realized savings;
- optional confidence and risk; and
- previous entry ID.

Safe metadata may be displayed only as a compact secondary fact list. It must
not control UI authority or rewrite historical meaning.

### 12.7 Business Value

Business Value has three exact states:

| API result | UI meaning |
|---|---|
| `200` valid response | Display validated realized savings, variance, validated cost facts and Ledger references |
| `409 BUSINESS_VALUE_NOT_READY` | Display `Not yet realized`; estimated ROI remains separate |
| Any other failure | Display the safe error state; show no realized amount |

Zero is shown only when the valid `200` response contains a zero amount. An
approval or implementation entry alone never becomes realized value.

## 13. State And Action Contract

The Decision status remains exactly one of:

- `CREATED`;
- `UNDER_REVIEW`;
- `APPROVED`;
- `REJECTED`; or
- `DEFERRED`.

`IMPLEMENTATION_MARKED` and `RESULT_VALIDATED` are Ledger facts, not new
Decision statuses. The UI may show their presence as historical milestones
only when corresponding Ledger entries exist.

The frontend does not compute a second `review_ready`, `implemented` or
`validated` state machine. It does not claim that an action is business-valid
before the server accepts it.

### 13.1 Role presentation matrix

| Existing action | `ADMIN` | `PLATFORM_ENGINEER` | `FINANCE` | `AUDITOR` |
|---|---:|---:|---:|---:|
| Approve | Show | Hide | Hide | Hide |
| Reject | Show | Hide | Hide | Hide |
| Defer | Show | Show | Show | Hide |
| Mark implementation | Hide | Show | Hide | Hide |
| Validate result | Hide | Hide | Show | Hide |

This matrix controls presentation only. D088 and downstream D083 checks remain
authoritative. No role hierarchy, multiple role, default role, ownership grant
or optimistic permission is allowed.

The UI does not derive a second action-eligibility policy from Decision status
or Ledger history. For a decoded role with a `Show` cell, the corresponding
control remains present; the backend accepts or rejects the submitted command
under D083 and D088. Pending submission is the only client-side disabled state.

### 13.2 Forms and command payloads

Forms map exactly to D086:

| Action | Exact fields |
|---|---|
| Approve or reject | `reviewedAt`, `reason`, optional `expectedPreviousEntryId` |
| Defer | `reviewedAt`, `reason`, optional `expectedPreviousEntryId`, exactly one of `requiredEvidence` or `reviewDate` |
| Mark implementation | `occurredAt`, `reason`, `evidenceIds`, `expectedPreviousEntryId`, `period` |
| Validate result | `occurredAt`, `reason`, `evidenceIds`, `expectedPreviousEntryId`, `period`, `annualizedBaselineCost`, `annualizedPostActionCost`, `actualTransitionCost` |

Required money inputs use EUR and scale-two decimal text. The browser validates
transport shape only; it does not calculate or recommend financial values.

There is no independent `Request evidence` action. The UI uses Defer with
`requiredEvidence`. There is no Execute, Apply, Deploy, Auto-fix or provider
mutation action.

Every command requires a confirmation step that names the action, Decision and
reason. While pending, the exact action is disabled to prevent duplicate user
submission. A failed command leaves authoritative screen state unchanged.

## 14. Error, Empty And Partial States

| Condition | Required behavior |
|---|---|
| No volatile token | Show the local session bootstrap; make no API call |
| `400` | Preserve form input and show the safe validation message |
| `401` | Clear token immediately and return to session bootstrap |
| `403` | Show access denied with correlation ID; do not hide prior authorized reads |
| Decision `404` | Show Decision not found; show no ROI, Recommendation or value |
| Secondary `404` | Show the corresponding block as unavailable; invent nothing |
| Business Value not ready | Show estimated value separately and realized value as unavailable |
| Other `409` | Show conflict, retain form values and offer authoritative refetch |
| `405` | Show controlled contract error; never change method |
| `5xx` or malformed response | Show unavailable block and safe retry |
| Timeout/network failure | Show timeout/network state and explicit read retry |
| Empty Evidence or Ledger page | Show a truthful empty state |
| Redacted Evidence | Show safe redaction indication from returned shape only |
| Multiple DRC-AOA-001 Decisions at root | Block automatic selection and report data inconsistency |

No error view exposes token, claims, request body, stack, provider payload,
internal URL or rejected response body. Correlation ID is the support handle.

## 15. Pagination, Ordering And Limits

The frontend preserves D086 pagination and ordering:

- default `page=0` and `size=20`;
- maximum `size=100`;
- no hidden request above the maximum;
- Evidence defaults to `timestamp ASC`;
- Timeline and per-Decision Ledger default to `occurredAt ASC`;
- next/previous pagination only;
- no infinite scroll;
- no automatic retrieval of every page; and
- no client-side resort that changes causal order.

The root bootstrap may request at most one page of 100 Decision summaries. It
does not continue scanning because the MVP contract permits one case only.

## 16. Visual And Interaction Contract

The workspace is a quiet operational tool, not a landing page.

Desktop order:

1. compact application bar with IMPERATOR, active role and clear-session icon;
2. Decision header and current status;
3. ROI and Recommendation summary;
4. action controls for the presented role;
5. Timeline;
6. Evidence;
7. Ledger; and
8. validated Business Value or explicit not-ready state.

Layout rules:

- one constrained work area, not floating page-section cards;
- repeated Evidence and Ledger items may use cards with radius at most 8 px;
- no cards nested inside cards;
- compact typography inside operational panels;
- stable grid tracks and action-bar dimensions;
- no gradient hero, decorative orb, bokeh or marketing composition;
- no chart whose facts are clearer as labeled values or an ordered timeline;
- neutral canvas with distinct cyan, green, amber and red semantic accents;
- no purple-dominant, beige-dominant or dark-slate-only palette;
- Lucide icons for familiar actions, with tooltips for unfamiliar icons;
- text labels accompany irreversible or governance commands;
- letter spacing is zero and font size never scales with viewport width; and
- no text, status or action may overlap at supported viewports.

Responsive behavior:

- desktop acceptance viewport: `1440x900`;
- compact desktop/tablet viewport: `1024x768`;
- mobile acceptance viewport: `390x844`;
- desktop may use two columns only where reading order remains clear;
- mobile is one column with the action controls remaining reachable;
- tables become semantic stacked rows rather than horizontal page overflow;
  and
- identifiers wrap or truncate with accessible full-value copy behavior.

## 17. Accessibility Contract

Sprint 4.2 targets WCAG 2.2 AA for the implemented surface.

Required behavior:

- semantic landmarks and heading order;
- keyboard access to every control;
- visible focus;
- programmatic labels and error association;
- no color-only status meaning;
- live-region announcement for command result and material load errors;
- focus moves to the first invalid form field;
- command dialog traps and restores focus;
- reduced-motion preference is respected;
- touch targets are at least 44 by 44 CSS pixels; and
- contrast passes AA for text, controls and focus indicators.

## 18. Performance And Reliability

Frozen targets from Document 27 remain:

- prepared review read p95 at most two seconds;
- Ledger command p95 at most three seconds; and
- provider synchronization and AI calls never block screen loading or action.

Frontend constraints:

- no polling, WebSocket, SSE or background synchronization;
- no provider request from the browser;
- no frontend cache that outlives the volatile tab session;
- secondary reads may execute concurrently after Decision validation;
- each API response is bounded to 1 MiB client-side;
- each request deadline is ten seconds;
- static shell assets may be cached by Next.js defaults;
- business JSON uses `no-store`; and
- a connector outage cannot prevent reading persisted Decision and Ledger
  history.

## 19. Security And Data Governance

The frontend is not a security boundary. It must nevertheless minimize
exposure:

- render only D086 fields received after D088 enforcement;
- never request Domain metadata or raw provider payloads;
- never store API response bodies outside volatile React state;
- never use `dangerouslySetInnerHTML`;
- treat every provider-originated string as untrusted text;
- allow no remote image or script source;
- ship no third-party font, analytics or CDN dependency;
- use no service worker;
- expose no source map in the production build;
- avoid logging security errors beyond safe code and correlation ID; and
- keep connector credentials completely outside the frontend.

The eventual deployment must add explicit security headers and production
identity before Pilot Readiness. Sprint 4.2 does not claim production browser
security, SSO or customer exposure.

## 20. Test And Certification Contract

Sprint 4.2 implementation is accepted only when all of these pass from a clean
checkout:

```text
node --version
npm --version
cd frontend
npm ci
npm run format:check
npm run lint
npm run typecheck
npm run test:coverage
npm run build
npm run test:e2e
```

Frontend coverage floor for owned executable TypeScript/TSX, excluding
framework configuration and test fixtures:

- statements: 80%;
- lines: 80%;
- functions: 80%; and
- branches: 75%.

Unit and component coverage must prove:

- exact Zod validation for every consumed response and error envelope;
- no ROI, confidence, risk, freshness or Business Value calculation;
- D088 presentation matrix for all four roles;
- token is absent from persistent browser APIs;
- 401 clears the volatile session;
- Business Value 409 is not rendered as zero or realized;
- every command body and Idempotency-Key follows D086;
- response-contract failure is fail-closed;
- redacted Evidence does not reveal omitted fields; and
- refetch replaces local state after a successful command.

Playwright uses protocol-faithful D086 fixtures, never real credentials or
customer data. It must verify at `1440x900`, `1024x768` and `390x844`:

- session bootstrap and Decision load;
- full read workspace;
- each role's exact action surface;
- estimated-versus-realized value behavior;
- Evidence redaction;
- empty, partial, 401, 403, 404, 409, malformed and timeout states;
- keyboard-only action form and focus behavior;
- no visible overlap or horizontal page overflow; and
- screenshot and DOM checks for the three viewports.

The backend regression gates remain mandatory and must still pass unchanged:

```text
mvnw.cmd clean verify
mvnw.cmd -Ppostgresql-integration clean verify
```

Expected baseline before any separately authorized test addition remains 139
default tests and 31 integration tests. Sprint 4.2 must not modify those tests
or their production code to make frontend certification pass.

## 21. Explicitly Out Of Scope

- portfolio or multi-Decision dashboard;
- company-wide Business Value aggregation;
- custom dashboards, widgets or layouts;
- charting and predictive analytics;
- generic query or dashboard framework;
- connector configuration, health or synchronization controls;
- AWS resource browser or GitHub repository browser;
- evidence import UI;
- standalone global Ledger page;
- new REST route, DTO, query port or read model;
- Java, Domain, Application, Port, SQL, Flyway or PostgreSQL change;
- OAuth login, Authorization Server, refresh token or SSO;
- persistent browser session or token;
- CORS expansion;
- multi-tenancy, organizations or user administration;
- notifications;
- AI explanation generation, recommendation or approval;
- automatic execution, deployment or remediation;
- billing, marketing site or public dashboard;
- mobile native application;
- production deployment, Docker or observability; and
- Pilot Readiness or real customer data.

## 22. Sprint 4.2 Implementation Boundary

The Implementation Agent may modify only `frontend/` during Sprint 4.2.

It may create:

- the frozen Next.js foundation;
- the single Decision Review feature;
- the same-origin rewrite;
- volatile demo session handling;
- exact API client and schemas;
- role-aware presentation and five action forms;
- responsive accessible styling; and
- focused frontend tests.

It may not modify:

- Java source or Java tests;
- `pom.xml` or Maven Wrapper;
- Domain, Application, Ports or adapters;
- REST routes, DTOs or security configuration;
- database, SQL or Flyway;
- GitHub or AWS connectors;
- D001-D091 or frozen contracts;
- project-control documentation; or
- files outside `frontend/`.

If implementation evidence proves that a backend change is objectively
required, Sprint 4.2 stops and returns a Contract Fix request. It does not make
the change opportunistically.

## 23. Sprint 4.2.0 Acceptance Gate

Sprint 4.2.0 is accepted only when:

- this is the sole new architecture document;
- D091 is the sole Decision Log append;
- D001-D090 are unchanged;
- the Decision Review Workspace remains the canonical product surface;
- D086 supplies the complete backend surface and no new endpoint is proposed;
- D087 and D088 remain the security authority;
- the frontend stack, package policy, route, transport, token boundary, data
  composition, action matrix, states, visual rules, limits and tests are frozen;
- no unresolved implementation choice or placeholder remains;
- no Java, frontend runtime, SQL, Flyway, dependency or test file changes;
- local Markdown links are valid;
- `git diff --check` passes;
- ASI is 100%;
- DII is 100%; and
- Decision Stability is 100% because no prior decision changed.

After acceptance, the sole next gate is Sprint 4.2 - Decision Review Workspace
Implementation. Sprint 4.2.1 may synchronize documentation only after frontend
implementation and all certification gates pass.
