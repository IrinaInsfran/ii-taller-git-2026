package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.FueraDeAlcanceException;

/**
 * Entidad hostil: ataca al jugador u otras entidades.
 * <p>
 * Es abstracta: un "hostil generico" no describe un comportamiento concreto
 * del juego, asi que cada subclase debe decir como se comporta.
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

        if (!Double.isFinite(rangoDeteccion) || rangoDeteccion < 0) {
            throw new DatoInvalidoException(
                    "El rango de detección no puede ser negativo."
            );
        }

        if (danoAtaque <= 0) {
            throw new DatoInvalidoException(
                    "El daño de ataque debe ser mayor que cero."
            );
        }

        this.rangoDeteccion = rangoDeteccion;
        this.danoAtaque = danoAtaque;
    }

    public void atacar(Entidad objetivo) {

        if (objetivo == null) {
            throw new DatoInvalidoException("El objetivo no puede ser nulo.");
        }

        if (!estaViva()) {
            throw new EntidadMuertaException("Una entidad muerta no puede atacar.");
        }

        if (!objetivo.estaViva()) {
            throw new EntidadMuertaException(
                    "No se puede atacar a una entidad que ya está muerta."
            );
        }

        if (distanciaA(objetivo) > rangoDeteccion) {
            throw new FueraDeAlcanceException(
                    "El objetivo está fuera del rango de detección."
            );
        }

        objetivo.recibirDano(danoAtaque);
    }

    /**
     * Un mob hostil hace su daño de ataque a quien este dentro de su rango
     * de deteccion; fuera de ese rango, o si alguno esta muerto, no hace nada.
     */
    @Override
    public int danoContra(Entidad objetivo) {

        if (objetivo == null) {
            throw new DatoInvalidoException("El objetivo no puede ser nulo.");
        }

        if (objetivo == this || !estaViva() || !objetivo.estaViva()
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
