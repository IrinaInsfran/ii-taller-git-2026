package py.edu.uc.lp3.client;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import py.edu.uc.lp3.dto.MechaCortaRequest.Objetivo;
import py.edu.uc.lp3.dto.MechaCortaRequest.Posicion;
import py.edu.uc.lp3.dto.RespuestaCompanera;
import py.edu.uc.lp3.dto.ValidacionCompaneraRequest;
import py.edu.uc.lp3.exceptions.CompaneraNoDisponibleException;
import py.edu.uc.lp3.exceptions.JugadaRechazadaException;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba los clientes de A2 y A3 sin levantar sus APIs: las respuestas
 * imitan las de Ceci y Sol, con sus formatos de error reales.
 */
class ReglaCompaneraClientTest {

	private static final ValidacionCompaneraRequest PEDIDO = new ValidacionCompaneraRequest(
			20, false, 30, 30, 6,
			new Posicion(0.0, 64.0, 0.0),
			List.of(new Objetivo("jugador", 20, new Posicion(2.0, 64.0, 0.0))));

	private final AtomicReference<ClientRequest> pedido = new AtomicReference<>();

	private WebClient.Builder queResponde(HttpStatus estado, String json) {
		return WebClient.builder().exchangeFunction(p -> {
			pedido.set(p);
			return Mono.just(ClientResponse.create(estado)
					.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
					.body(json)
					.build());
		});
	}

	private CeciReglasClient ceci(HttpStatus estado, String json) {
		return new CeciReglasClient(queResponde(estado, json), "Cecilia",
				"http://ceci:8083", "/explosiones/validar", Duration.ofSeconds(2));
	}

	private SolReglasClient sol(HttpStatus estado, String json) {
		return new SolReglasClient(queResponde(estado, json), "Sol",
				"http://sol:8080", "/explosiones/validar", Duration.ofSeconds(2));
	}

	@Test
	void siLaReglaAceptaDevuelveSuRespuesta() {
		RespuestaCompanera respuesta = ceci(HttpStatus.OK, """
				{"aceptada":true,"mensaje":"El Creeper está vivo y todavía no detonó."}""")
				.validar(PEDIDO);

		assertThat(pedido.get().method()).isEqualTo(HttpMethod.POST);
		assertThat(pedido.get().url()).hasToString("http://ceci:8083/explosiones/validar");
		assertThat(respuesta.rol()).isEqualTo("A2");
		assertThat(respuesta.companera()).isEqualTo("Cecilia");
		assertThat(respuesta.url()).isEqualTo("http://ceci:8083/explosiones/validar");
		assertThat(respuesta.estado()).isEqualTo(200);
		assertThat(respuesta.respuesta()).isInstanceOf(Map.class);
		assertThat(((Map<?, ?>) respuesta.respuesta()).get("aceptada")).isEqualTo(true);
	}

	@Test
	void unRechazoDeCeciLlevaSuCodigoYSuMensaje() {
		var cliente = ceci(HttpStatus.UNPROCESSABLE_CONTENT, """
				{"timestamp":"2026-11-21T19:00:00","status":422,"error":"Unprocessable Entity",
				 "tipo":"CreeperMuertoException","mensaje":"El Creeper está muerto: no puede detonar."}""");

		assertThatThrownBy(() -> cliente.validar(PEDIDO))
				.isInstanceOfSatisfying(JugadaRechazadaException.class, e -> {
					assertThat(e.getCodigo()).isEqualTo("rechazada_por_a2");
					assertThat(e.getOrigen()).isEqualTo("A2");
					assertThat(e.getEstadoCompanera()).isEqualTo(422);
					assertThat(((Map<?, ?>) e.getRespuestaCompanera()).get("tipo")).isEqualTo("CreeperMuertoException");
					assertThat(e.getMessage()).isEqualTo("Cecilia (A2) rechazó la jugada: "
							+ "El Creeper está muerto: no puede detonar. La jugada no se guardó.");
				});
	}

	@Test
	void unRechazoDeSolSeAtribuyeAA3() {
		var cliente = sol(HttpStatus.CONFLICT, """
				{"error":"Explosión no preparada","mensaje":"El Creeper necesita 30 y pasaron 10","estado":409}""");

		assertThatThrownBy(() -> cliente.validar(PEDIDO))
				.isInstanceOfSatisfying(JugadaRechazadaException.class, e -> {
					assertThat(e.getCodigo()).isEqualTo("rechazada_por_a3");
					assertThat(e.getCompanera()).isEqualTo("Sol");
					assertThat(e.getEstadoCompanera()).isEqualTo(409);
					assertThat(e.getMessage()).contains("El Creeper necesita 30 y pasaron 10.");
				});
	}

	@Test
	void unErrorDelServidorEsCompaneraNoDisponible() {
		var cliente = sol(HttpStatus.INTERNAL_SERVER_ERROR, "falló");

		assertThatThrownBy(() -> cliente.validar(PEDIDO))
				.isInstanceOfSatisfying(CompaneraNoDisponibleException.class, e -> {
					assertThat(e.getOrigen()).isEqualTo("A3");
					assertThat(e.getEstadoCompanera()).isEqualTo(500);
					assertThat(e.getRespuestaCompanera()).isEqualTo("falló");
				});
	}

	@Test
	void sinConexionEsCompaneraNoDisponibleSinEstado() {
		var cliente = new CeciReglasClient(WebClient.builder(), "Cecilia",
				"http://localhost:1", "/explosiones/validar", Duration.ofSeconds(2));

		assertThatThrownBy(() -> cliente.validar(PEDIDO))
				.isInstanceOfSatisfying(CompaneraNoDisponibleException.class, e -> {
					assertThat(e.getCodigo()).isEqualTo("companera_no_disponible");
					assertThat(e.getEstadoCompanera()).isNull();
					assertThat(e.getMessage()).startsWith("No se pudo consultar a Cecilia (A2):");
				});
	}
}
