# A-S3 Versioning And Byte Preservation

The original Windows checkout has CRLF for some ordinary source files while Git stores their canonical LF text. An initial isolated candidate disabled Git normalization and therefore introduced spurious whole-file changes. That candidate was stopped, never published or used for runtime approval. Its failed diff log is retained locally.

The corrected candidate uses standard input normalization (`core.autocrlf=input` in its isolated checkout). Application source semantics and original runtime-file bytes are unchanged. Git blob identity is verified against Git's actual path/attribute filtering, separately from SHA256 of raw runtime inputs. The exact source ZIP and manifest retain all pre-normalization file bytes for reproduction of the approved source/image binding; a Git SHA alone does not certify rebuilt images.

The only additional version-control metadata change is `.gitattributes`: `docs/tfg/evidence/** -text` preserves approved report/CSV/log/snapshot hashes rather than normalizing immutable evidence on checkout. No vulnerability filter, dependency, runtime code or image is changed. Original shell/frontend LF rules remain intact.

Raw immutable evidence may contain native log indentation/trailing whitespace. These files are byte/hash-verified, not edited to silence whitespace warnings. Ordinary source/documentation passes Git's CRLF-aware whitespace check; the full raw-evidence diff result remains recorded. This is not a scanner exclusion or waiver of source secrets.

Candidate commit, exact-byte ZIP, prerequisite-based Git bundle, secret scan and original Git-state preservation are recorded in the post-commit version receipt. Candidate is local/unpublished; accepted release SHA and hosted checks remain pending. A-S2 formal GO remains bound to its approved runtime and expiry; no approval transfers to CI-rebuilt images.
