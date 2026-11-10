# Especificaciones POO-08 — Minecraft: CRUD, H2, explosiones y excepciones

- **Alumna:** Irina Insfrán — LP3, Sección F
- **Repositorio:** https://github.com/IrinaInsfran/ii-taller-git-2026
- **Arranque:** `./mvnw spring-boot:run` (puerto 8080)

## Objetivo

Un servicio HTTP con Spring Boot que guarda en **H2** el mundo de Minecraft que
modelé en POO-06 y POO-07:

1. persiste la familia `Entidad` (padre abstracto con `Jugador`, `Creeper`,
   `Zombie`, `Esqueleto`, `Aldeano`, `Animal`) en una sola tabla, y la clase
   `Explosion` en otra;
2. ofrece un **CRUD REST** completo sobre las entidades;
3. simula **explosiones de creeper** con dos reglas del juego, y el resultado
   queda guardado;
4. expone el protocolo de dominio **`Amenaza`**, que implementan dos familias
   distintas (mobs hostiles y armas);
5. traduce la jerarquía de excepciones del dominio a JSON con **un handler por
   tipo**.

Las tres clases que necesita la simulación son `Creeper`, `Jugador` y
`Explosion`.

## URLs

Base: `http://localhost:8080`

| Verbo | URL | Cuerpo | Respuesta |
|---|---|---|---|
| GET | `/` | — | 200 `{"estado":"API funcionando",...}` |
| POST | `/api/minecraft/entidades` | `EntidadRequest` con `tipo` | 201 + `Location`, la entidad con su `id` |
| GET | `/api/minecraft/entidades` | — | 200, lista guardada en H2 |
| GET | `/api/minecraft/entidades/{id}` | — | 200 o 404 |
| PUT | `/api/minecraft/entidades/{id}` | `EntidadRequest` completo | 200 (reemplaza todo) o 400/404/409 |
| DELETE | `/api/minecraft/entidades/{id}` | — | 204 o 404 |
| GET | `/api/minecraft/entidades/{id}/amenazas?arma=ESPADA&material=DIAMANTE` | — | 200, `List<Amenaza>` evaluada contra la entidad |
| GET | `/api/minecraft/comportamientos` | — | 200, `describirComportamiento()` de cada entidad guardada |
| POST | `/api/minecraft/explosiones` | `{"creeperId": 2}` | 201 con los afectados, o 400/404/409/422 |
| GET | `/api/minecraft/explosiones` | — | 200, historial |
| GET | `/api/minecraft/explosiones/{id}` | — | 200 o 404 |

Campos de `EntidadRequest`: `tipo` (`JUGADOR`, `ZOMBIE`, `ESQUELETO`,
`CREEPER`, `ALDEANO`, `ANIMAL`), `salud`, `posicionX/Y/Z`, `velocidad`, y según
el tipo `nombre`, `nivelEXP`, `rangoDeteccion`, `danoAtaque`,
`tiempoExplosion`, `domesticable`, `profesion`, `montable`. Si no se manda
ninguna estadística se usa el constructor sobrecargado de la clase hija
(valores por defecto del juego).

## Reglas de la simulación

1. **Mecha corta** (`Creeper.elegirObjetivo`): un creeper vivo solo explota si
   hay un jugador vivo a 3 bloques o menos; apunta al más cercano. Si no hay,
   `FueraDeAlcanceException` y nada cambia.
2. **Onda expansiva** (`Explosion.detonar`): el creeper muere y cada entidad
   viva a menos de 6 bloques pierde salud según la distancia
   (`impacto = 1 − d/6`, `daño = (impacto² + impacto)/2 · 42 + 1`: 43 en el
   centro, 16 a 3 bloques, 0 a 6). Una explosión detona una sola vez.

## Invariantes

| Invariante | Quién lo sostiene | Si se rompe |
|---|---|---|
| `salud > 0` al crear; `0 ≤ salud ≤ saludMaxima` siempre | constructor de `Entidad`, `recibirDano`, `curar` | 400 `dato_invalido` |
| `velocidad ≥ 0`, posición y desplazamientos finitos | constructor de `Entidad`, `mover` | 400 |
| `rangoDeteccion ≥ 0`, `danoAtaque > 0`, `tiempoExplosion > 0` | `EntidadHostil`, `Creeper` | 400 |
| nombre de jugador no vacío y único (sin distinguir mayúsculas) | `Jugador`, `EntidadServiceImpl` | 400 `nombre_repetido` |
| `nivelEXP ≥ 0`, sin desbordar `int` | `Jugador` (`Math.addExact`) | 400 |
| el `id` lo asigna H2 y no cambia | `@GeneratedValue`, sin `setId` | — |
| una entidad no cambia de tipo con PUT | `Entidad.reemplazarCon` | 400 `cambio_de_tipo` |
| un muerto no actúa, no explota y no se edita | `Entidad`, `Creeper`, `reemplazarCon` | 409 `entidad_muerta` |
| solo un creeper explota | `ExplosionServiceImpl` | 422 `solo_creepers_explotan` |
| si la simulación falla no cambia nada | `@Transactional` + excepciones no comprobadas | — |
| JPA no puede saltarse los constructores de negocio | constructores vacíos `protected` | — |

## Jerarquía de excepciones

`MinecraftException` (abstracta, `RuntimeException`) →
`DatoInvalidoException` (400), `EntidadNoEncontradaException` (404),
`ReglaDelJuegoException` (422) → `EntidadMuertaException` (409),
`FueraDeAlcanceException` (422), `HerramientaRotaException`. Cada una tiene su
`@ExceptionHandler` en `ManejadorErrores` (`HerramientaRotaException` usa el
de su padre). Cuerpo: `{"error", "codigo", "mensaje"}`.

## Cómo probarlo

1. `./mvnw clean test` — 82 pruebas: dominio sin Spring y la API de punta a
   punta con H2 en memoria.
2. Borrar `data/` (base vacía), `./mvnw spring-boot:run`.
3. Importar `insomnia/minecraft-poo08-insomnia.json` en Insomnia, entorno
   **Local**, y correr las carpetas en orden:
   - **0. Estado**: `GET /` → 200.
   - **1. CRUD**: los `POST` crean los ids 1 a 6 → 201; `GET` lista y por id;
     `PUT` Steve → `nivelEXP` 30 (persiste en el `GET`); `DELETE` vaca → 204 y
     `GET` → 404; `POST` ilegal → 400 y la lista sigue con 5.
   - **2. Protocolo Amenaza**: con espada → Creeper 16, Espada 7, Zombie 3,
     total 26, `sobreviviria: false`.
   - **3. Simulación**: `POST` explosión → 201, Steve queda con salud 4, el
     aldeano y el creeper con `viva: false`, el zombie intacto; el historial
     muestra la explosión con `entidadesAlcanzadas: 2`, `entidadesMuertas: 1`.
   - **4. Excepciones**: 400 `dato_invalido`, 404 `no_encontrada`,
     422 `regla_del_juego`, 409 `entidad_muerta`, 422 `fuera_de_alcance`.
4. Consola H2 en `http://localhost:8080/h2-console` (JDBC URL
   `jdbc:h2:file:./data/entidades`, usuario `sa`): tablas `ENTIDAD` y
   `EXPLOSION`.
