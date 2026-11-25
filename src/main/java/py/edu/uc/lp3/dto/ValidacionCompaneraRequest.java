package py.edu.uc.lp3.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * El cuerpo que el escenario les manda a las dos compañeras. Es uno solo
 * porque sus contratos se solapan: Ceci (A2) lee {@code saludCreeper} y
 * {@code yaDetonado}; Sol (A3) lee los tiempos, el radio, el creeper y los
 * objetivos. Cada una ignora los campos que no usa.
 */
@Schema(description = "Lo que el escenario les manda a A2 y A3 en /explosiones/validar")
public record ValidacionCompaneraRequest(
		@Schema(description = "Salud del creeper (regla 1 de Ceci)", example = "20")
		int saludCreeper,
		@Schema(description = "Si ese creeper ya tiene una explosión guardada (regla 2 de Ceci)", example = "false")
		boolean yaDetonado,
		@Schema(description = "Tiempo de mecha del creeper (regla 1 de Sol)", example = "30")
		int tiempoExplosion,
		@Schema(description = "Tiempo transcurrido desde que se encendió la mecha (regla 1 de Sol)", example = "30")
		int tiempoTranscurrido,
		@Schema(description = "Radio de la explosión en bloques (regla 2 de Sol)", example = "6")
		double radioExplosion,
		MechaCortaRequest.Posicion creeper,
		@Schema(description = "Entidades vivas dentro del radio (regla 2 de Sol)")
		List<MechaCortaRequest.Objetivo> objetivos) {
}
