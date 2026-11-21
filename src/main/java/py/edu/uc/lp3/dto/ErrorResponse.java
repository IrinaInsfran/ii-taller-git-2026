package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Cuerpo de todas las respuestas de error (lo arma {@code ManejadorErrores}).
 */
@Schema(description = "Respuesta de error común a toda la API")
public record ErrorResponse(
		@Schema(description = "Tipo de error", example = "no_encontrada")
		String error,
		@Schema(description = "Caso concreto, estable para que un programa lo reconozca",
				example = "entidad_no_encontrada")
		String codigo,
		@Schema(description = "Texto para una persona", example = "No existe una entidad con id 99.")
		String mensaje) {
}
