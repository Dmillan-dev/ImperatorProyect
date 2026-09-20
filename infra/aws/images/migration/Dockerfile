# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM flyway/flyway:13.7.0@sha256:e613cab576abcaf09d64b652fbd5e6fd6df9057e0933a35d508e793caf91352a

ARG VCS_REF

LABEL org.opencontainers.image.title="IMPERATOR Database Migration" \
      org.opencontainers.image.description="One-shot Flyway migration task for the IMPERATOR AWS pilot" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}" \
      org.opencontainers.image.version="13.7.0-postgresql"

COPY database/migrations/ /flyway/sql/
COPY infra/docker/flyway/entrypoint.sh /opt/imperator/entrypoint.sh

USER root
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
