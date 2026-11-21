package py.edu.uc.lp3.dto;

import java.util.Map;

/**
 * Un objeto tal como lo devuelve https://api.restful-api.dev/objects.
 * <p>
 * {@code data} es libre: cada objeto trae claves distintas (color, price,
 * capacity...) o directamente {@code null}, por eso se guarda como mapa.
 * El {@code id} es texto porque los objetos creados por usuarios tienen ids
 * como {@code "ff808181932badb6..."}.
 */
public record ObjetoExterno(
		String id,
		String name,
		Map<String, Object> data) {
}
