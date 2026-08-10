FROM node:24.18.0-bookworm-slim@sha256:6f7b03f7c2c8e2e784dcf9295400527b9b1270fd37b7e9a7285cf83b6951452d AS dependencies

WORKDIR /app

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

FROM node:24.18.0-bookworm-slim@sha256:6f7b03f7c2c8e2e784dcf9295400527b9b1270fd37b7e9a7285cf83b6951452d AS build

ENV IMPERATOR_API_ORIGIN=http://backend:8080 \
    IMPERATOR_RUNTIME_PROFILE=docker-compose \
    NEXT_TELEMETRY_DISABLED=1

WORKDIR /app

COPY --from=dependencies /app/node_modules ./node_modules
COPY frontend/ ./

RUN npm run build

FROM node:24.18.0-bookworm-slim@sha256:6f7b03f7c2c8e2e784dcf9295400527b9b1270fd37b7e9a7285cf83b6951452d AS runtime

ARG VCS_REF

RUN set -eu; \
    npm pack tar@7.5.19 --pack-destination /tmp --ignore-scripts --loglevel=error >/tmp/npm-pack-name; \
    rm -rf /usr/local/lib/node_modules/npm/node_modules/tar; \
    mkdir -p /usr/local/lib/node_modules/npm/node_modules/tar; \
    tar -xzf /tmp/tar-7.5.19.tgz \
        -C /usr/local/lib/node_modules/npm/node_modules/tar \
        --strip-components=1; \
    rm -f /tmp/tar-7.5.19.tgz /tmp/npm-pack-name; \
    node -e "const p=require('/usr/local/lib/node_modules/npm/node_modules/tar/package.json'); if (p.version !== '7.5.19') throw new Error('unexpected tar version '+p.version)"

LABEL org.opencontainers.image.title="IMPERATOR Decision Review Workspace" \
      org.opencontainers.image.description="IMPERATOR certified Next.js runtime" \
      org.opencontainers.image.source="https://github.com/imperator/imperator" \
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
