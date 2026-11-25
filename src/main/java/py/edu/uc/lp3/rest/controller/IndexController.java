package py.edu.uc.lp3.rest.controller;

import py.edu.uc.lp3.constants.ApiPaths;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(ApiPaths.INDEX)
@Tag(name = "Estado", description = "Comprobar que la API está levantada")
public class IndexController {

	@GetMapping
	@Operation(summary = "Estado de la API")
	@ApiResponse(responseCode = "200", description = "La API está funcionando")
	public Map<String, String> index() {
		return Map.of(
				"autora", "Irina",
				"dominio", "Minecraft",
				"estado", "API funcionando"
		);
	}

}
