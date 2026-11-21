package py.edu.uc.lp3.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

/**
 * Datos generales de la especificación OpenAPI que genera springdoc.
 * <p>
 * springdoc recorre los {@code @RestController} y arma la especificación
 * solo; acá se agrega lo que no puede deducir del código: título,
 * descripción y versión.
 * <ul>
 *   <li>Especificación: {@code GET /v3/api-docs}</li>
 *   <li>Swagger UI: {@code GET /swagger-ui.html}</li>
 * </ul>
 */
@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI minecraftOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("API Minecraft — LP3")
						.version("0.0.1-SNAPSHOT")
						.description("""
								CRUD de entidades de Minecraft guardadas en H2, protocolo `Amenaza`, \
								comportamientos polimórficos y simulación de explosiones.

								Los errores siempre responden `{"error", "codigo", "mensaje"}`.""")
						.contact(new Contact().name("Irina"))
						.license(new License().name("Ver LICENSE del repositorio")));
		// Sin servidores fijos: springdoc usa la dirección con la que se abrió
		// Swagger (localhost, 10.0.2.15 o la IP que tenga la VM), así "Try it out"
		// le pega al mismo host y el navegador no lo bloquea por CORS.
	}
}
