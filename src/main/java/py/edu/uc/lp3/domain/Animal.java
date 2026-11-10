package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

/**
 * Animal: entidad pasiva que puede ser montable.
 */
@Entity
@DiscriminatorValue("ANIMAL")
public class Animal extends EntidadPasiva {

    private boolean montable;

    /** Valores por defecto de un animal de granja (una vaca) en Minecraft Java. */
    public static final int SALUD_POR_DEFECTO = 10;
    public static final int VELOCIDAD_POR_DEFECTO = 2;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Animal() {
    }

    /**
     * Constructor sobrecargado: un animal de granja normal, ni domesticable
     * ni montable. Delega en el constructor completo.
     */
    public Animal(double posicionX, double posicionY, double posicionZ) {
        this(
                SALUD_POR_DEFECTO,
                posicionX,
                posicionY,
                posicionZ,
                VELOCIDAD_POR_DEFECTO,
                false,
                false
        );
    }

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
            throw new DatoInvalidoException(
                    "El jugador no puede ser nulo."
            );
        }

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un animal muerto no puede ser montado."
            );
        }

        if (!jugador.estaViva()) {
            throw new EntidadMuertaException(
                    "Un jugador muerto no puede montar."
            );
        }

        if (!montable) {
            throw new ReglaDelJuegoException(
                    "animal_no_montable",
                    "Este animal no es montable."
            );
        }

        System.out.println(
                jugador.getNombre() + " monta al animal."
        );
    }

    @Override
    protected void copiarEstadoDe(Entidad otra) {
        super.copiarEstadoDe(otra);
        montable = ((Animal) otra).montable;
    }

    public boolean isMontable() {
        return montable;
    }

    // Se conserva por compatibilidad con el nombre usado anteriormente.
    public boolean isMontar() {
        return montable;
    }

    @Override
    public String describirComportamiento() {
        return "Huye del peligro y puede ser montado por el jugador.";
    }
}
