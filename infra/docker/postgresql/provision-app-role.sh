#!/bin/sh
set -eu

require_secret() {
    if [ ! -r "$1" ]; then
        printf '%s\n' "Required secret is unavailable: $2" >&2
        exit 78
    fi
}

require_secret /run/secrets/postgres-owner-password postgres-owner-password
require_secret /run/secrets/postgres-app-password postgres-app-password

PGPASSWORD=$(cat /run/secrets/postgres-owner-password)
IMPERATOR_APP_PASSWORD=$(cat /run/secrets/postgres-app-password)
if [ -z "$PGPASSWORD" ] || [ -z "$IMPERATOR_APP_PASSWORD" ]; then
    printf '%s\n' "Database secrets must not be empty" >&2
    exit 78
fi
export PGPASSWORD IMPERATOR_APP_PASSWORD

psql \
    --host=postgres \
    --username=imperator_owner \
    --dbname=imperator \
    --set=ON_ERROR_STOP=1 <<'SQL'
SELECT 'CREATE ROLE imperator_app LOGIN'
WHERE NOT EXISTS (
    SELECT 1 FROM pg_roles WHERE rolname = 'imperator_app'
)
\gexec

\getenv app_password IMPERATOR_APP_PASSWORD
ALTER ROLE imperator_app
    LOGIN
    PASSWORD :'app_password'
    NOSUPERUSER
    NOCREATEDB
    NOCREATEROLE
    NOINHERIT
    NOREPLICATION
    NOBYPASSRLS;

REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM imperator_app;
REVOKE CREATE ON DATABASE imperator FROM imperator_app;
REVOKE CREATE ON SCHEMA public FROM imperator_app;

GRANT CONNECT ON DATABASE imperator TO imperator_app;
GRANT USAGE ON SCHEMA public TO imperator_app;
GRANT SELECT, INSERT ON TABLE evidence TO imperator_app;
GRANT SELECT, INSERT, UPDATE ON TABLE decisions TO imperator_app;
GRANT SELECT, INSERT, DELETE ON TABLE decision_evidence TO imperator_app;
GRANT SELECT, INSERT ON TABLE recommendations TO imperator_app;
GRANT SELECT, INSERT, DELETE ON TABLE recommendation_evidence TO imperator_app;
GRANT SELECT, INSERT ON TABLE ledger_entries TO imperator_app;
GRANT SELECT, INSERT ON TABLE ledger_evidence_snapshots TO imperator_app;

DO $$
BEGIN
    IF NOT has_table_privilege('imperator_app', 'evidence', 'SELECT,INSERT')
       OR has_table_privilege('imperator_app', 'evidence', 'UPDATE,DELETE')
       OR NOT has_table_privilege('imperator_app', 'decisions', 'SELECT,INSERT,UPDATE')
       OR has_table_privilege('imperator_app', 'decisions', 'DELETE')
       OR NOT has_table_privilege('imperator_app', 'ledger_entries', 'SELECT,INSERT')
       OR has_table_privilege('imperator_app', 'ledger_entries', 'UPDATE,DELETE')
       OR NOT has_table_privilege(
           'imperator_app',
           'ledger_evidence_snapshots',
           'SELECT,INSERT'
       )
       OR has_table_privilege(
           'imperator_app',
           'ledger_evidence_snapshots',
           'UPDATE,DELETE'
       )
    THEN
        RAISE EXCEPTION 'imperator_app privilege verification failed';
    END IF;
END
$$;
SQL

unset PGPASSWORD IMPERATOR_APP_PASSWORD
printf '%s\n' "Runtime database permissions verified"
