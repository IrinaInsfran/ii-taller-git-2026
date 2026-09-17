package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Representa al jugador, que puede construir, craftear e interactuar con el mundo.
 */
public class Jugador extends Entidad {

    private final String nombre;
    private int nivelEXP;

    public Jugador(int salud, double posicionX, double posicionY, double posicionZ,
                   int velocidad, String nombre, int nivelEXP) {
        super(salud, posicionX, posicionY, posicionZ, velocidad);
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("el nombre es requerido");
        if (nivelEXP < 0) throw new IllegalArgumentException("el nivel de experiencia no puede ser negativo");
        this.nombre = nombre;
        this.nivelEXP = nivelEXP;
    }

    public String construir() {
        exigirViva();
        return nombre + " está construyendo una estructura.";
    }

    public String craftear() {
        exigirViva();
        return nombre + " está crafteando un objeto.";
    }

    public String interactuar() {
        exigirViva();
        return nombre + " interactúa con el mundo.";
    }

    /** La experiencia solo sube: reemplaza al setNivelEXP que aceptaba cualquier número. */
    public String ganarExperiencia(int puntos) {
        exigirViva();
        if (puntos <= 0) throw new IllegalArgumentException("los puntos de experiencia deben ser mayores a 0");
        nivelEXP += puntos;
        return nombre + " ahora tiene " + nivelEXP + " de experiencia.";
    }

    public String getNombre() { return nombre; }
    public int getNivelEXP() { return nivelEXP; }

    @Override
    public String detalle() {
        return "EXP " + nivelEXP;
    }
}
