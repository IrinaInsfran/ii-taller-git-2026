package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;

/**
 * Pico: rompe minerales. No es un arma.
 * <p>
 * Regla del juego: si el material del pico no alcanza el nivel que pide el
 * mineral, el bloque se rompe igual pero no suelta nada, y la durabilidad
 * se gasta de todas formas.
 */
public class Pico extends Herramienta {

    public Pico(Material material) {
        super(material);
    }

    public boolean puedeObtener(Mineral mineral) {

        if (mineral == null) {
            throw new DatoInvalidoException("El mineral no puede ser nulo.");
        }

        return getMaterial().getNivelMineria()
                >= mineral.getPicoMinimo().getNivelMineria();
    }

    /**
     * Pica el mineral y gasta 1 de durabilidad.
     *
     * @return true si el mineral se obtuvo; false si el pico es de un
     *         material insuficiente y el bloque no solto nada
     */
    public boolean minar(Mineral mineral) {

        boolean obtenido = puedeObtener(mineral);

        desgastar(1);

        return obtenido;
    }

    @Override
    public String describirUso() {
        return "Pica minerales: con " + getMaterial()
                + " obtiene los que piden nivel de minería "
                + getMaterial().getNivelMineria() + " o menor.";
    }
}
