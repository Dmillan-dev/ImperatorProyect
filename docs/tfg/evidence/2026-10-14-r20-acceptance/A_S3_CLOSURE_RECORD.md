# A-S3 Cierre formal de evidencia y versión

Fecha: **14/10/2026**. Resultado: **COMPLETE para A-S3 local académico**. [Acta verificable](A_S3_CLOSURE_RECORD.json). Fuente revisada **30a03dab7d26c1ddf3b30c4ea052614efe39c405**, checkout limpio y CI **10/10 PASS**. [R-20 aceptado](R20_ACCEPTED_DECISION.json), cuatro índices/manifiestos exactos y caducidad exclusiva 16/10/2026 00:00 Europe/Madrid. R-23 permanece OPEN con su disposición independiente.

## Aprobación y escaneo son hechos distintos

La aprobación del propietario se refiere al conjunto conservador de 25 CVE / 119 bindings de los informes 13/10, sin reparar CVE ni crear un escaneo. La revalidación independiente 14/10 mantiene 0 H/C corregibles y 0 secretos. Dos CVE GnuTLS pasan de HIGH a LOW en la base del scanner; siguen affected y sin fix. Los 25 identificadores y 119 bindings de paquete/versión aprobados siguen presentes en los mismos cuatro índices. No se reduce ni reemplaza el conjunto aprobado, no se añaden riesgos ni se acepta una imagen nueva.

El primer diagnóstico comparó solo H/C y se detuvo al observar 23 frente a 25. Se conserva intacto. La [revisión posterior](scan-delta-review.json) resuelve la discrepancia comparando los findings aprobados en todas las severidades: exactamente las mismas identidades y cuatro observaciones de severidad menor. La aceptación sigue cubriendo conservadoramente los 25 originales, con la misma caducidad. Las invalidaciones por nuevos riesgos, fixes, cambio de digest/configuración/exposición o caducidad siguen vigentes.

## Evidencia de cierre

- Fuente, índice/plataforma, origen de construcción, rootfs y runtime histórico están enlazados. No se relabelan imágenes como compiladas desde elmain documental ni se transfieren a las reconstruidas por CI.
- Escaneos completos nuevos y bases/motor están conservados, sin suppressions ni ignore-unfixed. npm producción: 0; full audit: 5 High del mismo tooling R-23, sin una versión corregida de braces ni nuevas cadenas. npm propone el mismo cambio mayor de eslint-config-next 16 a 14.2.35 que ya mostraba el 13/10; esa sugerencia no se aplica y no equivale a un parche publicado de braces.
- **858 evidencias anteriores byte-identical**, incluidos propuestas, aceptaciones caducadas, R-23 y receipts. b02e3e2 y sus archivos de versión no se sustituyen.
- Regresión 174/35/41, Playwright 9 PASS / 6 skips y Docker/RBAC/Flyway/persistencia/recovery corresponden al 09/10 y se citan como historia, sin inventar una nueva ejecución funcional en A-S3.

## Gate posterior

A-S4 puede avanzar bajo el preflight exacto y la aceptación vigente. Memoria final, ensayo real humano de 12 minutos, grabación narrada y revisión académica siguen exigidos. **TFG-A IN PROGRESS; AWS BLOCKED.** Cerrar A-S3 no declara COMPLETE el TFG, no valida proveedores y no dispensa los criterios finales.
