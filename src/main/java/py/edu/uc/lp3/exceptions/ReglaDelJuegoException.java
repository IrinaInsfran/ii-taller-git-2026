package py.edu.uc.lp3.exceptions;

/**
 * Los datos son validos, pero la accion rompe una regla de Minecraft.
 * <p>
 * Las reglas que se repiten en muchas clases tienen su propia subclase
 * ({@link EntidadMuertaException}, {@link FueraDeAlcanceException},
 * {@link HerramientaRotaException}); las reglas puntuales usan esta clase
 * directamente con su propio codigo.
 */
public class ReglaDelJuegoException extends MinecraftException {

    public ReglaDelJuegoException(String codigo, String mensaje) {
        super(codigo, mensaje);
    }
}
