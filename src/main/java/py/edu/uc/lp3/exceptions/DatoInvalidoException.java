package py.edu.uc.lp3.exceptions;

/**
 * Un valor no tiene sentido en el mundo del juego: salud no positiva,
 * velocidad negativa, posicion no numerica, nombre vacio, argumento nulo...
 * Se corrige cambiando los datos que se envian.
 */
public class DatoInvalidoException extends MinecraftException {

    public DatoInvalidoException(String mensaje) {
        this("dato_invalido", mensaje);
    }

    public DatoInvalidoException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
