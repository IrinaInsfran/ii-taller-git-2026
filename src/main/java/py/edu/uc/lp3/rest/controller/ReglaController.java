package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.dto.MechaCortaRequest;
import py.edu.uc.lp3.dto.ReglaResponse;
import py.edu.uc.lp3.service.ReglasPublicadasService;

/**
 * Reglas del juego publicadas para que otros servicios del taller las
 * consulten (POO-09). Responden si la jugada vale sin leer ni guardar nada:
 * el que pregunta decide si guarda su jugada.
 */
@RestController
@Tag(name = "Reglas", description = "Reglas que otros servicios consultan por HTTP; no leen ni modifican H2")
public class ReglaController {

	private static final String EJEMPLO = """
			{
			  "saludCreeper": 20,
			  "creeper": {"x": 0, "y": 64, "z": 0},
			  "objetivos": [
			    {"tipo": "jugador", "salud": 20, "posicion": {"x": 2, "y": 64, "z": 0}},
			    {"tipo": "aldeano", "salud": 20, "posicion": {"x": 1, "y": 64, "z": 0}}
			  ]
			}
			""";

	private final ReglasPublicadasService reglas;

	public ReglaController(ReglasPublicadasService reglas) {
		this.reglas = reglas;
	}

	@PostMapping(ApiPaths.REGLA_MECHA_CORTA)
	@Operation(summary = "Regla «mecha corta»",
			description = "Un creeper vivo solo detona si hay un jugador vivo a 3 bloques o menos, y apunta al "
					+ "más cercano. Solo cuentan los objetivos de tipo «jugador». Acepta el mismo cuerpo que "
					+ "`/explosiones/validar`: los campos de más (tiempoExplosion, radioExplosion...) se ignoran.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true,
					content = @Content(schema = @Schema(implementation = MechaCortaRequest.class),
							examples = @ExampleObject(name = "jugador a 2 bloques", value = EJEMPLO))))
	@ApiResponse(responseCode = "200", description = "La regla acepta: el creeper enciende la mecha")
	@ApiResponse(responseCode = "400", description = "Falta la posición del creeper, la lista de objetivos o alguna coordenada",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "Rechazo: el creeper está muerto (`entidad_muerta`)",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "422", description = "Rechazo: ningún jugador vivo a 3 bloques o menos (`fuera_de_alcance`)",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ReglaResponse mechaCorta(@RequestBody MechaCortaRequest pedido) {
		return reglas.mechaCorta(pedido);
	}
}
