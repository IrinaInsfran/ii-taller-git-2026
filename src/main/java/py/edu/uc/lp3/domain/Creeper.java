package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Creeper: entidad hostil que explota tras un tiempo de detonación.
 * <p>
 * Participa en las dos reglas de la simulación, que aplica
 * {@code ReglasCreeperServiceImpl} en la capa de servicio:
 * <ol>
 *   <li><b>Mecha corta:</b> solo enciende la mecha si hay un jugador vivo a
 *       3 bloques o menos, y apunta al más cercano;</li>
 *   <li><b>Onda expansiva:</b> al explotar muere, y la explosión daña a cada
 *       entidad viva de su radio, más cuanto más cerca esté.</li>
 * </ol>
 */
@Entity
@DiscriminatorValue("CREEPER")
public class Creeper extends EntidadHostil {

    /** Valores por defecto de un creeper "normal" del juego. */
    public static final int SALUD_POR_DEFECTO = 20;
    public static final int VELOCIDAD_POR_DEFECTO = 1;
    public static final double RANGO_DETECCION_POR_DEFECTO = 16;
    public static final int DANO_ATAQUE_POR_DEFECTO = 3;
    public static final int TIEMPO_EXPLOSION_POR_DEFECTO = 30;

    /** Potencia de la explosión de un creeper normal en Minecraft. */
    public static final double POTENCIA_EXPLOSION = 3;

    private int tiempoExplosion;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Creeper() {
    }

    /**
     * Constructor sobrecargado: crea un creeper con los valores por defecto
     * del juego y solo recibe su posición. Delega en el constructor
     * completo con this(...).
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

        super(salud, posicionX, posicionY, posicionZ, velocidad, rangoDeteccion, danoAtaque);
        this.tiempoExplosion = tiempoExplosion;
    }

    @Override
    public String describirAmenaza() {
        return "Creeper: " + getDanoAtaque() + " de daño a quien esté a "
                + getRangoDeteccion() + " bloques o menos, y explota con potencia "
                + POTENCIA_EXPLOSION + " si un jugador se le acerca.";
    }

    @Override
    protected void copiarEstadoDe(Entidad otra) {
        super.copiarEstadoDe(otra);
        tiempoExplosion = ((Creeper) otra).tiempoExplosion;
    }

    public int getTiempoExplosion() {
        return tiempoExplosion;
    }

    @Override
    public String describirComportamiento() {
        return "Se aproxima a su objetivo y explota después de su tiempo de detonación.";
    }
}
