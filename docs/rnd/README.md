# IMPERATOR I+D Evidence Documentation

## Purpose

This folder documents project-development evidence for IMPERATOR.

It exists so future startup, investor, grant, tax, technical-audit or I+D review work can understand:

- what was researched,
- what technical uncertainty existed,
- what architecture decisions were made,
- what code will be created later,
- which people spent time on which work packages,
- what experiments and tests were run,
- what technical objects were produced,
- what evidence supports each claim.

This folder is not legal, accounting or tax advice. Before using it for fiscal incentives, grants, informes motivados or formal I+D+i accreditation, review it with qualified advisors.

## External Reference Baseline

Use these public references as orientation:

- OECD Frascati Manual 2015: https://www.oecd.org/en/publications/frascati-manual-2015_9789264239012-en.html
- OECD Oslo Manual 2018: https://www.oecd.org/en/publications/2018/10/oslo-manual-2018_g1g9373b/full-report/component-10.html
- Ministerio de Ciencia, Innovacion y Universidades - Innovar: https://www.ciencia.gob.es/Innovar.html
- BOE Ley 27/2014, articulo 35: https://boe.es/buscar/act.php?id=BOE-A-2014-12328

## Folder Map

| Path | Purpose |
|---|---|
| `30_RD_Activity_Evidence_Dossier.md` | Master dossier for project, architecture, development, hours, objects and tests |
| `templates/01_ACTIVITY_LOG_TEMPLATE.md` | Daily or weekly activity and hours record |
| `templates/02_TECHNICAL_OBJECT_REGISTER_TEMPLATE.md` | Register of domain, architecture, code and test objects |
| `templates/03_EXPERIMENT_TEST_RECORD_TEMPLATE.md` | Experiment, prototype and test evidence record |
| `templates/04_MONTHLY_RD_SUMMARY_TEMPLATE.md` | Monthly summary for audit or management review |
| `logs/README.md` | Rules for future monthly activity logs |

## Phase 0 Rule

During Phase 0, this folder may document:

- conceptual architecture,
- domain modeling,
- technical uncertainty,
- research questions,
- manual evidence,
- expected tests,
- future object registers,
- future code evidence rules.

It must not invent:

- hours not actually worked,
- code that does not exist,
- test results that were not run,
- fiscal conclusions,
- I+D eligibility claims without evidence.

## Minimum Evidence Pack For Each Work Item

Every future R&D-relevant work item should keep:

1. activity ID,
2. owner,
3. date,
4. hours,
5. work package,
6. technical uncertainty,
7. action performed,
8. artifact produced,
9. evidence link,
10. result or learning,
11. reviewer or decision.

## Relationship With Other Folders

| Folder | Relationship |
|---|---|
| `docs/architecture/` | Source of architecture evidence and design rationale |
| `docs/product/` | Source of domain, API, ledger, ROI and acceptance evidence |
| `docs/decisions/` | Source of accepted decisions and chronology |
| `docs/rfcs/` | Source of unresolved or proposed technical evolution |
| `docs/research/` | Market/product hypothesis records |
| `docs/rnd/` | Technical development evidence, hours, objects, experiments and tests |

## Current Status

As of 2026-07-19, IMPERATOR is still documentation-first.

There is no runnable implementation, so this folder records:

- Phase 0 architecture and domain design,
- planned evidence discipline for future code,
- templates for future activity logs,
- expected R&D traceability model.

The first future monthly log should be created only when real tracked activity begins.
