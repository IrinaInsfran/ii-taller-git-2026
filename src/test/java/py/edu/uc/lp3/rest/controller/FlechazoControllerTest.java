package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import py.edu.uc.lp3.client.MinecraftRemotoClient;
import py.edu.uc.lp3.dto.DisparoRemoto;
import py.edu.uc.lp3.exceptions.ServicioExternoException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Flechazo de punta a punta con H2 en memoria. La API Minecraft remota se
 * reemplaza por un mock que responde como ella: alcanza o no alcanza.
 */
@SpringBootTest
@AutoConfigureMockMvc
class FlechazoControllerTest {

	private static final String ENTIDADES = "/api/minecraft/entidades";
	private static final String FLECHAZOS = "/api/minecraft/flechazos";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntidadRepository entidadRepository;

	@Autowired
	private ExplosionRepository explosionRepository;

	@MockitoBean
	private MinecraftRemotoClient minecraftRemoto;

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

	private long esqueletoEnElOrigen() throws Exception {
		// Esqueleto por defecto: danoAtaque 4.
		return crear("""
				{"tipo":"ESQUELETO","posicionX":0,"posicionY":64,"posicionZ":0}""");
	}

	private long jugadorEn(double x, int salud) throws Exception {
		return crear("""
				{"tipo":"JUGADOR","salud":%d,"posicionX":%s,"posicionY":64,"posicionZ":0,
				 "velocidad":4,"nombre":"Steve"}""".formatted(salud, x));
	}

	private ResultActions disparar(long esqueletoId, long objetivoId) throws Exception {
		return mockMvc.perform(post(FLECHAZOS)
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"esqueletoId\":%d,\"objetivoId\":%d}".formatted(esqueletoId, objetivoId)));
	}

	private void laApiRemotaResponde(double distancia, boolean alcanza) {
		DisparoRemoto disparo = new DisparoRemoto("Esqueleto", 16, alcanza ? 14 : 15, 8.0, distancia,
				"dispara a 0.0", alcanza ? "dispara a " + distancia : "no alcanza a " + distancia);
		when(minecraftRemoto.dispararEsqueleto(anyString(), anyInt(), anyDouble(), anyDouble(), anyDouble(),
				eq(distancia))).thenReturn(disparo);
	}

	@Test
	void siLaFlechaLlegaElObjetivoPierdeElDanoDelEsqueletoYQuedaGuardado() throws Exception {
		long esqueleto = esqueletoEnElOrigen();
		long jugador = jugadorEn(5, 20);
		laApiRemotaResponde(5.0, true);

		disparar(esqueleto, jugador)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.distancia").value(5.0))
				.andExpect(jsonPath("$.rangoRemoto").value(8.0))
				.andExpect(jsonPath("$.impacto").value(true))
				.andExpect(jsonPath("$.dano").value(4))
				.andExpect(jsonPath("$.saludAntes").value(20))
				.andExpect(jsonPath("$.saludDespues").value(16))
				.andExpect(jsonPath("$.objetivoMurio").value(false));

		verify(minecraftRemoto).dispararEsqueleto(
				"Esqueleto-" + esqueleto, 20, 0.0, 64.0, 0.0, 5.0);
		mockMvc.perform(get(ENTIDADES + "/" + jugador))
				.andExpect(jsonPath("$.salud").value(16));
	}

	@Test
	void siLaApiRemotaDiceQueNoAlcanzaNoHayDano() throws Exception {
		long esqueleto = esqueletoEnElOrigen();
		// A 12 bloques: dentro de la detección propia (16) pero fuera del rango remoto (8).
		long jugador = jugadorEn(12, 20);
		laApiRemotaResponde(12.0, false);

		disparar(esqueleto, jugador)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.impacto").value(false))
				.andExpect(jsonPath("$.dano").value(0))
				.andExpect(jsonPath("$.saludDespues").value(20));
	}

	@Test
	void elFlechazoPuedeMatarAlObjetivo() throws Exception {
		long esqueleto = esqueletoEnElOrigen();
		long jugador = jugadorEn(3, 3);
		laApiRemotaResponde(3.0, true);

		disparar(esqueleto, jugador)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.saludDespues").value(0))
				.andExpect(jsonPath("$.objetivoMurio").value(true));

		disparar(esqueleto, jugador)
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.codigo").value("entidad_muerta"));
	}

	@Test
	void soloUnEsqueletoDisparaYNoSeLlamaALaApiRemota() throws Exception {
		long jugador = jugadorEn(0, 20);
		long zombie = crear("""
				{"tipo":"ZOMBIE","posicionX":2,"posicionY":64,"posicionZ":0}""");

		disparar(zombie, jugador)
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.codigo").value("solo_esqueletos_disparan"));

		verify(minecraftRemoto, never()).dispararEsqueleto(any(), anyInt(), anyDouble(), anyDouble(), anyDouble(),
				anyDouble());
	}

	@Test
	void siLaApiRemotaFallaResponde502YNadieRecibeDano() throws Exception {
		long esqueleto = esqueletoEnElOrigen();
		long jugador = jugadorEn(5, 20);
		when(minecraftRemoto.dispararEsqueleto(anyString(), anyInt(), anyDouble(), anyDouble(), anyDouble(),
				anyDouble())).thenThrow(new ServicioExternoException("La API Minecraft remota no respondió.", null));

		disparar(esqueleto, jugador)
				.andExpect(status().isBadGateway())
				.andExpect(jsonPath("$.codigo").value("servicio_externo_no_disponible"));

		mockMvc.perform(get(ENTIDADES + "/" + jugador))
				.andExpect(jsonPath("$.salud").value(20));
	}

	@Test
	void faltanIdsEs400() throws Exception {
		mockMvc.perform(post(FLECHAZOS).contentType(MediaType.APPLICATION_JSON).content("{}"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("campo_obligatorio"));
	}
}
