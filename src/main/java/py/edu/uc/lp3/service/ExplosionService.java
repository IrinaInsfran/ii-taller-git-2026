package py.edu.uc.lp3.service;

import java.util.List;

import py.edu.uc.lp3.domain.Explosion;

/**
 * Simulacion de explosiones de creeper sobre las entidades guardadas.
 */
public interface ExplosionService {

	/** Intenta detonar el creeper; si las reglas lo permiten, guarda todo. */
	Explosion detonarCreeper(Long creeperId);

	List<Explosion> historial();

	Explosion obtenerPorId(Long id);
}
