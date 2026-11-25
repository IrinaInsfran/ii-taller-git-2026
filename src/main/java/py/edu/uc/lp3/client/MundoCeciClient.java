package py.edu.uc.lp3.client;

import java.time.Duration;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import py.edu.uc.lp3.dto.EntidadMundoCeci;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.exceptions.ServicioExternoException;
import reactor.core.publisher.Mono;

/**
 * Habla con la API Minecraft de Ceci. La URL no está en el código: sale de
 * {@code mundo-ceci.base-url}, que a su vez toma la variable de entorno
 * {@code MUNDO_CECI_URL} (por defecto {@code http://localhost:8083}).
 */
@Component
public class MundoCeciClient {

	private static final Logger log = LogManager.getLogger(MundoCeciClient.class);

	private static final ParameterizedTypeReference<Map<String, Object>> CUERPO_DE_ERROR =
			new ParameterizedTypeReference<>() {
			};

	private final WebClient webClient;
	private final Duration timeout;

	public MundoCeciClient(
			@Qualifier("mundoCeciWebClient") WebClient webClient,
			@Value("${mundo-ceci.timeout:5s}") Duration timeout) {
		this.webClient = webClient;
		this.timeout = timeout;
	}

	/**
	 * Crea la entidad en el mundo de Ceci con {@code POST /entidades}.
	 *
	 * @throws ReglaDelJuegoException si ese mundo la rechaza (HTTP 400): sus
	 *         reglas de validación dicen que no (nombre repetido, vida mayor
	 *         a la máxima, creeper detonado con vida...)
	 * @throws ServicioExternoException si esa API no responde o falla
	 */
	public EntidadMundoCeci crearEntidad(EntidadMundoCeci entidad) {
		log.debug("Enviando entidad al mundo de Ceci: {}", entidad);
		try {
			EntidadMundoCeci creada = webClient.post()
					.uri("/entidades")
					.bodyValue(entidad)
					.retrieve()
					.onStatus(estado -> estado.value() == HttpStatus.BAD_REQUEST.value(),
							respuesta -> respuesta.bodyToMono(CUERPO_DE_ERROR)
									.defaultIfEmpty(Map.of())
									.doOnNext(cuerpo -> log.debug("El mundo de Ceci rechazó la entidad (400): {}", cuerpo))
									.flatMap(cuerpo -> Mono.error(new ReglaDelJuegoException(
											"rechazada_por_mundo_ceci",
											"El mundo de Ceci rechazó la entidad: "
											+ cuerpo.getOrDefault("mensaje", "sin detalle")))))
					.bodyToMono(EntidadMundoCeci.class)
					.block(timeout);

			if (creada == null) {
				throw new ServicioExternoException("El mundo de Ceci respondió sin la entidad creada.", null);
			}
			log.debug("El mundo de Ceci aceptó la entidad: {}", creada);
			return creada;
		} catch (WebClientException excepcion) {
			log.warn("Falló la llamada al mundo de Ceci: {}", excepcion.getMessage());
			throw new ServicioExternoException(
					"No se pudo consultar el mundo de Ceci: " + excepcion.getMessage(), excepcion);
		} catch (IllegalStateException excepcion) {
			// block(timeout) lanza IllegalStateException si se vence el tiempo.
			log.warn("El mundo de Ceci no respondió en {}", timeout);
			throw new ServicioExternoException(
					"El mundo de Ceci no respondió en " + timeout.toSeconds() + " s.", excepcion);
		}
	}
}
