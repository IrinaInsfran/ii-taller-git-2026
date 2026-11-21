package py.edu.uc.lp3.dto;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

/** Una jugada guardada: lo que contestó cada compañera y la explosión que resultó. */
@Schema(description = "Jugada aceptada por A2 y A3 y guardada en H2")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JugadaResponse(
		@Schema(example = "1")
		Long id,
		@Schema(example = "2")
		Long creeperId,
		@Schema(example = "30")
		int tiempoTranscurrido,
		Instant momento,
		@Schema(description = "Lo que contestó Cecilia (A2)")
		RespuestaCompanera a2,
		@Schema(description = "Lo que contestó Sol (A3)")
		RespuestaCompanera a3,
		@Schema(description = "La explosión guardada: con el detalle de cada entidad al crearla, resumida al leerla")
		ExplosionResponse explosion) {
}
