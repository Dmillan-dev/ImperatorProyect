FROM node:26.8-bookworm-slim@sha256:cd9f682fa2885cd1056e830424764158570061c59736a1da836bc3d73df095ae AS dependencies

WORKDIR /app

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

FROM node:26.8-bookworm-slim@sha256:cd9f682fa2885cd1056e830424764158570061c59736a1da836bc3d73df095ae AS build

ENV IMPERATOR_API_ORIGIN=http://backend:8080 \
    IMPERATOR_RUNTIME_PROFILE=docker-compose \
    NEXT_TELEMETRY_DISABLED=1

WORKDIR /app

COPY --from=dependencies /app/node_modules ./node_modules
COPY frontend/ ./

RUN npm run build

FROM node:26.8-bookworm-slim@sha256:cd9f682fa2885cd1056e830424764158570061c59736a1da836bc3d73df095ae AS runtime

ARG VCS_REF

RUN set -eu; \
    rm -rf \
        /opt/yarn-v1.22.22 \
        /usr/local/lib/node_modules/corepack \
        /usr/local/lib/node_modules/npm; \
    rm -f \
        /usr/local/bin/corepack \
        /usr/local/bin/npm \
        /usr/local/bin/npx \
        /usr/local/bin/pnpx \
        /usr/local/bin/yarn \
        /usr/local/bin/yarnpkg

LABEL org.opencontainers.image.title="IMPERATOR Decision Review Workspace" \
      org.opencontainers.image.description="IMPERATOR pre-pilot Next.js runtime" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="0.1.0"

ENV HOSTNAME=0.0.0.0 \
    IMPERATOR_API_ORIGIN=http://backend:8080 \
    IMPERATOR_RUNTIME_PROFILE=docker-compose \
    NEXT_TELEMETRY_DISABLED=1 \
    NODE_ENV=production \
    PORT=3000

WORKDIR /app

COPY --from=build --chown=node:node /app/.next/standalone ./
COPY --from=build --chown=node:node /app/.next/static ./.next/static

USER 1000:1000

EXPOSE 3000

CMD ["node", "server.js"]
