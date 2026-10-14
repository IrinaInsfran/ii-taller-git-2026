package py.edu.uc.lp3.ii_taller_git_2026.minecraft;

/**
 * Enderman: entidad hostil que puede teletransportarse.
 */
public class Enderman extends EntidadHostil {

    public Enderman(int salud,
                    double posicionX,
                    double posicionY,
                    double posicionZ,
                    int velocidad,
                    double rangoDeteccion,
                    int danoAtaque) {

        super(
                salud,
                posicionX,
                posicionY,
                posicionZ,
                velocidad,
                rangoDeteccion,
                danoAtaque
        );
    }

    public void teletransportar(double dx, double dy, double dz) {

        if (!estaViva()) {
            throw new IllegalStateException(
                    "Un Enderman muerto no puede teletransportarse."
            );
        }

        double distancia = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distancia > getRangoDeteccion()) {
            throw new IllegalArgumentException(
                    "El Enderman no puede teletransportarse más allá de su rango de detección."
            );
        }

        mover(dx, dy, dz);
        System.out.println("El Enderman se teletransporta.");
    }
}