package py.edu.uc.lp3.dto;

/**
 * Datos que envia el cliente para crear o actualizar una entidad.
 * <p>
 * Los campos son wrappers (Integer, Double...) para poder distinguir un
 * campo ausente de un cero. Que campos son obligatorios depende del tipo
 * y lo decide la capa de servicio.
 */
public record EntidadRequest(
		TipoEntidad tipo,
		Integer salud,
		Double posicionX,
		Double posicionY,
		Double posicionZ,
		Integer velocidad,
		// Jugador
		String nombre,
		Integer nivelEXP,
		// Hostiles: Zombie, Esqueleto, Creeper
		Double rangoDeteccion,
		Integer danoAtaque,
		// Creeper
		Integer tiempoExplosion,
		// Pasivas: Aldeano, Animal
		Boolean domesticable,
		// Aldeano
		String profesion,
		// Animal
		Boolean montable) {
}
