package py.edu.uc.lp3.exceptions;

/**
 * La API externa no respondió o respondió con un error que no es culpa de
 * quien llamó a nuestra API (caída, timeout, 5xx).
 */
public class ServicioExternoException extends MinecraftException {

    public ServicioExternoException(String mensaje, Throwable causa) {
        super("servicio_externo_no_disponible", mensaje);
        initCause(causa);
    }
}
