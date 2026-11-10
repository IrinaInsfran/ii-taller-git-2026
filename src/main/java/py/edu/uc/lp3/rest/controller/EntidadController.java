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

import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.dto.EntidadResponse;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.service.EntidadService;

/**
 * CRUD REST de entidades. Solo traduce HTTP a llamadas al servicio y
 * entidades a {@link EntidadResponse}; las reglas estan en el servicio.
 */
@RestController
@RequestMapping(ApiPaths.ENTIDADES)
public class EntidadController {

	private final EntidadService entidadService;

	public EntidadController(EntidadService entidadService) {
		this.entidadService = entidadService;
	}

	@PostMapping
	public ResponseEntity<EntidadResponse> crear(@RequestBody EntidadRequest datos) {
		Entidad creada = entidadService.crear(datos);
		URI ubicacion = URI.create(ApiPaths.ENTIDADES + "/" + creada.getId());
		return ResponseEntity.created(ubicacion).body(EntidadResponse.desde(creada));
	}

	@GetMapping
	public List<EntidadResponse> listar() {
		return entidadService.obtenerTodas().stream()
				.map(EntidadResponse::desde)
				.toList();
	}

	@GetMapping("/{id}")
	public EntidadResponse obtener(@PathVariable Long id) {
		return EntidadResponse.desde(entidadService.obtenerPorId(id));
	}

	@PutMapping("/{id}")
	public EntidadResponse actualizar(@PathVariable Long id,
	                                  @RequestBody EntidadRequest datos) {
		return EntidadResponse.desde(entidadService.actualizar(id, datos));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable Long id) {
		entidadService.eliminar(id);
		return ResponseEntity.noContent().build();
	}
}
