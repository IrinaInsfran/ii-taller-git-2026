package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Creeper: entidad hostil que explota tras un tiempo de detonación.
 * Su ataque ES la explosión: daña al objetivo y el creeper muere.
 */
public class Creeper extends EntidadHostil {

    private final int tiempoExplosion;

    public Creeper(int salud, double posicionX, double posicionY, double posicionZ,
                   int velocidad, double rangoDeteccion, int danoAtaque, int tiempoExplosion) {
        super(salud, posicionX, posicionY, posicionZ, velocidad, rangoDeteccion, danoAtaque);
        if (tiempoExplosion <= 0) throw new IllegalArgumentException("el tiempo de explosión debe ser mayor a 0");
        this.tiempoExplosion = tiempoExplosion;
    }

    /** Redefine atacar: en un creeper, atacar es explotar. */
    @Override
    public String atacar(Entidad objetivo) {
        return explotar(objetivo);
    }

    public String explotar(Entidad objetivo) {
        String ataque = super.atacar(objetivo);          // reutiliza la regla del padre (rango, vida, daño)
        String autodestruccion = recibirDano(getSalud()); // y se autodestruye usando el mensaje, no el campo
        return "¡El creeper explota tras " + tiempoExplosion + " segundos! " + ataque + " " + autodestruccion;
    }

    @Override
    protected String alMorir() {
        return "El creeper desaparece tras la explosión.";
    }

    public int getTiempoExplosion() { return tiempoExplosion; }

    @Override
    public String detalle() {
        return super.detalle() + ", explota en " + tiempoExplosion + " s";
    }
}
