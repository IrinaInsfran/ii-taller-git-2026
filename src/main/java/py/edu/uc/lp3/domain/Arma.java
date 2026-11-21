package py.edu.uc.lp3.domain;

/**
 * Algo con lo que se puede atacar a una entidad.
 * <p>
 * Es una interfaz y no una clase porque "ser un arma" es una capacidad que
 * cruza la jerarquía: la espada y el hacha son herramientas que además
 * sirven para pelear, pero el pico no. Toda arma es una {@link Amenaza}.
 * El golpe (alcance, desgaste, arma rota) lo aplica {@code AccionServiceImpl}.
 */
public interface Arma extends Amenaza {

    int getDanoAtaque();

    /** Cuánta durabilidad gasta cada golpe con esta arma. */
    int getDesgastePorGolpe();

    boolean estaRota();

    /** Gasta usos de durabilidad (lo implementa {@link Herramienta}). */
    void desgastar(int usos);

    /**
     * Un arma no sabe dónde está: el alcance lo pone quien la empuña. Hace
     * su daño completo si no está rota y el objetivo sigue vivo.
     */
    @Override
    default int danoContra(Entidad objetivo) {
        return objetivo == null || estaRota() || !objetivo.estaViva() ? 0 : getDanoAtaque();
    }
}
