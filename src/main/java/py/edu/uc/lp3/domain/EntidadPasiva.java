package py.edu.uc.lp3.domain;

import jakarta.persistence.Entity;

/**
 * Representa una entidad pasiva que no ataca y puede huir de amenazas
 * (la huida la calcula {@code AccionServiceImpl}).
 * <p>
 * Es abstracta: una "pasiva genérica" no describe un comportamiento concreto
 * del juego, así que cada subclase debe decir cómo se comporta.
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
