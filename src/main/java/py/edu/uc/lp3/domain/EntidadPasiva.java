package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Representa una entidad pasiva que no ataca y puede huir de amenazas.
 */
public class EntidadPasiva extends Entidad {

    private final boolean domesticable;

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
            throw new IllegalArgumentException("La amenaza no puede ser nula.");
        }

        if (!estaViva()) {
            throw new IllegalStateException("Una entidad muerta no puede huir.");
        }

        double dx = getPosicionX() - amenaza.getPosicionX();
        double dy = getPosicionY() - amenaza.getPosicionY();
        double dz = getPosicionZ() - amenaza.getPosicionZ();

        double distancia = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (getVelocidad() == 0) {
            throw new IllegalStateException("La entidad no tiene velocidad para huir.");
        }

        if (distancia == 0) {
            mover(getVelocidad(), 0, 0);
            return;
        }

        double factor = getVelocidad() / distancia;

        mover(dx * factor, dy * factor, dz * factor);
    }

    public boolean isDomesticable() {
        return domesticable;
    }
    @Override
    public String describirComportamiento() {
    	return "Evita amenazas y puede huir cuando se encuentra en peligro.";
}
}
