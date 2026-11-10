package py.edu.uc.lp3.exceptions;

/**
 * El objetivo esta demasiado lejos: fuera del rango de deteccion de un mob,
 * del alcance de ataque del jugador o de la distancia de detonacion de un
 * creeper.
 */
public class FueraDeAlcanceException extends ReglaDelJuegoException {

    public FueraDeAlcanceException(String mensaje) {
        super("fuera_de_alcance", mensaje);
    }
}
