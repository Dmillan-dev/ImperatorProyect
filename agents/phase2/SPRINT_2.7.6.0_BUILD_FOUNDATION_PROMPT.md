# SPRINT 2.7.6.0 — BUILD FOUNDATION (NO SPRING)

## Role

Act as a Senior Principal Java Backend Architect.

Use the five separated IMPERATOR sprint roles:

- Architecture Guardian;
- Implementation Agent;
- Quality Agent;
- Context Keeper;
- CTO / Product Guardian.

Only the Implementation Agent may write implementation files. Architecture Guardian and CTO / Product Guardian have veto power.

## Project

IMPERATOR

## Dependency Gate

This micro-sprint may start only when Sprint 2.7.5 is green or explicitly waived by the founder/CTO.

Canonical sequence:

1. Sprint 2.7.6.0 — Build Foundation.
2. Sprint 2.7.6.1 — Persistence Design Contract.
3. Sprint 2.7.6.2 — Flyway V1 Initial Schema.
4. Sprint 2.7.7 — PostgreSQL Integration and Tests.

Documents 31–39 and accepted Decision Log entries are authority. Do not modify an accepted decision to make the build easier.

## Current Repository Status

The project already contains:

- Domain Layer;
- Application Layer;
- Inbound Ports;
- Outbound Ports;
- PostgreSQL persistence records;
- PostgreSQL mappers;
- PostgreSQL JDBC repository implementations;
- local repository-level JDBC transaction handling.

The project currently does not contain:

- Maven or Gradle;
- Maven Wrapper;
- enforced Java 21 build configuration;
- Flyway Maven Plugin;
- dependency management;
- automated Java tests;
- PostgreSQL schema or migrations;
- Spring Boot;
- REST;
- runtime composition/configuration.

The sources were manually compiled with JDK 17. That result does not certify Java 21.

## Objective

Create a reproducible Java 21 Maven build foundation.

Nothing else.

This sprint exists only to transform the current repository into a buildable Maven project.

## Single Deliverable

A root Maven build that compiles the existing Java sources with Java 21 through Maven Wrapper.

Required execution boundary:

```text
Maven
↓
Java 21
↓
Maven Wrapper
↓
Build plugins
↓
Compile
```

The sprint ends at successful compilation. Database and migration behavior begin in later micro-sprints.

## Allowed Files

The Implementation Agent may create or modify only:

- `pom.xml`;
- `mvnw`;
- `mvnw.cmd`;
- `.mvn/wrapper/**`;
- `.gitignore` only if the official wrapper requires it;
- root `README.md` only for build-command synchronization;
- `backend-java/README.md` only to remove obsolete build/JDBC status;
- `database/README.md` only to record Flyway tooling without claiming a migration exists.

Any other file requires explicit authorization before modification.

## You MAY Create

- one root Maven project;
- Maven Wrapper;
- Java 21 compiler configuration;
- Maven Toolchains Plugin configuration;
- Maven Enforcer Plugin configuration;
- Flyway Maven Plugin declaration;
- PostgreSQL JDBC Driver dependency;
- explicit Maven lifecycle-plugin configuration;
- minimal reproducible-build properties.

## You MUST NOT Create or Modify

- Spring or Spring Boot;
- REST or controllers;
- SQL;
- `V1__initial_schema.sql`;
- Flyway migrations;
- tests or test fixtures;
- JUnit or Testcontainers dependencies;
- Docker or Docker Compose;
- Domain code;
- Application code;
- inbound or outbound ports;
- persistence records;
- mappers;
- repositories;
- JDBC implementation;
- transaction implementation;
- logging;
- security;
- frontend;
- observability;
- package or source-file moves;
- multi-module architecture.

## Maven Rules

Use the smallest structure that compiles the repository:

- one root `pom.xml`;
- `backend-java/` configured as the main source root;
- no source relocation;
- no `build-helper-maven-plugin` if `backend-java/` works directly;
- no aggregator or child modules.

Configure explicitly:

- UTF-8 source and reporting encoding;
- Java release 21;
- `-Xlint:all`;
- `-Werror` if the existing JDK 21 source baseline is warning-free;
- reproducible archive timestamp;
- exact dependency versions;
- exact directly used plugin versions.

Pin the lifecycle plugins used by `clean verify`, including:

- Maven Clean Plugin;
- Maven Resources Plugin;
- Maven Compiler Plugin;
- Maven Surefire Plugin, without adding test dependencies or test sources;
- Maven Jar Plugin;
- Maven Enforcer Plugin.

