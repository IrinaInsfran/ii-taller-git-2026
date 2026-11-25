package py.edu.uc.lp3.exceptions;

/**
 * El objetivo está demasiado lejos: fuera del rango de detección de un mob,
 * del alcance de ataque del jugador o de la distancia de detonación de un
 * creeper.
 */
public class FueraDeAlcanceException extends ReglaDelJuegoException {

    public FueraDeAlcanceException(String mensaje) {
        super("fuera_de_alcance", mensaje);
    }
}
