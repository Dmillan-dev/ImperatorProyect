# Defensa local — Guion de doce minutos

**Preparado el 07/10/2026; no ensayado ni grabado como defensa oral.** Los bloques suman 720 segundos. El tiempo combina narración, navegación y lectura; debe medirse con una persona y un cronómetro. El texto no garantiza la duración por su longitud.

## Agenda cronometrada

| Bloque                        | Intervalo   | Segundos | Pantalla / evidencia                               |
| ----------------------------- | ----------- | -------: | -------------------------------------------------- |
| Problema y alcance            | 00:00–01:00 |       60 | Portada y flujo del caso                           |
| Arquitectura y datos          | 01:00–02:00 |       60 | Diagramas de la memoria                            |
| Evidencia y recomendación     | 02:00–04:00 |      120 | Caso compuesto; captura 01; informe de importación |
| Autoridad y aprobación        | 04:00–05:30 |       90 | Captura 02; aserciones 401/403                     |
| Implementación y validación   | 05:30–07:00 |       90 | Capturas 03/04; comandos API identificados         |
| Ledger y valor                | 07:00–08:00 |       60 | Captura 05; persistencia                           |
| Verificación y recuperación   | 08:00–09:00 |       60 | Informes de tests y readiness                      |
| Seguridad y decisión temporal | 09:00–10:15 |       75 | R-20 exacto, R-23 y gate                           |
| IA, AWS y límites             | 10:15–11:15 |       60 | Diagrama de límites; sin invocación externa        |
| Conclusión                    | 11:15–12:00 |       45 | Matriz de objetivos y pendientes                   |

## Narración y acciones

### 00:00–01:00 · Problema y alcance

«IMPERATOR permite reconstruir una decisión operativa: qué hechos la justificaron, qué regla produjo la recomendación, quién la autorizó y qué resultado se validó. El problema no termina al generar una recomendación: también hay que explicar y conservar su responsabilidad.

El MVP presentado cubre un único caso, DRC-AOA-001, con treinta evidencias sintéticas y cuatro roles. La demostración es local. Las cifras que mostraré sirven para evaluar el sistema; no representan ahorros reales de una empresa.»

Mostrar el flujo Evidence → Decision → Recommendation → Human Review → Ledger → Business Value. No dedicar este minuto a enumerar todas las tecnologías.

### 01:00–02:00 · Arquitectura y datos

«La solución utiliza una interfaz Next.js, una API Spring Boot y PostgreSQL. Dentro del backend, el dominio contiene políticas deterministas y los casos de uso coordinan puertos. Los adaptadores de infraestructura no adquieren autoridad sobre el negocio. Esta separación permite probar la política sin Spring o AWS y mantener el monolito sencillo para este alcance.

Flyway aplica las versiones V1 a V3. El Ledger conserva hechos ordenados; las explicaciones opcionales se registran aparte. La cuenta de aplicación y la cuenta de migraciones tienen responsabilidades diferentes.»

Mostrar los dos diagramas de la memoria. Indicar que el modelo conceptual no sustituye las migraciones físicas.

### 02:00–04:00 · Evidencia y recomendación

«El caso importa treinta hechos normalizados y compone una decisión trazable. La política produce la misma recomendación y estimación para la entrada canónica. El resultado mostrado es MODEL_CHANGE y la estimación sintética es 19.440 euros. Esa cifra la calcula el código; no la inventa un modelo generativo.

La recomendación conserva sus referencias de evidencia y supuestos. Podemos inspeccionar la relación entre hechos, decisión y resultado sin depender de una explicación de IA.»

Reservar tiempo para enseñar la importación/composición desde el informe real y abrir la captura 01 o la pantalla correspondiente. Si se usa evidencia grabada, decir expresamente «este es el ensayo ejecutado el 6 de octubre». No afirmar una importación nueva si solo se está leyendo un caso existente. No mostrar tokens, comandos que los contienen o pestañas personales.

### 04:00–05:30 · Autoridad y aprobación

«Los roles limitan las operaciones, y el actor asignado limita quién puede asumir la responsabilidad. Un auditor puede consultar, pero su intento de escritura se deniega sin añadir un hecho al Ledger. Sin autenticación, la API devuelve 401. Para la aprobación debe intervenir la persona con el rol y actor correctos.

Esta decisión humana no se delega al proveedor de explicación. Los controles se aplican en servidor; ocultar un botón en la interfaz no sería suficiente.»

Mostrar la captura 02 y las aserciones del ensayo. Si el runtime está disponible y aprobado, efectuar únicamente la operación prevista en un caso aislado. En modo grabado, describir la aserción histórica; no representar una captura como una interacción nueva.

### 05:30–07:00 · Implementación y validación

«La aprobación no acredita por sí misma un resultado. Primero se registra la implementación y después Finanzas valida el resultado con evidencias posteriores. La secuencia y los actores son parte del contrato.

En este ensayo, esos dos comandos se ejecutaron por la API autenticada y luego se leyeron en la UI. La evidencia no demuestra seleccionarlos mediante el selector de evidencias previamente no vinculadas. Expongo esta limitación para que se entienda exactamente qué comportamiento se verificó.»

Mostrar las capturas 03 y 04 y localizar los pasos correspondientes del informe. No ampliar la interfaz ni preparar otra funcionalidad durante la defensa.

### 07:00–08:00 · Ledger y valor

