package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IrinaTest {

    private Jugador steve() { return new Jugador(20, 0, 64, 0, 5, "Steve", 0); }
    private Zombie zombie() { return new Zombie(20, 5, 64, 0, 3, 16, 3); }
    private Creeper creeper() { return new Creeper(20, 6, 64, 0, 2, 16, 3, 30); }
    private Aldeano aldeano() { return new Aldeano(20, 10, 64, 0, 2, false, "Herrero"); }

    @Test
    void constructorRechazaEstadoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Zombie(0, 0, 64, 0, 3, 16, 3));
        assertThrows(IllegalArgumentException.class, () -> new Jugador(20, 0, 64, 0, 5, " ", 0));
        assertThrows(IllegalArgumentException.class, () -> new Creeper(20, 0, 64, 0, 2, 16, 3, 0));
    }

    @Test
    void laSaludNuncaQuedaNegativa() {
        Zombie z = zombie();
        z.recibirDano(1000);
        assertEquals(0, z.getSalud());
        assertFalse(z.estaViva());
    }

    @Test
    void danoNegativoSeRechaza() {
        Zombie z = zombie();
        assertThrows(IllegalArgumentException.class, () -> z.recibirDano(-5));
        assertEquals(20, z.getSalud());
    }

    @Test
    void curarNoSuperaLaSaludMaxima() {
        Jugador j = steve();
        j.recibirDano(5);
        j.curar(100);
        assertEquals(20, j.getSalud());
    }

    @Test
    void noSeMueveMasQueSuVelocidad() {
        Zombie z = zombie();
        assertThrows(IllegalArgumentException.class, () -> z.mover(10, 0, 0));
        assertEquals(5, z.getPosicionX());
    }

    @Test
    void unMuertoNoActua() {
        Zombie z = zombie();
        z.recibirDano(20);
        assertThrows(IllegalStateException.class, () -> z.mover(1, 0, 0));
        assertThrows(IllegalStateException.class, () -> z.atacar(steve()));
    }

    @Test
    void creeperExplotaYMuere() {
        Creeper c = creeper();
        Jugador j = steve();
        c.atacar(j);
        assertEquals(17, j.getSalud());
        assertFalse(c.estaViva());
    }

    @Test
    void esqueletoNoAtacaFueraDeRango() {
        Esqueleto e = new Esqueleto(20, 30, 64, 0, 3, 10, 4);
        Jugador j = steve();
        assertThrows(IllegalStateException.class, () -> e.atacar(j));
        assertEquals(20, j.getSalud());
    }

    @Test
    void aldeanoInfectadoNoComercia() {
        Aldeano a = aldeano();
        assertDoesNotThrow(a::comercio);
        zombie().infectaAldeano(a);
        assertTrue(a.isInfectado());
        assertThrows(IllegalStateException.class, a::comercio);
    }

    @Test
    void pasivaHuyeYSeAleja() {
        Aldeano a = aldeano();
        Zombie z = zombie();
        double antes = a.distanciaA(z);
        a.huir(z);
        assertTrue(a.distanciaA(z) > antes);
    }

    @Test
    void animalNoMontableSeRechaza() {
        Animal gallina = new Animal(4, 0, 64, 0, 1, false, false);
        assertThrows(IllegalStateException.class, () -> gallina.montar(steve()));
    }

    @Test
    void experienciaSoloSube() {
        Jugador j = steve();
        assertThrows(IllegalArgumentException.class, () -> j.ganarExperiencia(-10));
        j.ganarExperiencia(5);
        assertEquals(5, j.getNivelEXP());
    }

    @Test
    void todasLasEntidadesSeTratanIgualSinIfPorTipo() {
        List<Entidad> mundo = List.of(steve(), zombie(), creeper(), aldeano(),
                new Animal(10, 12, 64, 0, 2, true, true));
        mundo.forEach(e -> e.recibirDano(5));
        mundo.forEach(e -> assertEquals(e.getSaludMaxima() - 5, e.getSalud()));
    }
}
