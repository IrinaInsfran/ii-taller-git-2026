package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Aldeano: entidad pasiva con una profesión que puede comerciar.
 */
public class Aldeano extends EntidadPasiva {

    private final String profesion;
    private boolean infectado;

    public Aldeano(int salud,
                   double posicionX,
                   double posicionY,
                   double posicionZ,
                   int velocidad,
                   boolean domesticable,
                   String profesion) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                domesticable
        );

        if (profesion == null || profesion.isBlank()) {
            throw new IllegalArgumentException(
                    "La profesión no puede estar vacía."
            );
        }

        this.profesion = profesion;
        this.infectado = false;
    }

    public void comercio() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un aldeano muerto no puede comerciar."
            );
        }

        if (infectado) {
            throw new IllegalStateException(
                    "Un aldeano infectado no puede comerciar."
            );
        }

        System.out.println(
                "El aldeano (" +
                profesion +
                ") ofrece comerciar."
        );
    }

    /**
     * Cambio de estado controlado desde el dominio.
     * Package-private para que lo use Zombie sin exponer un setter público.
     */
    void infectar() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un aldeano muerto no puede ser infectado."
            );
        }

        infectado = true;
    }

    public boolean isInfectado() {
        return infectado;
    }

    public String getProfesion() {
        return profesion;
    }
}
