package py.edu.uc.lp3.domain;

import java.util.List;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lo nuevo de POO-08 en el dominio, sin Spring ni base de datos:
 * constructores sobrecargados, las dos reglas de la simulacion, el
 * protocolo Amenaza y el reemplazo de estado que usa el PUT.
 */
class SimulacionYProtocoloTest {

    // ---------- Constructores sobrecargados

    @Test
    void cadaClaseHijaTieneUnConstructorConValoresPorDefecto() {
        Jugador alex = new Jugador("Alex");
        assertEquals(20, alex.getSalud());
        assertEquals(64, alex.getPosicionY());
        assertEquals(0, alex.getNivelEXP());

        Zombie zombie = new Zombie(1, 64, 2);
        assertEquals(35, zombie.getRangoDeteccion());
        assertEquals(3, zombie.getDanoAtaque());

        Esqueleto esqueleto = new Esqueleto(1, 64, 2);
        assertEquals(4, esqueleto.getDanoAtaque());

        Aldeano aldeano = new Aldeano(1, 64, 2, "Bibliotecario");
        assertEquals("Bibliotecario", aldeano.getProfesion());
        assertFalse(aldeano.isDomesticable());

        Animal vaca = new Animal(1, 64, 2);
        assertEquals(10, vaca.getSalud());
        assertFalse(vaca.isMontable());
    }

    @Test
    void losConstructoresSimplesTambienValidan() {
        assertThrows(DatoInvalidoException.class, () -> new Jugador(" "));
        assertThrows(DatoInvalidoException.class, () -> new Zombie(Double.NaN, 64, 0));
        assertThrows(DatoInvalidoException.class, () -> new Aldeano(0, 64, 0, ""));
    }

    // ---------- Regla 1: mecha corta

    @Test
    void elCreeperApuntaAlJugadorVivoMasCercano() {
        Creeper creeper = new Creeper(0, 64, 0);
        Jugador lejos = new Jugador("Lejos", 2.5, 64, 0);
        Jugador cerca = new Jugador("Cerca", 1, 64, 0);
        Jugador muyLejos = new Jugador("MuyLejos", 10, 64, 0);

        assertSame(cerca, creeper.elegirObjetivo(List.of(lejos, muyLejos, cerca)));
    }

    @Test
    void sinJugadorCercaNoExplotaYNadaCambia() {
        Creeper creeper = new Creeper(0, 64, 0);
        Jugador steve = new Jugador("Steve", 5, 64, 0);

        assertThrows(FueraDeAlcanceException.class,
                () -> creeper.explotarCercaDe(List.of(steve), List.of(steve)));
        assertTrue(creeper.estaViva());
        assertEquals(20, steve.getSalud());
    }

    // ---------- Regla 2: onda expansiva

    @Test
    void laExplosionDanaAVariasEntidadesYMataAlCreeper() {
        Creeper creeper = new Creeper(3, 64, 0);
        Jugador steve = new Jugador("Steve", 0, 64, 0);
        Aldeano aldeano = new Aldeano(5, 64, 0, "Herrero");

        Explosion explosion = creeper.explotarCercaDe(
                List.of(steve), List.of(creeper, steve, aldeano));

        assertFalse(creeper.estaViva());
        assertEquals(4, steve.getSalud());
        assertFalse(aldeano.estaViva());
        assertEquals("Steve", explosion.getJugadorObjetivo());
        assertEquals(2, explosion.getEntidadesAlcanzadas());
        assertEquals(1, explosion.getEntidadesMuertas());
        assertEquals(16 + 24, explosion.getDanoTotal(), "cuenta el daño de la explosión, no la salud perdida");
    }

    @Test
    void unCreeperMuertoNoExplota() {
        Creeper creeper = new Creeper(0, 64, 0);
        creeper.explotar();
        Jugador steve = new Jugador("Steve", 1, 64, 0);

        assertThrows(EntidadMuertaException.class,
                () -> creeper.explotarCercaDe(List.of(steve), List.of(steve)));
    }

    @Test
    void unaExplosionDetonaUnaSolaVez() {
        Explosion explosion = new Explosion(0, 64, 0, 3);
        explosion.detonar(List.of());

        ReglaDelJuegoException error = assertThrows(ReglaDelJuegoException.class,
                () -> explosion.detonar(List.of()));
        assertEquals("explosion_ya_detonada", error.getCodigo());
    }

    // ---------- Protocolo Amenaza

    @Test
    void mobsYArmasSeConsultanPorElMismoProtocolo() {
        Jugador steve = new Jugador("Steve", 0, 64, 0);

        List<Amenaza> amenazas = List.of(
                new Zombie(10, 64, 0),          // dentro de su rango (35)
                new Esqueleto(30, 64, 0),       // fuera de su rango (16)
                new Creeper(3, 64, 0),          // su daño es el de la explosion
                new Espada(Material.DIAMANTE),  // otra familia: Herramienta
                new Hacha(Material.MADERA));

        List<Integer> danos = amenazas.stream().map(a -> a.danoContra(steve)).toList();

        assertEquals(List.of(3, 0, 16, 7, 7), danos);
        amenazas.forEach(a -> assertFalse(a.describirAmenaza().isBlank()));
        assertEquals(20, steve.getSalud(), "consultar no daña");
    }

    @Test
    void unArmaRotaNoEsAmenaza() {
        Espada espada = new Espada(Material.ORO);
        Zombie zombie = new Zombie(20, 0, 64, 0, 1, 16, 1);
        for (int i = 0; i < Material.ORO.getDurabilidad(); i++) {
            zombie.curar(10);
            espada.atacar(zombie);
        }

        assertTrue(espada.estaRota());
        assertEquals(0, espada.danoContra(zombie));
    }

    // ---------- PUT: reemplazar estado sin setters

    @Test
    void reemplazarConCopiaElEstadoPeroNoElTipo() {
        Jugador steve = new Jugador("Steve");
        steve.reemplazarCon(new Jugador(15, 1, 70, 2, 3, "Steve2", 9));

        assertEquals("Steve2", steve.getNombre());
        assertEquals(15, steve.getSalud());
        assertEquals(9, steve.getNivelEXP());

        assertThrows(DatoInvalidoException.class,
                () -> steve.reemplazarCon(new Zombie(0, 64, 0)));
    }

    @Test
    void unaEntidadMuertaNoSeReemplaza() {
        Creeper creeper = new Creeper(0, 64, 0);
        creeper.explotar();

        assertThrows(EntidadMuertaException.class,
                () -> creeper.reemplazarCon(new Creeper(0, 64, 0)));
    }
}
