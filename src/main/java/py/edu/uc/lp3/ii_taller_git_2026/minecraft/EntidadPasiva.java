package py.edu.uc.lp3.herencia;

/**
 * Representa una entidad pasiva que no ataca y puede huir de amenazas.
 */
public class EntidadPasiva extends Entidad {

    private final boolean domesticable;

    public EntidadPasiva(int salud,
                         double posicionX,
                         double posicionY,
                         double posicionZ,
                         int velocidad,
                         boolean domesticable) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad
        );

        this.domesticable = domesticable;
    }

    public void huir(Entidad amenaza) {

        if (amenaza == null) {
            throw new IllegalArgumentException(
                    "La amenaza no puede ser nula."
            );
        }

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Una entidad muerta no puede huir."
            );
        }

        System.out.println(
                "La entidad pasiva huye de la amenaza."
        );
    }

    public boolean isDomesticable() {
        return domesticable;
    }
}
