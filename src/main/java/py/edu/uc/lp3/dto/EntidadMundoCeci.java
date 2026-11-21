package py.edu.uc.lp3.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Una entidad tal como la entiende la API Minecraft de Ceci
 * ({@code POST /entidades}): {@code tipo} es "Jugador", "Zombie" o "Creeper"
 * y cada tipo agrega sus campos (nivel y experiencia para Jugador,
 * detonado para Creeper). {@code id} lo asigna esa API al guardarla.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EntidadMundoCeci(
		Long id,
		String tipo,
		String nombre,
		int vida,
		int vidaMaxima,
		double x,
		double y,
		double z,
		Integer nivel,
		Integer experiencia,
		Boolean detonado) {
}
