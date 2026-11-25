package py.edu.uc.lp3.constants;

/**
 * Centraliza las rutas de la API REST para que controllers, tests y README
 * hablen de las mismas URLs.
 */
public final class ApiPaths {

	private static final String BASE_API = "/api/minecraft";

	public static final String INDEX = "/";

	/** Recurso del CRUD: la familia Entidad. */
	public static final String ENTIDADES = BASE_API + "/entidades";

	/** Protocolo Amenaza: quién puede dañar a la entidad {id}. */
	public static final String AMENAZAS = ENTIDADES + "/{id}/amenazas";

	/** Comportamiento polimórfico de las entidades guardadas. */
	public static final String COMPORTAMIENTOS = BASE_API + "/comportamientos";

	/** Simulación: crear una explosión la detona; el historial queda guardado. */
	public static final String EXPLOSIONES = BASE_API + "/explosiones";

	/** Reglas que otros servicios consultan por HTTP; no leen ni guardan en H2 (POO-09). */
	public static final String REGLAS = BASE_API + "/reglas";

	/** Regla «mecha corta»: el creeper solo detona con un jugador vivo a 3 bloques o menos. */
	public static final String REGLA_MECHA_CORTA = REGLAS + "/mecha-corta";

	/** Escenario de POO-09: jugadas que A2 y A3 aceptaron, guardadas en H2. */
	public static final String JUGADAS = BASE_API + "/jugadas";

	/** Un creeper explota solo si A2 (Cecilia) y A3 (Sol) aceptan la jugada. */
	public static final String JUGADA_EXPLOSION = JUGADAS + "/explosion";

	/** A quién consulta el escenario y en qué URL. */
	public static final String JUGADAS_COMPANERAS = JUGADAS + "/companeras";

	/** La entidad {id} viaja al mundo Minecraft de Ceci. */
	public static final String PORTAL = ENTIDADES + "/{id}/portal";

	/** Un esqueleto guardado dispara; la regla de alcance es de la API Minecraft remota. */
	public static final String FLECHAZOS = BASE_API + "/flechazos";

	/** Objetos de la API externa https://api.restful-api.dev/objects (consultados con WebClient). */
	public static final String OBJETOS_EXTERNOS = "/api/externo/objetos";

	public static final String OBJETO_EXTERNO = OBJETOS_EXTERNOS + "/{id}";

	private ApiPaths() {
	}
}
