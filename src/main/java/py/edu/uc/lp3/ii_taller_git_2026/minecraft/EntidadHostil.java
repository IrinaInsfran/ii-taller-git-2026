package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Entidad hostil: ataca al jugador u otras entidades.
 */
public class EntidadHostil extends Entidad {

    private final double rangoDeteccion;
    private final int danoAtaque;

    public EntidadHostil(int salud,
                         double posicionX,
                         double posicionY,
                         double posicionZ,
                         int velocidad,
                         double rangoDeteccion,
                         int danoAtaque) {

        super(salud, posicionX, posicionY, posicionZ, velocidad);

        if (!Double.isFinite(rangoDeteccion) || rangoDeteccion < 0) {
            throw new IllegalArgumentException(
                    "El rango de detección no puede ser negativo."
            );
        }

        if (danoAtaque <= 0) {
            throw new IllegalArgumentException(
                    "El daño de ataque debe ser mayor que cero."
            );
        }

        this.rangoDeteccion = rangoDeteccion;
        this.danoAtaque = danoAtaque;
    }

    public void atacar(Entidad objetivo) {

        if (objetivo == null) {
            throw new IllegalArgumentException("El objetivo no puede ser nulo.");
        }

        if (!estaViva()) {
            throw new IllegalStateException("Una entidad muerta no puede atacar.");
        }

        if (!objetivo.estaViva()) {
            throw new IllegalStateException(
                    "No se puede atacar a una entidad que ya está muerta."
            );
        }

        if (distanciaA(objetivo) > rangoDeteccion) {
            throw new IllegalStateException(
                    "El objetivo está fuera del rango de detección."
            );
        }

        objetivo.recibirDano(danoAtaque);
    }

    public double getRangoDeteccion() {
        return rangoDeteccion;
    }

    public int getDanoAtaque() {
        return danoAtaque;
    }
    @Override
    public String describirComportamiento() {
    	return "Detecta objetivos dentro de su rango y puede atacarlos.";
}
}
