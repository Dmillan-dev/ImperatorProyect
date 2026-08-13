# Guía para explicar IMPERATOR

## Explicación en una frase

> IMPERATOR es un sistema de inteligencia para decisiones operativas que conecta evidencia técnica, autoridad humana y valor económico verificable.

## Explicación en 30 segundos

> GitHub muestra qué cambió y AWS cuánto cuesta, pero meses después suele ser difícil reconstruir por qué se tomó una decisión, quién la aprobó y qué resultado produjo. IMPERATOR reúne esa evidencia, genera una recomendación determinista con ROI explicable, permite revisión humana y conserva la historia en un Decision Ledger. La IA puede explicar la recomendación, pero no decide ni aprueba.

## Explicación en 90 segundos

> Las empresas ya disponen de datos operativos, pero están repartidos entre código, cloud, tickets, aprobaciones y finanzas. IMPERATOR propone tratarlos como evidencia de una decisión. En el caso sintético DRC-AOA-001, la plataforma relaciona consumo y coste del asistente de onboarding con una recomendación determinista de cambio de modelo. El coste actual es de 2.340 EUR al mes, el proyectado 720 EUR y la recuperación estimada 19.440 EUR al año. Un usuario autorizado revisa la evidencia y decide. La aprobación queda registrada de forma inmutable, y el valor permanece como estimación hasta que una evidencia posterior permite a Finance validar el resultado. El objetivo no es automatizar la autoridad, sino hacer que cada decisión sea buscable, explicable, medible y auditable.

## Recorrido del carrusel

### Página 1 - Executive Decision Workspace

Mensaje:

> Esta pantalla responde qué decisión requiere atención, qué evidencia existe y cuál es el impacto estimado.

Señala:

- una sola decisión, no una cartera ficticia;
- `19.440 EUR/año` como estimación;
- confianza `92%` y riesgo `LOW`;
- valor realizado todavía no disponible.

### Página 2 - Decision Detail

Mensaje:

> Antes de actuar, el revisor puede comprobar la evidencia, la política, la recomendación y el cálculo económico.

Señala:

- 30 Evidence del caso canónico;
- fuentes GitHub y AWS en lectura;
- política `DRC-AOA-001-v1`;
- recomendación `MODEL_CHANGE`;
- autoridad humana.

### Página 3 - Decision Ledger

Mensaje:

> El Ledger registra hechos históricos; no es una tabla CRUD ni puede reescribirse.

Explica:

- append-only;
- actor, momento, motivo y Decision vinculada;
- orden determinista;
- una corrección genera otra entrada, nunca modifica la anterior.

### Página 4 - Business Value

Mensaje:

> IMPERATOR separa estrictamente lo estimado de lo realizado.

Explica:

- ROI determinista;
- aprobación no equivale a ahorro realizado;
- implementación y validación requieren hechos posteriores;
- Finance valida el resultado antes de mostrar Business Value realizado.

### Página 5 - Discovery

Mensaje:

> No estoy pidiendo acceso a sistemas. Estoy buscando entender cómo resolvéis hoy este problema.

Termina con una pregunta, no con una venta:

> ¿Me contarías una decisión reciente que fuera difícil de reconstruir o cuyo resultado económico nunca llegase a comprobarse?

## Demo de 15 minutos

| Tiempo | Contenido | Pregunta al interlocutor |
|---|---|---|
| 0:00-2:00 | Problema y contexto | ¿Os ocurre algo parecido? |
| 2:00-4:00 | Executive Workspace | ¿Qué dato necesitaría ver primero un responsable? |
| 4:00-7:00 | Evidence y Decision Detail | ¿Confiaríais en esta trazabilidad? |
| 7:00-9:00 | Recomendación y ROI | ¿Qué supuesto financiero faltaría? |
| 9:00-11:00 | Aprobación y Ledger | ¿Quién debería tener autoridad? |
| 11:00-13:00 | Business Value | ¿Cómo validáis hoy el resultado? |
| 13:00-15:00 | Discovery | ¿Probaríais un piloto con un único caso? |

## Respuestas a objeciones

**¿Es otra herramienta de IA?**

No. La IA solo puede redactar una explicación. La evidencia, las reglas, el ROI y la autoridad humana no dependen del proveedor de IA.

**¿Automatiza cambios en producción?**

No. El MVP recomienda y registra decisiones. No ejecuta acciones autónomas.

**¿Los 19.440 EUR son ahorro real?**

No. Son una estimación determinista del caso sintético. El valor realizado permanece vacío hasta la validación del resultado.

**¿Está listo para conectarse a nuestra empresa?**

Todavía no. La fase actual busca validar el problema y preparar un piloto controlado antes de utilizar datos reales.

## Idea que debe recordar el interlocutor

> ERP gestiona recursos. CRM gestiona clientes. IMPERATOR gestiona decisiones y demuestra qué evidencia, autoridad y valor existieron detrás de ellas.
