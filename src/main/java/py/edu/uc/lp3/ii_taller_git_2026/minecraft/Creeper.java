package py.edu.uc.lp3.herencia;

/**
 * Creeper: entidad hostil que explota tras un tiempo de detonación.
 */
public class Creeper extends EntidadHostil {

    private final int tiempoExplosion;

    public Creeper(int salud,
                   double posicionX,
                   double posicionY,
                   double posicionZ,
                   int velocidad,
                   double rangoDeteccion,
                   int danoAtaque,
                   int tiempoExplosion) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                rangoDeteccion,
                danoAtaque
        );

        if (tiempoExplosion <= 0) {
            throw new IllegalArgumentException(
                    "El tiempo de explosión debe ser mayor que cero."
            );
        }

        this.tiempoExplosion = tiempoExplosion;
    }

    public void explotar() {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un Creeper muerto no puede explotar."
            );
        }

        System.out.println(
                "¡El creeper explota tras " +
                tiempoExplosion +
                " segundos!"
        );
    }

    public int getTiempoExplosion() {
        return tiempoExplosion;
    }
}
