package py.edu.uc.lp3.exceptions;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Espada;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.domain.Zombie;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprueba que cada situacion del juego lanza la excepcion de su rama y
 * que todas se pueden atrapar como {@link MinecraftException}.
 */
class ExcepcionesTest {

    private Jugador steve() { return new Jugador(20, 0, 64, 0, 5, "Steve", 0); }
    private Zombie zombie() { return new Zombie(20, 1, 64, 0, 3, 16, 3); }

    @Test
    void datoInvalidoEsUnaMinecraftException() {
        MinecraftException e = assertThrows(MinecraftException.class,
                () -> new Zombie(0, 0, 64, 0, 3, 16, 3));

        assertInstanceOf(DatoInvalidoException.class, e);
        assertEquals("dato_invalido", e.getCodigo());
        assertEquals("La salud debe ser mayor que cero.", e.getMessage());
    }

    @Test
    void entidadMuertaEsUnaReglaDelJuego() {
        Zombie z = zombie();
        z.recibirDano(20);

        ReglaDelJuegoException e = assertThrows(ReglaDelJuegoException.class,
                () -> z.atacar(steve()));

        assertInstanceOf(EntidadMuertaException.class, e);
        assertEquals("entidad_muerta", e.getCodigo());
    }

    @Test
    void cadaSubclaseTraeSuCodigo() {
        Zombie lejos = new Zombie(20, 50, 64, 0, 3, 16, 3);
        assertEquals("fuera_de_alcance", assertThrows(FueraDeAlcanceException.class,
                () -> lejos.atacar(steve())).getCodigo());

        Espada rota = new Espada(Material.ORO);
        Zombie resistente = new Zombie(2000, 1, 64, 0, 3, 16, 3);
        for (int golpe = 0; golpe < Material.ORO.getDurabilidad(); golpe++) {
            rota.atacar(resistente);
        }
        assertEquals("herramienta_rota", assertThrows(HerramientaRotaException.class,
                () -> rota.atacar(resistente)).getCodigo());
    }

    @Test
    void lasReglasPuntualesUsanSuPropioCodigo() {
        Animal gallina = new Animal(4, 0, 64, 0, 1, false, false);

        ReglaDelJuegoException e = assertThrows(ReglaDelJuegoException.class,
                () -> gallina.montar(steve()));

        assertEquals("animal_no_montable", e.getCodigo());
    }

    @Test
    void entidadNoEncontradaLlevaElId() {
        EntidadNoEncontradaException e = new EntidadNoEncontradaException(7L);

        assertEquals("entidad_no_encontrada", e.getCodigo());
        assertEquals("No existe una entidad con id 7.", e.getMessage());
    }
}
