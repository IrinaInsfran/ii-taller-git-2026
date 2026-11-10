package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;

/**
 * Hacha: herramienta para talar que tambien sirve de arma.
 * <p>
 * Pega mas fuerte que la espada, pero como no esta hecha para pelear cada
 * golpe le gasta 2 de durabilidad (talar gasta 1), igual que en el juego.
 */
public class Hacha extends Herramienta implements Arma {

    public Hacha(Material material) {
        super(material);
    }

    /** Daño del hacha en Minecraft Java segun su material. */
    @Override
    public int getDanoAtaque() {
        return switch (getMaterial()) {
            case MADERA, ORO -> 7;
            case PIEDRA, HIERRO, DIAMANTE -> 9;
            case NETHERITA -> 10;
        };
    }

    @Override
    public void atacar(Entidad objetivo) {

        if (objetivo == null) {
            throw new DatoInvalidoException("El objetivo no puede ser nulo.");
        }

        verificarQueNoEsteRota();
        objetivo.recibirDano(getDanoAtaque());
        desgastar(2);
    }

    public void talar() {

        verificarQueNoEsteRota();
        System.out.println("El hacha de " + getMaterial() + " tala un árbol.");
        desgastar(1);
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
