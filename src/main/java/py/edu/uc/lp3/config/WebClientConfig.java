package py.edu.uc.lp3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Arma los {@link WebClient} de las APIs externas: restful-api.dev y las
 * APIs Minecraft de otros proyectos del taller.
 * <p>
 * Parte del {@code WebClient.Builder} que Spring Boot ya configura (con el
 * mismo Jackson que usa la app) y le fija la URL base, así el servicio solo
 * escribe rutas relativas como {@code /objects/{id}}. Como hay varios, cada
 * servicio elige el suyo con {@code @Qualifier}. Todos llevan
 * {@link WebClientLogFilter}, que registra cada pedido en el log.
 */
@Configuration
public class WebClientConfig {

	@Bean
	public WebClient restfulApiWebClient(
			WebClient.Builder builder,
			@Value("${restful-api.base-url}") String baseUrl) {

		return builder
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.filter(WebClientLogFilter.para("restful-api"))
				.build();
	}

	/** Cliente para el mundo Minecraft de Ceci ({@code mundo-ceci.base-url}, variable {@code MUNDO_CECI_URL}). */
	@Bean
	public WebClient mundoCeciWebClient(
			WebClient.Builder builder,
			@Value("${mundo-ceci.base-url}") String baseUrl) {

		return builder
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.filter(WebClientLogFilter.para("mundo-ceci"))
				.build();
	}

	/** Cliente para la API Minecraft de otro proyecto del taller ({@code minecraft-remoto.base-url}). */
	@Bean
	public WebClient minecraftRemotoWebClient(
			WebClient.Builder builder,
			@Value("${minecraft-remoto.base-url}") String baseUrl) {

		return builder
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.filter(WebClientLogFilter.para("minecraft-remoto"))
				.build();
	}
}
