package py.edu.uc.lp3.rest.controller;

import py.edu.uc.lp3.constants.ApiPaths;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.dto.EntidadResponse;
import py.edu.uc.lp3.dto.ErrorResponse;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.service.EntidadService;

/**
 * CRUD REST de entidades. Solo traduce HTTP a llamadas al servicio y
 * entidades a {@link EntidadResponse}; las reglas están en el servicio.
 */
@RestController
@RequestMapping(ApiPaths.ENTIDADES)
@Tag(name = "Entidades", description = "CRUD de las entidades del mundo (jugadores, mobs, aldeanos, animales) guardadas en H2")
public class EntidadController {

	private final EntidadService entidadService;

	public EntidadController(EntidadService entidadService) {
		this.entidadService = entidadService;
	}

	@PostMapping
	@Operation(summary = "Crear una entidad",
			description = "El campo `tipo` decide qué subclase se crea y qué otros campos son obligatorios.")
	@ApiResponse(responseCode = "201", description = "Creada; el header Location apunta a la nueva entidad")
	@ApiResponse(responseCode = "400", description = "Datos inválidos o JSON mal formado",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<EntidadResponse> crear(@RequestBody EntidadRequest datos) {
		Entidad creada = entidadService.crear(datos);
		URI ubicacion = URI.create(ApiPaths.ENTIDADES + "/" + creada.getId());
		return ResponseEntity.created(ubicacion).body(EntidadResponse.desde(creada));
	}

	@GetMapping
	@Operation(summary = "Listar todas las entidades")
	@ApiResponse(responseCode = "200", description = "Lista (puede estar vacía)")
	public List<EntidadResponse> listar() {
		return entidadService.obtenerTodas().stream()
				.map(EntidadResponse::desde)
				.toList();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Obtener una entidad por id")
	@ApiResponse(responseCode = "200", description = "La entidad")
	@ApiResponse(responseCode = "404", description = "No existe una entidad con ese id",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public EntidadResponse obtener(@Parameter(description = "Id de la entidad", example = "1")
	                               @PathVariable Long id) {
		return EntidadResponse.desde(entidadService.obtenerPorId(id));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Reemplazar los datos de una entidad")
	@ApiResponse(responseCode = "200", description = "La entidad actualizada")
	@ApiResponse(responseCode = "400", description = "Datos inválidos",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	@ApiResponse(responseCode = "404", description = "No existe una entidad con ese id",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public EntidadResponse actualizar(@Parameter(description = "Id de la entidad", example = "1")
	                                  @PathVariable Long id,
	                                  @RequestBody EntidadRequest datos) {
		return EntidadResponse.desde(entidadService.actualizar(id, datos));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Eliminar una entidad")
	@ApiResponse(responseCode = "204", description = "Eliminada")
	@ApiResponse(responseCode = "404", description = "No existe una entidad con ese id",
			content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
	public ResponseEntity<Void> eliminar(@Parameter(description = "Id de la entidad", example = "1")
	                                     @PathVariable Long id) {
		entidadService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
