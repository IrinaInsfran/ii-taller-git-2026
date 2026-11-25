package py.edu.uc.lp3.exceptions;

/**
 * Un problema que viene de una compañera del grupo de POO-09 (A2 o A3):
 * su regla rechazó la jugada o su API no respondió. Lleva quién fue, qué
 * código HTTP devolvió y el cuerpo de su respuesta sin modificar, para que
 * quien llama sepa a quién reclamarle.
 */
public abstract class CompaneraException extends MinecraftException {

    private final String origen;
    private final String companera;
    private final Integer estadoCompanera;
    private final transient Object respuestaCompanera;

    protected CompaneraException(String codigo, String mensaje, String origen, String companera,
                                 Integer estadoCompanera, Object respuestaCompanera) {
        super(codigo, mensaje);
        this.origen = origen;
        this.companera = companera;
        this.estadoCompanera = estadoCompanera;
        this.respuestaCompanera = respuestaCompanera;
    }

    /** Rol de la compañera en el grupo: {@code A2} o {@code A3}. */
    public String getOrigen() {
        return origen;
    }

    public String getCompanera() {
        return companera;
    }

    /** Código HTTP que devolvió la compañera, o {@code null} si no respondió. */
    public Integer getEstadoCompanera() {
        return estadoCompanera;
    }

    /** Cuerpo que devolvió la compañera (JSON o texto), o {@code null} si no respondió. */
    public Object getRespuestaCompanera() {
        return respuestaCompanera;
    }
}
