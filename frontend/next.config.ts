import type { NextConfig } from "next";

const apiOriginValue = process.env.IMPERATOR_API_ORIGIN;

if (!apiOriginValue) {
  throw new Error("IMPERATOR_API_ORIGIN is required");
}

const apiOrigin = new URL(apiOriginValue);
const isLoopback = ["127.0.0.1", "localhost", "[::1]"].includes(
  apiOrigin.hostname,
);

if (!["http:", "https:"].includes(apiOrigin.protocol)) {
  throw new Error("IMPERATOR_API_ORIGIN must use HTTP or HTTPS");
}
if (apiOrigin.username || apiOrigin.password) {
  throw new Error("IMPERATOR_API_ORIGIN must not contain credentials");
}
if (!isLoopback && apiOrigin.protocol !== "https:") {
  throw new Error("IMPERATOR_API_ORIGIN must use HTTPS outside local loopback");
}
if (apiOrigin.pathname !== "/" || apiOrigin.search || apiOrigin.hash) {
  throw new Error("IMPERATOR_API_ORIGIN must contain an origin only");
}

const nextConfig: NextConfig = {
  devIndicators: false,
  poweredByHeader: false,
  productionBrowserSourceMaps: false,
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
