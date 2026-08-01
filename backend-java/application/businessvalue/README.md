# Business Value projection

This package contains the non-persisted Application projection for a fully validated decision outcome.

The projection reads existing Evidence, Decision, Recommendation, and Ledger authorities. It never creates a Business Value aggregate, repository, table, cache, or secondary ROI calculation. Realized savings are exposed only from the authoritative `result_validated` Ledger fact.
