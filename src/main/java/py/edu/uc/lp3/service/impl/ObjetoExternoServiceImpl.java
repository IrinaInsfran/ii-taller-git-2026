package py.edu.uc.lp3.service.impl;

import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import py.edu.uc.lp3.dto.ObjetoExterno;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.ServicioExternoException;
import py.edu.uc.lp3.service.ObjetoExternoService;
import reactor.core.publisher.Mono;

/**
 * Consume la API externa con {@link WebClient}.
 * <p>
 * WebClient es reactivo (devuelve {@link Mono}), pero esta app es Spring MVC
 * y sus controllers devuelven objetos comunes, así que cada consulta termina
 * en {@code block(timeout)}: espera la respuesta, con un límite para no
 * colgar el hilo si la API externa no contesta.
 */
@Service
public class ObjetoExternoServiceImpl implements ObjetoExternoService {

	private static final Logger log = LogManager.getLogger(ObjetoExternoServiceImpl.class);

	private static final String OBJETOS = "/objects";

	private static final ParameterizedTypeReference<List<ObjetoExterno>> LISTA_DE_OBJETOS =
			new ParameterizedTypeReference<>() {
			};

	private final WebClient webClient;
	private final Duration timeout;

	public ObjetoExternoServiceImpl(
			@Qualifier("restfulApiWebClient") WebClient webClient,
			@Value("${restful-api.timeout:5s}") Duration timeout) {
		this.webClient = webClient;
		this.timeout = timeout;
	}

	@Override
	public List<ObjetoExterno> listar(List<String> ids) {
		log.debug("Listando objetos de restful-api.dev, ids={}", ids == null || ids.isEmpty() ? "todos" : ids);
		Mono<List<ObjetoExterno>> respuesta = webClient.get()
				// GET /objects  o  GET /objects?id=3&id=5
				.uri(uri -> {
					uri.path(OBJETOS);
					// Con una lista vacía queryParam dejaría "?id" suelto: solo se agrega si hay ids.
					if (ids != null && !ids.isEmpty()) {
						uri.queryParam("id", ids);
					}
					return uri.build();
				})
				.retrieve()
				.bodyToMono(LISTA_DE_OBJETOS);

		List<ObjetoExterno> objetos = esperar(respuesta);
		log.debug("restful-api.dev devolvió {} objetos", objetos == null ? 0 : objetos.size());
		return objetos == null ? List.of() : objetos;
	}

	@Override
	public ObjetoExterno buscarPorId(String id) {
		log.debug("Buscando en restful-api.dev el objeto id={}", id);
		Mono<ObjetoExterno> respuesta = webClient.get()
				.uri(OBJETOS + "/{id}", id)
				.retrieve()
				// El 404 de la API externa es "no existe": lo traducimos a nuestro 404.
				.onStatus(estado -> estado.value() == HttpStatus.NOT_FOUND.value(),
						r -> {
							log.debug("restful-api.dev respondió 404 para id={}", id);
							return Mono.error(new EntidadNoEncontradaException(
										"objeto_externo_no_encontrado",
										"No existe un objeto con id " + id + " en restful-api.dev."));
						})
				.bodyToMono(ObjetoExterno.class);

		ObjetoExterno objeto = esperar(respuesta);
		log.debug("restful-api.dev devolvió: {}", objeto);
		return objeto;
	}

	private <T> T esperar(Mono<T> respuesta) {
		try {
			return respuesta.block(timeout);
		} catch (WebClientException excepcion) {
			log.warn("Falló la llamada a restful-api.dev: {}", excepcion.getMessage());
			// Errores HTTP (4xx/5xx no traducidos) o de red (DNS, conexión rechazada).
			throw new ServicioExternoException(
					"No se pudo consultar restful-api.dev: " + excepcion.getMessage(), excepcion);
		} catch (IllegalStateException excepcion) {
			// block(timeout) lanza IllegalStateException si se vence el tiempo.
			log.warn("restful-api.dev no respondió en {}", timeout);
			throw new ServicioExternoException(
					"restful-api.dev no respondió en " + timeout.toSeconds() + " s.", excepcion);
		}
	}
}
