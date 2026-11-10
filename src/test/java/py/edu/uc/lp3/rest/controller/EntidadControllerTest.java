package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.repository.EntidadRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba el CRUD de punta a punta: controller, servicio, repositorio y H2
 * en memoria.
 */
@SpringBootTest
@AutoConfigureMockMvc
class EntidadControllerTest {

	private static final String URL = "/api/minecraft/entidades";

	private static final String JUGADOR = """
			{"tipo":"JUGADOR","salud":20,"posicionX":0,"posicionY":64,"posicionZ":0,
			 "velocidad":4,"nombre":"Steve","nivelEXP":5}
			""";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntidadRepository entidadRepository;

	@BeforeEach
	void limpiarBase() {
		entidadRepository.deleteAll();
	}

	private long crearJugador() throws Exception {
		String respuesta = mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JUGADOR))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		return Long.parseLong(respuesta.replaceAll("^\\{\"id\":(\\d+).*", "$1").trim());
	}

	@Test
	void crearDevuelve201ConLocation() throws Exception {
		mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JUGADOR))
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.id").isNumber())
				.andExpect(jsonPath("$.tipo").value("JUGADOR"))
				.andExpect(jsonPath("$.nombre").value("Steve"))
				.andExpect(jsonPath("$.rangoDeteccion").doesNotExist());
	}

	@Test
	void listarYObtenerPorId() throws Exception {
		long id = crearJugador();

		mockMvc.perform(get(URL))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1));

		mockMvc.perform(get(URL + "/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.nombre").value("Steve"));
	}

	@Test
	void actualizarReemplazaLosDatos() throws Exception {
		long id = crearJugador();

		mockMvc.perform(put(URL + "/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JUGADOR.replace("Steve", "Alex").replace("\"salud\":20", "\"salud\":15")))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.nombre").value("Alex"))
				.andExpect(jsonPath("$.salud").value(15));
	}

	@Test
	void eliminarYLuegoNoEncontrar() throws Exception {
		long id = crearJugador();

		mockMvc.perform(delete(URL + "/" + id))
				.andExpect(status().isNoContent());

		mockMvc.perform(get(URL + "/" + id))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.error").value("no_encontrada"));
	}

	@Test
	void campoObligatorioFaltanteDevuelve400() throws Exception {
		mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"tipo":"ZOMBIE","salud":20,"posicionX":0,"posicionY":64,
								 "posicionZ":0,"velocidad":1,"danoAtaque":3}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje")
						.value("El campo 'rangoDeteccion' es obligatorio para un ZOMBIE."));
	}

	@Test
	void reglaDelDominioDevuelve400() throws Exception {
		mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JUGADOR.replace("\"salud\":20", "\"salud\":0")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value("La salud debe ser mayor que cero."));
	}

	@Test
	void nombreDeJugadorRepetidoDevuelve400() throws Exception {
		crearJugador();

		mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JUGADOR.replace("Steve", "STEVE")))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value("Ya existe un jugador con el nombre 'STEVE'."));
	}

	@Test
	void noSePuedeCambiarElTipo() throws Exception {
		long id = crearJugador();

		mockMvc.perform(put(URL + "/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"tipo":"ANIMAL","salud":10,"posicionX":0,"posicionY":64,
								 "posicionZ":0,"velocidad":1}
								"""))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value(
						"No se puede cambiar el tipo de una entidad: la entidad " + id + " es JUGADOR."));
	}

	@Test
	void sinEstadisticasUsaElConstructorSobrecargado() throws Exception {
		mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"tipo":"JUGADOR","nombre":"Alex"}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.salud").value(20))
				.andExpect(jsonPath("$.posicionY").value(64.0))
				.andExpect(jsonPath("$.velocidad").value(4))
				.andExpect(jsonPath("$.nivelEXP").value(0));

		mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"tipo":"ZOMBIE","posicionX":4,"posicionY":64,"posicionZ":0}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rangoDeteccion").value(35.0))
				.andExpect(jsonPath("$.danoAtaque").value(3));
	}

	@Test
	void postIlegalNoAgregaNadaALaBase() throws Exception {
		crearJugador();

		mockMvc.perform(post(URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"tipo":"CREEPER","salud":-5,"posicionX":0,"posicionY":64,
								 "posicionZ":0,"velocidad":1,"rangoDeteccion":16,
								 "danoAtaque":3,"tiempoExplosion":30}
								"""))
				.andExpect(status().isBadRequest());

		mockMvc.perform(get(URL))
				.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void unaEntidadMuertaNoSeEdita() throws Exception {
		long id = crearJugador();
		entidadRepository.findById(id).ifPresent(jugador -> {
			jugador.recibirDano(20);
			entidadRepository.save(jugador);
		});

		mockMvc.perform(put(URL + "/" + id)
						.contentType(MediaType.APPLICATION_JSON)
						.content(JUGADOR))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("entidad_muerta"));

		mockMvc.perform(get(URL + "/" + id))
				.andExpect(jsonPath("$.salud").value(0));
	}

	@Test
	void comportamientosSeLeenDeLoGuardado() throws Exception {
		crearJugador();

		mockMvc.perform(get("/api/minecraft/comportamientos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].tipo").value("Jugador"))
				.andExpect(jsonPath("$[0].comportamiento").isNotEmpty());
	}

	@Test
	void amenazasMezclaMobsYArmas() throws Exception {
		long steve = crearJugador();
		mockMvc.perform(post(URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"tipo":"ZOMBIE","posicionX":10,"posicionY":64,"posicionZ":0}
						"""));
		mockMvc.perform(post(URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"tipo":"CREEPER","posicionX":3,"posicionY":64,"posicionZ":0}
						"""));

		mockMvc.perform(get(URL + "/" + steve + "/amenazas")
						.param("arma", "ESPADA")
						.param("material", "DIAMANTE"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.objetivoId").value(steve))
				.andExpect(jsonPath("$.amenazas.length()").value(3))
				.andExpect(jsonPath("$.amenazas[0].origen").value("Creeper"))
				.andExpect(jsonPath("$.amenazas[0].dano").value(16))
				.andExpect(jsonPath("$.amenazas[1].origen").value("Espada"))
				.andExpect(jsonPath("$.amenazas[1].dano").value(7))
				.andExpect(jsonPath("$.amenazas[2].origen").value("Zombie"))
				.andExpect(jsonPath("$.amenazas[2].dano").value(3))
				.andExpect(jsonPath("$.danoTotal").value(26))
				.andExpect(jsonPath("$.sobreviviria").value(false));
	}

	@Test
	void armaInexistenteDevuelve400() throws Exception {
		long steve = crearJugador();

		mockMvc.perform(get(URL + "/" + steve + "/amenazas").param("arma", "BAZUCA"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("parametro_invalido"));
	}

	@Test
	void actualizarIdInexistenteDevuelve404() throws Exception {
		mockMvc.perform(put(URL + "/999999")
						.contentType(MediaType.APPLICATION_JSON)
						.content(JUGADOR))
				.andExpect(status().isNotFound());
	}
}
