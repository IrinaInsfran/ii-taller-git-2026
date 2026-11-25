package py.edu.uc.lp3.service.impl;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import py.edu.uc.lp3.client.CeciReglasClient;
import py.edu.uc.lp3.client.ReglaCompaneraClient;
import py.edu.uc.lp3.client.SolReglasClient;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Explosion;
import py.edu.uc.lp3.domain.Jugada;
import py.edu.uc.lp3.dto.CompaneraResponse;
import py.edu.uc.lp3.dto.ExplosionResponse;
import py.edu.uc.lp3.dto.JugadaRequest;
import py.edu.uc.lp3.dto.JugadaResponse;
import py.edu.uc.lp3.dto.MechaCortaRequest.Objetivo;
import py.edu.uc.lp3.dto.MechaCortaRequest.Posicion;
import py.edu.uc.lp3.dto.RespuestaCompanera;
import py.edu.uc.lp3.dto.TipoEntidad;
import py.edu.uc.lp3.dto.ValidacionCompaneraRequest;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.repository.ExplosionRepository;
import py.edu.uc.lp3.repository.JugadaRepository;
import py.edu.uc.lp3.service.ExplosionService;
import py.edu.uc.lp3.service.JugadaService;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

/**
 * El escenario «el creeper pide permiso»:
 * <ol>
 *   <li>Lee de H2 el creeper y las entidades vivas dentro de su radio, sin
 *       cambiar nada.</li>
 *   <li>Arma un solo cuerpo y le pregunta a Cecilia (A2) y, si acepta, a
 *       Sol (A3). Si alguna rechaza o no responde, corta: todavía no se
 *       escribió nada, así que H2 queda igual.</li>
 *   <li>Con las dos de acuerdo, en una sola transacción, aplica mis reglas
 *       (mecha corta y onda expansiva con {@link ExplosionService}) y guarda
 *       la jugada. Si mi regla rechaza, la transacción se deshace entera.</li>
 * </ol>
 * Las llamadas HTTP quedan fuera de la transacción a propósito: no se tiene
 * una conexión a la base abierta mientras se espera a otra API.
 */
@Service
public class JugadaServiceImpl implements JugadaService {

	private static final Logger log = LogManager.getLogger(JugadaServiceImpl.class);
	private static final JsonMapper JSON = JsonMapper.builder().build();

	private final EntidadRepository entidadRepository;
	private final ExplosionRepository explosionRepository;
	private final JugadaRepository jugadaRepository;
	private final ExplosionService explosionService;
	private final CeciReglasClient a2;
	private final SolReglasClient a3;
	private final TransactionTemplate transaccion;

	public JugadaServiceImpl(EntidadRepository entidadRepository,
	                         ExplosionRepository explosionRepository,
	                         JugadaRepository jugadaRepository,
	                         ExplosionService explosionService,
	                         CeciReglasClient a2,
	                         SolReglasClient a3,
	                         TransactionTemplate transaccion) {
		this.entidadRepository = entidadRepository;
		this.explosionRepository = explosionRepository;
		this.jugadaRepository = jugadaRepository;
		this.explosionService = explosionService;
		this.a2 = a2;
		this.a3 = a3;
		this.transaccion = transaccion;
	}

	@Override
	public JugadaResponse registrarExplosion(JugadaRequest pedido) {
		validar(pedido);
		Creeper creeper = buscarCreeper(pedido.creeperId());

		ValidacionCompaneraRequest consulta = armarConsulta(creeper, pedido.tiempoTranscurrido());
		log.debug("Jugada del creeper {}: consultando a A2 y A3 con {}", creeper.getId(), consulta);

		// Si una rechaza o no responde, la excepción corta acá: nada se escribió.
		RespuestaCompanera respuestaA2 = a2.validar(consulta);
		RespuestaCompanera respuestaA3 = a3.validar(consulta);

		JugadaResponse guardada = transaccion.execute(estado -> {
			Explosion explosion = explosionService.detonarCreeper(creeper.getId());
			Jugada jugada = jugadaRepository.save(new Jugada(
					creeper.getId(), explosion.getId(), pedido.tiempoTranscurrido(), Instant.now(),
					respuestaA2.companera(), respuestaA2.url(), respuestaA2.estado(), comoTexto(respuestaA2.respuesta()),
					respuestaA3.companera(), respuestaA3.url(), respuestaA3.estado(), comoTexto(respuestaA3.respuesta())));
			return new JugadaResponse(jugada.getId(), jugada.getCreeperId(), jugada.getTiempoTranscurrido(),
					jugada.getMomento(), respuestaA2, respuestaA3, ExplosionResponse.conDetalle(explosion));
		});
		log.info("Jugada {} guardada: A2 y A3 aceptaron la explosión del creeper {}", guardada.id(), creeper.getId());
		return guardada;
	}

