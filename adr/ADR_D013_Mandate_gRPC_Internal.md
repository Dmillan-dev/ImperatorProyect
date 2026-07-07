# ADR D013 — Mandato: gRPC + Protobuf para comunicación interna (vinculante)

Date: 2026-07-07
Status: Accepted

## Context

La arquitectura de IMPERATOR separa responsabilidades entre un motor de ingesta (Operational Intelligence Layer) y un motor de razonamiento (AI Intelligence Layer). Se requiere un contrato estable y tipado entre servicios internos escritos en diferentes lenguajes.

## Decision

Se decide que, desde Day‑1, toda comunicación entre servicios internos (ingesta, context engine, AI services, y otros microservicios internos) deberá usar **gRPC** con **Protocol Buffers** como formato canónico de mensajes. Esta decisión se limita a comunicaciones internas — no obliga a exponer gRPC como API pública del SaaS.

## Consecuencias

- Ventajas: contratos tipados, evolución segura de APIs, compatibilidad Java ⇄ Python, facilidad para generar bindings y tests de contrato.
- Requerimientos: mantener un repositorio `proto/` canónico, pipeline de generación de bindings (CI), gobernanza para cambios breaking (versionado y revisión de proto changes).
- Limitación: los clientes externos deberán usar una API pública (HTTP/GraphQL) o un gateway que traduzca a gRPC si es necesario.

## Rationale

El uso obligatorio de gRPC+Protobuf para el plano interno reduce fricción entre equipos, protege la neutralidad del motor de ingesta frente a cambios en los motores de IA, y facilita migraciones y pruebas. Restricción explícita: la decisión no obliga a exponer gRPC a clientes externos.

## Impact

- Crear y mantener el árbol `proto/` con todos los esquemas canónicos.
- Añadir CI que genere artefactos Java/Python y bloquee cambios breaking sin revisión.
- Actualizar documentación de agentes y READMEs para reflejar el mandato.

## Next Steps

1. Registrar D013 en `14_Decision_Log.md`.
2. Completar el set inicial de protos canónicos (Decision, Person, Team, Resource, Agent, Model, Policy, Cost, Incident).
3. Añadir plantilla de CI para generación de bindings y pruebas de compatibilidad.
