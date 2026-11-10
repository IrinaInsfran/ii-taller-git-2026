package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

/**
 * Aldeano: entidad pasiva que realiza comercio.
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
     * profesion. Delega en el constructor completo, que valida la profesion.
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

        if (profesion == null || profesion.isBlank()) {
            throw new DatoInvalidoException(
                    "La profesión no puede estar vacía."
            );
        }

        this.profesion = profesion;
        this.infectado = false;
    }

    public void comercio() {

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un aldeano muerto no puede comerciar."
            );
        }

        if (infectado) {
            throw new ReglaDelJuegoException(
                    "aldeano_infectado",
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
            throw new EntidadMuertaException(
                    "Un aldeano muerto no puede ser infectado."
            );
        }

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
