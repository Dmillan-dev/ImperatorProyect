import type { Metadata } from "next";
import type { ReactNode } from "react";
import { SessionProvider } from "@/services/auth/session";
import "@/styles/globals.css";

export const metadata: Metadata = {
  title: "IMPERATOR / Decision Review",
  description: "Auditable operational decision review workspace",
};

export default function RootLayout({
  children,
}: Readonly<{ children: ReactNode }>) {
  return (
    <html lang="en">
      <body>
        <SessionProvider>{children}</SessionProvider>
      </body>
    </html>
  );
}
