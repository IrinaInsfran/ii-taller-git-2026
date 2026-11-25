package py.edu.uc.lp3.service;

import java.util.List;

import py.edu.uc.lp3.dto.CompaneraResponse;
import py.edu.uc.lp3.dto.JugadaRequest;
import py.edu.uc.lp3.dto.JugadaResponse;

/**
 * Escenario de POO-09: un creeper guardado explota solo si A2 (Cecilia) y
 * A3 (Sol) aceptan la jugada con sus reglas. Si alguna la rechaza o no
 * responde, H2 queda igual.
 */
public interface JugadaService {

	/**
	 * Consulta a A2 y a A3 y, si las dos aceptan, aplica mis reglas y guarda
	 * la explosión, el daño y la jugada.
	 *
	 * @throws py.edu.uc.lp3.exceptions.DatoInvalidoException          faltan datos (400)
	 * @throws py.edu.uc.lp3.exceptions.EntidadNoEncontradaException   no existe el creeper (404)
	 * @throws py.edu.uc.lp3.exceptions.JugadaRechazadaException       A2 o A3 rechazó (409)
	 * @throws py.edu.uc.lp3.exceptions.CompaneraNoDisponibleException A2 o A3 no respondió (502)
	 * @throws py.edu.uc.lp3.exceptions.ReglaDelJuegoException         mi regla rechazó (422 o 409)
	 */
	JugadaResponse registrarExplosion(JugadaRequest pedido);

	List<JugadaResponse> listar();

	JugadaResponse obtener(Long id);

	/** A quién consulta el escenario y en qué URL. */
	List<CompaneraResponse> companeras();
}
