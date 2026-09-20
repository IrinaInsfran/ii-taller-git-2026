package py.edu.uc.lp3.herencia;

/**
 * Esqueleto: entidad hostil que ataca disparando flechas.
 */
public class Esqueleto extends EntidadHostil {

    public Esqueleto(int salud,
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

    public void dispararFlecha() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un Esqueleto muerto no puede disparar."
            );
        }

        System.out.println(
                "El esqueleto dispara una flecha."
        );
    }
}
