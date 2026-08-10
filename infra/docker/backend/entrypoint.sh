#!/bin/sh
set -eu

read_secret() {
    secret_path=$1
    secret_name=$2
    if [ ! -r "$secret_path" ]; then
        printf '%s\n' "Required secret is unavailable: $secret_name" >&2
        exit 78
    fi
    secret_value=$(cat "$secret_path")
    if [ -z "$secret_value" ]; then
        printf '%s\n' "Required secret is empty: $secret_name" >&2
        exit 78
    fi
    printf '%s' "$secret_value"
}

IMPERATOR_POSTGRESQL_PASSWORD=$(
    read_secret /run/secrets/postgres-app-password postgres-app-password
)
export IMPERATOR_POSTGRESQL_PASSWORD

if [ "${IMPERATOR_GITHUB_ENABLED:-false}" = "true" ]; then
    IMPERATOR_GITHUB_TOKEN=$(
        read_secret /run/secrets/github-token github-token
    )
    export IMPERATOR_GITHUB_TOKEN
fi

exec java -jar /opt/imperator/imperator-backend.jar
