package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Animal: entidad pasiva que puede ser montable.
 */
public class Animal extends EntidadPasiva {

    private final boolean montable;

    public Animal(int salud, double posicionX, double posicionY, double posicionZ,
                  int velocidad, boolean domesticable, boolean montable) {
        super(salud, posicionX, posicionY, posicionZ, velocidad, domesticable);
        this.montable = montable;
    }

    public String montar(Jugador jugador) {
        exigirViva();
        if (jugador == null || !jugador.estaViva()) throw new IllegalArgumentException("se necesita un jugador vivo");
        if (!montable) throw new IllegalStateException("este animal no se puede montar");
        return jugador.getNombre() + " monta al animal.";
    }

    public boolean isMontable() { return montable; }

    @Override
    public String detalle() {
        return super.detalle() + (montable ? ", montable" : "");
    }
}
