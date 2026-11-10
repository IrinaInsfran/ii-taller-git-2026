package py.edu.uc.lp3.rest.controller;

import py.edu.uc.lp3.constants.ApiPaths;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.INDEX)
public class IndexController {

	@GetMapping
	public Map<String, String> index() {
		return Map.of(
				"autora", "Irina",
				"dominio", "Minecraft",
				"estado", "API funcionando"
		);
	}

}