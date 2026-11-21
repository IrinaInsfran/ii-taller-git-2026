package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import py.edu.uc.lp3.client.CeciReglasClient;
import py.edu.uc.lp3.client.SolReglasClient;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.dto.RespuestaCompanera;
import py.edu.uc.lp3.dto.ValidacionCompaneraRequest;
import py.edu.uc.lp3.exceptions.CompaneraNoDisponibleException;
import py.edu.uc.lp3.exceptions.JugadaRechazadaException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;
import py.edu.uc.lp3.repository.JugadaRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * El escenario de punta a punta con H2 en memoria y las dos compañeras
 * simuladas: la jugada se guarda solo si A2 y A3 aceptan, y cuando alguna
 * rechaza o no responde la base queda exactamente igual.
 */
@SpringBootTest
@AutoConfigureMockMvc
class JugadaControllerTest {

	private static final String URL = "/api/minecraft/jugadas/explosion";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntidadRepository entidadRepository;

	@Autowired
	private ExplosionRepository explosionRepository;

	@Autowired
	private JugadaRepository jugadaRepository;

	@MockitoBean
	private CeciReglasClient ceci;

	@MockitoBean
	private SolReglasClient sol;

	private Long steveId;
	private Long creeperId;

	@BeforeEach
	void prepararMundo() {
		jugadaRepository.deleteAll();
		explosionRepository.deleteAll();
		entidadRepository.deleteAll();
		steveId = entidadRepository.save(new Jugador("Steve", 0, 64, 0)).getId();
		creeperId = entidadRepository.save(new Creeper(2, 64, 0)).getId();
	}

	private ResultActions jugar(Long creeper, int tiempo) throws Exception {
		return mockMvc.perform(post(URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"creeperId\":" + creeper + ",\"tiempoTranscurrido\":" + tiempo + "}"));
	}

	private void ceciAcepta() {
		when(ceci.validar(any())).thenReturn(new RespuestaCompanera("A2", "Cecilia",
				"http://ceci/explosiones/validar", 200, Map.of("aceptada", true)));
	}

	private void solAcepta() {
		when(sol.validar(any())).thenReturn(new RespuestaCompanera("A3", "Sol",
				"http://sol/explosiones/validar", 200, Map.of("aceptada", true)));
	}

	/** Lo que tiene que valer siempre después de un rechazo. */
	private void laBaseQuedoIgual() {
		assertThat(jugadaRepository.count()).isZero();
		assertThat(explosionRepository.count()).isZero();
		assertThat(entidadRepository.findById(steveId).orElseThrow().getSalud()).isEqualTo(20);
		assertThat(entidadRepository.findById(creeperId).orElseThrow().estaViva()).isTrue();
	}

	@Test
	void siLasDosAceptanSeGuardaLaJugadaYLaExplosion() throws Exception {
		ceciAcepta();
		solAcepta();

		jugar(creeperId, 30)
				.andExpect(status().isCreated())
				.andExpect(header().exists("Location"))
				.andExpect(jsonPath("$.creeperId").value(creeperId))
				.andExpect(jsonPath("$.a2.companera").value("Cecilia"))
				.andExpect(jsonPath("$.a3.companera").value("Sol"))
				.andExpect(jsonPath("$.explosion.jugadorObjetivo").value("Steve"))
				.andExpect(jsonPath("$.explosion.afectados.length()").value(1));

		assertThat(jugadaRepository.count()).isEqualTo(1);
		assertThat(explosionRepository.count()).isEqualTo(1);
		assertThat(entidadRepository.findById(steveId).orElseThrow().getSalud()).isLessThan(20);
		assertThat(entidadRepository.findById(creeperId).orElseThrow().estaViva()).isFalse();

		ArgumentCaptor<ValidacionCompaneraRequest> consulta = ArgumentCaptor.forClass(ValidacionCompaneraRequest.class);
		verify(ceci).validar(consulta.capture());
		assertThat(consulta.getValue().saludCreeper()).isEqualTo(20);
		assertThat(consulta.getValue().yaDetonado()).isFalse();
		assertThat(consulta.getValue().tiempoExplosion()).isEqualTo(30);
		assertThat(consulta.getValue().tiempoTranscurrido()).isEqualTo(30);
		assertThat(consulta.getValue().radioExplosion()).isEqualTo(6);
		assertThat(consulta.getValue().objetivos()).singleElement()
				.satisfies(o -> assertThat(o.tipo()).isEqualTo("jugador"));
	}

