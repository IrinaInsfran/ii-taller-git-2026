package py.edu.uc.lp3.exceptions;

/**
 * Se busca algo del mundo por id (una entidad o una explosion ya ocurrida)
 * y no existe.
 */
public class EntidadNoEncontradaException extends MinecraftException {

    public EntidadNoEncontradaException(Long id) {
        super("entidad_no_encontrada", "No existe una entidad con id " + id + ".");
    }

    /** Para otro recurso del mundo, por ejemplo {@code ("explosion", "explosión", 5)}. */
    public EntidadNoEncontradaException(String recurso, String nombre, Long id) {
        super(recurso + "_no_encontrada", "No existe una " + nombre + " con id " + id + ".");
    }
}
