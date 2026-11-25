package py.edu.uc.lp3.rest.controller;

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
import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.dto.AmenazasResponse;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.dto.TipoArma;
import py.edu.uc.lp3.service.AmenazaService;

/**
 * Endpoint de lectura del protocolo {@code Amenaza}: ¿quién le puede hacer
 * daño a la entidad {id} y cuánto? Opcionalmente suma un arma
 * ({@code ?arma=ESPADA&material=DIAMANTE}) para mezclar en la misma lista
 * mobs y herramientas.
 */
@RestController
@Tag(name = "Amenazas", description = "Protocolo Amenaza: quién le puede hacer daño a una entidad y cuánto")
public class AmenazaController {

	private final AmenazaService amenazaService;

	public AmenazaController(AmenazaService amenazaService) {
		this.amenazaService = amenazaService;
	}

	@GetMapping(ApiPaths.AMENAZAS)
	@Operation(summary = "Evaluar las amenazas contra una entidad",
			description = "Junta los mobs hostiles vivos y, si se pide, un arma; calcula el daño de cada uno "
					+ "y si la entidad sobreviviría a todos juntos.")
	@ApiResponse(responseCode = "200", description = "Reporte de amenazas, de mayor a menor daño")
	@ApiResponse(responseCode = "400", description = "Se pidió `arma` sin `material`, o un valor inválido",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "No existe la entidad objetivo",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public AmenazasResponse amenazas(@Parameter(description = "Id de la entidad objetivo", example = "1")
	                                 @PathVariable Long id,
	                                 @Parameter(description = "Arma a sumar a la lista (opcional)")
	                                 @RequestParam(required = false) TipoArma arma,
	                                 @Parameter(description = "Material del arma; obligatorio si se manda `arma`")
	                                 @RequestParam(required = false) Material material) {
		return amenazaService.evaluar(id, arma, material);
	}
}