	@Test
	void laJugadaGuardadaSeLeeDespues() throws Exception {
		ceciAcepta();
		solAcepta();
		jugar(creeperId, 30).andExpect(status().isCreated());
		long id = jugadaRepository.findAll().getFirst().getId();

		mockMvc.perform(get("/api/minecraft/jugadas/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.a2.estado").value(200))
				.andExpect(jsonPath("$.a2.respuesta.aceptada").value(true))
				.andExpect(jsonPath("$.explosion.id").isNumber());

		mockMvc.perform(get("/api/minecraft/jugadas"))
				.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	void siCeciRechazaNoSeConsultaASolYNoCambiaNada() throws Exception {
		when(ceci.validar(any())).thenThrow(new JugadaRechazadaException("A2", "Cecilia", 422,
				Map.of("tipo", "CreeperMuertoException", "mensaje", "El Creeper está muerto."),
				"El Creeper está muerto."));

		jugar(creeperId, 30)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.error").value("jugada_rechazada"))
				.andExpect(jsonPath("$.codigo").value("rechazada_por_a2"))
				.andExpect(jsonPath("$.origen").value("A2"))
				.andExpect(jsonPath("$.companera").value("Cecilia"))
				.andExpect(jsonPath("$.estadoCompanera").value(422))
				.andExpect(jsonPath("$.respuestaCompanera.tipo").value("CreeperMuertoException"));

		verify(sol, never()).validar(any());
		laBaseQuedoIgual();
	}

	@Test
	void siSolRechazaNoCambiaNada() throws Exception {
		ceciAcepta();
		when(sol.validar(any())).thenThrow(new JugadaRechazadaException("A3", "Sol", 409,
				Map.of("mensaje", "Todavía no pasó el tiempo de explosión."),
				"Todavía no pasó el tiempo de explosión."));

		jugar(creeperId, 10)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.codigo").value("rechazada_por_a3"))
				.andExpect(jsonPath("$.origen").value("A3"))
				.andExpect(jsonPath("$.estadoCompanera").value(409));

		laBaseQuedoIgual();
	}

	@Test
	void siUnaCompaneraNoRespondeDevuelve502() throws Exception {
		ceciAcepta();
		when(sol.validar(any())).thenThrow(new CompaneraNoDisponibleException("A3", "Sol", null, null,
				"su API no responde.", null));

		jugar(creeperId, 30)
				.andExpect(status().isBadGateway())
				.andExpect(jsonPath("$.codigo").value("companera_no_disponible"))
				.andExpect(jsonPath("$.origen").value("A3"));

		laBaseQuedoIgual();
	}

	@Test
	void siLasDosAceptanPeroMiReglaRechazaSeDeshaceTodo() throws Exception {
		ceciAcepta();
		solAcepta();
		// Steve a 5 bloques: dentro del radio de la onda (6), lejos para la mecha corta (3).
		entidadRepository.deleteById(creeperId);
		Long lejos = entidadRepository.save(new Creeper(5, 64, 0)).getId();

		jugar(lejos, 30)
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.codigo").value("fuera_de_alcance"));

		assertThat(jugadaRepository.count()).isZero();
		assertThat(explosionRepository.count()).isZero();
		assertThat(entidadRepository.findById(steveId).orElseThrow().getSalud()).isEqualTo(20);
	}

	@Test
	void unSegundoIntentoAvisaQueElCreeperYaDetono() throws Exception {
		ceciAcepta();
		solAcepta();
		entidadRepository.save(new Jugador("Alex", 0, 64, 4));
		jugar(creeperId, 30).andExpect(status().isCreated());

		jugar(creeperId, 30);

		ArgumentCaptor<ValidacionCompaneraRequest> consulta = ArgumentCaptor.forClass(ValidacionCompaneraRequest.class);
		verify(ceci, org.mockito.Mockito.times(2)).validar(consulta.capture());
		assertThat(consulta.getAllValues().get(1).yaDetonado()).isTrue();
		assertThat(consulta.getAllValues().get(1).saludCreeper()).isZero();
	}

	@Test
	void sinNadieEnElRadioNoSeConsultaANadie() throws Exception {
		entidadRepository.deleteById(steveId);

		jugar(creeperId, 30)
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.codigo").value("fuera_de_alcance"));

		verifyNoInteractions(ceci, sol);
	}

	@Test
	void datosFaltantesOInvalidos() throws Exception {
		mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("{\"creeperId\":1}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value("El campo 'tiempoTranscurrido' es obligatorio."));

		jugar(999999L, 30).andExpect(status().isNotFound());
		jugar(steveId, 30).andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.codigo").value("solo_creepers_explotan"));

		verifyNoInteractions(ceci, sol);
	}

	@Test
	void muestraAQuienConsulta() throws Exception {
		when(ceci.getRol()).thenReturn("A2");
		when(ceci.getNombre()).thenReturn("Cecilia");
		when(ceci.url()).thenReturn("http://ceci/explosiones/validar");
		when(sol.getRol()).thenReturn("A3");
		when(sol.getNombre()).thenReturn("Sol");
		when(sol.url()).thenReturn("http://sol/explosiones/validar");

		mockMvc.perform(get("/api/minecraft/jugadas/companeras"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].rol").value("A2"))
				.andExpect(jsonPath("$[1].companera").value("Sol"));
	}
}
