package py.edu.uc.lp3.domain;

/**
 * Minerales que se pueden picar y el material minimo de pico que exigen
 * para soltar algo (por ejemplo, el diamante necesita pico de hierro).
 */
public enum Mineral {

    CARBON(Material.MADERA),
    COBRE(Material.PIEDRA),
    HIERRO(Material.PIEDRA),
    LAPISLAZULI(Material.PIEDRA),
    ORO(Material.HIERRO),
    REDSTONE(Material.HIERRO),
    DIAMANTE(Material.HIERRO),
    ESMERALDA(Material.HIERRO),
    OBSIDIANA(Material.DIAMANTE);

    private final Material picoMinimo;

    Mineral(Material picoMinimo) {
        this.picoMinimo = picoMinimo;
    }

    public Material getPicoMinimo() {
        return picoMinimo;
    }
}
