package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** Lo que contestó una compañera cuando su regla aceptó la jugada. */
@Schema(description = "Respuesta de una compañera que aceptó la jugada")
public record RespuestaCompanera(
		@Schema(description = "Rol en el grupo", example = "A2")
		String rol,
		@Schema(description = "Nombre de la compañera", example = "Cecilia")
		String companera,
		@Schema(description = "URL consultada", example = "http://100.122.114.103:8083/explosiones/validar")
		String url,
		@Schema(description = "Código HTTP que devolvió", example = "200")
		int estado,
		@Schema(description = "Cuerpo que devolvió, sin modificar")
		Object respuesta) {
}
