package py.edu.uc.lp3.dto;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import py.edu.uc.lp3.domain.Explosion;

/**
 * Una explosion simulada. Al crearla trae {@code afectados} (el detalle de
 * cada impacto); al leerla del historial trae solo los contadores que se
 * guardaron en H2.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ExplosionResponse(
		Long id,
		Long creeperId,
		Long jugadorObjetivoId,
		String jugadorObjetivo,
		double centroX,
		double centroY,
		double centroZ,
		double potencia,
		double radio,
		Instant momento,
		int entidadesAlcanzadas,
		int entidadesMuertas,
		int danoTotal,
		List<Afectado> afectados) {

	public record Afectado(
			Long id,
			TipoEntidad tipo,
			double distancia,
			int dano,
			int saludRestante,
			boolean murio) {

		static Afectado desde(Explosion.Impacto impacto) {
			return new Afectado(
					impacto.entidad().getId(),
					TipoEntidad.de(impacto.entidad()),
					Math.round(impacto.distancia() * 100) / 100.0,
					impacto.dano(),
					impacto.entidad().getSalud(),
					!impacto.entidad().estaViva());
		}
	}

	/** Respuesta de la simulacion recien hecha, con el detalle de impactos. */
	public static ExplosionResponse conDetalle(Explosion explosion) {
		return armar(explosion, explosion.getImpactos().stream().map(Afectado::desde).toList());
	}

	/** Respuesta del historial: lo que quedo guardado. */
	public static ExplosionResponse resumen(Explosion explosion) {
		return armar(explosion, null);
	}

	private static ExplosionResponse armar(Explosion explosion, List<Afectado> afectados) {
		return new ExplosionResponse(
				explosion.getId(),
				explosion.getCreeperId(),
				explosion.getJugadorObjetivoId(),
				explosion.getJugadorObjetivo(),
				explosion.getCentroX(),
				explosion.getCentroY(),
				explosion.getCentroZ(),
				explosion.getPotencia(),
				explosion.getRadio(),
				explosion.getMomento(),
				explosion.getEntidadesAlcanzadas(),
				explosion.getEntidadesMuertas(),
				explosion.getDanoTotal(),
				afectados);
	}
}
