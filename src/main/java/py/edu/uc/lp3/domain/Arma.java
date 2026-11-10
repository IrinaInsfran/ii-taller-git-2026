package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;

/**
 * Algo con lo que se puede atacar a una entidad.
 * <p>
 * Es una interfaz y no una clase porque "ser un arma" es una capacidad que
 * cruza la jerarquia: la espada y el hacha son herramientas que ademas
 * sirven para pelear, pero el pico no. Toda arma es una {@link Amenaza}.
 */
public interface Arma extends Amenaza {

    int getDanoAtaque();

    void atacar(Entidad objetivo);

    boolean estaRota();

    /**
     * Un arma no sabe donde esta: el alcance lo pone quien la empuña. Hace
     * su daño completo si no esta rota y el objetivo sigue vivo.
     */
    @Override
    default int danoContra(Entidad objetivo) {

        if (objetivo == null) {
            throw new DatoInvalidoException("El objetivo no puede ser nulo.");
        }

        return estaRota() || !objetivo.estaViva() ? 0 : getDanoAtaque();
    }
}
