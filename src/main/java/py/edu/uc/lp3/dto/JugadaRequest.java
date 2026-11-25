package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Pedido del escenario: qué creeper guardado intenta explotar y cuánto tiempo pasó. */
@Schema(description = "Un creeper guardado en H2 intenta explotar")
public record JugadaRequest(
		@Schema(description = "Id del creeper en H2", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
		Long creeperId,
		@Schema(description = "Tiempo desde que se encendió la mecha; Sol rechaza si es menor que el "
				+ "tiempoExplosion del creeper (30 por defecto)", example = "30",
				requiredMode = Schema.RequiredMode.REQUIRED)
		Integer tiempoTranscurrido) {
}
