package py.edu.uc.lp3.exceptions;

/**
 * La regla de una compañera dijo que no (respondió 4xx). La jugada no se
 * guarda y la API responde 409 con el origen del rechazo.
 */
public class JugadaRechazadaException extends CompaneraException {

    public JugadaRechazadaException(String origen, String companera, int estadoCompanera,
                                    Object respuestaCompanera, String detalle) {
        super("rechazada_por_" + origen.toLowerCase(),
                companera + " (" + origen + ") rechazó la jugada: " + detalle + " La jugada no se guardó.",
                origen, companera, estadoCompanera, respuestaCompanera);
    }
}
