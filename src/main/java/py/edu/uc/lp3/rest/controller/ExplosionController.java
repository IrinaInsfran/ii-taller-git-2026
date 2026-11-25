package py.edu.uc.lp3.rest.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.dto.ExplosionResponse;
import py.edu.uc.lp3.service.ExplosionService;

/**
 * Simulación de explosiones. El recurso es la explosión: un POST crea una
 * (y con eso el creeper muere y las entidades cercanas pierden salud), y los
 * GET leen el historial guardado en H2.
 */
@RestController
@RequestMapping(ApiPaths.EXPLOSIONES)
@Tag(name = "Explosiones", description = "Simulación: un creeper explota y daña a las entidades cercanas; el historial queda en H2")
public class ExplosionController {

	private final ExplosionService explosionService;

	public ExplosionController(ExplosionService explosionService) {
		this.explosionService = explosionService;
	}

	@PostMapping
	@Operation(summary = "Detonar un creeper",
			description = "El creeper muere y las entidades dentro del radio pierden salud según la distancia.")
	@ApiResponse(responseCode = "201", description = "Explosión registrada; el header Location apunta a ella")
	@ApiResponse(responseCode = "400", description = "Falta `creeperId` o el JSON es inválido",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "No existe el creeper",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "409", description = "El creeper ya está muerto",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "422", description = "La entidad no es un creeper o no hay nadie al alcance",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<ExplosionResponse> detonar(@RequestBody ExplosionRequest pedido) {
		Explosion explosion = explosionService.detonarCreeper(
				pedido == null ? null : pedido.creeperId());
		URI ubicacion = URI.create(ApiPaths.EXPLOSIONES + "/" + explosion.getId());
		return ResponseEntity.created(ubicacion).body(ExplosionResponse.conDetalle(explosion));
	}

	@GetMapping
	@Operation(summary = "Historial de explosiones")
	@ApiResponse(responseCode = "200", description = "Lista (puede estar vacía)")
	public List<ExplosionResponse> historial() {
		return explosionService.historial().stream()
				.map(ExplosionResponse::resumen)
				.toList();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener una explosión por id")
	@ApiResponse(responseCode = "200", description = "La explosión")
	@ApiResponse(responseCode = "404", description = "No existe una explosión con ese id",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ExplosionResponse obtener(@Parameter(description = "Id de la explosión", example = "1")
	                                 @PathVariable Long id) {
		return ExplosionResponse.resumen(explosionService.obtenerPorId(id));
	}
}
