package py.edu.uc.lp3.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.dto.TipoEntidad;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;
import py.edu.uc.lp3.service.ExplosionService;

/**
 * Orquesta la simulacion; las reglas estan en el dominio.
 * <p>
 * Busca al creeper y a los jugadores vivos, y le pide al {@link Creeper} que
 * explote ({@link Creeper#explotarCercaDe}): la regla 1 (mecha corta) la
 * aplica el creeper y la regla 2 (onda expansiva) la {@link Explosion}.
 * Todo corre en una transaccion: si el dominio lanza una excepcion, ninguna
 * entidad queda dañada a medias y no se guarda la explosion.
 */
@Service
@Transactional
public class ExplosionServiceImpl implements ExplosionService {

	private final EntidadRepository entidadRepository;
	private final ExplosionRepository explosionRepository;

	public ExplosionServiceImpl(EntidadRepository entidadRepository,
	                            ExplosionRepository explosionRepository) {
		this.entidadRepository = entidadRepository;
		this.explosionRepository = explosionRepository;
	}

	@Override
	public Explosion detonarCreeper(Long creeperId) {
		if (creeperId == null) {
			throw new DatoInvalidoException(
					"campo_obligatorio", "El campo 'creeperId' es obligatorio.");
		}

		Entidad entidad = entidadRepository.findById(creeperId)
				.orElseThrow(() -> new EntidadNoEncontradaException(creeperId));

		if (!(entidad instanceof Creeper creeper)) {
			throw new ReglaDelJuegoException(
					"solo_creepers_explotan",
					"La entidad " + creeperId + " es " + TipoEntidad.de(entidad)
					+ ": solo un CREEPER puede explotar.");
		}

		// Las entidades cargadas estan gestionadas por JPA: el daño que les
		// aplica la explosion se guarda solo al terminar la transaccion.
		Explosion explosion = creeper.explotarCercaDe(
				entidadRepository.buscarJugadoresVivos(),
				entidadRepository.findAll());

		return explosionRepository.save(explosion);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Explosion> historial() {
		return explosionRepository.findAllByOrderByIdDesc();
	}

	@Override
	@Transactional(readOnly = true)
	public Explosion obtenerPorId(Long id) {
		return explosionRepository.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException("explosion", "explosión", id));
	}
}
