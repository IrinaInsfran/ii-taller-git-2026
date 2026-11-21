package py.edu.uc.lp3.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HerramientaTest {

    @Test
    void laDurabilidadInicialDependeDelMaterial() {
        assertEquals(59, new Espada(Material.MADERA).getDurabilidad());
        assertEquals(32, new Pico(Material.ORO).getDurabilidad());
        assertEquals(1561, new Hacha(Material.DIAMANTE).getDurabilidadMaxima());
    }

    @Test
    void desgastarNoBajaDeCero() {
        Pico pico = new Pico(Material.ORO);
        pico.desgastar(1000);
        assertEquals(0, pico.getDurabilidad());
        assertTrue(pico.estaRota());
    }

    @Test
    void desgasteNegativoNoReparaLaHerramienta() {
        Espada espada = new Espada(Material.MADERA);
        espada.desgastar(-10);
        assertEquals(59, espada.getDurabilidad());
    }

    @Test
    void cadaArmaDiceSuDanoYSuDesgaste() {
        Espada espada = new Espada(Material.HIERRO);
        Hacha hacha = new Hacha(Material.HIERRO);

        assertEquals(6, espada.getDanoAtaque());
        assertEquals(1, espada.getDesgastePorGolpe());
        assertEquals(9, hacha.getDanoAtaque());
        assertEquals(2, hacha.getDesgastePorGolpe());
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
