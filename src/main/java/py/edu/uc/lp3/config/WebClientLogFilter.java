package py.edu.uc.lp3.config;

import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

/**
 * Filtro de WebClient que deja en el log cada pedido HTTP a otro servicio:
 * método, URL, código de respuesta y cuánto tardó. Se agrega a todos los
 * WebClient de {@link WebClientConfig}, así ninguna llamada externa queda
 * sin registrar.
 * <p>
 * Los pasos normales van en DEBUG; los errores de red y los pedidos
 * cancelados por timeout, en WARN.
 */
public final class WebClientLogFilter {

	private static final Logger log = LogManager.getLogger(WebClientLogFilter.class);

	private WebClientLogFilter() {
	}

	/**
	 * @param servicio nombre corto del servicio, para distinguirlo en el log
	 *                 (por ejemplo "mundo-ceci")
	 */
	public static ExchangeFilterFunction para(String servicio) {
		return (pedido, siguiente) -> {
			long inicio = System.nanoTime();
			log.debug("[{}] --> {} {}", servicio, pedido.method(), pedido.url());

			return siguiente.exchange(pedido)
					.doOnNext(respuesta -> log.debug("[{}] <-- {} {} {} ({} ms)",
							servicio, respuesta.statusCode().value(), pedido.method(), pedido.url(),
							milisDesde(inicio)))
					.doOnError(error -> log.warn("[{}] <-- ERROR {} {} ({} ms): {}",
							servicio, pedido.method(), pedido.url(), milisDesde(inicio), error.toString()))
					.doOnCancel(() -> log.warn("[{}] <-- CANCELADO {} {} ({} ms): se venció el tiempo de espera",
							servicio, pedido.method(), pedido.url(), milisDesde(inicio)));
		};
	}

	private static long milisDesde(long inicioNanos) {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - inicioNanos);
	}
}
