package py.edu.uc.lp3.client;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * A2 del grupo: Cecilia. Su {@code POST /explosiones/validar} aplica dos
 * reglas sin tocar su base: el creeper solo detona si está vivo (si no,
 * 422) y solo detona una vez (si no, 409). Lee {@code saludCreeper} y
 * {@code yaDetonado}. URL en {@code companeras.a2.*} (variables
 * {@code CECI_URL} y {@code CECI_RUTA}).
 */
@Component
public class CeciReglasClient extends ReglaCompaneraClient {

	public CeciReglasClient(WebClient.Builder builder,
	                        @Value("${companeras.a2.nombre}") String nombre,
	                        @Value("${companeras.a2.base-url}") String baseUrl,
	                        @Value("${companeras.a2.ruta}") String ruta,
	                        @Value("${companeras.timeout:5s}") Duration timeout) {
		super("A2", nombre, baseUrl, ruta, builder, timeout);
	}
}
