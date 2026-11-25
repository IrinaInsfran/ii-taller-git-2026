package py.edu.uc.lp3.client;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.dto.EntidadMundoCeci;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.exceptions.ServicioExternoException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba el cliente sin el mundo de Ceci: las respuestas imitan las de su
 * {@code POST /entidades} (incluido su JSON de error).
 */
class MundoCeciClientTest {

	private final AtomicReference<ClientRequest> pedido = new AtomicReference<>();

	private MundoCeciClient clienteQueResponde(HttpStatus estado, String json) {
		WebClient webClient = WebClient.builder()
				.baseUrl("http://localhost:8081")
				.exchangeFunction(p -> {
					pedido.set(p);
					return Mono.just(ClientResponse.create(estado)
							.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.body(json)
							.build());
				})
				.build();
		return new MundoCeciClient(webClient, Duration.ofSeconds(2));
	}

	private static EntidadMundoCeci steve() {
		return new EntidadMundoCeci(null, "Jugador", "Steve", 20, 20, 0, 64, 0, 3, 0, null);
	}

	@Test
	void creaLaEntidadConPostYDevuelveElIdDelOtroMundo() {
		var cliente = clienteQueResponde(HttpStatus.OK, """
				{"tipo":"Jugador","id":7,"nombre":"Steve","vida":20,"vidaMaxima":20,
				 "x":0.0,"y":64.0,"z":0.0,"nivel":3,"experiencia":0}""");

		EntidadMundoCeci creada = cliente.crearEntidad(steve());

		assertThat(pedido.get().method()).isEqualTo(HttpMethod.POST);
		assertThat(pedido.get().url()).hasToString("http://localhost:8081/entidades");
		assertThat(creada.id()).isEqualTo(7L);
		assertThat(creada.tipo()).isEqualTo("Jugador");
		assertThat(creada.nivel()).isEqualTo(3);
	}

	@Test
	void siElOtroMundoLaRechazaEsUnaReglaDelJuegoConSuMensaje() {
		var cliente = clienteQueResponde(HttpStatus.BAD_REQUEST, """
				{"timestamp":"2026-11-19T10:00:00","status":400,"error":"Bad Request",
				 "tipo":"DatosInvalidosException","mensaje":"Ya existe una entidad con ese nombre."}""");

		assertThatThrownBy(() -> cliente.crearEntidad(steve()))
				.isInstanceOf(ReglaDelJuegoException.class)
				.hasMessageContaining("Ya existe una entidad con ese nombre.");
	}

	@Test
	void unErrorDelServidorEsServicioExterno() {
		var cliente = clienteQueResponde(HttpStatus.INTERNAL_SERVER_ERROR, "{}");

		assertThatThrownBy(() -> cliente.crearEntidad(steve()))
				.isInstanceOf(ServicioExternoException.class);
	}
}
