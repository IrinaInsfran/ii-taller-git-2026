package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Error del escenario atribuido a una compañera: dice quién rechazó (o no
 * respondió), con qué código y qué contestó, sin modificar su cuerpo.
 */
@Schema(description = "Error atribuido a A2 o A3; la jugada no se guardó")
public record ErrorCompaneraResponse(
		@Schema(description = "Tipo de error", example = "jugada_rechazada")
		String error,
		@Schema(description = "Caso concreto", example = "rechazada_por_a2")
		String codigo,
		@Schema(description = "Rol de la compañera que lo originó", example = "A2")
		String origen,
		@Schema(description = "Nombre de la compañera", example = "Cecilia")
		String companera,
		@Schema(description = "Código HTTP que devolvió; null si no respondió", example = "422", nullable = true)
		Integer estadoCompanera,
		@Schema(description = "Cuerpo que devolvió, sin modificar; null si no respondió", nullable = true)
		Object respuestaCompanera,
		@Schema(description = "Texto para una persona",
				example = "Cecilia (A2) rechazó la jugada: El Creeper está muerto. La jugada no se guardó.")
		String mensaje) {
}
