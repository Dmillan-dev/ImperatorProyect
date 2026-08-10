import type { NextConfig } from "next";

const apiOriginValue = process.env.IMPERATOR_API_ORIGIN;

if (!apiOriginValue) {
  throw new Error("IMPERATOR_API_ORIGIN is required");
}

const apiOrigin = new URL(apiOriginValue);
const isLoopback = ["127.0.0.1", "localhost", "[::1]"].includes(
  apiOrigin.hostname,
);
const isDockerComposeInternal =
  process.env.IMPERATOR_RUNTIME_PROFILE === "docker-compose" &&
  apiOrigin.protocol === "http:" &&
  apiOrigin.hostname === "backend" &&
  apiOrigin.port === "8080";

if (!["http:", "https:"].includes(apiOrigin.protocol)) {
  throw new Error("IMPERATOR_API_ORIGIN must use HTTP or HTTPS");
}
if (apiOrigin.username || apiOrigin.password) {
  throw new Error("IMPERATOR_API_ORIGIN must not contain credentials");
}
if (
  !isLoopback &&
  !isDockerComposeInternal &&
  apiOrigin.protocol !== "https:"
) {
  throw new Error("IMPERATOR_API_ORIGIN must use HTTPS outside local loopback");
}
if (apiOrigin.pathname !== "/" || apiOrigin.search || apiOrigin.hash) {
  throw new Error("IMPERATOR_API_ORIGIN must contain an origin only");
}

const nextConfig: NextConfig = {
  devIndicators: false,
  output: "standalone",
  poweredByHeader: false,
  productionBrowserSourceMaps: false,
  async headers() {
    return [
      {
        source: "/:path*",
        headers: [
          {
            key: "Content-Security-Policy",
            value:
              "default-src 'self'; base-uri 'self'; object-src 'none'; " +
              "frame-ancestors 'none'; form-action 'self'; " +
              "script-src 'self' 'unsafe-inline'; " +
              "style-src 'self' 'unsafe-inline'; img-src 'self' data:; " +
              "font-src 'self'; connect-src 'self'",
          },
          { key: "Cross-Origin-Opener-Policy", value: "same-origin" },
          {
            key: "Permissions-Policy",
            value: "camera=(), microphone=(), geolocation=()",
          },
          { key: "Referrer-Policy", value: "no-referrer" },
          { key: "X-Content-Type-Options", value: "nosniff" },
          { key: "X-Frame-Options", value: "DENY" },
        ],
      },
    ];
  },
  async rewrites() {
    return [
      {
        source: "/api/v1/:path*",
        destination: `${apiOrigin.origin}/api/v1/:path*`,
      },
    ];
  },
};

export default nextConfig;
