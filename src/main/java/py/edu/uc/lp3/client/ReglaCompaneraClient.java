package py.edu.uc.lp3.client;

import java.time.Duration;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import py.edu.uc.lp3.config.WebClientLogFilter;
import py.edu.uc.lp3.dto.RespuestaCompanera;
import py.edu.uc.lp3.dto.ValidacionCompaneraRequest;
import py.edu.uc.lp3.exceptions.CompaneraNoDisponibleException;
import py.edu.uc.lp3.exceptions.JugadaRechazadaException;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * Consulta la regla de una compañera del grupo de POO-09 y traduce su
 * respuesta a la jerarquía de este proyecto:
 * <ul>
 *   <li>2xx: la regla acepta y se devuelve lo que contestó;</li>
 *   <li>4xx: la regla rechaza, {@link JugadaRechazadaException};</li>
 *   <li>5xx, sin conexión o sin respuesta a tiempo:
 *       {@link CompaneraNoDisponibleException}.</li>
 * </ul>
 * En los dos errores viaja el cuerpo original de la compañera. Cada
 * subclase fija el rol (A2 o A3); la URL, la ruta y el nombre salen de
 * {@code application.properties}.
 */
public abstract class ReglaCompaneraClient {

	private static final Logger log = LogManager.getLogger(ReglaCompaneraClient.class);
	private static final JsonMapper JSON = JsonMapper.builder().build();

	private final String rol;
	private final String nombre;
	private final String baseUrl;
	private final String ruta;
	private final WebClient webClient;
	private final Duration timeout;

	/**
	 * @param builder el {@code WebClient.Builder} de Spring Boot (con su Jackson);
	 *                se clona, así cada compañera tiene su propio cliente
	 */
	protected ReglaCompaneraClient(String rol, String nombre, String baseUrl, String ruta,
	                               WebClient.Builder builder, Duration timeout) {
		this.rol = rol;
		this.nombre = nombre;
		this.baseUrl = baseUrl;
		this.ruta = ruta;
		this.timeout = timeout;
		this.webClient = builder.clone()
				.baseUrl(baseUrl)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.filter(WebClientLogFilter.para(rol + "-" + nombre.toLowerCase()))
				.build();
	}

	public String getRol() {
		return rol;
	}

	public String getNombre() {
		return nombre;
	}

	public String getRuta() {
		return ruta;
	}

	/**
	 * Le pregunta a la compañera si la jugada vale.
	 *
	 * @throws JugadaRechazadaException       si su regla la rechaza (4xx)
	 * @throws CompaneraNoDisponibleException si no responde, se vence el tiempo o falla (5xx)
	 */
	public RespuestaCompanera validar(ValidacionCompaneraRequest pedido) {
		log.debug("[{}] Consultando a {} en {}: {}", rol, nombre, ruta, pedido);
		ResponseEntity<String> respuesta;
		try {
			respuesta = webClient.post()
					.uri(ruta)
					.contentType(MediaType.APPLICATION_JSON)
					.bodyValue(pedido)
					.exchangeToMono(r -> r.toEntity(String.class))
					.block(timeout);
		} catch (WebClientException excepcion) {
			log.warn("[{}] {} no respondió: {}", rol, nombre, excepcion.getMessage());
			throw new CompaneraNoDisponibleException(rol, nombre, null, null,
					"su API no responde (" + excepcion.getMessage() + ").", excepcion);
		} catch (IllegalStateException excepcion) {
			// block(timeout) lanza IllegalStateException si se vence el tiempo.
			log.warn("[{}] {} no respondió en {}", rol, nombre, timeout);
			throw new CompaneraNoDisponibleException(rol, nombre, null, null,
					"no respondió en " + timeout.toSeconds() + " s.", excepcion);
		}

		if (respuesta == null) {
			throw new CompaneraNoDisponibleException(rol, nombre, null, null, "respondió sin contenido.", null);
		}

		int estado = respuesta.getStatusCode().value();
		Object cuerpo = comoJson(respuesta.getBody());
		log.debug("[{}] {} respondió {}: {}", rol, nombre, estado, cuerpo);

		if (respuesta.getStatusCode().is2xxSuccessful()) {
			return new RespuestaCompanera(rol, nombre, url(), estado, cuerpo);
		}
		if (respuesta.getStatusCode().is4xxClientError()) {
			throw new JugadaRechazadaException(rol, nombre, estado, cuerpo, detalle(cuerpo, estado));
		}
		throw new CompaneraNoDisponibleException(rol, nombre, estado, cuerpo,
				"su API falló con " + estado + ".", null);
	}

	/** La URL completa que se consulta, para mostrarla en las respuestas. */
	public String url() {
		return baseUrl + ruta;
	}

	/** El cuerpo como JSON si se puede leer; si no, el texto tal cual. */
	private static Object comoJson(String texto) {
		if (texto == null || texto.isBlank()) {
			return null;
		}
		try {
			return JSON.readValue(texto, Object.class);
		} catch (JacksonException excepcion) {
			return texto;
		}
	}

	/** El {@code mensaje} del error de la compañera, que usan las dos; si no hay, el código. */
	private static String detalle(Object cuerpo, int estado) {
		if (cuerpo instanceof Map<?, ?> mapa && mapa.get("mensaje") instanceof String mensaje && !mensaje.isBlank()) {
			return mensaje.endsWith(".") ? mensaje : mensaje + ".";
		}
		return "respondió " + estado + ".";
	}
}
