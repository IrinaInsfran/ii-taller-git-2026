package py.edu.uc.lp3.rest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.constants.ApiPaths;
import py.edu.uc.lp3.service.ComportamientoService;

@RestController
@RequestMapping(ApiPaths.COMPORTAMIENTOS)
public class ComportamientoController {

	private final ComportamientoService comportamientoService;

	public ComportamientoController(ComportamientoService comportamientoService) {
		this.comportamientoService = comportamientoService;
	}

	@GetMapping
	public List<Map<String, String>> comportamientos() {
		return comportamientoService.obtenerComportamientos();
	}

}