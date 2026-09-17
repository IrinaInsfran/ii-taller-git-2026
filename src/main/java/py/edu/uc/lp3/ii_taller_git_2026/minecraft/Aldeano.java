package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Aldeano: entidad pasiva con una profesión que puede comerciar.
 * Si un zombie lo infecta, deja de comerciar.
 */
public class Aldeano extends EntidadPasiva {

    private final String profesion;
    private boolean infectado;

    public Aldeano(int salud, double posicionX, double posicionY, double posicionZ,
                   int velocidad, boolean domesticable, String profesion) {
        super(salud, posicionX, posicionY, posicionZ, velocidad, domesticable);
        if (profesion == null || profesion.isBlank()) throw new IllegalArgumentException("la profesión es requerida");
        this.profesion = profesion;
    }

    public String comercio() {
        exigirViva();
        if (infectado) throw new IllegalStateException("un aldeano infectado no comercia");
        return "El aldeano (" + profesion + ") ofrece comerciar.";
    }

    /** Sin "public": solo otras clases del paquete minecraft (el Zombie) pueden infectarlo. */
    String serInfectado() {
        exigirViva();
        if (infectado) return "El aldeano ya estaba infectado.";
        infectado = true;
        return "El aldeano (" + profesion + ") fue infectado.";
    }

    public String getProfesion() { return profesion; }
    public boolean isInfectado() { return infectado; }

    @Override
    public String detalle() {
        return profesion + (infectado ? ", infectado" : "");
    }
}
