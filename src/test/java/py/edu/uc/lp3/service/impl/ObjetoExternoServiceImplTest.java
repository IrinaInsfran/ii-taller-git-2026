package py.edu.uc.lp3.service.impl;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.dto.ObjetoExterno;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.ServicioExternoException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba el servicio sin salir a internet: el WebClient recibe una
 * {@code ExchangeFunction} que responde lo que el test le indica y guarda
 * la URL pedida para poder revisarla.
 */
class ObjetoExternoServiceImplTest {

	private final AtomicReference<URI> urlPedida = new AtomicReference<>();

	private ObjetoExternoServiceImpl servicioQueResponde(HttpStatus estado, String json) {
		WebClient webClient = WebClient.builder()
				.baseUrl("https://api.restful-api.dev")
				.exchangeFunction(pedido -> {
					urlPedida.set(pedido.url());
					return Mono.just(ClientResponse.create(estado)
							.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.body(json)
							.build());
				})
				.build();
		return new ObjetoExternoServiceImpl(webClient, Duration.ofSeconds(2));
	}

	@Test
	void listarSinIdsPideTodosLosObjetos() {
		var servicio = servicioQueResponde(HttpStatus.OK, """
				[{"id":"1","name":"Google Pixel 6 Pro","data":{"color":"Cloudy White","capacity":"128 GB"}},
				 {"id":"2","name":"Apple iPhone 12 Mini, 256GB, Blue","data":null}]
				""");

		List<ObjetoExterno> objetos = servicio.listar(null);

		assertThat(urlPedida.get()).hasToString("https://api.restful-api.dev/objects");
		assertThat(objetos).extracting(ObjetoExterno::name)
				.containsExactly("Google Pixel 6 Pro", "Apple iPhone 12 Mini, 256GB, Blue");
		assertThat(objetos.get(0).data()).containsEntry("color", "Cloudy White");
		assertThat(objetos.get(1).data()).isNull();
	}

	@Test
	void listarConIdsLosMandaComoParametrosRepetidos() {
		var servicio = servicioQueResponde(HttpStatus.OK, "[]");

		servicio.listar(List.of("3", "5"));

		assertThat(urlPedida.get()).hasToString("https://api.restful-api.dev/objects?id=3&id=5");
	}

	@Test
	void buscarPorIdDevuelveElObjeto() {
		var servicio = servicioQueResponde(HttpStatus.OK, """
				{"id":"7","name":"Apple MacBook Pro 16","data":{"year":2019,"price":1849.99}}
				""");

		ObjetoExterno objeto = servicio.buscarPorId("7");

		assertThat(urlPedida.get()).hasToString("https://api.restful-api.dev/objects/7");
		assertThat(objeto.id()).isEqualTo("7");
		assertThat(objeto.data()).containsEntry("year", 2019);
	}

	@Test
	void buscarPorIdInexistenteLanzaNoEncontrada() {
		var servicio = servicioQueResponde(HttpStatus.NOT_FOUND,
				"{\"error\":\"Object with id=9999 was not found.\"}");

		assertThatThrownBy(() -> servicio.buscarPorId("9999"))
				.isInstanceOf(EntidadNoEncontradaException.class)
				.hasMessageContaining("9999");
	}

	@Test
	void errorDelServidorExternoLanzaServicioExterno() {
		var servicio = servicioQueResponde(HttpStatus.INTERNAL_SERVER_ERROR, "{}");

		assertThatThrownBy(() -> servicio.listar(null))
				.isInstanceOf(ServicioExternoException.class);
	}
}
