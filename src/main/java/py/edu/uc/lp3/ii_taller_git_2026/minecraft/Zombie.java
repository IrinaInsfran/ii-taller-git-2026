package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Zombie: entidad hostil que puede infectar aldeanos.
 */
public class Zombie extends EntidadHostil {

    public Zombie(int salud, double posicionX, double posicionY, double posicionZ,
                  int velocidad, double rangoDeteccion, int danoAtaque) {
        super(salud, posicionX, posicionY, posicionZ, velocidad, rangoDeteccion, danoAtaque);
    }

    public String infectaAldeano(Aldeano aldeano) {
        exigirViva();
        if (aldeano == null) throw new IllegalArgumentException("el aldeano es requerido");
        if (!detecta(aldeano)) throw new IllegalStateException("el aldeano está fuera del rango del zombie");
        return "El zombie infecta a un aldeano cercano. " + aldeano.serInfectado();
    }
}
