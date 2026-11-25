package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Aldeano: entidad pasiva que comercia según su profesión. Si puede comerciar
 * o ser infectado lo decide {@code AccionServiceImpl}.
 */
@Entity
@DiscriminatorValue("ALDEANO")
public class Aldeano extends EntidadPasiva {

    private String profesion;
    private boolean infectado;

    /** Valores por defecto de un aldeano de Minecraft Java. */
    public static final int SALUD_POR_DEFECTO = 20;
    public static final int VELOCIDAD_POR_DEFECTO = 2;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Aldeano() {
    }

    /**
     * Constructor sobrecargado: un aldeano normal (no domesticable) con su
     * profesión. Delega en el constructor completo.
     */
    public Aldeano(double posicionX, double posicionY, double posicionZ, String profesion) {
        this(
                SALUD_POR_DEFECTO,
                posicionX,
                posicionY,
                posicionZ,
                VELOCIDAD_POR_DEFECTO,
                false,
                profesion
        );
    }

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

        this.profesion = profesion;
        this.infectado = false;
    }

    /** El aldeano queda infectado (lo pide un zombie, a través del servicio). */
    public void infectar() {
        infectado = true;
    }

    @Override
    protected void copiarEstadoDe(Entidad otra) {
        super.copiarEstadoDe(otra);
        Aldeano aldeano = (Aldeano) otra;
        profesion = aldeano.profesion;
        infectado = aldeano.infectado;
    }

    public boolean isInfectado() {
        return infectado;
    }

    public String getProfesion() {
        return profesion;
    }
    @Override
    public String describirComportamiento() {
        return "Puede comerciar según su profesión y huir de las amenazas.";
    }
}
