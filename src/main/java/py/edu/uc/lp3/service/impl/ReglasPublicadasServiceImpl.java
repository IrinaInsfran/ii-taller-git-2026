package py.edu.uc.lp3.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Jugador;
import py.edu.uc.lp3.dto.MechaCortaRequest;
import py.edu.uc.lp3.dto.MechaCortaRequest.Objetivo;
import py.edu.uc.lp3.dto.MechaCortaRequest.Posicion;
import py.edu.uc.lp3.dto.ReglaResponse;
import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.service.ReglasCreeperService;
import py.edu.uc.lp3.service.ReglasPublicadasService;

/**
 * Traduce el pedido HTTP a entidades en memoria y aplica la regla del
 * dominio. Como no hay repositorio, una consulta nunca cambia la base.
 */
@Service
public class ReglasPublicadasServiceImpl implements ReglasPublicadasService {

	private static final String MECHA_CORTA = "mecha-corta";
	private static final String TIPO_JUGADOR = "jugador";
	private static final int SALUD_POR_DEFECTO = 20;

	private final ReglasCreeperService reglasCreeper;

	public ReglasPublicadasServiceImpl(ReglasCreeperService reglasCreeper) {
		this.reglasCreeper = reglasCreeper;
	}

	@Override
	public ReglaResponse mechaCorta(MechaCortaRequest pedido) {
		if (pedido == null) {
			throw new DatoInvalidoException("Falta el cuerpo del pedido.");
		}
		Posicion centro = validarPosicion(pedido.creeper(), "creeper");
		List<Objetivo> objetivos = pedido.objetivos();
		if (objetivos == null || objetivos.isEmpty()) {
			throw new DatoInvalidoException("La lista «objetivos» debe tener al menos una entidad.");
		}

		Creeper creeper = new Creeper(centro.x(), centro.y(), centro.z());
		if (pedido.saludCreeper() != null && pedido.saludCreeper() <= 0) {
			creeper.morir();
		}

		List<Jugador> jugadores = new ArrayList<>();
		List<Integer> indices = new ArrayList<>();
		for (int i = 0; i < objetivos.size(); i++) {
			Objetivo objetivo = objetivos.get(i);
			if (objetivo == null) {
				throw new DatoInvalidoException("objetivos[" + i + "] no puede ser nulo.");
			}
			Posicion posicion = validarPosicion(objetivo.posicion(), "objetivos[" + i + "].posicion");
			if (TIPO_JUGADOR.equalsIgnoreCase(objetivo.tipo())) {
				jugadores.add(jugadorEn(posicion, objetivo.salud(), i));
				indices.add(i);
			}
		}

		Jugador elegido = reglasCreeper.mechaCorta(creeper, jugadores);
		int indice = indices.get(posicionDe(elegido, jugadores));
		double distancia = creeper.distanciaA(elegido);
		return new ReglaResponse(true, MECHA_CORTA, indice, distancia,
				"El creeper enciende la mecha: el jugador de objetivos[" + indice + "] está a "
						+ distancia + " bloques.");
	}

	private Jugador jugadorEn(Posicion posicion, Integer salud, int indice) {
		Jugador jugador = new Jugador("objetivos[" + indice + "]", posicion.x(), posicion.y(), posicion.z());
		if (salud == null) {
			return jugador;
		}
		if (salud <= 0) {
			jugador.morir();
		} else {
			jugador.recibirDano(Math.max(0, SALUD_POR_DEFECTO - salud));
		}
		return jugador;
	}

	/** Busca por identidad: dos jugadores en el mismo lugar siguen siendo distintos. */
	private static int posicionDe(Jugador elegido, List<Jugador> jugadores) {
		for (int i = 0; i < jugadores.size(); i++) {
			if (jugadores.get(i) == elegido) {
				return i;
			}
		}
		throw new IllegalStateException("La regla devolvió un jugador que no estaba en la lista.");
	}

	private Posicion validarPosicion(Posicion posicion, String campo) {
		if (posicion == null || !finito(posicion.x()) || !finito(posicion.y()) || !finito(posicion.z())) {
			throw new DatoInvalidoException("«" + campo + "» necesita x, y, z numéricos.");
		}
		return posicion;
	}

	private static boolean finito(Double valor) {
		return valor != null && Double.isFinite(valor);
	}
}
