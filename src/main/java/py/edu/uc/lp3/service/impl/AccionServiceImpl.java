package py.edu.uc.lp3.service.impl;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Arma;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.EntidadHostil;
import py.edu.uc.lp3.domain.EntidadPasiva;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.service.AccionService;
import py.edu.uc.lp3.service.HerramientaService;

/**
 * Reglas de las acciones del juego. El dominio solo guarda el estado; acá
 * se decide si la acción se puede hacer:
 * <ul>
 *   <li>un muerto no actúa ni se cura, y no se puede atacar a un muerto;</li>
 *   <li>nadie se mueve más que su velocidad;</li>
 *   <li>un mob ataca solo dentro de su rango de detección; un jugador, solo
 *       a {@value #ALCANCE_ATAQUE_JUGADOR} bloques o menos y no a sí mismo;</li>
 *   <li>un aldeano infectado no comercia; un animal no montable no se monta;</li>
 *   <li>el daño, la curación y la experiencia son positivos, y la
 *       experiencia no puede pasar de {@link Integer#MAX_VALUE}.</li>
 * </ul>
 */
@Service
public class AccionServiceImpl implements AccionService {

	/** Alcance del ataque cuerpo a cuerpo del jugador en Minecraft Java. */
	public static final double ALCANCE_ATAQUE_JUGADOR = 3;

	/** Tolerancia para comparar el desplazamiento con la velocidad. */
	private static final double TOLERANCIA = 1e-9;

	private final HerramientaService herramientaService;

	public AccionServiceImpl(HerramientaService herramientaService) {
		this.herramientaService = herramientaService;
	}

	@Override
	public void mover(Entidad entidad, double dx, double dz) {
		mover(entidad, dx, 0, dz);
	}

	@Override
	public void mover(Entidad entidad, double dx, double dy, double dz) {
		requerido(entidad, "La entidad no puede ser nula.");
		vivo(entidad, "Una entidad muerta no puede moverse.");

		if (!Double.isFinite(dx) || !Double.isFinite(dy) || !Double.isFinite(dz)) {
			throw new DatoInvalidoException("El desplazamiento debe contener valores válidos.");
		}

		double distancia = Math.sqrt(dx * dx + dy * dy + dz * dz);

		if (distancia > entidad.getVelocidad() + TOLERANCIA) {
			throw new ReglaDelJuegoException(
					"excede_velocidad",
					"La entidad no puede moverse más que su velocidad.");
		}

		entidad.mover(dx, dy, dz);
	}

	@Override
	public void recibirDano(Entidad entidad, int cantidad) {
		requerido(entidad, "La entidad no puede ser nula.");

		if (cantidad <= 0) {
			throw new DatoInvalidoException("La cantidad de daño debe ser mayor que cero.");
		}

		vivo(entidad, "Una entidad muerta no puede recibir más daño.");
		entidad.recibirDano(cantidad);
	}

	@Override
	public void curar(Entidad entidad, int cantidad) {
		requerido(entidad, "La entidad no puede ser nula.");

		if (cantidad <= 0) {
			throw new DatoInvalidoException("La cantidad de curación debe ser mayor que cero.");
		}

		vivo(entidad, "Una entidad muerta no puede ser curada.");
		entidad.curar(cantidad);
	}

	@Override
	public void atacar(EntidadHostil atacante, Entidad objetivo) {
		requerido(atacante, "El atacante no puede ser nulo.");
		requerido(objetivo, "El objetivo no puede ser nulo.");
		vivo(atacante, "Una entidad muerta no puede atacar.");
		vivo(objetivo, "No se puede atacar a una entidad que ya está muerta.");

		if (atacante.distanciaA(objetivo) > atacante.getRangoDeteccion()) {
			throw new FueraDeAlcanceException("El objetivo está fuera del rango de detección.");
		}

		objetivo.recibirDano(atacante.getDanoAtaque());
	}

