package py.edu.uc.lp3.rest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.dto.AmenazasResponse;
import py.edu.uc.lp3.dto.TipoArma;
import py.edu.uc.lp3.service.AmenazaService;

/**
 * Endpoint de lectura del protocolo {@code Amenaza}: ¿quien le puede hacer
 * daño a la entidad {id} y cuanto? Opcionalmente suma un arma
 * ({@code ?arma=ESPADA&material=DIAMANTE}) para mezclar en la misma lista
 * mobs y herramientas.
 */
@RestController
public class AmenazaController {

	private final AmenazaService amenazaService;

	public AmenazaController(AmenazaService amenazaService) {
		this.amenazaService = amenazaService;
	}

	@GetMapping(ApiPaths.AMENAZAS)
	public AmenazasResponse amenazas(@PathVariable Long id,
	                                 @RequestParam(required = false) TipoArma arma,
	                                 @RequestParam(required = false) Material material) {
		return amenazaService.evaluar(id, arma, material);
	}
}
