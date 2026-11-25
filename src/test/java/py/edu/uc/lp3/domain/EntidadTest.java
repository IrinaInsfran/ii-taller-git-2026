package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * El dominio solo guarda el estado: estos tests revisan que los mensajes
 * cambien el estado como dicen y que la salud quede siempre entre 0 y la
 * salud máxima, sin lanzar excepciones. Las reglas se prueban en
 * {@code service.impl}.
 */
class EntidadTest {

    private Jugador steve() { return new Jugador(20, 0, 64, 0, 5, "Steve", 0); }
    private Zombie zombie() { return new Zombie(20, 5, 64, 0, 3, 16, 3); }
    private Creeper creeper() { return new Creeper(20, 6, 64, 0, 2, 16, 3, 30); }
    private Aldeano aldeano() { return new Aldeano(20, 10, 64, 0, 2, false, "Herrero"); }

    @Test
    void laSaludNuncaQuedaNegativa() {
        Zombie z = zombie();
        z.recibirDano(1000);
        assertEquals(0, z.getSalud());
        assertFalse(z.estaViva());
    }

    @Test
    void dañoMaximoDeIntNoDejaSaludNegativa() {
        Zombie z = zombie();
        z.recibirDano(Integer.MAX_VALUE);
        assertEquals(0, z.getSalud());
    }

    @Test
    void dañoNegativoNoSubeLaSaludPorEncimaDelMaximo() {
        Zombie z = zombie();
        z.recibirDano(Integer.MIN_VALUE);
        assertEquals(z.getSaludMaxima(), z.getSalud());
    }

    @Test
    void curarNoSuperaLaSaludMaxima() {
        Jugador j = steve();
        j.recibirDano(5);
        j.curar(100);
        assertEquals(20, j.getSalud());
    }

    @Test
    void curarConElMaximoDeIntNoDesborda() {
        Aldeano a = aldeano();
        a.recibirDano(19);
        a.curar(Integer.MAX_VALUE);
        assertEquals(a.getSaludMaxima(), a.getSalud());
    }

    @Test
    void morirDejaLaSaludEnCero() {
        Creeper c = creeper();
        c.morir();
        assertEquals(0, c.getSalud());
        assertFalse(c.estaViva());
    }

    @Test
    void todasLasEntidadesSeTratanIgualSinIfPorTipo() {
        List<Entidad> mundo = List.of(steve(), zombie(), creeper(), aldeano(),
                new Animal(10, 12, 64, 0, 2, true, true));
        mundo.forEach(e -> e.recibirDano(5));
        mundo.forEach(e -> assertEquals(e.getSaludMaxima() - 5, e.getSalud()));
    }

    @Test
    void moverEnHorizontalNoCambiaLaAltura() {
        Zombie z = zombie();
        z.mover(1, -2);
        assertEquals(6, z.getPosicionX());
        assertEquals(64, z.getPosicionY());
        assertEquals(-2, z.getPosicionZ());
    }

    @Test
    void distanciaEntreDosEntidades() {
        assertEquals(5, steve().distanciaA(zombie()));
    }

    @Test
    void laExperienciaSeSumaYNoDaLaVuelta() {
        Jugador j = steve();
        j.ganarExperiencia(5);
        assertEquals(5, j.getNivelEXP());
        j.ganarExperiencia(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, j.getNivelEXP());
    }

    @Test
    void aldeanoInfectadoCambiaDeEstado() {
        Aldeano a = aldeano();
        assertFalse(a.isInfectado());
        a.infectar();
        assertTrue(a.isInfectado());
    }

    @Test
    void cadaClaseConcretaDescribeSuPropioComportamiento() {
        List<String> descripciones = List.of(
                steve().describirComportamiento(),
                zombie().describirComportamiento(),
                new Esqueleto(20, 30, 64, 0, 3, 10, 4).describirComportamiento(),
                creeper().describirComportamiento(),
                aldeano().describirComportamiento(),
                new Animal(10, 12, 64, 0, 2, true, true).describirComportamiento()
        );
        assertEquals(descripciones.size(), Set.copyOf(descripciones).size());
        descripciones.forEach(d -> assertFalse(d.isBlank()));
    }

    @Test
    void elComportamientoSeObtieneSinPreguntarPorElTipo() {
        List<Entidad> mundo = List.of(creeper(), aldeano(), zombie());
        List<String> descripciones = mundo.stream()
                .map(Entidad::describirComportamiento)
                .toList();
        assertEquals(3, Set.copyOf(descripciones).size());
    }

    @Test
    void elConstructorSobrecargadoDeCreeperUsaLosValoresPorDefecto() {
        Creeper c = new Creeper(10, 64, -5);
        assertEquals(20, c.getSalud());
        assertEquals(20, c.getSaludMaxima());
        assertEquals(1, c.getVelocidad());
        assertEquals(16, c.getRangoDeteccion());
        assertEquals(3, c.getDanoAtaque());
        assertEquals(30, c.getTiempoExplosion());
        assertEquals(10, c.getPosicionX());
        assertEquals(64, c.getPosicionY());
        assertEquals(-5, c.getPosicionZ());
        assertTrue(c.estaViva());
    }
}
