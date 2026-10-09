# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM golang:1.26.9-bookworm@sha256:d9c68c2c51161e12fd77e4c6320687c9cd86e1af1e3ad6e6cd63ff970641453c AS gosu-build

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

ADD --checksum=sha256:d2f7edfcc7689b9e0761c2742cc824ac86a20768bc5e8057818dc6875291fe76 \
    https://security.debian.org/debian-security/pool/updates/main/p/pcre2/libpcre2-8-0_10.42-1+deb12u2_amd64.deb \
    /tmp/libpcre2-8-0.deb

ADD --checksum=sha256:d7d1943aec9597629bf73075efcc5ef6dc9bda96d78e80d843156ecd448478b8 \
    https://security.debian.org/debian-security/pool/updates/main/p/perl/perl-base_5.36.0-7+deb12u4_amd64.deb \
    /tmp/imperator-perl-base.deb

ADD --checksum=sha256:67e7a883c5d835939638f89a750799282df243ac1446281aa92ef9e19300940b \
    https://security.debian.org/debian-security/pool/updates/main/p/perl/perl_5.36.0-7+deb12u4_amd64.deb \
    /tmp/imperator-perl.deb

ADD --checksum=sha256:ed8480b621ad574c25aea30d36194d0133ea74093756ecbe3dff157d0510ef7f \
    https://security.debian.org/debian-security/pool/updates/main/p/perl/libperl5.36_5.36.0-7+deb12u4_amd64.deb \
    /tmp/imperator-libperl5.36.deb

ADD --checksum=sha256:3ed38ffa8320e9bf7597cefa01b6a6ace4a08ccc77cdfd15ef7d8b711dea7166 \
    https://security.debian.org/debian-security/pool/updates/main/p/perl/perl-modules-5.36_5.36.0-7+deb12u4_all.deb \
    /tmp/imperator-perl-modules-5.36.deb

LABEL org.opencontainers.image.title="IMPERATOR PostgreSQL Runtime" \
      org.opencontainers.image.description="PostgreSQL 18.6 with rebuilt gosu and a pinned Debian security patch" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="18.6-gosu1.19-go1.26.9-pcre2deb12u2-perldeb12u4" \
      org.opencontainers.image.base.name="postgres:18.6-bookworm" \
      org.opencontainers.image.base.digest="sha256:1c59e2c3c818eaa0f0628f695b36e7c9e362d6b219b36a54a32df645cbd7e1af" \
      org.opencontainers.image.licenses="PostgreSQL AND Apache-2.0"

COPY --from=gosu-build /out/gosu /usr/local/bin/gosu
COPY --from=gosu-build /build/gosu/LICENSE /usr/local/share/licenses/gosu/LICENSE
COPY --chown=999:999 provision-app-role.sh /opt/imperator/provision-app-role.sh

RUN dpkg --install /tmp/libpcre2-8-0.deb /tmp/imperator-perl-base.deb /tmp/imperator-perl.deb /tmp/imperator-libperl5.36.deb /tmp/imperator-perl-modules-5.36.deb \
    && rm -f \
        /tmp/libpcre2-8-0.deb \
        /tmp/imperator-perl-base.deb /tmp/imperator-perl.deb /tmp/imperator-libperl5.36.deb /tmp/imperator-perl-modules-5.36.deb \
        /var/cache/ldconfig/aux-cache \
        /var/log/dpkg.log \
    && chmod 0755 /usr/local/bin/gosu \
    && chmod 0555 /opt/imperator/provision-app-role.sh \
    && chmod 0444 /usr/local/share/licenses/gosu/LICENSE \
    && gosu --version \
    && gosu nobody true \
    && postgres --version

USER 999:999
