package py.edu.uc.lp3.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Amenaza;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.dto.AmenazasResponse;
import py.edu.uc.lp3.dto.TipoArma;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.service.AmenazaService;

/**
 * Junta en una sola {@code List<Amenaza>} a dos familias distintas: los mobs
 * hostiles guardados ({@code EntidadHostil}) y, opcionalmente, un arma
 * ({@code Espada} o {@code Hacha}, que son {@code Herramienta}).
 */
@Service
@Transactional(readOnly = true)
public class AmenazaServiceImpl implements AmenazaService {

	private final EntidadRepository entidadRepository;

	public AmenazaServiceImpl(EntidadRepository entidadRepository) {
		this.entidadRepository = entidadRepository;
	}

	@Override
	public AmenazasResponse evaluar(Long objetivoId, TipoArma arma, Material material) {
		Entidad objetivo = entidadRepository.findById(objetivoId)
				.orElseThrow(() -> new EntidadNoEncontradaException(objetivoId));

		List<Amenaza> amenazas = new ArrayList<>(entidadRepository.buscarHostilesVivos());
		amenazas.remove(objetivo);

		if (arma != null) {
			if (material == null) {
				throw new DatoInvalidoException(
						"campo_obligatorio",
						"Para sumar un arma hace falta también el parámetro 'material'.");
			}
			amenazas.add(arma.fabricar(material));
		}

		return AmenazasResponse.evaluar(objetivo, amenazas);
	}
}
