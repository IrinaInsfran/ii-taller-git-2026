package py.edu.uc.lp3.herencia;

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

    public void infectaAldeano() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un Zombie muerto no puede infectar aldeanos."
            );
        }

        System.out.println(
                "El zombie infecta a un aldeano cercano."
        );
    }
}
