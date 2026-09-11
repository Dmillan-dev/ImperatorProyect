# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM golang:1.26.6-bookworm@sha256:116d58cbd88c1297624acc6e967a060012422bacf9930927e23fb719189c6f36 AS gosu-build

WORKDIR /build/gosu

ADD --checksum=sha256:33d7537d588ea49458b9509bcf4554bdf5ceacc66da71e5caa1058ea3b689c3b \
    https://github.com/tianon/gosu/archive/6456aaa0f3c854d199d0f037f068eb97515b7513.tar.gz \
    /tmp/gosu-source.tar.gz

RUN tar --extract \
        --gzip \
        --file /tmp/gosu-source.tar.gz \
        --strip-components 1 \
    && rm /tmp/gosu-source.tar.gz \
    && test -f LICENSE \
    && CGO_ENABLED=0 GOOS=linux GOARCH=amd64 \
       go build \
         -trimpath \
         -buildvcs=false \
         -ldflags='-s -w -buildid=' \
         -o /out/gosu \
         . \
    && /out/gosu --version \
    && /out/gosu nobody true

FROM postgres:18.6-bookworm@sha256:1c59e2c3c818eaa0f0628f695b36e7c9e362d6b219b36a54a32df645cbd7e1af

ARG VCS_REF

LABEL org.opencontainers.image.title="IMPERATOR PostgreSQL Runtime" \
      org.opencontainers.image.description="PostgreSQL 18.6 with reproducibly rebuilt gosu 1.19" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="18.6-gosu1.19-go1.26.6" \
      org.opencontainers.image.base.name="postgres:18.6-bookworm" \
      org.opencontainers.image.base.digest="sha256:1c59e2c3c818eaa0f0628f695b36e7c9e362d6b219b36a54a32df645cbd7e1af" \
      org.opencontainers.image.licenses="PostgreSQL AND Apache-2.0"

COPY --from=gosu-build /out/gosu /usr/local/bin/gosu
COPY --from=gosu-build /build/gosu/LICENSE /usr/local/share/licenses/gosu/LICENSE

RUN chmod 0755 /usr/local/bin/gosu \
    && chmod 0444 /usr/local/share/licenses/gosu/LICENSE \
    && gosu --version \
    && gosu nobody true \
    && postgres --version

USER 999:999
