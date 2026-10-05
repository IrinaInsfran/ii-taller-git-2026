package py.edu.uc.lp3.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.minecraft.Aldeano;
import py.edu.uc.lp3.minecraft.Creeper;
import py.edu.uc.lp3.minecraft.Entidad;
import py.edu.uc.lp3.minecraft.Zombie;
import py.edu.uc.lp3.service.ComportamientoService;

@Service
public class ComportamientoServiceImpl implements ComportamientoService {

	/**
	 * Mundo de ejemplo. Todas las entidades se guardan como Entidad: el
	 * servicio no necesita saber el tipo concreto de cada una, solo
	 * invocar el comportamiento polimorfico de Entidad.
	 */
	private final List<Entidad> mundo = List.of(
			new Creeper(20, 6, 64, 0, 2, 16, 3, 30),
			new Aldeano(20, 10, 64, 0, 2, false, "Herrero"),
			new Zombie(20, 5, 64, 0, 3, 16, 3)
	);

	@Override
	public List<Map<String, String>> obtenerComportamientos() {
		return mundo.stream()
				.map(this::describir)
				.toList();
	}

	private Map<String, String> describir(Entidad entidad) {
		return Map.of(
				"tipo", entidad.getClass().getSimpleName(),
				"comportamiento", entidad.describirComportamiento()
		);
	}

}
