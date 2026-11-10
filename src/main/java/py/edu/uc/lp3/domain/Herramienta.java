package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.HerramientaRotaException;

/**
 * Clase base de las herramientas que usa el jugador.
 * <p>
 * Toda herramienta tiene un material y una durabilidad que se gasta con el
 * uso; al llegar a 0 se rompe y ya no sirve. Es abstracta: no existe una
 * "herramienta generica" en el juego, cada subclase dice para que se usa.
 */
public abstract class Herramienta {

    private final Material material;
    private int durabilidad;

    protected Herramienta(Material material) {

        if (material == null) {
            throw new DatoInvalidoException("El material no puede ser nulo.");
        }

        this.material = material;
        this.durabilidad = material.getDurabilidad();
    }

    /**
     * Gasta usos de durabilidad, sin bajar de 0. Como en el juego, el uso
     * que deja la durabilidad en 0 se completa y recien ahi se rompe.
     */
    protected void desgastar(int usos) {

        if (usos <= 0) {
            throw new DatoInvalidoException("El desgaste debe ser mayor que cero.");
        }

        verificarQueNoEsteRota();
        durabilidad = Math.max(0, durabilidad - usos);
    }

    protected void verificarQueNoEsteRota() {

        if (estaRota()) {
            throw new HerramientaRotaException(
                    "La herramienta de " + material + " está rota y no se puede usar."
            );
        }
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
