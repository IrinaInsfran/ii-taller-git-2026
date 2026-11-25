package py.edu.uc.lp3.service.impl;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Espada;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Las reglas de las acciones del juego, sin Spring ni base de datos.
 */
class AccionServiceImplTest {

	private final AccionServiceImpl acciones = new AccionServiceImpl(new HerramientaServiceImpl());

	private Jugador steve() { return new Jugador(20, 0, 64, 0, 5, "Steve", 0); }
	private Zombie zombie() { return new Zombie(20, 5, 64, 0, 3, 16, 3); }
	private Aldeano aldeano() { return new Aldeano(20, 10, 64, 0, 2, false, "Herrero"); }

	@Test
	void danoNegativoSeRechaza() {
		Zombie z = zombie();
		assertThrows(DatoInvalidoException.class, () -> acciones.recibirDano(z, -5));
		assertEquals(20, z.getSalud());
	}

	@Test
	void unMuertoNoSeCura() {
		Zombie z = zombie();
		acciones.recibirDano(z, 20);
		assertThrows(EntidadMuertaException.class, () -> acciones.curar(z, 5));
		assertEquals(0, z.getSalud());
	}

	@Test
	void noSeMueveMasQueSuVelocidad() {
		Zombie z = zombie();
		assertThrows(ReglaDelJuegoException.class, () -> acciones.mover(z, 10, 0, 0));
		assertEquals(5, z.getPosicionX());
	}

	@Test
	void moverEnHorizontalRespetaLaVelocidad() {
		Zombie z = zombie();
		assertThrows(ReglaDelJuegoException.class, () -> acciones.mover(z, 4, 0));
		acciones.mover(z, 1, -2);
		assertEquals(6, z.getPosicionX());
		assertEquals(64, z.getPosicionY());
		assertEquals(-2, z.getPosicionZ());
	}

	@Test
	void desplazamientoNoFinitoSeRechaza() {
		Zombie z = zombie();
		assertThrows(DatoInvalidoException.class, () -> acciones.mover(z, Double.NaN, 0, 0));
		assertEquals(5, z.getPosicionX());
	}

	@Test
	void unMuertoNoActua() {
		Zombie z = zombie();
		acciones.recibirDano(z, 20);
		assertThrows(EntidadMuertaException.class, () -> acciones.mover(z, 1, 0, 0));
		assertThrows(EntidadMuertaException.class, () -> acciones.atacar(z, steve()));
	}

	@Test
	void unMobAtacaDentroDeSuRango() {
		Jugador j = steve();
		acciones.atacar(zombie(), j);
		assertEquals(17, j.getSalud());
	}

	@Test
	void esqueletoNoAtacaFueraDeRango() {
		Esqueleto e = new Esqueleto(20, 30, 64, 0, 3, 10, 4);
		Jugador j = steve();
		FueraDeAlcanceException error = assertThrows(FueraDeAlcanceException.class,
				() -> acciones.atacar(e, j));
		assertEquals("fuera_de_alcance", error.getCodigo());
		assertEquals(20, j.getSalud());
	}

	@Test
	void jugadorAtacaConArmaSoloDentroDeSuAlcance() {
		Jugador steve = steve();
		Espada espada = new Espada(Material.DIAMANTE);

		Zombie cerca = new Zombie(20, 2, 64, 0, 3, 16, 3);
		acciones.atacar(steve, cerca, espada);
		assertEquals(20 - 7, cerca.getSalud());
		assertEquals(1561 - 1, espada.getDurabilidad());

		Zombie lejos = new Zombie(20, 10, 64, 0, 3, 16, 3);
		assertThrows(FueraDeAlcanceException.class, () -> acciones.atacar(steve, lejos, espada));
		assertEquals(20, lejos.getSalud());

		ReglaDelJuegoException error = assertThrows(ReglaDelJuegoException.class,
				() -> acciones.atacar(steve, steve, espada));
		assertEquals("auto_ataque", error.getCodigo());
	}

	@Test
	void aldeanoInfectadoNoComercia() {
		Aldeano a = aldeano();
		assertFalse(acciones.comerciar(a).isBlank());
		acciones.infectar(zombie(), a);
		assertTrue(a.isInfectado());
		ReglaDelJuegoException error = assertThrows(ReglaDelJuegoException.class,
				() -> acciones.comerciar(a));
		assertEquals("aldeano_infectado", error.getCodigo());
	}

	@Test
	void unZombieNoInfectaFueraDeSuRango() {
		Aldeano lejos = new Aldeano(20, 100, 64, 0, 2, false, "Herrero");
		assertThrows(FueraDeAlcanceException.class, () -> acciones.infectar(zombie(), lejos));
		assertFalse(lejos.isInfectado());
	}

	@Test
	void pasivaHuyeYSeAleja() {
		Aldeano a = aldeano();
		Zombie z = zombie();
		double antes = a.distanciaA(z);
		acciones.huir(a, z);
		assertTrue(a.distanciaA(z) > antes);
	}

	@Test
	void velocidadCeroEsValidaPeroImpideHuir() {
		Aldeano quieto = new Aldeano(20, 0, 64, 0, 0, false, "Pescador");
		assertThrows(ReglaDelJuegoException.class, () -> acciones.huir(quieto, zombie()));
		acciones.mover(quieto, 0, 0, 0);
		assertEquals(0, quieto.getPosicionX());
	}

	@Test
	void animalNoMontableSeRechaza() {
		Animal gallina = new Animal(4, 0, 64, 0, 1, false, false);
		ReglaDelJuegoException error = assertThrows(ReglaDelJuegoException.class,
				() -> acciones.montar(gallina, steve()));
		assertEquals("animal_no_montable", error.getCodigo());
		assertFalse(acciones.montar(new Animal(10, 0, 64, 0, 2, true, true), steve()).isBlank());
	}

	@Test
	void experienciaSoloSube() {
		Jugador j = steve();
		assertThrows(DatoInvalidoException.class, () -> acciones.ganarExperiencia(j, -10));
		acciones.ganarExperiencia(j, 5);
		assertEquals(5, j.getNivelEXP());
	}

	@Test
	void ganarExperienciaConDesbordamientoFallaYNoPierdeElNivel() {
		Jugador j = steve();
		acciones.ganarExperiencia(j, Integer.MAX_VALUE);
		assertEquals(Integer.MAX_VALUE, j.getNivelEXP());
		assertThrows(DatoInvalidoException.class, () -> acciones.ganarExperiencia(j, 1));
		assertEquals(Integer.MAX_VALUE, j.getNivelEXP());
	}
}
