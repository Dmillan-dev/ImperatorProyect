# 03 - Security, Data And Operations Readiness

Status: **PREPARATION ONLY / REQUIRES SECURITY, PRIVACY AND LEGAL REVIEW**

This is not legal advice and not an operational authorization. It prepares the
questions and controls that must be resolved before real customer data.

## 1. Deployment And Trust Boundary

The pilot target is one dedicated host and PostgreSQL volume for one customer.
The IMPERATOR frontend remains loopback-only under D092. Backend and database
remain on the private Docker network. Keycloak is external over trusted HTTPS;
GitHub and AWS are outbound read-only dependencies.

Required external host controls:

- supported patched OS and Docker Desktop/Engine;
- full-disk encryption;
- named administrator and least-privilege local access;
- screen lock and endpoint protection;
- firewall denying inbound public access;
- controlled outbound DNS/HTTPS;
- secure clock synchronization; and
- separate encrypted backup location.

These host controls are not implemented by the IMPERATOR repository.

## 2. Data Inventory

| Data class | Pilot handling |
|---|---|
| Internal normalized Evidence | Allowed when source-approved |
| Confidential cost/usage/ownership summaries | Allowed, role-filtered and minimized |
| Restricted data | Forbidden |
| Raw provider payload | `not_stored` only |
| JWT/password/secret/private key | Never persisted or logged |
| Ledger actor UUID and reason | Allowed governance record under agreed purpose |
| Operational logs | Safe fields only; no claims, payloads or financial bodies |
| Backups | Encrypted, access-controlled and included in deletion scope |
| R&D/commercial reports | Anonymized or aggregate unless customer expressly approves |

Restricted includes raw prompts/completions, customer conversations, API keys,
tokens, source code, unnecessary user identity and regulated/special-category
personal data.

## 3. Purpose And Minimization

The sole purpose is to evaluate one `DRC-AOA-001` decision recovery workflow.
Data may not be used for model training, unrelated analytics, sales demos for
other customers or additional Decision cases.

Before import, the source owner must prove that every field is necessary for:

- lineage and correlation;
- deterministic Recommendation/ROI;
- human authorization;
- result validation; or
- security/audit evidence.

Unknown or unnecessary fields are removed before R01.

## 4. Retention And Deletion Design

Recommended pilot targets, subject to signed agreement:

| Material | Proposed retention | Deletion method |
|---|---|---|
| Temporary source exports | Until normalized import verification, maximum 7 days | Secure deletion from controlled staging location |
| Normalized pilot database | Pilot term plus 30 days | Destroy dedicated database/volume after approved export or return |
| Operational logs | 30 days | Rotation and secure deletion |
| Encrypted backups | Maximum 30 days after pilot end | Delete backup objects and encryption-key access |
| Tokens/credentials | Only active pilot need | Revoke immediately at close or incident |
| Sanitized acceptance/R&D report | Agreed business record period | Retain only anonymized/approved content |

IMPERATOR has no row-level customer deletion workflow or multi-tenant boundary.
For the first pilot, deletion is therefore performed by destroying the entire
dedicated environment and every backup. This must be tested and evidenced
before customer data. The append-only Ledger never justifies retaining data
beyond the signed legal purpose.

## 5. Backup And Restore Design

Docker volume persistence is not backup. The proposed minimum pilot design is:

```text
Dedicated PostgreSQL
  -> nightly pg_dump custom-format archive
  -> encrypted backup location outside the Docker host
  -> separate encryption-key control
  -> restore into an isolated verification database
  -> integrity and application-read verification
```

Proposed service targets to agree:

| Target | Pilot objective |
|---|---|
| RPO | 24 hours, plus an on-demand backup before material pilot transitions |
| RTO | One business day |
| Backup retention | Rolling 7 daily copies, never beyond contractual deletion date |
| Restore test | Mandatory before first customer import and once before closeout |

The future implementation gate must define exact `pg_dump -Fc`/`pg_restore`
procedures, least-privilege credentials, encryption, checksums, error handling
and deletion. No backup script or schedule is authorized here.

## 6. DNS And TLS Preparation

| Surface | Required preparation |
|---|---|
| Keycloak issuer | Customer-approved DNS, trusted certificate, HTTPS issuer and JWKS |
| IMPERATOR browser | Loopback only under D092; no public DNS/TLS endpoint |
| GitHub/AWS | Official HTTPS endpoints only |
| Future remote IMPERATOR access | Separate architecture/security contract required |

Track certificate owner, issuer, hostname, issue/expiry dates and renewal
contact outside Git. Self-signed certificates are not accepted for pilot IdP
trust.

## 7. Secret Management

- secrets live only in an approved external secret manager or local Docker
  secret file boundary;
- `.env` contains no committed value;
- backup keys are separate from backups;
- Keycloak administrator and pilot users are separate;
- GitHub/AWS credentials are time-bounded and revocable;
- no secret is printed during preflight or captured in screenshots; and
- closeout includes revocation evidence.

Automated rotation is outside the MVP, but ownership, expiry and manual
rotation/revocation must be explicit.

## 8. Incident Response Preparation

Minimum incident record:

| Field | Requirement |
|---|---|
| Incident ID | Non-sensitive stable identifier |
| Detected/contained time | UTC |
| Reporter and incident lead | Named contacts |
| Scope | Environment, data class and affected capability |
| Evidence | Safe logs/correlation IDs only |
| Credentials affected | Yes/no, never the values |
| Customer notification | Time and channel |
| Containment/recovery | Actions and approval |
| Data impact | Confirmed, suspected or none |
| Closure | Root cause, corrective action and sign-off |

