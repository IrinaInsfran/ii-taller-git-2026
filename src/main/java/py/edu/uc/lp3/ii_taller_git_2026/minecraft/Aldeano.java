package py.edu.uc.lp3.herencia;

/**
 * Aldeano: entidad pasiva con una profesión que puede comerciar.
 */
public class Aldeano extends EntidadPasiva {

    private final String profesion;

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
    }

    public void comercio() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un aldeano muerto no puede comerciar."
            );
        }

        System.out.println(
                "El aldeano (" +
                profesion +
                ") ofrece comerciar."
        );
    }

    public String getProfesion() {
        return profesion;
    }
}
