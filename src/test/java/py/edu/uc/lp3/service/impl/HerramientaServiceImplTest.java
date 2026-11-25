package py.edu.uc.lp3.service.impl;

import java.util.List;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Espada;
import py.edu.uc.lp3.domain.Hacha;
import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.domain.Mineral;
import py.edu.uc.lp3.domain.Pico;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.exceptions.HerramientaRotaException;

import static org.junit.jupiter.api.Assertions.*;

class HerramientaServiceImplTest {

	private final HerramientaServiceImpl herramientas = new HerramientaServiceImpl();

	private Zombie zombieEn(double x) { return new Zombie(20, x, 64, 0, 3, 16, 3); }

	@Test
	void cadaArmaHaceSuPropioDano() {
		List<Arma> armas = List.of(new Espada(Material.HIERRO), new Hacha(Material.HIERRO));
		Zombie zombie = zombieEn(1);

		for (Arma arma : armas) {
			herramientas.golpear(arma, zombie);
		}

		// espada de hierro 6 + hacha de hierro 9
		assertEquals(20 - 6 - 9, zombie.getSalud());
	}

	@Test
	void elHachaSeDesgastaMasAlPelearQueAlTalar() {
		Hacha hacha = new Hacha(Material.PIEDRA);
		herramientas.golpear(hacha, zombieEn(1));
		assertEquals(131 - 2, hacha.getDurabilidad());

		herramientas.talar(hacha);
		assertEquals(131 - 3, hacha.getDurabilidad());
	}

	@Test
	void unaHerramientaRotaNoSePuedeUsar() {
		Espada espada = new Espada(Material.ORO);
		Zombie zombie = new Zombie(2000, 1, 64, 0, 3, 16, 3);

		for (int golpe = 0; golpe < Material.ORO.getDurabilidad(); golpe++) {
			herramientas.golpear(espada, zombie);
		}

		assertTrue(espada.estaRota());
		int saludAntes = zombie.getSalud();
		HerramientaRotaException error = assertThrows(HerramientaRotaException.class,
				() -> herramientas.golpear(espada, zombie));
		assertEquals("herramienta_rota", error.getCodigo());
		assertEquals("La espada está rota y no se puede usar.", error.getMessage());
		assertEquals(saludAntes, zombie.getSalud());
	}

	@Test
	void elPicoSoloObtieneMineralesDeSuNivel() {
		Pico madera = new Pico(Material.MADERA);
		Pico hierro = new Pico(Material.HIERRO);

		assertTrue(herramientas.minar(madera, Mineral.CARBON));
		assertFalse(herramientas.minar(madera, Mineral.DIAMANTE));
		assertEquals(59 - 2, madera.getDurabilidad());

		assertTrue(herramientas.minar(hierro, Mineral.DIAMANTE));
		assertFalse(herramientas.puedeObtener(hierro, Mineral.OBSIDIANA));
		assertTrue(herramientas.puedeObtener(new Pico(Material.DIAMANTE), Mineral.OBSIDIANA));
	}

	@Test
	void elPicoDeOroTieneElNivelDeLaMadera() {
		assertFalse(herramientas.puedeObtener(new Pico(Material.ORO), Mineral.HIERRO));
	}
}
