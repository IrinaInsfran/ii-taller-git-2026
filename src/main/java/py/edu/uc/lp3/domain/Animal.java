package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Animal: entidad pasiva que puede ser montable.
 */
public class Animal extends EntidadPasiva {

    private final boolean montable;

    public Animal(int salud,
                  double posicionX,
                  double posicionY,
                  double posicionZ,
                  int velocidad,
                  boolean domesticable,
                  boolean montable) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                domesticable
        );

        this.montable = montable;
    }

    public void montar(Jugador jugador) {

        if (jugador == null) {
            throw new IllegalArgumentException(
                    "El jugador no puede ser nulo."
            );
        }

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un animal muerto no puede ser montado."
            );
        }

        if (!jugador.estaViva()) {
            throw new IllegalStateException(
                    "Un jugador muerto no puede montar."
            );
        }

        if (!montable) {
            throw new IllegalStateException(
                    "Este animal no es montable."
            );
        }

        System.out.println(
                jugador.getNombre() + " monta al animal."
        );
    }

    public boolean isMontable() {
        return montable;
    }

    // Se conserva por compatibilidad con el nombre usado anteriormente.
    public boolean isMontar() {
        return montable;
    }
}
