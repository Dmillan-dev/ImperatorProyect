# 39 - Persistence Transaction Contract

Sprint: 2.7.5.1 - Persistence Transaction Contract

Status: Baseline candidate for persistence transaction implementation.

## Purpose

Definir los limites logicos de atomicidad de los use cases actuales antes de
introducir implementacion tecnica de transacciones.

Este documento no implementa transacciones ni define mecanismos tecnicos de
ejecucion.

## Scope

Use cases analizados:

- `ImportEvidenceUseCase`
- `CreateDecisionUseCase`
- `GenerateRecommendationUseCase`
- `ReviewDecisionUseCase`
- `AppendLedgerEntryUseCase`

Regla base:

Cada use case es una unidad logica de trabajo. Si un use case necesita escribir
mas de un aggregate o relacion persistente, esas escrituras deben aceptarse como
una unica unidad logica.

## Global Rules

### Public Port Method Rule

Cada nuevo metodo publico en un port o repositorio debe justificarse porque un
use case existente lo necesita.

Nunca se anade un metodo publico por conveniencia futura.

### Write Model Boundary

Los repositorios actuales pertenecen al write model.

No deben crecer con consultas de dashboard, KPI, listados, busqueda, filtros,
ranking o Business Value. Esas necesidades futuras pertenecen a query,
projection o read-model ports separados.

### External Work Boundary

Trabajo externo al estado persistente de IMPERATOR no debe abrir ni compartir
una unidad de atomicidad persistente.

Ejemplos:

- llamadas a proveedores de explicacion,
- conectores externos,
- calculos de lectura futura,
- renderizado UI,
- exportaciones.

## Use Case Contracts

### ImportEvidenceUseCase

Aggregate Root:

- `Evidence`

Repositories involucrados:

- `EvidenceRepository`

Read Only / Read Write:

- Read Write

Unidad logica de atomicidad:

- Importar una evidencia canonica aceptada.

Operaciones que deben pertenecer a la misma transaccion:

- persistir `Evidence`.

Operaciones que nunca deben mezclarse:

- crear `Decision`,
- generar `Recommendation`,
- llamar a proveedores de explicacion,
- anadir `LedgerEntry`,
- calcular o actualizar Business Value,
- ejecutar conectores externos.

### CreateDecisionUseCase

Aggregate Root:

- `Decision`

Aggregates leidos:

- `Evidence`

Repositories involucrados:

- `EvidenceRepository`
- `DecisionRepository`

Read Only / Read Write:

- Read Write

Unidad logica de atomicidad:

- Crear una decision trazable a una evidencia existente.

Operaciones que deben pertenecer a la misma transaccion:

- leer la evidencia originaria requerida para la trazabilidad,
- persistir `Decision`,
- persistir las relaciones internas del aggregate `Decision` con sus evidencias.

Operaciones que nunca deben mezclarse:

- importar nueva evidencia,
- generar recomendacion,
- revisar la decision,
- anadir ledger,
- llamar a proveedores de explicacion,
- actualizar Business Value.

### GenerateRecommendationUseCase

Aggregate Roots:

- `Recommendation`
- `Decision`

Aggregates leidos:

- `Evidence`

Repositories involucrados:

- `DecisionRepository`
- `EvidenceRepository`
- `RecommendationRepository`

Read Only / Read Write:

- Read Write

Unidad logica de atomicidad:

- Crear una recomendacion y enlazarla a la decision correspondiente.

Operaciones que deben pertenecer a la misma transaccion:

- persistir `Recommendation`,
- persistir `Decision` con la referencia a la recomendacion enlazada.

Operaciones que pueden ocurrir antes de la transaccion de escritura:

- cargar `Decision`,
- cargar evidencias requeridas,
- validar trazabilidad de evidencias,
- preparar contexto de explicacion.

Operaciones que nunca deben mezclarse:

- llamada a proveedor de explicacion,
- aprobacion o rechazo de la decision,
- anadir `LedgerEntry`,
- calcular o actualizar Business Value,
- ejecutar conectores externos.

Nota:

La decision es la fuente del limite del aggregate para saber si ya existe una
recomendacion enlazada. El repositorio de recomendaciones no debe buscar por
decision para proteger este limite.

### ReviewDecisionUseCase

Aggregate Root:

- `Decision`

Aggregates leidos:

- `Recommendation`

Repositories involucrados:

- `DecisionRepository`
- `RecommendationRepository`

Read Only / Read Write:

- Read Write

Unidad logica de atomicidad:

