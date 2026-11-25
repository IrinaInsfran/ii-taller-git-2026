package py.edu.uc.lp3.dto;

/**
 * Resultado de cruzar el portal: qué entidad salió de este mundo y con qué
 * id quedó guardada en el mundo de Ceci.
 */
public record PortalResponse(
		Long entidadId,
		TipoEntidad tipo,
		boolean conservadaEnEsteMundo,
		String mundoDestino,
		EntidadMundoCeci entidadEnDestino) {
}
