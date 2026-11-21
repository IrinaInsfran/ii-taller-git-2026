package py.edu.uc.lp3.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * A3 del grupo: Sol. Su {@code POST /explosiones/validar} aplica dos reglas
 * sin tocar su base: el creeper no detona antes de su tiempo de mecha
 * ({@code tiempoTranscurrido >= tiempoExplosion}, si no, 409) y cada
 * objetivo tiene que estar dentro del radio (si no, 422). URL en
 * {@code companeras.a3.*} (variables {@code SOL_URL} y {@code SOL_RUTA}).
 */
@Component
public class SolReglasClient extends ReglaCompaneraClient {

	public SolReglasClient(WebClient.Builder builder,
	                       @Value("${companeras.a3.nombre}") String nombre,
	                       @Value("${companeras.a3.base-url}") String baseUrl,
	                       @Value("${companeras.a3.ruta}") String ruta,
	                       @Value("${companeras.timeout:5s}") Duration timeout) {
		super("A3", nombre, baseUrl, ruta, builder, timeout);
	}
}
