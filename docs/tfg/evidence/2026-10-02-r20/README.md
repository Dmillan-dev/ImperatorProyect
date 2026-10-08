# R-20 Analysis Evidence — 2026-10-02

**ANALYSIS COMPLETE / DECISION PENDING. TFG-A IN PROGRESS.**
This pack supports the [formal security decision](../../R20_SECURITY_DECISION.md).
R-19, base-image updates and risk acceptance remain unapproved and unapplied.

- [Index](../TFG_A_R20_2026-10-02.json): baseline Git SHA, unchanged runtime/test
  fingerprint, exact scanned image identities, commands, timestamps and artifact hashes.
- [32-CVE vendor table](CVE_MATRIX.md) and [machine matrix](cve-matrix.json):
  all 153 binary-package findings, status, installed/fixed versions and bounded impact.
- [Scan comparison](scan-comparison.json): six official base candidates and
  three unchanged project images, including unfixed High/Critical. OS and
  language packages are reported separately; base scans are not application acceptance.
- [Scanner database metadata](trivy-db-metadata.json): one unchanged snapshot
  for candidate comparison; independent current Debian tracker retrieval is recorded.
- [Candidate manifest pins](candidate-manifests.json) and
  [primary retrieval metadata](primary-source-retrieval.json).
- [Runtime inspection](runtime-inspection.json): read-only ephemeral containers,
  no network, direct shared libraries, 64-bit Perl and actual module presence.
  Logs: [frontend](frontend-runtime-inspection.log), [PostgreSQL](postgres-runtime-inspection.log).
- [APT metadata and simulation](apt-probe.json): signature-verified configured
  repositories; zero frontend upgrades, only two PostgreSQL OpenSSL upgrades,
  with CVE-2026-84782 still open in Bookworm. No packages installed.
  Logs: [frontend](frontend-apt-probe.log), [PostgreSQL](postgres-apt-probe.log).
- [Binding discrepancy](binding-review.json): earlier rehearsal/facts and final
  scanner IDs differ because the verifier rebuilt mutable tags. Historical
  results remain intact; this analysis scans exact IDs.

No implementation, Java, Spring, application dependency, workflow or database
migration changed. No tests on candidate bases, accepted risk, hosted check,
publication, external IdP or AWS execution is claimed. Existing volumes and
pre-existing output remain untouched. Disposable inspection containers used
`--rm`; APT lists lived only in tmpfs. Diagnostic caches are useful retained
evidence, not a new cleanup target.