	@Override
	public void atacar(Jugador jugador, Entidad objetivo, Arma arma) {
		if (jugador == null || objetivo == null || arma == null) {
			throw new DatoInvalidoException("El jugador, el objetivo y el arma no pueden ser nulos.");
		}

		if (objetivo == jugador) {
			throw new ReglaDelJuegoException("auto_ataque", "Un jugador no puede atacarse a sí mismo.");
		}

		vivo(jugador, "Un jugador muerto no puede atacar.");
		vivo(objetivo, "No se puede atacar a una entidad que ya está muerta.");

		if (jugador.distanciaA(objetivo) > ALCANCE_ATAQUE_JUGADOR) {
			throw new FueraDeAlcanceException("El objetivo está fuera del alcance del jugador.");
		}

		herramientaService.golpear(arma, objetivo);
	}

	@Override
	public void huir(EntidadPasiva pasiva, Entidad amenaza) {
		requerido(pasiva, "La entidad que huye no puede ser nula.");
		requerido(amenaza, "La amenaza no puede ser nula.");
		vivo(pasiva, "Una entidad muerta no puede huir.");

		if (pasiva.getVelocidad() == 0) {
			throw new ReglaDelJuegoException("sin_velocidad", "La entidad no tiene velocidad para huir.");
		}

		double dx = pasiva.getPosicionX() - amenaza.getPosicionX();
		double dy = pasiva.getPosicionY() - amenaza.getPosicionY();
		double dz = pasiva.getPosicionZ() - amenaza.getPosicionZ();
		double distancia = pasiva.distanciaA(amenaza);

		if (distancia == 0) {
			mover(pasiva, pasiva.getVelocidad(), 0, 0);
			return;
		}

		double factor = pasiva.getVelocidad() / distancia;
		mover(pasiva, dx * factor, dy * factor, dz * factor);
	}

	@Override
	public void infectar(Zombie zombie, Aldeano aldeano) {
		requerido(zombie, "El zombie no puede ser nulo.");
		requerido(aldeano, "El aldeano no puede ser nulo.");
		vivo(zombie, "Un Zombie muerto no puede infectar aldeanos.");
		vivo(aldeano, "No se puede infectar a un aldeano muerto.");

		if (zombie.distanciaA(aldeano) > zombie.getRangoDeteccion()) {
			throw new FueraDeAlcanceException("El aldeano está fuera del rango de detección.");
		}

		aldeano.infectar();
	}

	@Override
	public String comerciar(Aldeano aldeano) {
		requerido(aldeano, "El aldeano no puede ser nulo.");
		vivo(aldeano, "Un aldeano muerto no puede comerciar.");

		if (aldeano.isInfectado()) {
			throw new ReglaDelJuegoException("aldeano_infectado", "Un aldeano infectado no puede comerciar.");
		}

		return "El aldeano (" + aldeano.getProfesion() + ") ofrece comerciar.";
	}

	@Override
	public String montar(Animal animal, Jugador jugador) {
		requerido(animal, "El animal no puede ser nulo.");
		requerido(jugador, "El jugador no puede ser nulo.");
		vivo(animal, "Un animal muerto no puede ser montado.");
		vivo(jugador, "Un jugador muerto no puede montar.");

		if (!animal.isMontable()) {
			throw new ReglaDelJuegoException("animal_no_montable", "Este animal no es montable.");
		}

		return jugador.getNombre() + " monta al animal.";
	}

	/**
	 * Se usa {@link Math#addExact(int, int)} para detectar el desbordamiento:
	 * si el total supera {@link Integer#MAX_VALUE} se responde con un error en
	 * lugar de dar la vuelta y dejar el nivel en negativo.
	 */
	@Override
	public void ganarExperiencia(Jugador jugador, int cantidad) {
		requerido(jugador, "El jugador no puede ser nulo.");

		if (cantidad <= 0) {
			throw new DatoInvalidoException("La experiencia ganada debe ser mayor que cero.");
		}

		try {
			Math.addExact(jugador.getNivelEXP(), cantidad);
		} catch (ArithmeticException desborde) {
			throw new DatoInvalidoException("La experiencia total supera el máximo permitido.");
		}

		jugador.ganarExperiencia(cantidad);
	}

	private static void vivo(Entidad entidad, String mensaje) {
		if (!entidad.estaViva()) {
			throw new EntidadMuertaException(mensaje);
		}
	}

	private static void requerido(Object valor, String mensaje) {
		if (valor == null) {
			throw new DatoInvalidoException(mensaje);
		}
	}
}
