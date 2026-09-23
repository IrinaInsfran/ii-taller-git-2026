## Diagrama de clases

```mermaid
classDiagram

    class Entidad {
        <<abstract>>
        -int salud
        -int saludMaxima
        -double posicionX
        -double posicionY
        -double posicionZ
        -int velocidad
        +mover()
        +mover(double dx, double dy, double dz)
        +recibirDano(int cantidad)
        +curar(int cantidad)
        +estaViva() boolean
        +describirComportamiento() String
    }

    class EntidadHostil {
        -double rangoDeteccion
        -int danoAtaque
        +atacar(Entidad objetivo)
        +describirComportamiento() String
    }

    class EntidadPasiva {
        -boolean domesticable
        +huir(Entidad amenaza)
        +describirComportamiento() String
    }

    class Jugador {
        -String nombre
        -int nivelEXP
        +construir()
        +craftear()
        +interactuar()
        +describirComportamiento() String
    }

    class Zombie {
        +infectaAldeano(Aldeano aldeano)
    }

    class Creeper {
        -int tiempoExplosion
        +atacar(Entidad objetivo)
        +explotar()
        +describirComportamiento() String
    }

    class Esqueleto {
        +dispararFlecha()
    }

    class Aldeano {
        -String profesion
        -boolean infectado
        +comercio()
        +describirComportamiento() String
    }

    class Animal {
        -boolean montable
        +montar(Jugador jugador)
    }

    Entidad <|-- EntidadHostil
    Entidad <|-- EntidadPasiva
    Entidad <|-- Jugador

    EntidadHostil <|-- Zombie
    EntidadHostil <|-- Creeper
    EntidadHostil <|-- Esqueleto

    EntidadPasiva <|-- Aldeano
    EntidadPasiva <|-- Animal
```   # ii-taller-git-2026

   - Nombre: Irina Insfrán
   - GitHub: IrinaInsfran
   - Comisión: CYT646 F

   ## Levantar la API
   ./mvnw spring-boot:run