	@Override
	public List<JugadaResponse> listar() {
		return jugadaRepository.findAllByOrderByIdDesc().stream().map(this::aRespuesta).toList();
	}

	@Override
	public JugadaResponse obtener(Long id) {
		return jugadaRepository.findById(id)
				.map(this::aRespuesta)
				.orElseThrow(() -> new EntidadNoEncontradaException("jugada", "jugada", id));
	}

	@Override
	public List<CompaneraResponse> companeras() {
		return List.of(info(a2), info(a3));
	}

	private void validar(JugadaRequest pedido) {
		if (pedido == null || pedido.creeperId() == null) {
			throw new DatoInvalidoException("campo_obligatorio", "El campo 'creeperId' es obligatorio.");
		}
		if (pedido.tiempoTranscurrido() == null) {
			throw new DatoInvalidoException("campo_obligatorio", "El campo 'tiempoTranscurrido' es obligatorio.");
		}
		if (pedido.tiempoTranscurrido() < 0) {
			throw new DatoInvalidoException("El tiempo transcurrido no puede ser negativo.");
		}
	}

	private Creeper buscarCreeper(Long id) {
		Entidad entidad = entidadRepository.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException(id));
		if (!(entidad instanceof Creeper creeper)) {
			throw new ReglaDelJuegoException("solo_creepers_explotan",
					"La entidad " + id + " es " + TipoEntidad.de(entidad) + ": solo un CREEPER puede explotar.");
		}
		return creeper;
	}

	/**
	 * Un cuerpo para las dos: Ceci usa la salud y si ya detonó; Sol, los
	 * tiempos, el radio y las posiciones. Los objetivos son las entidades
	 * vivas que alcanzaría la onda expansiva.
	 */
	private ValidacionCompaneraRequest armarConsulta(Creeper creeper, int tiempoTranscurrido) {
		double radio = Creeper.POTENCIA_EXPLOSION * 2.0;
		List<Objetivo> objetivos = entidadRepository.findAll().stream()
				.filter(e -> !e.getId().equals(creeper.getId()))
				.filter(Entidad::estaViva)
				.filter(e -> creeper.distanciaA(e) <= radio)
				.map(e -> new Objetivo(TipoEntidad.de(e).name().toLowerCase(Locale.ROOT), e.getSalud(), posicion(e)))
				.toList();

		if (objetivos.isEmpty()) {
			throw new FueraDeAlcanceException("No hay ninguna entidad viva a " + (int) radio
					+ " bloques o menos del creeper: no hay jugada que consultar.");
		}

		boolean yaDetonado = !explosionRepository.findByCreeperId(creeper.getId()).isEmpty();
		return new ValidacionCompaneraRequest(creeper.getSalud(), yaDetonado, creeper.getTiempoExplosion(),
				tiempoTranscurrido, radio, posicion(creeper), objetivos);
	}

	private static Posicion posicion(Entidad entidad) {
		return new Posicion(entidad.getPosicionX(), entidad.getPosicionY(), entidad.getPosicionZ());
	}

	private JugadaResponse aRespuesta(Jugada jugada) {
		ExplosionResponse explosion = jugada.getExplosionId() == null ? null
				: explosionRepository.findById(jugada.getExplosionId()).map(ExplosionResponse::resumen).orElse(null);
		return new JugadaResponse(jugada.getId(), jugada.getCreeperId(), jugada.getTiempoTranscurrido(),
				jugada.getMomento(),
				new RespuestaCompanera("A2", jugada.getCompaneraA2(), jugada.getUrlA2(), jugada.getEstadoA2(),
						comoJson(jugada.getRespuestaA2())),
				new RespuestaCompanera("A3", jugada.getCompaneraA3(), jugada.getUrlA3(), jugada.getEstadoA3(),
						comoJson(jugada.getRespuestaA3())),
				explosion);
	}

	private static CompaneraResponse info(ReglaCompaneraClient cliente) {
		return new CompaneraResponse(cliente.getRol(), cliente.getNombre(), cliente.url());
	}

	private static String comoTexto(Object cuerpo) {
		if (cuerpo == null) {
			return null;
		}
		String texto = cuerpo instanceof String s ? s : JSON.writeValueAsString(cuerpo);
		return texto.length() > Jugada.LARGO_RESPUESTA ? texto.substring(0, Jugada.LARGO_RESPUESTA) : texto;
	}

	private static Object comoJson(String texto) {
		if (texto == null) {
			return null;
		}
		try {
			return JSON.readValue(texto, Object.class);
		} catch (JacksonException excepcion) {
			return texto;
		}
	}
}
