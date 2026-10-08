# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM flyway/flyway:13.7.0@sha256:e613cab576abcaf09d64b652fbd5e6fd6df9057e0933a35d508e793caf91352a

# Scoped security patch; keep the pinned Flyway base unchanged.
ADD --checksum=sha256:219f43b1cd836a4da550938db5fda93160d269d39a4edcf1f1ce698a470db797 \
    https://security.ubuntu.com/ubuntu/pool/main/o/openssl/libssl3t64_3.0.13-0ubuntu3.16_amd64.deb \
    /tmp/imperator-libssl3t64.deb
ADD --checksum=sha256:675b84971ffd4467707008c25ef7520f90ea7c23ef27b7a76b0dccf1d7c4dc3f \
    https://security.ubuntu.com/ubuntu/pool/main/o/openssl/openssl_3.0.13-0ubuntu3.16_amd64.deb \
    /tmp/imperator-openssl.deb
ADD --checksum=sha256:8a501126a385b25841915d839508f8a66e2a0dbc8a6709d055ef3b3e852b094c \
    https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-core/2.22.3/jackson-core-2.22.3.jar \
    /flyway/lib/jackson-core-2.22.3.jar
ADD --checksum=sha256:556db5439e206114346043f68d200497dc96a0bca62a360a81784092ebd0e0a9 \
    https://repo.maven.apache.org/maven2/com/fasterxml/jackson/core/jackson-databind/2.22.3/jackson-databind-2.22.3.jar \
    /flyway/lib/jackson-databind-2.22.3.jar
ADD --checksum=sha256:7608c367fd8a92666d28711519599543c180298259963184ce04d2947a8f71e7 \
    https://repo.maven.apache.org/maven2/tools/jackson/core/jackson-core/3.1.7/jackson-core-3.1.7.jar \
    /flyway/lib/jackson-core-3.1.7.jar
ADD --checksum=sha256:48c781de87e2016e1f0d973748a27130f11643e7ac080173f40e2d9669d84be3 \
    https://repo.maven.apache.org/maven2/tools/jackson/core/jackson-databind/3.1.7/jackson-databind-3.1.7.jar \
    /flyway/lib/jackson-databind-3.1.7.jar

ARG VCS_REF

LABEL org.opencontainers.image.title="IMPERATOR Database Migration" \
      org.opencontainers.image.description="One-shot Flyway migration task for the IMPERATOR AWS pilot" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="13.7.0-postgresql"

COPY database/migrations/ /flyway/sql/
COPY infra/docker/flyway/entrypoint.sh /opt/imperator/entrypoint.sh

USER root
# Remove only unused drivers containing the affected shaded Jackson copies.
RUN dpkg --install /tmp/imperator-libssl3t64.deb /tmp/imperator-openssl.deb \
    && rm -f /tmp/imperator-libssl3t64.deb /tmp/imperator-openssl.deb /var/log/dpkg.log \
    && rm -rf /flyway/drivers/aws /flyway/drivers/couchbase \
    && rm -f /flyway/lib/jackson-core-2.22.2.jar /flyway/lib/jackson-databind-2.22.2.jar \
        /flyway/lib/jackson-core-3.1.6.jar /flyway/lib/jackson-databind-3.1.6.jar
RUN rm -f \
      /flyway/drivers/databricks-jdbc-*.jar \
      /flyway/drivers/mssql-jdbc-*.jar \
    && test -z "$(find /flyway/drivers -maxdepth 1 -type f \( -name 'databricks-jdbc-*.jar' -o -name 'mssql-jdbc-*.jar' \) -print -quit)" \
    && test "$(find /flyway/drivers -maxdepth 1 -type f -name 'postgresql-*.jar' | wc -l)" -eq 1 \
    && /flyway/flyway -v \
    && chmod 0555 /opt/imperator/entrypoint.sh \
    && chmod -R a-w /flyway/sql
USER 10001:10001

ENTRYPOINT ["/opt/imperator/entrypoint.sh"]
CMD ["migrate"]
