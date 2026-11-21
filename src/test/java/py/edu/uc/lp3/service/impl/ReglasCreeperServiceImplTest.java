package py.edu.uc.lp3.service.impl;

import java.util.List;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Las dos reglas de la simulación, sin Spring ni base de datos.
 */
class ReglasCreeperServiceImplTest {

	private final ReglasCreeperServiceImpl reglas = new ReglasCreeperServiceImpl();

	// ---------- Regla 1: mecha corta

	@Test
	void elCreeperApuntaAlJugadorVivoMasCercano() {
		Creeper creeper = new Creeper(0, 64, 0);
		Jugador lejos = new Jugador("Lejos", 2.5, 64, 0);
		Jugador cerca = new Jugador("Cerca", 1, 64, 0);
		Jugador muyLejos = new Jugador("MuyLejos", 10, 64, 0);

		assertSame(cerca, reglas.mechaCorta(creeper, List.of(lejos, muyLejos, cerca)));
	}

	@Test
	void unJugadorMuertoNoEnciendeLaMecha() {
		Creeper creeper = new Creeper(0, 64, 0);
		Jugador muerto = new Jugador("Muerto", 1, 64, 0);
		muerto.morir();

		assertThrows(FueraDeAlcanceException.class,
				() -> reglas.mechaCorta(creeper, List.of(muerto)));
	}

	@Test
	void sinJugadorCercaNoExplotaYNadaCambia() {
		Creeper creeper = new Creeper(0, 64, 0);
		Jugador steve = new Jugador("Steve", 5, 64, 0);

		FueraDeAlcanceException error = assertThrows(FueraDeAlcanceException.class,
				() -> reglas.explotarCercaDe(creeper, List.of(steve), List.of(steve)));
		assertEquals("fuera_de_alcance", error.getCodigo());
		assertTrue(creeper.estaViva());
		assertEquals(20, steve.getSalud());
	}

	@Test
	void unCreeperMuertoNoExplota() {
		Creeper creeper = new Creeper(0, 64, 0);
		reglas.explotar(creeper, List.of());
		Jugador steve = new Jugador("Steve", 1, 64, 0);

		assertThrows(EntidadMuertaException.class,
				() -> reglas.explotarCercaDe(creeper, List.of(steve), List.of(steve)));
		assertThrows(EntidadMuertaException.class, () -> reglas.explotar(creeper, List.of()));
	}

	@Test
	void listaNulaEsUnDatoInvalido() {
		assertThrows(DatoInvalidoException.class,
				() -> reglas.mechaCorta(new Creeper(0, 64, 0), null));
	}

	// ---------- Regla 2: onda expansiva

	@Test
	void elDanoBajaConLaDistancia() {
		Explosion e = new Explosion(0, 64, 0, Creeper.POTENCIA_EXPLOSION);
		assertEquals(6, e.getRadio());
		assertEquals(43, reglas.calcularDano(e, 0));
		assertEquals(33, reglas.calcularDano(e, 1));
		assertEquals(16, reglas.calcularDano(e, 3));
		assertEquals(0, reglas.calcularDano(e, 6));
		assertEquals(0, reglas.calcularDano(e, 20));
		assertThrows(DatoInvalidoException.class, () -> reglas.calcularDano(e, -1));
	}

	@Test
	void laExplosionDanaAVariasEntidadesYMataAlCreeper() {
		Creeper creeper = new Creeper(3, 64, 0);
		Jugador steve = new Jugador("Steve", 0, 64, 0);
		Aldeano aldeano = new Aldeano(5, 64, 0, "Herrero");

		Explosion explosion = reglas.explotarCercaDe(
				creeper, List.of(steve), List.of(creeper, steve, aldeano));

		assertFalse(creeper.estaViva());
		assertEquals(4, steve.getSalud());
		assertFalse(aldeano.estaViva());
		assertEquals("Steve", explosion.getJugadorObjetivo());
		assertEquals(2, explosion.getEntidadesAlcanzadas());
		assertEquals(1, explosion.getEntidadesMuertas());
		assertEquals(16 + 24, explosion.getDanoTotal(), "cuenta el daño de la explosión, no la salud perdida");
	}

	@Test
	void soloDanaALasEntidadesVivasDentroDelRadio() {
		Creeper creeper = new Creeper(3, 64, 0);
		Jugador steve = new Jugador(20, 0, 64, 0, 4, "Steve", 0);
		Aldeano aldeano = new Aldeano(20, 5, 64, 0, 2, false, "Herrero");
		Animal vacaLejos = new Animal(10, 20, 64, 0, 1, true, false);

		List<Explosion.Impacto> impactos =
				reglas.explotar(creeper, List.of(creeper, steve, aldeano, vacaLejos));

		assertFalse(creeper.estaViva());
		assertEquals(2, impactos.size());
		assertEquals(4, steve.getSalud());
		assertFalse(aldeano.estaViva());
		assertEquals(10, vacaLejos.getSalud());
	}

	@Test
	void unaExplosionDetonaUnaSolaVez() {
		Explosion explosion = new Explosion(0, 64, 0, 3);
		reglas.ondaExpansiva(explosion, List.of());

		ReglaDelJuegoException error = assertThrows(ReglaDelJuegoException.class,
				() -> reglas.ondaExpansiva(explosion, List.of()));
		assertEquals("explosion_ya_detonada", error.getCodigo());
	}
}
