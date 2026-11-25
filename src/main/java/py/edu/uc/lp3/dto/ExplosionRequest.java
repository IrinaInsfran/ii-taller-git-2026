package py.edu.uc.lp3.dto;

/**
 * Pedido de simulación: qué creeper intenta explotar. El resto (a quién
 * apunta, a quiénes daña) lo deciden las reglas de la capa de servicio.
 */
public record ExplosionRequest(Long creeperId) {
}
