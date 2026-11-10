package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

/**
 * Representa al jugador, que puede construir,
 * craftear e interactuar con el mundo.
 */
@Entity
@DiscriminatorValue("JUGADOR")
public class Jugador extends Entidad {

    /** Alcance del ataque cuerpo a cuerpo del jugador en Minecraft Java. */
    public static final double ALCANCE_ATAQUE = 3;

    private String nombre;
    private int nivelEXP;

    /** Valores con los que aparece un jugador nuevo en Minecraft Java. */
    public static final int SALUD_POR_DEFECTO = 20;
    public static final int VELOCIDAD_POR_DEFECTO = 4;
    public static final double SPAWN_X = 0;
    public static final double SPAWN_Y = 64;
    public static final double SPAWN_Z = 0;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected Jugador() {
    }

    /**
     * Constructor sobrecargado: un jugador nuevo en el punto de aparicion
     * (spawn), con salud y velocidad por defecto y sin experiencia.
     */
    public Jugador(String nombre) {
        this(nombre, SPAWN_X, SPAWN_Y, SPAWN_Z);
    }

    /**
     * Constructor sobrecargado: un jugador nuevo en otra posicion, con salud
     * y velocidad por defecto y sin experiencia. Delega en el completo.
     */
    public Jugador(String nombre, double posicionX, double posicionY, double posicionZ) {
        this(
                SALUD_POR_DEFECTO,
                posicionX,
                posicionY,
                posicionZ,
                VELOCIDAD_POR_DEFECTO,
                nombre,
                0
        );
    }

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
            throw new DatoInvalidoException(
                    "El nombre del jugador no puede estar vacío."
            );
        }

        if (nivelEXP < 0) {
            throw new DatoInvalidoException(
                    "El nivel de experiencia no puede ser negativo."
            );
        }

        this.nombre = nombre;
        this.nivelEXP = nivelEXP;
    }

    public void construir() {

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un jugador muerto no puede construir."
            );
        }

        System.out.println(nombre + " está construyendo una estructura.");
    }

    public void craftear() {

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un jugador muerto no puede craftear."
            );
        }

        System.out.println(nombre + " está crafteando un objeto.");
    }

    public void interactuar() {

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un jugador muerto no puede interactuar."
            );
        }

        System.out.println(nombre + " interactúa con el mundo.");
    }

    /**
     * Ataca con un arma (espada, hacha...) a una entidad a su alcance.
     * El daño y el desgaste los decide el arma.
     */
    public void atacar(Entidad objetivo, Arma arma) {

        if (objetivo == null || arma == null) {
            throw new DatoInvalidoException(
                    "El objetivo y el arma no pueden ser nulos."
            );
        }

        if (objetivo == this) {
            throw new ReglaDelJuegoException(
                    "auto_ataque",
                    "Un jugador no puede atacarse a sí mismo."
            );
        }

        if (!estaViva()) {
            throw new EntidadMuertaException(
                    "Un jugador muerto no puede atacar."
            );
        }

        if (distanciaA(objetivo) > ALCANCE_ATAQUE) {
            throw new FueraDeAlcanceException(
                    "El objetivo está fuera del alcance del jugador."
            );
        }

        arma.atacar(objetivo);
    }

    /**
     * Suma experiencia al jugador.
     * <p>
     * Se usa {@link Math#addExact(int, int)} para detectar el
     * desbordamiento: si {@code nivelEXP + cantidad} supera
     * {@link Integer#MAX_VALUE} se lanza {@link DatoInvalidoException}
     * en lugar de dar la vuelta y dejar el nivel en negativo. El nivel
     * nunca se satura, porque un nivel negativo seria peor que un error.
     *
     * @param cantidad experiencia ganada, mayor que cero
     * @throws DatoInvalidoException si la cantidad no es positiva o la
     *                               suma desborda el rango de int
     */
    public void ganarExperiencia(int cantidad) {

        if (cantidad <= 0) {
            throw new DatoInvalidoException(
                    "La experiencia ganada debe ser mayor que cero."
            );
        }

        try {
            nivelEXP = Math.addExact(nivelEXP, cantidad);
        } catch (ArithmeticException overflow) {
            throw new DatoInvalidoException(
                    "La experiencia total supera el máximo permitido."
            );
        }
    }

    @Override
    protected void copiarEstadoDe(Entidad otra) {
        super.copiarEstadoDe(otra);
        Jugador jugador = (Jugador) otra;
        nombre = jugador.nombre;
        nivelEXP = jugador.nivelEXP;
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
