"use client";

export default function GlobalError({
  reset,
}: {
  error: Error & { digest?: string };
  reset: () => void;
}) {
  return (
    <html lang="en">
      <body>
        <main className="full-page-state">
          <p className="eyebrow">CLIENT FAILURE</p>
          <h1>The workspace could not render safely</h1>
          <p>No business action was submitted.</p>
          <button
            className="button button-secondary"
            type="button"
            onClick={reset}
          >
            Try again
          </button>
        </main>
      </body>
    </html>
  );
}
