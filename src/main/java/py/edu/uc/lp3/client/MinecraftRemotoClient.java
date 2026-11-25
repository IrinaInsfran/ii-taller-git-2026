package py.edu.uc.lp3.client;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;

import py.edu.uc.lp3.dto.DisparoRemoto;
import py.edu.uc.lp3.exceptions.ServicioExternoException;

/**
 * Habla con la API Minecraft de otro proyecto del taller (la URL está en
 * {@code minecraft-remoto.base-url}). Solo usa su regla de disparo del
 * esqueleto: {@code GET /esqueleto/disparar}, que no guarda nada en ese
 * servidor.
 */
@Component
public class MinecraftRemotoClient {

	private static final Logger log = LogManager.getLogger(MinecraftRemotoClient.class);

	private final WebClient webClient;
	private final Duration timeout;

	public MinecraftRemotoClient(
			@Qualifier("minecraftRemotoWebClient") WebClient webClient,
			@Value("${minecraft-remoto.timeout:5s}") Duration timeout) {
		this.webClient = webClient;
		this.timeout = timeout;
	}

	/**
	 * Le pide a la API remota que un esqueleto en (x, y, z) dispare a
	 * {@code distancia} bloques.
	 */
	public DisparoRemoto dispararEsqueleto(String nombre, int vida,
	                                       double x, double y, double z,
	                                       double distancia) {
		log.debug("Pidiendo disparo a la API remota: nombre={}, vida={}, posición=({}, {}, {}), distancia={}",
				nombre, vida, x, y, z, distancia);
		try {
			DisparoRemoto disparo = webClient.get()
					.uri(uri -> uri.path("/esqueleto/disparar")
							.queryParam("nombre", nombre)
							.queryParam("vida", vida)
							.queryParam("x", x)
							.queryParam("y", y)
							.queryParam("z", z)
							.queryParam("distancia", distancia)
							.build())
					.retrieve()
					.bodyToMono(DisparoRemoto.class)
					.block(timeout);

			if (disparo == null) {
				throw new ServicioExternoException(
						"La API Minecraft remota respondió sin datos del disparo.", null);
			}
			log.debug("Respuesta de la API remota: rango={}, flechas {} -> {}, alcanzo={}, relato=\"{}\"",
					disparo.rango(), disparo.flechasIniciales(), disparo.flechasRestantes(),
					disparo.alcanzo(), disparo.disparoConDistancia());
			return disparo;
		} catch (WebClientException excepcion) {
			log.warn("Falló la llamada a la API Minecraft remota: {}", excepcion.getMessage());
			throw new ServicioExternoException(
					"No se pudo consultar la API Minecraft remota: " + excepcion.getMessage(), excepcion);
		} catch (IllegalStateException excepcion) {
			// block(timeout) lanza IllegalStateException si se vence el tiempo.
			log.warn("La API Minecraft remota no respondió en {}", timeout);
			throw new ServicioExternoException(
					"La API Minecraft remota no respondió en " + timeout.toSeconds() + " s.", excepcion);
		}
	}
}
