# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM node:24.20.0-bookworm-slim@sha256:ba849c60be29959425b8734d57b8b4b7d56f98edd9504c9af091d5281095a71e AS dependencies

WORKDIR /app

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

FROM node:24.20.0-bookworm-slim@sha256:ba849c60be29959425b8734d57b8b4b7d56f98edd9504c9af091d5281095a71e AS build

ARG IMPERATOR_API_ORIGIN=http://backend:8080
ARG IMPERATOR_RUNTIME_PROFILE=docker-compose

ENV IMPERATOR_API_ORIGIN=${IMPERATOR_API_ORIGIN} \
    IMPERATOR_RUNTIME_PROFILE=${IMPERATOR_RUNTIME_PROFILE} \
    NEXT_TELEMETRY_DISABLED=1

WORKDIR /app

COPY --from=dependencies /app/node_modules ./node_modules
COPY frontend/ ./

RUN npm run build

FROM node:24.20.0-bookworm-slim@sha256:ba849c60be29959425b8734d57b8b4b7d56f98edd9504c9af091d5281095a71e AS runtime

ARG VCS_REF
ARG IMPERATOR_API_ORIGIN=http://backend:8080
ARG IMPERATOR_RUNTIME_PROFILE=docker-compose

ADD --checksum=sha256:d2f7edfcc7689b9e0761c2742cc824ac86a20768bc5e8057818dc6875291fe76 \
    https://security.debian.org/debian-security/pool/updates/main/p/pcre2/libpcre2-8-0_10.42-1+deb12u2_amd64.deb \
    /tmp/libpcre2-8-0.deb

ADD --checksum=sha256:d7d1943aec9597629bf73075efcc5ef6dc9bda96d78e80d843156ecd448478b8 \
    https://security.debian.org/debian-security/pool/updates/main/p/perl/perl-base_5.36.0-7+deb12u4_amd64.deb \
    /tmp/imperator-perl-base.deb

RUN set -eu; \
    dpkg --install /tmp/libpcre2-8-0.deb /tmp/imperator-perl-base.deb; \
    rm -f /tmp/libpcre2-8-0.deb /tmp/imperator-perl-base.deb /var/log/dpkg.log; \
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
    IMPERATOR_API_ORIGIN=${IMPERATOR_API_ORIGIN} \
    IMPERATOR_RUNTIME_PROFILE=${IMPERATOR_RUNTIME_PROFILE} \
    NEXT_TELEMETRY_DISABLED=1 \
    NODE_ENV=production \
    PORT=3000

WORKDIR /app

COPY --from=build --chown=node:node /app/.next/standalone ./
COPY --from=build --chown=node:node /app/.next/static ./.next/static

USER 1000:1000

EXPOSE 3000

CMD ["node", "server.js"]
