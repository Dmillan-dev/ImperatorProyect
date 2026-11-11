# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM eclipse-temurin:21-jdk-jammy@sha256:6adefddd4a20bceef702cedb0b03952fcd7691f9c7ccffe27014992abc0b46bd AS build

WORKDIR /workspace

# CVE-2026-109518 also requires download retries in this disposable builder.
RUN apt-get -o Acquire::Retries=0 update \
    && apt-get -o Acquire::Retries=0 install --yes --no-install-recommends unzip \
    && rm -rf /var/lib/apt/lists/*

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY backend-java/ backend-java/

RUN sed -i 's/\r$//' mvnw \
    && chmod 0755 mvnw \
    && ./mvnw --batch-mode --no-transfer-progress -DskipTests package \
    && test -f target/imperator-backend-0.1.0.jar

FROM eclipse-temurin:21-jre-jammy@sha256:e9aaf73145bbd1f9f6ec7f6867dd75a44f34b1a6c32a813504bf4129be2d09d7 AS runtime

# CVE-2026-111497: patch only the installed Brotli runtime library.
ADD --checksum=sha256:908295e77a65219a62114c3c79690a3c99712274d1317c3ba47ffa2f8ea52ca5 \
    https://security.ubuntu.com/ubuntu/pool/main/b/brotli/libbrotli1_1.0.9-2ubuntu0.1_amd64.deb \
    /tmp/imperator-libbrotli1.deb

# R-17: patch only OpenSSL; preserve the pinned Java runtime and other OS packages.
ADD --checksum=sha256:d1b4636e32ac6d3b95d583ac05ac548b1aa248414bae1c51b14f54be0f8fd7bc \
    https://security.ubuntu.com/ubuntu/pool/main/o/openssl/libssl3_3.0.2-0ubuntu1.30_amd64.deb \
    /tmp/libssl3.deb
ADD --checksum=sha256:a5b9484a287aba47096fa9ea9821c5c98cf95b8dd726786e7c3a3311fb8eb3b4 \
    https://security.ubuntu.com/ubuntu/pool/main/o/openssl/openssl_3.0.2-0ubuntu1.30_amd64.deb \
    /tmp/openssl.deb

RUN dpkg --install /tmp/imperator-libbrotli1.deb /tmp/libssl3.deb /tmp/openssl.deb \
    && test "$(dpkg-query --showformat='${Version}' --show libbrotli1)" = "1.0.9-2ubuntu0.1" \
    && rm -f /tmp/imperator-libbrotli1.deb /tmp/libssl3.deb /tmp/openssl.deb /var/log/dpkg.log

# CVE-2026-109518: update only APT and its matching runtime library.
ADD --checksum=sha256:2d4db9a3017c5364e50af5bc2f0c76828b52933b682eff4728f1211e081c6736 \
    https://security.ubuntu.com/ubuntu/pool/main/a/apt/apt_2.4.15ubuntu0.1_amd64.deb \
    /tmp/imperator-apt.deb
ADD --checksum=sha256:3a6fbab029e15ca7b8da642c17ec110995a6e3a0bc40eaa08e239cfc94d6c426 \
    https://security.ubuntu.com/ubuntu/pool/main/a/apt/libapt-pkg6.0_2.4.15ubuntu0.1_amd64.deb \
    /tmp/imperator-libapt-pkg6.0.deb

RUN dpkg --install /tmp/imperator-apt.deb /tmp/imperator-libapt-pkg6.0.deb \
    && test "$(dpkg-query --showformat='${Version}' --show apt)" = "2.4.15ubuntu0.1" \
    && test "$(dpkg-query --showformat='${Version}' --show libapt-pkg6.0)" = "2.4.15ubuntu0.1" \
    && rm -f /tmp/imperator-apt.deb /tmp/imperator-libapt-pkg6.0.deb /var/log/dpkg.log

ARG VCS_REF

LABEL org.opencontainers.image.title="IMPERATOR Backend" \
      org.opencontainers.image.description="IMPERATOR pre-pilot Java runtime" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="0.1.0"

RUN groupadd --gid 10001 imperator \
    && useradd --uid 10001 --gid 10001 --home-dir /opt/imperator \
       --no-create-home --shell /usr/sbin/nologin imperator \
    && mkdir -p /opt/imperator \
    && chown 10001:10001 /opt/imperator

WORKDIR /opt/imperator

COPY --from=build --chown=10001:10001 \
    /workspace/target/imperator-backend-0.1.0.jar \
    /opt/imperator/imperator-backend.jar
COPY --chown=10001:10001 infra/docker/backend/entrypoint.sh \
    /opt/imperator/entrypoint.sh

RUN chmod 0555 /opt/imperator/entrypoint.sh

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["/opt/imperator/entrypoint.sh"]
