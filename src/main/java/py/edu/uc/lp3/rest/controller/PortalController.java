package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import py.edu.uc.lp3.dto.PortalResponse;
import py.edu.uc.lp3.service.PortalService;

/**
 * Portal hacia el mundo Minecraft de Ceci (URL configurable en
 * {@code mundo-ceci.base-url} / variable {@code MUNDO_CECI_URL}).
 */
@RestController
@Tag(name = "Portal", description = "Una entidad viaja al mundo Minecraft de Ceci; sus reglas de validación deciden si entra")
public class PortalController {

	private final PortalService portalService;

	public PortalController(PortalService portalService) {
		this.portalService = portalService;
	}

	@PostMapping(ApiPaths.PORTAL)
	@Operation(summary = "La entidad cruza el portal al mundo de Ceci",
			description = "Solo cruzan entidades vivas de tipo JUGADOR, ZOMBIE o CREEPER. Si el mundo de Ceci la "
					+ "acepta, se crea allá y (salvo `conservar=true`) sale de este mundo. Si la rechaza, acá no cambia nada.")
	@ApiResponse(responseCode = "200", description = "Cruzó: trae el id que le dio el mundo de Ceci")
	@ApiResponse(responseCode = "404", description = "No existe la entidad",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "La entidad está muerta",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "422", description = "El tipo no existe en el mundo de Ceci, o ese mundo la rechazó "
			+ "(por ejemplo, nombre repetido)",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "502", description = "El mundo de Ceci no respondió o falló",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public PortalResponse cruzar(@Parameter(description = "Id de la entidad que viaja", example = "1")
	                             @PathVariable Long id,
	                             @Parameter(description = "true: se copia y también queda en este mundo")
	                             @RequestParam(defaultValue = "false") boolean conservar) {
		return portalService.cruzar(id, conservar);
	}
}
