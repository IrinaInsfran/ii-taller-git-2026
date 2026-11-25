package py.edu.uc.lp3.service;

import py.edu.uc.lp3.dto.MechaCortaRequest;
import py.edu.uc.lp3.dto.ReglaResponse;

/**
 * Las reglas del juego que otros servicios consultan por HTTP (POO-09).
 * Arman entidades en memoria con los datos del pedido y delegan en
 * {@link ReglasCreeperService}; no leen ni guardan nada en H2.
 */
public interface ReglasPublicadasService {

	/**
	 * Regla «mecha corta»: el creeper vivo apunta al jugador vivo más cercano
	 * a {@value ReglasCreeperService#DISTANCIA_DE_DETONACION} bloques o menos.
	 *
	 * @throws py.edu.uc.lp3.exceptions.DatoInvalidoException   si faltan datos o no son números
	 * @throws py.edu.uc.lp3.exceptions.EntidadMuertaException  si el creeper está muerto
	 * @throws py.edu.uc.lp3.exceptions.FueraDeAlcanceException si no hay un jugador vivo tan cerca
	 */
	ReglaResponse mechaCorta(MechaCortaRequest pedido);
}
