# R-20 proposed exact-artifact decision — not accepted

The Go 1.26.9 patch and complete local regression pass on source `a931601102ee19a90b3dad8477408ac0d1854669`. All four image fixable H/C controls and file/config secret controls are zero. The 23 residual CVEs / 115 package records are unchanged and still have no published Bookworm fixes. Current PostgreSQL/frontend component probes and source-boundary review do not establish universal non-exploitability.

**`accepted: false`. Owner confirmation of this exact decision is pending.** Patch/test authorization does not automatically renew expired R-20.

Proposed scope: exclusively local, synthetic-data TFG-A rehearsal. Exclude AWS, production, real data, public exposure, customer deployment and product expansion. Preserve loopback/internal networking, non-root/capability/read-only restrictions, trusted fixed-format inputs, protocol limitations and resource bounds. No automatic renewal or transfer to other images.

Proposed exclusive expiry: **16 October 2026 at 00:00 Europe/Madrid (UTC+02:00), equal to 15 October 2026 at 22:00 UTC**. This date is not authorized until the owner confirms it.

| Component | Exact OCI index digest                                                    | linux/amd64 manifest digest                                               |
| --------- | ------------------------------------------------------------------------- | ------------------------------------------------------------------------- |
| migration | `sha256:c53c77aeba010ddfa1538aa75a62b20e5d826baba2869cf50b06c86512a2b8b4` | `sha256:a4b1f621b007dca0d3cf099bd4d1e3246bffccddb240ffba7ee0b567ac6f1227` |
| backend   | `sha256:298f4094799ca9c95e0a72ee65c04d19d42950cddcda2c0f5e9810c1bcc44757` | `sha256:645e2aa935ab08a3765f8b62c81b7c210c27867435efa22145b12aaba179a67f` |
| frontend  | `sha256:ec95fa1037974181cc6cb30041d82f4d888a056605c9a405899112c3842a9a78` | `sha256:85decf17bb4f50e1115839c3dbb0dfac31aafaafb567f50212598d08ce5f3daa` |
| postgres  | `sha256:3cb24d97e30a53f79de131660d8f0fcdb62f3cbc205c7b3f1710a0415834cfd9` | `sha256:c22d3892c45e412fbdb36d4e17d4580359be3bba87873710cceeeaf777b68717` |

[Machine-readable decision and mitigations](R20_PROPOSED_DECISION.json) bind these exact images and source origins. Backend/frontend/migration retain their recorded 6e79e83 build; the new PostgreSQL was built on a931601. Their unchanged application inputs are demonstrated; they are not relabelled as new builds.

R-23 remains independently OPEN / NO FIX AVAILABLE; this decision neither closes it nor authorizes speculative updates. Any future fixable H/C or secret finding fails its strict gate. A new image requires its own applicability review and explicit decision. R-20 approval alone does not establish hosted candidate CI or formally close A-S3.
