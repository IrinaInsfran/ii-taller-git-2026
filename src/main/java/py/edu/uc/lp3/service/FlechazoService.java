package py.edu.uc.lp3.service;

import py.edu.uc.lp3.dto.FlechazoResponse;

/**
 * Un esqueleto propio le dispara a una entidad propia, usando la regla de
 * alcance de la API Minecraft remota.
 */
public interface FlechazoService {

	FlechazoResponse disparar(Long esqueletoId, Long objetivoId);
}
