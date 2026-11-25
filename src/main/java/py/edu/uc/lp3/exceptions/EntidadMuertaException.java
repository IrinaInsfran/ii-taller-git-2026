package py.edu.uc.lp3.exceptions;

/**
 * Una entidad muerta (salud 0) intenta actuar, o se actúa sobre ella:
 * moverse, atacar, curarse, explotar, comerciar, montar...
 */
public class EntidadMuertaException extends ReglaDelJuegoException {

    public EntidadMuertaException(String mensaje) {
        super("entidad_muerta", mensaje);
    }
}
