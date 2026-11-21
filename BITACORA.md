# Bitácora de uso de asistentes de IA

Esta bitácora registra qué asistente de IA se usó, con qué modelo exacto y para
qué, en los ejercicios POO-06 a POO-09 de este repositorio. Las decisiones de
diseño y el código entregado son responsabilidad de la autora; el asistente se
usó como revisor, redactor y para escribir código que la autora revisó.

## Sesiones

| Fecha | Asistente (marca) | Modelo de LLM exacto | Ejercicio | Para qué |
|---|---|---|---|---|
| 2026-09-30 | Claude (Anthropic) | Claude Opus 5.5 (`claude-opus-5-5`) | POO-06 | Trabajo de la rama `poo-06-mejoras`: separar paquetes, invariantes, sobrecarga de `mover` y del constructor de `Creeper`, errores 400. |
| 2026-10-04 | Claude (Anthropic), en la app claude.ai | Claude Opus 5.5 (`claude-opus-5-5`) | POO-06 | Revisión de la rama contra el enunciado y la rúbrica antes del merge; redacción de esta bitácora y de la sección del README sobre sobrecarga y sobreescritura. |
| Octubre 2026 | Claude (Anthropic) | Claude Opus 5.5 (`claude-opus-5-5`) | POO-07 / POO-08 | Interfaz `Arma` y familia `Herramienta`; primera versión de H2, CRUD de entidades, `Explosion`, jerarquía de excepciones y sus tests. |
| 2026-11-10 | Claude Code (Anthropic), app de escritorio | Claude Opus 5.5 (`claude-opus-5-5`) | POO-08 | Corregir el trabajo práctico con las correcciones enviadas por el profesor. |
| 2026-11-19 al 2026-11-21 | Claude Code (Anthropic), app de escritorio | Claude Opus 5.5 (`claude-opus-5-5`) | POO-09 | Cerrar las observaciones de la corrección de POO-08 (reglas y excepciones fuera del dominio, ortografía). |

## Resumen de prompts — sesión del 2026-11-19 al 2026-11-21 (Claude Code, Claude Opus 5.5)

1. **Pedido inicial.** Se pegó la consigna de POO-09 (APIs cruzadas entre A1,
   A2 y A3) y se adjuntó el PDF con la corrección de POO-08 (20/24: 2/3 en
   organización en paquetes y 0/3 en documentación). El asistente leyó la
   corrección, el enunciado, la página de grupos y el ejemplo UNO, y resumió
   qué faltaba antes de tocar nada.
2. **Corrección de POO-08, criterio 2.** Se pidió empezar por la corrección.
   El asistente sacó del dominio las validaciones, las dos reglas y las
   excepciones:
   - creó `ReglasCreeperServiceImpl` (mecha corta y onda expansiva),
     `AccionServiceImpl` (moverse, atacar, huir, infectar, comerciar, montar,
     experiencia) y `HerramientaServiceImpl` (golpear, talar, minar);
   - pasó las validaciones de los constructores a `EntidadServiceImpl.validar`
     y las reglas del PUT a `EntidadServiceImpl.validarReemplazo`;
   - dejó las clases de `domain` con estado privado, sin setters públicos y
     con mensajes que no lanzan excepciones (la salud se acota entre 0 y el
     máximo);
   - movió las pruebas de reglas a `service.impl` (118 tests, todos pasan) y
     le dio a `HerramientaRotaException` su propio handler.
3. **Corrección de POO-08, criterio 8.** Revisó la ortografía de README,
   bitácora, `docs/`, Javadoc, comentarios, mensajes y descripciones de
   OpenAPI con el diccionario `es_PY` de hunspell y leyendo a mano las
   palabras que llevan tilde según el sentido; borró la frase suelta del
   README y agregó la sección «Corrección de POO-08» con cada observación y
   cómo se cerró.

## Resumen de prompts — sesión del 2026-11-10 (Claude Code, Claude Opus 5.5)

1. **Pedido inicial.** Se pegó la consigna de POO-08 (CRUD REST, H2, dos reglas
   con simulación de explosiones, jerarquía de excepciones con un handler por
   tipo, interfaz de dominio y colección de Insomnia) para que verifique hasta
   dónde nos habíamos quedado en la última clase, y se adjuntó el PDF con la
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
   - Actualizó y agregó tests (82 en total), generó la colección de Insomnia
     y la probó contra la app levantada, y reescribió el README y las
     especificaciones.
3. **Pregunta del asistente.** Preguntó con qué asistente se había hecho el
   trabajo previo, para completar esta tabla sin dejar marcas pendientes, y
   cómo organizar los commits (rama `poo-08-crud-h2`, commits separados).
4. **Verificación y entrega.** Se pidió una guía para verificar todo en
   consola y para importar la colección en Insomnia 13 (que tenía un error
   propio al resolver variables), que los commits no lleven al asistente como
   coautor, y una estimación del puntaje según la rúbrica.

## Resumen de prompts — sesión del 2026-10-04 (Claude Opus 5.5)

1. **Verificación previa al merge.** Se pidió verificar que el repositorio
   cumpla con el enunciado POO-06, la rúbrica y el template de paquetes. El
   asistente revisó el código, el historial y el README, comparó el diagrama
   Mermaid con el código y probó los invariantes.
2. **Aclaración de la rama.** Se indicó que la rama a mergear es
   `poo-06-mejoras`; el asistente confirmó que el merge era *fast-forward* y
   listó lo que faltaba (bitácora, licencia en el README, apartado de
   sobrecarga y sobreescritura, enlace al commit).
3. **Redacción.** Se pidió crear esta bitácora y la sección del README
   «Sobrecarga y sobreescritura».

## Qué no hizo el asistente

- No hizo push, merge ni pull requests.
