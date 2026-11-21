package py.edu.uc.lp3.service;

import java.util.List;
import java.util.Map;

/**
 * Servicio que expone la descripción del comportamiento de las entidades
 * del juego.
 */
public interface ComportamientoService {

	/**
	 * El comportamiento de cada entidad guardada.
	 *
	 * @return el id, el tipo y el comportamiento de cada entidad
	 */
	List<Map<String, String>> obtenerComportamientos();

}
