package py.edu.uc.lp3.dto;

import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Espada;
import py.edu.uc.lp3.domain.Hacha;
import py.edu.uc.lp3.domain.Material;

/**
 * Armas que se pueden sumar a la consulta de amenazas. Las herramientas no se
 * guardan en la base: se fabrican nuevas, con su durabilidad completa.
 */
public enum TipoArma {
	ESPADA,
	HACHA;

	public Arma fabricar(Material material) {
		return switch (this) {
			case ESPADA -> new Espada(material);
			case HACHA -> new Hacha(material);
		};
	}
}
