package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Esqueleto: entidad hostil que ataca a distancia.
 */
@Entity
@DiscriminatorValue("ESQUELETO")
public class Esqueleto extends EntidadHostil {

    /** Valores por defecto de un esqueleto normal de Minecraft Java. */
    public static final int SALUD_POR_DEFECTO = 20;
    public static final int VELOCIDAD_POR_DEFECTO = 2;
    public static final double RANGO_DETECCION_POR_DEFECTO = 16;
    public static final int DANO_ATAQUE_POR_DEFECTO = 4;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Esqueleto() {
    }

    /**
     * Constructor sobrecargado: un esqueleto normal en esa posición. Delega
     * en el constructor completo.
     */
    public Esqueleto(double posicionX, double posicionY, double posicionZ) {
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

    @Override
    public String describirComportamiento() {
        return "Mantiene la distancia y dispara flechas para dañar a su objetivo.";
    }
}
