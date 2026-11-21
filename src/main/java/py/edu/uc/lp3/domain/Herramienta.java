package py.edu.uc.lp3.domain;

/**
 * Clase base de las herramientas que usa el jugador.
 * <p>
 * Toda herramienta tiene un material y una durabilidad que se gasta con el
 * uso; al llegar a 0 se rompe y ya no sirve. Es abstracta: no existe una
 * "herramienta genérica" en el juego, cada subclase dice para qué se usa.
 * Cuánto se gasta en cada uso y qué pasa si está rota lo decide
 * {@code HerramientaServiceImpl}.
 */
public abstract class Herramienta {

    private final Material material;
    private int durabilidad;

    protected Herramienta(Material material) {
        this.material = material;
        this.durabilidad = material.getDurabilidad();
    }

    /** Gasta usos de durabilidad, sin bajar de 0. */
    public void desgastar(int usos) {
        durabilidad = Math.max(0, durabilidad - Math.max(0, usos));
    }

    public boolean estaRota() {
        return durabilidad == 0;
    }

    public Material getMaterial() {
        return material;
    }

    public int getDurabilidad() {
        return durabilidad;
    }

    public int getDurabilidadMaxima() {
        return material.getDurabilidad();
    }

    public abstract String describirUso();
}
