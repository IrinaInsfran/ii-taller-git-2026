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

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.dto.ExplosionRequest;
import py.edu.uc.lp3.dto.ExplosionResponse;
import py.edu.uc.lp3.service.ExplosionService;

/**
 * Simulacion de explosiones. El recurso es la explosion: un POST crea una
 * (y con eso el creeper muere y las entidades cercanas pierden salud), y los
 * GET leen el historial guardado en H2.
 */
@RestController
@RequestMapping(ApiPaths.EXPLOSIONES)
public class ExplosionController {

	private final ExplosionService explosionService;

	public ExplosionController(ExplosionService explosionService) {
		this.explosionService = explosionService;
	}

	@PostMapping
	public ResponseEntity<ExplosionResponse> detonar(@RequestBody ExplosionRequest pedido) {
		Explosion explosion = explosionService.detonarCreeper(
				pedido == null ? null : pedido.creeperId());
		URI ubicacion = URI.create(ApiPaths.EXPLOSIONES + "/" + explosion.getId());
		return ResponseEntity.created(ubicacion).body(ExplosionResponse.conDetalle(explosion));
	}

	@GetMapping
	public List<ExplosionResponse> historial() {
		return explosionService.historial().stream()
				.map(ExplosionResponse::resumen)
				.toList();
	}

	@GetMapping("/{id}")
	public ExplosionResponse obtener(@PathVariable Long id) {
		return ExplosionResponse.resumen(explosionService.obtenerPorId(id));
	}
}
