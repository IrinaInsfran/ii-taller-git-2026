package py.edu.uc.lp3.domain;

/**
 * Material con el que se fabrica una herramienta.
 * <p>
 * Define cuantos usos aguanta (durabilidad, valores de Minecraft Java) y su
 * nivel de mineria: un pico solo obtiene un mineral si su nivel alcanza el
 * que el mineral exige. El oro es la curiosidad del juego: mina rapido pero
 * tiene el nivel de la madera y se rompe enseguida.
 */
public enum Material {

    MADERA(59, 0),
    ORO(32, 0),
    PIEDRA(131, 1),
    HIERRO(250, 2),
    DIAMANTE(1561, 3),
    NETHERITA(2031, 4);

    private final int durabilidad;
    private final int nivelMineria;

    Material(int durabilidad, int nivelMineria) {
        this.durabilidad = durabilidad;
        this.nivelMineria = nivelMineria;
    }

    public int getDurabilidad() {
        return durabilidad;
    }

    public int getNivelMineria() {
        return nivelMineria;
    }
}
