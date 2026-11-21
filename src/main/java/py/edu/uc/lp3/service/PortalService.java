package py.edu.uc.lp3.service;

import py.edu.uc.lp3.dto.PortalResponse;

/**
 * Manda una entidad de este mundo al mundo Minecraft de Ceci.
 */
public interface PortalService {

	/**
	 * @param conservar si es true la entidad se copia; si es false, viaja
	 *                  (se borra de este mundo cuando el otro la acepta)
	 */
	PortalResponse cruzar(Long entidadId, boolean conservar);
}
