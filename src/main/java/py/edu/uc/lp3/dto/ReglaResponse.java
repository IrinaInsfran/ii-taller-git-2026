package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta cuando una regla acepta la jugada. Si la rechaza, la API
 * responde con un {@link ErrorResponse} y el código HTTP de la regla.
 */
@Schema(description = "La regla acepta la jugada")
public record ReglaResponse(
		@Schema(description = "Siempre true: un rechazo viaja como error", example = "true")
		boolean aceptada,
		@Schema(description = "Regla evaluada", example = "mecha-corta")
		String regla,
		@Schema(description = "Posición en la lista «objetivos» del jugador al que apunta el creeper", example = "0")
		int objetivo,
		@Schema(description = "Distancia en bloques entre el creeper y ese jugador", example = "2.0")
		double distancia,
		@Schema(description = "Texto para una persona",
				example = "El creeper enciende la mecha: el jugador de objetivos[0] está a 2.0 bloques.")
		String mensaje) {
}
