package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Hacha;
import py.edu.uc.lp3.domain.Herramienta;
import py.edu.uc.lp3.domain.Mineral;
import py.edu.uc.lp3.domain.Pico;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.HerramientaRotaException;
import py.edu.uc.lp3.service.HerramientaService;

/**
 * Reglas de uso de las herramientas.
 * <ul>
 *   <li>Una herramienta rota (durabilidad 0) no se puede usar.</li>
 *   <li>Como en el juego, el uso que deja la durabilidad en 0 se completa y
 *       recién ahí se rompe.</li>
 *   <li>Un golpe gasta lo que dice el arma (espada 1, hacha 2); talar y
 *       picar gastan 1.</li>
 *   <li>Si el pico es de un material insuficiente, el bloque se rompe pero
 *       no suelta nada.</li>
 * </ul>
 */
@Service
public class HerramientaServiceImpl implements HerramientaService {

	@Override
	public void golpear(Arma arma, Entidad objetivo) {
		requerido(arma, "El arma no puede ser nula.");
		requerido(objetivo, "El objetivo no puede ser nulo.");
		// Las armas (espada, hacha) son sustantivos femeninos: "La espada está rota...".
		verificarQueNoEsteRota(arma.estaRota(), "La " + arma.getClass().getSimpleName().toLowerCase());

		objetivo.recibirDano(arma.getDanoAtaque());
		arma.desgastar(arma.getDesgastePorGolpe());
	}

	@Override
	public void talar(Hacha hacha) {
		requerido(hacha, "El hacha no puede ser nula.");
		verificarQueNoEsteRota(hacha);
		hacha.desgastar(1);
	}

	@Override
	public boolean puedeObtener(Pico pico, Mineral mineral) {
		requerido(pico, "El pico no puede ser nulo.");
		requerido(mineral, "El mineral no puede ser nulo.");

		return pico.getMaterial().getNivelMineria()
				>= mineral.getPicoMinimo().getNivelMineria();
	}

	@Override
	public boolean minar(Pico pico, Mineral mineral) {
		boolean obtenido = puedeObtener(pico, mineral);
		verificarQueNoEsteRota(pico);
		pico.desgastar(1);
		return obtenido;
	}

	private static void verificarQueNoEsteRota(Herramienta herramienta) {
		verificarQueNoEsteRota(herramienta.estaRota(),
				"La herramienta de " + herramienta.getMaterial());
	}

	private static void verificarQueNoEsteRota(boolean rota, String cual) {
		if (rota) {
			throw new HerramientaRotaException(cual + " está rota y no se puede usar.");
		}
	}

	private static void requerido(Object valor, String mensaje) {
		if (valor == null) {
			throw new DatoInvalidoException(mensaje);
		}
	}
}
