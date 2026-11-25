package py.edu.uc.lp3.domain;

/**
 * Pico: rompe minerales. No es un arma.
 * <p>
 * Si el material alcanza el nivel que pide el mineral lo decide
 * {@code HerramientaServiceImpl}: si no alcanza, el bloque se rompe igual
 * pero no suelta nada, y la durabilidad se gasta de todas formas.
 */
public class Pico extends Herramienta {

    public Pico(Material material) {
        super(material);
    }

    @Override
    public String describirUso() {
        return "Pica minerales: con " + getMaterial()
                + " obtiene los que piden nivel de minería "
                + getMaterial().getNivelMineria() + " o menor.";
    }
}
