package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Pedido de flechazo: qué esqueleto (guardado en H2) le dispara a qué entidad.
 */
public record FlechazoRequest(
		@Schema(description = "Id de un ESQUELETO guardado", example = "3")
		Long esqueletoId,
		@Schema(description = "Id de la entidad a la que dispara", example = "1")
		Long objetivoId) {
}
