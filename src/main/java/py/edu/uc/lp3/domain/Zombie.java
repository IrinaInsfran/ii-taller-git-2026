package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Zombie: entidad hostil que persigue al jugador e infecta aldeanos (la
 * infección la valida {@code AccionServiceImpl}).
 */
@Entity
@DiscriminatorValue("ZOMBIE")
public class Zombie extends EntidadHostil {

    /** Valores por defecto de un zombie normal de Minecraft Java. */
    public static final int SALUD_POR_DEFECTO = 20;
    public static final int VELOCIDAD_POR_DEFECTO = 2;
    public static final double RANGO_DETECCION_POR_DEFECTO = 35;
    public static final int DANO_ATAQUE_POR_DEFECTO = 3;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Zombie() {
    }

    /**
     * Constructor sobrecargado: un zombie normal en esa posición. Delega en
     * el constructor completo.
     */
    public Zombie(double posicionX, double posicionY, double posicionZ) {
        this(
                SALUD_POR_DEFECTO,
                posicionX,
                posicionY,
                posicionZ,
                VELOCIDAD_POR_DEFECTO,
                RANGO_DETECCION_POR_DEFECTO,
                DANO_ATAQUE_POR_DEFECTO
        );
    }

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

    @Override
    public String describirComportamiento() {
        return "Persigue al jugador dentro de su rango e infecta a los aldeanos.";
    }
}
