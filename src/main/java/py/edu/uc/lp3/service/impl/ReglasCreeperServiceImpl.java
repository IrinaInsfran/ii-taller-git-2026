package py.edu.uc.lp3.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.service.ReglasCreeperService;

/**
 * Las dos reglas de la simulación.
 * <ol>
 *   <li><b>Mecha corta</b> ({@link #mechaCorta}): un creeper vivo solo
 *       enciende la mecha si hay un jugador vivo a 3 bloques o menos, y apunta
 *       al más cercano.</li>
 *   <li><b>Onda expansiva</b> ({@link #ondaExpansiva}): usa la fórmula de
 *       Minecraft Java para una víctima sin bloques que la cubran:
 *       <ul>
 *         <li>el radio de daño es el doble de la potencia;</li>
 *         <li>{@code impacto = 1 - distancia / radio};</li>
 *         <li>{@code daño = (impacto² + impacto) / 2 * 7 * radio + 1}.</li>
 *       </ul>
 *       Para un creeper (potencia 3) el radio es 6 bloques y el daño en el
 *       centro es 43: mata a un jugador sin armadura que esté muy cerca.</li>
 * </ol>
 * Las entidades y la explosión solo guardan el estado; este servicio decide
 * qué cambia y lanza las excepciones.
 */
@Service
public class ReglasCreeperServiceImpl implements ReglasCreeperService {

	@Override
	public Jugador mechaCorta(Creeper creeper, List<Jugador> jugadores) {
		requerido(creeper, "El creeper no puede ser nulo.");
		requerido(jugadores, "La lista de jugadores no puede ser nula.");

		if (!creeper.estaViva()) {
			throw new EntidadMuertaException("Un Creeper muerto no puede explotar.");
		}

		return jugadores.stream()
				.filter(jugador -> jugador != null && jugador.estaViva()
						&& creeper.distanciaA(jugador) <= DISTANCIA_DE_DETONACION)
				.min(Comparator.comparingDouble(creeper::distanciaA))
				.orElseThrow(() -> new FueraDeAlcanceException(
						"El creeper solo detona con un jugador vivo a "
						+ (int) DISTANCIA_DE_DETONACION
						+ " bloques o menos, y no hay ninguno."));
	}

	@Override
	public List<Explosion.Impacto> ondaExpansiva(Explosion explosion, List<? extends Entidad> entidades) {
		requerido(explosion, "La explosión no puede ser nula.");
		requerido(entidades, "La lista de entidades no puede ser nula.");

		if (explosion.isDetonada()) {
			throw new ReglaDelJuegoException(
					"explosion_ya_detonada",
					"Una explosión detona una sola vez.");
		}

		List<Explosion.Impacto> alcanzadas = new ArrayList<>();

		for (Entidad entidad : entidades) {
			if (entidad == null || !entidad.estaViva()) {
				continue;
			}

			double distancia = explosion.distanciaA(entidad);
			int dano = calcularDano(explosion, distancia);

			if (dano > 0) {
				entidad.recibirDano(dano);
				alcanzadas.add(new Explosion.Impacto(entidad, distancia, dano));
			}
		}

		explosion.registrarDetonacion(alcanzadas);
		return explosion.getImpactos();
	}

	@Override
	public int calcularDano(Explosion explosion, double distancia) {
		requerido(explosion, "La explosión no puede ser nula.");

		if (!Double.isFinite(distancia) || distancia < 0) {
			throw new DatoInvalidoException("La distancia debe ser un valor válido y no negativo.");
		}

		double radio = explosion.getRadio();

		if (distancia >= radio) {
			return 0;
		}

		double impacto = 1 - distancia / radio;

		return (int) ((impacto * impacto + impacto) / 2 * 7 * radio + 1);
	}

	@Override
	public Explosion explotarCercaDe(Creeper creeper, List<Jugador> jugadores,
	                                 List<? extends Entidad> alrededor) {
		Jugador objetivo = mechaCorta(creeper, jugadores);
		requerido(alrededor, "La lista de entidades alrededor no puede ser nula.");

		Explosion explosion = new Explosion(creeper, objetivo);
		creeper.morir();
		ondaExpansiva(explosion, alrededor);
		return explosion;
	}

	@Override
	public List<Explosion.Impacto> explotar(Creeper creeper, List<? extends Entidad> alrededor) {
		requerido(creeper, "El creeper no puede ser nulo.");
		requerido(alrededor, "La lista de entidades alrededor no puede ser nula.");

		if (!creeper.estaViva()) {
			throw new EntidadMuertaException("Un Creeper muerto no puede explotar.");
		}

		Explosion explosion = new Explosion(creeper, null);
		creeper.morir();
		return ondaExpansiva(explosion, alrededor);
	}

	private static void requerido(Object valor, String mensaje) {
		if (valor == null) {
			throw new DatoInvalidoException(mensaje);
		}
	}
}
