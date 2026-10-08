# Documentation candidate and preserved runtime

This local candidate descends from `8380e8d6e70ca4c858604ce4fcc0e0198fa162e1`. It updates documentation and evidence only. The original repository branch/index and prior candidate/bundle/archive are preserved; there is no remote push, tag, PR or merge.

The candidate uses standard Git input normalization. Raw evidence under `docs/tfg/evidence/** -text` retains exact bytes. All383runtime inputs remain equal to the approved raw manifest; ordinary committed source is checked using Git's actual path filters. The new exact-byte source ZIP preserves raw CRLF/LF bytes independently of the commit identity.

The source manifest excludes itself when constructed, then is included in the source archive/commit. Candidate SHA, tree, diff/secret results and bundle hashes are recorded after the commit in a version receipt. Those post-commit metadata files are an external attestation to avoid a self-referential SHA. The current root checkout may contain that attestation beyond the candidate; it is not represented as a clean accepted release.

An owner-accepted source SHA, a hosted CI head SHA and an approved runtime image are three separate bindings. None is supplied by this candidate alone. Documentation changes do not renew R20 or transfer image approval. Exact image identity is inherited solely as a reference to the unchanged, previously approved runtime, not a new build.

Source/document whitespace checks are separate from full immutable raw-evidence warnings. Byte preservation prevents rewriting historic logs to make a global check green. No secret/vulnerability scanner suppression is added.
