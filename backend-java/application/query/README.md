# MVP Query Application Boundary

This package contains the immutable query objects, projections, and read-only
use cases authorized by D086. REST adapters consume the inbound ports backed by
these use cases. The package must never contain Spring, HTTP, JDBC, PostgreSQL,
transport DTOs, persistence records, or write behavior.
