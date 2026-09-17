package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Entidad hostil: ataca al jugador u otras entidades que estén dentro de su rango de detección.
 */
public abstract class EntidadHostil extends Entidad {

    private final double rangoDeteccion;
    private final int danoAtaque;

    protected EntidadHostil(int salud, double posicionX, double posicionY, double posicionZ,
                            int velocidad, double rangoDeteccion, int danoAtaque) {
        super(salud, posicionX, posicionY, posicionZ, velocidad);
        if (rangoDeteccion <= 0) throw new IllegalArgumentException("el rango de detección debe ser mayor a 0");
        if (danoAtaque < 0) throw new IllegalArgumentException("el daño de ataque no puede ser negativo");
        this.rangoDeteccion = rangoDeteccion;
        this.danoAtaque = danoAtaque;
    }

    public boolean detecta(Entidad objetivo) {
        return objetivo != null && objetivo.estaViva() && distanciaA(objetivo) <= rangoDeteccion;
    }

    public String atacar(Entidad objetivo) {
        exigirViva();
        if (objetivo == null) throw new IllegalArgumentException("el objetivo es requerido");
        if (objetivo == this) throw new IllegalArgumentException(getTipo() + " no puede atacarse a sí misma");
        if (!objetivo.estaViva()) throw new IllegalStateException(objetivo.getTipo() + " ya no tiene salud");
        if (!detecta(objetivo)) {
            throw new IllegalStateException(objetivo.getTipo() + " está fuera del rango de detección de " + getTipo());
        }
        return getTipo() + " ataca a " + objetivo.getTipo() + " con " + danoAtaque + " de daño. "
                + objetivo.recibirDano(danoAtaque);
    }

    public double getRangoDeteccion() { return rangoDeteccion; }
    public int getDanoAtaque() { return danoAtaque; }

    @Override
    public String detalle() {
        return "rango " + rangoDeteccion + ", daño " + danoAtaque;
    }
}
