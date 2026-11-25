package py.edu.uc.lp3.client;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.dto.DisparoRemoto;
import py.edu.uc.lp3.exceptions.ServicioExternoException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba el cliente sin la API remota: las respuestas son copias de lo que
 * devuelve {@code GET /esqueleto/disparar} de ese proyecto.
 */
class MinecraftRemotoClientTest {

	private final AtomicReference<URI> urlPedida = new AtomicReference<>();

	private MinecraftRemotoClient clienteQueResponde(HttpStatus estado, String json) {
		WebClient webClient = WebClient.builder()
				.baseUrl("http://10.40.142.132:8080")
				.exchangeFunction(pedido -> {
					urlPedida.set(pedido.url());
					return Mono.just(ClientResponse.create(estado)
							.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
							.body(json)
							.build());
				})
				.build();
		return new MinecraftRemotoClient(webClient, Duration.ofSeconds(2));
	}

	@Test
	void mandaLosDatosDelEsqueletoYDetectaQueLaFlechaLlego() {
		var cliente = clienteQueResponde(HttpStatus.OK, """
				{"nombre":"Esqueleto-3","flechasIniciales":16,"flechasRestantes":14,"rango":8.0,
				 "distanciaPedida":5.0,"disparoSinDistancia":"Esqueleto-3 dispara una flecha a 0.0. Le quedan 15.",
				 "disparoConDistancia":"Esqueleto-3 dispara una flecha a 5.0. Le quedan 14."}
				""");

		DisparoRemoto disparo = cliente.dispararEsqueleto("Esqueleto-3", 20, 1, 64, 2, 5.0);

		assertThat(urlPedida.get()).hasToString(
				"http://10.40.142.132:8080/esqueleto/disparar"
				+ "?nombre=Esqueleto-3&vida=20&x=1.0&y=64.0&z=2.0&distancia=5.0");
		assertThat(disparo.alcanzo()).isTrue();
		assertThat(disparo.rango()).isEqualTo(8.0);
	}

	@Test
	void fueraDeRangoNoGastaFlechaYNoAlcanza() {
		var cliente = clienteQueResponde(HttpStatus.OK, """
				{"nombre":"Bony","flechasIniciales":16,"flechasRestantes":15,"rango":8.0,
				 "distanciaPedida":15.0,"disparoSinDistancia":"Bony dispara una flecha a 0.0. Le quedan 15.",
				 "disparoConDistancia":"Bony no alcanza a disparar a 15.0 (rango 8.0) y no gasta flecha."}
				""");

		assertThat(cliente.dispararEsqueleto("Bony", 20, 0, 64, 0, 15.0).alcanzo()).isFalse();
	}

	@Test
	void unErrorDeLaApiRemotaEsServicioExterno() {
		var cliente = clienteQueResponde(HttpStatus.INTERNAL_SERVER_ERROR, "{}");

		assertThatThrownBy(() -> cliente.dispararEsqueleto("Bony", 20, 0, 64, 0, 5.0))
				.isInstanceOf(ServicioExternoException.class);
	}
}
