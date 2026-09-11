FROM flyway/flyway:13.5.0@sha256:33a8a4a429aa8974e028f027e86779a2bdb89e518cab41b152d5639dac1f5337

ARG VCS_REF

USER root

# The upstream CLI bundles drivers for every supported database. IMPERATOR only
# migrates PostgreSQL; removing the others also removes their transitive runtime.
RUN find /flyway/drivers -mindepth 1 -maxdepth 1 \
        ! -name 'postgresql-42.7.12.jar' \
        ! -name 'put-your-jdbc-drivers-here.txt' \
        -exec rm -rf '{}' + \
    && find /flyway/lib/flyway -maxdepth 1 -type f \
        -name 'flyway-database-*.jar' \
        ! -name 'flyway-database-postgresql-13.5.0.jar' \
        -delete \
    && rm -f \
        /flyway/lib/flyway/flyway-firebird-*.jar \
        /flyway/lib/flyway/flyway-gcp-*.jar \
        /flyway/lib/flyway/flyway-locations-s3-*.jar \
        /flyway/lib/flyway/flyway-mysql-*.jar \
        /flyway/lib/flyway/flyway-singlestore-*.jar \
        /flyway/lib/flyway/flyway-sqlserver-*.jar

LABEL org.opencontainers.image.title="IMPERATOR Flyway PostgreSQL Runtime" \
      org.opencontainers.image.description="Flyway 13.5.0 reduced to the PostgreSQL JDBC driver" \
      org.opencontainers.image.version="13.5.0-postgresql1" \
      org.opencontainers.image.source="https://github.com/Dmillan-dev/ImperatorProyect" \
      org.opencontainers.image.revision="${VCS_REF}"

USER 65532:65532
