# Bitácora de uso de asistentes de IA

Esta bitácora registra qué asistente de IA se usó, con qué modelo exacto y para
qué, en los ejercicios POO-06 a POO-08 de este repositorio. Las decisiones de
diseño y el código entregado son responsabilidad de la autora; el asistente se
usó como revisor, redactor y para escribir código que la autora revisó.

## Sesiones

| Fecha | Asistente (marca) | Modelo de LLM exacto | Ejercicio | Para qué |
|---|---|---|---|---|
| 2026-09-30 | Claude (Anthropic) | Claude Opus 5.5 (`claude-opus-5-5`) | POO-06 | Trabajo de la rama `poo-06-mejoras`: separar paquetes, invariantes, sobrecarga de `mover` y del constructor de `Creeper`, errores 400. |
| 2026-10-04 | Claude (Anthropic), en la app claude.ai | Claude Opus 5.5 (`claude-opus-5-5`) | POO-06 | Revisión de la rama contra el enunciado y la rúbrica antes del merge; redacción de esta bitácora y de la sección del README sobre sobrecarga y sobreescritura. |
| Octubre 2026 | Claude (Anthropic) | Claude Opus 5.5 (`claude-opus-5-5`) | POO-07 / POO-08 | Interfaz `Arma` y familia `Herramienta`; primera versión de H2, CRUD de entidades, `Explosion`, jerarquía de excepciones y sus tests. |
| 2026-11-10 | Claude Code (Anthropic), app de escritorio | Claude Opus 5.5 (`claude-opus-5-5`) | POO-08 | Corregir el trabajo práctico con las correcciones enviadas por el profesor. |

## Resumen de prompts — sesión del 2026-11-10 (Claude Code, Claude Opus 5.5)

1. **Pedido inicial.** Se pegó la consigna de POO-08 (CRUD REST, H2, dos reglas
   con simulación de explosiones, jerarquía de excepciones con un handler por
   tipo, interfaz de dominio y colección de Insomnia) para que verifique hasta donde nos habiamos quedado en la ultia clase y se adjuntó el PDF con la
   corrección de POO-06, pidiendo corregir el trabajo.
2. **Lo que hizo el asistente:**
   - Leyó la corrección, el enunciado, la rúbrica y el template de paquetes, y
     comparó con el código que ya había.
   - **Corrección de POO-06, criterio 2:** renombró los paquetes al template
     (`minecraft` → `domain`, `web` → `rest.controller`, excepciones en
     `exceptions`, `ApiPaths` en `constants`).
   - **Corrección de POO-06, criterio 5:** agregó constructores sobrecargados
     a `Jugador`, `Zombie`, `Esqueleto`, `Aldeano` y `Animal`, y el `POST` los
     usa cuando no se mandan estadísticas.
   - Creó la interfaz `Amenaza` (mobs hostiles y armas) y el endpoint
     `GET /entidades/{id}/amenazas`.
   - Hizo de `Explosion` una entidad persistida, movió la regla de "mecha
     corta" al dominio (`Creeper.elegirObjetivo` / `explotarCercaDe`) y cambió
     la simulación a `POST /api/minecraft/explosiones` con historial.
   - Quitó el `setId` público: el PUT usa `Entidad.reemplazarCon`, que valida
     el tipo y no deja editar a un muerto.
   - Agregó handlers propios para `EntidadMuertaException` (409) y
     `FueraDeAlcanceException` (422).
   - Actualizó y agregó tests (82 en total) y reescribió el README y las
     especificaciones.
3. **Pregunta del asistente.** Preguntó con qué asistente se había hecho el
   trabajo previo, para completar esta tabla sin dejar marcas pendientes, y
   cómo organizar los commits (rama `poo-08-crud-h2`, commits separados).

## Resumen de prompts — sesión del 2026-10-04 (Claude Opus 5.5)

1. **Verificación previa al merge.** Se pidió verificar que el repositorio
   cumpla con el enunciado POO-06, la rúbrica y el template de paquetes. El
   asistente revisó el código, el historial y el README, comparó el diagrama
   Mermaid, con el código, probó los invariantes.
2. **Aclaración de la rama.** Se indicó que la rama a mergear es
   `poo-06-mejoras`; el asistente confirmó que el merge era *fast-forward* y
   listó lo que faltaba (bitácora, licencia en el README, apartado de
   sobrecarga y sobreescritura, enlace al commit).
3. **Redacción.** Se pidió crear esta bitácora y la sección del README
   «Sobrecarga y sobreescritura».

## Qué no hizo el asistente

- No hizo push, merge ni pull requests.
