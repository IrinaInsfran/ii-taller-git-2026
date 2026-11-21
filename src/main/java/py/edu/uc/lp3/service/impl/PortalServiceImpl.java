package py.edu.uc.lp3.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.client.MundoCeciClient;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.domain.Zombie;
import py.edu.uc.lp3.dto.EntidadMundoCeci;
import py.edu.uc.lp3.dto.PortalResponse;
import py.edu.uc.lp3.dto.TipoEntidad;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.service.PortalService;

/**
 * Portal entre dos mundos. Cada proyecto pone sus reglas:
 * <ol>
 *   <li><b>Este proyecto</b> decide si la entidad puede cruzar: tiene que
 *       existir, estar viva (un muerto no viaja) y ser de un tipo que exista
 *       en el otro mundo (JUGADOR, ZOMBIE o CREEPER).</li>
 *   <li>La traduce al formato del otro mundo: salud → vida, saludMaxima →
 *       vidaMaxima, nivelEXP → nivel. Los mobs no tienen nombre acá, así que
 *       viajan como "Zombie-5", "Creeper-7".</li>
 *   <li><b>El mundo de Ceci</b> aplica sus reglas de validación y decide si
 *       la acepta (por ejemplo, no admite dos entidades con el mismo nombre).</li>
 *   <li>Si la acepta y no se pidió conservarla, la entidad sale de este mundo.
 *       Si la rechaza o no responde, acá no cambia nada.</li>
 * </ol>
 */
@Service
@Transactional
public class PortalServiceImpl implements PortalService {

	private static final Logger log = LogManager.getLogger(PortalServiceImpl.class);

	private final EntidadRepository entidadRepository;
	private final MundoCeciClient mundoCeci;
	private final String mundoDestino;

	public PortalServiceImpl(EntidadRepository entidadRepository,
	                         MundoCeciClient mundoCeci,
	                         @Value("${mundo-ceci.base-url}") String mundoDestino) {
		this.entidadRepository = entidadRepository;
		this.mundoCeci = mundoCeci;
		this.mundoDestino = mundoDestino;
	}

	@Override
	public PortalResponse cruzar(Long entidadId, boolean conservar) {
		log.debug("Portal: inicio, entidadId={}, conservar={}, destino={}", entidadId, conservar, mundoDestino);
		Entidad entidad = entidadRepository.findById(entidadId)
				.orElseThrow(() -> new EntidadNoEncontradaException(entidadId));

		log.debug("Portal: entidad {} cargada, tipo {}, salud {}/{}", entidadId, TipoEntidad.de(entidad),
				entidad.getSalud(), entidad.getSaludMaxima());

		if (!entidad.estaViva()) {
			log.debug("Portal: entidad {} muerta, no cruza", entidadId);
			throw new EntidadMuertaException(
					"La entidad " + entidadId + " está muerta: un muerto no puede cruzar el portal.");
		}

		EntidadMundoCeci traducida = traducir(entidad);
		log.debug("Portal: entidad {} traducida al formato de Ceci: {}", entidadId, traducida);

		EntidadMundoCeci enDestino = mundoCeci.crearEntidad(traducida);
		log.debug("Portal: el mundo de Ceci la guardó con id {}", enDestino.id());

		if (conservar) {
			log.debug("Portal: conservar=true, la entidad {} sigue en este mundo", entidadId);
		} else {
			entidadRepository.delete(entidad);
			log.debug("Portal: entidad {} borrada de este mundo (se confirma al cerrar la transacción)", entidadId);
		}

		return new PortalResponse(entidadId, TipoEntidad.de(entidad), conservar, mundoDestino, enDestino);
	}

	/** Pasa la entidad al formato del mundo de Ceci, o explica por qué no existe allá. */
	private EntidadMundoCeci traducir(Entidad entidad) {
		Long id = entidad.getId();
		int vida = entidad.getSalud();
		int vidaMaxima = entidad.getSaludMaxima();
		double x = entidad.getPosicionX();
		double y = entidad.getPosicionY();
		double z = entidad.getPosicionZ();

		return switch (entidad) {
			case Jugador jugador -> new EntidadMundoCeci(null, "Jugador", jugador.getNombre(),
					vida, vidaMaxima, x, y, z, jugador.getNivelEXP(), 0, null);
			case Zombie zombie -> new EntidadMundoCeci(null, "Zombie", "Zombie-" + id,
					vida, vidaMaxima, x, y, z, null, null, null);
			case Creeper creeper -> new EntidadMundoCeci(null, "Creeper", "Creeper-" + id,
					vida, vidaMaxima, x, y, z, null, null, false);
			default -> {
				log.debug("Portal: el tipo {} no existe en el mundo de Ceci", TipoEntidad.de(entidad));
				throw new ReglaDelJuegoException(
					"tipo_inexistente_en_destino",
					"Un " + TipoEntidad.de(entidad) + " no puede cruzar: en el mundo de Ceci solo existen "
					+ "JUGADOR, ZOMBIE y CREEPER.");
			}
		};
	}
}
