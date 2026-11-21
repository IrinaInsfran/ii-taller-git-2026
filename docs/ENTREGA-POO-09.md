# Entrega POO-09 — APIs cruzadas

- **Alumna:** Irina Insfrán ([IrinaInsfran](https://github.com/IrinaInsfran))
- **Materia:** Lenguaje de Programación 3 — Sección F
- **Repositorio:** <https://github.com/IrinaInsfran/ii-taller-git-2026> (rama `feature/rest`)
- **Commit de la solución:** <https://github.com/IrinaInsfran/ii-taller-git-2026/commit/143f43bfe574075294779da7a028acbc1f2ef061>

## Grupo

| Rol | Integrante | Servicio (Tailscale) | OpenAPI | Regla que consulto |
|---|---|---|---|---|
| **A1** | Irina Insfrán | `http://100.87.95.24:8082` | `http://100.87.95.24:8082/v3/api-docs` | Mis reglas: **mecha corta** (el creeper vivo solo detona con un jugador vivo a 3 bloques o menos) y **onda expansiva** (daño según la distancia, radio 6). |
| **A2** | Cecilia Basualdo | `http://100.122.114.103:8083` | `http://100.122.114.103:8083/v3/api-docs` | `POST /explosiones/validar`: el creeper solo detona si está vivo (422) y solo una vez (409). |
| **A3** | María Sol Ferreira | `http://100.87.131.50:8080` | `http://100.87.131.50:8080/v3/api-docs` | `POST /explosiones/validar`: no detona antes de su tiempo de mecha (409) y cada objetivo tiene que estar dentro del radio (422). |

## Escenario en una oración

Un creeper guardado en mi H2 intenta explotar: antes de tocar la base le pregunto a Cecilia
si está vivo y no detonó y a Sol si ya pasó su tiempo de mecha y los alcanzados están dentro
del radio, y solo si las dos aceptan aplico mis reglas (mecha corta y onda expansiva) y guardo
la explosión, el daño y la jugada; si alguna rechaza, la base queda igual y la respuesta dice
quién fue.

Endpoint: `POST /api/minecraft/jugadas/explosion` con `{"creeperId": 2, "tiempoTranscurrido": 30}`.

| Resultado | HTTP |
|---|---|
| Las dos aceptan | 201, con la respuesta de cada una y la explosión guardada |
| Rechaza Cecilia (A2) | 409, `codigo: rechazada_por_a2`, `origen: A2` |
| Rechaza Sol (A3) | 409, `codigo: rechazada_por_a3`, `origen: A3` |
| Una no responde | 502, `codigo: companera_no_disponible` |

Mi regla 1 también está publicada para ellas: `POST /api/minecraft/reglas/mecha-corta`
(200, 422 `fuera_de_alcance`, 409 `entidad_muerta`; no guarda nada).

## Cómo probarlo

1. Levantar los tres servicios, cada una en su máquina conectada a Tailscale: Cecilia
   (puerto 8083) y Sol (puerto 8080) con `cd minecraft && ./mvnw spring-boot:run`; Irina con
   `./mvnw spring-boot:run` (puerto 8082), con la carpeta `data/` borrada para empezar con la
   base vacía.
2. En Insomnia, importar `insomnia/minecraft-poo09-insomnia.json` y recorrer las carpetas:
   - 0: OpenAPI de las tres;
   - 1: crear Steve, dos creepers y Alex;
   - 2: caso feliz (201);
   - 3: rechazo de Cecilia, repitiendo con el mismo creeper (409, la base no cambia);
   - 4: rechazo de Sol, con `tiempoTranscurrido` 10 (409, la base no cambia);
   - 5: mi regla `mecha-corta`.
3. Las respuestas de ejemplo, grabadas con los servicios reales de Cecilia y Sol, están en
   `insomnia/respuestas-poo09/`.
4. Los tests (`./mvnw test`, 138) cubren el escenario con las compañeras simuladas:
   `JugadaControllerTest` y `ReglaCompaneraClientTest`.

## Corrección de POO-08

Las cuatro observaciones están cerradas; el detalle está en la sección «Corrección de POO-08:
observaciones cerradas» del README.
