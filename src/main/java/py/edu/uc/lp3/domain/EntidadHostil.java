package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

/**
 * Entidad hostil: ataca al jugador u otras entidades.
 * <p>
 * Es abstracta: un "hostil genérico" no describe un comportamiento concreto
 * del juego, así que cada subclase debe decir cómo se comporta. Cómo ataca
 * (rango, muertos, daño aplicado) lo decide {@code AccionServiceImpl}.
 */
@Entity
public abstract class EntidadHostil extends Entidad implements Amenaza {

    private double rangoDeteccion;
    private int danoAtaque;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected EntidadHostil() {
    }

    public EntidadHostil(int salud,
                         double posicionX,
                         double posicionY,
                         double posicionZ,
                         int velocidad,
                         double rangoDeteccion,
                         int danoAtaque) {

        super(salud, posicionX, posicionY, posicionZ, velocidad);
        this.rangoDeteccion = rangoDeteccion;
        this.danoAtaque = danoAtaque;
    }

    /**
     * Un mob hostil hace su daño de ataque a quien esté dentro de su rango
     * de detección; fuera de ese rango, o si alguno está muerto, el daño es 0.
     */
    @Override
    public int danoContra(Entidad objetivo) {
        if (objetivo == null || objetivo == this || !estaViva() || !objetivo.estaViva()
                || distanciaA(objetivo) > rangoDeteccion) {
            return 0;
        }

        return danoAtaque;
    }

    @Override
    public String describirAmenaza() {
        return getClass().getSimpleName() + ": " + danoAtaque
                + " de daño a quien esté a " + rangoDeteccion + " bloques o menos.";
    }

    @Override
    protected void copiarEstadoDe(Entidad otra) {
        super.copiarEstadoDe(otra);
        EntidadHostil hostil = (EntidadHostil) otra;
        rangoDeteccion = hostil.rangoDeteccion;
        danoAtaque = hostil.danoAtaque;
    }

    public double getRangoDeteccion() {
        return rangoDeteccion;
    }

    public int getDanoAtaque() {
        return danoAtaque;
    }

    @Override
    public abstract String describirComportamiento();
}
