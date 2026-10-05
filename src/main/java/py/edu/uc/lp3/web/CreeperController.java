package py.edu.uc.lp3.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import py.edu.uc.lp3.minecraft.Creeper;
import py.edu.uc.lp3.service.CreeperService;

@RestController
@RequestMapping(ApiPaths.CREEPER)
public class CreeperController {

	private final CreeperService creeperService;

	public CreeperController(CreeperService creeperService) {
		this.creeperService = creeperService;
	}

	@GetMapping
	public Creeper crearCreeper(
			@RequestParam(name = "salud") int salud,
			@RequestParam(name = "x") double x,
			@RequestParam(name = "y") double y,
			@RequestParam(name = "z") double z,
			@RequestParam(name = "velocidad") int velocidad,
			@RequestParam(name = "rangoDeteccion") double rangoDeteccion,
			@RequestParam(name = "danoAtaque") int danoAtaque,
			@RequestParam(name = "tiempoExplosion") int tiempoExplosion) {

		return creeperService.crear(
				salud,
				x,
				y,
				z,
				velocidad,
				rangoDeteccion,
				danoAtaque,
				tiempoExplosion
		);
	}

	@GetMapping("/por-posicion")
	public Creeper crearCreeperPorPosicion(
			@RequestParam(name = "x") double x,
			@RequestParam(name = "y") double y,
			@RequestParam(name = "z") double z) {

		return creeperService.crearPorPosicion(x, y, z);
	}

}