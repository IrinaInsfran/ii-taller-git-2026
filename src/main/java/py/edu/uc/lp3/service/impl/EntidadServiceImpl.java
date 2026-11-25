package py.edu.uc.lp3.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.dto.EntidadRequest;
import py.edu.uc.lp3.dto.TipoEntidad;
import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.EntidadHostil;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.service.EntidadService;

/**
 * Reglas de negocio del CRUD. El dominio solo guarda el estado: todas las
 * validaciones están acá.
 * <ul>
 *   <li>el tipo es obligatorio y define qué campos son obligatorios;</li>
 *   <li>si no se manda ninguna estadística (salud, velocidad, rango...),
 *       se usa el constructor sobrecargado con los valores por defecto del
 *       juego; si se manda alguna, el constructor completo y todas son
 *       obligatorias;</li>
 *   <li>invariantes de cada entidad ({@link #validar(Entidad)}): salud
 *       mayor que cero, velocidad no negativa, posición con valores finitos,
 *       rango de detección no negativo, daño y tiempo de explosión positivos,
 *       nombre y profesión no vacíos, experiencia no negativa;</li>
 *   <li>no puede haber dos jugadores con el mismo nombre;</li>
 *   <li>el PUT no cambia el tipo de una entidad ni edita a una muerta;</li>
 *   <li>no se puede leer, actualizar ni borrar un id inexistente.</li>
 * </ul>
 */
@Service
@Transactional
public class EntidadServiceImpl implements EntidadService {

	private final EntidadRepository entidadRepository;

	public EntidadServiceImpl(EntidadRepository entidadRepository) {
		this.entidadRepository = entidadRepository;
	}

	@Override
	public Entidad crear(EntidadRequest datos) {
		Entidad nueva = construir(datos);
		validar(nueva);
		validarNombreUnico(nueva, null);
		return entidadRepository.save(nueva);
	}

