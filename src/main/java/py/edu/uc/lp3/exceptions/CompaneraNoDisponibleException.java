package py.edu.uc.lp3.exceptions;

/**
 * La API de una compañera no respondió, se venció el tiempo de espera o
 * devolvió 5xx. No se sabe si su regla acepta, así que la jugada no se
 * guarda y la API responde 502 con el origen.
 */
public class CompaneraNoDisponibleException extends CompaneraException {

    public CompaneraNoDisponibleException(String origen, String companera, Integer estadoCompanera,
                                          Object respuestaCompanera, String detalle, Throwable causa) {
        super("companera_no_disponible",
                "No se pudo consultar a " + companera + " (" + origen + "): " + detalle
                        + " La jugada no se guardó.",
                origen, companera, estadoCompanera, respuestaCompanera);
        initCause(causa);
    }
}
