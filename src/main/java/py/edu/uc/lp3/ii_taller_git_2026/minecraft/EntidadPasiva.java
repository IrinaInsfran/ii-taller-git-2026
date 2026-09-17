package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Representa una entidad pasiva que no ataca y puede huir de amenazas.
 */
public abstract class EntidadPasiva extends Entidad {

    private final boolean domesticable;
    private boolean domesticada;

    protected EntidadPasiva(int salud, double posicionX, double posicionY, double posicionZ,
                            int velocidad, boolean domesticable) {
        super(salud, posicionX, posicionY, posicionZ, velocidad);
        this.domesticable = domesticable;
    }

    /** Se aleja de la amenaza (en el plano X-Z) tanto como le permite su velocidad. */
    public String huir(Entidad amenaza) {
        exigirViva();
        if (amenaza == null) throw new IllegalArgumentException("la amenaza es requerida");
        if (amenaza == this) throw new IllegalArgumentException(getTipo() + " no puede huir de sí misma");
        double dx = getPosicionX() - amenaza.getPosicionX();
        double dz = getPosicionZ() - amenaza.getPosicionZ();
        double distancia = Math.hypot(dx, dz);
        if (distancia == 0) {
            dx = 1;
            dz = 0;
            distancia = 1;
        }
        mover(dx / distancia * getVelocidad(), 0, dz / distancia * getVelocidad());
        return getTipo() + " huye de " + amenaza.getTipo() + " hasta " + getPosicion();
    }

    public String domesticar(Jugador jugador) {
        exigirViva();
        if (jugador == null || !jugador.estaViva()) throw new IllegalArgumentException("se necesita un jugador vivo");
        if (!domesticable) throw new IllegalStateException(getTipo() + " no es domesticable");
        if (domesticada) return getTipo() + " ya estaba domesticada.";
        domesticada = true;
        return jugador.getNombre() + " domesticó a " + getTipo() + ".";
    }

    public boolean isDomesticable() { return domesticable; }
    public boolean isDomesticada() { return domesticada; }

    @Override
    public String detalle() {
        return domesticable ? (domesticada ? "domesticada" : "domesticable") : "no domesticable";
    }
}