	@Override
	@Transactional(readOnly = true)
	public Entidad obtenerPorId(Long id) {
		return entidadRepository.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException(id));
	}

	@Override
	@Transactional(readOnly = true)
	public List<Entidad> obtenerTodas() {
		return entidadRepository.findAll();
	}

	@Override
	public Entidad actualizar(Long id, EntidadRequest datos) {
		Entidad existente = obtenerPorId(id);

		// PUT reemplaza todos los datos: se construye una entidad nueva, pasa
		// por las mismas validaciones que el POST y la existente copia su
		// estado. La entidad nueva nunca se guarda; el id no cambia.
		Entidad nueva = construir(datos);
		validar(nueva);
		validarNombreUnico(nueva, id);
		validarReemplazo(existente, nueva);
		existente.reemplazarCon(nueva);

		// La entidad está gestionada por JPA: el cambio se guarda al cerrar
		// la transacción.
		return existente;
	}

	@Override
	public void eliminar(Long id) {
		entidadRepository.delete(obtenerPorId(id));
	}

	/**
	 * Invariantes de la entidad recién construida. Se revisan sobre el
	 * objeto (y no sobre el JSON) para cubrir igual el constructor completo y
	 * el sobrecargado, que pone los valores por defecto del juego.
	 */
	private static void validar(Entidad entidad) {
		if (entidad.getSalud() <= 0) {
			throw new DatoInvalidoException("La salud debe ser mayor que cero.");
		}
		if (entidad.getVelocidad() < 0) {
			throw new DatoInvalidoException("La velocidad no puede ser negativa.");
		}
		if (!Double.isFinite(entidad.getPosicionX())
				|| !Double.isFinite(entidad.getPosicionY())
				|| !Double.isFinite(entidad.getPosicionZ())) {
			throw new DatoInvalidoException("La posición debe contener valores válidos.");
		}

		if (entidad instanceof EntidadHostil hostil) {
			if (!Double.isFinite(hostil.getRangoDeteccion()) || hostil.getRangoDeteccion() < 0) {
				throw new DatoInvalidoException("El rango de detección no puede ser negativo.");
			}
			if (hostil.getDanoAtaque() <= 0) {
				throw new DatoInvalidoException("El daño de ataque debe ser mayor que cero.");
			}
		}

		switch (entidad) {
			case Creeper creeper when creeper.getTiempoExplosion() <= 0 ->
					throw new DatoInvalidoException("El tiempo de explosión debe ser mayor que cero.");
			case Jugador jugador when jugador.getNombre() == null || jugador.getNombre().isBlank() ->
					throw new DatoInvalidoException("El nombre del jugador no puede estar vacío.");
			case Jugador jugador when jugador.getNivelEXP() < 0 ->
					throw new DatoInvalidoException("El nivel de experiencia no puede ser negativo.");
			case Aldeano aldeano when aldeano.getProfesion() == null || aldeano.getProfesion().isBlank() ->
					throw new DatoInvalidoException("La profesión no puede estar vacía.");
			default -> {
				// El resto de los tipos no tiene campos propios que validar.
			}
		}
	}

	/** El PUT no cambia el tipo (un zombie no pasa a ser jugador) ni resucita a un muerto. */
	private static void validarReemplazo(Entidad existente, Entidad nueva) {
		if (nueva.getClass() != existente.getClass()) {
			throw new DatoInvalidoException(
					"cambio_de_tipo",
					"No se puede cambiar el tipo de una entidad: la entidad "
					+ existente.getId() + " es " + TipoEntidad.de(existente) + ".");
		}

		if (!existente.estaViva()) {
			throw new EntidadMuertaException(
					"La entidad " + existente.getId() + " está muerta: no se puede modificar.");
		}
	}

	private void validarNombreUnico(Entidad entidad, Long idExcluido) {
		if (entidad instanceof Jugador jugador
				&& entidadRepository.existeJugadorConNombre(jugador.getNombre(), idExcluido)) {
			throw new DatoInvalidoException(
					"nombre_repetido",
					"Ya existe un jugador con el nombre '" + jugador.getNombre() + "'.");
		}
	}

	/**
	 * Crea la clase hija que corresponde al tipo pedido. Acá solo se exige que
	 * los campos estén presentes; los valores los revisa {@link #validar(Entidad)}.
	 */
	private Entidad construir(EntidadRequest datos) {
		if (datos == null) {
			throw new DatoInvalidoException("El cuerpo de la petición es obligatorio.");
		}

		TipoEntidad tipo = requerido(datos.tipo(), "tipo", null);

		if (sinEstadisticas(tipo, datos)) {
			return construirPorDefecto(tipo, datos);
		}

		int salud = requerido(datos.salud(), "salud", tipo);
		double x = requerido(datos.posicionX(), "posicionX", tipo);
		double y = requerido(datos.posicionY(), "posicionY", tipo);
		double z = requerido(datos.posicionZ(), "posicionZ", tipo);
		int velocidad = requerido(datos.velocidad(), "velocidad", tipo);

		return switch (tipo) {
			case JUGADOR -> new Jugador(salud, x, y, z, velocidad,
					requerido(datos.nombre(), "nombre", tipo),
					valorOPorDefecto(datos.nivelEXP(), 0));
			case ZOMBIE -> new Zombie(salud, x, y, z, velocidad,
					requerido(datos.rangoDeteccion(), "rangoDeteccion", tipo),
					requerido(datos.danoAtaque(), "danoAtaque", tipo));
			case ESQUELETO -> new Esqueleto(salud, x, y, z, velocidad,
					requerido(datos.rangoDeteccion(), "rangoDeteccion", tipo),
					requerido(datos.danoAtaque(), "danoAtaque", tipo));
			case CREEPER -> new Creeper(salud, x, y, z, velocidad,
					requerido(datos.rangoDeteccion(), "rangoDeteccion", tipo),
					requerido(datos.danoAtaque(), "danoAtaque", tipo),
					requerido(datos.tiempoExplosion(), "tiempoExplosion", tipo));
			case ALDEANO -> new Aldeano(salud, x, y, z, velocidad,
					valorOPorDefecto(datos.domesticable(), false),
					requerido(datos.profesion(), "profesion", tipo));
			case ANIMAL -> new Animal(salud, x, y, z, velocidad,
					valorOPorDefecto(datos.domesticable(), false),
					valorOPorDefecto(datos.montable(), false));
		};
	}

	/** Ninguna estadística del tipo vino en el JSON: se usan las del juego. */
	private static boolean sinEstadisticas(TipoEntidad tipo, EntidadRequest d) {
		boolean sinBase = d.salud() == null && d.velocidad() == null;
		boolean sinHostil = d.rangoDeteccion() == null && d.danoAtaque() == null;

		return sinBase && switch (tipo) {
			case JUGADOR -> d.nivelEXP() == null;
			case ZOMBIE, ESQUELETO -> sinHostil;
			case CREEPER -> sinHostil && d.tiempoExplosion() == null;
			case ALDEANO -> d.domesticable() == null;
			case ANIMAL -> d.domesticable() == null && d.montable() == null;
		};
	}

	/** Usa los constructores sobrecargados de cada clase hija. */
	private static Entidad construirPorDefecto(TipoEntidad tipo, EntidadRequest d) {
		if (tipo == TipoEntidad.JUGADOR
				&& d.posicionX() == null && d.posicionY() == null && d.posicionZ() == null) {
			return new Jugador(requerido(d.nombre(), "nombre", tipo));
		}

		double x = requerido(d.posicionX(), "posicionX", tipo);
		double y = requerido(d.posicionY(), "posicionY", tipo);
		double z = requerido(d.posicionZ(), "posicionZ", tipo);

		return switch (tipo) {
			case JUGADOR -> new Jugador(requerido(d.nombre(), "nombre", tipo), x, y, z);
			case ZOMBIE -> new Zombie(x, y, z);
			case ESQUELETO -> new Esqueleto(x, y, z);
			case CREEPER -> new Creeper(x, y, z);
			case ALDEANO -> new Aldeano(x, y, z, requerido(d.profesion(), "profesion", tipo));
			case ANIMAL -> new Animal(x, y, z);
		};
	}

	private static <T> T requerido(T valor, String campo, TipoEntidad tipo) {
		if (valor == null) {
			String sufijo = tipo == null ? "" : " para un " + tipo;
			throw new DatoInvalidoException(
					"campo_obligatorio",
					"El campo '" + campo + "' es obligatorio" + sufijo + ".");
		}
		return valor;
	}

	private static <T> T valorOPorDefecto(T valor, T porDefecto) {
		return valor == null ? porDefecto : valor;
	}
}