Do not add custom artifact repositories when Maven Central is sufficient.

Do not use SNAPSHOT, milestone, `LATEST`, `RELEASE` or unbounded dependency versions.

## Maven Wrapper Rules

The repository must support:

Linux/macOS:

```text
./mvnw clean verify
```

Windows:

```text
mvnw.cmd clean verify
```

Requirements:

- pin one stable Maven distribution;
- use the official Apache Maven distribution;
- pin/validate its SHA-256 checksum when supported;
- preserve executable permission for `mvnw`;
- do not fabricate wrapper files;
- request authorization if network access is required.

After bootstrap, global Maven must not be required.

## Java Rules

Project target:

```text
Java 21
```

Use:

- Maven Compiler Plugin with `release=21`;
- Maven Enforcer with an exact Java 21 policy;
- Maven Toolchains Plugin requesting/discovering JDK 21;
- `-Xlint:all`.

Do not commit:

- machine-specific JDK paths;
- a user `.m2/toolchains.xml`;
- environment-specific absolute paths.

If JDK 21 is unavailable, do not install host software without authorization and do not substitute a Java 17 result. The sprint remains `FAIL`.

## Dependency Rules

The only allowed project dependency is:

- PostgreSQL JDBC Driver, runtime scope unless compilation evidence proves a narrower compatible choice impossible.

Do not add Flyway Core as an application dependency in this sprint.

Every dependency and plugin must have:

- exact version;
- purpose;
- scope;
- current justification.

## Flyway Rules

Declare only Flyway Maven Plugin with an exact version.

Do NOT:

- configure a migration location;
- configure a database URL, schema, user or credential;
- execute Flyway;
- bind Flyway to `clean`, `test`, `package` or `verify`;
- create migrations;
- require PostgreSQL;
- add Flyway runtime bootstrapping.

Flyway is installed only. It is not configured against a database, executed or tested in this sprint.

## Verification

The sprint succeeds only if all of the following are actually executed with JDK 21:

Linux/macOS:

```text
./mvnw --version
./mvnw clean verify
```

Windows:

```text
mvnw.cmd --version
mvnw.cmd clean verify
```

Required evidence:

- Maven Wrapper version;
- Java runtime reported by Maven;
- successful compilation of all current production Java sources;
- `-Xlint:all` active;
- no compiler warnings;
- zero tests created;
- Flyway Maven Plugin declared but not invoked;
- no database connection attempted;
- Git diff limited to allowed files.

Never report an unexecuted command as successful.

## Success Criteria

The sprint passes only when:

- Maven Wrapper works;
- Maven runs with Java 21;
- `clean verify` passes;
- existing production Java sources compile without warnings;
- architecture and source layout remain unchanged;
- no implementation code is modified;
- no SQL or test is created;
- no Spring dependency exists;
- Flyway Maven Plugin is installed only;
- no critical build debt is introduced.

## Output

Return:

1. Architectural decision: root Maven structure and rejected alternatives.
2. Files created.
3. Files modified.
4. Files deleted.
5. Dependency added, including scope and exact version.
6. Maven plugins, including exact versions.
7. Commands actually executed.
8. Java and Maven versions actually verified.
9. Build and warning results.
10. Risks and intentionally deferred work.
11. Green-gate table.
12. Final Gate.

Mandatory green-gate table:

| Criterion | Status |
|---|---|
| Builds/compiles | Pending |
| Tests/checks pass or valid zero-test justification | Pending |
| Architecture respected | Pending |
| No critical technical debt | Pending |
| No dead code | Pending |
| No unresolved TODOs | Pending |
| Documentation synchronized | Pending |
| ASI target met | Pending |
| DII target met | Pending |
| Decision Stability target met | Pending |
| Sprint duration within one week | Pending |

Mandatory final block:

```text
STATUS: PASS / FAIL

Architecture Guardian: PASS / FAIL
Quality Agent: PASS / FAIL
Context Keeper: PASS / FAIL
Product Guardian: PASS / FAIL
Implementation Agent: PASS / FAIL
ASI: <score>%
DII: <score>%
Decision Stability: <number of prior accepted decisions modified>
```

Targets:

- ASI: at least 95%;
- DII: 100%;
- Decision Stability: 0 modified prior accepted decisions.

If any mandatory line fails, the sprint is not complete and Sprint 2.7.6.1 must not start.
