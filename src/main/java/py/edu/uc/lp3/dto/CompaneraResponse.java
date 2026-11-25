package py.edu.uc.lp3.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** A quién consulta el escenario y en qué URL. */
@Schema(description = "Una compañera del grupo y la URL de su regla")
public record CompaneraResponse(
		@Schema(example = "A2") String rol,
		@Schema(example = "Cecilia") String companera,
		@Schema(example = "http://100.122.114.103:8083/explosiones/validar") String url) {
}
