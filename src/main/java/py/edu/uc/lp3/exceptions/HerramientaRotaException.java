package py.edu.uc.lp3.exceptions;

/**
 * Se intenta usar una herramienta cuya durabilidad llego a 0.
 */
public class HerramientaRotaException extends ReglaDelJuegoException {

    public HerramientaRotaException(String mensaje) {
        super("herramienta_rota", mensaje);
    }
}
