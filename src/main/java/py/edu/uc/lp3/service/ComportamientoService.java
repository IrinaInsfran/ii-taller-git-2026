package py.edu.uc.lp3.service;

import java.util.List;
import java.util.Map;

/*
 * Servicio que expone la descripcion de comportamiento
 * de las entidades del juego
 */
public interface ComportamientoService {

	/*
	 * Funcion para obtener el comportamiento de un conjunto de entidades
	 * Parametros:
	 * 				ninguno
	 * Retorno:
	 * 				List<Map<String, String>> : tipo y comportamiento de cada entidad
	 * */
	List<Map<String, String>> obtenerComportamientos();

}