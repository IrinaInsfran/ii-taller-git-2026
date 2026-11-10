package py.edu.uc.lp3.dto;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;

/**
 * Tipos concretos de entidad que se pueden crear por la API.
 * <p>
 * Entidad es abstracta, asi que el cliente debe decir que clase hija quiere.
 */
public enum TipoEntidad {
	JUGADOR,
	ZOMBIE,
	ESQUELETO,
	CREEPER,
	ALDEANO,
	ANIMAL;

	public static TipoEntidad de(Entidad entidad) {
		return switch (entidad) {
			case Jugador j -> JUGADOR;
			case Zombie z -> ZOMBIE;
			case Esqueleto e -> ESQUELETO;
			case Creeper c -> CREEPER;
			case Aldeano a -> ALDEANO;
			case Animal a -> ANIMAL;
			default -> throw new IllegalArgumentException(
					"Tipo de entidad no soportado: " + entidad.getClass().getSimpleName());
		};
	}
}
