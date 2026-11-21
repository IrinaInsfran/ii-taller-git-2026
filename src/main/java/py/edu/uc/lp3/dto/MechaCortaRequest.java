package py.edu.uc.lp3.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Pedido para consultar la regla «mecha corta» sin tocar la base. Usa el
 * mismo formato que el {@code /explosiones/validar} de Sol, así otro
 * servicio puede reutilizar su cuerpo: campos como {@code tiempoExplosion} o
 * {@code radioExplosion} se aceptan y se ignoran.
 */
@Schema(description = "Creeper y objetivos a evaluar con la regla «mecha corta». No se lee ni se guarda nada en H2.")
public record MechaCortaRequest(
		@Schema(description = "Salud del creeper; si falta se lo considera vivo. Con 0 o menos está muerto.",
				example = "20", nullable = true)
		Integer saludCreeper,
		@Schema(description = "Posición del creeper", requiredMode = Schema.RequiredMode.REQUIRED)
		Posicion creeper,
		@Schema(description = "Entidades alrededor; solo cuentan las de tipo «jugador». Al menos una.",
				requiredMode = Schema.RequiredMode.REQUIRED)
		List<Objetivo> objetivos) {

	@Schema(description = "Posición en bloques")
	public record Posicion(
			@Schema(example = "0") Double x,
			@Schema(example = "64") Double y,
			@Schema(example = "0") Double z) {
	}

	@Schema(description = "Entidad cercana al creeper")
	public record Objetivo(
			@Schema(description = "Tipo de la entidad; la regla solo mira los de tipo «jugador»",
					example = "jugador")
			String tipo,
			@Schema(description = "Salud actual; si falta se considera 20. Con 0 o menos está muerto.",
					example = "20", nullable = true)
			Integer salud,
			@Schema(description = "Posición de la entidad")
			Posicion posicion) {
	}
}