Suspected personal-data breaches require immediate privacy/legal escalation.
The signed DPA must define notification duties and timing; IMPERATOR must not
promise a legal conclusion through this technical runbook.

## 9. Customer Security Questionnaire

| Question | Prepared answer | Gap before pilot |
|---|---|---|
| Is the service multi-tenant? | No; dedicated single-customer environment | Do not claim tenant isolation |
| Is the application publicly exposed? | No; loopback-only D092 runtime | Remote access needs new contract |
| How are users authenticated? | D087 OIDC/JWT RS256; external Keycloak selected | Operational external conformance pending under D095 |
| How is authorization enforced? | Exact D088 RBAC plus Domain checks | Local real-token proof passed; external pilot proof pending |
| Are customer passwords stored? | No | External IdP terms required |
| Are provider credentials stored? | No; external secret/SDK boundaries | Provisioning and revocation pending |
| Are provider permissions read-only? | Contractually yes under D089/D090 | Customer policy review pending |
| Is data encrypted in transit? | External endpoints HTTPS; app loopback/private | IdP certificate proof pending |
| Is data encrypted at rest? | Requires encrypted pilot host and backups | Host control not yet evidenced |
| Is Restricted data processed? | Forbidden | Pre-import inspection required |
| Are raw prompts/source code stored? | No | Customer source mapping review |
| Is AI authoritative? | No; explanation only and optional | None |
| Is the Ledger mutable? | Append-only application contract | Local runtime E2E passed; pilot evidence pending |
| Are backups available? | Design only | Implement and restore-test before data |
| Is deletion supported? | Whole dedicated environment destruction | Procedure/test pending |
| Is vulnerability scanning performed? | Hosted and local Trivy gates | Current source, npm graph and maintained runtime images pass locally; D096 upstream monitoring images and hosted post-change evidence remain open |
| Is observability operational? | D096 local profile and technical verifier pass | Supply-chain, screenshot and hosted post-change certification remain open |
| Has an independent penetration test passed? | No | Decide if customer requires one |
| Is ISO 27001/SOC 2 certified? | No | Never imply certification |
| Where is data hosted? | Not selected | Must be fixed contractually |
| Are subprocessors known? | Not selected | Complete register before signature |
| Is an incident process defined? | Prepared in this document | Tabletop and contacts pending |

## 10. DPA Readiness Checklist

Qualified counsel must confirm whether the customer is controller and
IMPERATOR processor for the selected processing. Before signature, record:

- [ ] parties, roles and authorized instructions;
- [ ] processing subject, purpose, duration and nature;
- [ ] data categories and data-subject categories;
- [ ] technical and organizational measures;
- [ ] confidentiality obligations and access restrictions;
- [ ] subprocessor list, location and authorization mechanism;
- [ ] international-transfer mechanism and transfer assessment if applicable;
- [ ] assistance with data-subject requests and regulatory obligations;
- [ ] breach-notification contact and contractual timing;
- [ ] audit/information rights;
- [ ] return/deletion method, backups and deletion evidence; and
- [ ] liability, termination and governing-law alignment with the main
      agreement.

## 11. NDA And Commercial Agreement Checklist

- [ ] legal names and authorized signatories;
- [ ] definition and marking of confidential information;
- [ ] permitted pilot use and prohibited disclosure/use;
- [ ] standard exclusions and compelled-disclosure process;
- [ ] access limited to named need-to-know participants;
- [ ] security and incident-notification obligations;
- [ ] return/destruction duties and survival period;
- [ ] IP ownership for IMPERATOR and customer source data;
- [ ] no guaranteed savings, financial advice or production SLA;
- [ ] pilot fees, expenses, taxes and payment terms;
- [ ] support window, severity handling and stop rights;
- [ ] termination, measurement extension and final acceptance; and
- [ ] permission, if any, to use anonymized learnings or customer references.

NDA and DPA templates must come from qualified legal review. This repository
stores only requirements and sanitized execution evidence, not signed customer
contracts or personal contact data.

## 12. Security GO Gate

`GO` requires:

- Sprint 4.3/D092-D095 and D093 remain certified;
- Sprint 4.4 and Sprint 4.5 certified as required;
- real Keycloak, RBAC and read-only connector evidence;
- host encryption and no public exposure;
- successful backup and isolated restore;
- tested whole-environment deletion;
- signed commercial, NDA and applicable DPA terms;
- completed subprocessor/data-location register; and
- incident tabletop with named contacts.

## 13. Official References

- [GDPR Article 28 - processor obligations](https://eur-lex.europa.eu/eli/reg/2016/679/art_28/oj/eng)
- [GDPR Article 32 - security of processing](https://eur-lex.europa.eu/eli/reg/2016/679/art_32/oj/eng)
- [European Commission controller/processor guidance](https://commission.europa.eu/law/law-topic/data-protection/rules-business-and-organisations/obligations/controllerprocessor/what-data-controller-or-data-processor_en)
- [EU/EEA controller-processor clauses](https://commission.europa.eu/publications/standard-contractual-clauses-controllers-and-processors-eueea_en)
- [European Commission international-transfer guidance](https://commission.europa.eu/law/law-topic/data-protection/information-business-and-organisations/obligations/what-rules-apply-if-my-organisation-transfers-data-outside-eu_en)
- [PostgreSQL 18 backup and restore](https://www.postgresql.org/docs/18/backup.html)
- [PostgreSQL 18 pg_dump](https://www.postgresql.org/docs/18/app-pgdump.html)
- [Docker volume backup and restore](https://docs.docker.com/engine/storage/volumes/)
