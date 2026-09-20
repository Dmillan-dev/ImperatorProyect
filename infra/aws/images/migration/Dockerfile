# syntax=docker/dockerfile:1.7@sha256:a57df69d0ea827fb7266491f2813635de6f17269be881f696fbfdf2d83dda33e

FROM flyway/flyway:13.0.0@sha256:db6195c21e90872063ff257ec38edcb8d3c77259b25a7a8fee67189511079511

ARG VCS_REF

LABEL org.opencontainers.image.title="IMPERATOR Database Migration" \
      org.opencontainers.image.description="One-shot Flyway migration task for the IMPERATOR AWS pilot" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}"

COPY database/migrations/ /flyway/sql/
COPY infra/docker/flyway/entrypoint.sh /opt/imperator/entrypoint.sh

USER root
RUN chmod 0555 /opt/imperator/entrypoint.sh \
    && chmod -R a-w /flyway/sql
USER 10001:10001

ENTRYPOINT ["/opt/imperator/entrypoint.sh"]
CMD ["migrate"]
