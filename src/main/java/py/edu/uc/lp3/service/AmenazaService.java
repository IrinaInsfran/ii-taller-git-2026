package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Material;
import py.edu.uc.lp3.dto.AmenazasResponse;
import py.edu.uc.lp3.dto.TipoArma;

/**
 * Consulta de solo lectura sobre el protocolo Amenaza.
 */
public interface AmenazaService {

	/**
	 * Los mobs hostiles vivos del mundo y, si se pide, un arma, evaluados
	 * contra la entidad {@code objetivoId}.
	 */
	AmenazasResponse evaluar(Long objetivoId, TipoArma arma, Material material);
}
