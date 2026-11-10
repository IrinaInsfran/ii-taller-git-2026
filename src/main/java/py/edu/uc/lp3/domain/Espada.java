package py.edu.uc.lp3.domain;

import py.edu.uc.lp3.exceptions.DatoInvalidoException;

/**
 * Espada: el arma cuerpo a cuerpo por excelencia.
 * Cada golpe gasta 1 de durabilidad.
 */
public class Espada extends Herramienta implements Arma {

    public Espada(Material material) {
        super(material);
    }

    /** Daño de la espada en Minecraft Java segun su material. */
    @Override
    public int getDanoAtaque() {
        return switch (getMaterial()) {
            case MADERA, ORO -> 4;
            case PIEDRA -> 5;
            case HIERRO -> 6;
            case DIAMANTE -> 7;
            case NETHERITA -> 8;
        };
    }

    @Override
    public void atacar(Entidad objetivo) {

        if (objetivo == null) {
            throw new DatoInvalidoException("El objetivo no puede ser nulo.");
        }

        verificarQueNoEsteRota();
        objetivo.recibirDano(getDanoAtaque());
        desgastar(1);
    }

    @Override
    public String describirAmenaza() {
        return "Espada de " + getMaterial() + ": " + getDanoAtaque()
                + " de daño por golpe cuerpo a cuerpo.";
    }

    @Override
    public String describirUso() {
        return "Arma cuerpo a cuerpo: hace " + getDanoAtaque()
                + " de daño y gasta 1 de durabilidad por golpe.";
    }
}
