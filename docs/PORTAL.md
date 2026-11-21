# Caso de uso: Portal al mundo de Ceci

`POST /api/minecraft/entidades/{id}/portal[?conservar=true]`

Una entidad de este mundo viaja al mundo Minecraft de Ceci (otro proyecto del
taller, URL en `mundo-ceci.base-url` / variable `MUNDO_CECI_URL`). Cada
proyecto pone sus reglas:

| Quién | Regla |
|---|---|
| Este proyecto | La entidad tiene que existir, estar viva y ser JUGADOR, ZOMBIE o CREEPER. |
| Mundo de Ceci | Sus validaciones del CRUD: nombre único, vida ≤ vidaMaxima, nivel y experiencia no negativos. |
| Este proyecto | Si el otro mundo la acepta y no se pidió `conservar=true`, la entidad sale de este mundo. |

Clases que participan: `PortalController` → `PortalServiceImpl` →
`EntidadRepository` (H2 propia) y `MundoCeciClient` (WebClient) →
API de Ceci (`POST /entidades`, con su propia H2).

## Índice de respuestas

| Código | Caso | ¿Se llama a Ceci? | ¿La entidad sigue acá? |
|---|---|---|---|
| **200** | [Camino feliz: la entidad viaja](#200--camino-feliz-la-entidad-viaja) | Sí | No |
| **200** | [Variante: `conservar=true`, se copia](#200--variante-conservartrue-se-copia) | Sí | Sí |
| **400** | [Parámetro inválido](#400--parámetro-inválido) | No | Sí |
| **404** | [La entidad no existe](#404--la-entidad-no-existe) | No | — |
| **409** | [La entidad está muerta](#409--la-entidad-está-muerta) | No | Sí |
| **422** | [El tipo no existe en el mundo de Ceci](#422--el-tipo-no-existe-en-el-mundo-de-ceci) | No | Sí |
| **422** | [El mundo de Ceci la rechaza](#422--el-mundo-de-ceci-la-rechaza) | Sí | Sí |
| **502** | [El mundo de Ceci falla o no responde](#502--el-mundo-de-ceci-falla-o-no-responde) | Sí | Sí |
| **500** | [No cubierto A: falla el commit propio](#no-cubierto-a--500-falla-el-commit-propio) | Sí | Sí, **y también allá** |
| **502 → 422** | [No cubierto B: timeout, pero Ceci la guardó](#no-cubierto-b--502-y-después-422-timeout-pero-ceci-la-guardó) | Sí | Sí, **y también allá** |

Todos los errores responden con el mismo cuerpo `ErrorResponse`:
`{"error": "...", "codigo": "...", "mensaje": "..."}`.

---

## 200 · Camino feliz: la entidad viaja

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PC as PortalController
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant MC as MundoCeciClient<br/>(WebClient)
    participant API as API de Ceci<br/>POST /entidades

    C->>PC: POST /api/minecraft/entidades/1/portal
    PC->>PS: cruzar(1, conservar = false)
    PS->>R: findById(1)
    R-->>PS: Jugador "Steve" vivo
    PS->>PS: traducir(entidad)
    PS->>MC: crearEntidad(Jugador "Steve")
    MC->>API: POST /entidades
    API-->>MC: 200 entidad con id 42
    MC-->>PS: EntidadMundoCeci(id = 42)
    PS->>R: delete(entidad)
    PS-->>PC: PortalResponse
    PC-->>C: 200 OK
```

Respuesta:

```json
{
  "entidadId": 1,
  "tipo": "JUGADOR",
  "conservadaEnEsteMundo": false,
  "mundoDestino": "http://localhost:8081",
  "entidadEnDestino": {
    "id": 42, "tipo": "Jugador", "nombre": "Steve",
    "vida": 20, "vidaMaxima": 20, "x": 1.0, "y": 64.0, "z": 2.0,
    "nivel": 3, "experiencia": 0
  }
}
```

`traducir` convierte salud → vida, saludMaxima → vidaMaxima y nivelEXP →
nivel. Los mobs, que no tienen nombre en este proyecto, viajan como
`Zombie-{id}` o `Creeper-{id}`.

## 200 · Variante: `conservar=true`, se copia

Igual al camino feliz, pero la entidad no se borra: queda en los dos mundos.

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant MC as MundoCeciClient
    participant API as API de Ceci

    C->>PS: POST .../entidades/5/portal?conservar=true
    PS->>R: findById(5)
    R-->>PS: Zombie vivo
    PS->>MC: crearEntidad(Zombie "Zombie-5")
    MC->>API: POST /entidades
    API-->>MC: 200 entidad con id 43
    MC-->>PS: EntidadMundoCeci(id = 43)
    Note over PS,R: No se llama a delete:<br/>el zombie sigue en este mundo
    PS-->>C: 200 OK, conservadaEnEsteMundo = true
```

## 400 · Parámetro inválido

El `id` no es un número o `conservar` no es `true`/`false`. Spring rechaza
el pedido antes de llegar al controller.

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant S as Spring MVC
    participant ME as ManejadorErrores

    C->>S: POST .../entidades/1/portal?conservar=quizas
    S->>S: convertir "quizas" a boolean
    S-->>ME: MethodArgumentTypeMismatchException
    ME-->>C: 400 parametro_invalido
    Note over C,ME: No se ejecuta PortalController<br/>ni se llama a Ceci
```

```json
{"error": "dato_invalido", "codigo": "parametro_invalido",
 "mensaje": "El valor 'quizas' no sirve para el parámetro 'conservar'."}
```

## 404 · La entidad no existe

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PC as PortalController
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant ME as ManejadorErrores

    C->>PC: POST .../entidades/999/portal
    PC->>PS: cruzar(999, false)
    PS->>R: findById(999)
    R-->>PS: Optional.empty
    PS-->>ME: EntidadNoEncontradaException
    ME-->>C: 404 entidad_no_encontrada
```

```json
{"error": "no_encontrada", "codigo": "entidad_no_encontrada",
 "mensaje": "No existe una entidad con id 999."}
```

## 409 · La entidad está muerta

Regla de este proyecto: un muerto no cruza el portal.

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PC as PortalController
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant ME as ManejadorErrores

    C->>PC: POST .../entidades/3/portal
    PC->>PS: cruzar(3, false)
    PS->>R: findById(3)
    R-->>PS: Creeper con salud 0
    PS->>PS: estaViva() es false
    PS-->>ME: EntidadMuertaException
    ME-->>C: 409 entidad_muerta
    Note over C,ME: No se llama a Ceci
```

```json
{"error": "entidad_muerta", "codigo": "entidad_muerta",
 "mensaje": "La entidad 3 está muerta: un muerto no puede cruzar el portal."}
```

## 422 · El tipo no existe en el mundo de Ceci

Regla de este proyecto: en el mundo de Ceci solo hay Jugador, Zombie y
Creeper, así que ESQUELETO, ALDEANO y ANIMAL no pueden viajar.

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PC as PortalController
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant ME as ManejadorErrores

    C->>PC: POST .../entidades/2/portal
    PC->>PS: cruzar(2, false)
    PS->>R: findById(2)
    R-->>PS: Animal vivo
    PS->>PS: traducir(entidad): no hay equivalente
    PS-->>ME: ReglaDelJuegoException
    ME-->>C: 422 tipo_inexistente_en_destino
    Note over C,ME: No se llama a Ceci
```

```json
{"error": "regla_del_juego", "codigo": "tipo_inexistente_en_destino",
 "mensaje": "Un ANIMAL no puede cruzar: en el mundo de Ceci solo existen JUGADOR, ZOMBIE y CREEPER."}
```

## 422 · El mundo de Ceci la rechaza

Regla del otro proyecto: sus validaciones dicen que no (por ejemplo, ya hay
una entidad con ese nombre). Ceci responde 400 y acá se traduce a 422, porque
los datos del pedido estaban bien, pero el otro mundo no permite la acción.

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant MC as MundoCeciClient
    participant API as API de Ceci
    participant ME as ManejadorErrores

    C->>PS: POST .../entidades/1/portal
    PS->>R: findById(1)
    R-->>PS: Jugador "Steve" vivo
    PS->>MC: crearEntidad(Jugador "Steve")
    MC->>API: POST /entidades
    API->>API: validar: ya existe "Steve"
    API-->>MC: 400 "Ya existe una entidad con ese nombre."
    MC-->>ME: ReglaDelJuegoException
    Note over PS,R: ROLLBACK: la entidad sigue en este mundo
    ME-->>C: 422 rechazada_por_mundo_ceci
```

```json
{"error": "regla_del_juego", "codigo": "rechazada_por_mundo_ceci",
 "mensaje": "El mundo de Ceci rechazó la entidad: Ya existe una entidad con ese nombre."}
```

## 502 · El mundo de Ceci falla o no responde

La app de Ceci está apagada, responde con 5xx o tarda más de
`mundo-ceci.timeout` (5 s).

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant MC as MundoCeciClient
    participant API as API de Ceci
    participant ME as ManejadorErrores

    C->>PS: POST .../entidades/1/portal
    PS->>R: findById(1)
    R-->>PS: Jugador "Steve" vivo
    PS->>MC: crearEntidad(Jugador "Steve")
    MC-xAPI: POST /entidades
    Note over MC,API: Conexión rechazada, 5xx<br/>o más de 5 s sin respuesta
    MC-->>ME: ServicioExternoException
    Note over PS,R: ROLLBACK: la entidad sigue en este mundo
    ME-->>C: 502 servicio_externo_no_disponible
```

```json
{"error": "servicio_externo", "codigo": "servicio_externo_no_disponible",
 "mensaje": "No se pudo consultar el mundo de Ceci: <detalle del error de conexión>"}
```

---

## Casos no cubiertos: la entidad queda en los dos mundos

El `POST` a Ceci se confirma en **su** base apenas responde, pero el borrado
en la base propia recién se confirma al final de la transacción. Son dos bases
con dos transacciones independientes: si algo falla entre esos dos momentos,
no hay forma de deshacer lo que ya hizo Ceci.

### No cubierto A · 500: falla el commit propio

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant MC as MundoCeciClient
    participant API as API de Ceci

    C->>PS: POST .../entidades/1/portal
    PS->>R: findById(1)
    R-->>PS: Jugador "Steve" vivo
    PS->>MC: crearEntidad(Jugador "Steve")
    MC->>API: POST /entidades
    Note over API: COMMIT en la base de Ceci<br/>(ya no se puede deshacer)
    API-->>MC: 200 entidad con id 42
    MC-->>PS: EntidadMundoCeci(id = 42)
    PS->>R: delete(entidad)
    R--xPS: falla el COMMIT (base bloqueada,<br/>disco lleno, la app se cae...)
    Note over PS,R: ROLLBACK: la entidad sigue en este mundo
    PS-->>C: 500 error interno
    Note over C,API: Quedó en los DOS mundos<br/>y el cliente cree que el viaje falló
```

### No cubierto B · 502 y después 422: timeout, pero Ceci la guardó

```mermaid
sequenceDiagram
    autonumber
    actor C as Cliente
    participant PS as PortalServiceImpl
    participant R as EntidadRepository<br/>(H2 propia)
    participant MC as MundoCeciClient
    participant API as API de Ceci

    C->>PS: POST .../entidades/1/portal
    PS->>MC: crearEntidad(Jugador "Steve")
    MC->>API: POST /entidades
    Note over MC: Pasan 5 s sin respuesta
    MC-->>PS: ServicioExternoException (timeout)
    Note over PS,R: ROLLBACK: no se borra nada
    PS-->>C: 502 servicio_externo_no_disponible
    Note over API: A los 6 s termina y hace COMMIT:<br/>"Steve" SÍ quedó guardado allá
    C->>PS: reintenta POST .../entidades/1/portal
    PS->>MC: crearEntidad(Jugador "Steve")
    MC->>API: POST /entidades
    API-->>MC: 400 "Ya existe una entidad con ese nombre."
    MC-->>PS: ReglaDelJuegoException
    PS-->>C: 422 rechazada_por_mundo_ceci
    Note over C,API: Duplicado en los dos mundos y el reintento<br/>parece un error de validación
```

### Por qué el orden actual es el menos malo

Primero se crea en el mundo de Ceci y después se borra acá. Al revés (borrar
primero, crear después), si Ceci rechaza o no responde, la entidad se
**pierde en los dos mundos**. Con el orden actual, el peor caso es un
**duplicado**, nunca una pérdida.

### Cómo se podría cubrir (pendiente)

1. **Compensación (patrón Saga):** si falla el borrado o el commit propio,
   llamar a `DELETE /entidades/{id}` en Ceci para deshacer la creación.
2. **Verificación ante timeout:** antes de responder 502, consultar
   `GET /entidades/nombre/{nombre}` en Ceci. Si ya existe, el viaje ocurrió y
   se termina de borrar acá.
3. **Patrón outbox:** registrar el viaje pendiente en la base propia y que
   un proceso aparte lo complete o lo revierta. Es la solución de sistemas
   reales, pero es demasiado para este taller.
