package py.edu.uc.lp3.rest.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * La especificación OpenAPI se genera con todos los controllers y Swagger UI
 * queda publicado.
 */
@SpringBootTest
@AutoConfigureMockMvc
class OpenApiTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void laEspecificacionIncluyeTodosLosServicios() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("API Minecraft — LP3"))
				.andExpect(jsonPath("$.tags[*].name").value(hasItems(
						"Estado", "Entidades", "Amenazas", "Comportamientos",
						"Explosiones", "Objetos externos")))
				.andExpect(jsonPath("$.paths['/api/minecraft/entidades'].post.responses['201']").exists())
				.andExpect(jsonPath("$.paths['/api/minecraft/entidades/{id}'].get.responses['404']").exists())
				.andExpect(jsonPath("$.paths['/api/externo/objetos'].get").exists())
				.andExpect(jsonPath("$.components.schemas.ErrorResponse").exists());
	}

	@Test
	void elServidorEsLaDireccionConLaQueSeAbrio() throws Exception {
		// Simula abrir Swagger desde http://10.0.2.15:8080 (la IP de la VM).
		mockMvc.perform(get("/v3/api-docs").with(pedido -> {
					pedido.setServerName("10.0.2.15");
					pedido.setServerPort(8080);
					return pedido;
				}))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.servers[0].url").value("http://10.0.2.15:8080"));
	}

	@Test
	void swaggerUiEstaPublicado() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk());
	}
}
