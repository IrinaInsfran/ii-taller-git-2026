package py.edu.uc.lp3.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import py.edu.uc.lp3.client.MinecraftRemotoClient;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.Esqueleto;
import py.edu.uc.lp3.dto.DisparoRemoto;
import py.edu.uc.lp3.dto.FlechazoResponse;
import py.edu.uc.lp3.dto.TipoEntidad;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.EntidadNoEncontradaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;
import py.edu.uc.lp3.repository.EntidadRepository;
import py.edu.uc.lp3.service.FlechazoService;

/**
 * Combina los dos proyectos:
 * <ol>
 *   <li><b>Este proyecto</b> busca al esqueleto y al objetivo en H2, valida
 *       que el tirador sea un esqueleto y que los dos estén vivos, y calcula
 *       la distancia con {@link Entidad#distanciaA}.</li>
 *   <li><b>La API remota</b> aplica su regla de disparo: el esqueleto tiene
 *       un rango (8 bloques) y la flecha solo sale si el objetivo está dentro.</li>
 *   <li>Si la flecha llega, <b>este proyecto</b> le resta al objetivo el
 *       {@code danoAtaque} del esqueleto con {@link Entidad#recibirDano} (si
 *       la salud llega a 0, el objetivo muere). El cambio queda en H2.</li>
 * </ol>
 * Si la API remota falla, la transacción se revierte y nadie recibe daño.
 */
@Service
@Transactional
public class FlechazoServiceImpl implements FlechazoService {

	private static final Logger log = LogManager.getLogger(FlechazoServiceImpl.class);

	private final EntidadRepository entidadRepository;
	private final MinecraftRemotoClient minecraftRemoto;

	public FlechazoServiceImpl(EntidadRepository entidadRepository,
	                           MinecraftRemotoClient minecraftRemoto) {
		this.entidadRepository = entidadRepository;
		this.minecraftRemoto = minecraftRemoto;
	}

	@Override
	public FlechazoResponse disparar(Long esqueletoId, Long objetivoId) {
		log.debug("Flechazo: inicio, esqueletoId={}, objetivoId={}", esqueletoId, objetivoId);
		if (esqueletoId == null || objetivoId == null) {
			throw new DatoInvalidoException(
					"campo_obligatorio", "Los campos 'esqueletoId' y 'objetivoId' son obligatorios.");
		}
		if (esqueletoId.equals(objetivoId)) {
			throw new DatoInvalidoException(
					"objetivo_invalido", "Un esqueleto no puede dispararse a sí mismo.");
		}

		Entidad tirador = buscar(esqueletoId);
		if (!(tirador instanceof Esqueleto esqueleto)) {
			throw new ReglaDelJuegoException(
					"solo_esqueletos_disparan",
					"La entidad " + esqueletoId + " es " + TipoEntidad.de(tirador)
					+ ": solo un ESQUELETO dispara flechas.");
		}
		Entidad objetivo = buscar(objetivoId);

		if (!esqueleto.estaViva()) {
			throw new EntidadMuertaException("El esqueleto " + esqueletoId + " está muerto: no puede disparar.");
		}
		if (!objetivo.estaViva()) {
			throw new EntidadMuertaException("La entidad " + objetivoId + " ya está muerta.");
		}

		double distancia = Math.round(esqueleto.distanciaA(objetivo) * 100) / 100.0;
		log.debug("Flechazo: validado. Tirador ESQUELETO {} (salud {}, daño {}), objetivo {} {} (salud {}), distancia {}",
				esqueletoId, esqueleto.getSalud(), esqueleto.getDanoAtaque(),
				TipoEntidad.de(objetivo), objetivoId, objetivo.getSalud(), distancia);

		// Regla del otro proyecto: ¿la flecha llega a esa distancia?
		DisparoRemoto disparo = minecraftRemoto.dispararEsqueleto(
				"Esqueleto-" + esqueletoId,
				esqueleto.getSalud(),
				esqueleto.getPosicionX(), esqueleto.getPosicionY(), esqueleto.getPosicionZ(),
				distancia);

		int saludAntes = objetivo.getSalud();
		int dano = 0;

		// Reglas de este proyecto: el disparo y el daño.
		if (disparo.alcanzo()) {
			dano = esqueleto.getDanoAtaque();
			objetivo.recibirDano(dano);
			log.debug("Flechazo: la flecha llegó (rango remoto {}). Objetivo {}: salud {} -> {}{}",
					disparo.rango(), objetivoId, saludAntes, objetivo.getSalud(),
					objetivo.estaViva() ? "" : " (murió)");
		} else {
			log.debug("Flechazo: la API remota dice que no alcanza (distancia {} > rango {}). Sin daño",
					distancia, disparo.rango());
		}

		// objetivo está gestionado por JPA: su salud nueva se guarda al cerrar la transacción.
		return new FlechazoResponse(
				esqueletoId,
				objetivoId,
				TipoEntidad.de(objetivo),
				distancia,
				disparo.rango(),
				disparo.alcanzo(),
				dano,
				saludAntes,
				objetivo.getSalud(),
				!objetivo.estaViva(),
				disparo.flechasRestantes(),
				disparo.disparoConDistancia());
	}

	private Entidad buscar(Long id) {
		return entidadRepository.findById(id)
				.orElseThrow(() -> new EntidadNoEncontradaException(id));
	}
}
