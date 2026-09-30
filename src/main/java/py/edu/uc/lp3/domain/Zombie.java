package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Zombie: entidad hostil que puede infectar aldeanos.
 */
public class Zombie extends EntidadHostil {

    public Zombie(int salud,
                  double posicionX,
                  double posicionY,
                  double posicionZ,
                  int velocidad,
                  double rangoDeteccion,
                  int danoAtaque) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                rangoDeteccion,
                danoAtaque
        );
    }

    public void infectaAldeano(Aldeano aldeano) {

        if (aldeano == null) {
            throw new IllegalArgumentException("El aldeano no puede ser nulo.");
        }

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un Zombie muerto no puede infectar aldeanos."
            );
        }

        if (!aldeano.estaViva()) {
            throw new IllegalStateException(
                    "No se puede infectar a un aldeano muerto."
            );
        }

        if (distanciaA(aldeano) > getRangoDeteccion()) {
            throw new IllegalStateException(
                    "El aldeano está fuera del rango de detección."
            );
        }

        aldeano.infectar();
    }
}
