package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Esqueleto: entidad hostil que ataca disparando flechas.
 */
public class Esqueleto extends EntidadHostil {

    public Esqueleto(int salud, double posicionX, double posicionY, double posicionZ,
                     int velocidad, double rangoDeteccion, int danoAtaque) {
        super(salud, posicionX, posicionY, posicionZ, velocidad, rangoDeteccion, danoAtaque);
    }

    /** Llama a super: respeta la regla de rango y daño del padre y agrega la flecha. */
    @Override
    public String atacar(Entidad objetivo) {
        return "El esqueleto dispara una flecha. " + super.atacar(objetivo);
    }

    public String dispararFlecha(Entidad objetivo) {
        return atacar(objetivo);
    }
}