«El Ledger conserva tres hechos de gobernanza ordenados. Business Value proyecta el resultado validado: 18.960 euros sintéticos, frente a una estimación de 19.440. La diferencia es menos 480 euros. Distinguir previsto y realizado evita presentar una expectativa como un resultado confirmado.

El control append-only se verificó con los permisos de la cuenta de aplicación y pruebas de persistencia. No estoy afirmando que un administrador de base de datos no pueda modificar físicamente los datos.»

Mostrar captura 05 y el resultado de persistencia tras reemplazar los tres contenedores.

### 08:00–09:00 · Verificación y recuperación

«La evaluación local incluye 174 tests unitarios backend, 35 de integración PostgreSQL y 41 frontend. Playwright tiene nueve casos aprobados y seis skips intencionales, que no cuento como aprobados. Las pruebas con fixtures se complementan con un ensayo API/UI real en Docker.

Se verificaron Flyway V1 a V3, persistencia tras reemplazo y la transición de readiness cuando la base de datos deja de estar disponible. Liveness y readiness responden a preguntas diferentes: el proceso puede seguir vivo aunque temporalmente no esté preparado para servir.»

Mostrar resúmenes e informes fechados. No usar un badge como prueba de CI del SHA nuevo.

### 09:00–10:15 · Seguridad y decisión temporal

«Los parches autorizados dejan cero High/Critical corregibles en producción y cero secretos en los escaneos registrados. El informe completo conserva 23 CVE residuales, con 115 filas por paquete e imagen. No afirmo que sean inexplotables.

R-20 registra una aceptación condicionada a estos digests, al entorno local y a datos sintéticos. Caduca el 9 de octubre de 2026 a las cero horas, Europe/Madrid, y no se renueva ni transfiere automáticamente. R-23 permanece abierto: braces está en el tooling de desarrollo y no aparece como paquete en la imagen de producción inspeccionada. La disposición de ese riesgo es independiente.

Los hashes enlazan fuente, informes y capturas. El SHA del código y los digests de las imágenes son identidades distintas; una nueva compilación no hereda esta aceptación.»

Mostrar la decisión explícita y sus límites. Antes de cualquier ejecución comprobar que aún está vigente; después de caducar no presentar el runtime como actualmente autorizado.

### 10:15–11:15 · IA, AWS y límites

«La IA está limitada a explicar una recomendación ya calculada. No decide, no aprueba, no cambia el ROI y no escribe el Ledger. El adaptador y sus casos de fallo están implementados y probados offline; aquí no se ha efectuado una llamada real a Bedrock.

AWS es una arquitectura objetivo, con Terraform y evidencia offline. No presento un despliegue, coste medido ni rollback cloud. La propuesta tiene compromisos de disponibilidad y necesita resolver sus gates de entrega e identidad externa antes de avanzar. Esta defensa funciona con archivos locales sin AWS, Keycloak, Bedrock o acceso a GitHub.»

No abrir precios, consolas cloud ni endpoints externos. Si el tutor exige AWS, declarar ese requisito pendiente; no redefinirlo como satisfecho por el diseño.

### 11:15–12:00 · Conclusión

«El resultado demuestra un ciclo completo de trazabilidad: hechos, política, responsabilidad humana, historial y valor validado. Su contribución está en la coherencia del contrato y en la evidencia que permite evaluar cada afirmación.

El alcance local tiene limitaciones explícitas: aceptación temporal de seguridad, riesgo de tooling abierto y ausencia de validación cloud e identidad externa. La entrega debe vincularse a un SHA revisado y sus checks efectivos. El siguiente trabajo es finalizar revisión y defensa; cualquier evolución cloud requiere una decisión separada.»

Cerrar mostrando la matriz de requisitos y el estado real de la entrega, sin declarar una release operativa o aprobación académica no obtenida.

## Respuestas breves para el tribunal

| Pregunta                  | Respuesta sustentada                                                                                                                       |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------ |
| ¿Por qué un monolito?     | Un caso y transacciones relacionadas; reduce coordinación y despliegues. No se necesita separar servicios para demostrar los requisitos.   |
| ¿Qué aporta IA?           | Explicación opcional de un resultado ya calculado, con validación y fallo aislado. El resultado determinista permanece útil sin proveedor. |
| ¿Es segura la aplicación? | Tiene controles y evaluación explícita; quedan CVE aceptadas temporalmente. No afirmo seguridad absoluta.                                  |
| ¿Por qué no cero CVE?     | Se remedió lo corregible autorizado y se evaluó el residuo; no se eliminaron paquetes ni cambiaron bases para manipular el contador.       |
| ¿El Ledger es inmutable?  | Append-only bajo la identidad y contrato de aplicación verificados; no ofrece una garantía criptográfica frente al propietario de la base. |
| ¿Se ha desplegado AWS?    | No. Es arquitectura e infraestructura probada offline, con gates pendientes.                                                               |
| ¿Cuánto costó AWS?        | No hay coste medido ni estimación vigente aprobada en esta fase.                                                                           |
| ¿Puede repetirse?         | Los informes, fuente exacta y manifests identifican el ensayo; su ejecución requiere imágenes disponibles y autorización vigente.          |

## Registro pendiente del ensayo humano

- Duración real, desviación por bloque y correcciones: **PENDIENTES**.
- Grabación final con narración y revisión visual: **PENDIENTE**.
- SHA aceptado, digests comprobados y modo de demo usado: **PENDIENTES de cierre A-S3 y preflight**.
- Evaluación por tutor/centro: **PENDIENTE**.
