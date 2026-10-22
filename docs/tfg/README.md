# IMPERATOR — Guía del TFG local

**Un caso, cuatro roles y una decisión trazable de principio a fin.** IMPERATOR demuestra acceso a datos, reglas de negocio, interfaz, autorización, pruebas y entrega reproducible mediante `DRC-AOA-001`, con datos sintéticos.

[Producto](../../README.md) · [Arquitectura](05_ARCHITECTURE.md) · [Memoria](08_LOCAL_MVP_MEMORY.md) · [Defensa en inglés](09_DEFENSE_SCRIPT_12_MIN.md) · [Gate de cierre](10_LOCAL_MVP_RELEASE_GATE.md)

## Estado del cierre

Revisión 22/10/2026. **TFG-A IN PROGRESS; R-20 EXPIRED / PROPOSED; AWS BLOCKED.**

| Entregable                  | Estado                                                                                                                              |
| --------------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| Aplicación y flujo local    | Implementados; ensayo técnico real del 14/10 PASS.                                                                                  |
| A-S3 — Evidencia y versión  | Cierre histórico conservado; certificación y decisión del candidato nuevo pendientes. |
| Seguridad R-20              | La aceptación anterior venció el 19/10. Nueva propuesta para cuatro IDs y 26 CVE conservadores, `accepted: false`; solo local/sintético. |
| Gates técnicos de seguridad | Escaneos 22/10 del candidato: 0 H/C corregibles y 0 secretos; D094 reproducible PASS. R-23 OPEN; integración y ensayo real pendientes. |
| A-S4 — Memoria              | PDF de 29 páginas preparado; revisión académica pendiente.                                                                          |
| A-S4 — Defensa              | Guion inglés preparado; ensayo humano medido y grabación con voz pendientes.                                                        |
| TFG-A COMPLETE              | Pendiente de todos los criterios y revisión académica.                                                                              |

[Propuesta actual y escaneos](evidence/2026-10-22-security-go-vite/README.md) · [Ensayo técnico histórico y capturas](evidence/2026-10-14-a-s4-runtime/README.md).

## Qué demuestra

```text
Evidence → Decision → Deterministic Recommendation → Human Review
         → Implementation → Result Validation → Ledger → Business Value
```

Treinta hechos sintéticos, 19.440 EUR estimados y 18.960 EUR realizados simulados, con una variación de −480 EUR. ADMIN, PLATFORM_ENGINEER, FINANCE y AUDITOR tienen responsabilidades distintas. La política calcula; el humano conserva autoridad; la IA solo puede explicar opcionalmente.

El ensayo usa aprobación UI y comandos autenticados API para implementación/validación, con lectura posterior en la interfaz. Playwright automatiza el navegador contra servicios reales. No acredita una presentación oral humana ni ahorro real de una empresa.

## Ruta corta hasta terminar

1. **Revisar la memoria:** [PDF congelado](evidence/2026-10-14-a-s4-runtime/IMPERATOR_Memoria_TFG_Local.pdf), [texto editable](08_LOCAL_MVP_MEMORY.md) y [requisitos DAM](11_DAM_MEMORY_DELIVERY.md). Adjuntar la decisión de seguridad del 16/10; el PDF conserva su corte histórico del 14/10.
2. **Ensayar en inglés:** usar el [guion](09_DEFENSE_SCRIPT_12_MIN.md), medir duración y registrar desvíos. Sus 720 segundos son planificación.
3. **Grabar pantalla y voz:** identificar evidencia reproducida o ejecución viva; revisar audio, legibilidad y ausencia de secretos. El clip técnico de 24,64 segundos no tiene audio.
4. **Registrar y revisar:** completar el [formulario humano](evidence/2026-10-14-a-s4-runtime/HUMAN_DEFENSE_REVIEW.md), obtener revisión académica y comprobar el [gate final](10_LOCAL_MVP_RELEASE_GATE.md).

Una nueva ejecución requiere el [preflight](A_S4_LOCAL_DEFENSE.md#preflight-para-otra-ejecución), la aceptación vigente y exactamente los cuatro digests aprobados. La aceptación no se renueva ni se transfiere; AWS, producción, datos reales y exposición pública quedan fuera.

## Mapa documental

| Para                              | Documento                                                                                                                                                |
| --------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| Objetivos, alcance, riesgos y DoD | [Plan maestro](00_TFG_MASTER_PLAN.md), [alcance académico](01_ACADEMIC_SCOPE.md)                                                                         |
| Competencias y requisitos         | [Competencias DAM](02_COMPETENCY_MATRIX.md), [trazabilidad](03_TRACEABILITY_MATRIX.md)                                                                   |
| Resultados y límites verificables | [Registro de evidencias](04_EVIDENCE_REGISTER.md), [decisión R-20](evidence/2026-10-16-r20-local-acceptance/R20_ACCEPTED_DECISION.json)                  |
| Diseño y separación local/cloud   | [Arquitectura](05_ARCHITECTURE.md), [local frente a AWS](06_LOCAL_VS_AWS.md)                                                                             |
| Preparación académica             | [Memoria](08_LOCAL_MVP_MEMORY.md), [guion inglés](09_DEFENSE_SCRIPT_12_MIN.md), [entrega DAM](11_DAM_MEMORY_DELIVERY.md)                                 |
| Gates de salida                   | [A-S3](A_S3_EVIDENCE_VERSIONING.md), [A-S4](A_S4_LOCAL_DEFENSE.md), [seguridad](TFG_A_SECURITY_CLOSURE_GATE.md), [entrega](10_LOCAL_MVP_RELEASE_GATE.md) |

Los packs fechados de `evidence/` conservan los resultados originales; sus estados históricos no sustituyen la decisión vigente. Implementación, prueba local, aceptación de riesgo y aprobación académica son hechos diferentes. No se declara validación AWS por disponer de Terraform, SDKs o workflows.
