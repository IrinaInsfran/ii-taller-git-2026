package py.edu.uc.lp3.rest.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.dto.ObjetoExterno;
import py.edu.uc.lp3.service.ObjetoExternoService;

/**
 * Expone en nuestra API los objetos de https://api.restful-api.dev/objects,
 * consultados con WebClient.
 * <ul>
 *   <li>{@code GET /api/externo/objetos} → todos;</li>
 *   <li>{@code GET /api/externo/objetos?id=3&id=5} → solo esos;</li>
 *   <li>{@code GET /api/externo/objetos/7} → uno (404 si no existe).</li>
 * </ul>
 */
@RestController
@Tag(name = "Objetos externos", description = "Consulta con WebClient de la API pública https://api.restful-api.dev/objects")
public class ObjetoExternoController {

	private final ObjetoExternoService objetoExternoService;

	public ObjetoExternoController(ObjetoExternoService objetoExternoService) {
		this.objetoExternoService = objetoExternoService;
	}

	@GetMapping(ApiPaths.OBJETOS_EXTERNOS)
	@Operation(summary = "Listar objetos externos",
			description = "Sin `id` trae todos; con `id` repetido (`?id=3&id=5`) trae solo esos.")
	@ApiResponse(responseCode = "200", description = "Lista de objetos")
	@ApiResponse(responseCode = "502", description = "restful-api.dev falló o no respondió a tiempo",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public List<ObjetoExterno> listar(@Parameter(description = "Ids a buscar (opcional, se puede repetir)",
	                                             example = "3")
	                                  @RequestParam(name = "id", required = false) List<String> ids) {
		return objetoExternoService.listar(ids);
	}

	@GetMapping(ApiPaths.OBJETO_EXTERNO)
	@Operation(summary = "Obtener un objeto externo por id")
	@ApiResponse(responseCode = "200", description = "El objeto")
	@ApiResponse(responseCode = "404", description = "restful-api.dev no tiene un objeto con ese id",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "502", description = "restful-api.dev falló o no respondió a tiempo",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ObjetoExterno buscar(@Parameter(description = "Id del objeto en restful-api.dev", example = "7")
	                            @PathVariable String id) {
		return objetoExternoService.buscarPorId(id);
	}
}
