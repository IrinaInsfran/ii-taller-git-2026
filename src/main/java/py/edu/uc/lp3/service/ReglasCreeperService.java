package py.edu.uc.lp3.service;

import java.util.List;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.domain.Jugador;

/**
 * Las dos reglas de la simulación de explosiones. No usan la base: reciben
 * las entidades ya cargadas y las modifican en memoria; quien las llama
 * decide si guarda el resultado.
 */
public interface ReglasCreeperService {

	/** El creeper solo empieza a detonar si su objetivo está a 3 bloques o menos. */
	double DISTANCIA_DE_DETONACION = 3;

	/**
	 * Regla 1, "mecha corta": de los jugadores dados, el vivo más cercano
	 * que esté a {@value #DISTANCIA_DE_DETONACION} bloques o menos.
	 *
	 * @throws py.edu.uc.lp3.exceptions.EntidadMuertaException  si el creeper está muerto
	 * @throws py.edu.uc.lp3.exceptions.FueraDeAlcanceException si ningún jugador está tan cerca
	 */
	Jugador mechaCorta(Creeper creeper, List<Jugador> jugadores);

	/**
	 * Regla 2, "onda expansiva": daña a cada entidad viva dentro del radio,
	 * más cuanto más cerca esté, y registra el resultado en la explosión.
	 *
	 * @throws py.edu.uc.lp3.exceptions.ReglaDelJuegoException si la explosión ya detonó
	 */
	List<Explosion.Impacto> ondaExpansiva(Explosion explosion, List<? extends Entidad> entidades);

	/** Daño de la explosión a esa distancia del centro; 0 fuera del radio. */
	int calcularDano(Explosion explosion, double distancia);

	/**
	 * La simulación completa: aplica la regla 1 y, si encuentra jugador, el
	 * creeper muere y explota sobre {@code alrededor} (regla 2). Si la regla 1
	 * falla no cambia nada.
	 */
	Explosion explotarCercaDe(Creeper creeper, List<Jugador> jugadores, List<? extends Entidad> alrededor);

	/**
	 * El creeper explota donde está, sin buscar jugador: muere y la explosión
	 * daña a las entidades de {@code alrededor}.
	 */
	List<Explosion.Impacto> explotar(Creeper creeper, List<? extends Entidad> alrededor);
}
