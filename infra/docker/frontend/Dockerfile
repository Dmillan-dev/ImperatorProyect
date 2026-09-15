# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM node:24.19.0-bookworm-slim@sha256:a9f5f7c91a432850b2a8a7797adf5eadb6c733ceed61167806cee7ea7fbc29df AS dependencies

WORKDIR /app

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

FROM node:24.19.0-bookworm-slim@sha256:a9f5f7c91a432850b2a8a7797adf5eadb6c733ceed61167806cee7ea7fbc29df AS build

ENV IMPERATOR_API_ORIGIN=http://backend:8080 \
    IMPERATOR_RUNTIME_PROFILE=docker-compose \
    NEXT_TELEMETRY_DISABLED=1

WORKDIR /app

COPY --from=dependencies /app/node_modules ./node_modules
COPY frontend/ ./

RUN npm run build

FROM node:24.19.0-bookworm-slim@sha256:a9f5f7c91a432850b2a8a7797adf5eadb6c733ceed61167806cee7ea7fbc29df AS runtime

ARG VCS_REF

ADD --checksum=sha256:81c5502941118a24d47af69a17b8b0b9548d75cc6d72b3eb3fe01047b46fa10e \
    https://security.debian.org/debian-security/pool/updates/main/p/pcre2/libpcre2-8-0_10.42-1+deb12u1_amd64.deb \
    /tmp/libpcre2-8-0.deb

RUN set -eu; \
    dpkg --install /tmp/libpcre2-8-0.deb; \
    rm -f /tmp/libpcre2-8-0.deb /var/log/dpkg.log; \
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
