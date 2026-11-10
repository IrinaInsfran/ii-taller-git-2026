package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.HerramientaRotaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HerramientaTest {

    private Jugador steve() { return new Jugador(20, 0, 64, 0, 5, "Steve", 0); }
    private Zombie zombieEn(double x) { return new Zombie(20, x, 64, 0, 3, 16, 3); }

    @Test
    void laDurabilidadInicialDependeDelMaterial() {
        assertEquals(59, new Espada(Material.MADERA).getDurabilidad());
        assertEquals(32, new Pico(Material.ORO).getDurabilidad());
        assertEquals(1561, new Hacha(Material.DIAMANTE).getDurabilidadMaxima());
        assertThrows(DatoInvalidoException.class, () -> new Espada(null));
    }

    @Test
    void cadaArmaHaceSuPropioDano() {
        List<Arma> armas = List.of(new Espada(Material.HIERRO), new Hacha(Material.HIERRO));
        Zombie zombie = zombieEn(1);

        for (Arma arma : armas) {
            arma.atacar(zombie);
        }

        // espada de hierro 6 + hacha de hierro 9
        assertEquals(20 - 6 - 9, zombie.getSalud());
    }

    @Test
    void elHachaSeDesgastaMasAlPelearQueAlTalar() {
        Hacha hacha = new Hacha(Material.PIEDRA);
        hacha.atacar(zombieEn(1));
        assertEquals(131 - 2, hacha.getDurabilidad());

        hacha.talar();
        assertEquals(131 - 3, hacha.getDurabilidad());
    }

    @Test
    void unaHerramientaRotaNoSePuedeUsar() {
        Espada espada = new Espada(Material.ORO);
        Zombie zombie = new Zombie(2000, 1, 64, 0, 3, 16, 3);

        for (int golpe = 0; golpe < 32; golpe++) {
            espada.atacar(zombie);
        }

        assertTrue(espada.estaRota());
        int saludAntes = zombie.getSalud();
        assertThrows(HerramientaRotaException.class, () -> espada.atacar(zombie));
        assertEquals(saludAntes, zombie.getSalud());
    }

    @Test
    void elPicoSoloObtieneMineralesDeSuNivel() {
        Pico madera = new Pico(Material.MADERA);
        Pico hierro = new Pico(Material.HIERRO);

        assertTrue(madera.minar(Mineral.CARBON));
        assertFalse(madera.minar(Mineral.DIAMANTE));
        assertEquals(59 - 2, madera.getDurabilidad());

        assertTrue(hierro.minar(Mineral.DIAMANTE));
        assertFalse(hierro.puedeObtener(Mineral.OBSIDIANA));
        assertTrue(new Pico(Material.DIAMANTE).puedeObtener(Mineral.OBSIDIANA));
    }

    @Test
    void elPicoDeOroTieneElNivelDeLaMadera() {
        assertFalse(new Pico(Material.ORO).puedeObtener(Mineral.HIERRO));
    }

    @Test
    void jugadorAtacaConArmaSoloDentroDeSuAlcance() {
        Jugador steve = steve();
        Espada espada = new Espada(Material.DIAMANTE);

        Zombie cerca = zombieEn(2);
        steve.atacar(cerca, espada);
        assertEquals(20 - 7, cerca.getSalud());

        Zombie lejos = zombieEn(10);
        assertThrows(FueraDeAlcanceException.class, () -> steve.atacar(lejos, espada));
        assertEquals(20, lejos.getSalud());

        assertThrows(ReglaDelJuegoException.class, () -> steve.atacar(steve, espada));
    }

    @Test
    void cadaHerramientaDescribeSuUso() {
        List<Herramienta> herramientas = List.of(
                new Espada(Material.MADERA),
                new Hacha(Material.MADERA),
                new Pico(Material.MADERA));

        for (Herramienta herramienta : herramientas) {
            assertFalse(herramienta.describirUso().isBlank());
        }
    }
}
