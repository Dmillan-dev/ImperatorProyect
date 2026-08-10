#!/bin/sh
set -eu

if [ "$#" -ne 1 ]; then
    printf '%s\n' "Exactly one Flyway command is required" >&2
    exit 64
fi

case "$1" in
    migrate|validate)
        ;;
    *)
        printf '%s\n' "Flyway command is not authorized" >&2
        exit 64
        ;;
esac

if [ ! -r /run/secrets/postgres-owner-password ]; then
    printf '%s\n' "Required database owner secret is unavailable" >&2
    exit 78
fi

FLYWAY_PASSWORD=$(cat /run/secrets/postgres-owner-password)
if [ -z "$FLYWAY_PASSWORD" ]; then
    printf '%s\n' "Required database owner secret is empty" >&2
    exit 78
fi
export FLYWAY_PASSWORD

exec /flyway/flyway "$1"
