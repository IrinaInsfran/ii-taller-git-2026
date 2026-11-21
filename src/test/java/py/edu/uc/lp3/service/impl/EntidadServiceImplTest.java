package py.edu.uc.lp3.service.impl;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.dto.TipoEntidad;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.repository.EntidadRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Las validaciones del CRUD, que antes estaban en los constructores del
 * dominio. El repositorio es un mock: estos tests no usan H2.
 */
class EntidadServiceImplTest {

	private final EntidadRepository repositorio = mock(EntidadRepository.class);
	private final EntidadServiceImpl servicio = new EntidadServiceImpl(repositorio);

	private static EntidadRequest zombie(Integer salud, Double x, Double y, Double z,
	                                     Double rango, Integer dano) {
		return new EntidadRequest(TipoEntidad.ZOMBIE, salud, x, y, z, 3,
				null, null, rango, dano, null, null, null, null);
	}

	private static EntidadRequest jugador(String nombre, int nivelEXP) {
		return new EntidadRequest(TipoEntidad.JUGADOR, 20, 0.0, 64.0, 0.0, 5,
				nombre, nivelEXP, null, null, null, null, null, null);
	}

	private void rechaza(EntidadRequest datos, String mensaje) {
		DatoInvalidoException error = assertThrows(DatoInvalidoException.class,
				() -> servicio.crear(datos));
		assertEquals(mensaje, error.getMessage());
		verify(repositorio, never()).save(any());
	}

	@Test
	void rechazaEstadoInvalido() {
		rechaza(zombie(0, 0.0, 64.0, 0.0, 16.0, 3), "La salud debe ser mayor que cero.");
		rechaza(zombie(20, 0.0, 64.0, 0.0, 16.0, 0), "El daño de ataque debe ser mayor que cero.");
		rechaza(jugador(" ", 0), "El nombre del jugador no puede estar vacío.");
		rechaza(jugador("Steve", -1), "El nivel de experiencia no puede ser negativo.");
		rechaza(new EntidadRequest(TipoEntidad.CREEPER, 20, 0.0, 64.0, 0.0, 2,
						null, null, 16.0, 3, 0, null, null, null),
				"El tiempo de explosión debe ser mayor que cero.");
	}

	@Test
	void posicionesYRangoNoFinitosSeRechazan() {
		rechaza(zombie(20, Double.NaN, 64.0, 0.0, 16.0, 3), "La posición debe contener valores válidos.");
		rechaza(zombie(20, 0.0, Double.POSITIVE_INFINITY, 0.0, 16.0, 3), "La posición debe contener valores válidos.");
		rechaza(zombie(20, 0.0, 64.0, 0.0, Double.NaN, 3), "El rango de detección no puede ser negativo.");
		rechaza(zombie(20, 0.0, 64.0, 0.0, -1.0, 3), "El rango de detección no puede ser negativo.");
	}

	@Test
	void elConstructorSobrecargadoTambienSeValida() {
		rechaza(new EntidadRequest(TipoEntidad.CREEPER, null, Double.NaN, 64.0, 0.0, null,
						null, null, null, null, null, null, null, null),
				"La posición debe contener valores válidos.");
		rechaza(new EntidadRequest(TipoEntidad.ALDEANO, null, 0.0, 64.0, 0.0, null,
						null, null, null, null, null, null, "", null),
				"La profesión no puede estar vacía.");
	}

	@Test
	void unaEntidadValidaSeGuarda() {
		when(repositorio.save(any())).thenAnswer(invocacion -> invocacion.getArgument(0));

		Entidad creada = servicio.crear(jugador("Steve", 0));

		assertInstanceOf(Jugador.class, creada);
		verify(repositorio).save(creada);
	}

	@Test
	void elPutNoCambiaElTipo() {
		when(repositorio.findById(1L)).thenReturn(Optional.of(new Jugador("Steve")));

		DatoInvalidoException error = assertThrows(DatoInvalidoException.class,
				() -> servicio.actualizar(1L, zombie(20, 0.0, 64.0, 0.0, 16.0, 3)));
		assertEquals("cambio_de_tipo", error.getCodigo());
	}

	@Test
	void elPutNoEditaAUnMuerto() {
		Creeper muerto = new Creeper(0, 64, 0);
		muerto.morir();
		when(repositorio.findById(1L)).thenReturn(Optional.of(muerto));

		assertThrows(EntidadMuertaException.class,
				() -> servicio.actualizar(1L, new EntidadRequest(TipoEntidad.CREEPER, null, 0.0, 64.0, 0.0,
						null, null, null, null, null, null, null, null, null)));
		assertFalse(muerto.estaViva());
	}
}
