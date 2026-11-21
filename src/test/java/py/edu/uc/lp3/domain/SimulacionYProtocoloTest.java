package py.edu.uc.lp3.domain;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lo de POO-08 que queda en el dominio, sin Spring ni base de datos:
 * constructores sobrecargados, el protocolo Amenaza, el reemplazo de estado
 * que usa el PUT y el registro de una explosión.
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

    // ---------- Protocolo Amenaza

    @Test
    void mobsYArmasSeConsultanPorElMismoProtocolo() {
        Jugador steve = new Jugador("Steve", 0, 64, 0);

        List<Amenaza> amenazas = List.of(
                new Zombie(10, 64, 0),          // dentro de su rango (35)
                new Esqueleto(30, 64, 0),       // fuera de su rango (16)
                new Creeper(3, 64, 0),          // dentro de su rango (16)
                new Espada(Material.DIAMANTE),  // otra familia: Herramienta
                new Hacha(Material.MADERA));

        List<Integer> danos = amenazas.stream().map(a -> a.danoContra(steve)).toList();

        assertEquals(List.of(3, 0, 3, 7, 7), danos);
        amenazas.forEach(a -> assertFalse(a.describirAmenaza().isBlank()));
        assertEquals(20, steve.getSalud(), "consultar no daña");
    }

    @Test
    void unArmaRotaNoEsAmenaza() {
        Espada espada = new Espada(Material.ORO);
        espada.desgastar(Material.ORO.getDurabilidad());

        assertTrue(espada.estaRota());
        assertEquals(0, espada.danoContra(new Zombie(0, 64, 0)));
    }

    @Test
    void unMobMuertoNoEsAmenaza() {
        Zombie zombie = new Zombie(1, 64, 0);
        zombie.morir();
        assertEquals(0, zombie.danoContra(new Jugador("Steve")));
    }

    // ---------- PUT: reemplazar estado sin setters

    @Test
    void reemplazarConCopiaElEstadoPeroNoElId() {
        Jugador steve = new Jugador("Steve");
        steve.reemplazarCon(new Jugador(15, 1, 70, 2, 3, "Steve2", 9));

        assertEquals("Steve2", steve.getNombre());
        assertEquals(15, steve.getSalud());
        assertEquals(70, steve.getPosicionY());
        assertEquals(9, steve.getNivelEXP());
        assertNull(steve.getId());
    }

    // ---------- Registro de la explosión

    @Test
    void laExplosionDeUnCreeperGuardaQuienLaProvoco() {
        Creeper creeper = new Creeper(3, 64, 0);
        Explosion explosion = new Explosion(creeper, new Jugador("Steve", 0, 64, 0));

        assertEquals(Creeper.POTENCIA_EXPLOSION, explosion.getPotencia());
        assertEquals(6, explosion.getRadio());
        assertEquals(3, explosion.getCentroX());
        assertEquals("Steve", explosion.getJugadorObjetivo());
        assertFalse(explosion.isDetonada());
    }

    @Test
    void registrarDetonacionResumeLosImpactos() {
        Jugador steve = new Jugador("Steve", 0, 64, 0);
        Aldeano aldeano = new Aldeano(1, 64, 0, "Herrero");
        aldeano.morir();
        Explosion explosion = new Explosion(0, 64, 0, 3);

        explosion.registrarDetonacion(List.of(
                new Explosion.Impacto(steve, 0, 5),
                new Explosion.Impacto(aldeano, 1, 20)));

        assertTrue(explosion.isDetonada());
        assertNotNull(explosion.getMomento());
        assertEquals(2, explosion.getEntidadesAlcanzadas());
        assertEquals(1, explosion.getEntidadesMuertas());
        assertEquals(25, explosion.getDanoTotal());
    }
}
