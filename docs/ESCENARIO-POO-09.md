# Escenario POO-09: diagramas de secuencia

Caso de uso: **un creeper guardado intenta explotar y pide permiso a A2 y A3.**

`POST /api/minecraft/jugadas/explosion` con `{"creeperId": 2, "tiempoTranscurrido": 30}`.
La explosión se guarda en H2 solo si Cecilia (A2) y Sol (A3) aceptan y, después, mis
reglas (mecha corta y onda expansiva) también. Si algo falla, H2 queda igual.

| # | Caso | Código | ¿Consulta a A2 y A3? | ¿Cambia H2? |
|---|---|---|---|---|
| 1 | [Camino feliz](#1-camino-feliz-201-created) | **201** | Las dos | Sí: explosión, daño y jugada |
| 2 | [Falta un dato](#2-falta-un-dato-400-bad-request) | **400** `dato_invalido` | No | No |
| 3 | [El creeper no existe](#3-el-creeper-no-existe-404-not-found) | **404** `no_encontrada` | No | No |
| 4 | [No es un creeper o no hay nadie en el radio](#4-no-es-un-creeper-o-no-hay-nadie-en-el-radio-422) | **422** `regla_del_juego` / `fuera_de_alcance` | No | No |
| 5 | [Rechaza Cecilia (A2)](#5-rechaza-cecilia-a2-409-conflict) | **409** `rechazada_por_a2` | Solo A2 | No |
| 6 | [Rechaza Sol (A3)](#6-rechaza-sol-a3-409-conflict) | **409** `rechazada_por_a3` | Las dos | No |
| 7 | [Una compañera no responde](#7-una-compañera-no-responde-502-bad-gateway) | **502** `companera_no_disponible` | Hasta la que falla | No |
| 8 | [Las dos aceptan pero rechaza mi regla](#8-las-dos-aceptan-pero-rechaza-mi-regla-422-rollback) | **422** `fuera_de_alcance` | Las dos | No (rollback) |

Participantes:

- **Cliente:** Insomnia, `curl` o Swagger UI.
- **JugadaController:** recibe el pedido y arma el 201 con `Location`.
- **JugadaServiceImpl:** orquesta el escenario. Las llamadas HTTP van **fuera** de la transacción.
- **H2:** los repositorios `EntidadRepository`, `ExplosionRepository` y `JugadaRepository`.
- **Cecilia (A2):** `CeciReglasClient` → `POST /explosiones/validar` en `100.122.114.103:8083`.
- **Sol (A3):** `SolReglasClient` → `POST /explosiones/validar` en `100.87.131.50:8080`.
- **ExplosionService:** aplica mis reglas con `ReglasCreeperService` (mecha corta y onda expansiva).
- **ManejadorErrores:** traduce cada excepción a su código HTTP.

---

## 1. Camino feliz (201 Created)

Las dos compañeras aceptan y mis reglas encuentran un jugador vivo a 3 bloques o menos.

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant DB as H2
    participant A2 as Cecilia (A2)
    participant A3 as Sol (A3)
    participant E as ExplosionService

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 2, tiempoTranscurrido: 30}
    Ctrl->>S: registrarExplosion(pedido)
    S->>DB: buscar creeper 2 y entidades vivas a 6 bloques o menos
    DB-->>S: creeper y objetivos (Steve)
    S->>A2: POST /explosiones/validar<br/>saludCreeper 20, yaDetonado false
    A2-->>S: 200 aceptada
    S->>A3: POST /explosiones/validar<br/>tiempo 30 de 30, radio 6, objetivos
    A3-->>S: 200 aceptada
    rect rgba(120, 180, 120, 0.15)
        Note over S,E: Una sola transacción
        S->>E: detonarCreeper(2)
        E->>E: mecha corta: apunta a Steve<br/>onda expansiva: daño según la distancia
        E->>DB: guardar explosión y daño
        S->>DB: guardar Jugada con las respuestas de A2 y A3
    end
    S-->>Ctrl: JugadaResponse
    Ctrl-->>C: 201 Created<br/>Location: /api/minecraft/jugadas/1
```

Respuesta (resumida; la completa está en
[`insomnia/respuestas-poo09/01-caso-feliz-201.json`](../insomnia/respuestas-poo09/01-caso-feliz-201.json)):

```json
{
  "id": 1, "creeperId": 2, "tiempoTranscurrido": 30,
  "a2": {"rol": "A2", "companera": "Cecilia", "estado": 200,
         "respuesta": {"aceptada": true, "mensaje": "El Creeper está vivo y todavía no detonó."}},
  "a3": {"rol": "A3", "companera": "Sol", "estado": 200,
         "respuesta": {"aceptada": true, "mensaje": "La explosión cumple el tiempo de detonación y todos los objetivos están dentro del radio."}},
  "explosion": {"id": 1, "jugadorObjetivo": "Steve", "radio": 6.0, "danoTotal": 41,
                "afectados": [{"tipo": "JUGADOR", "distancia": 2.0, "dano": 24, "murio": true}]}
}
```

---

## 2. Falta un dato (400 Bad Request)

El pedido no trae `tiempoTranscurrido` (o es negativo, o falta `creeperId`). Se corta antes
de leer la base y sin consultar a nadie.

```mermaid
sequenceDiagram
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant M as ManejadorErrores

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 2}
    Ctrl->>S: registrarExplosion(pedido)
    S->>S: validar(pedido): falta tiempoTranscurrido
    S--xCtrl: DatoInvalidoException (campo_obligatorio)
    Ctrl->>M: la excepción sube al handler
    M-->>C: 400 Bad Request
    Note over C,M: error dato_invalido, codigo campo_obligatorio<br/>«El campo 'tiempoTranscurrido' es obligatorio.»<br/>H2 no se leyó, A2 y A3 no se consultaron
```

---

## 3. El creeper no existe (404 Not Found)

```mermaid
sequenceDiagram
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant DB as H2
    participant M as ManejadorErrores

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 999, tiempoTranscurrido: 30}
    Ctrl->>S: registrarExplosion(pedido)
    S->>DB: findById(999)
    DB-->>S: vacío
    S--xCtrl: EntidadNoEncontradaException
    Ctrl->>M: la excepción sube al handler
    M-->>C: 404 Not Found
    Note over C,M: error no_encontrada, codigo entidad_no_encontrada<br/>«No existe una entidad con id 999.»<br/>A2 y A3 no se consultaron
```

---

## 4. No es un creeper o no hay nadie en el radio (422)

Las dos situaciones se detectan leyendo H2, antes de consultar a las compañeras: no hay una
jugada que valga la pena validar.

```mermaid
sequenceDiagram
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant DB as H2
    participant M as ManejadorErrores

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 1, tiempoTranscurrido: 30}
    Ctrl->>S: registrarExplosion(pedido)
    S->>DB: findById(1)
    DB-->>S: entidad 1
    alt la entidad no es un CREEPER (por ejemplo, Steve)
        S--xCtrl: ReglaDelJuegoException (solo_creepers_explotan)
        Ctrl->>M: la excepción sube al handler
        M-->>C: 422 Unprocessable Content<br/>«La entidad 1 es JUGADOR: solo un CREEPER puede explotar.»
    else es un creeper, pero no hay entidades vivas a 6 bloques o menos
        S->>DB: entidades vivas del radio
        DB-->>S: ninguna
        S--xCtrl: FueraDeAlcanceException
        Ctrl->>M: la excepción sube al handler
        M-->>C: 422 Unprocessable Content<br/>«No hay ninguna entidad viva a 6 bloques o menos del creeper.»
    end
    Note over C,M: A2 y A3 no se consultaron, H2 queda igual
```

---

## 5. Rechaza Cecilia (A2) (409 Conflict)

El creeper 2 ya explotó en el caso feliz: está muerto y tiene una explosión guardada. Cecilia
lo rechaza y **a Sol no se le pregunta**, así el rechazo tiene un solo origen.

```mermaid
sequenceDiagram
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant DB as H2
    participant A2 as Cecilia (A2)
    participant A3 as Sol (A3)
    participant M as ManejadorErrores

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 2, tiempoTranscurrido: 30}
    Ctrl->>S: registrarExplosion(pedido)
    S->>DB: creeper 2, entidades del radio y explosiones del creeper
    DB-->>S: salud 0, ya tiene una explosión
    S->>A2: POST /explosiones/validar<br/>saludCreeper 0, yaDetonado true
    A2-->>S: 422 CreeperMuertoException
    S--xCtrl: JugadaRechazadaException (origen A2)
    Ctrl->>M: la excepción sube al handler
    M-->>C: 409 Conflict
    Note over A3: no se consulta
    Note over C,M: error jugada_rechazada, codigo rechazada_por_a2<br/>origen A2, estadoCompanera 422, respuestaCompanera sin modificar<br/>H2 queda igual
```

Respuesta real
([`02-rechazo-a2-ceci-409.json`](../insomnia/respuestas-poo09/02-rechazo-a2-ceci-409.json)):

```json
{
  "error": "jugada_rechazada", "codigo": "rechazada_por_a2",
  "origen": "A2", "companera": "Cecilia", "estadoCompanera": 422,
  "respuestaCompanera": {"status": 422, "error": "Unprocessable Entity",
                         "tipo": "CreeperMuertoException", "mensaje": "Creeper no puede detonar: está muerto."},
  "mensaje": "Cecilia (A2) rechazó la jugada: Creeper no puede detonar: está muerto. La jugada no se guardó."
}
```

---

## 6. Rechaza Sol (A3) (409 Conflict)

Cecilia acepta, pero la mecha se encendió hace 10 y el creeper necesita 30.

```mermaid
sequenceDiagram
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant DB as H2
    participant A2 as Cecilia (A2)
    participant A3 as Sol (A3)
    participant M as ManejadorErrores

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 3, tiempoTranscurrido: 10}
    Ctrl->>S: registrarExplosion(pedido)
    S->>DB: creeper 3 y entidades del radio
    DB-->>S: creeper vivo, Alex a 2 bloques
    S->>A2: POST /explosiones/validar
    A2-->>S: 200 aceptada
    S->>A3: POST /explosiones/validar<br/>tiempoExplosion 30, tiempoTranscurrido 10
    A3-->>S: 409 Explosión no preparada
    S--xCtrl: JugadaRechazadaException (origen A3)
    Ctrl->>M: la excepción sube al handler
    M-->>C: 409 Conflict
    Note over C,M: error jugada_rechazada, codigo rechazada_por_a3<br/>«Sol (A3) rechazó la jugada: El Creeper necesita 30 segundos<br/>para explotar, pero solo transcurrieron 10.»<br/>H2 queda igual: Alex sigue con la salud de antes (13)
```

---

## 7. Una compañera no responde (502 Bad Gateway)

La API de una compañera está apagada, se vence el tiempo de espera (5 s, `COMPANERAS_TIMEOUT`)
o responde 5xx. Como no se sabe si su regla acepta, no se guarda nada. El ejemplo muestra a
Cecilia caída; si la que falla es Sol, el diagrama es igual pero después del 200 de Cecilia.

```mermaid
sequenceDiagram
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant DB as H2
    participant A2 as Cecilia (A2)
    participant A3 as Sol (A3)
    participant M as ManejadorErrores

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 2, tiempoTranscurrido: 30}
    Ctrl->>S: registrarExplosion(pedido)
    S->>DB: creeper y entidades del radio
    DB-->>S: creeper y objetivos
    S-xA2: POST /explosiones/validar
    Note over S,A2: sin conexión o sin respuesta en 5 s
    S--xCtrl: CompaneraNoDisponibleException (origen A2)
    Ctrl->>M: la excepción sube al handler
    M-->>C: 502 Bad Gateway
    Note over A3: no se consulta
    Note over C,M: error companera_no_disponible, codigo companera_no_disponible<br/>origen A2, estadoCompanera null<br/>H2 queda igual
```

---

## 8. Las dos aceptan pero rechaza mi regla (422, rollback)

Steve está a 5 bloques: dentro del radio de la onda expansiva (6), así que hay jugada para
consultar y las compañeras aceptan, pero lejos para la mecha corta (3). Mi regla falla dentro
de la transacción y se deshace todo.

```mermaid
sequenceDiagram
    actor C as Cliente
    participant Ctrl as JugadaController
    participant S as JugadaServiceImpl
    participant DB as H2
    participant A2 as Cecilia (A2)
    participant A3 as Sol (A3)
    participant E as ExplosionService
    participant M as ManejadorErrores

    C->>Ctrl: POST /api/minecraft/jugadas/explosion<br/>{creeperId: 2, tiempoTranscurrido: 30}
    Ctrl->>S: registrarExplosion(pedido)
    S->>DB: creeper y entidades del radio
    DB-->>S: Steve a 5 bloques
    S->>A2: POST /explosiones/validar
    A2-->>S: 200 aceptada
    S->>A3: POST /explosiones/validar
    A3-->>S: 200 aceptada
    rect rgba(220, 120, 120, 0.15)
        Note over S,E: Transacción
        S->>E: detonarCreeper(2)
        E->>E: mecha corta: ningún jugador a 3 bloques o menos
        E--xS: FueraDeAlcanceException
        Note over S,DB: rollback: no se guarda la explosión, ni el daño, ni la jugada
    end
    S--xCtrl: FueraDeAlcanceException
    Ctrl->>M: la excepción sube al handler
    M-->>C: 422 Unprocessable Content
    Note over C,M: error fuera_de_alcance<br/>«El creeper solo detona con un jugador vivo a 3 bloques o menos, y no hay ninguno.»
```

Si el creeper estuviera muerto y A2 igual lo aceptara, mi regla respondería **409**
`entidad_muerta` por el mismo camino. Con Cecilia como A2 eso no pasa: ella rechaza antes
(caso 5).

---

Cada caso está cubierto por un test de `JugadaControllerTest`:

| Caso | Test |
|---|---|
| 1 | `siLasDosAceptanSeGuardaLaJugadaYLaExplosion` |
| 2, 3, 4 | `datosFaltantesOInvalidos`, `sinNadieEnElRadioNoSeConsultaANadie` |
| 5 | `siCeciRechazaNoSeConsultaASolYNoCambiaNada` |
| 6 | `siSolRechazaNoCambiaNada` |
| 7 | `siUnaCompaneraNoRespondeDevuelve502` |
| 8 | `siLasDosAceptanPeroMiReglaRechazaSeDeshaceTodo` |