- Cambiar el estado de revision de una decision.

Operaciones que deben pertenecer a la misma transaccion:

- cargar `Decision`,
- cargar `Recommendation` necesaria para validar propiedad,
- aplicar la transicion de estado en `Decision`,
- persistir `Decision`.

Operaciones que nunca deben mezclarse:

- crear recomendacion,
- anadir `LedgerEntry`,
- calcular o actualizar Business Value,
- llamar a proveedores de explicacion,
- ejecutar acciones externas.

Nota:

El ledger es una capacidad separada. La revision de decision no debe escribir
historia de ledger dentro de la misma unidad logica.

### AppendLedgerEntryUseCase

Aggregate Root:

- `LedgerEntry`

Aggregates leidos:

- `Decision`
- `Recommendation` cuando aplica
- `Evidence` cuando hay snapshots
- `LedgerEntry` anterior cuando aplica

Repositories involucrados:

- `DecisionRepository`
- `RecommendationRepository`
- `EvidenceRepository`
- `LedgerRepository`

Read Only / Read Write:

- Read Write

Unidad logica de atomicidad:

- Anadir un hecho historico al Decision Ledger.

Operaciones que deben pertenecer a la misma transaccion:

- cargar `Decision`,
- cargar `Recommendation` cuando aplica,
- cargar evidencias snapshot cuando aplican,
- cargar ledger entry anterior cuando aplica,
- construir `LedgerEntry`,
- anadir `LedgerEntry`,
- anadir sus relaciones de evidencia snapshot.

Operaciones que nunca deben mezclarse:

- cambiar estado de `Decision`,
- modificar `Recommendation`,
- calcular o actualizar Business Value,
- llamar a proveedores de explicacion,
- importar evidencia,
- corregir entradas historicas existentes.

Nota:

El ledger es append-only. Si una entrada historica es incorrecta, se anade otra
entrada. Nunca se modifica la anterior.

## Summary Matrix

| Use Case | Main Aggregate | Repositories | Mode | Atomic Work |
|---|---|---|---|---|
| ImportEvidence | Evidence | EvidenceRepository | Read Write | Persist one canonical Evidence |
| CreateDecision | Decision | EvidenceRepository, DecisionRepository | Read Write | Read origin Evidence and persist Decision |
| GenerateRecommendation | Recommendation, Decision | DecisionRepository, EvidenceRepository, RecommendationRepository | Read Write | Persist Recommendation and linked Decision |
| ReviewDecision | Decision | DecisionRepository, RecommendationRepository | Read Write | Transition and persist Decision |
| AppendLedgerEntry | LedgerEntry | DecisionRepository, RecommendationRepository, EvidenceRepository, LedgerRepository | Read Write | Append LedgerEntry and evidence snapshot links |

## Operations That Must Stay Separate

These operation pairs must not share one logical persistence transaction:

| Operation | Must stay separate from |
|---|---|
| Import evidence | Decision creation, recommendation generation, review, ledger append |
| Generate recommendation | Provider explanation call, review, ledger append, Business Value update |
| Review decision | Ledger append, Business Value update, external execution |
| Append ledger entry | Decision state mutation, recommendation mutation, Business Value update |
| Any write use case | Dashboard/read-model aggregation |

## Repository Foundation Gate

| Criterion | Status |
|---|---|
| EvidenceRepository | PASS |
| DecisionRepository | PASS |
| RecommendationRepository | PASS |
| LedgerRepository | PASS |
| Repository Minimalism | PASS |
| Mapper Purity | PASS |
| Persistence Purity | PASS |
| DII | PASS |
| Aggregate Boundaries | PASS |
| CQRS Rule | PASS |
| Ledger Append Rule | PASS |

## Transaction Contract Gate

| Criterion | Status |
|---|---|
| Only existing use cases analyzed | PASS |
| No transaction implementation introduced | PASS |
| No domain changes required | PASS |
| No port changes required | PASS |
| No adapter changes required | PASS |
| No technical transaction mechanism selected | PASS |
| External provider work excluded from persistence atomicity | PASS |
| Ledger remains append-only | PASS |
| Write model remains separate from read model | PASS |

## Next Authorized Step

The next micro-sprint may implement this logical contract in persistence
infrastructure only after this document is accepted.

Recommended next order:

1. Sprint 2.7.5.2 - Persistence Transaction Standardization
2. Sprint 2.7.6 - Schema and Migrations
3. Sprint 2.7.7 - Persistence Integration Tests
4. Sprint 2.8 - REST Adapter Foundation
