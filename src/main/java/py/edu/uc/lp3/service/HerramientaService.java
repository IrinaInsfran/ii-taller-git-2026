package py.edu.uc.lp3.service;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Hacha;
import py.edu.uc.lp3.domain.Mineral;
import py.edu.uc.lp3.domain.Pico;

/**
 * Reglas de uso de las herramientas: una herramienta rota no se usa y cada
 * uso gasta durabilidad.
 */
public interface HerramientaService {

	/**
	 * Golpea al objetivo con el arma: le hace su daño y gasta la durabilidad
	 * del golpe.
	 *
	 * @throws py.edu.uc.lp3.exceptions.HerramientaRotaException si el arma está rota
	 */
	void golpear(Arma arma, Entidad objetivo);

	/** Tala un árbol: gasta 1 de durabilidad. */
	void talar(Hacha hacha);

	/** Si el material del pico alcanza el nivel que pide el mineral. */
	boolean puedeObtener(Pico pico, Mineral mineral);

	/**
	 * Pica el mineral y gasta 1 de durabilidad.
	 *
	 * @return true si el mineral se obtuvo; false si el pico es de un
	 *         material insuficiente y el bloque no soltó nada
	 */
	boolean minar(Pico pico, Mineral mineral);
}
