package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.client.MundoCeciClient;
import py.edu.uc.lp3.dto.EntidadMundoCeci;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.exceptions.ServicioExternoException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Portal de punta a punta con H2 en memoria; el mundo de Ceci es un mock.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PortalControllerTest {

	private static final String ENTIDADES = "/api/minecraft/entidades";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntidadRepository entidadRepository;

	@Autowired
	private ExplosionRepository explosionRepository;

	@MockitoBean
	private MundoCeciClient mundoCeci;

	@BeforeEach
	void limpiarBase() {
		explosionRepository.deleteAll();
		entidadRepository.deleteAll();
	}

	private long crear(String json) throws Exception {
		String respuesta = mockMvc.perform(post(ENTIDADES)
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		return Long.parseLong(respuesta.replaceAll("^\\{\"id\":(\\d+).*", "$1").trim());
	}

	private long steve() throws Exception {
		return crear("""
				{"tipo":"JUGADOR","salud":20,"posicionX":1,"posicionY":64,"posicionZ":2,
				 "velocidad":4,"nombre":"Steve","nivelEXP":3}""");
	}

	private void elMundoDeCeciAcepta() {
		when(mundoCeci.crearEntidad(any())).thenAnswer(invocacion -> {
			EntidadMundoCeci e = invocacion.getArgument(0);
			return new EntidadMundoCeci(42L, e.tipo(), e.nombre(), e.vida(), e.vidaMaxima(),
					e.x(), e.y(), e.z(), e.nivel(), e.experiencia(), e.detonado());
		});
	}

	@Test
	void elJugadorViajaTraducidoYSaleDeEsteMundo() throws Exception {
		long jugador = steve();
		elMundoDeCeciAcepta();

		mockMvc.perform(post(ENTIDADES + "/" + jugador + "/portal"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.tipo").value("JUGADOR"))
				.andExpect(jsonPath("$.conservadaEnEsteMundo").value(false))
				.andExpect(jsonPath("$.mundoDestino").value("http://localhost:1"))
				.andExpect(jsonPath("$.entidadEnDestino.id").value(42))
				.andExpect(jsonPath("$.entidadEnDestino.tipo").value("Jugador"));

		ArgumentCaptor<EntidadMundoCeci> enviada = ArgumentCaptor.forClass(EntidadMundoCeci.class);
		verify(mundoCeci).crearEntidad(enviada.capture());
		assertThat(enviada.getValue()).isEqualTo(
				new EntidadMundoCeci(null, "Jugador", "Steve", 20, 20, 1, 64, 2, 3, 0, null));

		mockMvc.perform(get(ENTIDADES + "/" + jugador)).andExpect(status().isNotFound());
	}

	@Test
	void conConservarSeCopiaYSigueAca() throws Exception {
		long zombie = crear("""
				{"tipo":"ZOMBIE","posicionX":4,"posicionY":64,"posicionZ":0}""");
		elMundoDeCeciAcepta();

		mockMvc.perform(post(ENTIDADES + "/" + zombie + "/portal").param("conservar", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.conservadaEnEsteMundo").value(true))
				.andExpect(jsonPath("$.entidadEnDestino.nombre").value("Zombie-" + zombie));

		mockMvc.perform(get(ENTIDADES + "/" + zombie)).andExpect(status().isOk());
	}

	@Test
	void unTipoQueNoExisteAllaNoCruzaYNoSeLlamaAlOtroMundo() throws Exception {
		long esqueleto = crear("""
				{"tipo":"ESQUELETO","posicionX":0,"posicionY":64,"posicionZ":0}""");

		mockMvc.perform(post(ENTIDADES + "/" + esqueleto + "/portal"))
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.codigo").value("tipo_inexistente_en_destino"));

		verify(mundoCeci, never()).crearEntidad(any());
	}

	@Test
	void siElMundoDeCeciLaRechazaSeQuedaAca() throws Exception {
		long jugador = steve();
		when(mundoCeci.crearEntidad(any())).thenThrow(new ReglaDelJuegoException(
				"rechazada_por_mundo_ceci",
				"El mundo de Ceci rechazó la entidad: Ya existe una entidad con ese nombre."));

		mockMvc.perform(post(ENTIDADES + "/" + jugador + "/portal"))
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.codigo").value("rechazada_por_mundo_ceci"));

		mockMvc.perform(get(ENTIDADES + "/" + jugador)).andExpect(status().isOk());
	}

	@Test
	void siElMundoDeCeciNoRespondeEs502YSeQuedaAca() throws Exception {
		long jugador = steve();
		when(mundoCeci.crearEntidad(any())).thenThrow(
				new ServicioExternoException("El mundo de Ceci no respondió en 5 s.", null));

		mockMvc.perform(post(ENTIDADES + "/" + jugador + "/portal"))
				.andExpect(status().isBadGateway());

		mockMvc.perform(get(ENTIDADES + "/" + jugador)).andExpect(status().isOk());
	}

	@Test
	void unConservarQueNoEsBooleanoEs400YNoSeLlamaAlOtroMundo() throws Exception {
		long jugador = steve();

		mockMvc.perform(post(ENTIDADES + "/" + jugador + "/portal").param("conservar", "quizas"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("parametro_invalido"))
				.andExpect(jsonPath("$.mensaje").value("El valor 'quizas' no sirve para el parámetro 'conservar'."));

		verify(mundoCeci, never()).crearEntidad(any());
	}

	@Test
	void unaEntidadInexistenteEs404() throws Exception {
		mockMvc.perform(post(ENTIDADES + "/999/portal"))
				.andExpect(status().isNotFound());
	}
}
