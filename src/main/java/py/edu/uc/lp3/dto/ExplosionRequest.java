package py.edu.uc.lp3.dto;

/**
 * Pedido de simulacion: que creeper intenta explotar. El resto (a quien
 * apunta, a quienes daña) lo deciden las reglas del dominio.
 */
public record ExplosionRequest(Long creeperId) {
}
