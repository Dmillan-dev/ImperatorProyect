# database

## Purpose

Frontera de persistencia conceptual y migraciones PostgreSQL cuando sean autorizadas.

## Who Uses This Folder

- Implementation Agent during the PostgreSQL persistence sprint.
- Architecture Guardian to verify persistence remains an adapter detail.
- Quality Agent to review migrations and database naming once authorized.

## Contains

- Future PostgreSQL migrations.
- Future database documentation tied to implemented persistence.
- Future seed or fixture files only when explicitly authorized.

Sprint 1 status:
- Repository shell only.
- No SQL migrations.
- No seed data.
- No schema files.
- No database runtime configuration.

## Never Contains

- Domain model definitions as the source of truth.
- Java, Python or React source code.
- Business rules.
- Runtime credentials.
- Unapproved fake business data.

## Authorized Next Use

Sprint 2.7 may define PostgreSQL persistence foundation. Database work must
adapt to the existing domain and ports; it must not force domain changes.
