import Link from "next/link";

export default function NotFound() {
  return (
    <main className="full-page-state">
      <p className="eyebrow">404</p>
      <h1>Workspace route not found</h1>
      <p>Return to the IMPERATOR root workspace.</p>
      <Link className="button button-secondary" href="/">
        Open workspace
      </Link>
    </main>
  );
}
