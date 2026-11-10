package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba la simulacion de punta a punta: las dos reglas del dominio se
 * aplican, el daño queda guardado en H2 y la explosion queda en el historial.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ExplosionControllerTest {

	private static final String ENTIDADES = "/api/minecraft/entidades";
	private static final String EXPLOSIONES = "/api/minecraft/explosiones";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntidadRepository entidadRepository;

	@Autowired
	private ExplosionRepository explosionRepository;

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

	private long jugadorEn(double x) throws Exception {
		return crear("""
				{"tipo":"JUGADOR","salud":20,"posicionX":%s,"posicionY":64,"posicionZ":0,
				 "velocidad":4,"nombre":"Steve"}
				""".formatted(x));
	}

	private long creeperEn(double x) throws Exception {
		// Sin estadisticas: usa el constructor sobrecargado Creeper(x, y, z)
		return crear("""
				{"tipo":"CREEPER","posicionX":%s,"posicionY":64,"posicionZ":0}
				""".formatted(x));
	}

	private ResultActions detonar(Object creeperId) throws Exception {
		return mockMvc.perform(post(EXPLOSIONES)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"creeperId\":" + creeperId + "}"));
	}

	@Test
	void explosionDanaALasEntidadesCercanasYLoGuarda() throws Exception {
		long steve = jugadorEn(0);
		long creeper = creeperEn(3);
		long aldeano = crear("""
				{"tipo":"ALDEANO","salud":20,"posicionX":5,"posicionY":64,"posicionZ":0,
				 "velocidad":2,"profesion":"Herrero"}
				""");
		long vaca = crear("""
				{"tipo":"ANIMAL","salud":10,"posicionX":20,"posicionY":64,"posicionZ":0,
				 "velocidad":1}
				""");

		detonar(creeper)
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.creeperId").value(creeper))
				.andExpect(jsonPath("$.jugadorObjetivo").value("Steve"))
				.andExpect(jsonPath("$.radio").value(6.0))
				.andExpect(jsonPath("$.entidadesAlcanzadas").value(2))
				.andExpect(jsonPath("$.entidadesMuertas").value(1))
				.andExpect(jsonPath("$.afectados.length()").value(2))
				.andExpect(jsonPath("$.afectados[?(@.id == " + steve + ")].dano").value(16))
				.andExpect(jsonPath("$.afectados[?(@.id == " + steve + ")].saludRestante").value(4))
				.andExpect(jsonPath("$.afectados[?(@.id == " + aldeano + ")].murio").value(true));

		mockMvc.perform(get(ENTIDADES + "/" + creeper))
				.andExpect(jsonPath("$.viva").value(false));
		mockMvc.perform(get(ENTIDADES + "/" + steve))
				.andExpect(jsonPath("$.salud").value(4));
		mockMvc.perform(get(ENTIDADES + "/" + vaca))
				.andExpect(jsonPath("$.salud").value(10));

		mockMvc.perform(get(EXPLOSIONES))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].creeperId").value(creeper))
				.andExpect(jsonPath("$[0].danoTotal").value(16 + 24))
				.andExpect(jsonPath("$[0].afectados").doesNotExist());
	}

	@Test
	void sinJugadorCercaDevuelve422YNadaCambia() throws Exception {
		long steve = jugadorEn(0);
		long creeper = creeperEn(5);

		detonar(creeper)
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.error").value("fuera_de_alcance"))
				.andExpect(jsonPath("$.codigo").value("fuera_de_alcance"));

		mockMvc.perform(get(ENTIDADES + "/" + creeper))
				.andExpect(jsonPath("$.viva").value(true));
		mockMvc.perform(get(ENTIDADES + "/" + steve))
				.andExpect(jsonPath("$.salud").value(20));
		mockMvc.perform(get(EXPLOSIONES))
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void soloUnCreeperPuedeExplotar() throws Exception {
		long steve = jugadorEn(0);

		detonar(steve)
				.andExpect(status().is(422))
				.andExpect(jsonPath("$.error").value("regla_del_juego"))
				.andExpect(jsonPath("$.codigo").value("solo_creepers_explotan"));
	}

	@Test
	void unCreeperNoExplotaDosVeces() throws Exception {
		jugadorEn(0);
		long creeper = creeperEn(3);

		detonar(creeper).andExpect(status().isCreated());
		detonar(creeper)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("entidad_muerta"))
				.andExpect(jsonPath("$.codigo").value("entidad_muerta"));
	}

	@Test
	void creeperInexistenteDevuelve404() throws Exception {
		detonar(999999)
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codigo").value("entidad_no_encontrada"));
	}

	@Test
	void sinCreeperIdDevuelve400() throws Exception {
		detonar(null)
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("campo_obligatorio"));
	}

	@Test
	void explosionInexistenteDevuelve404() throws Exception {
		mockMvc.perform(get(EXPLOSIONES + "/999999"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.codigo").value("explosion_no_encontrada"));
	}
}
