package py.edu.uc.lp3.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Representa al jugador, que puede construir, craftear e interactuar con el
 * mundo. Sus acciones con reglas (atacar con un arma, ganar experiencia) las
 * valida {@code AccionServiceImpl}.
 */
@Entity
@DiscriminatorValue("JUGADOR")
public class Jugador extends Entidad {

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
     * Constructor sobrecargado: un jugador nuevo en el punto de aparición
     * (spawn), con salud y velocidad por defecto y sin experiencia.
     */
    public Jugador(String nombre) {
        this(nombre, SPAWN_X, SPAWN_Y, SPAWN_Z);
    }

    /**
     * Constructor sobrecargado: un jugador nuevo en otra posición, con salud
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

        super(salud, posicionX, posicionY, posicionZ, velocidad);
        this.nombre = nombre;
        this.nivelEXP = nivelEXP;
    }

    /**
     * Suma experiencia. La cuenta se hace en {@code long} y se acota a
     * {@code [0, Integer.MAX_VALUE]}: el nivel nunca da la vuelta a negativo.
     * Que la cantidad sea positiva y que no se pase del máximo lo valida el
     * servicio, que en ese caso responde con un error en lugar de saturar.
     */
    public void ganarExperiencia(int cantidad) {
        nivelEXP = (int) Math.max(0, Math.min(Integer.MAX_VALUE, (long) nivelEXP + cantidad));
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
