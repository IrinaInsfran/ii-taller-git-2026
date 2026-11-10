package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExplosionTest {

    private Explosion explosionDeCreeper() {
        return new Explosion(0, 64, 0, Creeper.POTENCIA_EXPLOSION);
    }

    @Test
    void elDanoBajaConLaDistancia() {
        Explosion e = explosionDeCreeper();
        assertEquals(6, e.getRadio());
        assertEquals(43, e.calcularDano(0));
        assertEquals(33, e.calcularDano(1));
        assertEquals(16, e.calcularDano(3));
        assertEquals(0, e.calcularDano(6));
        assertEquals(0, e.calcularDano(20));
    }

    @Test
    void potenciaInvalidaSeRechaza() {
        assertThrows(DatoInvalidoException.class, () -> new Explosion(0, 64, 0, 0));
        assertThrows(DatoInvalidoException.class, () -> new Explosion(0, 64, 0, Double.NaN));
    }

    @Test
    void creeperDanaSoloALasEntidadesVivasDentroDelRadio() {
        Creeper creeper = new Creeper(3, 64, 0);
        Jugador steve = new Jugador(20, 0, 64, 0, 4, "Steve", 0);
        Aldeano aldeano = new Aldeano(20, 5, 64, 0, 2, false, "Herrero");
        Animal vacaLejos = new Animal(10, 20, 64, 0, 1, true, false);

        List<Explosion.Impacto> impactos =
                creeper.explotar(List.of(creeper, steve, aldeano, vacaLejos));

        assertFalse(creeper.estaViva());
        assertEquals(2, impactos.size());
        assertEquals(4, steve.getSalud());
        assertFalse(aldeano.estaViva());
        assertEquals(10, vacaLejos.getSalud());
    }

    @Test
    void creeperSoloDetonaConUnJugadorVivoCerca() {
        Creeper creeper = new Creeper(3, 64, 0);
        assertTrue(creeper.puedeDetonarCerca(new Jugador(20, 0, 64, 0, 4, "Cerca", 0)));
        assertFalse(creeper.puedeDetonarCerca(new Jugador(20, 10, 64, 0, 4, "Lejos", 0)));
    }

    @Test
    void creeperMuertoNoPuedeExplotarDeNuevo() {
        Creeper creeper = new Creeper(3, 64, 0);
        creeper.explotar();
        assertThrows(EntidadMuertaException.class, () -> creeper.explotar(List.of()));
    }
}
