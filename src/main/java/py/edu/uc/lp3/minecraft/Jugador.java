package py.edu.uc.lp3.minecraft;

/**
 * Representa al jugador, que puede construir,
 * craftear e interactuar con el mundo.
 */
public class Jugador extends Entidad {

    private final String nombre;
    private int nivelEXP;

    public Jugador(int salud,
                   double posicionX,
                   double posicionY,
                   double posicionZ,
                   int velocidad,
                   String nombre,
                   int nivelEXP) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad
        );

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del jugador no puede estar vacío."
            );
        }

        if (nivelEXP < 0) {
            throw new IllegalArgumentException(
                    "El nivel de experiencia no puede ser negativo."
            );
        }

        this.nombre = nombre;
        this.nivelEXP = nivelEXP;
    }

    public void construir() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un jugador muerto no puede construir."
            );
        }

        System.out.println(nombre + " está construyendo una estructura.");
    }

    public void craftear() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un jugador muerto no puede craftear."
            );
        }

        System.out.println(nombre + " está crafteando un objeto.");
    }

    public void interactuar() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un jugador muerto no puede interactuar."
            );
        }

        System.out.println(nombre + " interactúa con el mundo.");
    }

    /**
     * Suma experiencia al jugador.
     * <p>
     * Se usa {@link Math#addExact(int, int)} para detectar el
     * desbordamiento: si {@code nivelEXP + cantidad} supera
     * {@link Integer#MAX_VALUE} se lanza {@link IllegalArgumentException}
     * en lugar de dar la vuelta y dejar el nivel en negativo. El nivel
     * nunca se satura, porque un nivel negativo seria peor que un error.
     *
     * @param cantidad experiencia ganada, mayor que cero
     * @throws IllegalArgumentException si la cantidad no es positiva o
     *                                  la suma desborda el rango de int
     */
    public void ganarExperiencia(int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La experiencia ganada debe ser mayor que cero."
            );
        }

        try {
            nivelEXP = Math.addExact(nivelEXP, cantidad);
        } catch (ArithmeticException overflow) {
            throw new IllegalArgumentException(
                    "La experiencia total supera el máximo permitido."
            );
        }
    }

    public String getNombre() {
        return nombre;
    }

    public int getNivelEXP() {
        return nivelEXP;
    }
    @Override
    public String describirComportamiento() {
    	return "Construye, craftea e interactúa con el mundo.";
}
}
