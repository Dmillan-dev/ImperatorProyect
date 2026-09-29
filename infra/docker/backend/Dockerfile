FROM eclipse-temurin:21-jdk-jammy@sha256:6adefddd4a20bceef702cedb0b03952fcd7691f9c7ccffe27014992abc0b46bd AS build

WORKDIR /workspace

RUN apt-get update \
    && apt-get install --yes --no-install-recommends unzip \
    && rm -rf /var/lib/apt/lists/*

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
COPY backend-java/ backend-java/

RUN sed -i 's/\r$//' mvnw \
    && chmod 0755 mvnw \
    && ./mvnw --batch-mode --no-transfer-progress -DskipTests package \
    && test -f target/imperator-backend-0.1.0.jar

FROM eclipse-temurin:21-jre-jammy@sha256:e9aaf73145bbd1f9f6ec7f6867dd75a44f34b1a6c32a813504bf4129be2d09d7 AS runtime

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
