package py.edu.uc.lp3.exceptions;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Espada;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.service.impl.AccionServiceImpl;
import py.edu.uc.lp3.service.impl.HerramientaServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprueba que cada situación del juego lanza la excepción de su rama y
 * que todas se pueden atrapar como {@link MinecraftException}. Las lanzan
 * los servicios, no el dominio.
 */
class ExcepcionesTest {

    private final HerramientaServiceImpl herramientas = new HerramientaServiceImpl();
    private final AccionServiceImpl acciones = new AccionServiceImpl(herramientas);

    private Jugador steve() { return new Jugador(20, 0, 64, 0, 5, "Steve", 0); }
    private Zombie zombie() { return new Zombie(20, 1, 64, 0, 3, 16, 3); }

    @Test
    void datoInvalidoEsUnaMinecraftException() {
        MinecraftException e = assertThrows(MinecraftException.class,
                () -> acciones.recibirDano(zombie(), 0));

        assertInstanceOf(DatoInvalidoException.class, e);
        assertEquals("dato_invalido", e.getCodigo());
        assertEquals("La cantidad de daño debe ser mayor que cero.", e.getMessage());
    }

    @Test
    void entidadMuertaEsUnaReglaDelJuego() {
        Zombie z = zombie();
        z.morir();

        ReglaDelJuegoException e = assertThrows(ReglaDelJuegoException.class,
                () -> acciones.atacar(z, steve()));

        assertInstanceOf(EntidadMuertaException.class, e);
        assertEquals("entidad_muerta", e.getCodigo());
    }

    @Test
    void cadaSubclaseTraeSuCodigo() {
        Zombie lejos = new Zombie(20, 50, 64, 0, 3, 16, 3);
        assertEquals("fuera_de_alcance", assertThrows(FueraDeAlcanceException.class,
                () -> acciones.atacar(lejos, steve())).getCodigo());

        Espada rota = new Espada(Material.ORO);
        rota.desgastar(Material.ORO.getDurabilidad());
        assertEquals("herramienta_rota", assertThrows(HerramientaRotaException.class,
                () -> herramientas.golpear(rota, zombie())).getCodigo());
    }

    @Test
    void lasReglasPuntualesUsanSuPropioCodigo() {
        Animal gallina = new Animal(4, 0, 64, 0, 1, false, false);

        ReglaDelJuegoException e = assertThrows(ReglaDelJuegoException.class,
                () -> acciones.montar(gallina, steve()));

        assertEquals("animal_no_montable", e.getCodigo());
    }

    @Test
    void entidadNoEncontradaLlevaElId() {
        EntidadNoEncontradaException e = new EntidadNoEncontradaException(7L);

        assertEquals("entidad_no_encontrada", e.getCodigo());
        assertEquals("No existe una entidad con id 7.", e.getMessage());
    }
}
