package py.edu.uc.lp3.web;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import py.edu.uc.lp3.service.impl.ComportamientoServiceImpl;
import py.edu.uc.lp3.service.impl.CreeperServiceImpl;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Se importan los servicios reales para que las reglas que lanzan las
 * excepciones sean las del dominio y no una simulacion.
 */
@WebMvcTest(controllers = {
		IndexController.class,
		CreeperController.class,
		ComportamientoController.class
})
@Import({CreeperServiceImpl.class, ComportamientoServiceImpl.class})
class WebControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void indexRespondeOk() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.estado").value("API funcionando"));
	}

	@Test
	void creeperValidoSeCreaConSalud() throws Exception {
		mockMvc.perform(get("/api/minecraft/creeper")
						.param("salud", "20")
						.param("x", "6")
						.param("y", "64")
						.param("z", "0")
						.param("velocidad", "2")
						.param("rangoDeteccion", "16")
						.param("danoAtaque", "3")
						.param("tiempoExplosion", "30"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.salud").value(20))
				.andExpect(jsonPath("$.saludMaxima").value(20))
				.andExpect(jsonPath("$.tiempoExplosion").value(30));
	}

	@Test
	void saludCeroDevuelve400ConMensaje() throws Exception {
		mockMvc.perform(get("/api/minecraft/creeper")
						.param("salud", "0")
						.param("x", "6")
						.param("y", "64")
						.param("z", "0")
						.param("velocidad", "2")
						.param("rangoDeteccion", "16")
						.param("danoAtaque", "3")
						.param("tiempoExplosion", "30"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists())
				.andExpect(jsonPath("$.mensaje").value("La salud debe ser mayor que cero."));
	}

	@Test
	void tiempoDeExplosionNegativoDevuelve400() throws Exception {
		mockMvc.perform(get("/api/minecraft/creeper")
						.param("salud", "20")
						.param("x", "6")
						.param("y", "64")
						.param("z", "0")
						.param("velocidad", "2")
						.param("rangoDeteccion", "16")
						.param("danoAtaque", "3")
						.param("tiempoExplosion", "-1"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.error").exists())
				.andExpect(jsonPath("$.mensaje")
						.value("El tiempo de explosión debe ser mayor que cero."));
	}

	@Test
	void comportamientoDevuelveUnItemPorClaseHija() throws Exception {
		mockMvc.perform(get("/api/minecraft/comportamientos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(3))
				.andExpect(jsonPath("$[0].tipo").value("Creeper"))
				.andExpect(jsonPath("$[1].tipo").value("Aldeano"))
				.andExpect(jsonPath("$[2].tipo").value("Zombie"))
				.andExpect(jsonPath("$[0].comportamiento").isNotEmpty())
				.andExpect(jsonPath("$[1].comportamiento").isNotEmpty())
				.andExpect(jsonPath("$[2].comportamiento").isNotEmpty());
	}

	@Test
	void creeperPorPosicionUsaElConstructorSobrecargado() throws Exception {
		mockMvc.perform(get("/api/minecraft/creeper/por-posicion")
						.param("x", "10")
						.param("y", "64")
						.param("z", "-5"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.salud").value(20))
				.andExpect(jsonPath("$.posicionX").value(10.0))
				.andExpect(jsonPath("$.posicionY").value(64.0))
				.andExpect(jsonPath("$.posicionZ").value(-5.0))
				.andExpect(jsonPath("$.tiempoExplosion").value(30));
	}

	@Test
	void posicionInvalidaDevuelve400() throws Exception {
		mockMvc.perform(get("/api/minecraft/creeper/por-posicion")
						.param("x", "NaN")
						.param("y", "64")
						.param("z", "0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensaje")
						.value("La posición debe contener valores válidos."));
	}

}
