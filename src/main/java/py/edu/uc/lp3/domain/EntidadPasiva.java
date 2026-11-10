package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;
import py.edu.uc.lp3.exceptions.EntidadMuertaException;
import py.edu.uc.lp3.exceptions.ReglaDelJuegoException;

/**
 * Representa una entidad pasiva que no ataca y puede huir de amenazas.
 * <p>
 * Es abstracta: una "pasiva generica" no describe un comportamiento concreto
 * del juego, asi que cada subclase debe decir como se comporta.
 */
@Entity
public abstract class EntidadPasiva extends Entidad {

    private boolean domesticable;

    /** Constructor requerido por JPA para reconstruir la entidad desde la base. */
    protected EntidadPasiva() {
    }

    public EntidadPasiva(int salud,
                         double posicionX,
                         double posicionY,
                         double posicionZ,
                         int velocidad,
                         boolean domesticable) {

        super(salud, posicionX, posicionY, posicionZ, velocidad);
        this.domesticable = domesticable;
    }

    public void huir(Entidad amenaza) {

        if (amenaza == null) {
            throw new DatoInvalidoException("La amenaza no puede ser nula.");
        }

        if (!estaViva()) {
            throw new EntidadMuertaException("Una entidad muerta no puede huir.");
        }

        double dx = getPosicionX() - amenaza.getPosicionX();
        double dy = getPosicionY() - amenaza.getPosicionY();
        double dz = getPosicionZ() - amenaza.getPosicionZ();

        double distancia = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (getVelocidad() == 0) {
            throw new ReglaDelJuegoException("sin_velocidad", "La entidad no tiene velocidad para huir.");
        }

        if (distancia == 0) {
            mover(getVelocidad(), 0, 0);
            return;
        }

        double factor = getVelocidad() / distancia;

        mover(dx * factor, dy * factor, dz * factor);
    }

    @Override
    protected void copiarEstadoDe(Entidad otra) {
        super.copiarEstadoDe(otra);
        domesticable = ((EntidadPasiva) otra).domesticable;
    }

    public boolean isDomesticable() {
        return domesticable;
    }

    @Override
    public abstract String describirComportamiento();
}
