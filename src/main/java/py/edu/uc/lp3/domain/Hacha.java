package py.edu.uc.lp3.domain;

/**
 * Hacha: herramienta para talar que también sirve de arma.
 * <p>
 * Pega más fuerte que la espada, pero como no está hecha para pelear cada
 * golpe le gasta 2 de durabilidad (talar gasta 1), igual que en el juego.
 */
public class Hacha extends Herramienta implements Arma {

    public Hacha(Material material) {
        super(material);
    }

    /** Daño del hacha en Minecraft Java según su material. */
    @Override
    public int getDanoAtaque() {
        return switch (getMaterial()) {
            case MADERA, ORO -> 7;
            case PIEDRA, HIERRO, DIAMANTE -> 9;
            case NETHERITA -> 10;
        };
    }

    @Override
    public int getDesgastePorGolpe() {
        return 2;
    }

    @Override
    public String describirAmenaza() {
        return "Hacha de " + getMaterial() + ": " + getDanoAtaque()
                + " de daño por golpe, pero se gasta el doble.";
    }

    @Override
    public String describirUso() {
        return "Tala árboles y sirve de arma: hace " + getDanoAtaque()
                + " de daño, pero gasta 2 de durabilidad por golpe.";
    }
}
