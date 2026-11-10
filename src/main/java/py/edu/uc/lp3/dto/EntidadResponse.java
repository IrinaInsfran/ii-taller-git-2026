package py.edu.uc.lp3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import py.edu.uc.lp3.domain.Aldeano;
import py.edu.uc.lp3.domain.Animal;
import py.edu.uc.lp3.domain.Creeper;
import py.edu.uc.lp3.domain.Entidad;
import py.edu.uc.lp3.domain.EntidadHostil;
import py.edu.uc.lp3.domain.EntidadPasiva;
import py.edu.uc.lp3.domain.Jugador;

/**
 * Lo que la API devuelve de una entidad. Los campos que no aplican al tipo
 * quedan en null y no se serializan.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EntidadResponse(
		Long id,
		TipoEntidad tipo,
		int salud,
		int saludMaxima,
		double posicionX,
		double posicionY,
		double posicionZ,
		int velocidad,
		boolean viva,
		String comportamiento,
		String nombre,
		Integer nivelEXP,
		Double rangoDeteccion,
		Integer danoAtaque,
		Integer tiempoExplosion,
		Boolean domesticable,
		String profesion,
		Boolean infectado,
		Boolean montable) {

	public static EntidadResponse desde(Entidad entidad) {
		Jugador jugador = entidad instanceof Jugador j ? j : null;
		EntidadHostil hostil = entidad instanceof EntidadHostil h ? h : null;
		Creeper creeper = entidad instanceof Creeper c ? c : null;
		EntidadPasiva pasiva = entidad instanceof EntidadPasiva p ? p : null;
		Aldeano aldeano = entidad instanceof Aldeano a ? a : null;
		Animal animal = entidad instanceof Animal a ? a : null;

		return new EntidadResponse(
				entidad.getId(),
				TipoEntidad.de(entidad),
				entidad.getSalud(),
				entidad.getSaludMaxima(),
				entidad.getPosicionX(),
				entidad.getPosicionY(),
				entidad.getPosicionZ(),
				entidad.getVelocidad(),
				entidad.estaViva(),
				entidad.describirComportamiento(),
				jugador == null ? null : jugador.getNombre(),
				jugador == null ? null : jugador.getNivelEXP(),
				hostil == null ? null : hostil.getRangoDeteccion(),
				hostil == null ? null : hostil.getDanoAtaque(),
				creeper == null ? null : creeper.getTiempoExplosion(),
				pasiva == null ? null : pasiva.isDomesticable(),
				aldeano == null ? null : aldeano.getProfesion(),
				aldeano == null ? null : aldeano.isInfectado(),
				animal == null ? null : animal.isMontable());
	}
}
