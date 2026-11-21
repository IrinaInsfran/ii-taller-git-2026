package py.edu.uc.lp3.exceptions;

/**
 * Raíz de todas las excepciones del dominio Minecraft.
 * <p>
 * Es abstracta: nunca se lanza "un error de Minecraft" a secas, sino uno de
 * sus tres tipos, que responden a preguntas distintas:
 * <ul>
 *   <li>{@link DatoInvalidoException}: ¿los datos tienen sentido?
 *       (salud negativa, nombre vacío, campo faltante);</li>
 *   <li>{@link EntidadNoEncontradaException}: ¿existe lo que se pide?;</li>
 *   <li>{@link ReglaDelJuegoException}: los datos están bien, pero ¿el
 *       juego permite hacer eso? (un muerto no ataca, una herramienta rota
 *       no se usa).</li>
 * </ul>
 * Cada excepción lleva un {@code codigo} corto y estable para que un
 * programa pueda reconocer el error sin leer el mensaje.
 * <p>
 * Es <b>no comprobada</b> ({@link RuntimeException}): un dato inválido o una
 * regla rota no se "recupera" en el método que llama (el servicio no puede
 * inventar otra salud para un creeper), sino que corta la operación entera.
 * Si fuera comprobada, cada servicio y controller tendría que declarar
 * {@code throws} solo para dejarla pasar hasta el handler HTTP. Además, al
 * ser {@code RuntimeException}, {@code @Transactional} hace rollback solo.
 */
public abstract class MinecraftException extends RuntimeException {

    private final String codigo;

    protected MinecraftException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
