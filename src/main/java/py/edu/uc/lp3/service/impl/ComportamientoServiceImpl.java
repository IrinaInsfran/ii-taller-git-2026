package py.edu.uc.lp3.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.minecraft.Aldeano;
import py.edu.uc.lp3.minecraft.Creeper;
import py.edu.uc.lp3.minecraft.Entidad;
import py.edu.uc.lp3.service.ComportamientoService;

@Service
public class ComportamientoServiceImpl implements ComportamientoService {

	@Override
	public List<Map<String, String>> obtenerComportamientos() {

		Entidad creeper = new Creeper(
				20,
				6,
				64,
				0,
				2,
				16,
				3,
				30
		);

		Entidad aldeano = new Aldeano(
				20,
				10,
				64,
				0,
				2,
				false,
				"Herrero"
		);

		return List.of(
				describir(creeper),
				describir(aldeano)
		);
	}

	private Map<String, String> describir(Entidad entidad) {
		return Map.of(
				"tipo", entidad.getClass().getSimpleName(),
				"comportamiento", entidad.describirComportamiento()
		);
	}

}