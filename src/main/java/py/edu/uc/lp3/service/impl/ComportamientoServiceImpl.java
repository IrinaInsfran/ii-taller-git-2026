package py.edu.uc.lp3.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.service.ComportamientoService;

@Service
@Transactional(readOnly = true)
public class ComportamientoServiceImpl implements ComportamientoService {

	private final EntidadRepository entidadRepository;

	public ComportamientoServiceImpl(EntidadRepository entidadRepository) {
		this.entidadRepository = entidadRepository;
	}

	/**
	 * Recorre las entidades guardadas como {@code List<Entidad>}: el servicio
	 * no necesita saber el tipo concreto de cada una, solo invocar el
	 * comportamiento polimorfico de Entidad.
	 */
	@Override
	public List<Map<String, String>> obtenerComportamientos() {
		return entidadRepository.findAll().stream()
				.map(this::describir)
				.toList();
	}

	private Map<String, String> describir(Entidad entidad) {
		Map<String, String> item = new LinkedHashMap<>();
		item.put("id", String.valueOf(entidad.getId()));
		item.put("tipo", entidad.getClass().getSimpleName());
		item.put("comportamiento", entidad.describirComportamiento());
		return item;
	}
}
