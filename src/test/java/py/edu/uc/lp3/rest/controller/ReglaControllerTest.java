package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import py.edu.uc.lp3.repository.EntidadRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * La regla «mecha corta» publicada para otros servicios: acepta con 200,
 * rechaza con 409 o 422 y nunca toca la base.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReglaControllerTest {

	private static final String URL = "/api/minecraft/reglas/mecha-corta";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntidadRepository entidadRepository;

	private ResultActions consultar(String cuerpo) throws Exception {
		return mockMvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(cuerpo));
	}

	@Test
	void apuntaAlJugadorVivoMasCercanoSinMirarAlAldeano() throws Exception {
		long antes = entidadRepository.count();

		consultar("""
				{"saludCreeper":20,"creeper":{"x":0,"y":64,"z":0},
				 "objetivos":[
				   {"tipo":"aldeano","salud":20,"posicion":{"x":1,"y":64,"z":0}},
				   {"tipo":"jugador","salud":20,"posicion":{"x":3,"y":64,"z":0}},
				   {"tipo":"JUGADOR","salud":5,"posicion":{"x":0,"y":64,"z":2}}]}
				""")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.aceptada").value(true))
				.andExpect(jsonPath("$.regla").value("mecha-corta"))
				.andExpect(jsonPath("$.objetivo").value(2))
				.andExpect(jsonPath("$.distancia").value(2.0));

		assertEquals(antes, entidadRepository.count(), "consultar una regla no guarda nada");
	}

	@Test
	void aceptaElCuerpoDeSolConCamposDeMas() throws Exception {
		consultar("""
				{"tiempoExplosion":3,"tiempoTranscurrido":3,"radioExplosion":5,
				 "creeper":{"x":0,"y":0,"z":0},
				 "objetivos":[
				   {"tipo":"jugador","salud":20,"posicion":{"x":2,"y":0,"z":0}},
				   {"tipo":"aldeano","salud":20,"posicion":{"x":4,"y":0,"z":0}}]}
				""")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.objetivo").value(0));
	}

	@Test
	void sinJugadorVivoCercaRechazaCon422() throws Exception {
		consultar("""
				{"creeper":{"x":0,"y":64,"z":0},
				 "objetivos":[
				   {"tipo":"jugador","salud":20,"posicion":{"x":5,"y":64,"z":0}},
				   {"tipo":"jugador","salud":0,"posicion":{"x":1,"y":64,"z":0}}]}
				""")
				.andExpect(status().isUnprocessableContent())
				.andExpect(jsonPath("$.codigo").value("fuera_de_alcance"));
	}

	@Test
	void creeperMuertoRechazaCon409() throws Exception {
		consultar("""
				{"saludCreeper":0,"creeper":{"x":0,"y":64,"z":0},
				 "objetivos":[{"tipo":"jugador","posicion":{"x":1,"y":64,"z":0}}]}
				""")
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.codigo").value("entidad_muerta"));
	}

	@Test
	void faltanDatosDevuelve400() throws Exception {
		consultar("""
				{"creeper":{"x":0,"y":64},"objetivos":[{"tipo":"jugador","posicion":{"x":1,"y":64,"z":0}}]}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje").value("«creeper» necesita x, y, z numéricos."));

		consultar("""
				{"creeper":{"x":0,"y":64,"z":0},"objetivos":[]}
				""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.codigo").value("dato_invalido"));
	}
}
