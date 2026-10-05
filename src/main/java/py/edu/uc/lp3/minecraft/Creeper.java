package py.edu.uc.lp3.minecraft;

/**
 * Creeper: entidad hostil que explota tras un tiempo de detonación.
 */
public class Creeper extends EntidadHostil {

    /** Valores por defecto de un creeper "normal" del juego. */
    public static final int SALUD_POR_DEFECTO = 20;
    public static final int VELOCIDAD_POR_DEFECTO = 1;
    public static final double RANGO_DETECCION_POR_DEFECTO = 16;
    public static final int DANO_ATAQUE_POR_DEFECTO = 3;
    public static final int TIEMPO_EXPLOSION_POR_DEFECTO = 30;

    private final int tiempoExplosion;

    /**
     * Constructor sobrecargado: crea un creeper con los valores por defecto
     * del juego y solo recibe su posicion. Delega en el constructor
     * completo con this(...) para no duplicar la validacion.
     */
    public Creeper(double posicionX, double posicionY, double posicionZ) {
        this(
                SALUD_POR_DEFECTO,
                posicionX,
                posicionY,
                posicionZ,
                VELOCIDAD_POR_DEFECTO,
                RANGO_DETECCION_POR_DEFECTO,
                DANO_ATAQUE_POR_DEFECTO,
                TIEMPO_EXPLOSION_POR_DEFECTO
        );
    }

    public Creeper(int salud,
                   double posicionX,
                   double posicionY,
                   double posicionZ,
                   int velocidad,
                   double rangoDeteccion,
                   int danoAtaque,
                   int tiempoExplosion) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                rangoDeteccion,
                danoAtaque
        );

        if (tiempoExplosion <= 0) {
            throw new IllegalArgumentException(
                    "El tiempo de explosión debe ser mayor que cero."
            );
        }

        this.tiempoExplosion = tiempoExplosion;
    }

    @Override
    public void atacar(Entidad objetivo) {
        super.atacar(objetivo);
        explotar();
    }

    public void explotar() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un Creeper muerto no puede explotar."
            );
        }

        System.out.println(
                "¡El creeper explota tras " +
                tiempoExplosion +
                " segundos!"
        );

        morir();
    }

    public int getTiempoExplosion() {
        return tiempoExplosion;
    }
    @Override
    public String describirComportamiento() {
    	return "Se aproxima a su objetivo y explota después de su tiempo de detonación.";
}
}
