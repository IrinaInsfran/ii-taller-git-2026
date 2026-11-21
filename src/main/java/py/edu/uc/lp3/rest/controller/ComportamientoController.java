package py.edu.uc.lp3.rest.controller;

import py.edu.uc.lp3.constants.ApiPaths;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import py.edu.uc.lp3.service.ComportamientoService;

@RestController
@RequestMapping(ApiPaths.COMPORTAMIENTOS)
@Tag(name = "Comportamientos", description = "Polimorfismo: cada entidad guardada describe su propio comportamiento")
public class ComportamientoController {

	private final ComportamientoService comportamientoService;

	public ComportamientoController(ComportamientoService comportamientoService) {
		this.comportamientoService = comportamientoService;
	}

	@GetMapping
	@Operation(summary = "Comportamiento de cada entidad",
			description = "Llama a `describirComportamiento()` de cada entidad guardada; cada subclase responde a su manera.")
	@ApiResponse(responseCode = "200", description = "Una descripción por entidad")
	public List<Map<String, String>> comportamientos() {
		return comportamientoService.obtenerComportamientos();
	}

}
