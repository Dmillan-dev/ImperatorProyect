# ADR D012 — Usar gRPC y Protocol Buffers desde el día uno (recomendación no vinculante)

Date: 2026-07-07
Status: Proposed

## Context

IMPERATOR separará responsabilidades entre un motor de ingesta (Operational Intelligence Layer) y un motor de razonamiento (AI Intelligence Layer). La interoperabilidad entre componentes escritos en diferentes lenguajes (p. ej. Java y Python) exige contratos claros y estables.

## Decision

Recomendar usar Protocol Buffers como esquema de mensajes y gRPC como protocolo de comunicación entre servicios críticos (especialmente Java ⇄ Python) desde el inicio del desarrollo, y codificar mensajes de Kafka en Protobuf para coherencia global.

## Consecuencia

- Ventajas: contratos tipados, evolución segura de APIs, interoperabilidad entre lenguajes, facilidad para sustituir motores internos.
- Costes: requiere definir esquemas `.proto` y generar artefactos; curva de aprendizaje inicial frente a enfoques basados en JSON.
- Mitigación: mantener la recomendación como no vinculante en Phase 0; adoptar gradualmente según validación de producto y volumen.

## Rationale

gRPC+Protobuf asegura un contrato estable entre Java y Python, mejora el rendimiento de las llamadas RPC y facilita la compatibilidad futura (p. ej. cambiar el motor de IA). Usar Protobuf en Kafka reduce discrepancias de formato entre productores/consumidores.

## Next Steps

1. Si aprobada, registrar la decisión en `14_Decision_Log.md` (D012).
2. Crear un repositorio `proto/` con primeros mensajes (p. ej. `Decision.proto`).
3. Generar artefactos en Java y Python y validar un ejemplo end-to-end entre ingestion → AI service.
