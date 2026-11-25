package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.dto.FlechazoRequest;
import py.edu.uc.lp3.dto.FlechazoResponse;
import py.edu.uc.lp3.service.FlechazoService;

/**
 * Un esqueleto guardado le dispara a otra entidad guardada. Si la flecha
 * llega lo decide la API Minecraft remota; el daño lo aplica este proyecto.
 */
@RestController
@Tag(name = "Flechazos", description = "Esqueleto propio + regla de alcance de la API Minecraft remota (/esqueleto/disparar)")
public class FlechazoController {

	private final FlechazoService flechazoService;

	public FlechazoController(FlechazoService flechazoService) {
		this.flechazoService = flechazoService;
	}

	@PostMapping(ApiPaths.FLECHAZOS)
	@Operation(summary = "Un esqueleto le dispara a una entidad",
			description = "Calcula la distancia entre los dos, le pregunta a la API remota si la flecha llega "
					+ "(rango del esqueleto remoto) y, si llega, le resta al objetivo el `danoAtaque` del esqueleto. "
					+ "Si la flecha no llega responde 200 con `impacto: false` y no hay daño.")
	@ApiResponse(responseCode = "200", description = "Resultado del disparo (con o sin impacto)")
	@ApiResponse(responseCode = "400", description = "Faltan ids o el esqueleto se apunta a sí mismo",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "No existe el esqueleto o el objetivo",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "El esqueleto o el objetivo ya están muertos",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "422", description = "El tirador no es un ESQUELETO",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "502", description = "La API Minecraft remota falló o no respondió",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public FlechazoResponse disparar(@RequestBody FlechazoRequest pedido) {
		return flechazoService.disparar(
				pedido == null ? null : pedido.esqueletoId(),
				pedido == null ? null : pedido.objetivoId());
	}
}
