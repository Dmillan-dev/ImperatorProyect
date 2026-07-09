# 18 — Architecture Thesis (Conceptual)

## Propósito

Documentar una tesis arquitectural conceptual que alinee responsabilidades, evolución y prioridades sin imponer decisiones de implementación en Phase 0.

## Alcance (Phase 0)

Este documento es conceptual: describe responsabilidades, límites y una hoja de ruta evolutiva. No prescribe detalles de producción ni obliga a un stack concreto. Cualquier cambio en ICP, categoría o wedge debe registrarse en `14_Decision_Log.md`.

## Tesis principal

Separar responsabilidades es más importante que elegir un lenguaje. Recomendación operativa: dividir la plataforma en tres capas lógicas —Operational Layer, Context Layer y AI Layer— y privilegiar responsabilidades, no nombres de lenguaje. Opciones de lenguaje (Java/Python) se presentan como recomendaciones operativas para experimentar, no como requisitos.

## Dos 'cerebros' (resumen)

- Operational Intelligence Layer (ingesta y procesamiento a escala): recibe millones de eventos, valida, normaliza, enriquece y almacena. Diseño optimizado para concurrencia, estabilidad y throughput. Recomendación operativa: soluciones basadas en JVM son una opción madura para esta capa, pero la decisión final queda para etapas posteriores.
- AI Intelligence Layer (razonamiento y experimentación): trabaja sobre datos ya procesados y enriquecidos; responde preguntas de negocio, genera análisis, recomendaciones y explicaciones. Recomendación operativa: ecosistema Python facilita experimentación y adopción temprana de frameworks ML/IA.

## Context Layer (activo estratégico)

Esta capa es el activo diferencial. Debe modelarse como un grafo conceptual de decisiones y relaciones:

```
Decision → Owner → Department → Project → Cloud Resource → Cost → Policy → Risk → Timeline → Evidence
```

Para el MVP, el grafo debe poder expresar una Decision ROI Timeline:

```
Business Context → Code & Deployment → Infrastructure & Cost → AI Consumption → ROI Recommendation
```

El objeto narrativo que atraviesa las capas debe ser el **Decision ROI Case**:

```
Business Decision → Technical Change → Infrastructure → AI Consumption → Recommendation → Result
```

Sistemas iniciales:
- Business Context: Jira
- Code & Deployment: GitHub
- Infrastructure & Cost: AWS
- AI Consumption: OpenAI + Anthropic Claude

Componentes clave:
- Decision Ledger: registro inmutable de decisiones y su metadatos.
- Context Engine: normalización, enrichments, correlación y construcción de contexto reutilizable.
- Knowledge Graph / modelo relacional: representación conceptual de entidades y relaciones (puede comenzar en PostgreSQL, migrable a DB de grafos si se justifica).
- Recommendation Engine: evaluación conceptual de las cinco familias MVP de recuperación económica.

## Integración y flujo (conceptual)

Evento → Ingesta → Decision Pipeline → Decision Ledger → Context Engine → Risk Engine → Knowledge Graph → AI Engine → Recomendaciones → Executive Workspace / Decision Detail

Notas operativas:
- La IA nunca debe operar sobre fuentes crudas; siempre trabaja sobre contexto preparado.
- Comunicación interna: por decisión D013, gRPC con Protocol Buffers es el mandato para futuras comunicaciones internas cuando existan servicios internos. En Phase 0 esto debe tratarse como contrato conceptual, no como obligación de construir servicios.
- Apache Arrow y optimizaciones avanzadas se dejan para fases maduras.

## Comunicación y contratos

- Recomendación operativa: usar **Protocol Buffers** como contrato canónico y **gRPC** para comunicación interna entre servicios cuando la fase de producto lo requiera. Beneficios: tipado fuerte, contratos estables, interoperabilidad entre lenguajes y facilidad para evolucionar el motor de IA sin romper el motor de ingesta.
- Kafka puede transportar mensajes codificados en Protobuf para mantener coherencia de contratos en toda la plataforma (mensajes tipados en broker).
- Nota: estas recomendaciones son **conceptuales** para Phase 0. Registrar cualquier adopción firme en `14_Decision_Log.md`.

## Roadmap evolutivo (sugerido)

- Año 1 (MVP): validar la Decision ROI Timeline con Jira, GitHub, AWS y OpenAI + Anthropic Claude antes de ampliar conectores o stack operativo.
- Año 2 (escala inicial): API Gateway, Kafka para eventos, Java ingestion scalable, Python services desacoplados, Redis para caches, mejoras en Context Engine.
- Año 3 (plataforma): Clústeres de ingesta (Java), clústeres de AI (Python), Knowledge Graph/Vector DB, OpenSearch/analytics, Data Lake.

## Recomendaciones y guardrails

- Mantener la tesis centrada en responsabilidades: "qué hace cada capa" antes de "cómo".
- Evitar decisiones irrevocables en Phase 0; priorizar rapidez para validar producto y GTM.
- Registrar cualquier cambio conceptual mayor en `14_Decision_Log.md`.
- Respetar el lenguaje canónico: `Enterprise Context Intelligence`, `Cross-platform Decision Traceability` y `Decision ROI Timeline`.

## Riesgos y mitigaciones

- Riesgo: proponer stacks demasiado pronto → Mitigación: declarar este documento conceptual y no vinculante.
- Riesgo: dependencia tecnológica que limite neutralidad → Mitigación: modelado por responsabilidades y APIs abiertas.

## Próximos pasos sugeridos

- Revisar esta tesis con stakeholders (producto, plataforma, seguridad, finanzas).
- Si hay acuerdo estratégico, registrar la decisión (entrada D011 añadida a `14_Decision_Log.md`).
- Mantener este documento en Phase 0 como guía de alineación; evolucionarlo con anotaciones de diseño cuando se avance a implementación.
